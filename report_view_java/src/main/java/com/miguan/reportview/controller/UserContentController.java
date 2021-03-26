package com.miguan.reportview.controller;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.miguan.reportview.common.enmus.VideosSourceEnmu;
import com.miguan.reportview.common.exception.NullParameterException;
import com.miguan.reportview.common.exception.ResultCheckException;
import com.miguan.reportview.common.utils.AmpFiled;
import com.miguan.reportview.common.utils.ExcelUtils;
import com.miguan.reportview.controller.base.BaseController;
import com.miguan.reportview.dto.*;
import com.miguan.reportview.entity.App;
import com.miguan.reportview.service.IAppService;
import com.miguan.reportview.service.ILdService;
import com.miguan.reportview.service.IUserContentService;
import com.miguan.reportview.service.IVideosService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author zhongli
 * @date 2020-07-30
 */
@Api(value = "用户内容运营数据", tags = {"用户内容运营数据"})
@RestController
public class UserContentController extends BaseController {

    @Autowired
    private IUserContentService userContentService;
    @Autowired
    private IAppService appService;
    @Autowired
    private IVideosService videosService;
    @Autowired
    private ILdService ldService;

    @ApiOperation(value = "拆线图")
    @PostMapping("api/usercontent/sta/getdata")
    public ResponseEntity<ChartAndTableDto<DisassemblyChartDto, UserContentDto>> getData(
            @ApiParam(value = "指标类型 多个用,隔开; 1=视频播放数2=视频曝光数3=视频播放率4=播放用户5=有效播放数6=曝光用户数7=人均播放数8=有效播放用户转化率9=人均播放时长10=完整播放数11=进文量12=进文量占比13=人均曝光数14=播放用户转化率15=平均播放进度16=有效播放率17=播放总时长18=完整播放率19=下线量20=评论量21=评论用户22=评论率23=人均评论数24=点赞量25=点赞用户26=收藏量27=收藏率28=人均完播数29=人均有效播放数30=新上线视频数31=新下线视频数32=线上视频数33=总视频数34=新采集视频数35=有效播放用户36=每曝光播放时长37=每播放播放时长", required = true)
                    String showType,
            @ApiParam(value = "分类类型 多个用,隔开; catid(分类标签)")
                    String showCustType,
            @ApiParam(value = "父渠道ID 多个用,隔开;") String pChannelId,
            @ApiParam(value = "渠道ID 多个用,隔开;") String channelId,
            @ApiParam(value = "是否新用户 多个用,隔开;true，false") String isNewUser,
            @ApiParam(value = "应用马甲包 多个用,隔开") String appPackage,
            @ApiParam(value = "应用版本号 多个用,隔开") String appVersion,
            @ApiParam(value = "是否激励视频；0=全部视频,1=激励视频") @RequestParam(defaultValue = "0") Integer incentiveTag,
            @ApiParam(value = "视频来源 多个用,隔开 多个用,隔") String videoSource,
            @ApiParam(value = "分组类型 多个用,隔开; 1=应用2=版本3=新老用户4=渠道5=分类6=父渠道7=视频来源") String groupType,
            @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
            @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate) {
        if (isAnyBlank(showType, startDate, endDate)) {
            throw new NullParameterException();
        }
        List<UserContentDto> list = userContentService.getData(isBlank(isNewUser) || isNewUser.indexOf(",") >= 0 ? null : Boolean.valueOf(isNewUser),
                seqArray2List(appPackage), seqArray2List(appVersion),
                seqArray2List(pChannelId),
                seqArray2List(channelId), seqArray2List(showCustType),
                incentiveTag,
                seqArray2List(videoSource),
                seqArray2List(groupType),
                startDate, endDate);
        if (isEmpty(list)) {
            throw new ResultCheckException();
        }
        String[] arrayS = showType.split(SEQ);
        List<DisassemblyChartDto> chartData = list.stream().flatMap(e -> {
            return Stream.of(arrayS).filter(StringUtils::isNotBlank).map(String::trim).mapToInt(Integer::parseInt).mapToObj(type -> {
                try {
                    return this.buildChartDto(e, type);
                } catch (Exception ex) {
                    throw new ResultCheckException(ex);
                }

            });
        }).collect(Collectors.toList());
        chartData = this.top10(chartData);
        return success(new ChartAndTableDto(chartData, list));
    }

