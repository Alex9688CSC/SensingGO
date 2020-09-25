package edu.nctu.wirelab.sensinggo;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class MyLinearLayoutManager extends LinearLayoutManager {
    private static final String TAG  = MyLinearLayoutManager.class.getSimpleName();

    private boolean isScrollEnabled = true;

    public MyLinearLayoutManager(Context context, boolean isScrollEnabled) {
        super(context);
        this.isScrollEnabled = isScrollEnabled;
    }

    public MyLinearLayoutManager(Context context,int orientation,boolean reverseLayout){
        super(context, orientation, reverseLayout);
    }

    @Override
    public boolean canScrollVertically() {
        //設定是否禁止滑動
        return isScrollEnabled && super.canScrollVertically();
    }
}
