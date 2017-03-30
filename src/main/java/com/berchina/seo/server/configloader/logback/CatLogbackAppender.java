package com.berchina.seo.server.configloader.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.LogbackException;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.dianping.cat.Cat;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @Package com.berchina.seo.server.configloader.logback
 * @Description: TODO (  )
 * @Author rxbyes
 * @Date 2017 下午8:39
 * @Version V1.0
 */
public class CatLogbackAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent event) {
        try {
            boolean isTraceMode = Cat.getManager().isTraceMode();

            Level level = event.getLevel();

            if (level.isGreaterOrEqual(Level.ERROR))
            {
                logError(event);

            } else if (isTraceMode)
            {
                this.logTrace(event);
            }
        } catch (Exception ex) {

            throw new LogbackException(event.getFormattedMessage(),ex);
        }
    }

    private void logError(ILoggingEvent event){

        ThrowableProxy info = (ThrowableProxy) event.getThrowableProxy();

        if (StringUtil.notNull(info))
        {
            Throwable exception = info.getThrowable();

            Object message = event.getFormattedMessage();

            if (StringUtil.notNull(message))
            {
                Cat.logError(String.valueOf(message),exception);
            }else {
                Cat.logError(exception);
            }
        }
    }

    private void logTrace(ILoggingEvent event){
        String type = "Logback";
        String name = event.getLevel().toString();
        Object message = event.getFormattedMessage();
        String data;
        if (message instanceof Throwable)
        {
            data = buildExceptionStack((Throwable) message);
        }else {
            data = event.getFormattedMessage().toLowerCase();
        }

        ThrowableProxy info = (ThrowableProxy) event.getThrowableProxy();

        if (StringUtil.notNull(info))
        {
            data += "\n".concat(buildExceptionStack(info.getThrowable()));
        }
        Cat.logTrace(type,name,"0",data);
    }

    private String buildExceptionStack(Throwable exception){
        if (StringUtil.notNull(exception)){
            StringWriter writer = new StringWriter(2048);
            exception.printStackTrace(new PrintWriter(writer));
            return  writer.toString();
        }else {
            return "";
        }
    }
}