    Map<Integer, AmpFiled> amp = FieldUtils.getFieldsListWithAnnotation(UserContentDto.class, ApiModelProperty.class)
            .stream().map(e -> {
                ApiModelProperty p = e.getAnnotation(ApiModelProperty.class);
                return new AmpFiled(p.position(), p.notes(), p.value(), e);
            }).collect(Collectors.toMap(AmpFiled::getPosition, e -> e));


    private DisassemblyChartDto buildChartDto(UserContentDto d, int type) throws IllegalAccessException {
        AmpFiled p = amp.get(type);
        if (p == null) {
            throw new NullParameterException(() -> String.format("未找到com.miguan.reportview.dto.UserContentDto.class 的相关属性ApiModelProperty#position = %d 的定义", type));
        }
        String name = p.getName();
        App app = appService.getAppByPackageName(d.getPackageName());
        name = name.concat(app == null ? "" : NAME_SEQ.concat(app.getName()));
        name = name.concat(isBlank(d.getAppVersion()) ? "" : NAME_SEQ.concat(d.getAppVersion()));
        name = name.concat(isBlank(d.getIsNew()) ? "" : NAME_SEQ.concat("1".equals(d.getIsNew()) ? "新用户" : "老用户"));
        name = name.concat(isBlank(d.getFatherChannel()) ? "" : NAME_SEQ.concat(d.getFatherChannel()));
        name = name.concat(isBlank(d.getChannel()) ? "" : NAME_SEQ.concat(d.getChannel()));
        name = name.concat(isBlank(d.getCatId()) ? "" : NAME_SEQ.concat(videosService.getCatName(d.getCatId())));
        name = name.concat(isBlank(d.getVideosSource()) ? "" : NAME_SEQ.concat(d.getVideosSource()));

        Object vaule = FieldUtils.readField(p.getField(), d, true);
        DisassemblyChartDto dto = new DisassemblyChartDto();
        dto.setDate(d.getDate());
        dto.setType(name);
        dto.setValue(vaule == null ? Double.NaN : Double.parseDouble(vaule.toString()));
        dto.setFormart(p.getNotes());
        return dto;
    }

