package cn.rsd.web.admin;

import cn.rsd.ReturnCode;
import cn.rsd.ReturnMessage;
import cn.rsd.dao.MeterDataMapper;
import cn.rsd.po.MeterData;
import cn.rsd.po.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 焦云亮
 * @date 2018/12/4
 * @modifyUser
 * @modifyDate
 */
@Controller
@RequestMapping("/admin/meter")
public class MeterDataController {

    protected static Logger logger = LogManager.getLogger(MeterDataController.class);

    @Autowired
    private MeterDataMapper meterDataMapper;

    @RequestMapping("index")
    public ModelAndView index(){
        ModelAndView view = new ModelAndView("meter");

        List<Map<String,String>> userTypes = new ArrayList<>();

        List<Map<String,String>> industryCategoryList = new ArrayList<>();

        userTypes.add(new HashMap<String, String>(10){{
            put("val","1");
            put("text","电厂记录表");
        }});
        userTypes.add(new HashMap<String, String>(10){{
            put("val","2");
            put("text","用户点记录表");
        }});
        view.addObject("userTypes",userTypes);

        view.addObject("industryCategoryList",industryCategoryList);

        industryCategoryList.add(new HashMap<String, String>(10){{
            put("val","展厅");
            put("text","展厅");
        }});

        industryCategoryList.add(new HashMap<String, String>(10){{
            put("val","工厂");
            put("text","工厂");
        }});
        industryCategoryList.add(new HashMap<String, String>(10){{
            put("val","学校");
            put("text","学校");
        }});

        return view;
    }

    @RequestMapping(value = "list",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> meterList(MeterData meterData, HttpServletRequest request){
        Page<MeterData> page = Page.createPageControl(request);
        List<MeterData> list= this.meterDataMapper.selectByRowBounds(meterData,page);
        page.setList(list);
        Map<String,Object> map = page.createMap();

        return map;
    }

    @RequestMapping(value = "save",method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage save(MeterData meterData){
        ReturnMessage message = ReturnMessage.buildMessage();

        try{
            if(meterData.getId() == null) {
                this.meterDataMapper.insert(meterData);
            }else{
                this.meterDataMapper.updateByPrimaryKey(meterData);
            }
        }catch (Exception e){
            logger.error(e,e);
            message.setCode(ReturnCode.EXCEPTION.value());
            message.setMsg(e.getMessage());
        }

        return message;
    }
}
