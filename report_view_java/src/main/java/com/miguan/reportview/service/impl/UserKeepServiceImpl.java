package com.miguan.reportview.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.miguan.reportview.entity.RpUserKeep;
import com.miguan.reportview.mapper.DwUserSimpleMapper;
import com.miguan.reportview.mapper.RpUserKeepMapper;
import com.miguan.reportview.service.IUserKeepService;
import com.miguan.reportview.vo.ParamsBuilder;
import com.miguan.reportview.vo.UserKeepVo;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static java.util.concurrent.Executors.newFixedThreadPool;
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
@Slf4j
@DS("report-db")
public class UserKeepServiceImpl implements IUserKeepService {


    @Resource
    private RpUserKeepMapper rpUserKeepMapper;
    @Resource
    private DwUserSimpleMapper dwUserSimpleMapper;
    private static ExecutorService excutorService = newFixedThreadPool(20);

    @Override
    public void delete(int sd) {
        rpUserKeepMapper.delete(Wrappers.<RpUserKeep>query().le("sd", sd));
    }

    private Map<String, Object> buildQueryParams(List<String> valueTypes,
                                                 Boolean isNew,
                                                 List<String> appPackages,
                                                 List<String> appVersions,
                                                 List<String> pChannelIds,
                                                 List<String> channelIds,
                                                 int appType,
                                                 String startDate,
                                                 String endDate) {
        List<String> groups = new ArrayList<>();
        if (!isEmpty(appPackages)) {
            groups.add("package_name");
        }
        if (!isEmpty(appVersions)) {
            groups.add("app_version");
        }
        if (!isEmpty(channelIds)) {
            groups.add("change_channel");
        }
        if (!isEmpty(pChannelIds)) {
            groups.add("father_channel");
        }
        return ParamsBuilder.builder(10)
                .put("valueTypes", valueTypes)
                .put("startDate", Integer.valueOf(startDate.replaceAll("-", "")))
                .put("endDate", Integer.valueOf(endDate.replaceAll("-", "")))
                .put("showType", isNew == null ? null : isNew.booleanValue() ? 1 : 0)
                .put("appPackages", appPackages)
                .put("appVersions", appVersions)
                .put("pChannelIds", pChannelIds)
                .put("channelIds", channelIds)
                .put("groups", isEmpty(groups) ? null : groups)
                .put("appType", appType)
                .get();
    }

    private Map<String, Object> buildUserKeepParams(Boolean isNew,
                                                    List<String> appPackages,
                                                    List<String> appVersions,
                                                    List<String> pChannelIds,
                                                    List<String> channelIds,
                                                    List<String> groups,
                                                    int appType,
                                                    String startDate,
                                                    String endDate) {
        //1=应用2=版本3=新老用户4=渠道5=父渠道
        if(!isEmpty(groups)) {
            groups = groups.stream().map(e -> {
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
                    return "change_channel";
                }
                if ("5".equals(e)) {
                    return "father_channel";
                }
                throw new NumberFormatException("无效的分组类型 ".concat(e));
            }).collect(Collectors.toList());
        } else {
            groups = null;
        }

        StringBuffer sql = new StringBuffer();
        groups = groups == null ? new ArrayList<>() : groups;
        String isNewStr = isNew == null ? "" : isNew ? "1" : "0";
        groups.forEach(r-> sql.append(" and " + r +" != '-1' "));

        //
        if(!groups.contains("package_name")) {
            appPackages = (appPackages == null ? Arrays.asList("-1") : appPackages);
            String in = "'" + String.join("','", appPackages) + "'";
            sql.append(" and package_name in (" + in + ") ");
        } else if(appPackages != null && groups.contains("package_name")) {
            String in = "'" + String.join("','", appPackages) + "'";
            sql.append(" and package_name in (" + in + ") ");
        }


        if(!groups.contains("app_version")) {
            appVersions = (appVersions == null ? Arrays.asList("-1") : appVersions);
            String in = "'" + String.join("','", appVersions) + "'";
            sql.append(" and app_version in (" + in + ") ");
        } else if(appVersions != null && groups.contains("app_version")) {
            String in = "'" + String.join("','", appVersions) + "'";
            sql.append(" and app_version in (" + in + ") ");
        }

        if(!groups.contains("is_new")) {
            sql.append(" and is_new = " + isNewStr + " ");
        } else if(!"".equals(isNewStr) && groups.contains("is_new")) {
            sql.append(" and is_new = " + isNewStr + " ");
        }

        if(!groups.contains("father_channel")) {
            pChannelIds = (pChannelIds == null ? Arrays.asList("-1") : pChannelIds);
            String in = "'" + String.join("','", pChannelIds) + "'";
            sql.append(" and father_channel in (" + in + ") ");
        } else if(pChannelIds != null && groups.contains("father_channel")) {
            String in = "'" + String.join("','", pChannelIds) + "'";
            sql.append(" and father_channel in (" + in + ") ");
        }

