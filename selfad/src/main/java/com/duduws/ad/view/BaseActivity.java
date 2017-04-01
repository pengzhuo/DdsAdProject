package com.duduws.ad.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.duduws.ad.R;
import com.duduws.ad.adapter.AppListAdapter;
import com.duduws.ad.log.MLog;
import com.duduws.ad.model.AdItemList;
import com.duduws.ad.model.PackageElement;
import com.duduws.ad.utils.FuncUtils;
import com.flurry.android.FlurryAgent;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

public class BaseActivity extends Activity implements OnItemClickListener {
    private static final String TAG = "BaseActivity";
    private ArrayList<PackageElement> mPkgList;
    private AppListAdapter mGvAdapter;
    private AdItemList mList;
    private int mAdNum;
    private LinearLayout mLl;
    private GridView mGvFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.icon_file_activity);
        WindowManager.LayoutParams a = getWindow().getAttributes();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        a.gravity = Gravity.CENTER;
        a.dimAmount = 0.75f;
        getWindow().setAttributes(a);

        mPkgList = new ArrayList<PackageElement>();
        mList = new AdItemList();
        mAdNum = 0;

        initView();
        initData();
    }

    private void initView() {
        mLl = (LinearLayout) findViewById(R.id.ll_bg);
        mGvFile = (GridView) findViewById(R.id.gv);
        mGvFile.setOnItemClickListener(this);
    }

    private void initData() {
        mPkgList = FuncUtils.getRecentApps(getApplicationContext());
        mGvAdapter = new AppListAdapter(getApplicationContext(), mPkgList);
        mGvFile.setAdapter(mGvAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mPkgList.get(position).ismIsNative()) {

        } else {
            FuncUtils.runApps(BaseActivity.this, mPkgList.get(position).getmPackageName());
        }
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
}
