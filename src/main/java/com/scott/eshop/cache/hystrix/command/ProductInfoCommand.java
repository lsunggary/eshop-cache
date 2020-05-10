package com.scott.eshop.cache.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.*;
import com.scott.eshop.cache.model.ProductInfo;

/**
 * @ClassName ProductInfoCommand
 * @Description
 * @Author 47980
 * @Date 2020/5/10 21:47
 * @Version V_1.0
 **/
public class ProductInfoCommand extends HystrixCommand<ProductInfo> {

    private Long productId;

    public ProductInfoCommand(Long productId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductService"))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                    .withCoreSize(10)
                    .withMaximumSize(30)
                    .withAllowMaximumSizeToDivergeFromCoreSize(true)
                    .withKeepAliveTimeMinutes(1)
                    .withMaxQueueSize(50)
                    .withQueueSizeRejectionThreshold(100)
            )
        );
        this.productId = productId;
    }

    @Override
    protected ProductInfo run() throws Exception {
        String productInfoJSON = "{\"id\":"+productId+",\"name\":\"iphone 11\",\"price\":11000,\"pictureUrl\":\"a.jpg,b.jpg\",\"specification\":\"iphone 11 的售后规格\",\"service\":\"iphone 11 的售后服务\",\"color\":\"红色, 紫色, 粉色\",\"size\":\"1111px\",\"shopId\": 1 ,\"modifiedTime\": \"2020-05-02 12:00:01\"}";
        return JSONObject.parseObject(productInfoJSON, ProductInfo.class);
    }
}
