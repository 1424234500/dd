package com.walker.dd.adapter;


import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.common.util.FileUtil;
import com.walker.dd.R;
import com.walker.dd.service.NowUser;
import com.walker.dd.struct.Message;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.dd.util.EmotionUtils;
import com.walker.dd.util.picasso.NetImage;

import com.walker.mode.*;
import com.walker.socket.server_1.plugin.*;
import com.walker.dd.view.ImageText;


/**
 * @author Walker
 * @date 2017-3-28 下午1:00:37
 * Description: 聊天消息适配器,统一群聊 私聊
 */
public   class AdapterLvChat extends  BaseAdapter      {
    private Context context;

    private List<Message>  listItems = null; // listview的数据集合
    private LayoutInflater layoutInflater; // 视图容器


    /**
     * 点击无效问题
     */
    public interface OnClick{
        public void onClick(int position);
    }
    OnClick onClick;
    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }


    //自定义控件集合  布局类型
    //头像，用户名，时间
    private  class ViewHolder{
        public View view;
        public ImageText it;
        public ImageView ivload;

        public TextView tvtime;
        public TextView tvtext;
    }
    //文本
    private class ViewHolderText extends ViewHolder{
    }
    private final class ViewHolderTextSelf extends ViewHolderText{
    }
    private final class ViewHolderTextOther extends ViewHolderText{
    }

    private class ViewHolderFile  extends ViewHolderText{
        public ImageView ivfile;
        public TextView tvinfo;
        public ProgressBar pb;
    }
    private final class ViewHolderFileSelf  extends ViewHolderFile{
    }
    private final class ViewHolderFileOther  extends ViewHolderFile{
    }

    private  class ViewHolderPhoto  extends ViewHolderFile{
        public ImageView ivphoto;
    }
    private final class ViewHolderPhotoSelf  extends ViewHolderPhoto{
    }
    private final class ViewHolderPhotoOther  extends ViewHolderPhoto{
    }


    private class ViewHolderVoice extends ViewHolderFile{
        public ImageView ivvoice;
    }
    private final class ViewHolderVoiceSelf extends ViewHolderVoice{
    }
    private final class ViewHolderVoiceOther extends ViewHolderVoice{
    }




    //布局类型 0开始
    final int TYPE_TEXT = 0;	//自己发的文本
    final int TYPE_FILE = 1;	//自己发的文件
    final int TYPE_PHOTO = 2;	//自己发的图片
    final int TYPE_VOICE = 3;	//自己发的语音
    Bean types = new Bean()
            .set(Key.TEXT, TYPE_TEXT)
            .set(Key.FILE, TYPE_FILE)
            .set(Key.PHOTO, TYPE_PHOTO)
//            .set("voice_true", 4)
//            .set("voice_flase", 5)
            ;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Message bean = listItems.get(position);
//        int type = getItemViewType(position);	//得到No.i条数据布局类型
        int type = types.get(bean.getMsgType(), TYPE_TEXT);
