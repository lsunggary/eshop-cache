package com.scott.eshop.cache.prewarm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scott.eshop.cache.model.ProductInfo;
import com.scott.eshop.cache.service.CacheService;
import com.scott.eshop.cache.spring.SpringContext;
import com.scott.eshop.cache.zk.ZookeeperSession;

/**
 * 缓存预热线程
 * @ClassName CachePrewarmThread
 * @Description
 * @Author 47980
 * @Date 2020/5/4 18:39
 * @Version V_1.0
 **/
public class CachePrewarmThread extends Thread {

    @Override
    public void run() {
        CacheService cacheService = (CacheService) SpringContext.getApplicationContext().getBean("cacheService");

        ZookeeperSession zkSession = ZookeeperSession.getInstance();

        // 获取storm taskid 列表
        String taskidList = zkSession.getNodeData("/taskid-list");

        System.out.println("【CachePrewarmThread 获取到taskid 列表】 taskidlist="+taskidList);

        if (taskidList != null && !"".equals(taskidList)) {
            String[] taskIdListSplited = taskidList.split(",");
            for (String taskId : taskIdListSplited) {
                String taskidLockPath = "/taskid-lock-" + taskId;
                boolean result = zkSession.acquireFastFailedDistributeLock(taskidLockPath);
                if (!result) {
                    continue;
                }

                String taskidStatusLockPath = "/taskid-status-lock-" + taskId;

                zkSession.acquireDistributeLock(taskidStatusLockPath);

                String taskidStatus = zkSession.getNodeData("/taskid-status-" + taskId);
                System.out.println("【CachePrewarmThread 获取task的预热状态】taskid="+taskId+", status="+taskidStatus);
                // 如果为空，说明没有人预热，那么这里就去预热
                if ("".equals(taskidStatus)) {
                    String productList = zkSession.getNodeData("/task-hot-product-list-"+taskId);
                    System.out.println("【CachePrewarmThread 获取到task热门商品列表】productList="+productList);
                    JSONArray productidJSONArray = JSONArray.parseArray(productList);
                    for (int i = 0; i < productidJSONArray.size(); i++) {
                        Long productId = productidJSONArray.getLong(i);

                        String productInfoJSON = "{\"id\":"+productId+",\"name\":\"iphone 11\",\"price\":11000,\"pictureUrl\":\"a.jpg,b.jpg\",\"specification\":\"iphone 11 的售后规格\",\"service\":\"iphone 11 的售后服务\",\"color\":\"红色, 紫色, 粉色\",\"size\":\"1111px\",\"shopId\": 1 ,\"modifiedTime\": \"2020-05-02 12:00:00\"}";
                        ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);

                        cacheService.saveProductInfo2LocalCache(productInfo);
                        System.out.println("【CachePrewarmThread 将商品数据设置到本地缓存中】productInfo="+productInfo);
                        cacheService.saveProductInfo2RedisCache(productInfo);
                        System.out.println("【CachePrewarmThread 将商品数据设置到redis缓存中】productInfo="+productInfo);
                    }

                    zkSession.createNode("/taskid-status-" + taskId);
                    zkSession.setNodeData("/taskid-status-" + taskId, "success");
                }

                zkSession.releaseDistributeLock(taskidStatusLockPath);

                zkSession.releaseDistributeLock(taskidLockPath);
            }

        }
    }
}
