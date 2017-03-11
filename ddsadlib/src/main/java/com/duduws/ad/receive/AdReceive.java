package com.duduws.ad.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.duduws.ad.common.ConstDefine;
import com.duduws.ad.log.MLog;
import com.duduws.ad.model.AppTaskTimer;
import com.duduws.ad.net.NetManager;
import com.duduws.ad.service.AdService;
import com.duduws.ad.utils.DspHelper;
import com.duduws.ad.utils.FuncUtils;
import com.duduws.ad.view.SelfActivity;

/**
 * 广播监听
 * Created by Pengz on 16/7/29.
 */
public class AdReceive extends BroadcastReceiver {
    private static final String TAG = "AdReceive";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        MLog.i(TAG, "AdReceive action " + action);
        if (TextUtils.isEmpty(action)) {
            return;
        }
        if (action.equalsIgnoreCase(Intent.ACTION_USER_PRESENT)) {
            //用户解锁进入桌面
            handleLockScreen(context);
        } else if (action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)) {
            //网络发生变化
            handleNetworkChange(context);
        } else if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            //设备重启完成
            handleBootCompleted(context);
        } else if (action.equalsIgnoreCase(Intent.ACTION_TIME_TICK)) {
            //时钟信息
            handleTimeTick(context);
        } else if (action.equalsIgnoreCase(Intent.ACTION_SCREEN_ON)) {
            //解锁
//            AppTaskTimer.getInstance(context).startAppCheck();
        } else if (action.equalsIgnoreCase(Intent.ACTION_SCREEN_OFF)) {
            //锁屏
//            AppTaskTimer.getInstance(context).pauseAppCheck();
        } else if (action.equalsIgnoreCase(ConstDefine.ACTION_ALARM_NETWORK)) {
            //连接服务器
//            NetManager.getInstance(context).startRequest();
        } else if (action.equalsIgnoreCase(ConstDefine.ACTION_ALARM_HEART)) {
            //发送心跳
//            NetManager.getInstance(context).startHeart();
        } else if (action.equalsIgnoreCase(ConstDefine.ACTION_ALARM_RECENT_APP)) {
            //更新RecentApp
//            FuncUtils.updateRecentApp(context);
        } else if (action.equalsIgnoreCase(ConstDefine.ACTION_RESTART_SERVER)) {
            //重启服务
            handleRestartService(context, ConstDefine.SERVICE_RESTART_SELF);
        } else if (action.equalsIgnoreCase(ConstDefine.ACTION_STOP_SERVER)) {
            //停止服务
//            handleStopService(context);
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
        boolean isServiceRunning = FuncUtils.isServiceRunning(context.getApplicationContext(), AdService.class.getName());
        if (!isServiceRunning){
            //启动服务
            handleRestartService(context, ConstDefine.SERVICE_RESTART_OTHER);
        } else {
            //连接服务器
            Intent intent = new Intent();
            intent.setClass(context, SelfActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 处理网络变化
     * @param context
     */
    public void handleNetworkChange(Context context) {
        boolean isServiceRunning = FuncUtils.isServiceRunning(context.getApplicationContext(), AdService.class.getName());
        if (!isServiceRunning){
            //启动服务
            handleRestartService(context, ConstDefine.SERVICE_RESTART_OTHER);
        } else {
            //连接服务器
            Intent intent = new Intent();
            intent.setClass(context, SelfActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
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
            handleRestartService(context, ConstDefine.SERVICE_RESTART_OTHER);
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
            handleRestartService(context, ConstDefine.SERVICE_RESTART_OTHER);
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
    }

}
