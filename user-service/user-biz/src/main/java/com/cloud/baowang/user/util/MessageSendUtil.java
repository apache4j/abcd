package com.cloud.baowang.user.util;

import com.cloud.baowang.activity.api.enums.task.TaskEnum;
import com.cloud.baowang.activity.api.vo.task.TaskNoviceTriggerVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/09/26 18:04
 * @description:
 */
@Slf4j
public class MessageSendUtil {
    public static void kafkaSend(UserInfoVO userInfoVO, String topic,  List<String> subTaskTypes) {
        try {
            TaskNoviceTriggerVO taskNoviceTriggerVO = new TaskNoviceTriggerVO();
            taskNoviceTriggerVO.setUserId(userInfoVO.getUserId());
            taskNoviceTriggerVO.setUserAccount(userInfoVO.getUserAccount());
            taskNoviceTriggerVO.setSiteCode(userInfoVO.getSiteCode());
            taskNoviceTriggerVO.setSuperAgentId(userInfoVO.getSuperAgentId());
            taskNoviceTriggerVO.setSubTaskTypes(subTaskTypes);
            taskNoviceTriggerVO.setVipGradeCode(userInfoVO.getVipGradeCode());
            taskNoviceTriggerVO.setVipRankCode(userInfoVO.getVipRank());
            taskNoviceTriggerVO.setSiteCode(userInfoVO.getSiteCode());
            taskNoviceTriggerVO.setRegisterTime(userInfoVO.getRegisterTime());
            KafkaUtil.send(topic, taskNoviceTriggerVO);
        } catch (Exception e) {
            log.info("{}发送失败", topic);
            e.printStackTrace();
        }
    }
}
