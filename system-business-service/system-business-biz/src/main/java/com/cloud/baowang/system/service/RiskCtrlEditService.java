package com.cloud.baowang.system.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.api.AgentDepositWithdrawApi;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.api.AgentMerchantApi;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoModifyVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.MerchantAgentInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithdrawRespVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantRiskUpdateVO;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserDepositWithdrawalResVO;
import com.cloud.baowang.common.redis.utils.IpAPICoUtils;
import com.cloud.baowang.system.api.vo.risk.*;
import com.cloud.baowang.system.po.risk.RiskAccountPO;
import com.cloud.baowang.system.po.risk.RiskChangeRecordPO;
import com.cloud.baowang.system.po.risk.RiskCtrlLevelPO;
import com.cloud.baowang.system.repositories.RiskCtrlLevelRepository;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.GetByUserAccountVO;
import com.cloud.baowang.wallet.api.api.SystemWithdrawWayApi;
import com.cloud.baowang.wallet.api.api.UserDepositWithdrawApi;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.user.api.vo.user.UserInfoEditVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 风控层级service
 */
@Slf4j
@Service
@AllArgsConstructor
public class RiskCtrlEditService extends ServiceImpl<RiskCtrlLevelRepository, RiskCtrlLevelPO> {

    private final UserInfoApi userInfoApi;
    private final AgentInfoApi agentInfoApi;
    private final RiskChangeRecordService riskChangeRecordService;
    private final RiskCtrlLevelRepository riskCtrlLevelRepository;
    private final RiskAccountService riskAccountService;
    private final UserDepositWithdrawApi withdrawApi;
    private final AgentDepositWithdrawApi agentWithdrawApi;
    private final SystemWithdrawWayApi wayApi;
    private final AgentMerchantApi merchantApi;

    /**
     * 根据风控类型+对应类型的账号获取风控详情
     *
     * @param riskInfoReqVO 风控类型+风控账号，如账号风控+会员账号，风险设备终端号+终端设备号
     * @return 对应的数据信息
     */
    public ResponseVO<RiskInfoRespVO> getRiskInfoByType(RiskInfoReqVO riskInfoReqVO) {
        //校验类型是否存在
        RiskTypeEnum riskTypeEnum = RiskTypeEnum.nameOfCode(riskInfoReqVO.getRiskControlTypeCode());
        if (riskTypeEnum == null) {
            throw new BaowangDefaultException(ResultCode.RISK_CONTROLLER_TYPE_IS_ERROR);
        }
        //校验参数
        checkRiskParam(riskTypeEnum, riskInfoReqVO.getRiskControlAccount());
        switch (riskTypeEnum) {
            //风险会员类型风控
            case RISK_MEMBER -> {
                return ResponseVO.success(getUserRiskInfo(riskInfoReqVO));
            }
            //风险代理类型风控
            case RISK_AGENT -> {
                return ResponseVO.success(getAgentRiskInfo(riskInfoReqVO));
            }
            //风险银行卡类型风控
            case RISK_BANK -> {
                return ResponseVO.success(getBankRiskInfo(riskInfoReqVO));
            }
            //风险虚拟币类型风控
            case RISK_VIRTUAL -> {
                return ResponseVO.success(getVirtualRiskInfo(riskInfoReqVO));
            }
            //风险IP类型风控
            case RISK_IP -> {
                return ResponseVO.success(getIPRiskInfo(riskInfoReqVO));
            }
            //风险终端设备号类型风控
            case RISK_DEVICE -> {
                return ResponseVO.success(getDeviceRiskInfo(riskInfoReqVO));
            }
            //风控电子钱包
            case RISK_WALLET -> {
                return ResponseVO.success(getWalletRiskInfo(riskInfoReqVO));
            }
            //风险商务
            case RISK_BUSINESS -> {
                return ResponseVO.success(getBusiness(riskInfoReqVO));
            }
        }
        return ResponseVO.success(null);
    }

