package com.walker.dd.activity.other;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.walker.common.util.JsonUtil;
import com.walker.common.util.Tools;
import com.walker.dd.R;
import com.walker.dd.activity.AcBase;
import com.walker.dd.service.NetModel;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.dd.util.OkHttpUtil;
import com.walker.socket.client.Client;
import com.walker.socket.client.ClientNetty;
import com.walker.socket.client.OnSocket;
import com.walker.socket.server_1.Key;
import com.walker.socket.server_1.MsgBuilder;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class ActivityTestOkhttp3 extends Activity implements View.OnClickListener {
    private EditText ettop;
    private TextView tvout;
    private EditText etsend;

    private SwipeRefreshLayout srl;
    private ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_okhttp3);

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
        final String url = ettop.getText().toString();
        final String data = etsend.getText().toString();
        out(url, data);

        try {
            switch (v.getId()) {
                case R.id.get:
                    OkHttpUtil.get(url, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            out("failure", e.toString());
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            out(response.headers());
                            out(response.body().toString());
                        }
                    });

                    break;
                case R.id.post:
                    OkHttpUtil.post(url, JsonUtil.get(data), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            out(response.headers());
                            out(response.body().toString());
                        }
                    });
                    break;
                case R.id.upload:
                    String savePath1 = Constant.getFilePathByKey(data);
                    out("upload", url, savePath1);
                    OkHttpUtil.upload(url, savePath1, "", new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            out(e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            out(response.headers());
                            out(response.body().toString());
                        }
                    });
                    break;
                case R.id.download:
                    String savePath = Constant.getFilePathByKey(data);
                    out("download", url, savePath);
                    OkHttpUtil.download(url, savePath, new OkHttpUtil.OnHttp() {
                        @Override
                        public void onOk(Call call, Response response) {
                            out("ok");
                            out(response.headers());
                        }

                        @Override
                        public void onLoading(int progress) {
                            out("onLoading", progress);
                        }

                        @Override
                        public void onError(Throwable e) {
                            out("onError", e.toString());
                        }
                    });
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

}
