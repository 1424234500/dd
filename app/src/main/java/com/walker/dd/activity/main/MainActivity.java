package com.walker.dd.activity.main;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.common.util.TimeUtil;
import com.walker.core.encode.Pinyin;
import com.walker.dd.R;
import com.walker.dd.activity.AcBase;
import com.walker.dd.service.User;
import com.walker.dd.util.AndroidTools;
import com.walker.socket.server_1.Key;
import com.walker.dd.view.NavigationBar;
import com.walker.dd.view.NavigationImageTextView;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.plugin.Plugin;

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
                toast("more main");
            }

            @Override
            public void onClickTvReturn(TextView view) {
                toast("return main");
                login(true);
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

        login(false);

    }
    public void login(boolean ifnew) {
        String user = User.getUser(Pinyin.getChinese());
        String pwd = User.getPwd();

        if (user.length() > 0 && !ifnew) {
            sendSocket(Plugin.KEY_LOGIN, new Bean().put(Key.USER, user).put(Key.PWD, pwd));
        } else {
            final EditText etuser = new EditText(getContext());
            final EditText etpwd = new EditText(getContext());
            etuser.setText(User.getUser());
            etpwd.setText(User.getPwd());
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("登录用户名").setIcon(android.R.drawable.ic_dialog_info).setView(etuser).setNegativeButton("Cancel", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String user = etuser.getText().toString();
                    String pwd = etuser.getText().toString();
                    User.setUser(user);
                    User.setPwd(pwd);
                    sendSocket(Plugin.KEY_LOGIN, new Bean().put(Key.USER, user).put(Key.PWD, pwd));
                }
            });
        builder.show();
        }
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
            if(plugin.equals(Plugin.KEY_LOGIN)){
                if(status == 0) {
                    Bean data = msg.getData();
                    User.setId(data.get(Key.ID, ""));
                    User.setUser(data.get(Key.USER, ""));
                    nb.setTitle(data.get(Key.USER, ""));
                    toast("login ok", data);
                    sendSocket(Plugin.KEY_SESSION, new Bean());
                }else{
                    toast(msg.getInfo());
                    login(true);
                }
            }
            else if(plugin.equals(Plugin.KEY_MESSAGE)){
//{"TD":1560423391906,"ST":"223.104.213.19:13523",
// "STATUS":1,"SF":"223.104.213.19:13523","DATA":[
// {"TIME":"","ID":"223.104.213.19:13523","USER":""}
// ],"TO":"","FROM":"","WS":0,"TYPE":"session","TR":1560423391901,"TC":1560423391883}
                Bean data = msg.getData();

                Bean item = new Bean()
                        .set(Key.TYPE, Key.TEXT)
                        .set(Key.ID, msg.getFrom())
                        .set(Key.NAME, msg.getUserFrom())
                        .set(Key.TEXT, data.get(Key.TEXT))
                        .set(Key.TIME, TimeUtil.format(msg.getTimeDo(), "yyyy-MM-dd HH:mm:ss"))
                        .set(Key.NUM, 1)
                        .set(Key.PROFILE, "");
                if(item.get(Key.NAME, "").length() == 0 && item.get(Key.ID, "").length() != 0){
                    item.set(Key.NAME, item.get(Key.ID, ""));
                }
                int i = AndroidTools.listIndex(listItemChat, item, sessionCompare);
                if(i >= 0){
                    item.set(Key.NUM, listItemChat.get(i).get(Key.NUM, 0) + 1);
                    listItemChat.remove(i);
                }
                listItemChat.add(0, item);

                fragmentChat.notifyDataSetChanged();
            }
            else if(plugin.equals(Plugin.KEY_SESSION)){
//{"time_client":1560238312727,"time_do":1560238312778,
// "data":[
// {"TIME":"2019-06-11 14:49:59",Key.ID:"蓼","KEY":"223.104.210.192:27961"},
// {"TIME":"2019-06-11 15:31:18",Key.ID:"岘","KEY":"223.104.210.192:29886"}],
// "sfrom":"223.104.210.192:29886","wait_size":0,
// "from":"岘","to":"","time_reveive":1560238312728,"type":"session","sto":"223.104.210.192:29886"}
                List<Bean> list = msg.getData();
                List<Bean> newList = new ArrayList<>();
                for(Bean data : list) {
                    Bean item = new Bean()
                        .set(Key.TYPE, Key.TEXT)
                        .set(Key.FROM, data.get(Key.ID, ""))
                        .set(Key.NAME, data.get(Key.USER, ""))
                        .set(Key.TEXT, data.get(Key.ID, ""))
                        .set(Key.TIME, TimeUtil.format(msg.getTimeDo(), "yyyy-MM-dd HH:mm:ss"))
                        .set(Key.NUM, 1)
                        .set(Key.PROFILE, "");
                    if(item.get(Key.NAME, "").length() == 0 && item.get(Key.FROM, "").length() != 0){
                        item.set(Key.NAME, item.get(Key.FROM, ""));
                    }
                    newList.add(item);
                }
                AndroidTools.listReplaceIndexAndAdd(0, listItemChat, newList, sessionCompare);
//            fragmentNow.onReceive(msg);
                fragmentChat.notifyDataSetChanged();
            }
            else{
                toast(msgJson);
            }

        }
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
