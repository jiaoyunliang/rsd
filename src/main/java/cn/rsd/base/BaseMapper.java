package cn.rsd.base;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

/**
 * @author 焦云亮
 * @date 2018/7/30
 * @modifyUser
 * @modifyDate
 */
public interface BaseMapper<T> extends Mapper<T>,InsertListMapper<T>,InsertUseGeneratedKeysMapper<T> {
}
