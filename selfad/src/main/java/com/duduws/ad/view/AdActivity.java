package com.duduws.ad.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.duduws.ad.common.ConfigDefine;
import com.duduws.ad.log.MLog;
import com.duduws.ad.model.AdModel;
import com.duduws.ad.utils.AdsPreferences;
import com.duduws.ad.utils.DspHelper;
import com.duduws.ad.utils.FuncUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class AdActivity extends Activity {
    private static final String TAG = "AdActivity";

    Bitmap bm_bg = null;
    Bitmap bm_body = null;
    Bitmap bm_icon = null;
    String title = null;
    String desc = null;
    String url = null;
    int type = 0;

    private AdModel adModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        WindowManager.LayoutParams a = getWindow().getAttributes();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        a.gravity = Gravity.CENTER;
        a.dimAmount = 0.0f;
        getWindow().setAttributes(a);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ConfigDefine.AD_LISTENER == null || ConfigDefine.ARR_LIST_ADS == null){
                    MLog.e(TAG, "make sure listener or ad is request success!");
                    finish();
                } else {
                    //初始化数据
                    adModel = randomAd();
                    if (adModel == null){
                        return;
                    }
                    String localStr = AdsPreferences.getInstance(AdActivity.this).getString("ad_"+adModel.getId(), "");
//                    MLog.e(TAG, "localStr id: " + "ad_" + adModel.getId() + " | str: " + localStr);
                    if (!localStr.equals("")){
                        AdModel localAdmodel = new AdModel();
                        localAdmodel.initWithJson(localStr);
                        if (adModel.getVersion() > localAdmodel.getVersion()){
                            bm_bg = FuncUtils.getBitMapFromNet(adModel.getImg_bg());
                            bm_body = FuncUtils.getBitMapFromNet(adModel.getImg_body());
                            bm_icon = FuncUtils.getBitMapFromNet(adModel.getIcon());
                            FuncUtils.saveBitmap(FuncUtils.getWritePath(AdActivity.this)+"adinfo/"+adModel.getId()+"/", "bg.jpg", bm_bg);
                            FuncUtils.saveBitmap(FuncUtils.getWritePath(AdActivity.this)+"adinfo/"+adModel.getId()+"/", "body.jpg", bm_body);
                            FuncUtils.saveBitmap(FuncUtils.getWritePath(AdActivity.this)+"adinfo/"+adModel.getId()+"/", "icon.png", bm_icon);
                        }else{
//                            MLog.e(TAG, "read image info from local !");
                            bm_bg = FuncUtils.getLocalBitmap(FuncUtils.getWritePath(AdActivity.this)+"adinfo/"+adModel.getId()+"/bg.jpg");
                            bm_body = FuncUtils.getLocalBitmap(FuncUtils.getWritePath(AdActivity.this)+"adinfo/"+adModel.getId()+"/body.jpg");
                            bm_icon = FuncUtils.getLocalBitmap(FuncUtils.getWritePath(AdActivity.this)+"adinfo/"+adModel.getId()+"/icon.png");
                        }
                    }else{
                        bm_bg = FuncUtils.getBitMapFromNet(adModel.getImg_bg());
                        bm_body = FuncUtils.getBitMapFromNet(adModel.getImg_body());
                        bm_icon = FuncUtils.getBitMapFromNet(adModel.getIcon());
                        FuncUtils.saveBitmap(FuncUtils.getWritePath(AdActivity.this)+"adinfo/"+adModel.getId()+"/", "bg.jpg", bm_bg);
                        FuncUtils.saveBitmap(FuncUtils.getWritePath(AdActivity.this)+"adinfo/"+adModel.getId()+"/", "body.jpg", bm_body);
                        FuncUtils.saveBitmap(FuncUtils.getWritePath(AdActivity.this)+"adinfo/"+adModel.getId()+"/", "icon.png", bm_icon);
                    }
                    type = adModel.getType();
                    title = adModel.getTitle();
                    desc = adModel.getDesc();
                    url = adModel.getUrl();

                    AdsPreferences.getInstance(AdActivity.this).setString("ad_"+adModel.getId(), adModel.toJson());
//                    MLog.e(TAG, "set id: " + "ad_" + adModel.getId() + " | str: " + adModel.toJson());

                    if (bm_bg == null || bm_body == null || bm_icon == null){
                        //重置广告展示标志
                        DspHelper.setCurrentAdsShowFlag(AdActivity.this, false);
                    } else {
                        handler.sendEmptyMessage(0);
                    }
                }
            }
        }).start();
        finish();
    }

    private AdModel randomAd(){
        AdModel adModel = null;
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i=0; i<ConfigDefine.ARR_LIST_ADS.size(); i++){
            AdModel tmp = ConfigDefine.ARR_LIST_ADS.get(i);
            for (int j=0; j<tmp.getProbability(); j++){
                arr.add(tmp.getId());
            }
        }
        if (arr.size() < 100){
            int len = 100 - arr.size();
            for (int i=0; i<len; i++){
                arr.add(0);
            }
        }
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int num = random.nextInt(100);
        if (arr.get(num) != 0){
            for (int i=0; i<ConfigDefine.ARR_LIST_ADS.size(); i++){
                AdModel tmp = ConfigDefine.ARR_LIST_ADS.get(i);
                if (tmp.getId() == arr.get(num)){
                    adModel = tmp;
                }
            }
        }
        return adModel;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:{
                    ConfigDefine.bm_bg = bm_bg;
                    ConfigDefine.bm_body = bm_body;
                    ConfigDefine.bm_icon = bm_icon;
                    Intent intent = new Intent();
                    intent.setClass(AdActivity.this, ShowDDSActivity.class);
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bm_bg.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    intent.putExtra("bm_bg", baos.toByteArray());
//                    bm_body.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    intent.putExtra("bm_body", baos.toByteArray());
//                    bm_icon.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    intent.putExtra("bm_icon", baos.toByteArray());
                    intent.putExtra("title", title);
                    intent.putExtra("desc", desc);
                    intent.putExtra("url", url);
                    intent.putExtra("type", type);
                    startActivity(intent);
                    ConfigDefine.AD_LISTENER.onDisplayed();
                }
                break;
            }
        }
    };
}
