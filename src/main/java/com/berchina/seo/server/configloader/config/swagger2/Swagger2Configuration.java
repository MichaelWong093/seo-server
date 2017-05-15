package com.berchina.seo.server.configloader.config.swagger2;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

/**
 * @Package com.berchina.seo.server.configloader.config
 * @Description: TODO ( Swagger2 API Doc Configuration )
 * @Mark https://gumutianqi1.gitbooks.io/specification-doc/content/tools-doc/spring-boot-swagger2-guide.html
 * @Author 任小斌 renxiaobin
 * @Date 2016 下午4:35
 * @Version V1.0
 */
@Configuration
@EnableSwagger2
public class Swagger2Configuration {

    @Bean
    public Docket splitterApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("search")
                .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(true)
                .forCodeGeneration(false)
                .apiInfo(apiInfo())
                .select()
                .paths(splitterPaths())
                .build();
    }

    private Predicate<String> splitterPaths() {
        return or(regex("/seo/search/.*"), regex("/seo/splitter/.*"),regex("/seo/suggest/.*")
        );
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("百合生活网搜索服务 Rest API 接口文档")
                .description(initContextInfo())
                .termsOfServiceUrl("http://www.bhelife.com/")
//                .contact(new Contact("springfox", "http://springfox.io", "renxiaobin@berchina.com"))
//                .license("Apache License Version 2.0")
//                .licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
                .version("2.0")
                .build();
    }

    private String initContextInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("REST API 设计在细节上有很多自己独特的需要注意的技巧，并且对开发人员在构架设计能力上比传统 API 有着更高的要求。")
                .append("<br/>")
                .append("本文通过翔实的叙述和一系列的范例，从整体结构，到局部细节，分析和解读了为了提高易用性和高效性，REST API 设计应该注意哪些问题以及如何解决这些问题。");
        return sb.toString();
    }

//    @Bean
//    public Docket userApi() {
//        AuthorizationScope[] authScopes = new AuthorizationScope[1];
//        authScopes[0] = new AuthorizationScopeBuilder()
//                .scope("read")
//                .description("read access")
//                .build();
//        SecurityReference securityReference = SecurityReference.builder()
//                .reference("test")
//                .scopes(authScopes)
//                .build();
//
//        ArrayList<SecurityContext> securityContexts = newArrayList(SecurityContext.builder().securityReferences
//                (newArrayList(securityReference)).build());
//        return new Docket(DocumentationType.SWAGGER_2)
//                .securitySchemes(newArrayList(new BasicAuth("test")))
//                .securityContexts(securityContexts)
//                .groupName("user-api")
//                .apiInfo(apiInfo())
//                .select()
//                .paths(userOnlyEndpoints())
//                .build();
//    }
//    private Predicate<String> userOnlyEndpoints() {
//        return new Predicate<String>() {
//            @Override
//            public boolean apply(String input) {
//                return input.contains("user");
//            }
//        };
//    }

//    @Bean
//    SecurityContext securityContext() {
//        AuthorizationScope readScope = new AuthorizationScope("read:pets", "read your pets");
//        AuthorizationScope[] scopes = new AuthorizationScope[1];
//        scopes[0] = readScope;
//        SecurityReference securityReference = SecurityReference.builder()
//                .reference("petstore_auth")
//                .scopes(scopes)
//                .build();
//
//        return SecurityContext.builder()
//                .securityReferences(newArrayList(securityReference))
//                .forPaths(ant("/seo/splitter.*"))
//                .build();
//    }

//    @Bean
//    SecurityScheme oauth() {
//        return new OAuthBuilder()
//                .name("petstore_auth")
//                .grantTypes(grantTypes())
//                .scopes(scopes())
//                .build();
//    }

//    @Bean
//    SecurityScheme apiKey() {
//        return new ApiKey("api_key", "api_key", "header");
//    }
//
//    List<AuthorizationScope> scopes() {
//        return newArrayList(
//                new AuthorizationScope("write:pets", "modify pets in your account"),
//                new AuthorizationScope("read:pets", "read your pets"));
//    }
//
//    List<GrantType> grantTypes() {
//        GrantType grantType = new ImplicitGrantBuilder()
//                .loginEndpoint(new LoginEndpoint("http://petstore.swagger.io/api/oauth/dialog"))
//                .build();
//        return newArrayList(grantType);
//    }
//
//    @Bean
//    public SecurityConfiguration securityInfo() {
//        return new SecurityConfiguration(
//                "abc", "123", "pets", "petstore", "123",
//                ApiKeyVehicle.HEADER, "", ",");
//    }
}
