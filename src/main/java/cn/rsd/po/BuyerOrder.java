package cn.rsd.po;

import cn.rsd.base.BasePo;

import javax.persistence.Table;
import java.util.Date;

/**
 * @author 焦云亮
 * @date 2018/7/30
 * @modifyUser
 * @modifyDate
 */
@Table(name = "BUYER_ORDER")
public class BuyerOrder extends BasePo {
    /**
     * 1.取消,2待接单,3已接单,4到达,5开始供热,6待支付,7完成
     */
    public enum BuyerOrderStateEnum {

        CANCEL(1),PREPARE(2),READY(3),REACH(4),START_WORK(5),UNPAID(6),COMPLETE(7);
        private int value = 0;
        BuyerOrderStateEnum(int value){
            this.value =value;
        }
        public int value() {
            return this.value;
        }
    }
    private Long buyer;
    private Float quantity;
    private Integer quantityUnit;
    private Integer price;
    private Date deliveryDate;
    private String phone;
    private String address;
    private String comment;
    private  Date updateDate;
    private Long updateUser;
    private  Date createDate;
    private Integer state;
    private String buyerName;
    private Long distribution;
    private Long supplyId;
    private String orderCode;
    private Double startNumber;
    private Double endNumber;
    private String startPic;
    private String endPic;
    private Double actualNumber;
    private String prepayId;

    private String transactionId;
    private Integer starLevel;

    private Long trucks;

    public Long getTrucks() {
        return trucks;
    }

    public void setTrucks(Long trucks) {
        this.trucks = trucks;
    }

    public Integer getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(Integer starLevel) {
        this.starLevel = starLevel;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    public Double getStartNumber() {
        return startNumber;
    }

    public void setStartNumber(Double startNumber) {
        this.startNumber = startNumber;
    }

    public Double getEndNumber() {
        return endNumber;
    }

    public void setEndNumber(Double endNumber) {
        this.endNumber = endNumber;
    }

    public String getStartPic() {
        return startPic;
    }

    public void setStartPic(String startPic) {
        this.startPic = startPic;
    }

    public String getEndPic() {
        return endPic;
    }

    public void setEndPic(String endPic) {
        this.endPic = endPic;
    }

    public Double getActualNumber() {
        return actualNumber;
    }

    public void setActualNumber(Double actualNumber) {
        this.actualNumber = actualNumber;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Long getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(Long supplyId) {
        this.supplyId = supplyId;
    }

    public Long getDistribution() {
        return distribution;
    }

    public void setDistribution(Long distribution) {
        this.distribution = distribution;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getBuyer() {
        return buyer;
    }

    public void setBuyer(Long buyer) {
        this.buyer = buyer;
    }


    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(Integer quantityUnit) {
        this.quantityUnit = quantityUnit;
    }
}
