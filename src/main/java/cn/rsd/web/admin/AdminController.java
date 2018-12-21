package cn.rsd.web.admin;

import cn.rsd.dao.MeterDataMapper;
import cn.rsd.dao.MeterDataReportMapper;
import cn.rsd.po.MeterData;
import cn.rsd.po.MeterDataReport;
import org.apache.ibatis.session.RowBounds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private MeterDataMapper meterDataMapper;

    @RequestMapping("login")
    public ModelAndView login(){
        ModelAndView view = new ModelAndView("login");

        return view;
    }

    @RequestMapping("{page}")
    public ModelAndView page(@PathVariable("page") String page) {
        ModelAndView view = new ModelAndView(page);

        return view;
    }
    @RequestMapping("index")
    public ModelAndView index(){
        ModelAndView view = new ModelAndView("index");
        Example example = new Example(MeterDataReport.class);

        MeterData meterData = new MeterData();
        meterData.setUserType(1);

        List<MeterData> meterDataList = this.meterDataMapper.select(meterData);

        example.orderBy("reportDate").desc();
        example.createCriteria().andIn("tableNumber", meterDataList.stream().map(x -> x.getTableNumber()).collect(Collectors.toList()));
        List<MeterDataReport> list = this.meterDataReportMapper.selectByExampleAndRowBounds(example, new RowBounds(0,30));


        Example example1 = new Example(MeterDataReport.class);
        meterData.setUserType(2);

        meterDataList = this.meterDataMapper.select(meterData);
        example1.orderBy("reportDate").desc();
        example1.createCriteria().andIn("tableNumber", meterDataList.stream().map(x -> x.getTableNumber()).collect(Collectors.toList()));
        List<MeterDataReport> list1 = this.meterDataReportMapper.selectByExampleAndRowBounds(example1, new RowBounds(0, 30));

        view.addObject("meterData",list);
        view.addObject("meterData1", list1);
        return view;
    }

}
