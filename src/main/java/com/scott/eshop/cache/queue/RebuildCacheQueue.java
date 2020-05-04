package com.scott.eshop.cache.queue;

import com.scott.eshop.cache.model.ProductInfo;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 重建缓存队列
 * @ClassName RebuildCacheQueue
 * @Description
 * @Author 47980
 * @Date 2020/5/2 17:25
 * @Version V_1.0
 **/
public class RebuildCacheQueue {

    private ArrayBlockingQueue<ProductInfo> queue = new ArrayBlockingQueue<>(1000);

    public void addMessage (ProductInfo productInfo) {
        try {
            queue.put(productInfo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ProductInfo takeProductInfo() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 内部单例类
     */
    public static class Singlenton {
        private static RebuildCacheQueue instance;

        static {
            instance = new RebuildCacheQueue();
        }

        public static RebuildCacheQueue getInstance() {
            return instance;
        }
    }

    /**
     * 获取单例类
     * @return
     */
    public static RebuildCacheQueue getInstance() {
        return Singlenton.getInstance();
    }

    /**
     * 快速初始化
     */
    public static void init () {
        Singlenton.getInstance();
    }
}
