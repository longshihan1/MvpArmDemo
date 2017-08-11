package com.longshihan.mvparm;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.longshihan.arm.base.delegate.AppLifecycles;
import com.longshihan.arm.dagger.module.AppModule;
import com.longshihan.arm.dagger.module.ClientModule;
import com.longshihan.arm.dagger.module.GlobalConfigModule;
import com.longshihan.arm.http.GlobalHttpHandler;
import com.longshihan.arm.http.RequestInterceptor;
import com.longshihan.arm.integration.ConfigModule;
import com.longshihan.arm.utils.UiUtils;
import com.longshihan.mvparm.mvp.model.api.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.rx_cache2.internal.RxCache;
import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * app 的全局配置信息在此配置,需要将此实现类声明到 AndroidManifest 中
 * Created by longshihan on 2017/8/10.
 */

public class GlobalConfiguration implements ConfigModule {
    //public static String sDomain = Api.APP_DOMAIN;

    /**
     * 在application初始化的时候使用
     * @param context
     * @param builder
     */
    @Override
    public void applyOptions(Context context, GlobalConfigModule.Builder builder) {
        if (!BuildConfig.DEBUG){
            builder.printHttpLogLevel(RequestInterceptor.Level.NONE);
        }
        //如果 BaseUrl 在 App 启动时不能确定,需要请求服务器接口动态获取,请使用以下代码
        //并且使用 Okhttp (AppComponent中提供) 请求服务器获取到正确的 BaseUrl 后赋值给 GlobalConfiguration.sDomain
        //切记整个过程必须在第一次调用 Retrofit 接口之前完成,如果已经调用过 Retrofit 接口,将不能动态切换 BaseUrl
        //                .baseurl(new BaseUrl() {
        //                    @Override
        //                    public HttpUrl url() {
        //                        return HttpUrl.parse(sDomain);
        //                    }
        //                })
        //统一注入到ClientModule里面对OKhttp做操作
        builder.baseurl(Api.APP_DOMAIN)
                .globalHttpHandler(new GlobalHttpHandler() {// 这里可以提供一个全局处理Http请求和响应结果的处理类,
                    // 这里可以比客户端提前一步拿到服务器返回的结果,可以做一些操作,比如token超时,重新获取
                    @Override
                    public Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response) {
                         /* 这里可以先客户端一步拿到每一次http请求的结果,可以解析成json,做一些操作,如检测到token过期后
                           重新请求token,并重新执行请求 */
                        try {
                            if (!TextUtils.isEmpty(httpResult) && RequestInterceptor.isJson(response.body().contentType())) {
                                JSONArray array = new JSONArray(httpResult);
                                JSONObject object = (JSONObject) array.get(0);
                                String login = object.getString("login");
                                String avatar_url = object.getString("avatar_url");
                                Timber.w("Result ------> " + login + "    ||   Avatar_url------> " + avatar_url);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            return response;
                        }

                     /* 这里如果发现token过期,可以先请求最新的token,然后在拿新的token放入request里去重新请求
                        注意在这个回调之前已经调用过proceed,所以这里必须自己去建立网络请求,如使用okhttp使用新的request去请求
                        create a new request and modify it accordingly using the new token
                        Request newRequest = chain.request().newBuilder().header("token", newToken)
                                             .build();

                        retry the request

                        response.body().close();
                        如果使用okhttp将新的请求,请求成功后,将返回的response  return出去即可
                        如果不需要返回新的结果,则直接把response参数返回出去 */

                        return response;
                    }
                    // 这里可以在请求服务器之前可以拿到request,做一些操作比如给request统一添加token或者header以及参数加密等操作
                    //还可以添加统一数据-比如token，手机信息等

                    @Override
                    public Request onHttpRequestBefore(Interceptor.Chain chain, Request request) {
                        /* 如果需要再请求服务器之前做一些操作,则重新返回一个做过操作的的requeat如增加header,不做操作则直接返回request参数
                           return chain.request().newBuilder().header("token", tokenId)
                                  .build(); */
                        return request;
                    }
                })
        .responseErrorListener(new ResponseErrorListener() {
            @Override
            public void handleResponseError(Context context, Throwable t) {
                  /* 用来提供处理所有错误的监听
                       rxjava必要要使用ErrorHandleSubscriber(默认实现Subscriber的onError方法),此监听才生效 */
                Timber.tag("Catch-Error").w(t.getMessage());
                //这里不光是只能打印错误,还可以根据不同的错误作出不同的逻辑处理
                String msg = "未知错误";
                if (t instanceof UnknownHostException) {
                    msg = "网络不可用";
                } else if (t instanceof SocketTimeoutException) {
                    msg = "请求网络超时";
                } else if (t instanceof HttpException) {
                    HttpException httpException = (HttpException) t;
                    msg = convertStatusCode(httpException);
                } else if (t instanceof JsonParseException || t instanceof ParseException || t instanceof JSONException || t instanceof JsonIOException) {
                    msg = "数据解析错误";
                }
                UiUtils.snackbarText(msg);
            }
        })
        .gsonConfiguration(new AppModule.GsonConfiguration() {
            @Override
            public void configGson(Context context, GsonBuilder builder) {
                //这里可以自己自定义配置Gson的参数
                builder.serializeNulls()//支持序列化null的参数
                        .enableComplexMapKeySerialization();//支持将序列化key为object的map,默认只能序列化key为string的map
            }
        })
        .retrofitConfiguration(new ClientModule.RetrofitConfiguration() {
            @Override
            public void configRetrofit(Context context, Retrofit.Builder builder) {
                //这里可以自己自定义配置Retrofit的参数,甚至你可以替换系统配置好的okhttp对象
                //   retrofitBuilder.addConverterFactory(FastJsonConverterFactory.create());//比如使用fastjson替代gson

            }
        })
        .okhttpConfiguration(new ClientModule.OkhttpConfiguration() {//这里可以自己自定义配置Okhttp的参数
            @Override
            public void configOkhttp(Context context, OkHttpClient.Builder builder) {
                builder.writeTimeout(10, TimeUnit.SECONDS);
                //开启使用一行代码监听 Retrofit／Okhttp 上传下载进度监听,以及 Glide 加载进度监听 详细使用方法查看 https://github.com/JessYanCoding/ProgressManager
               // ProgressManager.getInstance().with(builder);
            }
        })
        .rxCacheConfiguration(new ClientModule.RxCacheConfiguration() {//这里可以自己自定义配置RxCache的参数
            @Override
            public void configRxCache(Context context, RxCache.Builder builder) {
                builder.useExpiredDataIfLoaderNotAvailable(true);
            }
        })
        ;
    }

