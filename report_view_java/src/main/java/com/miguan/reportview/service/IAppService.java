package com.miguan.reportview.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miguan.reportview.entity.App;

import java.util.List;

/**
 * <p>
 * 应用 服务类
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
public interface IAppService extends IService<App> {
     void sysnApp();
    List<App> getApps();

    App getAppByPackageName(String appPackage);
}
