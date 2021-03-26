package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.mapper.GitMapper;
import com.miguan.ballvideo.repositories.GitDao;
import com.miguan.ballvideo.service.GiService;
import com.miguan.ballvideo.entity.Git;
import org.cgcg.redis.core.annotation.RedisCache;
import org.cgcg.redis.core.annotation.RedisNameSpace;
import org.cgcg.redis.core.enums.RedisTimeUnit;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
/**
 * RedisNameSpace缓存参数说明：
 * cache ->hash的name
 * expire->过期时间默认值是7200 可以填properties的key，也可以写固定值
 * unit-> 时间单位 RedisTimeUnit.SECONDS 默认是秒
 *
 * RedisNameSpace配合RedisCache使用，RedisCache优先的原则
 */
@Service
@RedisNameSpace(cache = "GitServiceImpl" , expire = "7200", unit = RedisTimeUnit.SECONDS)
public class GitServiceImpl implements GiService {

    @Resource
    private GitMapper gitMapper;

    @Resource
    private GitDao gitDao;

    /**
     * RedisCache缓存参数说明：
     * cache ->hash的name
     * key   ->hash里面的KV的key 支持SPEL表达式 #p0.id ，也可以写固定值 xxx
     * expire->过期时间默认值是7200 可以填properties的key，也可以写固定值
     * timeUnit-> 时间单位 RedisTimeUnit.SECONDS 默认是秒
     * type  -> 操作类型 RedisEnum.SEL,RedisEnum.UPD,RedisEnum.DEL 默认是SEL（查询）
     * lock -> 是否加锁 LockUnit.NULL LockUnit.LOCK LockUnit.UNLOCK ， 默认是NULL，自动根据操作类型判断是否加锁，其中UPD和DEL会自动上锁
     * @auth zhicong.lin
     * @date 2019/6/21
     */
    @RedisCache(cache = "queryProductList", key = "XXXX", expire="61", timeUnit=RedisTimeUnit.SECONDS)
    public List<Map<String, Object>> queryProductList(Map<String, Object> map) {
        return gitMapper.queryProductList(map);
    }

    public List<Git> findAll() {
        Git git = gitDao.findByLoginName("18502511111-3");
        List<Git> gitList = gitDao.findListByLoginName("18502511111");
        Git git1 = gitDao.findByLoginNameAndLoginPwd("18502511111", "12");
        List<Git> git2 = gitDao.queryUser("18502511111");
        List<Git> git3 = gitDao.queryUserParam("18502511111");
        return gitDao.findAll();
    }

    public void saveGit() {
        Git git = new Git();
        git.setLoginName("zbl");
        git.setLoginPwd("123123");
        gitDao.save(git);
    }
}
