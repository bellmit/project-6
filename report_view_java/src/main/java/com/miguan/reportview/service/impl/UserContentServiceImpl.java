package com.miguan.reportview.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.miguan.reportview.common.enmus.VideosSourceEnmu;
import com.miguan.reportview.dto.LdUserContentDto;
import com.miguan.reportview.dto.UserContentDetailDto;
import com.miguan.reportview.dto.UserContentDto;
import com.miguan.reportview.dto.VideoSectionDataDto;
import com.miguan.reportview.mapper.DwUserActionsMapper;
import com.miguan.reportview.mapper.FirstVideosMapper;
import com.miguan.reportview.service.IUserContentService;
import com.miguan.reportview.service.IVideoStaService;
import com.miguan.reportview.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.miguan.reportview.common.utils.NumCalculationUtil.divide;
import static com.miguan.reportview.common.utils.NumCalculationUtil.roundHalfUpDouble;
import static org.apache.commons.collections4.CollectionUtils.containsAny;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * <p>
 * 视频数据宽表 服务实现类
 * </p>
 *
 * @author zhongli
 * @since 2020-08-04
 */
@Service
public class UserContentServiceImpl implements IUserContentService {

    @Resource
    private DwUserActionsMapper dwUserActionsMapper;
    @Autowired
    private IVideoStaService videoStaService;
    @Resource
    private FirstVideosMapper firstVideosMapper;

    /**
     *
     * @param isNew
     * @param appPackages
     * @param appVersions
     * @param channelIds
     * @param groups 1=应用2=版本3=新老用户4=渠道
     * @param startDate
     * @param endDate
     * @return
     */
    private Map<String, Object> buildQueryParams(Boolean isNew,
                                                 List<String> appPackages,
                                                 List<String> appVersions,
                                                 List<String> pChannelIds,
                                                 List<String> channelIds,
                                                 List<String> catIds,
                                                 Integer incentiveTag,
                                                 List<String> videoSources,
                                                 List<String> groups,
                                                 String startDate,
                                                 String endDate) {

        List<String> groupColums = buildGroupColum(groups);
        List<String> orderColums = buildGroupColum(groups);
        String conditionSql = conditionSql(isNew, appPackages, appVersions, pChannelIds, channelIds, catIds, incentiveTag, groupColums);
        return ParamsBuilder.builder(11)
                .put("startDate", startDate)
                .put("endDate", endDate)
                .put("showType", isNew == null ? null : isNew ? 1 : 0)
                .put("appPackages", appPackages)
                .put("appVersions", appVersions)
                .put("pChannelIds", pChannelIds)
                .put("channelIds", channelIds)
                .put("catIds", isEmpty(catIds) ? null : catIds.stream().mapToInt(Integer::parseInt).toArray())
                .put("incentiveTag", incentiveTag)
                .put("videoSources", videoSources)
                .put("groups", isEmpty(groupColums) ? null : groupColums)
                .put("orderbys", isEmpty(orderColums) ? null : orderColums)
                .put("conditionSql", conditionSql)
                .get();
    }

