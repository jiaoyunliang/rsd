package cn.rsd.service.impl;

import cn.rsd.util.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author 焦云亮
 * @date 2018/9/25
 * @modifyUser
 * @modifyDate
 */
@Service
public class OrderCodeService {

    @Value("${workerId}")
    private long workerId;

    @Value("${datacenterId}")
    private long datacenterId;

    private SnowflakeIdWorker snowflakeIdWorker;

    public OrderCodeService(){
        snowflakeIdWorker = new SnowflakeIdWorker(this.workerId,this.datacenterId);
    }

    public String nextId(){
        return String.valueOf(this.snowflakeIdWorker.nextId());
    }
}
