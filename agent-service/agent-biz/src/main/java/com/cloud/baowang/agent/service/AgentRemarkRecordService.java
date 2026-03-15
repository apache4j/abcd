package com.cloud.baowang.agent.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.remark.AgentRemarkRecordVO;
import com.cloud.baowang.agent.po.AgentRemarkRecordPO;
import com.cloud.baowang.agent.repositories.AgentRemarkRecordRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 代理备注表 服务实现类
 * </p>
 *
 * @author awei
 * @since 2023-10-10
 */
@Service
public class AgentRemarkRecordService extends ServiceImpl<AgentRemarkRecordRepository, AgentRemarkRecordPO> {

    public void add(AgentRemarkRecordVO vo) {
        AgentRemarkRecordPO po = new AgentRemarkRecordPO();
        BeanUtils.copyProperties(vo, po);
        po.setCreatedTime(System.currentTimeMillis());
        po.setUpdatedTime(System.currentTimeMillis());
        this.save(po);
    }
}
