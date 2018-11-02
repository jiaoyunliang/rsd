package cn.rsd.service;

import cn.rsd.po.BuyerOrder;
import cn.rsd.po.OrderStateLog;

/**
 * @author 焦云亮
 * @date 2018/10/19
 * @modifyUser
 * @modifyDate
 */
public interface OrderService {

    int saveOrder(BuyerOrder buyerOrder)throws Exception;

    int batchSaveOrder(BuyerOrder buyerOrder,Integer times)throws Exception;

    int renewOrder(BuyerOrder buyerOrder,Long trucks)throws Exception;

    int completeOrder(String orderCode,Integer fee,String transactionId)throws Exception;

    int reachOrder(BuyerOrder buyerOrder)throws Exception;

    int payOrder(Long id)throws Exception;

    int saveOrderLog(OrderStateLog orderStateLog)throws Exception;
}