    @ApiOperation(value = "导出")
    @GetMapping("api/usercontent/sta/export")
    public void export(HttpServletResponse response,
                       @ApiParam(value = "分类类型 多个用,隔开; catid(分类标签)") String showCustType,
                       @ApiParam(value = "父渠道ID 多个用,隔开;") String pChannelId,
                       @ApiParam(value = "渠道ID 多个用,隔开;") String channelId,
                       @ApiParam(value = "是否新用户 多个用,隔开;true=新用户，false=老用户") String isNewUser,
                       @ApiParam(value = "应用马甲包 多个用,隔开") String appPackage,
                       @ApiParam(value = "应用版本号 多个用,隔开") String appVersion,
                       @ApiParam(value = "是否激励视频；0=全部视频,1=激励视频") @RequestParam(defaultValue = "0") Integer incentiveTag,
                       @ApiParam(value = "视频来源 多个用,隔开 多个用,隔") String videoSource,
                       @ApiParam(value = "分组类型 多个用,隔开; 1=应用2=版本3=新老用户4=渠道5=分类6=父渠道7=视频来源") String groupType,
                       @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
                       @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate,
                       @ApiParam(value = "显示类型 多个用,隔开; 1=视频播放数2=视频曝光数3=视频播放率4=播放用户5=有效播放数6=曝光用户数7=人均播放数8=有效播放用户转化率9=人均播放时长10=完整播放数11=进文量12=进文量占比13=人均曝光数14=播放用户转化率15=平均播放进度16=有效播放率17=播放总时长18=完整播放率19=下线量20=评论量21=评论用户22=评论率23=人均评论数24=点赞量25=点赞用户26=收藏量27=收藏率28=人均完播数29=人均有效播放数30=新上线视频数31=新下线视频数32=线上视频数33=总视频数34=新采集视频数35=有效播放用户36=每曝光播放时长37=每播放播放时长", required = true)
                                   String showType) throws IOException {
        if (isAnyBlank(startDate, endDate)) {
            throw new NullParameterException();
        }
        List<String> groups = seqArray2List(groupType);
        List<UserContentDto> list = userContentService.getData(isBlank(isNewUser) ? null : Boolean.valueOf(isNewUser),
                seqArray2List(appPackage), seqArray2List(appVersion),
                seqArray2List(pChannelId),
                seqArray2List(channelId), seqArray2List(showCustType),
                incentiveTag, seqArray2List(videoSource), groups,
                startDate, endDate);
        if (isEmpty(list)) {
            throw new ResultCheckException();
        }
        ExportParams params = new ExportParams("用户内容运营数据", "用户内容运营数据", ExcelType.XSSF);
//        params.setCreateHeadRows(false);
        //过滤掉不需要的字段
        ArrayList<String> allGroup = Lists.newArrayList("1", "2", "3", "4", "5","6","7");
        if (!isEmpty(groups)) {
            allGroup.removeAll(groups);
        }

        //导出排除字段/属性
        String[] exclusions1 = poiExclusions(allGroup, groupNames);
        String[] exclusions2 = poiExclusions(showType, amp);
        params.setExclusions(mergeExclusions(exclusions1, exclusions2));
        list.forEach(d -> {
            App app = appService.getAppByPackageName(d.getPackageName());
            d.setPackageName(app == null ? "" : app.getName());
            d.setIsNew(isBlank(d.getIsNew()) ? "" : "1".equals(d.getIsNew()) ? "新用户" : "老用户");
            d.setCatId(isBlank(d.getCatId()) ? "" : videosService.getCatName(d.getCatId()));
        });
        ExcelUtils.defaultExport(list, UserContentDto.class, "用户内容运营数据", response, params);
    }

    private final List<String> groupNames = Lists.newArrayList("应用", "版本号", "是否新用户", "渠道", "分类", "父渠道", "视频来源");

