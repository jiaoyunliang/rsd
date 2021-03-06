package cn.rsd.dao;

import cn.rsd.base.BaseMapper;
import cn.rsd.po.MeterData;
import cn.rsd.po.MeterDataReport;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 焦云亮
 * @date 2018/11/8
 * @modifyUser
 * @modifyDate
 */
public interface StatMapper extends BaseMapper<MeterDataReport>{
    List<Map<String,Object>> selectMeterData(Date now);

    List<Map<String,Object>> selectTableNumberMeterData (@Param(value="now") Date now);

    List<Map<String,Object>> selectTableNumberMeterData1 (Date now);

    List<Map<String,Object>> selectUserMeterData(Long userId);

    List<Map<String,Object>> selectCO2MeterData(Date now);

    List<Map<String,Object>> selectDriverName();

    List<Map<String,Object>> selectUserFree();

    List<Map<String,Object>> selectTableHeat(@Param(value="startDate") Date startDate,@Param(value="endDate") Date endDate,@Param(value="tables")  List<MeterData> tables);

    double selectSumAllNumber();

}
