package com.walker.dd.activity.chat;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.text.SpannableString;
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
import com.walker.dd.activity.FragmentBase;
import com.walker.dd.adapter.*;
import com.walker.dd.service.Cache;
import com.walker.dd.service.MsgModel;
import com.walker.dd.service.NowUser;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.dd.view.EmotionKeyboard;
import com.walker.socket.server_1.Key;
import com.walker.dd.util.RobotAuto;
import com.walker.dd.view.NavigationBar;
import com.walker.dd.view.NavigationImageView;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.view.View.GONE;
import static com.walker.dd.util.Constant.ACTIVITY_RESULT_CAMERA;

public class ActivityChat extends AcBase {
    SwipeRefreshLayout srl;
    EmotionKeyboard ekb;
    List<Bean> listItemMsg = new ArrayList<>();
    ListView lv;
    AdapterLvChat adapter;

    EditText etsend;
    Bean session;


    View viewfill;
    FragmentVoice fragmentVoice;
    FragmentPhoto fragmentPhoto;
    FragmentEmoji fragmentEmoji;
    List<Bean> listItemChat;

    FragmentMore fragmentMore;

    //    FragmentManager fragmentManager;
    android.support.v4.app.FragmentManager fragmentManager;

    FragmentBase fragmentNow;


    /**
     * 标题栏
     */
    NavigationBar nb;


    /**
     * 导航切换聊天 语言 表情 拍照 fragment
     */
    NavigationImageView niv;
    private NavigationImageView.OnControl onControl = new NavigationImageView.OnControl() {
        /**
         * 0则是关闭
         *
         * @param id
         */
        @Override
        public void onOpen(int id) {
            switch (id) {
                case R.id.ivvoice:
                    turnToFragment(fragmentVoice);
                    break;
                case R.id.ivphoto:
                    turnToFragment(fragmentPhoto);
                    break;
                case R.id.ivgraph:
                    Constant.TAKEPHOTO =  Constant.dirCamera + NowUser.getId() + "-" + TimeUtil.getTimeYmdHms()+".png";
                    AndroidTools.takePhoto(ActivityChat.this, Constant.TAKEPHOTO, ACTIVITY_RESULT_CAMERA);
                    break;
                case R.id.ivemoji:
                    turnToFragment(fragmentEmoji);
                    break;
                case R.id.ivmore:
                    turnToFragment(fragmentMore);
                    break;
                case 0:
                default:
                    //关闭
            }
            if(id == 0){
                ekb.hideViewHide(true);
            }else{
                ekb.showViewHide();
            }

        }

    };

    
    public void OnCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_chat);

/*
                .set(Key.ID, dd.getId())
                .set(Key.NAME, dd.getName())
                .set(Key.TIME, TimeUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
                .set(Key.TYPE, Key.TEXT)
                .set(Key.TEXT, "auto echo")
                .set(Key.NUM, 1)*/
        session = AndroidTools.getMapFromIntent(this.getIntent());
//        listItemMsg = Cache.getInstance().get(session.get(Key.ID, Key.ID), new ArrayList<Bean>());
        listItemMsg = MsgModel.findMsg(sqlDao, session.get(Key.ID, Key.ID), TimeUtil.getTimeYmdHms(), 15);


        viewfill = (View)findViewById(R.id.viewfill);
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
//                        ekb.hideViewHide(false);
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
                loadMore();
            }
        });

        etsend = (EditText)findViewById(R.id.etsend);
        etsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ekb.hideViewHide(true);
                niv.closeNocall();
            }
        });
        niv = (NavigationImageView)findViewById(R.id.niv);
        niv.setOnControl(onControl);
        niv.close();

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

        ekb = EmotionKeyboard.with(this)
                .bindViewMain(srl)//绑定内容view lvChat -> 下拉布局,需要weight 1来配合ekb解决闪屏
                .bindEditText(etsend)//EditView
                .bindViewHide(viewfill)//绑定隐藏布局
                .build();

        fragmentVoice = new FragmentVoice();
        fragmentVoice.setOnVoice( new FragmentVoice.OnVoice() {
            @Override
            public void onSend(String file) {
                toast("voice", file);
            }
        });
        fragmentPhoto = new FragmentPhoto();
        fragmentEmoji = new FragmentEmoji();
        fragmentEmoji.setCall(new FragmentEmoji.Call() {
            @Override
            public void onCall(SpannableString spannableString) {
                etsend.getText().insert(etsend.getSelectionStart(), spannableString);
            }
        });
        fragmentMore = new FragmentMore();