    /**
     * Application的生命周期中注入一些操作
     * 绑定app的生命周期
     * @param context
     * @param lifecycles
     */
    @Override
    public void injectAppLifecycle(Context context, List<AppLifecycles> lifecycles) {
        lifecycles.add(new AppLifecycles() {
            @Override
            public void attachBaseContext(Context base) {
                //绑定Activity对象
                //MultiDex.install(base);  //这里比 onCreate 先执行,常用于 MultiDex 初始化,插件化框架的初始化
            }

            @Override
            public void onCreate(Application application) {
                if (BuildConfig.DEBUG) {//Timber初始化
                    //Timber 是一个日志框架容器,外部使用统一的Api,内部可以动态的切换成任何日志框架(打印策略)进行日志打印
                    //并且支持添加多个日志框架(打印策略),做到外部调用一次 Api,内部却可以做到同时使用多个策略
                    //比如添加三个策略,一个打印日志,一个将日志保存本地,一个将日志上传服务器
                    Timber.plant(new Timber.DebugTree());
                    // 如果你想将框架切换为 Logger 来打印日志,请使用下面的代码,如想切换为其他日志框架请根据下面的方式扩展
                    //                    Logger.addLogAdapter(new AndroidLogAdapter());
                    //                    Timber.plant(new Timber.DebugTree() {
                    //                        @Override
                    //                        protected void log(int priority, String tag, String message, Throwable t) {
                    //                            Logger.log(priority, tag, message, t);
                    //                        }
                    //                    });
                    //leakCanary内存泄露检查

                }
            }

            @Override
            public void onTerminate(Application application) {

            }
        });
    }

    /**
     * 绑定Activity的生命周期中
     * @param context
     * @param lifecycles
     */
    @Override
    public void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles) {
        lifecycles.add(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Timber.w(activity + " - onActivityCreated");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Timber.w(activity + " - onActivityStarted");
                if (!activity.getIntent().getBooleanExtra("isInitToolbar",false)){
                    //由于加强框架的兼容性,故将 setContentView 放到 onActivityCreated 之后,onActivityStarted 之前执行
                    //而 findViewById 必须在 Activity setContentView() 后才有效,所以将以下代码从之前的 onActivityCreated 中移动到 onActivityStarted 中执行
                    //这里全局给Activity设置toolbar和title,你想象力有多丰富,这里就有多强大,以前放到BaseActivity的操作都可以放到这里
                    //暂时可以不添加。还需要设置沉浸式，效果不错
           /*         if (activity.findViewById(R.id.toolbar) != null) {
                        if (activity instanceof AppCompatActivity) {
                            ((AppCompatActivity) activity).setSupportActionBar((Toolbar) activity.findViewById(R.id.toolbar));
                            ((AppCompatActivity) activity).getSupportActionBar().setDisplayShowTitleEnabled(false);
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                activity.setActionBar((android.widget.Toolbar) activity.findViewById(R.id.toolbar));
                                activity.getActionBar().setDisplayShowTitleEnabled(false);
                            }
                        }
                    }
                    if (activity.findViewById(R.id.toolbar_title) != null) {
                        ((TextView) activity.findViewById(R.id.toolbar_title)).setText(activity.getTitle());
                    }
                    if (activity.findViewById(R.id.toolbar_back) != null) {
                        activity.findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                activity.onBackPressed();
                            }
                        });
                    }*/
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Timber.w(activity + " - onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Timber.w(activity + " - onActivityPaused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Timber.w(activity + " - onActivityStopped");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Timber.w(activity + " - onActivitySaveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Timber.w(activity + " - onActivityDestroyed");
            }
        });
    }

   /*
    fragment不做
   @Override
    public void injectFragmentLifecycle(Context context, List<FragmentManager.FragmentLifecycleCallbacks> lifecycles) {

    }*/

    private String convertStatusCode(HttpException httpException) {
        String msg;
        if (httpException.code() == 500) {
            msg = "服务器发生错误";
        } else if (httpException.code() == 404) {
            msg = "请求地址不存在";
        } else if (httpException.code() == 403) {
            msg = "请求被服务器拒绝";
        } else if (httpException.code() == 307) {
            msg = "请求被重定向到其他页面";
        } else {
            msg = httpException.message();
        }
        return msg;
    }
}
