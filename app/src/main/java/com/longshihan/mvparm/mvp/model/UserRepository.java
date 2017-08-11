package com.longshihan.mvparm.mvp.model;

import android.support.annotation.NonNull;

import com.longshihan.arm.mvp.IModel;
import com.longshihan.arm.mvp.IRepositoryManager;
import com.longshihan.mvparm.mvp.model.api.cache.CommonCache;
import com.longshihan.mvparm.mvp.model.api.service.UserService;
import com.longshihan.mvparm.mvp.model.entity.User;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.Reply;

/**
 * @author Administrator
 * @time 2017/8/11 13:24
 * @des 类作用：必须实现 IModel
 * 可以根据不同的业务逻辑划分多个 Repository 类,多个业务逻辑相近的页面可以使用同一个 Repository 类
 * 无需每个页面都创建一个独立的 Repository
 */

public class UserRepository implements IModel {
    private IRepositoryManager mManager;
    public static final int USERS_PER_PAGE = 10;

    /**
     * 必须含有一个接收IRepositoryManager接口的构造函数,否则会报错
     * @param manager
     */
    public UserRepository(IRepositoryManager manager) {
        this.mManager = manager;
    }

    public Observable<List<User>> getUsers(int lastIdQueried, boolean update) {
        Observable<List<User>> users = mManager.obtainRetrofitService(UserService.class)
                .getUsers(lastIdQueried, USERS_PER_PAGE);
        //使用rxcache缓存,上拉刷新则不读取缓存,加载更多读取缓存
        return mManager.obtainCacheService(CommonCache.class)
                .getUsers(users
                        , new DynamicKey(lastIdQueried)
                        , new EvictDynamicKey(update))
                .flatMap(new Function<Reply<List<User>>, ObservableSource<List<User>>>() {
                    @Override
                    public ObservableSource<List<User>> apply(@NonNull Reply<List<User>> listReply) throws Exception {
                        return Observable.just(listReply.getData());
                    }
                });
    }

    @Override
    public void onDestroy() {

    }
}
