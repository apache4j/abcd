import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.SystemBusinessApplication;
import com.cloud.baowang.system.po.SystemParamPO;
import com.cloud.baowang.system.repositories.SystemParamRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SystemBusinessApplication.class})
public class HTest {

    @Resource
    SystemParamRepository systemParamRepository;


    @Test
    public void clearMap() {
        RedisUtil.localCacheMapClear(CacheConstants.KEY_SYSTEM_PARAM);

    }

    @Test
    public void test1() {
        QueryWrapper<SystemParamPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id");
        List<SystemParamPO> systemParamPOS = systemParamRepository.selectList(queryWrapper);
        Map<String, List<SystemParamPO>> collect = systemParamPOS.stream().collect(Collectors.groupingBy(SystemParamPO::getType, LinkedHashMap::new, Collectors.toList()));
        AtomicLong i = new AtomicLong(10000L);
        AtomicLong next = new AtomicLong(10000L);
        collect.forEach((type, list) -> {
            i.set(next.get());
            for (SystemParamPO systemParamPO : list) {
                systemParamPO.setValue("LOOKUP_" + i.getAndIncrement());
                new LambdaUpdateChainWrapper<>(systemParamRepository)
                        .eq(SystemParamPO::getId, systemParamPO.getId())
                        .set(SystemParamPO::getValue, systemParamPO.getValue())
                        .update();
            }
            if (list.size() > 30) {
                next.addAndGet(40);
            } else if (list.size() > 20) {
                next.addAndGet(30);
            } else if (list.size() > 10) {
                next.addAndGet(20);
            } else {
                next.addAndGet(10);
            }
        });
    }

}
