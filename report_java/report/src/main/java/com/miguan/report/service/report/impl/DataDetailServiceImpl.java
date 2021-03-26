package com.miguan.report.service.report.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.miguan.report.common.util.DateUtil;
import com.miguan.report.common.util.ExportXlsxUtil;
import com.miguan.report.dto.BannerQuotaDto;
import com.miguan.report.dto.DataDetailDto;
import com.miguan.report.dto.LineChartDto;
import com.miguan.report.mapper.DataDetailMapper;
import com.miguan.report.service.report.DataDetailService;
import com.miguan.report.service.report.SelectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import tool.util.BigDecimalUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description 数据明细service
 * @Author zhangbinglin
 * @Date 2020/6/17 14:10
 **/
@Slf4j
@Service
public class DataDetailServiceImpl implements DataDetailService {

    @Resource
    private DataDetailMapper dataDetailMapper;

    /**
     * 查询数据明细列表（分页）
     *
     * @param pageNum     页码
     * @param pageSize    每页记录数
     * @param startDate   统计开始时间
     * @param endDate     统计结束时间
     * @param token      token
     * @param appType     app类型：1=西柚视频,2=炫来电
     * @param appClientId 应用客户端id
     * @param platForm    平台id
     * @param totalName   广告位置名称
     * @param adId        代码为id
     * @param sortField
     * @return
     */
    public DataDetailDto findDataDetailList(int pageNum, int pageSize, String startDate, String endDate, String token, Integer appType,
                                            String appClientId, String platForm, String totalName, String adId, String sortField) {
        DataDetailDto dataDetailDto = new DataDetailDto();
        Map<String, Object> params = getQueryParams(startDate, endDate, appType, appClientId, platForm, totalName, adId, sortField);

        List<BannerQuotaDto> headDtoList = dataDetailMapper.getHeadList(token, appType);
        filterHeadList(params, headDtoList);  //根据查询条件，过滤掉统计的字段
        if(StringUtils.isBlank(adId)) {
            filterAdFiled(headDtoList);
        }
        dataDetailDto.setHeaderList(headDtoList);   //表头列表

        PageInfo<LinkedHashMap<String, Object>> dataList = this.findDataDetailListByPage(pageNum, pageSize, params);  //查询数据明细的分页数据
        dataDetailDto.setDataList(dataList);
        return dataDetailDto;
    }

    /**
     * 如果没有选择代码位，则过滤掉代码位后面的字段
     * @param headDtoList
     */
    public void filterAdFiled(List<BannerQuotaDto> headDtoList) {
        List<String> filters = Arrays.asList("ad_space_request","ad_space_return","ad_space_filling","ad_request","ad_return","ad_filling","exposure_rate","error_rate");
        for(BannerQuotaDto bannerQuotaDto : headDtoList) {
            if(filters.contains(bannerQuotaDto.getField())) {
                bannerQuotaDto.setSelect(false);
            }
        }
    }

