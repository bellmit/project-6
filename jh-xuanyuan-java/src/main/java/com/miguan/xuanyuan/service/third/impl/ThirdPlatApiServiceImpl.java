package com.miguan.xuanyuan.service.third.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.miguan.xuanyuan.common.constant.RedisConstant;
import com.miguan.xuanyuan.common.enums.ThirdPlatEnum;
import com.miguan.xuanyuan.common.util.Global;
import com.miguan.xuanyuan.common.util.StringUtil;
import com.miguan.xuanyuan.mapper.ThirdPlatApiMapper;
import com.miguan.xuanyuan.service.common.RedisService;
import com.miguan.xuanyuan.service.third.ChuanShanJiaService;
import com.miguan.xuanyuan.service.third.GuanDianTongService;
import com.miguan.xuanyuan.service.third.KuaiShouService;
import com.miguan.xuanyuan.service.third.ThirdPlatApiService;
import com.miguan.xuanyuan.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;

/**
 * 第三方Service
 */
@Slf4j
@Service
public class ThirdPlatApiServiceImpl implements ThirdPlatApiService {

    @Resource
    private ChuanShanJiaService chuanShanJiaService;
    @Resource
    private GuanDianTongService guanDianTongService;
    @Resource
    private KuaiShouService kuaiShouService;
    @Resource
    private ThirdPlatApiMapper thirdPlatApiMapper;
    @Resource
    private RedisService redisService;

    /**
     * 调用第三方api接口，导入第三方广告数据
     * @param date 开始日期，格式：yyyy-MM-dd
     * @param isCover 如果数据已经导入，是否重新调用第三方api接口，并覆盖数据。true：覆盖
     */
    public void syncThirdPlatAdvData(String date, boolean isCover) {
        List<ThirdPlatVo> platList = thirdPlatApiMapper.findPlatList();
        for(ThirdPlatVo platVo : platList) {
            List<ThirdPlatDataVo> list = new ArrayList<>();
            String platKey = platVo.getPlatKey();  //平台code
            String appId = platVo.getAppId();
            String secret = platVo.getAppSecret();

            boolean isExecute = isCover || this.isNotSuccessExport(date, appId, secret);  //是否调用第三方api接口导入数据
            if(ThirdPlatEnum.GUANG_DIAN_TONG.getCode().equals(platKey) && isExecute) {
                //广点通
                list = guanDianTongService.getDataList(date, appId, secret);
            }
            if(ThirdPlatEnum.KUAI_SHOU.getCode().equals(platKey) && isExecute) {
                //快手
                list = kuaiShouService.getDataList(date, appId, secret);
            }
            if(ThirdPlatEnum.CHUANG_SHAN_JIA.getCode().equals(platKey) && isExecute) {
                //穿山甲
                list = chuanShanJiaService.getDataList(date, platVo.getUsername(), appId, secret);
            }
            if(!list.isEmpty()) {
                thirdPlatApiMapper.batchInsertPlatData(list);  //批量插入third_plat_data记录
            }
        }

//        List<String> oldAdvPackage = thirdPlatApiMapper.findOldAdvConfigPackage();  //查询出在旧配置平台配置广告的应用包名
//        List<Map<String, Object>> oldAppList = thirdPlatApiMapper.findOldAdvConfigApp(oldAdvPackage);  //查询出在旧配置平台配置广告的app信息
        List<Map<String, Object>> oldAppList = getOldAdvConfigApp();  //获取旧配置平台的应用


        //保存在轩辕配置的应用在第三方的广告数据到third_plat_data_total表
        Map<String, Object> params = Maps.newHashMap();
        params.put("date", date);
        params.put("oldAppList", oldAppList);
        thirdPlatApiMapper.saveXyThirdPlatDataTotal(params);

        //保存旧广告配置的应用在第三方的广告数据到third_plat_data_total表
        Map<String, ThirdAdPostionVo> totalNameMap = mapForAdPositionName(date);  //查询出旧广告配置平台的代码位对应的广告位名称
        List<ThirdPlatDataTotalVo> totalList = thirdPlatApiMapper.findXyThirdPlatDataTotal(params);
        if(totalList != null && !totalList.isEmpty()) {
            for(ThirdPlatDataTotalVo totalVo : totalList) {
                String key = totalVo.getAdSource() + "_" + totalVo.getAdId();
                ThirdAdPostionVo postionVo = totalNameMap.get(key);
                if(postionVo != null) {
                    totalVo.setPrice(postionVo.getPrice());
                    totalVo.setTotalName(postionVo.getTotalName());
                    totalVo.setAdTypeCode(postionVo.getAdTypeCode());
                    totalVo.setAdTypeName(postionVo.getAdTypeName());
                }
            }
            thirdPlatApiMapper.saveOldThirdPlatDataTotal(totalList);
            //往数据科学部的表插入数据，告诉他们前一天的第三方数据已经全部导入完成
            this.insertGetReady(date);
        }
    }

