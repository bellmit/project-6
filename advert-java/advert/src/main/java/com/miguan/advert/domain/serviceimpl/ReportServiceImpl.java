package com.miguan.advert.domain.serviceimpl;

import com.cgcg.context.util.StringUtils;
import com.miguan.advert.config.dynamicquery.Dynamic3Query;
import com.miguan.advert.domain.service.ReportService;
import com.miguan.advert.domain.vo.interactive.AdCpmVo;
import com.miguan.advert.domain.vo.interactive.AdProfitVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author laiyd 20200927
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Resource
    private Dynamic3Query dynamic3Query;

    @Override
    public int countChannelCost() {
        StringBuffer sb = new StringBuffer("select count(1) from dm_channel_cost sv ");
        Object obj = dynamic3Query.nativeQueryObject(sb.toString());
        return Integer.parseInt(obj.toString());
    }

    /**
     * 查询当天穿山甲，广点通，快手的cpm数据是否已经全部导入完毕
     * @param date
     * @return
     */
    public boolean isAllPlatFormReady(String date) {
        StringBuffer sb = new StringBuffer("select count(distinct plat_form) from banner_data where date='" + date + "'");
        Object obj = dynamic3Query.nativeQueryObject(sb.toString());
        int count = Integer.parseInt(obj.toString());
        if(count == 3) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询代码位的cpm
     * @param date
     * @return
     */
    public List<AdCpmVo> listAdCpmList(String date) {
        StringBuffer sb = new StringBuffer("select date, ad_space_id ad_id, cpm from banner_data where date='" + date + "'");
        return dynamic3Query.nativeQueryList(AdCpmVo.class, sb.toString());
    }

    /**
     * 获取代码位最新历史收益
     *
     * @param adCode
     * @return
     */
    public AdProfitVo getAdCodeLastProfit(String adCode, String date) {
        if (StringUtils.isEmpty(adCode) || StringUtils.isEmpty(date) ) {
            return null;
        }
        StringBuffer sql = new StringBuffer("select ad_space_id as ad_id,profit from banner_data where ad_space_id = '");
        sql.append(adCode);
        sql.append("' and date='");
        sql.append(date);
        sql.append("' limit 1");
        List<AdProfitVo> result = dynamic3Query.nativeQueryList(AdProfitVo.class, sql.toString());
        if (!CollectionUtils.isEmpty(result)) {
            return result.get(0);
        }
        return null;
    }

    @Override
    public String getCompletePlatDataLastDate(int platCount) {
        StringBuffer sb = new StringBuffer("select date from banner_data group by date having count(DISTINCT plat_form) = " + platCount + " order by date desc limit 1");
        try {
            Object obj = dynamic3Query.nativeQueryObject(sb.toString());
            return obj.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
