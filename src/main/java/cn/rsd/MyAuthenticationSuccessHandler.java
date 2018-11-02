package cn.rsd;


import cn.rsd.po.Menu;
import cn.rsd.po.Users;
import cn.rsd.service.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author 焦云亮
 * @date 2018/5/4
 * @modifyUser
 * @modifyDate
 */
@Component("myAuthenticationSuccessHandler")
public class MyAuthenticationSuccessHandler extends
        SavedRequestAwareAuthenticationSuccessHandler implements InitializingBean{

    @Autowired
    private UserService userService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        // 认证成功后，获取用户信息并添加到session中
        UserDetails userDetails;
        userDetails = (UserDetails) authentication.getPrincipal();
        try {
            Users user = userService.loadByUserName(userDetails.getUsername());
            List<Menu> menuList = userService.loadMenusByUserId(user.getId());
            request.getSession(true).setAttribute("user", user);
            request.getSession().setAttribute("menus",menuList);
        } catch (Exception e) {
            throw new IOException();
        }

        super.onAuthenticationSuccess(request, response, authentication);

    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl("/admin/index");
        this.setTargetUrlParameter("gurl");
    }
}
