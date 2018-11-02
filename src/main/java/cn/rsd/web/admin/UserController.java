package cn.rsd.web.admin;

import cn.rsd.ReturnCode;
import cn.rsd.ReturnMessage;
import cn.rsd.dao.RolesMapper;
import cn.rsd.dao.SupplyPostsMapper;
import cn.rsd.dao.UsersMapper;
import cn.rsd.po.*;
import cn.rsd.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author 焦云亮
 * @date 2018/9/26
 * @modifyUser
 * @modifyDate
 */
@Controller("admin/user")
@RequestMapping("/admin/user")
public class UserController {
    protected static Logger logger = LogManager.getLogger(UserController.class);
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private RolesMapper rolesMapper;

    @Autowired
    private SupplyPostsMapper supplyPostsMapper;

    @Autowired
    private UserService userService;
    /**             用户管理开始                                          **/
    @RequestMapping("index")
    public ModelAndView index(){
        ModelAndView view = new ModelAndView("users");

        List<SupplyPosts> supplyPosts = this.supplyPostsMapper.selectAll();
        List<Roles> roles = this.rolesMapper.selectAll();

        view.addObject("supplyPosts",supplyPosts);
        view.addObject("roles",roles);
        return view;
    }


    @RequestMapping(value = "list",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> usersList(Users users, HttpServletRequest request){
        Page<Users> page = Page.createPageControl(request);
        List<Users> list= this.usersMapper.selectByRowBounds(users,page);
        page.setList(list);
        Map<String,Object> map = page.createMap();

        return map;
    }

    @RequestMapping(value = "save",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage save(Users users,Long supplyPostId){
        ReturnMessage message = ReturnMessage.buildMessage();

        try{
            userService.save(users,supplyPostId);
        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
        }

        return message;
    }
    /**             用户管理结束                                          **/
}
