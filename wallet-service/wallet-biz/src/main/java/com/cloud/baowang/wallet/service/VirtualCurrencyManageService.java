package com.cloud.baowang.wallet.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.cloud.baowang.wallet.api.vo.userbankcard.VirtualCurrencyManagePageVO;
import com.cloud.baowang.wallet.api.vo.uservirtualcurrency.VirtualCurrencyManageResponseVO;
import com.cloud.baowang.wallet.po.VirtualCurrencyManagePO;
import com.cloud.baowang.wallet.repositories.VirtualCurrencyManageRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.wallet.api.vo.uservirtualcurrency.EditVirtualCurrencyAddressVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 会员虚拟币账号管理 服务类
 *
 */
@Slf4j
@Service
@AllArgsConstructor
public class VirtualCurrencyManageService extends ServiceImpl<VirtualCurrencyManageRepository, VirtualCurrencyManagePO> {

    private final VirtualCurrencyManageRepository virtualCurrencyManageRepository;

    private final UserInfoApi userInfoApi;

    private final SystemParamApi systemParamApi;

    private final RiskApi riskApi;

    public ResponseVO<Boolean> updateVirtualCurrencyById(EditVirtualCurrencyAddressVO editVirtualCurrencyAddressVO) {
        int update = virtualCurrencyManageRepository.update(null, Wrappers.<VirtualCurrencyManagePO>lambdaUpdate()
                .eq(VirtualCurrencyManagePO::getId, editVirtualCurrencyAddressVO.getId())
                .set(editVirtualCurrencyAddressVO.getRiskControlLevelId() != null, VirtualCurrencyManagePO::getRiskControlLevelId, editVirtualCurrencyAddressVO.getRiskControlLevelId())
                .set(VirtualCurrencyManagePO::getUpdater, editVirtualCurrencyAddressVO.getUpdater())
                .set(VirtualCurrencyManagePO::getUpdatedTime, System.currentTimeMillis())
                .set(VirtualCurrencyManagePO::getLastOperator, editVirtualCurrencyAddressVO.getUpdaterName())
        );

        return  update > 0 ? ResponseVO.success(true):ResponseVO.fail(ResultCode.UPDATE_FAIL);
    }
    public ResponseVO<Page<VirtualCurrencyManageResponseVO>> queryVirtualInfo(final UserBasicRequestVO requestVO) {
        try {
            UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(requestVO);
            if (userInfoVO != null) {
                requestVO.setUserAccount(userInfoVO.getUserAccount());
            }
            VirtualCurrencyManagePageVO vo = new VirtualCurrencyManagePageVO();
            vo.setCurrentBindingUserAccount(requestVO.getUserAccount());
            vo.setPageNumber(requestVO.getPageNumber());
            vo.setPageSize(requestVO.getPageSize());
            vo.setDataDesensitization(requestVO.getDataDesensitization());
            // 默认绑定时间
            vo.setOrderField(VirtualCurrencyManagePO.Fields.firstUseTime);
            vo.setOrderType(CommonConstant.ORDER_BY_DESC);
            vo.setSiteCode(requestVO.getSiteCode());
            Page<VirtualCurrencyManageResponseVO> result = getPage(vo);
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("查询会员:{}虚拟币账户信息异常", requestVO.getUserAccount(), e);
            return ResponseVO.fail(ResultCode.USER_VIRTUAL_CURRENCY_QUERY_ERROR);
        }
    }
    public Page<VirtualCurrencyManageResponseVO> getPage(VirtualCurrencyManagePageVO vo) {
        LambdaQueryWrapper<VirtualCurrencyManagePO> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotEmpty(vo.getVirtualCurrencyAddress())) {
            queryWrapper.eq(VirtualCurrencyManagePO::getVirtualCurrencyAddress, vo.getVirtualCurrencyAddress());
        }
        if (StrUtil.isNotEmpty(vo.getVirtualCurrencyType())) {
            queryWrapper.eq(VirtualCurrencyManagePO::getVirtualCurrencyType, vo.getVirtualCurrencyType());
        }
        if (StrUtil.isNotEmpty(vo.getVirtualCurrencyProtocol())) {
            queryWrapper.eq(VirtualCurrencyManagePO::getVirtualCurrencyProtocol, vo.getVirtualCurrencyProtocol());
        }
        if (null != vo.getBlackStatus()) {
            queryWrapper.eq(VirtualCurrencyManagePO::getBlackStatus, vo.getBlackStatus());
        }
        if (null != vo.getBindingStatus()) {
            queryWrapper.eq(VirtualCurrencyManagePO::getBindingStatus, vo.getBindingStatus());
        }
        if (StrUtil.isNotEmpty(vo.getLastOperator())) {
            queryWrapper.eq(VirtualCurrencyManagePO::getLastOperator, vo.getLastOperator());
        }
        if (StrUtil.isNotEmpty(vo.getUserName())) {
            List<String> userAccounts = userInfoApi.getUserAccountByName(vo.getUserName(),vo.getSiteCode());
            if (CollUtil.isEmpty(userAccounts)) {
                return new Page<>();
            } else {
                // userAccounts不为空
                if (StrUtil.isNotEmpty(vo.getCurrentBindingUserAccount())) {
                    if (userAccounts.contains(vo.getCurrentBindingUserAccount())) {
                        queryWrapper.eq(VirtualCurrencyManagePO::getCurrentBindingUserAccount, vo.getCurrentBindingUserAccount());
                    } else {
                        return new Page<>();
                    }
                } else {
                    queryWrapper.in(VirtualCurrencyManagePO::getCurrentBindingUserAccount, userAccounts);
                }
            }
        } else {
            if (StrUtil.isNotEmpty(vo.getCurrentBindingUserAccount())) {
                queryWrapper.eq(VirtualCurrencyManagePO::getCurrentBindingUserAccount, vo.getCurrentBindingUserAccount());
            }
        }
        if (null != vo.getUserWithdrawFailTimesMin()) {
            queryWrapper.ge(VirtualCurrencyManagePO::getUserWithdrawFailTimes, vo.getUserWithdrawFailTimesMin());
        }
        if (null != vo.getUserWithdrawFailTimesMax()) {
            queryWrapper.le(VirtualCurrencyManagePO::getUserWithdrawFailTimes, vo.getUserWithdrawFailTimesMax());
        }
        if (null != vo.getUserWithdrawSuccessTimesMin()) {
            queryWrapper.ge(VirtualCurrencyManagePO::getUserWithdrawSuccessTimes, vo.getUserWithdrawSuccessTimesMin());
        }
        if (null != vo.getUserWithdrawSuccessTimesMax()) {
            queryWrapper.le(VirtualCurrencyManagePO::getUserWithdrawSuccessTimes, vo.getUserWithdrawSuccessTimesMax());
        }
        if (null != vo.getUserWithdrawSumAmountMin()) {
            queryWrapper.ge(VirtualCurrencyManagePO::getUserWithdrawSumAmount, vo.getUserWithdrawSumAmountMin());
        }
        if (null != vo.getUserWithdrawSumAmountMax()) {
            queryWrapper.le(VirtualCurrencyManagePO::getUserWithdrawSumAmount, vo.getUserWithdrawSumAmountMax());
        }
        if (CollUtil.isNotEmpty(vo.getRiskControlLevelId())) {
            queryWrapper.in(VirtualCurrencyManagePO::getRiskControlLevelId, vo.getRiskControlLevelId());
        }
        if (null != vo.getAgentWithdrawFailTimesMin()) {
            queryWrapper.ge(VirtualCurrencyManagePO::getAgentWithdrawFailTimes, vo.getAgentWithdrawFailTimesMin());
        }
        if (null != vo.getAgentWithdrawFailTimesMax()) {
            queryWrapper.le(VirtualCurrencyManagePO::getAgentWithdrawFailTimes, vo.getAgentWithdrawFailTimesMax());
        }
        if (null != vo.getAgentWithdrawSuccessTimesMin()) {
            queryWrapper.ge(VirtualCurrencyManagePO::getAgentWithdrawSuccessTimes, vo.getAgentWithdrawSuccessTimesMin());
        }
        if (null != vo.getAgentWithdrawSuccessTimesMax()) {
            queryWrapper.le(VirtualCurrencyManagePO::getAgentWithdrawSuccessTimes, vo.getAgentWithdrawSuccessTimesMax());
        }
        if (null != vo.getAgentWithdrawSumAmountMin()) {
            queryWrapper.ge(VirtualCurrencyManagePO::getAgentWithdrawSumAmount, vo.getAgentWithdrawSumAmountMin());
        }
        if (null != vo.getAgentWithdrawSumAmountMax()) {
            queryWrapper.le(VirtualCurrencyManagePO::getAgentWithdrawSumAmount, vo.getAgentWithdrawSumAmountMax());
        }
        if (null != vo.getBindingAccountTimes()) {
            queryWrapper.eq(VirtualCurrencyManagePO::getBindingAccountTimes, vo.getBindingAccountTimes());
        }
        // siteCode
        queryWrapper.eq(VirtualCurrencyManagePO::getSiteCode,vo.getSiteCode());
        // 排序
        queryWrapperOrderBy(vo, queryWrapper);


