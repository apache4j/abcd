package com.cloud.baowang.agent.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.agentreview.info.ShortUrlChangeRecordPageVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.ShortUrlChangeRecordQueryVO;
import com.cloud.baowang.agent.po.AgentInfoChangeRecordPO;
import com.cloud.baowang.agent.repositories.AgentInfoChangeRecordRepository;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 代理信息变更记录表 服务实现类
 * </p>
 *
 * @author awei
 * @since 2023-10-10
 */
@Service
public class AgentInfoChangeRecordService extends ServiceImpl<AgentInfoChangeRecordRepository, AgentInfoChangeRecordPO> {
    private final AgentInfoChangeRecordRepository agentInfoChangeRecordRepository;

    public AgentInfoChangeRecordService(AgentInfoChangeRecordRepository agentInfoChangeRecordRepository) {
        this.agentInfoChangeRecordRepository = agentInfoChangeRecordRepository;
    }

    List<ShortUrlChangeRecordPageVO> shortRecordPageList(@Param("vo") ShortUrlChangeRecordQueryVO vo){
        return agentInfoChangeRecordRepository.shortRecordPageList(vo);
    }

}
