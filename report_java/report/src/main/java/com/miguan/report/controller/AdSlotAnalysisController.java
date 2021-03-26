package com.miguan.report.controller;

import com.google.common.collect.Maps;
import com.miguan.report.common.util.DateUtil;
import com.miguan.report.dto.AdStaDetailResDto;
import com.miguan.report.dto.ResponseDto;
import com.miguan.report.service.report.AdSlotAnalysisService;
import com.miguan.report.vo.AdSlotAnlyDetailVo;
import com.miguan.report.vo.AdSlotAnlyExtVo;
import com.miguan.report.vo.AdSlotAnlyVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.miguan.report.common.constant.CommonConstant.LINE_SEQ;
import static com.miguan.report.common.enums.AppEnum.GUO_GUO;
import static com.miguan.report.common.enums.AppEnum.LAI_DIAN;
import static com.miguan.report.common.enums.AppEnum.XI_YOU;
import static com.miguan.report.common.util.AppNameUtil.getAppIdForName;
import static com.miguan.report.common.util.NumCalculationUtil.calMomOrYoyDouble;
import static com.miguan.report.common.util.NumCalculationUtil.roundHalfUpDouble;

/**广告位分析页面
 * @author zhongli
 * @date 2020-06-19 
 *
 */
public abstract class AdSlotAnalysisController extends BaseController {
    abstract int appType();

    @Resource
    private AdSlotAnalysisService adSlotAnalysisService;

    @ApiOperation(value = "广告位环比统计，注：按{\"茜柚视频\", \"果果视频\"}和广告位分组统计")
    @PostMapping(value = "/data/load/sta")
    @ApiResponse(code = 200, message = "格式：{\"果果视频\": [], \"茜柚视频\": []}", response = AdSlotAnlyExtVo.class, responseContainer = "Map")
    public ResponseDto<Map<Integer, Map<String, Object>>> initload1(@ApiParam(value = "统计日期,格式：yyyy-MM-dd", required = true) String date,
                                                                    @ApiParam(value = "数据类型：0=汇总 1=广点通 2=穿山甲 3=快手", required = true) Integer dataType) {

        String momDay = DateUtil.yedyyyy_MM_dd(date);
        List<AdSlotAnlyVo> vos = adSlotAnalysisService.loadByAppNameAndAdspace(date, dataType, this.appType());
        List<AdSlotAnlyVo> momVos = adSlotAnalysisService.loadByAppNameAndAdspace(momDay, dataType, this.appType());
        List<AdSlotAnlyExtVo> mdata = mergeVos(vos, momVos);

        List<Map<String, String>> head = new ArrayList<>();
        head.add(headKeyNames("广告位", "adSpace))"));
        head.add(headKeyNames("收益", "revenue"));
        head.add(headKeyNames("日环比", "momRevenue"));
        head.add(headKeyNames("日活均价值", "activeValue"));
        head.add(headKeyNames("日环比", "momActiveValue"));
        head.add(headKeyNames("CPM", "cpm"));
        head.add(headKeyNames("日环比", "momCpm"));
        head.add(headKeyNames("人均展现", "preShowNum"));
        head.add(headKeyNames("日环比", "momPreShowNum"));

        /**数据格式
         * {
         *     "应用名称":
         * }
         */
        Map<Integer, Map<String, Object>> rdata = new HashMap<>();
        if (mdata == null) {
            Map<String, Object> ldata = new HashMap<>();
            ldata.put("head", head);
            ldata.put("data", new ArrayList<>());
            if (appType() == 1) {
                rdata.put(GUO_GUO.getId(), ldata);
                rdata.put(XI_YOU.getId(), ldata);
            } else {
                rdata.put(LAI_DIAN.getId(), ldata);
            }
            return success(rdata);
        }

        //key app名称
        Map<String, List<AdSlotAnlyExtVo>> gdata = mdata.stream().collect(Collectors.groupingBy(AdSlotAnlyExtVo::getAppName));

        gdata.entrySet().forEach(k -> {
            Map<String, Object> ldata = new HashMap<>();
            ldata.put("head", head);
            ldata.put("data", k.getValue());
            rdata.put(getAppIdForName(k.getKey()), ldata);

        });
        //补充数据。其中一个没有数据则不进行展示
        if (appType() == 1 && rdata.size() == 1) {
            Map<String, Object> ldata = new HashMap<>();
            ldata.put("head", head);
            ldata.put("data", new ArrayList<>());
            rdata.put(rdata.containsKey(XI_YOU.getId()) ? GUO_GUO.getId() : XI_YOU.getId(), ldata);
        }
        return success(rdata);
    }

    private Map<String, String> headKeyNames(String name, String key) {
        Map<String, String> head1 = Maps.newHashMapWithExpectedSize(2);
        head1.put("name", name);
        head1.put("key", key);
        return head1;
    }