    @ApiOperation(value = "来电拆线图和表格数据")
    @PostMapping("api/usercontent/sta/getlddata")
    public ResponseEntity<ChartAndTableDto<DisassemblyChartDto, LdUserContentDto>> getLdData(
            @ApiParam(value = "指标类型 多个用,隔开; 1=详情页播放次数2=详情页播放用户3=设置次数4=设置用户5=成功设置次数6=成功设置用户7=来电秀曝光量8=曝光用户9=来电详情播放率10=用户平均播放数11=用户转化率12=设置成功率13=人均曝光量14=收藏量15=收藏用户16=分享量17=分享用户18=分享率19=设置来电秀成功数20=设置来电秀成功率21=设置锁屏成功数22=设置锁屏成功率23=设置壁纸成功数24=设置壁纸成功率25=设置皮肤成功数26=设置皮肤成功率27=铃声试听数28=铃声试听用户数29=试听转化率30=铃声设置成功转化31=点击设铃声次数32=点击设铃声用户33=成功设置铃声次数34=成功设置铃声用户35=人均启动次数36=人均在线时长", required = true)
                    String showType,
            @ApiParam(value = "应用版本号 多个用,隔开") String appVersion,
            @ApiParam(value = "是否新用户 多个用,隔开;true，false") String isNewUser,
            @ApiParam(value = "渠道ID 多个用,隔开;") String channelId,
            @ApiParam(value = "父渠道ID 多个用,隔开;") String pChannelId,
            @ApiParam(value = "分类类型 多个用,隔开; catid(分类标签)") String showCustType,
            @ApiParam(value = "分组类型 多个用,隔开; 2=版本3=新老用户4=渠道5=分类6=父渠道") String groupType,
            @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
            @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate) {
        if (isAnyBlank(showType, startDate, endDate)) {
            throw new NullParameterException();
        }
        Boolean isNewApp = isBlank(isNewUser) || isNewUser.indexOf(",") >= 0 ? null : Boolean.valueOf(isNewUser);
        List<LdUserContentDto> list = userContentService.getLdData(seqArray2List(appVersion), isNewApp, seqArray2List(channelId), seqArray2List(pChannelId),
                seqArray2List(showCustType), seqArray2List(groupType), startDate, endDate);
        if (isEmpty(list)) {
            throw new ResultCheckException();
        }
        String[] arrayS = showType.split(SEQ);
        List<DisassemblyChartDto> chartData = list.stream().flatMap(e -> {
            return Stream.of(arrayS).filter(StringUtils::isNotBlank).map(String::trim).mapToInt(Integer::parseInt).mapToObj(type -> {
                try {
                    return this.buildLdChartDto(e, type);
                } catch (Exception ex) {
                    throw new ResultCheckException(ex);
                }

            });
        }).collect(Collectors.toList());
        chartData = this.top10(chartData);

        Iterator<LdUserContentDto> iterator = list.iterator();
        while(iterator.hasNext()) {
            LdUserContentDto d = iterator.next();
            d.setIsNewApp(isBlank(d.getIsNewApp()) ? "" : "1".equals(d.getIsNewApp()) ? "新用户" : "老用户");
            String catId = d.getVideoType();
            d.setVideoType(isBlank(d.getVideoType()) ? "" : ldService.getCatName(d.getVideoType()));
            if(groupType.contains("5") && d.getVideoType().equals(catId)) {
                iterator.remove();
            }
        }
        return success(new ChartAndTableDto(chartData, list));
    }

    Map<Integer, AmpFiled> ldAmp = FieldUtils.getFieldsListWithAnnotation(LdUserContentDto.class, ApiModelProperty.class)
            .stream().map(e -> {
                ApiModelProperty p = e.getAnnotation(ApiModelProperty.class);
                return new AmpFiled(p.position(), p.notes(), p.value(), e);
            }).collect(Collectors.toMap(AmpFiled::getPosition, e -> e));

    private DisassemblyChartDto buildLdChartDto(LdUserContentDto d, int type) throws IllegalAccessException {
        AmpFiled p = ldAmp.get(type);
        if (p == null) {
            throw new NullParameterException(() -> String.format("未找到com.miguan.reportview.dto.UserContentDto.class 的相关属性ApiModelProperty#position = %d 的定义", type));
        }
        String name = p.getName();
        name = name.concat(isBlank(d.getAppVersion()) ? "" : NAME_SEQ.concat(d.getAppVersion()));
        name = name.concat(isBlank(d.getIsNewApp()) ? "" : NAME_SEQ.concat("1".equals(d.getIsNewApp()) ? "新用户" : "老用户"));
        name = name.concat(isBlank(d.getFatherChannel()) ? "" : NAME_SEQ.concat(d.getFatherChannel()));
        name = name.concat(isBlank(d.getChannel()) ? "" : NAME_SEQ.concat(d.getChannel()));
        name = name.concat(isBlank(d.getVideoType()) ? "" : NAME_SEQ.concat(ldService.getCatName(d.getVideoType())));
        Object vaule = FieldUtils.readField(p.getField(), d, true);
        DisassemblyChartDto dto = new DisassemblyChartDto();
        dto.setDate(d.getDate());
        dto.setType(name);
        dto.setValue(vaule == null ? Double.NaN : Double.parseDouble(vaule.toString()));
        dto.setFormart(p.getNotes());
        return dto;
    }

