package com.longshihan.arm.dagger.component;

import android.app.Application;

import com.google.gson.Gson;
import com.longshihan.arm.base.delegate.AppDelegate;
import com.longshihan.arm.dagger.module.AppModule;
import com.longshihan.arm.dagger.module.ClientModule;
import com.longshihan.arm.dagger.module.GlobalConfigModule;
import com.longshihan.arm.integration.AppManager;
import com.longshihan.arm.mvp.IRepositoryManager;
import com.longshihan.arm.strategy.imageloader.ImageLoader;

import java.io.File;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Component;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import okhttp3.OkHttpClient;

/**
 * @author Administrator
 * @time 2017/8/10 13:50
 * @des 类作用：初始化一些常用的数据
 */

@Singleton
@Component(modules = {AppModule.class, ClientModule.class, GlobalConfigModule.class})
public interface AppComponent {
    Application application();

    //RXjava错误管理类
    RxErrorHandler rxErrorHandler();

    //OKHttp
    OkHttpClient okHttpClient();

    //图片管理器,用于加载图片的管理类,默认使用glide,使用策略模式,可替换框架
    ImageLoader imageLoader();


    //gson
    Gson gson();

    //用于管理所有activity
    AppManager appManager();

    //用于管理所有仓库,网络请求层,以及数据缓存层
    IRepositoryManager repositoryManager();

    //缓存文件根目录(RxCache和Glide的的缓存都已经作为子文件夹在这个目录里),应该将所有缓存放到这个根目录里,便于管理和清理,可在GlobeConfigModule里配置
    File cacheFile();

    //用来存取一些整个App公用的数据,切勿大量存放大容量数据
    Map<String, Object> extras();

    //dagger注册app相关的操作
    void inject(AppDelegate delegate);
}
