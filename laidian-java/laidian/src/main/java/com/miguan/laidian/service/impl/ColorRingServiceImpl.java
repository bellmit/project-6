package com.miguan.laidian.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cgcg.context.util.StringUtils;
import com.google.common.collect.Lists;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.common.util.extInt.IFlyteIntfCall;
import com.miguan.laidian.redis.service.RedisService;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.service.ColorRingService;
import com.miguan.laidian.vo.iFlytek.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

/**
 * 广告ServiceImpl
 * @author suhj
 * @date 2020-08-14
 **/

@Service("ColorRingServic")
public class ColorRingServiceImpl implements ColorRingService {

    @Resource
    private RedisService redisService;

    @Override
    public ResultMap getQCols(String colId, String operator) {
        IFlytekResultMap iFlytekResultMap = null;
        String channelId = IFlyteIntfCall.COlRES_CHANNEL_ID;
        //String ringUrl = IFlyteIntfCall.COlRES_RING_URL + IFlyteIntfCall.COlRES_CHANNEL_ID + "?wno=";
        if(IFlyteIntfCall.COlRES_VR_COL_ID.equals(colId)){
            channelId = IFlyteIntfCall.COlRES_VR_CHANNEL_ID;
            //ringUrl = IFlyteIntfCall.COlRES_VR_RING_URL + IFlyteIntfCall.COlRES_VR_CHANNEL_ID + "?videoId=";
        }
        try {
            String url = IFlyteIntfCall.INT_URL + IFlyteIntfCall.Q_COlS + "?a="+ channelId + "&" + IFlyteIntfCall.getTcAndUid();
            if(StringUtils.isNotEmpty(colId)){
                url += "&id=" + colId;
            }
            if(StringUtils.isNotEmpty(operator)){
                url += "&ct=" + operator;
            }
            JSONObject jsonObject = IFlyteIntfCall.getJsonObject(url);
            //获取跟目录
            iFlytekResultMap = JSONObject.parseObject( JSONObject.toJSONString(jsonObject) , IFlytekResultMap.class);
            //获取数据
            ColRes colRes = JSONObject.parseObject( JSONObject.toJSONString(jsonObject.get("data")) , ColRes.class);
            //colRes.setRingurl(ringUrl);
            List<ColRes> colResList = colRes.getCols();
            if(CollectionUtils.isNotEmpty(colResList)){
                colResList.stream().forEach(res ->{
                    //彩铃
                    if(IFlyteIntfCall.COlRES_COL_ID.equals(colId)){
                        if(CollectionUtils.isNotEmpty(res.getWks())){
                            //取前5的数据
                            res.setWks(res.getWks().subList(0,
                                    res.getWks().size() > 5 ? 5 : res.getWks().size()));

                            //设置彩铃详情信息
                            handlerRingDetInfo(res.getWks());

                        }
                        if(IFlyteIntfCall.COlRES_POLL_ID.equals(res.getId()))
                            colRes.setLunbo(res);
                        if(IFlyteIntfCall.COlRES_SORT_ID.equals(res.getId()))
                            colRes.setFenlei(res);
                        if(IFlyteIntfCall.COlRES_HOT_ID.equals(res.getId()))
                            colRes.setZuire(res);
                        if(IFlyteIntfCall.COlRES_NEW_ID.equals(res.getId()))
                            colRes.setZuixin(res);
                    }
                    //视频
                    if(IFlyteIntfCall.COlRES_VR_COL_ID.equals(colId)){
                        if(IFlyteIntfCall.COlRES_VR_SORT_ID.equals(res.getId())){
/*                            res.getCols().stream().forEach(childCol ->{
                                ResultMap resultMap = getQColResVr(childCol.getId(),"0","5",operator);
                                childCol.setWksVr((List<VideoRing>)resultMap.getData());
                            });*/
                            colRes.setFenlei(res);
                        }
                        if(IFlyteIntfCall.COlRES_VR_HOT_ID.equals(res.getId())){
                            ResultMap resultMap = getQColResVr(res.getId(),"0","4",operator);
                            res.setWksVr((List<VideoRing>)resultMap.getData());
                            colRes.setZuire(res);
                        }
                        if(IFlyteIntfCall.COlRES_VR_NEW_ID.equals(res.getId())){
                            ResultMap resultMap = getQColResVr(res.getId(),"0","4",operator);
                            res.setWksVr((List<VideoRing>)resultMap.getData());
                            colRes.setZuixin(res);
                        }
                    }
                });
                colRes.getCols().clear();
            }
            iFlytekResultMap.setData(colRes);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return returnResultMap(iFlytekResultMap);
        }
    }

