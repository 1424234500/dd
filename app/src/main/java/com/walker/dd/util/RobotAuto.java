package com.walker.dd.util;

import com.walker.common.util.Bean;
import com.walker.common.util.JsonUtil;
import com.walker.common.util.MD5;
import com.walker.common.util.Tools;
import com.walker.core.exception.ErrorException;
import com.walker.socket.server_0.Msg;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;


/**
 * 聊天机器人
 * tuling
 * tencent
 *
 */

public class RobotAuto {
//    http://www.tuling123.com/openapi/api?key=bfbf6432b655493b9e861b470bca9921&info=[可爱]
    public final static String APIKEY_TULING="17022af7b2824c3f917682c184b0ece9";   //bfbf6432b655493b9e861b470bca9921
    public static String getUrlTuling(String word) {
		return "http://www.tuling123.com/openapi/api?key=" + APIKEY_TULING + "&info=" + word;
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
//	"text": "亲，已帮你找到图片",
//	"url": "http://m.image.so.com/i?q=%E5%B0%8F%E7%8B%97"
	
//302000	新闻类
//	"code": 302000,
//	"text": "亲，已帮您找到相关新闻",
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
	//"text": "亲，已帮您找到菜谱信息",
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
    public static RequestBody getTencentParamRequest(String msg, String session){
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
     * @param params 请求参数集，所有参数必须已转换为字符串类型
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
            if (code == 0) {
                Bean data = map.get("data", new Bean());
                res += data.get("answer", "没有回复?");
            } else {
                res = jsonstr;
            }
        }catch (Exception e){
            res = jsonstr;
        }
        return res;
    }

//1. 接口描述
//    基础闲聊接口提供基于文本的基础聊天能力，可以让您的应用快速拥有具备上下文语义理解的机器聊天功能。
//
//2. 请求参数
//    参数名称	是否必选	数据类型	数据约束	示例数据	描述
//    app_id	是	int	正整数	1000001	应用标识（AppId）
//    time_stamp	是	int	正整数	1493468759	请求时间戳（秒级）
//    nonce_str	是	string	非空且长度上限32字节	fa577ce340859f9fe	随机字符串
//    sign	是	string	非空且长度固定32字节		签名信息，详见接口鉴权
//    session	是	string	UTF-8编码，非空且长度上限32字节	10000	会话标识（应用内唯一）
//    question	是	string	UTF-8编码，非空且长度上限300字节	你叫啥	用户输入的聊天内容
//3. 响应参数
//    参数名称	是否必选	数据类型	描述
//    ret	是	int	返回码； 0表示成功，非0表示出错
//    msg	是	string	返回信息；ret非0时表示出错时错误原因
//    data	是	object	返回数据；ret为0时有意义
//    session	是	string	UTF-8编码，非空且长度上限32字节
//    answer	是	string	UTF-8编码，非空
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
//    下面使用PHP实现该HTTP API调用，其中getReqSign、doHttpPost可以从接口鉴权获取。
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



		            
	
}
