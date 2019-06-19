package com.walker.dd.activity;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.walker.common.util.Bean;
import com.walker.core.database.BaseDao;
import com.walker.dd.database.BaseDaoImpl;
import com.walker.dd.service.LoginModel;
import com.walker.dd.service.MsgModel;
import com.walker.dd.service.NowUser;
import com.walker.dd.service.SessionModel;
import com.walker.dd.service.SocketModel;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.socket.client.Client;
import com.walker.socket.client.ClientNetty;
import com.walker.socket.client.OnSocket;
import com.walker.socket.server_1.Key;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.plugin.Plugin;

import java.io.File;


public class Application extends android.app.Application implements OnSocket {
    LocalBroadcastManager localBroadcastManager;	//本地的activity广播机制
    NotificationManager notificationManager;    //推送栏广播

    Bean onConn = new Bean().put(Key.TYPE, Key.SOCKET).put(Msg.KEY_STATUS, 0);
    Bean onDisConn = new Bean().put(Key.TYPE, Key.SOCKET).put(Msg.KEY_STATUS, 1);

    /**
     * socket
     */
    Client client;
    AsyncTask<Void, String, Void> taskInitSocket = new AsyncTask<Void, String, Void>() {
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                String ip = SocketModel.getServerIp();
                int port = SocketModel.getServerPort();

                client = new ClientNetty(ip, port);
                client.setOnSocket(Application.this);
                client.start();
            }catch (Exception e){
                e.printStackTrace();
                onDisconnect(e.toString());
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    };

    @Override
    public String out(Object... objects) {
        AndroidTools.out(objects);
        return "";
    }

    @Override
    public void onRead(String socket, String jsonstr) {
        out("onRead", socket, jsonstr);
        localBroadcastManager.sendBroadcast(new Intent(Constant.BROAD_URL).putExtra(Constant.BROAD_KEY, jsonstr)); //发送应用内广播
//        sendBroadcast(new Intent(Constant.BROAD_URL).putExtra(Constant.BROAD_KEY, jsonstr)); //发送应用内广播

    }
    public void send(String jsonstr){
        try {
            out("socket send", jsonstr);
            if (client != null && client.isStart()) {
                client.send(jsonstr);
            } else {
                out("socket no connect");
            }
        }catch (Exception e){
            out(e);
        }
    }

    @Override
    public void onSend(String s, String s1) {
//        out("socket onSend " , s, s1);
    }

    @Override
    public void onConnect(String s) {
        out( "socket onConnect", s);
        localBroadcastManager.sendBroadcast(new Intent(Constant.BROAD_URL).putExtra(Constant.BROAD_KEY, onConn.toString())); //发送应用内广播

    }

    @Override
    public void onDisconnect(String s) {
        out("socket onDisconnect", s);
        localBroadcastManager.sendBroadcast(new Intent(Constant.BROAD_URL).putExtra(Constant.BROAD_KEY, onDisConn.toString())); //发送应用内广播
    }


	@Override
	public void onCreate() {
		super.onCreate();

		// 初始化全局变量
		showSystemInfo();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);//推送栏广播
        localBroadcastManager = LocalBroadcastManager.getInstance(this);


        initSocket();
        NowUser.context = getApplicationContext();

        //初始化sharedPreference
        initSP();
        //初始化数据库表
        initDatabaseTable();
        initFileDir();
        //初始化时启动网络后台服务
        AndroidTools.log("App.oncreate");
        //逻辑处理，若没有登陆账号则跳转到 登陆界面
    }
    public void initSocket(){
        taskInitSocket.execute();
    }

    /**
     * 初始化
     */
    public void initSP(){
        String user = NowUser.getName();
        if(user.length() > 0){
            AndroidTools.toast(getApplicationContext(), "当前用户 " + user);
        }
    }
    public void initFileDir(){
//		public static final String root = Environment.getExternalStorageDirectory() + "/mycc/";
//		public static final String dirVoice = root + "record/";
//		public static final String dirPhoto =  root + "photo/";
//		public static final String dirFile =  root + "file/";
//		public static final String dirCamera = root +  "camera/";
//		public static final String dirProfile = root +  "profile/";
//		public static final String dirProfileWall = root +  "profilewall/";
        String dirs[] =  {Constant.dirVoice, Constant.dirPhoto, Constant.dirFile,
                Constant.dirCamera, Constant.dirProfile, Constant.dirProfileWall  };
        for(String str: dirs){
            File file = new File(str);
            if(!file.exists()){
                file.mkdirs();
            }
        }


    }



    //初始化数据库表
    public void initDatabaseTable(){
        BaseDao sqlDao = new BaseDaoImpl(this);
        //sqlDao.execSQL("drop table login_user");
        sqlDao.executeSql(LoginModel.SQL_LOGIN_USER);
        sqlDao.executeSql(MsgModel.SQL_MSG);
        sqlDao.executeSql(SessionModel.SQL_SESSION);



    }


    public void showSystemInfo(){
    	ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
		int memorySize = activityManager.getMemoryClass();
    	
    	out("设备内存限制:" + memorySize);
    }


}