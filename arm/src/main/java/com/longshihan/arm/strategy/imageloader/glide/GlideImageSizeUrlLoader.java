package com.longshihan.arm.strategy.imageloader.glide;

import android.content.Context;

import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

/**
 * @author Administrator
 * @time 2016/12/9 11:54
 * @des ${TODO}
 */
public class GlideImageSizeUrlLoader extends BaseGlideUrlLoader<GlideImageSizeModel> {
    public GlideImageSizeUrlLoader(Context context) {
        super(context);
    }

    @Override
    protected String getUrl(GlideImageSizeModel model, int width, int height) {
        return model.requestCustomSizeUrl(width, height);
    }
}
