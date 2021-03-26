package com.miguan.reportview.controller;

import com.google.common.collect.Lists;
import com.miguan.reportview.common.exception.NullParameterException;
import com.miguan.reportview.common.exception.ResultCheckException;
import com.miguan.reportview.common.utils.DateUtil;
import com.miguan.reportview.controller.base.BaseController;
import com.miguan.reportview.dto.DisassemblyChartDto;
import com.miguan.reportview.dto.LdRealTimeCheckereDto;
import com.miguan.reportview.dto.RealTimeCheckereDto;
import com.miguan.reportview.dto.ResponseEntity;
import com.miguan.reportview.service.IRealTimeStatisticsService;
import com.miguan.reportview.vo.LdRealTimeStaVo;
import com.miguan.reportview.vo.RealTimeStaVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.miguan.reportview.common.utils.NumCalculationUtil.calMomOrYoyDouble;
import static com.miguan.reportview.common.utils.NumCalculationUtil.roundHalfUpDouble;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author zhongli
 * @date 2020-07-30 
 *
 */
@Api(value = "数据概览-实时统计", tags = {"数据概览"})
@RestController
public class RealTimeStatisticsController extends BaseController {

    @Autowired
    private IRealTimeStatisticsService realTimeStatisticsService;

    @ApiOperation(value = "实时统计小方格")
    @PostMapping("api/realtime/sta/get/checkereddata")
    public ResponseEntity<RealTimeCheckereDto> getCheckereddata(@ApiParam(value = "显示类型 多个用,隔开; 1=新增用户2=活跃用户3=播放用户4=有效播放用户5=广告展现用户6=广告点击用户7=视频播放数8=有效播放数9=广告展现10=完播用户11=人均有效播放数12=人均播放时长13=广告有效点击量14=广告点击率15=人均广告展示16=人均广告点击17=每曝光播放时长18=每播放播放时长", required = true)
                                                                        String showType) {
        if (StringUtils.isBlank(showType)) {
            throw new NullParameterException();
        }
        LocalDateTime now = LocalDateTime.now().minusHours(1);
        String today = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String yesterday = now.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        ArrayList<String> dates = Lists.newArrayList(today, yesterday);
        Map<String, List<RealTimeStaVo>> map = realTimeStatisticsService.getCheckereddata(dates, now, seqArray2List(showType));
        if (isEmpty(map)) {
            return fail(() -> "未找到任何数据");
        }
        List<RealTimeStaVo> list1 = map.get(today);
        if (isEmpty(list1)) {
            throw new ResultCheckException(() -> String.format("今环比没有记录"));
        }
        if (list1.size() != 1) {
            throw new ResultCheckException(() -> String.format("今环比数据记录数应该是 1，实际 %d", list1.size()));
        }
        List<RealTimeStaVo> real = map.get("real");
        if (isEmpty(real)) {
            throw new ResultCheckException(() -> String.format("今日没有记录"));
        }
        RealTimeStaVo realVo = real.get(0);
        RealTimeStaVo vo1 = list1.get(0);

        realVo.setPerPlayTime(realVo.getPerPlayTime() / 60000);

        RealTimeCheckereDto dto = new RealTimeCheckereDto();
        dto.setNewUser(roundHalfUpDouble(0, realVo.getNewUser()));
        dto.setUser(roundHalfUpDouble(0, realVo.getSuser()));
        dto.setPlayUser(roundHalfUpDouble(0, realVo.getPlayUser()));
        dto.setValidPlayUser(roundHalfUpDouble(0, realVo.getValidPlayUser()));
        dto.setAdShowUser(roundHalfUpDouble(0, realVo.getAdShowUser()));
        dto.setAdClickUser(roundHalfUpDouble(0, realVo.getAdClickUser()));
        dto.setPlayCount(roundHalfUpDouble(0, realVo.getPlayCount()));
        dto.setValidPlayCount(roundHalfUpDouble(0, realVo.getValidPlayCount()));
        dto.setAdShowCount(roundHalfUpDouble(0, realVo.getAdShowCount()));
        dto.setVperPlayCount(roundHalfUpDouble(4, realVo.getVperPlayCount()));
        dto.setEndPlayUser(roundHalfUpDouble(0, realVo.getEndPlayUser()));
        dto.setPerPlayTime(roundHalfUpDouble(4, realVo.getPerPlayTime()));
        dto.setAdClickCount(roundHalfUpDouble(0, realVo.getAdClickCount()));
        dto.setAdClickRate(roundHalfUpDouble(4, realVo.getAdClickRate()));
        dto.setPerAdShowCount(roundHalfUpDouble(4, realVo.getPerAdShowCount()));
        dto.setPerAdclickCount(roundHalfUpDouble(4, realVo.getPerAdclickCount()));
        dto.setPreShowTime(roundHalfUpDouble(2, realVo.getPreShowTime()));
        dto.setPrePlayTime(roundHalfUpDouble(2, realVo.getPrePlayTime()));
        List<RealTimeStaVo> list2 = map.get(yesterday);
        if (list2 != null && list2.size() != 1) {
            throw new ResultCheckException(() -> String.format("昨环比数据记录数应该是 1，实际 %d", list2.size()));
        }
        RealTimeStaVo vo2 = list2 == null ? new RealTimeStaVo() : list2.get(0);
        dto.setMomnewUser(calMomOrYoyDouble(vo1.getNewUser(), vo2.getNewUser()));
        dto.setMomuser(calMomOrYoyDouble(vo1.getSuser(), vo2.getSuser()));
        dto.setMomplayUser(calMomOrYoyDouble(vo1.getPlayUser(), vo2.getPlayUser()));
        dto.setMomvalidPlayUser(calMomOrYoyDouble(vo1.getValidPlayUser(), vo2.getValidPlayUser()));
        dto.setMomadShowUser(calMomOrYoyDouble(vo1.getAdShowUser(), vo2.getAdShowUser()));
        dto.setMomadClickUser(calMomOrYoyDouble(vo1.getAdClickUser(), vo2.getAdClickUser()));
        dto.setMomplayCount(calMomOrYoyDouble(vo1.getPlayCount(), vo2.getPlayCount()));
        dto.setMomvalidPlayCount(calMomOrYoyDouble(vo1.getValidPlayCount(), vo2.getValidPlayCount()));
        dto.setMomadShowCount(calMomOrYoyDouble(vo1.getAdShowCount(), vo2.getAdShowCount()));

        dto.setMomendPlayUser(calMomOrYoyDouble(vo1.getEndPlayUser(), vo2.getEndPlayUser()));
        dto.setMomvperPlayCount(calMomOrYoyDouble(vo1.getVperPlayCount(), vo2.getVperPlayCount()));
        dto.setMomperPlayTime(calMomOrYoyDouble(vo1.getPerPlayTime(), vo2.getPerPlayTime()));
        dto.setMomadClickCount(calMomOrYoyDouble(vo1.getAdClickCount(), vo2.getAdClickCount()));
        dto.setMomadClickRate(calMomOrYoyDouble(vo1.getAdClickRate(), vo2.getAdClickRate()));
        dto.setMomperAdShowCount(calMomOrYoyDouble(vo1.getPerAdShowCount(), vo2.getPerAdShowCount()));
        dto.setMomperAdclickCount(calMomOrYoyDouble(vo1.getPerAdclickCount(), vo2.getPerAdclickCount()));
        dto.setMonpreShowTime(calMomOrYoyDouble(vo1.getPreShowTime(), vo2.getPreShowTime()));  //每曝光播放时长=播放总时长(分)/曝光总数
        dto.setMonprePlayTime(calMomOrYoyDouble(vo1.getPrePlayTime(), vo2.getPrePlayTime()));  //每播放播放时长=播放总时长(分)/播放视频数
        //按小时分组
        return success(dto);
    }

