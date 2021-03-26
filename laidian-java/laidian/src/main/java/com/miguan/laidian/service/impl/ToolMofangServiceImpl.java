package com.miguan.laidian.service.impl;

import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.dynamicquery.Dynamic2Query;
import com.miguan.laidian.service.ToolMofangService;
import com.miguan.laidian.vo.SysVersionVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chenwf
 * @date 2020/5/19
 */
@Service
public class ToolMofangServiceImpl implements ToolMofangService {
    @Resource
    private Dynamic2Query dynamic2Query;

    /**
     * 上报版本更新人数
     *
     * @param commonParams
     * @return
     */
    @Override
    @Transactional(value = "transactionManager2")
    public int updateSysVersionInfo(CommonParamsVo commonParams) {
        List<SysVersionVo> updateVersionSet = findUpdateVersionSet(commonParams.getAppType(), commonParams.getAppVersion());
        if (!CollectionUtils.isEmpty(updateVersionSet)) {
            SysVersionVo sysVersionVo = updateVersionSet.get(0);
            String insertSql = "update app_version_set set real_user = real_user + 1" +
                    " where group_id = '" + sysVersionVo.getGroupId() + "' and app_version = '" + sysVersionVo.getAppVersion() + "'";
            return dynamic2Query.nativeExecuteUpdate(insertSql);
        }
        return 0;
    }

    /**
     * 获取系统版本配置信息
     *
     * @param appPackage
     * @param appVersion
     * @return
     */
    @Override
    public List<SysVersionVo> findUpdateVersionSet(String appPackage, String appVersion) {
        String nativeSql = "select a.app_version as appVersion,a.group_id as groupId," +
                "a.remarks as updateContent,a.url as appAddress,a.channel,a.update_user as updateUserTotalCount,a.real_user as realUserUpdateCount," +
                "(case when c.tag_type=1 then '2' else '1' end) as mobileType," +
                "(case when c.update_type=1 then '20' else '10' end) as forceUpdate " +
                "from app_version_set a " +
                "LEFT JOIN subject s on a.s_id=s.id " +
                "LEFT JOIN channel_group c on a.group_id=c.id " +
                "where a.update_sub=1 and c.app_type = '" + appPackage + "' and a.app_version = '" + appVersion + "'";
        List<SysVersionVo> sysVersionVos = dynamic2Query.nativeQueryList(SysVersionVo.class, nativeSql);
        return sysVersionVos;
    }
}
