package com.longshihan.arm.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.longshihan.arm.base.delegate.IActivity;
import com.longshihan.arm.mvp.IPresenter;

import javax.inject.Inject;

/**
 * @author Administrator
 * @time 2017/8/10 16:10
 * @des 类作用：因为java只能单继承,所以如果有需要继承特定Activity的三方库,那你就需要自己自定义Activity，
 * 继承于这个特定的Activity,然后按照将BaseActivity的格式,复制过去记住一定要实现{@link IActivity}
 */

public abstract class BaseActivity <P extends IPresenter> extends AppCompatActivity implements IActivity {
    protected final String TAG = this.getClass().getSimpleName();
    @Inject
    protected P mPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            int layoutResID = initView(savedInstanceState);
            if (layoutResID != 0) {//如果initView返回0,框架则不会调用setContentView(),当然也不会 Bind ButterKnife
                setContentView(layoutResID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initData(savedInstanceState);
    }
    /**
     * 是否使用eventBus,默认为使用(true)，
     *
     * @return
     */
    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null){
            mPresenter.onDestroy();
        }
        this.mPresenter = null;
    }
}
