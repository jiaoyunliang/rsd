package cn.rsd.service;

import cn.rsd.dao.*;
import cn.rsd.po.*;
import cn.rsd.util.HttpClient4;
import cn.rsd.util.MessageUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.entity.StringEntity;
import org.apache.ibatis.session.RowBounds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * @author 焦云亮
 * @date 2018/8/28
 * @modifyUser
 * @modifyDate
 */
@Service
public class WeiXinMessageService {
    protected static Logger logger = LogManager.getLogger(WeiXinMessageService.class);

    @Value("${appId}")
    private String appId;

    @Value("${orderTmpl}")
    private String orderTmpl;

    @Value("${alert.time}")
    private Integer alertTime;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private BuyerOrderMapper buyerOrderMapper;

    @Autowired
    private SupplyPostsMapper supplyPostsMapper;

    @Autowired
    private MsgSendLogMapper msgSendLogMapper;

    @Autowired
    private AccessTokenService accessTokenServie;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private TrucksMapper trucksMapper;

    public static String WEIXIN_WEB_URL ="https://open.weixin.qq.com/connect/oauth2/authorize?appid={0}&redirect_uri=http://www.jxzyn.cn/wx/view?key={1}&response_type=code&scope=snsapi_base&state=1#wechat_redirect";

    @Scheduled(fixedDelay = 1000*60,initialDelay = 100)
    public void sendTmplMessage(){
        Example example = new Example(BuyerOrder.class);

        //查找所有待接单订单信息
        example.createCriteria().andEqualTo("state",BuyerOrder.BuyerOrderStateEnum.PREPARE);
        List<BuyerOrder> buyerOrderList = this.buyerOrderMapper.selectByExample(example);

        for(BuyerOrder buyerOrder :buyerOrderList){
            DateTime update = new DateTime(buyerOrder.getUpdateDate());
            DateTime now = DateTime.now();

            Period period = new Period(update, now, PeriodType.minutes());

            //判断时间是否到达提醒时间,如果到达则发送提醒.
            if(period.getMinutes() >= alertTime){
                SupplyPosts supplyPost = this.supplyPostsMapper.selectByPrimaryKey(buyerOrder.getSupplyId());

                Users adminUser = this.usersMapper.selectByPrimaryKey(supplyPost.getAdminId());

                Trucks truck = new Trucks();
                truck.setUserId(adminUser.getId());
                truck = this.trucksMapper.selectOne(truck);
                if(adminUser != null && adminUser.getOpenId() != null) {
                    addAppIdTmplMessage(adminUser.getAppId(), adminUser.getOpenId(), orderTmpl, "order_detail_send_new.html?id=" + buyerOrder.getId(), "你有新的订单需要运输！", buyerOrder.getOrderCode(),
                            DateUtils.formatDate(buyerOrder.getCreateDate(), "yyyy-MM-dd HH:mm"),
                            "热量", buyerOrder.getBuyerName(), truck.getPlateNumber(), "");
                }
            }
        }

    }

    @Scheduled(fixedDelay = 3000,initialDelay = 100)
    public void sendFailTmplMessage(){
        //查找所有未发送成功的模板信息
        List<MsgSendLog> msgSendLogs = this.msgSendLogMapper.selectByRowBounds(null,new RowBounds(0,20));

        for(MsgSendLog msgSendLog:msgSendLogs){
            String megUrl =  MessageFormat.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token={0}",accessTokenServie.getAccessToken(msgSendLog.getAppId()));

            JSONObject json = JSON.parseObject(msgSendLog.getMsgBody());
            json.put("touser",msgSendLog.getOpenId());
            StringEntity entity = new StringEntity(json.toJSONString(),"utf-8");

            try {
                String result = HttpClient4.post(megUrl,null,null,entity);
                System.out.println(result);
                JSONObject resultJson = JSON.parseObject(result);
                if(resultJson.get("msgid") != null){
                    this.msgSendLogMapper.deleteByPrimaryKey(msgSendLog.getId());
                }
            } catch (Exception e) {
                logger.error(e,e);
            }
        }
    }