    /**
     * 风险商务信息查询
     *
     * @param riskInfoReqVO
     * @return
     */
    private RiskInfoRespVO getBusiness(RiskInfoReqVO riskInfoReqVO) {
        RiskInfoRespVO respVO = new RiskInfoRespVO();

        String riskControlAccount = riskInfoReqVO.getRiskControlAccount();
        String siteCode = riskInfoReqVO.getSiteCode();
        MerchantAgentInfoVO vo = merchantApi.getMerchantAgentInfo(siteCode, riskControlAccount);
        if (vo != null) {
            respVO.setMerchantAccount(vo.getMerchantAccount());
            respVO.setMerchantName(vo.getMerchantName());
            respVO.setAgentCount(vo.getAgentCount());
        }
        riskDesc(riskInfoReqVO, respVO, siteCode);
        return respVO;
    }


    /**
     * 组装风控代理类型所属视图
     *
     * @param riskInfoReqVO 代理账号+siteCode
     * @return 视图
     */
    private RiskInfoRespVO getAgentRiskInfo(RiskInfoReqVO riskInfoReqVO) {
        AgentInfoVO agentInfoVO = agentInfoApi.getByAgentAccountAndSiteCode(riskInfoReqVO.getRiskControlAccount(), riskInfoReqVO.getSiteCode());
        if (agentInfoVO == null) {
            throw new BaowangDefaultException(ResultCode.AGENT_NOT_EXISTS);
        }
        RiskInfoRespVO respVO = BeanUtil.toBean(agentInfoVO, RiskInfoRespVO.class);
        //统计一下当前代理下有多少会员被风控了
        List<UserInfoVO> userInfoVos = userInfoApi.getUserInfoByAgentId(agentInfoVO.getAgentId());
        if (CollectionUtil.isNotEmpty(userInfoVos)) {
            // 统计每个 riskLevelId 的数量
            Map<String, Long> riskLevelCountMap = userInfoVos.stream()
                    .filter(userInfo -> StringUtils.isNotBlank(userInfo.getRiskLevelId()))
                    .collect(Collectors.groupingBy(
                            UserInfoVO::getRiskLevelId, // 分组依据
                            Collectors.counting() // 统计每个分组的数量
                    ));

            // 将统计结果转换为 RiskUserCountVo 的列表
            List<RiskUserCountVo> riskUserCountVos = riskLevelCountMap.entrySet().stream()
                    .map(item -> {
                        RiskUserCountVo vo = new RiskUserCountVo();
                        String riskId = item.getKey();
                        RiskCtrlLevelPO po = riskCtrlLevelRepository.selectById(riskId);
                        if (po != null) {
                            vo.setRiskLevel(po.getRiskControlLevel());
                        }
                        vo.setRiskUserCount(item.getValue());
                        return vo;
                    })
                    .toList();
            Long total = 0L;
            for (RiskUserCountVo riskUserCountVo : riskUserCountVos) {
                total += riskUserCountVo.getRiskUserCount();
            }
            //统计所有风控会员总数
            respVO.setRiskUserCountTotal(total);
            //封装每个风控层级对应的会员数
            respVO.setUserCountVos(riskUserCountVos);
        }
        if (agentInfoVO.getRiskLevelId() != null) {
            riskDesc(riskInfoReqVO, respVO, riskInfoReqVO.getSiteCode());
        }
        return respVO;
    }

