package com.berchina.seo.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.WebApplicationInitializer;

/**
 * @Package com.berchina.seo.server
 * @Description: TODO(SEO 启动入口)
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 15/12/1 下午1:14
 * @Version V1.0
 */
@EnableScheduling
@SpringBootApplication
public class SeoApplication extends SpringBootServletInitializer implements WebApplicationInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SeoApplication.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SeoApplication.class, args);
    }
}
