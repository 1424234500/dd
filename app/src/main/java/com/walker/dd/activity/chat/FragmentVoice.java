package com.walker.dd.activity.chat;
 
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.walker.common.util.Tools;
import com.walker.dd.R;
import com.walker.dd.activity.FragmentBase;
import com.walker.dd.service.NowUser;
import com.walker.dd.core.AndroidTools;
import com.walker.dd.core.AudioRecoderUtils;
import com.walker.dd.core.Constant;
import com.walker.dd.core.MyFile;
import com.walker.dd.core.MyMediaPlayer;

public class FragmentVoice extends FragmentBase implements View.OnTouchListener {
//	VoiceView vl;
    View view;

    TextView tvtip, tvtime ;
    ImageView ivpress ;

    View viewcancelok;
    ImageView ivplay,ivdel;
    TextView tvcancel,tvok;
    int status = 0;//0默认，1按住状态，2试听状态
    int flag = 0;	//动画状态
    int play = 0;	//播放暂停状态

    AudioRecoderUtils mAudioRecoderUtils;
    String file = "";	//操作的录音文件名，录制完毕后存储

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidTools.log("FragmentVoice onCreate");
		
	}
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {  
    	View viewVoice = inflater.inflate(R.layout.layout_voice, container, false);
		AndroidTools.log("FragmentVoice onCreateView");

        tvtime = ((TextView)viewVoice.findViewById(R.id.tvtime));
        tvtip = ((TextView)viewVoice.findViewById(R.id.tvtip));
        ivpress = ((ImageView)viewVoice.findViewById(R.id.ivpressvoice));

        ivplay = ((ImageView)viewVoice.findViewById(R.id.ivplay));
        ivdel = ((ImageView)viewVoice.findViewById(R.id.ivdel));

        viewcancelok = ((View)viewVoice.findViewById(R.id.viewcancelok));
        tvcancel = ((TextView)viewVoice.findViewById(R.id.tvcancel));
        tvok = ((TextView)viewVoice.findViewById(R.id.tvok));

        mAudioRecoderUtils = new AudioRecoderUtils();
        //录音回调
        mAudioRecoderUtils.setOnAudioStatusUpdateListener(new AudioRecoderUtils.OnAudioStatusUpdateListener() {
            @Override
            public void onUpdate(double db, long time) {  //录音中....db为声音分贝，time为录音时长
                //根据分贝值来设置录音时话筒图标的上下波动，下面有讲解
                // mImageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
                tvtime.setText(Tools.calcTime(time));
            }
            @Override
            public void onStop(String filePath) {   //录音结束，filePath为保存路径
                AndroidTools.log( "" + filePath);
                file = filePath;
            }
        });

        onStatusInit();

        viewVoice.setOnTouchListener(this);
        return viewVoice;
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        switch(e.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(status != 2){
                    if(AndroidTools.isOnClick(ivpress, x, y)){
                        AndroidTools.log("ACTION_DOWN ivpress");

                        // 动画效果
                        Animation myAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_voice_press);
                        ivpress.startAnimation(myAnimation);
                        onStatusPress();

                        //AndroidTools.systemVoiceToast(context);
                        //开始录音

                        // /sdcard/mycc/record/100-101020120120120.amr
                        String filePath = Constant.dirVoice + NowUser.getId() + "-" + System.currentTimeMillis() + ".amr" ;
                        mAudioRecoderUtils.startRecord(filePath);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(status == 1){
                    if(AndroidTools.isOnClick(ivplay, x, y)){//试听
                        AndroidTools.log("ACTION_UP ivplay");
                        onStatusPlay();
                        mAudioRecoderUtils.stopRecord();        //结束录音（保存录音文件）
                    } else if(AndroidTools.isOnClick(ivdel, x, y)){//取消发送
                        AndroidTools.log("ACTION_UP ivdel");
                        onStatusInit();
                        mAudioRecoderUtils.cancelRecord();    //取消录音（不保存录音文件）
                    } else{	 //发送语音文件
                        AndroidTools.log("ACTION_UP send");
                        onStatusInit();
                        mAudioRecoderUtils.stopRecord();        //结束录音（保存录音文件）
                        if(onVoice != null)
                            onVoice.onSend(file);
                    }
                }else 	if(status == 2 && AndroidTools.isOnClick(ivpress, x, y)){
                    //点击播放
                    if(play == 0){
                        ivpress.setImageResource(R.drawable.voice_play_play_on);
                        play = 1;

                        MyMediaPlayer.getInstance().play(file,new MyMediaPlayer.OnPlay() {
                            @Override
                            public void onPlayEnd(MediaPlayer mediaPlayerr) {
                                //mediaPlayerr.release();
                                ivpress.setImageResource(R.drawable.voice_play_play);
                                play = 0;
                            }
                        });

                    }else{//停止播放
                        ivpress.setImageResource(R.drawable.voice_play_play);
                        play = 0;
                        MyMediaPlayer.getInstance().stop();

                        //MyMediaPlayer.release();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if(status == 1){
                    if(AndroidTools.isOnClick(ivplay, x, y)){
                        //	Tools.log("ACTION_MOVE ivplay");
                        tvtip.setText("松手试听");
                        tvtime.setVisibility(View.INVISIBLE);
                        tvtip.setVisibility(View.VISIBLE);

                        ivplay.setImageResource(R.drawable.voice_play_on);
                        if(flag == 0){
                            // 动画效果
                            Animation myAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scaleup);
                            ivplay.startAnimation(myAnimation);
                            flag = 1;
                        }
                    } else if(AndroidTools.isOnClick(ivdel, x, y)){
                        //		Tools.log("ACTION_MOVE ivdel");
                        tvtip.setText("松手取消发送");
                        tvtime.setVisibility(View.INVISIBLE);
                        tvtip.setVisibility(View.VISIBLE);

                        ivdel.setImageResource(R.drawable.voice_del_on);
                        if(flag == 0){
                            // 动画效果
                            Animation myAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scaleup);
                            ivdel.startAnimation(myAnimation);
                            flag = 1;
                        }
                    }else{
                        //Tools.log("ACTION_MOVE nothing");
                        flag = 0;
                        tvtime.setVisibility(View.VISIBLE);
                        tvtip.setVisibility(View.INVISIBLE);

                        ivdel.setImageResource(R.drawable.voice_del);
                        ivplay.setImageResource(R.drawable.voice_play);
                    }
                }
                break;


        }


        return true;
    }
    //初始状态，不显示取消/发送，也不显示
    void onStatusInit(){
        status = 0;
        tvtip.setVisibility(View.VISIBLE);
        tvtime.setVisibility(View.INVISIBLE);
        ivplay.setVisibility(View.INVISIBLE);
        ivdel.setVisibility(View.INVISIBLE);
        viewcancelok.setVisibility(View.INVISIBLE);
        tvtip.setText("按住说话");

    }
    //按住说话状态,显示试听/取消 图标
    void onStatusPress(){
        status = 1;
        tvtime.setVisibility(View.VISIBLE);
        tvtip.setVisibility(View.INVISIBLE);

        ivplay.setVisibility(View.VISIBLE);
        ivdel.setVisibility(View.VISIBLE);
        viewcancelok.setVisibility(View.INVISIBLE);
    }
    //试听状态,显示文本按钮，不现实图标，改变ivpress样式及其作用
    void onStatusPlay(){
        status = 2;

        tvtime.setVisibility(View.VISIBLE);
        tvtip.setVisibility(View.INVISIBLE);

        ivplay.setVisibility(View.INVISIBLE);
        ivdel.setVisibility(View.INVISIBLE);
        viewcancelok.setVisibility(View.VISIBLE);

        ivpress.setImageResource(R.drawable.voice_play_play);

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //取消发送，删除语音文件
                ivpress.setImageResource(R.drawable.voice_icon);
                onStatusInit();
                MyFile.deleteFileInSDCard(file);
            }
        });
        tvok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //停止播放先
                play = 0;
                MyMediaPlayer.getInstance().stop();

                //发送文件，<删除>
                ivpress.setImageResource(R.drawable.voice_icon);
                onStatusInit();
                if(onVoice != null)
                    onVoice.onSend(file);
            }
        });
        //播放按钮ivpress改变
    }


    public void setOnVoice(OnVoice onVoice){
        this.onVoice = onVoice;
    }
    OnVoice onVoice;
    public interface OnVoice{
        public void onSend(String file);
    }


    
    //设置存储文件名前缀
    public void setFileBegin(String toid) {
		begin = toid;
	} 
    String begin = "";

    /**
     * 传递引用对象数据初始化
     *
     * @param data
     */
    @Override
    public void setData(Object data) {

    }

    /**
     * 更新数据 后 通知更新页面
     */
    @Override
    public void notifyDataSetChanged() {

    }

    /**
     * 数据广播传递 activity通过baseAc收到广播后派发给当前fragment
     *
     * @param msg
     */
    @Override
    public void onReceive(String msg) {

    }
}