package cn.rsd.po;

import cn.rsd.base.BasePo;

import java.util.Date;

/**
 * @author 焦云亮
 * @date 2018/9/27
 * @modifyUser
 * @modifyDate
 */
public class UserTrack extends BasePo {
    public enum TrackTypeEnum {
        PAY(1), CONSUME(2);
        private int value = 0;
        TrackTypeEnum(int value){
            this.value =value;
        }
        public int value() {
            return this.value;
        }
    }

    private Long deductionsAccount;
    private Long orderId;
    private Integer alipay;
    private Integer weixin;
    private Integer accountBalance;
    private Integer donatedCapital;
    private Integer trackType;

    private Date createDate;
    private Long createUser;

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    public Integer getTrackType() {
        return trackType;
    }

    public void setTrackType(Integer trackType) {
        this.trackType = trackType;
    }

    public Long getDeductionsAccount() {
        return deductionsAccount;
    }

    public void setDeductionsAccount(Long deductionsAccount) {
        this.deductionsAccount = deductionsAccount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getAlipay() {
        return alipay;
    }

    public void setAlipay(Integer alipay) {
        this.alipay = alipay;
    }

    public Integer getWeixin() {
        return weixin;
    }

    public void setWeixin(Integer weixin) {
        this.weixin = weixin;
    }

    public Integer getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Integer accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Integer getDonatedCapital() {
        return donatedCapital;
    }

    public void setDonatedCapital(Integer donatedCapital) {
        this.donatedCapital = donatedCapital;
    }
}
