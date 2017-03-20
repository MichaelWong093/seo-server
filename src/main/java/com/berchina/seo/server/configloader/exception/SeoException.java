package com.berchina.seo.server.configloader.exception;

import com.berchina.seo.server.configloader.exception.server.ServerException;

/**
 * @Package com.berchina.seo.server.configloader.exception.server
 * @Description: TODO ( 搜索异常处理类 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/8/30 上午11:42
 * @Version V1.0
 */
public class SeoException extends RuntimeException {

    private ServerException seo;

    public SeoException(ServerException seo) {
        this.seo = seo;
    }

    public SeoException(String message, ServerException seo) {
        super(message);
        this.seo = seo;
    }

    public SeoException(String message, Throwable cause, ServerException seo) {
        super(message, cause);
        this.seo = seo;
    }

    public SeoException(Throwable cause, ServerException seo) {
        super(cause);
        this.seo = seo;
    }

    public SeoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ServerException seo) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.seo = seo;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public ServerException getSeo() {
        return seo;
    }

    public void setSeo(ServerException seo) {
        this.seo = seo;
    }
}
