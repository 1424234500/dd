package com.walker.dd.activity.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.walker.common.util.Bean;
import com.walker.dd.R;
import com.walker.dd.activity.AcBase;
import com.walker.dd.activity.FragmentBase;
import com.walker.dd.service.LoginModel;
import com.walker.dd.service.MsgModel;
import com.walker.dd.service.NowUser;
import com.walker.dd.service.SessionModel;
import com.walker.dd.service.NetModel;
import com.walker.dd.struct.Message;
import com.walker.dd.struct.Session;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.socket.server_1.Key;
import com.walker.dd.view.NavigationBar;
import com.walker.dd.view.NavigationImageTextView;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.plugin.Plugin;
import com.walker.socket.server_1.session.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AcBase {
    SwipeRefreshLayout srl;

    /**
     * 标题栏目
     */
    protected NavigationBar nb;


    TextView mTextMessage;

    FragmentBase fragmentSession;
    List<Session> listItemSession = new ArrayList<>();

    FragmentBase fragmentContact;
    List<Session> listItemContact = new ArrayList<>();

    FragmentBase fragmentOther;
    List<Bean> listItemOther = new ArrayList<>();

//    FragmentManager fragmentManager;
    android.support.v4.app.FragmentManager fragmentManager;

    FragmentBase fragmentNow;




    private NavigationImageTextView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new NavigationImageTextView.OnNavigationItemSelectedListener() {

//        R.id.itmsg, R.id.itcontact, R.id.itdollar
        @Override
        public boolean onNavigationItemSelected(int id) {
            switch (id) {
                case R.id.itmsg:
//                    mTextMessage.setText(R.string.title_home);
                    nb.setTitle(R.string.msg);
                    turnToFragment(fragmentSession);
                    return true;
                case R.id.itcontact:
//                    mTextMessage.setText(R.string.title_dashboard);
                    nb.setTitle(R.string.contact);
                    turnToFragment(fragmentContact);
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
//
//        srl = (SwipeRefreshLayout)findViewById(R.id.srl);
//        //设置刷新时动画的颜色，可以设置4个
//        srl.setColorSchemeResources(Constant.SRLColors);
//        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (fragmentNow == fragmentSession){
//                    sendSocket(Plugin.KEY_SESSION, new Bean());
//               }else if(fragmentNow == fragmentOther){
//                    AndroidTools.toast(getContext(), "refresh");
//                }else{
//                    AndroidTools.toast(getContext(), "refresh");
//
//                }
//                srl.setRefreshing(false);
//            }
//        });

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


        fragmentSession = new FragmentSession();
        fragmentSession.setData(listItemSession);
        fragmentContact = new FragmentContact();
        fragmentContact.setData(listItemContact);
        fragmentOther = new FragmentOther();
        fragmentOther.setData(listItemOther);

//        fragmentManager = getFragmentManager();
        fragmentManager = getSupportFragmentManager();  //v4

//        turnToFragment(fragmentSession);
        mOnNavigationItemSelectedListener.onNavigationItemSelected(R.id.itmsg);


        addSession(SessionModel.getDd());
        addContact(SessionModel.getDd());


        if(!NowUser.isLogin()) {
            if (!NowUser.isOffline()) {
                toast("未登录");
                goLogin();
            }else{
                toast("离线模式");
                addSession(SessionModel.finds(sqlDao, NowUser.getId(), 20));
            }
        }else{
            addSession(SessionModel.finds(sqlDao, NowUser.getId(), 20));
            sendSocket(Plugin.KEY_OFFLINEMSG, new Bean().put(Key.BEFORE, MsgModel.getLastMsgTime(sqlDao)));
            sendSocket(Plugin.KEY_SESSION, new Bean());
        }

        checkPermission();
    }


    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                ,Manifest.permission.CAMERA
        };
        List<String> errs = new ArrayList<>();
        for(String item : permissions) {
            if (checkSelfPermission(item) != PackageManager.PERMISSION_GRANTED) {
                errs.add(item);
                if (shouldShowRequestPermissionRationale(item)) {
                    AndroidTools.toast(this, "请开通相关权限，否则无法正常使用本应用！");
                }
                AndroidTools.log("check permissions error " + item);
            }else{
                AndroidTools.log("check permissions ok " + item);
            }
        }

        if(errs.size() > 0){
            requestPermissions(errs.toArray(new String[0]), 1);
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
                LoginModel.login(MainActivity.this, NowUser.getId(), NowUser.getPwd(), name);
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
                    toast("网络连接成功 " + NetModel.getServerSocketIp() + " " + NetModel.getServerSocketPort());
                    NetModel.setConn(true);
                }else{
                    NetModel.setConn(false);
                    toast("网络连接失败 " + NetModel.getServerSocketIp() + " " + NetModel.getServerSocketPort());
                }
            }else if(plugin.equals(Plugin.KEY_LOGIN)){
                if(status == 0) {
                    Bean data = msg.getData();
                    String id = data.get(Key.ID, "");
                    String name = data.get(Key.NAME, "");

                    NowUser.setId(id);
                    NowUser.setName(name);
                    nb.setTitle(name);
                    LoginModel.save(sqlDao, id, "", name, "");
                    toast("login ok", data);
                    sendSocket(Plugin.KEY_SESSION, new Bean());
                }else{
                    toast(msg.getInfo());
                    goLogin();
                }
            }
            else if(plugin.equals(Plugin.KEY_MESSAGE)){
               addMsg(msg);
            }
            //批量离线消息
            else if(plugin.equals(Plugin.KEY_OFFLINEMSG)){
                List<Bean> list = msg.getData();
                for(Bean item : list){
                    addMsg(new Msg(item));
                }
            }
            else if(plugin.equals(Plugin.KEY_SESSION)){
// {"TD":1560495038606,"STATUS":1,"SF":"117.136.8.129:29247","DATA":[
// {"TIME":"2019-06-14 14:47:41","USER":{"ID":"walker","PWD":"123","NAME":""},"KEY":"117.136.8.129:29247"}
// ,{"TIME":"","USER":{},"KEY":"101.230.233.82:41500"}
// ],"TO":"","FROM":{"ID":"walker","PWD":"123","NAME":""}
// ,"WS":0,"TYPE":"session","TR":1560495038578,"TC":1560495037721}
                List<Bean> list = msg.getData();
                List<Session> newList = new ArrayList<>();
                for(Bean data : list) {
                    User user = new User(data.get(Key.USER, new Bean()));
                    String toId = user.getId();
                    String toName = user.getName();
                    String type = Key.TEXT;
                    String time = data.get(Key.TIME, "");
                    String text = "" + data.get(Key.KEY, "");
                    int num = 0;
                    Session item = SessionModel.save(sqlDao, new Session(NowUser.getId(),toId, toName, time, type, text, num));
                    newList.add(item);
                }
                addContact(newList);
            }
            else{
                toast(msgJson);
            }

        }
    }

    private void addMsg(Msg msg){
        Message bean = MsgModel.addMsg(sqlDao, new Message(msg));   //存储消息

        Bean data = msg.getData();
        User from = msg.getUserFrom();
        String toId = "";
        String toName = "";
        if(msg.getUserTo()[0].equals(NowUser.getId())){
            toId = from.getId();
            toName = from.getName();
        }else{
            toId = msg.getUserTo()[0];
            toName = "G:"+ toId;
        }
        User user = new User(data.get(Key.USER, new Bean()));
        String type = bean.getMsgType();//bean.get(Key.TYPE, Key.TEXT);
        String time = bean.getTime();//bean.get(Key.TIME, "");
        String text = bean.getText();//bean.get(Key.TEXT, "");
        Session item = new Session(NowUser.getId(), toId, toName, time, type, text, -1);
        Session get = SessionModel.get(sqlDao, NowUser.getId(), toId);
        if(get == null){
            item.setNum(1);
        }else{
            item.setNum(get.getNum() + 1);
        }
        item = SessionModel.save(sqlDao, item);

        addSession(item);
    }

    private void addContact(Session item) {
        List<Session> list = new ArrayList<>();
        list.add(item);
        addContact(list);
    }
    private void addContact(List<Session> newList){
        handler.post(new Runnable() {
            @Override
            public void run() {
                AndroidTools.listReplaceIndexAndAdd(0, listItemContact, newList);
                Collections.sort(listItemContact, new Comparator<Session>() {
                    @Override
                    public int compare(Session o1, Session o2) {
                        return o2.getTime().compareTo(o1.getTime());
                    }
                });
                fragmentContact.notifyDataSetChanged();
            }
        });
    }

    private void addSession(List<Session> newList) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                AndroidTools.listReplaceIndexAndAdd(0, listItemSession, newList);
                Collections.sort(listItemSession, new Comparator<Session>() {
                    @Override
                    public int compare(Session o1, Session o2) {
                        return o2.getTime().compareTo(o1.getTime());
                    }
                });
                fragmentSession.notifyDataSetChanged();
            }
        });
    }
    private void addSession(Session item) {
        List<Session> list = new ArrayList<>();
        list.add(item);
        addSession(list);
    }

    //fragmentManager.beginTransaction().replace(R.id.main_fragment, fragmentSession).commit();
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