    /**
     * 查询数据明细列表（分页）
     *
     * @param startDate   统计开始时间
     * @param endDate     统计结束时间
     * @param token       token
     * @param appType     app类型：1=西柚视频,2=炫来电
     * @param appClientId 应用客户端id
     * @param platForm    平台id
     * @param totalName   广告位置名称
     * @param adId        代码为id
     * @param sortField
     * @return
     */
    public void export(HttpServletResponse response, String startDate, String endDate, String token, Integer appType,
                       String appClientId, String platForm, String totalName, String adId, String sortField) {
        DataDetailDto dataDetailDto = findDataDetailList(0, 0, startDate, endDate, token, appType, appClientId,
                platForm, totalName, adId, sortField);
        List<LinkedHashMap<String, Object>> dataList = dataDetailDto.getDataList().getList();   //数据项
        List<BannerQuotaDto> headerList = dataDetailDto.getHeaderList();  //表头

        //显示的列明
        List<String> showNameList = headerList.stream().filter(head -> head.isSelect()).map(head -> head.getName()).collect(Collectors.toList());
        //获取隐藏的列字段
        List<String> hideFieldList = headerList.stream().filter(head -> !head.isSelect()).map(head -> head.getField()).collect(Collectors.toList());
        //删除隐藏的列的值
        for (Map<String, Object> map : dataList) {
            hideFieldList.forEach(hideField -> map.remove(hideField));
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        //生成一个表格，设置表格名称为"学生表"
        HSSFSheet sheet = workbook.createSheet("明细数据");
        //创建第一行表头
        HSSFRow headrow = sheet.createRow(0);
        for (int i = 0; i < showNameList.size(); i++) {
            //创建一个单元格
            HSSFCell cell = headrow.createCell(i);
            //创建一个内容对象
            HSSFRichTextString text = new HSSFRichTextString(showNameList.get(i));
            //将内容对象的文字内容写入到单元格中
            cell.setCellValue(text);
        }

        List<String> fieldRates = Arrays.asList("click_rate","ad_space_filling","ad_filling","exposure_rate","error_rate");  //需要转车百分比的字段
        for(int i=0;i<dataList.size();i++) {
            LinkedHashMap<String, Object> dataMap = dataList.get(i);
            HSSFRow row1 = sheet.createRow(i + 1);
            int j = 0;
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                HSSFCell cell = row1.createCell(j);
                HSSFRichTextString text = null;
                if(fieldRates.contains(entry.getKey())) {
                    //需要转成百分比的字段
                    Double value = (entry.getValue() == null ? 0 : Double.parseDouble(entry.getValue().toString()));
                    String valstr = BigDecimalUtil.mul(value, 100) + "%";
                    text = new HSSFRichTextString(valstr);
                } else {
                    text = new HSSFRichTextString(entry.getValue().toString());
                }
                cell.setCellValue(text);
                j++;
            }
        }

        try {
            //准备将Excel的输出流通过response输出到页面下载
            //八进制输出流
            response.setContentType("application/octet-stream");
            //这后面可以设置导出Excel的名称，
            String fileName = "数据明细统计表-".concat(DateUtil.yyyyMMdd());
            fileName = new String(fileName.getBytes("utf-8"),"ISO-8859-1" );
            response.setHeader("Content-disposition", "attachment;filename="+ fileName +".xls");

            //刷新缓冲
            response.flushBuffer();
            //workbook将Excel写入到response的输出流中，供页面下载
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            log.error("导出数据明细错误");
        }
    }

    /**
     * 根据查询条件，过滤掉统计的字段
     *
     * @param params      应用客户端id
     * @param headDtoList 用户自定义的字段列表
     */
    public void filterHeadList(Map<String, Object> params, List<BannerQuotaDto> headDtoList) {
        BannerQuotaDto bannerQuotaDto = new BannerQuotaDto();
        List<String> groupField = Stream.of("concat(cut_app_name,d.client_id)", "plat_form", "total_name", "ad_space_id").collect(Collectors.toList());  //统计的字段

        int index = 0;
        if (params.get("appClientId") == null) {
            //过滤 应用客户端id列
            bannerQuotaDto = new BannerQuotaDto();
            bannerQuotaDto.setField("app_id");
            index = headDtoList.indexOf(bannerQuotaDto);
            bannerQuotaDto = headDtoList.get(index);
            bannerQuotaDto.setSelect(false);
            groupField.remove("concat(cut_app_name,d.client_id)");
        }

        if (params.get("platForm") == null) {
            //过滤平台列
            bannerQuotaDto = new BannerQuotaDto();
            bannerQuotaDto.setField("plat_form");
            index = headDtoList.indexOf(bannerQuotaDto);
            bannerQuotaDto = headDtoList.get(index);
            bannerQuotaDto.setSelect(false);
            groupField.remove("plat_form");
        }

        if (params.get("totalName") == null) {
            //过滤广告位置列
            bannerQuotaDto = new BannerQuotaDto();
            bannerQuotaDto.setField("ad_space");  //广告位置
            index = headDtoList.indexOf(bannerQuotaDto);
            bannerQuotaDto = headDtoList.get(index);
            bannerQuotaDto.setSelect(false);
            groupField.remove("total_name");
        }

        if (params.get("adId") == null) {
            //过滤代码位
            bannerQuotaDto = new BannerQuotaDto();
            bannerQuotaDto.setField("ad_space_id");
            index = headDtoList.indexOf(bannerQuotaDto);
            bannerQuotaDto = headDtoList.get(index);
            bannerQuotaDto.setSelect(false);

            bannerQuotaDto = new BannerQuotaDto();
            bannerQuotaDto.setField("ad_style");  //广告类型
            index = headDtoList.indexOf(bannerQuotaDto);
            bannerQuotaDto = headDtoList.get(index);
            bannerQuotaDto.setSelect(false);

            bannerQuotaDto = new BannerQuotaDto();
            bannerQuotaDto.setField("ad_type");   //广告样式
            index = headDtoList.indexOf(bannerQuotaDto);
            bannerQuotaDto = headDtoList.get(index);
            bannerQuotaDto.setSelect(false);

            groupField.remove("ad_space_id");
        }

        String groupSql = String.join(",", groupField);
        if (StringUtils.isNotBlank(groupSql)) {
            groupSql = "," + groupSql;
        }
        //拼接groupby语句
        params.put("groupSql", groupSql);
    }

