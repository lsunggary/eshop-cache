package com.scott.eshop.cache.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.scott.eshop.cache.model.ProductInfo;
import com.scott.eshop.cache.spring.SpringContext;
import redis.clients.jedis.JedisCluster;

/**
 * @ClassName GetProductInfoFromRedisCacheCommand
 * @Description
 * @Author 47980
 * @Date 2020/5/10 21:03
 * @Version V_1.0
 **/
public class GetProductInfoFromRedisCacheCommand extends HystrixCommand<ProductInfo> {

    private Long productId;

    public GetProductInfoFromRedisCacheCommand(Long productId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RedisGroup"))
        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
        .withExecutionTimeoutInMilliseconds(100)
        .withCircuitBreakerRequestVolumeThreshold(1000)
        .withCircuitBreakerErrorThresholdPercentage(70)
        .withCircuitBreakerSleepWindowInMilliseconds(60 * 1000)
        ));
        this.productId = productId;
    }

    @Override
    protected ProductInfo run() throws Exception {
        JedisCluster jedisCluster = (JedisCluster) SpringContext.getApplicationContext().getBean("JedisClusterFactory");
        String key = "product_info_" + productId;
        String json = jedisCluster.get(key);
        if (json != null) {
            return JSONObject.parseObject(json, ProductInfo.class);
        }
        return null;
    }

    @Override
    protected ProductInfo getFallback() {
        HBaseColdDataCommand command = new HBaseColdDataCommand(productId);
        return command.execute();
    }

    /**
     * 一级降级，可以去查询冷备数据（数仓）
     */
    private class HBaseColdDataCommand extends HystrixCommand<ProductInfo> {

        private Long productId;

        public HBaseColdDataCommand(Long productId) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("HBaseGroup"))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                            .withExecutionTimeoutInMilliseconds(100)
                            .withCircuitBreakerRequestVolumeThreshold(1000)
                            .withCircuitBreakerErrorThresholdPercentage(70)
                            .withCircuitBreakerSleepWindowInMilliseconds(60 * 1000)
                    ));
            this.productId = productId;
        }

        @Override
        protected ProductInfo run() throws Exception {
            // 查询HBase
            String productInfoJSON = "{\"id\":"+productId+",\"name\":\"iphone 11\",\"price\":11000,\"pictureUrl\":\"a.jpg,b.jpg\",\"specification\":\"iphone 11 的售后规格\",\"service\":\"iphone 11 的售后服务\",\"color\":\"红色, 紫色, 粉色\",\"size\":\"1111px\",\"shopId\": 1 ,\"modifiedTime\": \"2020-05-02 12:00:01\"}";
            return JSONObject.parseObject(productInfoJSON, ProductInfo.class);
        }

        /**
         * 二级降级，stubbed fallback，拼装数据
         * @return
         */
        @Override
        protected ProductInfo getFallback() {
            ProductInfo productInfo = new ProductInfo();
            productInfo.setId(productId); // 从请求参数中获取到的唯一值
            productInfo.setColor("默认颜色");
            productInfo.setModifiedTime("2020-05-06 12:00:00");
            productInfo.setSpecification("默认规格");
            productInfo.setService("默认服务");
            productInfo.setPrice(100.00);
            productInfo.setName("降级商品");
            return productInfo;
        }
    }
}
