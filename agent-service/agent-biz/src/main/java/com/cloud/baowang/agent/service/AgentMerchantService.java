package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantResultVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.MerchantAgentInfoVO;
import com.cloud.baowang.agent.api.vo.merchant.*;
import com.cloud.baowang.agent.po.AgentMerchantModifyReviewPO;
import com.cloud.baowang.agent.po.AgentMerchantPO;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.agent.repositories.AgentMerchantModifyReviewRepository;
import com.cloud.baowang.agent.repositories.AgentMerchantRepository;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.common.core.enums.ReviewOperationEnum;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentMerchantService extends ServiceImpl<AgentMerchantRepository, AgentMerchantPO> {
    private final RiskApi riskApi;
    private final AgentInfoRepository agentInfoRepository;
    private final AgentMerchantModifyReviewRepository modifyReviewRepository;

    public AgentMerchantVO getAdminByMerchantAccountAndSite(String merchantAccount, String siteCode) {
        LambdaQueryWrapper<AgentMerchantPO> queryWrapper = new LambdaQueryWrapper<AgentMerchantPO>();
        queryWrapper.eq(AgentMerchantPO::getMerchantAccount, merchantAccount);
        queryWrapper.eq(AgentMerchantPO::getSiteCode, siteCode);
        AgentMerchantPO po = baseMapper.selectOne(queryWrapper);
        if (po == null) {
            return null;
        }
        return BeanUtil.copyProperties(po, AgentMerchantVO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAgentMerchantLoginInfo(AgentMerchantResultVO resultVO) {
        AgentMerchantPO po = super.getById(resultVO.getId());
        if (po == null) {
            return;
        }
        po.setLastLoginTime(System.currentTimeMillis());
        this.updateById(po);
    }

    public ResponseVO<Page<AgentMerchantPageRespVO>> pageQuery(AddMerchantPageQueryVO queryVO) {
        Page<AgentMerchantPO> page = new Page<>(queryVO.getPageNumber(), queryVO.getPageSize());
        LambdaQueryWrapper<AgentMerchantPO> query = Wrappers.lambdaQuery();
        query.eq(AgentMerchantPO::getSiteCode, queryVO.getSiteCode());
        if (StringUtils.isNotBlank(queryVO.getRiskId())) {
            query.eq(AgentMerchantPO::getRiskId, queryVO.getRiskId());
        }
        if (StringUtils.isNotBlank(queryVO.getMerchantAccount())) {
            query.eq(AgentMerchantPO::getMerchantAccount, queryVO.getMerchantAccount());
        }
        if (StringUtils.isNotBlank(queryVO.getMerchantName())) {
            query.eq(AgentMerchantPO::getMerchantName, queryVO.getMerchantName());
        }
        if (queryVO.getStatus() != null) {
            query.eq(AgentMerchantPO::getStatus, queryVO.getStatus());
        }
        if (queryVO.getRegisterTimeStart() != null) {
            query.ge(AgentMerchantPO::getRegisterTime, queryVO.getRegisterTimeStart());
        }
        if (queryVO.getRegisterTimeEnd() != null) {
            query.le(AgentMerchantPO::getRegisterTime, queryVO.getRegisterTimeEnd());
        }
        if (queryVO.getUpdatedTimeStart() != null) {
            query.ge(AgentMerchantPO::getUpdatedTime, queryVO.getUpdatedTimeStart());
        }
        if (queryVO.getUpdatedTimeEnd() != null) {
            query.le(AgentMerchantPO::getUpdatedTime, queryVO.getUpdatedTimeEnd());
        }
        query.orderByDesc(AgentMerchantPO::getCreatedTime);
        page = this.page(page, query);
        IPage<AgentMerchantPageRespVO> convert = page.convert(item -> BeanUtil.copyProperties(item, AgentMerchantPageRespVO.class));
        List<AgentMerchantPageRespVO> records = convert.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            //设置风控信息
            List<String> riskIds = records.stream().map(AgentMerchantPageRespVO::getRiskId).filter(StringUtils::isNotBlank).toList();
            if (CollectionUtil.isNotEmpty(riskIds)) {
                Map<String, RiskLevelDetailsVO> riskMap = riskApi.getByIds(riskIds);
                if (CollectionUtil.isNotEmpty(riskMap)) {
                    //设置风控信息
                    records.forEach(item -> {
                        if (riskMap.containsKey(item.getRiskId())) {
                            item.setRiskLevel(riskMap.get(item.getRiskId()).getRiskControlLevel());
                        }
                    });
                }
            }
            List<String> merchantAccountList = records.stream().map(AgentMerchantPageRespVO::getMerchantAccount).toList();
            //统计每个商务下对应总代人数
            List<MerchantAgentCountVO> agentCountList = agentInfoRepository.getCountGroupByMerchant(merchantAccountList, queryVO.getSiteCode());
            if (CollectionUtil.isNotEmpty(agentCountList)) {
                Map<String, Long> agentCountMap = agentCountList.stream()
                        .collect(Collectors.toMap(
                                MerchantAgentCountVO::getMerchantAccount,
                                MerchantAgentCountVO::getAgentCount
                        ));
                //设置下级总代人数
                records.forEach(item -> {
                    if (agentCountMap.containsKey(item.getMerchantAccount())) {
                        item.setAgentCount(agentCountMap.get(item.getMerchantAccount()));
                    }
                });
            }
            //判断当前商务是否已存在待审核流程数据
            LambdaQueryWrapper<AgentMerchantModifyReviewPO> modifyQuery = Wrappers.lambdaQuery();
            modifyQuery.eq(AgentMerchantModifyReviewPO::getSiteCode, queryVO.getSiteCode())
                    .in(AgentMerchantModifyReviewPO::getMerchantAccount, merchantAccountList)
                    .eq(AgentMerchantModifyReviewPO::getReviewOperation, ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
            List<AgentMerchantModifyReviewPO> agentMerchantModifyReviewPOS = modifyReviewRepository.selectList(modifyQuery);
            if (CollectionUtil.isNotEmpty(agentMerchantModifyReviewPOS)) {
                Map<String, AgentMerchantModifyReviewPO> map = agentMerchantModifyReviewPOS.stream()
                        .collect(Collectors.toMap(
                                AgentMerchantModifyReviewPO::getMerchantAccount,
                                review -> review
                        ));
                records.forEach(item -> {
                    if (map.containsKey(item.getMerchantAccount())) {
                        item.setIsHavePending(Integer.parseInt(YesOrNoEnum.YES.getCode()));
                    } else {
                        item.setIsHavePending(Integer.parseInt(YesOrNoEnum.NO.getCode()));
                    }
                });
            }
        }
        return ResponseVO.success(ConvertUtil.toConverPage(convert));
    }

    public List<AgentMerchantVO> getList(String siteCode) {
        LambdaQueryWrapper<AgentMerchantPO> query = Wrappers.lambdaQuery();
        query.eq(AgentMerchantPO::getSiteCode, siteCode);
        List<AgentMerchantPO> list = this.list(query);
        return BeanUtil.copyToList(list, AgentMerchantVO.class);
    }

    public MerchantAgentInfoVO getMerchantAgentInfo(String siteCode, String merchantAccount) {
        LambdaQueryWrapper<AgentMerchantPO> query = Wrappers.lambdaQuery();
        query.eq(AgentMerchantPO::getSiteCode, siteCode).eq(AgentMerchantPO::getMerchantAccount, merchantAccount);
        AgentMerchantPO po = this.getOne(query);
        MerchantAgentInfoVO vo = BeanUtil.copyProperties(po, MerchantAgentInfoVO.class);
        if (po != null) {
            Long agentCount = agentInfoRepository.selectAgentCount(siteCode, merchantAccount);
            vo.setAgentCount(agentCount);
        }
        return vo;
    }

    public Long getTeamNum(String siteCode, String merchantAccount) {
       return  agentInfoRepository.selectAgentTeamNumMerchant(siteCode, merchantAccount);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> updateRiskInfo(MerchantRiskUpdateVO updateVO) {
        LambdaUpdateWrapper<AgentMerchantPO> upd = Wrappers.lambdaUpdate();
        upd.eq(AgentMerchantPO::getSiteCode, updateVO.getSiteCode()).eq(AgentMerchantPO::getMerchantAccount, updateVO.getMerchantAccount());
        upd.set(AgentMerchantPO::getRiskId, updateVO.getRiskId());
        upd.set(AgentMerchantPO::getUpdater, updateVO.getAccount());
        upd.set(AgentMerchantPO::getUpdatedTime, System.currentTimeMillis());
        this.update(upd);
        return ResponseVO.success();
    }

    public List<AgentMerchantVO> getListByAccounts(String siteCode, List<String> merchantAccounts) {
        List<AgentMerchantPO> list = this.list(new LambdaQueryWrapper<AgentMerchantPO>().eq(AgentMerchantPO::getSiteCode, siteCode).in(AgentMerchantPO::getMerchantAccount, merchantAccounts));
        return ConvertUtil.entityListToModelList(list, AgentMerchantVO.class);
    }

    public boolean validate(AgentMerchantVO agentMerchantVO, String password) {
        AgentMerchantVO merchantVO = getAdminByMerchantAccountAndSite(agentMerchantVO.getMerchantAccount(), agentMerchantVO.getSiteCode());
        String salt = merchantVO.getSalt();
        // 密码加密
        String encryptPassword = AgentServerUtil.getEncryptPassword(password, salt);

        return encryptPassword.equals(merchantVO.getMerchantPassword());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(AgentMerchantVO agentMerchantVO, String newPassword) {
        String salt = agentMerchantVO.getSalt();
        String encryptPassword = AgentServerUtil.getEncryptPassword(newPassword, salt);
        AgentMerchantPO agentMerchantPO = BeanUtil.copyProperties(agentMerchantVO, AgentMerchantPO.class);
        agentMerchantPO.setMerchantPassword(encryptPassword);
        return this.updateById(agentMerchantPO);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> bindEmail(String merchantAccount, String email, String siteCode) {
        LambdaUpdateWrapper<AgentMerchantPO> upd = Wrappers.lambdaUpdate();
        upd.eq(AgentMerchantPO::getMerchantAccount, merchantAccount);
        upd.eq(AgentMerchantPO::getSiteCode, siteCode);
        upd.set(AgentMerchantPO::getEmail, email);
        this.update(upd);
        return ResponseVO.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> bindGoogle(AgentMerchantVO agentMerchantVO, String googleAuthKey) {
        LambdaUpdateWrapper<AgentMerchantPO> upd = Wrappers.lambdaUpdate();
        upd.eq(AgentMerchantPO::getMerchantAccount, agentMerchantVO.getMerchantAccount());
        upd.eq(AgentMerchantPO::getSiteCode, agentMerchantVO.getSiteCode());
        upd.set(AgentMerchantPO::getGoogleAuthKey, googleAuthKey);
        this.update(upd);
        return ResponseVO.success();
    }

    public ResponseVO<MerchantSecuritySetVO> column(String siteCode, String merchatAccount) {
        MerchantSecuritySetVO setVO = new MerchantSecuritySetVO();
        AgentMerchantVO vo = this.getAdminByMerchantAccountAndSite(merchatAccount, siteCode);
        if (Objects.isNull(vo)) {
            return ResponseVO.success(setVO);
        }
        setVO.setAgentPasswordSet(StringUtils.isNotEmpty(vo.getMerchantPassword()) ? 1 : 0);
        setVO.setEmailSet(StringUtils.isNotEmpty(vo.getEmail()) ? 1 : 0);
        setVO.setGoogleAuthKeySet(StringUtils.isNotEmpty(vo.getGoogleAuthKey()) ? 1 : 0);
        return ResponseVO.success(setVO);
    }

    public long countByAccountAndSite(String email, String siteCode, String id) {
        LambdaQueryWrapper<AgentMerchantPO> query = Wrappers.lambdaQuery();
        query.eq(AgentMerchantPO::getSiteCode, siteCode);
        query.eq(AgentMerchantPO::getEmail, email);
        query.ne(AgentMerchantPO::getId, id);
        return this.count(query);
    }
}
