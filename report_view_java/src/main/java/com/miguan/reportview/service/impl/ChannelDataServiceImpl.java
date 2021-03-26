package com.miguan.reportview.service.impl;

import com.baomidou.mybatisplus.extension.api.R;
import com.cgcg.context.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.miguan.reportview.common.utils.DateUtil;
import com.miguan.reportview.dto.*;
import com.miguan.reportview.entity.App;
import com.miguan.reportview.entity.RpUserKeep;
import com.miguan.reportview.mapper.ChannelDetailMapper;
import com.miguan.reportview.mapper.DwVideoActionsMapper;
import com.miguan.reportview.service.IAppService;
import com.miguan.reportview.service.IChannelDataService;
import com.miguan.reportview.service.IUserKeepService;
import com.miguan.reportview.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.miguan.reportview.common.utils.NumCalculationUtil.divide;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author zhongli
 * @date 2020-08-05
 * a
 */
@Service
public class ChannelDataServiceImpl implements IChannelDataService {

    @Resource
    private DwVideoActionsMapper dwVideoActionsMapper;
    @Resource
    private ChannelDetailMapper channelDetailMapper;
    @Resource
    private IAppService iAppService;
    @Resource
    private IUserKeepService userKeepService;

    /**
     * @param isNew
     * @param appPackages
     * @param appVersions
     * @param channelIds
     * @param groups      1=应用2=版本3=新老用户4=渠道
     * @param startDate
     * @param endDate
     * @return
     */
    private Map<String, Object> buildQueryParams(Boolean isNew,
                                                 List<String> appPackages,
                                                 List<String> appVersions,
                                                 List<String> pChannelIds,
                                                 List<String> channelIds,
                                                 List<String> groups,
                                                 String startDate,
                                                 String endDate) {

        List<String> groupColums = buildGroupColum(groups);
        String conditionSql = conditionSql(isNew, appPackages, appVersions, pChannelIds, channelIds, groupColums);
        return ParamsBuilder.builder(9)
                .put("startDate", startDate)
                .put("endDate", endDate)
                .put("showType", isNew == null ? null : isNew ? 1 : 0)
                .put("appPackages", appPackages)
                .put("appVersions", appVersions)
                .put("pChannelIds", pChannelIds)
                .put("channelIds", channelIds)
                .put("groups", isEmpty(groupColums) ? null : groupColums)
                .put("conditionSql", conditionSql)
                .get();
    }

    /**
     * 创建查询条件
     *
     * @return
     */
    private String conditionSql(Boolean isNew,
                                List<String> appPackages,
                                List<String> appVersions,
                                List<String> pChannelIds,
                                List<String> channelIds,
                                List<String> groups) {
        StringBuffer sql = new StringBuffer();
        groups = groups == null ? new ArrayList<>() : groups;
        String isNewStr = isNew == null ? "" : isNew ? "1" : "0";
        groups.forEach(r -> sql.append(" and " + r + " != '' "));

        if (!groups.contains("package_name")) {
            appPackages = (appPackages == null ? Arrays.asList("") : appPackages);
            String in = "'" + String.join("','", appPackages) + "'";
            sql.append(" and package_name in (" + in + ") ");
        } else if (appPackages != null && groups.contains("package_name")) {
            String in = "'" + String.join("','", appPackages) + "'";
            sql.append(" and package_name in (" + in + ") ");
        }

        if (!groups.contains("app_version")) {
            appVersions = (appVersions == null ? Arrays.asList("") : appVersions);
            String in = "'" + String.join("','", appVersions) + "'";
            sql.append(" and app_version in (" + in + ") ");
        } else if (appVersions != null && groups.contains("app_version")) {
            String in = "'" + String.join("','", appVersions) + "'";
            sql.append(" and app_version in (" + in + ") ");
        }

        if (!groups.contains("is_new")) {
            sql.append(" and is_new = '" + isNewStr + "' ");
        } else if (!"".equals(isNewStr) && groups.contains("is_new")) {
            sql.append(" and is_new = '" + isNewStr + "' ");
        }

        if (!groups.contains("father_channel")) {
            pChannelIds = (pChannelIds == null ? Arrays.asList("") : pChannelIds);
            String in = "'" + String.join("','", pChannelIds) + "'";
            sql.append(" and father_channel in (" + in + ") ");
        } else if (pChannelIds != null && groups.contains("father_channel")) {
            String in = "'" + String.join("','", pChannelIds) + "'";
            sql.append(" and father_channel in (" + in + ") ");
        }

        if (!groups.contains("channel")) {
            channelIds = (channelIds == null ? Arrays.asList("") : channelIds);
            String in = "'" + String.join("','", channelIds) + "'";
            sql.append(" and channel in (" + in + ") ");
        } else if (channelIds != null && groups.contains("channel")) {
            String in = "'" + String.join("','", channelIds) + "'";
            sql.append(" and channel in (" + in + ") ");
        }
        sql.append(" and incentive_tag = 0 ");

        sql.append(" and catid = '' ");   //不需要分类纬度
        sql.append(" and sync_tag = 0");
        return sql.toString();
    }

