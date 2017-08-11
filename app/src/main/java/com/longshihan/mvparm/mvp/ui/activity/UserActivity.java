package com.longshihan.mvparm.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.longshihan.arm.base.BaseActivity;
import com.longshihan.arm.base.DefaultAdapter;
import com.longshihan.arm.dagger.component.AppComponent;
import com.longshihan.arm.utils.UiUtils;
import com.longshihan.mvparm.R;
import com.longshihan.mvparm.mvp.contract.UserContract;
import com.longshihan.mvparm.mvp.persenter.UserPersenter;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class UserActivity extends BaseActivity<UserPersenter> implements UserContract.View, SwipeRefreshLayout.OnRefreshListener {

    private boolean isLoadingMore;
    private RxPermissions mRxPermissions;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        this.mRxPermissions = new RxPermissions(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_user;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mPresenter.requestUsers(true);//打开app时自动加载列表

    }

    private void initRecycleView() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        UiUtils.configRecycleView(mRecyclerView, new GridLayoutManager(this, 2));

    }

    @Override
    public void showLoading() {
        Timber.tag(TAG).w("showLoading");
        Observable.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
    }

    @Override
    public void hideLoading() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMessage(String message) {
        UiUtils.snackbarText(message);
    }


    @Override
    public void launchActivity(Intent intent) {
        UiUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }


    @Override
    public void setAdapter(DefaultAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
        initRecycleView();
    }
    @Override
    public void onRefresh() {
        mPresenter.requestUsers(true);
    }
    @Override
    public void startLoadMore() {
        isLoadingMore = true;
    }

    @Override
    public void endLoadMore() {
        isLoadingMore = false;
    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
    }


    @Override
    protected void onDestroy() {
        DefaultAdapter.releaseAllHolder(mRecyclerView);//super.onDestroy()之后会unbind,所有view被置为null,所以必须在之前调用
        super.onDestroy();
        this.mRxPermissions = null;
    }
}
