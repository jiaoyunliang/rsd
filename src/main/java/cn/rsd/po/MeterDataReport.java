package cn.rsd.po;

import cn.rsd.base.BasePo;

import java.util.Date;

/**
 * @author 焦云亮
 * @date 2018/10/30
 * @modifyUser
 * @modifyDate
 */
public class MeterDataReport extends BasePo {
    private String hexStr;

    private String dataStr;

    private Date reportDate;

    private Double aggregateHeat;

    private Double currentHeat;

    private Date runDate;

    private String tableNumber;

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Double getAggregateHeat() {
        return aggregateHeat;
    }

    public void setAggregateHeat(Double aggregateHeat) {
        this.aggregateHeat = aggregateHeat;
    }

    public Double getCurrentHeat() {
        return currentHeat;
    }

    public void setCurrentHeat(Double currentHeat) {
        this.currentHeat = currentHeat;
    }

    public Date getRunDate() {
        return runDate;
    }

    public void setRunDate(Date runDate) {
        this.runDate = runDate;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getDataStr() {
        return dataStr;
    }

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }

    public String getHexStr() {
        return hexStr;
    }

    public void setHexStr(String hexStr) {
        this.hexStr = hexStr;
    }
}
