package cn.rsd.po;

import cn.rsd.base.BasePo;

/**
 * @author 焦云亮
 * @date 2018/9/26
 * @modifyUser
 * @modifyDate
 */
public class SupplyPosts extends BasePo {
    private String supplyName;
    private String address;
    private Long adminId;
    private Long trucks;

    public Long getTrucks() {
        return trucks;
    }

    public void setTrucks(Long trucks) {
        this.trucks = trucks;
    }

    public String getSupplyName() {
        return supplyName;
    }

    public void setSupplyName(String supplyName) {
        this.supplyName = supplyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}
