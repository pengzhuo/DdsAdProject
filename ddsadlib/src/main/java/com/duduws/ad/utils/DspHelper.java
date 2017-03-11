package com.duduws.ad.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.duduws.ad.common.ConstDefine;
import com.duduws.ad.common.MacroDefine;
import com.duduws.ad.log.MLog;
import com.duduws.ad.model.SiteModel;
import com.duduws.ad.view.SelfActivity;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/2/23 19:25
 */

public class DspHelper {
    private static final String TAG = "DspHelper";
    private static boolean CURRENT_ADS_SHOW_FLAG_EX = false;

    /**
     * 设置当前广告展示标志
     * @param context
     * @param flag
     */
    public static void setCurrentAdsShowFlag(Context context, boolean flag){
        CURRENT_ADS_SHOW_FLAG_EX = flag;
    }

    /**
     * 获取当前广告展示标志
     * @param context
     * @return
     */
    public static boolean getCurrentAdsShowFlag(Context context){
        return CURRENT_ADS_SHOW_FLAG_EX;
    }

    /**
     * 获取偏移
     * @param triggerType
     * @return
     */
    public static int getTriggerOffSet(int triggerType){
        int offset = 0;
        switch (triggerType){
            case ConstDefine.TRIGGER_TYPE_APP_ENTER:
                offset = ConstDefine.OFFSET_TRIGGER_VALUE_APPENTER;
                break;
            case ConstDefine.TRIGGER_TYPE_APP_EXIT:
                offset = ConstDefine.OFFSET_TRIGGER_VALUE_APPEXIT;
                break;
            case ConstDefine.TRIGGER_TYPE_NETWORK:
                offset = ConstDefine.OFFSET_TRIGGER_VALUE_NETWORK;
                break;
            case ConstDefine.TRIGGER_TYPE_UNLOCK:
                offset = ConstDefine.OFFSET_TRIGGER_VALUE_UNLOCK;
                break;
        }
        return offset;
    }

    /**
     * 保存广告信息到本地
     * @param context
     * @param channel
     * @param siteModel
     */
    public static void setSiteInfoWithChannel(Context context, int channel, final SiteModel siteModel){
        AdsPreferences.getInstance(context).setString(channel, MacroDefine.MACRO_SITE_INFO, siteModel.toJson());
    }

    /**
     * 从本地拉取广告信息
     * @param context
     * @param channel
     * @return
     */
    public static SiteModel getSiteInfoWithChannel(Context context, int channel){
        String str = AdsPreferences.getInstance(context).getString(channel, MacroDefine.MACRO_SITE_INFO, "");
        if (!TextUtils.isEmpty(str)){
            SiteModel siteModel = new SiteModel();
            siteModel.initWithJson(str);
        } else {
            MLog.w(TAG, "getSiteInfoWithChannel not found channel[" + channel + "] !");
        }
        return null;
    }

    /**
     * 更新请求数据
     * @param context
     */
    public static void updateRequestData(Context context, int channel) {
        //更新全局次数
//        setDspSpotRequestNum(context, ConstDefine.DSP_GLOABL, getDspSpotRequestNum(context, ConstDefine.DSP_GLOABL)+1);
        //更新单个SITE次数
//        setDspSpotRequestNum(context, channel, getDspSpotRequestNum(context, channel)+1);
    }

    /**
     * 更新展示数据
     * @param context
     */
    public static void updateShowData(Context context, int channel) {
        //更新全局次数
//        setDspSpotShowNum(context, ConstDefine.DSP_GLOABL, getDspSpotShowNum(context, ConstDefine.DSP_GLOABL)+1);
        //更新单个SITE次数
//        setDspSpotShowNum(context, channel, getDspSpotShowNum(context, channel)+1);
    }

    /**
     * 展示广告
     * @param context
     * @param channel
     * @param triggerType
     */
    public static void showAds(Context context, int channel, int triggerType)  {
        openActivity(context, channel, triggerType);
    }

    /**
     * 打开一个Activity
     * @param context
     * @param channel
     * @param triggerType
     */
    private static synchronized void openActivity(Context context, int channel, int triggerType){
        if (getCurrentAdsShowFlag(context)){
            MLog.e(TAG, "openActivity already open ads! channel: " + channel + ", triggerType: " + triggerType);
            return;
        }
        Intent mIntent = new Intent();
        mIntent.putExtra("channel", channel);
        mIntent.putExtra("triggerType", triggerType);
        mIntent.setClass(context.getApplicationContext(), SelfActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
    }
}
