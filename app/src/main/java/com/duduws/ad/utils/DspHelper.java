package com.duduws.ad.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.duduws.ad.common.ConfigDefine;
import com.duduws.ad.common.ConstDefine;
import com.duduws.ad.common.MacroDefine;
import com.duduws.ad.log.MLog;
import com.duduws.ad.model.ProductModel;
import com.duduws.ad.model.SiteModel;
import com.duduws.ad.view.LoadingActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

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
        if (siteModel != null){
            AdsPreferences.getInstance(context).setString(channel, MacroDefine.MACRO_SITE_INFO, siteModel.toJson());
        } else {
            AdsPreferences.getInstance(context).setString(channel, MacroDefine.MACRO_SITE_INFO, "");
        }
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
            return siteModel;
        } else {
            MLog.w(TAG, "getSiteInfoWithChannel not found channel[" + channel + "] !");
        }
        return null;
    }

    /**
     * 保存产品信息到本地
     * @param context
     * @param productModel
     */
    public static void setProductInfo(Context context, final ProductModel productModel){
        if (productModel != null){
            AdsPreferences.getInstance(context).setString(MacroDefine.MACRO_PRODUCT_INFO, productModel.toJson());
        } else {
            AdsPreferences.getInstance(context).setString(MacroDefine.MACRO_PRODUCT_INFO, "");
        }
    }

    /**
     * 获取本地产品信息
     * @param context
     * @return
     */
    public static ProductModel getProductInfo(Context context){
        String str = AdsPreferences.getInstance(context).getString(MacroDefine.MACRO_PRODUCT_INFO, "");
        if (!TextUtils.isEmpty(str)){
            ProductModel productModel = new ProductModel();
            productModel.initWithJson(str);
            return productModel;
        } else {
            MLog.w(TAG, "getProductInfo not found !");
        }
        return null;
    }

    /**
     * 更新请求数据
     * @param context
     */
    public static void updateRequestData(Context context, int channel) {
        //更新全局次数
        setDspSpotRequestNum(context, ConstDefine.DSP_GLOABL, getDspSpotRequestNum(context, ConstDefine.DSP_GLOABL)+1);
        //更新单个SITE次数
        setDspSpotRequestNum(context, channel, getDspSpotRequestNum(context, channel)+1);
    }

    /**
     * 更新展示数据
     * @param context
     */
    public static void updateShowData(Context context, int channel) {
        //更新全局次数
        setDspSpotShowNum(context, ConstDefine.DSP_GLOABL, getDspSpotShowNum(context, ConstDefine.DSP_GLOABL)+1);
        //更新单个SITE次数
        setDspSpotShowNum(context, channel, getDspSpotShowNum(context, channel)+1);
    }

    /**
     * 设置请求插屏广告次数
     * @param context
     * @param channel
     * @param num
     */
    public static void setDspSpotRequestNum(Context context, int channel, int num){
        AdsPreferences.getInstance(context).setInt(channel, MacroDefine.MACRO_DSP_SPOT_REQ_NUM, num);
    }

    /**
     * 获取请求插屏广告次数
     * @param context
     * @param channel
     * @return
     */
    public static int getDspSpotRequestNum(Context context, int channel) {
        return AdsPreferences.getInstance(context).getInt(channel, MacroDefine.MACRO_DSP_SPOT_REQ_NUM, 0);
    }

    /**
     * 设置展示插屏广告次数
     * @param context
     * @param channel
     * @param num
     */
    public static void setDspSpotShowNum(Context context, int channel, int num){
        AdsPreferences.getInstance(context).setInt(channel, MacroDefine.MACRO_DSP_SPOT_SHOW_NUM, num);
    }

    /**
     * 获取展示插屏广告次数
     * @param context
     * @param channel
     * @return
     */
    public static int getDspSpotShowNum(Context context, int channel){
        return AdsPreferences.getInstance(context).getInt(channel, MacroDefine.MACRO_DSP_SPOT_SHOW_NUM, 0);
    }

    /**
     * 设置下一次插屏出现的时间
     * @param context
     * @param channel
     */
    public static void setDspSpotNextTime(Context context, int channel, long interval) {
        String key = new StringBuffer().append(MacroDefine.MACRO_DSP_SITE_SHOW_NEXT_TIME)
                .append("_")
                .append(ConstDefine.AD_TYPE_SDK_SPOT)
                .toString().trim();
        long curTime = System.currentTimeMillis();
        long nextTime = curTime + interval;
        AdsPreferences.getInstance(context).setLong(channel, key, nextTime);
    }

    /**
     * 获取下一次插屏出现的时间
     * @param context
     * @param channel
     * @return
     */
    public static long getDspSpotNextTime(Context context, int channel) {
        String key = new StringBuffer().append(MacroDefine.MACRO_DSP_SITE_SHOW_NEXT_TIME)
                .append("_")
                .append(ConstDefine.AD_TYPE_SDK_SPOT)
                .toString().trim();
        return AdsPreferences.getInstance(context).getLong(channel, key, 0);
    }

    /**
     * 设置最后一次插屏出现的时间
     * @param context
     * @param time
     */
    public static void setDspSpotLastTime(Context context, long time) {
        AdsPreferences.getInstance(context).setLong(MacroDefine.MACRO_DSP_SPOT_LAST_SHOW_TIME, time);
    }

    /**
     * 获取最后一次插屏出现的时间
     * @param context
     * @return
     */
    public static long getDspSpotLastTime(Context context) {
        return AdsPreferences.getInstance(context).getLong(MacroDefine.MACRO_DSP_SPOT_LAST_SHOW_TIME, 0);
    }

    /**
     * 处理延时请求
     * @param context
     * @param delayTime
     * @return
     */
    public static boolean isDelayValid(Context context, long delayTime){
        boolean ret = false;
        long currentTime = System.currentTimeMillis()/1000;
        long startTime = AdsPreferences.getInstance(context).getLong(MacroDefine.MACRO_APP_START_TIME, 0);
        if (delayTime > 0 && ((currentTime-startTime) < delayTime)){
            ret = true;
        }
        return ret;
    }

    /**
     * 设置下次联网时间
     * @param context
     * @param time
     */
    public static void setNextNetConTime(Context context, long time) {
        AdsPreferences.getInstance(context).setLong(MacroDefine.MACRO_NET_CONN_TIME, time);
    }

    /**
     * 获取下次联网时间
     * @param context
     * @return
     */
    public static long getNextNetConTime(Context context) {
        return AdsPreferences.getInstance(context).getLong(MacroDefine.MACRO_NET_CONN_TIME, 0L);
    }

    /**
     * 重置数据
     * @param context
     */
    public static void resetData(Context context) {
        MLog.e(TAG, "DspHelper resetData !!!!!!!!!!!!!!!!!!!!!!!!!!");
        //清除全局次数
        setDspSpotRequestNum(context, ConstDefine.DSP_GLOABL, 0);
        setDspSpotShowNum(context, ConstDefine.DSP_GLOABL, 0);
        //清单个SITE次数
        Iterator iterator = ConfigDefine.DDS_ARR.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            int channel = (Integer)entry.getKey();
            setDspSpotRequestNum(context, channel, 0);
            setDspSpotShowNum(context, channel, 0);
        }
        //重置日志发送标志
        AdsPreferences.getInstance(context).setBoolean(MacroDefine.MACRO_LOG_SEND_FLAG, true);
    }

    /**
     * 清除本地缓存数据
     * @param context
     */
    public static void resetLocalConfig(Context context){
        //清除产品信息
        DspHelper.setProductInfo(context, null);
        //清除单个广告信息
        for (int i=1; i<=5; i++){
            for (int j=1; j<=4; j++){
                int channel = i + DspHelper.getTriggerOffSet(j);
                DspHelper.setSiteInfoWithChannel(context, channel, null);
            }
        }
    }

    /**
     * 展示广告
     * @param context
     * @param channel
     * @param triggerType
     */
    public static void showAds(Context context, int channel, int triggerType)  {
        int mChannel = channel + getTriggerOffSet(triggerType);
        setDspSpotNextTime(context, ConstDefine.DSP_GLOABL, ConfigDefine.productInfo.getAppInterval());
        setDspSpotNextTime(context, channel, ConfigDefine.DDS_ARR.get(mChannel).getAppInterval());
        setDspSpotLastTime(context, System.currentTimeMillis());
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
        mIntent.setClass(context.getApplicationContext(), LoadingActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
    }

    /**
     * 获取指定触发类型展示的插屏渠道
     * @param context
     * @return
     */
    public static int getDspSpotChannelByTrigger(Context context, int trigger){
        int channel = ConstDefine.DSP_GLOABL;
        //检测是否屏敝广告
        if (AdsPreferences.getInstance(context).getBoolean(MacroDefine.MACRO_AD_MASK_FLAG, false)){
            MLog.w(TAG, "getDspSpotChannelByTrigger mask ad !");
            return channel;
        }
        //检测全局条件
        if (!checkDspSpotChannelByTrigger(context, ConstDefine.DSP_GLOABL, trigger)){
            MLog.w(TAG, "getDspSpotChannelByTrigger gloabl condition fail !");
            return ConstDefine.DSP_GLOABL;
        }
        //检测单个SITE条件
        for (SiteModel siteModel : ConfigDefine.DDS_AD_MAP.get(trigger)){
            int cid = ConstDefine.AD_INDEX_MAP.get(siteModel.getSdkName());
            if (checkDspSpotChannelByTrigger(context, cid, trigger)){
                channel = cid;
                break;
            }
        }
        return channel;
    }

    /**
     * 检测指定触发条件是否满足
     * @param context
     * @param channel
     * @param trigger
     * @return
     */
    public static boolean checkDspSpotChannelByTrigger(Context context, int channel, int trigger){
        boolean ret = false;
        int mChannel = channel + getTriggerOffSet(trigger);
        if (channel == ConstDefine.DSP_GLOABL){
            ProductModel productModel = ConfigDefine.productInfo;
            //开关
            switch (trigger){
                case ConstDefine.TRIGGER_TYPE_UNLOCK:
                    if (!productModel.isLockFlag()){
                        MLog.w(TAG, "checkDspSpotChannelByTrigger productModel lock flag is false ! ");
                        return ret;
                    }
                    break;
                case ConstDefine.TRIGGER_TYPE_NETWORK:
                    if (!productModel.isNetFlag()){
                        MLog.w(TAG, "checkDspSpotChannelByTrigger productModel network flag is false !");
                        return ret;
                    }
                    break;
                case ConstDefine.TRIGGER_TYPE_APP_ENTER:
                    if (!productModel.isAppEnterFlag()){
                        MLog.w(TAG, "checkDspSpotChannelByTrigger productModel appEnter flag is false !");
                        return ret;
                    }
                    break;
                case ConstDefine.TRIGGER_TYPE_APP_EXIT:
                    if (!productModel.isAppExitFlag()){
                        MLog.w(TAG, "checkDspSpotChannelByTrigger productModel appExit flag is false !");
                        return ret;
                    }
                    break;
            }
            //时间间隔
            long lastTime = getDspSpotNextTime(context, channel);
            if (System.currentTimeMillis() < lastTime){
                MLog.w(TAG, "checkDspSpotChannelByTrigger productModel next time fail !");
                return ret;
            }
            //次数
            int show = getDspSpotShowNum(context, mChannel);
            int req = getDspSpotRequestNum(context, mChannel);
            int totalNum = productModel.getAppCount();
            if (show >= totalNum || req >= totalNum*2){
                MLog.w(TAG, "checkDspSpotChannelByTrigger productModel num fail ! [" + totalNum + "," + req + "," + show + "]");
                return ret;
            }
        } else {
            SiteModel siteModel = ConfigDefine.DDS_ARR.get(mChannel);
            //检测延时配置
            if (isDelayValid(context, siteModel.getDelayTime())){
                MLog.w(TAG, "checkDspSpotChannelByTrigger channel is delay time ! [" + channel + "]");
                return ret;
            }
            //开关
            switch (trigger){
                case ConstDefine.TRIGGER_TYPE_UNLOCK:
                    if (!siteModel.isLockFlag()){
                        MLog.w(TAG, "checkDspSpotChannelByTrigger siteModel lock flag is false ! [" + channel + "]");
                        return ret;
                    }
                    break;
                case ConstDefine.TRIGGER_TYPE_NETWORK:
                    if (!siteModel.isNetFlag()){
                        MLog.w(TAG, "checkDspSpotChannelByTrigger siteModel network flag is false ! [" + channel + "]");
                        return ret;
                    }
                    break;
                case ConstDefine.TRIGGER_TYPE_APP_ENTER:
                    if (!siteModel.isAppEnterFlag()){
                        MLog.w(TAG, "checkDspSpotChannelByTrigger siteModel appEnter flag is false ! [" + channel + "]");
                        return ret;
                    }
                    break;
                case ConstDefine.TRIGGER_TYPE_APP_EXIT:
                    if (!siteModel.isAppExitFlag()){
                        MLog.w(TAG, "checkDspSpotChannelByTrigger siteModel appExit flag is false ! [" + channel + "]");
                        return ret;
                    }
                    break;
            }
            //时间间隔
            long lastTime = getDspSpotNextTime(context, mChannel);
            if (System.currentTimeMillis() < lastTime){
                MLog.w(TAG, "checkDspSpotChannelByTrigger siteModel next time fail ! [" + channel + "]");
                return ret;
            }
            //次数
            int show = getDspSpotShowNum(context, mChannel);
            int req = getDspSpotRequestNum(context, mChannel);
            int totalNum = siteModel.getAppCount();
            if (show >= totalNum || req >= totalNum*2){
                MLog.w(TAG, "checkDspSpotChannelByTrigger siteModel num fail ! [" + totalNum + "," + req + "," + show + "]");
                return ret;
            }
        }
        ret = true;
        return ret;
    }
}
