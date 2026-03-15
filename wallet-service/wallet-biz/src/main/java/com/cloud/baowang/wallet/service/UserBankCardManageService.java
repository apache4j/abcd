package com.cloud.baowang.wallet.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.SymbolUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.GetByUserAccountVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.wallet.api.vo.userbankcard.UserBankCardManagePageVO;
import com.cloud.baowang.wallet.api.vo.userbankcard.UserBankCardManageResponseVO;
import com.cloud.baowang.wallet.po.UserBankCardManagePO;
import com.cloud.baowang.wallet.repositories.UserBankCardManageRepository;
import com.cloud.baowang.wallet.api.vo.userbankcard.EditBankCardInfoVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class UserBankCardManageService extends ServiceImpl<UserBankCardManageRepository, UserBankCardManagePO> {

    private final UserBankCardManageRepository userBankCardManageRepository;

    private final UserInfoApi userInfoApi;

    private final SystemParamApi systemParamApi;

    private final RiskApi riskApi;

    public ResponseVO<Boolean> updateBankInfoById(EditBankCardInfoVO editBankCardInfoVO) {
        int update = userBankCardManageRepository.update(null, Wrappers.<UserBankCardManagePO>lambdaUpdate()
                .eq(UserBankCardManagePO::getId, editBankCardInfoVO.getId())
                .set(editBankCardInfoVO.getRiskControlLevelId() != null, UserBankCardManagePO::getRiskControlLevelId, editBankCardInfoVO.getRiskControlLevelId())
                .set(UserBankCardManagePO::getUpdater, editBankCardInfoVO.getUpdater())
                .set(UserBankCardManagePO::getUpdatedTime, System.currentTimeMillis())
                .set(UserBankCardManagePO::getLastOperator, editBankCardInfoVO.getUpdaterName())
        );

        return update > 0 ? ResponseVO.success(true) : ResponseVO.fail(ResultCode.UPDATE_FAIL);

    }


    public ResponseVO<Page<UserBankCardManageResponseVO>> queryBankCardInfo(final UserBasicRequestVO requestVO) {
        try {
            UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(requestVO);
            if (userInfoVO != null) {
                requestVO.setUserAccount(userInfoVO.getUserAccount());
            }
            UserBankCardManagePageVO vo = new UserBankCardManagePageVO();
            vo.setCurrentBindingUserAccount(requestVO.getUserAccount());
            vo.setPageNumber(requestVO.getPageNumber());
            vo.setPageSize(requestVO.getPageSize());
            vo.setDataDesensitization(requestVO.getDataDesensitization());
            // 默认绑定时间
            vo.setOrderField(UserBankCardManagePO.Fields.firstUseTime);
            vo.setOrderType(CommonConstant.ORDER_BY_DESC);
            vo.setSiteCode(requestVO.getSiteCode());
            Page<UserBankCardManageResponseVO> result = getPage(vo);
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("查询会员:{}银行卡信息异常", requestVO.getUserAccount(), e);
            return ResponseVO.fail(ResultCode.USER_BANK_CARD_QUERY_ERROR);
        }
    }

    public Page<UserBankCardManageResponseVO> getPage(UserBankCardManagePageVO vo) {
        LambdaQueryWrapper<UserBankCardManagePO> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotEmpty(vo.getBankCardNo())) {
            queryWrapper.eq(UserBankCardManagePO::getBankCardNo, vo.getBankCardNo());
        }
        if (StrUtil.isNotEmpty(vo.getBankName())) {
            queryWrapper.eq(UserBankCardManagePO::getBankName, vo.getBankName());
        }
        if (StrUtil.isNotEmpty(vo.getBranchBankName())) {
            queryWrapper.eq(UserBankCardManagePO::getBranchBankName, vo.getBranchBankName());
        }
        if (null != vo.getBlackStatus()) {
            queryWrapper.eq(UserBankCardManagePO::getBlackStatus, vo.getBlackStatus());
        }
        if (null != vo.getBindingStatus()) {
            queryWrapper.eq(UserBankCardManagePO::getBindingStatus, vo.getBindingStatus());
        }
        if (StrUtil.isNotEmpty(vo.getLastOperator())) {
            queryWrapper.eq(UserBankCardManagePO::getLastOperator, vo.getLastOperator());
        }
        if (StrUtil.isNotEmpty(vo.getUserName())) {
            List<String> userAccounts = userInfoApi.getUserAccountByName(vo.getUserName(),vo.getSiteCode());
            if (CollUtil.isEmpty(userAccounts)) {
                return new Page<>();
            } else {
                // userAccounts不为空
                if (StrUtil.isNotEmpty(vo.getCurrentBindingUserAccount())) {
                    if (userAccounts.contains(vo.getCurrentBindingUserAccount())) {
                        queryWrapper.eq(UserBankCardManagePO::getCurrentBindingUserAccount, vo.getCurrentBindingUserAccount());
                    } else {
                        return new Page<>();
                    }
                } else {
                    queryWrapper.in(UserBankCardManagePO::getCurrentBindingUserAccount, userAccounts);
                }
            }
        } else {
            if (StrUtil.isNotEmpty(vo.getCurrentBindingUserAccount())) {
                queryWrapper.eq(UserBankCardManagePO::getCurrentBindingUserAccount, vo.getCurrentBindingUserAccount());
            }
        }
        if (null != vo.getUserWithdrawFailTimesMin()) {
            queryWrapper.ge(UserBankCardManagePO::getUserWithdrawFailTimes, vo.getUserWithdrawFailTimesMin());
        }
        if (null != vo.getUserWithdrawFailTimesMax()) {
            queryWrapper.le(UserBankCardManagePO::getUserWithdrawFailTimes, vo.getUserWithdrawFailTimesMax());
        }
        if (null != vo.getUserWithdrawSuccessTimesMin()) {
            queryWrapper.ge(UserBankCardManagePO::getUserWithdrawSuccessTimes, vo.getUserWithdrawSuccessTimesMin());
        }
        if (null != vo.getUserWithdrawSuccessTimesMax()) {
            queryWrapper.le(UserBankCardManagePO::getUserWithdrawSuccessTimes, vo.getUserWithdrawSuccessTimesMax());
        }
        if (null != vo.getUserWithdrawSumAmountMin()) {
            queryWrapper.ge(UserBankCardManagePO::getUserWithdrawSumAmount, vo.getUserWithdrawSumAmountMin());
        }
        if (null != vo.getUserWithdrawSumAmountMax()) {
            queryWrapper.le(UserBankCardManagePO::getUserWithdrawSumAmount, vo.getUserWithdrawSumAmountMax());
        }
        if (CollUtil.isNotEmpty(vo.getRiskControlLevelId())) {
            queryWrapper.in(UserBankCardManagePO::getRiskControlLevelId, vo.getRiskControlLevelId());
        }
        if (null != vo.getAgentWithdrawFailTimesMin()) {
            queryWrapper.ge(UserBankCardManagePO::getAgentWithdrawFailTimes, vo.getAgentWithdrawFailTimesMin());
        }
        if (null != vo.getAgentWithdrawFailTimesMax()) {
            queryWrapper.le(UserBankCardManagePO::getAgentWithdrawFailTimes, vo.getAgentWithdrawFailTimesMax());
        }
        if (null != vo.getAgentWithdrawSuccessTimesMin()) {
            queryWrapper.ge(UserBankCardManagePO::getAgentWithdrawSuccessTimes, vo.getAgentWithdrawSuccessTimesMin());
        }
        if (null != vo.getAgentWithdrawSuccessTimesMax()) {
            queryWrapper.le(UserBankCardManagePO::getAgentWithdrawSuccessTimes, vo.getAgentWithdrawSuccessTimesMax());
        }
        if (null != vo.getAgentWithdrawSumAmountMin()) {
            queryWrapper.ge(UserBankCardManagePO::getAgentWithdrawSumAmount, vo.getAgentWithdrawSumAmountMin());
        }
        if (null != vo.getAgentWithdrawSumAmountMax()) {
            queryWrapper.le(UserBankCardManagePO::getAgentWithdrawSumAmount, vo.getAgentWithdrawSumAmountMax());
        }
        if (null != vo.getBindingAccountTimes()) {
            queryWrapper.eq(UserBankCardManagePO::getBindingAccountTimes, vo.getBindingAccountTimes());
        }
        queryWrapper.eq(UserBankCardManagePO::getSiteCode,vo.getSiteCode());
        // 排序
        queryWrapperOrderBy(vo, queryWrapper);

        Page<UserBankCardManagePO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<UserBankCardManagePO> pageList = this.page(page, queryWrapper);

        // 集中获取配置参数
        List<String> types = Lists.newArrayList();
        types.add(CommonConstant.BLACK_STATUS);
        types.add(CommonConstant.BINDING_STATUS);
        Map<String, List<CodeValueVO>> systemParamsMap = systemParamApi.getSystemParamsByList(types).getData();
        List<CodeValueVO> blackStatus = systemParamsMap.get(CommonConstant.BLACK_STATUS);
        List<CodeValueVO> bindingStatus = systemParamsMap.get(CommonConstant.BINDING_STATUS);

        List<UserBankCardManageResponseVO> list = pageList.getRecords().stream().map(record -> {
            UserBankCardManageResponseVO bo = ConvertUtil.entityToModel(record, UserBankCardManageResponseVO.class);

            // 银行卡号-脱敏
            if (vo.getDataDesensitization()) {
                bo.setBankCardNo(SymbolUtil.showBankOrVirtualNo(bo.getBankCardNo()));
            }

            // 黑名单状态 0禁用 1启用
            if (null != bo.getBlackStatus()) {
                String blackStatusName = blackStatus.stream().filter(item ->
                        item.getCode().equals(bo.getBlackStatus().toString())
                ).toList().get(0).getValue();
                bo.setBlackStatusName(blackStatusName);
            }

            // 绑定状态 0未绑定 1绑定中
            if (null != bo.getBindingStatus()) {
                String bindingStatusName = bindingStatus.stream().filter(item ->
                        item.getCode().equals(bo.getBindingStatus().toString())
                ).toList().get(0).getValue();
                bo.setBindingStatusName(bindingStatusName);
            }

            // 风控层级riskControlLevel
            if (null != bo.getRiskControlLevelId()) {
                RiskLevelDetailsVO riskLevelDetailsVO =
                        riskApi.getById(IdVO.builder().id(String.valueOf(bo.getRiskControlLevelId())).build());
                String riskControlLevel = null;
                if (null != riskLevelDetailsVO) {
                    riskControlLevel = riskLevelDetailsVO.getRiskControlLevel();
                }
                bo.setRiskControlLevel(riskControlLevel);
            }

            // 会员姓名
            if (StrUtil.isNotEmpty(bo.getCurrentBindingUserAccount())
                    && !"—".equals(bo.getCurrentBindingUserAccount())) {
                GetByUserAccountVO currentBindingUser = userInfoApi.getByUserAccountAndSiteCode(bo.getCurrentBindingUserAccount(),bo.getSiteCode());
                bo.setUserName(currentBindingUser.getUserName());
            } else {
                bo.setUserName("—");
            }

            return bo;
        }).toList();

        return new Page<UserBankCardManageResponseVO>(vo.getPageNumber(), vo.getPageSize(), pageList.getTotal()).setRecords(list);
    }

    private void queryWrapperOrderBy(UserBankCardManagePageVO vo, LambdaQueryWrapper<UserBankCardManagePO> queryWrapper) {
        if (StrUtil.isNotEmpty(vo.getOrderField())
                && StrUtil.isNotEmpty(vo.getOrderType())) {
            String orderField = vo.getOrderField();
            String orderType = vo.getOrderType();

            if ("firstUseTime".equals(orderField) && CommonConstant.ORDER_BY_ASC.equalsIgnoreCase(orderType)) {
                // 银行卡新增时间 升序排列
                queryWrapper.orderByAsc(UserBankCardManagePO::getFirstUseTime);
            } else if ("firstUseTime".equals(orderField) && CommonConstant.ORDER_BY_DESC.equalsIgnoreCase(orderType)) {
                // 银行卡新增时间 降序排列
                queryWrapper.orderByDesc(UserBankCardManagePO::getFirstUseTime);
            } else if ("lastWithdrawTime".equals(orderField) && CommonConstant.ORDER_BY_ASC.equalsIgnoreCase(orderType)) {
                // 最近提款时间 升序排列
                queryWrapper.orderByAsc(UserBankCardManagePO::getLastWithdrawTime);
            } else if ("lastWithdrawTime".equals(orderField) && CommonConstant.ORDER_BY_DESC.equalsIgnoreCase(orderType)) {
                // 最近提款时间 降序排列
                queryWrapper.orderByDesc(UserBankCardManagePO::getLastWithdrawTime);
            } else if ("updatedTime".equals(orderField) && CommonConstant.ORDER_BY_ASC.equalsIgnoreCase(orderType)) {
                // 最近操作时间 升序排列
                queryWrapper.orderByAsc(UserBankCardManagePO::getUpdatedTime);
            } else if ("updatedTime".equals(orderField) && CommonConstant.ORDER_BY_DESC.equalsIgnoreCase(orderType)) {
                // 最近操作时间 降序排列
                queryWrapper.orderByDesc(UserBankCardManagePO::getUpdatedTime);
            } else {
                // 默认：银行卡新增时间(firstUseTime) 降序排列
                queryWrapper.orderByDesc(UserBankCardManagePO::getFirstUseTime);
            }
        } else {
            // 默认：银行卡新增时间(firstUseTime) 降序排列
            queryWrapper.orderByDesc(UserBankCardManagePO::getFirstUseTime);
        }
    }

}
