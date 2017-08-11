package com.longshihan.mvparm.mvp.model.api.service;

import com.longshihan.mvparm.mvp.model.entity.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * @author Administrator
 * @time 2017/8/11 13:28
 * @des 类作用：存放关于用户的一些api,retrofit的接口样式
 */

public interface UserService {
    String HEADER_API_VERSION = "Accept: application/vnd.github.v3+json";

    @Headers({HEADER_API_VERSION})
    @GET("/users")
    Observable<List<User>> getUsers(@Query("since") int lastIdQueried, @Query("per_page") int perPage);
}
