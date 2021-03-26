package com.miguan.reportview.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miguan.reportview.entity.App;
import com.miguan.reportview.mapper.AppMapper;
import com.miguan.reportview.service.IAppService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * <p>
 * 应用 服务实现类
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
@Service
@DS("ad-db")
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements IAppService {
    private ConcurrentMap<String, App> apps = new ConcurrentHashMap<>();
    private Object lock = new Object();

    @PostConstruct
    public void init() {
        if (CollectionUtils.isEmpty(apps)) {
            synchronized (lock) {
                if (CollectionUtils.isEmpty(apps)) {
                    try {
                        LambdaQueryChainWrapper<App> qw = this.lambdaQuery().select(App::getAppPackage, App::getName, App::getMobileType).orderByAsc(App::getSort);
                        DynamicDataSourceContextHolder.push("ad-db");
                        List<App> qaaps = list(qw.getWrapper());
                        qaaps.forEach(app->{
                            if("xld".equals(app.getAppPackage())) {
                                app.setAppPackage("com.mg.phonecall");
                            }
                        });
                        if (!CollectionUtils.isEmpty(qaaps)) {
                            apps = qaaps.stream().collect(Collectors.toConcurrentMap(App::getAppPackage, d -> d));

                        }
                    } catch (Exception e) {
                        log.error("初始化app缓存失败", e);
                    } finally {
                        DynamicDataSourceContextHolder.poll();
                    }

                }
            }
        }
    }


    @Override
    public void sysnApp() {
        apps = null;
        init();
    }

    @Override
    public List<App> getApps() {
        return apps == null ? null : apps.values().stream().collect(Collectors.toList());
    }


    @Override
    public App getAppByPackageName(String appPackage) {
        if (StringUtils.isBlank(appPackage)) {
            return null;
        }
        return apps == null ? null : apps.get(appPackage);
    }


}
