package com.sunmi.template.ui.view.recyclerview;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by bps .
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space = 0;
    private boolean isBottom;

    public SpaceItemDecoration() {
    }

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    public SpaceItemDecoration(int space, boolean isBottom) {
        this.space = space;
        this.isBottom = isBottom;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (isBottom) {
            outRect.bottom = space;
        } else {
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.left = space;
            }
        }
    }
}
