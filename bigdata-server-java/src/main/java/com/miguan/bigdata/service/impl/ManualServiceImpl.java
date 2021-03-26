package com.miguan.bigdata.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.miguan.bigdata.common.constant.KafkaConstants;
import com.miguan.bigdata.common.constant.RabbitMqConstants;
import com.miguan.bigdata.common.constant.SymbolConstants;
import com.miguan.bigdata.common.util.Global;
import com.miguan.bigdata.dto.RabbitMQSender;
import com.miguan.bigdata.mapper.ManulPushDistinctMapper;
import com.miguan.bigdata.mapper.UserCatPoolMapper;
import com.miguan.bigdata.service.BloomFilterService;
import com.miguan.bigdata.service.ManualService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
@Service
public class ManualServiceImpl implements ManualService {

    @Resource
    private UserCatPoolMapper userCatPoolMapper;
    @Resource
    private ManulPushDistinctMapper manulPushDistinctMapper;
    @Resource
    private BloomFilterService bloomFilterService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    private ExecutorService executor = new ThreadPoolExecutor(10, 100, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));

    @Override
    public Long countPush(String appPackage, String catids) {
        List<Integer> catList = Stream.of(catids.split(SymbolConstants.comma)).map(Integer::valueOf).collect(Collectors.toList());
        List<String> whiteChannels = null;
        String whiteChannelStr = Global.getValue("push_hand_white_list_channel");
        if (!isEmpty(whiteChannelStr)) {
            whiteChannels = Arrays.asList(whiteChannelStr.split(SymbolConstants.comma));
        }
        return userCatPoolMapper.countDistinictIdWhereTopThreeInCatids(null, catList, whiteChannels);
    }

    @Override
    public void findAndSendToMq(String type, String businessId, JSONObject param) {
        LocalDate localDate = LocalDate.now();
        String recevice_time = localDate.minusDays(30L).format(DateTimeFormatter.ISO_LOCAL_DATE) + " 00:00:00";
        Integer dt = Integer.parseInt(localDate.format(DateTimeFormatter.BASIC_ISO_DATE));
        String pushId = param.getString("pushId");
        String videoId = param.getString("videoId");
        String appPackage = param.getString("appPackage");
        log.info("interestCat 开始推送>>{}", pushId, param);

        List<String> appPackageList = Arrays.asList(appPackage.split(SymbolConstants.comma));
        List<Integer> catList = Stream.of(param.getString("catIds").split(SymbolConstants.comma)).map(Integer::valueOf).collect(Collectors.toList());

        String whiteChannelStr = Global.getValue("push_hand_white_list_channel");
        List<String> whiteChannels = isEmpty(whiteChannelStr) ? null : Arrays.asList(whiteChannelStr.split(SymbolConstants.comma));
        log.info("interestCat 白名单渠道>>{}", whiteChannelStr);

        String partOfBloomKey = isEmpty(videoId) ? "npush-" + pushId : videoId;
        appPackageList.forEach(e -> {
            Long needSendCount = manulPushDistinctMapper.countByParams(businessId, e, whiteChannels);
            if(needSendCount < 1){
                userCatPoolMapper.initManualPushDistinct(dt, businessId, recevice_time, catList);
            }

            needSendCount = manulPushDistinctMapper.countByParams(businessId, e, whiteChannels);
            log.info("interestCat 应用[{}]-[{}]预估需要推送[{}]，用户{}人", e, pushId, partOfBloomKey, needSendCount);
            int size = 3000;
            long threadNum = needSendCount % size > 0 ? (needSendCount / size) + 1 : needSendCount / size;
            for (int i = 0; i < threadNum; i++) {
                int skip = i * size;
                log.info("interestCat 应用[{}]-[{}]开始分批异步推送[{}], skip:{}, size:{}", e, pushId, partOfBloomKey, skip, size);
                this.process(type, businessId, pushId, e, catList, whiteChannels, skip, size, partOfBloomKey);
            }
        });
    }

    private void process(String type, String businessId, String pushId, String appPackage, List<Integer> catList, List<String> whiteChannels, int skip, int size, String partOfBloomKey) {
        List<String> distinctIdList = manulPushDistinctMapper.selectByParams(businessId, appPackage, whiteChannels, skip, size);
        if (StringUtils.isEmpty(distinctIdList)) {
            log.info("interestCat 应用[{}]-[{}]开始分批异步推送[{}], skip:{}, size:{}, 0人", appPackage, pushId, partOfBloomKey, skip, size);
            return;
        }
        log.info("interestCat 应用[{}]-[{}]开始分批异步推送[{}], skip:{}, size:{}, {}人", appPackage, pushId, partOfBloomKey, skip, size, distinctIdList.size());

        int index = 0;
        int step = 600;
        int maxIndex = distinctIdList.size();
        while (index < maxIndex) {
            int stop = Math.min(index + step, maxIndex);
            List<String> tmpList = Lists.newArrayList(distinctIdList.subList(index, stop));
            this.splitProcessAsync(type, pushId, appPackage, tmpList, partOfBloomKey);
            index += stop;
        }
        distinctIdList.clear();
    }

    public void splitProcessAsync(String type, String pushId, String appPackage, List<String> distinctIdList, String partOfBloomKey) {
        executor.execute(() -> {
            // 曝光过滤
            int start = 0;
            int step = 100;
            int max = distinctIdList.size();
            // 开始过滤已曝光过的视频
            List<String> sendDistinctidList = new ArrayList<String>();
            while (start < max) {
                int stop = Math.min(max, start + step);
                Map<String, List<String>> partBloomResult = bloomFilterService.existsMuilByDistinctId(distinctIdList.subList(start, stop), partOfBloomKey);
                if (!StringUtils.isEmpty(partBloomResult.get(BloomFilterService.not_exists))) {
                    sendDistinctidList.addAll(partBloomResult.get(BloomFilterService.not_exists));
                }
                partBloomResult.clear();
                start += step;
            }
            distinctIdList.clear();

            if (isEmpty(sendDistinctidList)) {
                log.info("interestCat 应用[{}]-[{}]开始分批异步推送[{}], 其中0人未曝光, 结束推送", appPackage, pushId, partOfBloomKey);
                return;
            }

            sendDistinctidList.forEach(e -> {
                JSONObject json = new JSONObject();
                json.put(KafkaConstants.PARAM_TYPE, KafkaConstants.TYPE_NUPUSH_BLOOM);
                json.put(KafkaConstants.PARAM_PACKAGE_NAME, appPackage);
                json.put(KafkaConstants.PARAM_DISTINCT_ID, e);
                json.put(KafkaConstants.PARAM_PART_OF_BLOOM_KEY, partOfBloomKey);
                log.debug("interestCat 应用[{}]-[{}]开始分批异步推送[{}]，发送[{}]到kafka", appPackage, pushId, partOfBloomKey, e);
                kafkaTemplate.sendDefault(json.toJSONString());
                json.clear();
            });

            log.info("interestCat 应用[{}]-[{}]开始分批异步推送[{}], 其中{}人未曝光, 开始发送到MQ", appPackage, pushId, partOfBloomKey, sendDistinctidList.size());
            JSONObject mqParams = new JSONObject();
            mqParams.put("pushId", pushId);
            mqParams.put("distinctIds", sendDistinctidList);
            mqParams.put("appPackage", appPackage);
            RabbitMQSender sender = new RabbitMQSender(type, mqParams, rabbitTemplate, RabbitMqConstants.BIGDATA_POINT_NPUSH_POOL_EXCHANGE, RabbitMqConstants.BIGDATA_POINT_NPUSH_POOL_RUTEKEY);
            sender.run();
            sendDistinctidList.clear();
        });
    }
}
