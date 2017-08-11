package com.longshihan.mvparm.mvp.contract;

import com.longshihan.arm.base.DefaultAdapter;
import com.longshihan.arm.mvp.IModel;
import com.longshihan.arm.mvp.IView;
import com.longshihan.mvparm.mvp.model.entity.User;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author Administrator
 * @time 2017/8/11 16:52
 * @des 类作用：
 */

public interface UserContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void setAdapter(DefaultAdapter adapter);
        void startLoadMore();
        void endLoadMore();
        //申请权限
        RxPermissions getRxPermissions();
    }
    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<List<User>> getUsers(int lastIdQueried, boolean update);
    }
}
