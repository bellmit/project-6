package com.miguan.reportview.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miguan.reportview.entity.DwAppVersionDict;
import com.miguan.reportview.mapper.DwAppVersionDictMapper;
import com.miguan.reportview.service.IAppVersionSetService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * APP版本配置表 服务实现类
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
@Service
@DS("clickhouse")
public class AppVersionSetServiceImpl extends ServiceImpl<DwAppVersionDictMapper, DwAppVersionDict> implements IAppVersionSetService {

    @Resource
    private DwAppVersionDictMapper dwAppVersionDictMapper;

    @Override
    public List<String> getAppVersion(int appType) {
        if(appType == 1) {
            QueryWrapper<DwAppVersionDict> query = Wrappers.<DwAppVersionDict>query().select("DISTINCT app_version").orderByAsc("app_version");
            return this.listObjs(query, Object::toString);
        } else {
            return dwAppVersionDictMapper.getLdAppVersion();
        }

    }
}
