package com.longshihan.mvparm.mvp.model;

import com.longshihan.arm.dagger.scope.FragmentScope;
import com.longshihan.arm.mvp.BaseModel;
import com.longshihan.arm.mvp.IRepositoryManager;
import com.longshihan.mvparm.mvp.contract.UserContract;
import com.longshihan.mvparm.mvp.model.api.cache.CommonCache;
import com.longshihan.mvparm.mvp.model.api.service.UserService;
import com.longshihan.mvparm.mvp.model.entity.User;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.Reply;

/**
 * @author Administrator
 * @time 2017/8/11 16:54
 * @des 类作用：user的model对象
 */

@FragmentScope
public class UserfModel extends BaseModel implements UserContract.Model {
    public static final int USERS_PER_PAGE = 10;

    @Inject
    public UserfModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    /**
     * 获取user的集合
     *
     * @param lastIdQueried
     * @param update
     * @return
     */
    @Override
    public Observable<List<User>> getUsers(int lastIdQueried, boolean update) {
        Observable<List<User>> users = mRepositoryManager.obtainRetrofitService(UserService.class)
                .getUsers(lastIdQueried, USERS_PER_PAGE);
        //使用rxcache缓存,上拉刷新则不读取缓存,加载更多读取缓存
        return mRepositoryManager.obtainCacheService(CommonCache.class)
                .getUsers(users,
                        new DynamicKey(lastIdQueried)
                        , new EvictDynamicKey(update))
                .flatMap(new Function<Reply<List<User>>, ObservableSource<List<User>>>() {
                    @Override
                    public ObservableSource<List<User>> apply(Reply<List<User>> listReply) throws Exception {
                        return Observable.just(listReply.getData());
                    }
                });
    }
}
