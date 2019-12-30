package com.walker.dd.service;

import android.content.Context;

import com.walker.dd.core.service.SharedPreferencesServiceImpl;

import com.walker.mode.*;


public    class NowUser {
    public static Context context;
    public static boolean login = false;
    public static boolean logining = false;
    public static boolean offline = false;
    public static boolean loginauto = false;

    public static UserSocket getUser(){
        UserSocket user = new UserSocket();
        user.setId(getId());
        user.setName(getName());
        return user;
    }
    public static UserSocket getDd(){
        UserSocket user = new UserSocket();
        user.setId(Key.DD);
        user.setName(Key.DD);
        return user;
    }
    public static void setOffline(Boolean offline){
        NowUser.offline = offline;
    }
    public static void setLogin(Boolean login){
        NowUser.login = login;
        if(login){
            setOffline(false);
            setAutoLogin(true);
        }else{
            setAutoLogin(false);
        }
    }
    public static Boolean isLogining(){
        return NowUser.logining;
    }
    public static void setLogining(Boolean login){
        NowUser.logining = login;
    }
    public static Boolean isLogin(){
        return NowUser.login;
    }
    public static Boolean isOffline(){
        return NowUser.offline;
    }

    public static void setAutoLogin(Boolean login){
        SharedPreferencesServiceImpl.put(context, Key.AUTO, login+"");
    }
    public static Boolean isAutoLogin(){
        return Boolean.valueOf(SharedPreferencesServiceImpl.get(context, Key.AUTO, "false"));
    }

    public static void setId(String id) {
        SharedPreferencesServiceImpl.put(context, Key.ID, id);
    }

    public static void setName(String name) {
        SharedPreferencesServiceImpl.put(context, Key.NAME, name);
    }

    public static void setPwd(String pwd) {
        SharedPreferencesServiceImpl.put(context, Key.PWD, pwd);
    }

    public static void setProfile(String profile) {
        SharedPreferencesServiceImpl.put(context, Key.PROFILE, profile);
    }

    public static String getId() {
        return SharedPreferencesServiceImpl.get(context, Key.ID, "000");
    }

    public static String getName() {
        return SharedPreferencesServiceImpl.get(context, Key.NAME, "nobody");
    }
    public static String getName(String name) {
        return SharedPreferencesServiceImpl.get(context, Key.NAME, name);
    }

    public static String getPwd() {
        return SharedPreferencesServiceImpl.get(context, Key.PWD, "");
    }

    public static String getProfile() {
        return SharedPreferencesServiceImpl.get(context, Key.PROFILE, "");
    }
}
