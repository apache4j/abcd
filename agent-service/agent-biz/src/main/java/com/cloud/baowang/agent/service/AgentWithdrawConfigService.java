package com.cloud.baowang.agent.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoBasicVO;
import com.cloud.baowang.agent.api.vo.withdrawConfig.*;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentWithdrawConfigDetailPO;
import com.cloud.baowang.agent.po.AgentWithdrawConfigPO;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.agent.repositories.AgentWithdrawConfigDetailRepository;
import com.cloud.baowang.agent.repositories.AgentWithdrawConfigRepository;
import com.cloud.baowang.agent.constant.AgentConstant;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.agent.api.enums.AgentSwitchIntEnum;
import com.cloud.baowang.agent.api.enums.AgentWithdrawConfigStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 代理提款设置 服务实现类
 *
 * @author kimi
 * @since 2024-06-12
 */
@AllArgsConstructor
@Slf4j
@Service
public class AgentWithdrawConfigService extends ServiceImpl<AgentWithdrawConfigRepository, AgentWithdrawConfigPO> {

    private final AgentWithdrawConfigRepository configRepository;
    private final RiskApi riskApi;
    private final AgentInfoRepository agentInfoRepository;
    private final AgentWithdrawConfigDetailService detailService;
    private final AgentWithdrawConfigDetailRepository detailRepository;
    private final SiteApi siteApi;

    public void syncAgentWithdrawConfig(String siteCode) {
        AgentWithdrawConfigPO withdrawConfigPO = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(AgentWithdrawConfigPO::getAgentAccount, AgentConstant.AGENT_WITHDRAW_CONFIG_COMMON)
                .eq(AgentWithdrawConfigPO::getSiteCode, CommonConstant.ADMIN_CENTER_SITE_CODE)
                .one();
        if (withdrawConfigPO != null) {
            AgentWithdrawConfigPO configPO = new AgentWithdrawConfigPO();
            configPO.setAgentAccount(AgentConstant.AGENT_WITHDRAW_CONFIG_COMMON);
            configPO.setSiteCode(siteCode);
            configPO.setStatus(AgentWithdrawConfigStatusEnum.OPEN.getCode());
            configPO.setCreatedTime(System.currentTimeMillis());
            this.save(configPO);

            List<AgentWithdrawConfigDetailVO> detailVOList = detailService.getByConfigId(withdrawConfigPO.getId());

            List<AgentWithdrawConfigDetailPO> detailList = new ArrayList<>();
            for (AgentWithdrawConfigDetailVO configVO : detailVOList) {
                AgentWithdrawConfigDetailPO detailPO = new AgentWithdrawConfigDetailPO();
                BeanUtils.copyProperties(configVO, detailPO);
                detailPO.setConfigId(configPO.getId());
                detailList.add(detailPO);
            }
            if (detailList.size() > 0) {
                detailService.saveBatch(detailList);
            }
        }
    }

