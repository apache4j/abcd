package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantLoginInfoPageQueryVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantLoginInfoRespVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantLoginInfoVO;
import com.cloud.baowang.agent.po.AgentMerchantLoginInfoPO;
import com.cloud.baowang.agent.repositories.AgentMerchantLoginInfoRepository;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.enums.LoginTypeEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskListAccountQueryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentMerchantLoginInfoService extends ServiceImpl<AgentMerchantLoginInfoRepository, AgentMerchantLoginInfoPO> {
    private final AgentMerchantLoginInfoRepository repository;
    private final RiskApi riskApi;

    @Transactional(rollbackFor = Exception.class)
    public boolean addLoginInfo(AgentMerchantVO agentMerchantVO) {
        AgentMerchantLoginInfoPO po = new AgentMerchantLoginInfoPO();
        agentMerchantVO.setId(null);
        BeanUtil.copyProperties(agentMerchantVO, po);
        po.setCreatedTime(System.currentTimeMillis());
        po.setUpdatedTime(System.currentTimeMillis());
        po.setCreator(CurrReqUtils.getAccount());
        po.setUpdater(CurrReqUtils.getAccount());
        po.setTerminalDeviceNo(agentMerchantVO.getLoginDeviceNo());
        this.save(po);
        return true;
    }

    public ResponseVO<AgentMerchantLoginInfoRespVO> pageQuery(AgentMerchantLoginInfoPageQueryVO queryVO) {
        AgentMerchantLoginInfoRespVO respVO = new AgentMerchantLoginInfoRespVO();
        String siteCode = queryVO.getSiteCode();
        Long allCount = repository.getCountByLoginType(siteCode, null);
        Long successCount = repository.getCountByLoginType(siteCode, LoginTypeEnum.SUCCESS.getCode());
        Long failCount = repository.getCountByLoginType(siteCode, LoginTypeEnum.FAIL.getCode());
        String loginIp = queryVO.getLoginIp();
        Integer loginType = queryVO.getLoginType();
        Long createdTimeStart = queryVO.getCreatedTimeStart();
        Long createdTimeEnd = queryVO.getCreatedTimeEnd();
        String merchantAccount = queryVO.getMerchantAccount();
        String merchantName = queryVO.getMerchantName();
        String ipAddress = queryVO.getIpAddress();
        String terminalDeviceNo = queryVO.getTerminalDeviceNo();
        Page<AgentMerchantLoginInfoPO> page = new Page<>(queryVO.getPageNumber(), queryVO.getPageSize());
        LambdaQueryWrapper<AgentMerchantLoginInfoPO> query = Wrappers.lambdaQuery();
        query.eq(AgentMerchantLoginInfoPO::getSiteCode, siteCode);

        if (StringUtils.isNotBlank(loginIp)) {
            query.eq(AgentMerchantLoginInfoPO::getLoginIp, loginIp);
        }

        if (loginType != null) {
            query.eq(AgentMerchantLoginInfoPO::getLoginType, loginType);
        }

        if (createdTimeStart != null) {
            query.ge(AgentMerchantLoginInfoPO::getCreatedTime, createdTimeStart);
        }

        if (createdTimeEnd != null) {
            query.le(AgentMerchantLoginInfoPO::getCreatedTime, createdTimeEnd);
        }

        if (StringUtils.isNotBlank(merchantAccount)) {
            query.eq(AgentMerchantLoginInfoPO::getMerchantAccount, merchantAccount);
        }

        if (StringUtils.isNotBlank(merchantName)) {
            query.eq(AgentMerchantLoginInfoPO::getMerchantName, merchantName);
        }

        if (StringUtils.isNotBlank(ipAddress)) {
            query.eq(AgentMerchantLoginInfoPO::getIpAddress, ipAddress);
        }

        if (StringUtils.isNotBlank(terminalDeviceNo)) {
            query.eq(AgentMerchantLoginInfoPO::getTerminalDeviceNo, terminalDeviceNo);
        }
        query.orderByDesc(AgentMerchantLoginInfoPO::getCreatedTime);
        page = this.page(page, query);
        List<AgentMerchantLoginInfoPO> records = page.getRecords();
        Map<String, String> riskMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(records)) {
            List<String> ips = records.stream().map(AgentMerchantLoginInfoPO::getLoginIp).filter(StringUtils::isNotBlank).toList();
            RiskListAccountQueryVO riskQuery = new RiskListAccountQueryVO();
            riskQuery.setRiskControlAccounts(ips);
            riskQuery.setSiteCode(siteCode);
            riskQuery.setRiskControlTypeCode(RiskTypeEnum.RISK_IP.getCode());
            List<RiskAccountVO> riskListAccount = riskApi.getRiskListAccount(riskQuery);
            riskMap = riskListAccount.stream()
                    .collect(Collectors.toMap(RiskAccountVO::getRiskControlAccount, RiskAccountVO::getRiskControlLevel));

        }
        Map<String, String> finalRiskMap = riskMap;
        IPage<AgentMerchantLoginInfoVO> convert = page.convert(item -> {
            AgentMerchantLoginInfoVO vo = BeanUtil.copyProperties(item, AgentMerchantLoginInfoVO.class);
            if (finalRiskMap.containsKey(vo.getLoginIp())) {
                vo.setRiskIpLevel(finalRiskMap.get(vo.getLoginIp()));
            }
            return vo;
        });
        respVO.setPages(ConvertUtil.toConverPage(convert));
        respVO.setAllLoginCount(allCount);
        respVO.setFailLoginCount(failCount);
        respVO.setSuccessLoginCount(successCount);
        return ResponseVO.success(respVO);
    }
}