    /**
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
                return "father_channel";
            }
            throw new NumberFormatException("无效的分组类型 ".concat(e));
        }).collect(Collectors.toList());
    }


    //处理存量用户

    //时间列表


    @Override
    public List<ChannelDataDto> getData(Boolean isNew,
                                        List<String> appPackages,
                                        List<String> appVersions,
                                        List<String> pChannelIds,
                                        List<String> channelIds,
                                        List<String> groups,
                                        String startDate,
                                        String endDate) {

        Map<String, Object> params = buildQueryParams(isNew, appPackages, appVersions, pChannelIds, channelIds, groups, startDate, endDate);
       // System.out.println(startDate+"==>"+endDate);
        List<String> dateDetailList=DateUtil.getDateDetailList(startDate,endDate);
        StringBuilder  unionAllBitmapSql=new StringBuilder();
        for(String date:dateDetailList) {
            String last1Day=DateUtil.getSpecifiedDay(date.replace("-",""), -1);
            String last30Day=DateUtil.getSpecifiedDay(date.replace("-",""), -30);
            //凭借union all
           unionAllBitmapSql.append(" SELECT \n" +
                    "        toDate('"+date+"') AS dd,\n" +
                    "        groupBitmapMergeState(measure) AS nActiveUser\n" +
                    "    FROM dm_data_product.mid_roi_bitmap_result_disb\n" +
                    "    WHERE (dt >= "+last30Day+") AND (dt <= "+last1Day+") AND (app_type = 1) "+
                    "  UNION ALL ");
        }
        unionAllBitmapSql.delete(unionAllBitmapSql.lastIndexOf("UNION ALL"),unionAllBitmapSql.length()-1);
       // System.out.println(unionAllBitmapSql);
        params.put("unionAllBitmapSql",unionAllBitmapSql);
       /* List<Map<String,Object>> newStockUserList=dwVideoActionsMapper.getNewStockUsers(params);
        System.out.println(newStockUserList);*/
      //  System.out.println(params);
        List<ChannelDataVo> list = null;
        //如果查询条件不是多选，则直接查询使用cube汇总后的数据。如果查询条件有多选，则不能查询使用cube汇总的表，直接使用统计sql查询，因为cube不支持多选的汇总
        if(isMutileCondition(appPackages, appVersions, pChannelIds, channelIds)) {
            //查询条件有多选
            list = dwVideoActionsMapper.getData(params);
        } else {
            list = dwVideoActionsMapper.getNewData(params);
        }

        if (isEmpty(list)) {
            return null;
        }

