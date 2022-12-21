package com.project.shop.global.config;

import com.fasterxml.classmate.TypeResolver;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableSwagger2
@EnableWebMvc
public class SwaggerConfig implements WebMvcConfigurer {
    /**
     * http://localhost:8080/swagger-ui/index.html 접속
     */

    private final TypeResolver typeResolver;
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(true) // Swagger 에서 제공해주는 기본 응답 코드 (200, 401, 403, 404) 등의 노출 여부
                .alternateTypeRules(AlternateTypeRules.newRule(typeResolver.resolve(Pageable.class), typeResolver.resolve(Page.class)))
                .apiInfo(apiInfo()) // Swagger UI 로 노출할 정보
                .securityContexts(List.of(securityContext()))
                .securitySchemes(List.of(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.project.shop")) // api 스펙이 작성되어 있는 패키지 (controller)
                .paths(PathSelectors.any()) // apis 에 위치하는 API 중 특정 path 를 선택
                .build();
    }

    @Getter
    @Setter
    @ApiModel
    static class Page {
        @ApiModelProperty(value = "페이지 번호(0..N)", example = "0")
        private Integer page;

        @ApiModelProperty(value = "페이지 크기", example = "0")
        private Integer size;

        @ApiModelProperty(value = "상품 ID 내림차순")
        private List<String> sort;
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "OpenMarket shoppingMall",
                "springboot + Jpa 를 이용한 오픈마켓 쇼핑몰 프로젝트 입니다.",
                "1.0.0",
                "https://github.com/dbswhd4932/shoppingmall_project.git",
                new Contact("Jo Yoon Jong", "https://github.com/dbswhd4932/shoppingmall_project.git", "dbswhd4932@gmail.com"),
                "License of API",
                "API license URL",
                Collections.emptyList());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new PageableHandlerMethodArgumentResolver());
    }

    // swagger 에 토큰 인증 기능 추가  apiKey() , securityContext(), defaultAuth()
    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }

}
