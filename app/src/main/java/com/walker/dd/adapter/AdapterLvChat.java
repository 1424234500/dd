package com.walker.dd.adapter;


import java.io.File;
import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.common.util.FileUtil;
import com.walker.dd.R;
import com.walker.dd.service.MsgModel;
import com.walker.dd.service.NowUser;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.dd.util.EmotionUtils;
import com.walker.socket.server_1.Key;
import com.walker.dd.view.ImageText;
import com.walker.socket.server_1.session.User;


/**
 * @author Walker
 * @date 2017-3-28 下午1:00:37
 * Description: 聊天消息适配器,统一群聊 私聊
 */
public   class AdapterLvChat extends  BaseAdapter      {
    private Context context;

    private List<Bean>  listItems = null; // listview的数据集合
    private LayoutInflater layoutInflater; // 视图容器



    //自定义控件集合  布局类型
    //头像，用户名，时间
    private  class ViewHolder{
//        public ImageView ivprofile;
//        public TextView tvusername;
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
    }
    private final class ViewHolderFileSelf  extends ViewHolderFile{
    }
    private final class ViewHolderFileOther  extends ViewHolderFile{
    }


    private class ViewHolderVoice extends ViewHolderFile{
        public ImageView ivvoice;
    }
    private final class ViewHolderVoiceSelf extends ViewHolderVoice{
    }
    private final class ViewHolderVoiceOther extends ViewHolderVoice{
    }

    private  class ViewHolderPhoto  extends ViewHolderFile{
        public ImageView ivphoto;
    }
    private final class ViewHolderPhotoSelf  extends ViewHolderPhoto{
    }
    private final class ViewHolderPhotoOther  extends ViewHolderPhoto{
    }



    //布局类型 0开始
    final int TYPE_TEXT = 0;	//自己发的文本
    final int TYPE_FILE = 1;	//自己发的文件
    final int TYPE_VOICE = 2;	//自己发的语音
    final int TYPE_PHOTO = 3;	//自己发的图片
    Bean types = new Bean()
            .set(Key.TEXT, TYPE_TEXT)
            .set(Key.FILE, TYPE_FILE)
//            .set("voice_false", 3)
//            .set("voice_true", 4)
//            .set("voice_flase", 5)
            ;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Bean bean = listItems.get(position);
//        int type = getItemViewType(position);	//得到No.i条数据布局类型
        int type = types.get(bean.get(Key.TYPE, Key.TEXT), TYPE_TEXT);
        User user = new User(bean.get(Key.FROM, new Bean()));
        boolean ifSelf = user.getId().equals(NowUser.getId());

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

                convertView.setTag(viewHolderFile);// 设置控件集到convertView
                break;
            default:
                AndroidTools.log("未明确的类型适配????");
            }
            //公用视图配置
            ViewHolder viewHolder = (ViewHolder)convertView.getTag();
//            viewHolder.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
//            viewHolder.tvusername = (TextView) convertView .findViewById(R.id.tvusername);
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
        viewHolder.tvtime.setText( bean.get(Key.TIME, Key.TIME)) ;
//        NetImage.loadProfile(context, MapListUtil.getList(listItems, position, "PROFILEPATH").toString(), viewHolderTextSelf.ivprofile);
        viewHolder.it.setText(user.getName(), R.color.black, R.color.blue);
        viewHolder.tvtext.setText(
                EmotionUtils.getEmotionContent(context,
                        viewHolder.tvtext, bean.get(Key.TEXT, "")));
        String sta = bean.get(Key.STA, Key.STA_FALSE);

        //私有属性设置
        switch (type){
            case TYPE_TEXT:
                ViewHolderText viewHolderText = (ViewHolderText) viewHolder;
                break;
            case TYPE_FILE:
                ViewHolderFile viewHolderFile = (ViewHolderFile) viewHolder;
                String path = bean.get(Key.TEXT, "");
                switch (sta){
                    case Key.STA_TRUE:
                        if(new File(path).exists()){
                            int resid = Constant.getFileImageByType(FileUtil.getFileType(path));
                            viewHolderFile.ivfile.setImageResource(resid);
                        }else{
                            bean.set(Key.STA, Key.STA_FALSE);
                        }
                        break;
                }

                break;
            default:
                AndroidTools.log("未明确的类型适配????");
        }

        viewHolder.ivload.setVisibility(View.VISIBLE);
        switch (sta){
            case Key.STA_LOADING:
                viewHolder.ivload.setImageResource(R.drawable.ccd);
                break;
            case Key.STA_FALSE:
                viewHolder.ivload.setImageResource(R.drawable.cc);
                break;
            default:
                viewHolder.ivload.setVisibility(View.INVISIBLE);
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
        Bean bean = listItems.get(position);
        String key = bean.get(Key.TYPE, Key.TEXT);

        User user = new User(bean.get(Key.FROM, new Bean()));
        boolean ifSelf = user.getId().equals(NowUser.getId());
        return types.get(key, 0) * 2 + (ifSelf ? 1 : 0);
    }


    public AdapterLvChat(Context context, List<Bean> listItems) {
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
