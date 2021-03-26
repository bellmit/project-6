package com.miguan.reportview.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.miguan.reportview.dto.ContentQualityDto;
import com.miguan.reportview.dto.OnlineDataContrastDto;
import com.miguan.reportview.dto.OnlineVideoDto;
import com.miguan.reportview.dto.UserContentDetailDto;
import com.miguan.reportview.entity.App;
import com.miguan.reportview.mapper.ContentQualityMapper;
import com.miguan.reportview.service.ContentQualityService;
import com.miguan.reportview.service.IAppService;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.miguan.reportview.common.utils.NumCalculationUtil.divide;

/**
 * 内容质量评估service
 */
@Service
public class ContentQualityServiceImpl implements ContentQualityService {

    @Resource
    private ContentQualityMapper contentQualityMapper;
    @Autowired
    private IAppService appService;

    /**
     * 线上短视频库列表
     * @param video_id 视频id
     * @param video_title 视频标题
     * @param video_source 内容源
     * @param cat_id 分类id
     * @param gather_id 合集id
     * @param play_effect 播放效果
     * @param startUpdateDate  更新时间（start）
     * @param endUpdateDate  更新时间（end）
     * @param startOnlineDate 上线日期（start）
     * @param endOnlineDate  上线日期（end）
     * @param startStaDate  统计日期（start）
     * @param endStaDate   统计日期（end）
     * @param orderByField  排序字段
     * @param pageNum  页码
     * @param pageSize 每页记录数
     * @return
     */
    public PageInfo<OnlineVideoDto> listOnlineVideo(String video_id,
                                                    String video_title,
                                                    String video_source,
                                                    String package_name,
                                                    Long cat_id,
                                                    Long gather_id,
                                                    Integer play_effect,
                                                    Integer sensitive,
                                                    String startUpdateDate,
                                                    String endUpdateDate,
                                                    String startOnlineDate,
                                                    String endOnlineDate,
                                                    String startStaDate,
                                                    String endStaDate,
                                                    String orderByField,
                                                    int pageNum,
                                                    int pageSize) {
        Map<String, Object> params = new HashMap<>();
        if(StringUtils.isNotBlank(video_id)) {
            params.put("video_ids", Arrays.asList(video_id.split(",")));
        }
        params.put("video_title", video_title);
        params.put("video_source", video_source);
        params.put("package_name", package_name);
        if(StringUtils.isNotBlank(package_name)) {
            params.put("ascription_application", "'%" + package_name + "%'");
        }
        params.put("cat_id", cat_id);
        params.put("gather_id", gather_id);
        params.put("playEffect", play_effect);
        params.put("sensitive", sensitive);
        params.put("startUpdateDate", startUpdateDate);
        params.put("endUpdateDate", endUpdateDate);
        params.put("startOnlineDate", startOnlineDate);
        params.put("endOnlineDate", endOnlineDate);
        params.put("startStaDate", startStaDate);
        params.put("endStaDate", endStaDate);
        params.put("orderByField", orderByField);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);

        //取出视频源关系表
        List<Map<String,Object>> videoSourceList=contentQualityMapper.listVideoSource();
       //拼接casewhen视频源类型,之前是写死的
        //case  WHEN anyLast(videos_source) = '98du' THEN '98°视频'  WHEN anyLast(videos_source) = 'wuli' THEN '唔哩视频'  WHEN anyLast(videos_source) = 'yiyouliao' THEN '易有料'  WHEN anyLast(videos_source) = 'yueke' THEN '阅客视频'  ELSE '98°视频' END
        StringBuilder videoSourceCasewhenSb=new StringBuilder("CASE ");
        for(Map<String,Object> rowObj:videoSourceList){
            videoSourceCasewhenSb.append(" WHEN anyLast(videos_source) = '"+rowObj.get("source_key")+"' THEN '"+rowObj.get("source_name")+"' ");
        }
        videoSourceCasewhenSb.append(" ELSE '98°视频' END ");
        params.put("video_source_type",videoSourceCasewhenSb.toString());
       // System.out.println(casewhenSb.toString());

