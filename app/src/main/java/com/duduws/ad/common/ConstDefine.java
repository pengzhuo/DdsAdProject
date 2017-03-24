package com.duduws.ad.common;

import java.util.HashMap;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/2/21 19:45
 */

public class ConstDefine {
    private static final String TAG = "ConstDefine";

    /**
     * 广告SDK索引集合(名称 ---> 索引)
     */
    public static HashMap<String, Integer> AD_INDEX_MAP = new HashMap<String, Integer>();

    /**
     * 广告SDK索引集合(索引 ---> 名称)
     */
    public static HashMap<Integer, String> AD_INDEX_MAP_EX = new HashMap<Integer, String>();

    public static void init(){
        AD_INDEX_MAP.put("facebook", DSP_CHANNEL_FACEBOOK);
        AD_INDEX_MAP.put("admob", DSP_CHANNEL_ADMOB);
        AD_INDEX_MAP.put("cm", DSP_CHANNEL_CM);
        AD_INDEX_MAP.put("ddsad", DSP_CHANNEL_DDS);
        AD_INDEX_MAP.put("gdt", DSP_CHANNEL_GDT);

        AD_INDEX_MAP_EX.put(DSP_CHANNEL_FACEBOOK, "facebook");
        AD_INDEX_MAP_EX.put(DSP_CHANNEL_ADMOB, "admob");
        AD_INDEX_MAP_EX.put(DSP_CHANNEL_CM, "cm");
        AD_INDEX_MAP_EX.put(DSP_CHANNEL_DDS, "ddsad");
        AD_INDEX_MAP_EX.put(DSP_CHANNEL_GDT, "gdt");
    }

    /**
     * 单个广告出现的最大次数
     */
    public static final int APP_COUNT_SITE_DEFAULT = 10;

    /**
     * 全局广告出现次数
     */
    public static final int APP_COUNT_GLOABL_DEFAULT = 20;

    /**
     * 自有广告产品编号
     */
    public static final String DDS_APPID = "10001";
    /**
     * 自有广告产品密钥
     */
    public static final String DDS_APPSECRET = "";

    /**
     * 服务销毁重新启动服务
     */
    public static final int SERVICE_RESTART_SELF = 1;

    /**
     * 网络变化启动服务
     */
    public static final int SERVICE_RESTART_NET = 2;

    /**
     * 其他启动服务
     */
    public static final int SERVICE_RESTART_OTHER = 3;

    /**
     * 主逻辑服务类完整路径
     */
    public static final String ACTION_MAIN_SERVICE = "com.duduws.ads.service.AdService";
    /**
     * 连接网络ACTION
     */
    public static final String ACTION_ALARM_NETWORK = "android.intent.action.alarm.duduws.network";

    /**
     * 更新RecentApp ACTION
     */
    public static final String ACTION_ALARM_RECENT_APP = "android.intent.action.alarm.duduws.recent_app";

    /**
     * 心跳ACTION
     */
    public static final String ACTION_ALARM_HEART = "android.intent.action.alarm.duduws.heart";

    /**
     * 重启服务ACTION
     */
    public static final String ACTION_RESTART_SERVER = "android.intent.action.duduws.restart";

    /**
     * 停止服务ACTION
     */
    public static final String ACTION_STOP_SERVER = "android.intent.action.duduws.stop";

    /**
     * 超时时间  单位：秒
     */
    public static final int NET_SOCKET_TIMEOUT = 60;

    /**
     * 默认连接服务器时间间隔  单位：秒
     */
    public static final long NET_CONN_TIME_DEFAULT = 6*3600;

    /**
     * 服务器通信密钥
     */
    public static final String XXTEA_KEY = "8.W2{kQfo?9?Dm)rbLh9";

//    /**
//     * 服务器地址
//     */
//    public static final String SERVER_URL = "http://c.swork.us/gateway.php?mod=api&file=gps";
//
//    /**
//     * 心跳请求地址
//     */
//    public static final String SERVER_URL_HEART = "http://c.swork.us/gateway.php?mod=api&file=user";
//
//    /**
//     * 自有广告请求地址
//     */
//    public static final String SERVER_SELF_AD_URL = "http://c.swork.us/gateway.php?mod=api&file=ownad";
//
//    /**
//     * 统计日志请求地址
//     */
//    public static final String SERVER_LOG_URL = "http://c.swork.us/gateway.php?mod=api&file=statistics";
    private static final String IP = "http://192.168.44.68:8080";
    public static final String SERVER_URL = IP + "/gateway.php?mod=api&file=gps";
    public static final String SERVER_URL_HEART = IP + "/gateway.php?mod=api&file=user";
    public static final String SERVER_SELF_AD_URL = IP + "/gateway.php?mod=api&file=ownad";
    public static final String SERVER_LOG_URL = IP + "/gateway.php?mod=api&file=statistics";

