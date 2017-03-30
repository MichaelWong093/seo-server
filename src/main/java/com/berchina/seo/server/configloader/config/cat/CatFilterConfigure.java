package com.berchina.seo.server.configloader.config.cat;

import com.dianping.cat.servlet.CatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Package com.berchina.seo.server.configloader.config.cat
 * @Description: TODO ( 集成 Cat )
 * @Author rxbyes
 * @Date 2017 下午8:29
 * @Version V1.0
 */

@Configuration
public class CatFilterConfigure {

    @Bean
    public FilterRegistrationBean catFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        CatFilter catFilter = new CatFilter();
        bean.setFilter(catFilter);
        bean.addUrlPatterns("/*");
        bean.setName("cat-filter");
        bean.setOrder(1);
        return bean;
    }
}
