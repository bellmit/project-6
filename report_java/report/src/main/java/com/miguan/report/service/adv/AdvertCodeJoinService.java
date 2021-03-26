package com.miguan.report.service.adv;

import com.miguan.report.dto.AdIdAndNameDto;
import com.miguan.report.dto.AdvertCodeJoinDto;

import java.util.List;
import java.util.Map;

/**
 * 代码位联合查询
 */
public interface AdvertCodeJoinService {

    /**
     * 代码位联合查询
     * @param totalName 广告位名称
     * @param mobileType  手机类型：1 IOS， 2 Android
     * @param packageName 包名
     * @param optionValues 序号
     * @return
     */
    public List<AdvertCodeJoinDto> advertCodeQuery(List<Integer> mobileType, List<String> packageName, List<String> totalName, List<Integer> optionValues);

    /**
     * 查询定时任务数据(每日定时将广告库的数据，读取到报表库)
     * @return
     */
    public List<AdvertCodeJoinDto> queryTaskData();

    /**
     * 查询所有代码位id和对应的代码位名称
     * @return
     */
    public Map<String, AdIdAndNameDto> queryAdIdAndName(int appType);
}
