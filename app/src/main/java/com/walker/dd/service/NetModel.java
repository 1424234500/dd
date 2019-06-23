package com.walker.dd.service;

import com.walker.dd.util.MySP;

public class NetModel {
    /**
     * socket服务器
     */
    public static String ipSocket = "39.106.111.11";
//    public static String ipSocket = "192.168.43.245";
    public static int portSocket = 8093;

    /**
     * http服务器
     */
    public static String ipWeb = "192.168.43.245";
    public static int portWeb = 8080;

    public static boolean conn = false;
    public static boolean isConn(){
        return conn;
    }
    public static void setConn(Boolean conn){
        NetModel.conn = conn;
    }

    public static String getServerSocketIp(){
        return MySP.get(NowUser.context, "IP_SOCKET", ipSocket);
    }
    public static void setServerSocketIp(String ip){
        MySP.put(NowUser.context, "IP_SOCKET", ip);
    }

    public static int getServerSocketPort(){
        return Integer.valueOf(MySP.get(NowUser.context, "PORT_SOCKET", portSocket + ""));
    }
    public static void setServerSocketPort(int port){
        MySP.put(NowUser.context, "PORT", port + "");
    }


    public static int getServerWebPort(){
        return Integer.valueOf(MySP.get(NowUser.context, "PORT_WEB", portWeb + ""));
    }
    public static String getServerWebIp(){
        return MySP.get(NowUser.context, "IP_WEB", ipWeb + "");
    }
    /**
     * //服务器http上传地址"/walker-web/file/uploadCmf.do";
     * @return
     */
    public static String httpUpload(){
        return "http://"+NetModel.getServerWebIp() + ":" + NetModel.getServerWebPort() + "/walker-web/file/uploadCmf.do";
    }

    public static String makeProfileById(String id){
        return "http://"+NetModel.getServerWebIp() + ":" + NetModel.getServerWebPort() + "/walker-web/file/download.do"+"?ID=" + id + ".png";
    }












}
