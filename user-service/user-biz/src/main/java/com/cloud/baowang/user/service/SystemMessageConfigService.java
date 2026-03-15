package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.AgentSystemMessageConfigVO;
import com.cloud.baowang.user.api.vo.UserSystemMessageConfigVO;
import com.cloud.baowang.user.po.SystemMessageConfigPO;
import com.cloud.baowang.user.repositories.SystemMessageConfigRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息配置表 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2024-10-31
 */
@Slf4j
@Service
@AllArgsConstructor
public class SystemMessageConfigService extends ServiceImpl<SystemMessageConfigRepository, SystemMessageConfigPO> {

    private SystemMessageConfigRepository systemMessageConfigRepository;

    public AgentSystemMessageConfigVO getUserMessage(String messageType, String language) {
        LambdaQueryWrapper<SystemMessageConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemMessageConfigPO::getMessageType, messageType);
        SystemMessageConfigPO po = systemMessageConfigRepository.selectOne(queryWrapper);
        AgentSystemMessageConfigVO vo = new AgentSystemMessageConfigVO();
        if (po != null) {
            BeanUtils.copyProperties(po, vo);
        }

        return vo;
    }


}
