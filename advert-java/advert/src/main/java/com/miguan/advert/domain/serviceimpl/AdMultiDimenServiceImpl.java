package com.miguan.advert.domain.serviceimpl;

import com.cgcg.context.util.StringUtils;
import com.miguan.advert.common.util.DateUtils;
import com.miguan.advert.config.dynamicquery.DynamicQuery;
import com.miguan.advert.domain.service.AdMultiDimenService;
import com.miguan.advert.domain.vo.interactive.AdMultiDimenVo;
import com.miguan.advert.domain.vo.interactive.AdProfitVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class AdMultiDimenServiceImpl implements AdMultiDimenService {

    @Resource
    private DynamicQuery dynamicQuery;


    /**
     * 获取代码位统计数据
     * @param adId
     * @param packageName
     * @param channelId
     * @param userType
     * @param city
     * @return
     */
    public AdMultiDimenVo getAdReport(String adId, String packageName, String channelId, int userType, String city, String date) {
        if (StringUtils.isEmpty(adId) || StringUtils.isEmpty(packageName) || date == null) {
            return null;
        }

        channelId = StringUtils.isEmpty(channelId) ? "-1" : channelId;
        city = StringUtils.isEmpty(city) ? "-1" : city;

        StringBuffer sql = new StringBuffer("select ad_id,package_name,channel,is_new,city,click,exposure,valid_click,`show` from ad_multi_dimensional_data ");
        sql.append(" where ad_id='");
        sql.append(adId);
        sql.append("' and package_name ='");
        sql.append(packageName);
        sql.append("' and date ='");
        sql.append(date);
        sql.append("' and channel ='");
        sql.append(channelId);
        sql.append("' and is_new =");
        sql.append(userType);
        sql.append(" and city ='");
        sql.append(city);
        sql.append("' limit 1");
        List<AdMultiDimenVo> result = dynamicQuery.nativeQueryList(AdMultiDimenVo.class, sql.toString());
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }
}
