package com.cloud.baowang.common.core.utils;

import com.cloud.baowang.common.core.utils.snowFlake.SingletonUtils;
import com.cloud.baowang.common.core.utils.snowFlake.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Twitter的Snowflake 算法<br>
 * 分布式系统中，有一些需要使用全局唯一ID的场景，有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。
 *
 * <p>
 * snowflake的结构如下(每部分用-分开):<br>
 *
 * <pre>
 * 符号位（1bit）- 时间戳相对值（41bit）- 数据中心标志（5bit）- 机器标志（5bit）- 递增序号（12bit）
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * </pre>
 * <p>
 * 第一位为未使用(符号位表示正数)，接下来的41位为毫秒级时间(41位的长度可以使用69年)<br>
 * 然后是5位datacenterId和5位workerId(10位的长度最多支持部署1024个节点）<br>
 * 最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
 * <p>
 * 并且可以通过生成的id反推出生成时间,datacenterId和workerId
 * <p>
 * 参考：http://www.cnblogs.com/relucent/p/4955340.html<br>
 * 关于长度是18还是19的问题见：https://blog.csdn.net/unifirst/article/details/80408050
 *
 * @author Looly
 * @since 3.0.1
 */
@Slf4j
public class SnowFlakeUtils {
    private final static String REDBAG_PREFIX = "S";//红包雨场次
    /**
     * 大写字母+数字
     */
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";


    /**
     * 构造雪花算法生成器 单例模式
     * @return 雪花算法生成器
     */
    public static SnowflakeIdWorker getSnowflake() {
        return SingletonUtils.get(SnowflakeIdWorker.class);
    }

    /**
     * 生成雪花算法Id 长度 18
     * @return 18个长度
     */
    public static String getSnowId() {
        return String.valueOf(getSnowflake().nextId());
    }


    /**
     * 可以自定义 workId 获取自增序列
     * 服务器是同一台 dataCenterId 一定相同,workId 可以不一定相同
     * 比如 wallet服务在同一台,传入站点编号,可以生成不同的workId,从而区分出不同的自增序列
     * @param selfWorkNumStr 自定义 workId 字符串
     * @return 获取自增序列
     */
    public static String getSnowIdBySelfCenterId(String selfWorkNumStr) {
        SnowflakeIdWorker snowflakeIdWorker =  SingletonUtils.get(SnowflakeIdWorker.class,selfWorkNumStr);
        return String.valueOf(snowflakeIdWorker.nextId());
    }


    /**
     * 随机生成 8个长度的字符串
     * 大写字母+数字
     * @return 8个长度的 大写字母+数字 字符串
     */
    public static String getCommonRandomId() {
        int totalLen=8;
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(totalLen);
        for (int i = 0; i < totalLen; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }

    /**
     * 随机生成 制定长度的随机数加数字
     * 大写字母+数字
     * @return 8个长度的 大写字母+数字 字符串
     */
    public static String getCommonRandomIdByLenght(int totalLen) {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(totalLen);
        for (int i = 0; i < totalLen; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }


    public static String genRedBagSessionId() {
        return REDBAG_PREFIX + getSnowflake().nextId();
    }


    public static String getSevenNumber () {
        Long id = Long.parseLong(getSnowId());
        StringBuilder sb= new StringBuilder(id+"");
        StringBuilder reverse = sb.reverse();
        id= Long.parseLong(reverse.toString())/1000;
        while(id>19999999){
            id/=10;
        }
        return id.toString();
    }
    public static String getRandomZm(){
        Random random = new Random();
        char upperCaseLetter1 = (char) ('A' + random.nextInt(26));
        char upperCaseLetter2 = (char) ('A' + random.nextInt(26));
        return String.valueOf(upperCaseLetter1) + upperCaseLetter2;
    }


}
