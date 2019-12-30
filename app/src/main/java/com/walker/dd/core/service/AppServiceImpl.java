package com.walker.dd.core.service;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.walker.dd.core.AndroidTools;

/**
 * 应用信息管理
 */
public class AppServiceImpl {
    static Cache cache = Cache.getInstance();


    public static String getBaiduTextScan(Context context){
        String key = "baidu_url_text_scan";
        String res = cache.get(key, "");
        if(res.length() == 0){
            res = getMetaValue(context, key);
            cache.set(key, res);
        }
        return res;
    }
    public static String getBaiduApiKey(Context context){
        String key = "baidu_api_key";
        String res = cache.get(key, "");
        if(res.length() == 0){
            res = getMetaValue(context, key);
            cache.set(key, res);
        }
        return res;
    }
    public static String getBaiduSecretKey(Context context){
        String key = "baidu_secret_key";
        String res = cache.get(key, "");
        if(res.length() == 0){
            res = getMetaValue(context, key);
            cache.set(key, res);
        }
        return res;
    }

    /**
     * 获取AndroidManifest.xml键值配置
     *
      */
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            AndroidTools.out(e);
        }
        return apiKey;
    }

}
