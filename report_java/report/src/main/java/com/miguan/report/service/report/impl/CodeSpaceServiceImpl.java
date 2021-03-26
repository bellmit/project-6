package com.miguan.report.service.report.impl;

import com.miguan.expression.util.StringUtils;
import com.miguan.report.common.enums.AdvChannelEnum;
import com.miguan.report.common.enums.ClientEnum;
import com.miguan.report.dto.BannerDataDto;
import com.miguan.report.mapper.BannerDataMapper;
import com.miguan.report.service.laidian.AdvErrorService4Laidian;
import com.miguan.report.service.report.CodeSpaceService;
import com.miguan.report.service.video.AdErrorService4Video;
import com.miguan.report.vo.AdErrorVo;
import com.miguan.report.vo.CodeSpaceDataVo;
import com.miguan.report.vo.CodeSpaceVo;
import com.miguan.report.vo.PageInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tool.util.BigDecimalUtil;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class CodeSpaceServiceImpl implements CodeSpaceService {

    @Resource
    private AdErrorService4Video adErrorService4Video;
    @Resource
    private AdvErrorService4Laidian advErrorService4Laidian;
    @Resource
    private BannerDataMapper bannerDataMapper;

    /**
     * 获取代码位分析列表(导出功能)
     *
     * @param startDdate
     * @param endDdate
     * @param packageNames
     * @param clientType
     * @param codeSpaceNames
     * @param serialNums
     * @return
     */
    @Override
    public List getAnalyzeListNew(String reportType, String startDdate, String endDdate,
                                  List<String> packageNames, List<Integer> clientType,
                                  List<String> codeSpaceNames, List<Integer> serialNums,
                                  int pageNum, int pageSize) {
        long count = bannerDataMapper.findForCodeSpaceReportListCount(startDdate, endDdate, packageNames, clientType, codeSpaceNames, serialNums);
        pageSize = Long.valueOf(count).intValue();
        List<BannerDataDto> pageList = bannerDataMapper.findForCodeSpaceReportList(startDdate, endDdate, packageNames, clientType, codeSpaceNames, serialNums, 0, pageSize);
        return packAgeShowVo(pageList);
    }

    /**
     * 获取代码位分析列表(展示列表)
     *
     * @param reportType
     * @param startDdate
     * @param endDdate
     * @param packageNames
     * @param clientType
     * @param codeSpaceNames
     * @param serialNums
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfoVo<CodeSpaceVo> getAnalyzeListNew4Page(String reportType, String startDdate, String endDdate, List<String> packageNames, List<Integer> clientType, List<String> codeSpaceNames, List<Integer> serialNums, int pageNum, int pageSize) {
        int poffset = (pageNum - 1) * pageSize;
        //查询总数量
        long count = bannerDataMapper.findForCodeSpaceReportListCount(startDdate, endDdate, packageNames, clientType, codeSpaceNames, serialNums);
        //总页数
        int totalPageNum = (Long.valueOf(count).intValue() + pageSize - 1) / pageSize;
        if (poffset > count) {
            //取最后一页
            poffset = (totalPageNum - 1) * pageSize;
        }

        List<BannerDataDto> pageList = bannerDataMapper.findForCodeSpaceReportList(startDdate, endDdate, packageNames, clientType, codeSpaceNames, serialNums, poffset, pageSize);

        List<CodeSpaceVo> showVoList = packAgeShowVo(pageList);

        PageInfoVo<CodeSpaceVo> pageInfoVo = new PageInfoVo<>();
        pageInfoVo.setLastPage(poffset + pageSize >= count ? true : false);
        pageInfoVo.setFirstPage(pageNum == 1 ? true : false);
        pageInfoVo.setDatalist(showVoList);
        pageInfoVo.setPageNum(pageNum);
        pageInfoVo.setPageSize(pageSize);
        //当前页数量
        pageInfoVo.setSize(showVoList == null ? 0 : showVoList.size());
        //总数量
        pageInfoVo.setTotal(count);
        pageInfoVo.setPages(totalPageNum);
        return pageInfoVo;
    }

    /**
     * 获取代码位错误明细
     *
     * @param date      日期
     * @param codeSpace 代码位
     * @return
     */
    @Override
    public List<AdErrorVo> getCodeSpaceErrorDetail(String detailType, String date, String codeSpace) {
        String queryDate = date.replaceAll("-", "");
        List<AdErrorVo> resultList = null;
        try {
            switch (detailType) {
                case "2" :
                    resultList = advErrorService4Laidian.findAdError(queryDate, codeSpace);
                    break;
                default:
                    resultList = adErrorService4Video.findAdError(queryDate, codeSpace);
            }

            resultList.stream().forEach(t -> {
                t.setErrorRate(BigDecimalUtil.decimal(t.getErrorRate() * 100, 2));
            });
        } catch (Exception e) {
            log.info("代码位错误明细异常>>{}", e.getMessage());
        }
        return resultList;
    }

    /**
     * 将数据库数据，包装成页面展示实体
     *
     * @param dataList
     * @return
     */
    private List<CodeSpaceVo> packAgeShowVo(List<BannerDataDto> dataList) {
        // 根据广告库查询的代码位结果中的广告位名称、代码位ID, 从报表库中查询剩余数据
        // 根据APP、广告位组装返回结果
        Map<String, List<CodeSpaceDataVo>> resultVoMap = new LinkedHashMap<String, List<CodeSpaceDataVo>>();
        for (BannerDataDto data : dataList) {
            String date = data.getDate();
            String appName = data.getCutAppName();
            ClientEnum clientEnum = ClientEnum.getById(data.getClientId());
            String totalName = data.getTotalName();
            String key = date + "&" + appName + "&" + clientEnum.getId() + "&" + totalName;

            CodeSpaceDataVo vo = packageDataVo(data);
            if (resultVoMap.containsKey(key)) {
                resultVoMap.get(key).add(vo);
            } else {
                List<CodeSpaceDataVo> dataVoList = new ArrayList<CodeSpaceDataVo>();
                dataVoList.add(vo);
                resultVoMap.put(key, dataVoList);
            }
        }

        if (!resultVoMap.isEmpty()) {
            List<CodeSpaceVo> resultVoList = new ArrayList<CodeSpaceVo>();
            Set<String> keySet = resultVoMap.keySet();
            for (String key : keySet) {
                List<CodeSpaceDataVo> dataVoList = resultVoMap.get(key);
                dataVoList.sort(Comparator.comparing(CodeSpaceDataVo::getOptionValue));

                // 计算广告位汇总数据
                Integer adRequestCount = 0;
                Integer adReturnCount = 0;
                Integer showNumberCount = 0;
                Integer clickNumberCount = 0;
                Double cpmCount = 0.0d;
                Double earningCount = 0.0d;
                Integer errorNumberCount = 0;
                Integer requestNumerCount = 0;
                for (CodeSpaceDataVo dataVo : dataVoList) {
                    adRequestCount += dataVo.getAdRequest();
                    adReturnCount += dataVo.getAdReturn();
                    showNumberCount += dataVo.getShowNumber();
                    clickNumberCount += dataVo.getClickNumber();
                    cpmCount += dataVo.getCpm();
                    earningCount = BigDecimalUtil.add(earningCount, dataVo.getEarnings());
                    requestNumerCount += dataVo.getRequestNumber();
                    errorNumberCount += dataVo.getErrorNumber();
                }
                CodeSpaceDataVo countDate = this.getCountCodeSpaceDateVo(adRequestCount, adReturnCount, showNumberCount, clickNumberCount,
                        cpmCount, earningCount, errorNumberCount, requestNumerCount);
                dataVoList.add(countDate);

                String[] keySplits = key.split("&");
                String date = keySplits[0];
                String appName = keySplits[1];
                String clientId = keySplits[2];
                String totalName = keySplits[3];

                CodeSpaceVo codeSpaceVo = getCodeSpaceVo(date, appName, ClientEnum.getById(Integer.valueOf(clientId)).getName(), totalName, dataVoList);
                resultVoList.add(codeSpaceVo);

            }
            return resultVoList;
        }
        return null;
    }

    /**
     * 获取广告位汇总展示数据
     *
     * @param adRequestCount    广告总请求数
     * @param adReturnCount     广告总返回数
     * @param showNumberCount   总展示数
     * @param clickNumberCount  总点击数
     * @param cpmCount          总cpm
     * @param earningCount      总收益
     * @param errorNumberCount  总错误数
     * @param requestNumerCount 总请求数
     * @return
     */
    private CodeSpaceDataVo getCountCodeSpaceDateVo(Integer adRequestCount, Integer adReturnCount, Integer showNumberCount, Integer clickNumberCount,
                                                    Double cpmCount, Double earningCount, Integer errorNumberCount, Integer requestNumerCount) {
        CodeSpaceDataVo dataVo = new CodeSpaceDataVo();
        dataVo.setOptionValue(0);
        dataVo.setAdRequest(adRequestCount);
        dataVo.setAdReturn(adReturnCount);
        dataVo.setAdFilling(this.divideAndMultiply4Rate(adReturnCount, adRequestCount));
        dataVo.setShowNumber(showNumberCount);
        dataVo.setShowNumberRate(this.divideAndMultiply4Rate(showNumberCount, adReturnCount));
        dataVo.setClickNumber(clickNumberCount);
        dataVo.setClickRate(this.divideAndMultiply4Rate(clickNumberCount, showNumberCount));
        dataVo.setEarnings(earningCount);
        dataVo.setCpm(this.divideAndMultiply4Cpm(earningCount, showNumberCount));
        dataVo.setRequestNumber(requestNumerCount);
        dataVo.setErrorNumber(errorNumberCount);
        dataVo.setErrorRate(this.divideAndMultiply4Rate(errorNumberCount, requestNumerCount));
        return dataVo;
    }

    /**
     * 包装成广告位展示实体
     *
     * @param date
     * @param appName
     * @param clientName
     * @param totalName
     * @param dataVoList
     * @return
     */
    private CodeSpaceVo getCodeSpaceVo(String date, String appName, String clientName, String totalName, List<CodeSpaceDataVo> dataVoList) {
        CodeSpaceVo codeSpaceVo = new CodeSpaceVo();
        codeSpaceVo.setDate(date);
        codeSpaceVo.setCut_app_name(appName + "_" + clientName);
        codeSpaceVo.setTotal_name(totalName);
        codeSpaceVo.setCode(dataVoList);
        return codeSpaceVo;
    }

    /**
     * 包装成代码位展示实体
     *
     * @param bannerData
     * @return
     */
    public CodeSpaceDataVo packageDataVo(BannerDataDto bannerData) {
        CodeSpaceDataVo dataVo = new CodeSpaceDataVo();
        dataVo.setAdSpaceId(bannerData.getAdSpaceId());
        dataVo.setPrice(StringUtils.isBlank(bannerData.getLadderPrice()) ? 0d : Double.parseDouble(bannerData.getLadderPrice()));
        dataVo.setChannelType(bannerData.getChannelType());
        dataVo.setChannelTypeStr(bannerData.getChannelType() == null ? null : AdvChannelEnum.getById(bannerData.getChannelType()).getName());
        dataVo.setOptionValue(bannerData.getOptionValue());
        dataVo.setAdRequest(bannerData.getAdRequest() == null ? 0 : bannerData.getAdRequest());
        dataVo.setAdReturn(bannerData.getAdReturn() == null ? 0 : bannerData.getAdReturn());
        dataVo.setAdFilling(this.getDoubleValueOfScale2(bannerData.getAdFilling()));
        dataVo.setShowNumber(bannerData.getShowNumber() == null ? 0 : bannerData.getShowNumber());
        Double showNumberRate = 0.0d;
        if (dataVo.getAdReturn() > 0) {
            showNumberRate = this.divideAndMultiply4Rate(bannerData.getShowNumber(), bannerData.getAdReturn());
        }
        dataVo.setShowNumberRate(showNumberRate);
        dataVo.setClickNumber(bannerData.getClickNumber() == null ? 0 : bannerData.getClickNumber());
        dataVo.setClickRate(this.getDoubleValueOfScale2(bannerData.getClickRate()));
        dataVo.setEarnings(this.getDoubleValueOfScale2(bannerData.getProfit()));
        dataVo.setCpm(this.getDoubleValueOfScale2(bannerData.getCpm()));
        dataVo.setRequestNumber(bannerData.getReqNum() == null ? 0 : bannerData.getReqNum());
        dataVo.setErrorNumber(bannerData.getErrNum() == null ? 0 : bannerData.getErrNum());
        dataVo.setErrorRate(this.getDoubleValueOfScale2(bannerData.getErrRate()));
        dataVo.setComputer(bannerData.getComputer());
        return dataVo;
    }

    /**
     * (numberA /numberB) * 100
     *
     * @param numberA
     * @param numberB
     * @param numberB
     * @return
     */
    private Double divideAndMultiply4Rate(int numberA, int numberB) {
        if (numberB == 0) {
            return 0.0d;
        }
        return new BigDecimal(numberA).divide(new BigDecimal(numberB), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue();
    }

    /**
     * (numberA /numberB) * 1000
     *
     * @param numberA
     * @param numberB
     * @param numberB
     * @return
     */
    private static Double divideAndMultiply4Cpm(double numberA, int numberB) {
        if (numberB == 0) {
            return 0.0d;
        }
        return new BigDecimal(numberA).divide(new BigDecimal(numberB), 5, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(1000)).doubleValue();
    }

    /**
     * 保留2位小数
     *
     * @param number
     * @return
     */
    private Double getDoubleValueOfScale2(BigDecimal number) {
        if (number == null) {
            return 0.0d;
        }
        return number.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
    }
}
