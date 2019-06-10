package com.walker.dd.activity.other;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.common.util.Tools;
import com.walker.dd.R;
import com.walker.dd.activity.AcBase;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.dd.util.RobotAuto;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ActivityTestAuto extends AcBase {

    TextInputEditText tietIpPort;
    TextView tietOut;
    TextInputEditText tietMsg;
    /**
     * 生命周期
     *
     * @param savedInstanceState
     */
    @Override
    public void OnCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_test_auto);

        tietIpPort = this.findViewById(R.id.tietIpPort);
        tietOut = this.findViewById(R.id.tietOut);
        tietMsg = this.findViewById(R.id.tietMsg);

    }

    /**
     * 回退键处理，返回false则执行finish，否则不处理
     */
    @Override
    public boolean OnBackPressed() {
        return false;
    }

    /**
     * 收到广播处理
     *
     * @param msg
     */
    @Override
    public void OnReceive(String msg) {
        out(msg);
    }

    public void out(Object...objects){
        String str = Tools.objects2string(objects);
        Message message = new Message();
        Bundle b = new Bundle();
        b.putString("res", str);
        message.setData(b);
        handler.sendMessage(message);
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
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        String ipport = tietIpPort.getText().toString();
        final String msg = tietMsg.getText().toString();

        if(v.getId() == R.id.sendtuling){
            String url = RobotAuto.getUrlTuling(msg);
            out(url);

            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .get()//默认就是GET请求，可以不写
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    out( "onFailure: ", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String str = response.body().string();
                    out("onResponse", str);
                    String res=RobotAuto.parseTulingRes(str);
                    out(res);
                }
            });
        }else  if(v.getId() == R.id.sendtencent){
            String url = RobotAuto.TENCENT_URL;
            OkHttpClient okHttpClient = new OkHttpClient();

            RequestBody requestBody = RobotAuto.getTencentParamRequest(msg, "dd");
            out(url, requestBody);

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    out( "onFailure: ", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String str = response.body().string();
                    out("onResponse", str);
                    String res=RobotAuto.parseTencentRes(str);
                    out(res);
                }
            });
        }

    }
}
