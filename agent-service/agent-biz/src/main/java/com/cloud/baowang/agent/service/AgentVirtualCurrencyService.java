package com.cloud.baowang.agent.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.EnableOrDisableVO;
import com.cloud.baowang.agent.api.vo.UnbindVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentUpdateWithdrawByAddressVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyAddVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyPageRequestVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyPageVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyResVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyResponseVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentVirtualCurrencyPO;
import com.cloud.baowang.agent.repositories.AgentVirtualCurrencyRepository;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.SymbolUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 代理虚拟币地址信息 服务实现类
 * </p>
 *
 * @author qiqi
 * @since 2023-10-11
 */
@Service
@AllArgsConstructor
public class AgentVirtualCurrencyService extends ServiceImpl<AgentVirtualCurrencyRepository, AgentVirtualCurrencyPO> {

    private final AgentInfoService agentInfoService;

    private final SystemParamApi systemParamApi;

    private final RiskApi riskApi;


    public Page<AgentVirtualCurrencyResVO> listAgentVirtualCurrency(AgentVirtualCurrencyPageVO vo) {
        Page<AgentVirtualCurrencyPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<AgentVirtualCurrencyPO> lqw = new LambdaQueryWrapper<>();
        lqw.ge(null != vo.getUseStartTime(),AgentVirtualCurrencyPO::getLastWithdrawTime,vo.getUseStartTime());
        lqw.lt(null != vo.getUseEndTime(),AgentVirtualCurrencyPO::getLastWithdrawTime,vo.getUseEndTime());
        lqw.eq(StringUtils.isNotBlank(vo.getAgentAccount()),AgentVirtualCurrencyPO::getCurrentBindingAgentAccount,vo.getAgentAccount());
        lqw.eq(StringUtils.isNotBlank(vo.getAgentType()),AgentVirtualCurrencyPO::getAgentType,vo.getAgentType());
        lqw.eq(StringUtils.isNotBlank(vo.getRiskControlLevelId()),AgentVirtualCurrencyPO::getRiskControlLevelId,vo.getRiskControlLevelId());
        lqw.eq(StringUtils.isNotBlank(vo.getVirtualCurrencyAddress()),AgentVirtualCurrencyPO::getVirtualCurrencyAddress,vo.getVirtualCurrencyAddress());
        lqw.eq(StringUtils.isNotBlank(vo.getVirtualCurrencyType()),AgentVirtualCurrencyPO::getVirtualCurrencyType,vo.getVirtualCurrencyType());
        lqw.eq(StringUtils.isNotBlank(vo.getVirtualCurrencyProtocol()),AgentVirtualCurrencyPO::getVirtualCurrencyProtocol,vo.getVirtualCurrencyProtocol());
        if (StringUtils.isNotBlank(vo.getOrderField()) && StringUtils.isNotBlank(vo.getOrderType())) {
            if ("lastWithdrawTime".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), AgentVirtualCurrencyPO::getLastWithdrawTime);
            }
        }else{
            lqw.orderByDesc(AgentVirtualCurrencyPO::getLastWithdrawTime);
        }
        Page<AgentVirtualCurrencyPO>  agentVirtualCurrencyPOPage = this.baseMapper.selectPage(page,lqw);
        Page<AgentVirtualCurrencyResVO> agentVirtualCurrencyResVOPage = new Page<>();
        BeanUtils.copyProperties(agentVirtualCurrencyPOPage,agentVirtualCurrencyResVOPage);
        List<AgentVirtualCurrencyResVO> agentVirtualCurrencyResVOList = agentVirtualCurrencyPOPage.getRecords().stream().map(record -> {
            AgentVirtualCurrencyResVO agentVirtualCurrencyResVO = ConvertUtil.entityToModel(record, AgentVirtualCurrencyResVO.class);
            /*if(StringUtils.isNotBlank(record.getAgentType())){
                agentVirtualCurrencyResVO.setAgentName(AgentTypeEnum.nameOfCode(Integer.parseInt(record.getAgentType())).getName());
            }*/
            return agentVirtualCurrencyResVO;
        }).toList();
        agentVirtualCurrencyResVOPage.setRecords(agentVirtualCurrencyResVOList);
        return agentVirtualCurrencyResVOPage;
    }

    public Integer virtualCurrencyAdd(AgentVirtualCurrencyAddVO agentVirtualCurrencyAddVO) {
        LambdaQueryWrapper<AgentVirtualCurrencyPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentVirtualCurrencyPO::getCurrentBindingAgentId,agentVirtualCurrencyAddVO.getAgentId());


        List<AgentVirtualCurrencyPO> virtualCurrencyList = this.baseMapper.selectList(lqw);
        if (virtualCurrencyList.size() >= 10) {
            throw new BaowangDefaultException(ResultCode.AGENT_BINDING_NUMBER_LIMIT);
        }
        AgentInfoPO agentInfoPO = agentInfoService.getOne(new LambdaQueryWrapper<AgentInfoPO>().eq(AgentInfoPO::getAgentAccount,agentVirtualCurrencyAddVO.getAgentAccount()));

        String salt = agentInfoPO.getSalt();
        // 密码加密
        String encryptPassword = AgentServerUtil.getEncryptPassword(agentVirtualCurrencyAddVO.getLoginPassword(), salt);
        if (!encryptPassword.equals(agentInfoPO.getAgentPassword())) {
            throw new BaowangDefaultException(ResultCode.AGENT_LOGIN_PASSWROD_ERROR);
        }

        // 风险虚拟币
        RiskAccountVO riskVirtualCurrency = riskApi.getRiskAccountByAccount(
                new RiskAccountQueryVO(agentVirtualCurrencyAddVO.getVirtualCurrencyAddress(), CommonConstant.business_four.toString(),agentInfoPO.getSiteCode()));
        AgentVirtualCurrencyPO one = this
                .lambdaQuery()
                .eq(AgentVirtualCurrencyPO::getVirtualCurrencyAddress, agentVirtualCurrencyAddVO.getVirtualCurrencyAddress())
                .one();
         if (null != one) {
            if (CommonConstant.business_zero.equals(one.getBlackStatus())) {
                throw new BaowangDefaultException(ResultCode.VIRTUAL_CURRENCY_BLACK_STATUS_NOT);
            }
            if (CommonConstant.business_one.equals(one.getBindingStatus())) {
                if (agentVirtualCurrencyAddVO.getAgentId().equals(one.getCurrentBindingAgentId())) {
                    throw new BaowangDefaultException(ResultCode.VIRTUAL_CURRENCY_ALREADY_BIND);
                } else {
                    throw new BaowangDefaultException(ResultCode.VIRTUAL_CURRENCY_ALREADY_BIND_NOT);
                }
            } else {
                // 0未绑定
                // 解绑后的账户不能再次绑定 -- 新增需求
                throw new BaowangDefaultException(ResultCode.ALREADY_UNBIND_CAN_NOT_BIND);
            }
        }
        AgentVirtualCurrencyPO po = new AgentVirtualCurrencyPO();
        po.setVirtualCurrencyAddress(agentVirtualCurrencyAddVO.getVirtualCurrencyAddress());
        po.setVirtualCurrencyAddressAlias(agentVirtualCurrencyAddVO.getVirtualCurrencyAddressAlias());
        po.setVirtualCurrencyType(CurrencyEnum.USD.getCode());
        po.setVirtualCurrencyProtocol(agentVirtualCurrencyAddVO.getVirtualCurrencyProtocol());
        po.setBlackStatus(CommonConstant.business_one);
        po.setBindingStatus(CommonConstant.business_one);
        if (null != riskVirtualCurrency) {
            po.setRiskControlLevelId(riskVirtualCurrency.getRiskControlLevelId());
        }
        po.setBindingAccountTimes(CommonConstant.business_one);
        po.setCurrentBindingAgentId(agentVirtualCurrencyAddVO.getAgentId());
        po.setCurrentBindingAgentAccount(agentVirtualCurrencyAddVO.getAgentAccount());
        po.setAgentWithdrawSuccessTimes(CommonConstant.business_zero);
        po.setAgentWithdrawFailTimes(CommonConstant.business_zero);
        po.setAgentWithdrawSumAmount(BigDecimal.ZERO);
        po.setFirstUseTime(System.currentTimeMillis());
