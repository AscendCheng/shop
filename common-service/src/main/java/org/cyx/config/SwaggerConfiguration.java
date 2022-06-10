package org.cyx.config;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.*;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description SwaggerConfiguration
 * @Author cyx
 * @Date 2021/2/9
 **/
@Component
@Data
@EnableOpenApi
public class SwaggerConfiguration {
    @Bean
    public Docket webApiDoc() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("用户端接口文档")
                .pathMapping("/")
                // 是否开启
                .enable(true)
                // 配置接口文档的元信息
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.cyx"))
                // 正则匹配请求路径，并分配到当前项目组
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .globalRequestParameters(globalRequestParameter())
                .globalResponses(HttpMethod.GET, getGlobalResponseMessage());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("shop")
                .description("微服务接口文档")
                .contact(new Contact("chengyx", "#", "chengyxnet@163.com"))
                .version("v1.0")
                .build();
    }

    private List<RequestParameter> globalRequestParameter() {
        List<RequestParameter> parameters = new ArrayList<>();
        parameters.add(new RequestParameterBuilder().name("token")
                .description("登录令牌")
                .in(ParameterType.HEADER)
                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                .required(true)
                .build());
        return parameters;
    }

    private List<Response> getGlobalResponseMessage() {
        List<Response> list = new ArrayList<>();
        list.add(new ResponseBuilder().code("4xx").description("请求错误，根据code和msg检查").build());
        return list;
    }
}
