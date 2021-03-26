package com.miguan.reportview.service;

import com.alibaba.fastjson.JSON;
import com.miguan.reportview.common.utils.SerializeUtil;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xujinbang
 * @date 2019/11/8.
 */
@Service
public class RedisService {

    @Resource(name="defaultPool")
    private JedisPool jedisPool;

    /**
     * h获取单个对象
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(String key, Class<T> clazz){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String str = jedis.get(key);
            T t = stringToBean(str, clazz);
            return t;
        }finally {
            jedis.close();
        }
    }

    public String get(String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String str = jedis.get(key);
            return str;
        }finally {
            jedis.close();
        }
    }

    /**
     * 设置缓存值
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean set(String key, T value,int second){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if(str == null || str.length() <= 0){
                return false;
            }
            if(second <= 0){
                jedis.set(key, str);
            } else {
                jedis.setex(key, second, str);
            }
            return true;
        } finally {
            jedis.close();
        }
    }

    /**
     * 设置缓存值
     * @param key
     * @param second
     * @param value
     * @return
     */
    public boolean sadd(String key,int second,String... value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            if(value == null){
                return false;
            }
            jedis.sadd(key,value);
            if(second > 0){
                jedis.expire(key,second);
            }
            return true;
        } finally {
            jedis.close();
        }
    }

    /**
     * 设置hash缓存值
     * @param key
     * @param second
     * @param value
     * @return
     */
    public boolean hmset(String key,int second,Map<String,String> value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            if(value == null || value.isEmpty()){
                return false;
            }
            jedis.hmset(key,value);
            if(second > 0){
                jedis.expire(key,second);
            }
            return true;
        } finally {
            jedis.close();
        }
    }

    /**
     * 设置hash缓存值
     * @param key
     * @param videoIds
     * @return
     */
    public List<String> hmget(String key,String... videoIds){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.hmget(key,videoIds);
        } finally {
            jedis.close();
        }
    }

    /**
     * 随机从redis中获取指定条数
     * @param key
     * @param count
     * @return
     */
    public List<String> randomValue(String key, int count) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.srandmember(key,count);
        } finally {
            jedis.close();
        }
    }


    private <T> String beanToString(T value) {
        if(value == null){
            return null;
        }
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class){
            return "" + value;
        }else if(clazz == String.class){
            return (String) value;
        } else if(clazz == long.class || clazz == Long.class){
            return "" + value;
        }else{
            return JSON.toJSONString(value);
        }
    }

    private <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }

        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }


    /**
     * 判断是否存在
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean exits(String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.exists(key);
        } finally {
            jedis.close();
        }
    }


    /**
     * 增加值
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.incr(key);
        } finally {
            jedis.close();
        }

    }

    /**
     * 减少值
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long decr(String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.decr(key);
        } finally {
            jedis.close();
        }

    }

    public <T> boolean setByByte(String key, T value, int second) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.setex(key.getBytes(), second, SerializeUtil.serialize(value));
            return true;
        } finally {
            jedis.close();
        }
    }

    public boolean setBit(String key, long offset,boolean value, Integer second) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.setbit(key,offset,value);
            if(second != null) {
                jedis.expire(key,second);
            }
            return true;
        } finally {
            jedis.close();
        }
    }

    /**
     * h获取单个对象
     * @param key
     * @return
     */
    public byte[] getByte(String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.get(key.getBytes());
        }finally {
            jedis.close();
        }
    }

    public long bitcount(String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.bitcount(key.getBytes());
        }finally {
            jedis.close();
        }
    }


    public <T> Object getByByte(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            byte[] b = jedis.get(key.getBytes());
            return SerializeUtil.unSerialize(b);
        } finally {
            jedis.close();
        }
    }

    public <T> void delByByte(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key.getBytes());
        } finally {
            jedis.close();
        }
    }

    public <T> void del(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
        } finally {
            jedis.close();
        }
    }

    public Set<String> keys(String key) {
        Jedis jedis = null;
        Set<String> keys = null;
        try {
            jedis = jedisPool.getResource();
            keys = jedis.keys(key);
            return keys;
        } finally {
            jedis.close();
        }
    }

    public String hget(String key,String field) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hget(key,field);
        } finally {
            jedis.close();
        }
    }

    /**
     * 判断是否存在
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean hexits(String key,String field){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.hexists(key,field);
        } finally {
            jedis.close();
        }
    }

    public void hset(String key,String field,String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hset(key,field,value);
        } finally {
            jedis.close();
        }
    }

    public void hset(String key,String field,String value,int secondes) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hset(key,field,value);
            jedis.expire(key,secondes);
        } finally {
            jedis.close();
        }
    }

    public <T> void hdel(String key,String field) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hdel(key,field);
        } finally {
            jedis.close();
        }
    }

    public void hmset(String key,Map<String,String> value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hmset(key,value);
        } finally {
            jedis.close();
        }
    }
    public Map<String,String> hgetAll(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hgetAll(key);
        } finally {
            jedis.close();
        }
    }

    public void append(String key,String value,int second) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.append(key,value);
            if(second > 0){
                jedis.expire(key,second);
            }
        } finally {
            jedis.close();
        }
    }

    /**
     * 通过key向list头部添加字符串
     *
     * @param key
     * @param strs 可以是一个string 也可以是string数组
     * @return 返回list的value个数
     */
    public Long lpush(String key, String... strs) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lpush(key, strs);
        } finally {
            jedis.close();
        }
    }

    /**
     * 通过key向list尾部添加字符串
     *
     * @param key
     * @param strs 可以是一个string 也可以是string数组
     * @return 返回list的value个数
     */
    public Long rpush(String key, String... strs) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.rpush(key, strs);
        } finally {
            jedis.close();
        }
    }

    /**
     * 通过key获取list指定下标位置的value
     * 如果start 为 0 end 为 -1 则返回全部的list中的value
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> lrange(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lrange(key, start, end);
        } finally {
            jedis.close();
        }
    }
}
