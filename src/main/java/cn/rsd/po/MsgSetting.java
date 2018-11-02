package cn.rsd.po;

import cn.rsd.base.BasePo;

/**
 * @author 焦云亮
 * @date 2018/9/27
 * @modifyUser
 * @modifyDate
 */
public class MsgSetting extends BasePo{
    private Integer msgType;
    private Integer userType;

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }
}