    @Override
    public ResultMap getQColRes(String colId, String currentPage, String pageSize, String operator) {
        IFlytekResultMap iFlytekResultMap = null;
        try {
            //  JSONObject jsonObject = IFlyteIntfCall.getQColRes(colId,currentPage,pageSize,operator);
            String url = IFlyteIntfCall.INT_URL + IFlyteIntfCall.Q_COlRES + "?a="+ IFlyteIntfCall.COlRES_CHANNEL_ID + "&" + IFlyteIntfCall.getTcAndUid();
            if(StringUtils.isNotEmpty(colId)){
                url += "&id=" + colId;
            }
            if(StringUtils.isNotEmpty(currentPage)){
                url += "&px=" + currentPage;
            }
            if(StringUtils.isNotEmpty(pageSize)){
                url += "&ps=" + pageSize;
            }
            if(StringUtils.isNotEmpty(operator)){
                url += "&ct=" + operator;
            }
            JSONObject jsonObject = IFlyteIntfCall.getJsonObject(url);
            //获取跟目录
            iFlytekResultMap = JSONObject.parseObject( JSONObject.toJSONString(jsonObject) , IFlytekResultMap.class);
            //获取数据
            List<ResItemSimple> resItemSimpleList = JSONObject.parseObject( JSONObject.toJSONString(jsonObject.get("data")) , new TypeReference<List<ResItemSimple>>(){});
            //设置彩铃详情信息
            handlerRingDetInfo(resItemSimpleList);

            iFlytekResultMap.setData(resItemSimpleList);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return returnResultMap(iFlytekResultMap);
        }
    }

    @Override
    public ResultMap getQColResVr(String colId, String currentPage, String pageSize, String operator) {
        IFlytekResultMap iFlytekResultMap = null;
        try {
            //  JSONObject jsonObject = IFlyteIntfCall.getQColRes(colId,currentPage,pageSize,operator);
            String url = IFlyteIntfCall.INT_URL + IFlyteIntfCall.Q_COlRES_VR + "?a="+ IFlyteIntfCall.COlRES_VR_CHANNEL_ID + "&" + IFlyteIntfCall.getTcAndUid();
            if(StringUtils.isNotEmpty(colId)){
                url += "&id=" + colId;
            }
            if(StringUtils.isNotEmpty(currentPage)){
                url += "&px=" + currentPage;
            }
            if(StringUtils.isNotEmpty(pageSize)){
                url += "&ps=" + pageSize;
            }
            if(StringUtils.isNotEmpty(operator)){
                url += "&ct=" + operator;
            }
            JSONObject jsonObject = IFlyteIntfCall.getJsonObject(url);
            //获取跟目录
            iFlytekResultMap = JSONObject.parseObject( JSONObject.toJSONString(jsonObject) , IFlytekResultMap.class);
            //获取数据
            List<VideoRing> retList = JSONObject.parseObject( JSONObject.toJSONString(jsonObject.get("data")) , new TypeReference<List<VideoRing>>(){});
            //设置彩铃H5链接
            if(CollectionUtils.isNotEmpty(retList)){
                retList.stream().forEach(videoRing -> videoRing.setRingurl(IFlyteIntfCall.COlRES_VR_RING_URL + IFlyteIntfCall.COlRES_VR_CHANNEL_ID + "?videoId=" + videoRing.getId()));
            }

            iFlytekResultMap.setData(retList);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return returnResultMap(iFlytekResultMap);
        }
    }

