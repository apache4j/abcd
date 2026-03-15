import com.cloud.baowang.activity.ActivityApplication;
import com.cloud.baowang.activity.cache.redbag.RedBagRainIdCacheService;
import com.cloud.baowang.activity.cache.redbag.RedBagRainSessionCacheService;
import com.cloud.baowang.activity.po.SiteActivityRedBagRecordPO;
import com.cloud.baowang.activity.service.redbag.SiteActivityRedBagRecordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ActivityApplication.class})
public class HTest {

    @Resource
    RedBagRainIdCacheService idCacheService;
    @Resource
    RedBagRainSessionCacheService sessionCacheService;
    @Resource
    SiteActivityRedBagRecordService redBagRecordService;

    @Test
    public void test1() {
        List<SiteActivityRedBagRecordPO> siteActivityRedBagRecordPOS = redBagRecordService.selectBySessionIdByGroup("10002", "S306667974864556032");
        System.out.println(siteActivityRedBagRecordPOS);
    }

}