    public void add(AgentWithdrawConfigAddVO vo) {
        AgentWithdrawConfigPO po = new AgentWithdrawConfigPO();
        po.setSiteCode(vo.getSiteCode());
        po.setAgentAccount(vo.getAgentAccount());
        po.setStatus(vo.getStatus());
        // 校验金额
        List<AgentWithdrawConfigDetailPO> detailList = new ArrayList<>();
        List<AgentWithdrawWayVO> withdrawWay = queryWithdrawWay();
        List<AgentWithdrawConfigDetailVO> tempList = new ArrayList<>();
        List<AgentWithdrawDetailRspVO> totalList = vo.getConfigList();
        for (AgentWithdrawDetailRspVO totalVo : totalList) {
            List<AgentWithdrawConfigDetailVO> addList = totalVo.getDetailList();
            tempList.addAll(addList);
        }
        checkParam(tempList);

        // 通用账号校验 只允许添加一个通用账号
        if (vo.getAgentAccount().equals(AgentConstant.AGENT_WITHDRAW_CONFIG_COMMON)) {
            AgentWithdrawConfigPO one = new LambdaQueryChainWrapper<>(baseMapper)
                    .eq(AgentWithdrawConfigPO::getAgentAccount, po.getAgentAccount())
                    .ne(AgentWithdrawConfigPO::getStatus, AgentWithdrawConfigStatusEnum.DELETE.getCode())
                    .eq(AgentWithdrawConfigPO::getSiteCode, vo.getSiteCode())
                    .one();
            if (ObjectUtil.isNotEmpty(one)) {
                throw new BaowangDefaultException(ResultCode.AGENT_WITHDRAW_CONFIG_COMMON_ERROR);
            }
        } else {
            AgentInfoPO agentInfoPO = agentInfoRepository.findAgentInfoNotCase(vo.getSiteCode(),po.getAgentAccount());
            if (null == agentInfoPO) {
                throw new BaowangDefaultException(ResultCode.AGENT_SUPER_AGENT_EMPTY_ERROR);
            }
            // 代理只能添加一个配置
            AgentWithdrawConfigPO withdrawConfigPO = new LambdaQueryChainWrapper<>(baseMapper)
                    .eq(AgentWithdrawConfigPO::getAgentAccount, po.getAgentAccount())
                    .ne(AgentWithdrawConfigPO::getStatus, AgentWithdrawConfigStatusEnum.DELETE.getCode())
                    .eq(AgentWithdrawConfigPO::getSiteCode, vo.getSiteCode())
                    .one();
            if (ObjectUtil.isNotEmpty(withdrawConfigPO)) {
                throw new BaowangDefaultException(ResultCode.AGENT_WITHDRAW_CONFIG_ONLY_ERROR);
            }
        }
        po.setCreatedTime(System.currentTimeMillis());
        po.setUpdatedTime(System.currentTimeMillis());
        this.save(po);


        for (AgentWithdrawConfigDetailVO configVO : tempList) {
            checkParam(configVO);
            AgentWithdrawConfigDetailPO detailPO = new AgentWithdrawConfigDetailPO();
            BeanUtils.copyProperties(configVO, detailPO);
            detailPO.setConfigId(po.getId());
            Optional<AgentWithdrawWayVO> first = withdrawWay.stream().filter(item -> configVO.getWithdrawWayId().equals(String.valueOf(item.getWithdrawWayId()))).findFirst();
            detailPO.setWithdrawWayI18(first.map(AgentWithdrawWayVO::getWithdrawWayI18).orElse(null));
            detailList.add(detailPO);
        }
        log.error("AgentWithdrawConfigService.add 操作人 : "+CurrReqUtils.getAccount()+ " 代理帐号 : " +vo.getAgentAccount()+ " 代理提款设置添加详情 : " + detailList);
        detailService.saveBatch(detailList);

    }

    private void checkParam(AgentWithdrawConfigDetailVO configVO) {
        if (configVO.getWithdrawMinQuotaSingle().compareTo(configVO.getWithdrawMaxQuotaSingle()) > 0) {
            throw new BaowangDefaultException(ResultCode.AGENT_BANK_MIN_GT_BANK_MAX_ERROR);
        }
        if (String.valueOf(configVO.getFeeType()).equals(CommonConstant.business_one_str)){
            BigDecimal feeAmount = configVO.getFeeRate();
            if (feeAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new BaowangDefaultException(ResultCode.FIEXD_FEE_AMOUNT_INVALID);
            }
            BigDecimal intPart = feeAmount.setScale(0, RoundingMode.DOWN);
            if (feeAmount.compareTo(intPart) != 0) {
                throw new BaowangDefaultException(ResultCode.FIEXD_FEE_AMOUNT_INVALID);
            }
        }
    }

