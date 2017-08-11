package com.longshihan.mvparm.app.component;

import com.longshihan.arm.dagger.component.AppComponent;
import com.longshihan.arm.dagger.scope.ActivityScope;
import com.longshihan.mvparm.mvp.model.UserModel;
import com.longshihan.mvparm.mvp.ui.activity.UserActivity;

import dagger.Component;

/**
 * @author Administrator
 * @time 2017/8/11 18:21
 * @des 类作用：useractivity的dagger
 */
@ActivityScope
@Component(modules = UserModel.class, dependencies = AppComponent.class)
public interface UserComponent {
    void inject(UserActivity activity);
}
