package com.walker.dd.activity.chat;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.common.util.TimeUtil;
import com.walker.dd.R;
import com.walker.dd.activity.AcBase;
import com.walker.dd.adapter.*;
import com.walker.dd.service.User;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.socket.server_1.Key;
import com.walker.dd.util.MySP;
import com.walker.dd.util.RobotAuto;
import com.walker.dd.view.NavigationBar;
import com.walker.dd.view.NavigationImageView;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.plugin.Plugin;

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

//.set(Key.TYPE, Key.TEXT)
//.set(Key.FROM, User.getId())
//.set(Key.NAME, User.getUser())
//.set(Key.TO, User.getId())
//.set(Key.TONAME, User.getUser())
//.set(Key.PROFILE, "")
//.set(Key.TIME, TimeUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
//.set(Key.TEXT, str)
//.set(Key.FILE, str)
    List<Bean> listItemMsg = new ArrayList<>();
    ListView lv;
    AdapterLvChat adapter;

    EditText etsend;
//            .set(Key.TYPE, Key.TEXT)
//            .set(Key.FROM, msg.getFrom())
//            .set(Key.NAME, msg.getUserFrom())
//            .set(Key.TEXT, data.get(Key.TEXT))
//            .set(Key.TIME, TimeUtil.format(msg.getTimeDo(), "yyyy-MM-dd HH:mm:ss"))
//            .set(Key.NUM, 1)
//            .set(Key.PROFILE, "");
    Bean acData;

    /**
     * 导航切换聊天 语言 表情 拍照 fragment
     */
    NavigationImageView niv;

    /**
     * 标题栏
     */
    NavigationBar nb;
    
    public void OnCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_chat);
        srl =(SwipeRefreshLayout)findViewById(R.id.srl);
        lv = (ListView)findViewById(R.id.lv);
        adapter = new AdapterLvChat(this, listItemMsg);
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
                srl.setRefreshing(false);
            }
        });

        etsend = (EditText)findViewById(R.id.etsend);
        niv = (NavigationImageView)findViewById(R.id.niv);
        niv.setOnControl(new NavigationImageView.OnControl() {
            @Override
            public void onClose() {
                AndroidTools.toast(getContext(), "onClose");
            }

            @Override
            public void onOpen(int id) {
                AndroidTools.toast(getContext(), "onOpen id " + id);
            }
        });

        this.acData = AndroidTools.getMapFromIntent(this.getIntent());
//            .set(Key.TYPE, Key.TEXT)
//            .set(Key.FROM, msg.getFrom())
//            .set(Key.NAME, msg.getUserFrom())
//            .set(Key.TEXT, data.get(Key.TEXT))
//            .set(Key.TIME, TimeUtil.format(msg.getTimeDo(), "yyyy-MM-dd HH:mm:ss"))
//            .set(Key.NUM, 1)
//            .set(Key.PROFILE, "");
        nb = (NavigationBar)findViewById(R.id.nb);
        nb.setMenu(R.drawable.more);
        nb.setTitle(acData.get(Key.NAME, Key.NAME));
        nb.setSubtitle(acData.get(Key.FROM, Key.FROM));
        nb.setReturn("消息");
//        nb.setReturnIcon(R.id.ivprofile);
        nb.setOnNavigationBar(new NavigationBar.OnNavigationBar() {
            @Override
            public void onClickIvMenu(ImageView view) {
                toast("more");
            }

            @Override
            public void onClickTvReturn(TextView view) {
                onBackPressed();
            }

            @Override
            public void onClickTvTitle(TextView view) {

            }

            @Override
            public void onClickTvSubtitle(TextView view) {

            }
        });

        //初始化数据


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
     * @param msgJson
     */
    @Override
    public void OnReceive(String msgJson) {
        Msg msg = new Msg(msgJson);
        String plugin = msg.getType();
        if(msg.getFrom().equals(acData.get(Key.ID)))return;
        if(plugin.equals(Plugin.KEY_MESSAGE)){
// {"time_client":1560235377468,"time_do":1560235377498,"
// data":{"type":"txt","body":"看看"},
// "sfrom":"223.104.210.192:27959","wait_size":0,
// "from":"洋","to":"洋",
// "time_reveive":1560235377469,"type":"message",
// "sto":"223.104.210.192:27959"}
            Bean data = msg.getData();

            Bean item = new Bean()
                    .set(Key.TYPE, data.get(Key.TYPE, Key.TEXT))
                    .set(Key.FROM, msg.getFrom())
                    .set(Key.NAME, msg.getUserFrom())
                    .set(Key.PROFILE, "")
                    .set(Key.TIME, TimeUtil.format(msg.getTimeDo(), "yyyy-MM-dd HH:mm:ss"))
                    .set(Key.TEXT, data.get(Key.TEXT, ""))
                    ;

            addMsg(item);
        }

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
                        .set(Key.TYPE, Key.TEXT)
                        .set(Key.FROM, User.getId())
                        .set(Key.NAME, User.getUser())
                        .set(Key.PROFILE, "")
                        .set(Key.TIME, TimeUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
                        .set(Key.TEXT, str)
                        ;

                addMsg(bean);
//            NAME TEXT
//            ID KEY
//            {type:message,to:"all_socket",from:222,data:{type:txt,body:hello} }
               sendSocket(Plugin.KEY_MESSAGE, acData.get(Key.NAME, ""), new Bean().set(Key.TYPE, Key.TEXT).set(Key.TEXT, str));

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
        if(type.equals(Key.AUTO)){
            Bean bean = new Bean()
                    .set(Key.TYPE, Key.TEXT)
                    .set(Key.FROM, Key.DD)
                    .set(Key.NAME, Key.DD)
                    .set(Key.PROFILE, "")
                    .set(Key.TIME, TimeUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
                    .set(Key.TEXT, msg)
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
                sendHandler(Key.AUTO, res);
            }
        });
    }
    private void addMsg(Bean bean) {
        listItemMsg.add(bean);
        lv.setSelection(listItemMsg.size());	//选中最新一条，滚动到底部
    }

    /**
     * 更多菜单点击
     */
    @Override
    public void OnMoreClick() {
        toast("more");

    }
}
