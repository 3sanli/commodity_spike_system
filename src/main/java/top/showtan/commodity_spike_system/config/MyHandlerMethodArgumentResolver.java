package top.showtan.commodity_spike_system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import top.showtan.commodity_spike_system.entity.User;
import top.showtan.commodity_spike_system.redis.RedisService;
import top.showtan.commodity_spike_system.redis.UserKeyPre;
import top.showtan.commodity_spike_system.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: sanli
 * @Date: 2019/3/25 22:21
 */
@Component
public class MyHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        //判断参数列表中是否含有User参数，若有则自动注入
        Class<?> parameterType = methodParameter.getParameterType();
        return parameterType == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        return UserContext.getUser();
    }
}
