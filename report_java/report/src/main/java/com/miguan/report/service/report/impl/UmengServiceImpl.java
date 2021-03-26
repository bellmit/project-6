package com.miguan.report.service.report.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.ocean.rawsdk.ApiExecutor;
import com.alibaba.ocean.rawsdk.client.exception.OceanException;
import com.miguan.report.common.enums.UmengAppKeyEnum;
import com.miguan.report.common.util.NumCalculationUtil;
import com.miguan.report.entity.report.UmengData;
import com.miguan.report.mapper.UmengDataMapper;
import com.miguan.report.repository.UmengDataRepository;
import com.miguan.report.service.report.UmengService;
import com.miguan.report.vo.UmengChannelVo;
import com.umeng.uapp.param.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Configuration
@Service
public class UmengServiceImpl implements UmengService {

    @Value("${umeng.appKey}")
    private String appKey;
    @Value("${umeng.secKey}")
    private String secKey;
    @Resource
    private UmengDataRepository umengDataRepository;
    @Resource
    private UmengDataMapper umengDataMapper;

    /**
     * 通过友盟获取APP使用时长
     *
     * @param appId      友盟应用ID(茜柚视频-Android:5d59ff453fc195a47d000c8c, 果果视频_Android:5dd52df2570df3a75800008d)
     * @param dateString 时间字符串(例：2020-06-01)
     * @return Double 使用时长(秒)
     */
    @Override
    public Double getDurations(String appId, String dateString) {
        UmengUappGetDurationsParam param = new UmengUappGetDurationsParam();
        param.setAppkey(appId);
        param.setDate(dateString);
        //param.setStatType("daily");
        //param.setChannel("App%20Store");
        //param.setVersion("1.0.0");

        ApiExecutor apiExecutor = new ApiExecutor(appKey, secKey);
        apiExecutor.setServerHost("gateway.open.umeng.com");

        try {
            UmengUappGetDurationsResult result = apiExecutor.execute(param);
            log.info("{}友盟获取应用时:{}", dateString, result.getAverage());
            return result.getAverage();
        } catch (OceanException e) {
            log.error("友盟获取应用时长异常，应用ID>>{}, errorCode>>{}, errorMsg>>{}", appId, e.getErrorCode(), e.getErrorMessage());
        }
        return 0d;
    }

    /**
     * 统计日活用户数，新增用户数，总用户数
     * @param appId 友盟应用ID(茜柚视频-Android:5d59ff453fc195a47d000c8c, 果果视频_Android:5dd52df2570df3a75800008d)
     * @param date 日期
     * @return
     */
    @Override
    public UmengUappDailyDataInfo getStatData(String appId, String date) {
        UmengUappGetDailyDataParam param = new UmengUappGetDailyDataParam();
        param.setDate(date);
        param.setAppkey(appId);
        ApiExecutor apiExecutor = new ApiExecutor(appKey, secKey);
        apiExecutor.setServerHost("gateway.open.umeng.com");

        try {
            UmengUappGetDailyDataResult result = apiExecutor.execute(param);
            UmengUappDailyDataInfo dailyData = result.getDailyData();
            log.info("友盟App统计数据：" + JSONObject.toJSONString(result));
            return dailyData;
        } catch (OceanException e) {
            System.out.println("errorCode=" + e.getErrorCode() + ", errorMessage=" + e.getErrorMessage());
            log.error("errorCode=" + e.getErrorCode() + ", errorMessage=" + e.getErrorMessage());
            return null;
        }
    }

    /**
     * 获取渠道维度统计数据
     * @param appId appID
     * @param date 统计日期：yyyy-MM-dd
     * @param page 页码
     * @param pageSize 每页记录数
     *
     */
    private UmengUappChannelInfo[] umengUappGetChannelData(String appId, String date, int page, int pageSize) {
        UmengUappGetChannelDataParam param = new UmengUappGetChannelDataParam();
        param.setAppkey(appId);
        param.setDate(date);
        param.setPage(page);
        param.setPerPage(pageSize);
        ApiExecutor apiExecutor = new ApiExecutor(appKey, secKey);
        apiExecutor.setServerHost("gateway.open.umeng.com");

        try {
            UmengUappGetChannelDataResult result = apiExecutor.execute(param);
            return result.getChannelInfos();
        } catch (OceanException e) {
            return null;
        }
    }

