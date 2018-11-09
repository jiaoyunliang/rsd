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
    /**
     * 1 储热完成 2 修理 3 停运 4运送中 5 储热中
     */
    public enum TrucksStateEnum {

        COMPLETE(1),REPAIR(2),OUTAGE(3),TRANSIT(4),HEAT_STORAGE(5);
        private int value = 0;
        TrucksStateEnum(int value){
            this.value =value;
        }
        public int value() {
            return this.value;
        }

        public String textValue(int value){
            String stateText = "";

            switch (value){
                case 1:
                    stateText = "储热完成";
                    break;
                case 2:
                    stateText = "修理";
                    break;
                case 3:
                    stateText = "停运";
                    break;
                case 4:
                    stateText = "运送中";
                    break;
                case 5:
                    stateText = "储热中";
                    break;
            }
            return stateText;
        }
    }
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