        //1天后的新用户留存
        List<UserKeepVo> userKeepList = userKeepService.getNewUserKeep(true, appPackages, appVersions, pChannelIds, channelIds,
                groups, 1, startDate, endDate);
        //友盟统计的新增用户数
        if(groups == null || groups.contains("1") || groups.contains("4") || groups.contains("5")) {
            //统计纬度如果有，应用、渠道、父渠道中的一个，则需要统计友盟新增用户数
            params.put("groups", groups);
            params.put("appType", 1);  //视频类型
            List<UmengChannelDataVo> umengDataList = dwVideoActionsMapper.findUmengChannelData(params);
            return list.stream().map(r->{return convertDto(r, userKeepList, umengDataList);}).collect(Collectors.toList());
        } else {
            return list.stream().map(r->{return convertDto(r, userKeepList, null);}).collect(Collectors.toList());
        }
    }

    private boolean isMutileCondition(List<String> appPackages,List<String> appVersions,List<String> pChannelIds,List<String> channelIds) {
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
        return false;
    }

    private ChannelDataDto convertDto(ChannelDataVo vo, List<UserKeepVo> userKeepList, List<UmengChannelDataVo> umengDataList) {
        vo.setPlayTime(vo.getPlayTime() / 60000);
        ChannelDataDto dto = new ChannelDataDto();
        dto.setNewUser(vo.getNewUser());
        dto.setRegUser(vo.getRegUser());
        dto.setVbRate(divide(vo.getVbUser(), vo.getUser(), true));
        dto.setIndexshowRate(divide(vo.getIndexPageUser(), vo.getUser(), true));
        dto.setUplayRate(divide(vo.getPlayUser(), vo.getUser(), true));
        dto.setPerplayNum(divide(vo.getPlayCount(), vo.getUser(), false));
        dto.setVuplayRate(divide(vo.getVplayUser(), vo.getUser(), true));
        dto.setVperplayNum(divide(vo.getVplayCount(), vo.getUser(), false));
        dto.setPerplayTime(divide(vo.getPlayTime(), vo.getUser(), false));
        dto.setVplayRate(divide(vo.getVplayCount(), vo.getPlayCount(), true));
        dto.setAdclickRate(divide(vo.getAdclickUser(), vo.getUser(), true));
        dto.setAdclickNum(divide(vo.getAdClick(), vo.getUser(), false));
        dto.setDate(vo.getDd());
        dto.setPackageName(vo.getPackageName());
        dto.setAppVersion(vo.getAppVersion());
        dto.setIsNew(vo.getIsNew());
        dto.setChannel(vo.getChannel());
        dto.setFatherChannel(vo.getFatherChannel());
        dto.setNewStockUser(vo.getNewStockUser());

        //新用户留存
        if(userKeepList != null && !userKeepList.isEmpty()) {
            String date = dto.getDate().replace("-", "");
            String packageName = StringUtils.isBlank(dto.getPackageName()) ? "-1" : dto.getPackageName();
            String appVersion = StringUtils.isBlank(dto.getAppVersion()) ? "-1" : dto.getAppVersion();
            String fatherChannel = StringUtils.isBlank(dto.getFatherChannel()) ? "-1" : dto.getFatherChannel();
            String channel = StringUtils.isBlank(dto.getChannel()) ? "-1" : dto.getChannel();
            String key = date + packageName + appVersion  + fatherChannel + channel;
            for(UserKeepVo r:userKeepList) {
                String packageName1 = StringUtils.isBlank(r.getPackageName()) ? "-1" : r.getPackageName();
                String appVersion1 = StringUtils.isBlank(r.getAppVersion()) ? "-1" : r.getAppVersion();
                String fatherChannel1 = StringUtils.isBlank(r.getFatherChannel()) ? "-1" : r.getFatherChannel();
                String channel1 = StringUtils.isBlank(r.getChangeChannel()) ? "-1" : r.getChangeChannel();
                String userKeepKey = r.getDd() + packageName1 + appVersion1 + fatherChannel1 + channel1;
                if(key.equals(userKeepKey)) {
                    dto.setNewUserKeepRate(divide(r.getShowValue(), r.getUser(), true));
                    break;
                }
            }
        }

        //新增用户数（友盟）
        if(umengDataList != null && !umengDataList.isEmpty()) {
            String date = dto.getDate();
            String packageName = StringUtils.isBlank(dto.getPackageName()) ? "" : dto.getPackageName();
            String fatherChannel = StringUtils.isBlank(dto.getFatherChannel()) ? "" : dto.getFatherChannel();
            String channel = StringUtils.isBlank(dto.getChannel()) ? "" : dto.getChannel();
            String key = date + packageName + fatherChannel  + channel;
            for(UmengChannelDataVo u : umengDataList) {
                if(key.equals(u.getKey())) {
                    dto.setNewUserUmeng(u.getNewUser());
                    break;
                }
            }
        }
        return dto;
    }

    @Override
    public List<LdChannelDataDto> getLdData(List<String> appVersions,
                                            Boolean isNewApp,
                                            List<String> pChannelIds,
                                            List<String> channelIds,
                                            List<String> groups,
                                            String startDate,
                                            String endDate) {
        Map<String, Object> params = buildLdQueryParams(appVersions, isNewApp, channelIds, pChannelIds,  groups, startDate, endDate);
        List<String> dateDetailList=DateUtil.getDateDetailList(startDate,endDate);
        StringBuilder  unionAllBitmapSql=new StringBuilder();
        for(String date:dateDetailList) {
            String last1Day=DateUtil.getSpecifiedDay(date.replace("-",""), -1);
            String last30Day=DateUtil.getSpecifiedDay(date.replace("-",""), -30);
            //凭借union all
            unionAllBitmapSql.append(" SELECT \n" +
                    "        toDate('"+date+"') AS dd,\n" +
                    "        groupBitmapMergeState(measure) AS nActiveUser\n" +
                    "    FROM dm_data_product.mid_roi_bitmap_result_disb\n" +
                    "    WHERE (dt >= "+last30Day+") AND (dt <= "+last1Day+") AND (app_type = 2) "+
                    "  UNION ALL ");
        }
        unionAllBitmapSql.delete(unionAllBitmapSql.lastIndexOf("UNION ALL"),unionAllBitmapSql.length()-1);
        params.put("unionAllBitmapSql",unionAllBitmapSql);
        List<LdUserContentDataVo> list =null;
        //如果查询条件不是多选，则直接查询使用cube汇总后的数据。如果查询条件有多选，则不能查询使用cube汇总的表，直接使用统计sql查询，因为cube不支持多选的汇总
        if(isMutileCondition(new ArrayList<>(), appVersions, pChannelIds, channelIds)) {
            //查询条件有多选
            list = dwVideoActionsMapper.getLdMultipleData(params);
        } else {
            list = dwVideoActionsMapper.getLdData(params);
        }
        if (isEmpty(list)) {
            return null;
        }
        //1天后的新用户留存
        List<UserKeepVo> userKeepList = userKeepService.getNewUserKeep(true, null, appVersions, pChannelIds, channelIds,
                groups, 2,startDate, endDate);
        //友盟统计的新增用户数
        if(groups == null || groups.contains("1") || groups.contains("4") || groups.contains("5")) {
            //统计纬度如果有，应用、渠道、父渠道中的一个，则需要统计友盟新增用户数
            params.put("groups", groups);
            params.put("appType", 2);  //视频类型
            List<UmengChannelDataVo> umengDataList = dwVideoActionsMapper.findUmengChannelData(params);
            return list.stream().map(r->{return convertLdDto(r, userKeepList, umengDataList);}).collect(Collectors.toList());
        } else {
            return list.stream().map(r->{return convertLdDto(r, userKeepList, null);}).collect(Collectors.toList());
        }
    }

    private Map<String, Object> buildLdQueryParams(List<String> appVersions,
                                                   Boolean isNewApp,
                                                   List<String> channelIds,
                                                   List<String> pChannelIds,
                                                   List<String> groups,
                                                   String startDate,
                                                   String endDate) {
        List<String> groupColums = buildLdGroupColum(groups);
        String conditionSql = conditionLdSql(appVersions, isNewApp, channelIds, pChannelIds, groupColums);
        return ParamsBuilder.builder(9)
                .put("startDate", startDate)
                .put("endDate", endDate)
                .put("showType", isNewApp == null ? null : isNewApp ? 1 : 0)
                .put("appVersions", appVersions)
                .put("pChannelIds", pChannelIds)
                .put("channelIds", channelIds)
                .put("groups", isEmpty(groupColums) ? null : groupColums)
                .put("conditionSql", conditionSql)
                .get();
    }

    /**
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
                return "father_channel";
            }
            throw new NumberFormatException("无效的分组类型 ".concat(e));
        }).collect(Collectors.toList());
    }

    /**
     * 创建来电查询条件
     *
     * @return
     */
    private String conditionLdSql(List<String> appVersions,
                                  Boolean isNewApp,
                                  List<String> channelIds,
                                  List<String> pChannelIds,
                                  List<String> groups) {
        StringBuffer sql = new StringBuffer();
        groups = groups == null ? new ArrayList<>() : groups;
        String isNewStr = isNewApp == null ? "" : isNewApp ? "1" : "0";
        groups.forEach(r -> sql.append(" and " + r + " != '' "));

        if (!groups.contains("app_version")) {
            appVersions = (appVersions == null ? Arrays.asList("") : appVersions);
            String in = "'" + String.join("','", appVersions) + "'";
            sql.append(" and app_version in (" + in + ") ");
        } else if (appVersions != null && groups.contains("app_version")) {
            String in = "'" + String.join("','", appVersions) + "'";
            sql.append(" and app_version in (" + in + ") ");
        }

        if (!groups.contains("is_new_app")) {
            sql.append(" and is_new_app = '" + isNewStr + "' ");
        } else if (!"".equals(isNewStr) && groups.contains("is_new_app")) {
            sql.append(" and is_new_app = '" + isNewStr + "' ");
        }

        if (!groups.contains("father_channel")) {
            pChannelIds = (pChannelIds == null ? Arrays.asList("") : pChannelIds);
            String in = "'" + String.join("','", pChannelIds) + "'";
            sql.append(" and father_channel in (" + in + ") ");
        } else if (pChannelIds != null && groups.contains("father_channel")) {
            String in = "'" + String.join("','", pChannelIds) + "'";
            sql.append(" and father_channel in (" + in + ") ");
        }

        if (!groups.contains("channel")) {
            channelIds = (channelIds == null ? Arrays.asList("") : channelIds);
            String in = "'" + String.join("','", channelIds) + "'";
            sql.append(" and channel in (" + in + ") ");
        } else if (channelIds != null && groups.contains("channel")) {
            String in = "'" + String.join("','", channelIds) + "'";
            sql.append(" and channel in (" + in + ") ");
        }
        sql.append(" and video_type = '' ");   //不需要分类纬度
        sql.append(" and sync_tag = 0");
        return sql.toString();
    }

    private LdChannelDataDto convertLdDto(LdUserContentDataVo vo, List<UserKeepVo> userKeepList, List<UmengChannelDataVo> umengDataList) {
        LdChannelDataDto dto = new LdChannelDataDto();
        BeanUtils.copyProperties(vo, dto);
        dto.setDate(vo.getDd());
        int activeUser = vo.getActiveUser();  //日活
        dto.setHomePageBrowseRate(divide(vo.getBrowseHomePageUser(), activeUser, true)); //首页浏览率=首页浏览用户/日活
        dto.setUserConversionRate(divide( vo.getDetailPlayUser(), activeUser, true)); //用户转化率=详情页播放用户/日活
        dto.setSetConversionRate(divide(vo.getSetUser(), activeUser, true)); //设置转化率=设置用户/日活（包含来电、锁屏、壁纸、微信/qq皮肤）
        dto.setSetPhoneRate(divide(vo.getSetPhoneUser(), activeUser, true)); //来电设置成功率=来电设置成功用户数/日活
        dto.setSetWallpaperRate(divide(vo.getSetWallpaperUser(), activeUser, true)); //壁纸设置成功率=壁纸设置成功用户数/日活
        dto.setSetLockScreenRate(divide(vo.getSetLockScreenUser(), activeUser, true));//锁屏设置成功率=锁屏设置成功用户数/日活
        dto.setSetSkinRate(divide(vo.getSetSkinUser(), activeUser, true)); //QQ/微信皮肤设置成功率=QQ/微信皮肤设置成功用户数/日活
        dto.setSetRingConfirmRate(divide(vo.getSetRingConfirmUser(), activeUser, true)); //铃声设置成功率=铃声设置成功用户数/日活
        dto.setAdClickRate(divide(vo.getAdClickUser(), activeUser, true)); //广告点击转化率=广告点击用户/日活
        dto.setPreAdClick(divide(vo.getAdClickCount(), activeUser, false)); //人均广告点击次数=广告点击次数/日活
        dto.setNewStockUser(vo.getNewStockUser());
        //新用户留存
        if(userKeepList != null && !userKeepList.isEmpty()) {
            String date = dto.getDate().replace("-", "");
            String appVersion = StringUtils.isBlank(dto.getAppVersion()) ? "-1" : dto.getAppVersion();
            String fatherChannel = StringUtils.isBlank(dto.getFatherChannel()) ? "-1" : dto.getFatherChannel();
            String channel = StringUtils.isBlank(dto.getChannel()) ? "-1" : dto.getChannel();
            String key = date + appVersion  + fatherChannel + channel;
            for(UserKeepVo r:userKeepList) {
                String appVersion1 = StringUtils.isBlank(r.getAppVersion()) ? "-1" : r.getAppVersion();
                String fatherChannel1 = StringUtils.isBlank(r.getFatherChannel()) ? "-1" : r.getFatherChannel();
                String channel1 = StringUtils.isBlank(r.getChangeChannel()) ? "-1" : r.getChangeChannel();
                String userKeepKey = r.getDd()  + appVersion1 + fatherChannel1 + channel1;
                if(key.equals(userKeepKey)) {
                    dto.setNewUserKeepRate(divide(r.getShowValue(), r.getUser(), true));
                    break;
                }
            }
        }

        //新增用户数（友盟）
        if(umengDataList != null && !umengDataList.isEmpty()) {
            String date = dto.getDate();
            String fatherChannel = StringUtils.isBlank(dto.getFatherChannel()) ? "" : dto.getFatherChannel();
            String channel = StringUtils.isBlank(dto.getChannel()) ? "" : dto.getChannel();
            String key = date + fatherChannel  + channel;
            for(UmengChannelDataVo u : umengDataList) {
                if(key.equals(u.getKey())) {
                    dto.setNewUserUmeng(u.getNewUser());
                    break;
                }
            }
        }
        return dto;
    }

    /**
     * 视频渠道明细数据
     *
     * @param appPackages  应用
     * @param appVersions  版本
     * @param channelIds   子渠道
     * @param orderByField 排序字段
     * @param startDate    开始时间
     * @param endDate      结束时间
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @param startRow 分页起始记录数
     * @param endRow 分页结束记录数(startRow和endRow参数，只能与pageNum和pageSize两参数任选一种)
     * @return
     */
    public PageInfo<ChannelDetailDto> listChannelDetail(List<String> appPackages, List<String> appVersions, List<String> channelIds, String orderByField,
                                                        String startDate, String endDate, Integer pageNum, Integer pageSize,Integer startRow, Integer endRow) {
        if (StringUtils.isBlank(orderByField)) {
            orderByField = "dd desc";
        }
        Map<String, Object> params = new HashMap<>();
        params.put("appPackages", appPackages);
        params.put("appVersions", appVersions);
        params.put("channelIds", channelIds);
        params.put("orderByField", orderByField);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        if(pageNum != null) {
            PageHelper.startPage(pageNum, pageSize);
        } else if(startRow != null) {
            startRow = (startRow == 0 ? 0 : startRow -1);
            PageHelper.offsetPage(startRow,endRow - startRow);
        }
        PageInfo<ChannelDetailDto> pageList = channelDetailMapper.listChannelDetail(params).toPageInfo();
        pageList.getList().stream().forEach(r->{
            App app = iAppService.getAppByPackageName(r.getPackageName());
            if(app != null) {
                r.setPackageName(app.getName());  //应用名称
            }
        });
        return pageList;
    }

    private List<String> seqArray2List(String str) {
        if (org.apache.commons.lang3.StringUtils.isBlank(str)) {
            return null;
        }
        String[] arrayS = str.split(",");
        return Stream.of(arrayS).filter(org.apache.commons.lang3.StringUtils::isNotBlank).map(String::trim).collect(Collectors.toList());
    }


    /**
     * 渠道ROI评估
     **/
    public List<ChannelRoiEstimateDto> getRoiEstimate(Map<String, Object> params){

//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        String updateDt=formatter.format(new Date());
//        params.put("updateDt", updateDt);
//        Object channelIds = params.get("channelIds");
//        if(channelIds!=null){
//            params.put("channelIds", isBlank(channelIds.toString()) ? null : channelIds.toString().split(","));
//        }

        List<ChannelRoiEstimateDto> list = channelDetailMapper.getRoiEstimate( params);
        return list;
    };

    /**
     * 渠道ROI预测
     **/
    public List<ChannelRoiPrognosisDto> getRoiPrognosis(Map<String, Object> params){

//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        String updateDt=formatter.format(new Date());
//        params.put("updateDt", updateDt);
//        Object channelIds = params.get("channelIds");
//        if(channelIds!=null){
//            params.put("channelIds", isBlank(channelIds.toString()) ? null : channelIds.toString().split(","));
//        }

        List<ChannelRoiPrognosisDto> list = channelDetailMapper.getRoiPrognosis( params);
        return list;
    };


    /**
     * 渠道ROI评估-条数（分页用）
     **/
    public int getRoiEstimateCount(Map<String, Object> params){
        int count = channelDetailMapper.getRoiEstimateCount( params);
        return count;
    };

    /**
     * 渠道ROI预测-条数（分页用）
     **/
    public int getRoiPrognosisCount(Map<String, Object> params){
        int count = channelDetailMapper.getRoiPrognosisCount( params );
        return count;
    };


    /**
     * 渠道ROI评估-获取最大更新时间
     **/
    public String getRoiEstimateDate(){
        return  channelDetailMapper.getRoiEstimateDate();
    };

    /**
     * 渠道ROI预测-获取最大更新时间
     **/
    public String getRoiPrognosisDate(){
        return  channelDetailMapper.getRoiPrognosisDate();
    };



        /**
     * 统计渠道每小时的人均点击数
     * @param type  1--根据子渠道汇总，2--根据父渠道汇总
     * @param date  日期，格式：yyyy-MM-dd
     * @param channelList  渠道列表
     */
    public List<AdClickNumVo> staChannelPreAdClick(int type, String date, List<String> channelList) {
        Map<String, Object> params = new HashMap<>();
        params.put("dd", date);
        params.put("channelField", type == 1 ? "channel" : "father_channel");
        params.put("channelList", channelList);
        List<AdClickNumVo> list = dwVideoActionsMapper.staChannelPreAdClick(params);
        return list;
    }


}
