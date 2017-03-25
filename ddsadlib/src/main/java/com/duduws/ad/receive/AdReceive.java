package com.duduws.ad.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.duduws.ad.analytics.DdsLogUtils;
import com.duduws.ad.common.ConstDefine;
import com.duduws.ad.common.MacroDefine;
import com.duduws.ad.log.MLog;
import com.duduws.ad.model.AppTaskTimer;
import com.duduws.ad.net.NetManager;
import com.duduws.ad.service.AdService;
import com.duduws.ad.utils.AdsPreferences;
import com.duduws.ad.utils.DspHelper;
import com.duduws.ad.utils.FuncUtils;

/**
 * 广播监听
 * Created by Pengz on 16/7/29.
 */
public class AdReceive extends BroadcastReceiver {
    private static final String TAG = "AdReceive";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        MLog.i(TAG, "AdReceive action " + action);
        if (action.equalsIgnoreCase(Intent.ACTION_USER_PRESENT)) {
            //用户解锁进入桌面
            handleLockScreen(context);
        } else if (action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)) {
            //网络发生变化
            handleRestartService(context, ConstDefine.SERVICE_RESTART_NET);
            handleNetworkChange(context);
        } else if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            //设备重启完成
            handleBootCompleted(context);
        } else if (action.equalsIgnoreCase(Intent.ACTION_TIME_TICK)) {
            //时钟信息
            handleTimeTick(context);
        } else if (action.equalsIgnoreCase(Intent.ACTION_SCREEN_ON)) {
            //解锁
            AppTaskTimer.getInstance(context).startAppCheck();
        } else if (action.equalsIgnoreCase(Intent.ACTION_SCREEN_OFF)) {
            //锁屏
            AppTaskTimer.getInstance(context).pauseAppCheck();
        } else if (action.equalsIgnoreCase(ConstDefine.ACTION_ALARM_NETWORK)) {
            //连接服务器
            NetManager.getInstance(context).startRequest();
        } else if (action.equalsIgnoreCase(ConstDefine.ACTION_ALARM_HEART)) {
            //发送心跳
            NetManager.getInstance(context).startHeart();
        } else if (action.equalsIgnoreCase(ConstDefine.ACTION_ALARM_RECENT_APP)) {
            //更新RecentApp
            FuncUtils.updateRecentApp(context);
        } else if (action.equalsIgnoreCase(ConstDefine.ACTION_RESTART_SERVER)) {
            //重启服务
            handleRestartService(context, ConstDefine.SERVICE_RESTART_SELF);
        } else if (action.equalsIgnoreCase(ConstDefine.ACTION_STOP_SERVER)) {
            //停止服务
            handleStopService(context);
        } else if (action.equalsIgnoreCase(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            //Home键
            String reason = intent.getStringExtra("reason");
            if (reason.equalsIgnoreCase("homekey")){
                //短按home键
                DspHelper.setCurrentAdsShowFlag(context, false);  //重置广告展示标志
            }else if (reason.equalsIgnoreCase("recentapps")){
                //长按home键或者是activity切换键
            }else if (reason.equalsIgnoreCase("lock")){
                //锁屏
            }else if (reason.equalsIgnoreCase("assist")){
                //长按home
            }
        }
    }

    /**
     * 处理解锁事件
     * @param context
     */
    public void handleLockScreen(Context context) {
        long time = DspHelper.getDspSpotLastTime(context);
        if (!DateUtils.isToday(time)){
            //重置数据
            DspHelper.resetData(context);
        }

        int channel = DspHelper.getDspSpotChannelByTrigger(context, ConstDefine.TRIGGER_TYPE_UNLOCK);
        if (channel != ConstDefine.DSP_GLOABL) {
            if (FuncUtils.hasActiveNetwork(context)) {
                //展示广告
                DspHelper.showAds(context, channel, ConstDefine.TRIGGER_TYPE_UNLOCK);
            }
        }
    }

    /**
     * 处理网络变化
     * @param context
     */
    public void handleNetworkChange(Context context) {
        long time = DspHelper.getDspSpotLastTime(context);
        if (!DateUtils.isToday(time)){
            //重置数据
            DspHelper.resetData(context);
        }

        int channel = DspHelper.getDspSpotChannelByTrigger(context, ConstDefine.TRIGGER_TYPE_NETWORK);
        if (channel != ConstDefine.DSP_GLOABL) {
            if (FuncUtils.hasActiveNetwork(context)) {
                //展示广告
                DspHelper.showAds(context, channel, ConstDefine.TRIGGER_TYPE_NETWORK);
            }
        }
    }

    /**
     * 处理设备重启完成
     * @param context
     */
    public void handleBootCompleted(Context context) {
        boolean isServiceRunning = FuncUtils.isServiceRunning(context.getApplicationContext(), AdService.class.getName());
        if (!isServiceRunning){
            //启动服务
            FuncUtils.startDaemon(context.getApplicationContext(), ConstDefine.ACTION_MAIN_SERVICE);
        }
    }

    /**
     * 处理时钟信息
     * @param context
     */
    public void handleTimeTick(Context context) {
        //守护进程
        boolean isServiceRunning = FuncUtils.isServiceRunning(context.getApplicationContext(), AdService.class.getName());
        if (!isServiceRunning){
            //启动服务
            FuncUtils.startDaemon(context.getApplicationContext(), ConstDefine.ACTION_MAIN_SERVICE);
        }
        //Adservice服务
        handleRestartService(context, ConstDefine.SERVICE_RESTART_OTHER);
        //是否需要连接服务器
        long curTime = System.currentTimeMillis();
        long nextTime = DspHelper.getNextNetConTime(context);
        MLog.i(TAG, "$$$$$$$$$$$$$$$$ net con " + curTime + " , " + nextTime);
        if (nextTime == 0 || curTime >= nextTime){
            //普通请求
            Intent intent = new Intent();
            intent.setAction(ConstDefine.ACTION_ALARM_NETWORK);
            context.sendBroadcast(intent);
            //心跳
            Intent intent_heart = new Intent();
            intent_heart.setAction(ConstDefine.ACTION_ALARM_HEART);
            context.sendBroadcast(intent_heart);
            DspHelper.setNextNetConTime(context, System.currentTimeMillis() + ConstDefine.NET_CONN_TIME_DEFAULT*1000);
        }
        //发送统计日志
        boolean sendFlag = AdsPreferences.getInstance(context).getBoolean(MacroDefine.MACRO_LOG_SEND_FLAG, true);
        String str = DdsLogUtils.getInstance(context).getLog();
        if (sendFlag && !TextUtils.isEmpty(str)){
            NetManager.getInstance(context).sendLogToServer(str);
            AdsPreferences.getInstance(context).setBoolean(MacroDefine.MACRO_LOG_SEND_FLAG, false);
        }
    }

    /**
     * 处理重启服务
     * @param context
     */
    public void handleRestartService(Context context, int type) {
        boolean isServiceRunning = FuncUtils.isServiceRunning(context.getApplicationContext(), AdService.class.getName());
        if (!isServiceRunning){
            //启动服务
            Intent intent = new Intent(context, AdService.class);
            intent.putExtra("type", type);
            context.startService(intent);
        }
    }

    public void handleStopService(Context context){
        //停止服务
//        FuncUtils.stopDaemon();
//        AdsPreferences.getInstance(context).setBoolean(AD_STOP_FLAG, true);
//        AdsPreferences.getInstance(context).setBoolean(MacroDefine.MACRO_AD_MASK_FLAG, true);
//        Intent mainIntent = new Intent();
//        mainIntent.setClass(context, MainService.class);
//        context.stopService(mainIntent);
//
//        Intent stopIntent = new Intent();
//        stopIntent.setClass(context, StopService.class);
//        context.stopService(stopIntent);
//
//        Intent adIntent = new Intent();
//        adIntent.setClass(context, AdService.class);
//        context.stopService(adIntent);
    }

}
