package com.scott.eshop.cache.controller;

import com.alibaba.fastjson.JSONObject;
import com.scott.eshop.cache.model.ProductInfo;
import com.scott.eshop.cache.model.ShopInfo;
import com.scott.eshop.cache.prewarm.CachePrewarmThread;
import com.scott.eshop.cache.queue.RebuildCacheQueue;
import com.scott.eshop.cache.service.CacheService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 缓存Controller
 * @ClassName CacheController
 * @Description
 * @Author 47980
 * @Date 2020/5/1 20:25
 * @Version V_1.0
 **/
@Controller
public class CacheController {

    @Resource
    private CacheService cacheService;

    @RequestMapping("/textPutCache")
    @ResponseBody
    public String testPutCache(ProductInfo productInfo) {
        cacheService.saveProductInfo2LocalCache(productInfo);
        return "success";
    }

    @RequestMapping("/textGetCache")
    @ResponseBody
    public ProductInfo testGetCache(Long id) {
        return cacheService.getProductInfoFromLocalCache(id);
    }

    @RequestMapping("/getProductInfo")
    @ResponseBody
    public ProductInfo getProductInfo(Long productId) {
        ProductInfo productInfo = null;

        productInfo = cacheService.getProductInfoFromRedisCache(productId);
        if (productId != null) {
            System.out.println("=============== 从redis中获取缓存，商品信息="+productInfo);
        }

        if (productInfo == null) {
            productInfo = cacheService.getProductInfoFromLocalCache(productId);
            if (productInfo != null) {
                System.out.println("=============== 从ehcache中获取缓存，商品信息="+productInfo);
            }
        }

        if (productInfo == null) {
            // 就需要从数据源重新去拉去数据，重建缓存。
            // 模拟数据
            String productInfoJSON = "{\"id\":"+productId+",\"name\":\"iphone 11\",\"price\":11000,\"pictureUrl\":\"a.jpg,b.jpg\",\"specification\":\"iphone 11 的售后规格\",\"service\":\"iphone 11 的售后服务\",\"color\":\"红色, 紫色, 粉色\",\"size\":\"1111px\",\"shopId\": 1 ,\"modifiedTime\": \"2020-05-02 12:00:01\"}";
            productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);

            // 将数据推送到内存队列中
            RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
            rebuildCacheQueue.addMessage(productInfo);
        }
        return productInfo;
    }

    @RequestMapping("/getShopInfo")
    @ResponseBody
    public ShopInfo getShopInfo(Long shopId) {
        ShopInfo shopInfo = null;

        shopInfo = cacheService.getShopInfoFromRedisCache(shopId);
        System.out.println("=============== 从redis中获取缓存，店铺信息="+shopInfo);

        if (shopInfo == null) {
            shopInfo = cacheService.getShopInfoFromLocalCache(shopId);
            System.out.println("=============== 从ehcache中获取缓存，店铺信息="+shopInfo);
        }

        if (shopInfo == null) {
            // 就需要从数据源重新去拉去数据，重建缓存。
            //_TODO_
        }
        return shopInfo;
    }

    @RequestMapping("/prewarmCache")
    @ResponseBody
    public void prewarmCache() {
        new CachePrewarmThread().start();
    }
}
