package com.walker.dd.activity.other;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.common.util.HttpBuilder;
import com.walker.common.util.HttpUtil;
import com.walker.common.util.Tools;
import com.walker.core.exception.InfoException;
import com.walker.dd.R;
import com.walker.dd.activity.AcBase;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.RobotTuling;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


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
        tietOut.append(Tools.objects2string(objects));
        if(tietOut.length() > 40000){
            tietOut.setText("clear");
        }
    }

    //离线模式专用handler异步刷新界面
    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            String res = b.getString("res");
            Bean mapd = new Bean();
            mapd.put("USERNAME", "CC");
            mapd.put("TYPE", "text");	//类型，文本，语音，图片，文件
            mapd.put("PROFILE", "http://img03.tooopen.com/images/20131111/sy_46708898917.jpg");
            mapd.put("FROMID", "");
            mapd.put("TOID", "");
            mapd.put("TIME", Tools.getNowTimeS());
            mapd.put("TEXT", res) ;
//            listChatMsg.add(listChatMsg.size(), mapd);

            OnReceive(res);

            AndroidTools.log("robot res=" + res);
        }
    };

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.send){

            String url = tietIpPort.getText().toString();
            final String msg = tietMsg.getText().toString();

            out("send ", msg);
            String h = RobotTuling.getHttp() + msg;
            String res = "";
            try {
                res = HttpUtil.doGet(h, null, null, null);
                res=RobotTuling.parseMsg(res);
            } catch (Exception e){
                out(res);
            }
            try {
                res = HttpUtil.doPost(h, null, null, null);
                res=RobotTuling.parseMsg(res);
            } catch (Exception e){
                out(res);
            }
            try {
                res = HttpUtil.doPut(h, null, null, null);
                res=RobotTuling.parseMsg(res);
            } catch (Exception e){
                out(res);
            }
            try {
                res = HttpUtil.doGet(h, null, null, null);
                res=RobotTuling.parseMsg(res);
            } catch (Exception e){
                out(res);
            }
            try {
                res = HttpUtil.doDelete(h, null, null, null);
                res=RobotTuling.parseMsg(res);
            } catch (Exception e){
                out(res);
            }



            //离线模式专用开启线程来发起网络请求,知道为何 okhttp 和网上通用httpUtil都无法获取结果
            new Thread(new Runnable(){
                @Override
                public void run() {
                    HttpURLConnection connection=null;
                    try {
                        String res;
                        URL url=new URL(RobotTuling.getHttp() + msg);
                        connection =(HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        java.io.InputStream in=connection.getInputStream();
                        //下面对获取到的输入流进行读取
                        java.io.BufferedReader reader=new java.io.BufferedReader(new java.io.InputStreamReader(in));
                        StringBuilder response=new StringBuilder();
                        String line;
                        while((line=reader.readLine())!=null){
                            response.append(line);
                        }

                        res=RobotTuling.parseMsg(response.toString());

                        Message message = new Message();
                        Bundle b = new Bundle();
                        b.putString("res", res);
                        message.setData(b);
                        handler.sendMessage(message);
                    } catch(Exception e){
                        e.printStackTrace();
//                        out(e.toString());
                        out(Tools.toString(e));
                    }finally{
                        if(connection!=null){
                            connection.disconnect();
                        }
                    }
                }

            }).start();

        }

    }
}
