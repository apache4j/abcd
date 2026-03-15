package com.cloud.baowang.user.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.SymbolUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.user.reponse.GetRegisterInfoByAccountVO;
import com.cloud.baowang.user.api.vo.user.request.InsertUserRegistrationInfoVO;
import com.cloud.baowang.user.api.vo.user.request.UserRegistrationInfoReqVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskListAccountQueryVO;
import com.cloud.baowang.user.api.vo.user.UserRegistrationInfoResVO;
import com.cloud.baowang.user.po.UserRegistrationInfoPO;
import com.cloud.baowang.user.repositories.UserRegistrationInfoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserRegistrationInfoService {

    private final UserRegistrationInfoRepository userRegistrationInfoRepository;


    private final RiskApi riskApi;


    public Long getTotalCount(UserRegistrationInfoReqVO userRegistrationInfoReqVO) {
        return userRegistrationInfoRepository.getTotalCount(userRegistrationInfoReqVO);
    }

    public ResponseVO<Page<UserRegistrationInfoResVO>> getRegistrationInfo(UserRegistrationInfoReqVO userRegistrationInfoReqVO) {
        Page<UserRegistrationInfoResVO> page = new Page<>(userRegistrationInfoReqVO.getPageNumber(), userRegistrationInfoReqVO.getPageSize());
        Page<UserRegistrationInfoResVO> result = userRegistrationInfoRepository.getByPage(page, userRegistrationInfoReqVO);

        List<UserRegistrationInfoResVO> records = result.getRecords();
        if (CollUtil.isEmpty(records)) {
            return ResponseVO.success(new Page<>());
        }

        // IP风控
        RiskListAccountQueryVO queryVO = new RiskListAccountQueryVO();
        queryVO.setRiskControlTypeCode(RiskTypeEnum.RISK_IP.getCode());
        queryVO.setSiteCode(userRegistrationInfoReqVO.getSiteCode());
        List<String> ipList = records.stream().map(UserRegistrationInfoResVO::getRegisterIp).filter(StringUtils::isNotBlank).toList();
        Map<String, String> ipRiskMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(ipList)) {
            queryVO.setRiskControlAccounts(ipList);
            List<RiskAccountVO> ipRiskListAccount = riskApi.getRiskListAccount(queryVO);
            if (CollectionUtil.isNotEmpty(ipRiskListAccount)) {
                ipRiskMap = ipRiskListAccount.stream().collect(Collectors.toMap(RiskAccountVO::getRiskControlAccount, RiskAccountVO::getRiskControlLevel, (k1, k2) -> k2));
            }
        }

        // 设备id风控
        RiskListAccountQueryVO deviceQueryVO = new RiskListAccountQueryVO();
        deviceQueryVO.setRiskControlTypeCode(RiskTypeEnum.RISK_DEVICE.getCode());
        List<String> deviceNoList = records.stream().map(UserRegistrationInfoResVO::getTerminalDeviceNumber).filter(StringUtils::isNotBlank).toList();
        Map<String, String> deviceRiskMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(deviceNoList)) {
            deviceQueryVO.setRiskControlAccounts(deviceNoList);
            List<RiskAccountVO> deviceRiskListAccount = riskApi.getRiskListAccount(deviceQueryVO);
            if (CollectionUtil.isNotEmpty(deviceRiskListAccount)) {
                deviceRiskMap = deviceRiskListAccount.stream().collect(Collectors.toMap(RiskAccountVO::getRiskControlAccount, RiskAccountVO::getRiskControlLevel, (k1, k2) -> k2));
            }
        }

        for (UserRegistrationInfoResVO record : records) {
            // 敏感信息
            if (userRegistrationInfoReqVO.getDataDesensitization()) {
                if (StringUtils.isNotBlank(record.getEmail()) && record.getEmail().contains("@")) {
                    // 邮箱账号
                    record.setEmail(SymbolUtil.showEmail(record.getEmail()));
                }
                if (StringUtils.isNotBlank(record.getPhone())) {
                    // 手机号码
                    record.setPhone(SymbolUtil.showPhone(record.getPhone()));
                }


            }
            //去掉，号
            if (StringUtils.isNotBlank(record.getPhone())) {
                // 手机号码
                String[] arr = record.getPhone().split(CommonConstant.COMMA);
                if(arr.length == 2){
                    record.setAreaCode(arr[0]);
                    record.setPhone(arr[1]);
                }
            }
            // ip风控
            if (StringUtils.isNotBlank(record.getRegisterIp())) {
                record.setRegisterIpLevel(ipRiskMap.get(record.getRegisterIp()));
            }
            // 设备风控
            if (StringUtils.isNotBlank(record.getTerminalDeviceNumber())) {
                record.setTerminalDeviceNumberLevel(deviceRiskMap.get(record.getTerminalDeviceNumber()));
            }
        }

        return ResponseVO.success(result);
    }

    public void insertUserRegistrationInfo(InsertUserRegistrationInfoVO vo) {
        UserRegistrationInfoPO entity = new UserRegistrationInfoPO();
        entity.setRegistrationTime(System.currentTimeMillis());
        entity.setMemberId(vo.getMemberId());
        entity.setMemberAccount(vo.getMemberAccount());
        entity.setMemberName(vo.getMemberName());
        entity.setMainCurrency(vo.getMainCurrency());
        entity.setMemberType(vo.getMemberType());
        entity.setSuperiorAgent(vo.getSuperiorAgent());
        entity.setAgentId(vo.getAgentId());
        entity.setRegisterIp(vo.getRegisterIp());
        entity.setIpAttribution(vo.getIpAttribution());
        entity.setTerminalDeviceNumber(vo.getTerminalDeviceNumber());
        entity.setRegisterTerminal(vo.getRegisterTerminal());
        entity.setMemberDomain(vo.getMemberDomain());
        entity.setCreator(vo.getCreator());
        entity.setCreatedTime(System.currentTimeMillis());
        entity.setUpdater(vo.getUpdater());
        entity.setUpdatedTime(System.currentTimeMillis());
        entity.setSiteCode(vo.getSiteCode());
        entity.setEmail(vo.getEmail());
        // 去掉 "null"
        /*if ("null".equals(vo.getPhone())) {
            vo.setPhone(null);
        }*/
        entity.setPhone(vo.getPhone());
        userRegistrationInfoRepository.insert(entity);
    }

    public GetRegisterInfoByAccountVO getRegisterInfoByAccount(String userAccount) {
        UserRegistrationInfoPO userRegistrationInfoPO = userRegistrationInfoRepository.selectOne(new LambdaQueryWrapper<UserRegistrationInfoPO>()
                .eq(UserRegistrationInfoPO::getMemberId, userAccount));
        return ConvertUtil.entityToModel(userRegistrationInfoPO, GetRegisterInfoByAccountVO.class);
    }

    public GetRegisterInfoByAccountVO getRegisterInfoByAccountAndSiteCode(String userAccount, String siteCode) {
        UserRegistrationInfoPO userRegistrationInfoPO = userRegistrationInfoRepository.selectOne(new LambdaQueryWrapper<UserRegistrationInfoPO>()
                .eq(UserRegistrationInfoPO::getMemberAccount, userAccount).eq(UserRegistrationInfoPO::getSiteCode, siteCode));
        return ConvertUtil.entityToModel(userRegistrationInfoPO, GetRegisterInfoByAccountVO.class);
    }

    public Page<UserRegistrationInfoResVO> listPage(UserRegistrationInfoReqVO userRegistrationInfoReqVO) {
        Page<UserRegistrationInfoResVO> page = new Page<>(userRegistrationInfoReqVO.getPageNumber(), userRegistrationInfoReqVO.getPageSize());
        return userRegistrationInfoRepository.getByPage(page, userRegistrationInfoReqVO);
    }
}
