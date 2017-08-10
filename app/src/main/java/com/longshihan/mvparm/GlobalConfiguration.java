package com.longshihan.mvparm;

import android.app.Application;
import android.content.Context;

import com.longshihan.arm.base.delegate.AppLifecycles;
import com.longshihan.arm.dagger.module.GlobalConfigModule;
import com.longshihan.arm.integration.ConfigModule;

import java.util.List;

/**
 * app 的全局配置信息在此配置,需要将此实现类声明到 AndroidManifest 中
 * Created by longshihan on 2017/8/10.
 */

public class GlobalConfiguration implements ConfigModule {
    //public static String sDomain = Api.APP_DOMAIN;
    @Override
    public void applyOptions(Context context, GlobalConfigModule.Builder builder) {

    }

    @Override
    public void injectAppLifecycle(Context context, List<AppLifecycles> lifecycles) {

    }

    @Override
    public void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles) {

    }

   /* @Override
    public void injectFragmentLifecycle(Context context, List<FragmentManager.FragmentLifecycleCallbacks> lifecycles) {

    }*/
}
