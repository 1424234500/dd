package com.walker.dd.service;

import com.walker.dd.util.MySP;

public class SocketModel {
    public static String ip = "39.106.111.11";
    public static int port = 8093;
    public static boolean conn = false;
    public static boolean isConn(){
        return conn;
    }
    public static void setConn(Boolean conn){
        SocketModel.conn = conn;
    }

    public static String getServerIp(){
        return MySP.get(NowUser.context, "IP", ip);
    }
    public static void setServerIp(String ip){
        MySP.put(NowUser.context, "IP", ip);
    }

    public static int getServerPort(){
        return Integer.valueOf(MySP.get(NowUser.context, "PORT", port + ""));
    }

    public static void setServerPort(int port){
        MySP.put(NowUser.context, "PORT", port + "");
    }


}
