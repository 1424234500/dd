package com.walker.dd.activity.chat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.walker.common.util.FileUtil;
import com.walker.common.util.JsonUtil;
import com.walker.common.util.LangUtil;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.dd.R;
import com.walker.dd.activity.AcBase;
import com.walker.dd.activity.FragmentBase;
import com.walker.dd.adapter.*;
import com.walker.dd.service.Cache;
import com.walker.dd.service.MsgModel;
import com.walker.dd.service.NowUser;
import com.walker.dd.struct.Message;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.dd.util.KeyUtil;
import com.walker.dd.util.MyFile;
import com.walker.dd.util.MyImage;
import com.walker.dd.util.OkHttpUtil;
import com.walker.dd.util.UriUtil;
import com.walker.dd.util.picasso.NetImage;
import com.walker.dd.view.DialogImageShow;
import com.walker.dd.view.EmotionKeyboard;
import com.walker.socket.server_1.Key;
import com.walker.dd.util.RobotAuto;
import com.walker.dd.view.NavigationBar;
import com.walker.dd.view.NavigationImageView;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ActivityChat extends AcBase {
    SwipeRefreshLayout srl;
    EmotionKeyboard ekb;
    List<Message> listItemMsg = new ArrayList<>();
    Comparator<Message> compare = new Comparator<Message>() {
        @Override
        public int compare(Message o1, Message o2) {
            return o1.getMsgId().compareTo(o2.getMsgId());
        }
    };
    Comparator<Message> compareTime = new Comparator<Message>() {
        @Override
        public int compare(Message o1, Message o2) {
            return o1.getTime().compareTo(o2.getTime());
        }
    };
    ListView lv;
    AdapterLvChat adapter;

    EditText etsend;
    Bean session;


    View viewfill;
    FragmentVoice fragmentVoice;
    FragmentPhoto fragmentPhoto;
    FragmentEmoji fragmentEmoji;

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
//                    turnToFragment(fragmentPhoto);
                    AndroidTools.chosePhoto(ActivityChat.this, Constant.ACTIVITY_RESULT_PHOTO);
                    niv.close();
                    break;
                case R.id.ivgraph:
                    Constant.TAKEPHOTO =  Constant.dirCamera + NowUser.getId() + "-" + TimeUtil.getTimeYmdHms()+".png";
                    AndroidTools.takePhoto(ActivityChat.this, Constant.TAKEPHOTO);
                    niv.close();
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
                ekb.hideViewHide(false);
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
                        NetImage.resume(getContext());
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING://滚动状态
                        NetImage.pause(getContext());
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://触摸后滚动

                        break;
                }
            }
            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

            }
        });

        adapter.setOnClick(new AdapterLvChat.OnClick() {
            @Override
            public void onClick(int position) {
                final Message bean = listItemMsg.get(position);
                String type = bean.getMsgType();//bean.get(Key.TYPE, Key.TEXT);
                String sta = bean.getSta();//bean.get(Key.STA, Key.STA_FALSE);
                final String key = bean.getFile();//bean.get(Key.FILE, "");
                final String newPath = KeyUtil.getFileLocal(key);
                //文件下载
                if(type.equals(Key.FILE)){
                    assert key.length() > 0;
                    if(new File(newPath).exists()){
                        sta = Key.STA_TRUE;
                    }else if(sta.equals(Key.STA_TRUE)){
                        sta = Key.STA_FALSE;
                    }
                    if(sta.equals(Key.STA_FALSE)){  //下载文件
                        sta = Key.STA_LOADING;
                        OkHttpUtil.download(KeyUtil.getFileHttp(key), newPath, new OkHttpUtil.OnHttp() {
                            @Override
                            public void onOk(Call call, Response response, long length) {
                                bean.setSta(Key.STA_TRUE);
                                addMsg(bean);
                            }

                            @Override
                            public void onLoading(final float progress, final long length, long allLength, final long sudo) {
                                if(Integer.valueOf(bean.getSta()) !=  (int)progress) {
                                    bean.setSta("" + (int) progress);
                                    bean.setInfo("P " + Tools.calcSize((long) (allLength/100*progress)) + "/" + Tools.calcSize(allLength) + " " + progress + "%" + " " + Tools.calcSize(sudo) + "/s" );
                                    out(bean.getInfo());
                                    addMsg(bean);
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                FileUtil.delete(newPath);
                                bean.setSta(Key.STA_FALSE);
                                addMsg(bean);
                                toast("下载文件失败", key);
                            }
                        });
                    }else if(sta.equals(Key.STA_TRUE)){  //打开文件
                        AndroidTools.openFile(ActivityChat.this, newPath);
                    }else{  //
                        toast("loading...");
                    }
                    bean.setSta(sta);
                    addMsg(bean);
                }
                //图片详情
                else if(type.equals(Key.PHOTO)){
                    //携带参数跳转
                    String path = KeyUtil.getFileLocal(key);
                    DialogImageShow dis = new DialogImageShow(ActivityChat.this, path);
                    dis.show();
                    dis.setCancelable(true);
                }
                //语言自动播放
                else if(type.equals(Key.VOICE)){

                }


            }
        });
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
                scroll();
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



        //初始化滚动
        scroll();

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
            ekb.hideViewHide(false);
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
        Message item = MsgModel.addMsg(sqlDao, new Message(msg));

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
                sendMsg(str);
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
                final String str = response.body().string();
                //                sendHandler(Key.AUTO, res);

                out("onResponse", str);
                final String res=RobotAuto.parseTencentRes(str);
                out(res);
                String toid = session.get(Key.ID, "");   //目标人 或群
                String msgid = LangUtil.getGenerateId();
                String file = "";
                Bean data = new Bean()
                        .set(Key.ID, msgid)
                        .set(Key.TYPE, Key.TEXT)
                        .set(Key.TEXT, res)
                        .set(Key.FILE, file)
                        ;
                Msg msg = new Msg().setUserFrom(NowUser.getDd())
                        .setUserTo(toid)
                        .setTimeDo(System.currentTimeMillis())
                        ;
                msg.setData(data);
                Message bean = MsgModel.addMsg(sqlDao, new Message(msg));  //存储
                addMsg(bean);   //表现

            }
        });
    }

    /**
     * 关于异步操作 耗时socket操作 数据状态改变 及时更新listview 和 选中最新滚动的问题!
     * @param str
     */
    private void sendMsg(final String str){
        String toid = session.get(Key.ID, "");   //目标人 或群
        String msgid = LangUtil.getGenerateId();
        String file = "";
        Bean data = new Bean()
                .set(Key.ID, msgid)
                .set(Key.TYPE, Key.TEXT)
                .set(Key.STA, Key.STA_LOADING)
                .set(Key.TEXT, str)
                .set(Key.FILE, file)
                ;
        Msg msg = new Msg().setUserFrom(NowUser.getUser())
                .setUserTo(toid)
                .setTimeDo(System.currentTimeMillis())
                .setData(data)
                ;
        Message bean = MsgModel.addMsg(sqlDao, new Message(msg));  //存储
        addMsg(bean);   //表现
        try {
            data.set(Key.STA, "");  //不发给其他端
            sendSocket(Plugin.KEY_MESSAGE, toid, data); //发送socket
            //成功后 更新发送状态 发送失败异常跳出
            bean.setSta(Key.STA_TRUE);
        }catch (Exception e){
            e.printStackTrace();
            toast("发送失败", data);
            bean.setSta(Key.STA_FALSE);
        }
        bean = MsgModel.addMsg(sqlDao, bean);  //存储
        addMsg(bean);   //表现
        //自动会话才自动回复
        if(toid.equals(NowUser.getDd().getId()))
            sendAuto(str);   //自动回复
    }
    private void sendFile(final String path){
        String name = FileUtil.getFileName(path);
        String type = FileUtil.getFileType(path);
        final String toid = session.get(Key.ID, "");   //目标人 或群
        final String msgid = LangUtil.getGenerateId();
        final Bean data = new Bean()
                .set(Key.ID, msgid)
                .set(Key.TYPE, Key.FILE)
                .set(Key.STA, "1")
                .set(Key.TEXT, name)
                .set(Key.FILE, path)    //先暂存本地发送路径 待上传成功cp到下载路径
                ;
        final Msg msg = new Msg().setUserFrom(NowUser.getUser())
                .setUserTo(toid)
                .setTimeDo(System.currentTimeMillis())
                .setData(data)
                ;

        final Message bean = MsgModel.addMsg(sqlDao, new Message(msg));  //存储
        addMsg(bean);   //表现

        //上传文件拿到 文件key 把被发送文件复制到 下载路径作为下载到的文件 消息存储该路径文件path 若有则用 若无则按照规则下载
        OkHttpUtil.upload(path, new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
              e.printStackTrace();
              handler.post(new Runnable() {
                  @Override
                  public void run() {
                      bean.setSta(Key.STA_FALSE);
                      MsgModel.addMsg(sqlDao, bean);  //存储
                      addMsg(bean);   //表现
                      toast("上传失败", path);
                  }
              });
          }
          @Override
          public void onResponse(Call call, Response response) throws IOException {

              final String res = response.body().string();
//              sendHandler(Key.FILE + ":true", path);
              Bean resBean = JsonUtil.get(res);
              final String key = resBean.get("data", "");
                  log("上传成功", res);
                  // 消息存储该路径文件path 若有则用 若无则按照规则下载
                  data.set(Key.FILE, key);
                  bean.setFile(key);
                  try {
                      data.set(Key.STA, "");    //不需要发送给其他端
                      sendSocket(Plugin.KEY_MESSAGE, toid, data); //发送socket
                      bean.setSta(Key.STA_TRUE);
                      // 把被发送文件复制到 下载路径作为下载到的文件
                      String newPath = KeyUtil.getFileLocal(key);
//                          FileUtil.cp(path, newPath);
                      MyFile.copyFile(path, newPath);
                  }catch (Exception e){
                        e.printStackTrace();
                        toast("发送文件消息失败", key);
                        bean.setSta(Key.STA_FALSE);
                  }
                  MsgModel.addMsg(sqlDao, bean);  //存储
                  addMsg(bean);   //表现
          }
        });

    }


    private void sendPhoto(final String path){
        String name = FileUtil.getFileName(path);
        String type = FileUtil.getFileType(path);

        //图片上传 压缩的文件 控制大小在500kb以内 分辨率800
        // 把被发送文件压缩转换 下载路径作为下载到的文件 原名
        String tempName = "T_" + System.currentTimeMillis() + "." + type;
        String tempPath = KeyUtil.getFileLocal(tempName);
//        MyFile.copyFile(path, tempPath);
        MyImage.savePNG_After(MyImage.getBitmapByDecodeFile(path), tempPath);

        final String toid = session.get(Key.ID, "");   //目标人 或群
        final String msgid = LangUtil.getGenerateId();
        final Bean data = new Bean()
                .set(Key.ID, msgid)
                .set(Key.TYPE, Key.PHOTO)
                .set(Key.STA, "1")
                .set(Key.TEXT, name)
                .set(Key.FILE, tempName)    //先暂存本地发送路径 待上传成功cp到下载路径
                ;
        final Msg msg = new Msg().setUserFrom(NowUser.getUser())
                .setUserTo(toid)
                .setTimeDo(System.currentTimeMillis())
                .setData(data)
                ;

        final Message bean = MsgModel.addMsg(sqlDao, new Message(msg));  //存储
        addMsg(bean);   //表现

        //上传文件拿到 文件key 把被发送文件复制到 下载路径作为下载到的文件 消息存储该路径文件path 若有则用 若无则按照规则下载
        OkHttpUtil.upload(tempPath, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        bean.setSta(Key.STA_FALSE);
                        MsgModel.addMsg(sqlDao, bean);  //存储
                        addMsg(bean);   //表现
                        toast("上传失败", path);
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String res = response.body().string();
//              sendHandler(Key.FILE + ":true", path);
                Bean resBean = JsonUtil.get(res);
                final String key = resBean.get("data", "");
                log("上传成功", res);

                String newPath = KeyUtil.getFileLocal(key);
                new File(tempPath).renameTo(new File(newPath)); //重命名临时文件
                // 消息存储该路径文件path 若有则用 若无则按照规则下载
                data.set(Key.FILE, key);
                bean.setFile(key);
                try {
                    data.set(Key.STA, "");    //不需要发送给其他端
                    sendSocket(Plugin.KEY_MESSAGE, toid, data); //发送socket
                    bean.setSta(Key.STA_TRUE);

                }catch (Exception e){
                    e.printStackTrace();
                    toast("发送文件消息失败", key);
                    bean.setSta(Key.STA_FALSE);
                }
                MsgModel.addMsg(sqlDao, bean);  //存储
                addMsg(bean);   //表现
            }
        });



    }



    private void loadMore(){
        String time = listItemMsg.size() > 0 ? listItemMsg.get(0).getTime(): TimeUtil.getTimeYmdHms();
        List<Message> list = MsgModel.findMsg(sqlDao, session.get(Key.ID, Key.ID), time, Constant.NUM);
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
     .set(Key.STA, Key.STA_LOADING)
    .set(Key.FROM, NowUser.getUser())
    .set(Key.TO, toid)
    .set(Key.TIME, TimeUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
    .set(Key.TEXT, str)
    .set(Key.FILE, file)
    */
    private void addMsg(final Message bean) {
        if(bean != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {

                int i = AndroidTools.listIndex(listItemMsg, bean, compare);
                if(i >= 0){
//                    listItemMsg.get(i).putAll(bean);
                    listItemMsg.remove(i);
                    listItemMsg.add(i, bean);
                }else{
                    listItemMsg.add(bean);
    //                lv.setSelection(listItemMsg.size());	//选中最新一条，滚动到底部
                }
                if(listItemMsg.size() >  Constant.NUM * 3){
                    listItemMsg.remove(0);
                }
                Collections.sort(listItemMsg, compareTime);
                adapter.notifyDataSetChanged();
                if(i < 0){
                    lv.smoothScrollByOffset(listItemMsg.size());
                }

                }
            });
        }

    }
    public void scroll(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                lv.smoothScrollByOffset(listItemMsg.size());
            }
        });
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
        if(resultCode != Activity.RESULT_OK || data == null){
            log("onActivityResult 操作取消??", resultCode, data);
//            return;
        }
        try{
            if (requestCode == Constant.ACTIVITY_RESULT_FILE ) {
                Uri uri = data.getData();
                String path = UriUtil.getpath(getContext(), uri);
                sendFile(path);
            }else if (requestCode == Constant.ACTIVITY_RESULT_PHOTO  ) {
                Uri uri = data.getData();
                String path = UriUtil.getpath(getContext(), uri);
                sendPhoto(path);
            }else if (requestCode == Constant.ACTIVITY_RESULT_TAKEPHOTO  ) {
                sendPhoto(Constant.TAKEPHOTO);
            }else{
                toast("onAc " + data);
            }
        }  catch (Exception e) {
            toast(e.toString());
            e.printStackTrace();
        }
    }

}
