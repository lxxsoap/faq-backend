package co.yiiu.pybbs.config;

/**
 * <p>@description TODO </p >
 *
 * @author <a href=" ">Shaw</a >
 * @version v1.1.0
 * @since 2024-08-15 JDK11+
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("co.yiiu.pybbs.controller.api"))
                .paths(PathSelectors.any())
                .build();
    }
}