    List<String> hour = Lists.newArrayList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");

    @ApiOperation(value = "实时统计拆线图(按小时)")
    @PostMapping("api/realtime/sta/getdata")
    public ResponseEntity<List<DisassemblyChartDto>> getData(@ApiParam(value = "显示类型 多个用,隔开; 1=新增用户2=活跃用户3=播放用户4=有效播放用户5=广告展现用户6=广告点击用户7=视频播放数8=有效播放数9=广告展现10=完播用户11=人均有效播放数12=人均播放时长13=广告有效点击量14=广告点击率15=人均广告展示16=人均广告点击17=每曝光播放时长18=每播放播放时长", required = true)
                                                                     String showType,
                                                             @ApiParam(value = "显示日期 多个用,隔开,今日 放第1个，昨日放第2个; 日期格式: yyyy-MM-dd", required = true)
                                                                     String showDate) {
        if (StringUtils.isAnyBlank(showDate, showType)) {
            throw new NullParameterException();
        }
        List<String> dates = seqArray2List(showDate);
        List<RealTimeStaVo> list = realTimeStatisticsService.getData(dates, seqArray2List(showType));
        if (isEmpty(list)) {
            return fail(() -> "未找到任何数据");
        }

        Map<String, List<RealTimeStaVo>> map = list.stream().collect(Collectors.groupingBy(RealTimeStaVo::getDd));
        list = map.entrySet().stream().flatMap(entry -> {
            List<RealTimeStaVo> listv = entry.getValue();
            hour.stream().forEach(e -> {
                Optional<RealTimeStaVo> op = listv.stream().filter(d -> d.getDh().equals(e)).findFirst();
                if (!op.isPresent()) {
                    RealTimeStaVo addvo = new RealTimeStaVo();
                    addvo.setDh(e);
                    addvo.setDd(entry.getKey());
                    addvo.setShowValue(Double.valueOf(0));
                    listv.add(addvo);
                }
            });
            return listv.stream();
        }).collect(Collectors.toList());
        boolean isRate = showType.contains("14");
        List<DisassemblyChartDto> rdata = list.stream().map(e -> {
            String type = DateUtil.yyyy_MM_dd().equals(e.getDd()) ? "今日" : DateUtil.yedyyyy_MM_dd().equals(e.getDd()) ? "昨日" : e.getDd().concat(" ");
            DisassemblyChartDto dto = new DisassemblyChartDto();
            dto.setDate(e.getDh());
            dto.setType(type);
            dto.setFormart(isRate ? "%" : "");
            dto.setValue(e.getShowValue() == null ? 0 : e.getShowValue().doubleValue());
            return dto;
        }).collect(Collectors.toList());
        rdata.sort(Comparator.comparing(DisassemblyChartDto::getDate));
        //按小时分组
        rdata = this.top10(rdata);
        return success(rdata);
    }

