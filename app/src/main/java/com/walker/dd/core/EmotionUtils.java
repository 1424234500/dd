package com.walker.dd.core;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.walker.dd.R;


//表情加载类,可自己添加多种表情，分别建立不同的map存放和不同的标志符即可
public class EmotionUtils {
    /**
     * 表情类型标志符
     */
    public static final int EMOTION_CLASSIC_TYPE=0x0001;//经典表情
    /**
     * key-表情文字;
     * value-表情图片资源
     */
    public static HashMap<String, Integer> map;
    public static String regex = "\\[[\u4e00-\u9fa5\\w]+\\]";
    
    
    public static SpannableString getEmotionContent(final Context context, final TextView tv, String source) {
        SpannableString spannableString = new SpannableString(source);
        Resources res = context.getResources();
        Pattern patternEmotion = Pattern.compile(regex);
        Matcher matcherEmotion = patternEmotion.matcher(spannableString);
        while (matcherEmotion.find()) {
            // 获取匹配到的具体字符
            String key = matcherEmotion.group();
            // 匹配字符串的开始位置
            int start = matcherEmotion.start();
            // 利用表情名字获取到对应的图片
            Integer imgRes = EmotionUtils.getImgByName(key);
            if (imgRes != null) {
                // 压缩表情图片
                int size = (int) tv.getTextSize()*13/10;
                Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes);
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                ImageSpan span = new ImageSpan(context, scaleBitmap);
                spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }
    static {
        map = new HashMap<String, Integer>();
        map.put("[嘻嘻]", R.drawable.d_xixi);
        map.put("[哈哈]", R.drawable.d_haha);
        map.put("[爱你]", R.drawable.d_aini);
        map.put("[挖鼻屎]", R.drawable.d_wabishi);
        map.put("[吃惊]", R.drawable.d_chijing);
        map.put("[泪]", R.drawable.d_lei);
        map.put("[馋嘴]", R.drawable.d_chanzui);
        map.put("[抓狂]", R.drawable.d_zhuakuang);
        map.put("[哼]", R.drawable.d_heng);
        map.put("[可爱]", R.drawable.d_keai);
        map.put("[怒]", R.drawable.d_nu);
        map.put("[汗]", R.drawable.d_han);
        map.put("[害羞]", R.drawable.d_haixiu);
        map.put("[睡觉]", R.drawable.d_shuijiao);
        map.put("[钱]", R.drawable.d_qian);
        map.put("[偷笑]", R.drawable.d_touxiao);
        map.put("[笑哭]", R.drawable.d_xiaoku);
        map.put("[狗狗]", R.drawable.d_doge);
        map.put("[喵喵]", R.drawable.d_miao);
        map.put("[酷]", R.drawable.d_ku);
        map.put("[衰]", R.drawable.d_shuai);
        map.put("[闭嘴]", R.drawable.d_bizui);
        map.put("[鄙视]", R.drawable.d_bishi);
        map.put("[花心]", R.drawable.d_huaxin);
        map.put("[鼓掌]", R.drawable.d_guzhang);
        map.put("[悲伤]", R.drawable.d_beishang);
        map.put("[思考]", R.drawable.d_sikao);
        map.put("[亲亲]", R.drawable.d_qinqin);
        map.put("[怒骂]", R.drawable.d_numa);
        map.put("[太开心]", R.drawable.d_taikaixin);
        map.put("[懒得理你]", R.drawable.d_landelini);
        map.put("[右哼哼]", R.drawable.d_youhengheng);
        map.put("[左哼哼]", R.drawable.d_zuohengheng);
        map.put("[嘘]", R.drawable.d_xu);
        map.put("[委屈]", R.drawable.d_weiqu);
        map.put("[吐]", R.drawable.d_tu);
        map.put("[可怜]", R.drawable.d_kelian);
        map.put("[打哈气]", R.drawable.d_dahaqi);
        map.put("[挤眼]", R.drawable.d_jiyan);
        map.put("[失望]", R.drawable.d_shiwang);
        map.put("[顶]", R.drawable.d_ding);
        map.put("[疑问]", R.drawable.d_yiwen);
        map.put("[困]", R.drawable.d_kun);
        map.put("[感冒]", R.drawable.d_ganmao);
        map.put("[拜拜]", R.drawable.d_baibai);
        map.put("[黑线]", R.drawable.d_heixian);
        map.put("[阴险]", R.drawable.d_yinxian);
        map.put("[打脸]", R.drawable.d_dalian);
        map.put("[傻眼]", R.drawable.d_shayan);
        map.put("[猪头]", R.drawable.d_zhutou);
        map.put("[熊猫]", R.drawable.d_xiongmao);
        map.put("[兔子]", R.drawable.d_tuzi);

       
        
        
    }
    /**
     * 根据名称获取当前表情图标R值
     * @param imgName 名称
     * @return
     */
    public static int getImgByName(String imgName) {
        Integer  integer = map.get(imgName);
        return integer == null ? -1 : integer;
    }
  
    public static Map<String, Integer> getEmojiMap(){
     
        return map;
    }
}