    /**
     * 组装设备终端风控详情对象
     *
     * @param riskInfoReqVO 终端号+类型+siteCode
     * @return vo
     */
    private RiskInfoRespVO getDeviceRiskInfo(RiskInfoReqVO riskInfoReqVO) {
        String riskControlAccount = riskInfoReqVO.getRiskControlAccount();
        String riskControlTypeCode = riskInfoReqVO.getRiskControlTypeCode();
        String siteCode = riskInfoReqVO.getSiteCode();
        RiskChangeRecordVO riskChangeRecordVO = riskChangeRecordService.getLastRiskRecord(riskControlAccount, riskControlTypeCode, siteCode);
        if (riskChangeRecordVO == null) {
            throw new BaowangDefaultException(ResultCode.RISK_DEVICE_NO_IS_NOT_EXIST);
        }

        RiskInfoRespVO riskInfoRespVO = new RiskInfoRespVO();
        riskInfoRespVO.setDeviceNo(riskInfoReqVO.getRiskControlAccount());
        riskInfoRespVO.setRiskLevel(riskChangeRecordVO.getRiskAfter());
        riskInfoRespVO.setRiskDesc(riskChangeRecordVO.getRiskDesc());
        return riskInfoRespVO;
    }

    private RiskInfoRespVO getWalletRiskInfo(RiskInfoReqVO riskInfoReqVO) {
        String riskControlAccount = riskInfoReqVO.getRiskControlAccount();
        String siteCode = riskInfoReqVO.getSiteCode();
        String withdrawTypeCode = WithdrawTypeEnum.ELECTRONIC_WALLET.getCode();
        String wayId = riskInfoReqVO.getWayId();

        //代理提款记录数据(使用当前虚拟货币)
        List<AgentDepositWithdrawRespVO> agentDeVOS = agentWithdrawApi.getListByTypeAndAddress(withdrawTypeCode, riskControlAccount, wayId, siteCode);
        //会员提款记录数据(使用当前虚拟货币)
        List<UserDepositWithdrawalResVO> userDeVOS = withdrawApi.getListByBankNoAndSiteCode(withdrawTypeCode, riskControlAccount, wayId, siteCode);

        RiskInfoRespVO result = new RiskInfoRespVO();
        BigDecimal withdrawAmount = BigDecimal.ZERO;
        if (CollectionUtil.isNotEmpty(userDeVOS) && CollectionUtil.isNotEmpty(agentDeVOS)) {
            //用会员提款中的银行卡信息
            UserDepositWithdrawalResVO newUWithdraw = userDeVOS.get(0);
            result.setWalletAccount(riskControlAccount);
            result.setWalletName(newUWithdraw.getDepositWithdrawChannelName());

            for (UserDepositWithdrawalResVO userDeVO : userDeVOS) {
                withdrawAmount = withdrawAmount.add(userDeVO.getArriveAmount());
            }
            for (AgentDepositWithdrawRespVO agentDeVO : agentDeVOS) {
                withdrawAmount = withdrawAmount.add(agentDeVO.getArriveAmount());
            }
        } else if (CollectionUtil.isNotEmpty(userDeVOS)) {
            //用会员提款中的银行卡信息
            UserDepositWithdrawalResVO newUWithdraw = userDeVOS.get(0);
            result.setWalletAccount(riskControlAccount);
            result.setWalletName(newUWithdraw.getDepositWithdrawChannelName());

            for (UserDepositWithdrawalResVO userDeVO : userDeVOS) {
                withdrawAmount = withdrawAmount.add(userDeVO.getArriveAmount());
            }
        } else if (CollectionUtil.isNotEmpty(agentDeVOS)) {
            //用会员提款中的银行卡信息
            AgentDepositWithdrawRespVO newUWithdraw = agentDeVOS.get(0);
            result.setWalletAccount(riskControlAccount);
            result.setWalletName(newUWithdraw.getDepositWithdrawChannelName());

            for (AgentDepositWithdrawRespVO userDeVO : agentDeVOS) {
                withdrawAmount = withdrawAmount.add(userDeVO.getArriveAmount());
            }

        }
        result.setTotalAmount(withdrawAmount);

        riskDesc(riskInfoReqVO, result, riskInfoReqVO.getSiteCode());
        return result;
    }

