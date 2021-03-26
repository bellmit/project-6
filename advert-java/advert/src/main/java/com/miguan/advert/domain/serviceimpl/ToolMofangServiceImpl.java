package com.miguan.advert.domain.serviceimpl;

import com.google.common.collect.Lists;
import com.miguan.advert.common.constants.Constant;
import com.miguan.advert.common.util.StringUtil;
import com.miguan.advert.common.util.VersionUtil;
import com.miguan.advert.config.dynamicquery.Dynamic5Query;
import com.miguan.advert.domain.service.ToolMofangService;
import com.miguan.advert.domain.vo.ChannelInfoVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author laiyd 20200927
 */
@Service
public class ToolMofangServiceImpl implements ToolMofangService {

    @Resource
    private Dynamic5Query dynamic5Query;

    @Override
    public List<ChannelInfoVo> findChannelInfo() {
        String nativeSql = "SELECT name as channelName,channel_id as channelId  FROM  agent_users";
        List<ChannelInfoVo> channelVos = dynamic5Query.nativeQueryList(ChannelInfoVo.class, nativeSql);
        return channelVos;
    }

    @Override
    public List<ChannelInfoVo> findChannelInfoByKeys(String channelIds) {
        if(StringUtil.isEmpty(channelIds)){
            return Lists.newArrayList();
        }
        String chanIdSql = StringUtil.sqlStrChg(channelIds);
        String nativeSql = "SELECT name as channelName,channel_id as channelId  FROM  agent_users where channel_id in ("
                + chanIdSql + ")";
        List<ChannelInfoVo> channelVos = dynamic5Query.nativeQueryList(ChannelInfoVo.class, nativeSql);
        return channelVos;
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
            String postitionType = param.get("positionType") + "";
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
        Object obj = dynamic5Query.nativeQueryObject(sb.toString());
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
        Object obj = dynamic5Query.nativeQueryObject(sb.toString());
        return Integer.parseInt(obj.toString());
    }

    public List<String> findVersionInfo(String appType){
        StringBuffer sb = new StringBuffer("select app_version as value from app_version_set av ");
        sb.append("inner join channel_group g on g.id = av.group_id ");
        sb.append("where g.app_type = '").append(appType).append("' ");
        sb.append("and av.status = 0 order by app_version desc");
        List<List<String>> versionInfos = dynamic5Query.nativeQueryList(sb.toString());
        if(CollectionUtils.isEmpty(versionInfos)){
            return null;
        }
        List<String> newVersion = Lists.newArrayList();
        for (List<String> version:versionInfos) {
            newVersion.addAll(version);
        }
        return newVersion;
    }

    public Integer searchAppId(String appType){
        StringBuffer sb = new StringBuffer("select ab_app_id from channel_group  ");
        sb.append("where app_type = '").append(appType).append("' ");
        sb.append("and status = 1 limit 1");
        Object obj = dynamic5Query.nativeQueryObject(sb.toString());
        return obj == null ? null : Integer.parseInt(obj.toString());
    }
}
