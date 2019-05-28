package top.showtan.commodity_spike_system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @Author: sanli
 * @Date: 2019/3/25 22:19
 */
@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {
    @Autowired
    MyHandlerMethodArgumentResolver handlerMethodArgumentResolver;

    @Autowired
    MyHandlerInterceptor handlerInterceptor;

    //自定义参数配置
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(handlerMethodArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(handlerInterceptor);
    }
}
