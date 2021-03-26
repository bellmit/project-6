package com.miguan.bigdata.service.impl;

import com.miguan.bigdata.common.constant.RedisKeyConstants;
import com.miguan.bigdata.common.enums.PushUserEnmu;
import com.miguan.bigdata.dto.PushDto;
import com.miguan.bigdata.dto.PushVideoDto;
import com.miguan.bigdata.vo.FirstVideosPushVo;
import com.miguan.bigdata.vo.FirstVideosVo;
import com.miguan.bigdata.vo.PushResultVo;
import com.miguan.bigdata.mapper.PushVideoMapper;
import com.miguan.bigdata.service.PushVideoService;
import com.miguan.bigdata.service.Redis2Service;
import com.miguan.bigdata.vo.PushVideoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tool.util.DateUtil;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.util.*;

/**
 * 自动推送
 */
@Service
@Slf4j
public class PushVideoServiceImpl extends BaseAutoPushService implements PushVideoService {

    @Resource
    private PushVideoMapper pushVideoMapper;
    @Resource
    private Redis2Service redis2Service;

    /**
     * 根据视频id查询当天的视频播放数、完播数
     *
     * @param videoIds
     * @return
     */
    @Override
    public List<PushVideoDto> findPushVideosInfo(List<String> videoIds) {
        if (videoIds == null || videoIds.isEmpty()) {
            return null;
        }
        return pushVideoMapper.findPushVideosInfo(videoIds);
    }

    /**
     * 新增或删除push视频库
     *
     * @param type     1--新增，2--删除
     * @param videoIds
     */
    @Override
    public void saveAndDeletePushVideos(Integer type, List<String> videoIds) {
        if (videoIds == null || videoIds.isEmpty()) {
            return;
        }
        pushVideoMapper.deletePushVideos(videoIds);
        if (type == 1) {
            pushVideoMapper.batchSavePushVideos(videoIds);
        }
    }

