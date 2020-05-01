package com.scott.eshop.cache.spring;

import org.springframework.context.ApplicationContext;

/**
 * spring 上下文
 * @ClassName ServletContext
 * @Description
 * @Author 47980
 * @Date 2020/5/1 22:59
 * @Version V_1.0
 **/
public class SpringContext {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContext.applicationContext = applicationContext;
    }
}
