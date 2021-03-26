package com.miguan.laidian.common.factory;


import com.miguan.laidian.common.strategy.pushStrategy.ActivityPushStrategy;
import com.miguan.laidian.common.strategy.pushStrategy.DefaultPushStrategy;
import com.miguan.laidian.common.strategy.pushStrategy.SignInPushStrategy;
import com.miguan.laidian.common.strategy.pushStrategy.UserActionPushStrategy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PushStrategyFactory {
    public static final Map<Integer, Class> PUSH_STRATEGY = new ConcurrentHashMap<>();

    static {

        PUSH_STRATEGY.put(1, DefaultPushStrategy.class);//内容推送（默认）

        PUSH_STRATEGY.put(2, SignInPushStrategy.class);//签到推送

        PUSH_STRATEGY.put(3, ActivityPushStrategy.class);//活动推送

        PUSH_STRATEGY.put(4, UserActionPushStrategy.class);//用户行为推送
    }
}
