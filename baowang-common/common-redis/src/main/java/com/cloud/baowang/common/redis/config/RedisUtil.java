package com.cloud.baowang.common.redis.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
@Component
public class RedisUtil implements ApplicationContextAware {

    private static RedissonClient redissonClient;


    /**
     * 立即获取redis锁，不会等待
     *
     * @param lockName   锁名称
     * @param expireTime 锁过期时间（单位: 秒）
     * @return 返回为NULL 则代表没有获取到锁
     */
    public static String acquireImmediate(String lockName, Long expireTime) {
        return doAcquire(lockName, expireTime);
    }

    /**
     * 在规定时间内，尝试获取redis锁
     *
     * @param lockName   锁名称
     * @param expireTime 锁过期时间（单位: 秒）
     * @return 返回为NULL 则代表没有获取到锁
     */
    private static String doAcquire(String lockName, Long expireTime) {
        String lock = IdWorker.get32UUID();


        if (expireTime == null) {
            expireTime = 10L;
        }

        boolean block = redissonClient.getBucket(lockName).setIfAbsent(lock, Duration.ofSeconds(expireTime));
        if (block) {
            return lock;
        }
        return null;
    }


    /**
     * 释放 lockName 的锁资源, 以 lock 值匹配确保释放锁的当前应用持有该锁资源
     *
     * @param lockName 锁的名称
     * @param lock     锁的唯一值
     * @return true 释放锁成功,false 释放锁失败
     */
    public static boolean release(String lockName, String lock) {
        String lockValue = getValue(lockName);
        if (lockValue == null) {
            return true;
        }
        if (!StrUtil.isBlank(lock) && lock.equals(lockValue)) {
            return redissonClient.getBucket(lockName).delete();
        }
        return false;
    }


    /**
     * 判斷Redis key是否存在
     *
     * @param key Redis key
     * @return 存在:true, 不存在: false
     */
    public static boolean isKeyExist(final String key) {
        boolean isKeyExist = false;
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (ObjUtil.isNotEmpty(bucket)) {
            isKeyExist = bucket.isExists();
        }
        return isKeyExist;
    }

