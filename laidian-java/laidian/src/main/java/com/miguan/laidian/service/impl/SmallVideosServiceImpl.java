package com.miguan.laidian.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.mapper.SmallVideoUserMapper;
import com.miguan.laidian.mapper.SmallVideoMapper;
import com.miguan.laidian.service.SmallVideoService;
import com.miguan.laidian.vo.SmallVideoUserVo;
import com.miguan.laidian.vo.SmallVideoVo;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * iOS视频源列表ServiceImpl
 *
 * @author xy.chen
 * @date 2019-07-08
 **/

@Service("smallVideosService")
public class SmallVideosServiceImpl implements SmallVideoService {

    @Resource
    private SmallVideoMapper smallVideosMapper;

    @Resource
    private SmallVideoUserMapper clUserVideosMapper;

    //收藏
    private static final String COLLECTION = "10";
    //取消收藏
    private static final String UNCOLLECTION = "40";
    //点赞
    private static final String LOVE = "20";
    //取消点赞
    private static final String UNLOVE = "50";
    //不感兴趣
    public static final String NO_INTEREST_CODE = "60";

    @Override
    public Page<SmallVideoVo> findIOSVideosList(Map<String, Object> params, int currentPage, int pageSize) {
        String appType = MapUtils.getString(params, "appType");
        PageHelper.startPage(currentPage, pageSize);
        List<SmallVideoVo> iOSVideosVoList = smallVideosMapper.findVideosList(params);
        int detailPageCount = Global.getInt("detail_page_count",appType);//iOS来电详情广告位次数
        int smallVideoListDeblockingCount = Global.getInt("small_video_list_deblocking",appType);//android小视频列表广告位次数
        int smallVideoDetailsDeblockingCount = Global.getInt("small_video_details_deblocking",appType);//android小视频详情广告位次数
        for (SmallVideoVo iOSVideosVo : iOSVideosVoList) {
            iOSVideosVo.setDetailPageCount(detailPageCount);
            iOSVideosVo.setSmallVideoListDeblockingCount(smallVideoListDeblockingCount);
            iOSVideosVo.setSmallVideoDetailsDeblockingCount(smallVideoDetailsDeblockingCount);
        }
        return (Page<SmallVideoVo>)iOSVideosVoList;
    }

    @Override
    public int updateReportCount(Map<String, Object> params) {
        return smallVideosMapper.updateReportCount(params);
    }

    @Override
    public Page<SmallVideoVo> findMyCollection(String userId, int currentPage, int pageSize, String appType) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("collection", 1);
        map.put("appType",appType);
        PageHelper.startPage(currentPage, pageSize);
        List<SmallVideoVo> firstVideoListByMyCollection = smallVideosMapper.findFirstVideoListByMyCollection(map);
        return (Page<SmallVideoVo>) firstVideoListByMyCollection;
    }

    /**
     * 操作类型：10--收藏 20--点赞 30--观看 40--取消收藏 50--取消点赞 60--不感兴趣
     * @param params
     * @return
     */
    @Override
    public synchronized int updateVideosCount(Map<String, Object> params) {
        String id = MapUtils.getString(params, "id");
        String userId = MapUtils.getString(params, "userId");
        String opType = MapUtils.getString(params, "opType");
        String appType = MapUtils.getString(params, "appType");
        int num = 0;
        if (!"60".equals(opType)) {
            //更新小视频表
            num = smallVideosMapper.updateIOSVideosCount(params);
        }
        if (!"30".equals(opType)) {
            if ("60".equals(opType)) {
                num = 1;
            }
            if (num == 1) {
                //查询用户视频关联表
                Map<String, Object> paraMap = new HashMap<String, Object>();
                paraMap.put("userId", userId);
                paraMap.put("videoId", id);
                paraMap.put("appType", appType);
                List<SmallVideoUserVo> clUserVideosList = clUserVideosMapper.findClUserVideosList(paraMap);
                //保存用户视频关联表
                if (clUserVideosList == null || clUserVideosList.size() == 0) {
                    SmallVideoUserVo clUserVideosVo = new SmallVideoUserVo();
                    clUserVideosVo.setUserId(Long.valueOf(userId));
                    clUserVideosVo.setVideoId(Long.valueOf(id));
                    clUserVideosVo.setCollection("0");
                    clUserVideosVo.setLove("0");
                    clUserVideosVo.setInterest("0");
                    clUserVideosVo.setAppType(appType);
                    if (COLLECTION.equals(opType)) {
                        clUserVideosVo.setCollection("1");
                    } else if (LOVE.equals(opType)) {
                        clUserVideosVo.setLove("1");
                    } else if (NO_INTEREST_CODE.equals(opType)) {
                        clUserVideosVo.setInterest("1");
                    }
                    clUserVideosVo.setUpdateTime(new Date());
                    num = clUserVideosMapper.saveClUserVideos(clUserVideosVo);
                } else {
                    //更新用户视频关联表
                    SmallVideoUserVo clUserVideosVo = new SmallVideoUserVo();
                    clUserVideosVo.setUserId(Long.valueOf(userId));
                    clUserVideosVo.setVideoId(Long.valueOf(id));
                    clUserVideosVo.setOpType(opType);
                    clUserVideosVo.setUpdateTime(new Date());
                    clUserVideosVo.setAppType(appType);
                    num = clUserVideosMapper.updateClUserVideos(clUserVideosVo);
                }
            }
        }
        return num;
    }

    @Override
    public List<SmallVideoVo> findVideosDetail(Map<String, Object> params) {
        return smallVideosMapper.findSmallVideosList(params);
    }

    @Override
    public SmallVideoVo findVideosDetailByOne(Map<String, Object> params) {
        return smallVideosMapper.findVideosDetailByOne(params);
    }

}