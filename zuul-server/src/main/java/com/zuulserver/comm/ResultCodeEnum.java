package com.zuulserver.comm;

public enum ResultCodeEnum {

    SUCCESS(0,"成功"),
    Fail(-1,"失败"),

    NoLogin(-1001,"未登录"),
    UnAuth(-1002,"无权限"),
    NeedAuthValidation(-1003, "当前请求需要用户验证。"),
    firstLogin(-1004,"用户首次登录，请修改密码！"),
    DataTampering(-1005,"用户数据被篡改，请修改密码！"),
    LongTime(-1006,"用户长时间未登录，请修改密码！"),
    KickOut(-1007, "此用户已经在其他地方登录,你被迫下线!"),

    ServiceException(-1101,"服务异常"),
    ServiceDataFormatException(-1102,"服务返回数据格式不正确"),
    ServiceNotRegistered(-1103,"服务未注册"),
    ServiceURLIllegal(-1104,"服务路径非法"),
    ServiceDisable(-1105,"服务已停用"),
    ServiceAddressNotFound(-1106,"404 服务地址错误"),


    SystemException(-2001,"系统异常"),
    DBException(-2002,"数据库操作异常"),

    ParamException(-3001,"参数验证错误");


    private Integer code;
    private String msg;
    ResultCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public Integer getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }


}
