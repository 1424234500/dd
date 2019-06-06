package com.walker.dd.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.walker.common.util.Bean;
import com.walker.common.util.Tools;
import com.walker.dd.database.BaseDao;
import com.walker.dd.database.BaseDaoImpl;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.view.DialogBeats;

public abstract class AcBase extends AppCompatActivity implements View.OnClickListener{
    /**
     * 生命周期
     */
    public abstract   void OnCreate(Bundle savedInstanceState);
    public    void OnStart(){}//111111
    public    void OnResume(){}//回来唤醒

    public    void OnPause(){} //离开暂停
    public    void OnStop(){} //1111111
    public    void OnDestroy(){}//finish销毁

    /**
     * 回退键处理，返回false则执行finish，否则不处理
     */
    public  abstract boolean OnBackPressed();

    /**
     * 收到广播处理
     */
    public abstract void OnReceive(String msg);



    public Context getContext(){
        return this;
    }

    //统一注册广播
    LocalBroadcastManager localBroadcastManager;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String msg = intent.getExtras().getString("msg");
                log("BaseAc.receive." + msg);
                OnReceive(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    //数据库处理
    public BaseDao sqlDao ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //registerReceiver(broadcastReceiver, new IntentFilter(MSGTYPE.broadcastUrl));
        //registerReceiver(mBroadcastReceiver, intentFilter);
        //注册应用内广播接收器
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter("cc.broadcast.client"));
        sqlDao = new BaseDaoImpl(this);

        OnCreate(savedInstanceState);
    }
    @Override
    protected void onStart() {
        super.onStart();
        OnStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        OnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        OnPause();
    }
    @Override
    protected void onStop() {
        OnStop();
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        OnDestroy();
        //unregisterReceiver(mBroadcastReceiver);
        //取消注册应用内广播接收器
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        //此处只是关闭当前ac，不会关闭net service socket
        super.onDestroy();
    }



    private static DialogBeats beatsDialog;
    public void loadingStart( ) {
        beatsDialog = new DialogBeats(this);
        //beatsDialog.init();
        beatsDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingStop();
            }
        }, 6000);
        beatsDialog.setCancelable(true);
        beatsDialog.setCancelable(false);
    }
    public void loadingStop() {
        if (beatsDialog != null) {
            if (beatsDialog.isShowing()) {
                beatsDialog.dismiss();
                beatsDialog = null;
            }
        }

    }
    public void sendSocket(String plugin, Bean data){
        ((Application)getApplication()).send(new Bean().set("type", plugin).set("data", data).toString());
    }


    public void toast(Object...objects){
        log(objects);
        Toast.makeText(this, Tools.objects2string(objects), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if(!OnBackPressed()){
            super.onBackPressed();
        }
    }

    public void log(Object...objects){
        AndroidTools.out(this.getClass().getName() + "." + Tools.objects2string(objects));
    }



}
