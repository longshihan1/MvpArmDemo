package com.longshihan.mvparm.mvp.persenter;

import android.app.Application;

import com.longshihan.arm.base.DefaultAdapter;
import com.longshihan.arm.dagger.scope.ActivityScope;
import com.longshihan.arm.integration.AppManager;
import com.longshihan.arm.mvp.BasePresenter;
import com.longshihan.mvparm.mvp.contract.UserContract;
import com.longshihan.mvparm.mvp.model.entity.User;
import com.longshihan.mvparm.mvp.ui.adapter.UserAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

/**
 * @author Administrator
 * @time 2017/8/11 13:23
 * @des 类作用：
 */

@ActivityScope
public class UserPersenter extends BasePresenter<UserContract.Model,UserContract.View> {
    private RxErrorHandler mErrorHandler;
    private AppManager mAppManager;
    private Application mApplication;
    private List<User> mUsers = new ArrayList<>();
    private DefaultAdapter mAdapter;
    private int lastUserId = 1;
    private boolean isFirst = true;
    private int preEndIndex;

    @Inject
    public UserPersenter(UserContract.Model model, UserContract.View rootView, RxErrorHandler handler
            , AppManager appManager, Application application) {
        super(model, rootView);
        this.mApplication = application;
        this.mErrorHandler = handler;
        this.mAppManager = appManager;
    }

    public void requestUsers(final boolean pullToRefresh) {
        if (mAdapter == null) {
            mAdapter = new UserAdapter(mUsers);
            getView().setAdapter(mAdapter);//设置Adapter
        }


        //权限管理使用注解的形式或者是rx的形式
       /* //请求外部存储权限用于适配android6.0的权限管理机制
        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
                //request permission success, do something.
            }

            @Override
            public void onRequestPermissionFailure() {
                msg.getTarget().showMessage("Request permissons failure");
            }
        }, (RxPermissions) msg.objs[1], mErrorHandler);*/


        if (pullToRefresh) lastUserId = 1;//下拉刷新默认只请求第一页,页数

        //关于RxCache缓存库的使用请参考 http://www.jianshu.com/p/b58ef6b0624b

        boolean isEvictCache = pullToRefresh;//是否驱逐缓存,为ture即不使用缓存,每次下拉刷新即需要最新数据,则不使用缓存

        if (pullToRefresh && isFirst) {//默认在第一次下拉刷新时使用缓存
            isFirst = false;
            isEvictCache = false;
        }

        mModel.getUsers(lastUserId,isEvictCache)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3,2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
        .doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
               addDispose(disposable); //在订阅时必须调用这个方法,不然Activity退出时可能内存泄漏
                if (pullToRefresh)
                    getView().showLoading();//显示下拉刷新的进度条
                else {
                    //显示上拉加载更多的进度条
                    getView().endLoadMore();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .doFinally(new Action() {
            @Override
            public void run() throws Exception {
                if (pullToRefresh) {
                    getView().hideLoading();//隐藏下拉刷新的进度条
                } else {
                    //隐藏上拉加载更多的进度条
                    getView().endLoadMore();
                }
            }
        }).subscribe(new ErrorHandleSubscriber<List<User>>(mErrorHandler) {
            @Override
            public void onNext(List<User> users) {
                lastUserId = users.get(users.size() - 1).getId();//记录最后一个id,用于下一次请求

                if (pullToRefresh) mUsers.clear();//如果是下拉刷新则清空列表

                preEndIndex = mUsers.size();//更新之前列表总长度,用于确定加载更多的起始位置
                mUsers.addAll(users);

                if (pullToRefresh)
                    mAdapter.notifyDataSetChanged();
                else
                    mAdapter.notifyItemRangeInserted(preEndIndex, users.size());
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mAdapter = null;
        this.mUsers = null;
        this.mErrorHandler = null;
    }
}