    @Override
    public ResultMap getSearch(String keyword, String singer, String song, String currentPage, String pageSize, String operator) {
        //  JSONObject jsonObject = IFlyteIntfCall.getSearch(keyword,singer,song,currentPage,pageSize,operator);
        IFlytekResultMap iFlytekResultMap = null;
        try {
            String url = IFlyteIntfCall.INT_URL + IFlyteIntfCall.SEARCH + "?a="+ IFlyteIntfCall.COlRES_CHANNEL_ID + "&" + IFlyteIntfCall.getTcAndUid();
            if(StringUtils.isNotEmpty(keyword)){
                url += "&w=" + IFlyteIntfCall.getURLEncoderString(keyword.trim());
            }
            if(StringUtils.isNotEmpty(singer)){
                url += "&sg=" + IFlyteIntfCall.getURLEncoderString(singer.trim());
            }
            if(StringUtils.isNotEmpty(song)){
                url += "&song=" + IFlyteIntfCall.getURLEncoderString(song.trim());
            }
            if(StringUtils.isNotEmpty(currentPage)){
                url += "&px=" + currentPage;
            }
            if(StringUtils.isNotEmpty(pageSize)){
                url += "&ps=" + pageSize;
            }
            if(StringUtils.isNotEmpty(operator)){
                url += "&ct=" + operator;
            }
            JSONObject jsonObject = IFlyteIntfCall.getJsonObject(url);
            //获取跟目录
            iFlytekResultMap = JSONObject.parseObject( JSONObject.toJSONString(jsonObject) , IFlytekResultMap.class);
            //获取数据
            List<ResItemSimple> resItemSimpleList = JSONObject.parseObject( JSONObject.toJSONString(jsonObject.get("data")) , new TypeReference<List<ResItemSimple>>(){});
            //设置彩铃详情信息
            handlerRingDetInfo(resItemSimpleList);

            iFlytekResultMap.setData(resItemSimpleList);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return returnResultMap(iFlytekResultMap);
        }
    }

    @Override
    public ResultMap getSearchVr(String keyword, String currentPage, String pageSize, String operator) {
        // JSONObject jsonObject = IFlyteIntfCall.getSearchVr(keyword,currentPage,pageSize,operator);
        IFlytekResultMap iFlytekResultMap = null;
        try {
            //  JSONObject jsonObject = IFlyteIntfCall.getQColRes(colId,currentPage,pageSize,operator);
            String url = IFlyteIntfCall.INT_URL + IFlyteIntfCall.SEARCH_VR + "?a="+ IFlyteIntfCall.COlRES_VR_CHANNEL_ID + "&" + IFlyteIntfCall.getTcAndUid();
            if(StringUtils.isNotEmpty(keyword)){
                url += "&w=" + IFlyteIntfCall.getURLEncoderString(keyword.trim());
            }
            if(StringUtils.isNotEmpty(currentPage)){
                url += "&px=" + currentPage;
            }
            if(StringUtils.isNotEmpty(pageSize)){
                url += "&ps=" + pageSize;
            }
            if(StringUtils.isNotEmpty(operator)){
                url += "&ct=" + operator;
            }
            JSONObject jsonObject = IFlyteIntfCall.getJsonObject(url);
            //获取跟目录
            iFlytekResultMap = JSONObject.parseObject( JSONObject.toJSONString(jsonObject) , IFlytekResultMap.class);
            //获取数据
            List<VideoRing> retList = JSONObject.parseObject( JSONObject.toJSONString(jsonObject.get("data")) , new TypeReference<List<VideoRing>>(){});
            //设置彩铃H5链接
            if(CollectionUtils.isNotEmpty(retList)){
                retList.stream().forEach(videoRing -> videoRing.setRingurl(IFlyteIntfCall.COlRES_VR_RING_URL + IFlyteIntfCall.COlRES_VR_CHANNEL_ID + "?videoId=" + videoRing.getId()));
            }
            iFlytekResultMap.setData(retList);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return returnResultMap(iFlytekResultMap);
        }
    }

