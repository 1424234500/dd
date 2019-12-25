package com.walker.dd.util;

import android.content.Context;

import com.walker.common.util.Bean;
import com.walker.common.util.JsonUtil;
import com.walker.common.util.MD5;
import com.walker.common.util.Tools;
import com.walker.core.exception.ErrorException;

import com.walker.mode.*;
import com.walker.socket.server_1.plugin.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;


/**
 * 聊天机器人
 * tuling
 * tencent
 * baidu
 *
 */

public class RobotAuto {
//    http://www.tuling123.com/openapi/api?key=bfbf6432b655493b9e861b470bca9921&info=[可爱]
    public final static String TULING_APIKEY="17022af7b2824c3f917682c184b0ece9";   //bfbf6432b655493b9e861b470bca9921
    public static String getUrlTuling(String word) {
		return "http://www.tuling123.com/openapi/api?key=" + TULING_APIKEY + "&info=" + word;
	}

	public static String parseTulingRes(String jsonstr){
        if(!Tools.notNull( jsonstr))return "nothing";
        String res = "";
        List<Map<String,Object>> list;
        Bean map = JsonUtil.get(jsonstr);
        int code = map.get("code", 0);
        switch(code){
        case 100000:
            res = map.get("text", "");
            break;
        case 200000:
            res = map.get("text", "") + "\n" + map.get("url", "");;
            break;
        case 302000:
            res = map.get("text", "") +  "\n" ;
            list = map.get("text", new ArrayList<Map<String, Object>>()); //JsonUtil.getList(jsonstr, "list");
            for(Map<String,Object> item: list){
                res += "" + item.get("source").toString() + "\n";
                res += "" + item.get("article").toString() + "\n";
                res += "" + item.get("detailurl").toString() + "\n";
//					"article": "工信部:今年将大幅提网速降手机流量费",
//					"source": "网易新闻",
//					"icon": "",
//					"detailurl": "http://news.163.com/15/0416/03/AN9SORGH0001124J.html"
            }
            break;
        default:
            res = map.get("text", "");
            break;
        }
        Tools.out(  "聊天机器人返回." + jsonstr + "  解析结果." + res );
        return res;
    }
//100000	文本类
//	"code":100000,
//	"text":"你也好 嘻嘻"
	
//200000	链接类
//	"code": 200000,
//	"text": "亲,已帮你找到图片",
//	"url": "http://m.image.so.com/i?q=%E5%B0%8F%E7%8B%97"
	
//302000	新闻类
//	"code": 302000,
//	"text": "亲,已帮您找到相关新闻",
//	"list": [
//	{
//	"article": "工信部:今年将大幅提网速降手机流量费",
//	"source": "网易新闻",
//	"icon": "",
//	"detailurl": "http://news.163.com/15/0416/03/AN9SORGH0001124J.html"
//	},
//	{
//	"article": "北京最强沙尘暴午后袭沪 当地叫停广场舞",
//	"source": "网易新闻",
//	"icon": "",
//	"detailurl": "http://news.163.com/15/0416/14/ANB2VKVC00011229.html"
//	},
//	{
//	"article": "公安部:小客车驾照年内试点自学直考",
//	"source": "网易新闻",
//	"icon": "",
//	"detailurl": "http://news.163.com/15/0416/01/AN9MM7CK00014AED.html"
//	} ]
	            
//308000	菜谱类
	//"text": "亲,已帮您找到菜谱信息",
	//"list": [{
	//"name": "鱼香肉丝",
	//"icon": "http://i4.xiachufang.com/image/280/cb1cb7c49ee011e38844b8ca3aeed2d7.jpg",
	//"info": "猪肉、鱼香肉丝调料 | 香菇、木耳、红萝卜、黄酒、玉米淀粉、盐",
	//"detailurl": "http://m.xiachufang.com/recipe/264781/"
	//}]
//313000（儿童版）	儿歌类
//314000（儿童版）	诗词类
//40001	参数key错误
//	40002	请求内容info为空
//	40004	当天请求次数已使用完
//	40007	数据格式异常



//  腾讯接口
//    https://ai.qq.com/doc/nlpchat.shtml
    public final static String TENCENT_URL = "https://api.ai.qq.com/fcgi-bin/nlp/nlp_textchat";
    public final static String TENCENT_APPID = "2117236035";
    public final static String TENCENT_APIKEY="JeUEjcyohO0MrWCe";
    public static Bean getTencentParam(String msg, String session){
        Bean param = new Bean()
                .put("app_id", RobotAuto.TENCENT_APPID)
                .put("time_stamp", System.currentTimeMillis() / 1000)
                .put("nonce_str", Math.random() + "")
                .put("sign", "")
                .put("session", session)
                .put("question", msg);
        param.put("sign", tencentMakeSign(TENCENT_APIKEY, param));
        return param;
    }
    public static RequestBody getTencentAutoTextRequest(String msg, String session){
        FormBody.Builder builder = new FormBody.Builder();
//                .add("search", "Jurassic Park");
        Bean bean = getTencentParam(msg, session);
        for(Object key : bean.keySet()){
            builder.add(String.valueOf(key), String.valueOf(bean.get(key)));
        }
        return builder.build();
    }



