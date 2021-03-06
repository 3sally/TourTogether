package com.ssafy.tourtogether.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * API 문서 관련 swagger2 설정 정의.
 */
@Configuration
@EnableWebMvc
public class SwaggerConfig {

//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2).useDefaultResponseMessages(false)
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build()
//                .securityContexts(newArrayList(securityContext()))
//                .securitySchemes(newArrayList(apiKey()))
//                .apiInfo(apiInfo())
//                ;
//    }

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.OAS_30).useDefaultResponseMessages(false).select()
				.apis(RequestHandlerSelectors.basePackage("com.ssafy.tourtogether.api.controller"))
				.paths(PathSelectors.any()).build().apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		String description = "Welcome TourTogether";
		return new ApiInfoBuilder().title("TourTogether").description(description).version("1.0").build();
	}

//    private ApiKey apiKey() {
//        return new ApiKey(SECURITY_SCHEMA_NAME, "Authorization", "header");
//    }
//
//    private SecurityContext securityContext() {
//        return SecurityContext.builder()
//                .securityReferences(defaultAuth())
//                .build();
//    }
//
//    public static final String SECURITY_SCHEMA_NAME = "JWT";
//    public static final String AUTHORIZATION_SCOPE_GLOBAL = "global";
//    public static final String AUTHORIZATION_SCOPE_GLOBAL_DESC = "accessEverything";
//
//    private List<SecurityReference> defaultAuth() {
//        AuthorizationScope authorizationScope = new AuthorizationScope(AUTHORIZATION_SCOPE_GLOBAL, AUTHORIZATION_SCOPE_GLOBAL_DESC);
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return newArrayList(new SecurityReference(SECURITY_SCHEMA_NAME, authorizationScopes));
//    }
//
//    @Bean
//    UiConfiguration uiConfig() {
//        return UiConfigurationBuilder.builder()
////                .supportedSubmitMethods(newArrayList("get").toArray(new String[0])) // try it 기능 활성화 범위
////                .operationsSorter(METHOD)
//                .build();
//    }
}