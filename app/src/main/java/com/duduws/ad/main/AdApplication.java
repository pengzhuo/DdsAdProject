package com.duduws.ad.main;

import android.app.Application;
import android.text.TextUtils;

import com.duduws.ad.analytics.AnalyticsUtils;
import com.duduws.ad.common.ConfigDefine;
import com.duduws.ad.common.ConstDefine;
import com.duduws.ad.common.MacroDefine;
import com.duduws.ad.log.MLog;
import com.duduws.ad.model.ProductModel;
import com.duduws.ad.model.SiteModel;
import com.duduws.ad.model.UserModel;
import com.duduws.ad.utils.AdsPreferences;
import com.duduws.ad.utils.DspHelper;
import com.duduws.ad.utils.FuncUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/2/21 19:22
 */

public class AdApplication extends Application {
    private static final String TAG = "AdApplication";

    private ArrayList<SiteModel> lockList = new ArrayList<>();
    private ArrayList<SiteModel> netList = new ArrayList<>();
    private ArrayList<SiteModel> appEnterList = new ArrayList<>();
    private ArrayList<SiteModel> appExitList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        //记录APP启动的时间
        if (AdsPreferences.getInstance(this).getLong(MacroDefine.MACRO_APP_START_TIME, 0) <= 0){
            long startTime = System.currentTimeMillis()/1000;
            AdsPreferences.getInstance(this).setLong(MacroDefine.MACRO_APP_START_TIME, startTime);
        }

        //设置是否输出日志
        MLog.setLogEnable(true);

        //初始化
        init();

