package com.walker.dd.activity.main;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.walker.common.util.Tools;
import com.walker.dd.R;
import com.walker.dd.activity.AcBase;
import com.walker.dd.core.AndroidTools;
import com.walker.dd.service.NetModel;
import com.walker.dd.view.ClearEditText;

public class ActivityConfigSystem extends AcBase implements OnClickListener {

	ClearEditText cettag;
	RadioGroup rgSex;
	Button bok;
	TextView tvHelp, tvReturn;

	Spinner sipssocket, sipsweb;


	@Override
	public void OnCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_config_system);

		cettag = (ClearEditText)findViewById(R.id.cettag);
		sipssocket = (Spinner)findViewById(R.id.sipssocket);
		sipsweb = (Spinner)findViewById(R.id.sipsweb);

		rgSex = (RadioGroup)findViewById(R.id.rgsex);
		bok = (Button)findViewById(R.id.bok);
		
		tvHelp = (TextView)findViewById(R.id.tvhelp);
		tvReturn = (TextView)findViewById(R.id.tvreturn);
		
		tvReturn.setOnClickListener(this);
		tvHelp.setOnClickListener(this);
		bok.setOnClickListener(this);

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
		case R.id.bok:
			ClickRegiste();
			break;
		 
		}
	}

	private void ClickRegiste() {

		String tag = cettag.getText().toString();
		String socketIp = sipssocket.getSelectedItem().toString();
		String webIp = sipsweb.getSelectedItem().toString();

	    RadioButton radioButton = (RadioButton)findViewById(rgSex.getCheckedRadioButtonId());
		String sex =  radioButton.getText() + "";
		
		if(Tools.notNull(socketIp, webIp, sex) ){
			try {
				NetModel.setServerSocketIp(socketIp.substring(0, socketIp.lastIndexOf(":")));
				NetModel.setServerSocketPort(Integer.valueOf(socketIp.substring(socketIp.lastIndexOf(":"), 0)));

				NetModel.setServerWebIp(socketIp.substring(0, webIp.lastIndexOf(":")));
				NetModel.setServerWebPort(Integer.valueOf(webIp.substring(webIp.lastIndexOf(":"), 0)));

			}catch (Exception e){
				AndroidTools.toast(getContext(), Tools.toString(e));
			}

		}else{
			toast("有数据未填写");
		}
		

	}

	  
	public void out(String str) {
		Tools.out("ActivityConfigSystem." + str);
	}

	 

	@Override
	public void OnStart() {

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
