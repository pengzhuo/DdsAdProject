package com.duduws.ad.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.WindowManager;

import com.duduws.ad.common.MacroDefine;
import com.duduws.ad.log.MLog;
import com.duduws.ad.model.PackageElement;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/2/21 19:15
 */

public class FuncUtils {
    private static final String TAG = "FuncUtils";
    private static final int TOTAL_RECENT_APPS = 13;
    private static final String PREFS_FILE_NAME = "recent_apps";
    private static final String PREFS_KEY_RECENT = "recent";

    /**
     * 获取当前APP应用列表
     * @param context
     * @return
     */
    public static ArrayList<PackageElement> getRecentApps(Context context) {
        String recentApp = updateRecentApp(context);
        MLog.d("RecentTasksHelper.getRecentApps recentApp: " + recentApp);
        if (TextUtils.isEmpty(recentApp)) {
            return null;
        }
        String[] recents = recentApp.split(",");
        if (recents == null || recents.length < 1) {
            return null;
        }
        MLog.d("RecentTasksHelper.getRecentApps recentApp: " + recents.toString());
        int total = 0;
        PackageManager pm = context.getPackageManager();
        ArrayList<PackageElement> rts = new ArrayList<PackageElement>();
        for (int i=recents.length-1; i>=0; i--) {
            String recent = recents[i];
            if (TextUtils.isEmpty(recent)) {
                continue;
            }
            if (total >= TOTAL_RECENT_APPS) {
                break;
            }
            String pkgname = recent.trim();
            try {
                PackageElement pe = new PackageElement();
                ApplicationInfo info = pm.getApplicationInfo(pkgname, 0);
                String name = pm.getApplicationInfo(pkgname, 0).loadLabel(pm).toString();
                pe.setLabel(name);
                pe.setmIcon(info.loadIcon(pm));
                pe.setmPackageName(pkgname);
                pe.setmIsNative(false);
                rts.add(pe);
                total++;
            } catch (Exception e) {
                MLog.e(TAG, e.toString());
            }
        }
        return rts;
    }

