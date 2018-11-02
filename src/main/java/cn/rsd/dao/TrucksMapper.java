package cn.rsd.dao;

import cn.rsd.base.BaseMapper;
import cn.rsd.po.Trucks;

import java.util.List;

/**
 * @author 焦云亮
 * @date 2018/10/19
 * @modifyUser
 * @modifyDate
 */
public interface TrucksMapper extends BaseMapper<Trucks> {
    List<Trucks> selectTrucksByIdle(Long supplyId);
}