    /**
     * 创建查询条件
     * @return
     */
    private String conditionSql(Boolean isNew,
                                List<String> appPackages,
                                List<String> appVersions,
                                List<String> pChannelIds,
                                List<String> channelIds,
                                List<String> catIds,
                                Integer incentiveTag,
                                List<String> groups) {
        StringBuffer sql = new StringBuffer();
        groups = groups == null ? new ArrayList<>() : groups;
        String isNewStr = isNew == null ? "" : isNew ? "1" : "0";
        groups.forEach(r-> sql.append(" and " + r +" != '' "));

        if(!groups.contains("package_name")) {
            appPackages = (appPackages == null ? Arrays.asList("") : appPackages);
            String in = "'" + String.join("','", appPackages) + "'";
            sql.append(" and package_name in (" + in + ") ");
        } else if(appPackages != null && groups.contains("package_name")) {
            String in = "'" + String.join("','", appPackages) + "'";
            sql.append(" and package_name in (" + in + ") ");
        }

        if(!groups.contains("app_version")) {
            appVersions = (appVersions == null ? Arrays.asList("") : appVersions);
            String in = "'" + String.join("','", appVersions) + "'";
            sql.append(" and app_version in (" + in + ") ");
        } else if(appVersions != null && groups.contains("app_version")) {
            String in = "'" + String.join("','", appVersions) + "'";
            sql.append(" and app_version in (" + in + ") ");
        }

        if(!groups.contains("is_new")) {
            sql.append(" and is_new = '" + isNewStr + "' ");
        } else if(!"".equals(isNewStr) && groups.contains("is_new")) {
            sql.append(" and is_new = '" + isNewStr + "' ");
        }

        if(!groups.contains("father_channel")) {
            pChannelIds = (pChannelIds == null ? Arrays.asList("") : pChannelIds);
            String in = "'" + String.join("','", pChannelIds) + "'";
            sql.append(" and father_channel in (" + in + ") ");
        } else if(pChannelIds != null && groups.contains("father_channel")) {
            String in = "'" + String.join("','", pChannelIds) + "'";
            sql.append(" and father_channel in (" + in + ") ");
        }

        if(!groups.contains("channel")) {
            channelIds = (channelIds == null ? Arrays.asList("") : channelIds);
            String in = "'" + String.join("','", channelIds) + "'";
            sql.append(" and channel in (" + in + ") ");
        } else if(channelIds != null && groups.contains("channel")) {
            String in = "'" + String.join("','", channelIds) + "'";
            sql.append(" and channel in (" + in + ") ");
        }

        if(!groups.contains("catid")) {
            catIds = (catIds == null ? Arrays.asList("") : catIds);
            String in = "'" + String.join("','", catIds) + "'";
            sql.append(" and catid in (" + in + ") ");
        } else if(catIds != null && groups.contains("catid")) {
            String in = "'" + String.join("','", catIds) + "'";
            sql.append(" and catid in (" + in + ") ");
        }

        if(incentiveTag == 0) {
            sql.append(" and incentive_tag = 0 ");
        } else {
            sql.append(" and incentive_tag = 1 ");
        }
        sql.append(" and catid != '0'");
        sql.append(" and sync_tag = 0");
        return sql.toString();
    }

    /**
     *
     * @param groups 1=应用2=版本3=新老用户4=渠道
     * @return
     */
    private List<String> buildGroupColum(List<String> groups) {
        if (isEmpty(groups)) {
            return null;
        }
        return groups.stream().map(e -> {
            if ("1".equals(e)) {
                return "package_name";
            }
            if ("2".equals(e)) {
                return "app_version";
            }
            if ("3".equals(e)) {
                return "is_new";
            }
            if ("4".equals(e)) {
                return "channel";
            }
            if ("5".equals(e)) {
                return "catid";
            }
            if ("6".equals(e)) {
                return "father_channel";
            }
            if ("7".equals(e)) {
                return "videos_source";
            }
            throw new NumberFormatException("无效的分组类型 ".concat(e));
        }).collect(Collectors.toList());
    }

    @Override
    public List<UserContentDto> getData(Boolean isNew,
                                        List<String> appPackages,
                                        List<String> appVersions,
                                        List<String> pChannelIds,
                                        List<String> channelIds,
                                        List<String> catIds,
                                        Integer incentiveTag,
                                        List<String> videoSources,
                                        List<String> groups,
                                        String startDate,
                                        String endDate) {

        Map<String, Object> params = buildQueryParams(isNew, appPackages, appVersions, pChannelIds, channelIds, catIds, incentiveTag, videoSources,groups, startDate, endDate);
        List<UserContentDataVo> list = null;
        //如果查询条件不是多选，则直接查询使用cube汇总后的数据。如果查询条件有多选，则不能查询使用cube汇总的表，直接使用统计sql查询，因为cube不支持多选的汇总
        if(isMutileCondition(appPackages, appVersions, pChannelIds, channelIds, catIds) || (groups != null && groups.contains("7")) || videoSources != null) {
            //查询条件有多选
            list = dwUserActionsMapper.getStaData(params);
        } else {
            list = dwUserActionsMapper.getNewData(params);
        }
        if (isEmpty(list)) {
            return null;
        }
        if (isEmpty(appPackages) && isEmpty(appVersions) && isEmpty(channelIds)) {
            boolean ise = isEmpty(groups);
            if (ise || !containsAny(groups, "1", "2", "3", "4")) {
                List<VideoStaVo> list2 = videoStaService.getData(startDate, endDate, !ise && groups.contains("5"));
                return list.stream().map(e -> convertDto(e, list2)).collect(Collectors.toList());
            }
        }
        return list.stream().map(e -> convertDto(e, null)).collect(Collectors.toList());
    }

