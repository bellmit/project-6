package com.miguan.laidian.service.impl;

import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.util.VersionUtil;
import com.miguan.laidian.entity.Channel;
import com.miguan.laidian.entity.ShieldMenuConfig;
import com.miguan.laidian.service.MoFangService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 魔方后台ServiceImpl
 * @author xy.chen
 * @date 2020-03-23
 **/

@Service("moFangService")
public class MoFangServiceImpl implements MoFangService {

	@Resource
	private HikariDataSource secodDataSource;

	@Override
	public List<ShieldMenuConfig> countVersionList(String codes, String appVersion, String appType, int tagType) {
		String nativeSql = "select ss.para_num as cks, count(1) ct from shield_version sv  "
				+ "inner join app_version_set v on v.id = sv.version_id "
				+ "inner join channel_group g on g.s_id  = v.s_id AND sv.group_id = g.id "
				+ "right join shield_set ss on sv.shield_id = ss.id "
				+ "where v.app_version ='" + appVersion + "'" +
				"and g.app_type = '" + appType + "'" +
				"and g.tag_type = '" + tagType + "'" +
				"and ss.para_num in  ("+codes+")" +
				"and type = 1 group by ss.para_num";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(secodDataSource);
		return jdbcTemplate.query(nativeSql, new ShieldMenuConfig());
	}

	@Override
	public List<ShieldMenuConfig> countChannelList(String codes, String channelId, String appType, String appVersion, int tagType) {
		String nativeSql = "select ss.para_num as cks, count(1) ct from shield_channel sc "
				+ "inner join channel_group g on g.id = sc.group_id "
				+ "right join shield_set ss on sc.shield_id = ss.id "
				+ "inner join shield_version sv on sv.id = sc.v_id "
				+ "inner join app_version_set v on v.id = sv.version_id "
				+ "inner join agent_users aus on aus.channel_id = sc.channel_id "
				+ "inner join site si on si.agent_user_id = aus.id "
				+ "and g.app_type = '" + appType + "'" +
				"and ss.para_num in ("+codes+")" +
				"and v.app_version = '" + appVersion + "'" +
				"and si.domain = '" + channelId + "' " +
				"and g.tag_type = '" + tagType + "'";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(secodDataSource);
		return jdbcTemplate.query(nativeSql, new ShieldMenuConfig());
	}

	@Override
	public int countVersion(String postitionType, String appVersion, String appType, int tagType) {
		String nativeSql = "select count(1) from shield_version sv  "
				+ "inner join app_version_set v on v.id = sv.version_id "
				+ "inner join channel_group g on g.s_id  = v.s_id AND sv.group_id = g.id "
				+ "inner join shield_set ss on sv.shield_id = ss.id "
				+ "where v.app_version = '" + appVersion + "'" +
				" and g.app_type = '" + appType + "'" +
				" and ss.para_num = '" + postitionType + "'" +
				" and g.tag_type = " + tagType +
				" and type = 1";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(secodDataSource);
		return jdbcTemplate.queryForObject(nativeSql,Integer.class);
	}

	@Override
	public int countChannel(String postitionType, String channelId, String appType, String appVersion, int tagType) {
		String nativeSql = "select count(1) from shield_channel sc "
				+ "inner join channel_group g on g.id = sc.group_id "
				+ "inner join shield_set ss on sc.shield_id = ss.id "
				+ "inner join shield_version sv on sv.id = sc.v_id "
				+ "inner join app_version_set v on v.id = sv.version_id "
				+ "inner join agent_users aus on aus.channel_id = sc.channel_id "
				+ "inner join site si on si.agent_user_id = aus.id "
				+ "where g.app_type = '" + appType + "'" +
				" and ss.para_num = '" + postitionType + "'" +
				" and v.app_version = '" + appVersion + "'" +
				" and si.domain = '" + channelId + "'" +
				" and g.tag_type = " + tagType ;
		JdbcTemplate jdbcTemplate = new JdbcTemplate(secodDataSource);
		return jdbcTemplate.queryForObject(nativeSql,Integer.class);
	}

	@Override
	public List<Channel> getChannels() {
		String nativeSql = "SELECT au.channel_id as channelId,s.domain as domain from channel_tool_mofang.site s " +
				"LEFT JOIN channel_tool_mofang.agent_users au on s.agent_user_id  = au.id ";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(secodDataSource);
		return  jdbcTemplate.query(nativeSql, new Channel());
	}

	/**
	 * 查询魔方后台是否禁用该渠道的广告:true禁用，false非禁用
	 * @param param
	 * @return
	 */
	@Override
	public boolean stoppedByMofang(Map<String, Object> param) {
		String appVersion = param.get("appVersion") + "";
		if (!"null".equals(appVersion) && VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_190)) {
			String mobileType = param.get("mobileType") + "";
			String postitionType = param.get("postitionType") + "";
			String appType = param.get("appType") + "";
			String channelId = param.get("channelId") + "";
			int tagType = Constant.IOS.equals(mobileType) ? 2 : 1;
			int count = countVersion(postitionType, appVersion, appType, tagType);
			//根据版本判断是否屏蔽全部广告
			if (count > 0) {
				return true;
			}
			//非全部的屏蔽根据渠道查询是否屏蔽广告
			int countChannel = countChannel(postitionType, channelId, appType, appVersion, tagType);
			if (countChannel > 0) {
				return true;
			}
		}
		return false;
	}
}