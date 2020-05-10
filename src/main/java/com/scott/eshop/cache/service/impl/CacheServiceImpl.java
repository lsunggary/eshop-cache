package com.scott.eshop.cache.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.scott.eshop.cache.hystrix.command.GetProductInfoFromRedisCacheCommand;
import com.scott.eshop.cache.hystrix.command.GetShopInfoFromRedisCacheCommand;
import com.scott.eshop.cache.hystrix.command.SaveProductInfo2RedisCacheCommand;
import com.scott.eshop.cache.hystrix.command.SaveShopInfo2RedisCacheCommand;
import com.scott.eshop.cache.model.ProductInfo;
import com.scott.eshop.cache.model.ShopInfo;
import com.scott.eshop.cache.service.CacheService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;
import scala.Product;

import javax.annotation.Resource;

/**
 * 商品详情service实现类
 * @ClassName CacheServiceImpl
 * @Description
 * @Author 47980
 * @Date 2020/5/1 20:21
 * @Version V_1.0
 **/
@Service("cacheService")
public class CacheServiceImpl implements CacheService {

    public static final String CACHE_NAME = "local";

    @Resource
    private JedisCluster jedisCluster;

    @Override
    @CachePut(value = CACHE_NAME, key= "'product_info_'+#productInfo.getId()")
    public ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo) {
        return productInfo;
    }

    @Override
    @CachePut(value = CACHE_NAME, key= "'shop_info_'+#shopInfo.getId()")
    public ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo) {
        return shopInfo;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'product_info_'+#productId")
    public ProductInfo getProductInfoFromLocalCache(Long productId) {
        return null;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'shop_info_'+#shopId")
    public ShopInfo getShopInfoFromLocalCache(Long shopId) {
        return null;
    }

    @Override
    public void saveProductInfo2RedisCache(ProductInfo productInfo) {
        SaveProductInfo2RedisCacheCommand command = new SaveProductInfo2RedisCacheCommand(productInfo);
        command.execute();
    }

    @Override
    public void saveShopInfo2RedisCache(ShopInfo shopInfo) {
        SaveShopInfo2RedisCacheCommand command = new SaveShopInfo2RedisCacheCommand(shopInfo);
        command.execute();
    }

    @Override
    public ProductInfo getProductInfoFromRedisCache(Long productId) {
        GetProductInfoFromRedisCacheCommand command = new GetProductInfoFromRedisCacheCommand(productId);
        return command.execute();
    }

    @Override
    public ShopInfo getShopInfoFromRedisCache(Long shopId) {
        GetShopInfoFromRedisCacheCommand command = new GetShopInfoFromRedisCacheCommand(shopId);
        return command.execute();
    }

}
