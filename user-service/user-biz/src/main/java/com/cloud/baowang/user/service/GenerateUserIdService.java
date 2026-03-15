package com.cloud.baowang.user.service;

import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.user.repositories.UserInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @className: GenerateUserIdService
 * @author: wade
 * @description: 产生userId
 * @date: 9/8/24 09:12
 */
@Slf4j
@Service
@Transactional
public class GenerateUserIdService {

    private static final int USER_ID_COUNT = 1000; // 生成100个不重复的userId


    private final UserInfoRepository userInfoRepository;
    private final RedissonClient redissonClient;
    private final GenerateUserIdService self;

    public GenerateUserIdService(UserInfoRepository userInfoRepository,
                                 RedissonClient redissonClient,
                                 @Lazy GenerateUserIdService self) {
        this.userInfoRepository = userInfoRepository;
        this.redissonClient = redissonClient;
        this.self = self;
    }

    /**
     * 初始化生成1000个唯一的userId并存储到Redis中。
     */
    @DistributedLock(name = RedisConstants.USER_ID, waitTime = 0, leaseTime = RedisLockConstants.UNLOCK_TIME)
    public void initUserId() {
        log.debug("Generating 100 unique 9-digit userIds...");
        long startTime = System.currentTimeMillis();
        log.info("startTime:{}", startTime);
        try {
            generateUniqueUserIds();
            long endTime = System.currentTimeMillis();
            log.info("生成并存储1000个userIds到Redis中耗时: {}秒", (endTime - startTime) / 1000);
        } catch (Exception e) {
            log.error("启动生成 100个userId 出错");
            throw e;
        }

    }


    /**
     * 异步生成1000个唯一的userId并存储到Redis中，直到Redis中存满。
     */
    @Async
    public void generateUniqueUserIds() {
        long startTime = System.currentTimeMillis();
        log.info("异步生成1000个唯一的userId并存储到Redis中，直到Redis中存满 。开始 ：{}", startTime);
        try {
            // 去获取
            int countString = redissonClient.getSet(RedisConstants.USER_ID_LIST).size();
            while (countString < USER_ID_COUNT) {
                // 存放在redis中
                Set<String> userIdList = generate100UniqueUserIds();
                // 查询数据库，检查哪些userId已经存在
                Set<String> userIdListData = userInfoRepository.isExistsUserIdList(userIdList);
                userIdList.removeAll(userIdListData);
                if (!userIdList.isEmpty()) {
                    storeUserIdsInRedis(userIdList);
                }
                countString = redissonClient.getSet(RedisConstants.USER_ID_LIST).size();

            }
        } catch (Exception e) {
            log.error("生成userId 出错， Error occurred while ensuring Redis userId count.", e);
        }
        long endTime = System.currentTimeMillis();
        log.info("异步生成1000个唯一的userId并存储到Redis中，直到Redis中存满。结束 ：{}，大小：{}", endTime, redissonClient.getSet(RedisConstants.USER_ID_LIST).size());
        log.info("异步生成1000个唯一的userId并存储到Redis中，直到Redis中存满。花费时间 ：{}", (endTime - startTime) / 1000);

    }


    /**
     * 生成指定数量的不重复8位数字userId，第一位不能是0
     *
     * @return 生成指定数量的不重复8位数字userId 100个
     */
    public Set<String> generate100UniqueUserIds() {
        Set<String> userIds = new HashSet<>();
        while (userIds.size() < 100) {
            userIds.add(generate8DigitUserId());
        }
        return userIds;
    }

    private String generate8DigitUserId() {

        Random random = new Random();
        int firstDigit = random.nextInt(9) + 1; // 保证首位不为0
        StringBuilder userIdBuilder = new StringBuilder();
        userIdBuilder.append(firstDigit);
        for (int i = 0; i < 7; i++) {
            userIdBuilder.append(random.nextInt(10)); // 生成0-9的数字
        }
        return userIdBuilder.toString();
    }

    // 将生成的userId存储到Redis中
    private void storeUserIdsInRedis(Set<String> userIds) {
        for (String element : userIds) {
            redissonClient.getSet(RedisConstants.USER_ID_LIST).add(element);
        }
    }

    // 消费
    /*@Async
    public void consumeUserId() {
        while (true) {
            try {
                String userId = getAndRemoveRandomElementTest();
                log.info("消费userId :{}", userId);
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }*/

    /**
     * 从Redis中随机获取并删除一个userId
     *
     * @return 返回随机获取的userId
     */
    public String getAndRemoveRandomElement() {
        RSet<String> redisSet = redissonClient.getSet(RedisConstants.USER_ID_LIST);
        String userId = redisSet.removeRandom();  // 随机获取并删除一个元素
        self.generateUniqueUserIds();
        log.info("从Redis中随机获取并删除一个,redis 大小：{}, userId :{}", redissonClient.getSet(RedisConstants.USER_ID_LIST).size(), userId);
        int i = 0;
        while (StringUtils.isBlank(userId) && i < 5) {
            self.generateUniqueUserIds();
            redisSet = redissonClient.getSet(RedisConstants.USER_ID_LIST);
            userId = redisSet.removeRandom();
            log.info("从Redis中随机获取并删除一个,redis 大小：{}, userId :{}", redissonClient.getSet(RedisConstants.USER_ID_LIST).size(), userId);
            if (StringUtils.isBlank(userId)) {
                log.error("生成userId 出错， Error occurred while ensuring Redis userId count." + i);
            }
            i++;
        }
        if (StringUtils.isBlank(userId)) {
            throw new IllegalStateException("重试后仍无法获取用户ID。");
        }
        return userId;
    }
   /* public String getAndRemoveRandomElement() {
        String userId = generate8DigitUserId();
        String existsUserId = userInfoRepository.isExistsUserId(userId);
        if (ObjectUtil.isEmpty(existsUserId)) {
            return userId;
        } else {
            throw new BaowangDefaultException(ResultCode.VERIFY_CODE_LIMIT_HOUR);
        }

    }*/


}