    private boolean isMutileCondition(List<String> appPackages,List<String> appVersions,List<String> pChannelIds,List<String> channelIds,List<String> catIds) {
        if(appPackages != null && appPackages.size() >= 2) {
            return true;
        }
        if(appVersions != null && appVersions.size() >= 2) {
            return true;
        }
        if(pChannelIds != null && pChannelIds.size() >= 2) {
            return true;
        }
        if(channelIds != null && channelIds.size() >= 2) {
            return true;
        }
        if(catIds != null && catIds.size() >= 2) {
            return true;
        }
        return false;
    }

    private UserContentDto convertDto(UserContentDataVo vo, List<VideoStaVo> list) {
        //时间转换
        double playTimeReal = vo.getPlayTimeReal()/60000D;
        double playTime = vo.getPlayTime()/60000D;
        double videoTime = vo.getVideoTime()/60000D;

        UserContentDto dto = new UserContentDto();
        dto.setPlayNum(vo.getPlayCount());
        dto.setShowNum(vo.getShowCount());
        dto.setPlayRate(divide(vo.getPlayCount(), vo.getShowCount(), true));
        dto.setPlayUser(vo.getPlayUser());
        dto.setVplayNum(vo.getVplayCount());
        dto.setShowUser(vo.getShowUser());
        dto.setPerPlayNum(divide(vo.getPlayCount(), vo.getActiveUser(), false));
        dto.setVpuRate(divide(vo.getVplayUser(), vo.getActiveUser(), true));
        dto.setPerPlayTime(divide(playTimeReal, vo.getActiveUser(), false));
        dto.setAllPlayNum(vo.getAllPlayCount());
        dto.setVplayUser(vo.getVplayUser());

        Double jw = null;   //进文量
        Double offline = null;  //下线量
        Double atotal = null;   //总视频数
        Integer newOnlineVideoCount = null;  //新上线视频数
        Integer newOfflineVideoCount = null;   //新下线视频数
        Integer olineVideoCount = null;  //线上视频数
        Integer newCollectVideoCount = null;  //新采集视频数
        if (!isEmpty(list)) {
            for (VideoStaVo e : list) {
                if (StringUtils.isNotBlank(e.getCatId()) && !e.getCatId().equals(vo.getCatid())) {
                    continue;
                }
                if (!e.getDate().equals(vo.getDd())) {
                    continue;
                }
                //0=下线 1=进文2=汇总各分类视频总数3=新上线视频数4=新下线视频数5=线上视频数6=新采集视频数
                Long num = e.getNum();  //视频量
                switch (e.getType()) {
                    case 0:  //下线量
                        offline = num.doubleValue();
                        break;
                    case 1: //进文量
                        jw = num.doubleValue();
                        break;
                    case 2: //视频总量
                        atotal = num.doubleValue();
                        break;
                    case 3: //新上线视频数
                        newOnlineVideoCount = num.intValue();
                        break;
                    case 4: //新下线视频数
                        newOfflineVideoCount = num.intValue();
                        break;
                    case 5: //线上视频数
                        olineVideoCount = num.intValue();
                        break;
                    case 6: //新采集视频数
                        newCollectVideoCount = num.intValue();
                        break;
                }
                if (jw != null && offline != null && atotal != null && newOnlineVideoCount != null && newOfflineVideoCount != null
                        && olineVideoCount != null && newCollectVideoCount != null) {
                    break;
                }
            }
        }
        Double jwd = jw == null ? Double.NaN : jw.doubleValue();
        Double offlined = offline == null ? Double.NaN : offline.doubleValue();
        Double atotald = atotal == null ? Double.NaN : atotal.doubleValue();
        dto.setJwNum(jwd);
        dto.setOfflineNum(offlined);
        dto.setNewOnlineVideoCount(newOnlineVideoCount);
        dto.setNewOfflineVideoCount(newOfflineVideoCount);
        dto.setOlineVideoCount(olineVideoCount);
        dto.setVideoCount(atotald.intValue());
        dto.setNewCollectVideoCount(newCollectVideoCount);
        dto.setPreEndPayCount(divide(vo.getAllPlayCount(), vo.getActiveUser(), false));  //人均完播数=播放率80%以上的视频的视频数/日活用户
        dto.setPreVplayCount(divide(vo.getVplayCount(), vo.getActiveUser(), false));  //人均有效播放数=播放率30%的视频数/日活用户
        dto.setJwRate(divide(jwd, atotald, true));
        dto.setPerShowNum(divide(vo.getShowCount(), vo.getActiveUser(), false));
        dto.setPuRate(divide(vo.getPlayUser(), vo.getActiveUser(), true));
        dto.setPerPlayPro(divide(playTimeReal, videoTime, true));
        dto.setVplayRate(divide(vo.getVplayCount(), vo.getPlayCount(), true));
        dto.setPlayTime(roundHalfUpDouble(playTimeReal));
        dto.setAllPlayRate(divide(vo.getAllPlayCount(), vo.getPlayCount(), true));
        dto.setReviewNum(vo.getReviewCount());
        dto.setReviewUser(vo.getReviewUser());
        dto.setReviewRate(divide(vo.getReviewUser(), vo.getActiveUser(), true));
        dto.setPerReviewNum(divide(vo.getReviewCount(), vo.getActiveUser(), false));
        dto.setLikeNum(vo.getLikeCount());
        dto.setLikeUser(vo.getLikeUser());
        dto.setFavNum(vo.getFavCount());
        dto.setFavRate(divide(vo.getFavUser(), vo.getActiveUser(), true));
        dto.setDate(vo.getDd());
        dto.setPackageName(vo.getPackageName());
        dto.setAppVersion(vo.getAppVersion());
        dto.setIsNew(vo.getIsNew());
        dto.setChannel(vo.getChannel());
        dto.setCatId(vo.getCatid());
        dto.setVideosSource(VideosSourceEnmu.getValueByCode(vo.getVideosSource()));
        dto.setFatherChannel(vo.getFatherChannel());
        dto.setPreShowTime(divide(playTimeReal, vo.getShowCount(), false));  //每曝光播放时长=播放总时长(分)/曝光总数
        dto.setPrePlayTime(divide(playTimeReal, vo.getPlayCount(), false));  //每播放播放时长=播放总时长(分)/播放视频数

        return dto;
    }

