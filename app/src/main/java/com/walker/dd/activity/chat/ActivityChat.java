package com.walker.dd.activity.chat;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.walker.common.util.Bean;
import com.walker.common.util.JsonUtil;
import com.walker.common.util.TimeUtil;
import com.walker.core.encode.Pinyin;
import com.walker.dd.R;
import com.walker.dd.activity.AcBase;
import com.walker.dd.adapter.*;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.dd.util.MySP;
import com.walker.dd.util.RobotAuto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ActivityChat extends AcBase {
    SwipeRefreshLayout srl;

    //type <user,group>,toid id,username,profilepath,nickname,name,   msg,time,status <在线,离线>
    List<Bean> listItemMsg = new ArrayList<>();
    ListView lv;
    adapter.AdapterLvChat adapter;

    EditText etsend;
    //NAME TEXT TIME
    Bean acData;
    
    
    public void OnCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_chat);

        srl =findViewById(R.id.srl);
        lv = findViewById(R.id.lv);
        adapter = new adapter.AdapterLvChat(this, listItemMsg);
        lv.setAdapter( adapter);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                switch(scrollState){
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        AndroidTools.out("listview 空闲，开始加载图片");
//                        NetImage.resume(getContext());
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING://滚动状态
                        AndroidTools.out("listview 滚动，暂停加载图片");
//                        NetImage.pause(getContext());
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://触摸后滚动

                        break;
                }
            }
            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

            }
        });
//        lv.setOnItemClickListener(this);
//        lv.setOnItemLongClickListener(this);

//        /设置刷新时动画的颜色，可以设置4个
        srl.setColorSchemeResources(Constant.SRLColors);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AndroidTools.toast(getContext(), "refresh");
                Bean bean = new Bean()
                        .set("TYPE", "text")
                        .set("SELF", ((int)(Math.random() * 10)) % 2 == 0)
                        .set("USERNAME", "self")
                        .set("PROFILE", "")
                        .set("TIME", TimeUtil.getTimeYmdHms())
                        .set("TEXT", "refresh")
                ;
                sendSocket("echo", bean);
                //TYPE<text,voice,photo,file>
                //SELF<true,false>

                //USERNAME
                //PROFILE
                //TIME

                //TEXT
                srl.setRefreshing(false);
            }
        });

        etsend = findViewById(R.id.etsend);

        this.acData = AndroidTools.getMapFromIntent(this.getIntent());
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
        Bean bean = JsonUtil.get(msg);
        Bean data = bean.get("data", new Bean());
        data.set("SELF", false);
        addMsg(data);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tvsend){
            String str = etsend.getText().toString();

            if(str.length() > 0){
                Bean bean = new Bean()
                        .set("TYPE", "text")
                        .set("SELF", true)
                        .set("USERNAME", MySP.get(getContext(), "user", "self"))
                        .set("PROFILE", "")
                        .set("TIME", TimeUtil.getTimeYmdHms())
                        .set("TEXT", str)
                        ;

                addMsg(bean);
//            NAME TEXT
//            ID KEY
//            {type:message,to:"all_socket",from:222,data:{type:txt,body:hello} }
               sendSocket("message",acData.get("NAME", ""), new Bean().set("type", "txt").set("body", str));

               sendAuto(str);


               etsend.setText("");

           }

        }
    }

    /**
     * handler处理器
     *
     * @param type
     * @param msg
     */
    @Override
    public void OnHandler(String type, String msg) {
        super.OnHandler(type, msg);
        if(type.equals("auto")){
            Bean bean = new Bean()
                    .set("TYPE", "text")
                    .set("SELF", false)
                    .set("USERNAME", "dd")
                    .set("PROFILE", "")
                    .set("TIME", TimeUtil.getTimeYmdHms())
                    .set("TEXT", msg)
                    ;
            addMsg(bean);
        }

    }

    /**
     * 自动应答
     */
    private void sendAuto(String str){

        String url = RobotAuto.TENCENT_URL;
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody requestBody = RobotAuto.getTencentParamRequest(str, "dd");
//                out(url, requestBody);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log( "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                out("onResponse", str);
                String res=RobotAuto.parseTencentRes(str);
                out(res);
                sendHandler("auto", res);
            }
        });
    }
    private void addMsg(Bean bean) {
        listItemMsg.add(bean);
        lv.setSelection(listItemMsg.size());	//选中最新一条，滚动到底部
    }


}
