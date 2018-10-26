package com.polen.receipt.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View; 
public class GridItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpace;

    private int mNumColumns;

    public GridItemDecoration(int spacing, int numColumns) {
        mSpace = spacing;
        mNumColumns = numColumns;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getLayoutManager().getPosition(view);
        int column = position % mNumColumns;
        outRect.left = column * mSpace / mNumColumns;
        outRect.right = mSpace - (column + 1) * mSpace / mNumColumns;
        outRect.top = mSpace;
    }
}