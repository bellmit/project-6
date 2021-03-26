package com.miguan.reportview.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miguan.reportview.entity.DwAppVersionDict;

import java.util.List;

/**
 * <p>
 * APP版本配置表 服务类
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
public interface IAppVersionSetService extends IService<DwAppVersionDict> {

    /**
     * 获取app版本号
     * @param appType 1:视频，2：来电
     * @return
     */
    List<String> getAppVersion(int appType);
}
