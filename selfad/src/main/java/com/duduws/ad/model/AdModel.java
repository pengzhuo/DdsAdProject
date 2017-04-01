package com.duduws.ad.model;

import com.duduws.ad.log.MLog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 2017/1/6 22:26
 */

public class AdModel {
    private static final String TAG = "AdModel";

    public AdModel(){

    }

    public void initWithJson(String jsonStr){
        try {
            JSONObject json = new JSONObject(jsonStr);
            setId(json.optInt("id"));
            setTitle(json.optString("title"));
            setDesc(json.optString("desc"));
            setIcon(json.optString("icon"));
            setImg_bg(json.getString("img_bg"));
            setImg_body(json.optString("img_body"));
            setType(json.optInt("type"));
            setUrl(json.optString("url"));
            setVersion(json.optInt("version"));
            setProbability(json.optInt("probability"));
        } catch (JSONException e) {
            MLog.e(TAG, e.toString());
        }
    }

    public String toJson(){
        StringBuffer sb = new StringBuffer();
        sb.append("{\"id\":").append(getId()).append(",")
            .append("\"title\":\"").append(getTitle()).append("\",")
            .append("\"desc\":\"").append(getDesc()).append("\",")
            .append("\"icon\":\"").append(getIcon()).append("\",")
            .append("\"img_bg\":\"").append(getImg_bg()).append("\",")
            .append("\"img_body\":\"").append(getImg_body()).append("\",")
            .append("\"type\":").append(getType()).append(",")
            .append("\"url\":\"").append(getUrl()).append("\",")
            .append("\"version\":").append(getVersion()).append(",")
            .append("\"probability\":").append(getProbability())
            .append("}");

        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getImg_bg() {
        return img_bg;
    }

    public void setImg_bg(String img_bg) {
        this.img_bg = img_bg;
    }

    public String getImg_body() {
        return img_body;
    }

    public void setImg_body(String img_body) {
        this.img_body = img_body;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }

    private int id;
    private String title;
    private String desc;
    private String icon;
    private String img_bg;
    private String img_body;
    private int type;
    private String url;
    private int version;
    private int probability;
}
