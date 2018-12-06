package cn.rsd.po;

import cn.rsd.base.BasePo;

import javax.persistence.Table;

/**
 * @author 焦云亮
 * @date 2018/12/4
 * @modifyUser
 * @modifyDate
 */
@Table(name = "METER_DATA")
public class MeterData extends BasePo{
    private String tableNumber;
    private String tableName;
    private Integer userType;
    private String industryCategory;

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getIndustryCategory() {
        return industryCategory;
    }

    public void setIndustryCategory(String industryCategory) {
        this.industryCategory = industryCategory;
    }
}
