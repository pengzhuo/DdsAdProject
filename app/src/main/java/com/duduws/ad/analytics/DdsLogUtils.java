package com.duduws.ad.analytics;

import android.content.Context;

import com.duduws.ad.common.ConfigDefine;
import com.duduws.ad.common.MacroDefine;
import com.duduws.ad.log.MLog;
import com.duduws.ad.model.LogModel;
import com.duduws.ad.utils.AdsPreferences;
import com.duduws.ad.utils.FuncUtils;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * 自定义日志打点
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/3/1 19:52
 */

public class DdsLogUtils {
    private static final String TAG = "DdsLogUtils";
    private static DdsLogUtils instance = null;
    private JSONArray jsonArray = null;
    private Context mContext = null;

    private DdsLogUtils(Context context){
        String localStr = AdsPreferences.getInstance(context).getString(MacroDefine.MACRO_LOG_FLAG, "");
        if (!localStr.equals("")){
            try {
                jsonArray = new JSONArray(localStr);
            } catch (JSONException e) {
                MLog.e(TAG, e.getMessage());
            }
        } else {
            jsonArray = new JSONArray();
        }
        mContext = context;
    }

    synchronized public static DdsLogUtils getInstance(Context context){
        if (instance == null){
            instance = new DdsLogUtils(context);
        }
        return instance;
    }

    /**
     * 打点
     * @param site
     * @param adType
     * @param resultType
     * @param type
     */
    synchronized public void log(String site, int adType, int resultType, int type){
        if (jsonArray != null){
            String cid = ConfigDefine.APP_CHANNEL_NO;
            String imei = FuncUtils.getIMEI(mContext);
            LogModel logModel = new LogModel(cid, site, adType, resultType, type, imei);
            jsonArray.put(logModel.toJsonObj());
            AdsPreferences.getInstance(mContext).setString(MacroDefine.MACRO_LOG_FLAG, jsonArray.toString());
        } else {
            MLog.w(TAG, "log jsonArray is null !");
        }
    }

    /**
     * 获取本地日志
     * @return
     */
    synchronized public String getLog(){
        String ret = null;
        if (jsonArray != null && jsonArray.length() > 0){
            ret = jsonArray.toString();
            jsonArray = new JSONArray();
            AdsPreferences.getInstance(mContext).setString(MacroDefine.MACRO_LOG_FLAG, jsonArray.toString());
        } else {
            MLog.w(TAG, "getLog jsonArray is null !");
        }
        return ret;
    }
}
