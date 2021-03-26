package com.miguan.advert.domain.serviceimpl;

import com.cgcg.context.util.StringUtils;
import com.miguan.advert.common.util.DateUtils;
import com.miguan.advert.common.util.StringUtil;
import com.miguan.advert.config.dynamicquery.DynamicDspQuery;
import com.miguan.advert.domain.service.DspReportService;
import com.miguan.advert.domain.vo.interactive.AdCpmVo;
import com.miguan.advert.domain.vo.interactive.AdProfitVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Date;

@Service
public class DspReportServiceImpl implements DspReportService {

    @Resource
    private DynamicDspQuery dynamicDspQuery;


    /**
     * 获取代码位最新历史收益
     *
     * @param adCode
     * @param date
     * @return
     */
    public AdProfitVo getAdCodeLastProfit(String adCode, String date) {
        if (StringUtils.isEmpty(adCode) || StringUtil.isEmpty(date)) {
            return null;
        }

        StringBuffer sql = new StringBuffer("select ad_id,sum(actual_consumption) as profit from idea_advert_report ");
        sql.append(" where date='");
        sql.append(date);
        sql.append("' and ad_id ='");
        sql.append(adCode);
        sql.append("' limit 1");
        List<AdProfitVo> result = dynamicDspQuery.nativeQueryList(AdProfitVo.class, sql.toString());
        if (!CollectionUtils.isEmpty(result)) {
            AdProfitVo vo = result.get(0);
            if (vo.getAdId() != null) {
                return vo;
            }
        }
        return null;
    }

    /**
     * 查询98代码位的cpm
     * @param date
     * @return
     */
    public List<AdCpmVo> listAd98CpmList(String date) {
        StringBuffer sb = new StringBuffer();
        sb.append("select date,ad_id, ");
        sb.append("       if(sum(exposure)=0,0,1000*sum(actual_consumption)/sum(exposure)) cpm ");
        sb.append("from idea_advert_report ");
        sb.append("where date = '" + date + "' ");
        sb.append("group by date,ad_id ");
        return dynamicDspQuery.nativeQueryList(AdCpmVo.class, sb.toString());
    }
}
