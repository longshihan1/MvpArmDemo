package com.longshihan.arm.base.delegate;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;

import com.longshihan.arm.base.App;
import com.longshihan.arm.dagger.component.AppComponent;
import com.longshihan.arm.dagger.component.DaggerAppComponent;
import com.longshihan.arm.dagger.module.AppModule;
import com.longshihan.arm.dagger.module.ClientModule;
import com.longshihan.arm.dagger.module.GlobalConfigModule;
import com.longshihan.arm.integration.ActivityLifecycle;
import com.longshihan.arm.integration.ConfigModule;
import com.longshihan.arm.integration.ManifestParser;
import com.longshihan.arm.strategy.imageloader.glide.GlideImageConfig;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Administrator
 * @time 2017/8/10 14:14
 * @des 类作用： AppDelegate可以代理Application的生命周期,在对应的生命周期,执行对应的逻辑,因为Java只能单继承
 * 所以当遇到某些三方库需要继承于它的Application的时候,就只有自定义Application并继承于三方库的Application,这时就不用再继承BaseApplication
 * 只用在自定义Application中对应的生命周期调用AppDelegate对应的方法(Application一定要实现APP接口),框架就能照常运行
 *
 * 简而言之，这是一个仿的application的实现类，我们需要自定义的application的方法在这里面定义
 *
 *
 */

public class AppDelegate implements App, AppLifecycles {
    private Application mApplication;
    private AppComponent mAppComponent;
    @Inject
    protected ActivityLifecycle mActivityLifecycle;
    private final List<ConfigModule> mModules;
    private List<AppLifecycles> mAppLifecycles = new ArrayList<>();
    private List<Application.ActivityLifecycleCallbacks> mActivityLifecycles = new ArrayList<>();
    private ComponentCallbacks2 mComponentCallback;

    public AppDelegate(Context context) {
        this.mModules = new ManifestParser(context).parse();
        for (ConfigModule module : mModules) {
            module.injectAppLifecycle(context, mAppLifecycles);
            module.injectActivityLifecycle(context, mActivityLifecycles);
        }
    }

    @Override
    public void attachBaseContext(Context base) {
        for (AppLifecycles lifecycle : mAppLifecycles) {
            lifecycle.attachBaseContext(base);
        }
    }

    @Override
    public void onCreate(Application application) {
        this.mApplication = application;
        mAppComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(mApplication))//提供application
                .clientModule(new ClientModule())//用于提供okhttp和retrofit的单例
                .globalConfigModule(getGlobalConfigModule(mApplication, mModules))//全局配置
                .build();
        mAppComponent.inject(this);

        mAppComponent.extras().put(ConfigModule.class.getName(), mModules);

        mApplication.registerActivityLifecycleCallbacks(mActivityLifecycle);

        for (Application.ActivityLifecycleCallbacks lifecycle : mActivityLifecycles) {
            mApplication.registerActivityLifecycleCallbacks(lifecycle);
        }

        for (AppLifecycles lifecycle : mAppLifecycles) {
            lifecycle.onCreate(mApplication);
        }

        mComponentCallback = new AppComponentCallbacks(mApplication, mAppComponent);

        mApplication.registerComponentCallbacks(mComponentCallback);
    }

    @Override
    public void onTerminate(Application application) {

    }

    /**
     * 将app的全局配置信息封装进module(使用Dagger注入到需要配置信息的地方)
     * 需要在AndroidManifest中声明{@link ConfigModule}的实现类,和Glide的配置方式相似
     *
     * @return
     */
    private GlobalConfigModule getGlobalConfigModule(Context context, List<ConfigModule> modules) {

        GlobalConfigModule.Builder builder = GlobalConfigModule
                .builder();
        for (ConfigModule module : modules) {
            module.applyOptions(context, builder);
        }

        return builder.build();
    }


    /**
     * 将AppComponent返回出去,供其它地方使用, AppComponent接口中声明的方法返回的实例,在getAppComponent()拿到对象后都可以直接使用
     *
     * @return
     */
    @Override
    public AppComponent getAppComponent() {
        return mAppComponent;
    }


    private static class AppComponentCallbacks implements ComponentCallbacks2 {
        private Application mApplication;
        private AppComponent mAppComponent;

        public AppComponentCallbacks(Application application, AppComponent appComponent) {
            this.mApplication = application;
            this.mAppComponent = appComponent;
        }

        @Override
        public void onTrimMemory(int level) {

        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {

        }

        @Override
        public void onLowMemory() {
            //内存不足时清理图片请求框架的内存缓存
            mAppComponent.imageLoader().clear(mApplication, GlideImageConfig
                    .builder()
                    .isClearMemory(true)
                    .build());
        }
    }
}