    /**
     * 获取旧配置平台的应用
     * @return
     */
    private List<Map<String, Object>> getOldAdvConfigApp() {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            JSONArray array = Global.getJsonArray("old_config_app_list");
            for(int i=0;i<array.size();i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();
                map.put("package_name", jsonObject.getString("package_name"));
                map.put("client_type", jsonObject.getIntValue("client_type"));
                list.add(map);
            }
        } catch (Exception e) {
            log.error("解析old_config_app_list参数配置的json串错误");
        }
        return list;
    }

    private Map<String, ThirdAdPostionVo> mapForAdPositionName(String date) {
        List<ThirdAdPostionVo> list = thirdPlatApiMapper.findOldAdIdAndTotalName(date);
        Map<String, ThirdAdPostionVo> totalNameMap = new HashMap<>();
        for(ThirdAdPostionVo one:list) {
            String playKey = one.getPlatKey();
            String adId = one.getAdId();
            totalNameMap.put(playKey + "_" + adId, one);
        }
        return totalNameMap;
    }


    /**
     * 设置redis缓存，用来确定第三方平台的广告数据是否已经成功的自动导入了
     * @param date
     * @param appId
     * @param secret
     * @return
     */
    public void setSuccessTag(String date, String appId, String secret) {
        String md5 = DigestUtils.md5Hex(appId + secret);
        String key = date + md5;
        redisService.set(key, "1", RedisConstant.DAY_SECOND);
    }

    /**
     * 获取 第三方平台的广告数据是否没有成功的自动导入了 标识
     * @param date
     * @param appId
     * @param secret
     * @return
     */
    public boolean isNotSuccessExport(String date, String appId, String secret) {
        String md5 = DigestUtils.md5Hex(appId + secret);
        String key = date + md5;
        return StringUtil.isBlank(redisService.get(key));
    }

    /**
     * 按天保存广告配置信息
     * @param date 日期，格式：yyyy-MM-dd
     */
    public void syncAdConfigLog(String date) {
        //旧配置系统的广告配置信息
        log.info("旧配置系统的广告配置信息");
        List<AdConfigLogVo> list  = thirdPlatApiMapper.findOldAdvConfigList(date);
        if(list != null && !list.isEmpty()) {
            thirdPlatApiMapper.saveAdConfigLog(list);
        }
        //轩辕系统的广告配置信息
        thirdPlatApiMapper.saveXyAdConfigLog(date);
    }

    /**
     * 往数据科学部的表插入数据，告诉他们前一天的第三方数据已经全部导入完成
     * @param date
     */
    private void insertGetReady(String date) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", date);
        params.put("dh", Integer.parseInt(date.replace("-", "")));
        int totalTotalCount = thirdPlatApiMapper.countDataTotal(params);  //已经导入的第三方平台数
        int platNum = ThirdPlatEnum.values().length;  //第三方平台数据
        if(totalTotalCount == platNum) {
            int getRedayCount = thirdPlatApiMapper.countGetReady(params);
            if(getRedayCount >= 0) {
                thirdPlatApiMapper.insertGetReady(params);
            }
        }
    }
}

