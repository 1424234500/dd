package com.walker.dd.activity.main;

import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.dd.R;
import com.walker.dd.activity.AcBase;
import com.walker.dd.adapter.AdapterLvIds;
import com.walker.dd.service.LoginModel;
import com.walker.dd.service.NowUser;
import com.walker.dd.service.SocketModel;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.dd.view.ClearEditText;
import com.walker.socket.server_1.Key;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.plugin.Plugin;

public class ActivityLogin extends AcBase implements OnClickListener, TextWatcher {

	ClearEditText cetId, cetPwd;
	ImageView ivdown, ivprofile;
	Button blogin;
	TextView tvcannotlogin, tvnewuser;
	View rllogin, llloginmove;
	boolean keyon = false;

	TextView tvpay;
	
	static int cc = 0;


	@Override
	public void OnCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_login);
		rllogin = (View) findViewById(R.id.rllogin);
		llloginmove = (View) findViewById(R.id.llloginmove);
		cetId = (ClearEditText) findViewById(R.id.id);
		cetPwd = (ClearEditText) findViewById(R.id.pwd);
		ivdown = (ImageView) findViewById(R.id.ivdown);
		ivprofile = (ImageView) findViewById(R.id.ivprofile);
		blogin = (Button) findViewById(R.id.blogin);
		tvcannotlogin = (TextView) findViewById(R.id.tvcannotlogin);
		tvnewuser = (TextView) findViewById(R.id.tvnewuser);
		
		
		


		
		
		
		
		rllogin.setOnClickListener(this);
		cetId.setOnClickListener(this);
		cetId.setOnClick(new ClearEditText.OnClear() {
            @Override
            public void onClear() {
                cetPwd.setText("");
            }
		});
		cetId.addTextChangedListener(this);

		cetPwd.setOnClickListener(this);

		ivdown.setOnClickListener(this);
		blogin.setOnClickListener(this);
		tvcannotlogin.setOnClickListener(this);
		tvcannotlogin.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				final EditText inputServer = new EditText(ActivityLogin.this);
				inputServer.setText(SocketModel.getServerIp());
		        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
		        builder.setTitle("设置服务器IP").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)  .setNegativeButton("Cancel", null);
		        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	String ip = inputServer.getText().toString();
		            	SocketModel.setServerIp(ip);
                        getApp().initSocket();
		             }
		        });
		        builder.show();
				return true;
			}
		});
		tvnewuser.setOnClickListener(this);
 
		// 动画效果
		Animation myAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_login_move);
		llloginmove.startAnimation(myAnimation);
		
		//初始化账号密码
		String id = NowUser.getId();
		String pwd = NowUser.getPwd();

		cetId.setText(id);
		cetPwd.setText(pwd);

		if(! SocketModel.isConn())
		loadingStart();//加载等待socket连接
	}

	 
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		/////////////////
		case R.id.ivdown:
			if (popupWindow == null)
				OpenIvDown(v);
			else
				CloseIvDown();
			closeKeyboard(rllogin);
			break;
		case R.id.blogin:
			ClickLogin();
			break;
		case R.id.tvnewuser:
			//创建用户跳转RegisteAc,之后再回来登录
			startActivity(new Intent(ActivityLogin.this, ActivityRegiste.class));
			this.finish();
			
			break;
		case R.id.tvcannotlogin:    //离线模式
			NowUser.setLogin(false);
			NowUser.setOffline(true);
			goMain();
			break;
		case R.id.rllogin:
			closeKeyboard(rllogin);
			break;
		}
	}

    private void goMain() {
        startActivity(new Intent(ActivityLogin.this, MainActivity.class));
        this.finish();
    }

    /**
     * 点击登录
     */
    private void ClickLogin() {
		String id = cetId.getText().toString();
		String pwd = cetPwd.getText().toString();
		if (id.length() > 0) {
			//合法账号密码，发送登陆请求,并且本地放入本地临时账户信息记录
			NowUser.setId(id);
			NowUser.setPwd(pwd);
            LoginModel.login(this, id, pwd, NowUser.getName());
			//登陆中 提示
            loadingStart();
		} else {
			out("请输入有效账号和密码");
		}

	}

	public void openKeyboard() {
		keyon = true;
	}

	public void closeKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) getApplicationContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0); // 关闭软键盘
		keyon = false;
	}

	PopupWindow popupWindow = null; // 下拉窗口
	List<Bean>  liststr = null; // listview的数据集合
	AdapterLvIds mAdapter = null;// listview的适配器

	public void CloseIvDown() {
		popupWindow.dismiss();
		popupWindow = null;
		liststr = null;
		mAdapter = null;
	}

	private void OpenIvDown(View view) {
		// 构造需要显示的数据
		liststr = LoginModel.finds(sqlDao);
		if (liststr.size() <= 0)
			return;

		View contentView = LayoutInflater.from(getApplicationContext())
				.inflate(R.layout.listview_popup, null);
		ListView lvids = (ListView) contentView.findViewById(R.id.lvids);// 取出临时布局中的空间lv
		// 设置 事件回调处理
		mAdapter = new AdapterLvIds(this, liststr);
        mAdapter.setOnClick(new AdapterLvIds.OnClick() {
            @Override
            public void onOk(Bean res) {
                out("chose id=" + res.get(Key.ID) + " pwd=" + res.get(Key.PWD));
                cetId.setText( res.get(Key.ID).toString());
                cetPwd.setText(res.get(Key.PWD).toString());
                popupWindow.dismiss();// 关闭
            }

            @Override
            public void onDel(Bean res) {
                String id = res.get(Key.ID, "");
                LoginModel.delete(sqlDao, id);
                liststr.remove(res);	//上下转后 这个对象还是不是list中的那个呢？能否删除
                mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变
                // popupWindow.dismiss();//关闭，
                // ClickIvDown(ivdown);//重启下拉菜单
            }
        });
		lvids.setAdapter(mAdapter);

		popupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		popupWindow.setTouchable(true);

		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.showAsDropDown(view);

	}

	public void out(String str) {
		AndroidTools.out("ActivityLogin." + str);
	}

    /**
     * 自动填充密码
     * @param e
     */
	@Override
	public void afterTextChanged(Editable e) {
	    String id = cetId.getText().toString();

		Bean llu = LoginModel.get(sqlDao, id);
		if (llu != null){
			String getid = llu.get(Key.ID, "");
            String getpwd = llu.get(Key.PWD, "");
            String getname = llu.get(Key.NAME, "");
            cetPwd.setText(getpwd);
            NowUser.setName(getname);
//            if (getpath.length() > 0) {
//                NetImage.loadProfile(this, getpath, this.ivprofile);
//            }
		} else{
			cetPwd.setText("");
			if(cetId.getText().toString().length() > 0) {
//				MSGSender.getProfileByPath(this,  cetId.getText().toString());
            }
		}
		
		

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}



	@Override
	public void OnStart() {

	}



	@Override
	public void OnResume() {

	}



	@Override
	public void OnPause() {

	}



	@Override
	public void OnStop() {

	}



	@Override
	public void OnDestroy() {

	}




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
        int status = msg.getStatus();
        if(plugin.equals(Key.SOCKET)) {
            loadingStop();
            if(status == 0){
                toast("网络连接成功 " + SocketModel.getServerIp() + " " + SocketModel.getServerPort());
                if(NowUser.isAutoLogin()){
                    ClickLogin();
                }
                SocketModel.setConn(true);
            }else{
                SocketModel.setConn(false);
                toast("网络连接失败 " + SocketModel.getServerIp() + " " + SocketModel.getServerPort());
            }
        }else if(plugin.equals(Plugin.KEY_LOGIN)){
            loadingStop();
            if(status == 0) {
                Bean data = msg.getData();
                String id = data.get(Key.ID, "");
                String pwd = NowUser.getPwd();
                String name = data.get(Key.NAME, "");
                String profile = data.get(Key.PROFILE, "");

                NowUser.setId(id);
                NowUser.setName(name);

                LoginModel.save(sqlDao, id, pwd, name, profile);

                toast("login ok", data);
                NowUser.setLogin(true);
                goMain();
            }else{
                toast(msg.getInfo());
            }
        }



    }

}
