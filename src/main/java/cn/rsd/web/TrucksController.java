package cn.rsd.web;

import cn.rsd.ReturnCode;
import cn.rsd.ReturnMessage;
import cn.rsd.dao.TrucksMapper;
import cn.rsd.po.SupplyPosts;
import cn.rsd.po.Trucks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
        return message;
    }


    @RequestMapping(value = "normal",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage normal(Long id){
        ReturnMessage message = ReturnMessage.buildMessage();
        try{
            updateTruckState(id,1);
        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
        }

        return message;
    }

    private void updateTruckState(Long id,Integer state)throws Exception{
        Trucks trucks = new Trucks();
        trucks.setId(id);
        trucks.setState(state);
        this.trucksMapper.updateByPrimaryKeySelective(trucks);
    }

    @RequestMapping(value = "outage",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage outage(Long id){
        ReturnMessage message = ReturnMessage.buildMessage();
        try{
            updateTruckState(id,3);
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
            updateTruckState(id,2);
        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
        }

        return message;
    }
}