    @ApiOperation(value = "实时统计拆线图(按分钟)")
    @PostMapping("api/realtime/sta/getminuteData")
    public List<DisassemblyChartDto> getMinuteData(@ApiParam(value = "显示类型 多个用,隔开; 1=新增用户2=活跃用户3=播放用户4=有效播放用户5=广告展现用户6=广告点击用户7=视频播放数8=有效播放数9=广告展现10=完播用户11=人均有效播放数12=人均播放时长13=广告有效点击量14=广告点击率15=人均广告展示16=人均广告点击17=每曝光播放时长18=每播放播放时长", required = true)
                                                                           String showType,
                                                                   @ApiParam(value = "显示日期 多个用,隔开,今日 放第1个，昨日放第2个; 日期格式: yyyy-MM-dd", required = true)
                                                                           String showDate) {
        if (StringUtils.isAnyBlank(showDate, showType)) {
            throw new NullParameterException();
        }
        List<String> dates = seqArray2List(showDate);
        List<DisassemblyChartDto> list = realTimeStatisticsService.getMinuteData(dates, seqArray2List(showType));
        return list;
    }

    @ApiOperation(value = "来电实时统计小方格")
    @PostMapping("api/realtime/sta/get/ldcheckereddata")
    public ResponseEntity<LdRealTimeCheckereDto> getLdCheckereddata() {
        LocalDateTime now = LocalDateTime.now().minusHours(1);
        String today = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String yesterday = now.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        ArrayList<String> dates = Lists.newArrayList(today, yesterday);
        Map<String, List<LdRealTimeStaVo>> map = realTimeStatisticsService.getLdCheckereddata(dates, now);

        if (isEmpty(map)) {
            return fail(() -> "未找到任何数据");
        }
        List<LdRealTimeStaVo> list1 = map.get(today);
        if (isEmpty(list1)) {
            throw new ResultCheckException(() -> String.format("今环比没有记录"));
        }
        if (list1.size() != 1) {
            throw new ResultCheckException(() -> String.format("今环比数据记录数应该是 1，实际 %d", list1.size()));
        }
        List<LdRealTimeStaVo> real = map.get("real");
        if (isEmpty(real)) {
            throw new ResultCheckException(() -> String.format("今日没有记录"));
        }
        LdRealTimeStaVo realVo = real.get(0);
        LdRealTimeStaVo vo1 = list1.get(0);


        LdRealTimeCheckereDto dto = new LdRealTimeCheckereDto();
        BeanUtils.copyProperties(realVo, dto);
        dto.setPresetConfirmCount(realVo.getPreSetConfirmCount());

        List<LdRealTimeStaVo> list2 = map.get(yesterday);
        if (list2 != null && list2.size() != 1) {
            throw new ResultCheckException(() -> String.format("昨环比数据记录数应该是 1，实际 %d", list2.size()));
        }
        LdRealTimeStaVo vo2 = list2 == null ? new LdRealTimeStaVo() : list2.get(0);
        dto.setMonNewUser(calMomOrYoyDouble(vo1.getNewUser(), vo2.getNewUser()));
        dto.setMonUser(calMomOrYoyDouble(vo1.getUser(), vo2.getUser()));
        dto.setMonDetailPlayUser(calMomOrYoyDouble(vo1.getDetailPlayUser(), vo2.getDetailPlayUser()));
        dto.setMonPrePlayCount(calMomOrYoyDouble(vo1.getPrePlayCount(), vo2.getPrePlayCount()));
        dto.setMonPreSetCount(calMomOrYoyDouble(vo1.getAdShowUser(), vo2.getAdShowUser()));
        dto.setMonPresetConfirmCount(calMomOrYoyDouble(vo1.getPreSetConfirmCount(), vo2.getPreSetConfirmCount()));
        dto.setMonAppStart(calMomOrYoyDouble(vo1.getAppStart(), vo2.getAppStart()));
        dto.setMonAdShow(calMomOrYoyDouble(vo1.getAdShow(), vo2.getAdShow()));
        dto.setMonAdClick(calMomOrYoyDouble(vo1.getAdClick(), vo2.getAdClick()));
        dto.setMonSetUser(calMomOrYoyDouble(vo1.getSetUser(), vo2.getSetUser()));  //环比设置用户数
        dto.setMonSetConfirmUser(calMomOrYoyDouble(vo1.getSetConfirmUser(), vo2.getSetConfirmUser()));  //环比成功设置用户数
        dto.setMonPreAdClick(calMomOrYoyDouble(vo1.getPreAdClick(), vo2.getPreAdClick()));  //人均广告点击

        dto.setMonAdShowUser(calMomOrYoyDouble(vo1.getAdShowUser(), vo2.getAdShowUser()));
        dto.setMonAdClickUser(calMomOrYoyDouble(vo1.getAdClickUser(), vo2.getAdClickUser()));
        dto.setMonUserRetention(calMomOrYoyDouble(vo1.getUserRetention(), vo2.getUserRetention()));
        dto.setMonNewUserRetention(calMomOrYoyDouble(vo1.getNewUserRetention(), vo2.getNewUserRetention()));
        dto.setMonPreAdShow(calMomOrYoyDouble(vo1.getPreAdShow(), vo2.getPreAdShow()));
        dto.setMonAdClickRate(calMomOrYoyDouble(vo1.getAdClickRate(), vo2.getAdClickRate()));
        dto.setMonAdClickShowRate(calMomOrYoyDouble(vo1.getAdClickShowRate(), vo2.getAdClickShowRate()));
        dto.setMonPreAppStart(calMomOrYoyDouble(vo1.getPreAppStart(), vo2.getPreAppStart()));
        //按小时分组
        return success(dto);
    }

