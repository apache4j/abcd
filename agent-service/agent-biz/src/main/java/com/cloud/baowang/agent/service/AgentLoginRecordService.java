package com.cloud.baowang.agent.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.agentLogin.*;
import com.cloud.baowang.agent.po.AgentLabelPO;
import com.cloud.baowang.agent.po.AgentLoginRecordPO;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.agent.repositories.AgentLabelRepository;
import com.cloud.baowang.agent.repositories.AgentLoginRecordRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.LoginTypeEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskListAccountQueryVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author : kimi
 * @Version : 1.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentLoginRecordService extends ServiceImpl<AgentLoginRecordRepository, AgentLoginRecordPO> {

    private final AgentLoginRecordRepository agentLoginRecordRepository;
    private final RiskApi riskApi;
    private final AgentInfoRepository agentInfoRepository;
    private final AgentLabelRepository agentLabelRepository;
    private final SystemParamApi systemParamApi;


    public ResponseVO<Long> getTotalCount(final AgentLoginRecordParam param) {
        return ResponseVO.success(agentLoginRecordRepository.queryAgentLoginRecordCount(param,param.getSiteCode()));
    }

    public ResponseVO<AgentLoginRecordVO> queryAgentLoginRecord(final AgentLoginRecordParam param) {
        try {
            AgentLoginRecordVO vo = new AgentLoginRecordVO();
            String siteCode = param.getSiteCode();
            Page<AgentLoginRecordPageVO> page = new Page<>(param.getPageNumber(), param.getPageSize());
            Page<AgentLoginRecordPageVO> result = agentLoginRecordRepository.queryAgentLoginRecordPage(page, param, siteCode);
            if (result.getTotal() == 0) {
                vo.setAgentLoginRecordPageVO(result);
                return ResponseVO.success(vo);
            }
            List<AgentLoginRecordPageVO> records = result.getRecords();

            Map<String, String> ipRiskLevelMap = new HashMap<>();
            Map<String, String> deviceLevelMap = new HashMap<>();
            Map<String, AgentLabelPO> labelIdPOMap = new HashMap<>();
            if (CollectionUtil.isNotEmpty(records)) {
                List<String> loginIps = records.stream().map(AgentLoginRecordPageVO::getLoginIp).filter(StringUtils::isNotBlank).toList();
                List<String> loginDeviceNos = records.stream().map(AgentLoginRecordPageVO::getDeviceNumber).filter(StringUtils::isNotBlank).toList();
                List<String> labellist = records.stream().map(AgentLoginRecordPageVO::getAgentLabelId).filter(StringUtils::isNotBlank).toList();

                RiskListAccountQueryVO ipQuery = new RiskListAccountQueryVO();
                ipQuery.setSiteCode(param.getSiteCode());
                ipQuery.setRiskControlTypeCode(RiskTypeEnum.RISK_IP.getCode());
                ipQuery.setRiskControlAccounts(loginIps);
                List<RiskAccountVO> riskListAccount = riskApi.getRiskListAccount(ipQuery);
                if (CollectionUtil.isNotEmpty(riskListAccount)) {
                    ipRiskLevelMap = riskListAccount.stream()
                            .collect(Collectors.toMap(RiskAccountVO::getRiskControlAccount, RiskAccountVO::getRiskControlLevel));
                }
                RiskListAccountQueryVO deviceQuery = new RiskListAccountQueryVO();
                deviceQuery.setSiteCode(param.getSiteCode());
                deviceQuery.setRiskControlTypeCode(RiskTypeEnum.RISK_DEVICE.getCode());
                deviceQuery.setRiskControlAccounts(loginDeviceNos);
                List<RiskAccountVO> deviceRiskLevel = riskApi.getRiskListAccount(deviceQuery);
                if (CollectionUtil.isNotEmpty(deviceRiskLevel)) {
                    deviceLevelMap = deviceRiskLevel.stream()
                            .collect(Collectors.toMap(RiskAccountVO::getRiskControlAccount, RiskAccountVO::getRiskControlLevel));
                }
                //NOTE 增加代理标签
                if (CollUtil.isNotEmpty(labellist)){
                    Set<String> labelSet = new HashSet<>();
                    for (String labelIds : labellist) {
                        String[] parts = labelIds.split(",");
                        labelSet.addAll(Arrays.asList(parts));
                    }
                    if (CollUtil.isNotEmpty(labelSet)){
                        LambdaQueryWrapper<AgentLabelPO> labelQuery = Wrappers.lambdaQuery();
                        labelQuery.in(AgentLabelPO::getId, labelSet);
                        List<AgentLabelPO> agentLabelList = agentLabelRepository.selectList(labelQuery);
                        labelIdPOMap = agentLabelList.stream()
                                .collect(Collectors.toMap(AgentLabelPO::getId, label -> label));
                    }

                }
            }
            Map<String, String> finalIpRiskLevelMap = ipRiskLevelMap;
            Map<String, String> finalDeviceLevelMap = deviceLevelMap;
            Map<String, AgentLabelPO> finalLabelIdPOMap = labelIdPOMap;

            List<CodeValueVO> agentTypeList = systemParamApi.getSystemParamByType(CommonConstant.AGENT_TYPE).getData();

            for (CodeValueVO codeValueVO : agentTypeList) {
                codeValueVO.setValue(I18nMessageUtil.getI18NMessage(codeValueVO.getValue()));
            }

            Map<String, String> agentTypeMap = agentTypeList.stream().collect(Collectors.toMap(CodeValueVO::getCode, CodeValueVO::getValue));

            List<CodeValueVO> loginTypeList = systemParamApi.getSystemParamByType(CommonConstant.LOGIN_TYPE).getData();
            for (CodeValueVO codeValueVO : loginTypeList) {
                codeValueVO.setValue(I18nMessageUtil.getI18NMessage(codeValueVO.getValue()));
            }
            Map<String, String> loginTypeMap = loginTypeList.stream().collect(Collectors.toMap(CodeValueVO::getCode, CodeValueVO::getValue));



            records.forEach(item -> {
                String loginIp = item.getLoginIp();
                if (finalIpRiskLevelMap.containsKey(loginIp)) {
                    item.setIpControlName(finalIpRiskLevelMap.get(loginIp));
                }
                if (finalDeviceLevelMap.containsKey(item.getDeviceNumber())) {
                    item.setDeviceControlName(finalDeviceLevelMap.get(item.getDeviceNumber()));
                }
                String agentLabelId = item.getAgentLabelId();

                if (StrUtil.isNotEmpty(agentLabelId)) {
                    List<AgentLabelResponseVO> labelResp = new ArrayList<>();
                    List<String>  labelName = new ArrayList<>();
                    for (String labelId : agentLabelId.split(CommonConstant.COMMA)) {
                        if (finalLabelIdPOMap.containsKey(labelId)) {
                            AgentLabelPO agentLabelPO = finalLabelIdPOMap.get(labelId);
                            AgentLabelResponseVO resp = new AgentLabelResponseVO();
                            resp.setColor("");
                            resp.setLabelName(agentLabelPO.getName());
                            labelResp.add(resp);
                            labelName.add(agentLabelPO.getName());
                        }
                    }
                    item.setLabelList(labelResp);
                    item.setLabelNameList(String.join("#",labelName));
                }else {
                    item.setLabelList(new ArrayList<>());
                }
                item.setLoginTimeText(timestampToString(item.getLoginTime(), param.getTimezone()));

                item.setAgentTypeText(agentTypeMap.get(item.getAgentType()));
                item.setLoginStatusText(loginTypeMap.get(item.getLoginStatus()));


            });
            vo.setAgentLoginRecordPageVO(result);
            vo.setSuccessLoginCount(agentLoginRecordRepository.selectCount(getCommonQuery(param).eq(
                    AgentLoginRecordPO::getLoginStatus, LoginTypeEnum.SUCCESS.getCode())));
            vo.setFailLoginCount(agentLoginRecordRepository.selectCount(getCommonQuery(param).eq(
                    AgentLoginRecordPO::getLoginStatus, LoginTypeEnum.FAIL.getCode())));
            vo.setTotalLoginCount(agentLoginRecordRepository.selectCount(getCommonQuery(param)));
            return ResponseVO.success(vo);
        } catch (Exception e) {
            log.error("查询代理登录日志发生异常", e);
            return ResponseVO.fail(ResultCode.QUERY_AGENT_LOGIN_ERROR);
        }
    }

    public static String convertUTCPlus8ToOffset(String utcStr) {
        String signAndHour = utcStr.substring(3);
        char sign = signAndHour.charAt(0);
        int hour = Integer.parseInt(signAndHour.substring(1));
        return String.format("%c%02d:00", sign, hour);
    }

    public static String timestampToString(long timestampMillis, String timeZone) {
        ZoneOffset offset = ZoneOffset.ofHours(Integer.parseInt(timeZone.substring(3)));
        OffsetDateTime odt = Instant.ofEpochMilli(timestampMillis).atOffset(offset);
        return odt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private LambdaQueryWrapper<AgentLoginRecordPO> getCommonQuery(
            final AgentLoginRecordParam param) {
        LambdaQueryWrapper<AgentLoginRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(param.getAgentAccount())) {
            queryWrapper.eq(AgentLoginRecordPO::getAgentAccount, param.getAgentAccount());
        }
        if (ObjectUtil.isNotEmpty(param.getAgentType())) {
            queryWrapper.eq(AgentLoginRecordPO::getAgentType, param.getAgentType());
        }
        if (ObjectUtil.isNotEmpty(param.getLoginStatus())) {
            queryWrapper.eq(AgentLoginRecordPO::getLoginStatus, param.getLoginStatus());
        }
        if (ObjectUtil.isNotEmpty(param.getLoginIp())) {
            queryWrapper.eq(AgentLoginRecordPO::getLoginIp, param.getLoginIp());
        }
        if (ObjectUtil.isNotEmpty(param.getIpAttribution())) {
            queryWrapper.eq(AgentLoginRecordPO::getIpAttribution, param.getIpAttribution());
        }
        if (ObjectUtil.isNotEmpty(param.getLoginDevice())) {
            queryWrapper.in(AgentLoginRecordPO::getLoginDevice, param.getLoginDevice());
        }
        if (ObjectUtil.isNotEmpty(param.getDeviceNumber())) {
            queryWrapper.eq(AgentLoginRecordPO::getDeviceNumber, param.getDeviceNumber());
        }
        queryWrapper.gt(AgentLoginRecordPO::getLoginTime, param.getStartTime());
        queryWrapper.lt(AgentLoginRecordPO::getLoginTime, param.getEndTime());
        queryWrapper.eq(AgentLoginRecordPO::getSiteCode, CurrReqUtils.getSiteCode());
        return queryWrapper;
    }

    public ResponseVO<?> insertAgentLoginRecord(final AgentLoginRecordInsertVO vo) {
        try {
            AgentLoginRecordPO po = new AgentLoginRecordPO();
            po.setAgentId(vo.getAgentId());
            po.setAgentAccount(vo.getAgentAccount());
            po.setAgentType(Integer.parseInt(vo.getAgentType()));
            po.setLoginIp(vo.getLoginIp());
            po.setLoginAddress(vo.getLoginAddress());
            po.setLoginDevice(Integer.parseInt(vo.getLoginDevice()));
            po.setDeviceNumber(vo.getDeviceNumber());
            po.setDeviceVersion(vo.getDeviceVersion());
            po.setLoginTime(vo.getLoginTime());
            po.setIpAttribution(vo.getIpAttribution());
            po.setLoginStatus(Integer.parseInt(vo.getLoginStatus()));
            po.setRemark(vo.getRemark());
            po.setCreatedTime(System.currentTimeMillis());
            po.setUpdatedTime(System.currentTimeMillis());
            po.setSiteCode(vo.getSiteCode());
            po.setAgentLabelId(vo.getAgentLabelId());
            // IP风控层级
            if (ObjectUtil.isNotEmpty(vo.getLoginIp())) {
                RiskAccountQueryVO riskAccountQueryVO = new RiskAccountQueryVO();
                riskAccountQueryVO.setRiskControlAccount(vo.getLoginIp());
                riskAccountQueryVO.setRiskControlTypeCode(RiskTypeEnum.RISK_IP.getCode());
                RiskAccountVO riskAccountVO = riskApi.getRiskAccountByAccount(riskAccountQueryVO);
                if (riskAccountVO != null)
                    po.setIpControlId(riskAccountVO.getRiskControlLevelId());
            }
            // 终端设备风控层级
            if (ObjectUtil.isNotEmpty(vo.getDeviceNumber())) {
                RiskAccountQueryVO riskAccountQueryVO = new RiskAccountQueryVO();
                riskAccountQueryVO.setRiskControlAccount(vo.getDeviceNumber());
                riskAccountQueryVO.setRiskControlTypeCode(RiskTypeEnum.RISK_DEVICE.getCode());
                RiskAccountVO riskAccountVO = riskApi.getRiskAccountByAccount(riskAccountQueryVO);
                if (riskAccountVO != null)
                    po.setDeviceControlId(riskAccountVO.getRiskControlLevelId());
            }
            agentLoginRecordRepository.insert(po);
            return ResponseVO.success();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("记录代理日志信息发生异常", e);
            return ResponseVO.fail(ResultCode.INSERT_AGENT_LOGIN_ERROR);
        }
    }

    public AgentLoginRecordPageVO getLatestLoginRecord(String agentId) {
        LambdaQueryWrapper<AgentLoginRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentLoginRecordPO::getAgentId, agentId);
        queryWrapper.orderByDesc(AgentLoginRecordPO::getLoginTime);
        queryWrapper.last("limit 1");
        AgentLoginRecordPO po = agentLoginRecordRepository.selectOne(queryWrapper);

        return ConvertUtil.entityToModel(po, AgentLoginRecordPageVO.class);
    }
}