    /**
     * 组装风控ip查询视图
     *
     * @param riskInfoReqVO ip地址+风控类型ip风控+siteCode
     * @return vo
     */
    private RiskInfoRespVO getIPRiskInfo(RiskInfoReqVO riskInfoReqVO) {
        String riskControlAccount = riskInfoReqVO.getRiskControlAccount();
        String riskControlTypeCode = riskInfoReqVO.getRiskControlTypeCode();
        String siteCode = riskInfoReqVO.getSiteCode();
        RiskChangeRecordVO riskChangeRecordVO = riskChangeRecordService.getLastRiskRecord(riskControlAccount, riskControlTypeCode, siteCode);
        if (riskChangeRecordVO == null) {
            throw new BaowangDefaultException(ResultCode.RISK_IP_NO_IS_NOT_EXIST);
        }
        //String address = IPInfoUtils.getIpInfo(riskInfoReqVO.getRiskControlAccount()).getAddress();
        String address = IpAPICoUtils.getIp(riskInfoReqVO.getRiskControlAccount()).getAddress();
        RiskInfoRespVO riskInfoRespVO = new RiskInfoRespVO();
        riskInfoRespVO.setIp(riskInfoReqVO.getRiskControlAccount());
        riskInfoRespVO.setAddress(address);
        riskInfoRespVO.setRiskLevel(riskChangeRecordVO.getRiskAfter());
        riskInfoRespVO.setRiskDesc(riskChangeRecordVO.getRiskDesc());
        return riskInfoRespVO;
    }

    /**
     * 组装风险虚拟币风控类型查询视图
     *
     * @param riskInfoReqVO 虚拟币地址+siteCode
     * @return vo
     */
    private RiskInfoRespVO getVirtualRiskInfo(RiskInfoReqVO riskInfoReqVO) {

        String riskControlAccount = riskInfoReqVO.getRiskControlAccount();
        String siteCode = riskInfoReqVO.getSiteCode();
        String withdrawTypeCode = WithdrawTypeEnum.CRYPTO_CURRENCY.getCode();
        //代理提款记录数据(使用当前虚拟货币)
        List<AgentDepositWithdrawRespVO> agentDeVOS = agentWithdrawApi.getListByTypeAndAddress(withdrawTypeCode, riskControlAccount, riskInfoReqVO.getWayId(), siteCode);
        //会员提款记录数据(使用当前虚拟货币)
        List<UserDepositWithdrawalResVO> userDeVOS = withdrawApi.getListByBankNoAndSiteCode(withdrawTypeCode, riskControlAccount, riskInfoReqVO.getWayId(), siteCode);

        RiskInfoRespVO result = new RiskInfoRespVO();
        result.setVirtualCurrencyAddress(riskControlAccount);
        BigDecimal withdrawAmount = BigDecimal.ZERO;
        if (CollectionUtil.isNotEmpty(userDeVOS) && CollectionUtil.isNotEmpty(agentDeVOS)) {
            UserDepositWithdrawalResVO newUWithdraw = userDeVOS.get(0);
            result.setVirtualCurrencyType(newUWithdraw.getCoinCode());
            result.setCryptoProtocol(newUWithdraw.getAccountBranch());
            for (UserDepositWithdrawalResVO userDeVO : userDeVOS) {
                withdrawAmount = withdrawAmount.add(userDeVO.getTradeCurrencyAmount());
            }
            for (AgentDepositWithdrawRespVO agentDeVO : agentDeVOS) {
                withdrawAmount = withdrawAmount.add(agentDeVO.getTradeCurrencyAmount());
            }
        } else if (CollectionUtil.isNotEmpty(userDeVOS)) {
            UserDepositWithdrawalResVO newUWithdraw = userDeVOS.get(0);
            result.setVirtualCurrencyType(newUWithdraw.getCoinCode());
            result.setCryptoProtocol(newUWithdraw.getAccountBranch());
            for (UserDepositWithdrawalResVO userDeVO : userDeVOS) {
                withdrawAmount = withdrawAmount.add(userDeVO.getTradeCurrencyAmount());
            }
        } else if (CollectionUtil.isNotEmpty(agentDeVOS)) {
            AgentDepositWithdrawRespVO newUWithdraw = agentDeVOS.get(0);
            result.setVirtualCurrencyType(newUWithdraw.getCoinCode());
            result.setCryptoProtocol(newUWithdraw.getAccountBranch());
            for (AgentDepositWithdrawRespVO userDeVO : agentDeVOS) {
                withdrawAmount = withdrawAmount.add(userDeVO.getTradeCurrencyAmount());
            }
        }
        result.setVirtualAmount(withdrawAmount);
        riskDesc(riskInfoReqVO, result, riskInfoReqVO.getSiteCode());
        return result;
    }

