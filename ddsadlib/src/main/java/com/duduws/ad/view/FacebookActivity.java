package com.duduws.ad.view;

import android.content.Context;
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
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

public class FacebookActivity extends BaseActivity implements InterstitialAdListener {
    private static final String TAG = "FacebookActivity";
    private InterstitialAd interstitialAd = null;
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

        site = ConfigDefine.DDS_ARR.get(ConstDefine.DSP_CHANNEL_FACEBOOK+offset).getSite();

        if (!TextUtils.isEmpty(site)){
            //初始化Facebook
            loadInterstitialAd(getApplicationContext(), site);
        }else{
            //重置广告展示标志
            DspHelper.setCurrentAdsShowFlag(this, false);
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        if (interstitialAd != null){
            interstitialAd.destroy();
        }
        super.onDestroy();
    }

    private void loadInterstitialAd(Context context, String id){
        MLog.e(TAG, "#### loadInterstitialAd " + id);
        interstitialAd = new InterstitialAd(context, id);
        interstitialAd.setAdListener(this);
        interstitialAd.loadAd();
        DspHelper.updateRequestData(this, ConstDefine.DSP_CHANNEL_FACEBOOK+offset);
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_REQUEST);
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {
        MLog.i(TAG, "onInterstitialDisplayed " + ad.toString());
        DspHelper.updateShowData(this, ConstDefine.DSP_CHANNEL_FACEBOOK+offset);
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_SHOW);
        DdsLogUtils.getInstance(this).log(site, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_SHOW, ConstDefine.DSP_CHANNEL_FACEBOOK);
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        MLog.i(TAG, "onInterstitialDismissed " + ad.toString());
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_CLOSE);
        //重置广告展示标志
        DspHelper.setCurrentAdsShowFlag(this, false);
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        MLog.i(TAG, "onError " + ad.toString() + " error: " + adError.getErrorCode() + " , " + adError.getErrorMessage());
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_FAIL);
        //重置广告展示标志
        DspHelper.setCurrentAdsShowFlag(this, false);
    }

    @Override
    public void onAdLoaded(Ad ad) {
        MLog.i(TAG, "onAdLoaded " + ad.toString());
        interstitialAd.show();
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_SUCCESS);
    }

    @Override
    public void onAdClicked(Ad ad) {
        MLog.i(TAG, "onAdClicked " + ad.toString());
        AnalyticsUtils.onEvent(this, ConstDefine.DSP_CHANNEL_FACEBOOK, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_CLICK);
        DdsLogUtils.getInstance(this).log(site, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_CLICK, ConstDefine.DSP_CHANNEL_FACEBOOK);
    }
}
