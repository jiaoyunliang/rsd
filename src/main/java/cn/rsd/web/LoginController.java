package cn.rsd.web;

import cn.rsd.ReturnCode;
import cn.rsd.ReturnMessage;
import cn.rsd.dao.SupplyPostsMapper;
import cn.rsd.dao.TrucksMapper;
import cn.rsd.dao.UserAccountMapper;
import cn.rsd.dao.UsersMapper;
import cn.rsd.po.SupplyPosts;
import cn.rsd.po.Trucks;
import cn.rsd.po.UserAccount;
import cn.rsd.po.Users;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 焦云亮
 * @date 2018/7/30
 * @modifyUser
 * @modifyDate
 */
@Controller
@RequestMapping("/")
public class LoginController {
    protected static Logger logger = LogManager.getLogger(LoginController.class);
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private SupplyPostsMapper supplyPostsMapper;

    @Autowired
    private TrucksMapper trucksMapper;

    @Value("${appId}")
    private String appId;
    @Value("${orderTmpl}")
    private String orderTmpl;
    @Value("${webUrl}")
    private String webUrl;
    @Value("${appSecret}")
    private String appSecret;

    @RequestMapping("login")
    @ResponseBody
    public ReturnMessage login(Users users,String openid,HttpServletRequest request){
        ReturnMessage message = ReturnMessage.buildMessage();
        Users user = this.usersMapper.selectOne(users);

        if(user == null){
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg("用户名或密码错误!");
        }else{

            if(openid != null){
                //一个openid只能绑定一个用户,这里删除掉该ID绑定的用户.
                this.usersMapper.updateUserOpendIdIsNull(openid);

                user.setOpenId(openid);
                user.setAppId(this.appId);
                user.setOrderTmpl(this.orderTmpl);
                user.setWebUrl(this.webUrl);
                this.usersMapper.updateByPrimaryKey(user);
            }

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

            request.getSession().setAttribute("supplyPosts",supplyPosts);
            request.getSession().setAttribute("user",user);

        }

        logger.debug("key={}",request.getSession().getAttribute("referer"));
        String referer = (String)request.getSession().getAttribute("referer");

        if(referer != null && referer.equals("order_new.html") && user.getRole() == 3){
            referer = "order_send.html";
        }
        message.putData("referer",referer);
        message.putData("user",user);
        return message;
    }

    @RequestMapping("logout")
    public String logout(HttpServletRequest request){

        Users user = (Users)request.getSession().getAttribute("user");
        String openId = user.getOpenId();
/**退出先不把openid,变空.
        user.setOpenId(" ");
        this.usersMapper.updateByPrimaryKey(user);
*/
        request.getSession().removeAttribute("user");
        request.getSession().invalidate();

        return "redirect:index.html?openid="+openId;
    }

    @RequestMapping("location")
    @ResponseBody
    public ReturnMessage location(String latitude,String longitude,String openid){
        ReturnMessage message = ReturnMessage.buildMessage();
        Users user = new Users();
        user.setLatitude(latitude);
        user.setLongitude(longitude);
        try {
            if(openid != null) {
                Example example = new Example(Users.class);

                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("openId", openid);

                this.usersMapper.updateByExampleSelective(user, example);
            }
        }catch (Exception e){
            logger.error(e,e);
        }
        return message;
    }

    @RequestMapping("user")
    @ResponseBody
    public ReturnMessage user(HttpServletRequest request){
        ReturnMessage message = ReturnMessage.buildMessage();
        Users user = (Users)request.getSession().getAttribute("user");
        UserAccount userAccount = (UserAccount)request.getSession().getAttribute("account");
        if(user == null){
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg("未找到登录信息!");
        }else{
            request.getSession().setAttribute("user",user);

            Trucks truck = new Trucks();
            truck.setUserId(user.getId());
            truck = this.trucksMapper.selectOne(truck);

            if(userAccount == null) {
                userAccount = new UserAccount();
                userAccount.setUserId(user.getId());
                userAccount = this.userAccountMapper.selectOne(userAccount);
                request.getSession().setAttribute("account",userAccount);
            }

            message.putData("user",user);
            message.putData("account",userAccount);
            message.putData("truck",truck);
        }
        return message;
    }
}