    @ApiOperation(value = "来电导出")
    @GetMapping("api/usercontent/sta/ldexport")
    public void ldExport(HttpServletResponse response,
                         @ApiParam(value = "应用版本号 多个用,隔开") String appVersion,
                         @ApiParam(value = "是否新用户 多个用,隔开;true，false") String isNewUser,
                         @ApiParam(value = "渠道ID 多个用,隔开;") String channelId,
                         @ApiParam(value = "父渠道ID 多个用,隔开;") String pChannelId,
                         @ApiParam(value = "分类类型 多个用,隔开; catid(分类标签)") String showCustType,
                         @ApiParam(value = "分组类型 多个用,隔开; 2=版本3=新老用户4=渠道5=分类6=父渠道") String groupType,
                         @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
                         @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate,
                         @ApiParam(value = "显示类型 多个用,隔开; 1=详情页播放次数2=详情页播放用户3=设置次数4=设置用户5=成功设置次数6=成功设置用户7=来电秀曝光量8=曝光用户9=来电详情播放率10=用户平均播放数11=用户转化率12=设置成功率13=人均曝光量14=收藏量15=收藏用户16=分享量17=分享用户18=分享率19=设置来电秀成功数20=设置来电秀成功率21=设置锁屏成功数22=设置锁屏成功率23=设置壁纸成功数24=设置壁纸成功率25=设置皮肤成功数26=设置皮肤成功率27=铃声试听数28=铃声试听用户数29=试听转化率30=铃声设置成功转化31=点击设铃声次数32=点击设铃声用户33=成功设置铃声次数34=成功设置铃声用户35=人均启动次数36=人均在线时长", required = true)
                                     String showType) throws IOException {
        if (isAnyBlank(startDate, endDate)) {
            throw new NullParameterException();
        }
        List<String> groups = seqArray2List(groupType);
        Boolean isNewApp = isBlank(isNewUser) || isNewUser.indexOf(",") >= 0 ? null : Boolean.valueOf(isNewUser);
        List<LdUserContentDto> list = userContentService.getLdData(seqArray2List(appVersion), isNewApp, seqArray2List(channelId), seqArray2List(pChannelId),
                seqArray2List(showCustType), groups, startDate, endDate);
        if (isEmpty(list)) {
            throw new ResultCheckException();
        }
        ExportParams params = new ExportParams("用户内容运营数据", "用户内容运营数据", ExcelType.XSSF);
        ArrayList<String> allGroup = Lists.newArrayList("1", "2", "3", "4", "5","6");
        if (!isEmpty(groups)) {
            allGroup.removeAll(groups);
        }
        //导出排除字段
        String[] exclusions1 = poiExclusions(allGroup, groupNames);
        String[] exclusions2 = poiExclusions(showType, ldAmp);
        params.setExclusions(mergeExclusions(exclusions1, exclusions2));

        list.forEach(d -> {
            d.setIsNewApp(isBlank(d.getIsNewApp()) ? "" : "1".equals(d.getIsNewApp()) ? "新用户" : "老用户");
            d.setVideoType(isBlank(d.getVideoType()) ? "" : ldService.getCatName(d.getVideoType()));
        });
        ExcelUtils.defaultExport(list, LdUserContentDto.class, "用户内容运营数据", response, params);
    }


