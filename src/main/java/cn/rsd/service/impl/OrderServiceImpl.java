package cn.rsd.service.impl;

import cn.rsd.dao.*;
import cn.rsd.po.*;
import cn.rsd.service.OrderService;
import cn.rsd.service.WeiXinMessageService;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author 焦云亮
 * @date 2018/10/19
 * @modifyUser
 * @modifyDate
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private BuyerOrderMapper buyerOrderMapper;

    @Autowired
    private TrucksMapper trucksMapper;

    @Autowired
    private WeiXinMessageService weiXinMessageServie;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UserTrackMapper userTrackMapper;

    @Autowired
    private OrderCodeService orderCodeService;

    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SupplyPostsMapper supplyPostsMapper;

    @Autowired
    private OrderStateLogMapper orderStateLogMapper;

    @Override
    public int saveOrderLog(OrderStateLog orderStateLog) throws Exception {

        Users user = this.usersMapper.selectByPrimaryKey(orderStateLog.getCreateUser());

        if(user != null && user.getOpenId() != null){
            String latitude = this.redisTemplate.opsForValue().get("latitude_"+user.getOpenId());
            String longitude = this.redisTemplate.opsForValue().get("longitude_"+user.getOpenId());
            orderStateLog.setLatitude(latitude);
            orderStateLog.setLongitude(longitude);
        }

        return this.orderStateLogMapper.insert(orderStateLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int renewOrder(BuyerOrder buyerOrder,Long trucks) throws Exception {

        Trucks truck = this.trucksMapper.selectByPrimaryKey(trucks);
        Users driver = this.usersMapper.selectByPrimaryKey(truck.getUserId());

        //释放旧车辆
        Users oldDriver = this.usersMapper.selectByPrimaryKey(buyerOrder.getDistribution());
        Trucks oldTruck = new Trucks();
        oldTruck.setUserId(oldDriver.getId());
        oldTruck = this.trucksMapper.selectOne(oldTruck);
        oldTruck.setState(5);
        oldTruck.setUpdateDate(new Date());
        this.trucksMapper.updateByPrimaryKey(oldTruck);

        buyerOrder.setDistribution(driver.getId());

        buyerOrder.setUpdateDate(new Date());
        buyerOrder.setState(2);
        buyerOrder.setTrucks(truck.getId());
        this.buyerOrderMapper.updateByPrimaryKeySelective(buyerOrder);

        if(driver != null && driver.getOpenId() != null){

            weiXinMessageServie.sendAppIdTmplMessage(driver.getAppId(), driver.getOpenId(), driver.getOrderTmpl(),driver.getWebUrl(), "order_detail_send_new.html?id=" + buyerOrder.getId(), "你有新的订单需要运输！", buyerOrder.getOrderCode(),
                    DateUtils.formatDate(buyerOrder.getCreateDate(),"yyyy-MM-dd HH:mm"),
                    "热量", buyerOrder.getBuyerName(), truck.getPlateNumber(), "");
        }

        //锁定车辆
        truck.setState(4);
        truck.setUpdateDate(new Date());
        this.trucksMapper.updateByPrimaryKey(truck);

        OrderStateLog stateLog = new OrderStateLog();
        stateLog.setState(buyerOrder.getState());
        stateLog.setCreateDate(buyerOrder.getUpdateDate());
        stateLog.setOrderId(buyerOrder.getId());
        stateLog.setCreateUser(buyerOrder.getUpdateUser());
        this.saveOrderLog(stateLog);

        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchSaveOrder(BuyerOrder buyerOrder,Integer times) throws Exception {
        for(int i = 0 ; i < times;i++){
            this.saveOrder(buyerOrder);
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized int saveOrder(BuyerOrder buyerOrder) throws Exception {

        SupplyPosts supplyPosts = this.supplyPostsMapper.selectByPrimaryKey(buyerOrder.getSupplyId());

        if(supplyPosts == null ){
            throw new Exception("没有找到供热点信息!");
        }

        //供热点下的全部车辆列表.
        List<Trucks> trucks = this.trucksMapper.selectTrucksByIdle(supplyPosts.getId());

        if(trucks == null || trucks.isEmpty()){
            throw new Exception("没有足够的空闲车辆可用,请稍候重试.");
        }

        Trucks truck = this.trucksMapper.selectByPrimaryKey(supplyPosts.getTrucks());

        //自动分配运送车辆
        if(truck == null){
            truck = trucks.get(0);
        //如果指定的车辆不能正常使用,则找一个可用车辆
        }else if(truck != null && truck.getState() != 1){
             truck = nextTrucks(trucks,truck);
        }

        if(truck == null){
            throw new Exception("没有空闲车辆可用,请稍候重试.");
        }

        //锁定车辆
        truck.setState(4);
        truck.setUpdateDate(new Date());
        this.trucksMapper.updateByPrimaryKey(truck);

        //更新供热点下一个可派的车辆
        supplyPosts.setTrucks(nextTrucks(trucks,truck).getId());
        this.supplyPostsMapper.updateByPrimaryKey(supplyPosts);


        //保存订单基本信息
        buyerOrder.setDistribution(truck.getUserId());
        buyerOrder.setOrderCode(orderCodeService.nextId());
        buyerOrder.setState(2);
        buyerOrder.setTrucks(truck.getId());
        this.buyerOrderMapper.insertUseGeneratedKeys(buyerOrder);

        //发送创建订单消息
        

        Users adminUser = this.usersMapper.selectByPrimaryKey(supplyPosts.getAdminId());

        //查找司机信息
        Users driver = this.usersMapper.selectByPrimaryKey(buyerOrder.getDistribution());
        //发送订单通知
        if (adminUser.getOpenId() != null) {
            weiXinMessageServie.sendAppIdTmplMessage(adminUser.getAppId(),adminUser.getOpenId(), adminUser.getOrderTmpl(), adminUser.getWebUrl(),"order_detail_preview.html?id=" + buyerOrder.getId(), "你有新的订单！", buyerOrder.getOrderCode(),
                    DateUtils.formatDate(buyerOrder.getCreateDate(),"yyyy-MM-dd HH:mm"),
                    "热量", buyerOrder.getBuyerName(), truck.getPlateNumber(), "");
        }

        if(driver != null && driver.getOpenId() != null){
            weiXinMessageServie.sendAppIdTmplMessage(driver.getAppId(),driver.getOpenId(), driver.getOrderTmpl(), driver.getWebUrl(), "order_detail_send_new.html?id=" + buyerOrder.getId(), "你有新的订单需要运输！", buyerOrder.getOrderCode(),
                    DateUtils.formatDate(buyerOrder.getCreateDate(),"yyyy-MM-dd HH:mm"),
                    "热量", buyerOrder.getBuyerName(), truck.getPlateNumber(), "");

        }

        OrderStateLog stateLog = new OrderStateLog();
        stateLog.setState(buyerOrder.getState());
        stateLog.setCreateDate(buyerOrder.getUpdateDate());
        stateLog.setOrderId(buyerOrder.getId());
        stateLog.setCreateUser(buyerOrder.getUpdateUser());
        this.saveOrderLog(stateLog);

        return 0;
    }


    private Trucks nextTrucks(List<Trucks> list,Trucks trucks)throws Exception{
        Trucks reTrucks = null;

        for(Trucks t:list){
            if(t.getId() > trucks.getId()){
                reTrucks = t;
                break;
            }
        }

        //找不到合适的,从头开始.
        if(reTrucks == null) {
            reTrucks = list.get(0);
        }

        return reTrucks;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int completeOrder(String orderCode,Integer fee,String transactionId)throws Exception{

        BuyerOrder buyerOrder = new BuyerOrder();
        buyerOrder.setOrderCode(orderCode);

        buyerOrder = this.buyerOrderMapper.selectOne(buyerOrder);
        buyerOrder.setTransactionId(transactionId);
        if(buyerOrder.getState() == BuyerOrder.BuyerOrderStateEnum.UNPAID.value()){
            buyerOrder.setState(BuyerOrder.BuyerOrderStateEnum.COMPLETE.value());

            this.buyerOrderMapper.updateByPrimaryKey(buyerOrder);

            UserAccount userAccount = new UserAccount();
            userAccount.setUserId(buyerOrder.getBuyer());

            userAccount = this.userAccountMapper.selectOne(userAccount);

            //增加交易记录
            UserTrack userTrack = new UserTrack();
            userTrack.setOrderId(buyerOrder.getId());
            userTrack.setWeixin(fee);
            userTrack.setAccountBalance(0);
            userTrack.setAlipay(0);
            userTrack.setTrackType(UserTrack.TrackTypeEnum.CONSUME.value());
            userTrack.setCreateDate(new Date());
            userTrack.setCreateUser(buyerOrder.getUpdateUser());

            //设置交易的关联账户
            if(userAccount != null) {
                userTrack.setDeductionsAccount(userAccount.getId());
            }

            this.userTrackMapper.insert(userTrack);


            OrderStateLog stateLog = new OrderStateLog();
            stateLog.setState(buyerOrder.getState());
            stateLog.setOrderId(buyerOrder.getId());
            stateLog.setCreateDate(buyerOrder.getUpdateDate());
            stateLog.setCreateUser(buyerOrder.getUpdateUser());
            this.saveOrderLog(stateLog);
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int reachOrder(BuyerOrder buyerOrder) throws Exception {
        if(buyerOrder.getActualNumber() != null) {
            buyerOrder.setState(6);

            if(buyerOrder.getActualNumber() < 1){
                throw new Exception("实际热量不能小于1!");
            }

            //计算价格,单位分
            buyerOrder.setPrice(buyerOrder.getActualNumber().intValue()*(85*100));
        }else{
            buyerOrder.setState(5);
        }

        this.buyerOrderMapper.updateByPrimaryKeySelective(buyerOrder);

        //供热结束,释放车辆信息
        if(buyerOrder.getActualNumber() != null) {
            buyerOrder = this.buyerOrderMapper.selectByPrimaryKey(buyerOrder.getId());
            Trucks truck = new Trucks();
            truck.setUserId(buyerOrder.getDistribution());
            truck = this.trucksMapper.selectOne(truck);

            truck.setState(5);
            truck.setUpdateDate(new Date());
            this.trucksMapper.updateByPrimaryKey(truck);
        }

        OrderStateLog stateLog = new OrderStateLog();
        stateLog.setState(buyerOrder.getState());
        stateLog.setOrderId(buyerOrder.getId());
        stateLog.setCreateDate(buyerOrder.getUpdateDate());
        stateLog.setCreateUser(buyerOrder.getUpdateUser());
        this.saveOrderLog(stateLog);
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized int payOrder(Long id) throws Exception {
        BuyerOrder buyerOrder = this.buyerOrderMapper.selectByPrimaryKey(id);

        if(buyerOrder == null){
            throw new Exception("未找到订单信息");
        }

        //查询订单是否已经支付成功
        UserTrack orderTrack = new UserTrack();
        orderTrack.setOrderId(buyerOrder.getId());
        if(this.userTrackMapper.selectCount(orderTrack) > 0){
            throw new Exception("请不要重复支付订单");
        }

        buyerOrder.setState(7);
        buyerOrder.setUpdateDate(new Date());
        buyerOrder.setUpdateUser(buyerOrder.getBuyer());
        this.buyerOrderMapper.updateByPrimaryKeySelective(buyerOrder);

        //扣费
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(buyerOrder.getBuyer());
        userAccount = this.userAccountMapper.selectOne(userAccount);

        if(userAccount == null){
            throw new Exception("余额不足");
        }

        if(userAccount.getBalance() < buyerOrder.getPrice()){
            throw new Exception("余额不足");
        }

        userAccount.setBalance(userAccount.getBalance() - buyerOrder.getPrice());

        this.userAccountMapper.updateByPrimaryKey(userAccount);

        //增加交易记录
        UserTrack userTrack = new UserTrack();
        userTrack.setOrderId(buyerOrder.getId());
        userTrack.setDeductionsAccount(userAccount.getId());
        userTrack.setAccountBalance(buyerOrder.getPrice());
        userTrack.setTrackType(UserTrack.TrackTypeEnum.CONSUME.value());
        userTrack.setCreateDate(new Date());
        userTrack.setCreateUser(buyerOrder.getUpdateUser());

        this.userTrackMapper.insert(userTrack);

        //增加状态变更日志
        OrderStateLog stateLog = new OrderStateLog();
        stateLog.setState(buyerOrder.getState());
        stateLog.setOrderId(buyerOrder.getId());
        stateLog.setCreateDate(buyerOrder.getUpdateDate());
        stateLog.setCreateUser(buyerOrder.getUpdateUser());
        this.saveOrderLog(stateLog);

        return 0;
    }
}
