package com.polen.receipt.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.polen.receipt.R;
import com.polen.receipt.utils.Utils;


public class GalleryNavigator extends View {
    private static final int SPACING = 10;
    private static final int RADIUS = 10;
    private int mSize = 10;
    private int mPosition = 0;
    private static final Paint mOnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);;
    private static final Paint mOffPaint = new Paint(Paint.ANTI_ALIAS_FLAG);;

    public GalleryNavigator(Context context) {
        super(context);
        mOnPaint.setColor(getResources().getColor(R.color.primary));
        mOffPaint.setColor(getResources().getColor(R.color.right_grey));
    }

    public GalleryNavigator(Context c, int size) {
        this(c);
        int nScreenWidth = Utils.getScreenWidth((Activity) c);
        mSize = nScreenWidth / 40;
        //mSize = size;
    }

    public GalleryNavigator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mOnPaint.setColor(getResources().getColor(R.color.primary));
        mOffPaint.setColor(getResources().getColor(R.color.right_grey));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mSize; ++i) {
            if (i == mPosition) {
                canvas.drawCircle(i * (2 * RADIUS + SPACING) + RADIUS, RADIUS, RADIUS, mOnPaint);
            } else {
                canvas.drawCircle(i * (2 * RADIUS + SPACING) + RADIUS, RADIUS, RADIUS, mOffPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mSize * (2 * RADIUS + SPACING) - SPACING, 2 * RADIUS);
    }



//    @Override
//	public boolean isInEditMode() {
//		return false;
//	}

    public void setPosition(int id) {
        mPosition = id;
    }

    public void setSize(int size) {
        mSize = size;
    }

    public void setPaints(int onColor, int offColor) {
        mOnPaint.setColor(onColor);
        mOffPaint.setColor(offColor);
    }

    public void setBlack() {
        setPaints(0xE6000000, 0x66000000);
    }

}
