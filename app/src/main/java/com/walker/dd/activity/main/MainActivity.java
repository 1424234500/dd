package com.walker.dd.activity.main;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.common.util.TimeUtil;
import com.walker.dd.R;
import com.walker.dd.activity.AcBase;
import com.walker.dd.service.Login;
import com.walker.dd.service.MsgModel;
import com.walker.dd.service.NowUser;
import com.walker.dd.service.SocketModel;
import com.walker.dd.util.AndroidTools;
import com.walker.socket.server_1.Key;
import com.walker.dd.view.NavigationBar;
import com.walker.dd.view.NavigationImageTextView;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.plugin.Plugin;
import com.walker.socket.server_1.session.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AcBase {
    /**
     * 标题栏目
     */
    protected NavigationBar nb;


    TextView mTextMessage;

    //所有fragment的数据都用static共用数据
    FragmentBase fragmentChat;

    List<Bean> listItemChat = new ArrayList<>();
    Comparator<Bean> sessionCompare = new Comparator<Bean>() {
        @Override
        public int compare(Bean o1, Bean o2) {
            return o1.get(Key.FROM, "").compareTo(o2.get(Key.FROM, ""));
        }
    };
    FragmentBase fragmentSession;

    FragmentBase fragmentOther;
    List<Bean> listItemOther = new ArrayList<>();
    FragmentManager fragmentManager;
//    android.support.v4.app.FragmentManager fragmentManager;
    FragmentBase fragmentNow;

    //添加自动会话
    User dd = NowUser.getDd();
    Bean sessionDd = new Bean()
            .set(Key.ID, dd.getId())
            .set(Key.NAME, dd.getName())
            .set(Key.TYPE, Key.TEXT)
            .set(Key.FROM, dd)
            .set(Key.TO, NowUser.getId())
            .set(Key.TEXT, "auto echo")
            .set(Key.TIME, TimeUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
            .set(Key.NUM, 1)
                ;


    private NavigationImageTextView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new NavigationImageTextView.OnNavigationItemSelectedListener() {

//        R.id.itmsg, R.id.itcontact, R.id.itdollar
        @Override
        public boolean onNavigationItemSelected(int id) {
            switch (id) {
                case R.id.itmsg:
//                    mTextMessage.setText(R.string.title_home);
                    nb.setTitle(R.string.msg);
                    turnToFragment(fragmentChat);
                    return true;
                case R.id.itcontact:
//                    mTextMessage.setText(R.string.title_dashboard);
                    nb.setTitle(R.string.contact);
                    turnToFragment(fragmentSession);
                    return true;
                case R.id.itdollar:
//                    mTextMessage.setText(R.string.title_notifications);
                    nb.setTitle(R.string.other);
                    turnToFragment(fragmentOther);
                    return true;
            }
            return false;
        }
    };

    @Override
    public void OnCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        //初始化系统
        AndroidTools.init(this);


        mTextMessage = (TextView)findViewById(R.id.message);
        nb = (NavigationBar)findViewById(R.id.nb);
        nb.setMenu(R.drawable.more);
        nb.setTitle("");
        nb.setSubtitle("");
        nb.setReturn("");
        nb.setReturnIcon(R.drawable.profile);
        nb.setOnNavigationBar(new NavigationBar.OnNavigationBar() {
            @Override
            public void onClickIvMenu(ImageView view) {
//                toast("more main");
                rename();
            }

            @Override
            public void onClickTvReturn(TextView view) {
//                toast("return main");
                NowUser.setLogin(false);
                goLogin();
            }

            @Override
            public void onClickTvTitle(TextView view) {

            }

            @Override
            public void onClickTvSubtitle(TextView view) {

            }
        });



        NavigationImageTextView navigation = (NavigationImageTextView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        fragmentChat = new FragmentSession();
        fragmentChat.setData(listItemChat);
        fragmentSession = new FragmentContact();
        fragmentOther = new FragmentOther();
        fragmentOther.setData(listItemOther);

        fragmentManager = getFragmentManager();
//        fragmentManager = getSupportFragmentManager();  //v4

//        turnToFragment(fragmentChat);
        mOnNavigationItemSelectedListener.onNavigationItemSelected(R.id.itmsg);


        addSession(dd);


        if(!NowUser.isLogin()) {
            if (!NowUser.isOffline()) {
                toast("未登录");
                goLogin();
            }else{
                toast("离线模式");
            }
        }else{
            sendSocket(Plugin.KEY_SESSION, new Bean());
        }
    }
    public void goLogin(){
        startActivity(new Intent(this, ActivityLogin.class));
        this.finish();
    }
    public void rename() {
        String name = NowUser.getName();

        final EditText etuser = new EditText(getContext());
        final EditText etpwd = new EditText(getContext());
        etuser.setText(name);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("昵称").setIcon(android.R.drawable.ic_dialog_info).setView(etuser).setNegativeButton("Cancel", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String name = etuser.getText().toString();
                NowUser.setName(name);
                Login.login(MainActivity.this, NowUser.getId(), NowUser.getPwd(), name);
            }
        });
        builder.show();
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
        if(fragmentNow != null){
            Msg msg = new Msg(msgJson);
            String plugin = msg.getType();
            int status = msg.getStatus();
            if(plugin.equals(Key.SOCKET)) {
                loadingStop();
                if(status == 0){
                    toast("网络连接成功 " + SocketModel.getServerIp() + " " + SocketModel.getServerPort());
                    SocketModel.setConn(true);
                }else{
                    SocketModel.setConn(false);
                    toast("网络连接失败 " + SocketModel.getServerIp() + " " + SocketModel.getServerPort());
                }
            }else if(plugin.equals(Plugin.KEY_LOGIN)){
                if(status == 0) {
                    Bean data = msg.getData();
                    NowUser.setId(data.get(Key.ID, ""));
                    NowUser.setName(data.get(Key.NAME, ""));
                    nb.setTitle(data.get(Key.NAME, ""));
                    toast("login ok", data);
                    sendSocket(Plugin.KEY_SESSION, new Bean());
                }else{
                    toast(msg.getInfo());
                    rename();
                }
            }
            else if(plugin.equals(Plugin.KEY_MESSAGE)){
                MsgModel.addMsg();  //存储消息

                Bean data = msg.getData();
                User from = msg.getUserFrom();
                String id = "";
                String name = "";
                if(msg.getUserTo()[0].equals(NowUser.getId())){
                    id = from.getId();
                    name = from.getName();
                }else{
                    id = msg.getUserTo()[0];
                    name = "group name "+ id;
                }
                if(name.length() <= 0)name = id;

                Bean item = new Bean()
                        .set(Key.ID, id)
                        .set(Key.NAME, name)
                        .set(Key.TYPE, Key.TEXT)
                        .set(Key.FROM, from)
                        .set(Key.TO, msg.getUserTo())
                        .set(Key.TEXT, data.get(Key.TEXT))
                        .set(Key.TIME, TimeUtil.format(msg.getTimeDo(), "yyyy-MM-dd HH:mm:ss"))
                        .set(Key.NUM, 1)
                        ;
                addSession(item);
            }
            else if(plugin.equals(Plugin.KEY_SESSION)){
// {"TD":1560495038606,"STATUS":1,"SF":"117.136.8.129:29247","DATA":[
// {"TIME":"2019-06-14 14:47:41","USER":{"ID":"walker","PWD":"123","NAME":""},"KEY":"117.136.8.129:29247"}
// ,{"TIME":"","USER":{},"KEY":"101.230.233.82:41500"}
// ],"TO":"","FROM":{"ID":"walker","PWD":"123","NAME":""}
// ,"WS":0,"TYPE":"session","TR":1560495038578,"TC":1560495037721}
                List<Bean> list = msg.getData();
                List<Bean> newList = new ArrayList<>();
                for(Bean data : list) {
                    User user = new User(data.get(Key.USER, new Bean()));
    //会话列表=在线用户给自己发一个空消息
                    Bean item = new Bean()
                        .set(Key.ID, user.getId())
                        .set(Key.NAME, user.getName().length()>0?user.getName():user.getId())
                        .set(Key.TYPE, Key.TEXT)
                        .set(Key.FROM, user)
                        .set(Key.TO, NowUser.getId())
                        .set(Key.TEXT, data.get(Key.KEY, ""))
                        .set(Key.TIME, data.get(Key.TIME, ""))
                        .set(Key.NUM, 1)
                        ;
                    newList.add(item);
                }
                addSession(newList);
            }
            else{
                toast(msgJson);
            }

        }
    }


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
    private void addSession(List<Bean> newList) {
        listItemChat.clear();
        listItemChat.add(dd );
        AndroidTools.listReplaceIndexAndAdd(0, listItemChat, newList, sessionCompare);
        fragmentChat.notifyDataSetChanged();
    }
    private void addSession(Bean item) {
        int i = AndroidTools.listIndex(listItemChat, item, sessionCompare);
        if(i >= 0){
            item.set(Key.NUM, listItemChat.get(i).get(Key.NUM, 0) + 1);
            listItemChat.remove(i);
        }
        listItemChat.add(0, item);
        fragmentChat.notifyDataSetChanged();
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
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.rlsearch:
                AndroidTools.toast(this, "rlsearch");
                break;

        }

    }
}