    @Override
    public List<LdUserContentDto> getLdData(List<String> appVersions,
                                            Boolean isNewApp,
                                            List<String> channelIds,
                                            List<String> pChannelIds,
                                            List<String> videoTypes,
                                            List<String> groups,
                                            String startDate,
                                            String endDate) {
        Map<String, Object> params = buildLdQueryParams(isNewApp, appVersions, pChannelIds, channelIds, videoTypes, groups, startDate, endDate);
        List<LdUserContentDataVo> list = null;
        if(isMutileCondition(new ArrayList<>(), appVersions, pChannelIds, channelIds, videoTypes)) {
            //查询条件有多选
            list = dwUserActionsMapper.getLdMultipleData(params);
        } else {
            list = dwUserActionsMapper.getLdData(params);
        }
        if (isEmpty(list)) {
            return null;
        }
        return list.stream().map(e -> convertLdDto(e)).collect(Collectors.toList());
    }

    private Map<String, Object> buildLdQueryParams(Boolean isNewApp,
                                                   List<String> appVersions,
                                                   List<String> pChannelIds,
                                                   List<String> channelIds,
                                                   List<String> videoTypes,
                                                   List<String> groups,
                                                   String startDate,
                                                   String endDate) {

        List<String> groupColums = buildLdGroupColum(groups);
        List<String> orderColums = buildLdGroupColum(groups);
        String conditionSql = conditionLdSql(isNewApp, appVersions, pChannelIds, channelIds, videoTypes, groupColums);
        return ParamsBuilder.builder(10)
                .put("startDate", startDate)
                .put("endDate", endDate)
                .put("showType", isNewApp == null ? null : isNewApp ? 1 : 0)
                .put("appVersions", appVersions)
                .put("pChannelIds", pChannelIds)
                .put("channelIds", channelIds)
                .put("videoTypes", isEmpty(videoTypes) ? null : videoTypes.stream().mapToInt(Integer::parseInt).toArray())
                .put("groups", isEmpty(groupColums) ? null : groupColums)
                .put("orderbys", isEmpty(orderColums) ? null : orderColums)
                .put("conditionSql", conditionSql)
                .get();
    }

