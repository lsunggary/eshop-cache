package com.scott.eshop.cache.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.scott.eshop.cache.model.ProductInfo;
import com.scott.eshop.cache.spring.SpringContext;
import redis.clients.jedis.JedisCluster;

/**
 * @ClassName SaveProductInfo2RedisCacheCommand
 * @Description
 * @Author 47980
 * @Date 2020/5/10 20:56
 * @Version V_1.0
 **/
public class SaveProductInfo2RedisCacheCommand extends HystrixCommand<Boolean> {

    private ProductInfo productInfo;


    public SaveProductInfo2RedisCacheCommand (ProductInfo productInfo) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RedisGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(100)
                        .withCircuitBreakerRequestVolumeThreshold(1000)
                        .withCircuitBreakerErrorThresholdPercentage(70)
                        .withCircuitBreakerSleepWindowInMilliseconds(60 * 1000)
                ));
        this.productInfo = productInfo;
    }

    @Override
    protected Boolean run() throws Exception {
        JedisCluster jedisCluster = (JedisCluster) SpringContext.getApplicationContext().getBean("JedisClusterFactory");
        String key = "product_info_" + productInfo.getId();
        jedisCluster.set(key, JSONObject.toJSONString(productInfo));
        return true;
    }

    @Override
    protected Boolean getFallback() {
        return false;
    }
}