    private List<AdSlotAnlyExtVo> mergeVos(List<AdSlotAnlyVo> data1, List<AdSlotAnlyVo> data2) {
        if (data1 == null && data2 == null) {
            return null;
        }
        List<AdSlotAnlyVo> newVos = new ArrayList<>();
        List<AdSlotAnlyExtVo> resVos = new ArrayList<>();
        if (data2 != null && data1 != null) {
            //查询前一天的广告类跟今天对比，如果今天有不包含昨天的类要进行合并
            data2.forEach(v -> {
                //查询当天是否有此类
                Optional<AdSlotAnlyVo> op = data1.stream().filter(v1 -> v1.getAdSpace().equals(v.getAdSpace()) && v1.getAppName().equals(v.getAppName())).findFirst();
                if (!op.isPresent()) {
                    //如果前一天有此类，当天没有此类则要加入此类进行展示
                    newVos.add(new AdSlotAnlyVo(v.getDeviceType(), v.getAdSpace(), v.getAppName()));
                }
            });
        }
        if (data1 == null) {
            //进行环比计算 当天没数据，前一天有数据
            data2.forEach(v -> {
                resVos.add(new AdSlotAnlyExtVo(v.getAdSpace(), v.getAppName(),
                        0,
                        0,
                        0,
                        0,
                        calMomOrYoyDouble(0, v.getRevenue()),
                        calMomOrYoyDouble(0, v.getActiveValue()),
                        calMomOrYoyDouble(0, v.getCpm()),
                        calMomOrYoyDouble(0, v.getPreShowNum())));
            });
            return resVos;
        }
        newVos.addAll(data1);
        //进行环比计算
        newVos.forEach(v -> {
            //查询前一天是否有此类
            Optional<AdSlotAnlyVo> op = data2 == null ? Optional.empty() :
                    data2.stream().filter(v2 -> v2.getAdSpace().equals(v.getAdSpace()) && v2.getAppName().equals(v.getAppName())).findFirst();
            if (!op.isPresent()) {
                //前一天没有此类，则环比100%
                double ohPercent = 1;
                resVos.add(new AdSlotAnlyExtVo(v.getAdSpace(), v.getAppName(),
                        roundHalfUpDouble(v.getRevenue()),
                        roundHalfUpDouble(4, v.getActiveValue()),
                        roundHalfUpDouble(v.getCpm()),
                        roundHalfUpDouble(v.getPreShowNum()),
                        ohPercent, ohPercent, ohPercent, ohPercent));
            } else {
                AdSlotAnlyVo v2 = op.get();
                resVos.add(new AdSlotAnlyExtVo(v.getAdSpace(), v.getAppName(),
                        roundHalfUpDouble(v.getRevenue()),
                        roundHalfUpDouble(4, v.getActiveValue()),
                        roundHalfUpDouble(v.getCpm()),
                        roundHalfUpDouble(v.getPreShowNum()),
                        calMomOrYoyDouble(v.getRevenue(), v2.getRevenue()),
                        calMomOrYoyDouble(v.getActiveValue(), v2.getActiveValue()),
                        calMomOrYoyDouble(v.getCpm(), v2.getCpm()),
                        calMomOrYoyDouble(v.getPreShowNum(), v2.getPreShowNum())));
            }
        });
        return resVos;
    }

    @ApiOperation(value = "广告位环比统计点击详情(拆线图)")
    @PostMapping(value = "/data/load/stadetail")
    @ApiResponse(code = 200, message = "", response = AdSlotAnlyExtVo.class, responseContainer = "List")
    public ResponseDto<List<AdStaDetailResDto>> initload1(
            @ApiParam(value = "广告位", required = true) String adSpace,
            @ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
            @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate,
            @ApiParam(value = "应用类型 字符串用,隔开 应用类型格式：应用ID_设备类型ID；注：设备类型ID 1=安卓2=苹果;可取下拉接口appWithDeviceList属性值", required = true) String apps,
            @ApiParam(value = "数据类型：0=汇总 1=广点通 2=穿山甲 3=快手", required = true) Integer palatType,
            @ApiParam(value = "数据类型：0=CPM 1=人均展现 2=日活均价值 3=用户行为数据；多个用,隔开", required = true) String dataType) {
        List<String> appList = Arrays.asList(apps.split(","));

        List<AdSlotAnlyDetailVo> data = adSlotAnalysisService.loadAdSlotDetail(adSpace, startDate, endDate, appList, palatType, this.appType());
        if (data == null) {
            return successForNotData();
        }

        //未在数据库进行过滤，放到这里进行过滤。
        List<Integer> list = Stream.of(dataType.split(",")).map(Integer::valueOf).collect(Collectors.toList());
        List<AdStaDetailResDto> dodata = data.stream().flatMap(v -> {
            String appName = v.getAppName();
            List<AdStaDetailResDto> listC = new ArrayList<>();
            if (list.contains(0)) {
                AdStaDetailResDto cpm = new AdStaDetailResDto(v.getDate(), StringUtils.joinWith(LINE_SEQ, appName, v.getAdSpace(), "CPM"), roundHalfUpDouble(v.getCpm()));
                listC.add(cpm);
            }
            if (list.contains(1)) {
                AdStaDetailResDto preShowNum = new AdStaDetailResDto(v.getDate(), StringUtils.joinWith(LINE_SEQ, appName, v.getAdSpace(), "人均展现"), roundHalfUpDouble(v.getPreShowNum()));
                listC.add(preShowNum);
            }
            if (list.contains(2)) {
                AdStaDetailResDto activeValue = new AdStaDetailResDto(v.getDate(), StringUtils.joinWith(LINE_SEQ, appName, v.getAdSpace(), "日活均价值"), roundHalfUpDouble(4, v.getActiveValue()));
                listC.add(activeValue);
            }
            return listC.isEmpty() ? null : listC.stream();
        }).collect(Collectors.toList());
        if (list.contains(3)) {
            //用户行为数据
            List<AdSlotAnlyDetailVo> ubData = adSlotAnalysisService.loadAdUserBehaviorDetail(adSpace, startDate, endDate, appList, this.appType());
            if (!CollectionUtils.isEmpty(ubData)) {
                List<AdStaDetailResDto> ulist = ubData.stream().map(e -> new AdStaDetailResDto(e.getDate(),
                        StringUtils.joinWith(LINE_SEQ, e.getAppName(), e.getAdSpace(), "用户行为数据"),
                        roundHalfUpDouble(e.getDataValue()))).collect(Collectors.toList());
                dodata.addAll(ulist);
            }

        }
        return success(dodata);
    }
}
