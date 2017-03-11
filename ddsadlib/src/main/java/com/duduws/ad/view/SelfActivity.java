package com.duduws.ad.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.duduws.ad.common.ConfigDefine;
import com.duduws.ad.listener.AdListener;
import com.duduws.ad.log.MLog;
import com.duduws.ad.net.NetManager;
import com.duduws.ad.utils.FuncUtils;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/2/13 15:16
 */

public class SelfActivity extends Activity {
    private static final String TAG = "SelfActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        MLog.e(TAG, "SelfActivity onCreate...");
        if (FuncUtils.hasActiveNetwork(this)){
            ConfigDefine.AD_LISTENER = new AdListener() {
                @Override
                public void onError(int code) {
                    MLog.e(TAG, "DDS ad onError " + code);
                }

                @Override
                public void onLoaded() {
                    MLog.e(TAG, "DDS ad onLoaded ");
                    Intent intent = new Intent();
                    intent.setClass(SelfActivity.this, AdActivity.class);
                    SelfActivity.this.startActivity(intent);
                }

                @Override
                public void onClicked() {
                    MLog.e(TAG, "DDS ad onClicked ");
                }

                @Override
                public void onDisplayed() {
                    MLog.e(TAG, "DDS ad onDisplayed ");
                }

                @Override
                public void onDismissed() {
                    MLog.e(TAG, "DDS ad onDismissed ");
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    NetManager.getInstance(SelfActivity.this).getAdInfoFromServer(1);
                }
            }).start();
        }

        finish();
    }
}
