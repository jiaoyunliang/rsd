package cn.rsd;

import cn.rsd.po.Users;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author 焦云亮
 * @date 2018/7/30
 * @modifyUser
 * @modifyDate
 */
public class LoginInterceptor implements HandlerInterceptor {
    protected static Logger logger = LogManager.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求的URL
        String url = request.getRequestURI();
        //URL:login.jsp是公开的;这个demo是除了login.jsp是可以公开访问的，其它的URL都进行拦截控制

        //获取Session
        HttpSession session = request.getSession();

        if(url.indexOf("/admin/")>=0 || url.indexOf("/wx/")>=0 || (url.indexOf("login")>=0 && !url.endsWith(".html")) || url.indexOf("index.html")>=0 ){
            return true;
        }

        if(session.getAttribute("user") != null){
            //权限配置
            Users user = (Users) session.getAttribute("user");


            Map<String,Set<String>> urls = new HashMap<>(3);

            Set<String> ggUrl = new HashSet<>();
            Set<String> sjUrl = new HashSet<>();
            Set<String> mfUrl = new HashSet<>();
            Set<String> kfUrl = new HashSet<>();

            ggUrl.add("/index.html");
            ggUrl.add("/user_pass.html");
            ggUrl.add("/wx/jssignature");
            ggUrl.add("/location");
            ggUrl.add("/login");
            ggUrl.add("/wx/jssignature");
            ggUrl.add("/order/list");
            ggUrl.add("/order/get");
            ggUrl.add("/user");
            ggUrl.add("/order_msg.html");
            ggUrl.add("/order_warn.html");
            ggUrl.add("/test.html");

            sjUrl.addAll(ggUrl);
            mfUrl.addAll(ggUrl);
            kfUrl.addAll(ggUrl);

            sjUrl.add("/order_send.html");
            sjUrl.add("/order_detail_send_new.html");
            sjUrl.add("/send_user.html");
            urls.put("role"+3L, sjUrl);

            kfUrl.add("/order_detail_preview.html");
            kfUrl.add("/trucks.html");
            kfUrl.add("/order.html");
            kfUrl.add("/login_user.html");
            urls.put("role"+2L, kfUrl);

            mfUrl.add("/order_detail_new.html");
            mfUrl.add("/order_new.html");
            mfUrl.add("/request_order_new.html");
            mfUrl.add("/order_pay.html");
            mfUrl.add("/order/pay");
            mfUrl.add("/order/weixin");
            mfUrl.add("/login_user.html");
            urls.put("role"+4L, mfUrl);

            Set<String> currUrls =  urls.get("role"+user.getRole());

            System.out.println(url);
            if(url.endsWith(".html") && !currUrls.contains(url)){
                response.setStatus(302);
                response.setHeader("location", "/order_warn.html?openid="+request.getParameter("openid"));
            }
            return true;
        }

        response.setStatus(302);
        response.setHeader("location", "/index.html?openid="+request.getParameter("openid"));
        //不符合条件的，跳转到登录界面

        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