    /**
     *
     * @param groups 1=应用2=版本3=新老用户4=渠道
     * @return
     */
    private List<String> buildLdGroupColum(List<String> groups) {
        if (isEmpty(groups)) {
            return null;
        }
        return groups.stream().map(e -> {
            if ("2".equals(e)) {
                return "app_version";
            }
            if ("3".equals(e)) {
                return "is_new_app";
            }
            if ("4".equals(e)) {
                return "channel";
            }
            if ("5".equals(e)) {
                return "video_type";
            }
            if ("6".equals(e)) {
                return "father_channel";
            }

            throw new NumberFormatException("无效的分组类型 ".concat(e));
        }).collect(Collectors.toList());
    }

    /**
     * 创建来电查询条件
     * @return
     */
    private String conditionLdSql(Boolean isNewApp,
                                  List<String> appVersions,
                                  List<String> pChannelIds,
                                  List<String> channelIds,
                                  List<String> catIds,
                                  List<String> groups) {
        StringBuffer sql = new StringBuffer();
        groups = groups == null ? new ArrayList<>() : groups;
        String isNewStr = isNewApp == null ? "" : isNewApp ? "1" : "0";
        groups.forEach(r-> sql.append(" and " + r +" != '' "));

        if(!groups.contains("app_version")) {
            appVersions = (appVersions == null ? Arrays.asList("") : appVersions);
            String in = "'" + String.join("','", appVersions) + "'";
            sql.append(" and app_version in (" + in + ") ");
        } else if(appVersions != null && groups.contains("app_version")) {
            String in = "'" + String.join("','", appVersions) + "'";
            sql.append(" and app_version in (" + in + ") ");
        }

        if(!groups.contains("is_new_app")) {
            sql.append(" and is_new_app = '" + isNewStr + "' ");
        } else if(!"".equals(isNewStr) && groups.contains("is_new_app")) {
            sql.append(" and is_new_app = '" + isNewStr + "' ");
        }

        if(!groups.contains("father_channel")) {
            pChannelIds = (pChannelIds == null ? Arrays.asList("") : pChannelIds);
            String in = "'" + String.join("','", pChannelIds) + "'";
            sql.append(" and father_channel in (" + in + ") ");
        } else if(pChannelIds != null && groups.contains("father_channel")) {
            String in = "'" + String.join("','", pChannelIds) + "'";
            sql.append(" and father_channel in (" + in + ") ");
        }

        if(!groups.contains("channel")) {
            channelIds = (channelIds == null ? Arrays.asList("") : channelIds);
            String in = "'" + String.join("','", channelIds) + "'";
            sql.append(" and channel in (" + in + ") ");
        } else if(channelIds != null && groups.contains("channel")) {
            String in = "'" + String.join("','", channelIds) + "'";
            sql.append(" and channel in (" + in + ") ");
        }

        if(!groups.contains("video_type")) {
            catIds = (catIds == null ? Arrays.asList("") : catIds);
            String in = "'" + String.join("','", catIds) + "'";
            sql.append(" and video_type in (" + in + ") ");
        } else if(catIds != null && groups.contains("video_type")) {
            String in = "'" + String.join("','", catIds) + "'";
            sql.append(" and video_type in (" + in + ") ");
        }
        sql.append(" and video_type != '0' ");
        sql.append(" and sync_tag = 0");
        return sql.toString();
    }

