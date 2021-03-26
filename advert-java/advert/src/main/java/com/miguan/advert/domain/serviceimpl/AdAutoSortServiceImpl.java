package com.miguan.advert.domain.serviceimpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cgcg.context.util.StringUtils;
import com.miguan.advert.common.util.redis.RedisService;
import com.miguan.advert.domain.mapper.AdAutoSortMapper;
import com.miguan.advert.domain.service.AdAutoSortService;
import com.miguan.advert.domain.service.ReportService;
import com.miguan.advert.domain.vo.interactive.AdCpmVo;
import com.miguan.advert.domain.vo.interactive.AdTestCodeSortVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tool.util.DateUtil;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 代码位自动排序serviceImpl
 * @Author zhangbinglin
 * @Date 2020/11/12 11:04
 **/
@Slf4j
@Service
public class AdAutoSortServiceImpl implements AdAutoSortService {

    @Resource
    private ReportService reportService;
    @Resource
    private AdAutoSortMapper adAutoSortMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private DspReportServiceImpl dspReportService;

    @Value("${bigdata-server.show-threshold}")
    private String showThresholdUrl;

    /**
     * 代码位自动排序
     */
    public void adAutoSort() {
        String now = DateUtil.dateStr2(new Date()); //今天
        String yesterday = DateUtil.dateStr2(DateUtil.rollDay(new Date(), -1));  //昨天
        if (!reportService.isAllPlatFormReady(yesterday)) {
            //查询昨天的穿山甲，广点通，快手的cpm数据是否已经全部导入完毕，全部导入完毕才开始自动排序
            log.info("广点通，快手的cpm数据还未导入完毕，暂不进行代码位自动排序");
            return;
        }

        List<AdCpmVo> adCpmList = reportService.listAdCpmList(yesterday);  //查询代码位的cpm
        List<AdCpmVo> ad98CpmList = dspReportService.listAd98CpmList(yesterday);  //查询98代码位的cpm
        if(ad98CpmList != null) {
            adCpmList.addAll(ad98CpmList);
        }
        Map<String, Double> adCpmMap = adCpmList.stream().collect(Collectors.toMap(AdCpmVo::getAdId, AdCpmVo::getCpm));

        List<AdTestCodeSortVo> adAutoSortList = adAutoSortMapper.listAdAutoSort();  //查询出需要自动排序的代码位列表
        //给adAutoSortList的cpm赋值
        for (AdTestCodeSortVo adSortVo : adAutoSortList) {
            log.info("需要自动排序的config_id:{}", adSortVo.getConfigId());
            Double cpm = adCpmMap.get(adSortVo.getAdId()) == null ? 0D : adCpmMap.get(adSortVo.getAdId());
            adSortVo.setCpm(cpm);
        }

        Map<String, List<AdTestCodeSortVo>> adAutoSortMap = adAutoSortList.stream().collect(Collectors.groupingBy(AdTestCodeSortVo::getConfigId));
        adAutoSortList.clear();
        for (List<AdTestCodeSortVo> adSortList : adAutoSortMap.values()) {
            //根据cpm降序
            adSortList = adSortList.stream().sorted(Comparator.comparing(AdTestCodeSortVo::getCpm)).collect(Collectors.toList());
            //给每个默认分组的代码位设置排序字段值
            for (int i = 0; i < adSortList.size(); i++) {
                adSortList.get(i).setOrderNum(i + 1);
            }
            adAutoSortList.addAll(adSortList);
        }
        //批量更新默认分组中代码位的顺序
        if(adAutoSortList != null && !adAutoSortList.isEmpty()) {
            adAutoSortMapper.batchUpdateOrderNum(adAutoSortList);
            redisService.set("adAutoSortList" + now, JSON.toJSON(adAutoSortList), 3 * 24 * 60 * 60);  //把今天的排序结果存入redis中，供第二天实时更新排序使用
        }

        //查询出展现量小于阀值的代码位,并且保存到redid缓存中
        String ltThresholdAdIds = getListAdIdShowThreshold(1, yesterday, null);
        //把展现量小于阀值的代码位存入到redis中（缓存在凌晨过期）
        redisService.set("ltThresholdAdIds", ltThresholdAdIds, this.getTodayEndSecond());

        //设置自动排序tag（只有当天自动排序跑完后，才需要实时监展现量没有超过阀值的代码位）
        redisService.set("todayAutoSortTag", "1", this.getTodayEndSecond());
    }


