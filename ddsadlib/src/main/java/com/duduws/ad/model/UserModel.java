package com.duduws.ad.model;

import android.content.Context;
import android.os.Build;

import com.duduws.ad.common.ConfigDefine;
import com.duduws.ad.log.MLog;
import com.duduws.ad.utils.FuncUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/2/22 10:02
 */

public class UserModel {
    private static final String TAG = "UserModel";

    private Context mContext;

    public UserModel(Context context){
        mContext = context;
        init();
    }

    private void init(){
        setImei(FuncUtils.getIMEI(mContext));
        setIp(FuncUtils.getHostIP());
        setModel(Build.MODEL);
        setOs(Build.VERSION.RELEASE);
        setNetwork(FuncUtils.getNetWorkType(mContext));
        setArea(Locale.getDefault().getCountry());
        setVersion(ConfigDefine.APP_VERSION);
        setProductid(ConfigDefine.APP_PRODUCT_ID);
        setApps(FuncUtils.getAppList(mContext));
        setChannelid(ConfigDefine.APP_CHANNEL_ID);
    }

    public String toJson(){
        JSONObject json = toJsonObj();
        if (json != null){
            return json.toString();
        }else{
            return null;
        }
    }

    public JSONObject toJsonObj(){
        JSONObject json = new JSONObject();
        try {
            json.put("imei", getImei());
            json.put("ip", getIp());
            json.put("model", getModel());
            json.put("os", getOs());
            json.put("network", getNetwork());
            json.put("area", getArea());
            json.put("cid", getChannelid());
            json.put("version", getVersion());
            json.put("pid", getProductid());
            json.put("apps", getApps());
            return json;
        } catch (JSONException e) {
            MLog.e(TAG, e.toString());
        }
        return null;
    }

    public JSONObject toSimpleJsonObj(){
        JSONObject json = new JSONObject();
        try {
            json.put("imei", getImei());
            json.put("model", getModel());
            json.put("area", getArea());
            json.put("cid", getChannelid());
            json.put("version", getVersion());
            json.put("pid", getProductid());
            return json;
        } catch (JSONException e) {
            MLog.e(TAG, e.toString());
        }
        return null;
    }

    private String imei;
    private String ip;
    private String model;
    private String os;
    private String network;
    private String area;
    private String channelid;
    private String version;
    private String productid;
    private String apps;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getChannelid() {
        return channelid;
    }

    public void setChannelid(String channelid) {
        this.channelid = channelid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getApps() {
        return apps;
    }

    public void setApps(String apps) {
        this.apps = apps;
    }
}
