package com.duduws.ad.main;

import android.content.Context;
import android.content.Intent;

import com.duduws.ad.log.MLog;
import com.duduws.ad.service.AdService;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/2/27 00:14
 */

public class UMGameAgent {
    private static final String TAG = "UMGameAgent";

    public static void init(Context context){
        Intent intent = new Intent();
        intent.setClass(context, AdService.class);
        context.startService(intent);
        MLog.setLogEnable(true);
        MLog.e(TAG, "#### UMGameAgent start !");
    }
}
