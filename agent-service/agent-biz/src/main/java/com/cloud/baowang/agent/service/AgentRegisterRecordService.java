package com.cloud.baowang.agent.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentTypeEnum;
import com.cloud.baowang.agent.api.vo.agentRegister.AgentRegisterInfo;
import com.cloud.baowang.agent.api.vo.agentRegister.AgentRegisterRecordInsertVO;
import com.cloud.baowang.agent.api.vo.agentRegister.AgentRegisterRecordParam;
import com.cloud.baowang.agent.api.vo.agentRegister.AgentRegisterRecordVO;
import com.cloud.baowang.agent.po.AgentRegisterInfoPO;
import com.cloud.baowang.agent.repositories.AgentRegisterRecordRepository;
import com.cloud.baowang.common.core.enums.DeviceType;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author : 小智
 * @Date : 10/10/23 10:57 PM
 * @Version : 1.0
 */
@Service
public class AgentRegisterRecordService extends ServiceImpl<AgentRegisterRecordRepository,
        AgentRegisterInfoPO> {

    @Resource
    private  AgentRegisterRecordRepository agentRegisterRecordRepository;

    @Autowired
    private RiskApi riskApi;


    public ResponseVO<Page<AgentRegisterRecordVO>> queryAgentRegisterRecord(
            final AgentRegisterRecordParam param) {
        try {
            Page<AgentRegisterInfoPO> page = new Page<>(param.getPageNumber(), param.getPageSize());
            Page<AgentRegisterRecordVO> result = agentRegisterRecordRepository.queryAgentRegisterRecord(page,
                    param);
            result.getRecords().forEach(obj->{
                obj.setAgentTypeName(AgentTypeEnum.nameOfCode(Integer.parseInt(obj.getAgentType())).getName());
                obj.setRegisterDeviceName(Objects.requireNonNull(DeviceType.nameOfCode(Integer.parseInt(obj
                        .getRegisterDevice()))).getName());
                // IP风控层级
                if (null != obj.getRegisterIpControlId()) {
                    RiskLevelDetailsVO riskLevelDetailsVO =
                            riskApi.getById(IdVO.builder().id(obj.getRegisterIpControlId().toString()).build());
                    String riskLevel = null;
                    if (null != riskLevelDetailsVO) {
                        riskLevel = riskLevelDetailsVO.getRiskControlLevel();
                    }
                    obj.setRegisterIpControlName(riskLevel);
                }
                // 终端设备号风控层级
                if (null != obj.getDeviceControlId()) {
                    RiskLevelDetailsVO riskLevelDetailsVO =
                            riskApi.getById(IdVO.builder().id(obj.getDeviceControlId().toString()).build());
                    String riskLevel = null;
                    if (null != riskLevelDetailsVO) {
                        riskLevel = riskLevelDetailsVO.getRiskControlLevel();
                    }
                    obj.setDeviceControlName(riskLevel);
                }
            });
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("查询代理注册信息记录发生异常", e);
            return ResponseVO.fail(ResultCode.QUERY_AGENT_REGISTER_RECORD_ERROR);
        }
    }

    public ResponseVO<?> recordAgentRegister(final AgentRegisterRecordInsertVO vo){
        try {
            AgentRegisterInfoPO po = new AgentRegisterInfoPO();
            po.setAgentId(vo.getAgentId());
            po.setAgentAccount(vo.getAgentAccount());
            po.setRegisterIp(vo.getRegisterIp());
            po.setRegistrant(vo.getRegistrant());
            po.setIpAttribution(vo.getIpAttribution());
            po.setRegisterTime(vo.getRegisterTime());
            po.setAgentType(String.valueOf(vo.getAgentType()));
            po.setRegisterDevice(String.valueOf(vo.getRegisterDevice()));
            po.setDeviceNumber(vo.getDeviceNumber());
            po.setCreatedTime(System.currentTimeMillis());
            po.setUpdatedTime(System.currentTimeMillis());
            po.setSiteCode(vo.getSiteCode());
            // IP风控层级
            if (ObjectUtil.isNotEmpty(vo.getRegisterIp())) {
                RiskAccountQueryVO riskAccountQueryVO = new RiskAccountQueryVO();
                riskAccountQueryVO.setRiskControlAccount(vo.getRegisterIp());
                riskAccountQueryVO.setRiskControlTypeCode(RiskTypeEnum.RISK_IP.getCode());
                RiskAccountVO riskAccountVO = riskApi.getRiskAccountByAccount(riskAccountQueryVO);
                if (riskAccountVO != null)
                    po.setRegisterIpControlId(riskAccountVO.getRiskControlLevelId());
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
            agentRegisterRecordRepository.insert(po);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error("代理注册记录信息表发生异常", e);
            return ResponseVO.fail(ResultCode.INSERT_AGENT_REGISTER_RECORD_ERROR);
        }
    }

    public AgentRegisterInfo getRegisterInfoByAccount(String agentAccount, String siteCode) {
        AgentRegisterInfoPO po = agentRegisterRecordRepository.selectOne(new LambdaQueryWrapper<AgentRegisterInfoPO>()
                .eq(AgentRegisterInfoPO::getAgentAccount, agentAccount)
                .eq(AgentRegisterInfoPO::getSiteCode, siteCode));
        return ConvertUtil.entityToModel(po, AgentRegisterInfo.class);
    }
}
