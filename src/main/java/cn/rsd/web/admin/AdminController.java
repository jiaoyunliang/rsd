package cn.rsd.web.admin;

import cn.rsd.dao.MeterDataReportMapper;
import cn.rsd.po.MeterDataReport;
import org.apache.ibatis.session.RowBounds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author 焦云亮
 * @date 2018/9/21
 * @modifyUser
 * @modifyDate
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    protected static Logger logger = LogManager.getLogger(AdminController.class);

    @Autowired
    private MeterDataReportMapper meterDataReportMapper;

    @RequestMapping("login")
    public ModelAndView login(){
        ModelAndView view = new ModelAndView("login");

        return view;
    }

    @RequestMapping("index")
    public ModelAndView index(){
        ModelAndView view = new ModelAndView("index");
        Example example = new Example(MeterDataReport.class);

        example.orderBy("reportDate").desc();
        List<MeterDataReport> list = this.meterDataReportMapper.selectByExampleAndRowBounds(example, new RowBounds(0,30));

        view.addObject("meterData",list);
        return view;
    }

}
