package top.showtan.commodity_spike_system.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: sanli
 * @Date: 2019/4/2 20:59
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {
    boolean needLogin() default false;

    boolean isMiaosha() default false;

    boolean canRepeat() default false;      //false代表不可重复，true为可重复

    boolean isTest() default false;         //是否为测试环境（需关闭秒杀限流以及验证码）

    boolean isLimit() default true;         //是否开启限流,由于业务判断跟随秒杀，默认为true

    int limit() default 5;                  //秒杀限流

    int expireTime() default 0;                  //秒杀限流过期时间，默认不过期

    boolean needPermission() default false;

    int[] permission() default 1;             //登录权限
}
