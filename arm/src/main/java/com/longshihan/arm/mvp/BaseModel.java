package com.longshihan.arm.mvp;

/**
 * @author Administrator
 * @time 2017/8/11 16:55
 * @des 类作用：
 */

public class BaseModel implements IModel {
    protected IRepositoryManager mRepositoryManager;//用于管理网络请求层,以及数据缓存层，在子层就减少一些处理

    public BaseModel(IRepositoryManager repositoryManager) {
        this.mRepositoryManager = repositoryManager;
    }

    @Override
    public void onDestroy() {
        mRepositoryManager = null;
    }
}
