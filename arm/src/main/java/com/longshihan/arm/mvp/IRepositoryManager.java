package com.longshihan.arm.mvp;

/**
 * @author Administrator
 * @time 2017/8/10 14:12
 * @des 类作用：用于管理所有仓库,网络请求层,以及数据缓存层
 */

public interface IRepositoryManager {


    /**
     * 根据传入的Class获取对应的Retrift service
     *
     * @param service
     * @param <T>
     * @return
     */
    <T> T obtainRetrofitService(Class<T> service);

    /**
     * 根据传入的Class获取对应的RxCache service
     *
     * @param cache
     * @param <T>
     * @return
     */
    <T> T obtainCacheService(Class<T> cache);
    /**
     * 清理所有缓存
     */
    void clearAllCache();
}
