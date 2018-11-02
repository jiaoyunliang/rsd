package cn.rsd;

/**
 * @author 焦云亮
 * @date 2018/5/10
 * @modifyUser
 * @modifyDate
 */
/***
 * 返回编号  带扩展
 * */
public enum ReturnCode {
    // 通用状态
    UNKNOWN(100),
    // 成功
    OK(200),

    // 没有该业务类型
    BUS_NOT_FUNCTION_ERROR(401),

    //发送范围没有值 0对用户 1，对机构下用户
    BUS_NOT_SEND_SCOPE(402),

    //没有设置任何发送方式，如mq,短息，app等
    BUS_NOT_SEND(403),

    // 解析json报错
    JSONERR(401),
    // 发送范围为空
    SENDSCOPENULL(402),
    // 发送范围非法
    SENDSCOPEERR(403),
    // 发送类型为空
    SENDTYPENULL(404),
    // 发送类型为空
    SENDTYPEERR(405),
    // 发送源为空
    SOURCENULL(406),
    // 发送目标为空
    TARGETNULL(407),
    // 邮件信息为空
    USERNULL(408),
    // 插入消息主题信息报错
    INSERT_DETAIL_ERR(409),
    //发送的业务类型为空
    SENDBUSTYPENULL(410),
    //异常
    EXCEPTION(500),

    // JSON解析失败
    JSON_ANALYTICAL_ERROR(501);

    private final int value;

    ReturnCode(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

}
