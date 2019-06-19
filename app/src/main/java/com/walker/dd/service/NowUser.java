package com.walker.dd.service;

import android.content.Context;

import com.walker.dd.util.MySP;
import com.walker.socket.server_1.Key;
import com.walker.socket.server_1.session.User;

public    class NowUser {
    public static Context context;
    public static boolean login = false;
    public static boolean offline = false;
    public static boolean loginauto = false;

    public static User getUser(){
        User user = new User();
        user.setId(getId());
        user.setName(getName());
        return user;
    }
    public static User getDd(){
        User user = new User();
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
    public static Boolean isLogin(){
        return NowUser.login;
    }
    public static Boolean isOffline(){
        return NowUser.offline;
    }

    public static void setAutoLogin(Boolean login){
        MySP.put(context, Key.AUTO, login+"");
    }
    public static Boolean isAutoLogin(){
        return Boolean.valueOf(MySP.get(context, Key.AUTO, "false"));
    }

    public static void setId(String id) {
        MySP.put(context, Key.ID, id);
    }

    public static void setName(String name) {
        MySP.put(context, Key.NAME, name);
    }

    public static void setPwd(String pwd) {
        MySP.put(context, Key.PWD, pwd);
    }

    public static void setProfile(String profile) {
        MySP.put(context, Key.PROFILE, profile);
    }

    public static String getId() {
        return MySP.get(context, Key.ID, "000");
    }

    public static String getName() {
        return MySP.get(context, Key.NAME, "nobody");
    }
    public static String getName(String name) {
        return MySP.get(context, Key.NAME, name);
    }

    public static String getPwd() {
        return MySP.get(context, Key.PWD, "");
    }

    public static String getProfile() {
        return MySP.get(context, Key.PROFILE, "");
    }
}
