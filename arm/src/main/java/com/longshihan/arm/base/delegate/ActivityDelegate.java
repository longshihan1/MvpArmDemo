package com.longshihan.arm.base.delegate;

import android.os.Bundle;
import android.os.Parcelable;

/**
 * @author Administrator
 * @time 2017/8/10 17:03
 * @des 类作用：
 */

public interface ActivityDelegate extends Parcelable {
    String LAYOUT_LINEARLAYOUT = "LinearLayout";
    String LAYOUT_FRAMELAYOUT = "FrameLayout";
    String LAYOUT_RELATIVELAYOUT = "RelativeLayout";
    String ACTIVITY_DELEGATE = "activity_delegate";

    void onCreate(Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onSaveInstanceState(Bundle outState);

    void onDestroy();
}
