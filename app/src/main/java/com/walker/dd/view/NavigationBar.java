package com.walker.dd.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.walker.dd.R;
import com.walker.dd.util.AndroidTools;


public class NavigationBar extends LinearLayout implements View.OnClickListener {
    public interface OnNavigationBar{
        void onClickIvMenu(ImageView view);
        void onClickTvReturn(TextView view);
        void onClickTvTitle(TextView view);
        void onClickTvSubtitle(TextView view);
    }
    OnNavigationBar onNavigationBar;

    Context context;

    ImageView ivmenu;
    TextView tvreturn;
    ImageView ivreturn;
    ImageView ivicon;

    TextView tvtitle;
    TextView tvsubtitle;

    public NavigationBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.item_navigator_bar, this, true);
        ivmenu = (ImageView)view.findViewById(R.id.ivmenu);
        tvtitle = (TextView)view.findViewById(R.id.tvtitle);
        tvreturn = (TextView)view.findViewById(R.id.tvreturn);
        ivreturn = (ImageView)view.findViewById(R.id.ivreturn);
        ivicon = (ImageView)view.findViewById(R.id.ivicon);


        tvsubtitle = (TextView)view.findViewById(R.id.tvsubtitle);
        ivmenu.setOnClickListener(this);
        tvtitle.setOnClickListener(this);
        tvreturn.setOnClickListener(this);
        ivreturn.setOnClickListener(this);
        ivicon.setOnClickListener(this);
        tvsubtitle.setOnClickListener(this);

		AndroidTools.out("NavigationBar"     );

	}
	public void setOnNavigationBar(OnNavigationBar onNavigationBar){
		this.onNavigationBar = onNavigationBar;
	}
	 
	@Override
	public void onClick(View view) {
        if(onNavigationBar == null){
            AndroidTools.toast(getContext(), "no call onclick " + view.getId());
            return;
        }
		switch (view.getId()){
            case R.id.ivmenu:
                onNavigationBar.onClickIvMenu(ivmenu);
                break;
            case R.id.tvtitle:
                onNavigationBar.onClickTvTitle(tvtitle);
                break;
            case R.id.tvsubtitle:
                onNavigationBar.onClickTvSubtitle(tvsubtitle);
                break;
            case R.id.ivreturn:
            case R.id.tvreturn:
            case R.id.ivicon:
                onNavigationBar.onClickTvReturn(tvreturn);
                break;
        }
	}

    public String getTitle(){
        return tvtitle.getText().toString();
    }
    public String getSubtitle(){
        return tvsubtitle.getText().toString();
    }
    public NavigationBar setTitle(String title){
        tvtitle.setText(title);
        return this;
    }
    public NavigationBar setSubtitle(String title){
        tvsubtitle.setText(title);
        if(title.length() == 0){
            tvsubtitle.setVisibility(INVISIBLE);
        }else{
            tvsubtitle.setVisibility(VISIBLE);
        }
        return this;
    }
    public NavigationBar setReturn(String title){
        tvreturn.setText(title);
        if(title.length() == 0){
            ivreturn.setVisibility(GONE);
            tvreturn.setVisibility(GONE);
        }else{
            ivreturn.setVisibility(VISIBLE);
            tvreturn.setVisibility(VISIBLE);
            ivicon.setVisibility(GONE);
        }
        return this;
    }

    public NavigationBar setTitle(int title){
        tvtitle.setText(title);
        return this;
    }
    public NavigationBar setSubtitle(int title){
        tvsubtitle.setText(title);
        tvsubtitle.setVisibility(VISIBLE);
        return this;
    }
    public NavigationBar setReturn(int title){
        tvreturn.setText(title);

        ivreturn.setVisibility(VISIBLE);
        tvreturn.setVisibility(VISIBLE);
        ivicon.setVisibility(GONE);
        return this;
    }
    public NavigationBar setReturnIcon(int icon){
        ivicon.setImageResource(icon);

        ivreturn.setVisibility(GONE);
        tvreturn.setVisibility(GONE);
        ivicon.setVisibility(VISIBLE);
        return this;
    }

    public NavigationBar setMenu(int draw){
        if(draw <= 0){
            ivmenu.setVisibility(INVISIBLE);
        }else {
            ivmenu.setImageResource(draw);
            ivmenu.setVisibility(VISIBLE);
        }
        return this;
    }

}
