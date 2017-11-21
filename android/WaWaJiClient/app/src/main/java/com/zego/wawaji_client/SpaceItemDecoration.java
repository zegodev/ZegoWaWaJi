package com.zego.wawaji_client;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Mark on 2016/3/15
 *
 * Des: 列表的分隔线.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildPosition(view) != 0)
            outRect.top = 0;
            outRect.bottom = space * 8;
            outRect.left = 0;
            outRect.right = 0;

//        if (parent.getChildLayoutPosition(view) == 0 || parent.getChildLayoutPosition(view) == 1){
//            outRect.top = 0;
//        }

        if (parent.getChildAdapterPosition(view) % 2 == 0){
            outRect.left = space * 30;
            outRect.right = space * 15;
        }

        if (parent.getChildAdapterPosition(view) % 2 == 1){
            outRect.left = space * 15;
            outRect.right = space * 30;
        }
    }
}
