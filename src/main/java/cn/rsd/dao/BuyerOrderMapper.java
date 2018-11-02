package cn.rsd.dao;

import cn.rsd.base.BaseMapper;
import cn.rsd.po.BuyerOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author 焦云亮
 * @date 2018/7/30
 * @modifyUser
 * @modifyDate
 */
@Component
public interface BuyerOrderMapper extends BaseMapper<BuyerOrder> {

    List<Map<String, Object>> selectPriceByYear(@Param("buyer") Long buyer);

    List<Map<String, Object>> selectPriceByMonth( @Param("buyer")Long buyer);
}
