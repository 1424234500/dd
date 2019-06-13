package com.walker.dd.service;

import android.content.Context;

import com.walker.dd.util.MySP;
import com.walker.socket.server_1.Key;

public    class User{
    public static Context context;


    public static void setId(String id) {
        MySP.put(context, Key.ID, id);
    }

    public static void setUser(String user) {
        MySP.put(context, Key.USER, user);
    }

    public static void setPwd(String pwd) {
        MySP.put(context, Key.PWD, pwd);
    }

    public static void setProfile(String profile) {
        MySP.put(context, Key.PROFILE, profile);
    }

    public static String getId() {
        return MySP.get(context, Key.ID, "");
    }

    public static String getUser() {
        return MySP.get(context, Key.USER, "");
    }
    public static String getUser(String user) {
        return MySP.get(context, Key.USER, user);
    }

    public static String getPwd() {
        return MySP.get(context, Key.PWD, "");
    }

    public static String getProfile() {
        return MySP.get(context, Key.PROFILE, "");
    }
}