    /**
     * SIGN签名生成算法-JAVA版本 通用。默认参数都为UTF-8适用
     *
     * @param params 请求参数集,所有参数必须已转换为字符串类型
     * @return 签名
     * @throws IOException
     */
    public static String tencentMakeSign(String appKey, Bean params){
        try {

            Map<Object, Object> sortedParams = new TreeMap<>(params);
            Set<Map.Entry<Object, Object>> entrys = sortedParams.entrySet();
            StringBuilder baseString = new StringBuilder();
            for (Map.Entry<Object, Object> param : entrys) {
                if (param.getValue() != null && !"".equals(String.valueOf(param.getKey()).trim())
                        && !"sign".equals(String.valueOf(param.getKey()).trim())
                        && !"".equals(param.getValue())) {
                    baseString
                            .append(String.valueOf(param.getKey()).trim())
                            .append("=")
                            .append(URLEncoder.encode(param.getValue().toString(), "UTF-8"))
                            .append("&");
                }
            }
            if (baseString.length() > 0) {
                baseString.deleteCharAt(baseString.length() - 1).append("&app_key=")
                        .append(appKey);
            }
            String sign = MD5.makeStr(baseString.toString());
//            System.out.println("sign:" + sign.toUpperCase());
            return sign.toUpperCase();
        } catch (Exception ex) {
            throw new ErrorException(ex);
        }
    }

    public static String parseTencentRes(String jsonstr) {

        String res = "";
        try {
            Bean map = JsonUtil.get(jsonstr);
            int code = map.get("ret", -1);
            Bean data = map.get("data", new Bean());
            res = data.get("answer", "没有回复?");
        }catch (Exception e){
            res = jsonstr;
        }
        return res;
    }

//1. 接口描述
//    基础闲聊接口提供基于文本的基础聊天能力,可以让您的应用快速拥有具备上下文语义理解的机器聊天功能。
//
//2. 请求参数
//    参数名称	是否必选	数据类型	数据约束	示例数据	描述
//    app_id	是	int	正整数	1000001	应用标识（AppId）
//    time_stamp	是	int	正整数	1493468759	请求时间戳（秒级）
//    nonce_str	是	string	非空且长度上限32字节	fa577ce340859f9fe	随机字符串
//    sign	是	string	非空且长度固定32字节		签名信息,详见接口鉴权
//    session	是	string	UTF-8编码,非空且长度上限32字节	10000	会话标识（应用内唯一）
//    question	是	string	UTF-8编码,非空且长度上限300字节	你叫啥	用户输入的聊天内容
//3. 响应参数
//    参数名称	是否必选	数据类型	描述
//    ret	是	int	返回码； 0表示成功,非0表示出错
//    msg	是	string	返回信息；ret非0时表示出错时错误原因
//    data	是	object	返回数据；ret为0时有意义
//    session	是	string	UTF-8编码,非空且长度上限32字节
//    answer	是	string	UTF-8编码,非空
//4. 参考示例
//    假设示例请求数据如下。
//
//    参数名称	参数数据	描述
//    app_id	1000001	仅供参考
//    session	10000	仅供参考
//    question	你叫啥	使用UTF-8编码
//    time_stamp		实时计算
//    nonce_str		实时计算
//    sign		实时计算
//    假设应用密钥为：a95eceb1ac8c24ee28b70f7dbba912bf。
//
//    下面使用PHP实现该HTTP API调用,其中getReqSign、doHttpPost可以从接口鉴权获取。
//
//// 设置请求数据
//    $appkey = 'a95eceb1ac8c24ee28b70f7dbba912bf';
//    $params = array(
//            'app_id'     => '1000001',
//            'session'    => '10000',
//            'question'   => '你叫啥',
//            'time_stamp' => strval(time()),
//            'nonce_str'  => strval(rand()),
//            'sign'       => '',
//            );
//    $params['sign'] = getReqSign($params, $appkey);
//
//// 执行API调用
//    $url = 'https://api.ai.qq.com/fcgi-bin/nlp/nlp_textchat';
//    $response = doHttpPost($url, $params);
//    echo $response;
//    上述echo $response的输出结果即API的响应结果（注意使用UTF-8编码）：
//
//    {
//        "ret": 0,
//            "msg": "ok",
//            "data": {
//        "session": "10000",
//                "answer": "我叫小豪豪~"
//    }
//    }

