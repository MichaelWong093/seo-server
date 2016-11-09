package com.berchina.esb.server.configloader.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Package com.berchina.esb.server.configloader.config
 * @Description: TODO ( Swagger2 API Doc Configuration )
 * @Author 任小斌 renxiaobin
 * @Date 2016 下午4:35
 * @Version V1.0
 */
@Configuration
@EnableSwagger2
public class Swagger2AutoConfiguration {

    @Bean
    public Docket createApi() {
        return
                new Docket(DocumentationType.SWAGGER_2)
                        .apiInfo(apiInfo())
                        .select()
                        .apis(RequestHandlerSelectors.basePackage("com.berchina.esb.server.provider.handle"))
                        .paths(PathSelectors.any())
                        .build();
    }

    public ApiInfo apiInfo() {
        return
                new ApiInfoBuilder()
                        .title(" 百合生活搜索服务 API 相关文档 ")
                        .description("百合生活网址：http://www.bhelife.com")
                        .termsOfServiceUrl("http://www.bhelife.com")
                        .contact("联系方式：renxiaobin@berchina.com")
                        .version("1.0").build();
    }
}
