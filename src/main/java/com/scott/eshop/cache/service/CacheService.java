package com.scott.eshop.cache.service;

import com.scott.eshop.cache.model.ProductInfo;
import com.scott.eshop.cache.model.ShopInfo;

import java.util.Properties;

/**
 * 缓存service接口
 * @ClassName CacheService
 * @Description
 * @Author 47980
 * @Date 2020/5/1 20:19
 * @Version V_1.0
 **/
public interface CacheService {

    /**
     * 将商品信息保存本地缓存
     * @param productInfo 商品详情
     * @return
     */
    ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo);

    /**
     * 将店铺信息保存本地缓存
     * @param shopInfo 商品详情
     * @return
     */
    ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo);


    /**
     * 从escache本地缓存中获取商品信息
     * @param productId 商品详情id
     * @return
     */
    ProductInfo getProductInfoFromLocalCache(Long productId);

    /**
     * 从escache本地缓存中获取商品信息
     * @param shopId 商品详情id
     * @return
     */
    ShopInfo getShopInfoFromLocalCache(Long shopId);

    /**
     * 将商品信息保存到redis中
     * @param productInfo
     * @return
     */
    void saveProductInfo2RedisCache(ProductInfo productInfo);

    /**
     * 将店铺信息保存到redis中
     * @param shopInfo
     * @return
     */
    void saveShopInfo2RedisCache(ShopInfo shopInfo);

    /**
     * 从redis中获取商品信息
     * @param productId
     * @return
     */
    ProductInfo getProductInfoFromRedisCache(Long productId);

    /**
     * 从redis中获取商品店铺信息
     * @param shopId
     * @return
     */
    ShopInfo getShopInfoFromRedisCache(Long shopId);
}
