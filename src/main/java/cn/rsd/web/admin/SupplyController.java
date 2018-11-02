package cn.rsd.web.admin;

import cn.rsd.ReturnCode;
import cn.rsd.ReturnMessage;
import cn.rsd.dao.SupplyPostsMapper;
import cn.rsd.dao.UsersMapper;
import cn.rsd.po.Page;
import cn.rsd.po.SupplyPosts;
import cn.rsd.po.Users;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author 焦云亮
 * @date 2018/9/26
 * @modifyUser
 * @modifyDate
 */
@Controller("admin/supply")
@RequestMapping("/admin/supply")
public class SupplyController {

    protected static Logger logger = LogManager.getLogger(SupplyController.class);

    @Autowired
    private SupplyPostsMapper supplyPostsMapper;

    @Autowired
    private UsersMapper usersMapper;
    /**             供热点管理开始                                          **/
    @RequestMapping("index")
    public ModelAndView index(){
        ModelAndView view = new ModelAndView("supply");
        //获取司机列表
        Example example = new Example(Users.class);
        example.createCriteria().andEqualTo("role",2);
        List<Users> usersList = this.usersMapper.selectByExample(example);

        view.addObject("adminList",usersList);

        return view;
    }


    @RequestMapping(value = "list",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> supplyList(SupplyPosts supplyPosts, HttpServletRequest request){
        Page<SupplyPosts> page = Page.createPageControl(request);
        List<SupplyPosts> list= this.supplyPostsMapper.selectByRowBounds(supplyPosts,page);
        page.setList(list);
        Map<String,Object> map = page.createMap();

        return map;
    }

    @RequestMapping(value = "save",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage save(SupplyPosts supplyPosts){
        ReturnMessage message = ReturnMessage.buildMessage();

        try{
            SupplyPosts exits = new SupplyPosts();
            exits.setAdminId(supplyPosts.getAdminId());

            if(this.supplyPostsMapper.selectCount(exits) > 0){
                throw new Exception("此客服已经指定了供热点,请选择其它点.");
            }

            if(supplyPosts.getId() == null) {
                this.supplyPostsMapper.insert(supplyPosts);
            }else{
                this.supplyPostsMapper.updateByPrimaryKey(supplyPosts);
            }
        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
        }

        return message;
    }

    /**             用户管理结束                                          **/
}
