package cn.rsd.web.admin;

import cn.rsd.dao.*;
import cn.rsd.po.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
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
    private MeterDataMapper meterDataMapper;

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
    @RequestMapping("index4")
    public ModelAndView index4(){
        ModelAndView view = new ModelAndView("stat4");

        Double heatSum = this.statMapper.selectSumAllNumber();
        view.addObject("heatSum",heatSum);

        Users users = new Users();
        users.setRole(4);
        List<Users> list = this.usersMapper.select(users);

        view.addObject("users",list);

        return view;
    }

    @RequestMapping("index5")
    public ModelAndView index5(){
        ModelAndView view = new ModelAndView("stat5");

        Double heatSum = this.statMapper.selectSumAllNumber();
        view.addObject("heatSum",heatSum);

        Users users = new Users();
        users.setRole(4);
        List<Users> list = this.usersMapper.select(users);

        view.addObject("users",list);

        MeterData meterData = new MeterData();
        meterData.setUserType(1);

        List<MeterData> meterDataList = this.meterDataMapper.select(meterData);

        view.addObject("meterDataList",meterDataList);

        return view;
    }

    @RequestMapping("index3")
    public ModelAndView index3(){
        ModelAndView view = new ModelAndView("stat3");

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
    @RequestMapping("heat/table")
    @ResponseBody
    public MeterDataReport heatTable(String tableNumber){
        MeterDataReport meterDataReport = null;
        try{
            meterDataReport = this.meterDataReportMapper.selectTabelAggregateHeat(tableNumber);
        }catch (Exception e){
            logger.error(e,e);
        }

        return meterDataReport;
    }

    @RequestMapping("heat/table/c")
    @ResponseBody
    public List<Map<String,Object>> heatTable3(){
        List<Map<String,String>> list = new ArrayList<>();
        List<Map<String,Object>> industryCategoryList = new ArrayList<>();
        try{


            MeterData meterData = new MeterData();
            meterData.setUserType(2);

            List<MeterData> meterDataList = this.meterDataMapper.select(meterData);
            Map<String,Integer> countMap = new HashMap<>(10);
            for(MeterData  meterData1:meterDataList){
                if(countMap.containsKey(meterData1.getIndustryCategory())){
                    countMap.put(meterData1.getIndustryCategory(),countMap.get(meterData1.getIndustryCategory())+1);
                }else{
                    countMap.put(meterData1.getIndustryCategory(),1);
                }
            }


            countMap.forEach((k,v)->{
                industryCategoryList.add(new HashMap<String, Object>(10){{
                    put("item",k);
                    put("count",v);
                }});
            });


        }catch (Exception e){
            logger.error(e,e);
        }

        return industryCategoryList;
    }

    @RequestMapping("heat/table/a")
    @ResponseBody
    public List<Map<String,Object>> heatTable1(){
        List<Map<String,Object>> list = null;
        try{
            LocalDate d = LocalDate.now();
            LocalDate.Property e = d.minusWeeks(1).dayOfWeek();

            MeterData meterData = new MeterData();
            meterData.setUserType(1);

            List<MeterData> meterDataList = this.meterDataMapper.select(meterData);
            LocalTime  localTime = new LocalTime(23,59,59);
            list = this.statMapper.selectTableHeat(e.withMinimumValue().toDate(),e.withMaximumValue().toDateTime(localTime).toDate(),meterDataList);

            for(Map<String,Object> map:list){
                DateTime dt4 = new DateTime(map.get("runDate"));
                map.put("week",dt4.toString("EE",Locale.CHINESE));
                map.put("heat",map.get("heat"));
            }
        }catch (Exception e){
            logger.error(e,e);
        }

        return list;
    }

    @RequestMapping("heat/table/b")
    @ResponseBody
    public List<Map<String,Object>> heatTable2(){
        List<Map<String,Object>> list = null;
        List<Map<String,Object>> list1 = null;
        try{
            LocalDate d = LocalDate.now();
            LocalDate.Property e = d.minusWeeks(1).dayOfWeek();

            MeterData meterData = new MeterData();
            meterData.setUserType(2);
            LocalTime  localTime = new LocalTime(23,59,59);
            List<MeterData> meterDataList = this.meterDataMapper.select(meterData);
            list = this.statMapper.selectTableHeat(e.withMinimumValue().toDate(),e.withMaximumValue().toDateTime(localTime).toDate(),meterDataList);

            meterData.setUserType(1);
            List<MeterData> meterDataList1 = this.meterDataMapper.select(meterData);
            list1 = this.statMapper.selectTableHeat(e.withMinimumValue().toDate(),e.withMaximumValue().toDateTime(localTime).toDate(),meterDataList1);

            for(Map<String,Object> map:list){
                DateTime dt4 = new DateTime(map.get("runDate"));
                map.put("week",dt4.toString("EE",Locale.CHINESE));
                map.put("gongren",map.get("heat"));

                for(Map<String,Object> map1:list1){
                    DateTime dt41 = new DateTime(map1.get("runDate"));
                    if(dt41.toString("EE",Locale.CHINESE).equals(map.get("week"))){
                        map.put("quren",map1.get("heat"));
                        break;
                    }
                }
            }

        }catch (Exception e){
            logger.error(e,e);
        }

        return list;
    }

    @RequestMapping("heat/table/line")
    @ResponseBody
    public List<Map<String,Object>> heatTableLine(){
        List<Map<String,Object>> list = null;
        try{
            list = this.statMapper.selectTableNumberMeterData(null);
        }catch (Exception e){
            logger.error(e,e);
        }

        return list;
    }
    @RequestMapping("heat/table/line1")
    @ResponseBody
    public List<Map<String,Object>> heatTableLine1(){
        List<Map<String,Object>> list = null;
        try{
            list = this.statMapper.selectTableNumberMeterData(new Date());
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
        NumberFormat format = NumberFormat.getInstance();
        try{
            heatSum = this.statMapper.selectSumAllNumber();

        }catch (Exception e){
            logger.error(e,e);
        }
        format.setMaximumFractionDigits(2);
        return format.format(heatSum);

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
    private static final String FORMATE_DATE = "yyyy-MM-dd";
    private static final String FORMATE_SECONDS = "HH:mm:ss";
    private static final String FORMATE_FULL = FORMATE_DATE.concat(" ").concat(FORMATE_SECONDS);

    public static void main(String[] args){
        LocalDate d = LocalDate.now();
        LocalDate.Property e = d.minusWeeks(1).dayOfWeek();
        LocalTime  localTime = new LocalTime(23,59,59);


        DateTime dateTime = e.withMaximumValue().toDateTime(localTime);

        System.out.println("上周的周一：" + e.withMinimumValue().toString("yyyy年MM月dd日 e", Locale.CHINESE));
        System.out.println("上周的周日：" + dateTime.toString(FORMATE_FULL, Locale.CHINESE));

    }

}
