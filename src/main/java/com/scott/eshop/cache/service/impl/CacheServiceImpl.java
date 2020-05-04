package com.scott.eshop.cache.service.impl;

import com.alibaba.fastjson.JSONObject;
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
        String key = "product_info_" + productInfo.getId();
        jedisCluster.set(key, JSONObject.toJSONString(productInfo));
    }

    @Override
    public void saveShopInfo2RedisCache(ShopInfo shopInfo) {
        String key = "shop_info_" + shopInfo.getId();
        jedisCluster.set(key, JSONObject.toJSONString(shopInfo));
    }

    @Override
    public ProductInfo getProductInfoFromRedisCache(Long productId) {
        String key = "product_info_" + productId;
        String json = jedisCluster.get(key);
        if (json != null) {
            return JSONObject.parseObject(json, ProductInfo.class);
        }
        return null;
    }

    @Override
    public ShopInfo getShopInfoFromRedisCache(Long shopId) {
        String key = "shop_info_" + shopId;
        String json = jedisCluster.get(key);
        if (json != null) {
            return JSONObject.parseObject(json, ShopInfo.class);
        }
        return null;
    }

}
