package com.berchina.seo.server.configloader.config.logger;

import com.berchina.seo.server.provider.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @Package com.berchina.seo.server.configloader.config.logger
 * @Description: TODO (  )
 * @Author rxbyes
 * @Date 2017 下午1:53
 * @Version V1.0
 */

@Configuration
public class LoggerConfigure {

    @Autowired
    private Environment evn;

    @Bean
    public boolean info() {

        String leve = evn.getRequiredProperty("info");

        return StringUtil.notNull(leve) ? Boolean.parseBoolean(leve) : false;
    }
}
