package com.miguan.reportview.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.miguan.reportview.entity.DwAdErrorsModelDict;
import com.miguan.reportview.entity.DwAppVersionChannelDict;
import com.miguan.reportview.mapper.DwAdErrorsModelDictMapper;
import com.miguan.reportview.mapper.DwAppVersionChannelDictMapper;
import com.miguan.reportview.service.ICickhouseCommonService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhongli
 * @date 2020-08-06 
 *
 */
@Service
@DS("clickhouse")
public class CickhouseCommonServiceImpl implements ICickhouseCommonService {
    @Resource
    private DwAppVersionChannelDictMapper dwAppVersionChannelDictMapper;
    @Resource
    private DwAdErrorsModelDictMapper dwAdErrorsModelDictMapper;

    @Override
    public List<Object> getModel() {
        QueryWrapper<DwAdErrorsModelDict> wrap = Wrappers.<DwAdErrorsModelDict>query().select("distinct model");
        return dwAdErrorsModelDictMapper.selectObjs(wrap);
    }

    @Override
    public List<Object> getParentChannelDist(Integer appType) {
        if(appType == 1) {
            QueryWrapper<DwAppVersionChannelDict> wrap = Wrappers.<DwAppVersionChannelDict>query().select("distinct father_channel");
            return dwAppVersionChannelDictMapper.selectObjs(wrap);
        } else {
            return dwAppVersionChannelDictMapper.getLdParentChannelDist();
        }
    }

    @Override
    public List<DwAppVersionChannelDict> getSubChannelDist(String parent, Integer appType) {
        if(appType == 1) {
            QueryWrapper<DwAppVersionChannelDict> wrap = Wrappers.<DwAppVersionChannelDict>query().select("channel, father_channel")
                    .eq("father_channel", parent).isNotNull("channel").ne("channel","");
            return dwAppVersionChannelDictMapper.selectList(wrap);
        } else {
            return dwAppVersionChannelDictMapper.getLdSubChannelDist(parent);
        }
    }

    @Override
    public List<DwAppVersionChannelDict> getAllSubChannel(Integer appType) {
        if(appType == 1) {
            QueryWrapper<DwAppVersionChannelDict> wrap = Wrappers.<DwAppVersionChannelDict>query()
                    .select("father_channel, channel").isNotNull("channel")
                    .ne("channel","").groupBy("father_channel, channel");
            return dwAppVersionChannelDictMapper.selectList(wrap);
        } else {
            return dwAppVersionChannelDictMapper.getAllSubChannel();
        }
    }
}