    @ApiOperation(value = "视频明细数据")
    @PostMapping("api/usercontent/findVideoDetailList")
    public ResponseEntity<PageInfo<UserContentDetailDto>> findVideoDetailList(
            @ApiParam(value = "应用马甲包 多个用,隔开") String appPackage,
            @ApiParam(value = "应用版本号 多个用,隔开") String appVersion,
            @ApiParam(value = "分类Id 多个用,隔开") String catId,
            @ApiParam(value = "自定义曝光区间：1=6个区间，2=9个区间") @RequestParam(defaultValue = "1") Integer sectionType,
            @ApiParam(value = "是否当天上线：1=当天，2=全部") Integer nowOnlineType,
            @ApiParam(value = "是否当进文：1=当天，2=全部") Integer nowJwType,
            @ApiParam(value = "时间类型：1=行为日期，2=上线日期，3=进文日期") @RequestParam(defaultValue = "1") Integer dateType,
            @ApiParam(value = "日期; 日期格式: yyyy-MM-dd", required = true) String day,
            @ApiParam(value = "是否激励视频，0-否，1-是") Integer isIncentive,
            @ApiParam(value = "排序字段,格式:字段名+空格+升降序标识,例如：dd asc(日期升序), dd desc(日期降序)。dd=日期,onlineDate=上线日期,jwDate=进文日期," +
                    "catId=分类,videoTitle=视频标题,isIncentive=是否激励视频,showNum=曝光次数,showSection=曝光区间,playNum=播放次数,playRate=播放率,catPlayRate=分类平均播放率," +
                    "totalPlayRate=总体平均播放率,showUser=曝光用户数,playUser=播放用户数,playTimeR=播放时长,everyPlayTimeR=每次曝光播放时长,perShowNum=人均曝光次数," +
                    "perPlayNum=人均播放次数,vplayNum=有效播放次数,vplayUser=有效播放用户,allPlayNum=完播次数,allPlayUser=完播用户数,preEndPayCount=人均完播次数,likeNum=点赞数," +
                    "likeUser=点赞用户数,shareNum=分享数,shareUser=分享用户数,favNum=收藏数,favUser=收藏用户数,") String orderByField,
            @ApiParam(value = "页码", required = true) int pageNum,
            @ApiParam(value = "每页记录数", required = true) int pageSize) {
        PageInfo<UserContentDetailDto> pageList = userContentService.findVideoDetailList(seqArray2List(appPackage), seqArray2List(appVersion), seqArray3List(catId),
                sectionType, nowOnlineType, nowJwType, dateType, day, isIncentive, orderByField, pageNum, pageSize, null, null);
        return success(pageList);
    }

