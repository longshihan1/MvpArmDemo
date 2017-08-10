package com.longshihan.arm.strategy.imageloader;

import android.content.Context;

/**
 * @author Administrator
 * @time 2017/8/10 15:17
 * @des 类作用：
 */

public interface BaseImageLoaderStrategy<T extends ImageConfig> {
    void loadImage(Context ctx, T config);
    void clear(Context ctx, T config);
}