    /**
     * 更新当前APP列表信息
     * @param context
     * @return
     */
    public static String updateRecentApp(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE_NAME, 0);
        String recentApp = prefs.getString(PREFS_KEY_RECENT, "");
        MLog.d("RecentTasksHelper.updateRecentApp begin, recentApp: "+recentApp);
        List<String> recentList = getRecentTaskList(context);
        if (recentList != null && recentList.size() > 0) {
            String bbList = AdsPreferences.getInstance(context).getString(MacroDefine.MACRO_BB_LIST_STRING, "");
            for (int i=0; i< recentList.size(); i++) {
                String pkgname = recentList.get(i);
                if (bbList.contains(pkgname)) {
                    if (recentApp.contains(pkgname)) {
                        recentApp = recentApp.replace(pkgname, "");
                    }
                    continue;
                }
                if (TextUtils.isEmpty(pkgname) || recentApp.contains(pkgname)) {
                    MLog.d("AdsReceiver.updateRecentApp pkgname empty or exists");
                    continue;
                }
                recentApp += pkgname + ", ";

            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREFS_KEY_RECENT, recentApp);
            editor.commit();
        }
        return recentApp;
    }

    /**
     * 获取当前系统运行的APP列表
     * @param context
     * @return
     */
    public static List<String> getRecentTaskList( Context context ) {
        List<String> rts = new ArrayList<String>();
        try {
            ActivityManager am = (ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE );
            List<ActivityManager.RecentTaskInfo> list = am.getRecentTasks(64, 0);
            PackageManager pm  = context.getPackageManager();
            String pkgName = "";
            for(ActivityManager.RecentTaskInfo task : list ){
                ComponentName cn = task.origActivity;
                if( cn != null ){
                    pkgName = cn.getPackageName();
                    if (!TextUtils.isEmpty(pkgName) && !isSystemApp(context, pkgName)) {
                        rts.add(pkgName);
                        continue;
                    }
                }
                Intent baseIntent = task.baseIntent;
                ResolveInfo ri = pm.resolveActivity( baseIntent, 0 );
                if( ri != null ){
                    pkgName = ri.activityInfo.packageName;
                    if (!TextUtils.isEmpty(pkgName) && !isSystemApp(context, pkgName)) {
                        rts.add(pkgName);
                        continue;
                    }
                }
            }
        } catch (SecurityException e) {
            MLog.e(TAG, e.toString());
        }
        return rts;
    }

    /**
     * 获取当前使用过的APP信息
     * @param context
     * @return
     */
    public static String getRecentAppString(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE_NAME, 0);
        String recentApp = prefs.getString(PREFS_KEY_RECENT, "");
        if (TextUtils.isEmpty(recentApp)) {
            recentApp = updateRecentApp(context);
        }
        return recentApp;
    }

    /**
     * 更新当前使用过的APP信息
     * @param context
     * @param recentApp
     */
    public static void setRecentAppString(Context context, String recentApp) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_KEY_RECENT, recentApp);
        editor.commit();
    }

    /**
     * 启动so守护进程
     * @param context
     * @param clsName
     */
    public static void startDaemon(final Context context, final String clsName) {
        String executable = "libhelper.so";
        String aliasfile = "helper";
        NativeRuntime.getInstance().RunExecutable(context.getPackageName(), executable, aliasfile, context.getPackageName() + "/" + clsName);

        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NativeRuntime.getInstance().startService(context.getPackageName() + "/" + clsName, createRootPath(context));
                } catch (Exception e) {
                    MLog.e(TAG, e.toString());
                }
            }
        })).start();
    }

    /**
     * 获取格式化的日期
     * @param timeInMillis
     * @return
     */
    public static String getFormattedTime(long timeInMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date curDate = new Date(timeInMillis);
        String str = formatter.format(curDate);
        return str.toString();
    }

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean hasActiveNetwork(Context context) {
        ConnectivityManager mConnectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 检查网络连接，如果无网络可用，就不需要进行连网操作等
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 得到手机的IMEI号, 需要context参数, 获取不到时, 返回本机MAC地址或自定义序列号
     */
    public static String getIMEI(Context context) {
        String imei = "";
        try {
            if (context != null) {
                TelephonyManager mTelephonyMgr = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                imei = mTelephonyMgr.getDeviceId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return TextUtils.isEmpty(imei) ? getLocalMacAddress(context) : imei;
    }

    /**
     * 获取本机MAC地址
     * @param context
     * @return
     */
    public static String getLocalMacAddress(Context context) {
        String mac = "";
        try {
            WifiManager wifi = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            info.getMacAddress().replace(":", "");
        } catch (Exception e) {
            mac = "";
        }

        if (TextUtils.isEmpty(mac)){
            String uuid = AdsPreferences.getInstance(context).getString("DWS_UUID", "-1");
            if (uuid.equalsIgnoreCase("-1")){
                uuid = "DWS_" + UUID.randomUUID().toString();
                AdsPreferences.getInstance(context).setString("DWS_UUID", uuid);
            }
            mac = uuid;
        }

        return mac;
    }

    /**
     * 获取ip地址
     * @return
     */
    public static String getHostIP() {
        String hostIp = "unknow";
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            MLog.i(TAG, e.toString());
        }
        return hostIp;
    }

    /**
     * 获取网络类型
     * @param context
     * @return
     */
    public static String getNetWorkType(Context context){
        String netType = "unknow";
        try{
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectMgr.getActiveNetworkInfo();
            if (info.getType() == ConnectivityManager.TYPE_MOBILE){
                netType = info.getSubtypeName();
            }else{
                netType = info.getTypeName();
            }
        }catch (Exception e){
            MLog.e(TAG, e.toString());
        }
        return netType;
    }

    /**
     * 获取本机APP信息
     * @param context
     * @return
     */
    public static String getAppList(Context context) {
        JSONObject jsonObject = new JSONObject();
        try{
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> packages = pm.getInstalledPackages(0);
            for (PackageInfo packageInfo : packages) {
                // 判断系统/非系统应用
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
                {
                    JSONObject obj = new JSONObject();
                    obj.put("pkgName", packageInfo.packageName);
                    obj.put("appName", packageInfo.applicationInfo.loadLabel(pm).toString());
                    jsonObject.put(packageInfo.applicationInfo.loadLabel(pm).toString(), obj);
                } else {
                    // 系统应用　　　
                }
            }
        }catch (Exception e){
            MLog.e(TAG, e.toString());
        }
        return jsonObject.toString();
    }

    /**
     * 从Manifest文件中获取指定的属性值
     * @param context
     * @param name
     * @return
     */
    public static String getManifestApplicationMetaData(Context context, String name){
        try{
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(name);
        }catch (Exception e){
            MLog.e(TAG, e.toString());
        }
        return null;
    }

    /**
     * 获取顶层APP
     * @param context
     * @return
     */
    public static String appInFront(Context context){
        String str = "";
        if (Build.VERSION.SDK_INT >= 21) {
            Iterator localIterator = AndroidProcesses.getRunningForegroundApps(context).iterator();

            while (localIterator.hasNext())
            {
                AndroidAppProcess localAndroidAppProcess = (AndroidAppProcess)localIterator.next();
                str = localAndroidAppProcess.getPackageName();
                if (TextUtils.isEmpty(str))
                {
                    if (localAndroidAppProcess.foreground) {
                        return str;
                    }
                }
            }
            return str;
        }else{
            ActivityManager mActivityManager  = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> rtList = mActivityManager.getRunningTasks(1);
            if( rtList == null ||
                    rtList.size() == 0 ){
                return "" ;
            }
            ActivityManager.RunningTaskInfo taskInfo = rtList.get(0);
            if (null != taskInfo) {
                String packageName = taskInfo.topActivity.getPackageName();
                return packageName;
            }
            return "";
        }
    }

    /**
     * 检查是否是系统APP
     * @param pkgname
     * @return
     */
    public static boolean isSystemApp(Context context, String pkgname) {
        if (!TextUtils.isEmpty(pkgname)){
            MLog.w(TAG, "isSystemApp check pkgName is null!");
            return false;
        }
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(pkgname, 0);
            // 是系统软件或者是系统软件更新
            if (isSystemApp(pInfo) || isSystemUpdateApp(pInfo)) {
                MLog.i(TAG, pkgname + " is system app !");
                return true;
            } else {
                MLog.i(TAG, pkgname + " is not system app !");
                return false;
            }

        } catch (PackageManager.NameNotFoundException e) {
            StackTraceElement stackTraceElement= e.getStackTrace()[0];
            MLog.e(TAG, "file: " + stackTraceElement.getFileName() +
                        "method: " + stackTraceElement.getMethodName() +
                        "line: " + stackTraceElement.getLineNumber() +
                        "class: " + stackTraceElement.getClassName());
        }
        return false;
    }

    /**
     * 是否为系统APP
     * @param pInfo
     * @return
     */
    private static boolean isSystemApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    /**
     * 是否为系统APP更新
     * @param pInfo
     * @return
     */
    private static boolean isSystemUpdateApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }

    /**
     * 启动指定应用
     * @param context
     * @param packageName
     */
    public static void runApps(Context context, String packageName) {
        if (null == packageName || packageName.length() < 1) {
            return;
        }
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    /**
     * dip单位转换
     */
    public static int getDip(Context context, float size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, context.getResources().getDisplayMetrics());
    }

    /**
     * SD卡是否可用
     * @return
     */
    public static boolean isSdCardAvailable() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 检测服务是否运行
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName){
        boolean isRunning = false;
        if (TextUtils.isEmpty(serviceName)){
            return isRunning;
        }
        final ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : runningServiceInfos){
            if (service.service.getClassName().equalsIgnoreCase(serviceName)){
                if (service.uid == context.getApplicationInfo().uid){
                    isRunning = true;
                    break;
                }
            }
        }
        return isRunning;
    }

    /**
     * 获取网络图片
     * @param url
     * @return
     */
    public static Bitmap getBitMapFromNet(String url){
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            MLog.e(TAG, e.toString());
        }
        return bitmap;
    }

    /**
     * 跳转到指定GP界面
     * @param context
     * @param url
     */
    public static void jumpToGP(Context context, String url){
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    /**
     * 跳转到指定URL界面
     * @param context
     * @param url
     */
    public static void jumpToUrl(Context context, String url){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * 保存图片到指定路径
     * @param path
     * @param fileName
     * @param bm
     */
    public static void saveBitmap(String path, String fileName, Bitmap bm){
        if (bm == null){
            MLog.e(TAG, "BitMap is null! [path:" + path + " filename:" + fileName + "]");
            return;
        }
        File fdir = new File(path);
        if (!fdir.exists()){
            fdir.mkdirs();
        }
        File file = new File(path, fileName);
        if (file.exists()){
            file.delete();
        }
        try {
            FileOutputStream fout = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, fout);
            fout.flush();
            fout.close();
        } catch (FileNotFoundException e) {
            MLog.e(TAG, e.toString());
        } catch (IOException e) {
            MLog.e(TAG, e.toString());
        }
    }

    /**
     * 获取可写路径
     * @param context
     * @return
     */
    public static String getWritePath(Context context){
        return context.getFilesDir().getAbsolutePath();
    }

    /**
     * 创建根路径
     * @param context
     * @return
     */
    public static String createRootPath(Context context) {
        String rootPath = "";
        if (isSdCardAvailable()) {
            // /sdcard/Android/data/<application package>/cache
            rootPath = context.getExternalCacheDir().getPath();
        } else {
            // /data/data/<application package>/cache
            rootPath = context.getCacheDir().getPath();
        }
        return rootPath;
    }

    /**
     * 获取指定路径的图片
     * @param pathString
     * @return
     */
    public static Bitmap getLocalBitmap(String pathString){
        Bitmap bitmap = null;

        try{
            File file = new File(pathString);
            if(file.exists())
            {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e){
            MLog.e(TAG, e.toString());
        }

        return bitmap;
    }

    /**
     * 获取CPU信息
     * @return
     */
    public static String[] getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo={"",""};
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
            MLog.e(TAG, e.getMessage());
        }
        return cpuInfo;
    }

    /**
     * 获取设备分辨率
     * @param context
     * @return
     */
    public static String getDeviceResolution(Context context){
        Point point = new Point();
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealSize(point);
        return point.x + "*" + point.y;
    }
}
