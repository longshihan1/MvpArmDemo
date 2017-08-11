package com.longshihan.arm.mvp;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import io.rx_cache2.internal.RxCache;
import retrofit2.Retrofit;

/**
 * @author Administrator
 * @time 2017/8/10 14:41
 * @des 类作用：用来管理所有业务逻辑的仓库,网络请求层,以及数据缓存层,以后可以添加数据库请求层
 * 所有仓库不直接持有却通过RepositoryManager拿到需要的请求层做数据处理的好处是
 * 仓库可以直接和对应的请求层解耦,比如网路请求层,需要从Retrofit替换为其他网络请求库,这时就仓库就不会受到影响
 */

@Singleton
public class RepositoryManager implements IRepositoryManager {

    //lazy是懒加载
    private Lazy<Retrofit> mRetrofit;
    private Lazy<RxCache> mRxCache;
    private final Map<String, Object> mRetrofitServiceCache = new HashMap<>();
    private final Map<String, Object> mCacheServiceCache = new HashMap<>();

    @Inject
    public RepositoryManager(Lazy<Retrofit> retrofit, Lazy<RxCache> rxCache) {
        this.mRetrofit = retrofit;
        this.mRxCache = rxCache;
    }



    /**
     * 根据传入的Class获取对应的Retrift service
     *
     * @param service
     * @param <T>
     * @return
     */
    @Override
    public <T> T obtainRetrofitService(Class<T> service) {
        T retrofitService;
        synchronized (mRetrofitServiceCache) {
            retrofitService = (T) mRetrofitServiceCache.get(service.getName());
            if (retrofitService == null) {
                retrofitService = mRetrofit.get().create(service);
                mRetrofitServiceCache.put(service.getName(), retrofitService);
            }
        }
        return retrofitService;
    }

    /**
     * 根据传入的Class获取对应的RxCache service
     *
     * @param cache
     * @param <T>
     * @return
     */
    @Override
    public <T> T obtainCacheService(Class<T> cache) {
        T cacheService;
        synchronized (mCacheServiceCache) {
            cacheService = (T) mCacheServiceCache.get(cache.getName());
            if (cacheService == null) {
                cacheService = mRxCache.get().using(cache);
                mCacheServiceCache.put(cache.getName(), cacheService);
            }
        }
        return cacheService;
    }

    /**
     * 清理所有缓存
     */
    @Override
    public void clearAllCache() {
        mRxCache.get().evictAll();
    }

}
