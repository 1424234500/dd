package com.walker.dd.activity;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import com.walker.common.util.Tools;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.socket.client.Client;
import com.walker.socket.client.ClientNetty;
import com.walker.socket.client.OnSocket;


public class Application extends android.app.Application implements OnSocket {
    LocalBroadcastManager localBroadcastManager;	//本地的activity广播机制
    NotificationManager notificationManager;    //推送栏广播

    /**
     * socket
     */
    Client client;
    AsyncTask<Void, String, Void> taskInitSocket = new AsyncTask<Void, String, Void>() {
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                String ip = "39.106.111.11";
                int port = 8093;

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
        out("socket send", jsonstr);
        if(client != null && client.isStart()) {
            client.send(jsonstr);
        }else{
            out("socket no connect");
        }
    }

    @Override
    public void onSend(String s, String s1) {
        out("socket onSend " , s, s1);
    }

    @Override
    public void onConnect(String s) {
        out( "socket onConnect", s);
    }

    @Override
    public void onDisconnect(String s) {
        out("socket onDisconnect", s);
    }


	@Override
	public void onCreate() {
		super.onCreate();
		
		// 初始化全局变量
		
		showSystemInfo();
		taskInitSocket.execute();


        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);//推送栏广播
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

    }



    public void showSystemInfo(){
    	ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
		int memorySize = activityManager.getMemoryClass();
    	
    	out("设备内存限制:" + memorySize);
    }


}