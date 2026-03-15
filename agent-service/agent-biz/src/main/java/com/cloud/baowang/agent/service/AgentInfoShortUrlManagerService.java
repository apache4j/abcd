package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentShortUrlManagerAddVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentShortUrlManagerPageQueryVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentShortUrlManagerRespVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Slf4j
@Service
public class AgentInfoShortUrlManagerService extends ServiceImpl<AgentInfoRepository, AgentInfoPO> {
    private final AgentInfoRepository infoRepository;

    public ResponseVO<Page<AgentShortUrlManagerRespVO>> pageQuery(AgentShortUrlManagerPageQueryVO queryVO) {
        Page<AgentInfoPO> page = new Page<>(queryVO.getPageNumber(), queryVO.getPageSize());
        page = infoRepository.pageQuery(page, queryVO);
        return ResponseVO.success(ConvertUtil.toConverPage(page.convert(item -> BeanUtil.copyProperties(item, AgentShortUrlManagerRespVO.class))));
    }

    public ResponseVO<Boolean> addShortUrl(AgentShortUrlManagerAddVO addVO) {
        LambdaQueryWrapper<AgentInfoPO> query = Wrappers.lambdaQuery();
        query.eq(AgentInfoPO::getSiteCode, addVO.getSiteCode()).eq(AgentInfoPO::getAgentAccount, addVO.getAgentAccount());
        AgentInfoPO agentInfoPO = infoRepository.selectOne(query);
        if (agentInfoPO == null) {
            throw new BaowangDefaultException(ResultCode.AGENT_NOT_EXISTS);
        }
        agentInfoPO.setShortUrl(addVO.getShortUrl());
        agentInfoPO.setBindShortUrlTime(addVO.getBindShortUrlTime());
        agentInfoPO.setBindShortUrlOperator(addVO.getBindShortUrlOperator());
        infoRepository.updateById(agentInfoPO);
        return ResponseVO.success();
    }

    public ResponseVO<Boolean> deleteShortUrl(String id) {
        //删除短链接,也就是滞空
        AgentInfoPO agentInfoPO = infoRepository.selectById(id);
        if (agentInfoPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        LambdaUpdateWrapper<AgentInfoPO> upd = Wrappers.lambdaUpdate();
        upd.eq(AgentInfoPO::getId, id).set(AgentInfoPO::getShortUrl, null).set(AgentInfoPO::getBindShortUrlTime, null).set(AgentInfoPO::getBindShortUrlOperator, null);
        this.update(upd);
        return ResponseVO.success();
    }

    public Long pageCount(AgentShortUrlManagerPageQueryVO queryVO) {
        LambdaQueryWrapper<AgentInfoPO> query = Wrappers.lambdaQuery();
        query.eq(AgentInfoPO::getSiteCode, queryVO.getSiteCode()).isNotNull(AgentInfoPO::getShortUrl);
        String agentAccount = queryVO.getAgentAccount();
        String shortUrl = queryVO.getShortUrl();
        String bindShortUrlOperator = queryVO.getBindShortUrlOperator();
        if (StringUtils.isNotBlank(agentAccount)) {
            query.eq(AgentInfoPO::getAgentAccount, agentAccount);
        }
        if (StringUtils.isNotBlank(shortUrl)) {
            query.eq(AgentInfoPO::getShortUrl, shortUrl);
        }
        if (StringUtils.isNotBlank(bindShortUrlOperator)) {
            query.eq(AgentInfoPO::getBindShortUrlOperator, bindShortUrlOperator);
        }
        query.orderByDesc(AgentInfoPO::getBindShortUrlTime);
        return this.count(query);
    }
}