    /**
     * 新增umeng_data友盟数据
     * @param date 日期，格式：yyyy-MM-dd
     */
    @Override
    public void saveUmengData(String date) {
        UmengAppKeyEnum[] UmengApps = UmengAppKeyEnum.values();
        for(UmengAppKeyEnum appkey : UmengApps) {
                UmengData ud = new UmengData();
                UmengUappDailyDataInfo dailyDataInfo = getStatData(appkey.getAppId(), date);
                if(dailyDataInfo == null) {
                    continue;
                }
            ud.setDate(date);
            ud.setUseTime(getDurations(appkey.getAppId(), date).intValue());  //使用时长
            ud.setNewUsers(dailyDataInfo.getNewUsers());   //新增用户数
            ud.setTotalUsers(dailyDataInfo.getTotalUsers());  //总用户数
            ud.setActive(dailyDataInfo.getActivityUsers());   //日活用户数
            ud.setAppType(appkey.getAppEnum().getAppType());   //app类型
            ud.setAppId(appkey.getAppEnum().getId());   //app表对应的id
            ud.setAppName(appkey.getName());   //appName
            ud.setClientId(appkey.getClientEnum().getId());  //对应客户端类型
            ud.setCreatedAt(new Date());
            this.deleteUmengData(date, date, appkey.getName());
            umengDataRepository.save(ud);
        }
    }

    /**
     * 从友盟获取渠道对应的数据信息
     * @param date
     */
    public void saveUmengChannel(String date) {
        UmengAppKeyEnum[] UmengApps = UmengAppKeyEnum.values();
        for(UmengAppKeyEnum umengAppkey : UmengApps) {
            int page = 1;  //页码
            int pageSize = 100;  //每页记录数（最大支持100）
            List<UmengChannelVo> list = new ArrayList<>();
            while(true) {
                UmengUappChannelInfo[] channels = umengUappGetChannelData(umengAppkey.getAppId(), date, page, pageSize); //调用友盟接口，获取数据
                if(channels != null) {
                    page++;
                    for(int i=0;i<channels.length;i++) {
                        UmengChannelVo ucv = new UmengChannelVo();
                        BeanUtils.copyProperties(channels[i], ucv);
                        ucv.setPackageName(umengAppkey.getPackageName());
                        ucv.setFatherChannel(getFatherChannel(ucv.getChannel()));   //父渠道
                        ucv.setAppType(umengAppkey.getAppEnum().getAppType());
                        ucv.setAppId(umengAppkey.getAppEnum().getId());
                        ucv.setAppName(umengAppkey.getName());
                        ucv.setClientId(umengAppkey.getClientEnum().getId());
                        list.add(ucv);
                    }
                }
                if(channels == null || channels.length < pageSize) {
                    break;
                }
            }
            if(!list.isEmpty()) {
                umengDataMapper.deleteChannelData(date, umengAppkey.getPackageName());  //同步前先删除旧数据
                umengDataMapper.batchInsertChannelData(list);  //批量保存数据
            }
        }
    }

    /**
     * 根据子渠道 获取父渠道
     * @param channel
     * @return
     */
    private String getFatherChannel(String channel) {
        if("0".equals(channel)) {
            return "0";
        }
        try {
            String fatherChannel = channel;
            String lastChar = fatherChannel.substring(fatherChannel.length()-1);
            while(NumCalculationUtil.isInteger(lastChar)) {
                //如果最后一个字符串是数字，则截取掉
                fatherChannel = fatherChannel.substring(0, fatherChannel.length()-1);
                lastChar = fatherChannel.substring(fatherChannel.length()-1);
            }
            return fatherChannel;
        } catch (Exception e) {
            log.error("根据子渠道获取父渠道异常, channel="+channel, e);
            return "";
        }
    }

     /**
     * 删除友盟的数据
     * @param startDate
     * @param endDate
     */
    @Override
    public void deleteUmengData(String startDate, String endDate, String appName) {
        umengDataRepository.deleteByDate(startDate, endDate, appName);
    }

    public int countByDate(String date) {
        return umengDataRepository.countByDate(date);
    }
}