    private LdUserContentDto convertLdDto(LdUserContentDataVo vo) {
        //时间转换
        double playTime = vo.getPlayTime() / 60000;
        int activeUser = vo.getActiveUser();  //日活
        int detailPlayCount = vo.getDetailPlayCount();  //详情页观看次数

        LdUserContentDto dto = new LdUserContentDto();
        BeanUtils.copyProperties(vo, dto);
        dto.setDate(vo.getDd());
        dto.setLdPlayRate(divide(detailPlayCount, vo.getVideoShowCount(), true));  //来电详情播放率=详情页播放次数/来电秀曝光量
        dto.setPrePlaySpeed(divide(detailPlayCount, vo.getVideoShowUser(), false)); //用户平均播放数=详情页播放次数/曝光用户数
        dto.setUserConversionRate(divide(vo.getDetailPlayUser(), activeUser, true)); //用户转化率=详情页播放用户/活跃用户
        dto.setSetConfirmRate(divide(vo.getSetConfirmCount(), detailPlayCount, true));  //设置成功率=设置成功数/详情页观看次数
        dto.setPreShowUser(divide(vo.getVideoShowCount(), vo.getVideoShowUser(), false)); //人均曝光量=曝光总量/曝光人数
        dto.setShareRate(divide(vo.getShareUser(), activeUser, true)); //分享率=产生分享行为用户数/日活
        dto.setSetPhoneRate(divide(vo.getSetPhoneCount(), detailPlayCount, true));//设置来电秀成功率=设置来电秀成功数/详情页观看次数
        dto.setSetLockScreenRate(divide(vo.getSetLockScreenCount(), detailPlayCount, true)); //设置锁屏成功率=设置锁屏成功次数/详情页观看次数
        dto.setSetWallpaperRate(divide(vo.getSetWallpaperCount(), detailPlayCount, true));//设置壁纸成功率=设置壁纸的成功总数/详情页观看次数
        dto.setSetSkinRate(divide(vo.getSetSkinCount(), detailPlayCount, true)); //设置皮肤成功率=设置皮肤的成功总数/详情页观看次数
        dto.setRingAuditionRate(divide(vo.getRingAuditionUser(), vo.getBrowseRingCount(), true)); //试听转化率=铃声试听用户数/浏览铃声页面用户数
        dto.setSetRingConfirmRate(divide(vo.getSetRingConfirmCount(), vo.getRingAuditionCount(), true)); //铃声设置成功转化=设置成功数/试听数
        dto.setPreAppStart(divide(vo.getAppStartCount(), activeUser, false));//人均启动次数=app启动次数/日活
        dto.setPrePlayTime(divide(playTime, activeUser, false)); //人均在线时长=使用总时长/日活
        return dto;
    }

