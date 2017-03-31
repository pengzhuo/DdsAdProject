package com.duduws.ad.net;

import android.content.Context;
import android.text.TextUtils;

import com.duduws.ad.analytics.DdsLogUtils;
import com.duduws.ad.common.ConfigDefine;
import com.duduws.ad.common.ConstDefine;
import com.duduws.ad.common.MacroDefine;
import com.duduws.ad.log.MLog;
import com.duduws.ad.model.AdModel;
import com.duduws.ad.model.ProductModel;
import com.duduws.ad.model.SiteModel;
import com.duduws.ad.utils.AdsPreferences;
import com.duduws.ad.utils.DspHelper;
import com.duduws.ad.utils.FuncUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/7/30 11:34
 */
public class NetManager {
    private static final String TAG = "NetManager";
    private static NetManager instance;
    private Context context;

    private NetManager(Context context) {
        this.context = context;
    }

    public static NetManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetManager(context);
        }
        return instance;
    }

    /**
     * 拼装请求信息
     * @return
     */
    private static JSONObject getRequestInfo(){
        if (ConfigDefine.userInfo != null){
            JSONObject jsonObject = new JSONObject();
            JSONObject userInfo = ConfigDefine.userInfo.toSimpleJsonObj();
            if (userInfo != null){
                try {
                    jsonObject.put("base", userInfo);
                    return jsonObject;
                } catch (JSONException e) {
                    MLog.e(TAG, e.toString());
                }
            } else {
                MLog.w(TAG, "getRequestInfo UserModel toSimpleJsonObj return null !");
            }
        } else {
            MLog.w(TAG, "getRequestInfo ConfigDefine toSimpleJsonObj is null !");
        }
        return null;
    }

    /**
     * 拼装心跳信息
     * @return
     */
    private static JSONObject getHeartInfo(){
        if (ConfigDefine.userInfo != null){
            JSONObject jsonObject = new JSONObject();
            JSONObject userInfo = ConfigDefine.userInfo.toJsonObj();
            if (userInfo != null){
                try {
                    jsonObject.put("base", userInfo);
                    return jsonObject;
                } catch (JSONException e) {
                    MLog.e(TAG, e.toString());
                }
            } else {
                MLog.w(TAG, "getHeartInfo UserModel toJsonObj return null !");
            }
        } else {
            MLog.w(TAG, "getHeartInfo ConfigDefine userInfo is null !");
        }
        return null;
    }

    /**
     * 请求服务器
     */
    public void startRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (FuncUtils.hasActiveNetwork(context)) {
                    JSONObject jsonObject = getRequestInfo();
                    if (jsonObject != null) {
                        String str = new String(Base64.encode(XXTea.encrypt(jsonObject.toString().getBytes(), ConstDefine.XXTEA_KEY.getBytes())));
                        String response = NetHelper.sendPost(ConstDefine.SERVER_URL, str);
                        if (!TextUtils.isEmpty(response)) {
                            try {
                                response = new String(XXTea.decrypt(Base64.decode(response.toCharArray()), ConstDefine.XXTEA_KEY.getBytes()));
                                parseRequest(response);
                            } catch (Exception e) {
                                MLog.e(TAG, e.toString());
                            }
                        } else {
                            MLog.w(TAG, "startRequest server return null !");
                        }
                    }
                } else {
                    MLog.e(TAG, "startRequest have no network !");
                }
            }
        }).start();
    }

    /**
     * 解析服务器返回数据
     * @param response
     */
    private void parseRequest(String response) {
//        MLog.i(TAG, response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject == null){
                return;
            }
            //解析服务器返回状态码
            int resCode = jsonObject.optInt("code");
            if (resCode == ConstDefine.SERVER_RES_SUCCESS) {
                //重置屏敝标志
                if (AdsPreferences.getInstance(context).getBoolean(MacroDefine.MACRO_AD_MASK_FLAG, false)){
                    AdsPreferences.getInstance(context).setBoolean(MacroDefine.MACRO_AD_MASK_FLAG, false);
                }
                //设置下次联网时间
                DspHelper.setNextNetConTime(context, System.currentTimeMillis() + ConfigDefine.DEFAULT_CONN_NET_TIME*1000);
                //设置本地配置信息标志
                AdsPreferences.getInstance(context).setString(MacroDefine.MACRO_LOCAL_CONFIG_FLAG, "success");
                //清除本地缓存
                DspHelper.resetLocalConfig(context);
                //解析全局控制参数
                if (!jsonObject.isNull("product")){
                    JSONObject productObj = jsonObject.optJSONObject("product");
                    ProductModel productModel = new ProductModel();
                    productModel.initWithJson(productObj.toString());
                    ConfigDefine.productInfo = productModel;
                    DspHelper.setProductInfo(context, ConfigDefine.productInfo);
                }else{
                    AdsPreferences.getInstance(context).setBoolean(MacroDefine.MACRO_AD_MASK_FLAG, true);
                }
                //解析单个SITE控制参数
                ConfigDefine.DDS_ARR.clear();
                ConfigDefine.DDS_AD_MAP.clear();
                if (!jsonObject.isNull("site")){
                    ArrayList<SiteModel> lockList = new ArrayList<>();
                    ArrayList<SiteModel> netList = new ArrayList<>();
                    ArrayList<SiteModel> appEnterList = new ArrayList<>();
                    ArrayList<SiteModel> appExitList = new ArrayList<>();
                    JSONArray siteArray = jsonObject.optJSONArray("site");
                    for (int i=0; i<siteArray.length(); i++){
                        JSONObject siteObj = siteArray.optJSONObject(i);
                        SiteModel siteModel = new SiteModel();
                        siteModel.initWithJson(siteObj.toString());
                        int channel = ConstDefine.AD_INDEX_MAP.get(siteModel.getSdkName());
                        channel += DspHelper.getTriggerOffSet(siteModel.getTriggerType());
                        ConfigDefine.DDS_ARR.put(channel, siteModel);
                        switch (siteModel.getTriggerType()){
                            case ConstDefine.TRIGGER_TYPE_APP_ENTER:
                                appEnterList.add(siteModel);
                                break;
                            case ConstDefine.TRIGGER_TYPE_APP_EXIT:
                                appExitList.add(siteModel);
                                break;
                            case ConstDefine.TRIGGER_TYPE_NETWORK:
                                netList.add(siteModel);
                                break;
                            case ConstDefine.TRIGGER_TYPE_UNLOCK:
                                lockList.add(siteModel);
                                break;
                        }
                        DspHelper.setSiteInfoWithChannel(context, channel, siteModel);
                    }
                    ConfigDefine.DDS_AD_MAP.put(ConstDefine.TRIGGER_TYPE_UNLOCK, lockList);
                    ConfigDefine.DDS_AD_MAP.put(ConstDefine.TRIGGER_TYPE_NETWORK, netList);
                    ConfigDefine.DDS_AD_MAP.put(ConstDefine.TRIGGER_TYPE_APP_ENTER, appEnterList);
                    ConfigDefine.DDS_AD_MAP.put(ConstDefine.TRIGGER_TYPE_APP_EXIT, appExitList);
                }
                //黑名单
                if (!jsonObject.isNull("blackList")) {
                    JSONArray pkgArray = jsonObject.optJSONArray("blackList");
                    String bblistString = "";
                    for (int i=0; i< pkgArray.length(); i++) {
                        try {
                            JSONObject bl = pkgArray.getJSONObject(i);
                            String pkgname = bl.optString("pkg");
                            bblistString += pkgname + ", ";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    AdsPreferences.getInstance(context).setString(MacroDefine.MACRO_BB_LIST_STRING, bblistString);
                }
                //白名单
                if (!jsonObject.isNull("whiteList")) {
                    JSONObject whiteApps = jsonObject.optJSONObject("whiteList");
                    JSONArray whiteArr = whiteApps.optJSONArray("apps");
                    if (whiteArr != null) {
                        String recentApp = FuncUtils.getRecentAppString(context);
                        MLog.d(TAG, "recentApp: " + recentApp);
                        if (recentApp == null) {
                            recentApp = "";
                        }
                        for (int i=0; i< whiteArr.length(); i++) {
                            JSONObject object = whiteArr.optJSONObject(i);
                            if (object != null) {
                                String pkgname = object.optString("pkg");
                                if (!TextUtils.isEmpty(pkgname)) {
                                    if (!recentApp.contains(pkgname)) {
                                        recentApp += pkgname + ", ";
                                    }
                                }
                            }
                        }
                        MLog.d(TAG, "current app info: " + recentApp);
                        FuncUtils.setRecentAppString(context, recentApp);
                    }else{
                        MLog.d(TAG, "apps is null!");
                    }
                }
            } else if (resCode == ConstDefine.SERVER_RES_CHANNEL_BE_MASK || resCode == ConstDefine.SERVER_RES_DEVICE_BE_MASK) {
                MLog.i(TAG, "be mask [" + resCode + "]");
                AdsPreferences.getInstance(context).setBoolean(MacroDefine.MACRO_AD_MASK_FLAG, true);
            } else {
                MLog.i(TAG, "server return error [" + resCode + "]");
            }
        } catch (JSONException e) {
            MLog.e(TAG, e.toString());
        }
    }

    /**
     * 向服务器发送心跳
     */
    public void startHeart(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (FuncUtils.hasActiveNetwork(context)) {
                    JSONObject jsonObject = getHeartInfo();
                    if (jsonObject != null) {
                        String str = new String(Base64.encode(XXTea.encrypt(jsonObject.toString().getBytes(), ConstDefine.XXTEA_KEY.getBytes())));
                        String response = NetHelper.sendPost(ConstDefine.SERVER_URL_HEART, str);
                        if (!TextUtils.isEmpty(response)) {
                            try {
                                response = new String(XXTea.decrypt(Base64.decode(response.toCharArray()), ConstDefine.XXTEA_KEY.getBytes()));
                                parseHeart(response);
                            } catch (Exception e) {
                                MLog.e(TAG, e.toString());
                            }
                        } else {
                            MLog.w(TAG, "startHeart server return null !");
                        }
                    }
                } else {
                    MLog.e(TAG, "startHeart have no network !");
                }
            }
        }).start();
    }

    /**
     * 解析心跳返回
     * @param response
     */
    private void parseHeart(String response) {
//        MLog.i(TAG, response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject == null) {
                return;
            }
            //解析服务器返回状态码
            int resCode = jsonObject.optInt("code");
            if (resCode == ConstDefine.SERVER_RES_SUCCESS) {
                MLog.e(TAG, "add user info success !");
            }else if (resCode == ConstDefine.SERVER_RES_ADD_USER_FAIL){
                MLog.e(TAG, "add user info fail !");
            }else{
                MLog.e(TAG, "aad user info tail other reason ! " + resCode);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 从服务器拉取广告
     * @param number  数量
     * @return
     */
    public void getAdInfoFromServer(int number){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appid", ConfigDefine.DDS_APPID);
            jsonObject.put("secret", ConfigDefine.DDS_APPSECRET);
            jsonObject.put("num", number);
            String reqStr = new String(Base64.encode(XXTea.encrypt(jsonObject.toString().getBytes(), ConstDefine.XXTEA_KEY.getBytes())));
            String response = NetHelper.sendPost(ConstDefine.SERVER_SELF_AD_URL, reqStr);
            if (!TextUtils.isEmpty(response)){
                response = new String(XXTea.decrypt(Base64.decode(response.toCharArray()), ConstDefine.XXTEA_KEY.getBytes()));
//                MLog.e(TAG, "res " + response);
                JSONObject obj = new JSONObject(response);
                int code = obj.optInt("code");
                if (code == ConstDefine.SERVER_RES_SUCCESS && !obj.isNull("ads")){
                    JSONArray arr = obj.optJSONArray("ads");
                    ArrayList<AdModel> list = new ArrayList<>();
                    for (int i=0; i<arr.length(); i++){
                        JSONObject _obj = arr.optJSONObject(i);
                        AdModel adModel = new AdModel();
                        adModel.initWithJson(_obj.toString());
                        list.add(adModel);
                    }
                    ConfigDefine.ARR_LIST_ADS = list;
                }
            }
            if (ConfigDefine.ARR_LIST_ADS == null || ConfigDefine.ARR_LIST_ADS.size() <= 0){
                ConfigDefine.AD_LISTENER.onError(-1);
            }else{
                ConfigDefine.AD_LISTENER.onLoaded();
            }
        } catch (Exception e) {
            MLog.e(TAG, e.toString());
            ConfigDefine.AD_LISTENER.onError(-2);
        }
    }

    /**
     * 发送日志到服务器
     * @param message
     */
    public void sendLogToServer(final String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (FuncUtils.hasActiveNetwork(context)) {
                    if (!TextUtils.isEmpty(message)) {
                        String str = new String(Base64.encode(XXTea.encrypt(message.getBytes(), ConstDefine.XXTEA_KEY.getBytes())));
                        String response = NetHelper.sendPost(ConstDefine.SERVER_LOG_URL, str);
                        if (!TextUtils.isEmpty(response)) {
                            try {
                                response = new String(XXTea.decrypt(Base64.decode(response.toCharArray()), ConstDefine.XXTEA_KEY.getBytes()));
                                MLog.d(TAG, "sendLogToServer response " + response);
                            } catch (Exception e) {
                                MLog.e(TAG, e.toString());
                            }
                        } else {
                            MLog.w(TAG, "startHeart server return null !");
                        }
                    }
                } else {
                    MLog.e(TAG, "startHeart have no network !");
                }
            }
        }).start();
    }
}
