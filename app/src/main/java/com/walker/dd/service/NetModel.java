package com.walker.dd.service;

import com.walker.dd.activity.Application;
import com.walker.dd.core.service.SharedPreferencesServiceImpl;

public class NetModel {
    /**
     * socket服务器
     */
    public static String ipSockets[] = {"192.168.43.245", "39.106.111.11"};
    public static int portSockets[] = {8093, 8093};
    public static int ccSocket = 1;

    /**
     * http服务器
     */
    public static String ipWebs[] = {"192.168.43.245", "39.106.111.11"};
    public static int portWebs[] = {8080, 8090};
    public static int ccWeb = 1;

    public static boolean conn = false;
    public static boolean isConn(){
        return conn;
    }
    public static void setConn(Boolean conn){
        NetModel.conn = conn;
    }

    public static String getServerSocketIp(){
        return SharedPreferencesServiceImpl.get(Application.context, "IP_SOCKET", ipSockets[ccSocket]);
    }
    public static void setServerSocketIp(String ip){
        SharedPreferencesServiceImpl.put(Application.context, "IP_SOCKET", ip);
    }

    public static int getServerSocketPort(){
        return Integer.valueOf(SharedPreferencesServiceImpl.get(Application.context, "PORT_SOCKET", portSockets[ccSocket] + ""));
    }
    public static void setServerSocketPort(int port){
        SharedPreferencesServiceImpl.put(Application.context, "PORT_SOCKET", port + "");
    }

    public static String getServerWebUrl(){
        return "http://"  + getServerWebIp() + ":" + getServerWebPort() ;
    }
    public static int getServerWebPort(){
        return Integer.valueOf(SharedPreferencesServiceImpl.get(Application.context, "PORT_WEB", portWebs[ccWeb] + ""));
    }
    public static String getServerWebIp(){
        return SharedPreferencesServiceImpl.get(Application.context, "IP_WEB", ipWebs[ccWeb] + "");
    }
    public static void setServerWebIp(String ip){
        SharedPreferencesServiceImpl.put(Application.context, "IP_WEB", ip);
    }
    public static void setServerWebPort(int port){
        SharedPreferencesServiceImpl.put(Application.context, "PORT_WEB", port + "");
    }

    /**
     http://127.0.0.1:8090/file/upload.do
     */
    public static String httpUpload(){
        return getServerWebUrl() +  "/file/upload.do";
    }

    /**
     http://127.0.0.1:8090/file/download.do?key=undefined
     */
    public static String httpDownload(String id){
        return getServerWebUrl() + "/file/download.do"+"?key=" + id;
    }












}
