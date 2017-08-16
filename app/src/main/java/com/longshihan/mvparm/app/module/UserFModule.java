package com.longshihan.mvparm.app.module;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.longshihan.arm.dagger.scope.FragmentScope;
import com.longshihan.arm.mvp.IView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by longshihan on 2017/8/12.
 */

@Module
public class UserFModule {
    private IView view;
    private Fragment mFragment;

    public UserFModule(Fragment fragment) {
        this.mFragment = fragment;
    }


    @Provides
    @FragmentScope
    public Activity provideActivity() {
        return mFragment.getActivity();
    }

}
