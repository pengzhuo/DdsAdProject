package com.duduws.ad.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.duduws.ad.log.MLog;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/2/21 19:23
 */

public class AdsPreferences {
    private static final String TAG = "AdsPreferences";

    private Context mContext;
    private SharedPreferences mPref;
    private Editor mEditor;
    private static AdsPreferences instance;

    private AdsPreferences(Context context){
        mContext = context;
        mPref = context.getSharedPreferences("dds_ad_config", 0);
        mEditor = mPref.edit();
    }

    public static synchronized AdsPreferences getInstance(Context context){
        if (instance == null){
            instance = new AdsPreferences(context);
        }
        return instance;
    }

    public void setString(String key, String value){
        if (mEditor != null){
            mEditor.putString(key, value).commit();
        } else {
            MLog.w(TAG, "mEditor is null !");
        }
    }

    public void setString(int channel, String key, String value){
        if (mEditor != null){
            String k = key + "_" + channel;
            mEditor.putString(k, value).commit();
        } else {
            MLog.w(TAG, "mEditor is null !");
        }
    }

    public String getString(String key, String defValue){
        String ret = null;
        if (mPref != null){
            ret = mPref.getString(key, defValue);
        } else {
            MLog.w(TAG, "mPref is null !");
        }
        return ret;
    }

    public String getString(int channel, String key, String defValue){
        String ret = null;
        if (mPref != null){
            String k = key + "_" + channel;
            ret = mPref.getString(k, defValue);
        } else {
            MLog.w(TAG, "mPref is null !");
        }
        return ret;
    }

    public void setLong(String key, long value){
        if (mEditor != null){
            mEditor.putLong(key, value).commit();
        } else {
            MLog.w(TAG, "mEditor is null !");
        }
    }

    public void setLong(int channel, String key, long value){
        if (mEditor != null){
            String k = key + "_" + channel;
            mEditor.putLong(k, value).commit();
        } else {
            MLog.w(TAG, "mEditor is null !");
        }
    }

    public long getLong(String key, long defValue){
        long ret = 0;
        if (mPref != null){
            ret = mPref.getLong(key, defValue);
        } else {
            MLog.w(TAG, "mPref is null !");
        }
        return ret;
    }

    public long getLong(int channel, String key, long defValue){
        long ret = 0;
        if (mPref != null){
            String k = key + "_" + channel;
            ret = mPref.getLong(k, defValue);
        } else {
            MLog.w(TAG, "mPref is null !");
        }
        return ret;
    }

    public void setInt(String key, int value){
        if (mEditor != null){
            mEditor.putInt(key, value).commit();
        } else {
            MLog.w(TAG, "mEditor is null !");
        }
    }

    public void setInt(int channel, String key, int value){
        if (mEditor != null){
            String k = key + "_" + channel;
            mEditor.putInt(k, value).commit();
        } else {
            MLog.w(TAG, "mEditor is null !");
        }
    }

    public int getInt(String key, int defValue){
        int ret = 0;
        if (mPref != null){
            ret = mPref.getInt(key, defValue);
        } else {
            MLog.w(TAG, "mPref is null !");
        }
        return ret;
    }

    public int getInt(int channel, String key, int defValue){
        int ret = 0;
        if (mPref != null){
            String k = key + "_" + channel;
            ret = mPref.getInt(k, defValue);
        } else {
            MLog.w(TAG, "mPref is null !");
        }
        return ret;
    }

    public void setBoolean(String key, boolean value){
        if (mEditor != null){
            mEditor.putBoolean(key, value).commit();
        } else {
            MLog.w(TAG, "mEditor is null !");
        }
    }

    public void setBoolean(int channel, String key, boolean value){
        if (mEditor != null){
            String k = key + "_" + channel;
            mEditor.putBoolean(k, value).commit();
        } else {
            MLog.w(TAG, "mEditor is null !");
        }
    }

    public boolean getBoolean(String key, boolean defValue){
        boolean ret = false;
        if (mPref != null){
            ret = mPref.getBoolean(key, defValue);
        } else {
            MLog.w(TAG, "mPref is null !");
        }
        return ret;
    }

    public boolean getBoolean(int channel, String key, boolean defValue){
        boolean ret = false;
        if (mPref != null){
            String k = key + "_" + channel;
            ret = mPref.getBoolean(k, defValue);
        } else {
            MLog.w(TAG, "mPref is null !");
        }
        return ret;
    }
}
