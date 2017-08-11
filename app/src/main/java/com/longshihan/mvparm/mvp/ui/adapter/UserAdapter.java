package com.longshihan.mvparm.mvp.ui.adapter;

import android.view.View;

import com.longshihan.arm.base.BaseHolder;
import com.longshihan.arm.base.DefaultAdapter;
import com.longshihan.mvparm.R;
import com.longshihan.mvparm.mvp.model.entity.User;
import com.longshihan.mvparm.mvp.ui.viewholder.UserItemHolder;

import java.util.List;

/**
 * @author Administrator
 * @time 2017/8/11 13:35
 * @des 类作用：
 */

public class UserAdapter extends DefaultAdapter<User> {
    public UserAdapter(List<User> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<User> getHolder(View v, int viewType) {
        return new UserItemHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.recycle_list;
    }
}