    /**
     * 组装风控银行卡查询信息
     *
     * @param riskInfoReqVO 包含银行卡号，siteCode
     * @return vo
     */
    private RiskInfoRespVO getBankRiskInfo(RiskInfoReqVO riskInfoReqVO) {
        String riskControlAccount = riskInfoReqVO.getRiskControlAccount();
        String siteCode = riskInfoReqVO.getSiteCode();
        String withdrawTypeCode = WithdrawTypeEnum.BANK_CARD.getCode();
        //代理提款记录数据(使用当前银行卡号的)
        List<AgentDepositWithdrawRespVO> agentDeVOS = agentWithdrawApi.getListByTypeAndAddress(withdrawTypeCode, riskControlAccount, riskInfoReqVO.getWayId(), siteCode);
        //会员提款记录数据(使用当前银行卡号的)
        List<UserDepositWithdrawalResVO> userDeVOS = withdrawApi.getListByBankNoAndSiteCode(withdrawTypeCode, riskControlAccount, riskInfoReqVO.getWayId(), siteCode);

        RiskInfoRespVO result = new RiskInfoRespVO();
        result.setBankCardNo(riskControlAccount);
        BigDecimal withdrawAmount = BigDecimal.ZERO;
        if (CollectionUtil.isNotEmpty(userDeVOS) && CollectionUtil.isNotEmpty(agentDeVOS)) {
            //用会员提款中的银行卡信息
            UserDepositWithdrawalResVO newUWithdraw = userDeVOS.get(0);
            result.setBankName(newUWithdraw.getAccountType());

            for (UserDepositWithdrawalResVO userDeVO : userDeVOS) {
                withdrawAmount = withdrawAmount.add(userDeVO.getArriveAmount());
            }
            for (AgentDepositWithdrawRespVO agentDeVO : agentDeVOS) {
                withdrawAmount = withdrawAmount.add(agentDeVO.getArriveAmount());
            }
        } else if (CollectionUtil.isNotEmpty(userDeVOS)) {
            //用会员提款中的银行卡信息
            UserDepositWithdrawalResVO newUWithdraw = userDeVOS.get(0);
            result.setBankName(newUWithdraw.getAccountType());

            for (UserDepositWithdrawalResVO userDeVO : userDeVOS) {
                withdrawAmount = withdrawAmount.add(userDeVO.getArriveAmount());
            }
        } else if (CollectionUtil.isNotEmpty(agentDeVOS)) {
            //用会员提款中的银行卡信息
            AgentDepositWithdrawRespVO newUWithdraw = agentDeVOS.get(0);
            result.setBankName(newUWithdraw.getAccountType());
            for (AgentDepositWithdrawRespVO userDeVO : agentDeVOS) {
                withdrawAmount = withdrawAmount.add(userDeVO.getArriveAmount());
            }
        }
        result.setBankWithdrawAmount(withdrawAmount);
        riskDesc(riskInfoReqVO, result, siteCode);
        return result;
    }