    public void del(IdVO vo) {
        AgentWithdrawConfigPO configPO = getById(vo.getId());
        if (Objects.isNull(configPO)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        // 通用账号不可删除
        if (configPO.getAgentAccount().equals(AgentConstant.AGENT_WITHDRAW_CONFIG_COMMON)) {
            throw new BaowangDefaultException(ResultCode.AGENT_WITHDRAW_CONFIG_COMMON_DEL_ERROR);
        }
        // 关闭状态才可删除
        if (!configPO.getStatus().equals(AgentWithdrawConfigStatusEnum.CLOSE.getCode())) {
            throw new BaowangDefaultException(ResultCode.AGENT_WITHDRAW_CONFIG_CLOSE_DEL_ERROR);
        }
        log.error("AgentWithdrawConfigService.del 操作人 : "+CurrReqUtils.getAccount()+ " 被删除代理帐号 : " +configPO.getAgentAccount());
        configRepository.deleteById(configPO.getId());
        detailService.deleteByConfigId(configPO.getId());
    }

    /**
     * 根据代理账号查询代理提款配置,未配置则返回默认配置
     *
     * @param account
     * @return
     */
    public AgentWithdrawConfigVO getWithdrawConfigByAgentAccount(String account) {
        String siteCode = CurrReqUtils.getSiteCode();
        AgentWithdrawConfigPO withdrawConfigPO;
        withdrawConfigPO = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(AgentWithdrawConfigPO::getAgentAccount, account)
                .eq(AgentWithdrawConfigPO::getStatus, AgentWithdrawConfigStatusEnum.OPEN.getCode())
                .eq(AgentWithdrawConfigPO::getSiteCode, siteCode)
                .one();
        AgentWithdrawConfigVO vo = new AgentWithdrawConfigVO();
        // 未配置走默认
        if (ObjectUtil.isEmpty(withdrawConfigPO)) {
            withdrawConfigPO = new LambdaQueryChainWrapper<>(baseMapper)
                    .eq(AgentWithdrawConfigPO::getAgentAccount, AgentConstant.AGENT_WITHDRAW_CONFIG_COMMON)
                    .eq(AgentWithdrawConfigPO::getStatus, AgentWithdrawConfigStatusEnum.OPEN.getCode())
                    .eq(AgentWithdrawConfigPO::getSiteCode, siteCode)
                    .one();
        }

        if (ObjectUtil.isNotEmpty(withdrawConfigPO)) {
            BeanUtils.copyProperties(withdrawConfigPO, vo);
            List<AgentWithdrawConfigDetailVO> detailVOList = detailService.getByConfigId(withdrawConfigPO.getId());
            vo.setDetailList(detailVOList);
        }

        return vo;
    }

    public void edit(AgentWithdrawConfigEditVO vo) {
        AgentWithdrawConfigPO po = getById(vo.getId());
        if (ObjectUtil.isEmpty(po) || po.getStatus().equals(AgentWithdrawConfigStatusEnum.DELETE.getCode())) {
            throw new BaowangDefaultException(ResultCode.AGENT_WITHDRAW_CONFIG_EMPTY_ERROR);
        }
        po.setStatus(vo.getStatus());
        po.setId(vo.getId());
        List<AgentWithdrawConfigDetailVO> tempList = new ArrayList<>();
        List<AgentWithdrawDetailRspVO> totalList = vo.getDetailTotalList();
        for (AgentWithdrawDetailRspVO totalVo : totalList) {
            List<AgentWithdrawConfigDetailVO> detailList = totalVo.getDetailList();
            tempList.addAll(detailList);
        }
        // 校验金额
        for (AgentWithdrawConfigDetailVO configVO : tempList) {
            // 法币单次提款最低限额 不能大于 法币单次提款最高限额
            checkParam(configVO);

        }

        po.setUpdatedTime(System.currentTimeMillis());
        updateById(po);

        //详情配置先删除再插入
        detailService.deleteByConfigId(po.getId());
        List<AgentWithdrawConfigDetailPO> detailList = new ArrayList<>();
        List<AgentWithdrawWayVO> withdrawWay = queryWithdrawWay();
        tempList.forEach(config -> {
            AgentWithdrawConfigDetailPO detailPO = new AgentWithdrawConfigDetailPO();
            BeanUtils.copyProperties(config, detailPO);

            detailPO.setConfigId(po.getId());
            detailPO.setCurrency(config.getCurrency());
            detailPO.setWithdrawWayId(config.getWithdrawWayId());
            detailPO.setUpdater(CurrReqUtils.getAccount());
            detailPO.setUpdatedTime(System.currentTimeMillis());
            Optional<AgentWithdrawWayVO> first = withdrawWay.stream().filter(item -> config.getWithdrawWayId().equals(String.valueOf(item.getWithdrawWayId()))).findFirst();
            detailPO.setWithdrawWayI18(first.map(AgentWithdrawWayVO::getWithdrawWayI18).orElse(null));
            detailList.add(detailPO);
        });

        detailService.saveBatch(detailList);
        log.error("AgentWithdrawConfigService.edit 操作人 : "+CurrReqUtils.getAccount()+ " 代理帐号 : " +po.getAgentAccount()+ " 代理提款设置修改详情 : " + detailList);

    }

    private void checkParam(List<AgentWithdrawConfigDetailVO> addList) {
        for (AgentWithdrawConfigDetailVO configVO : addList) {
            // 法币单次提款最低限额 不能大于 法币单次提款最高限额
            if (configVO.getWithdrawMinQuotaSingle().compareTo(configVO.getWithdrawMaxQuotaSingle()) > 0) {
                throw new BaowangDefaultException(ResultCode.AGENT_BANK_MIN_GT_BANK_MAX_ERROR);
            }
            if (configVO.getFeeRate().compareTo(BigDecimal.ZERO) < 0){
                throw new BaowangDefaultException(ResultCode.RED_BAG_AMOUNT_OVER_ZERO_ERROR);
            }
            if (configVO.getFeeRate().scale() > 2){
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_SCALE_GT_TWO);
            }
        }

    }

