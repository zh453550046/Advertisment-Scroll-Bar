package com.example.zhouwn.advertisment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 滚动点View
 * Created by zhouwn on 2015/7/8.
 */
public class AutoScrollView extends View {

    private Paint paint;

    /**
     * 当滚动点位为图片缓存需要画的bitmap
     */
    private Bitmap bitmap;

    /**
     * size 滚动点个数
     * index 索引
     */
    private int size, selectColor = -1, unSelectColor = -1, selectImage = -1, unSelectImage = -1, index;

    /**
     * 是否设置滚动点的图片
     */
    private boolean isSetColor;

    /**
     * radius 设置滚动点为圆点时默认的半径
     * with 最小滚动图片滚动点的宽度
     * height 最小滚动图片的高度
     * 设置滚动点为图片时滚动到的图片和未滚动到的图片大小可能不同
     * widthSelect 滚动到的图片的宽度
     * widthUnselect 未滚动到导入图片的宽度
     * start 滚动到的图片的宽度和滚动到的图片的宽度差值的一半,记作起始偏移量
     */
    private float radius = 10f, width, height, widthSelect, widthUnselect, start;

    public AutoScrollView(Context context) {
        super(context);
        init();
    }

    public AutoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    public AutoScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int w, h;
        if (widthMode == MeasureSpec.EXACTLY) {
            w = widthSize;
        } else {
            //设置宽度
            if (isSetColor) {
                w = (int) (2 * width * size - width) + 10;
            } else {
                w = (int) (widthSelect > widthUnselect ? 2 * width * (size - 1) + width + 10 + 2 * start : 2 * width * (size - 1) + widthUnselect + 10 + 2 * start);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            h = heightSize;
        } else {
            //设置高度
            if (isSetColor) {
                h = (int) height;
            } else {
                h = (int) (2 * height);
            }

        }
        setMeasuredDimension(w, h);
    }

    /**
     * 初始化画笔
     */
    private void init() {
        paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 1; i <= size; i++) {
            if ((i - 1) != index) {
                if (isSetColor) {
                    if (unSelectColor != -1) {
                        paint.setColor(unSelectColor);
                    } else {
                        paint.setColor(Color.GRAY);
                    }
                } else {
                    bitmap = BitmapFactory.decodeResource(getResources(), unSelectImage);
                }
            } else {
                if (isSetColor) {
                    if (selectColor != -1) {
                        paint.setColor(selectColor);
                    } else {
                        paint.setColor(Color.BLACK);
                    }
                } else {
                    bitmap = BitmapFactory.decodeResource(getResources(), selectImage);
                }
            }
            if (isSetColor) {
                //画点
                canvas.drawCircle((i - 1) * 2 * width + width / 2 + 5, height / 2, radius, paint);
            } else {
                //画图片
                canvas.drawBitmap(bitmap, start + 5 + (i - 1) * 2 * width - bitmap.getWidth() / 2 + width / 2, (2 * height - bitmap.getHeight()) / 2, paint);
            }
        }
    }

    /**
     * 设置滚动点为圆点
     *
     * @param selectColor
     * @param unSelectColor
     */
    public void setColor(int selectColor, int unSelectColor) {
        this.selectColor = selectColor;
        this.unSelectColor = unSelectColor;
        isSetColor = true;
        width = 2 * radius;
        height = 2 * radius;
    }

    /**
     * 设置滚动点为资源图片
     *
     * @param selectImage
     * @param unSelectImage
     */
    public void setImageResource(int selectImage, int unSelectImage) {
        this.selectImage = selectImage;
        this.unSelectImage = unSelectImage;
        isSetColor = false;
        Bitmap unSelect = BitmapFactory.decodeResource(getResources(), unSelectImage);
        Bitmap select = BitmapFactory.decodeResource(getResources(), unSelectImage);
        widthSelect = select.getWidth();
        widthUnselect = unSelect.getWidth();
        start = widthSelect > widthUnselect ? widthSelect - widthUnselect : widthUnselect - widthSelect;
        if (start == 0) {
            start = widthSelect / 2;
        }
        width = widthSelect > widthUnselect ? widthUnselect : widthSelect;
        height = select.getHeight() > unSelect.getHeight() ? unSelect.getHeight() : select.getHeight();
        unSelect.recycle();
        select.recycle();
        isSetColor = false;
    }

    /**
     * 设置滚动点的间隔宽度,在画图时间隔宽度由width控制
     *
     * @param width
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * 设置滚动点的间隔高度,在画图时间隔宽度由height控制
     *
     * @param height
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * 设置滚动点个数
     *
     * @param size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * 设置索引,用于同步viewpager滚动到的位置
     *
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
        invalidate();
    }

    /**
     * 设置索引,用于同步viewpager滚动到的位置
     * 非UI线程调用
     *
     * @param index
     */
    public void setIndexNotUIThread(int index) {
        this.index = index;
        postInvalidate();
    }

    /**
     * 回收用于缓冲的bitmap
     */
    public void recycle() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}
