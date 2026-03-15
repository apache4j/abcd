package com.cloud.baowang.controller;

import com.cloud.baowang.handler.ThirdOrderPullHandler;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/6/10 20:07
 * @Version: V1.0
 **/
@Controller
@RequestMapping("/jobinfo")
public class JobInfoController {
    private static final Logger logger= LoggerFactory.getLogger(JobInfoController.class);

    @Resource
    private ThirdOrderPullHandler thirdOrderPullHandler;


    /**
     * ------------------封装方法----------------------
     */

    /**
     * 新增任务
     * @return 任务id
     */
    @RequestMapping("/addJob")
    @ResponseBody
    public String addJob() {
        thirdOrderPullHandler.cmdGamePullTask();
        return null;
    }

    /**
     * 修改任务 并且启动
     * @param jobUpdateAndStartVo 任务参数
     * @return
     */
   /* @RequestMapping("/updateAndStart")
    @ResponseBody
    public String updateAndStart(@RequestBody @Validated JobUpdateAndStartVo jobUpdateAndStartVo) {
        logger.info("updateAndStart---param:[{}]",jobUpdateAndStartVo);
        return xxlJobUtil.updateAndStart(jobUpdateAndStartVo);
    }*/

    /**
     * 删除任务
     * @param jobInfo 任务
     * @return
     */
   /* @RequestMapping("/removeJob")
    @ResponseBody
    public String removeJob(@RequestBody XxlJobInfo jobInfo) {
        logger.info("removeJob---param:[{}]",jobInfo);
        return xxlJobUtil.remove(jobInfo.getId());
    }
*/
    /**
     * 停止任务
     * @param jobInfo 任务id
     * @return
     */
  /*  @RequestMapping("/stopJob")
    @ResponseBody
    public String stopJob(@RequestBody XxlJobInfo jobInfo) {
        logger.info("stopJob---param:[{}]",jobInfo);
        return xxlJobUtil.stop(jobInfo.getId());
    }*/

    /**
     * 启动任务
     * @param jobInfo 任务id
     * @return
     */
   /* @RequestMapping("/startJob")
    @ResponseBody
    public String startJob(@RequestBody XxlJobInfo jobInfo) {
        logger.info("startJob---param:[{}]",jobInfo);
        return xxlJobUtil.start(jobInfo.getId());
    }*/

}
