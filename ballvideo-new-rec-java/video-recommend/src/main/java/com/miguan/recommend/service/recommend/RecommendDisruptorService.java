package com.miguan.recommend.service.recommend;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.common.util.Global;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * @author zhongli
 * @date 2020-08-20 
 *
 */
@Service
@Slf4j
public class RecommendDisruptorService {
    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Resource(name = "recDB10Pool")
    private JedisPool recDB10Pool;
    Disruptor<Element> disruptor;

    @PostConstruct
    public void init() {
        // 指定RingBuffer的大小
        int bufferSize = 256;
        // 生产者的线程工厂
        ThreadFactory threadFactory = r -> new Thread(r, "recDisr");
        // 创建disruptor，采用单生产者模式
        disruptor = new Disruptor(Element::new, bufferSize, threadFactory, ProducerType.SINGLE, new BlockingWaitStrategy());
        // 设置EventHandler
        disruptor.handleEventsWith(this::consumer);
        // 启动disruptor的线程
        disruptor.start();
    }


    public void consumer(Element event, long sequence, boolean endOfBatch) {
        try {
            switch (event.getType()) {
                case 0: {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("distinct_id").is(event.getUuid()));
                    mongoTemplate.remove(query, MongoConstants.user_incentive_log);
                    break;
                }
                case 1: {
                    int expire = Global.getInt("rec_cache_expire_second");
                    try (Jedis con = recDB10Pool.getResource()) {
                        List<String> list = (List<String>) event.getValue();
                        list.forEach(e -> con.expire(e, expire ==0 ? 30 : expire));
                    } catch (Exception e) {
                    }
                }
                default:
            }
        } catch (Exception e) {
            log.error("Disruptor消费失败", e);
        }
    }

    // 队列中的元素
    @Getter
    @Setter
    public static class Element {
        private Object value;
        private String uuid;
        private int type;

    }

    public void pushEvent(int type, String uuid, Object value) {
        if (log.isDebugEnabled()) {
            log.debug("Disruptor发送事件：{} -> {}", type, value);
        }
        RingBuffer<Element> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publishEvent((event, sequence, t, u, v) -> {
            event.setType(t);
            event.setUuid(u);
            event.setValue(v);
        }, type, uuid, value);
    }
}
