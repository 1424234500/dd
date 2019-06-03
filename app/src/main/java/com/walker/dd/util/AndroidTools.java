package com.walker.dd.util;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.walker.common.util.Tools;

public class AndroidTools {

    public static void systemVoiceToast(Context context) {
        RingtoneManager.getRingtone(context.getApplicationContext(),
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).play();
    }


    public static void post(View view, Runnable run) {
        view.postDelayed(run, 100);
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static boolean fileExist(String str) {
        File f = new File(str);
        return f.exists() && f.isFile() && f.length() > 10;
    }


    public static String getLocalIp(Context c) {
        WifiManager wifiManager = (WifiManager) c
                .getSystemService(c.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();
        // 返回整型地址转换成“*.*.*.*”地址
        return String.format("%d.%d.%d.%d", (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
    }



    public static void toast(Context c, String str){
        Toast.makeText(c, str, Toast.LENGTH_SHORT).show();
        log("toast." + str);
    }
    public static void out(Object...objects) {
        String str = Tools.objects2string(objects);
        Log.e("tools", ""+ str.length() + "." + str);
    }

    public static void log(String str) {
        Log.e("tools.logs", str);
    }
    public static void life(String str) {
        Log.e("tools.life", str);
    }
    public static void tip(String str) {
        Log.e("tools.tip", str);
    }
    public static void list(String str) {
        Log.e("tools.list", str);
    }
}
