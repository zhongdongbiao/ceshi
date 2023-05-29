package utry.data.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import utry.core.cloud.module.UtryCloudModuleManager;

/**
 *
 * @author SiuWongLi
 * @date 17/9/6
 */
@Configuration
@EnableSwagger2
@Profile("dev")
public class DataSwaggerConfig {
    @Bean
    @ConditionalOnMissingBean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(UtryCloudModuleManager.swagger2Apis())
                .paths(PathSelectors.any())
                .build();
    }
}
