package cn.rsd.dao;

import cn.rsd.base.BaseMapper;
import cn.rsd.po.MeterDataReport;
import org.apache.ibatis.annotations.Param;

/**
 * @author 焦云亮
 * @date 2018/10/30
 * @modifyUser
 * @modifyDate
 */
public interface MeterDataReportMapper extends BaseMapper<MeterDataReport> {
    int selectTabelMaxSeq (@Param(value="tableNumber") String tableNumber);
    MeterDataReport selectTabelAggregateHeat (@Param(value="tableNumber") String tableNumber);
}
