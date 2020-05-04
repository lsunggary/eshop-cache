package com.scott.eshop.cache.kafka;

import com.alibaba.fastjson.JSONObject;
import com.scott.eshop.cache.model.ProductInfo;
import com.scott.eshop.cache.model.ShopInfo;
import com.scott.eshop.cache.service.CacheService;
import com.scott.eshop.cache.spring.SpringContext;
import com.scott.eshop.cache.zk.ZookeeperSession;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * kafka 消息处理线程
 * @ClassName KafkaMassageProcessor
 * @Description
 * @Author 47980
 * @Date 2020/5/1 22:24
 * @Version V_1.0
 **/
@SuppressWarnings("rawtypes")
public class KafkaMassageProcessor implements Runnable {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private KafkaStream kafkaStream;

    private CacheService cacheService;

    public KafkaMassageProcessor(KafkaStream kafkaStream) {
        this.kafkaStream = kafkaStream;
        this.cacheService = (CacheService) SpringContext.getApplicationContext().getBean("cacheService");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        ConsumerIterator iterator = kafkaStream.iterator();

        while (iterator.hasNext()) {
            String message = new String((byte[]) iterator.next().message());

            // 首先将message转化成json对象
            JSONObject messageJSONObject = JSONObject.parseObject(message);

            // 从这里提取出消息对应的服务标识
            try {
                String serviceId = messageJSONObject.getString("serviceId");
                // 如果是商品信息服务
                if ("productInfoService".equals(serviceId)) {
                    processProductInfoChangeMessage(messageJSONObject);
                } else if ("shopInfoService".equals(serviceId)) {
                    processShopInfoChangeMessage(messageJSONObject);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * 处理商品信息更变的消息
     * @param messageJSONObject
     */
    private void processProductInfoChangeMessage(JSONObject messageJSONObject) {
        // 提取出商品id
        Long productId = messageJSONObject.getLong("productId");

        // 调用商品服务信息的接口
        // 这里使用模拟数据
        String productInfoJSON = "{\"id\":6,\"name\":\"iphone 11\",\"price\":11000,\"pictureUrl\":\"a.jpg,b.jpg\",\"specification\":\"iphone 11 的售后规格\",\"service\":\"iphone 11 的售后服务\",\"color\":\"红色, 紫色, 粉色\",\"size\":\"1111px\",\"shopId\": 1 ,\"modifiedTime\": \"2020-05-02 12:00:00\"}";
        ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);

        // 在将数据直接写入redis缓存之前，应该先加一个分布式锁
        ZookeeperSession zkSession = ZookeeperSession.getInstance();
        zkSession.acquireDistributeLock(productId);

        //获取到了锁
        // 先从redis中获取数据
        ProductInfo existProductInfo = cacheService.getProductInfoFromRedisCache(productId);
        if (existProductInfo != null) {
            // 比较时间版本
            try {
                Date date = sdf.parse(productInfo.getModifiedTime());
                Date existedDate = sdf.parse(existProductInfo.getModifiedTime());

                if (date.before(existedDate)) {
                    System.out.println("current date = " + productInfo.getModifiedTime() + " is before existed date["+existProductInfo.getModifiedTime()+"]");
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println(" current date is after existed date[" + existProductInfo.getModifiedTime()+"]");
        } else {
            System.out.println("existed product info is null");
        }

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        cacheService.saveProductInfo2LocalCache(productInfo);
        System.out.println("================== 获取刚保存到本地缓存的商品信息 ================== ：" + cacheService.getProductInfoFromLocalCache(productId));
        cacheService.saveProductInfo2RedisCache(productInfo);

        // 分布式锁释放
        zkSession.releaseDistributeLock(productId);
    }

    /**
     * 处理店铺信息更变的消息
     * @param messageJSONObject
     */
    private void processShopInfoChangeMessage(JSONObject messageJSONObject) {
        // 提取出商品id 和店铺id
        Long productId = messageJSONObject.getLong("productId");
        Long shopId = messageJSONObject.getLong("shopId");

        // 调用商品服务信息的接口
        // 这里使用模拟数据
        String shopInfoJSON = "{\"id\":1,\"name\":\"小张的手机店\",\"level\": 3,\"goodCommentRate\": 0.99 }";
        ShopInfo shopInfo = JSONObject.parseObject(shopInfoJSON, ShopInfo.class);
        cacheService.saveShopInfo2LocalCache(shopInfo);
        System.out.println("================== 获取刚保存到本地缓存的店铺信息 ================== ：" + cacheService.getShopInfoFromLocalCache(shopId));
        cacheService.saveShopInfo2RedisCache(shopInfo);
    }
}
