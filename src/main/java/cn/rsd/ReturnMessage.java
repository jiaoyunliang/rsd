package cn.rsd;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 焦云亮
 * @date 2018/5/10
 * @modifyUser
 * @modifyDate
 */

public class ReturnMessage implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 代码
     */
    private int code;

    /**
     * 消息
     */
    private String msg = "DataModel对象没有值";


    //成功个数
    private int successNum;

    //失败个数
    private int failNum;

    //所有数量
    private int allNum;


    /**
     * 消息附加属性
     */
    private Map<String, Object> data = new HashMap<String, Object>();

    public int getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(int successNum) {
        this.successNum = successNum;
    }

    public int getFailNum() {
        return failNum;
    }

    public void setFailNum(int failNum) {
        this.failNum = failNum;
    }

    public int getAllNum() {
        return allNum;
    }

    public void setAllNum(int allNum) {
        this.allNum = allNum;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }


    public void putData(String key,Object val){
        this.data.put(key,val);
    }
    public static ReturnMessage buildMessage(){
        ReturnMessage message = new ReturnMessage();
        message.setMsg("成功");
        message.setCode(ReturnCode.OK.value());
        return message;
    }
}

