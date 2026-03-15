import com.cloud.baowang.JobServerApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/9 17:28
 * @Version: V1.0
 **/

@RunWith(SpringRunner.class)
@SpringBootTest(
      //  webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = JobServerApplication.class)
//@AutoConfigureMockMvc
//@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class ActivityDailMedalAwardActiveTest {


   /* @Resource
    private ActivityParticipateApi activityParticipateApi;
    @Test
    public void testMedalAward(){
        //发放真人场馆勋章
        activityParticipateApi.activityDailMedalAwardActive(VenueTypeEnum.SH.getCode());
        //发放电子场馆勋章
        activityParticipateApi.activityDailMedalAwardActive(VenueTypeEnum.ELECTRONICS.getCode());
    }*/
}
