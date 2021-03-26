package com.miguan.laidian.common.util.extInt;

import com.alibaba.fastjson.JSONObject;
import com.cgcg.context.util.UUIDUtils;
import com.miguan.laidian.common.util.HttpClientUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 与讯飞接口对接方法
 * Created by suhj on 2020/8/13.
 */
public class IFlyteIntfCall {

    //https://api.kuyinyun.com/p/search?a=d5be54d3df6bddba&w=%E5%B0%91%E5%B9%B4&px=0&ps=15&ct=1&tc=testTc&uid=testUid

    private final static String ENCODE = "UTF-8";
    //对接讯飞接口地址
    public static String INT_URL =  "https://api.kuyinyun.com/p/";

    //调用5.1 q_cols接口，获取根栏目下所有分类
    public static String Q_COlS = "q_cols";

    //调用5.2 q_colres接口，根据栏目ID（使用id）获取对应XX的铃音资源列表
    public static String Q_COlRES = "q_colres";

    //调用5.3 q_skw接口，获取搜索热词
    public static String Q_SKW = "q_skw";

    //调用5.4 search接口，搜索铃音
    public static String SEARCH = "search";

    //调用5.11 q_colres_vr接口，根据栏目ID（使用id）查询对应XX的视频列表
    public static String Q_COlRES_VR = "q_colres_vr";

    //调用5.12 q_skw_vr接口，获取视频搜索热词
    public static String Q_SKW_VR = "q_skw_vr";

    //调用5.13 search_vr接口，搜索视频
    public static String SEARCH_VR = "search_vr";

    //铃声渠道号
    public static String COlRES_CHANNEL_ID = "d5be54d3df6bddba";

    //设彩铃URL
    public static String COlRES_RING_URL =  "https://iring.diyring.cc/friend/";

    //彩铃根栏目ID
    public static String COlRES_COL_ID = "318641";

    //铃声-轮播图 ID
    public static String COlRES_POLL_ID = "318553";

    //铃声-分类 ID
    public static String COlRES_SORT_ID = "318637";

    //铃声-最新 ID
    public static String COlRES_NEW_ID = "310301";

    //铃声-最热 ID
    public static String COlRES_HOT_ID = "315497";


    //视频渠道号
    public static String COlRES_VR_CHANNEL_ID = "d0b23f78c27e9078";

    //设视频彩铃URL
    public static String COlRES_VR_RING_URL =  "https://vring.kuyin123.com/friend/";

    //视频根栏目ID
    public static String COlRES_VR_COL_ID = "318633";

    //视频-分类 ID
    public static String COlRES_VR_SORT_ID = "318629";

    //视频-最新 ID
    public static String COlRES_VR_NEW_ID = "315897";

    //视频-最热 ID
    public static String COlRES_VR_HOT_ID = "315893";

    //默认图片链接
    static {


    }

    public static final Map<Integer, String> PICTURE = new HashMap<>();

    static {
        PICTURE.put(1, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824180509964.jpg");
        PICTURE.put(2, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181349178.jpg");
        PICTURE.put(3, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181410033.jpg");
        PICTURE.put(4, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181435729.jpg");
        PICTURE.put(5, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181455569.jpg");
        PICTURE.put(6, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181509331.jpg");
        PICTURE.put(7, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181539059.jpg");
        PICTURE.put(8, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181553804.jpg");
        PICTURE.put(9, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181633642.jpg");
        PICTURE.put(10, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181651020.jpg");
        PICTURE.put(11, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181703309.jpg");
        PICTURE.put(12, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181720638.jpg");
        PICTURE.put(13, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181734415.jpg");
        PICTURE.put(14, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181857592.jpg");
        PICTURE.put(15, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181748980.jpg");
        PICTURE.put(16, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181918857.jpg");
        PICTURE.put(17, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181934416.jpg");
        PICTURE.put(18, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824181946376.jpg");
        PICTURE.put(19, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824182001246.jpg");
        PICTURE.put(20, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824182013317.jpg");
        PICTURE.put(21, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824182025383.jpg");
        PICTURE.put(22, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824182043005.jpg");
        PICTURE.put(23, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824182058927.jpg");
        PICTURE.put(24, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824182113968.jpg");
        PICTURE.put(25, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824182128538.jpg");
        PICTURE.put(26, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824182145632.jpg");
        PICTURE.put(27, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824182248499.jpg");
        PICTURE.put(28, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824182312138.jpg");
        PICTURE.put(29, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824182327417.jpg");
        PICTURE.put(30, "http://ss.bscstorage.com/oss/p/dev-opinionimg-laidian/202008/20200824_20200824182340228.jpg");
    }

    //获取请求会话ID和合作方的用户账号唯一标识
    public static String getTcAndUid(){
        String tc = UUIDUtils.getUUID();
        String uid = "testUid";
        return "tc=" + tc + "&uid=" + uid;
    }

    public static JSONObject getJsonObject(String url){
        JSONObject jsonObject = new JSONObject();
        try {
            String jsonStr = HttpClientUtil.get(url);

            jsonObject = JSONObject.parseObject(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            // parseJSON2Map(jsonObject);
            return jsonObject;
        }
    }


    /**
     * URL 解码
     *
     * @return String
     * @author suhj
     */
    public static String getURLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * URL 转码
     *
     * @return String
     * @author suhj
     */
    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
/*        JSONObject jsonObject1 = getQCols("318641","0","20","1");
        JSONObject jsonObject2 = getQColRes("317133","0","20","1");
        JSONObject jsonObject3 = getQColResVr("315893","0","20","1");
        JSONObject jsonObject4 = getQSkw("6");
        Map<String, Object> jsonObject5 = getQSkwVr("");
        Map<String, Object> jsonObject6 = getSearch("%E5%B0%91%E5%B9%B4","","","0","10","1");
        Map<String, Object> jsonObject7 = getSearchVr("%E5%B0%91%E5%B9%B4","0","20","1");*/
    }

}
