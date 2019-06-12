package com.walker.dd.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.walker.dd.R;
import com.walker.dd.util.AndroidTools;


/**
 * 图片切换视图
 */
public class NavigationImageView extends LinearLayout implements View.OnClickListener {

    Context context;

    int img1s[] = {R.drawable.voice, R.drawable.photo, R.drawable.graph, R.drawable.emoji, R.drawable.more  };
    int img2s[] = {R.drawable.voiceblue, R.drawable.photoblue, R.drawable.graphblue, R.drawable.emojiblue, R.drawable.moreblue  };
    int ids[] =   {R.id.ivvoice, R.id.ivphoto, R.id.ivgraph, R.id.ivemoji, R.id.ivmore};
    android.widget.ImageView ivs[] = new android.widget.ImageView[ids.length];
    int nowChoseId = 0;


	public NavigationImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View parentView = inflater.inflate(R.layout.item_navigator_image, this, true);
		for(int i = 0; i < ids.length; i++){
            android.widget.ImageView  it = (android.widget.ImageView )parentView.findViewById(ids[i]);
			 it.setOnClickListener(this);
			 ivs[i] = it;
		}
		AndroidTools.out("NavigationImageView on create count :"     );

	}


    @Override
    public void onClick(View v) {
	    int id = v.getId();
        open(id);
    }

    /**
     * 转换到目标tab
     * 0则全部关闭
     * @param id
     */
    public void open(int id){
        for(int i = 0; i < ivs.length; i++){
            if(id == ivs[i].getId()){
                if(nowChoseId == id){//重复点，关闭
                    if(this.onControl != null){
                        onControl.onClose();
                    }
                    nowChoseId = 0;
                    ivs[i].setImageResource(img1s[i]);
                }else{//从其它切换到，从关闭到打开
                    ivs[i].setImageResource(img2s[i]);
                    nowChoseId = id;
                    if(this.onControl != null){
                        onControl.onOpen(id);
                    }
                }

            }else{
                ivs[i].setImageResource(img1s[i]);
            }
        }
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

    OnControl onControl;

    public void setOnControl(OnControl onControl){
        this.onControl = onControl;
    }
    public interface OnControl{
        public void onClose();
        public void onOpen(int id);
    }
   
 



}
