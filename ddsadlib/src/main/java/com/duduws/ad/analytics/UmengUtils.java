package com.duduws.ad.analytics;

import android.content.Context;
import android.text.TextUtils;

import com.duduws.ad.log.MLog;
import com.duduws.ad.utils.FuncUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * 友盟打点统计
 * Created by Pengz on 16/7/20.
 */
public class UmengUtils {
    private static final String TAG = "UmengUtils";

    public static void init(Context context){
        MobclickAgent.setDebugMode(true);
    }

    public static void onEvent(Context context, String eventId, Map<String, String> map) {
        if (context == null || TextUtils.isEmpty(eventId)) {
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
                MobclickAgent.onEvent(context, eventId, params);
            } else {
                MobclickAgent.onEvent(context, eventId);
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
