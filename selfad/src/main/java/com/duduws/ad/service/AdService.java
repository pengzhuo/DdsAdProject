package com.duduws.ad.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.duduws.ad.common.ConfigDefine;
import com.duduws.ad.common.ConstDefine;
import com.duduws.ad.log.MLog;
import com.duduws.ad.net.NetManager;
import com.duduws.ad.receive.AdReceive;
import com.duduws.ad.utils.DspHelper;
import com.duduws.ad.utils.FuncUtils;

/**
 * Created by Pengz on 16/7/20.
 */
public class AdService extends Service {
    private static final String TAG = "AdService";

    @Override
    public void onCreate() {
        super.onCreate();

        FuncUtils.startDaemon(getApplicationContext(), ConstDefine.ACTION_MAIN_SERVICE);

        AdReceive adReceive = new AdReceive();
        //监听时钟信息
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(adReceive, intentFilter);

        //连接服务器
        long curTime = System.currentTimeMillis();
        long nextTime = DspHelper.getNextNetConTime(this);
        MLog.i(TAG, "AdService net con " + curTime + " , " + nextTime);
        if (nextTime == 0 || curTime >= nextTime){
            NetManager.getInstance(getApplicationContext()).startRequest();
            NetManager.getInstance(getApplicationContext()).startHeart();
            DspHelper.setNextNetConTime(this, System.currentTimeMillis() + ConfigDefine.DEFAULT_CONN_NET_TIME*1000);
        }

        //启动APP打开和关闭的监听
//        AppTaskTimer.getInstance(getApplicationContext()).startAppCheck();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //发送重启服务的广播
        Intent intent = new Intent();
        intent.setAction(ConstDefine.ACTION_RESTART_SERVER);
        sendBroadcast(intent);
        //销毁APP检测
//        AppTaskTimer.getInstance(this).destroyAppCheck();
        super.onDestroy();
    }
}
