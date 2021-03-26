package com.miguan.ballvideo.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.constants.VideoContant;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.common.util.adv.AdvUtils;
import com.miguan.ballvideo.common.util.video.VideoUtils;
import com.miguan.ballvideo.mapper.SmallVideosMapper;
import com.miguan.ballvideo.service.AdvertOldService;
import com.miguan.ballvideo.service.AdvertService;
import com.miguan.ballvideo.service.SmallVideosService;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.miguan.ballvideo.vo.AdvertVo;
import com.miguan.ballvideo.vo.SmallVideosVo;
import com.miguan.ballvideo.vo.video.SmallVideos16Vo;
import com.miguan.ballvideo.vo.video.SmallVideosNewVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 小视频列表ServiceImpl
 *
 * @author xy.chen
 * @date 2019-08-09
 **/

@Service("smallVideosService")
public class SmallVideosServiceImpl implements SmallVideosService {

    @Resource
    private SmallVideosMapper smallVideosMapper;

    @Resource
    private AdvertOldService advertOldService;

    @Resource
    private AdvertService advertService;

    @Override
    public Page<SmallVideosVo> findSmallVideosList(Map<String, Object> params, int currentPage, int pageSize) {
        List<SmallVideosVo> smallVideosList;
        if (MapUtils.getString(params, "userId") == null) {
            PageHelper.startPage(currentPage, pageSize);
            smallVideosList = smallVideosMapper.findSmallVideosList(params);
        } else {
            PageHelper.startPage(currentPage, pageSize);
            smallVideosList = smallVideosMapper.findSmallVideosListByUserId(params);
        }
        if (CollectionUtils.isNotEmpty(smallVideosList)) {
            VideoUtils.cntSmallVideoLoveAndWatchNum(smallVideosList);
            //广告信息
            List<AdvertVo> advertVoList = advertOldService.queryAdertList(params);
            for (SmallVideosVo smallVideosVo : smallVideosList) {
                smallVideosVo.setAdvertVoList(advertVoList);
            }
            return (Page<SmallVideosVo>) smallVideosList;
        }
        return null;
    }

    @Override
    public List<SmallVideosVo> findSmallVideosList(Map<String, Object> params) {
        List<SmallVideosVo> smallVideosList;
        if (MapUtils.getString(params, "userId") == null) {
            smallVideosList = smallVideosMapper.findSmallVideosList(params);
        } else {
            smallVideosList = smallVideosMapper.findSmallVideosListByUserId(params);
        }
        if (CollectionUtils.isNotEmpty(smallVideosList)) {
            VideoUtils.cntSmallVideoLoveAndWatchNum(smallVideosList);
            //广告信息
            params.remove("state");
            List<AdvertVo> advertVoList = advertOldService.queryAdertList(params);
            for (SmallVideosVo smallVideosVo : smallVideosList) {
                smallVideosVo.setAdvertVoList(advertVoList);
            }
        }
        return smallVideosList;
    }

    @Override
    public SmallVideosNewVo findSmallVideosList13(Map<String, Object> params, int currentPage, int flag) {
        //广告信息
        final SmallVideosNewVo smallVideosNewVo = new SmallVideosNewVo();
        final AdvertVo advertVo = advertOldService.queryOneByRandom(params);
        final int pageSize = VideoUtils.getPageSize(advertVo, currentPage, VideoContant.smallVideo_default_pageSize, flag);
        List<SmallVideosVo> smallVideosList = null;
        if (MapUtils.getString(params, "userId") == null) {
            PageHelper.startPage(currentPage, pageSize);
            smallVideosList = smallVideosMapper.findSmallVideosList(params);
        } else {
            PageHelper.startPage(currentPage, pageSize);
            smallVideosList = smallVideosMapper.findSmallVideosListByUserId(params);
        }
        if (CollectionUtils.isNotEmpty(smallVideosList)) {
            VideoUtils.cntSmallVideoLoveAndWatchNum(smallVideosList);
        }
        smallVideosNewVo.setAdvertVo(advertVo);
        smallVideosNewVo.setPage((Page<SmallVideosVo>) smallVideosList);
        return smallVideosNewVo;
    }

