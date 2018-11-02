package cn.rsd.po;

import cn.rsd.base.BasePo;

import javax.persistence.Table;

/**
 * @author 焦云亮
 * @date 2018/10/19
 * @modifyUser
 * @modifyDate
 */
@Table(name = "TRUCKS")
public class Trucks extends BasePo{

    private String plateNumber;
    private Long userId;
    private Integer state;
    private Long supplyId;

    public Long getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(Long supplyId) {
        this.supplyId = supplyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }
}
