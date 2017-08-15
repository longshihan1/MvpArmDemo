package com.longshihan.mvparm.app.module;

import com.longshihan.arm.dagger.scope.FragmentScope;
import com.longshihan.mvparm.mvp.contract.UserContract;
import com.longshihan.mvparm.mvp.model.UserfModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by longshihan on 2017/8/12.
 */

@Module
public class UserFModule {
    private UserContract.View view;

    /**
     * 构建UserModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     * @param view
     */
    public UserFModule(UserContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    UserContract.View provideUserView(){
        return this.view;
    }

    @FragmentScope
    @Provides
    UserContract.Model provideUserModel(UserfModel model){
        return model;
    }
}
