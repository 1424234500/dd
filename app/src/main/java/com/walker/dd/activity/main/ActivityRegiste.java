package com.walker.dd.activity.main;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.walker.common.util.Tools;
import com.walker.dd.R;
import com.walker.dd.activity.AcBase;
import com.walker.dd.service.LoginModel;
import com.walker.dd.service.NowUser;
import com.walker.dd.view.ClearEditText;

public class ActivityRegiste extends AcBase implements OnClickListener {

	ClearEditText cetUsername, cetEmail, cetPwd, cetRepwd;
	RadioGroup rgSex;
	Button bRegiste;
	TextView tvHelp, tvReturn;
 

	@Override
	public void OnCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_registe);
	 
		cetUsername = (ClearEditText)findViewById(R.id.cetusername);
		cetPwd = (ClearEditText)findViewById(R.id.cetpwd);
		cetEmail = (ClearEditText)findViewById(R.id.cetemail);
		cetRepwd = (ClearEditText)findViewById(R.id.cetrepwd);
		
		rgSex = (RadioGroup)findViewById(R.id.rgsex);
		bRegiste = (Button)findViewById(R.id.bregiste);
		
		tvHelp = (TextView)findViewById(R.id.tvhelp);
		tvReturn = (TextView)findViewById(R.id.tvreturn);
		
		tvReturn.setOnClickListener(this);
		tvHelp.setOnClickListener(this);
		bRegiste.setOnClickListener(this);
		cetPwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			@Override
			public void afterTextChanged(Editable arg0) {
				cetRepwd.setText("");
			}
		});
		
	} 

	 
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvreturn:
			startActivity(new Intent(this, ActivityLogin.class));
			this.finish();
			break;
		case R.id.tvhelp:
			break;
		case R.id.bregiste:
			ClickRegiste();
			break;
		 
		}
	}

	private void ClickRegiste() {

		String username = cetUsername.getText().toString();
		String pwd = cetPwd.getText().toString();
		String repwd = cetRepwd.getText().toString();
		String email = cetEmail.getText().toString();
		
	    RadioButton radioButton = (RadioButton)findViewById(rgSex.getCheckedRadioButtonId());
		String sex =  radioButton.getText() + "";
		
		if(Tools.notNull(username,pwd,repwd,email,sex) ){
			if(repwd.equals(pwd)){
                NowUser.setPwd(pwd);
                LoginModel.registe(this, username, email, sex, pwd);
				loadingStart();
			}else{
				toast("两次密码不同");
				cetRepwd.setText("");
			}
		}else{
			toast("有数据未填写");
		}
		

	}

	  
	public void out(String str) {
		Tools.out("ActivityRegiste." + str);
	}

	 

	@Override
	public void OnStart() {
		// TODO 自动生成的方法存根
		
	}



	@Override
	public void OnResume() {
		// TODO 自动生成的方法存根
		
	}



	@Override
	public void OnPause() {
		// TODO 自动生成的方法存根
		
	}



	@Override
	public void OnStop() {
		// TODO 自动生成的方法存根
		
	}



	@Override
	public void OnDestroy() {
		// TODO 自动生成的方法存根
		
	}



	@Override
	public boolean OnBackPressed() {
		//返回登录
		startActivity(new Intent(this, ActivityLogin.class));
		
		return false;	//false关闭this ac 
	}

    /**
     * 收到广播处理
     *
     * @param msgJson
     */
    @Override
    public void OnReceive(String msgJson) {

    }

}