    @ApiOperation(value = "视频明细数据-导出")
    @GetMapping("api/usercontent/sta/videoDetailExport")
    public void videoDetailExport(HttpServletResponse response,
                                  @ApiParam(value = "应用马甲包 多个用,隔开") String appPackage,
                                  @ApiParam(value = "应用版本号 多个用,隔开") String appVersion,
                                  @ApiParam(value = "分类Id 多个用,隔开") String catId,
                                  @ApiParam(value = "自定义曝光区间：1=6个区间，2=9个区间") @RequestParam(defaultValue = "1") Integer sectionType,
                                  @ApiParam(value = "是否当天上线：1=当天，2=全部") Integer nowOnlineType,
                                  @ApiParam(value = "是否当进文：1=当天，2=全部") Integer nowJwType,
                                  @ApiParam(value = "时间类型：1=行为日期，2=上线日期，3=进文日期") @RequestParam(defaultValue = "1") Integer dateType,
                                  @ApiParam(value = "日期; 日期格式: yyyy-MM-dd", required = true) String day,
                                  @ApiParam(value = "是否激励视频，0-否，1-是") Integer isIncentive,
                                  @ApiParam(value = "排序字段,格式:字段名+空格+升降序标识,例如：dd asc(日期升序), dd desc(日期降序)。dd=日期,onlineDate=上线日期,jwDate=进文日期," +
                                          "catId=分类,videoTitle=视频标题,isIncentive=是否激励视频,showNum=曝光次数,showSection=曝光区间,playNum=播放次数,playRate=播放率,catPlayRate=分类平均播放率," +
                                          "totalPlayRate=总体平均播放率,showUser=曝光用户数,playUser=播放用户数,playTimeR=播放时长,everyPlayTimeR=每次曝光播放时长,perShowNum=人均曝光次数," +
                                          "perPlayNum=人均播放次数,vplayNum=有效播放次数,vplayUser=有效播放用户,allPlayNum=完播次数,allPlayUser=完播用户数,preEndPayCount=人均完播次数,likeNum=点赞数," +
                                          "likeUser=点赞用户数,shareNum=分享数,shareUser=分享用户数,favNum=收藏数,favUser=收藏用户数,")
                                          String orderByField,
                                  @ApiParam(value = "导出起始记录数", required = true) int startRow,
                                  @ApiParam(value = "导出结束记录数", required = true) int endRow,
                                  @ApiParam(value = "显示类型，多个逗号分隔:1=日期2=上线日期3=进文日期4=分类5=内容来源6=视频标题7=视频id8=是否激励视频9=曝光次数10=曝光区间11=播放次数12=播放率13=分类平均播放率14=总体平均播放率15=曝光用户数16=播放用户数17=播放时长18=每次曝光播放时长19=人均曝光次数20=人均播放次数21=有效播放次数22=有效播放率23=有效播放用户24=完播次数25=完播率26=完播用户数27=人均完播次数28=点赞数29=点赞用户数30=分享数31=分享用户数32=收藏数33=收藏用户数", required = true)
                                          String showType) throws IOException {
        PageInfo<UserContentDetailDto> pageList = userContentService.findVideoDetailList(seqArray2List(appPackage), seqArray2List(appVersion), seqArray3List(catId),
                sectionType, nowOnlineType, nowJwType, dateType, day, isIncentive, orderByField, null, null, startRow, endRow);

        ExportParams params = new ExportParams("视频明细数据", "视频明细数据", ExcelType.XSSF);
        params.setExclusions(poiExclusions(showType, detailAmp));
        ExcelUtils.defaultExport(pageList.getList(), UserContentDetailDto.class, "视频明细数据", response, params);
    }

    Map<Integer, AmpFiled> detailAmp = FieldUtils.getFieldsListWithAnnotation(UserContentDetailDto.class, ApiModelProperty.class)
            .stream().map(e -> {
                ApiModelProperty p = e.getAnnotation(ApiModelProperty.class);
                return new AmpFiled(p.position(), p.notes(), p.value(), e);
            }).collect(Collectors.toMap(AmpFiled::getPosition, e -> e));

    @ApiOperation(value = "视频明细区间汇总数据")
    @PostMapping("api/usercontent/findVideoSectionList")
    public ResponseEntity<List<VideoSectionDataDto>> videoSectionExport(
            @ApiParam(value = "分类Id 多个用,隔开") String catId,
            @ApiParam(value = "自定义曝光区间：1=6个区间，2=9个区间") @RequestParam(defaultValue = "1") Integer sectionType,
            @ApiParam(value = "日期; 日期格式: yyyy-MM-dd", required = true) String day) {
        List<VideoSectionDataDto> list = userContentService.findVideoSectionList(seqArray3List(catId), sectionType, day);
        return success(list);
    }


    @ApiOperation(value = "视频明细区间汇总数据-导出")
    @GetMapping("api/usercontent/videoSectionExport")
    public void findVideoSectionList(HttpServletResponse response,
                                     @ApiParam(value = "分类Id 多个用,隔开") String catId,
                                     @ApiParam(value = "自定义曝光区间：1=6个区间，2=9个区间") @RequestParam(defaultValue = "1") Integer sectionType,
                                     @ApiParam(value = "日期; 日期格式: yyyy-MM-dd", required = true) String day) throws IOException {
        List<VideoSectionDataDto> list = userContentService.findVideoSectionList(seqArray3List(catId), sectionType, day);
        ExportParams params = new ExportParams("视频明细区间汇总数据", "视频明细区间汇总数据", ExcelType.XSSF);
        ExcelUtils.defaultExport(list, VideoSectionDataDto.class, "视频明细区间汇总数据", response, params);
    }
}