    /**
     * 视频明细数据
     * @param appPackages 应用
     * @param appVersions 版本
     * @param catIds 分类id
     * @param sectionType 自定义曝光区间：1=6个区间，2=9个区间
     * @param nowOnlineType 是否当天上线：1=当天，2=全部
     * @param nowJwType 是否当进文：1=当天，2=全部
     * @param dateType 时间类型：1=行为日期，2=上线日期，3=进文日期
     * @param day 日期; 日期格式: yyyy-MM-dd
     * @param orderByField 排序字段,格式:字段名+空格+升降序标识
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @param startRow 分页起始记录数
     * @param endRow 分页结束记录数(startRow和endRow参数，只能与pageNum和pageSize两参数任选一种)
     * @return
     */
    public PageInfo<UserContentDetailDto> findVideoDetailList(List<String> appPackages,
                                                              List<String> appVersions,
                                                              List<Integer> catIds,
                                                              Integer sectionType,
                                                              Integer nowOnlineType,
                                                              Integer nowJwType,
                                                              Integer dateType,
                                                              String day,
                                                              Integer isIncentive,
                                                              String orderByField,
                                                              Integer pageNum,
                                                              Integer pageSize,
                                                              Integer startRow,
                                                              Integer endRow) {
        if (StringUtils.isBlank(orderByField)) {
            orderByField = "order by showNum desc";
        } else {
            orderByField = "order by " + orderByField;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("appPackages", appPackages);
        params.put("appVersions", appVersions);
        params.put("catIds", catIds);
        params.put("section", sectionType == 1 ? "section6" : "section9");
        params.put("nowOnlineType", nowOnlineType);
        params.put("nowJwType", nowJwType);
        params.put("dateType", dateType);
        params.put("day", day);
        params.put("isIncentive", isIncentive);
        params.put("orderByField", orderByField);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);

        if(pageNum != null) {
            PageHelper.startPage(pageNum, pageSize);
        } else if(startRow != null) {
            startRow = (startRow == 0 ? 0 : startRow -1);
            PageHelper.offsetPage(startRow,endRow - startRow);
        }
        PageInfo<UserContentDetailDto> pageList = firstVideosMapper.findVideoDetailList(params).toPageInfo();   //明显列表

        UserContentDetailDto total = firstVideosMapper.totalVideoDetail(params);   //汇总数据
        total.setDd("汇总");
        total.setPlayRate(divide(total.getPlayNum(), total.getShowNum(), true));  //汇总播放率
        total.setCatPlayRate(divide(total.getCatPlayNum(), total.getCatShowNum(), true));  //汇总分类平均播放率
        total.setTotalPlayRate(divide(total.getDdPlayNum(), total.getDdShowNum(), true));  //汇总天平均播放率
        total.setEveryPlayTimeR(divide(total.getPlayTimeR(), total.getShowNum(), false));  //每次曝光播放时长
        total.setPerShowNum(divide(total.getShowNum(), total.getShowUser(), false));  //人均曝光次数
        total.setPerPlayNum(divide(total.getPlayNum(), total.getPlayUser(), false));  //人均播放次数
        total.setPreEndPayCount(divide(total.getAllPlayNum(), total.getAllPlayUser(), false));  //人均完播次数
        pageList.getList().add(total);
        return pageList;
    }


    /**
     * 视频曝光区间明显汇总列表
     * @param catIds 分类id
     * @param sectionType 区间类型，1=6区间，2=9区间
     * @param day 日期，yyyy-MM-dd
     * @return
     */
    public List<VideoSectionDataDto> findVideoSectionList(List<Integer> catIds, Integer sectionType, String day) {
        Map<String, Object> params = new HashMap<>();
        params.put("section", sectionType == 1 ? "section6" : "section9");
        params.put("catIds", catIds);
        params.put("day", day);
        List<VideoSectionDataDto> list = firstVideosMapper.findVideoSectionList(params);
        VideoSectionDataDto total = getTotalVideoSectionData(list);  //汇总数据
        list.add(total);
        return list;
    }

    private VideoSectionDataDto getTotalVideoSectionData(List<VideoSectionDataDto> list) {
        VideoSectionDataDto total = new VideoSectionDataDto();
        for(VideoSectionDataDto dto : list) {
            total.setDd(dto.getDd());
            total.setSection("汇总");
            total.setAllVideoCount(total.getAllVideoCount() + dto.getAllVideoCount());
            total.setAllShowNum(total.getAllShowNum() + dto.getAllShowNum());
            total.setAllPlayNum(total.getAllPlayNum() + dto.getAllPlayNum());
            total.setAllPlayTimeR(total.getAllPlayTimeR() + dto.getAllPlayTimeR());

            total.setVideoCount(total.getVideoCount() + dto.getVideoCount());
            total.setShowNum(total.getShowNum() + dto.getShowNum());
            total.setPlayNum(total.getPlayNum() + dto.getPlayNum());
            total.setPlayTimeR(total.getPlayTimeR() + dto.getPlayTimeR());
        }
        total.setAllPlayRate(divide(total.getAllPlayNum(), total.getAllShowNum(), true));
        total.setAllPrePlayTimeR(divide(total.getAllPlayTimeR(), total.getAllPlayNum(), false));
        total.setPlayRate(divide(total.getPlayNum(), total.getShowNum(), true));
        total.setPrePlayTimeR(divide(total.getPlayTimeR(), total.getPlayNum(), false));
        return total;
    }
}
