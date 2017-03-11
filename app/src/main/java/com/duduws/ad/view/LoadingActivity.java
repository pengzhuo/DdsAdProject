package com.duduws.ad.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.duduws.ad.R;
import com.duduws.ad.common.ConstDefine;
import com.duduws.ad.common.MacroDefine;
import com.duduws.ad.log.MLog;
import com.duduws.ad.utils.DspHelper;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/9/10 17:31
 */
public class LoadingActivity extends Activity {
    private static final String TAG = "LoadingActivity";
    int channel = -1;
    int triggerType = 0;
    boolean isOutSide = false;
    Intent intent = null;
    ImageView img = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        DspHelper.setCurrentAdsShowFlag(LoadingActivity.this, true);
        //打开相应渠道的广告
        channel = intent.getIntExtra("channel", -1);
        triggerType = intent.getIntExtra("triggerType", -1);
        isOutSide = intent.getBooleanExtra("isOutSide", false);

        if (triggerType == ConstDefine.TRIGGER_TYPE_APP_ENTER){
            Message msg = new Message();
            msg.what = 0;
            handler.sendMessage(msg);
        }else{
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.loading);
            img = (ImageView)findViewById(R.id.infoOperating);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.tip);
            LinearInterpolator lin = new LinearInterpolator();
            animation.setInterpolator(lin);
            img.startAnimation(animation);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        Message msg = new Message();
                        msg.what = 0;
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        MLog.e(TAG, e.getMessage());
                    }
                }
            }).start();
        }
    }

    @Override
    protected void onDestroy() {
        if (img != null){
            img.clearAnimation();
            img = null;
        }
        super.onDestroy();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    {
                        switch (channel){
                            case ConstDefine.DSP_CHANNEL_FACEBOOK:
                                intent.setClass(LoadingActivity.this, FacebookActivity.class);
                                break;
                            case ConstDefine.DSP_CHANNEL_ADMOB:
                                intent.setClass(LoadingActivity.this, AdmobActivity.class);
                                break;
                            case ConstDefine.DSP_CHANNEL_CM:

                                break;
                            case ConstDefine.DSP_CHANNEL_DDS:
                                intent.setClass(LoadingActivity.this, SelfActivity.class);
                                break;
                            case ConstDefine.DSP_CHANNEL_GDT:

                                break;
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(MacroDefine.MACRO_AD_TRIGGER_TYPE, triggerType);
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
        }
    };
}