    @Override
    public SmallVideos16Vo findSmallVideosList16(Map<String, Object> params, int currentPage, int flag) {
        final SmallVideos16Vo smallVideosNewVo = new SmallVideos16Vo();
        //随机5广告
        final List<AdvertVo> advertVos = advertOldService.queryVideoByRandom(params, 5);
        if (flag == 1) currentPage = 1;//安卓不能判断是否是首次，多传一个flag判断；
        final int pageSize = Global.getInt("page_number");
        List<SmallVideosVo> smallVideosList = null;
        if (MapUtils.getString(params, "userId") == null) {
            PageHelper.startPage(currentPage, pageSize);
            smallVideosList = smallVideosMapper.findSmallVideosList(params);
        } else {
            PageHelper.startPage(currentPage, pageSize);
            smallVideosList = smallVideosMapper.findSmallVideosListByUserId(params);
        }
        if (CollectionUtils.isNotEmpty(smallVideosList)) {
            VideoUtils.cntSmallVideoLoveAndWatchNum(smallVideosList);
        }
        smallVideosNewVo.setAdvertVos(advertVos);
        smallVideosNewVo.setPage((Page<SmallVideosVo>) smallVideosList);
        return smallVideosNewVo;
    }

    @Override
    public SmallVideos16Vo findSmallVideosList17(AbTestAdvParamsVo queueVo, Map<String, Object> params) {
        final SmallVideos16Vo smallVideosNewVo = new SmallVideos16Vo();
        final Page<SmallVideosVo> page = new Page<>();
        final List<SmallVideosVo> smallVideosList = page.getResult();

        //根据推荐池规则获取随机视频
        getRecommendSmallVideosList(params, smallVideosList);

        String appVersion = MapUtils.getString(params, "appVersion");
        boolean newFlag = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_249);
        if (newFlag){
            //V2.5.0走新广告逻辑
            List<AdvertCodeVo> advertCodeVos = advertService.commonSearch(queueVo, params);
            smallVideosNewVo.setAdvertCodeVos(advertCodeVos);
        }else {
            boolean flag = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_220);
            //随机5广告
            params.remove("state");
            List<AdvertVo> advertlist = advertOldService.getAdvertsBySection(params);
            if (flag) {
                if (CollectionUtils.isNotEmpty(advertlist)) {
                    int type = advertlist.get(0).getType();
                    if (type == 0) {
                        advertlist = AdvUtils.computer(advertlist, advertlist.size());
                    }
                }
                smallVideosNewVo.setAdvertVos(advertlist);
            }else {
                List<AdvertVo> advertList = AdvUtils.computer(advertlist, 5);
                smallVideosNewVo.setAdvertVos(advertList);
            }
        }
        //组装点赞和观看数
        if (CollectionUtils.isNotEmpty(smallVideosList)) {
            VideoUtils.cntSmallVideoLoveAndWatchNum(smallVideosList);
        }
        smallVideosNewVo.setPage(page);
        return smallVideosNewVo;
    }

    /**
     * 根据推荐池规则获取随机视频
     */
    private void getRecommendSmallVideosList(Map<String, Object> params, List<SmallVideosVo> smallVideosList) {
        final int weekNewValue = Global.getInt("week_new_value");//一周内最新视频条数（小视频）
        final int monthRecommendValue = Global.getInt("month_recommend_value");//一月内推荐视频条数（小视频）
        final int pageNumber = weekNewValue + monthRecommendValue;
        params.put("queryNumber", pageNumber);//每页加载条数（小视频）
        final String excludeIds = MapUtils.getString(params, "excludeIds");
        List<String> list = new ArrayList();
        if (!StringUtils.isEmpty(excludeIds)) {
            String[] split = excludeIds.split(",");
            list.addAll(Arrays.asList(split));
            if(list.size()>100){
                int size = list.size();
                list = list.subList(size-100,size);
            }
            params.put("excludeIds", list);//排除已浏览视频
        } else {
            params.remove("excludeIds");
        }

        //查询近一个月后台推荐的视频
        List<SmallVideosVo> smallVideosList1;
        params.put("monthRecommendPool", "1");
        final String userId = MapUtils.getString(params, "userId");
        if (userId == null || "0".equals(userId)) {
            smallVideosList1 = smallVideosMapper.findSmallVideosList(params);
        } else {
            smallVideosList1 = smallVideosMapper.findSmallVideosListByUserId(params);
        }
        final int size1 = smallVideosList1.size();

        //查询一周内上传的新视频
        List<SmallVideosVo> smallVideosList2;
        params.remove("monthRecommendPool");
        params.put("weekRecommendPool", "1");
        if (userId == null || "0".equals(userId)) {
            smallVideosList2 = smallVideosMapper.findSmallVideosList(params);
        } else {
            smallVideosList2 = smallVideosMapper.findSmallVideosListByUserId(params);
        }
        final int size2 = smallVideosList2.size();

        //第一种情况：一周内上传的新视频和近一个月后台推荐的视频都满足3条以上
        if (size1 >= monthRecommendValue && size2 >= weekNewValue) {
            for (int i = 0; i < monthRecommendValue; i++) {
                SmallVideosVo smallVideosVo = smallVideosList1.get(i);
                smallVideosList.add(smallVideosVo);
            }
            for (int i = 0; i < weekNewValue; i++) {
                SmallVideosVo smallVideosVo = smallVideosList2.get(i);
                smallVideosList.add(smallVideosVo);
            }
        }

        //第二种情况：一周内上传的新视频和近一个月后台推荐的视频累计满足6条以上，但其中一个不满足3条
        if ((size1 + size2) >= pageNumber && (size1 < monthRecommendValue || size2 < weekNewValue)) {
            if (size1 < monthRecommendValue) {
                for (int i = 0; i < size1; i++) {
                    SmallVideosVo smallVideosVo = smallVideosList1.get(i);
                    smallVideosList.add(smallVideosVo);
                }
                for (int i = 0; i < (pageNumber - size1); i++) {
                    SmallVideosVo smallVideosVo = smallVideosList2.get(i);
                    smallVideosList.add(smallVideosVo);
                }
            }
            if (size2 < weekNewValue) {
                for (int i = 0; i < (pageNumber - size2); i++) {
                    SmallVideosVo smallVideosVo = smallVideosList1.get(i);
                    smallVideosList.add(smallVideosVo);
                }
                for (int i = 0; i < size2; i++) {
                    SmallVideosVo smallVideosVo = smallVideosList2.get(i);
                    smallVideosList.add(smallVideosVo);
                }
            }
        }

        //第三种情况：推荐池内累计条数不够，池外数据补齐
        if ((size1 + size2) < pageNumber) {
            for (int i = 0; i < size1; i++) {
                SmallVideosVo smallVideosVo = smallVideosList1.get(i);
                smallVideosList.add(smallVideosVo);
            }
            for (int i = 0; i < size2; i++) {
                SmallVideosVo smallVideosVo = smallVideosList2.get(i);
                smallVideosList.add(smallVideosVo);
            }
            //查询推荐池外信息
            List<SmallVideosVo> smallVideosList3;
            params.remove("weekRecommendPool");
            params.put("recommendPool", "0");
            params.put("queryNumber", pageNumber - (size1 + size2));
            if (userId == null || "0".equals(userId)) {
                smallVideosList3 = smallVideosMapper.findSmallVideosList(params);
            } else {
                smallVideosList3 = smallVideosMapper.findSmallVideosListByUserId(params);
            }
            smallVideosList.addAll(smallVideosList3);


            //当池外数据不够时，查询所有视频（不用ID数据过滤）
            List<SmallVideosVo> smallVideosList4;
            final int currentSize = smallVideosList.size();
            if (currentSize < pageNumber) {
                if (currentSize == 0) {
                    //当时所有视频都浏览过时，去掉池查询和ID排除条件随机取视频
                    params.remove("recommendPool");
                    params.remove("excludeIds");
                } else {
                    //未浏览数据不够6条时，随机取浏览过的数据不齐
                    String id;
                    for (int i = 0; i < currentSize; i++) {
                        id = String.valueOf(smallVideosList.get(i).getId());
                        list.add(id);
                    }
                    params.put("excludeIds", list);
                }
                params.put("queryNumber", pageNumber - currentSize);
                if (userId == null || "0".equals(userId)) {
                    smallVideosList4 = smallVideosMapper.findSmallVideosList(params);
                } else {
                    smallVideosList4 = smallVideosMapper.findSmallVideosListByUserId(params);
                }
                smallVideosList.addAll(smallVideosList4);
            }

        }
    }
}