        PageHelper.startPage(pageNum, pageSize);
        PageInfo<OnlineVideoDto> pageList = contentQualityMapper.listOnlineVideo(params).toPageInfo();   //明显列表
        pageList.getList().forEach(r->{
            if(r.getCatPlayRate() == -0.0) {
                r.setCatPlayRate(0D);
            }
            if(r.getCatVplayRate() == -0.0) {
                r.setCatVplayRate(0D);
            }
            if(r.getCatAllPlayRate() == -0.0) {
                r.setCatAllPlayRate(0D);
            }
        });
        return pageList;
    }

    /**
     * 线上短视频-数据对比
     * @param video_id 视频id
     * @param startStaDate  统计日期（start）
     * @param endStaDate   统计日期（end）
     * @param pageNum  页码
     * @param pageSize 每页记录数
     * @return
     */
    public PageInfo<OnlineDataContrastDto> listOnlineDataContrast(Long video_id, String startStaDate, String endStaDate, int pageNum, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("video_id", video_id);
        params.put("startStaDate", startStaDate);
        params.put("endStaDate", endStaDate);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);

        PageHelper.startPage(pageNum, pageSize);
        PageInfo<OnlineDataContrastDto> pageList = contentQualityMapper.listOnlineDataContrast(params).toPageInfo();
        return pageList;
    }

    /**
     * 内容质量评估报表
     * @param video_source 内容源
     * @param cat_id 分类id
     * @param startOnlineDate 进文时间（start）
     * @param endOnlineDate  进文时间（end）
     * @param startStaDate  统计日期（start）
     * @param endStaDate   统计日期（end）
     * @param orderByField  排序字段
     * @param pageNum  页码
     * @param pageSize 每页记录数
     * @return
     */
    public PageInfo<ContentQualityDto> listContentQuality(String video_source,String package_name, Long cat_id, String startOnlineDate, String endOnlineDate, String startStaDate,
                                                          String endStaDate, String orderByField, int pageNum, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("video_source", video_source);
        params.put("package_name", package_name);
        if(StringUtils.isNotBlank(package_name)) {
            params.put("ascription_application", "'%" + package_name + "%'");
        }
        params.put("cat_id", cat_id);
        params.put("startOnlineDate", startOnlineDate);
        params.put("endOnlineDate", endOnlineDate);
        params.put("startStaDate", startStaDate);
        params.put("endStaDate", endStaDate);
        params.put("orderByField", orderByField);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        Long totalShowCount = contentQualityMapper.totalShowCount(params);  /*所有分类所有视频来源的总曝光数*/
        params.put("totalShowCount", totalShowCount);

        PageHelper.startPage(pageNum, pageSize);

        //取出视频源关系表
        List<Map<String,Object>> videoSourceList=contentQualityMapper.listVideoSource();
        //拼接casewhen视频源类型,之前是写死的
        //case  WHEN anyLast(videos_source) = '98du' THEN '98°视频'  WHEN anyLast(videos_source) = 'wuli' THEN '唔哩视频'  WHEN anyLast(videos_source) = 'yiyouliao' THEN '易有料'  WHEN anyLast(videos_source) = 'yueke' THEN '阅客视频'  ELSE '98°视频' END
        StringBuilder videoSourceCasewhenSb=new StringBuilder("CASE ");
        for(Map<String,Object> rowObj:videoSourceList){
            videoSourceCasewhenSb.append(" WHEN anyLast(videos_source) = '"+rowObj.get("source_key")+"' THEN '"+rowObj.get("source_name")+"' ");
        }
        videoSourceCasewhenSb.append(" ELSE '98°视频' END ");
        params.put("video_source_type",videoSourceCasewhenSb.toString());
        PageInfo<ContentQualityDto> pageList = contentQualityMapper.listContentQuality(params).toPageInfo();
//        pageList.getList().forEach(r->{
//            r.setPackageName(this.packageNameConvert(r.getPackageName().replace(",", "、")));  //把应用包名转换成中文名称
//        });
        return pageList;
    }

    /**
     * 把应用包名转换成中文名称
     * @param packageName
     * @return
     */
    public String packageNameConvert(String packageName){
        String result = "";
        String[] packageNameArray = packageName.split("、");
        for(int i=0;i<packageNameArray.length;i++) {
            App app = appService.getAppByPackageName(packageNameArray[i]);
            String appName = (app == null ? packageNameArray[i] : app.getName());
            if(i == 0) {
                result = appName;
            } else {
                result = result + "," + appName;
            }
        }
        return result;
    }
}