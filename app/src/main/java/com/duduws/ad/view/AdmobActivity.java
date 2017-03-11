package com.duduws.ad.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.duduws.ad.analytics.AnalyticsUtils;
import com.duduws.ad.analytics.DdsLogUtils;
import com.duduws.ad.common.ConfigDefine;
import com.duduws.ad.common.ConstDefine;
import com.duduws.ad.common.MacroDefine;
import com.duduws.ad.log.MLog;
import com.duduws.ad.utils.DspHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Pengz on 16/7/29.
 */
public class AdmobActivity extends BaseActivity{
    private static final String TAG = "AdmobActivity";
    private static InterstitialAd interstitialAd;
    private int triggerType = -1;
    private int offset = 0;
    private String site = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null){
            triggerType = intent.getExtras().getInt(MacroDefine.MACRO_AD_TRIGGER_TYPE);
        }

        offset = DspHelper.getTriggerOffSet(triggerType);

        site = ConfigDefine.DDS_ARR.get(ConstDefine.DSP_CHANNEL_ADMOB+offset).getSite();

        if (!TextUtils.isEmpty(site)){
            initInterstitialAd();
        }else{
            //重置广告展示标志
            DspHelper.setCurrentAdsShowFlag(AdmobActivity.this, false);
        }

        finish();
    }

    private void initInterstitialAd(){
        interstitialAd = new InterstitialAd(getApplicationContext());
        interstitialAd.setAdUnitId(site);
        interstitialAd.setAdListener(listener);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        DspHelper.updateRequestData(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB+offset);
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_ADMOB, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_REQUEST);
    }

    AdListener listener = new AdListener() {
        @Override
        public void onAdClosed() {
            super.onAdClosed();
            MLog.i(TAG, "onAdClosed ");
            AnalyticsUtils.onEvent(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_CLOSE);
            //重置广告展示标志
            DspHelper.setCurrentAdsShowFlag(AdmobActivity.this, false);
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            super.onAdFailedToLoad(errorCode);
            MLog.i(TAG, "onAdFailedToLoad " + errorCode);
            AnalyticsUtils.onEvent(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_FAIL);
            //重置广告展示标志
            DspHelper.setCurrentAdsShowFlag(AdmobActivity.this, false);
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
            MLog.i(TAG, "onAdOpened ");
            DspHelper.updateShowData(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB+offset);
            AnalyticsUtils.onEvent(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_SHOW);
            DdsLogUtils.getInstance(AdmobActivity.this).log(site, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_SHOW, ConstDefine.DSP_CHANNEL_ADMOB);
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
            MLog.i(TAG, "onAdLeftApplication ");
            AnalyticsUtils.onEvent(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_CLICK);
            DdsLogUtils.getInstance(AdmobActivity.this).log(site, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_CLICK, ConstDefine.DSP_CHANNEL_ADMOB);
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            MLog.i(TAG, "onAdLoaded ");
            interstitialAd.show();
            AnalyticsUtils.onEvent(AdmobActivity.this, ConstDefine.DSP_CHANNEL_ADMOB, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_SUCCESS);
        }
    };
}
