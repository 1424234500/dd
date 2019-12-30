package com.walker.dd.view;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.walker.dd.R;
import com.walker.dd.core.AndroidTools;


public class NavigationImageTextView extends LinearLayout implements View.OnClickListener {
    public interface OnNavigationItemSelectedListener{
        boolean onNavigationItemSelected(int id);
    }


    Context context;
	private OnNavigationItemSelectedListener callback = null;
	private List<ImageText> its = new ArrayList<ImageText>();
	
	String[] texts = {"msg", "contact", "other"};
	int color1 =   R.color.colorPrimary, color2 = R.color.qqtoppanelblue;
	int img1s[] = {R.drawable.msg, R.drawable.contact, R.drawable.more };
	int img2s[] = {R.drawable.msgblue, R.drawable.contactblue, R.drawable.moreblue };
	int ids[] =   {R.id.itmsg, R.id.itcontact, R.id.itdollar};
    int nowChoseId = 0;

	public NavigationImageTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View parentView = inflater.inflate(R.layout.item_navigator_image_text, this, true);
		 for(int i = 0; i < ids.length; i++){
			 ImageText it = (ImageText)parentView.findViewById(ids[i]);
			 it.setText(texts[i], color1, color2);
			 it.setImages(img1s[i], img2s[i]);
			 it.setOnClickListener(this);
			 its.add(it);
		 }
		AndroidTools.out("Bottom on create count :"     );

	}
	public void setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener callback){
		this.callback = callback;
	}
	 
	@Override
	public void onClick(View view) {
		open(view.getId());
	}


    /**
     * 转换到目标tab
     * 0则全部关闭
     * @param id
     */
    public void open(int id){
        if(id == nowChoseId){
            return;
        }
        for(int i = 0; i < its.size(); i++){
            if(its.get(i).getId() == id){
                if(callback != null) {
                    callback.onNavigationItemSelected(id);    //选中页传递
                }
                its.get(i).setChecked(true);
            }else{
                its.get(i).setChecked(false);
            }
        }
        nowChoseId = id;
    }

    public void close(){
        open(0);
    }
    public int getNowId(){
        return nowChoseId;
    }
    public boolean isOpen(){
        return nowChoseId > 0;
    }
 



}
