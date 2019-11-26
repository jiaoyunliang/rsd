package cn.rsd.po;

import cn.rsd.base.BasePo;

import javax.persistence.Table;
import java.util.Date;

@Table(name = "data_log")
public class DataLog extends BasePo {

    private Integer val;

    private Date createDate;

    private String byteStr;


    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public String getByteStr() {
        return byteStr;
    }

    public void setByteStr(String byteStr) {
        this.byteStr = byteStr;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


}
