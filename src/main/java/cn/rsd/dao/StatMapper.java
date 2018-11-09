package cn.rsd.dao;

import cn.rsd.base.BaseMapper;
import cn.rsd.po.MeterDataReport;

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

    List<Map<String,Object>> selectUserMeterData(Long userId);

    List<Map<String,Object>> selectCO2MeterData(Date now);

    List<Map<String,Object>> selectDriverName();

    List<Map<String,Object>> selectUserFree();

    double selectSumAllNumber();

}
