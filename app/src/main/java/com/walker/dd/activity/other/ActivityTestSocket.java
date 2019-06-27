package com.walker.dd.activity.other;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.dd.R;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.socket.client.Client;
import com.walker.socket.client.ClientNetty;
import com.walker.socket.client.OnSocket;
import com.walker.socket.server_1.Key;
import com.walker.socket.server_1.MsgBuilder;

public class ActivityTestSocket extends Activity implements View.OnClickListener, OnSocket {
    private EditText ettop;
    private TextView tvout;
    private EditText etsend;

    private SwipeRefreshLayout srl;
    private ScrollView sv;

    Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_socket);

        ettop = (EditText) this.findViewById(R.id.ettop);
        tvout = (TextView)this.findViewById(R.id.tvout);
        etsend = (EditText)this.findViewById(R.id.etsend);


        sv = (ScrollView) findViewById(R.id.sv);

        srl = (SwipeRefreshLayout)findViewById(R.id.srl);
        //设置刷新时动画的颜色，可以设置4个
        srl.setColorSchemeResources(Constant.SRLColors);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tvout.setText("clear");
                srl.setRefreshing(false);
            }
        });
//        tvout.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    public void onClick(View v) {

        try {
            switch (v.getId()) {
                case R.id.conn:
                    if (client == null || !client.isStart()) {
                        String[] str = ettop.getText().toString().split(" +");
                        client = new ClientNetty(str[0], Integer.valueOf(str[1]));
                        client.setOnSocket(this);
                        client.start();
                    } else {
                        client.stop();
                        client = null;
                    }
                    break;
                case R.id.login:
                    etsend.setText(MsgBuilder.makeLogin("test", TimeUtil.getTimeYmdHmss()).toString());
                    break;
                case R.id.tvsend:
                    if (client != null && client.isStart()) {
                        client.send(etsend.getText().toString());
                    } else {
                        out("么有建立长连接");
                    }
                    break;
                case R.id.session:
                    etsend.setText(MsgBuilder.makeSession("session").toString());
                    break;
                case R.id.auto:
                    etsend.setText(MsgBuilder.makeMessageTo(Key.ALL_USER, "{type:txt,body:hello}").toString());

                    break;
                case R.id.other:
                    etsend.setText(MsgBuilder.makeMessageTo(Key.ALL_SOCKET, "{type:txt,body:hello}").toString());
                    break;

            }
        }catch (Exception e){
            out(e.toString());
            out(Tools.toString(e));
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

            tvout.append("\n" + res);
            if(tvout.length() > 40000){
                tvout.setText("clear");
            }
//            tvout.setScrollY(999999);
            sv.scrollTo(sv.getBottom(), 0);

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