    @Override
    public void syncAutoPushLog(String arrayList) {
        if (StringUtils.isBlank(arrayList)) {
            log.error("同步推送日志记录失败");
            return;
        }
        String[] array = arrayList.split(";");
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            try {
                String[] one = array[i].split(":");
                String distinctIds = one[0];
                Integer videoId = Integer.parseInt(one[1]);
                String packageName = one[2];
                String[] distinctIdArry = distinctIds.split(",");
                for (int j = 0; j < distinctIdArry.length; j++) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("distinct_id", distinctIdArry[j]);
                    map.put("video_id", videoId);
                    map.put("package_name", packageName);
                    list.add(map);
                }
            } catch (Exception e) {
                continue;
            }
        }
        pushVideoMapper.syncAutoPushLog(list);
    }

    /**
     * 同步push视频库视频的播放数，有效播放数，完播数
     */
    @Override
    public void syncPushVideos() {
        log.info("同步push视频库视频的播放数，有效播放数，完播数到mysql(start)");
        try {
            pushVideoMapper.deletePushVideosMid();  //删除中间表的数据

            int count = pushVideoMapper.countPushVideos();  //总记录数
            int index = 0;
            int pageSize = 2000;
            Map<String, Object> params = new HashMap<>();
            params.put("pageSize", pageSize);
            while (index < count) {
                params.put("startRow", index);
                List<PushVideoVo> list = pushVideoMapper.listPushVideos(params);   //在ck中查询出视频的播放数，有效播放数，完播数

                pushVideoMapper.batchSavePushVideosMid(list);  //把视频的播放数，有效播放数，完播数同步到mysql的中间表中
                index = index + pageSize;
                log.info("同步push视频库数据到中间表，记录：{}", index);
            }
            pushVideoMapper.updatePushVideosNums();  //把push视频库中间表的数据更新到push视频表中
        } catch (Exception e) {
            log.error("同步push视频库视频的播放数，有效播放数，完播数到mysql异常", e);
        }
        log.info("同步push视频库视频的播放数，有效播放数，完播数到mysql(end)");
    }

    /**
     * 查询出push推送的用户
     *
     * @param userType    用户类型
     * @param packageName 包名
     * @param activityDay 活跃天数范围
     * @param triggerType 触发类型  1：当天，2：明天
     */
    @Override
    public void batchSavePushUser(Integer userType, String packageName, String activityDay, Integer triggerType) {
        log.info("统计自动推送的数据(start);userType：{},packageName：{}, activityDay：{},  triggerType：{}", userType, packageName, activityDay, triggerType);
        Map<String, Object> params = new HashMap<>();
        String dd = DateUtil.dateStr2(new Date());
        if (triggerType == 2) {
            dd = DateUtil.dateStr2(DateUtil.rollDay(new Date(), -1));  //获取昨天日期
        }
        Integer dt = Integer.parseInt(dd.replace("-", ""));

        params.put("dt", dt);  //日期
        params.put("userType", userType);  //用户类型
        params.put("packageName", packageName);  //包名

        if (PushUserEnmu.newUserNoPlayOne.getCode() == userType) {
            //新用户（激活当天）,20点前新增的用户，且（0-19：59）未产生播放行为
            log.info("新用户（激活当天）,20点前新增的用户，且（0-19：59）未产生播放行为start");
            params.put("startTime", dd + " 00:00:00");
            params.put("endTime", dd + " 19:59:59");
            pushVideoMapper.deletePushUser(params);
            pushVideoMapper.batchSaveNewUserNoPlay(params);
            log.info("新用户（激活当天）,20点前新增的用户，且（0-19：59）未产生播放行为end");
        } else if (PushUserEnmu.newUserNoPlayTwo.getCode() == userType) {
            //新用户（激活当天）,20点后新增的用户，且（20-23：59）未产生播放行为
            log.info("新用户（激活当天）,20点后新增的用户，且（20-23：59）未产生播放行为start");
            params.put("startTime", dd + " 20:00:00");
            params.put("endTime", dd + " 23:59:59");
            pushVideoMapper.deletePushUser(params);
            pushVideoMapper.batchSaveNewUserNoPlay(params);
            log.info("新用户（激活当天）,20点后新增的用户，且（20-23：59）未产生播放行为end");
        } else if (PushUserEnmu.newUserPlay.getCode() == userType) {
            //新用户（激活当天) 当日产生播放行为
            log.info("新用户（激活当天) 当日产生播放行为start");
            pushVideoMapper.deletePushUser(params);
            pushVideoMapper.batchSaveNewUserPlay(params);
            log.info("新用户（激活当天) 当日产生播放行为end");
        } else if (PushUserEnmu.oldUserPlay.getCode() == userType) {
            //老用户（激活次日以后） 当日产生播放行为
            log.info("老用户（激活次日以后） 当日产生播放行为start");
            String[] activityDays = activityDay.split(",");
            if (activityDays.length > 1) {
                params.put("startDay", 0 - Integer.parseInt(activityDays[1]));  //活跃开始天数
                params.put("endDay", 0 - Integer.parseInt(activityDays[0]));  //活跃结束天数
                pushVideoMapper.deletePushUser(params);
                pushVideoMapper.batchSaveOldUserPlay(params);
            }
            log.info("老用户（激活次日以后） 当日产生播放行为end");
        } else if (PushUserEnmu.oldUserNoPlay.getCode() == userType) {
            //老用户（激活次日以后） 当日未产生播放行为
            log.info("老用户（激活次日以后） 当日未产生播放行为 start");
            String[] activityDays = activityDay.split(",");
            if (activityDays.length > 1) {
                params.put("startDay", 0 - Integer.parseInt(activityDays[1]));  //活跃开始天数
                params.put("endDay", 0 - Integer.parseInt(activityDays[0]));  //活跃结束天数
                pushVideoMapper.deletePushUser(params);
                pushVideoMapper.batchSaveOldUserNoPlay(params);
            }
            log.info("老用户（激活次日以后） 当日未产生播放行为end");
        } else if (PushUserEnmu.oldUserNoActive.getCode() == userType) {
            //不活跃用户（未启动天数>=30天）
            log.info("不活跃用户（未启动天数>=30天）start");
            String[] activityDays = activityDay.split(",");
            if (activityDays.length > 1) {
                params.put("startDay", Integer.parseInt(activityDays[0]));  //活跃开始天数
                params.put("endDay", Integer.parseInt(activityDays[1]));  //活跃结束天数
                pushVideoMapper.deletePushUser(params);
                pushVideoMapper.batchSaveOldUserNoActive(params);
            }
            log.info("不活跃用户（未启动天数>=30天）end");
        }

        //根据参数生成推送结果表名
        String tableName = "push_" + packageName.replace(".", "_") + "_" + userType + "_" + dt;
        synchronized (tableName) {
            if (pushVideoMapper.isExistAutoPushTable(tableName) > 0) {
                //表如果已经存在，则drop表
                pushVideoMapper.dropAutoPushTable(tableName);
            }
            //创建创建一张空表
            pushVideoMapper.createAutoPushTable(tableName);
            params.put("tableName", tableName);
            pushVideoMapper.insertAutoPushUserVideo(params);
        }
        log.info("统计自动推送的数据(end);userType：{},packageName：{}, activityDay：{},  triggerType：{}", userType, packageName, activityDay, triggerType);
    }

    /**
     * 生成用户推送结果表
     *
     * @param userType 用户类型
     * @param dd       日期
     */
    @Override
    public void batchStaPushVidosResult(Integer userType, String dd) {
        Map<String, Object> params = new HashMap<>();
        params.put("dd", dd);
        params.put("userType", userType);

        try {
            log.info("生成用户推送结果表service(start)");
            pushVideoMapper.batchStaPushVidosResult(params);  //生成用户推送结果表
            pushVideoMapper.deletePushVideosResult(params);
            pushVideoMapper.updatePushVideosResultTag(params);
            log.info("生成用户推送结果表service(end)");
        } catch (Exception e) {
            log.error("生成用户推送结果表service异常", e);
        }
    }

    /**
     * 自动推送视频接口
     *
     * @param pushId    推送ID
     * @param userType    用户类型
     * @param packageName app包名
     * @param dd          日期：yyyy-MM-dd
     * @param pageNum     页码
     * @param pageSize    每页记录数
     * @return
     */
    @Override
    public List<PushResultVo> findAutoPushList(Integer pushId, Integer userType, String packageName, String dd, Integer pageNum, Integer pageSize) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("packageName", packageName);
        //根据参数生成推送结果表名
        int dt = Integer.parseInt(dd.replace("-", ""));
        String tableName = "push_" + packageName.replace(".", "_") + "_" + userType + "_" + dt;
        params.put("tableName", tableName);
        //分页参数
        int startRow = (pageNum == 1 ? 0 : pageNum * pageSize);
        int endRow = pageSize;
        params.put("startRow", startRow);
        params.put("endRow", endRow);

        List<PushResultVo> pageList = pushVideoMapper.findPushVideosResult(params);
        return pageList;
    }

    @Override
    public List<PushResultVo> findAutoPushList(PushDto pushDto) {
        return super.findPushUserList(pushDto);
    }

    /**
     * 从mysql的first_videos中同步视频数据到clickhouse的video_info中
     */
    public void syncVideoInfo() {
        String maxUpdateTime = pushVideoMapper.findMaxVideoUpdatedTime();
        maxUpdateTime = (maxUpdateTime == null ? "0000-00-00 00:00:00" : maxUpdateTime);

        int count = pushVideoMapper.countNewVideos(maxUpdateTime);
        int index = 0;
        int pageSize = 5000;

        Map<String, Object> params = new HashMap<>();
        params.put("pageSize", pageSize);
        params.put("maxUpdateTime", maxUpdateTime);
        while (index < count) {
            params.put("startRow", index);
            List<FirstVideosPushVo> dataList = pushVideoMapper.queryNewVideos(params);  //在mysql查询出视频数据

            if(dataList != null && !dataList.isEmpty()) {
                pushVideoMapper.deleteVideoInfoById(dataList);   //删除clickhouse中已经存在的视频数据
                pushVideoMapper.batchInsertUpdateVideo(dataList);  //重新同步最新的视频数据到clickhouse中
            }
            index = index + pageSize;
            log.info("同步视频数据，记录：{}", index);
        }
    }

    /**
     * 汇总视频明细数据到clickhouse的video_detail表中
     * @param day
     */
    public void syncVideoDetail(String day) {
        if(StringUtil.isBlank(day)) {
            return;
        }

        pushVideoMapper.deleteVideoDetail(day);
        pushVideoMapper.batchSaveVideoDetail(day);
    }
}