    private Map<String, Object> getQueryParams(String startDate, String endDate, Integer appType, String appClientId, String platForm,
                                               String totalName, String adId, String sortField) {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        int statType = 0;   //日活数的统计方式
        String activeSelectField = "";  //汇总日活使用
        String activeGroupField = "";  //汇总日活使用
        String activeRelationField = "";  //汇总日活使用
        if(StringUtils.isNotBlank(appClientId)) {
            params.put("appClientId", "'" + String.join("','", appClientId.split(",")) + "'");
        }
        if(StringUtils.isNotBlank(platForm)) {
            params.put("platForm", "'" + String.join("','", platForm.split(",")) + "'");
        }
        if(StringUtils.isNotBlank(totalName)) {
            params.put("totalName", "'" + String.join("','", totalName.split(",")) + "'");
        }
        if(StringUtils.isNotBlank(adId)) {
            params.put("adId", "'" + String.join("','", adId.split(",")) + "'");
            statType = 1;
        }
        params.put("statType", statType);
        params.put("appType", appType);
        params.put("sortField", sortField);
        if(StringUtils.isBlank(adId)) {
            params.put("adIdNull", 1);
        }
        return params;
    }

    /**
     * 查询数据明细的分页数据
     *
     * @param pageNum
     * @param pageSize
     * @param params
     * @return
     */
    private PageInfo<LinkedHashMap<String, Object>> findDataDetailListByPage(int pageNum, int pageSize, Map<String, Object> params) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<LinkedHashMap<String, Object>> pageInfo = dataDetailMapper.findDataDetailList(params).toPageInfo();
        return pageInfo;
    }

    /**
     * 数据明细折线图
     *
     * @param startDate   开始时间
     * @param endDate     结束时间
     * @param appType     app类型：1=西柚视频,2=炫来电(默认值为1)
     * @param statItem    统计项,1-展现量,2-点击量，3-点击率，4-点击单价，5-cpm，6-营收，7-人均展现数，8-日活均价值，9-展示率，10-报错率，11-广告填充率
     * @param appClientId 应用id(下拉列表取appClientList)
     * @param platForm    平台id
     * @param totalName   广告位置名称
     * @param adId        代码为id
     * @param timeType    时间类型：0-按小时，1=按日，2=按周，3=按月(默认值为1)
     * @return
     */
    public List<LineChartDto> countLineDataDetailChart(String startDate, String endDate, Integer appType, String statItem, String appClientId,
                                                       String platForm, String totalName, String adId, Integer timeType) {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("appType", appType);
        params.put("timeType", timeType);
        List<String> statItems = Arrays.asList(statItem.split(","));
        if(StringUtils.isNotBlank(appClientId)) {
            params.put("appClientId", "'" + String.join("','", appClientId.split(",")) + "'");
        }
        if(StringUtils.isNotBlank(platForm)) {
            params.put("platForm", "'" + String.join("','", platForm.split(",")) + "'");
        }
        if(StringUtils.isNotBlank(totalName)) {
            params.put("totalName", "'" + String.join("','", totalName.split(",")) + "'");
        }
        if(StringUtils.isNotBlank(adId)) {
            params.put("adId", "'" + String.join("','", adId.split(",")) + "'");
        }
        List<Map<String, Object>> items = new ArrayList<>();
        for(String oneItem : statItems) {
            Map<String, Object> item = new HashMap<>();
            item.put("statItme", oneItem);
            item.put("selectNameSql", selectNameSql(appClientId, platForm, totalName, adId, oneItem));
            items.add(item);
        }
        params.put("items", items);
        params.put("groupSql", groupDateSql(timeType, appClientId, platForm, totalName, adId));
        return dataDetailMapper.countLineDataDetailChart(params);
    }

    private String selectNameSql(String appClientId, String platForm, String totalName, String adId, String statItem) {

        String type = "concat(";
        String midType = "";
        if (StringUtils.isNotBlank(adId)) {
            //代码位多选
            midType = "ad_space_id,'-',";
        } else {
            if (StringUtils.isNotBlank(appClientId)) {
                //平台多选
                midType = midType + "cut_app_name,if(d.client_id=1, 'Android', 'ios'), '-',";
            }
            if (StringUtils.isNotBlank(platForm)) {
                //平台多选
                midType = midType + "(case plat_form when 1 then '穿山甲' when 2 then '广点通' when 3 then '快手' end),'-',";
            }
            if (StringUtils.isNotBlank(totalName)) {
                //广告位多选
                midType = "total_name,'-',";
            }
            if(StringUtils.isBlank(midType)) {
                midType =  "'', ";
            }
        }

        type = type + midType;

        //统计项,1-展现量,2-点击量，3-点击率，4-点击单价，5-cpm，6-营收，7-人均展现数，8-日活均价值，9-展示率，10-报错率，11-广告填充率
        switch (statItem) {
            case "1":
                return type + "'展现量') type,";
            case "2":
                return type + "'点击量') type,";
            case "3":
                return type + "'点击率') type,";
            case "4":
                return type + "'点击单价') type,";
            case "5":
                return type + "'cpm') type,";
            case "6":
                return type + "'营收') type,";
            case "7":
                return type + "'人均展现数') type,";
            case "8":
                return type + "'日活均价值') type,";
            case "9":
                return type + "'展示率') type,";
            case "10":
                return type + "'报错率') type,";
            case "11":
                return type + "'广告填充率') type,";
            default:
                return "";
        }
    }

    private String groupDateSql(Integer timeType, String appClientId, String platForm, String totalName, String adId) {
        String groupDateSql = "";
        if (timeType == 0) {
            //按小时
            groupDateSql = "group by d.date, e.hours ";
        } else if (timeType == 1) {
            //按天
            groupDateSql = "group by d.date ";
        } else if (timeType == 2) {
            //按周
            groupDateSql = "group by date_format(d.date,'%Y%u') ";
        } else {
            groupDateSql = "group by date_format(d.date,'%m') ";
        }

        if (StringUtils.isNotBlank(appClientId)) {
            groupDateSql = groupDateSql + ",concat(cut_app_name,d.client_id)";
        }
        if (StringUtils.isNotBlank(platForm)) {
            groupDateSql = groupDateSql + ",plat_form";
        }
        if (StringUtils.isNotBlank(totalName)) {
            groupDateSql = groupDateSql + ",total_name";
        }
        if (StringUtils.isNotBlank(adId)) {
            groupDateSql = groupDateSql + ",ad_space_id";
        }
        return groupDateSql;
    }

    /**
     * 按小时数据明细折线图
     *
     * @param date        日期，多个的时候逗号分隔
     * @param appType     app类型：1=西柚视频,2=炫来电(默认值为1)
     * @param statItem    统计项,1-展现量,2-点击量，3-点击率，4-点击单价，5-cpm，6-营收，7-人均展现数，8-日活均价值，9-展示率，10-报错率，11-广告填充率
     * @param appClientId 应用id(下拉列表取appClientList)
     * @param platForm    平台id
     * @param totalName   广告位置名称
     * @param adId        代码为id
     * @return
     */
    public List<LineChartDto> countLineHourChart(String date, Integer appType, String statItem, String appClientId, String platForm, String totalName, String adId) {
        Map<String, Object> params = getHourLineParams(date, appType, statItem, appClientId, platForm, totalName, adId);
        return dataDetailMapper.countLineHourChart(params);
    }

    private Map<String, Object> getHourLineParams(String date, Integer appType, String statItem, String appClientId, String platForm, String totalName, String adId) {
        Map<String, Object> params = new HashMap<>();
        params.put("statItmes", statItem.split(","));
//        if (statItem.contains(",")) {
//            params.put("statByItem", "0");
//        }
        params.put("appType", appType);
        params.put("date", "'" + String.join("','", date.split(",")) + "'");
        String createTypeSql = createTypeSql(date, statItem, appClientId, platForm, totalName, adId);
        if(StringUtils.isBlank(createTypeSql)) {
            params.put("statByItem", "0");
        }
        params.put("typeSql", createTypeSql);  //type字段的sql
        params.put("groupSql", groupDateSql(0, appClientId, platForm, totalName, adId));    //groupsql的sql
        if (StringUtils.isNotBlank(appClientId)) {
            params.put("appClientIds", "'" + String.join("','", appClientId.split(",")) + "'");
        }
        if (StringUtils.isNotBlank(platForm)) {
            params.put("platForms", "'" + String.join("','", platForm.split(",")) + "'");
        }
        if (StringUtils.isNotBlank(totalName)) {
            params.put("totalNames", "'" + String.join("','", totalName.split(",")) + "'");
        }
        if (StringUtils.isNotBlank(adId)) {
            params.put("adIds", "'" + String.join("','", adId.split(",")) + "'");
        }
        return params;
    }

    /**
     * 创建折线图type的sql（日期，统计项，查询条件3个只能有一个多选，谁多选那就谁的值作为type）
     *
     * @param date
     * @param statItem
     * @param appClientId
     * @param platForm
     * @param totalName
     * @param adId
     * @return
     */
    private String createTypeSql(String date, String statItem, String appClientId, String platForm, String totalName, String adId) {
        String type = "";
        if(date.contains(",")) {
            type = "concat(replace(e.date,'-',''),'-',";
        } else {
            type = "concat(";
        }
        String midType = "";
        if (StringUtils.isNotBlank(adId)) {
            //代码位多选
            return type + "ad_space_id,'-',";
        }
        if (StringUtils.isNotBlank(totalName) && totalName.contains(",")) {
            //广告位多选(多选，则折线图名字需要)
            midType = "total_name,'-',";
        }
        if (StringUtils.isNotBlank(platForm) && platForm.contains(",")) {
            //平台多选(多选，则折线图名字需要)
            midType = midType + "(case plat_form when 1 then '穿山甲' when 2 then '广点通' when 3 then '快手' end),'-',";
        }
        if (StringUtils.isNotBlank(appClientId) && appClientId.contains(",")) {
            //平台多选(多选，则折线图名字需要)
            midType = midType + "cut_app_name,if(client_id=1, 'Android', 'ios'), '-',";
        }
        type = type + midType;
        return type;
    }

    /**
     * 保存用户自定义列
     * @param token
     * @param appType
     * @param ids
     */
    public void saveHeadField(String token, Integer appType, String ids) {
        dataDetailMapper.deleteUserFields(token, appType);
        if(StringUtils.isNotBlank(ids)) {
            Map<String, Object> params = new HashMap<>();
            String[] idArray = ids.split(",");
            params.put("token", token);
            params.put("appType", appType);
            params.put("ids", idArray);
            dataDetailMapper.saveUserFields(params);
        }
    }
}
