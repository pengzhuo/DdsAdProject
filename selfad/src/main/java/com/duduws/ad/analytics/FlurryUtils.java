package com.duduws.ad.analytics;

import android.content.Context;
import android.text.TextUtils;

import com.duduws.ad.common.ConfigDefine;
import com.duduws.ad.log.MLog;
import com.duduws.ad.utils.FuncUtils;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/3/13 15:10
 */

public class FlurryUtils {
    private static final String TAG = "FlurryUtils";

    private static boolean initFlag = false;

    public static void init(Context context){
        new FlurryAgent.Builder()
                .withListener(new FlurryAgentListener() {
                    @Override
                    public void onSessionStarted() {
                        initFlag = true;
                        MLog.w(TAG, "Flurry init success!");
                    }
                })
                .withLogEnabled(true)
                .build(context, ConfigDefine.APP_KEY_FLURRY);
    }

    public static void onEvent(Context context, String eventId, Map<String, String> map) {
        if (context == null || TextUtils.isEmpty(eventId) || !initFlag) {
            MLog.w(TAG, "Flurry onEvent fail ! [flag:" + initFlag + ", eventId:" + eventId + "]");
            return;
        }
        try {
            HashMap<String, String> params = getPubParams(context);
            if( params != null  ){
                if( map != null ){
                    params.putAll(map);
                }
            } else if (map != null ){
                params = (HashMap<String, String>) map ;
            }
            if (params != null) {
                FlurryAgent.logEvent(eventId, map);
            } else {
                FlurryAgent.logEvent(eventId);
            }
        } catch (Exception e) {
            MLog.e(TAG, e.toString());
        }
    }

    /**
     * 公共参数:
     * Time - 格式改为：MM-DD
     * @return
     */
    private static HashMap<String, String> getPubParams(Context context){
        HashMap<String, String> pubParamMap = new HashMap<String, String>();
        String time = FuncUtils.getFormattedTime(System.currentTimeMillis());
        pubParamMap.put("TIME", time);
        return pubParamMap;
    }
}
