package com.longshihan.mvparm.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.longshihan.arm.base.BaseActivity;
import com.longshihan.arm.base.DefaultAdapter;
import com.longshihan.arm.dagger.component.AppComponent;
import com.longshihan.mvparm.R;
import com.longshihan.mvparm.mvp.contract.UserContract;
import com.longshihan.mvparm.mvp.persenter.UserPersenter;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class User2Activity extends BaseActivity<UserPersenter> implements UserContract.View{


    @Override
    public void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_user2;
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void launchActivity(Intent intent) {

    }

    @Override
    public void killMyself() {

    }

    @Override
    public void setAdapter(DefaultAdapter adapter) {

    }

    @Override
    public void startLoadMore() {

    }

    @Override
    public void endLoadMore() {

    }

    @Override
    public RxPermissions getRxPermissions() {
        return null;
    }
}
