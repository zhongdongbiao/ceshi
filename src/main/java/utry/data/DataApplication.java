package utry.data;

import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.client.RestTemplate;
import utry.core.CoreAutoConfiguration;
import utry.core.CoreAutoConfigurationSiteLetter;
import utry.core.CoreAutoConfigurationWeb;
import utry.core.cloud.module.UtryCloudModule;
import utry.core.cloud.module.UtryCloudModuleManager;
import utry.core.dataBase.MybatisMapperSplitListener;
import utry.core.thread.ThreadPoolManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author wangyaping@utry.cn
 * @date 2017年8月18日下午1:12:26
 * @description
 */

@EnableEurekaClient
@SpringBootApplication(exclude = {JmsAutoConfiguration.class})
@Import({CoreAutoConfiguration.class, CoreAutoConfigurationSiteLetter.class, CoreAutoConfigurationWeb.class})
@ComponentScan(value = {UtryCloudModule.ROOT_PACKAGE_NAME})
@MapperScan({"utry.**.dao"})
public class DataApplication extends SpringBootServletInitializer implements WebApplicationInitializer {



    @Bean
    @LoadBalanced
    public RestTemplate template() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage(
                StringUtils.join(UtryCloudModuleManager.allMapperPackages(), ","));
        return mapperScannerConfigurer;
    }


    private static void init() {
        preloadModules();
        splitMapper();
    }

    private static void preloadModules() {
        UtryCloudModuleManager.initModules();
    }

    private static void splitMapper() {
        new MybatisMapperSplitListener("mapper").onApplicationEvent(null);
    }

    @Bean
    public ThreadPoolManager threadPoolManager() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<ThreadPoolManager> constructor = ThreadPoolManager.class.getDeclaredConstructor();
        boolean flag = constructor.isAccessible();
        constructor.setAccessible(true);
        ThreadPoolManager manager = constructor.newInstance();
        constructor.setAccessible(flag);
        return manager;
    }

    public static void main(String[] args) {
        System.out.println("data开始启动....");
        init();
        try {
            SpringApplication springApp = new SpringApplication(DataApplication.class);
            springApp.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("data启动成功....");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        init();
        return application.sources(DataApplication.class);
    }
}