        Page<VirtualCurrencyManagePO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<VirtualCurrencyManagePO> pageList = this.page(page, queryWrapper);

        // 集中获取配置参数
        List<String> types = Lists.newArrayList();
        types.add(CommonConstant.BLACK_STATUS);
        types.add(CommonConstant.BINDING_STATUS);
        Map<String, List<CodeValueVO>> systemParamsMap = systemParamApi.getSystemParamsByList(types).getData();
        List<CodeValueVO> blackStatus = systemParamsMap.get(CommonConstant.BLACK_STATUS);
        List<CodeValueVO> bindingStatus = systemParamsMap.get(CommonConstant.BINDING_STATUS);

        List<VirtualCurrencyManageResponseVO> list = pageList.getRecords().stream().map(record -> {
            VirtualCurrencyManageResponseVO bo = ConvertUtil.entityToModel(record, VirtualCurrencyManageResponseVO.class);

            // 虚拟币账号地址-脱敏
            if (null == vo.getDataDesensitization() || vo.getDataDesensitization()) {
                bo.setVirtualCurrencyAddress(SymbolUtil.showBankOrVirtualNo(bo.getVirtualCurrencyAddress()));
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
                GetByUserAccountVO currentBindingUser = userInfoApi.getByUserAccountAndSiteCode( bo.getCurrentBindingUserAccount(),bo.getSiteCode());
                bo.setUserName(currentBindingUser.getUserName());
            } else {
                bo.setUserName("—");
            }

            return bo;
        }).toList();

