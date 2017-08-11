package com.longshihan.arm.mvp;

import android.view.View;

import org.simple.eventbus.EventBus;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by jess on 16/4/28.
 */
public class BasePresenter<M extends IModel, V extends IView> implements IPresenter {
    protected final String TAG = this.getClass().getSimpleName();
    //rx2，和1的使用CompositeDisposable统一管理persenter线程
    protected CompositeDisposable mCompositeDisposable;
    protected M mModel;
    protected V mRootView;
    protected WeakReference<V> mViewRef;


    /**
     * 关联 p和V
     *
     * @param view
     */
    public void attachView(V rootView) {
        mViewRef = new WeakReference<V>(rootView);
    }

    public BasePresenter() {
        onStart();
    }

    public BasePresenter(M model, V rootView) {
        this.mModel = model;
        this.mRootView = rootView;
        attachView(rootView);
        onStart();
    }

    public BasePresenter(V rootView) {
        this.mRootView = rootView;
        onStart();
    }

    public void onStart() {
        if (useEventBus())//如果要使用eventbus请将此方法返回true
            EventBus.getDefault().register(this);//注册eventbus
    }

    @Override
    public void onDestroy() {
        if (useEventBus())//如果要使用eventbus请将此方法返回true
            EventBus.getDefault().unregister(this);//解除注册eventbus
        unDispose();//解除订阅
        if (mModel != null)
            mModel.onDestroy();
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
        this.mModel = null;
        this.mRootView = null;
        this.mCompositeDisposable = null;
    }

    /**
     * 获取view对象
     *
     * @return
     */
    protected V getView() {
        if (mViewRef == null) {
            return null;
        }
        return mViewRef.get();
    }

    /**
     * 是否使用eventBus,默认为使用(true)，
     *
     * @return
     */
    public boolean useEventBus() {
        return true;
    }


    public void addDispose(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);//将所有disposable放入,集中处理
    }

    public void unDispose() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();//保证activity结束时取消所有正在执行的订阅
        }
    }

}
