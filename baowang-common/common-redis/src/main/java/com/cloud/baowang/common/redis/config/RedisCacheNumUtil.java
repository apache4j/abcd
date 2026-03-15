package com.cloud.baowang.common.redis.config;

import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * @Author: ford
 * @Since: 2025-08-11
 * 数字缓存相关,直接从缓存中移除,防止重复使用
 * 例如: agentId 7位数字 先事先缓存好,后续直接获取 防止重复
 */
@Component
@Slf4j
public class RedisCacheNumUtil {

    private static final long MAX_FILL_NUM=1000;

  /*  public static void main(String[] args) {
        fillCacheNum("aaa",3);
    }*/

    /**
     * 生成唯一短id
     * @return 长度为8的数字串
     */
    public static  String genCommonShortId(String redisKey,Integer numLen){
        Long userId= (Long) RedisUtil.rPop(redisKey);
        if(userId==null){
            //获取数据为空 重新补充
            if(numLen==null){
                numLen=8;
            }
            fillCacheNum(redisKey,numLen);
            userId= (Long) RedisUtil.rPop(redisKey);
        }
        return userId.toString();
    }

    /**
     * 获取代理ID
     * @return 长度为7的数字串
     */
    public static String genAgentId(){
        Long agentId= (Long) RedisUtil.rPop(RedisKeyTransUtil.CACHE_NUM_AGENT_ID);
        if(agentId==null){
            //获取数据为空 重新补充
            fillCacheNum(RedisKeyTransUtil.CACHE_NUM_AGENT_ID,7);
            agentId= (Long) RedisUtil.rPop(RedisKeyTransUtil.CACHE_NUM_AGENT_ID);
        }
        return agentId.toString();
    }

    /**
     * 获取商务ID
     * @return 长度为7的数字串
     */
    public static  String genMerId(){
        Long merId= (Long) RedisUtil.rPop(RedisKeyTransUtil.CACHE_NUM_MERCHANT_ID);
        if(merId==null){
            //获取数据为空 重新补充
            fillCacheNum(RedisKeyTransUtil.CACHE_NUM_MERCHANT_ID,7);
            merId= (Long) RedisUtil.rPop(RedisKeyTransUtil.CACHE_NUM_MERCHANT_ID);
        }
        return merId.toString();
    }

    /**
     * 获取总用户ID
     * @return 长度为7的数字串
     */
    public static  String genUserId(){
        Long userId= (Long) RedisUtil.rPop(RedisKeyTransUtil.CACHE_NUM_USER_ID);
        if(userId==null){
            //获取数据为空 重新补充
            fillCacheNum(RedisKeyTransUtil.CACHE_NUM_USER_ID,8);
            userId= (Long) RedisUtil.rPop(RedisKeyTransUtil.CACHE_NUM_USER_ID);
        }
        return userId.toString();
    }


    /**
     * 获取总站用户ID
     * @return 长度为7的数字串
     */
    public static  String genAdminId(){
        Long userId= (Long) RedisUtil.rPop(RedisKeyTransUtil.CACHE_NUM_ADMIN_ID);
        if(userId==null){
            //获取数据为空 重新补充
            fillCacheNum(RedisKeyTransUtil.CACHE_NUM_ADMIN_ID,7);
            userId= (Long) RedisUtil.rPop(RedisKeyTransUtil.CACHE_NUM_ADMIN_ID);
        }
        return userId.toString();
    }

    /**
     * 站点用户ID
     * @return 长度为7的数字串
     */
    public static  String genSiteAdminId(){
        Long userId= (Long) RedisUtil.rPop(RedisKeyTransUtil.CACHE_NUM_SITE_ADMIN_ID);
        if(userId==null){
            //获取数据为空 重新补充
            fillCacheNum(RedisKeyTransUtil.CACHE_NUM_SITE_ADMIN_ID,7);
            userId= (Long) RedisUtil.rPop(RedisKeyTransUtil.CACHE_NUM_SITE_ADMIN_ID);
        }
        return userId.toString();
    }

    /**
     * 向缓存队列中填充数字 每次填充1000个
     * @param key 缓存队列key
     * @param numLen 数字长度
     */
    public  static void fillCacheNum(String key,int numLen){
        if(numLen<3){
            throw new RuntimeException("最小长度不能小于3");
        }
        long startTime=System.currentTimeMillis();
        boolean firstInitFlag=true;//是否是初次初始化
        long minNum=BigInteger.TEN.pow(numLen-1).longValue();
        long maxNum=BigInteger.TEN.pow(numLen).subtract(BigInteger.ONE).longValue();
       // log.info("minNum:{},maxNum:{}",minNum,maxNum);
        String minNumCacheKey=key.concat(":min");
        boolean minNumFlag=RedisUtil.isKeyExist(minNumCacheKey);
        if(minNumFlag){
            long cacheMinNum=RedisUtil.getValue(minNumCacheKey);
            //超过最大值之后 从头开始
            if(minNum<cacheMinNum && cacheMinNum<maxNum){
                firstInitFlag=false;
                minNum=cacheMinNum;
            }
        }
        List<Long> resultList= Lists.newArrayList();
        long cacheMinNum=minNum;
        long tmpNum=0;
        long maxTempNum=MAX_FILL_NUM-1;
        if(!firstInitFlag){
            tmpNum=1;
            maxTempNum=MAX_FILL_NUM;
        }
         for(;tmpNum<=maxTempNum;tmpNum++){
            cacheMinNum=minNum+tmpNum;
           // System.err.println(cacheMinNum);
            resultList.add(cacheMinNum);
        }
        RedisUtil.setValue(minNumCacheKey,cacheMinNum);
        Collections.shuffle(resultList);//顺序打乱
        resultList.forEach(cacheVal-> {
            RedisUtil.lPush(key,cacheVal);
             }
        );
        long endTime=System.currentTimeMillis();
        log.info("数据填充耗时:{}ms",(endTime-startTime));
    }


}