    /**
     * 加入Reids key and value，不指定過期時間
     *
     * @param key   鍵值
     * @param value 數值
     */
    public static void setValue(final String key, final Object value) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (ObjUtil.isNotEmpty(bucket)) {
            bucket.set(value);
        }
    }

    /**
     * 加入Redis key and value 並且指定過期時間(秒)
     *
     * @param key        鍵值
     * @param value      數值
     * @param expireTime 過期時間(秒)
     */
    public static void setValue(final String key, final Object value, final Long expireTime) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (ObjUtil.isNotEmpty(bucket)) {
            bucket.set(value, expireTime, TimeUnit.SECONDS);
        }
    }

    /**
     * 加入Redis key and value 並且指定過期時間
     *
     * @param key        鍵值
     * @param value      數值
     * @param expireTime 過期時間
     * @param timeUnit   時間類型Enum
     */
    public static void setValue(final String key, final Object value, final Long expireTime, final TimeUnit timeUnit) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (ObjUtil.isNotEmpty(bucket)) {
            bucket.set(value, expireTime, timeUnit);
        }
    }

    public static <T> T getValue(final String key) {
        T value = null;
        RBucket<T> bucket = redissonClient.getBucket(key);
        if (bucket.isExists()) {
            value = bucket.get();
        }
        return value;
    }

    public static Iterable<String> getKeysByPattern(final String keyPattern) {
        return redissonClient.getKeys().getKeysByPattern(keyPattern);
    }

    public static boolean deleteKey(final String key) {
        return redissonClient.getBucket(key).delete();
    }

    public static Integer deleteKeyByList(final List<String> keys) {
        int i = 0;
        if (CollectionUtil.isEmpty(keys)) {
            return i;
        }
        for (String key : keys) {
            boolean bool = redissonClient.getBucket(key).delete();
            i += bool ? 1 : 0;
        }
        return i;
    }


    /**
     * 对指定的key进行自增操作，如果key不存在，默认从0开始
     *
     * @param key   自增的key
     * @param count 增加的值（例如 1 表示加1）
     * @return 自增后的值
     */
    public static long incr(final String key, final long count) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        return atomicLong.addAndGet(count);
    }


    /**
     * 该方法 调用 incr 自增,并且设置超时时间,但是超时时间只针对第一次自增时生效,后期二次调用超时时间是不处理
     */
    public static long incrAndExpirationFirst(final String key, final long count, final long time) {
        long resultCount = incr(key, count);
        long timeOut = getRemainExpireTime(key);
        if (timeOut <= 0 && time > 0) {
            setKeyExpiration(key, time);
        }
        return resultCount;
    }


    public static long incr(final String key, final long count, final long expireSeconds) {
        Object l = redissonClient.getBucket(key).get();
        long p = 0;
        if (ObjUtil.isNotEmpty(l)) {
            p = Long.parseLong(l.toString());
        }
        if (expireSeconds <= 0) {
            redissonClient.getBucket(key).set(p + count);
        } else {
            redissonClient.getBucket(key).set(p + count, expireSeconds, TimeUnit.SECONDS);
        }

        return p + count;
    }

    public static Long getAtomicLong(final String key) {
        return redissonClient.getAtomicLong(key).get();
    }

    public static Long atomicLongIncr(final String key, final long expireSeconds) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        Long increment = atomicLong.incrementAndGet();
        if (expireSeconds >= 0) {
            atomicLong.expire(expireSeconds, TimeUnit.SECONDS);
        }
        return increment;
    }

    public static boolean setKeyExpiration(final String key, final long expireSeconds) {
        return redissonClient.getBucket(key).expire(Duration.ofSeconds(expireSeconds));
    }

    /**
     * 剩余多少秒到期
     */
    public static Long getRemainExpireTime(String key) {
        long expireTime = redissonClient.getBucket(key).getExpireTime();
        if (expireTime < 0L) {
            return 0L;
        }
        return (expireTime - System.currentTimeMillis()) / 1000;
    }

    public static RLock getFairLock(String lockKey) {
        return redissonClient.getFairLock(lockKey);
    }

    public static RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    public static <T> RSet<T> getSet(String key) {
        return redissonClient.getSet(key);
    }

    public static <T> boolean setSet(String key, Set<T> set) {
        if (CollUtil.isEmpty(set)) {
            return true;
        }
        return redissonClient.getSet(key).addAll(set);
    }

    /**
     * 将单个元素添加到 Redis 中的集合（RSet）中。
     * 如果元素为空，则不执行任何操作。
     * 此方法不设置过期时间，元素会永久保存在 Redis 中，直到手动删除。
     *
     * @param key  Redis 中集合的键
     * @param item 要添加到集合中的单个元素
     * @param <T>  集合元素的类型
     * @return 如果添加成功则返回 true；如果集合中已存在该元素则返回 false
     */
    public static <T> boolean setSet(String key, T item) {
        // 检查 item 是否为空，避免添加空元素
        if (item == null) {
            return true;
        }
        // 获取 Redis 中的 RSet 实例
        RSet<T> rSet = redissonClient.getSet(key);
        // 将单个元素添加到集合中
        return rSet.add(item);
    }

    public static <T> RList<T> getList(String key) {
        return redissonClient.getList(key);
    }

    public static <T> boolean setList(String key, List<T> list) {
        RList<T> rList = redissonClient.getList(key);
        rList.clear();
        return rList.addAll(list);
    }

    public static <T> boolean setList(String key, List<T> list, final Long expireTime, final TimeUnit timeUnit) {
        // 获取 RList 实例
        RList<T> rList = redissonClient.getList(key);
        rList.clear();
        // 添加所有元素到列表
        rList.addAll(list);

        // 设置超时时间
        return rList.expire(expireTime, timeUnit);
    }

    public static <T> boolean setSet(String key, Set<T> list, final Long expireTime, final TimeUnit timeUnit) {
        // 获取 RSet 实例
        RSet<T> rSet = redissonClient.getSet(key);
        rSet.clear();
        // 添加所有元素到集合
        rSet.addAll(list);
        // 设置超时时间
        return rSet.expire(expireTime, timeUnit);
    }


    public static Object expireList(String key, Long expireTime) {
        return redissonClient.getList(key).expire(Instant.ofEpochSecond(expireTime));
    }

    /**
     * 入队列操作
     * @param key
     * @return
     * @param <T> 入队到尾部
     */
    public static <T> boolean lPush(String key,T val) {
        // 获取 RSet 实例
        RList<T> rList = redissonClient.getList(key);
        // 添加所有元素到集合
        return rList.add(val);
    }

    /**
     * 出队列操作
     * @param key
     * @return  从队列头部出队（类似于 FIFO）
     */
    public static <T> Object rPop(String key) {
        Object retVal =null;
        if(isKeyExist(key)){
            // 获取 RSet 实例
            RList<T> rList = redissonClient.getList(key);
            retVal=rList.get(0);
            rList.remove(0);
            if (rList.isEmpty()) {
                // 队列为空,删除键
                redissonClient.getKeys().delete(key);
            }
        }
        return retVal;
    }


    public static <K, V> RMap<K, V> getMap(String mapName) {
        return redissonClient.getMap(mapName);
    }

    public static Object getMapValue(String mapName, String key) {
        return redissonClient.getMap(mapName).get(key);
    }

    public static Object setMap(String mapName, String key, Object value) {
        return redissonClient.getMap(mapName).put(key, value);
    }

    public static Object setMapOfExpireTime(String mapName, String key, Object value, Long expireTime) {
        redissonClient.getMap(mapName).expire(Instant.ofEpochSecond(expireTime));
        return redissonClient.getMap(mapName).put(key, value);
    }

    public static Object expireMap(String mapName, Long expireTime) {
        return redissonClient.getMap(mapName).expire(Instant.ofEpochSecond(expireTime));
    }


    public static <K, V> RMapCache<K, V> getMapCache(String mapName) {
        return redissonClient.getMapCache(mapName);
    }

    public static Integer getMapCacheCount(String mapName) {
        return redissonClient.getMapCache(mapName).size();
    }

    public static Object setMapCache(String mapName, String key, Object value) {
        return redissonClient.getMapCache(mapName).put(key, value);
    }

    public static Object getMapCacheValue(String mapName, String key) {
        return redissonClient.getMapCache(mapName).get(key);
    }

    public static Object setMapCacheOfExpireTime(String mapName, String key, Object value, Long expireTime, TimeUnit timeUnit) {
        return redissonClient.getMapCache(mapName).put(key, value, expireTime, timeUnit);
    }

    public static void expireMapCache(String mapName, Long expireTime) {
        redissonClient.getMapCache(mapName).expire(Instant.ofEpochSecond(expireTime));
    }

    private static final Map<String, RLocalCachedMap<Object, Object>> localCachedMap = Maps.newConcurrentMap();

    public static RLocalCachedMap<Object, Object> getRLocalCachedMap(String redisKey) {
        RLocalCachedMap<Object, Object> map = localCachedMap.get(redisKey);
        if (map == null) {
            synchronized (RedisUtil.class) {
                if (map == null) {
                    LocalCachedMapOptions<Object, Object> options = LocalCachedMapOptions.defaults()
                            .cacheProvider(LocalCachedMapOptions.CacheProvider.CAFFEINE)
                            .maxIdle(1000 * 1000)// 本地缓存空闲时间 1000s
                            .evictionPolicy(LocalCachedMapOptions.EvictionPolicy.NONE)
                            .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR)
                            .syncStrategy(LocalCachedMapOptions.SyncStrategy.INVALIDATE);

                    map = redissonClient.getLocalCachedMap(redisKey, options);
                    localCachedMap.put(redisKey, map);
                }
            }
        }
        return map;
    }

    public static void localCacheMapClear(final String redisKey) {
        RLocalCachedMap<Object, Object> rLocalCachedMap = getRLocalCachedMap(redisKey);
        rLocalCachedMap.clear();
    }

    public static void setLocalCachedMap(final String redisKey, final String mapKey, final Object mapValue) {
        RLocalCachedMap<Object, Object> rLocalCachedMap = getRLocalCachedMap(redisKey);
        rLocalCachedMap.fastPut(mapKey, mapValue);
    }

    public static Object getLocalCachedMap(final String redisKey, final String mapKey) {
        RLocalCachedMap<Object, Object> rLocalCachedMap = getRLocalCachedMap(redisKey);
        return rLocalCachedMap.get(mapKey);
    }

    public static void deleteLocalCachedMap(final String redisKey, final String mapKey) {
        RLocalCachedMap<Object, Object> rLocalCachedMap = getRLocalCachedMap(redisKey);
        rLocalCachedMap.fastRemove(mapKey);
    }

    public static long deleteKeysByPattern(final String redisKey) {
        long count = 0;
        RKeys rKeys = redissonClient.getKeys();
        // 获取匹配的键并删除
        Iterable<String> keys = rKeys.getKeysByPattern(redisKey);
        for (String key : keys) {
            count += rKeys.delete(key);
        }
        return count;
    }



    public static long deleteKeysByPatternList(final List<String> redisKey) {
        long i = 0;
        for (String key : redisKey) {
            RKeys rKeys = redissonClient.getKeys();
            // 删除符合模式的键
            i += rKeys.deleteByPattern(key);
        }
        return i;
    }

    public static <T> RDelayedQueue<T> getDelayedQueue(final String queue) {
        return redissonClient.getDelayedQueue(redissonClient.getQueue(queue));
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RedisUtil.redissonClient = applicationContext.getBean(RedissonClient.class);
    }

    public static Object containLocalCachedMap(final String redisKey, final String mapKey) {
        RLocalCachedMap<Object, Object> rLocalCachedMap = getRLocalCachedMap(redisKey);
        String keyword = ".*".concat(mapKey.concat(".*"));
        Pattern compile = Pattern.compile(keyword);
        // 模糊匹配
        for (Map.Entry<Object, Object> key : rLocalCachedMap.entrySet()) {
            if (Objects.nonNull(key.getKey()) && compile.matcher((String) key.getKey()).matches()) {
                return key.getValue();
            }
        }
        // 精确匹配
        return rLocalCachedMap.get(mapKey);
    }
}