    @ApiOperation(value = "来电实时统计拆线图(按小时)")
    @PostMapping("api/realtime/sta/getlddata")
    public ResponseEntity<List<DisassemblyChartDto>> getLdData(@ApiParam(value = "显示类型 多个用,隔开; 1=新增用户,2=活跃用户,3=详情页播放用户,4=设置用户,5=成功设置用户,6=人均观看次数,7=人均设置次数,8=人均成功设置次数,9=广告展现用户,10=广告点击用户,11=广告展现量,12=广告点击量,13=人均广告展现,14=人均广告点击,15=广告点击率,16=广告点击用户占比,17=人均APP启动次数", required = true)
                                                                     String showType,
                                                               @ApiParam(value = "显示日期 多个用,隔开,今日 放第1个，昨日放第2个; 日期格式: yyyy-MM-dd", required = true)
                                                                      String showDate) {
        if (StringUtils.isAnyBlank(showDate, showType)) {
            throw new NullParameterException();
        }
        List<String> dates = seqArray2List(showDate);
        List<LdRealTimeStaVo> list = realTimeStatisticsService.getLdData(dates, seqArray2List(showType));
        if (isEmpty(list)) {
            return fail(() -> "未找到任何数据");
        }

        boolean isRate = showType.contains("15") || showType.contains("16");
        List<DisassemblyChartDto> rdata = list.stream().map(e -> {
            String type = DateUtil.yyyy_MM_dd().equals(e.getDd()) ? "今日" : DateUtil.yedyyyy_MM_dd().equals(e.getDd()) ? "昨日" : e.getDd().concat(" ");
            DisassemblyChartDto dto = new DisassemblyChartDto();
            dto.setDate(e.getDh());
            dto.setType(type);
            dto.setFormart(isRate ? "%" : "");
            dto.setValue(e.getShowValue() == null ? 0 : e.getShowValue().doubleValue());
            return dto;
        }).collect(Collectors.toList());
        rdata.sort(Comparator.comparing(DisassemblyChartDto::getDate));
        //按小时分组
//        rdata = this.top10(rdata);
        return success(rdata);
    }

    @ApiOperation(value = "来电实时统计拆线图(按分钟)")
    @PostMapping("api/realtime/sta/getldminuteData")
    public List<DisassemblyChartDto> getLdMinuteData(@ApiParam(value = "显示类型 多个用,隔开; 1=新增用户,2=活跃用户,3=详情页播放用户,4=设置用户,5=成功设置用户,6=人均观看次数,7=人均设置次数,8=人均成功设置次数,9=广告展现用户,10=广告点击用户,11=广告展现量,12=广告点击量,13=人均广告展现,14=人均广告点击,15=广告点击率,16=广告点击用户占比,17=人均APP启动次数", required = true)
                                                             String showType,
                                                     @ApiParam(value = "显示日期 多个用,隔开,今日 放第1个，昨日放第2个; 日期格式: yyyy-MM-dd", required = true)
                                                             String showDate) {
        if (StringUtils.isAnyBlank(showDate, showType)) {
            throw new NullParameterException();
        }
        List<String> dates = seqArray2List(showDate);
        List<DisassemblyChartDto> list = realTimeStatisticsService.getLdMinuteData(dates, seqArray2List(showType));
        return list;
    }
}