//        fragmentManager = getFragmentManager();
        fragmentManager = getSupportFragmentManager();  //v4

//      turnToFragment(fragmentChat);



        //初始化数据
        addMsg(null);

    }
    //fragmentManager.beginTransaction().replace(R.id.main_fragment, fragmentChat).commit();
    public void turnToFragment(FragmentBase fragment){
        if(fragment == fragmentNow) return;

        FragmentTransaction t = fragmentManager.beginTransaction();
        if(fragmentNow == null){
            t.replace(R.id.main_fragment, fragment);
        }else{
            if(!fragment.isAdded()){
                t.add(R.id.main_fragment, fragment);
            }
            t.hide(fragmentNow).show(fragment);
        }
        fragmentNow = fragment;
        t.commit();

    }



    /**
     * 回退键处理，返回false则执行finish，否则不处理
     */
    @Override
    public boolean OnBackPressed() {
        if(niv.isOpen()){
            niv.closeNocall();
            ekb.hideViewHide(true);
            return true;
        }
        if(ekb.isViewHideShow()){
            ekb.hideViewHide(false);
            return true;
        }
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
        Bean item = MsgModel.addMsg(sqlDao, msg);

        String toid = session.get(Key.ID, "");
        String plugin = msg.getType();

        if(!(msg.getUserFrom().getId().equals(toid)
                || msg.getTo().equals(toid)))return;

        if(plugin.equals(Plugin.KEY_MESSAGE)){
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
                Bean data = new Bean()
                        .set(Key.ID, msgid)
                        .set(Key.TYPE, Key.TEXT)
                        .set(Key.TEXT, str)
                        .set(Key.FILE, file)
                        ;
                Msg msg = new Msg().setUserFrom(NowUser.getUser())
                        .setUserTo(toid)
                        .setTimeDo(System.currentTimeMillis())
                        ;
                msg.setData(data);

                Bean bean = MsgModel.addMsg(sqlDao, msg);  //存储
                addMsg(bean);   //表现
               sendSocket(Plugin.KEY_MESSAGE, toid, data);

               //自动会话才自动回复
               if(toid.equals(NowUser.getDd().getId()))
                   sendAuto(str);   //自动回复


               etsend.setText("");

           }

        }
    }

    /**
     * handler处理器
     *
     * @param type
     * @param str
     */
    @Override
    public void OnHandler(String type, String str) {
        super.OnHandler(type, str);
        if(type.equals(Key.AUTO)){
            String toid = session.get(Key.ID, "");   //目标人 或群
            String msgid = LangUtil.getGenerateId();
            String file = "";
            Bean data = new Bean()
                    .set(Key.ID, msgid)
                    .set(Key.TYPE, Key.TEXT)
                    .set(Key.TEXT, str)
                    .set(Key.FILE, file)
                    ;
            Msg msg = new Msg().setUserFrom(NowUser.getDd())
                    .setUserTo(toid)
                    .setTimeDo(System.currentTimeMillis())
                    ;
            msg.setData(data);
            Bean bean = MsgModel.addMsg(sqlDao, msg);  //存储
            addMsg(bean);   //表现
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


    private void loadMore(){
        String time = listItemMsg.size() > 0 ? listItemMsg.get(0).get("TIME", "") : TimeUtil.getTimeYmdHms();
        List<Bean> list = MsgModel.findMsg(sqlDao, session.get(Key.ID, Key.ID), time, Constant.NUM);
        listItemMsg.addAll(0, list);
        adapter.notifyDataSetChanged();

        int sel = list.size();
        sel = sel > 0 ? sel - 1 : 0;
        lv.setSelection(sel);
        srl.setRefreshing(false);
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
        if(listItemMsg.size() >  Constant.NUM * 3){
            listItemMsg.remove(0);
        }
        adapter.notifyDataSetChanged();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_RESULT_CAMERA && resultCode == Activity.RESULT_OK  ) {
            String path = Constant.TAKEPHOTO;
            toast("拍照结果"+ path);
            List<String> list = new ArrayList<String>();
            list.add(path);
//            sendPhotos(list);
        }

    }

}
