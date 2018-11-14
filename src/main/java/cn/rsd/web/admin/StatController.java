package cn.rsd.web.admin;

import cn.rsd.dao.*;
import cn.rsd.po.SupplyPosts;
import cn.rsd.po.Trucks;
import cn.rsd.po.Users;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 焦云亮
 * @date 2018/9/26
 * @modifyUser
 * @modifyDate
 */
@Controller("admin/stat")
@RequestMapping("/admin/stat")
public class StatController {
    protected static Logger logger = LogManager.getLogger(StatController.class);

    @Autowired
    private MeterDataReportMapper meterDataReportMapper;

    @Autowired
    private SupplyPostsMapper supplyPostsMapper;

    @Autowired
    private StatMapper statMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private BuyerOrderMapper buyerOrderMapper;

    /**             用户管理开始                                          **/
    @RequestMapping("index")
    public ModelAndView index(){
        ModelAndView view = new ModelAndView("stat");

        Double heatSum = this.statMapper.selectSumAllNumber();
        view.addObject("heatSum",heatSum);

        Users users = new Users();
        users.setRole(4);
        List<Users> list = this.usersMapper.select(users);

        view.addObject("users",list);

        return view;
    }

    @RequestMapping("index1")
    public ModelAndView index1(){
        ModelAndView view = new ModelAndView("stat1");

        Double heatSum = this.statMapper.selectSumAllNumber();
        view.addObject("heatSum",heatSum);

        Users users = new Users();
        users.setRole(4);
        List<Users> list = this.usersMapper.select(users);

        view.addObject("users",list);

        return view;
    }

    @RequestMapping("index2")
    public ModelAndView index2(){
        ModelAndView view = new ModelAndView("stat2");

        Double heatSum = this.statMapper.selectSumAllNumber();
        view.addObject("heatSum",heatSum);

        Users users = new Users();
        users.setRole(4);
        List<Users> list = this.usersMapper.select(users);

        view.addObject("users",list);

        return view;
    }

    @RequestMapping("heat/line")
    @ResponseBody
    public List<Map<String,Object>> heatLine(){
        List<Map<String,Object>> list = null;
        try{
            list = this.statMapper.selectMeterData(new Date());
        }catch (Exception e){
            logger.error(e,e);
        }

        return list;
    }

    @RequestMapping("co2/line")
    @ResponseBody
    public List<Map<String,Object>> co2Line(){
        List<Map<String,Object>> list = null;
        try{
            list = this.statMapper.selectCO2MeterData(new Date());
        }catch (Exception e){
            logger.error(e,e);
        }

        return list;
    }

    @RequestMapping("heat/all")
    @ResponseBody
    public String heatALl(){
        Double heatSum = null;
        NumberFormat format = NumberFormat.getCurrencyInstance();
        try{
            heatSum = this.statMapper.selectSumAllNumber();
        }catch (Exception e){
            logger.error(e,e);
        }

        return format.format(heatSum);

    }

    public static void main(String[] args){
        System.out.println(randomDate("2018-01-01", "2018-11-01"));
    }

    @RequestMapping("user/heat/line")
    @ResponseBody
    public List<Map<String,Object>> heatLine(Long userId){
        List<Map<String,Object>> list = null;
        if(userId == null){
            userId = 0L;
        }
        try{
            list = this.statMapper.selectUserMeterData(userId);
        }catch (Exception e){
            logger.error(e,e);
        }
        return list;
    }

    @RequestMapping("driver")
    @ResponseBody
    public List<Map<String,Object>> driver(){
        List<Map<String,Object>> list = null;
        try{
            list = this.statMapper.selectDriverName();

            for(Map<String,Object> map:list){
                List<SupplyPosts> supplyPostsList = this.supplyPostsMapper.selectSupplyPostsByUser(((Integer)map.get("id")).longValue());

                if(supplyPostsList != null && !supplyPostsList.isEmpty()) {
                    SupplyPosts supplyPosts = supplyPostsList.get(0);

                    if(map.get("buyer") != null){
                        Users buyer = this.usersMapper.selectByPrimaryKey(((Integer)map.get("buyer")).longValue());
                        map.put("route", supplyPosts.getAddress() + "->" + buyer.getAddress());
                    }
                }

                map.put("state",Trucks.TrucksStateEnum.COMPLETE.textValue((Integer) map.get("state")));
            }
        }catch (Exception e){
            logger.error(e,e);
        }
        return list;
    }
    @RequestMapping("free")
    @ResponseBody
    public List<Map<String,Object>> free(){
        List<Map<String,Object>> list = null;
        try{
            list = this.statMapper.selectUserFree();
        }catch (Exception e){
            logger.error(e,e);
        }

        return list;
    }


    private static String randomDate(String beginDate, String endDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(beginDate);
            Date end = format.parse(endDate);
            // getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = random(start.getTime(), end.getTime());

            return format.format(new Date(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long random(long begin, long end) {
        long rtn = begin + (long) (Math.random() * (end - begin));
        // 如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
        if (rtn == begin || rtn == end) {
            return random(begin, end);
        }
        return rtn;
    }

}
