package com.berchina.seo.server.provider.client.base;

import java.io.Serializable;
import java.util.Date;

/**
 * @Package com.berchina.seo.server.provider.client
 * @Description: TODO ( 响应基类 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/9/13 下午6:34
 * @Version V1.0
 */
public class Response implements Serializable {

    private static final long serialVersionUID = 8527717000710486036L;
    /**
     * 响应时间
     */
    private String time;

    /**
     * 响应状态
     */
    private String code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 业务流水号
     */
    private String serialNum;

    public Response() {
    }

    public Response(String time, String code, String message, String serialNum) {
        this.time = time;
        this.code = code;
        this.message = message;
        this.serialNum = serialNum;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    @Override
    public String toString() {
        return "Response{" +
                "time=" + time +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", serialNum='" + serialNum + '\'' +
                '}';
    }
}
