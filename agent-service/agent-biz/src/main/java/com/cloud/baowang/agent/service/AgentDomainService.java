package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.RecordDomainEnum;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainPageQueryVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainResponseVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainVO;
import com.cloud.baowang.agent.po.AgentDomainPO;
import com.cloud.baowang.agent.po.AgentDomainRecordPO;
import com.cloud.baowang.agent.repositories.AgentDomainRecordRepository;
import com.cloud.baowang.agent.repositories.AgentDomainRepository;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 域名管理
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentDomainService extends ServiceImpl<AgentDomainRepository,AgentDomainPO> {


    private final AgentDomainRepository agentDomainRepository;

    private final AgentDomainRecordRepository agentDomainRecordRepository;


    /**
     * 添加域名管理
     */
    public ResponseVO<Boolean> addAgentDomain(List<AgentDomainVO> agentDomainVO) {
        //添加域名管理
        List<AgentDomainPO> agentDomainPO = BeanUtil.copyToList(agentDomainVO, AgentDomainPO.class);
        this.saveBatch(agentDomainPO);
        return ResponseVO.success();
    }

    /**
     * 修改域名管理
     */
    public ResponseVO<Boolean> updateAgentDomain(AgentDomainVO agentDomainVO) {
        String id = agentDomainVO.getId();
        AgentDomainPO agentDomainPO = agentDomainRepository.selectById(id);
        if (agentDomainPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        //修改域名管理
        AgentDomainPO domainPO = new AgentDomainPO();
        BeanUtils.copyProperties(agentDomainVO, domainPO);
        domainPO.setUpdater(agentDomainVO.getUpdater());
        domainPO.setUpdatedTime(System.currentTimeMillis());
        agentDomainRepository.updateById(domainPO);

        List<AgentDomainRecordPO> arr = new ArrayList<>();


        //判断当前是什么类型的变更
        //描述变更
        if (!StringUtils.equals(agentDomainPO.getDomainName(), agentDomainVO.getDomainDescription())) {
            //保存记录
            AgentDomainRecordPO agentDomainRecordPO = new AgentDomainRecordPO();
            agentDomainRecordPO.setSiteCode(agentDomainVO.getSiteCode());
            agentDomainRecordPO.setDomainName(domainPO.getDomainName());
            //变更类型为域名变更
            agentDomainRecordPO.setRecordType(RecordDomainEnum.DESC.getType());
            agentDomainRecordPO.setBeforeText(agentDomainPO.getDomainDescription());
            agentDomainRecordPO.setAfterText(agentDomainVO.getDomainDescription());

            agentDomainRecordPO.setRemark(agentDomainVO.getRemark());
            agentDomainRecordPO.setCreator(agentDomainVO.getUpdater());
            agentDomainRecordPO.setCreatedTime(System.currentTimeMillis());
            agentDomainRecordPO.setUpdater(agentDomainVO.getUpdater());
            agentDomainRecordPO.setUpdatedTime(System.currentTimeMillis());

            arr.add(agentDomainRecordPO);
        }
        //排序变更
        if (null != agentDomainVO.getSort() && !agentDomainVO.getSort().equals(agentDomainPO.getSort())) {
            //保存记录
            AgentDomainRecordPO agentDomainRecordPO = new AgentDomainRecordPO();
            agentDomainRecordPO.setSiteCode(agentDomainVO.getSiteCode());
            agentDomainRecordPO.setDomainName(domainPO.getDomainName());
            agentDomainRecordPO.setRecordType(RecordDomainEnum.SORT.getType());
            agentDomainRecordPO.setBeforeText(agentDomainPO.getSort() == null ? null : String.valueOf(agentDomainPO.getSort()));
            agentDomainRecordPO.setAfterText(agentDomainVO.getSort() == null ? null : String.valueOf(agentDomainVO.getSort()));

            agentDomainRecordPO.setRemark(agentDomainVO.getRemark());
            agentDomainRecordPO.setCreator(agentDomainVO.getUpdater());
            agentDomainRecordPO.setCreatedTime(System.currentTimeMillis());
            agentDomainRecordPO.setUpdater(agentDomainVO.getUpdater());
            agentDomainRecordPO.setUpdatedTime(System.currentTimeMillis());
            arr.add(agentDomainRecordPO);
        }

        for (AgentDomainRecordPO agentDomainRecordPO : arr) {
            agentDomainRecordRepository.insert(agentDomainRecordPO);
        }
        return ResponseVO.success();
    }


    /**
     * 删除域名管理
     */
    public ResponseVO<Boolean> deleteAgentDomain(String domainName) {
        LambdaQueryWrapper<AgentDomainPO> del = Wrappers.lambdaQuery();
        del.eq(AgentDomainPO::getDomainName, domainName);
        //删除域名管理
        agentDomainRepository.delete(del);
        return ResponseVO.success();
    }


    /**
     * 获取域名管理
     */
    public ResponseVO<AgentDomainResponseVO> getAgentDomainById(String id) {
        AgentDomainPO agentDomainPO = agentDomainRepository.selectById(id);
        if (agentDomainPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        AgentDomainResponseVO responseVO = new AgentDomainResponseVO();
        BeanUtils.copyProperties(agentDomainPO, responseVO);
        return ResponseVO.success(responseVO);
    }


    /**
     * 获取域名管理的列表
     */
    public ResponseVO<Page<AgentDomainResponseVO>> getAgentDomainList(AgentDomainPageQueryVO agentDomainVO) {
        LambdaQueryWrapper<AgentDomainPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        String siteCode = agentDomainVO.getSiteCode();
        if (StringUtils.isNotBlank(siteCode)) {
            lambdaQueryWrapper.eq(AgentDomainPO::getSiteCode, siteCode);
        }
        String domainName = agentDomainVO.getDomainName();
        if (StringUtils.isNotBlank(domainName)) {
            lambdaQueryWrapper.eq(AgentDomainPO::getDomainName, domainName);
        }

        String creator = agentDomainVO.getCreator();
        if (StringUtils.isNotBlank(creator)) {
            lambdaQueryWrapper.eq(AgentDomainPO::getCreator, creator);
        }

        String updater = agentDomainVO.getUpdater();
        if (StringUtils.isNotBlank(updater)) {
            lambdaQueryWrapper.eq(AgentDomainPO::getUpdater, updater);
        }

        Integer domainType = agentDomainVO.getDomainType();
        if (domainType != null) {
            lambdaQueryWrapper.eq(AgentDomainPO::getDomainType, domainType);
        }
        if(StringUtils.isNotBlank(agentDomainVO.getOrderField()) && StringUtils.isNotBlank(agentDomainVO.getOrderType())){
            if(agentDomainVO.getOrderField().equals("updatedTime")){
                lambdaQueryWrapper.orderBy(true, agentDomainVO.getOrderType().equals("asc"), AgentDomainPO::getUpdatedTime);
            }else if(agentDomainVO.getOrderField().equals("createdTime")){
                lambdaQueryWrapper.orderBy(true, agentDomainVO.getOrderType().equals("asc"), AgentDomainPO::getCreatedTime);
            }
        }else {
            lambdaQueryWrapper.orderByAsc(AgentDomainPO::getSort);
            lambdaQueryWrapper.orderByDesc(AgentDomainPO::getUpdatedTime);
        }
        Page<AgentDomainPO> page = new Page<>(agentDomainVO.getPageNumber(), agentDomainVO.getPageSize());
        page = agentDomainRepository.selectPage(page, lambdaQueryWrapper);
        IPage<AgentDomainResponseVO> convert = page.convert(item -> BeanUtil.copyProperties(item, AgentDomainResponseVO.class));
        return ResponseVO.success(ConvertUtil.toConverPage(convert));
    }


}
