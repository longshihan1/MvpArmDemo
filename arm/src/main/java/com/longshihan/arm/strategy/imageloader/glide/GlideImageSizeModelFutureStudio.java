package com.longshihan.arm.strategy.imageloader.glide;

/**
 * @author Administrator
 * @time 2016/12/9 11:56
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class GlideImageSizeModelFutureStudio implements GlideImageSizeModel {
    String baseImageUrl;

    public GlideImageSizeModelFutureStudio(String baseImageUrl) {
        this.baseImageUrl = baseImageUrl;
    }

    @Override
    public String requestCustomSizeUrl(int width, int height) {
        return baseImageUrl + "@" + height + "h_" + width+"w_2e";
    }
}
