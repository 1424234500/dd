package com.walker.dd.activity.other;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.dd.R;
import com.walker.dd.util.AndroidTools;
import com.walker.socket.client.Client;
import com.walker.socket.client.ClientNetty;
import com.walker.socket.client.OnSocket;

public class ActivityTestSocket extends AppCompatActivity implements View.OnClickListener, OnSocket {
    TextInputEditText tietIpPort;
    TextView tietOut;
    TextInputEditText tietMsg;
    Button bconn;


    Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_socket);

        bconn = this.findViewById(R.id.conn);
        bconn.setOnClickListener(this);

        tietIpPort = this.findViewById(R.id.tietIpPort);
        tietOut = this.findViewById(R.id.tietOut);
        tietMsg = this.findViewById(R.id.tietMsg);

//        tietOut.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.conn:
                if(client == null || !client.isStart()) {
                    String[] str = tietIpPort.getText().toString().split(" +");
                    client = new ClientNetty(str[0], Integer.valueOf(str[1]));
                    client.setOnSocket(this);
                    client.start();
                }else{
                    client.stop();
                    client = null;
                }
                break;
            case R.id.login:
                tietMsg.setText("{type:login,data:{user:78,pwd:123456} }");
                break;
            case R.id.send:
                client.send(tietMsg.getText().toString());
                break;
            case R.id.session:
                tietMsg.setText("{type:monitor,data:{type:show} }");
                break;
            case R.id.auto:
                tietMsg.setText("{type:message,to:\"all_user\",from:222,data:{type:txt,body:hello} }");

                break;
            case R.id.other:
                tietMsg.setText("{type:message,to:\"all_socket\",from:222,data:{type:txt,body:hello} }");

                break;

        }


    }
    public String out(Object...objects){
        String str = Tools.objects2string(objects);
        Message message = new Message();
        Bundle b = new Bundle();
        b.putString("res", str);
        message.setData(b);
        handler.sendMessage(message);
        return str;
    }
    //handler异步刷新界面
    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            String res = b.getString("res");

            tietOut.append("\n" + res);
            if(tietOut.length() > 40000){
                tietOut.setText("clear");
            }
            tietOut.setScrollY(999999);
            AndroidTools.log("" + res);
        }
    };

    @Override
    public void onRead(String s, String s1) {
        out("收到", s1);
        sendBroadcast(new Intent("111").putExtra("msg", s1)); //发送应用内广播
    }

    @Override
    public void onSend(String s, String s1) {
        out("发送", s1);
    }

    @Override
    public void onConnect(String s) {
        out("建立socket", s);
    }

    @Override
    public void onDisconnect(String s) {
        out("断开socket", s);
    }
}
