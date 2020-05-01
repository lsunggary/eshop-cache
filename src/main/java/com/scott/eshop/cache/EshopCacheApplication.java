package com.scott.eshop.cache;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 库存服务 Application 类
 */
@MapperScan("com.scott.eshop.cache.mapper")
@SpringBootApplication
public class EshopCacheApplication {

    /**
     * 构建数据源
     * @return
     */
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        return new DataSource();
    }

    /**
     * 构建mybatis 的入口类：SqlSessionFactory
     * @return
     * @throws Exception
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 构建事务管理器
     * @return
     */
    @Bean
    public PlatformTransactionManager platformTransactionManager () {
        return new DataSourceTransactionManager(dataSource());
    }

    /**
     * 程序入口类 main 方法
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(EshopCacheApplication.class, args);
        System.out.println(" (                                                \n" +
                " )\\ )                  )    *   )              )  \n" +
                "(()/(               ( /(  ` )  /(           ( /(  \n" +
                " /(_)) (   (    (   )\\())  ( )(_))(   `  )  )\\()) \n" +
                "(_))   )\\  )\\   )\\ (_))/  (_(_()) )\\  /(/( ((_)\\  \n" +
                "/ __| ((_)((_) ((_)| |_   |_   _|((_)((_)_\\ / (_) \n" +
                "\\__ \\/ _|/ _ \\/ _ \\|  _|    | | / _ \\| '_ \\)| |   \n" +
                "|___/\\__|\\___/\\___/ \\__|    |_| \\___/| .__/ |_|   \n" +
                "                                     |_|          \n" +
                "\n");
    }

}
