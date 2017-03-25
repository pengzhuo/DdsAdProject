package com.dd.ad;

import android.app.Application;

import com.duduws.ad.main.DdsManager;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/3/25 10:33
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DdsManager.init(this, true);
    }
}
