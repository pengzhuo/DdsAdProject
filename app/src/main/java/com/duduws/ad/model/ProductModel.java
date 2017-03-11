package com.duduws.ad.model;

import com.duduws.ad.common.ConfigDefine;
import com.duduws.ad.common.ConstDefine;
import com.duduws.ad.log.MLog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/2/23 10:41
 */

public class ProductModel {
    private static final String TAG = "ProductModel";

    public ProductModel(){
        setWorkid(ConfigDefine.APP_COOPERATION_ID);
        setAppCount(ConstDefine.APP_COUNT_GLOABL_DEFAULT);
        setAppInterval(ConfigDefine.APP_GLOABL_INTERVAL*1000);
        setCid(ConfigDefine.APP_CHANNEL_ID);
        setPid(ConfigDefine.APP_PRODUCT_ID);
        setAppEnterFlag(true);
        setAppExitFlag(true);
        setLockFlag(true);
        setNetFlag(true);
        setLaunchFlag(true);
        setStatus(true);
    }

    public void initWithJson(String jsonStr){
        try {
            JSONObject obj = new JSONObject(jsonStr);
            setWorkid(obj.optString("workerid"));
            setAppCount(obj.optInt("app_count"));
            setAppInterval(obj.optInt("app_interval"));
            setCid(obj.optString("channelid"));
            setPid(obj.optString("pid"));
            setAppEnterFlag((obj.optInt("topapp_enter_action",1)==1)?true:false);
            setAppExitFlag((obj.optInt("topapp_exit_action",1)==1)?true:false);
            setLockFlag((obj.optInt("lock_action",1)==1)?true:false);
            setNetFlag((obj.optInt("net_action",1)==1)?true:false);
            setLaunchFlag((obj.optInt("launch_action",1)==1)?true:false);
            setStatus((obj.optInt("status")==1)?true:false);
        } catch (JSONException e) {
            MLog.e(TAG, e.toString());
        }
    }

    public String toJson(){
        JSONObject json = new JSONObject();
        try {
            json.put("workerid", getWorkid());
            json.put("channelid", getCid());
            json.put("pid", getPid());
            json.put("topapp_enter_action", isAppEnterFlag()?1:0);
            json.put("topapp_exit_action", isAppExitFlag()?1:0);
            json.put("lock_action", isLockFlag()?1:0);
            json.put("net_action", isNetFlag()?1:0);
            json.put("launch_action", isLaunchFlag()?1:0);
            json.put("status", isStatus()?1:0);
            json.put("app_count", getAppCount());
            json.put("app_interval", getAppInterval());
            return json.toString();
        } catch (JSONException e) {
            MLog.e(TAG, e.toString());
        }
        return null;
    }

    private String workid;
    private String cid;
    private String pid;
    private boolean lockFlag;
    private boolean appEnterFlag;
    private boolean appExitFlag;
    private boolean netFlag;
    private boolean launchFlag;
    private int appCount;
    private long appInterval;
    private boolean status;

    public String getWorkid() {
        return workid;
    }

    public void setWorkid(String workid) {
        this.workid = workid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public boolean isLockFlag() {
        return lockFlag;
    }

    public void setLockFlag(boolean lockFlag) {
        this.lockFlag = lockFlag;
    }

    public boolean isAppEnterFlag() {
        return appEnterFlag;
    }

    public void setAppEnterFlag(boolean appEnterFlag) {
        this.appEnterFlag = appEnterFlag;
    }

    public boolean isAppExitFlag() {
        return appExitFlag;
    }

    public void setAppExitFlag(boolean appExitFlag) {
        this.appExitFlag = appExitFlag;
    }

    public boolean isNetFlag() {
        return netFlag;
    }

    public void setNetFlag(boolean netFlag) {
        this.netFlag = netFlag;
    }

    public boolean isLaunchFlag() {
        return launchFlag;
    }

    public void setLaunchFlag(boolean launchFlag) {
        this.launchFlag = launchFlag;
    }

    public int getAppCount() {
        return appCount;
    }

    public void setAppCount(int appCount) {
        this.appCount = appCount;
    }

    public long getAppInterval() {
        return appInterval;
    }

    public void setAppInterval(long appInterval) {
        this.appInterval = appInterval;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
