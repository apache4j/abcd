package com.cloud.baowang.agent.service;

import com.cloud.baowang.agent.api.vo.AgentUserLanguageVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.agent.api.vo.AgentSystemMessageConfigVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: fangfei
 * @createTime: 2024/11/30 10:10
 * @description:
 */
@Service
@Slf4j
@AllArgsConstructor
public class AgentCommonService {

    private AgentInfoRepository agentInfoRepository;

    public AgentSystemMessageConfigVO getAgentLanguage(AgentUserLanguageVO vo) {
        String agentId = vo.getUserId();
        //默认为英文
        String language = CurrReqUtils.getLanguage();
        if (org.apache.commons.lang3.StringUtils.isEmpty(language)) {
            language = LanguageEnum.EN_US.getLang();
        }
        String noticeContentI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.NOTICE_CONTENT.getCode());
        log.info("=======" + noticeContentI18Code);
        //先从缓存中取用户的语言
        String key = "agentAccount::language::" + agentId;
        String cacheVal = RedisUtil.getValue(key);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(cacheVal)) {
            log.info("get cache value:{}-{}", agentId, language);
            language = cacheVal;
        } else {
            AgentInfoPO agentInfoPO = agentInfoRepository.selectByAgentId(agentId);
            language = agentInfoPO.getLanguage();
            RedisUtil.setValue(key, language);
        }
        String code = vo.getMessageType().getCode();
        String title = "BIZ_" + code + "_TITLE_8100";
        String content = "BIZ_" + code + "_CONTENT_8100";
        String titleString = I18nMessageUtil.getI18NMessage(title);
        String contentString = I18nMessageUtil.getI18NMessage(content);

        AgentSystemMessageConfigVO agentSystemMessageConfigVO = new AgentSystemMessageConfigVO();
        agentSystemMessageConfigVO.setTitleI18nCode(title);
        agentSystemMessageConfigVO.setTitle(titleString);
        agentSystemMessageConfigVO.setContentI18nCode(content);
        agentSystemMessageConfigVO.setContent(contentString);
        agentSystemMessageConfigVO.setLanguage(language);
        return agentSystemMessageConfigVO;
    }

}
