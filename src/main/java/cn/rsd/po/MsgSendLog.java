package cn.rsd.po;

import cn.rsd.base.BasePo;

/**
 * @author 焦云亮
 * @date 2018/10/28
 * @modifyUser
 * @modifyDate
 */
public class MsgSendLog extends BasePo{
    private String msgBody;
    private String resultStr;
    private String appId;
    private String openId;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getAppId() {

        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public String getResultStr() {
        return resultStr;
    }

    public void setResultStr(String resultStr) {
        this.resultStr = resultStr;
    }
}
