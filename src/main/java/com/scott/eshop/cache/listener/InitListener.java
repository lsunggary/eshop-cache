package com.scott.eshop.cache.listener;

import com.scott.eshop.cache.kafka.KafkaConsumer;
import com.scott.eshop.cache.rebuild.RebuildCacheThread;
import com.scott.eshop.cache.spring.SpringContext;
import com.scott.eshop.cache.zk.ZookeeperSession;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 系统初始化的监听器
 * @ClassName InitListener
 * @Description
 * @Author 47980
 * @Date 2020/5/1 22:11
 * @Version V_1.0
 **/
public class InitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sc);
        SpringContext.setApplicationContext(context);

        new Thread(new KafkaConsumer("cache-message")).start();
        new Thread(new RebuildCacheThread()).start();

        ZookeeperSession.init();


    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
