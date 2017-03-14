package com.duduws.ad.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.duduws.ad.analytics.AnalyticsUtils;
import com.duduws.ad.analytics.DdsLogUtils;
import com.duduws.ad.common.ConfigDefine;
import com.duduws.ad.common.ConstDefine;
import com.duduws.ad.common.MacroDefine;
import com.duduws.ad.listener.AdListener;
import com.duduws.ad.log.MLog;
import com.duduws.ad.model.SiteModel;
import com.duduws.ad.net.NetManager;
import com.duduws.ad.utils.DspHelper;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/2/13 15:16
 */

public class SelfActivity extends Activity {
    private static final String TAG = "SelfActivity";
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

        int channel = ConstDefine.DSP_CHANNEL_DDS + offset;
        SiteModel siteModel = ConfigDefine.DDS_ARR.get(channel);
        site = siteModel.getSite();

        ConfigDefine.AD_LISTENER = new AdListener() {
            @Override
            public void onError(int code) {
                MLog.e(TAG, "DDS ad onError " + code);
                AnalyticsUtils.onEvent(SelfActivity.this, ConstDefine.DSP_CHANNEL_DDS, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_FAIL);
                //重置广告展示标志
                DspHelper.setCurrentAdsShowFlag(SelfActivity.this, false);
            }

            @Override
            public void onLoaded() {
                MLog.e(TAG, "DDS ad onLoaded ");
                Intent intent = new Intent();
                intent.setClass(SelfActivity.this, AdActivity.class);
                SelfActivity.this.startActivity(intent);
                AnalyticsUtils.onEvent(SelfActivity.this, ConstDefine.DSP_CHANNEL_DDS, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_SUCCESS);
            }

            @Override
            public void onClicked() {
                MLog.e(TAG, "DDS ad onClicked ");
                AnalyticsUtils.onEvent(SelfActivity.this, ConstDefine.DSP_CHANNEL_DDS, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_CLICK);
                DdsLogUtils.getInstance(SelfActivity.this).log(site, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_CLICK, ConstDefine.DSP_CHANNEL_DDS);
            }

            @Override
            public void onDisplayed() {
                MLog.e(TAG, "DDS ad onDisplayed ");
                DspHelper.updateShowData(SelfActivity.this, ConstDefine.DSP_CHANNEL_DDS+offset);
                AnalyticsUtils.onEvent(SelfActivity.this, ConstDefine.DSP_CHANNEL_DDS, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_SHOW);
                DdsLogUtils.getInstance(SelfActivity.this).log(site, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_SHOW, ConstDefine.DSP_CHANNEL_DDS);
            }

            @Override
            public void onDismissed() {
                MLog.e(TAG, "DDS ad onDismissed ");
                AnalyticsUtils.onEvent(SelfActivity.this, ConstDefine.DSP_CHANNEL_DDS, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_CLOSE);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                NetManager.getInstance(SelfActivity.this).getAdInfoFromServer(1);
                DspHelper.updateRequestData(SelfActivity.this, ConstDefine.DSP_CHANNEL_DDS+offset);
                AnalyticsUtils.onEvent(SelfActivity.this, ConstDefine.DSP_CHANNEL_DDS, triggerType, ConstDefine.AD_TYPE_SDK_SPOT, ConstDefine.AD_RESULT_REQUEST);
            }
        }).start();

        finish();
    }
}
