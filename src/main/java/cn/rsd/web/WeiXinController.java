package cn.rsd.web;

import cn.rsd.ReturnMessage;
import cn.rsd.dao.SupplyPostsMapper;
import cn.rsd.dao.TrucksMapper;
import cn.rsd.dao.UserAccountMapper;
import cn.rsd.dao.UsersMapper;
import cn.rsd.po.SupplyPosts;
import cn.rsd.po.UserAccount;
import cn.rsd.po.Users;
import cn.rsd.service.AccessTokenService;
import cn.rsd.service.OrderService;
import cn.rsd.service.WeiXinMessageService;
import cn.rsd.util.HttpClient4;
import cn.rsd.util.Sign;
import cn.rsd.weixin.MyWXPayConfig;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.http.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * @author 焦云亮
 * @date 2018/7/31
 * @modifyUser
 * @modifyDate
 */
@Controller
@RequestMapping("/wx")
public class WeiXinController {

    protected static Logger logger = LogManager.getLogger(WeiXinController.class);

    @Value("${appId}")
    private String appId;
    @Value("${appSecret}")
    private String appSecret;
    @Value("${token}")
    private String token;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private SupplyPostsMapper supplyPostsMapper;

    @Autowired
    private TrucksMapper trucksMapper;

    @Autowired
    private  MyWXPayConfig myWXPayConfig;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private AccessTokenService accessTokenServie;

    @Autowired
    private WeiXinMessageService weiXinMessageServie;

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/msg",method = {RequestMethod.GET})
    @ResponseBody
    public void msg(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //获取Get请求携带参数
        String content=request.getQueryString();

        // 微信加密签名
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");

        PrintWriter out = response.getWriter();

        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        //if (SignUtil.checkSignature(signature, timestamp, nonce,token)) {
            response.addHeader("Content-Type", "text/html; charset=UTF-8");
            out.print(echostr);

        //}

        out.close();

    }

    @ResponseBody
    @RequestMapping(value = "/msg",method = {RequestMethod.POST})
    public String msg(HttpServletRequest request) throws Exception {
        return weiXinMessageServie.processRequest(request);
    }

    @RequestMapping(value = "/pay/msg",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String payMsg(@RequestBody String notifyData) throws Exception {

        logger.debug("通知消息={}",notifyData);

        WXPay wxPay = new WXPay(myWXPayConfig,true,true);
        String xml = setXml(WXPayConstants.SUCCESS,WXPayConstants.OK);
        // 转换成map
        Map<String, String> notifyMap = WXPayUtil.xmlToMap(notifyData);

        if (wxPay.isPayResultNotifySignatureValid(notifyMap)) {
            // 签名正确
            // 进行处理。
            // 注意特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户侧订单状态从退款改成支付成功
            String outTradeNo = notifyMap.get("out_trade_no");
            String fee = notifyMap.get("total_fee");
            String orderCode = outTradeNo.substring(0, outTradeNo.indexOf("_"));

            logger.debug("接收到支付成功订单信息={}.",orderCode);
            this.orderService.completeOrder(orderCode,new Integer(fee),notifyMap.get("transaction_id"));
        }
        else {
            xml = setXml(WXPayConstants.FAIL,"签名错误");
            // 签名错误，如果数据里没有sign字段，也认为是签名错误
        }
        return xml;
    }

    private String setXml(String return_code, String return_msg) {
        return "<xml><return_code><![CDATA[" + return_code + "]]>" +
                "</return_code><return_msg><![CDATA[" + return_msg + "]]></return_msg></xml>";
    }

    @ResponseBody
    @RequestMapping(value = "/view",method = {RequestMethod.GET})
    public void view(String code,String key,HttpServletRequest request,HttpServletResponse response){

        String openid = accessTokenServie.getOauth2OpendId(code);
        Users user = new Users();
        user.setOpenId(openid);

        List<Users> users = this.usersMapper.select(user);

        String queryFu = "?";
        if(key.indexOf("?") > -1){
            queryFu = "&";
        }

        String forwardUrl = "/index.html?openid="+openid;

        if(!users.isEmpty()){
            user = users.get(0);

            //获取用户管理的供热点信息
            SupplyPosts supplyPosts = new SupplyPosts();

            if(user.getRole() == 2) {
                supplyPosts.setAdminId(user.getId());
                supplyPosts = this.supplyPostsMapper.selectOne(supplyPosts);

                //获取供热点下所有用户信息
                if (supplyPosts != null) {
                    List<Users> usersList = this.supplyPostsMapper.selectUserBySupplyPosts(supplyPosts.getId());

                    request.getSession().setAttribute("usersList", usersList);
                }
            }else if(user.getRole() == 3){
                List<SupplyPosts> supplyPostsList = this.supplyPostsMapper.selectSupplyPostsByUser(user.getId());

                supplyPosts = supplyPostsList.get(0);
            }else if(user.getRole() == 4){
                List<SupplyPosts> supplyPostsList = this.supplyPostsMapper.selectSupplyPostsByUser(user.getId());

                supplyPosts = supplyPostsList.get(0);

                UserAccount userAccount = new UserAccount();
                userAccount.setUserId(user.getId());
                userAccount = this.userAccountMapper.selectOne(userAccount);

                request.getSession().setAttribute("account",userAccount);
            }

            request.getSession().setAttribute("user",user);
            request.getSession().setAttribute("supplyPosts",supplyPosts);

            logger.debug("key={},角色={}",key,user.getRole());
            if(key != null && key.equals("order_new.html") && user != null && user.getRole() != null && user.getRole() == 3){
                key = "order_send.html";
            }
            if(key != null && key.equals("order_new.html") && user != null && user.getRole() != null && user.getRole() == 2){
                key = "order.html";
            }
            logger.debug("key={},角色={}",key,user.getRole());
            forwardUrl = "/"+key+queryFu+"openid="+openid;
        }


        request.getSession().setAttribute("referer",key);

        response.setStatus(302);
        response.setHeader("location", forwardUrl);
    }

    @ResponseBody
    @RequestMapping("jssignature")
    public ReturnMessage jsconfig(String pageurl) throws UnsupportedEncodingException {
        ReturnMessage message = ReturnMessage.buildMessage();
        message.setData(Sign.sign(appId,accessTokenServie.getJsApiTicket(), URLDecoder.decode(pageurl,"UTF-8")));
        return message;
    }

    @ResponseBody
    @RequestMapping(value = "/menu",method = {RequestMethod.POST})
    public String menu(String json){

        String url = MessageFormat.format("https://api.weixin.qq.com/cgi-bin/menu/create?access_token={0}",accessTokenServie.getAccessToken());

        logger.debug(url);

        StringEntity entity = null;
        String result = null;
        try {
            entity = new StringEntity(json,"utf-8");
            result = HttpClient4.post(url,null,null,entity);
        } catch (UnsupportedEncodingException e) {
            logger.error(e,e);
        } catch (Exception e) {
            logger.error(e,e);
        }

        return result;
    }
    @ResponseBody
    @RequestMapping(value = "/menu",method = {RequestMethod.GET})
    public String downMedia(String mediaId){
        String url = MessageFormat.format("https://api.weixin.qq.com/cgi-bin/media/get?access_token={0}&media_id={1}",accessTokenServie.getAccessToken(),mediaId);


        return "";
    }
}
