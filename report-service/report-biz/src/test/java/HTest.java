import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossMqVO;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossSendVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.report.ReportApplication;
import com.cloud.baowang.report.service.ReportUserVenueWinLoseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ReportApplication.class})
public class HTest {

    @Resource
    ReportUserVenueWinLoseService reportUserVenueWinLoseService;

    @Test
    public void test2() {
        String json = """
                {
                  "siteCode": "Vd438R",
                  "dayHour": 1729598400000,
                  "userAccount": "fairy004",
                  "userId": 88006201,
                  "currency": "USDT",
                  "agentId": null,
                  "agentAccount": null,
                  "venueCode": "PG",
                  "venueType": 4,
                  "venueGameType": 91,
                  "betAmount": 20.00,
                  "validAmount": 20.00,
                  "winLossAmount": 460.00,
                  "lastDayHour": null,
                  "lastBetAmount": 0,
                  "lastValidBetAmount": 0,
                  "lastBetWinLose": 0,
                  "lastAgentId": null,
                  "lastAgentAccount": null,
                  "betCount": 1,
                  "orderId": "PG1848703732213219328",
                  "timeZone": null
                }
                              
                """;
        reportUserVenueWinLoseService.userVenueWinLossHandler(JSON.parseObject(json,UserVenueWinLossMqVO.class));
    }

    @Test
    public void test1() {
        String json = """
                {
                  "voList": [
                    {
                      "siteCode": "Vd438R",
                      "dayHour": 1728392400000,
                      "userAccount": "hwp004",
                      "userId": 67461057,
                      "currency": "UUS",
                      "agentId": null,
                      "agentAccount": null,
                      "venueCode": "SBA",
                      "venueType": 1,
                      "venueGameType": "足球",
                      "betAmount": 1.00,
                      "validAmount": 1.00,
                      "winLossAmount": 2.35,
                      "lastValidBetAmount": 0,
                      "lastBetWinLose": 0,
                      "betCount": 1,
                      "orderId": "SBA17283917256106818390387682893-4338900_6903_U"
                    },
                    {
                      "siteCode": "Vd438R",
                      "dayHour": 1728392400000,
                      "userAccount": "hwp004",
                      "userId": 67461057,
                      "currency": "UUS",
                      "agentId": null,
                      "agentAccount": null,
                      "venueCode": "SBA",
                      "venueType": 1,
                      "venueGameType": "足球",
                      "betAmount": 1.00,
                      "validAmount": 1.00,
                      "winLossAmount": -1.00,
                      "lastValidBetAmount": 0,
                      "lastBetWinLose": 0,
                      "betCount": 1,
                      "orderId": "SBA17283917864292151560387682896-4338900_6904_U"
                    },
                    {
                      "siteCode": "Vd438R",
                      "dayHour": 1728392400000,
                      "userAccount": "hwp004",
                      "userId": 67461057,
                      "currency": "UUS",
                      "agentId": null,
                      "agentAccount": null,
                      "venueCode": "SBA",
                      "venueType": 1,
                      "venueGameType": "足球",
                      "betAmount": 1.00,
                      "validAmount": 1.00,
                      "winLossAmount": -1.00,
                      "lastValidBetAmount": 0,
                      "lastBetWinLose": 0,
                      "betCount": 1,
                      "orderId": "SBA17283917976725508880387682897-4338900_6905_U"
                    }
                  ]
                }
                """;
        UserVenueWinLossSendVO sendVO = JSON.parseObject(json, UserVenueWinLossSendVO.class);

        long start = System.currentTimeMillis();
        List<UserVenueWinLossMqVO> voList = sendVO.getVoList();
        if (Objects.isNull(voList)) {
            log.error("会员场馆盈亏报表batch-MQ队列-JSON解析异常");
            return;
        }


        boolean res = false;
        RLock fairLock = RedisUtil.getFairLock(RedisConstants.VENUE_WIN_LOSS_LOCK_KEY);
        try {
            res = fairLock.tryLock(RedisLockConstants.WAIT_TIME, RedisLockConstants.UNLOCK_TIME * 2L, TimeUnit.SECONDS);
            if (res) {

                for (UserVenueWinLossMqVO vo : voList) {
                    reportUserVenueWinLoseService.userVenueWinLossHandler(vo);
                    log.info("会员每日场馆盈亏报表-MQ队列-------------------------------执行success,耗时{}毫秒", System.currentTimeMillis() - start);
                }


            } else {
                log.error("会员场馆盈亏报表-MQ队列-------------------------------抢锁fail,耗时{}毫秒", System.currentTimeMillis() - start);
            }
        } catch (Exception e) {

            log.error("会员场馆盈亏报表-MQ队列-------------------------------执行fail", e);
        } finally {
            if (fairLock.isLocked()) {
                fairLock.unlock();
            }
        }
    }

}
