package com.duduws.ad.listener;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/1/10 11:52
 */

public interface AdListener {
    public void onError(int code);
    public void onLoaded();
    public void onClicked();
    public void onDisplayed();
    public void onDismissed();
}
