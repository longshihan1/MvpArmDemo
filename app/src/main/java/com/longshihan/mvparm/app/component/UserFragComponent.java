package com.longshihan.mvparm.app.component;

import com.longshihan.arm.dagger.component.AppComponent;
import com.longshihan.arm.dagger.scope.ActivityScope;
import com.longshihan.mvparm.app.module.UserModule;
import com.longshihan.mvparm.mvp.ui.fragment.UserFragment;

import dagger.Component;

/**
 * @author Administrator
 * @time 2017/8/11 18:21
 * @des 类作用：useractivity的dagger
 */
@ActivityScope
@Component(modules = UserModule.class, dependencies = AppComponent.class)
public interface UserFragComponent {
    void inject(UserFragment fragment);
}