        //初始化数据统计接口
        AnalyticsUtils.getInstance(getApplicationContext());
    }

    private void init(){
        //初始化版本信息
        ConstDefine.init();
        ConfigDefine.APP_KEY_FLURRY      = FuncUtils.getManifestApplicationMetaData(this, "FLURRY_APPKEY");
        ConfigDefine.APP_KEY_UMENG       = FuncUtils.getManifestApplicationMetaData(this, "UMENG_APPKEY");
        ConfigDefine.APP_CHANNEL_UMENG   = FuncUtils.getManifestApplicationMetaData(this, "UMENG_CHANNEL");
        ConfigDefine.APP_VERSION 		 = FuncUtils.getManifestApplicationMetaData(this, "APP_VERSION");
        ConfigDefine.APP_CHANNEL_ID		 = FuncUtils.getManifestApplicationMetaData(this, "APP_CHANNEL_ID");
        ConfigDefine.APP_COOPERATION_ID	 = FuncUtils.getManifestApplicationMetaData(this, "APP_COOPERATION_ID");
        ConfigDefine.APP_PRODUCT_ID		 = FuncUtils.getManifestApplicationMetaData(this, "APP_PRODUCT_ID");
        String gloablStr = FuncUtils.getManifestApplicationMetaData(this, "GLOABL_INTERVAL");
        gloablStr = gloablStr.substring(3, gloablStr.length());
        ConfigDefine.APP_GLOABL_INTERVAL = Long.parseLong(gloablStr);
        String siteStr = FuncUtils.getManifestApplicationMetaData(this, "SITE_INTERVAL");
        siteStr = siteStr.substring(3, siteStr.length());
        ConfigDefine.APP_SITE_INTERVAL   = Long.parseLong(siteStr);

        //设置用户信息
        ConfigDefine.userInfo = new UserModel(this);

        //如果本地不存在缓存配置信息，则加载默认配置
        ConfigDefine.DDS_ARR.clear();
        ConfigDefine.DDS_AD_MAP.clear();
        if (AdsPreferences.getInstance(this).getString(MacroDefine.MACRO_LOCAL_CONFIG_FLAG, "").equalsIgnoreCase("success")){
            //本地缓存配置
            //产品信息
            ConfigDefine.productInfo = DspHelper.getProductInfo(this);
            //Site信息
            for (int i=1; i<=5; i++){
                for (int j=1; j<=4; j++){
                    int channel = i + DspHelper.getTriggerOffSet(j);
                    SiteModel siteModel = DspHelper.getSiteInfoWithChannel(this, channel);
                    if (siteModel != null){
                        ConfigDefine.DDS_ARR.put(channel, siteModel);
                        switch (j){
                            case ConstDefine.TRIGGER_TYPE_APP_ENTER:
                                appEnterList.add(siteModel);
                                break;
                            case ConstDefine.TRIGGER_TYPE_APP_EXIT:
                                appExitList.add(siteModel);
                                break;
                            case ConstDefine.TRIGGER_TYPE_NETWORK:
                                netList.add(siteModel);
                                break;
                            case ConstDefine.TRIGGER_TYPE_UNLOCK:
                                lockList.add(siteModel);
                                break;
                        }
                    }
                }
            }
            ConfigDefine.DDS_AD_MAP.put(ConstDefine.TRIGGER_TYPE_UNLOCK, lockList);
            ConfigDefine.DDS_AD_MAP.put(ConstDefine.TRIGGER_TYPE_NETWORK, netList);
            ConfigDefine.DDS_AD_MAP.put(ConstDefine.TRIGGER_TYPE_APP_ENTER, appEnterList);
            ConfigDefine.DDS_AD_MAP.put(ConstDefine.TRIGGER_TYPE_APP_EXIT, appExitList);
        } else {
            //默认配置
            //设置产品
            ProductModel productModel = new ProductModel();
            ConfigDefine.productInfo = productModel;
            //设置渠道Site
            String fb_site = FuncUtils.getManifestApplicationMetaData(this, "SITE_FACEBOOK");
            initSiteInfo(ConstDefine.DSP_CHANNEL_FACEBOOK, fb_site);
            String admob_site = FuncUtils.getManifestApplicationMetaData(this, "SITE_ADMOB");
            initSiteInfo(ConstDefine.DSP_CHANNEL_ADMOB, admob_site);
            String dds_site = FuncUtils.getManifestApplicationMetaData(this, "SITE_DDS");
            initSiteInfo(ConstDefine.DSP_CHANNEL_DDS, dds_site);
            ConfigDefine.DDS_AD_MAP.put(ConstDefine.TRIGGER_TYPE_UNLOCK, lockList);
            ConfigDefine.DDS_AD_MAP.put(ConstDefine.TRIGGER_TYPE_NETWORK, netList);
            ConfigDefine.DDS_AD_MAP.put(ConstDefine.TRIGGER_TYPE_APP_ENTER, appEnterList);
            ConfigDefine.DDS_AD_MAP.put(ConstDefine.TRIGGER_TYPE_APP_EXIT, appExitList);
        }
    }

    private void initSiteInfo(int type, String json){
        try {
            int diff = ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE - ConstDefine.DSP_CHANNEL_FACEBOOK;
            int diff_ex = ConstDefine.DSP_CHANNEL_FACEBOOK_VIDEO - ConstDefine.DSP_CHANNEL_FACEBOOK;

            JSONObject jsonObject = new JSONObject(json);
            if (!jsonObject.isNull("sdk")){
                JSONObject sdk = jsonObject.optJSONObject("sdk");
                initSubSite(type, sdk);
            }
            if (!jsonObject.isNull("native")){
                JSONObject nativeObj = jsonObject.optJSONObject("native");
                initSubSite(type+diff, nativeObj);
            }
            if (!jsonObject.isNull("video")){
                JSONObject videoObj = jsonObject.optJSONObject("video");
                initSubSite(type+diff_ex, videoObj);
            }
        } catch (JSONException e) {
            MLog.e(TAG, e.getMessage());
        }
    }

    private void initSubSite(int channel, JSONObject jsonObject){
        //解锁
        if (!jsonObject.isNull("unlock")){
            if (!TextUtils.isEmpty(jsonObject.optString("unlock"))){
                SiteModel siteModel = new SiteModel();
                siteModel.setSite(jsonObject.optString("unlock"));
                siteModel.setSdkName(ConstDefine.AD_INDEX_MAP_EX.get(channel));
                siteModel.setAdType(ConstDefine.AD_TYPE_SDK_SPOT);
                siteModel.setTriggerType(ConstDefine.TRIGGER_TYPE_UNLOCK);
                lockList.add(siteModel);
                ConfigDefine.DDS_ARR.put(channel+DspHelper.getTriggerOffSet(ConstDefine.TRIGGER_TYPE_UNLOCK), siteModel);
            }
        }
        //开网
        if (!jsonObject.isNull("net")){
            if (!TextUtils.isEmpty(jsonObject.optString("net"))){
                SiteModel siteModel = new SiteModel();
                siteModel.setSite(jsonObject.optString("net"));
                siteModel.setSdkName(ConstDefine.AD_INDEX_MAP_EX.get(channel));
                siteModel.setAdType(ConstDefine.AD_TYPE_SDK_SPOT);
                siteModel.setTriggerType(ConstDefine.TRIGGER_TYPE_NETWORK);
                netList.add(siteModel);
                ConfigDefine.DDS_ARR.put(channel+DspHelper.getTriggerOffSet(ConstDefine.TRIGGER_TYPE_NETWORK), siteModel);
            }
        }
        //APP进入
        if (!jsonObject.isNull("enter")){
            if (!TextUtils.isEmpty(jsonObject.optString("enter"))){
                SiteModel siteModel = new SiteModel();
                siteModel.setSite(jsonObject.optString("enter"));
                siteModel.setSdkName(ConstDefine.AD_INDEX_MAP_EX.get(channel));
                siteModel.setAdType(ConstDefine.AD_TYPE_SDK_SPOT);
                siteModel.setTriggerType(ConstDefine.TRIGGER_TYPE_APP_ENTER);
                appEnterList.add(siteModel);
                ConfigDefine.DDS_ARR.put(channel+DspHelper.getTriggerOffSet(ConstDefine.TRIGGER_TYPE_APP_ENTER), siteModel);
            }
        }
        //APP退出
        if (!jsonObject.isNull("exit")){
            if (!TextUtils.isEmpty(jsonObject.optString("exit"))){
                SiteModel siteModel = new SiteModel();
                siteModel.setSite(jsonObject.optString("exit"));
                siteModel.setSdkName(ConstDefine.AD_INDEX_MAP_EX.get(channel));
                siteModel.setAdType(ConstDefine.AD_TYPE_SDK_SPOT);
                siteModel.setTriggerType(ConstDefine.TRIGGER_TYPE_APP_EXIT);
                appExitList.add(siteModel);
                ConfigDefine.DDS_ARR.put(channel+DspHelper.getTriggerOffSet(ConstDefine.TRIGGER_TYPE_APP_EXIT), siteModel);
            }
        }
    }
}