    public interface Echo{
        void echo(String str);
    }

    static String[] echos = {
            "吃屎吧你",
            "我草~",
            "坑爹啊",
            "干春啊?",
            "你奶奶的熊",
            "我diao你啊",
            "长的很科幻,长的很抽象！ ",
            "大哥,把你脸上的分辨率调低点好吗？ ",
            "你的长相突破了人类的想象… ",
            "别跟我说话,我有洁癖。 ",
            "有病你治病,你别找我啊,我又不是兽医。 ",
            "你内张脸长地比盆骨都标志。 ",
            "一脸兴冲冲的,跟喝了尿糖似的。 ",
            "当初你傲成那个样子,现在又是玩哪出呢。 ",
            "我给狗扔块骨头它都知道冲我摇摇尾巴,你算什么啊。 ",
            "把你的眼屎擦掉了看清楚是谁说话再行不。 ",
            "麻烦你看清楚什么叫货在说话好吗。 ",
            "我怎么敢碰你呢,我怕我买洗手液买穷自己。 ",
            "缺狗粮才知道来找我？ ",
            "别人若是骂你,看p啊or看毛啊,你可以回,看你呢啊。 ",
            "你的长相和智商都挺哈韩的。 ",
            "别以为你长的稀有样我们就应该物以稀为贵。 ",
            "你是不是觉得你胸小还替国家省布料了还挺骄傲啊。 ",
            "你说你装的个名媛淑女的样er,对了,你爹是天蓬啊。 ",
            "有种人,必须要人指着他鼻子骂,他才知道骂的是他。 ",
            "遇见你之前我还不以貌取人呢。 ",
            "你说你能干点啥,去打仗的话子弹飞弹会忍不住向你飞。 ",
            "祝你女朋友永远都是充气的。 ",
            "祝你男朋友永远都是电动的。 ",   
            "要不是那晚,我忘了买套套,你早就被冲进下水道。 ",
            "对方骂你：（各种骂人的脏话）,你回,你在做自我介绍吗。 ",
            "听你说话,一种智商上的优越感油然而生。 ",
            "好久没有听到有人能把牛吹得这么清新脱俗了。 ",
            "如果对方要是骂你,你可以回,请别跟我说话吐口水,我没拿钱,买不起湿巾。 ",
            "其实你算什么呢,不过是一条乱吠的狗罢了。 ",
            "老娘自然卷怎样,比起你的锅盖是不是好太多了呢, ",
            "跟马桶盖似的你不怕着苍蝇吗,你个西瓜太郎快滚回你的日本去,别显眼得瑟了。 ",
            "听说你傍大款了，认二郎神当主人了。    ",
            "说我嫉妒你，矮油，擦干你的眼屎看看。     ",
            "天天拿瓶自己家自来水灌的农夫山泉还觉得自己过的挺小资是吗。    ",
            "你说不要等你翻了身，可咸鱼翻身，还是咸鱼。    ",
            "一群SB往南飞，一会排成S,一会排成B、    ",
            "你在做自我介绍吗。    ",
            "你活着浪费空气，死了浪费土地，半死不活浪费RMB。    ",
            "唾沫是用来数钞票的，而不是用来讲道理的。     ",
            "我们要向前看，不错过些歪瓜劣枣怎么知道什么是好的。     ",
            "别做点错事就把什么脏水都往自己身上泼，姐还要留着冲厕所呢。     ",
            "有头在空中飞得口吐白沫的牛，要我帮它下来，唯一的方法是让你闭上嘴。    ",
            "巴黎圣母院少个敲钟的，就你了。     ",
            "等我有钱了，咱买棒棒糖，买二根。一根你看着我吃，另一根我吃给你看。    ",
            "我忍屎忍尿也忍不下你呀。     ",
            "遛累了，他坐这，狗坐这，一边高，谁过来都纳闷：这是谁家双胞胎啊？    ",
            "你还追个时髦剪个中分，麻烦你瞅瞅你那是三八分好吗。     ",
            "你是说你老母小三如此多娇吗，非要你老母损你你才爽吗，真是骚13数不胜数啊。    ",
            "敢惹我，把你名字电话发到猫扑大杂烩上让MOPPER喷死你。    ",
            "jian人永远都是jian人，就算经济危机了，你也贵不了。    ",
            "你脑子进水了吧，还是100°的那种沸水。     ",
            "看你一天天在男人身边晃悠，谁爱瞅你给你扔根骨头了？    ",
            "你一出门，千山鸟飞绝，万径人踪灭。     ",
            "回去洗洗脸，姐送你10斤香水，盖盖你身上的大渣子味。    ",
            "你以为你说你是处女我就感觉不到你是被处理过的女人吗。    ",
            "贱人永远都是贱人，就算经济危机了，你也贵不了！    ",
            "我是多想跟你们将素质，可我忍屎忍尿也忍不了你们啊，     ",
            "哪天遇上个满不吝的，给你一板儿砖，你就知道什么是肝儿颤了。    ",
            "人类朝着~傻~逼~的方向一路狂奔！     ",
            "你甘心给别人当厕纸，人家还嫌你纸软弄脏了手指，纸硬，擦伤了屁股。    ",
            "别人骂你吵，你回说，我炒shi给你吃啊。    ",
            "一巴掌把你打到墙上扣都扣不下来。     ",
            "跟马桶盖似的你不怕着苍蝇吗，你个西瓜太郎快滚回你的日本去，别显眼得瑟了。    ",
            "如果不是遇见你，我一辈子都不会理解装13的真正含义。    ",
            "如果你看到面前的阴影，别怕，()那是因为你的背后有阳光。 ",
            "你是我见过的容量最大的铅笔盒了，装那么多笔你不累吗。     ",
            "如果对方要是骂你，你可以回，请别跟我说话吐口水，我没拿钱，买不起湿巾。    ",
            "说老娘男人是两条腿的男人，矮油，看来你男人是三条腿的蛤蟆啊。    ",
            "祝你男朋友永远都是电动的。     ",
            "小子，今儿是怎么了？出门儿吃错药了？还是忘吃药了？     ",
            "就你那眼缝，跟ATM银行自动取款机插卡口差不多宽的能见度。    ",
            "祝你女朋友永远都是充气的。     ",
            "其实你算什么呢，不过是一条乱吠的狗罢了。    ",
            "当初惊艳，完完全全，只为世面见得少。     ",
            "你可以说，那么爱占便宜，假如拿人家的真手短的话，你早就高位截瘫了。    ",
            "你以为自己是哈雷彗星，全地球60亿人都要瞻仰啊。    ",
            "我没认识你之前，我真没发现原来我有以貌取人这毛病。     ",
            "你居然好意思把自己当人类，你也不用你那个为负数的智商想一下你配当人类吗？    ",
            "看你一天天也穿的人模狗样的怎么就不干点人事呢。    ",
            "31把XX的照片贴墙上白天避邪晚上避孕。    ",
            "不该看的不看，不该说的不说，不该听的不听，不该想的不想，该干什么干什么去。    ",
            "老娘除了没你那么不要脸还有什么，你还有什么可得瑟的。    ",
            "老娘自然卷怎样，比起你的锅盖是不是好太多了呢。     ",
            "无论你再怎么骂我，我都不会生气，人干嘛要和一个连狗都不如的东西生气呢。    ",
            "你的牙如同天上的繁星，色泽鲜艳，相距甚远。     ",
            "和人接触的时间越长，我就越喜欢狗，狗永远是狗，人有时候不是人！     ",
            "吹NB能带动经济建设吗？吹NB能促进事业发展吗？吹NB能引领共奔小康吗？    ",
            "你装什么黑社会，哦哦原来你就是非洲黑人难民社区居委会。     ",
            "知道自己是飞机场走路就隐蔽点，别昂首挺胸生怕别人不知道似的。    ",
            "如果多吃鱼可以补脑让人变聪明的话，那么你至少得吃一对儿鲸鱼。    ",
            "你以为你装可爱就回归童年了？     ",
            "你哪家学校毕业的啊？你讨人厌的学位都修到博士后了！    ",
            "你说你能干点啥，去打仗的话子弹飞弹会忍不住向你飞。    ",
            "我在你觉得你混的很牛13，别忘了当初你是什么狗样。     ",
            "这帅哥心思细腻又有手段，风流俊俏身材又火，真是要才有才要貌有貌要胸有胸！    ",
            "瞅你长得姥姥不疼，舅舅不爱的。     ",
            "别老问别人为什么不愿意理你，不愿意跟你说话，因为太稀罕你而不愿意搭理你现实吗？你信吗？     ",
            "其实我有多爱瞅你你知道吗，一天看不见你风骚的母狗样姐就蛋疼。    ",
            "牛粪终归是牛粪，上锅蒸了也不会变成香饽饽。    ",
            "好久没有听到有人能把牛吹得这么清新脱俗了。    ",
            "请你卷成一团圆润的离开。    ",
            "别在分手的时候和我说：“其实你很好”那你还甩我？     ",
            "遇事要先从自己身上找原因，别一拉不出屎就怪地球没有吸引力？你喷粪之前先想想你自己都干过什么，有没有资格说别人！我是不够完美，但是我坦白自然，你呢？    ",
            "你玩劈腿，劈那么开，不怕蛋蛋受凉啊。    ",
            "听你说话，一种智商上的优越感油然而生。 ",
            "上帝造就你是他的创意，你能活在这世上是你的勇气。     ",
            "虽然你身上喷了古龙水，但我还是能隐约闻到一股人渣味儿。    ",
            "人人都说我丑，其实我只是美得不明显。     ",
            "你才买中石油了你们全家都买中石油了还买中石化了。     ",
            "看你一天天的装柔弱，看见你老娘立马就懂了什么叫小姐的身子丫鬟的命。    ",
            "这两片嘴唇，切切倒有一大碟子。     ",
            "别和我装你活的精彩过的幸福，也别祝我幸福，你有那资格吗？     ",
            "煮饭时，一只螃蟹顶出锅盖，对你说：我热！，答曰：想红就忍着。     ",
            "讲素质你们配吗，嚼舌根不怕嘴巴烂掉吗，狗乱叫算什么本事，真咬到我才算你们厉害。     ",
            "你跟谁俩整那表情呢，我欠你贷款要到期了还是怎么的。     ",
            "你是脑壳里面全是粑粑，所以想的事情都和苍蝇一样没有方向。    ",
            "看见你就好像看见市场上快下市的小青菜，5毛钱一大把。    ",
            "思想有多远，你就滚多远；光速有多快，你就滚多快。    ",
            "你很了不起么。不就是有动物保护协会撑？    ",


};
    static Random r = new Random(System.currentTimeMillis());
    public static void selfEcho(Context context, String text, Echo echo){
        InterfaceBaidu.doTextScan(context, text, new InterfaceBaidu.OnRes() {
            @Override
            public void onException(Exception e) {
                echo.echo(e.toString());
            }

            @Override
            public void onRes(int lable, String info, List<String> list) {
                if(lable == 5){
                    echo.echo(echos[(int) (r.nextDouble() * echos.length)]);
                }else{
                    echo.echo(info + " " + Arrays.toString(list.toArray()));
                }
            }
        });

    }



}