    /**
     * 封装desc和层级（用于风控设备号等一些需要从历史数据中获取信息的视图）
     *
     * @param riskInfoReqVO  风控类型+对应的风控账号+siteCode
     * @param riskInfoRespVO 返回的视图
     * @param siteCode       站点code
     */
    private void riskDesc(RiskInfoReqVO riskInfoReqVO, RiskInfoRespVO riskInfoRespVO, String siteCode) {
        RiskChangeRecordVO riskChangeRecordVO = riskChangeRecordService.getLastRiskRecord(riskInfoReqVO.getRiskControlAccount(), riskInfoReqVO.getRiskControlTypeCode(), siteCode);
        if (riskChangeRecordVO != null) {
            riskInfoRespVO.setRiskDesc(riskChangeRecordVO.getRiskDesc());
            riskInfoRespVO.setRiskLevel(riskChangeRecordVO.getRiskAfter());
        }
    }

    /**
     * 创建会员类型风控查询对象
     *
     * @param vo 会员账号+siteCode
     * @return 会员风控详情视图
     */
    private RiskInfoRespVO getUserRiskInfo(RiskInfoReqVO vo) {
        UserInfoVO userInfo = userInfoApi.getUserByUserAccountAndSiteCode(vo.getRiskControlAccount(), vo.getSiteCode());

        if (userInfo == null) {
            throw new BaowangDefaultException(ResultCode.RISK_USER_IS_NOT_EXIST);
        }
        RiskInfoRespVO riskInfoRespVO = BeanUtil.toBean(userInfo, RiskInfoRespVO.class);

        if (StringUtils.isNotBlank(userInfo.getRiskLevelId())) {
            riskDesc(vo, riskInfoRespVO, vo.getSiteCode());
        }
        return riskInfoRespVO;

    }

    /**
     * 根据当前风控类型，校验参数
     *
     * @param riskTypeEnum       当前发起风控的类型
     * @param riskControlAccount 当前风控类型对应的参数，如类型为会员账号时，当前字段为对应的会员账号
     */
    public void checkRiskParam(RiskTypeEnum riskTypeEnum, String riskControlAccount) {
        /*switch (riskTypeEnum) {
            case RISK_MEMBER, RISK_AGENT -> {
                if (riskControlAccount.length() > 11) {
                    throw new BaowangDefaultException(ResultCode.RISK_MEMBER_MAX_LENGHT);
                }

            }
            case RISK_BANK -> {
                if (riskControlAccount.length() > 25) {
                    throw new BaowangDefaultException(ResultCode.RISK_BANK_MAX_LENGHT);
                }
            }
            case RISK_VIRTUAL -> {
                if (riskControlAccount.length() > 100) {
                    throw new BaowangDefaultException(ResultCode.RISK_VIRTUAL_MAX_LENGHT);
                }
            }
            case RISK_IP -> {
                if (riskControlAccount.length() > 15) {
                    throw new BaowangDefaultException(ResultCode.RISK_IP_MAX_LENGHT);
                }
                if (!IPUtil.validateIP(riskControlAccount)) {
                    throw new BaowangDefaultException(ResultCode.IP_FORMAT_ERROR);
                }
            }
            case RISK_DEVICE -> {
                if (riskControlAccount.length() > 50) {
                    throw new BaowangDefaultException(ResultCode.RISK_DEVICE_MAX_LENGHT);
                }
            }
        }*/
    }