        return new Page<VirtualCurrencyManageResponseVO>(vo.getPageNumber(), vo.getPageSize(), pageList.getTotal()).setRecords(list);
    }

    private void queryWrapperOrderBy(VirtualCurrencyManagePageVO vo, LambdaQueryWrapper<VirtualCurrencyManagePO> queryWrapper) {
        if (StrUtil.isNotEmpty(vo.getOrderField())
                && StrUtil.isNotEmpty(vo.getOrderType())) {
            String orderField = vo.getOrderField();
            String orderType = vo.getOrderType();

            if ("firstUseTime".equals(orderField) && CommonConstant.ORDER_BY_ASC.equalsIgnoreCase(orderType)) {
                // 虚拟币账号新增时间 升序排列
                queryWrapper.orderByAsc(VirtualCurrencyManagePO::getFirstUseTime);
            } else if ("firstUseTime".equals(orderField) && CommonConstant.ORDER_BY_DESC.equalsIgnoreCase(orderType)) {
                // 虚拟币账号新增时间 降序排列
                queryWrapper.orderByDesc(VirtualCurrencyManagePO::getFirstUseTime);
            } else if ("lastWithdrawTime".equals(orderField) && CommonConstant.ORDER_BY_ASC.equalsIgnoreCase(orderType)) {
                // 最近提款时间 升序排列
                queryWrapper.orderByAsc(VirtualCurrencyManagePO::getLastWithdrawTime);
            } else if ("lastWithdrawTime".equals(orderField) && CommonConstant.ORDER_BY_DESC.equalsIgnoreCase(orderType)) {
                // 最近提款时间 降序排列
                queryWrapper.orderByDesc(VirtualCurrencyManagePO::getLastWithdrawTime);
            } else if ("updatedTime".equals(orderField) && CommonConstant.ORDER_BY_ASC.equalsIgnoreCase(orderType)) {
                // 最近操作时间 升序排列
                queryWrapper.orderByAsc(VirtualCurrencyManagePO::getUpdatedTime);
            } else if ("updatedTime".equals(orderField) && CommonConstant.ORDER_BY_DESC.equalsIgnoreCase(orderType)) {
                // 最近操作时间 降序排列
                queryWrapper.orderByDesc(VirtualCurrencyManagePO::getUpdatedTime);
            } else {
                // 默认：虚拟币账号新增时间(firstUseTime) 降序排列
                queryWrapper.orderByDesc(VirtualCurrencyManagePO::getFirstUseTime);
            }
        } else {
            // 默认：虚拟币账号新增时间(firstUseTime) 降序排列
            queryWrapper.orderByDesc(VirtualCurrencyManagePO::getFirstUseTime);
        }
    }

}