//        User user = new User(bean.get(Key.FROM, new Bean()));
        boolean ifSelf = bean.getFromUserId().equals(NowUser.getId());

        //构建或者取出可复用布局
        if (convertView == null) { //若无可复用布局
            switch (type){
            case TYPE_TEXT:
                ViewHolderText viewHolderText = new ViewHolderText();
                convertView = layoutInflater.inflate(ifSelf ? R.layout.chat_item_text_right : R.layout.chat_item_text_left, null);	// 获取list_item布局文件的视图

                convertView.setTag(viewHolderText);// 设置控件集到convertView
            break;
            case TYPE_FILE:
                ViewHolderFile viewHolderFile = new ViewHolderFile();
                convertView = layoutInflater.inflate(ifSelf ? R.layout.chat_item_file_right : R.layout.chat_item_file_left, null);	// 获取list_item布局文件的视图
                viewHolderFile.ivfile = (ImageView) convertView .findViewById(R.id.ivfile);
                viewHolderFile.pb = (ProgressBar) convertView .findViewById(R.id.pb);
                viewHolderFile.tvinfo = (TextView) convertView .findViewById(R.id.tvinfo);

                convertView.setTag(viewHolderFile);// 设置控件集到convertView
                break;
            case TYPE_PHOTO:
                ViewHolderPhoto viewHolderPhoto = new ViewHolderPhoto();
                convertView = layoutInflater.inflate(ifSelf ? R.layout.chat_item_photo_right : R.layout.chat_item_photo_left, null);	// 获取list_item布局文件的视图
                viewHolderPhoto.ivphoto = (ImageView) convertView .findViewById(R.id.ivphoto);

                convertView.setTag(viewHolderPhoto);// 设置控件集到convertView
                break;
            default:
                AndroidTools.log("未明确的类型适配????");
            }
            //公用视图配置
            ViewHolder viewHolder = (ViewHolder)convertView.getTag();
            viewHolder.view = (View) convertView .findViewById(R.id.view);
            viewHolder.it = (ImageText) convertView .findViewById(R.id.it);
            viewHolder.ivload = (ImageView) convertView .findViewById(R.id.ivload);
            viewHolder.tvtime = (TextView) convertView .findViewById(R.id.tvtime);
            viewHolder.tvtext = (TextView) convertView .findViewById(R.id.tvtext);


        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        /**
         .set(Key.ID, msgid)
         .set(Key.STA, Key.STA_LOADING)
         .set(Key.TYPE, Key.TEXT)
         .set(Key.FROM, NowUser.getUser())
         .set(Key.TO, toid)
         .set(Key.TIME, TimeUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
         .set(Key.TEXT, str)
         .set(Key.FILE, file)
         */
        //共用属性设置
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onClick(position);
            }
        });
        viewHolder.tvtime.setText(bean.getTime());// bean.get(Key.TIME, Key.TIME)) ;

        viewHolder.it.setText(bean.getFromUserName(), R.color.black, R.color.blue);
        NetImage.loadProfile(context, bean.getFromUserId(), viewHolder.it.iv);

        viewHolder.tvtext.setText(
                EmotionUtils.getEmotionContent(context,
                        viewHolder.tvtext, bean.getText()));
        String sta = bean.getSta();
        String path = bean.getFile();

        //私有属性设置
        switch (type){
            case TYPE_TEXT:
                ViewHolderText viewHolderText = (ViewHolderText) viewHolder;
                break;
            case TYPE_FILE:
                ViewHolderFile viewHolderFile = (ViewHolderFile) viewHolder;
                viewHolderFile.ivfile.setImageResource(Constant.getFileImageByType(FileUtil.getFileType(path)));
                if(!sta.equals(Key.STA_TRUE) && !sta.equals(Key.STA_FALSE) ){
                    viewHolderFile.pb.setVisibility(View.VISIBLE);
                    viewHolderFile.tvinfo.setVisibility(View.VISIBLE);
                    viewHolderFile.tvinfo.setText(bean.getInfo());
                    try {
                        viewHolderFile.pb.setProgress(Integer.valueOf(sta));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    viewHolderFile.pb.setVisibility(View.GONE);
                    viewHolderFile.tvinfo.setVisibility(View.GONE);
                }
                break;
            case TYPE_PHOTO:
                ViewHolderPhoto viewHolderPhoto = (ViewHolderPhoto) viewHolder;
                NetImage.load(context, path, viewHolderPhoto.ivphoto);
                break;
            default:
                AndroidTools.log("未明确的类型适配????");
        }

        viewHolder.ivload.setVisibility(View.VISIBLE);
        switch (sta){

            case Key.STA_TRUE:
                viewHolder.ivload.setVisibility(View.INVISIBLE);
                break;
            case Key.STA_FALSE:
                viewHolder.ivload.setImageResource(R.drawable.cc);
                break;
            case Key.STA_LOADING:
            default:
                viewHolder.ivload.setImageResource(R.drawable.ccd);

        }
        return convertView;
    }

    //必须实现，通知adapter有几种布局类型 0开始 必须有序
    @Override
    public int getViewTypeCount() {
        return types.size() * 2;
    }
    //必须实现，让adapter可控布局类型
    @Override
    public int getItemViewType(int position) {
        Message bean = listItems.get(position);
        String key = bean.getMsgType();

        boolean ifSelf = bean.getFromUserId().equals(NowUser.getId());
        return types.get(key, 0) * 2 + (ifSelf ? 1 : 0);
    }


    public AdapterLvChat(Context context, List<Message> listItems) {
        layoutInflater = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return listItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }





}
