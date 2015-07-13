package com.example.zhouwn.advertisment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 最外部调用的view,包含两个子view
 * Created by zhouwn on 2015/7/7.
 */
public class MyAdvertisment extends RelativeLayout {


    /**
     * 滚动显示广告图片
     */
    private ViewPager vp;

    private MyPagerAdapter adapter;

    /**
     * 最下面的滚动点view
     */
    private AutoScrollView autoScrollView;

    /**
     * size记录广告图片数量
     * index当前播放到第几条广告
     */
    private int size, index = 0;

    private Timer timer;

    private static final String TAG = MyAdvertisment.class.getSimpleName();

    public MyAdvertisment(Context context) {
        super(context);
        init(context);
    }

    public MyAdvertisment(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context);
    }

    public MyAdvertisment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化位置等
     *
     * @param context
     */
    private void init(Context context) {
        vp = new ViewPager(context);
        vp.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        adapter = new MyPagerAdapter();
        vp.setAdapter(adapter);
        vp.addOnPageChangeListener(new MyOnPageChangListener());
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            FixedSpeedScroller mScroller = new FixedSpeedScroller(getContext(), new AccelerateInterpolator());
            mScroller.setmDuration(400); // 设置vp切换的时间
            mField.set(vp, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
        autoScrollView = new AutoScrollView(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.bottomMargin = 10;
        autoScrollView.setLayoutParams(layoutParams);
        addView(vp);
        addView(autoScrollView);
    }

    /**
     * 外部调用设置vp资源文件图片的方法
     *
     * @param context
     * @param list
     */
    public void setListResource(Context context, List<Integer> list) {
        adapter.setListResource(context, list);
        adapter.notifyDataSetChanged();
        this.size = list.size();
        autoScrollView.setSize(size);
    }

    /**
     * 外部调用设置vp装填Bitmap图片的方法
     *
     * @param context
     * @param list
     */
    public void setListBitmap(Context context, List<Bitmap> list) {
        adapter.setListBitmap(context, list);
        adapter.notifyDataSetChanged();
        this.size = list.size();
        autoScrollView.setSize(size);
    }

    /**
     * 设置滚动点为带颜色的原点
     *
     * @param selectColor
     * @param unSelectColor
     */
    public void setColor(int selectColor, int unSelectColor) {
        autoScrollView.setColor(selectColor, unSelectColor);
    }

    /**
     * 设置滚动点位资源图片
     *
     * @param selectImage
     * @param unSelectImage
     */
    public void setImageResource(int selectImage, int unSelectImage) {
        autoScrollView.setImageResource(selectImage, unSelectImage);
    }

    /**
     * 设置滚动点的间隔宽度,必须要在设置滚动点之后调用
     *
     * @param width
     */
    public void setScrollWidth(float width) {
        autoScrollView.setWidth(width);
    }

    /**
     * 开启自动滚动
     *
     * @param activity 调用的Activity实例
     * @param Delay    开启延迟
     * @param period   滚动播放间隔
     */
    public void startAutoScroll(final Activity activity, long Delay, long period) {
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                index++;
                if (index >= size) {
                    index = 0;
                }
                autoScrollView.setIndexNotUIThread(index);
                //在UI线程中执行
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vp.setCurrentItem(index);
                    }
                });
            }
        }, Delay, period);
    }

    /**
     * 回收autoScrollView中的bitmap
     */
    public void bitmapRecycle() {
        if (autoScrollView != null) {
            autoScrollView.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 装填广告图片的viewpager适配器
     */
    class MyPagerAdapter extends PagerAdapter {

        private List<ImageView> list = new ArrayList<ImageView>();

        void setListResource(Context context, List<Integer> list) {
            this.list.clear();
            for (int i = 0; i < list.size(); i++) {
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(list.get(i));
                this.list.add(imageView);
            }
        }

        void setListBitmap(Context context, List<Bitmap> list) {
            this.list.clear();
            for (int i = 0; i < list.size(); i++) {
                ImageView imageView = new ImageView(context);
                imageView.setImageBitmap(list.get(i));
                this.list.add(imageView);
            }
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }
    }

    /**
     * 监听vp滚动的事件,同步vp和autoScrollView
     */
    class MyOnPageChangListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int i, float v, int i2) {
        }

        @Override
        public void onPageSelected(int i) {
            index = i;
            autoScrollView.setIndex(index);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }

}
