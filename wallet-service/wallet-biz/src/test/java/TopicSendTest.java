import com.alibaba.fastjson.JSONObject;
import com.cloud.baowang.common.core.utils.RandomStringUtil;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.wallet.WalletApplication;
import com.cloud.baowang.wallet.api.vo.mq.UserGamePayoutMqVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2025/3/13 13:35
 * @Version: V1.0
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WalletApplication.class})
public class TopicSendTest {

    @Test
    public void sendThirdGame() throws InterruptedException {
        int i=1;
        do{
            String userGamePayoutMqJson="{\"msgId\":\"1d7d97bc7da39a69c2998d3a56856337\",\"traceId\":\"\",\"userRecordPayoutVOList\":[{\"betAmount\":100.00,\"orderClassify\":1,\"payoutAmount\":101.00,\"thirdOrderId\":\"1688\",\"userAccount\":\"pf2cdavis2\",\"userId\":\"12138010\",\"venueCode\":\"S128\"},{\"betAmount\":50.00,\"orderClassify\":1,\"payoutAmount\":50.50,\"thirdOrderId\":\"1689\",\"userAccount\":\"pf2cdavis2\",\"userId\":\"12138010\",\"venueCode\":\"S128\"},{\"betAmount\":50.00,\"orderClassify\":1,\"payoutAmount\":50.50,\"thirdOrderId\":\"1694\",\"userAccount\":\"pf2cdavis2\",\"userId\":\"12138010\",\"venueCode\":\"S128\"},{\"betAmount\":50.00,\"orderClassify\":1,\"payoutAmount\":50.50,\"thirdOrderId\":\"1681\",\"userAccount\":\"pf2cdavis2\",\"userId\":\"10339170\",\"venueCode\":\"S128\"},{\"betAmount\":50.00,\"orderClassify\":1,\"payoutAmount\":50.50,\"thirdOrderId\":\"1682\",\"userAccount\":\"pf2cdavis2\",\"userId\":\"10339170\",\"venueCode\":\"S128\"},{\"betAmount\":50.00,\"orderClassify\":1,\"payoutAmount\":0.00,\"thirdOrderId\":\"1692\",\"userAccount\":\"pf2cdavis2\",\"userId\":\"12138010\",\"venueCode\":\"S128\"},{\"betAmount\":50.00,\"orderClassify\":1,\"payoutAmount\":0.00,\"thirdOrderId\":\"1693\",\"userAccount\":\"pf2cdavis2\",\"userId\":\"12138010\",\"venueCode\":\"S128\"},{\"betAmount\":50.00,\"orderClassify\":1,\"payoutAmount\":0.00,\"thirdOrderId\":\"1685\",\"userAccount\":\"pf2cdavis2\",\"userId\":\"10339170\",\"venueCode\":\"S128\"},{\"betAmount\":50.00,\"orderClassify\":1,\"payoutAmount\":0.00,\"thirdOrderId\":\"1686\",\"userAccount\":\"pf2cdavis2\",\"userId\":\"10339170\",\"venueCode\":\"S128\"},{\"betAmount\":50.00,\"orderClassify\":1,\"payoutAmount\":0.00,\"thirdOrderId\":\"1687\",\"userAccount\":\"pf2cdavis2\",\"userId\":\"10339170\",\"venueCode\":\"S128\"},{\"betAmount\":50.00,\"orderClassify\":1,\"payoutAmount\":0.00,\"thirdOrderId\":\"1690\",\"userAccount\":\"pf2cdavis2\",\"userId\":\"12138010\",\"venueCode\":\"S128\"},{\"betAmount\":50.00,\"orderClassify\":1,\"payoutAmount\":0.00,\"thirdOrderId\":\"1691\",\"userAccount\":\"pf2cdavis2\",\"userId\":\"12138010\",\"venueCode\":\"S128\"},{\"betAmount\":50.00,\"orderClassify\":1,\"payoutAmount\":0.00,\"thirdOrderId\":\"1683\",\"userAccount\":\"pf2cdavis2\",\"userId\":\"10339170\",\"venueCode\":\"S128\"},{\"betAmount\":50.00,\"orderClassify\":1,\"payoutAmount\":0.00,\"thirdOrderId\":\"1684\",\"userAccount\":\"pf2cdavis2\",\"userId\":\"10339170\",\"venueCode\":\"S128\"}]}";
            UserGamePayoutMqVO userGamePayoutVOMq = JSONObject.parseObject(userGamePayoutMqJson,UserGamePayoutMqVO.class);
            userGamePayoutVOMq.setMsgId(SnowFlakeUtils.getSnowId());
            KafkaUtil.send("sit_third_game_payout_topic", userGamePayoutVOMq);
            i++;
            int randomNum= RandomStringUtil.getIntervalIntegerRandom(100,1000);
            if(i%randomNum==0){
                i=1;
                TimeUnit.SECONDS.sleep(5);
            }

        }while(true);


    }

}
