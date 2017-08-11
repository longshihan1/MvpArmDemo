package com.longshihan.mvparm.mvp.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.longshihan.arm.base.App;
import com.longshihan.arm.base.BaseHolder;
import com.longshihan.arm.dagger.component.AppComponent;
import com.longshihan.arm.strategy.imageloader.ImageLoader;
import com.longshihan.arm.strategy.imageloader.glide.GlideImageConfig;
import com.longshihan.mvparm.R;
import com.longshihan.mvparm.mvp.model.entity.User;

/**
 * @author Administrator
 * @time 2017/8/11 13:35
 * @des 类作用：
 */

public class UserItemHolder extends BaseHolder<User>{
    ImageView mAvater;
    TextView mName;
    private AppComponent mAppComponent;
    private ImageLoader mImageLoader;//用于加载图片的管理类,默认使用glide,使用策略模式,可替换框架

    public UserItemHolder(View itemView) {
        super(itemView);
        mAvater= (ImageView) itemView.findViewById(R.id.iv_avatar);
        mName= (TextView) itemView.findViewById(R.id.tv_name);
        //可以在任何可以拿到Application的地方,拿到AppComponent,从而得到用Dagger管理的单例对象
        mAppComponent = ((App) itemView.getContext().getApplicationContext()).getAppComponent();
        mImageLoader = mAppComponent.imageLoader();
    }

    @Override
    public void setData(User data, int position) {
        mName.setText(data.getLogin());
        mImageLoader.loadImage(mAppComponent.appManager().getCurrentActivity() == null
                        ? mAppComponent.application() : mAppComponent.appManager().getCurrentActivity(),
                GlideImageConfig
                        .builder()
                        .url(data.getAvatarUrl())
                        .imageView(mAvater)
                        .build());
    }

    @Override
    protected void onRelease() {
        mImageLoader.clear(mAppComponent.application(), GlideImageConfig.builder()
                .imageViews(mAvater)
                .build());
    }
}
