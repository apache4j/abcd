package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.security.*;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentSecurityPO;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.agent.repositories.AgentSecurityRepository;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: fangfei
 * @createTime: 2024/06/18 15:55
 * @description:
 */
@AllArgsConstructor
@Slf4j
@Service
public class AgentSecurityService extends ServiceImpl<AgentSecurityRepository, AgentSecurityPO> {

    private final AgentInfoService agentInfoService;
    private final AgentInfoRepository agentInfoRepository;

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> securityQASet(AgentSecurityEditVO vo) {
        if (CollectionUtil.isEmpty(vo.getAgentSecurityVOList())){
            return ResponseVO.success();
        }

        List<AgentSecurityPO> agentSecurityPOs = populateAgentSecurity(vo);

        long qaNumbs = this.count(Wrappers.<AgentSecurityPO>lambdaQuery().eq(AgentSecurityPO::getAgentAccount, vo.getAgentAccount()));
        if(qaNumbs > 0){
//            // 非首次需要校验并且删除历史记录
//            // 编辑校验
//            verifyEditCode(vo.getAgentInfoId(),vo.getEditCode());
            // 删除历史密保
            this.remove(Wrappers.<AgentSecurityPO>lambdaQuery().eq(AgentSecurityPO::getAgentAccount, vo.getAgentAccount()));
        }
        // 生成密保
        this.saveBatch(agentSecurityPOs);

        // 更新用户密保设置状态
        AgentInfoPO agentInfoPO = agentInfoService.getAgentInfoPO(vo.getSiteCode(),vo.getAgentAccount());
        if(agentInfoPO != null && (agentInfoPO.getSecuritySet() == null || agentInfoPO.getSecuritySet() == 0)){
            agentInfoRepository.update(null, Wrappers.<AgentInfoPO>lambdaUpdate()
                    .set(AgentInfoPO::getSecuritySet,1)
                    .eq(AgentInfoPO::getAgentAccount,vo.getAgentAccount())
            );
        }
        return ResponseVO.success();
    }

    private static List<AgentSecurityPO> populateAgentSecurity(AgentSecurityEditVO vo) {
        List<AgentSecurityPO> agentSecurityPOs = Lists.newArrayList();

        List<AgentSecurityVO> agentSecurityVOList = vo.getAgentSecurityVOList();
        for (AgentSecurityVO agentSecurityVO : agentSecurityVOList) {
            AgentSecurityPO agentSecurityPO = new AgentSecurityPO();
            agentSecurityPO.setAgentAccount(vo.getAgentAccount());
            // 优化 答案加密处理
            BeanUtil.copyProperties(agentSecurityVO,agentSecurityPO);
            agentSecurityPO.setCreator(vo.getAgentAccount());
            agentSecurityPO.setCreatedTime(System.currentTimeMillis());
            agentSecurityPOs.add(agentSecurityPO);
        }
        return agentSecurityPOs;
    }

    public ResponseVO<Boolean> securityQAVerify(AgentSecurityVerifyVO vo) {
        List<AgentSecurityPO> list = this.list(Wrappers.<AgentSecurityPO>lambdaQuery()
                .eq(AgentSecurityPO::getSiteCode, vo.getSiteCode())
                .eq(AgentSecurityPO::getAgentAccount, vo.getAgentAccount())
        );
        if(CollectionUtil.isEmpty(list)){
            return ResponseVO.fail(ResultCode.AGENT_QA_NOT_SET);
        }

        Map<String, String> questionId2Answer = list.stream().collect(Collectors.toMap(AgentSecurityPO::getSecurityQuestionId, AgentSecurityPO::getSecurityAnswer));
        List<AgentSecurityVO> agentSecurityVOList = vo.getAgentSecurityList();
        for (AgentSecurityVO agentSecurityVO : agentSecurityVOList) {
            String securityQuestionId = agentSecurityVO.getSecurityQuestionId();
            String answer = questionId2Answer.get(securityQuestionId);
            if(StringUtils.isEmpty(answer)){
                log.error("不存在的密保问题，密保id {}，代理账号 {}",securityQuestionId,vo.getAgentAccount());
                return ResponseVO.fail(ResultCode.AGENT_SECURITY_QA_VERIFY_ERROR);
            }
            if(!answer.equals(agentSecurityVO.getSecurityAnswer().trim())){
                log.error("不存在的密保问题答案错误，密保id {}，代理id {}，用户答案：{}",securityQuestionId,vo.getAgentAccount(), agentSecurityVO.getSecurityAnswer());
                return ResponseVO.fail(ResultCode.AGENT_SECURITY_QA_INCORRECT_ERROR);
            }
        }

        String key = String.format(RedisConstants.AGENT_FORGET_PASSWORD_VERIFY,vo.getAgentAccount());
        RedisUtil.setValue(key, true, 10 * 60L);


        return ResponseVO.success(true);
    }

    public List<AgentSecurityListVO> getAgentSecurityQuestions(String agentAccount) {
        List<AgentSecurityPO> list = this.list(Wrappers.<AgentSecurityPO>lambdaQuery().eq(AgentSecurityPO::getAgentAccount, agentAccount));
        if(CollectionUtil.isEmpty(list)){
            return Lists.newArrayList();
        }
        return BeanUtil.copyToList(list,AgentSecurityListVO.class);
    }
}