        if(!groups.contains("change_channel")) {
            channelIds = (channelIds == null ? Arrays.asList("-1") : channelIds);
            String in = "'" + String.join("','", channelIds) + "'";
            sql.append(" and change_channel in (" + in + ") ");
        } else if(channelIds != null && groups.contains("change_channel")) {
            String in = "'" + String.join("','", channelIds) + "'";
            sql.append(" and change_channel in (" + in + ") ");
        }

        sql.append(" and app_type =" + appType);
        String conditionSql = sql.toString();

        return ParamsBuilder.builder(10)
                .put("startDate", Integer.valueOf(startDate.replaceAll("-", "")))
                .put("endDate", Integer.valueOf(endDate.replaceAll("-", "")))
                .put("conditionSql", conditionSql)
                .put("groups", groups)
                .get();
    }

    @Override
    public List<UserKeepVo> getData(List<String> valueTypes,
                                    Boolean isNew,
                                    List<String> appPackages,
                                    List<String> appVersions,
                                    List<String> pChannelIds,
                                    List<String> channelIds,
                                    int appType,
                                    String startDate,
                                    String endDate) {
        return rpUserKeepMapper.getData(buildQueryParams(valueTypes, isNew, appPackages, appVersions, pChannelIds, channelIds, appType, startDate, endDate));
    }

    final List<String> default_valueTypes = Lists.newArrayList("0", "1", "2", "3",
            "4", "5", "6", "7", "14", "30");

    @Override
    public List<RpUserKeep> getTableData(Boolean isNew,
                                         List<String> appPackages,
                                         List<String> appVersions,
                                         List<String> pChannelIds,
                                         List<String> channelIds,
                                         int appType,
                                         String startDate,
                                         String endDate) {
        return rpUserKeepMapper.getTableData(buildQueryParams(default_valueTypes, isNew, appPackages, appVersions, pChannelIds, channelIds, appType, startDate, endDate));
    }

    @Override
    public int deleteBySd(Integer sd, int appType) {
        return rpUserKeepMapper.deleteBySd(sd, appType);
    }

    @Override
    @Transactional
    public int insert(RpUserKeep rpUserKeep) {
        return rpUserKeepMapper.insert(rpUserKeep);
    }

    @Override
    public int insertBatch(List<RpUserKeep> dataList) {
        return rpUserKeepMapper.insertBatch(dataList);
    }

    @Override
    public int insertLdBatch(List<RpUserKeep> dataList) {
        return rpUserKeepMapper.insertLdBatch(dataList);
    }

    @Override
    public RpUserKeep findByDto(Map<String, Object> yesterDayUserKeepDto) {
        return rpUserKeepMapper.findByDto(yesterDayUserKeepDto);
    }

    @Override
    @Transactional
    public int updateUserAndKeep1ById(Long id, Integer user, Integer keep1) {
        return rpUserKeepMapper.updateUserAndKeep1ById(id, user, keep1);
    }

    /**
     * 同步来电指定日期的历史留存数据
     * @param sd 留存日期
     */
    public void syncLdUserKeepData(Date sd) {
        List<Integer> historyDateIndex = Arrays.asList(1,2,3,4,5,6,7,14,30);
        Map<String, Object> params = new HashMap<>();
        List<String> dateList = getHistoryDateList(sd, historyDateIndex);
        int sdInt = Integer.parseInt(DateUtil.dateStr7(sd));
        params.put("dateList", dateList);
        params.put("dd", Integer.parseInt(DateUtil.dateStr7(new Date())));
        params.put("sd", sdInt);

        int count = dwUserSimpleMapper.countLdDayUserKeep(params);
        int index = 0;
        int pageSize = 10000;
        params.put("pageSize", pageSize);
        this.deleteBySd(sdInt, 2);  //删除来电的留存数据
        while (index < count) {
            log.info("同步来电指定日期的历史留存数据, startRow:{},pageSize:{}",index, pageSize);
            params.put("startRow", index);
            excutorService.execute(() -> {
                List<RpUserKeep> dataList = dwUserSimpleMapper.findLdDayUserKeep(params);
                this.insertLdBatch(dataList);
            });
            index = index + pageSize;
        }
    }

    private List<String> getHistoryDateList(Date sd, List<Integer> historyDateIndex) {
        List<String> dateList = new ArrayList<>();
        dateList.add(DateUtil.dateStr2(sd));

        historyDateIndex.forEach(r -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sd);
            calendar.add(Calendar.DAY_OF_YEAR, r);
            dateList.add(DateUtil.dateStr2(calendar.getTime()));
        });
        return dateList;
    }

    public List<UserKeepVo> getNewUserKeep(Boolean isNew,
                                           List<String> appPackages,
                                           List<String> appVersions,
                                           List<String> pChannelIds,
                                           List<String> channelIds,
                                           List<String> groups,
                                           int appType,
                                           String startDate,
                                           String endDate) {
        return rpUserKeepMapper.getNewUserKeep(buildUserKeepParams(isNew, appPackages, appVersions, pChannelIds, channelIds, groups, appType, startDate, endDate));
    }
}