    /**
     * 提交风控编辑
     *
     * @param riskEditReqVO siteCode+风控类型+对应风控账号+风控层级id
     * @return true
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> submitRiskRecord(RiskEditReqVO riskEditReqVO) {
        String siteCode = riskEditReqVO.getSiteCode();
        Long riskLevelId = riskEditReqVO.getRiskLevelId();
        String riskControlAccount = riskEditReqVO.getRiskControlAccount();
        String riskDesc = riskEditReqVO.getRiskDesc();
        //风控id
        if (riskLevelId == null) {
            throw new BaowangDefaultException(ResultCode.RISK_LEVEL_ID_IS_NULL);
        }
        //风控对应的账号（这里不同类型都使用此字段传值）
        if (StringUtils.isBlank(riskControlAccount)) {
            throw new BaowangDefaultException(ResultCode.RISK_ACCOUNT_IS_NULL);
        }
        //风控描述
        if (StringUtils.isBlank(riskDesc)) {
            throw new BaowangDefaultException(ResultCode.RISK_DESC_IS_NULL);
        }
        /*//长度
        if (riskDesc.length() > 50) {
            throw new BaowangDefaultException(ResultCode.RISK_DESC_MAX_LENGHT_LIMIT);
        }*/
        //获取风控层级信息
        RiskCtrlLevelPO riskLevelPO = riskCtrlLevelRepository.selectById(riskLevelId);
        if (riskLevelPO == null) {
            throw new BaowangDefaultException(ResultCode.RISK_LEVEL_IS_NOT_EXIST);
        }
        if (riskLevelPO.getStatus().equals(EnableStatusEnum.DISABLE.getCode())) {
            throw new BaowangDefaultException(ResultCode.RISK_LEVEL_IS_ALREAD_DELETE);
        }

        String riskControlType = riskLevelPO.getRiskControlType();
        String riskControlLevel = riskLevelPO.getRiskControlLevel();
        RiskTypeEnum riskTypeEnum = RiskTypeEnum.nameOfCode(riskControlType);
        if (riskTypeEnum == null) {
            throw new BaowangDefaultException(ResultCode.RISK_CONTROLLER_TYPE_IS_ERROR);
        }
        checkRiskParam(riskTypeEnum, riskControlAccount);
        switch (riskTypeEnum) {
            case RISK_MEMBER -> {
                //变更会员对应风控信息
                GetByUserAccountVO userInfoRO = userInfoApi.getByUserAccountAndSiteCode(riskControlAccount, siteCode);
                if (userInfoRO == null) {
                    throw new BaowangDefaultException(ResultCode.RISK_USER_IS_NOT_EXIST);
                }
                UserInfoEditVO editVO = new UserInfoEditVO();
                editVO.setRiskLevelId(riskLevelPO.getId());
                editVO.setId(userInfoRO.getId());
                //会员信息绑定风控id
                ResponseVO<Boolean> responseVO = userInfoApi.updateUserInfoById(editVO);
                if (!responseVO.isOk()) {
                    throw new BaowangDefaultException(ResultCode.RISK_LEVEL_UPDATE_IS_FAIL);
                }
            }
            case RISK_AGENT -> {
                //变更风险代理对应风险信息
                AgentInfoVO agentInfoVO = agentInfoApi.getByAgentAccountAndSiteCode(riskControlAccount, siteCode);
                if (agentInfoVO == null) {
                    throw new BaowangDefaultException(ResultCode.RISK_USER_IS_NOT_EXIST);
                }
                AgentInfoModifyVO editVO = new AgentInfoModifyVO();
                editVO.setRiskLevelId(riskLevelPO.getId());
                editVO.setId(agentInfoVO.getId());
                ResponseVO<Boolean> responseVO = agentInfoApi.updateAgentInfoById(editVO);
                if (!responseVO.isOk()) {
                    throw new BaowangDefaultException(ResultCode.RISK_LEVEL_UPDATE_IS_FAIL);
                }
            }
            case RISK_WALLET -> {
                //风险电子钱包,校验提款方式是否正确
                String wayId = riskEditReqVO.getWayId();
                if (StringUtils.isBlank(wayId)) {
                    throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
                }
                IdReqVO idReqVO = new IdReqVO();
                idReqVO.setId(wayId);
                if (!wayApi.getInfoById(idReqVO).isOk()) {
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            }
            case RISK_BUSINESS -> {
                //风险商务
                MerchantRiskUpdateVO merchantRiskUpdateVO = new MerchantRiskUpdateVO();
                merchantRiskUpdateVO.setRiskId(riskLevelId + "");
                merchantRiskUpdateVO.setSiteCode(siteCode);
                merchantRiskUpdateVO.setMerchantAccount(riskControlAccount);
                merchantRiskUpdateVO.setAccount(riskEditReqVO.getCreator());
                ResponseVO<Boolean> responseVO = merchantApi.updateRiskInfo(merchantRiskUpdateVO);
                if (!responseVO.isOk()) {
                    throw new BaowangDefaultException(ResultCode.RISK_LEVEL_UPDATE_IS_FAIL);
                }

            }
        }


        // 添加记录
        RiskChangeRecordPO riskChangeRecordPO = new RiskChangeRecordPO();
        riskChangeRecordPO.setSiteCode(siteCode);

        riskChangeRecordPO.setRiskControlAccount(riskControlAccount);
        riskChangeRecordPO.setRiskControlType(riskControlType);
        riskChangeRecordPO.setRiskAfter(riskControlLevel);
        riskChangeRecordPO.setRiskDesc(riskDesc);
        //判断站点下 当前风控类型，对应账号有没有风控记录，如风险ip,风险终端设备号类型
        RiskChangeRecordVO vo = riskChangeRecordService.getLastRiskRecord(riskControlAccount, riskControlType, siteCode);
        //变更前为之前的历史记录
        riskChangeRecordPO.setRiskBefore(vo == null ? null : vo.getRiskAfter());
        riskChangeRecordPO.setCreator(riskEditReqVO.getCreator());
        riskChangeRecordPO.setCreatedTime(System.currentTimeMillis());
        riskChangeRecordPO.setUpdater(riskEditReqVO.getCreator());
        riskChangeRecordPO.setUpdatedTime(System.currentTimeMillis());
        boolean recordSaveFlag = riskChangeRecordService.save(riskChangeRecordPO);
        if (!recordSaveFlag) {
            throw new BaowangDefaultException(ResultCode.RISK_RECORD_ADD_IS_FAIL);
        }


        // riskAccount记录
        LambdaQueryWrapper<RiskAccountPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RiskAccountPO::getRiskControlAccount, riskControlAccount);
        lambdaQueryWrapper.eq(RiskAccountPO::getRiskControlType, riskControlType);
        lambdaQueryWrapper.eq(RiskAccountPO::getSiteCode, siteCode);
        RiskAccountPO riskAccountPO = riskAccountService.getOne(lambdaQueryWrapper);

