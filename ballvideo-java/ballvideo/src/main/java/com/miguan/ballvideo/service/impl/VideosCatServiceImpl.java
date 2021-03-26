package com.miguan.ballvideo.service.impl;

import com.github.pagehelper.PageHelper;
import com.miguan.ballvideo.common.util.ChannelUtil;
import com.miguan.ballvideo.entity.MarketAudit;
import com.miguan.ballvideo.entity.VideoGather;
import com.miguan.ballvideo.mapper.VideosCatMapper;
import com.miguan.ballvideo.mapper.VideosCatSortMapper;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.MarketAuditService;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.service.VideosCatService;
import com.miguan.ballvideo.vo.VideosCatSortVo;
import com.miguan.ballvideo.vo.VideosCatVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 首页视频分类表ServiceImpl
 *
 * @author xy.chen
 * @date 2019-08-09
 **/

@Service("videosCatService")
public class VideosCatServiceImpl implements VideosCatService {

	@Resource
	private VideosCatMapper videosCatMapper;

    @Resource
    private MarketAuditService  marketAuditService;

    @Resource
    private RedisService redisService;

    @Resource
    private VideosCatSortMapper videosCatSortMapper;

    //屏蔽美女视频分类
    private final static int removeCatId = 251;

    @Override
    public List<VideosCatVo> findFirstVideosCatList(String channelId, String appVersion, String appPackage) {
        Map<String, Object> params = new HashMap<>();
        //根据渠道和版本号查询排序
        this.getVideosCatSort(channelId, appVersion, appPackage, params);
        params.put("state", "1");//状态 2关闭 1开启
        params.put("type", "10");//类型 10首页视频 20 小视频
        params.put("appPackage", appPackage);//作用包
        marketAudit(channelId, appVersion, params,"");
        return videosCatMapper.findFirstVideosCatList(params);
    }
    //根据渠道和版本号查询排序
    private void getVideosCatSort(String channelId, String appVersion, String appPackage, Map<String, Object> params) {
        String lastChannelId = channelId;
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("channelId", channelId);
        paraMap.put("appPackage",appPackage);
        paraMap.put("appVersion",appVersion);
        paraMap.put("parentFlag", 2);
        //查询子渠道排序是否存在
        List<VideosCatSortVo> list = videosCatSortMapper.queryVideosCatSortList(paraMap);
        if (CollectionUtils.isEmpty(list)){
            //查询父渠道排序是否存在
            lastChannelId = ChannelUtil.filter(channelId);
            paraMap.put("channelId", lastChannelId);
            paraMap.put("parentFlag", 1);
            list = videosCatSortMapper.queryVideosCatSortList(paraMap);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, List<VideosCatSortVo>> collect = list.stream().collect(Collectors.groupingBy(VideosCatSortVo::getChannel));
            String sorts = "";
            if (list.size() > 1) {
                for (String str : collect.keySet()) {
                    String[] split = str.split(",");
                    List<String> list1 = Arrays.asList(split);
                    if (list1.contains(lastChannelId)) {
                        sorts = collect.get(str).get(0).getSort();
                        break;
                    }
                }
            } else {
                sorts = list.get(0).getSort();
            }
            if (StringUtils.isNotBlank(sorts)) {
                params.put("sorts", sorts);
            }
        }
    }

    @Override
    public Map<String, Object> findFirstVideosCatList18(String channelId, String appVersion,String appPackage,String deviceId,String teenagerModle) {
        Map<String, Object> params = new HashMap<>();
        //根据渠道和版本号查询排序
        this.getVideosCatSort(channelId, appVersion, appPackage, params);
        params.put("state", "1");//状态 2关闭 1开启
        params.put("type", "10");//类型 10首页视频 20 小视频
        params.put("appPackage", appPackage);//作用包
        String excludeCatIds = marketAudit(channelId, appVersion, params,teenagerModle);
        List<VideosCatVo> firstVideosCatList = videosCatMapper.findFirstVideosCatList(params);
        Map<String, Object> result = new HashMap<>();
        result.put("firstVideosCatList", firstVideosCatList);
        result.put("excludeCatIds", excludeCatIds);
        return result;
    }

    @Override
    public List<String> getCatIdsByStateAndType(int state, String type) {
        String key = RedisKeyConstant.CAT_IDS+state+"_"+type;
        String catIds = redisService.get(key);
        if(StringUtils.isNotEmpty(catIds))return new ArrayList<>(Arrays.asList(catIds.split(",")));
        List<String> catIds_db = videosCatMapper.getCatIdsByStateAndType(state,type);
        if(CollectionUtils.isNotEmpty(catIds_db)){
            redisService.set(key,String.join(",",catIds_db),RedisKeyConstant.CAT_IDS_SECONDS);
        }
        return catIds_db;
    }

    //市场屏蔽分类标签+青少年模式屏蔽分类标签
    private String marketAudit(String channelId, String appVersion, Map<String, Object> params,String teenagerModle) {
        //2.0.0新增青少年模式，如果用户开启&&后台也开启，则过滤
        if("1".equals(teenagerModle)){
            String otherCatIds = marketAuditService.getCatIdsByChannelIdAndAppVersionFromTeenager(channelId, appVersion);
            if(StringUtils.isNotEmpty(otherCatIds)){
                params.put("excludeCatIds", Arrays.asList(otherCatIds.split(",")));
                return otherCatIds;
            }
        }
        String catIds="";
        //根据渠道和版本号进行市场屏蔽
        MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(channelId, appVersion);
        if(marketAudit!=null && StringUtils.isNotEmpty(marketAudit.getCatIds())){
            catIds = marketAudit.getCatIds();
            params.put("excludeCatIds", Arrays.asList(catIds.split(",")));
        }
        return catIds;
    }

    @Override
    public List<VideoGather> findGatherIdsNotIn(String[] excludeIds, int currentPage, int pageSize){
        PageHelper.startPage(currentPage, pageSize);
        return videosCatMapper.findGatherIdsNotIn(excludeIds);
    }

    @Override
    public boolean existByCatIdAndApp(String catId, String appPackage) {
        Map params = new HashMap();
        params.put("id",catId);
        params.put("appPackage",appPackage);
        int count = videosCatMapper.existByCatIdAndApp(params);
        if(count > 0){
            return true;
        } else {
            return false;
        }
    }


}