package cn.rsd.web.admin;

import cn.rsd.ReturnCode;
import cn.rsd.ReturnMessage;
import cn.rsd.dao.DataLogMapper;
import cn.rsd.dao.SupplyPostsMapper;
import cn.rsd.dao.UsersMapper;
import cn.rsd.po.DataLog;
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
@Controller("admin/data")
@RequestMapping("/admin/data")
public class DataController {

    protected static Logger logger = LogManager.getLogger(DataController.class);

    @Autowired
    private DataLogMapper dataLogMapper;

    @Autowired
    private UsersMapper usersMapper;
    /**             供热点管理开始                                          **/
    @RequestMapping("index")
    public ModelAndView index(HttpServletRequest request){
        ModelAndView view = new ModelAndView("data");

        return view;
    }


    @RequestMapping(value = "list",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> supplyList( HttpServletRequest request){
        Page<DataLog> page = Page.createPageControl(request);

        Example example = new Example(DataLog.class);

        example.createCriteria().andCondition(" val <= 200 ").andCondition(" byte_str like '3223%'");
        example.orderBy("createDate").desc();
        List<DataLog> list= this.dataLogMapper.selectByExampleAndRowBounds(example,page);
        page.setList(list);
        Map<String,Object> map = page.createMap();

        return map;
    }

}
