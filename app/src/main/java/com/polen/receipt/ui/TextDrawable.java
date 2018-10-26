package com.polen.receipt.ui;



import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TextDrawable extends ShapeDrawable {
    private static final Typeface LIGHT = Typeface.create("sans-serif-light", Typeface.NORMAL);

    public static final int DRAWABLE_SHAPE_NONE = -1;

    public static final int DRAWABLE_SHAPE_RECT = 0;

    public static final int DRAWABLE_SHAPE_ROUND_RECT = 1;

    public static final int DRAWABLE_SHAPE_OVAL = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DRAWABLE_SHAPE_NONE, DRAWABLE_SHAPE_RECT, DRAWABLE_SHAPE_ROUND_RECT, DRAWABLE_SHAPE_OVAL})
    public @interface DrawableShape {
    }

    private static final float SHADE_FACTOR = 0.9f;

    private Paint mTextPaint;

    private Paint mBorderPaint;

    @Nullable
    private String mText;

    private int mColor;

    @DrawableShape
    private int mShape;

    private int mHeight;

    private int mWidth;

    private int mFontSize;

    private float mCornerRadius;

    private int mBorderThickness;

    @Nullable
    private Bitmap mBitmap;

    private TextDrawable(Builder builder) {
        super(builder.getShapeDrawable());

        // shape properties
        mShape = builder.shape;
        mHeight = builder.height;
        mWidth = builder.width;
        mCornerRadius = builder.mCornerRadius;

        // text and color
        mText = builder.text;
        mColor = builder.color;

        // text paint settings
        mFontSize = builder.fontSize;
        mTextPaint = new Paint();
        mTextPaint.setColor(builder.textColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTypeface(builder.tf);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStrokeWidth(builder.borderThickness);

        // border paint settings
        mBorderThickness = builder.borderThickness;
        mBorderPaint = new Paint();
        mBorderPaint.setColor(getDarkerShade(mColor));
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderThickness);

        // drawable paint color
        Paint paint = getPaint();
        paint.setColor(mColor);

        mBitmap = builder.bitmap;
    }

    private int getDarkerShade(int color) {
        return Color.rgb((int) (SHADE_FACTOR * Color.red(color)),
                (int) (SHADE_FACTOR * Color.green(color)),
                (int) (SHADE_FACTOR * Color.blue(color)));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect r = getBounds();


        // draw border
        if (mBorderThickness > 0) {
            drawBorder(canvas);
        }

        int count = canvas.save();
        if (mBitmap == null) {
            canvas.translate(r.left, r.top);
        }

        // draw text
        int width = this.mWidth < 0 ? r.width() : this.mWidth;
        int height = this.mHeight < 0 ? r.height() : this.mHeight;
        int fontSize = this.mFontSize < 0 ? (Math.min(width, height) / 2) : this.mFontSize;

        if (mBitmap == null) {
            mTextPaint.setTextSize(fontSize);
            canvas.drawText(mText, width / 2, height / 2 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2), mTextPaint);
        } else {
            canvas.drawBitmap(mBitmap, (width - mBitmap.getWidth()) / 2, (height - mBitmap.getHeight()) / 2, null);
        }

        canvas.restoreToCount(count);
    }

    private void drawBorder(Canvas canvas) {
        RectF rect = new RectF(getBounds());
        rect.inset(mBorderThickness / 2, mBorderThickness / 2);

        switch (mShape) {
            case DRAWABLE_SHAPE_ROUND_RECT:
                canvas.drawRoundRect(rect, mCornerRadius, mCornerRadius, mBorderPaint);
                break;

            case DRAWABLE_SHAPE_OVAL:
                canvas.drawOval(rect, mBorderPaint);
                break;

            case DRAWABLE_SHAPE_NONE:
            case DRAWABLE_SHAPE_RECT:
            default:
                canvas.drawRect(rect, mBorderPaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mTextPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mTextPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mHeight;
    }

    /**
     * Converts the {@link TextDrawable} to a {@link Bitmap}
     *
     * @return
     */
    public Bitmap toBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getIntrinsicWidth(), getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        draw(canvas);
        return bitmap;
    }

    /**
     * Builder class for generating a {@link TextDrawable}
     */
    public static class Builder {
        String text = null;

        int color = Color.GRAY;

        int borderThickness = 0;

        int width = -1;

        int height = -1;

        Typeface tf = LIGHT;

        @DrawableShape
        int shape = DRAWABLE_SHAPE_NONE;

        int textColor = Color.WHITE;

        int fontSize = -1;

        float mCornerRadius;

        Bitmap bitmap;

        public Builder() {
        }

        /**
         * Sets the width of the drawable
         *
         * @param width
         * @return
         */
        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        /**
         * Sets the height of the drawable
         *
         * @param height
         * @return
         */
        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        /**
         * Sets the text color to be used for the drawable. Will be ignored if {@link #setIcon(Bitmap)} is called
         *
         * @param color
         * @return
         */
        public Builder setTextColor(@ColorInt int color) {
            this.textColor = color;
            return this;
        }

        /**
         * Sets the border thickness for the drawable. Defaults to 0
         *
         * @param thickness
         * @return
         */
        public Builder setBorderThickness(int thickness) {
            this.borderThickness = thickness;
            return this;
        }

        /**
         * Sets the typeface for the drawable
         *
         * @param tf
         * @return
         */
        public Builder setTypeface(Typeface tf) {
            this.tf = tf;
            return this;
        }

        /**
         * Sets the text size of the drawable. Will be ignored if {@link #setIcon(Bitmap)} is called
         *
         * @param size
         * @return
         */
        public Builder setTextSize(int size) {
            this.fontSize = size;
            return this;
        }

        /**
         * Sets the {@link com.polen.receipt.ui.TextDrawable.DrawableShape} to be used for the drawable
         *
         * @param shape
         * @return
         */
        public Builder setShape(@DrawableShape int shape) {
            this.shape = shape;
            return this;
        }

        /**
         * Sets the corner radius for the drawable. Will be ignored unless the {@link com.polen.receipt.ui.TextDrawable.DrawableShape} is
         * SHAPE_ROUND_RECT
         *
         * @param cornerRadius
         * @return
         */
        public Builder setCornerRadius(int cornerRadius) {
            this.mCornerRadius = cornerRadius;
            return this;
        }

        /**
         * Sets the icon to be used for the drawable
         *
         * @param bitmap
         * @return
         */
        public Builder setIcon(Bitmap bitmap) {
            this.text = null;
            this.bitmap = bitmap;
            return this;
        }

        /**
         * Sets the text to be used for the drawable.
         *
         * @param text
         * @return
         */
        public Builder setText(String text) {
            this.bitmap = null;
            this.text = text;
            return this;
        }

        /**
         * Sets the color of the drawable
         *
         * @param color
         * @return
         */
        public Builder setColor(@ColorInt int color) {
            this.color = color;
            return this;
        }

        /**
         * Returns the {@link TextDrawable}
         *
         * @return
         */
        public TextDrawable build() {
            return new TextDrawable(this);
        }

        /**
         * Returns the {@link ShapeDrawable} used for the {@link TextDrawable}
         *
         * @return
         */
        private Shape getShapeDrawable() {
            switch (shape) {
                case DRAWABLE_SHAPE_ROUND_RECT:
                    float[] radii = {mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius};
                    return new RoundRectShape(radii, null, null);

                case DRAWABLE_SHAPE_OVAL:
                    return new OvalShape();

                case DRAWABLE_SHAPE_NONE:
                case DRAWABLE_SHAPE_RECT:
                default:
                    return new RectShape();
            }
        }
    }
}