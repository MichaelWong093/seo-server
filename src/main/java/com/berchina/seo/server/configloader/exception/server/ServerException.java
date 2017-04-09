package com.berchina.seo.server.configloader.exception.server;

/**
 * @Package com.berchina.seo.server.configloader.exception.server
 * @Description: TODO ( 搜索异常基类 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/8/30 上午11:35
 * @Version V1.0
 */
public enum ServerException {

    /**
     * @ rxbyes  自定义错误信息以 10 开头
     * @ xuming  自定义错误信息以 20 开头
     * @ huiqing 自定义错误信息以 30 开头
     * <p>
     * 方便自定义错误信息冲突,若有相同错误信息提示,可用之前其他人定义的错误信息,
     */
    SEO_NULL_ERROR(10000, "输入参数不能为空"),
    SEO_SYSTEM(00000, "服务器异常"),
    SEO_REQ_STYLE_CONVERT_ERROR(10001, "请求数据格式转化异常"),
    SEO_REQUEST_ADAPTER(10002, "SEO 拒绝消费者调用,请联系管理员授权,谢谢!"),
    SEO_REQUEST_PARAMTER(10003, "系统参数异常"),
    SEO_SERVER_URL_ERROR(10004, "搜索服务器地址不存在"),
    SEO_SERVER_URL_CONNECT_ERROR(10005, "搜索服务器连接异常"),
    SEO_RESPONSE_HANDLE_ERROR(10006, "搜索服务数据响应异常"),

    SEO_SUGGEST_ADD_FAIL_ERROR(210, "新增联想词已经存在"),
    SEO_SUGGEST_DEL_FAIL_ERROR(211, "联想词已经被删除"),
    SEO_SUGGEST_NULL(211, "暂无数据"),

    SEO_FILE_CANNOT_WRITE(30100, "文件没有写入的权限"),;


    /**
     * 错误码
     */
    private Integer errCode;

    /**
     * 错误信息
     */
    private String erroMssage;

    ServerException() {
    }

    ServerException(Integer errCode, String erroMssage) {
        this.errCode = errCode;
        this.erroMssage = erroMssage;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getErroMssage() {
        return erroMssage;
    }

    public void setErroMssage(String erroMssage) {
        this.erroMssage = erroMssage;
    }
}