    /**
     * 服务器错误码定义  成功
     */
    public static final int SERVER_RES_SUCCESS = 1000;

    /**
     * 服务器错误码定义  设备在被屏敝列表中
     */
    public static final int SERVER_RES_DEVICE_BE_MASK =  1002;

    /**
     * 服务器错误码定义  渠道屏敝
     */
    public static final int SERVER_RES_CHANNEL_BE_MASK = 1003;

    /**
     * 服务器错误码定义  添加用户信息失败
     */
    public static final int SERVER_RES_ADD_USER_FAIL = 1004;

    /**
     * 广告触发类型  解锁
     */
    public static final int TRIGGER_TYPE_UNLOCK = 1;

    /**
     * 广告触发类型  网络变化
     */
    public static final int TRIGGER_TYPE_NETWORK = 2;

    /**
     * 广告触发类型  进入APP
     */
    public static final int TRIGGER_TYPE_APP_ENTER = 3;

    /**
     * 广告触发类型  退出APP
     */
    public static final int TRIGGER_TYPE_APP_EXIT = 4;

    /**
     * 广告触发类型  其他
     */
    public static final int TRIGGER_TYPE_OTHER = 5;

    /**
     * 广告事件  请求广告
     */
    public static final int AD_RESULT_REQUEST = 1;

    /**
     * 广告事件  请求广告成功
     */
    public static final int AD_RESULT_SUCCESS = 2;

    /**
     * 广告事件  请求广告失败
     */
    public static final int AD_RESULT_FAIL = 3;

    /**
     * 广告事件  展示广告
     */
    public static final int AD_RESULT_SHOW = 4;

    /**
     * 广告事件  点击广告
     */
    public static final int AD_RESULT_CLICK = 5;

    /**
     * 广告事件 关闭广告
     */
    public static final int AD_RESULT_CLOSE = 6;

    /**
     * 解锁触发偏移
     */
    public static final int OFFSET_TRIGGER_VALUE_UNLOCK = 1000;

    /**
     * 网络触发偏移
     */
    public static final int OFFSET_TRIGGER_VALUE_NETWORK = 2000;

    /**
     * APP进入触发偏移
     */
    public static final int OFFSET_TRIGGER_VALUE_APPENTER = 3000;

    /**
     * APP退出触发偏移
     */
    public static final int OFFSET_TRIGGER_VALUE_APPEXIT = 4000;

    /**
     * 默认全局编号
     */
    public static final int DSP_GLOABL = -1;

    /**
     * Facebook 渠道编号
     */
    public static final int DSP_CHANNEL_FACEBOOK = 1;

    /**
     * Admob 渠道编号
     */
    public static final int DSP_CHANNEL_ADMOB = 2;

    /**
     * 猎豹CM 渠道编号
     */
    public static final int DSP_CHANNEL_CM = 3;

    /**
     * 嘟嘟
     */
    public static final int DSP_CHANNEL_DDS = 4;

    /**
     * 广点通
     */
    public static final int DSP_CHANNEL_GDT = 5;

    /**
     * Facebook 渠道编号
     */
    public static final int DSP_CHANNEL_FACEBOOK_NATIVE = 11;

    /**
     * Admob 渠道编号
     */
    public static final int DSP_CHANNEL_ADMOB_NATIVE = 12;

    /**
     * 猎豹CM 渠道编号
     */
    public static final int DSP_CHANNEL_CM_NATIVE = 13;

    /**
     * 嘟嘟
     */
    public static final int DSP_CHANNEL_DDS_NATIVE = 14;

    /**
     * 广点通
     */
    public static final int DSP_CHANNEL_GDT_NATIVE = 15;

    /**
     * Facebook 渠道编号
     */
    public static final int DSP_CHANNEL_FACEBOOK_VIDEO = 21;

    /**
     * Admob 渠道编号
     */
    public static final int DSP_CHANNEL_ADMOB_VIDEO = 22;

    /**
     * 猎豹CM 渠道编号
     */
    public static final int DSP_CHANNEL_CM_VIDEO = 23;

    /**
     * 嘟嘟
     */
    public static final int DSP_CHANNEL_DDS_VIDEO = 24;

    /**
     * 广点通
     */
    public static final int DSP_CHANNEL_GDT_VIDEO = 25;

    /**
     * 插屏广告类型
     */
    public static final int AD_TYPE_SDK_SPOT = 1;

    /**
     * Banner广告类型
     */
    public static final int AD_TYPE_SDK_BANNER = 2;
}