        RiskAccountPO newRiskAccountPO = new RiskAccountPO();
        newRiskAccountPO.setSiteCode(siteCode);
        newRiskAccountPO.setRiskControlAccount(riskEditReqVO.getRiskControlAccount());
        newRiskAccountPO.setRiskControlLevelId(riskLevelPO.getId());
        newRiskAccountPO.setRiskControlType(riskLevelPO.getRiskControlType());
        newRiskAccountPO.setRiskControlLevel(riskLevelPO.getRiskControlLevel());
        newRiskAccountPO.setRiskControlTypeCode(riskLevelPO.getRiskControlType());
        newRiskAccountPO.setRiskDesc(riskEditReqVO.getRiskDesc());
        if (RiskTypeEnum.RISK_WALLET.getCode().equals(riskTypeEnum.getCode())) {
            //电子钱包类型,不仅要存储一个账号,再多存储一个提款方式id
            newRiskAccountPO.setWithdrawWayId(riskEditReqVO.getWayId());
        }

        if (riskAccountPO == null) {
            newRiskAccountPO.setCreator(riskEditReqVO.getCreator());
            newRiskAccountPO.setCreatedTime(System.currentTimeMillis());

        } else {
            newRiskAccountPO.setId(riskAccountPO.getId());
        }
        newRiskAccountPO.setUpdater(riskEditReqVO.getCreator());
        newRiskAccountPO.setUpdatedTime(System.currentTimeMillis());
        if (!riskAccountService.saveOrUpdate(newRiskAccountPO)) {
            throw new BaowangDefaultException(ResultCode.RISK_RECORD_SAVE_IS_FAIL);
        }

        return ResponseVO.success(true);
    }
}
