package com.miguan.recommend.task;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RedisDelTask {
    @Resource(name = "recDB10Pool")
    private JedisPool recDB10Pool;

    public void delKeys() throws InterruptedException {
        ScanParams scanParams = new ScanParams();
        scanParams.count(300);
        String scanRet = "0";
        try(Jedis con = recDB10Pool.getResource()){
            ScanResult ret = null;
            do{
                ret = con.scan(scanRet, scanParams.match(BloomFilterService.KEY_PREFIX + "*"));
                scanRet = ret.getCursor();
                List<String> retList = ret.getResult();
                log.info("scanRet>>{}", scanRet);
                log.info("查询到key>>{}", JSONObject.toJSONString(retList));
                retList.forEach(e->{
                    if(con.ttl(e) < 0){
                        log.info("删除key>>{}", e);
                        con.del(e);
                    }
                });
                Thread.sleep(1000);
            } while(!ret.isCompleteIteration());
        }
    }

}
