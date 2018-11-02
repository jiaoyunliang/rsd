package cn.rsd.web;

import cn.rsd.ReturnCode;
import cn.rsd.ReturnMessage;
import cn.rsd.dao.BuyerOrderMapper;
import cn.rsd.dao.OrderStateLogMapper;
import cn.rsd.dao.TrucksMapper;
import cn.rsd.dao.UsersMapper;
import cn.rsd.po.*;
import cn.rsd.service.OrderService;
import cn.rsd.service.WeiXinMessageService;
import cn.rsd.util.IpUtil;
import cn.rsd.util.Sign;
import cn.rsd.weixin.MyWXPayConfig;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Decoder;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author 焦云亮
 * @date 2018/7/30
 * @modifyUser
 * @modifyDate
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    protected static Logger logger = LogManager.getLogger(OrderController.class);

    @Autowired
    private BuyerOrderMapper buyerOrderMapper;

    @Autowired
    private TrucksMapper trucksMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Value("${orderTmpl}")
    private String orderTmpl;

    @Value("${webUrl}")
    private String webUrl;

    @Value("${uploadPath}")
    private String uploadPath;

    @Autowired
    private WeiXinMessageService weiXinMessageServie;

    @Autowired
    private OrderService orderService;

    @Autowired
    private  MyWXPayConfig myWXPayConfig;

    @Autowired
    private OrderStateLogMapper orderStateLogMapper;

    @ResponseBody
    @RequestMapping("/list")
    public ReturnMessage list(@RequestParam("state") List<Integer> states, HttpServletRequest request){
        Page<BuyerOrder> page = Page.createPageControl(request);
        ReturnMessage message = ReturnMessage.buildMessage();
        Users user = (Users) request.getSession().getAttribute("user");
        SupplyPosts supplyPosts =(SupplyPosts)request.getSession().getAttribute("supplyPosts");

        Example example = new Example(BuyerOrder.class);
        example.orderBy("state").asc();
        example.orderBy("createDate").desc();
        Example.Criteria criteria = example.createCriteria();

        BuyerOrder buyerOrder = new BuyerOrder();
        //买方
        if(user.getRole() == 3) {
            buyerOrder.setDistribution(user.getId());
        //司机
        }else if(user.getRole() == 4){
            buyerOrder.setBuyer(user.getId());
        //管理员
        }else if(user.getRole() == 2){
            buyerOrder.setSupplyId(supplyPosts.getId());
        }
        criteria.andEqualTo(buyerOrder);

        if(states != null && !states.isEmpty()) {
            criteria.andIn("state", states);
        }

        List<BuyerOrder> list = this.buyerOrderMapper.selectByExampleAndRowBounds(example,page);
        page.setList(list);
        message.setData(page.createMap());

        return message;
    }

    @ResponseBody
    @RequestMapping("/save")
    public ReturnMessage save(BuyerOrder buyerOrder,Integer times,HttpServletRequest request){
        ReturnMessage message = ReturnMessage.buildMessage();
        Users user = (Users) request.getSession().getAttribute("user");
        SupplyPosts supplyPosts = (SupplyPosts)request.getSession().getAttribute("supplyPosts");

            buyerOrder.setBuyer(user.getId());
            buyerOrder.setBuyerName(user.getCompany());
            buyerOrder.setState(2);
            buyerOrder.setSupplyId(supplyPosts.getId());
            buyerOrder.setCreateDate(new Date());
            buyerOrder.setUpdateDate(new Date());
            buyerOrder.setUpdateUser(user.getId());

            //金额固定
            buyerOrder.setPrice(425);
            //处理金额,把元单位乘100转成分.
            buyerOrder.setPrice(buyerOrder.getPrice() * 100);
            try {
                if(times == null || times == 1) {
                    orderService.saveOrder(buyerOrder);
                }else{
                    orderService.batchSaveOrder(buyerOrder,times);
                }
            } catch (Exception e) {
                message.setCode(ReturnCode.EXCEPTION.value());
                message.setMsg(e.getMessage());
                logger.error(e,e);
            }

        return message;
    }

    @RequestMapping("/fileUploadPicture")
    @ResponseBody
    public ReturnMessage uploadImg(String imgData,String fileName) throws IOException {
        ReturnMessage message = ReturnMessage.buildMessage();
        BASE64Decoder decoder = new BASE64Decoder();
        String dir = System.getProperty("rsd.root");
        String path = dir+ uploadPath ;

        //如果文件夹不存在
        File fileDir = new File(path);
        fileDir.mkdirs();

        String newFileName = UUID.randomUUID()+"."+ FilenameUtils.getExtension(fileName);
        byte[] b = decoder.decodeBuffer(imgData.split(",")[1]);
        FileCopyUtils.copy(b,new File(path+File.separator+newFileName));

        message.putData("newFileName",uploadPath+newFileName);
        return message;
    }

    @ResponseBody
    @RequestMapping("/rater")
    public ReturnMessage rater(Long id,Integer rater){
        ReturnMessage message = ReturnMessage.buildMessage();

        BuyerOrder buyerOrder = this.buyerOrderMapper.selectByPrimaryKey(id);

        if(buyerOrder != null){
            buyerOrder.setStarLevel(rater);
            this.buyerOrderMapper.updateByPrimaryKey(buyerOrder);
        }

        return message;
    }
    @ResponseBody
    @RequestMapping("/get")
    public ReturnMessage get(Long id,HttpServletRequest request){
        ReturnMessage message = ReturnMessage.buildMessage();
        Users user = (Users) request.getSession().getAttribute("user");
        BuyerOrder buyerOrder = this.buyerOrderMapper.selectByPrimaryKey(id);

        if(buyerOrder != null && user.getRole() == 4 && buyerOrder.getBuyer().equals(user.getId())) {
            message.putData("order", buyerOrder);
        }else if(buyerOrder != null && user.getRole() == 3 && buyerOrder.getDistribution().equals(user.getId())){
            message.putData("order",buyerOrder);
        }else if(user.getRole() == 2){
            message.putData("order",buyerOrder);
        }

        Trucks truck = new Trucks();
        truck.setUserId(buyerOrder.getDistribution());
        truck = this.trucksMapper.selectOne(truck);

        Users buyer = this.usersMapper.selectByPrimaryKey(buyerOrder.getBuyer());

        message.putData("buyer",buyer);
        message.putData("truck",truck);

        message.putData("usersList",request.getSession().getAttribute("usersList"));

        return message;
    }

    @RequestMapping(value="/reach",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage reachOrder(Long id, Double startNumber, Double endNumber, Double actualNumber,String startPic,String endPic,HttpServletRequest request){
        ReturnMessage message = ReturnMessage.buildMessage();
        Users user = (Users) request.getSession().getAttribute("user");

        BuyerOrder buyerOrder = new BuyerOrder();
        buyerOrder.setId(id);
        buyerOrder.setStartNumber(startNumber);
        buyerOrder.setEndNumber(endNumber);
        buyerOrder.setActualNumber(actualNumber);
        buyerOrder.setUpdateDate(new Date());
        buyerOrder.setUpdateUser(user.getId());

        try{

            if(!StringUtils.isEmpty(startPic)) {
                buyerOrder.setStartPic(startPic);
            }
            if(!StringUtils.isEmpty(endPic)) {
                buyerOrder.setEndPic(endPic);
            }
            this.orderService.reachOrder(buyerOrder);
        } catch (Exception e) {
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
            logger.error(e,e);

        }
        return message;
    }

    @RequestMapping(value="/weixin",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage weixin(Long id,HttpServletRequest request) throws Exception{
        ReturnMessage message = ReturnMessage.buildMessage();


        BuyerOrder buyerOrder = this.buyerOrderMapper.selectByPrimaryKey(id);

        if(buyerOrder == null){
            throw new Exception("无效订单!");
        }

        SortedMap<Object,Object> signMap = new TreeMap<>();
        try {
            WXPay wxPay = new WXPay(myWXPayConfig, false, false);

            if(buyerOrder.getPrepayId() == null || buyerOrder.getPrepayId().isEmpty()) {

                Users user = (Users) request.getSession().getAttribute("user");
                Map<String, String> data = new HashMap<>(20);
                data.put("body", "热量费用");
                data.put("out_trade_no", buyerOrder.getOrderCode()+"_"+WXPayUtil.getCurrentTimestamp());

                data.put("total_fee", buyerOrder.getPrice()+"");
                //先测试支付1分钱
                data.put("total_fee", "1");
                data.put("spbill_create_ip", IpUtil.getIpAddr(request));
                data.put("notify_url", webUrl + "wx/pay/msg");
                data.put("trade_type", "JSAPI");
                data.put("openid", user.getOpenId());

                Map<String, String> resp = wxPay.unifiedOrder(data);

                logger.debug(resp.get("err_code"));
                logger.debug(resp.get("err_code_des"));
                logger.debug(resp.get("return_msg"));

                if (WXPayConstants.SUCCESS.equals(resp.get("return_code")) && WXPayConstants.SUCCESS.equals(resp.get("result_code"))) {

                    message.putData("timeStamp",WXPayUtil.getCurrentTimestamp());
                    message.putData("nonceStr", data.get("nonce_str"));
                    message.putData("package", "prepay_id=" + resp.get("prepay_id"));
                    message.putData("signType", WXPayConstants.MD5);


                    signMap.put("appId", myWXPayConfig.getAppID());
                    signMap.put("timeStamp", message.getData().get("timeStamp") + "");
                    signMap.put("nonceStr", message.getData().get("nonceStr") + "");
                    signMap.put("package", "prepay_id=" + resp.get("prepay_id"));
                    signMap.put("signType", WXPayConstants.MD5);

                    message.putData("paySign",Sign.createSign(signMap, myWXPayConfig.getKey()));

                    buyerOrder.setPrepayId(resp.get("prepay_id"));
                    this.buyerOrderMapper.updateByPrimaryKey(buyerOrder);
                } else {
                    message.setCode(ReturnCode.EXCEPTION.value());
                    message.setMsg(resp.get("err_code_des"));
                }
            }else{
                message.putData("timeStamp", WXPayUtil.getCurrentTimestamp());
                message.putData("nonceStr", WXPayUtil.generateNonceStr());
                message.putData("package", "prepay_id=" + buyerOrder.getPrepayId());
                message.putData("signType", WXPayConstants.MD5);

                signMap.put("appId", myWXPayConfig.getAppID());
                signMap.put("timeStamp", message.getData().get("timeStamp") + "");
                signMap.put("nonceStr", message.getData().get("nonceStr") + "");
                signMap.put("package", "prepay_id=" + buyerOrder.getPrepayId());
                signMap.put("signType", WXPayConstants.MD5);
                message.putData("paySign",Sign.createSign(signMap, myWXPayConfig.getKey()));
                //message.putData("paySign", WXPayUtil.generateSignature(signMap,this.myWXPayConfig.getKey() ,WXPayConstants.SignType.MD5));
            }
        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
        }
        return message;
    }
    @RequestMapping(value="/pay",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage pay(Long id){
        ReturnMessage message = ReturnMessage.buildMessage();

        try{
            this.orderService.payOrder(id);
        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
        }

        return message;
    }

    @RequestMapping(value="/renew",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage renew(Long id,Long trucks,HttpServletRequest request){
        ReturnMessage message = ReturnMessage.buildMessage();
        Users user = (Users) request.getSession().getAttribute("user");

        try{

            BuyerOrder buyerOrder = this.buyerOrderMapper.selectByPrimaryKey(id);
            buyerOrder.setUpdateDate(new Date());
            buyerOrder.setUpdateUser(user.getId());

            this.orderService.renewOrder(buyerOrder,trucks);

        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
        }

        return message;
    }
    @ResponseBody
    @RequestMapping("/update")
    public ReturnMessage update(Long id,Integer state,HttpServletRequest request){
        ReturnMessage message = ReturnMessage.buildMessage();
        Users user = (Users) request.getSession().getAttribute("user");

        BuyerOrder buyerOrder = new BuyerOrder();
        buyerOrder.setId(id);
        buyerOrder.setState(state);
        buyerOrder.setUpdateDate(new Date());
        buyerOrder.setUpdateUser(user.getId());
        this.buyerOrderMapper.updateByPrimaryKeySelective(buyerOrder);

        OrderStateLog stateLog = new OrderStateLog();
        stateLog.setState(buyerOrder.getState());
        stateLog.setOrderId(buyerOrder.getId());
        stateLog.setCreateDate(buyerOrder.getUpdateDate());
        stateLog.setCreateUser(buyerOrder.getUpdateUser());
        try {
            this.orderService.saveOrderLog(stateLog);
        }catch (Exception e){
            logger.error(e,e);
        }
//        buyerOrder = this.buyerOrderMapper.selectByPrimaryKey(buyerOrder.getId());

//        if(state == 3) {
//
//            Users user = this.usersMapper.selectByPrimaryKey(buyerOrder.getBuyer());
//            if (user.getOpenId() != null) {
//                weiXinMessageServie.sendTmplMessage(user.getOpenId(), orderTmpl, "order_detail.html?id=" + buyerOrder.getId(), "您的订单已经确认！", "E" + buyerOrder.getId(),
//                        DateUtils.formatDate(buyerOrder.getCreateDate(),"yyyy-MM-dd HH:mm"),
//                        buyerOrder.getQuantity() + "热量(GJ)", buyerOrder.getBuyerName(), "E192231", "");
//            }
//        }else if(state == 1){
//            Users user = this.usersMapper.selectByPrimaryKey(2L);
//            if (user.getOpenId() != null) {
//                weiXinMessageServie.sendTmplMessage(user.getOpenId(), orderTmpl, "order_detail.html?id=" + buyerOrder.getId(), "你有新的订单需要确认！", "E" + buyerOrder.getId(),
//                        DateUtils.formatDate(buyerOrder.getCreateDate(),"yyyy-MM-dd HH:mm"),
//                        buyerOrder.getQuantity() + "热量(GJ)", buyerOrder.getBuyerName(), "E192231", "");
//            }
//        }
        return message;
    }

    @ResponseBody
    @RequestMapping("/statistic")
    public ReturnMessage statistic(HttpServletRequest request){
        ReturnMessage message = ReturnMessage.buildMessage();

        Users user = (Users) request.getSession().getAttribute("user");
        Long userId = null;
        if(user.getRole() == 2) {
            userId = user.getId();
        }

        message.putData("year",this.buyerOrderMapper.selectPriceByYear(userId));
        message.putData("month",this.buyerOrderMapper.selectPriceByMonth(userId));
        return message;
    }
}
