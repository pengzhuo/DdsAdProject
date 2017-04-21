package com.duduws.ad.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duduws.ad.R;
import com.duduws.ad.common.ConfigDefine;
import com.duduws.ad.utils.DspHelper;
import com.duduws.ad.utils.FuncUtils;

public class ShowDDSActivity extends Activity {
    private static final String TAG = "ShowDDSActivity";
    Bitmap bm_bg = null;
    Bitmap bm_body = null;
    Bitmap bm_icon = null;
    String title = null;
    String desc = null;
    String url = null;
    int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null){
            //重置广告展示标志
            DspHelper.setCurrentAdsShowFlag(this, false);
            finish();
        }
        Intent intent = getIntent();
//        byte[] bis_bg = intent.getByteArrayExtra("bm_bg");
//        bm_bg = BitmapFactory.decodeByteArray(bis_bg, 0, bis_bg.length);
//        byte[] bis_body = intent.getByteArrayExtra("bm_body");
//        bm_body = BitmapFactory.decodeByteArray(bis_body, 0, bis_body.length);
//        byte[] bis_icon = intent.getByteArrayExtra("bm_icon");
//        bm_icon = BitmapFactory.decodeByteArray(bis_icon, 0, bis_icon.length);
        title = intent.getStringExtra("title");
        desc = intent.getStringExtra("desc");
        url = intent.getStringExtra("url");
        type = intent.getIntExtra("type", 0);

        //设置布局
        setContentView(R.layout.ad_unit_self);
        //罗列控件
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.rlayout_bg);
        ImageView img_body = (ImageView)findViewById(R.id.img_body);
        ImageView img_icon = (ImageView)findViewById(R.id.native_ad_icon);
        TextView tv_title = (TextView)findViewById(R.id.native_ad_title);
        TextView tv_desc = (TextView)findViewById(R.id.native_ad_body);
        Button button = (Button)findViewById(R.id.native_ad_call_to_action);
        ImageView img_del = (ImageView)findViewById(R.id.ad_delete);

        relativeLayout.setBackground(new BitmapDrawable(ConfigDefine.bm_bg));
        img_body.setImageBitmap(ConfigDefine.bm_body);
        img_icon.setImageBitmap(ConfigDefine.bm_icon);
        tv_title.setText(title);
        tv_desc.setText(desc);
        if (type == 1){
            //跳转GP
            button.setText(getString(R.string.ad_type_1));
        } else if (type == 2) {
            //直接下载
            button.setText(getString(R.string.ad_type_1));
        } else {
            //跳网页
            button.setText(getString(R.string.ad_type_2));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigDefine.AD_LISTENER.onClicked();
                if (type == 1){
                    //跳转GP
                    FuncUtils.jumpToGP(ShowDDSActivity.this, url);
                } else if (type == 2) {
                    //直接下载
                    FuncUtils.jumpToUrl(ShowDDSActivity.this, url);
                } else {
                    //跳网页
                    FuncUtils.jumpToUrl(ShowDDSActivity.this, url);
                }
            }
        });

        img_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigDefine.AD_LISTENER.onDismissed();
                ShowDDSActivity.this.finish();
            }
        });

        RelativeLayout clickArea = (RelativeLayout)findViewById(R.id.clickLayout);
        clickArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigDefine.AD_LISTENER.onClicked();
                if (type == 1){
                    //跳转GP
                    FuncUtils.jumpToGP(ShowDDSActivity.this, url);
                } else if (type == 2) {
                    //直接下载
                    FuncUtils.jumpToUrl(ShowDDSActivity.this, url);
                } else {
                    //跳网页
                    FuncUtils.jumpToUrl(ShowDDSActivity.this, url);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //重置广告展示标志
        DspHelper.setCurrentAdsShowFlag(this, false);
    }
}
