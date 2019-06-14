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
import com.walker.common.util.LangUtil;
import com.walker.common.util.TimeUtil;
import com.walker.dd.R;
import com.walker.dd.activity.AcBase;
import com.walker.dd.adapter.*;
import com.walker.dd.service.Cache;
import com.walker.dd.service.MsgModel;
import com.walker.dd.service.NowUser;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.socket.server_1.Key;
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

    List<Bean> listItemMsg = new ArrayList<>();
    ListView lv;
    AdapterLvChat adapter;

    EditText etsend;
    Bean session;

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

        /**
         .set(Key.ID, msg.getUserTo().equals(NowUser.getId())?from.getId() : msg.getUserTo()) //当前会话id 关联from to
         .set(Key.NAME, "name")  //会话名 关联from to
         .set(Key.TYPE, Key.TEXT)  //文本类型
         .set(Key.FROM, msg.getUserFrom()) //消息来自谁发的 User[id, name, pwd]
         .set(Key.TO, msg.getUserTo) //消息发送的目标 user id group id
         .set(Key.TEXT, data.get(Key.TEXT))    //文本内容
         .set(Key.TIME, TimeUtil.format(msg.getTimeDo(), "yyyy-MM-dd HH:mm:ss"))   //时间
         .set(Key.NUM, 1)  //红点数
         */
        session = AndroidTools.getMapFromIntent(this.getIntent());
        listItemMsg = Cache.getInstance().get(session.get(Key.ID, Key.ID), new ArrayList<Bean>());



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
        nb = (NavigationBar)findViewById(R.id.nb);
        nb.setMenu(R.drawable.more);
        nb.setTitle(session.get(Key.NAME, Key.NAME));
        nb.setSubtitle(session.get(Key.ID, Key.ID));
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
        addMsg(null);

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
        String toid = session.get(Key.ID, "");
        String plugin = msg.getType();
        if(!(msg.getUserFrom().getId().equals(toid)
                || msg.getTo().equals(toid)))return;

        if(plugin.equals(Plugin.KEY_MESSAGE)){
            Bean data = msg.getData();
            Bean item = new Bean()
                    .set(Key.ID, data.get(Key.ID, Key.ID))
                    .set(Key.TYPE, data.get(Key.TYPE, Key.TEXT))
                    .set(Key.FROM, msg.getUserFrom())
                    .set(Key.TO, msg.getTo())
                    .set(Key.TIME, TimeUtil.format(msg.getTimeDo(), "yyyy-MM-dd HH:mm:ss"))
                    .set(Key.TEXT, data.get(Key.TEXT, ""))
                    .set(Key.FILE, data.get(Key.FILE, ""))
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
                String toid = session.get(Key.ID, "");   //目标人 或群
                String msgid = LangUtil.getGenerateId();
                String file = "";
                Bean bean = new Bean()
                        .set(Key.ID, msgid)
                        .set(Key.TYPE, Key.TEXT)
                        .set(Key.FROM, NowUser.getUser())
                        .set(Key.TO, toid)
                        .set(Key.TIME, TimeUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
                        .set(Key.TEXT, str)
                        .set(Key.FILE, file)
                        ;
                MsgModel.addMsg();  //存储
                addMsg(bean);   //表现
               sendSocket(Plugin.KEY_MESSAGE, session.get(Key.ID, ""), new Bean().set(Key.TYPE, Key.TEXT).set(Key.ID, msgid).set(Key.TEXT, str).set(Key.FILE, file));

               sendAuto(str);   //自动回复


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
            String toid = session.get(Key.ID, "");   //目标人 或群
            String msgid = LangUtil.getGenerateId();
            Bean bean = new Bean()
                    .set(Key.ID, msgid)
                    .set(Key.TYPE, Key.TEXT)
                    .set(Key.FROM, NowUser.getDd())
                    .set(Key.TO, toid)
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

    /**
    .set(Key.ID, msgid)
    .set(Key.TYPE, Key.TEXT)
    .set(Key.FROM, NowUser.getUser())
    .set(Key.TO, toid)
    .set(Key.TIME, TimeUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
    .set(Key.TEXT, str)
    .set(Key.FILE, file)
    */
    private void addMsg(Bean bean) {
        if(bean != null)
            listItemMsg.add(bean);
        lv.setSelection(listItemMsg.size());	//选中最新一条，滚动到底部
    }

    @Override
    public void OnPause() {
        Cache.getInstance().put(session.get(Key.ID, Key.ID), listItemMsg);
    }

    /**
     * 更多菜单点击
     */
    @Override
    public void OnMoreClick() {
        toast("more");

    }
}
