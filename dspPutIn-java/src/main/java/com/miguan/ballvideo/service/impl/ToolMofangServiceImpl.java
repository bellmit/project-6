package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.interceptor.argument.params.CommonParamsVo;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.dynamicquery.Dynamic4Query;
import com.miguan.ballvideo.entity.ChannelGroup;
import com.miguan.ballvideo.entity.common.Channel;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.service.ToolMofangService;
import com.miguan.ballvideo.vo.SysVersionVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author laiyd 20200402
 */
@Service(value = "toolMofangService")
public class ToolMofangServiceImpl implements ToolMofangService {

    @Resource
    private Dynamic4Query dynamic4Query;

    @Resource
    private RedisService redisService;

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
        List<SysVersionVo> sysVersionVos = null;//dynamic4Query.nativeQueryList(SysVersionVo.class, nativeSql);
        return sysVersionVos;
    }


    @Override
    @Transactional(value = "transactionManager4")
    public int updateSysVersionInfo(CommonParamsVo commonParams) {
        List<SysVersionVo> updateVersionSet = findUpdateVersionSet(commonParams.getAppPackage(), commonParams.getAppVersion());
        if (!CollectionUtils.isEmpty(updateVersionSet)) {
            SysVersionVo sysVersionVo = updateVersionSet.get(0);
            String insertSql = "update app_version_set set real_user = real_user + 1" +
                    " where group_id = '" + sysVersionVo.getGroupId() + "' and app_version = '" + sysVersionVo.getAppVersion() + "'";
            return 0;
        }
        return 0;
    }

    @Override
    public List<ChannelGroup> getChannelGroups(String appPackage) {
        String nativeSql = "select a.app_type as appPackage,a.msg_sign as msgSign from channel_group a where a.status=1 and a.app_type = ?";
        List<ChannelGroup> channelGroups = null;
        return channelGroups;
    }

    @Override
    public int countVersion(String postitionType, String appVersion, String appPackage, int tagType) {
        StringBuffer sb = new StringBuffer("select count(1) from shield_version sv ");
        sb.append("inner join app_version_set v on v.id = sv.version_id ");
        sb.append("inner join channel_group g on g.s_id  = v.s_id AND sv.group_id = g.id ");
        sb.append("inner join shield_set ss on sv.shield_id = ss.id ");
        sb.append("where v.app_version = '").append(appVersion).append("' ");
        sb.append("and g.app_type = '").append(appPackage).append("' ");
        sb.append("and ss.para_num = '").append(postitionType).append("' ");
        sb.append("and g.tag_type = ").append(tagType).append(" ");
        sb.append("and type = 1");
        Object obj = dynamic4Query.nativeQueryObject(sb.toString());
        return Integer.parseInt(obj.toString());
    }

    @Override
    public int countChannel(String postitionType, String channelId, String appPackage, String appVersion, int tagType) {
        StringBuffer sb = new StringBuffer("select count(1) from shield_channel sc ");
        sb.append("inner join channel_group g on g.id = sc.group_id ");
        sb.append("inner join shield_set ss on sc.shield_id = ss.id ");
        sb.append("inner join shield_version sv on sv.id = sc.v_id ");
        sb.append("inner join app_version_set v on v.id = sv.version_id ");
        sb.append("inner join agent_users aus on aus.channel_id = sc.channel_id ");
        sb.append("inner join site si on si.agent_user_id = aus.id ");
        sb.append("where g.app_type = '").append(appPackage).append("' ");
        sb.append("and ss.para_num = '").append(postitionType).append("' ");
        sb.append("and v.app_version = '").append(appVersion).append("' ");
        sb.append("and si.domain = '").append(channelId).append("' ");
        sb.append("and g.tag_type = ").append(tagType);
        Object obj = dynamic4Query.nativeQueryObject(sb.toString());
        return Integer.parseInt(obj.toString());
    }

    /**
     * 查询魔方后台是否禁用该渠道的广告:1禁用，0非禁用
     * @param param
     * @return
     */
    @Override
    public boolean stoppedByMofang(Map<String, Object> param) {
        String appVersion = param.get("appVersion") + "";
        if (VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_240)) {
            String mobileType = param.get("mobileType") + "";
            String postitionType = param.get("poskey") + "";
            String appPackage = param.get("appPackage") + "";
            String channelId = param.get("channelId") + "";
            int tagType = Constant.IOS.equals(mobileType) ? 2 : 1;
            int count1 = countVersion(postitionType, appVersion, appPackage, tagType);
            //根据版本判断是否屏蔽全部广告
            if (count1 > 0) {
                return true;
            }
            //非全部的屏蔽根据渠道查询是否屏蔽广告
            int count2 = countChannel(postitionType, channelId, appPackage, appVersion, tagType);
            if (count2 > 0) {
                return true;
            }
        }
        return false;
    }

    @PostConstruct
    @Override
    public void ChannelInit() {
        if (redisService.exits(Channel.CHANNEL_REDIS)) {
            redisService.del(Channel.CHANNEL_REDIS);
        }
        //跨库查询魔方后台数据，获取渠道id和供应商
        String nativeSql = "SELECT au.channel_id as channelId,s.domain as domain from site s LEFT JOIN agent_users au on s.agent_user_id  = au.id ";
        //List<Channel> channels = null;
        //Map<String, String> collect = channels.stream().collect(Collectors.toMap(Channel::getDomain,Channel::getChannelId));
        //redisService.hmset(Channel.CHANNEL_REDIS,null);
    }
}
