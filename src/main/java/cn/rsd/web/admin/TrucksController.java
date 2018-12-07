package cn.rsd.web.admin;

import cn.rsd.ReturnCode;
import cn.rsd.ReturnMessage;
import cn.rsd.dao.SupplyPostsMapper;
import cn.rsd.dao.TrucksMapper;
import cn.rsd.dao.UsersMapper;
import cn.rsd.po.Page;
import cn.rsd.po.SupplyPosts;
import cn.rsd.po.Trucks;
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
 * @date 2018/10/19
 * @modifyUser
 * @modifyDate
 */
@Controller("admin/trucks")
@RequestMapping("/admin/trucks")
public class TrucksController {
    protected static Logger logger = LogManager.getLogger(TrucksController.class);

    @Autowired
    private TrucksMapper trucksMapper;

    @Autowired
    private SupplyPostsMapper supplyPostsMapper;

    @Autowired
    private UsersMapper usersMapper;
    /**             车辆管理开始                                          **/
    @RequestMapping("index")
    public ModelAndView index(){
        ModelAndView view = new ModelAndView("trucks");
        List<SupplyPosts> supplyPosts = this.supplyPostsMapper.selectAll();

        //获取司机列表
        Example example = new Example(Users.class);
        example.createCriteria().andEqualTo("role",3);
        List<Users> usersList = this.usersMapper.selectByExample(example);

        view.addObject("drivers",usersList);
        view.addObject("supplyPosts",supplyPosts);
        return view;
    }

    @RequestMapping(value = "list",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> trucksList(Trucks trucks, HttpServletRequest request){
        Page<Trucks> page = Page.createPageControl(request);
        List<Trucks> list= this.trucksMapper.selectByRowBounds(trucks,page);
        page.setList(list);
        Map<String,Object> map = page.createMap();

        return map;
    }

    @RequestMapping(value = "save",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage save(Trucks trucks){
        ReturnMessage message = ReturnMessage.buildMessage();

        try{

            Trucks whereTruck = new Trucks();
            whereTruck.setUserId(trucks.getUserId());
            whereTruck = this.trucksMapper.selectOne(whereTruck);

            if(whereTruck != null){
                //如果和查找到来的对象id不等于才报重复异常.
                if(!whereTruck.getId().equals(trucks.getId())) {
                    throw new Exception("一个用户只能绑定一个车辆!");
                }
            }

            if(trucks.getId() == null) {
                trucks.setState(Trucks.TrucksStateEnum.HEAT_STORAGE.value());
                this.trucksMapper.insert(trucks);
            }else{
                this.trucksMapper.updateByPrimaryKey(trucks);
            }
        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
        }

        return message;
    }


    @RequestMapping(value = "normal",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage normal(Long id){
        ReturnMessage message = ReturnMessage.buildMessage();
        try{
            Trucks trucks = new Trucks();
            trucks.setId(id);
            trucks.setState(1);
            this.trucksMapper.updateByPrimaryKeySelective(trucks);

        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
        }

        return message;
    }

    @RequestMapping(value = "outage",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage outage(Long id){
        ReturnMessage message = ReturnMessage.buildMessage();
        try{
            Trucks trucks = new Trucks();
            trucks.setId(id);
            trucks.setState(3);
            this.trucksMapper.updateByPrimaryKeySelective(trucks);

        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
        }

        return message;
    }

    @RequestMapping(value = "repair",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage repair(Long id){
        ReturnMessage message = ReturnMessage.buildMessage();

        try{
            Trucks trucks = new Trucks();
            trucks.setId(id);
            trucks.setState(2);
            this.trucksMapper.updateByPrimaryKeySelective(trucks);

        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
        }

        return message;
    }
}