    /**
     * 从大数据中获取代码位的展示量
     * @param type 类型，1:未达到阀值，2：已达到阀值"
     * @param dd 日期
     * @param ltAdIds 代码位，多个逗号分隔（type为2的时候使用）
     * @return
     */
    private String getListAdIdShowThreshold(Integer type, String dd, String ltAdIds) {
        ltAdIds = (ltAdIds == null ? "" : ltAdIds);
        String showThreshold = redisService.getValue("show_threshold");  //展现量阀值
        String params = "?type={0}&dd={1}&showThreshold={2}&adIds={3}";
        params = MessageFormat.format(params, type, dd, showThreshold, ltAdIds);
        String url = showThresholdUrl + params;
        String resultJson = restTemplate.postForObject(url, null, String.class);  //获取小于阀值的代码位
        String result = JSONObject.parseObject(resultJson).getString("data");
        return result.replace("[", "").replace("]", "").replace("\"", "");
    }

    /**
     * 获取当前时间 至 当天23:59:59 的秒数
     * @return
     */
    private int getTodayEndSecond() {
        String today = DateUtil.dateStr2(new Date());  //当天
        today = today + " 23:59:59";
        Date todayEnd = DateUtil.valueOf(today, "yyyy-MM-dd HH:mm:ss");
        Long second = (todayEnd.getTime() - System.currentTimeMillis()) / 1000;
        return second.intValue();
    }

    /**
     * 实时监测未满足阈值的代码位id，在下次重新排序前展现量是否达到了阈值。如果达到了阀值，将该代码位id回退至昨日排名，否则该代码位id的排序保持不变
     *
     */
    public void updateSortWhenGtThreshold() {
        String dd = DateUtil.dateStr2(new Date());  //今天
        String yesterday = DateUtil.dateStr2(DateUtil.rollDay(new Date(), -1));  //昨天
        String ltAdIds = redisService.get("ltThresholdAdIds");  //获取展现量小于阀值的代码位
        ltAdIds = (ltAdIds == null ? "" : ltAdIds);
        List<String> ltAdIdList = new ArrayList<>(Arrays.asList(ltAdIds.split(",")));

        String adIdShow = getListAdIdShowThreshold(2, dd, ltAdIds);  //获取超过展示量阀值的代码位
        String yesAdAutoSort = redisService.get("adAutoSortList" + yesterday);  //获取昨天的排序
        String todayAdAutoSort = redisService.get("adAutoSortList" + dd);  //获取今天的排序

        if(StringUtils.isBlank(adIdShow) || StringUtils.isBlank(yesAdAutoSort) || StringUtils.isBlank(todayAdAutoSort)) {
            log.info("实时监测排序必要参数为空");
            return;
        }
        List<AdTestCodeSortVo> yesAdAutoSortList = JSON.parseArray(yesAdAutoSort, AdTestCodeSortVo.class);
        List<AdTestCodeSortVo> todayAdAutoSortList = JSON.parseArray(todayAdAutoSort, AdTestCodeSortVo.class);
        List<String> list = Arrays.asList(adIdShow.split(","));
        for(String adId : list) {
            for(AdTestCodeSortVo vo : yesAdAutoSortList) {
                if(adId.equals(vo.getAdId())) {
                    //命中昨天的代码位
                    String configId = vo.getConfigId();  //默认分组配置id
                    Integer yesOrderNum = vo.getOrderNum();  //昨天的排序位置
                    Integer todayOrderNum = 0;  //今天的排序
                    //更新当天的最新排序,以及获取当前的排序
                    for(AdTestCodeSortVo voToday : todayAdAutoSortList) {
                        if(voToday.getConfigId().equals(configId) && voToday.getAdId().equals(adId)) {
                            todayOrderNum = voToday.getOrderNum();
                            voToday.setOrderNum(yesOrderNum);
                            log.info("达到阀值，把排序改成昨天的排序,vo：{},yesOrderNum:{}", voToday, yesOrderNum);
                        }
                    }

                    Map<String, Object> params = new HashMap<>();
                    params.put("yesOrderNum", yesOrderNum);
                    params.put("todayOrderNum", todayOrderNum);
                    params.put("configId", configId);
                    params.put("adId", adId);
                    if(adAutoSortMapper.ifExistAdId(params) == 0) {
                        //代码位在已经不再此配置中
                        continue;
                    }
                    adAutoSortMapper.updateOtherOrderNum(params);  //修改同一个而配置下的其他代码位的排序
                    adAutoSortMapper.updateOrderNum(params);  //修改代码位的排序

                    ltAdIdList.remove(adId); //把超过阀值的代码位从ltAdIdList中移除
                }
            }
        }
        //更新redis缓存
        redisService.set("ltThresholdAdIds", String.join(",", ltAdIdList), this.getTodayEndSecond());
        redisService.set("adAutoSortList" + dd, JSON.toJSONString(todayAdAutoSortList), this.getTodayEndSecond());
    }

    public static void main(String[] args) {
        AdAutoSortServiceImpl s = new AdAutoSortServiceImpl();
        System.out.println(s.getTodayEndSecond());
    }
}
