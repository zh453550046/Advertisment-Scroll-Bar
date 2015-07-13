package com.example.zhouwn.advertisment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity {

    MyAdvertisment advertisment;
    List<Integer> list;
    private static final Integer[] AD = {
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<Integer>();
        list.addAll(Arrays.asList(AD));
        advertisment = (MyAdvertisment) findViewById(R.id.ad);

        advertisment.setListResource(this, list);//广告的图片资源
//        advertisment.setListBitmap();可以传bitmap
        advertisment.setColor(Color.BLACK, Color.GRAY);//如果滚动点为带颜色的点
//        advertisment.setImageResource(R.mipmap.dayuan, R.mipmap.xiaoyuan);//设置滚动点为图片
//        advertisment.setScrollWidth(30f);可以手动设置滚动点的间隔宽度,必须在设置完滚动点类型后调用才有用
        advertisment.startAutoScroll(this, 2000, 3000);//打开自动滚动,延迟和滚动间隔3
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        advertisment.bitmapRecycle();
    }
}
