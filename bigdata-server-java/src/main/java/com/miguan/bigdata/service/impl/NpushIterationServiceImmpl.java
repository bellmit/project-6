package com.miguan.bigdata.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.miguan.bigdata.common.constant.KafkaConstants;
import com.miguan.bigdata.common.constant.RabbitMqConstants;
import com.miguan.bigdata.common.constant.SymbolConstants;
import com.miguan.bigdata.dto.NpushIterationDto;
import com.miguan.bigdata.dto.RabbitMQSender;
import com.miguan.bigdata.entity.mongo.AppDeviceVo;
import com.miguan.bigdata.entity.npush.PushDataIterArcticle;
import com.miguan.bigdata.entity.npush.PushDataIterConf;
import com.miguan.bigdata.entity.xy.ClDevice;
import com.miguan.bigdata.mapper.*;
import com.miguan.bigdata.service.AppDeviceService;
import com.miguan.bigdata.service.BloomFilterService;
import com.miguan.bigdata.service.NpushIterationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
public class NpushIterationServiceImmpl implements NpushIterationService {

    @Resource
    private DwdUserActionDisbMapper dwdUserActionDisbMapper;
    @Resource
    private PushDataIterConfMapper pushDataIterConfMapper;
    @Resource
    private PushDataIterArcticleMapper pushDataIterArcticleMapper;
    @Resource
    private AppDeviceService appDeviceService;
    @Resource
    private XyClDeviceMapper xyClDeviceMapper;
    @Resource
    private LdClDeviceMapper ldClDeviceMapper;
    @Resource
    private BloomFilterService bloomFilterService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    private FirstVideosMapper firstVideosMapper;