    @Override
    public ResultMap getQSkw(String showSize) {
        //   JSONObject jsonObject = IFlyteIntfCall.getQSkw(showSize);
        IFlytekResultMap iFlytekResultMap = null;
        try {
            //  JSONObject jsonObject = IFlyteIntfCall.getQColRes(colId,currentPage,pageSize,operator);
            String url = IFlyteIntfCall.INT_URL + IFlyteIntfCall.Q_SKW + "?a=" + IFlyteIntfCall.COlRES_CHANNEL_ID;
            if(StringUtils.isNotEmpty(showSize)){
                url += "&ps=" + showSize;
            }
            JSONObject jsonObject = IFlyteIntfCall.getJsonObject(url);
            //获取跟目录
            iFlytekResultMap = JSONObject.parseObject( JSONObject.toJSONString(jsonObject) , IFlytekResultMap.class);
            //获取数据
            List<String> retList = JSONObject.parseObject( JSONObject.toJSONString(jsonObject.get("data")) , new TypeReference<List<String>>(){});
            iFlytekResultMap.setData(retList);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return returnResultMap(iFlytekResultMap);
        }
    }

    @Override
    public ResultMap getQSkwVr(String showSize) {
        //  JSONObject jsonObject = IFlyteIntfCall.getQSkwVr(showSize);
        IFlytekResultMap iFlytekResultMap = null;
        try {
            //  JSONObject jsonObject = IFlyteIntfCall.getQColRes(colId,currentPage,pageSize,operator);
            String url = IFlyteIntfCall.INT_URL + IFlyteIntfCall.Q_SKW_VR + "?a=" + IFlyteIntfCall.COlRES_VR_CHANNEL_ID;
            if(StringUtils.isNotEmpty(showSize)){
                url += "&ps=" + showSize;
            }
            JSONObject jsonObject = IFlyteIntfCall.getJsonObject(url);
            //获取跟目录
            iFlytekResultMap = JSONObject.parseObject( JSONObject.toJSONString(jsonObject) , IFlytekResultMap.class);
            //获取数据
            List<Word> retList = JSONObject.parseObject( JSONObject.toJSONString(jsonObject.get("data")) , new TypeReference<List<Word>>(){});
            iFlytekResultMap.setData(retList);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return returnResultMap(iFlytekResultMap);
        }
    }


    /**
     * 设置彩铃详情信息
     * @param wksLst
     */
    private void handlerRingDetInfo(List<ResItemSimple> wksLst){
        if(CollectionUtils.isEmpty(wksLst)){
            return;
        }
        wksLst.stream().forEach(wks -> {
            //设置彩铃H5链接
            wks.setRingurl(IFlyteIntfCall.COlRES_RING_URL + IFlyteIntfCall.COlRES_CHANNEL_ID + "?wno=" + wks.getId());
            //设置默认图片
            if(StringUtils.isEmpty(wks.getImgurl())){
                String wksVal = redisService.get(IFlyteIntfCall.Q_COlRES+wks.getId());
                if(StringUtils.isEmpty(wksVal)){
                    int max=30,min=1;
                    int rand = (int) (Math.random()*(max-min)+min);
                    wks.setImgurl(IFlyteIntfCall.PICTURE.get(rand));
                    redisService.set(IFlyteIntfCall.Q_COlRES+wks.getId(), wks.getImgurl(), RedisKeyConstant.EMPTY_VALUE_SECONDS);
                }else {
                    wks.setImgurl(wksVal);
                }

            }
        });
    }

    private ResultMap returnResultMap(IFlytekResultMap iFlytekMap){
        try {
            if(iFlytekMap == null){
                return ResultMap.error(401,"网络异常");
            }
            if("1000".equals(iFlytekMap.getRetcode())){
                return ResultMap.error(401,"参数验证失败");
            }
            if("9999".equals(iFlytekMap.getRetcode())){
                return ResultMap.error(401,"内部异常");
            }
            if("2000".equals(iFlytekMap.getRetcode())){
                return ResultMap.success(Lists.newArrayList(),"无更多内容");
            }
            if("0000".equals(iFlytekMap.getRetcode())){
                return ResultMap.success(iFlytekMap.getData());
            }
            return ResultMap.error();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }
}