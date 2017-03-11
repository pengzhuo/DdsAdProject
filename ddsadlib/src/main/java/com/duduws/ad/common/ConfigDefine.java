package com.duduws.ad.common;

import android.graphics.Bitmap;

import com.duduws.ad.listener.AdListener;
import com.duduws.ad.model.AdModel;
import com.duduws.ad.model.ProductModel;
import com.duduws.ad.model.SiteModel;
import com.duduws.ad.model.UserModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/2/21 19:16
 */

public class ConfigDefine {
    /**
     * 用户信息
     */
    public static UserModel userInfo = null;

    /**
     * 产品信息
     */
    public static ProductModel productInfo = null;

    /**
     * 广告集合
     */
    public static HashMap<Integer, ArrayList<SiteModel>> DDS_AD_MAP = new HashMap<>();

    /**
     * 广告索引集合
     */
    public static HashMap<Integer, SiteModel> DDS_ARR = new HashMap<>();

    /**
     * 公共图片
     */
    public static Bitmap bm_bg = null;
    public static Bitmap bm_body = null;
    public static Bitmap bm_icon = null;
    /**
     * 广告集合
     */
    public static ArrayList<AdModel> ARR_LIST_ADS = null;

    /**
     * 广告回调监听
     */
    public static AdListener AD_LISTENER = null;

    /**
     * 版本
     */
    public static String APP_VERSION = "";

    /**
     * 渠道号ID
     */
    public static String APP_CHANNEL_ID	= "c501a";

    /**
     * 合作方ID
     */
    public static String APP_COOPERATION_ID	= "";

    /**
     * 产品ID
     */
    public static String APP_PRODUCT_ID	= "p501a";

    /**
     * 友盟AppKey
     */
    public static String APP_KEY_UMENG = "";

    /**
     * 友盟渠道编号
     */
    public static String APP_CHANNEL_UMENG = "";

    /**
     * FB产品编号
     */
    public static String FB_APPID = "";
}
