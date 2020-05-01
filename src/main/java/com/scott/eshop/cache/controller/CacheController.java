package com.scott.eshop.cache.controller;

import com.scott.eshop.cache.model.ProductInfo;
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
}
