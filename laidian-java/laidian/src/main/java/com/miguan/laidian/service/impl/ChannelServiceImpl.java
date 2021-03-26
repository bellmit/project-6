package com.miguan.laidian.service.impl;

import com.miguan.laidian.entity.Channel;
import com.miguan.laidian.redis.service.RedisService;
import com.miguan.laidian.service.ChannelService;
import com.miguan.laidian.service.MoFangService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 2019年10月29日21:26:58  HYL
 */
@Service
public class ChannelServiceImpl implements ChannelService {

    @Resource
    private MoFangService moFangService;

    @Resource
    private RedisService redisService;

    @PostConstruct
    @Override
    public void ChannelInit() {
        //跨库查询魔方后台数据，获取渠道id和供应商
        redisService.del(Channel.CHANNEL_REDIS);
        //跨库查询魔方后台数据，获取渠道id和供应商
        List<Channel> channels = moFangService.getChannels();
        Map<String, String> collect = channels.stream().collect(Collectors.toMap(Channel::getDomain, Channel::getChannelId));
        redisService.hmset(Channel.CHANNEL_REDIS, collect);
    }
}
