package com.scott.eshop.cache.rebuild;

import com.scott.eshop.cache.model.ProductInfo;
import com.scott.eshop.cache.queue.RebuildCacheQueue;
import com.scott.eshop.cache.service.CacheService;
import com.scott.eshop.cache.spring.SpringContext;
import com.scott.eshop.cache.zk.ZookeeperSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 缓存重建的线程
 * @ClassName RebuildCacheThread
 * @Description
 * @Author 47980
 * @Date 2020/5/2 17:32
 * @Version V_1.0
 **/
public class RebuildCacheThread implements Runnable {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void run() {
        RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
        ZookeeperSession zkSessiong = ZookeeperSession.getInstance();
        CacheService cacheService = (CacheService) SpringContext.getApplicationContext().getBean("cacheService");

        while (true) {
            ProductInfo productInfo = rebuildCacheQueue.takeProductInfo();

            zkSessiong.acquireDistributeLock(productInfo.getId());

            ProductInfo existProductInfo = cacheService.getProductInfoFromRedisCache(productInfo.getId());

            if (existProductInfo != null) {
                // 比较时间版本
                try {
                    Date date = sdf.parse(productInfo.getModifiedTime());
                    Date existedDate = sdf.parse(existProductInfo.getModifiedTime());

                    if (date.before(existedDate)) {
                        System.out.println("current date = " + productInfo.getModifiedTime() + " is before existed date["+existProductInfo.getModifiedTime()+"]");
                        continue;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println(" current date is after existed date[" + existProductInfo.getModifiedTime()+"]");
            } else {
                System.out.println("existed product info is null");
            }

            cacheService.saveProductInfo2LocalCache(productInfo);
            cacheService.saveProductInfo2RedisCache(productInfo);

            // 分布式锁释放
            zkSessiong.releaseDistributeLock(productInfo.getId());
        }
    }
}