//        po.setLastOperator(agentVirtualCurrencyAddVO.getAgentAccount());
        po.setCreator(agentVirtualCurrencyAddVO.getAgentId());
        po.setCreatedTime(System.currentTimeMillis());
        /*po.setUpdater(agentVirtualCurrencyAddVO.getAgentId());
        po.setUpdatedTime(System.currentTimeMillis());*/

        return this.baseMapper.insert(po);

    }

    public Integer virtualCurrencyDelete(IdVO idVO) {
        return  this.baseMapper.deleteById(idVO.getId());
    }

    public List<AgentVirtualCurrencyResVO> virtualCurrencyList(String agentAccount){
        LambdaQueryWrapper<AgentVirtualCurrencyPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentVirtualCurrencyPO::getCurrentBindingAgentAccount,agentAccount);
        lqw.orderByDesc(AgentVirtualCurrencyPO::getLastWithdrawTime);
        List<AgentVirtualCurrencyPO> bankCardPOS = this.baseMapper.selectList(lqw);
        List<AgentVirtualCurrencyResVO> agentBankCardResVOList = ConvertUtil.entityListToModelList(bankCardPOS,AgentVirtualCurrencyResVO.class);
        return agentBankCardResVOList;
    }

    public Page<AgentVirtualCurrencyResponseVO> getAgentVirtualCurrencyPage(AgentVirtualCurrencyPageRequestVO vo) {
        LambdaQueryWrapper<AgentVirtualCurrencyPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotEmpty(vo.getVirtualCurrencyAddress()),AgentVirtualCurrencyPO::getVirtualCurrencyAddress, vo.getVirtualCurrencyAddress());
        queryWrapper.eq(StrUtil.isNotEmpty(vo.getVirtualCurrencyType()),AgentVirtualCurrencyPO::getVirtualCurrencyType, vo.getVirtualCurrencyType());
        queryWrapper.eq(StrUtil.isNotEmpty(vo.getVirtualCurrencyProtocol()),AgentVirtualCurrencyPO::getVirtualCurrencyProtocol, vo.getVirtualCurrencyProtocol());
        queryWrapper.eq(null != vo.getBlackStatus(),AgentVirtualCurrencyPO::getBlackStatus, vo.getBlackStatus());
        queryWrapper.eq(null != vo.getBindingStatus(),AgentVirtualCurrencyPO::getBindingStatus, vo.getBindingStatus());
        queryWrapper.eq(StrUtil.isNotEmpty(vo.getLastOperator()),AgentVirtualCurrencyPO::getLastOperator, vo.getLastOperator());
        if (StrUtil.isNotEmpty(vo.getAgentName())) {
            List<String> agentAccounts = agentInfoService.getAgentAccountByName(vo.getAgentName());
            if (CollUtil.isEmpty(agentAccounts)) {
                return new Page<>();
            } else {
                // agentAccounts不为空
                if (StrUtil.isNotEmpty(vo.getCurrentBindingAgentAccount())) {
                    if (agentAccounts.contains(vo.getCurrentBindingAgentAccount())) {
                        queryWrapper.eq(AgentVirtualCurrencyPO::getCurrentBindingAgentAccount, vo.getCurrentBindingAgentAccount());
                    } else {
                        return new Page<>();
                    }
                } else {
                    queryWrapper.in(AgentVirtualCurrencyPO::getCurrentBindingAgentAccount, agentAccounts);
                }
            }
        } else {
            if (StrUtil.isNotEmpty(vo.getCurrentBindingAgentAccount())) {
                queryWrapper.eq(AgentVirtualCurrencyPO::getCurrentBindingAgentAccount, vo.getCurrentBindingAgentAccount());
            }
        }
        queryWrapper.in(CollUtil.isNotEmpty(vo.getRiskControlLevelId()),AgentVirtualCurrencyPO::getRiskControlLevelId, vo.getRiskControlLevelId());
        queryWrapper.ge(null != vo.getAgentWithdrawFailTimesMin(),AgentVirtualCurrencyPO::getAgentWithdrawFailTimes, vo.getAgentWithdrawFailTimesMin());
        queryWrapper.le(null != vo.getAgentWithdrawFailTimesMax(),AgentVirtualCurrencyPO::getAgentWithdrawFailTimes, vo.getAgentWithdrawFailTimesMax());
        queryWrapper.ge(null != vo.getAgentWithdrawSuccessTimesMin(),AgentVirtualCurrencyPO::getAgentWithdrawSuccessTimes, vo.getAgentWithdrawSuccessTimesMin());
        queryWrapper.le(null != vo.getAgentWithdrawSuccessTimesMin(),AgentVirtualCurrencyPO::getAgentWithdrawSuccessTimes, vo.getAgentWithdrawSuccessTimesMax());
        queryWrapper.ge(null != vo.getAgentWithdrawSumAmountMin(),AgentVirtualCurrencyPO::getAgentWithdrawSumAmount, vo.getAgentWithdrawSumAmountMin());
        queryWrapper.le(null != vo.getAgentWithdrawSumAmountMax(),AgentVirtualCurrencyPO::getAgentWithdrawSumAmount, vo.getAgentWithdrawSumAmountMax());
        queryWrapper.eq(null != vo.getBindingAccountTimes(),AgentVirtualCurrencyPO::getBindingAccountTimes, vo.getBindingAccountTimes());
        // 排序
        queryWrapperOrderBy(vo, queryWrapper);
        Page<AgentVirtualCurrencyPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<AgentVirtualCurrencyPO> pageList = this.page(page, queryWrapper);

        // 集中获取配置参数
        List<String> types = Lists.newArrayList();
        types.add(CommonConstant.BLACK_STATUS);
        types.add(CommonConstant.BINDING_STATUS);
        ResponseVO<Map<String, List<CodeValueVO>>> systemParamsMapResp = systemParamApi.getSystemParamsByList(types);
        Map<String, List<CodeValueVO>>  systemParamsMap= systemParamsMapResp.getData();
        List<CodeValueVO> blackStatus = systemParamsMap.get(CommonConstant.BLACK_STATUS);
        List<CodeValueVO> bindingStatus = systemParamsMap.get(CommonConstant.BINDING_STATUS);

        List<AgentVirtualCurrencyResponseVO> list = pageList.getRecords().stream().map(record -> {
            AgentVirtualCurrencyResponseVO bo = ConvertUtil.entityToModel(record, AgentVirtualCurrencyResponseVO.class);

            // 黑名单状态 0禁用 1启用
            if (null != bo.getBlackStatus()) {
                String blackStatusName = blackStatus.stream().filter(item ->
                        item.getCode().equals(bo.getBlackStatus().toString())
                ).toList().get(0).getValue();
                bo.setBlackStatusName(blackStatusName);
            }
            if(vo.getDataDesensitization()){
                bo.setVirtualCurrencyAddress(SymbolUtil.showBankOrVirtualNo(record.getVirtualCurrencyAddress()));
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
                        riskApi.getById(IdVO.builder().id(bo.getRiskControlLevelId()).build());
                String riskControlLevel = null;
                if (null != riskLevelDetailsVO) {
                    riskControlLevel = riskLevelDetailsVO.getRiskControlLevel();
                }
                bo.setRiskControlLevel(riskControlLevel);
            }

            // 代理姓名
            if (StrUtil.isNotEmpty(bo.getCurrentBindingAgentAccount())
                    && !"—".equals(bo.getCurrentBindingAgentAccount())) {
                AgentInfoVO agentInfoVO = agentInfoService.getByAgentAccount(bo.getCurrentBindingAgentAccount());
                bo.setAgentName(agentInfoVO.getName());
            } else {
                bo.setAgentName("—");
            }

            return bo;
        }).toList();

        return new Page<AgentVirtualCurrencyResponseVO>(vo.getPageNumber(), vo.getPageSize(), pageList.getTotal()).setRecords(list);
    }

    private void queryWrapperOrderBy(AgentVirtualCurrencyPageRequestVO vo, LambdaQueryWrapper<AgentVirtualCurrencyPO> queryWrapper) {
        if (StrUtil.isNotEmpty(vo.getOrderField())
                && StrUtil.isNotEmpty(vo.getOrderType())) {
            String orderField = vo.getOrderField();
            String orderType = vo.getOrderType();

            if ("firstUseTime".equals(orderField) && CommonConstant.ORDER_BY_ASC.equalsIgnoreCase(orderType)) {
                // 银行卡新增时间 升序排列
                queryWrapper.orderByAsc(AgentVirtualCurrencyPO::getFirstUseTime);
            } else if ("firstUseTime".equals(orderField) && CommonConstant.ORDER_BY_DESC.equalsIgnoreCase(orderType)) {
                // 银行卡新增时间 降序排列
                queryWrapper.orderByDesc(AgentVirtualCurrencyPO::getFirstUseTime);
            } else if ("lastWithdrawTime".equals(orderField) && CommonConstant.ORDER_BY_ASC.equalsIgnoreCase(orderType)) {
                // 最近提款时间 升序排列
                queryWrapper.orderByAsc(AgentVirtualCurrencyPO::getLastWithdrawTime);
            } else if ("lastWithdrawTime".equals(orderField) && CommonConstant.ORDER_BY_DESC.equalsIgnoreCase(orderType)) {
                // 最近提款时间 降序排列
                queryWrapper.orderByDesc(AgentVirtualCurrencyPO::getLastWithdrawTime);
            } else if ("updatedTime".equals(orderField) && CommonConstant.ORDER_BY_ASC.equalsIgnoreCase(orderType)) {
                // 最近操作时间 升序排列
                queryWrapper.orderByAsc(AgentVirtualCurrencyPO::getUpdatedTime);
            } else if ("updatedTime".equals(orderField) && CommonConstant.ORDER_BY_DESC.equalsIgnoreCase(orderType)) {
                // 最近操作时间 降序排列
                queryWrapper.orderByDesc(AgentVirtualCurrencyPO::getUpdatedTime);
            } else {
                // 默认：银行卡新增时间(firstUseTime) 降序排列
                queryWrapper.orderByDesc(AgentVirtualCurrencyPO::getFirstUseTime);
            }
        } else {
            // 默认：银行卡新增时间(firstUseTime) 降序排列
            queryWrapper.orderByDesc(AgentVirtualCurrencyPO::getFirstUseTime);
        }
    }

    public ResponseVO<?> enableOrDisable(EnableOrDisableVO vo, String adminId, String adminName) {
        AgentVirtualCurrencyPO byId = this.getById(vo.getId());
        if (null == byId) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        if (CommonConstant.business_zero.equals(byId.getBlackStatus())) {
            // 启用
            // 黑名单状态 0禁用 1启用
            Integer myBlackStatus = CommonConstant.business_one;

            LambdaUpdateWrapper<AgentVirtualCurrencyPO> lambdaUpdate = new LambdaUpdateWrapper<>();
            lambdaUpdate.eq(AgentVirtualCurrencyPO::getId, vo.getId())
                    .set(AgentVirtualCurrencyPO::getBlackStatus, myBlackStatus)
                    .set(AgentVirtualCurrencyPO::getLastOperator, adminName)
                    .set(AgentVirtualCurrencyPO::getUpdater, adminId)
                    .set(AgentVirtualCurrencyPO::getUpdatedTime, System.currentTimeMillis());
            this.update(null, lambdaUpdate);

        } else {
            // 禁用
            // 黑名单状态变更为禁用后，将解除银行卡与代理的绑定关系
            disableOperate(vo.getId(), adminId, adminName);

        }

        return ResponseVO.success();
    }


    public ResponseVO<?> unbind(UnbindVO vo, String adminId, String adminName, boolean flag) {
        // 获取参数
        String id = vo.getId();
        String remark = vo.getRemark();
        Integer isChosen = vo.getIsChosen();

        AgentVirtualCurrencyPO byId = this.getById(id);
        if (null == byId) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        if (CommonConstant.business_one.equals(isChosen)) {
            // 1选中，即：做禁用操作(包含解绑)
            disableOperate(id, adminId, adminName);

        } else {
            // 0未选中，只做解绑
            onlyUnbind(id, adminId, adminName);

        }

        return ResponseVO.success();
    }
    private void onlyUnbind(String id, String adminId, String adminName) {
        // 绑定状态 0未绑定 1绑定中
        Integer bindingStatus = CommonConstant.business_zero;
        // 当前绑定代理id
        Long currentBindingUserId = null;
        // 当前绑定代理账号
        String currentBindingUserAccount = "—";

        LambdaUpdateWrapper<AgentVirtualCurrencyPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(AgentVirtualCurrencyPO::getId, id)
                .set(AgentVirtualCurrencyPO::getBindingStatus, bindingStatus)
                .set(AgentVirtualCurrencyPO::getCurrentBindingAgentId, currentBindingUserId)
                .set(AgentVirtualCurrencyPO::getCurrentBindingAgentAccount, currentBindingUserAccount)
                .set(AgentVirtualCurrencyPO::getLastOperator, adminName)
                .set(AgentVirtualCurrencyPO::getUpdater, adminId)
                .set(AgentVirtualCurrencyPO::getUpdatedTime, System.currentTimeMillis());
        this.update(null, lambdaUpdate);
    }

    private void disableOperate(String id, String adminId, String adminName) {
        // 黑名单状态 0禁用 1启用
        Integer myBlackStatus = CommonConstant.business_zero;
        // 绑定状态 0未绑定 1绑定中
        Integer bindingStatus = CommonConstant.business_zero;
        // 当前绑定代理id
        Long currentBindingUserId = null;
        // 当前绑定代理账号
        String currentBindingUserAccount = "—";

        LambdaUpdateWrapper<AgentVirtualCurrencyPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(AgentVirtualCurrencyPO::getId, id)
                .set(AgentVirtualCurrencyPO::getBlackStatus, myBlackStatus)
                .set(AgentVirtualCurrencyPO::getBindingStatus, bindingStatus)
                .set(AgentVirtualCurrencyPO::getCurrentBindingAgentId, currentBindingUserId)
                .set(AgentVirtualCurrencyPO::getCurrentBindingAgentAccount, currentBindingUserAccount)
                .set(AgentVirtualCurrencyPO::getLastOperator, adminName)
                .set(AgentVirtualCurrencyPO::getUpdater, adminId)
                .set(AgentVirtualCurrencyPO::getUpdatedTime, System.currentTimeMillis());
        this.update(null, lambdaUpdate);
    }


    /**
     * 更新 会员提款信息
     */
    public ResponseVO<?> updateWithdrawByAddress(AgentUpdateWithdrawByAddressVO vo) {
        AgentVirtualCurrencyPO one =
                this.lambdaQuery().eq(AgentVirtualCurrencyPO::getVirtualCurrencyAddress, vo.getVirtualCurrencyAddress()).one();
        if (null == one) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        // 黑名单状态 0禁用 1启用
        if (CommonConstant.business_zero.equals(one.getBlackStatus())) {
            return ResponseVO.fail(ResultCode.BLACK_STATUS_INCORRECT);
        }
        // 绑定状态 0未绑定 1绑定中
        if (CommonConstant.business_zero.equals(one.getBindingStatus())) {
            return ResponseVO.fail(ResultCode.BINDING_STATUS_INCORRECT);
        }
        one.setLastWithdrawTime(System.currentTimeMillis());
        one.setLastOperator(vo.getAdminName());
        one.setUpdater(vo.getAdminId());
        one.setUpdatedTime(System.currentTimeMillis());
        if (null != vo.getAgentWithdrawSuccess() && vo.getAgentWithdrawSuccess()) {
            one.setAgentWithdrawSuccessTimes(one.getAgentWithdrawSuccessTimes() + 1);
        }
        if (null != vo.getAgentWithdrawFail() && vo.getAgentWithdrawFail()) {
            one.setAgentWithdrawFailTimes(one.getAgentWithdrawFailTimes() + 1);
        }
        if (null != vo.getAgentWithdrawSumAmount()) {
            one.setAgentWithdrawSumAmount(one.getAgentWithdrawSumAmount().add(vo.getAgentWithdrawSumAmount()));
        }
        this.updateById(one);
        return ResponseVO.success();
    }

    public boolean checkVirtualCurrencyUnique(String virtualCurrencyAddress, Object o) {
        LambdaQueryWrapper<AgentVirtualCurrencyPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentVirtualCurrencyPO::getVirtualCurrencyAddress, virtualCurrencyAddress);
        List<AgentVirtualCurrencyPO> agentVirtualCurrencyPOS = this.baseMapper.selectList(lqw);
        return agentVirtualCurrencyPOS.isEmpty();
    }
}
