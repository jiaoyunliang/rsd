package cn.rsd.web;

import cn.rsd.ReturnMessage;
import cn.rsd.dao.UsersMapper;
import cn.rsd.po.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 焦云亮
 * @date 2018/8/31
 * @modifyUser
 * @modifyDate
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UsersMapper usersMapper;

    @ResponseBody
    @RequestMapping("/get")
    public ReturnMessage get(Long id, HttpServletRequest request){
        return null;
    }

    @ResponseBody
    @RequestMapping("/renew/pass")
    public ReturnMessage pass(String pass,HttpServletRequest request){
        ReturnMessage message = ReturnMessage.buildMessage();

        Users user = (Users) request.getSession().getAttribute("user");

        user.setUserPass(pass);
        this.usersMapper.updateByPrimaryKey(user);
        return message;
    }

}
