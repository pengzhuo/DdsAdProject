package com.duduws.ad.model;

import com.duduws.ad.log.MLog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/3/2 09:26
 */

public class LogModel {
    private static final String TAG = "LogModel";

    public LogModel(String cid, String site, int adType, int resultType, int type, String imei){
        this.cid = cid;
        this.site = site;
        this.adType = adType;
        this.resultType = resultType;
        this.type = type;
        this.imei = imei;
        this.time = System.currentTimeMillis();
    }

    public JSONObject toJsonObj(){
        JSONObject jsonObject = null;
        if (cid != null && site != null && adType != 0 && resultType != 0 && type != 0){
            try {
                jsonObject = new JSONObject();
                jsonObject.put("cid", cid);
                jsonObject.put("site", site);
                jsonObject.put("adType", adType);
                jsonObject.put("resultType", resultType);
                jsonObject.put("type", type);
                jsonObject.put("imei", imei);
                jsonObject.put("time", time);
            } catch (JSONException e) {
                MLog.e(TAG, e.getMessage());
            }
        } else {
            MLog.w(TAG, "make sure cid, site, adType, resultType, type is not null !");
        }
        return jsonObject;
    }

    private String cid = null;  //渠道编号
    private String site = null;  //广告版位
    private int adType = 1;  //广告类型(banner, spot)
    private int resultType = 0;  //类型(展示广告， 点击广告)
    private int type = 0;  //广告SDK编号
    private String imei = null;  //设备号

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private long time;  //时间戳

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    public int getResultType() {
        return resultType;
    }

    public void setResultType(int resultType) {
        this.resultType = resultType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
