package com.miguan.reportview.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miguan.reportview.entity.AdPlat;
import com.miguan.reportview.mapper.AdPlatMapper;
import com.miguan.reportview.service.IAdPlatService;
import org.springframework.stereotype.Service;
import tool.util.StringUtil;

import java.util.List;

/**
 * <p>
 * 广告平台 服务实现类
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
@Service
@DS("ad-db")
public class AdPlatServiceImpl extends ServiceImpl<AdPlatMapper, AdPlat> implements IAdPlatService {

    @Override
    public List<AdPlat> getAllPlat() {
        AbstractWrapper wrap = this.lambdaQuery().select(AdPlat::getAdvName, AdPlat::getPlatKey).getWrapper();
        return this.list(wrap);
    }

    /**
     * 根据广告key 获取广告平台名称
     * @param list
     * @param platKey
     * @return
     */
    public String getPlatName(List<AdPlat> list, String platKey) {
        if(list == null || list.isEmpty() || StringUtil.isBlank(platKey)) {
            return "";
        }
        for(AdPlat plat : list) {
            if(platKey.equals(plat.getPlatKey())) {
                return plat.getAdvName();
            }
        }
        return "";
    }
}
