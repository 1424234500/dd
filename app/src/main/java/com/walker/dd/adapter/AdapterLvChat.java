package com.walker.dd.adapter;


import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walker.common.util.Bean;
import com.walker.dd.R;
import com.walker.dd.service.User;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.EmotionUtils;
import com.walker.dd.util.MySP;
import com.walker.socket.server_1.Key;
import com.walker.dd.view.ImageText;


/**
 * @author Walker
 * @date 2017-3-28 下午1:00:37
 * Description: 聊天消息适配器,统一群聊 私聊
 */
public   class AdapterLvChat extends  BaseAdapter      {
    private Context context;

//.set(Key.TYPE, Key.TEXT)
//.set(Key.FROM, User.getId())
//.set(Key.NAME, User.getUser())
//.set(Key.PROFILE, "")
//.set(Key.TIME, TimeUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
//.set(Key.TEXT, str)
    private List<Bean>  listItems = null; // listview的数据集合
    private LayoutInflater layoutInflater; // 视图容器



    //自定义控件集合  布局类型
    //头像，用户名，时间
    private  class ViewHolder{
//        public ImageView ivprofile;
//        public TextView tvusername;
        public ImageText it;
        public TextView tvtime;
    }
    //文本
    private class ViewHolderText extends ViewHolder{
        public TextView tvtext;
    }
    private final class ViewHolderTextSelf extends ViewHolderText{
    }
    private final class ViewHolderTextOther extends ViewHolderText{
    }

    private class ViewHolderFile  extends ViewHolder{
        public TextView tvdown;
        public TextView tvtext;
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
    final int TYPE_FILE = 2;	//自己发的文件
    final int TYPE_VOICE = 4;	//自己发的语音
    final int TYPE_PHOTO = 6;	//自己发的图片
    Bean types = new Bean()
            .set(Key.TEXT, TYPE_TEXT)
//            .set("voice_true", 2)
//            .set("voice_false", 3)
//            .set("voice_true", 4)
//            .set("voice_flase", 5)
//            .set("file_true", 6)
//            .set("file_false", 7)
            ;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //TIME,TYPE TEXT VOICE,FROMID,TOID,PROFILEPATH
        Bean data = listItems.get(position);
//        int type = getItemViewType(position);	//得到No.i条数据布局类型
        int type = types.get(data.get(Key.TYPE, Key.TEXT), TYPE_TEXT);
        boolean ifSelf = data.get(Key.FROM, "").equals(User.getId());

        //构建或者取出可复用布局
        if (convertView == null) { //若无可复用布局
            switch (type){
            case TYPE_TEXT:
                ViewHolderText viewHolderText = new ViewHolderText();
                convertView = layoutInflater.inflate(ifSelf ? R.layout.chat_item_text_right : R.layout.chat_item_text_left, null);	// 获取list_item布局文件的视图
                viewHolderText.tvtext = (TextView) convertView .findViewById(R.id.tvtext);

                convertView.setTag(viewHolderText);// 设置控件集到convertView
            break;
            default:
                AndroidTools.log("未明确的类型适配????");
            }
            //公用视图配置
            ViewHolder viewHolder = (ViewHolder)convertView.getTag();
//            viewHolder.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
//            viewHolder.tvusername = (TextView) convertView .findViewById(R.id.tvusername);
            viewHolder.it = (ImageText) convertView .findViewById(R.id.it);
            viewHolder.tvtime = (TextView) convertView .findViewById(R.id.tvtime);

        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();


//.set(Key.TYPE, Key.TEXT)
//.set(Key.FROM, User.getId())
//.set(Key.NAME, User.getUser())
//.set(Key.PROFILE, "")
//.set(Key.TIME, TimeUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
//.set(Key.TEXT, str)
        //共用属性设置
        viewHolder.tvtime.setText( data.get(Key.TIME, Key.TIME)) ;
//        NetImage.loadProfile(context, MapListUtil.getList(listItems, position, "PROFILEPATH").toString(), viewHolderTextSelf.ivprofile);
//        viewHolder.tvusername.setText( data.get("USERNAME", "username")) ;
        viewHolder.it.setText(data.get(Key.NAME, Key.NAME), R.color.black, R.color.blue);
        //私有属性设置
        switch (type){
            case TYPE_TEXT:
                ViewHolderText viewHolderText = (ViewHolderText) viewHolder;
                viewHolderText.tvtext.setText(EmotionUtils.getEmotionContent(convertView.getContext(),viewHolderText.tvtext,data.get(Key.TEXT, "")));
                break;
            default:
                AndroidTools.log("未明确的类型适配????");
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
        boolean ifSelf = bean.get(Key.FROM, "").equals(User.getId());
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
