package com.cloud.baowang.handler;

import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/3/8 17:19
 * @Version: V1.0
 **/
@Component
@Slf4j
public class DemoHandler {
    /**
     * 测试使用 可删除
     */
    @XxlJob(value = "demoJob")
    public void doExecute() {
        log.info("测试job开始......");
        XxlJobHelper.log("----------- glue.version: 20000-----------");

        // param parse
        String param = XxlJobHelper.getJobParam();
        log.info("获取参数:{}", param);
        XxlJobHelper.handleSuccess("执行成功!!!!!!!!");
        if (param == null || param.trim().length() == 0) {
            XxlJobHelper.log("param[" + param + "] invalid.");

            XxlJobHelper.handleFail();
            return;
        }


    }


    /**
     * 2、分片广播任务
     */
    @XxlJob("shardingJobHandler")
    public void shardingJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        //日志可以会写到admin的控制台上
        XxlJobHelper.log("分片参数：当前分片序号 = {}, 总分片数 = {}", shardIndex, shardTotal);

        // 业务逻辑
        for (int i = 0; i < shardTotal; i++) {
            if (i == shardIndex) {
                XxlJobHelper.log("第 {} 片, 命中分片开始处理", i);
            } else {
                XxlJobHelper.log("第 {} 片, 忽略", i);
            }
        }

    }


}
