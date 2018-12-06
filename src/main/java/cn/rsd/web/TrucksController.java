package cn.rsd.web;

import cn.rsd.ReturnCode;
import cn.rsd.ReturnMessage;
import cn.rsd.dao.TrucksMapper;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author 焦云亮
 * @date 2018/10/25
 * @modifyUser
 * @modifyDate
 */
@Controller
@RequestMapping("/trucks")
public class TrucksController {
    protected static Logger logger = LogManager.getLogger(TrucksController.class);

    @Autowired
    private TrucksMapper trucksMapper;

    @RequestMapping(value = "list")
    @ResponseBody
    public ReturnMessage trucksList(Integer state, HttpServletRequest request){
        SupplyPosts supplyPosts = (SupplyPosts) request.getSession().getAttribute("supplyPosts");
        ReturnMessage message = ReturnMessage.buildMessage();
        Trucks trucks = new Trucks();

        trucks.setSupplyId(supplyPosts.getId());

        if(state != null) {
            trucks.setState(state);
        }

        List<Trucks> list= this.trucksMapper.select(trucks);
        message.putData("list",list);

        Users user = (Users) request.getSession().getAttribute("user");

        if(user == null || user.getRole() != 2){
            message.setMsg("没有权限");
            message.setCode(ReturnCode.EXCEPTION.value());
            message.getData().clear();
        }

        return message;
    }
    @RequestMapping(value = "await",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage await(Long id, HttpServletRequest request){
        ReturnMessage message = ReturnMessage.buildMessage();
        try{
            updateTruckState(id,1,request);
        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
        }

        return message;
    }

    @RequestMapping(value = "normal",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage normal(Long id, HttpServletRequest request){
        ReturnMessage message = ReturnMessage.buildMessage();
        try{
            updateTruckState(id,5,request);
        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
        }

        return message;
    }

    private void updateTruckState(Long id,Integer state, HttpServletRequest request)throws Exception{
        Users user = (Users) request.getSession().getAttribute("user");

        Trucks oldTrucks = this.trucksMapper.selectByPrimaryKey(id);

        if(user == null ||  ((user.getRole() == 3  && !user.getId().equals(oldTrucks.getUserId())))){
            throw new Exception("没有权限");
        }

        if(user.getRole() == 3 && oldTrucks.getState() != 5){
            throw new Exception("无法变更车辆状态!");
        }

        Trucks trucks = new Trucks();
        trucks.setId(id);
        trucks.setState(state);
        trucks.setUpdateDate(new Date());
        this.trucksMapper.updateByPrimaryKeySelective(trucks);
    }

    @RequestMapping(value = "outage",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage outage(Long id, HttpServletRequest request){
        ReturnMessage message = ReturnMessage.buildMessage();
        try{
            updateTruckState(id,3,request);
        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
        }

        return message;
    }

    @RequestMapping(value = "repair",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage repair(Long id, HttpServletRequest request){
        ReturnMessage message = ReturnMessage.buildMessage();

        try{
            updateTruckState(id,2,request);
        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
        }

        return message;
    }
}
