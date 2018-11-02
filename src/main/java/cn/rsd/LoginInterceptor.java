package cn.rsd;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

        if(url.indexOf("/admin/")>=0 || url.indexOf("/wx/")>=0 || url.indexOf("login")>=0 || url.indexOf("index.html")>=0){
            return true;
        }

        if(session.getAttribute("user") != null){
            return true;
        }

        response.setStatus(302);
        response.setHeader("location", "index.html?openid="+request.getParameter("openid"));
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