    public String addAppIdTmplMessage(String appId,String openId,String tmplId,String url,String... msg){

        String result = "";
        JSONObject json = JSON.parseObject("{}");

        json.put("touser",openId);
        json.put("template_id",tmplId);
        json.put("url",MessageFormat.format(WEIXIN_WEB_URL,appId,url));

        JSONObject data = JSON.parseObject("{}");

        for(int i = 0 ; i < msg.length ;i++){
            if(i == 0) {
                data.put("first",JSON.parseObject("{value:\""+msg[i]+"\"}"));
            }else if(i == msg.length){
                data.put("remark",JSON.parseObject("{value:\""+msg[i]+"\"}"));
            }else{
                data.put("keyword"+i,JSON.parseObject("{value:\""+msg[i]+"\"}"));
            }
        }

        json.put("data",data);

        try {
            if(openId != null && !StringUtils.isEmpty(openId)) {
                MsgSendLog msgSendLog = new MsgSendLog();
                msgSendLog.setMsgBody(json.toJSONString());
                msgSendLog.setResultStr(result);
                msgSendLog.setAppId(appId);
                msgSendLog.setOpenId(openId);
                this.msgSendLogMapper.insert(msgSendLog);
            }
        } catch (Exception e) {
            logger.error(e,e);
        }

        return result;
    }
    public String sendAppIdTmplMessage(String appId,String openId,String tmplId,String url,String... msg){
        String megUrl =  MessageFormat.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token={0}",accessTokenServie.getAccessToken(appId));
        String result = "";
        JSONObject json = JSON.parseObject("{}");

        json.put("touser",openId);
        json.put("template_id",tmplId);
        json.put("url",MessageFormat.format(WEIXIN_WEB_URL,appId,url));

        JSONObject data = JSON.parseObject("{}");

        for(int i = 0 ; i < msg.length ;i++){
            if(i == 0) {
                data.put("first",JSON.parseObject("{value:\""+msg[i]+"\"}"));
            }else if(i == msg.length){
                data.put("remark",JSON.parseObject("{value:\""+msg[i]+"\"}"));
            }else{
                data.put("keyword"+i,JSON.parseObject("{value:\""+msg[i]+"\"}"));
            }
        }

        json.put("data",data);

        StringEntity entity = new StringEntity(json.toJSONString(),"utf-8");
        try {
            result = HttpClient4.post(megUrl,null,null,entity);

            JSONObject resultJson = JSON.parseObject(result);

            if(resultJson.get("msgid") == null && openId != null && !StringUtils.isEmpty(openId)){
                MsgSendLog msgSendLog = new MsgSendLog();
                msgSendLog.setMsgBody(json.toJSONString());
                msgSendLog.setResultStr(result);
                msgSendLog.setAppId(appId);
                msgSendLog.setOpenId(openId);
                this.msgSendLogMapper.insert(msgSendLog);
            }

        } catch (Exception e) {
            logger.error(e,e);
        }

        return result;
    }
    public String sendTmplMessage(String openId,String tmplId,String url,String... msg){

        return sendAppIdTmplMessage(appId,openId,tmplId,url,msg);

    }

    public String processRequest(HttpServletRequest request) {
        // xml格式的消息数据
        String respXml = "";

        try {
            // 调用parseXml方法解析请求消息
            Map<String,String> requestMap = MessageUtil.parseXml(request);
            // 发送方帐号
            String fromUserName = requestMap.get("FromUserName");
            // 开发者微信号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");

            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                // 事件类型
                String eventType = requestMap.get("Event");

                // 关注
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    Example example = new Example(Users.class);
                    example.createCriteria().andEqualTo("openId",fromUserName);

                    if(this.usersMapper.selectCountByExample(example) == 0){

                    }
                }
                // 取消关注
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                    // TODO 取消订阅后用户不会再收到公众账号发送的消息，因此不需要回复
                }
                // 扫描带参数二维码
                else if (eventType.equals(MessageUtil.EVENT_TYPE_SCAN)) {
                    // TODO 处理扫描带参数二维码事件
                }
                // 上报地理位置
                else if (eventType.equals(MessageUtil.EVENT_TYPE_LOCATION)) {
                    // TODO 处理上报地理位置事件
                    String latitude = requestMap.get("Latitude");
                    String longitude = requestMap.get("Longitude");
                    String precision = requestMap.get("Precision");

                    this.redisTemplate.opsForValue().set("latitude_"+fromUserName,latitude);
                    this.redisTemplate.opsForValue().set("longitude_"+fromUserName,longitude);
                    this.redisTemplate.opsForValue().set("precision_"+fromUserName,precision);
                }
                // 自定义菜单
                else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
                    // TODO 处理菜单点击事件
                }
            }
        }catch (Exception e){
            logger.error(e,e);
        }
        return respXml;
    }

}