    public Page<AgentWithdrawConfigPageVO> pageList(AgentWithdrawConfigPageQueryVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        Page<AgentWithdrawConfigPO> page = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(StringUtils.isNotBlank(vo.getAgentAccount()), AgentWithdrawConfigPO::getAgentAccount, vo.getAgentAccount())
                .eq(AgentWithdrawConfigPO::getSiteCode, siteCode)
                .orderByAsc(AgentWithdrawConfigPO::getCreatedTime)
                .page(new Page<>(vo.getPageNumber(), vo.getPageSize()));
        List<AgentWithdrawConfigPO> records = page.getRecords();
        Page<AgentWithdrawConfigPageVO> resultPage = new Page<>();
        if (CollectionUtil.isEmpty(records)) {
            return resultPage;
        }

        List<AgentInfoPO> agentAccounts = agentInfoRepository.selectList(new LambdaQueryWrapper<AgentInfoPO>()
                .in(AgentInfoPO::getAgentAccount, records.stream().map(AgentWithdrawConfigPO::getAgentAccount).toList())
                .eq(AgentInfoPO::getSiteCode, siteCode));
        // 风控层级
        List<String> riskIds = agentAccounts.stream().map(AgentInfoPO::getRiskLevelId).filter(ObjectUtil::isNotEmpty).toList();
        Map<String, RiskLevelDetailsVO> riskLevelDetailsVOMap = riskApi.getByIds(riskIds);

        List<AgentWithdrawConfigPageVO> pageList = new ArrayList<>();
        Map<String, AgentInfoPO> agentAccountMap = Optional.of(agentAccounts).orElse(Lists.newArrayList()).stream().collect(Collectors.toMap(AgentInfoPO::getAgentAccount, t -> t));
        for (AgentWithdrawConfigPO record : records) {
            AgentWithdrawConfigPageVO pageVO = new AgentWithdrawConfigPageVO();
            BeanUtils.copyProperties(record, pageVO);
            // 代理信息
            AgentInfoPO agentInfoPO = agentAccountMap.get(record.getAgentAccount());
            if (ObjectUtil.isNotEmpty(agentInfoPO)) {
                pageVO.setName(agentInfoPO.getName());
                pageVO.setAgentStatus(agentInfoPO.getStatus());
                pageVO.setAgentType(agentInfoPO.getAgentType());
                // 风控层级
                pageVO.setRiskLevelName(Optional.ofNullable(riskLevelDetailsVOMap.get(agentInfoPO.getRiskLevelId())).map(RiskLevelDetailsVO::getRiskControlLevel).orElse(null));
            }

            pageList.add(pageVO);
        }
        BeanUtils.copyProperties(page, resultPage);
        resultPage.setRecords(pageList);
        return resultPage;
    }

