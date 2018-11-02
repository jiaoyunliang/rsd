package cn.rsd.service;

import cn.rsd.util.HttpClient4;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

/**
 * @author 焦云亮
 * @date 2018/8/28
 * @modifyUser
 * @modifyDate
 */
@Service
public class AccessTokenService {

    @Value("${appId}")
    private String appId;
    @Value("${appSecret}")
    private String appSecret;

    @Autowired
    public StringRedisTemplate redisTemplate;
    public static String ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}";
    public static String JS_API_TICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token={0}&type=jsapi";
    protected static Logger logger = LogManager.getLogger(AccessTokenService.class);

    @Scheduled(fixedDelay = 1000*7000,initialDelay = 100)
    public void jsApiTicket(){
        logger.debug("开始获取jsApiTicket");
        String accessToken = this.getAccessToken();
        //如果没有取到访问票据,自旋等待.
        while (accessToken == null){
            accessToken = this.getAccessToken();
        }

        String url = MessageFormat.format(JS_API_TICKET, accessToken);
        try {
            String result = HttpClient4.get(url);

            if (result != null) {
                logger.debug(result);
                JSONObject jsonObject = JSON.parseObject(result);

                String ticket = jsonObject.getString("ticket");

                if(ticket != null) {
                    redisTemplate.opsForValue().set(appId+"_js_ticket", ticket, 7100, TimeUnit.SECONDS);
                }
            }

        }catch (Exception e){
            logger.error(e,e);
        }
        logger.debug("结束获取jsApiTicket");
    }
    @Scheduled(fixedDelay = 1000*7000,initialDelay = 1)
    public void accessToken(){
        logger.debug("开始获取accessToken");
        try {

            String url = MessageFormat.format(ACCESS_TOKEN, appId, appSecret);
            String result = HttpClient4.get(url);

            if (result != null) {
                logger.debug(result);
                JSONObject jsonObject = JSON.parseObject(result);

                String accessToken = jsonObject.getString("access_token");

                if(accessToken != null) {
                    redisTemplate.opsForValue().set(appId+"_access_token", accessToken, 7100, TimeUnit.SECONDS);
                }
            }

        } catch (Exception e) {
            logger.error(e,e);
        }

        logger.debug("结束获取accessToken");
    }

    public String getAccessToken(){
        return redisTemplate.opsForValue().get(appId+"_access_token");
    }

    public String getAccessToken(String appId){
        return redisTemplate.opsForValue().get(appId+"_access_token");
    }

    public String getJsApiTicket(){
        return redisTemplate.opsForValue().get(appId+"_js_ticket");
    }

    public String getOauth2OpendId(String code){
        String openId = null;
        String url = MessageFormat.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid={0}&secret={1}&code={2}&grant_type=authorization_code",appId,appSecret,code);
        logger.debug(url);

        try {
            String result =  HttpClient4.get(url);
            logger.debug(result);
            JSONObject jsonObject = JSON.parseObject(result);

            openId = jsonObject.getString("openid");
        } catch (Exception e) {
            logger.error(e,e);
        }

        return openId;
    }
}
