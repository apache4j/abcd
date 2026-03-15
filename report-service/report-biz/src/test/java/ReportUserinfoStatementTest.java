import com.cloud.baowang.report.ReportApplication;
import com.cloud.baowang.report.api.vo.ReportUserInfoStatementSyncVO;
import com.cloud.baowang.report.service.ReportUserInfoStatementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ReportApplication.class})
public class ReportUserinfoStatementTest {

    @Resource
    ReportUserInfoStatementService reportUserInfoStatementService;

    @Test
    public void test() {

    }
}
