package com.miguan.ballvideo.common.factory;

import com.miguan.ballvideo.common.strategy.pushStrategy.DefaultStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PushStrategyFactory {
    public static final Map<Integer, Class> PUSH_STRATEGY = new ConcurrentHashMap<>();

    static {
        PUSH_STRATEGY.put(-1, DefaultStrategy.class);
        //PUSH_STRATEGY.put(1, NewUserStrategy.class);
    }
}
