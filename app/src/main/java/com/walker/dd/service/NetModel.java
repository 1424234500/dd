package com.walker.dd.service;

import com.walker.common.util.RobotUtil;
import com.walker.dd.util.MySP;
import com.walker.dd.util.RobotAuto;

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
        return MySP.get(NowUser.context, "IP_SOCKET", ipSockets[ccSocket]);
    }
    public static void setServerSocketIp(String ip){
        MySP.put(NowUser.context, "IP_SOCKET", ip);
    }

    public static int getServerSocketPort(){
        return Integer.valueOf(MySP.get(NowUser.context, "PORT_SOCKET", portSockets[ccSocket] + ""));
    }
    public static void setServerSocketPort(int port){
        MySP.put(NowUser.context, "PORT", port + "");
    }


    public static int getServerWebPort(){
        return Integer.valueOf(MySP.get(NowUser.context, "PORT_WEB", portWebs[ccWeb] + ""));
    }
    public static String getServerWebIp(){
        return MySP.get(NowUser.context, "IP_WEB", ipWebs[ccWeb] + "");
    }
    /**
     * //服务器http上传地址"/walker-web/file/uploadCmf.do";
     * @return
     */
    public static String httpUpload(){
        return "http://"+NetModel.getServerWebIp() + ":" + NetModel.getServerWebPort() + "/walker-web/file/uploadCmf.do";
    }

    public static String httpDownload(String id){
        return "http://"+NetModel.getServerWebIp() + ":" + NetModel.getServerWebPort() + "/walker-web/file/downloadRe.do"+"?KEY=" + id;
    }












}
