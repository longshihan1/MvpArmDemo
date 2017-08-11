package com.longshihan.arm.strategy.imageloader.glide;

import android.content.Context;

import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;

import java.io.InputStream;

/**
 * @author Administrator
 * @time 2016/12/9 11:51
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class GLideImageSizeModelFactory implements ModelLoaderFactory<GlideImageSizeModel, InputStream> {
    @Override
    public ModelLoader<GlideImageSizeModel, InputStream> build(Context context, GenericLoaderFactory factories) {
        return new GlideImageSizeUrlLoader( context );
    }

    @Override
    public void teardown() {

    }
}