    public AgentWithdrawConfigDetailResVO detail(IdVO vo) {
        AgentWithdrawConfigPO configPO = getById(vo.getId());
        AgentWithdrawConfigDetailResVO detailVO = new AgentWithdrawConfigDetailResVO();
        detailVO.setStatus(configPO.getStatus());

        // 代理信息
        AgentInfoBasicVO agentInfoBasicVO = new AgentInfoBasicVO();

        if (AgentConstant.AGENT_WITHDRAW_CONFIG_COMMON.equals(configPO.getAgentAccount())) {
            agentInfoBasicVO.setAgentAccount(AgentConstant.AGENT_WITHDRAW_CONFIG_COMMON);
        } else {
            AgentInfoPO agentInfoPO = agentInfoRepository.selectOne(new LambdaQueryWrapper<AgentInfoPO>()
                    .eq(AgentInfoPO::getAgentAccount, configPO.getAgentAccount())
                    .eq(AgentInfoPO::getSiteCode, configPO.getSiteCode()));
            RiskLevelDetailsVO levelDetailsVO = riskApi.getById(IdVO.builder().id(agentInfoPO.getRiskLevelId()).build());
            if (ObjectUtil.isNotEmpty(agentInfoPO)) {
                agentInfoBasicVO.setAgentAccount(agentInfoPO.getAgentAccount());
                agentInfoBasicVO.setName(agentInfoPO.getName());
                agentInfoBasicVO.setStatus(agentInfoPO.getStatus());
                agentInfoBasicVO.setAgentType(agentInfoPO.getAgentType());
                // 风控层级
                agentInfoBasicVO.setRiskLevelText(levelDetailsVO == null ? null : levelDetailsVO.getRiskControlLevel());
            }
        }

        List<AgentWithdrawConfigDetailVO> detailVOList = detailService.getByConfigId(vo.getId());
        List<AgentWithdrawConfigDetailVO> detailRspList = new ArrayList<>();
        List<AgentWithdrawWayVO> wayVOS = queryWithdrawWay();
        for (AgentWithdrawWayVO wayVO : wayVOS) {
            // 已经配置提款方式?
            boolean exists = false;
            for (AgentWithdrawConfigDetailVO detailExistVO : detailVOList) {
                if (detailExistVO.getWithdrawWayId().equals(wayVO.getWithdrawWayId())) {
                    exists = true;
                    detailRspList.add(detailExistVO);
                    break;
                }
            }
            //创建新对象赋值提款方式,给前端展示
            if (!exists) {
                AgentWithdrawConfigDetailVO newDetailVO = new AgentWithdrawConfigDetailVO();
                newDetailVO.setWithdrawWayId(wayVO.getWithdrawWayId());
                newDetailVO.setWithdrawWayI18(wayVO.getWithdrawWayI18());
                newDetailVO.setCurrency(wayVO.getCurrency());
                detailRspList.add(newDetailVO);
            }
        }

        Map<String, List<AgentWithdrawConfigDetailVO>> map = detailRspList.stream()
                .collect(Collectors.groupingBy(AgentWithdrawConfigDetailVO::getCurrency));
//        detailVO.setDetailList(detailVOList);
        List<CodeValueVO> currencyCodeVos = siteApi.chooseCurrency(CurrReqUtils.getSiteCode()).getData();
        List<String> currencyList = wayVOS.stream().map(AgentWithdrawWayVO::getCurrency).distinct().toList();
        List<AgentWithdrawDetailRspVO> resultList = new ArrayList<>();
        currencyList.forEach(currency -> {
            currencyCodeVos.stream()
                    .filter(item -> item.getCode().equals(currency))
                    .findFirst()
                    .ifPresent(item -> {
                        // 构建币种多语言
                        AgentWithdrawDetailRspVO rspVO = AgentWithdrawDetailRspVO.builder().value(item.getValue()).currencyCode(item.getCode()).build();
                        // 根据currencyCode设置detailList
                        List<AgentWithdrawConfigDetailVO> detailList = map.get(currency);
                        if (detailList != null) {
                            rspVO.setDetailList(detailList);
                        }
                        resultList.add(rspVO); // 添加到 resultList
                    });
        });
        detailVO.setDetailTotalList(resultList);
        detailVO.setAgentInfoBasicVO(agentInfoBasicVO);

        return detailVO;
    }

    public Map<String, Object> getDownBox() {
        record CodeNameRecord(Integer code, String name) {
        }
        List<CodeNameRecord> statusList = AgentSwitchIntEnum.getList().stream().map(s -> new CodeNameRecord(s.getCode(), s.getName())).toList();
        Map<String, Object> map = new HashMap<>();
        map.put("statusList", statusList);
        return map;
    }

    /**
     * 根据站点查询提款方式
     * @return
     */
    public List<AgentWithdrawWayVO> queryWithdrawWay() {
        String siteCode = CurrReqUtils.getSiteCode();
        return agentInfoRepository.queryWithdrawWay(siteCode);
    }

    public List<AgentWithdrawWayRspVO> queryWithdrawWayRsp() {
        List<AgentWithdrawWayRspVO> result = new ArrayList<>();
        List<AgentWithdrawWayVO> wayVOS = queryWithdrawWay();
        List<String> currencyList = queryWithdrawWay().stream().map(AgentWithdrawWayVO::getCurrency).distinct().toList();
        List<CodeValueVO> currencyCodeVos = siteApi.chooseCurrency(CurrReqUtils.getSiteCode()).getData();
        Map<String, List<AgentWithdrawWayVO>> map = wayVOS.stream()
                .collect(Collectors.groupingBy(AgentWithdrawWayVO::getCurrency));
        currencyList.forEach(e -> {
            // 查找匹配的 currencyCodeVO 并添加到结果中
            currencyCodeVos.stream()
                    .filter(item -> item.getCode().equals(e))
                    .findFirst()
                    .ifPresent(item -> {
                        // 构建币种多语言
                        AgentWithdrawWayRspVO rspVO = AgentWithdrawWayRspVO.builder()
                                .value(item.getValue())
                                .currencyCode(item.getCode())
                                .build();
                        // 构建提款方式集合
                        List<AgentWithdrawWayVO> detailList = map.get(item.getCode());
                        if (detailList != null) {
                            rspVO.setDetailList(detailList);
                        }
                        result.add(rspVO);
                    });
        });
        return result;
    }
}
