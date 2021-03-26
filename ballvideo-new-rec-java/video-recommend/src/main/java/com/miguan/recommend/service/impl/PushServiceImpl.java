package com.miguan.recommend.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.common.constants.RabbitMqConstants;
import com.miguan.recommend.common.constants.SymbolConstants;
import com.miguan.recommend.common.util.Global;
import com.miguan.recommend.mapper.UserCatPoolMapper;
import com.miguan.recommend.service.PushService;
import com.miguan.recommend.service.RabbitMQSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
@Service
public class PushServiceImpl implements PushService {

    @Resource
    private UserCatPoolMapper userCatPoolMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    ExecutorService executor = new ThreadPoolExecutor(2, 1000, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(3000));

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
    public void findAndSendToMq(String type, JSONObject param) {
        String pushId = param.getString("pushId");
        String videoId = param.getString("videoId");
        String appPackage = param.getString("appPackage");

        List<String> appPackageList = Arrays.asList(appPackage.split(SymbolConstants.comma));
        List<Integer> catList = Stream.of(param.getString("catIds").split(SymbolConstants.comma)).map(Integer::valueOf).collect(Collectors.toList());

        String whiteChannelStr = Global.getValue("push_hand_white_list_channel");
        List<String> whiteChannels = isEmpty(whiteChannelStr) ? null : Arrays.asList(whiteChannelStr.split(SymbolConstants.comma));

        String partOfBloomKey = isEmpty(videoId) ? "npush-" + pushId : videoId;
        appPackageList.forEach(e -> {
            Long needSendCount = userCatPoolMapper.countDistinictIdWhereTopThreeInCatids(e, catList, whiteChannels);
            log.info("NPushMQ 预估需要推送的用户{}人", needSendCount);
            int size = 500;
            long threadNum = needSendCount % size > 0 ? (needSendCount / size) + 1 : needSendCount / size;
            for (int i = 0; i < threadNum; i++) {
                int skip = i * size;
                this.proessBatchAsyn(type, pushId, e, catList, whiteChannels, skip, size, partOfBloomKey);
            }
        });
    }

    private void proessBatchAsyn(String type, String pushId, String appPackage, List<Integer> catList, List<String> whiteChannels, int skip, int size, String partOfBloomKey) {
        executor.execute(() -> {
            List<String> distinictIds = userCatPoolMapper.findDistinictIdWhereTopThreeInCatids(appPackage, catList, whiteChannels, skip, size);
            // TODO 曝光过滤


            JSONObject mqParams = new JSONObject();
            mqParams.put("pushId", pushId);
            mqParams.put("distinctIds", distinictIds);
            mqParams.put("appPackage", appPackage);
            log.info("NPushMQ 参数-> {}", mqParams);
            log.info("NPushMQ 兴趣标签-> {}", catList);
            Thread thread = new Thread(new RabbitMQSender(type, mqParams, rabbitTemplate, RabbitMqConstants.BIGDATA_POINT_NPUSH_POOL_EXCHANGE, RabbitMqConstants.BIGDATA_POINT_NPUSH_POOL_RUTEKEY));
            thread.start();
        });
    }
}
