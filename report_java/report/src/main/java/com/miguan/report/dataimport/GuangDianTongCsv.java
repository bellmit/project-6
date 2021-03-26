package com.miguan.report.dataimport;

import com.alibaba.fastjson.JSON;
import com.miguan.report.common.util.AppNameUtil;
import com.miguan.report.common.util.DateUtil;
import com.miguan.report.entity.BannerData;
import com.miguan.report.entity.BannerDataTotalName;
import com.miguan.report.entity.BannerRule;
import com.miguan.report.mapper.ImportMapper;
import com.miguan.report.repository.BannerRuleRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.transaction.NotSupportedException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static com.miguan.report.common.util.NumCalculationUtil.roundHalfUpDouble;

/**
 * @author zhongli
 * @date 2020-06-30 
 *
 */
@Component
@Slf4j
public class GuangDianTongCsv implements ImportInterface {


    @Resource
    private BannerRuleRepository bannerRuleRepository;
    @Resource
    private ImportMapper importMapper;

    @Override
    public void doXlsxImport(XSSFWorkbook workbook) {

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doCsvImport(InputStreamReader reader) throws IOException {

        CSVParser records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
        // ["0时间","1媒体","2媒体ID","3广告位","4广告位ID","5渲染方式","6广告展示数",
        // 7"点击量","8预计收入","9千次展示收益CPM","10点击率","11广告位请求量","12广告位返回量",
        // "13曝光率","14广告请求量","15广告返回量"]
        int platForm = 2;
        Date date = null;
        String dateS = null;
        Date createDate = new Date();
        List<BannerData> list = new ArrayList<>();
        int rowNum = 1;
        int cellNum = 0;
        try {
            for (CSVRecord record : records) {
                cellNum = 0;
                String date0 = getStringValue(record, cellNum++);
                if (StringUtils.isBlank(date0)) {
                    continue;
                }
                if (date == null) {
                    boolean b = DateUtil.isYyyy_MM_dd(date0);
                    if (!b) {
                        throw new RuntimeException("第2行为数据起始行，日期格式要为 'yyyy_MM_dd'");
                    }
                    date = DateUtil.strToDate(date0, "yyyy-MM-dd");
                    dateS = date0;
                }
                rowNum++;
                if (!dateS.equals(date0)) {
                    throw new NotSupportedException(String.format("第%d行日期跟第2行日期不匹配，同批次导入只能导入同一日期的数据", rowNum));
                }
                BannerData entity = new BannerData();

                //旧 时间	媒体	媒体ID	广告展示数	点击量	预计收入	千次展示收益	点击率	广告位请求量	广告位返回量	曝光率	广告请求量	广告返回量
                //新 日期	广告位名称	广告位ID	预估收入(元)	千次展示收入(元)	广告位请求量	广告位返回量	广告请求量	广告返回量	曝光量	点击量	广告位曝光率	点击率
                //日期  app名称  appID  广告位类型  广告位名称  广告位ID  预估收入(元)  千次展示收入(元)  广告位请求量  广告位返回量  广告请求量  广告返回量  曝光量  点击量  点击率  广告曝光率
                entity.setDate(date);
                entity.setAppName(getStringValue(record, cellNum++));
                entity.setAppId(getStringValue(record, cellNum++));

                entity.setAdSpaceType(getStringValue(record, cellNum++));
                entity.setAdSpace(getStringValue(record, cellNum++));
                entity.setAdSpaceId(getStringValue(record, cellNum++));
                entity.setAccessMode("");

                entity.setProfit(getDoubleValue(record, cellNum++));//预计收入
                entity.setCpm(getDoubleValue(record, cellNum++));//千次展示收益
                entity.setAdSpaceRequest(getIntergeValue(record, cellNum++));//广告位请求量
                entity.setAdSpaceReturn(getIntergeValue(record, cellNum++));//广告位返回量
                entity.setAdRequest(getIntergeValue(record, cellNum++));//广告请求量
                entity.setAdReturn(getIntergeValue(record, cellNum++));//广告返回量
                entity.setShowNumber(getIntergeValue(record, cellNum++));//广告展示数
                entity.setClickNumber(getIntergeValue(record, cellNum++));//广告点击量

                String rate = getStringValue(record, cellNum++).replace("%", "");
                entity.setClickRate(Double.valueOf(rate));//点击率
                String erate = getStringValue(record, cellNum++).replace("%", "");//曝光率
                entity.setExposureRate(Double.valueOf(erate));//曝光率






                entity.setCreatedAt(createDate);
                entity.setUpdatedAt(createDate);
                //app类型:1视频，2炫来电，3飞快清理大师，4锦鲤万年历
                int appType = 1;
                if(entity.getAppName().contains("炫来电") ){
                    appType = 2;
                }else if(entity.getAppName().contains("飞快清理") ){
                    appType = 3;
                }else if(entity.getAppName().contains("锦鲤") ){
                    appType = 4;
                }
                entity.setAppType(appType);
                //1穿山甲 2广点通 3快手
                entity.setPlatForm(platForm);
                //客户端：1安卓 2ios
                int clientId =1;
                if (Arrays.asList("1109647473","1109869225","1110490327").contains(entity.getAppId())) {
                    clientId =2;
                }
                //int clientId = entity.getAppName().toLowerCase().contains("android") ? 1 : 2;
                entity.setClientId(clientId);
                list.add(entity);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("第%d行第%d列数据处理异常: %s", rowNum, cellNum, e.getMessage()), e);
        }


        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("没有可以导入的数据");
        }
        log.debug(JSON.toJSONString(list));
        List<BannerRule> rules = bannerRuleRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        list = list.stream().map(e -> {
            Optional<BannerRule> op = rules.stream()
                    .filter(r -> r.getStatus() == 1 && r.getAppType().intValue() == e.getAppType().intValue())
                    .filter(r -> pattern(e, r.getKey()))
                    .findFirst();
            if (op.isPresent()) {
                BannerRule rule = op.get();
                String totalName = rule.getTotalName();
                String adStyle = rule.getAdStyle();
                String adType = rule.getAdType();
                e.setTotalName(totalName);
                e.setAdStyle(adStyle);
                e.setAdType(adType);
                e.setCutAppName(getAppName(e.getAppName()));
                e.setClickPrice(e.getClickNumber().intValue() == 0 ? 0 : roundHalfUpDouble(e.getProfit().doubleValue() / e.getClickNumber().doubleValue()));
                e.setRuleAdSpace(rule.getAdSpace());
                e.setRuleId(rule.getId());
                e.setAdSpaceFilling(e.getAdSpaceReturn() == null || e.getAdSpaceReturn().intValue() == 0
                        ? 0 : roundHalfUpDouble(e.getAdSpaceReturn().doubleValue() / e.getAdSpaceRequest().doubleValue() * 100));
                e.setAdFilling(e.getAdRequest() == null || e.getAdRequest().intValue() == 0
                        ? 0 : roundHalfUpDouble(e.getAdReturn().doubleValue() / e.getAdRequest().doubleValue() * 100));
                return e;
            }
            log.warn("未找到相关的广告规则 app_name='{}' ad_space='{}'", e.getAppName(), e.getAdSpace());
            return null;
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("规则校验后，没有符合规范的数据可以导入");
        }
        String dateClone = dateS;
        list = list.stream().filter(e -> e != null).collect(Collectors.toList());
        //按md5(date + total_name+ app_name + client_id +  plat_form)分组统计
        Map<String, List<BannerData>> map = list.stream().collect(Collectors.groupingBy(e -> {
            //        md5(date + total_name+ app_name + client_id +  plat_form)
            StringBuilder buf = new StringBuilder().append(dateClone).append(e.getTotalName())
                    .append(e.getCutAppName()).append(e.getClientId()).append(e.getPlatForm());
            return DigestUtils.md5Hex(buf.toString());
        }));
        //生成banner_data_total_name表数据
        List<BannerDataTotalName> listT = map.entrySet().stream().map(et -> {
            BannerData v = et.getValue().get(0);
            //点击量
            double tClickNumber = et.getValue().stream().mapToDouble(e -> e.getClickNumber().doubleValue()).sum();
            //展现量
            double tShowNumber = et.getValue().stream().mapToDouble(e -> e.getShowNumber().doubleValue()).sum();
            //营收 预估收益
            double profit = et.getValue().stream().mapToDouble(BannerData::getProfit).sum();
            BannerDataTotalName bannerDataTotalName = new BannerDataTotalName();
            bannerDataTotalName.setDate(v.getDate());
            bannerDataTotalName.setShowNumber(Double.valueOf(tShowNumber).intValue());
            bannerDataTotalName.setClickNumber(Double.valueOf(tClickNumber).intValue());
            //点击率=点击量/展现量
            bannerDataTotalName.setClickRate(tShowNumber == 0 ? 0 : roundHalfUpDouble(tClickNumber / tShowNumber));
            //点击单价=营收/点击量
            bannerDataTotalName.setClickPrice(tClickNumber == 0 ? 0 : roundHalfUpDouble(profit / tClickNumber));
            //cpm=营收/展现量*1000
            bannerDataTotalName.setCpm(tShowNumber == 0 ? 0 : roundHalfUpDouble(2, profit / tShowNumber * 1000));
            bannerDataTotalName.setRevenue(profit);
            bannerDataTotalName.setCreatedAt(createDate);
            bannerDataTotalName.setUpdatedAt(createDate);
            bannerDataTotalName.setAppName(v.getCutAppName());
            bannerDataTotalName.setAppId(String.valueOf(AppNameUtil.getAppIdForName(v.getCutAppName())));
            bannerDataTotalName.setClientId(v.getClientId());
            bannerDataTotalName.setPlatForm(v.getPlatForm());
            bannerDataTotalName.setAdStyle(v.getAdStyle());
            bannerDataTotalName.setAdType(v.getAdType());
            bannerDataTotalName.setAdSpace(v.getTotalName());
            bannerDataTotalName.setAppType(v.getAppType());
            bannerDataTotalName.setUniqueKey(et.getKey());
            return bannerDataTotalName;
        }).collect(Collectors.toList());

        importMapper.deleteBannerDataByDateAndPlatForm(dateS, platForm);
        importMapper.deleteBannerDataTotalNameByDateAndPlatForm(dateS, platForm);
        importMapper.addBannerData(list);
        importMapper.addBannerDataTotalName(listT);
    }

    private String getStringValue(CSVRecord record, int cellNum) {
        return record.get(cellNum).trim();
    }

    private Integer getIntergeValue(CSVRecord record, int cellNum) {
        return Integer.valueOf(getStringValue(record, cellNum).replaceAll(",", ""));
    }

    private double getDoubleValue(CSVRecord record, int cellNum) {
        return Double.valueOf(getStringValue(record, cellNum).replaceAll(",", ""));
    }
}