    private ExecutorService executor = new ThreadPoolExecutor(10, 100, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));

    @Override
    public void initDistinctNpushStateOfSomeActDay(String actDay) {
        String beginTime = actDay + " 00:00:00";
        String endTime = actDay + " 23:59:59";
        int skip = 0;
        int limit = 5000;
        List<AppDeviceVo> appDeviceList = appDeviceService.findDistinctByTime(beginTime, endTime, skip, limit);
        while (!isEmpty(appDeviceList)) {
            log.info("Npush推送设备初始化 beginTime:{}, endTime:{}, skip:{}, limit:{}, 查询到[{}]个设备", beginTime, endTime, skip, limit, appDeviceList.size());
            appDeviceList.forEach(a -> {
                List<ClDevice> devices = null;
                if (StringUtil.isEmpty(a.getPackage_name())) {
                    log.warn("Npush推送设备初始化 {}无包名，直接禁止推送", a.getDistinct_id());
                } else {
                    // 根据报名判单需要查询的库
                    if (a.getPackage_name().contains("phonecall") || a.getPackage_name().contains("xld")) {
                        // 来电库设备表
                        devices = ldClDeviceMapper.selectByDistinctIdAndPackageName(null, a.getDistinct_id());
                    } else {
                        // 视频库设备表
                        devices = xyClDeviceMapper.selectByDistinctIdAndPackageName(a.getPackage_name(), a.getDistinct_id());
                    }
                }
                int npushState = 0;// 推送状态 0 禁止，1开启
                int npushChannel = -1; // 推送渠道 -1 无 1 友盟 2 华为 3 vivo 4 oppo 5 小米 int
                int lastVisitDate = 0; // 设备最后一次登录日期
                log.debug("Npush推送设备初始化 应用[{}], 设备[{}], 查询到设备结果>>{}", a.getPackage_name(), a.getDistinct_id(), JSONObject.toJSONString(devices));
                if (isEmpty(devices)) {
                    log.debug("Npush推送设备初始化 未查询到应用[{}], 设备[{}], dt[{}], 直接更新推送状态为[0], 推送渠道[-1], 最后访问日期[0]", a.getPackage_name(), a.getDistinct_id(), actDay);
                    appDeviceService.update4NpushInit(a.getPackage_name(), a.getDistinct_id(), actDay, npushState, npushChannel, lastVisitDate);
                } else {
                    npushState = 1;
                    for (ClDevice d : devices) {
                        npushState = this.checkDevicePushState(d);
                        if (npushState == 0) {
                            log.debug("Npush推送设备初始化 查询到应用[{}], 设备[{}], 已卸载结束循环遍历", a.getPackage_name(), a.getDistinct_id());
                            break;
                        }
                        npushChannel = this.checkDeviceNPushChannel(d);
                    }
                    Integer dt = dwdUserActionDisbMapper.selectLastVisitDate(a.getPackage_name(), a.getDistinct_id());
                    lastVisitDate = dt == null ? 0 : dt.intValue();
                    // 同步推送状态到app_device表
                    log.debug("Npush推送设备初始化 更新应用[{}], 设备[{}], dt[{}], 推送状态[{}], 推送渠道[{}], 最后访问日期[{}]", a.getPackage_name(), a.getDistinct_id(), actDay, npushState, npushChannel, lastVisitDate);
                    appDeviceService.update4NpushInit(a.getPackage_name(), a.getDistinct_id(), actDay, npushState, npushChannel, lastVisitDate);
                }
            });
            skip += limit;
            appDeviceList = appDeviceService.findDistinctByTime(beginTime, endTime, skip, limit);
        }
    }

    @Override
    public void updateDistinctPushStateToStop(String distinctId) {
        List<ClDevice> xyDeviceList = xyClDeviceMapper.selectByDistinctIdAndPackageName(null, distinctId);
        if (!isEmpty(xyDeviceList)) {
            xyDeviceList.forEach(c -> {
                int pushState = this.checkDevicePushState(c);
                if (pushState == 0) {
                    appDeviceService.updateNpushStateToStop(c.getAppPackage(), c.getDistinctId());
                }
            });
        }

        List<ClDevice> ldDeviceList = ldClDeviceMapper.selectByDistinctIdAndPackageName(null, distinctId);
        if (!isEmpty(ldDeviceList)) {
            ldDeviceList.forEach(c -> {
                int pushState = this.checkDevicePushState(c);
                if (pushState == 0) {
                    appDeviceService.updateNpushStateToStop("com.mg.phonecall", c.getDistinctId());
                }
            });
        }

    }

    /**
     * 检查设备的推送渠道
     * 推送渠道：-1 无 1 友盟 2 华为 3 vivo 4 oppo 5 小米 int
     *
     * @param clDevice
     * @return
     */
    private int checkDeviceNPushChannel(ClDevice clDevice) {
        if (StringUtil.isNotBlank(clDevice.getHuaweiToken())) {
            return 2;
        } else if (StringUtil.isNotBlank(clDevice.getVivoToken())) {
            return 3;
        } else if (StringUtil.isNotBlank(clDevice.getOppoToken())) {
            return 4;
        } else if (StringUtil.isNotBlank(clDevice.getXiaomiToken())) {
            return 5;
        } else if (StringUtil.isNotBlank(clDevice.getDeviceToken())) {
            return 1;
        }
        return -1;
    }

    private int checkDevicePushState(ClDevice clDevice) {
        int pushState = 0;
        if (StringUtil.equals("10", clDevice.getState()) && (clDevice.getIsDelete() == null || clDevice.getIsDelete() == 0)) {
            pushState = 1;
        }
        return pushState;
    }

    @Override
    public List<PushDataIterConf> getTaskFromConfig(Integer dayOfWeek) {
        return pushDataIterConfMapper.selectByServenDay(dayOfWeek);
    }

    @Override
    public List<PushDataIterArcticle> getPushDateIterArcticleByActDatAndSortNum(Integer projectType, Integer actDay, Integer sortNum) {
        return pushDataIterArcticleMapper.selectByActDayAndSortNum(projectType, actDay, sortNum);
    }

    @Override
    public List<PushDataIterArcticle> getAlternativeArcticleList(Integer projectType, Integer actDay, Integer maxBatchNum) {
        List<PushDataIterArcticle> arcticleList = pushDataIterArcticleMapper.selectByActDayAndAfterSortNum(projectType, actDay, maxBatchNum);

        // 如果是视频的素材，已经下线的视频不返回
        List<Integer> videoIds = arcticleList.stream().filter(e -> {
            return e.getType() == 3;
        }).map(PushDataIterArcticle::getVideoId).map(Integer::new).distinct().collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(videoIds)) {
            videoIds = firstVideosMapper.checkVideosIsOnline(videoIds);
            log.debug("推送素材中上线的视频有：{}", JSONObject.toJSONString(videoIds));
            // 如果所有的视频都没有上线，直接返回非视频的推送素材
            if (CollectionUtils.isEmpty(videoIds)) {
                return arcticleList.stream().filter(e -> {
                    return e.getType() != 3;
                }).collect(Collectors.toList());
            } else {
                Iterator<PushDataIterArcticle> it = arcticleList.iterator();
                while (it.hasNext()) {
                    PushDataIterArcticle arcticle = it.next();
                    if (arcticle.getType() == 3) {
                        if (!videoIds.contains(Integer.parseInt(arcticle.getVideoId()))) {
                            log.debug("推送素材视频ID>>{}, 未上线，移除", arcticle.getVideoId());
                            it.remove();
                        }
                    }
                }
            }
        }

        return arcticleList;
    }

    /**
     * 某一激活日的用户，每pushSize个用户推送1次
     *
     * @param dto
     * @param arcticle
     * @param excludeChannel
     */
    @Override
    public void pushAnyUserAsSomeOneActDayFromMongo(NpushIterationDto dto, PushDataIterArcticle arcticle, List<String> excludeChannel) {
        List<PushDataIterArcticle> arcticleList = this.getAlternativeArcticleList(dto.getProjectType(), arcticle.getActDay(), dto.getMaxBatchNum());
        if (isEmpty(arcticleList)) {
            log.info("NpushIter 应用{} - {}日激活的用户 无备用推送素材", dto.getAppPackage(), arcticle.getActDay());
            arcticleList = new ArrayList<PushDataIterArcticle>();
            arcticleList.add(arcticle);
        } else {
            log.info("NpushIter 应用{} - {}日激活的用户 备用推送素材>>{}", dto.getAppPackage(), arcticle.getActDay(), JSONObject.toJSONString(arcticleList));
            arcticleList.add(0, arcticle);
        }
        // 每个素材对应的推送设备列表
        Map<Long, List<String>> arcticleDistinctIdsMap = new HashMap<Long, List<String>>(arcticleList.size());
        arcticleList.forEach(a -> {
            arcticleDistinctIdsMap.put(a.getId(), new ArrayList<String>());
        });

        // 跳过记录条数
        int skip = 0;
        // 每次查询出3000个设备
        int limit = 3000;
        // 每批发送600个设备
        int sendSize = 600;
        Integer dt = Integer.parseInt(LocalDate.now().minusDays(arcticle.getActDay() - 1).format(DateTimeFormatter.BASIC_ISO_DATE));
        String mongoPackageName = dto.getAppPackage().contains("xld") ? "com.mg.phonecall" : dto.getAppPackage();
        long count = appDeviceService.countDistinctByDtAndPackageName(dt, mongoPackageName);
        log.info("NpushIter 应用{} - {}日激活的设备共计{}个", dto.getAppPackage(), arcticle.getActDay(), count);
        try {
            while (skip < count) {
                List<String> distinctList = appDeviceService.findDistinctByDtAndPackageName(dt, mongoPackageName, skip, limit);
                // 遍历推送素材，校验设备列表有无推送记录
                this.existsMultiForEachArcticle(dto, arcticleList, distinctList, arcticleDistinctIdsMap);
                // 当推送素材对应设备个数满足sendSize时, 进行发送
                this.doSend(dto, arcticleList, arcticleDistinctIdsMap, sendSize);
                skip += limit;
                // 放缓推送速度
                // 因各部门都共同使用同一个mongo服务，推送任务触发时，突然大量的查询，以及后续的修改，导致视频推荐慢查询->io阻塞->cpu飙升
                Thread.sleep(500L);
            }
        } catch (InterruptedException e) {
            log.error("");
        }
        // 将剩下的推送信息，全部发送出去
        this.doSendLast(dto, arcticleList, arcticleDistinctIdsMap);
        arcticleDistinctIdsMap.clear();
    }

    /**
     * 遍历推送素材，校验设备列表有无推送记录
     *
     * @param dto
     * @param arcticleList
     * @param distinctIds
     * @param arcticleDistinctIdsMap
     */
    private void existsMultiForEachArcticle(NpushIterationDto dto, List<PushDataIterArcticle> arcticleList, List<String> distinctIds, Map<Long, List<String>> arcticleDistinctIdsMap) {
        for (PushDataIterArcticle a : arcticleList) {
            // 推送记录校验结果
            Map<String, List<String>> resultMap = this.existsMultiSplit(dto, a, distinctIds);
            // 获取未推送过的设备列表, 放入每个素材对应的推送设备列表
            List<String> sendCurrentArticleList = resultMap.get(BloomFilterService.not_exists);
            arcticleDistinctIdsMap.get(a.getId()).removeAll(sendCurrentArticleList);
            arcticleDistinctIdsMap.get(a.getId()).addAll(sendCurrentArticleList);
            sendCurrentArticleList.clear();
            // 获取推送过的设备列表，校验下一个推送素材
            distinctIds = resultMap.get(BloomFilterService.exists);
        }
        distinctIds.clear();
    }

    /**
     * 拆分过滤
     *
     * @param dto
     * @param arcticle
     * @param distinctIdList
     * @return
     */
    private Map<String, List<String>> existsMultiSplit(NpushIterationDto dto, PushDataIterArcticle arcticle, List<String> distinctIdList) {
        String partOfBloomKey = this.getPartOfBloomKey(arcticle);
        // 未推送过该素材的设备列表
        List<String> notSendList = new ArrayList<String>();
        // 已推送过该素材的设备列表
        List<String> hadSendList = new ArrayList<String>();

        // 每次拆分100个设备进行过滤
        int start = 0;
        int step = 100;
        int max = distinctIdList.size();
        // 开始过滤已推送过的设备
        while (start < max) {
            int stop = Math.min(max, start + step);
            Map<String, List<String>> partBloomResult = bloomFilterService.existsMuilByDistinctId(distinctIdList.subList(start, stop), partOfBloomKey);
            log.debug("NpushIter 应用{} - {}日激活的用户, 素材[{}]，曝光过滤结果>>{}", dto.getAppPackage(), arcticle.getActDay(), arcticle.getId(), partBloomResult);
            hadSendList.addAll(partBloomResult.get(BloomFilterService.exists));
            notSendList.addAll(partBloomResult.get(BloomFilterService.not_exists));
            partBloomResult.clear();
            start += step;
        }
        log.info("NpushIter 应用{} - {}日激活的用户, 素材[{}], 未曝光的用户[{}]个, 曝光的用户[{}]个", dto.getAppPackage(), arcticle.getActDay(), arcticle.getId(), notSendList.size(), hadSendList.size());
        Map<String, List<String>> allBloomResult = new HashMap<String, List<String>>(2);
        allBloomResult.put(BloomFilterService.exists, hadSendList);
        allBloomResult.put(BloomFilterService.not_exists, notSendList);
        return allBloomResult;
    }

    /**
     * 发送到kafka，将素材设备放入Redis Bloom过滤器
     *
     * @param appPackage
     * @param arcticle
     * @param distinctIdList
     */
    private void sendToKafka4Bloom(String appPackage, PushDataIterArcticle arcticle, List<String> distinctIdList) {
        String partOfBloomKey = this.getPartOfBloomKey(arcticle);
        distinctIdList.forEach(e -> {
            JSONObject json = new JSONObject();
            json.put(KafkaConstants.PARAM_TYPE, KafkaConstants.TYPE_NUPUSH_BLOOM);
            json.put(KafkaConstants.PARAM_PACKAGE_NAME, appPackage);
            json.put(KafkaConstants.PARAM_DISTINCT_ID, e);
            json.put(KafkaConstants.PARAM_PART_OF_BLOOM_KEY, partOfBloomKey);
            log.debug("NpushIter 应用{} - {}日激活的用户，发送到kafka", appPackage, arcticle.getActDay());
            kafkaTemplate.sendDefault(json.toJSONString());
            json.clear();
        });
    }

    /**
     * 将推送素材设备列表，发送到kafka，MQ
     *
     * @param dto
     * @param arcticleList
     * @param arcticleDistinctIdsMap
     * @param sendSize
     */
    private void doSend(NpushIterationDto dto, List<PushDataIterArcticle> arcticleList, Map<Long, List<String>> arcticleDistinctIdsMap, Integer sendSize) {
        arcticleList.forEach(a -> {
            List<String> disctinctIds = arcticleDistinctIdsMap.get(a.getId());
            while (disctinctIds.size() >= sendSize) {
                List<String> sendList = disctinctIds.subList(0, sendSize);
                // 发送到kafka，进行推送记录
                this.baseSend(dto, a, Lists.newArrayList(sendList));
                disctinctIds.removeAll(sendList);
            }
        });
    }

    /**
     * 将最后剩余的推送素材设备列表，发送到kafka，MQ
     *
     * @param dto
     * @param arcticleList
     * @param arcticleDistinctIdsMap
     */
    private void doSendLast(NpushIterationDto dto, List<PushDataIterArcticle> arcticleList, Map<Long, List<String>> arcticleDistinctIdsMap) {
        arcticleList.forEach(a -> {
            List<String> disctinctIds = arcticleDistinctIdsMap.get(a.getId());
            if (!isEmpty(disctinctIds)) {
                // 发送到kafka，进行推送记录
                this.baseSend(dto, a, disctinctIds);
            }
        });
    }

    /**
     * 发送到kafka，进行推送记录
     *
     * @param dto
     * @param a
     * @param disctinctIds
     */
    private void baseSend(NpushIterationDto dto, PushDataIterArcticle a, List<String> disctinctIds) {
        executor.execute(() -> {
            sendToKafka4Bloom(dto.getAppPackage(), a, disctinctIds);
            JSONObject param = new JSONObject();
            param.put("msgId", dto.getTimestamp() + SymbolConstants.underline + a.getId().toString());
            param.put("pushId", a.getId().toString());
            param.put("appPackage", dto.getAppPackage());
            param.put("distinctIds", disctinctIds);
            RabbitMQSender sender = new RabbitMQSender("iterArcticlePush", param, rabbitTemplate, RabbitMqConstants.BIGDATA_POINT_NPUSH_POOL_EXCHANGE, RabbitMqConstants.BIGDATA_POINT_NPUSH_POOL_RUTEKEY);
            sender.send();
            log.info("NpushIter 应用{} - {}日激活的用户, 素材[{}]，未曝光的用户[{}]个开始发送到MQ", dto.getAppPackage(), a.getActDay(), a.getId(), disctinctIds.size());
            log.info("NpushIter 应用{} - {}日激活的用户, 素材[{}]，未曝光的用户>>{}", dto.getAppPackage(), a.getActDay(), a.getId(), JSONObject.toJSONString(disctinctIds));
            disctinctIds.clear();
        });
    }

    private String getPartOfBloomKey(PushDataIterArcticle arcticle) {
        return arcticle.getType() == 3 ? arcticle.getVideoId() : String.join(SymbolConstants.underline, "npush", arcticle.getId().toString());
    }
}
