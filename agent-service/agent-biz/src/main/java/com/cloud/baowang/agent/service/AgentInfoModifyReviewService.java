package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.api.AgentCommissionPlanApi;
import com.cloud.baowang.agent.api.enums.AgentAttributionEnum;
import com.cloud.baowang.agent.api.enums.AgentStatusEnum;
import com.cloud.baowang.agent.api.enums.AgentUserBenefitEnum;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentListTreeVO;
import com.cloud.baowang.agent.api.vo.agentreview.UserAccountUpdateVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.*;
import com.cloud.baowang.agent.api.vo.agentreview.list.GetAllListVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPlanVO;
import com.cloud.baowang.agent.po.*;
import com.cloud.baowang.agent.repositories.AgentInfoModifyReviewRepository;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.agent.service.commission.AgentCommissionPlanService;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.common.auth.util.AgentAuthUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.agent.api.enums.AgentInfoChangeTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentSwitchEnum;
import com.cloud.baowang.agent.api.enums.AgentTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.SymbolUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.RiskCtrlEditApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.risk.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author aomiao
 * 代理信息变更审核相关
 */
@Slf4j
@AllArgsConstructor
@RestController
public class AgentInfoModifyReviewService extends ServiceImpl<AgentInfoModifyReviewRepository, AgentInfoModifyReviewPO> {

    private final AgentInfoChangeRecordService agentInfoChangeRecordService;
    private final RiskApi riskApi;
    private final RiskCtrlEditApi riskEditApi;
    private final AgentInfoService agentInfoService;
    private final AgentInfoRepository agentInfoRepository;
    private final SystemParamApi systemParamApi;
    private final PromotionDomainService promotionDomainService;
    private final AgentRemarkRecordService agentRemarkRecordService;
    private final AgentLabelService agentLabelService;
    private final AgentCommissionPlanApi commissionPlanApi;
    private final AgentCommissionPlanService agentCommissionPlanService;
    /**
     * 发起修改代理信息变更
     *
     * @param vo 申请实体
     */
    @Transactional(rollbackFor = Exception.class)
    public void initiateAgentInfoChange(AgentInfoEditVO vo) {
        // 校验代理账号是否存在
        LambdaQueryWrapper<AgentInfoPO> infoQuery = Wrappers.lambdaQuery();
        infoQuery.eq(AgentInfoPO::getAgentAccount, vo.getAgentAccount()).eq(AgentInfoPO::getSiteCode, vo.getSiteCode());
        AgentInfoPO agentInfo = agentInfoService.getOne(infoQuery);
        if (agentInfo == null) {
            throw new BaowangDefaultException(ResultCode.AGENT_NOT_EXISTS);
        }

        // 账号备注无需审核
        if (AgentInfoChangeTypeEnum.ACCOUNT_REMARK.getCode().equals(vo.getChangeType())) {
            // 直接修改
            syncAgentRemark(agentInfo, vo);
            return;
        }

        if (AgentInfoChangeTypeEnum.MEMBER_BENEFITS.getCode().equals(vo.getChangeType())) {

            // 查询是否有子代
            if(agentInfo.getLevel() != CommonConstant.business_one){
                throw new BaowangDefaultException(ResultCode.AGENT_CORRECTION_ERROR);
            }
        }

        //已存在待审核申请，不能再发起相同类型的申请
        long count = this.count(new LambdaQueryWrapper<AgentInfoModifyReviewPO>()
                .eq(AgentInfoModifyReviewPO::getSiteCode, vo.getSiteCode())
                .eq(AgentInfoModifyReviewPO::getAgentAccount, vo.getAgentAccount())
                .eq(AgentInfoModifyReviewPO::getReviewApplicationType, vo.getChangeType())
                .and(wrapper -> wrapper.eq(AgentInfoModifyReviewPO::getReviewStatus, ReviewStatusEnum.REVIEW_PROGRESS.getCode())
                        .or()
                        .eq(AgentInfoModifyReviewPO::getReviewStatus, ReviewStatusEnum.REVIEW_PENDING.getCode())
                ));
        if (count > 0) {
            throw new BaowangDefaultException(ResultCode.AGENT_EXISTS_TO_REVIEWED);
        }


        AgentInfoModifyReviewPO po = new AgentInfoModifyReviewPO();
        // 校验业务，设置申请参数
        checkAgentInfoChange(vo, po, agentInfo);
        // 插表
        long time = System.currentTimeMillis();
        po.setCreatedTime(time);
        po.setUpdatedTime(time);
        po.setCreator(vo.getOperator());
        po.setUpdater(vo.getOperator());
        po.setSiteCode(vo.getSiteCode());
        // 默认为未锁单
        po.setLockStatus(LockStatusEnum.UNLOCK.getCode());
        // 审核单号
        po.setReviewOrderNumber(AgentServerUtil.getAgentOrderNo());
        // 审核申请类型
        po.setReviewApplicationType(vo.getChangeType());
        // 会员账号
        po.setAgentAccount(vo.getAgentAccount());
        // 账号类型
        po.setAgentType(agentInfo.getAgentType());
        // 申请人
        po.setApplicant(vo.getOperator());
        //申请时间
        po.setApplicationTime(time);
        // 申请信息  存入提交备注
        po.setApplicationInformation(vo.getRemark());
        // 审核状态 处理中
        po.setReviewStatus(ReviewStatusEnum.REVIEW_PENDING.getCode());
        // 审核操作 一审审核
        po.setReviewOperation(ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
        // 同步更新到会员账号修改审核表
        this.save(po);
    }


    /**
     * 校验参数,设置申请内容 变更前后
     *
     * @param vo        当前发起的变更参数
     * @param po        申请记录
     * @param agentInfo 变更前代理信息
     */
    private void checkAgentInfoChange(AgentInfoEditVO vo, AgentInfoModifyReviewPO po, AgentInfoPO agentInfo) {
        AgentInfoChangeTypeEnum changeTypeEnum = AgentInfoChangeTypeEnum.of(vo.getChangeType());
        if (null == changeTypeEnum) {
            throw new BaowangDefaultException(ResultCode.AGENT_CHANGE_TYPE_ERROR);
        }
        switch (changeTypeEnum) {
            //账号状态
            case ACCOUNT_STATUS -> {
                if (vo.getAccountStatus() == null) {
                    throw new BaowangDefaultException(ResultCode.AGENT_ACCOUNT_STATUS_EMPTY);
                }
                //变更前后一样,直接打回
                if (agentInfo.getStatus().equals(vo.getAccountStatus())) {
                    throw new BaowangDefaultException(ResultCode.AGENT_MSG_NOT_CHANGE);
                }
                po.setBeforeFixing(agentInfo.getStatus());
                po.setAfterModification(vo.getAccountStatus());
            }
            //风控层级
            case RISK_LEVEL -> {
                // 待审核校验
                if (StringUtils.isBlank(vo.getRiskLevel())) {
                    throw new BaowangDefaultException(ResultCode.AGENT_RISK_LEVEL_EMPTY);
                }
                String riskLevelId = agentInfo.getRiskLevelId();
                //变更前后一样,打回
                if (StringUtils.isNotBlank(riskLevelId) && riskLevelId.equals(vo.getRiskLevel())) {
                    throw new BaowangDefaultException(ResultCode.AGENT_MSG_NOT_CHANGE);
                }
                po.setBeforeFixing(ObjectUtil.isEmpty(agentInfo.getRiskLevelId()) ? null : agentInfo.getRiskLevelId());
                po.setAfterModification(vo.getRiskLevel());
            }
            //代理标签
            case AGENT_LABEL -> {
                if (StringUtils.isBlank(vo.getAgentLabel())) {
                    throw new BaowangDefaultException(ResultCode.AGENT_LABEL_EMPTY);
                }
                String agentLabelId = agentInfo.getAgentLabelId();
                if (StringUtils.isNotBlank(agentLabelId) && agentLabelId.equals(vo.getAgentLabel())) {
                    throw new BaowangDefaultException(ResultCode.AGENT_MSG_NOT_CHANGE);
                }
                po.setBeforeFixing(ObjectUtil.isEmpty(agentInfo.getAgentLabelId()) ? null : agentInfo.getAgentLabelId());
                po.setAfterModification(vo.getAgentLabel());
            }

            //代理归属
            case AGENT_BELONGING -> {
                //变更前代理归属
                Integer beforeAgentAttr = agentInfo.getAgentAttribution();
                if (beforeAgentAttr != null) {
                    po.setBeforeFixing(String.valueOf(beforeAgentAttr));
                }
                Integer afterAgentAttr = vo.getAgentAttribution();
                if (afterAgentAttr == null) {
                    throw new BaowangDefaultException(ResultCode.AGENT_AGENT_ATTRIBUTION_EMPTY);
                }
                po.setAfterModification(String.valueOf(afterAgentAttr));
            }
            //佣金方案
            case COMMISSION_PLAN -> {
                if (agentInfo.getLevel() > 1) {
                    throw new BaowangDefaultException(ResultCode.ONLY_MAIN_AGENT_MODIFY_PLAN);
                }
                String beforePlanCode = agentInfo.getPlanCode();
                String afterPlanCode = vo.getPlanCode();
                if (StringUtils.isBlank(afterPlanCode)) {
                    throw new BaowangDefaultException(ResultCode.AGENT_PLAN_CODE_EMPTY);
                }
                if (StringUtils.isNotBlank(beforePlanCode) && beforePlanCode.equals(afterPlanCode)) {
                    throw new BaowangDefaultException(ResultCode.AGENT_MSG_NOT_CHANGE);
                }
                po.setBeforeFixing(beforePlanCode);
                po.setAfterModification(afterPlanCode);
            }
            //会员福利
            case MEMBER_BENEFITS -> {
                String beforeBene = agentInfo.getUserBenefit();
                po.setBeforeFixing(beforeBene);
                String afterBene = vo.getUserBenefit();
                if (StringUtils.isBlank(afterBene)) {
                    throw new BaowangDefaultException(ResultCode.AGENT_USER_BENEFIT_EMPTY);
                }
                if (StringUtils.isNotBlank(beforeBene) && beforeBene.equals(afterBene)) {
                    throw new BaowangDefaultException(ResultCode.AGENT_MSG_NOT_CHANGE);
                }

                // beforeBene 可能的值是 1,2,3,4,5,6,7,8,9,10,11,12
                // afterBene 可能的值是 1,2,3,4,5,6,7,8,9,10,11,12
                // 判断beforeBene 之间的值差异如果包括可 5,6,7,8,9,10,11,12 任何一个，给出校验
                Boolean flag = hasOverlapWithRange(beforeBene, afterBene);
                if (flag) {
                    // 判断会员级别，如果代理级别是1.才能修改返回的配置
                    if (agentInfo.getLevel() != 1) {
                        throw new BaowangDefaultException(ResultCode.AGENT_CORRECTION_ERROR);
                    }
                }
                po.setAfterModification(afterBene);

            }
            //入口权限
            case ENTRANCE_PERM -> {
                // 待审核校验
                if (ObjectUtil.isEmpty(vo.getEntrancePerm())) {
                    throw new BaowangDefaultException(ResultCode.AGENT_ENTRANCE_PERM_EMPTY);
                }
                // 非主代理才需校验
                if (!CommonConstant.business_one.equals(agentInfo.getLevel())) {
                    AgentInfoPO parentInfo = agentInfoService.getById(agentInfo.getParentId());
                    if (ObjectUtil.isEmpty(parentInfo)) {
                        throw new BaowangDefaultException(ResultCode.AGENT_SUPER_AGENT_EMPTY_ERROR);
                    }

                }
                po.setBeforeFixing(ObjectUtil.isEmpty(agentInfo.getEntrancePerm()) ? null : agentInfo.getEntrancePerm().toString());
                po.setAfterModification(String.valueOf(vo.getEntrancePerm()));
            }
            //支付密码重置
            case PAYMENT_PASSWORD_RESET -> {
                po.setBeforeFixing(agentInfo.getPayPassword());
                po.setAfterModification("");
            }
            //邮箱重置
            case EMAIL -> {
                po.setBeforeFixing(agentInfo.getEmail());
                po.setAfterModification("");
            }
            //推广信息
            case ADS_PROMOTION -> {

                String beforeEdit="Fb像素 PixId:".concat(agentInfo.getFbPixId()).concat(",")
                        .concat("Fb像素 Token:").concat(agentInfo.getFbToken()).concat(",")
                        .concat("Google Ads PixId:").concat(agentInfo.getGooglePixId()).concat(",")
                        .concat("Google Ads Token:").concat(agentInfo.getGoogleToken());

                String afterEdit="Fb像素 PixId:".concat(vo.getFbPixId()).concat(",")
                        .concat("Fb像素 Token:").concat(vo.getFbToken()).concat(",")
                        .concat("Google Ads PixId:").concat(vo.getGooglePixId()).concat(",")
                        .concat("Google Ads Token:").concat(vo.getGoogleToken());

                po.setBeforeFixing(beforeEdit);
                po.setAfterModification(afterEdit);
                JSONObject afterModificationJson=new JSONObject();
                afterModificationJson.put("fbPixId",vo.getFbPixId());
                afterModificationJson.put("fbToken",vo.getFbToken());
                afterModificationJson.put("googlePixId",vo.getGooglePixId());
                afterModificationJson.put("googleToken",vo.getGoogleToken());
                po.setAfterModificationJson(afterModificationJson.toString());

            }
            default -> throw new BaowangDefaultException(ResultCode.AGENT_CHANGE_TYPE_ERROR);
        }
    }

    /**
     * 判断 beforeBene 与 afterBene 的差集中是否包含 5~12 范围内的任意一个值。
     *
     * <p>
     * 输入参数为两个用英文逗号分隔的字符串，例如："1,2,3"。
     * 方法会解析字符串为整数集合，计算 beforeBene 中有而 afterBene 中没有的值（差集），
     * 并判断这个差集中是否包含 5~12 范围内的任意一个数字。
     * </p>
     *
     * @param beforeBene 字符串形式的 before 值集合（例如 "1,2,3"）
     * @param afterBene  字符串形式的 after 值集合（例如 "2,3,4"）
     * @return 如果差集中包含 5~12 的任意值，则返回 true；否则返回 false
     */
    public static boolean hasOverlapWithRange(String beforeBene, String afterBene) {
        // 将 beforeBene 字符串转成 Set<Integer>
        Set<Integer> beforeSet = Arrays.stream(beforeBene.split(","))
                .map(String::trim)                 // 去除空格
                .filter(s -> !s.isEmpty())         // 过滤空字符串
                .map(Integer::parseInt)            // 转换为整数
                .collect(Collectors.toSet());      // 收集成 Set

        // 将 afterBene 字符串转成 Set<Integer>
        Set<Integer> afterSet = Arrays.stream(afterBene.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        // 计算差集：beforeSet 中有而 afterSet 中没有的值
        Set<Integer> diff = new HashSet<>(beforeSet);
        diff.removeAll(afterSet);

        // 构造目标范围集合 [5, 12]
        Set<Integer> targetRange = new HashSet<>();
        for (int i = 5; i <= 12; i++) {
            targetRange.add(i);
        }

        // 判断差集中是否包含目标范围内的值
        for (Integer num : diff) {
            if (targetRange.contains(num)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 修改备注信息无需审核直接修改
     *
     * @param agentInfo 代理信息
     * @param vo        备注信息
     */
    private void syncAgentRemark(AgentInfoPO agentInfo, AgentInfoEditVO vo) {
        //修改代理备注信息
        AgentInfoPO infoPO = new AgentInfoPO();
        infoPO.setId(agentInfo.getId());
        infoPO.setUpdatedTime(System.currentTimeMillis());
        infoPO.setRemark(vo.getAccountRemark());
        agentInfoService.updateById(infoPO);

        // 修改备注
        AgentRemarkRecordPO recordPO = new AgentRemarkRecordPO();
        recordPO.setSiteCode(agentInfo.getSiteCode());
        recordPO.setCreatedTime(System.currentTimeMillis());
        recordPO.setUpdatedTime(System.currentTimeMillis());
        recordPO.setRemark(vo.getAccountRemark());
        recordPO.setOperator(vo.getOperator());
        recordPO.setAgentAccount(vo.getAgentAccount());
        recordPO.setStatus(EnableStatusEnum.ENABLE.getCode());
        agentRemarkRecordService.save(recordPO);

        // 新增变更记录
        AgentInfoChangeRecordPO changeRecordPO = new AgentInfoChangeRecordPO();
        changeRecordPO.setSiteCode(agentInfo.getSiteCode());
        changeRecordPO.setAgentType(agentInfo.getAgentType());
        changeRecordPO.setAgentAccount(agentInfo.getAgentAccount());
        changeRecordPO.setChangeType(vo.getChangeType());
        changeRecordPO.setOperatorTime(System.currentTimeMillis());
        changeRecordPO.setOperator(vo.getOperator());
        changeRecordPO.setChangeBefore(agentInfo.getRemark());
        changeRecordPO.setChangeAfter(vo.getAccountRemark());
        changeRecordPO.setStatus(EnableStatusEnum.ENABLE.getCode());
        agentInfoChangeRecordService.save(changeRecordPO);
    }


    @Transactional(rollbackFor = Exception.class)
    public void lock(AgentInfoModifyReviewLockVO vo) {
        // 获取参数
        String id = vo.getId();
        AgentInfoModifyReviewPO reviewPO = this.getById(id);
        if (null == reviewPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        //锁单人不能是申请人
        if (reviewPO.getApplicant().equals(vo.getOperator())) {
            throw new BaowangDefaultException(ResultCode.WRONG_OPERATION);
        }
        if (reviewPO.getReviewStatus().equals(ReviewStatusEnum.REVIEW_PASS.getCode()) ||
                reviewPO.getReviewStatus().equals(ReviewStatusEnum.REVIEW_REJECTED.getCode())) {
            throw new BaowangDefaultException(ResultCode.AUDITED);
        }

        //当前审核记录处于处理中，并且已锁，锁单人不是自己时，不允许解锁
        if (reviewPO.getReviewStatus().equals(ReviewStatusEnum.REVIEW_PROGRESS.getCode())
                && reviewPO.getLockStatus().equals(LockStatusEnum.LOCK.getCode())
                && !reviewPO.getLocker().equals(vo.getOperator())) {
            throw new BaowangDefaultException(ResultCode.LOCKED);
        }
        boolean res;
        RLock fairLock = RedisUtil.getFairLock(RedisConstants.AGENT_INFO_CHANGE_REVIEW_ORDER_NO + reviewPO.getReviewOrderNumber());
        try {
            res = fairLock.tryLock(0, RedisLockConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (res) {
                // 业务操作
                lockOperate(id, reviewPO.getLockStatus(), vo.getOperator());
            } else {
                throw new BaowangDefaultException(ResultCode.USER_REVIEW_ALREADY_LOCK_ERROR);
            }
        } catch (Exception e) {
            log.error("代理信息修改审核-锁单/解锁 error,审核单号:{},操作人:{}", reviewPO.getReviewOrderNumber(), vo.getOperator(), e);
            throw new BaowangDefaultException(ResultCode.USER_REVIEW_LOCK_ERROR);
        } finally {
            if (fairLock.isLocked() && fairLock.isHeldByCurrentThread()) {
                fairLock.unlock();
            }
        }
    }

    /**
     * 锁单/解锁操作
     *
     * @param id         申请主键
     * @param lockStatus 当前申请锁单状态
     * @param operator   操作人
     */
    private void lockOperate(String id, Integer lockStatus, String operator) {
        Integer myLockStatus;
        Integer myReviewStatus;
        // 锁单人
        String locker;
        //当前未锁状态，设置为已锁,申请状态变更为处理中
        if (LockStatusEnum.UNLOCK.getCode().equals(lockStatus)) {
            // 锁单
            myLockStatus = LockStatusEnum.LOCK.getCode();
            myReviewStatus = ReviewStatusEnum.REVIEW_PROGRESS.getCode();
            locker = operator;
        } else {
            //当前已锁状态，设置为未锁.申请状态为待审核
            myLockStatus = LockStatusEnum.UNLOCK.getCode();
            myReviewStatus = ReviewStatusEnum.REVIEW_PENDING.getCode();
            locker = null;
        }
        new LambdaUpdateChainWrapper<>(baseMapper)
                .eq(AgentInfoModifyReviewPO::getId, id)
                .set(AgentInfoModifyReviewPO::getLockStatus, myLockStatus)
                .set(AgentInfoModifyReviewPO::getLocker, locker)
                .set(AgentInfoModifyReviewPO::getReviewStatus, myReviewStatus)
                .set(AgentInfoModifyReviewPO::getUpdater, operator)
                .set(AgentInfoModifyReviewPO::getUpdatedTime, System.currentTimeMillis())
                .update();
    }

    /**
     * 审核操作
     *
     * @param vo 审核实体
     */
    @Transactional(rollbackFor = Exception.class)
    public void review(AgentInfoModifyReviewVO vo) {
        String operator = vo.getOperator();
        String siteCode = vo.getSiteCode();

        AgentInfoModifyReviewPO reviewPO = this.getById(vo.getId());
        if (ObjectUtil.isEmpty(reviewPO)) {
            throw new BaowangDefaultException(ResultCode.AGENT_NOT_EXISTS);
        }
        if (reviewPO.getApplicant().equals(operator)) {
            throw new BaowangDefaultException(ResultCode.WRONG_OPERATION);
        }
        //未处于处理中申请不能审核
        if (!ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(reviewPO.getReviewStatus())) {
            throw new BaowangDefaultException(ResultCode.REVIEW_STATUS_ERROR);
        }
        //驳回
        if (ReviewStatusEnum.REVIEW_REJECTED.getCode().equals(vo.getStatus())) {
            new LambdaUpdateChainWrapper<>(baseMapper)
                    .eq(AgentInfoModifyReviewPO::getId, vo.getId())
                    .set(AgentInfoModifyReviewPO::getFirstReviewTime, System.currentTimeMillis())
                    .set(AgentInfoModifyReviewPO::getFirstInstance, vo.getOperator())
                    .set(AgentInfoModifyReviewPO::getReviewRemark, vo.getRemark())
                    .set(AgentInfoModifyReviewPO::getReviewOperation, ReviewOperationEnum.CHECK.getCode())
                    .set(AgentInfoModifyReviewPO::getReviewStatus, ReviewStatusEnum.REVIEW_REJECTED.getCode())
                    .set(AgentInfoModifyReviewPO::getLockStatus, LockStatusEnum.UNLOCK.getCode())
                    .set(AgentInfoModifyReviewPO::getLocker, null)
                    .set(AgentInfoModifyReviewPO::getUpdater, vo.getOperator())
                    .set(AgentInfoModifyReviewPO::getUpdatedTime, System.currentTimeMillis())
                    .update();
        } else {
            // 通过
            //判断当前申请变更类型
            Integer typeCode = reviewPO.getReviewApplicationType();
            AgentInfoChangeTypeEnum changeTypeEnum = AgentInfoChangeTypeEnum.of(typeCode);
            if (changeTypeEnum == null) {
                throw new BaowangDefaultException(ResultCode.AGENT_CHANGE_TYPE_ERROR);
            }
            //修改代理表
            LambdaUpdateWrapper<AgentInfoPO> updateWrapper = new LambdaUpdateWrapper<AgentInfoPO>()
                    .eq(AgentInfoPO::getAgentAccount, reviewPO.getAgentAccount())
                    .eq(AgentInfoPO::getSiteCode, siteCode)
                    .set(AgentInfoPO::getUpdatedTime, System.currentTimeMillis())
                    .set(AgentInfoPO::getUpdater, vo.getOperator());

            switch (changeTypeEnum) {
                //账号状态变更
                case ACCOUNT_STATUS -> {
                    updateWrapper.set(AgentInfoPO::getStatus, reviewPO.getAfterModification());
                    // 审核通过后， 如果是登录锁定， 清除登录的token
                    if (reviewPO.getAfterModification().equals(AgentStatusEnum.LOGIN_LOCK.getCode())) {
                        AgentInfoPO agentInfoPO = agentInfoService.getAgentInfoPO(siteCode, reviewPO.getAgentAccount());
                        if (Objects.nonNull(agentInfoPO)) {
                            String token = RedisUtil.getValue(AgentAuthUtil.getJwtKey(siteCode, agentInfoPO.getAgentId()));
                            log.info("登录锁定删除token;agentId={},siteCode={},token={}", agentInfoPO.getAgentId(), siteCode, token);
                            if (StringUtils.isNotEmpty(token)) {
                                String tokenMd5 = AgentAuthUtil.getTokenMd5(token);
                                RedisUtil.deleteKey(AgentAuthUtil.getTokenKey(siteCode, tokenMd5));
                                RedisUtil.deleteKey(AgentAuthUtil.getJwtKey(siteCode, agentInfoPO.getAgentId()));
                            }
                        }
                    }
                }
                //风控层级
                case RISK_LEVEL -> {
                    updateWrapper.set(AgentInfoPO::getRiskLevelId, reviewPO.getAfterModification());
                    RiskLevelDownReqVO riskLevelDownReqVO = new RiskLevelDownReqVO();
                    riskLevelDownReqVO.setSiteCode(vo.getSiteCode());
                    riskLevelDownReqVO.setRiskControlType(RiskTypeEnum.RISK_AGENT.getCode());
                    //获取风控层级
                    ResponseVO<List<RiskLevelResVO>> riskLevelList = riskApi.getRiskLevelList(riskLevelDownReqVO);
                    RiskAccountQueryVO riskAccountQueryVO = new RiskAccountQueryVO();
                    riskAccountQueryVO.setRiskControlAccount(reviewPO.getAgentAccount());
                    riskAccountQueryVO.setRiskControlTypeCode(RiskTypeEnum.RISK_AGENT.getCode());
                    riskAccountQueryVO.setSiteCode(vo.getSiteCode());
                    //查询当前会员是否存在风控会员账号记录
                    RiskAccountVO riskAccountByAccount = riskApi.getRiskAccountByAccount(riskAccountQueryVO);

                    if (ObjectUtil.isNotEmpty(riskAccountByAccount) && StringUtils.isNotBlank(riskAccountByAccount.getId())) {
                        RiskAccountVO riskAccountVO = new RiskAccountVO();
                        riskAccountVO.setId(riskAccountByAccount.getId());
                        for (RiskLevelResVO datum : riskLevelList.getData()) {
                            if (ObjectUtil.isNotEmpty(reviewPO.getAfterModification()) &&
                                    datum.getRiskControlLevel().equals(reviewPO.getAfterModification())) {
                                riskAccountVO.setRiskControlLevel(datum.getRiskControlLevel());
                            }
                        }
                        riskAccountVO.setRiskDesc(reviewPO.getApplicationInformation());
                        riskAccountVO.setUpdater(operator);
                        riskAccountVO.setUpdatedTime(System.currentTimeMillis());
                        riskApi.updateRiskListAccount(riskAccountVO);
                    } else {
                        RiskAccountVO riskAccountVO = new RiskAccountVO();
                        riskAccountVO.setRiskControlAccount(reviewPO.getAgentAccount());
                        riskAccountVO.setRiskControlType(RiskTypeEnum.RISK_AGENT.getCode());
                        riskAccountVO.setRiskControlTypeCode(RiskTypeEnum.RISK_AGENT.getCode());
                        riskAccountVO.setRiskControlLevelId(reviewPO.getAfterModification());
                        for (RiskLevelResVO datum : riskLevelList.getData()) {
                            if (ObjectUtil.isNotEmpty(reviewPO.getAfterModification()) &&
                                    datum.getId().equals(reviewPO.getAfterModification())) {
                                riskAccountVO.setRiskControlLevel(datum.getRiskControlLevel());
                            }
                        }
                        riskAccountVO.setRiskDesc(reviewPO.getApplicationInformation());
                        riskAccountVO.setCreator(operator);
                        riskAccountVO.setCreatedTime(System.currentTimeMillis());
                        riskAccountVO.setSiteCode(vo.getSiteCode());
                        riskApi.saveRiskListAccount(riskAccountVO);
                    }

                }
                //代理标签
                case AGENT_LABEL -> updateWrapper.set(AgentInfoPO::getAgentLabelId, reviewPO.getAfterModification());
                //代理归属
                case AGENT_BELONGING -> {
                    // 如果变更类型是"代理归属"，那么需要同步更新 所有下级代理的代理归属
                    updateWrapper.set(AgentInfoPO::getAgentAttribution, reviewPO.getAfterModification());
                    AgentInfoVO modifyAgentAccount = agentInfoService.getByCurrAgentAccount(reviewPO.getAgentAccount());
                    if (null == modifyAgentAccount) {
                        throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
                    }
                    List<AgentListTreeVO> agentListTreeVOS = ConvertUtil.entityListToModelList(agentInfoRepository.findAllAgentInfoById(modifyAgentAccount.getId()), AgentListTreeVO.class);
                    List<String> allAgentId = agentListTreeVOS.stream().map(AgentListTreeVO::getAgentId).toList();
                    // 同步更新
                    agentInfoService.updateAgentAttributionByAgentIds(allAgentId, reviewPO.getAfterModification());
                }

                //佣金方案
                case COMMISSION_PLAN -> {
                    updateWrapper.set(AgentInfoPO::getPlanCode, reviewPO.getAfterModification());
                    updateWrapper.set(AgentInfoPO::getCurrentPlanCode, reviewPO.getAfterModification());
                    //修改下级的佣金方案
                    AgentInfoVO superAccount = agentInfoService.getByAgentAccountAndSite(reviewPO.getAgentAccount(), siteCode);
                    List<String> agentIds = agentInfoService.getSubAgentIdList(superAccount.getAgentId());
                    if (agentIds != null && agentIds.size() > 1) {
                        agentIds = agentIds.stream().filter(a -> !a.equals(superAccount.getAgentId())).toList();
                        LambdaUpdateWrapper<AgentInfoPO> subUpdateWrapper = new LambdaUpdateWrapper<AgentInfoPO>()
                                .in(AgentInfoPO::getAgentId, agentIds)
                                .eq(AgentInfoPO::getSiteCode, siteCode)
                                .set(AgentInfoPO::getUpdatedTime, System.currentTimeMillis())
                                .set(AgentInfoPO::getUpdater, vo.getOperator())
                                .set(AgentInfoPO::getPlanCode, reviewPO.getAfterModification())
                                .set(AgentInfoPO::getCurrentPlanCode, reviewPO.getAfterModification());
                        agentInfoService.update(subUpdateWrapper);
                    }
                }
                //会员福利
                case MEMBER_BENEFITS -> {
                    updateWrapper.set(AgentInfoPO::getUserBenefit, reviewPO.getAfterModification());
                    // 修改子代的会员福利
                    AgentInfoVO byAgentAccountSite = agentInfoService.getByAgentAccountSite(siteCode, reviewPO.getAgentAccount());
                    List<GetAllListVO> allDownAgentById = agentInfoService.findAllDownAgentById(byAgentAccountSite.getAgentId());
                    agentInfoService.updateUserBenefit(allDownAgentById, reviewPO.getAfterModification());
                }
                //入口权限开启
                case ENTRANCE_PERM -> updateWrapper.set(AgentInfoPO::getEntrancePerm, reviewPO.getAfterModification());
                //支付密码重置
                case PAYMENT_PASSWORD_RESET -> updateWrapper.set(AgentInfoPO::getPayPassword, null);
                //重置邮箱
                case EMAIL -> updateWrapper.set(AgentInfoPO::getEmail, null);
                //推广信息
                case ADS_PROMOTION -> {
                    JSONObject adsPromotionJson= JSON.parseObject(reviewPO.getAfterModificationJson());
                    updateWrapper.set(AgentInfoPO::getFbPixId, adsPromotionJson.getString("fbPixId"));
                    updateWrapper.set(AgentInfoPO::getFbToken, adsPromotionJson.getString("fbToken"));
                    updateWrapper.set(AgentInfoPO::getGooglePixId, adsPromotionJson.getString("googlePixId"));
                    updateWrapper.set(AgentInfoPO::getGoogleToken, adsPromotionJson.getString("googleToken"));
                }

                default -> throw new BaowangDefaultException(ResultCode.AGENT_CHANGE_TYPE_ERROR);
            }
            agentInfoService.update(updateWrapper);

            // 修改审核表
            new LambdaUpdateChainWrapper<>(baseMapper)
                    .eq(AgentInfoModifyReviewPO::getId, vo.getId())
                    .set(AgentInfoModifyReviewPO::getFirstReviewTime, System.currentTimeMillis())
                    .set(AgentInfoModifyReviewPO::getFirstInstance, vo.getOperator())
                    .set(AgentInfoModifyReviewPO::getReviewRemark, vo.getRemark())
                    //结单查看
                    .set(AgentInfoModifyReviewPO::getReviewOperation, ReviewOperationEnum.CHECK.getCode())
                    //审核通过
                    .set(AgentInfoModifyReviewPO::getReviewStatus, ReviewStatusEnum.REVIEW_PASS.getCode())
                    //锁单状态还原
                    .set(AgentInfoModifyReviewPO::getLockStatus, LockStatusEnum.UNLOCK.getCode())
                    .set(AgentInfoModifyReviewPO::getLocker, null)
                    .set(AgentInfoModifyReviewPO::getUpdater, vo.getOperator())
                    .set(AgentInfoModifyReviewPO::getUpdatedTime, System.currentTimeMillis())
                    .update();
            // 新增代理信息修改记录表
            AgentInfoChangeRecordPO changeRecordPO = new AgentInfoChangeRecordPO();
            changeRecordPO.setAgentAccount(reviewPO.getAgentAccount());
            changeRecordPO.setAgentType(reviewPO.getAgentType());
            changeRecordPO.setChangeType(reviewPO.getReviewApplicationType());
            changeRecordPO.setChangeBefore(reviewPO.getBeforeFixing());
            changeRecordPO.setChangeAfter(reviewPO.getAfterModification());
            changeRecordPO.setRemark(vo.getRemark());
            changeRecordPO.setOperator(vo.getOperator());
            changeRecordPO.setOperatorTime(System.currentTimeMillis());
            changeRecordPO.setCreatedTime(System.currentTimeMillis());
            changeRecordPO.setUpdatedTime(System.currentTimeMillis());
            changeRecordPO.setCreator(vo.getOperator());
            changeRecordPO.setUpdater(vo.getOperator());
            changeRecordPO.setSiteCode(siteCode);
            agentInfoChangeRecordService.save(changeRecordPO);

        }
    }

    public Page<AgentInfoModifyReviewPageVO> pageList(AgentInfoModifyReviewPageQueryVO vo) {
        String operator = vo.getOperator();
        String siteCode = vo.getSiteCode();
        Page<AgentInfoModifyReviewPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<AgentInfoModifyReviewPO> query = Wrappers.lambdaQuery();
        query.eq(AgentInfoModifyReviewPO::getSiteCode, siteCode);
        //申请时间
        Long applyStartTime = vo.getApplyStartTime();
        if (applyStartTime != null) {
            query.ge(AgentInfoModifyReviewPO::getApplicationTime, applyStartTime);
        }
        Long applyEndTime = vo.getApplyEndTime();
        if (applyEndTime != null) {
            query.le(AgentInfoModifyReviewPO::getApplicationTime, applyEndTime);
        }
        //审核完成时间
        Long firstFinishStartTime = vo.getFirstFinishStartTime();
        if (firstFinishStartTime != null) {
            query.ge(AgentInfoModifyReviewPO::getFirstReviewTime, firstFinishStartTime);
        }
        Long firstFinishEndTime = vo.getFirstFinishEndTime();
        if (firstFinishEndTime != null) {
            query.le(AgentInfoModifyReviewPO::getFirstReviewTime, firstFinishEndTime);
        }

        String agentAccount = vo.getAgentAccount();
        if (StringUtils.isNotBlank(agentAccount)) {
            query.eq(AgentInfoModifyReviewPO::getAgentAccount, agentAccount);
        }

        Integer agentType = vo.getAgentType();
        if (agentType != null) {
            query.eq(AgentInfoModifyReviewPO::getAgentType, agentType);
        }
        Integer reviewOperation = vo.getReviewOperation();
        if (reviewOperation != null) {
            query.eq(AgentInfoModifyReviewPO::getReviewOperation, reviewOperation);
        }

        Integer reviewStatus = vo.getReviewStatus();
        if (reviewStatus != null) {
            query.eq(AgentInfoModifyReviewPO::getReviewStatus, reviewStatus);
        }
        String applicant = vo.getApplicant();
        if (StringUtils.isNotBlank(applicant)) {
            query.eq(AgentInfoModifyReviewPO::getApplicant, applicant);
        }
        String firstInstance = vo.getFirstInstance();
        if (StringUtils.isNotBlank(firstInstance)) {
            query.eq(AgentInfoModifyReviewPO::getFirstInstance, firstInstance);
        }
        Integer lockStatus = vo.getLockStatus();
        if (lockStatus != null) {
            query.eq(AgentInfoModifyReviewPO::getLockStatus, lockStatus);
        }
        String agentReviewOrderNo = vo.getAgentReviewOrderNo();
        if (StringUtils.isNotBlank(agentReviewOrderNo)) {
            query.eq(AgentInfoModifyReviewPO::getReviewOrderNumber, agentReviewOrderNo);
        }
        query.orderByAsc(AgentInfoModifyReviewPO::getReviewOperation);
        query.orderByAsc(AgentInfoModifyReviewPO::getLockStatus);
        query.orderByAsc(AgentInfoModifyReviewPO::getApplicationTime);
        page = this.page(page, query);
        List<AgentInfoModifyReviewPO> records = page.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            // 风空层级查询
            Map<String, RiskLevelDetailsVO> riskLevelMap = getRiskLevelDetailsVOMap(records);
            //佣金方案
            List<String> planCodes = records.stream().filter(s -> s.getReviewApplicationType().equals(AgentInfoChangeTypeEnum.COMMISSION_PLAN.getCode())).flatMap(po -> {
                String afterModification = po.getAfterModification();
                String beforeFixing = po.getBeforeFixing();
                return Lists.newArrayList(afterModification, beforeFixing).stream();
            }).toList();
            Map<String, String> planCodeNameMap = new HashMap<>();
            if (CollectionUtil.isNotEmpty(planCodes)) {
                List<AgentCommissionPlanVO> planVOS = commissionPlanApi.getPlanBySiteAndCodes(siteCode, planCodes);
                planCodeNameMap = planVOS.stream()
                        .collect(Collectors.toMap(AgentCommissionPlanVO::getPlanCode, AgentCommissionPlanVO::getPlanName));

            }
            // 代理标签
            Map<String, String> agentLabelMap = getAgentLabelMap(records);
            Map<String, String> finalPlanCodeNameMap = planCodeNameMap;
            return ConvertUtil.toConverPage(page.convert(item -> {
                AgentInfoModifyReviewPageVO pageVO = BeanUtil.copyProperties(item, AgentInfoModifyReviewPageVO.class);
                //按变更类型替换变更前后文本
                Pair<String, String> pair = replaceChangeTextByChangeType(pageVO.getReviewApplicationType(), pageVO.getBeforeFixing(), pageVO.getAfterModification(), riskLevelMap, agentLabelMap, finalPlanCodeNameMap, vo.getDataDesensitization());
                pageVO.setBeforeFixing(pair.getKey());
                pageVO.setAfterModification(pair.getValue());
                // 判断登录人是否申请人
                pageVO.setIsApplicant(item.getApplicant().equals(operator) ? Integer.parseInt(YesOrNoEnum.YES.getCode()) : Integer.parseInt(YesOrNoEnum.NO.getCode()));
                // 判断登录人是否锁单人
                pageVO.setIsLoginLocker(StringUtils.isNotBlank(pageVO.getLocker()) && pageVO.getLocker().equals(operator) ? Integer.parseInt(YesOrNoEnum.YES.getCode()) : Integer.parseInt(YesOrNoEnum.NO.getCode()));

                return pageVO;
            }));
        }
        return ConvertUtil.toConverPage(page.convert(item -> BeanUtil.copyProperties(item, AgentInfoModifyReviewPageVO.class)));

    }

    private Map<String, String> getAgentLabelMap(List<AgentInfoModifyReviewPO> records) {
        List<String> labelIds = records.stream().filter(s -> s.getReviewApplicationType().equals(AgentInfoChangeTypeEnum.AGENT_LABEL.getCode())).flatMap(po -> {
            String afterModification = po.getAfterModification();
            String beforeFixing = po.getBeforeFixing();
            ArrayList<String> labelList = Lists.newArrayList();
            if (StringUtils.isNotBlank(afterModification)) {
                labelList.addAll(Arrays.asList(afterModification.split(CommonConstant.COMMA)));
            }
            if (StringUtils.isNotBlank(beforeFixing)) {
                labelList.addAll(Arrays.asList(beforeFixing.split(CommonConstant.COMMA)));
            }
            return labelList.stream();
        }).toList();
        Map<String, String> agentLabelMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(labelIds)) {
            agentLabelMap = agentLabelService.listByIds(labelIds).stream().collect(Collectors.toMap(AgentLabelPO::getId, AgentLabelPO::getName));
        }
        return agentLabelMap;
    }

    private Map<String, RiskLevelDetailsVO> getRiskLevelDetailsVOMap(List<AgentInfoModifyReviewPO> records) {
        List<String> riskIds = records.stream().filter(s -> s.getReviewApplicationType().equals(AgentInfoChangeTypeEnum.RISK_LEVEL.getCode())).flatMap(po -> {
            String afterModification = po.getAfterModification();
            String beforeFixing = po.getBeforeFixing();
            return Lists.newArrayList(afterModification, beforeFixing).stream();
        }).toList();
        Map<String, RiskLevelDetailsVO> riskLevelMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(riskIds)) {
            riskLevelMap = riskApi.getByIds(riskIds);
        }
        return riskLevelMap;
    }

    public AgentInfoModifyReviewDetailVO detail(AgentInfoModifyReviewDetailQueryVO vo) {
        String adminName = CurrReqUtils.getAccount();
        AgentInfoModifyReviewPO reviewPO = this.getById(vo.getId());
        if (ObjectUtil.isEmpty(reviewPO)) {
            throw new BaowangDefaultException(ResultCode.AGENT_NOT_EXISTS);
        }
        AgentInfoVO agentInfoVO = agentInfoService.getByCurrAgentAccount(reviewPO.getAgentAccount());
        AgentInfoModifyReviewDetailVO detailVO = new AgentInfoModifyReviewDetailVO();
        BeanUtils.copyProperties(reviewPO, detailVO);
        BeanUtils.copyProperties(agentInfoVO, detailVO);

        // 风控层级
        List<String> riskIds = new ArrayList<>();
        if (detailVO.getReviewApplicationType().equals(AgentInfoChangeTypeEnum.RISK_LEVEL.getCode())) {
            riskIds = Lists.newArrayList(detailVO.getBeforeFixing(), detailVO.getAfterModification());
        }
        if (ObjectUtil.isNotEmpty(detailVO.getRiskLevelId())) {
            riskIds.add(String.valueOf(detailVO.getRiskLevelId()));
        }
        Map<String, RiskLevelDetailsVO> riskLevelMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(riskIds)) {
            riskLevelMap = riskApi.getByIds(riskIds);
        }
        // 代理标签
        List<String> agentLabelIds = Lists.newArrayList();
        if (detailVO.getReviewApplicationType().equals(AgentInfoChangeTypeEnum.AGENT_LABEL.getCode())) {
            if (StringUtils.isNotBlank(detailVO.getBeforeFixing())) {
                String before = detailVO.getBeforeFixing();
                agentLabelIds.addAll(Arrays.asList(before.split(CommonConstant.COMMA)));
            }
            if (StringUtils.isNotBlank(detailVO.getAfterModification())) {
                String after = detailVO.getAfterModification();
                agentLabelIds.addAll(Arrays.asList(after.split(CommonConstant.COMMA)));
            }
        }
        //佣金方案
        List<String> planCodes = Lists.newArrayList();
        if (detailVO.getReviewApplicationType().equals(AgentInfoChangeTypeEnum.COMMISSION_PLAN.getCode())) {
            if (StringUtils.isNotBlank(detailVO.getBeforeFixing())) {
                String before = detailVO.getBeforeFixing();
                planCodes.add(before);
            }
            if (StringUtils.isNotBlank(detailVO.getAfterModification())) {
                String after = detailVO.getAfterModification();
                planCodes.add(after);
            }
        }

        if (ObjectUtil.isNotEmpty(detailVO.getAgentLabelId())) {
            agentLabelIds.addAll(Arrays.asList(detailVO.getAgentLabelId().split(CommonConstant.COMMA)));
        }
        Map<String, String> agentLabelMap = Maps.newHashMap();
        if (CollectionUtil.isNotEmpty(agentLabelIds)) {
            agentLabelMap = agentLabelService.listByIds(agentLabelIds).stream().collect(Collectors.toMap(AgentLabelPO::getId, AgentLabelPO::getName));
        }
        if (ObjectUtil.isNotEmpty(detailVO.getRiskLevelId())) {
            detailVO.setRiskLevelText(Optional.ofNullable(riskLevelMap.get(detailVO.getRiskLevelId())).map(RiskLevelDetailsVO::getRiskControlLevel).orElse(null));
        }
        if (ObjectUtil.isNotEmpty(detailVO.getAgentLabelId())) {
            List<String> nameList = agentLabelService.listByIds(Arrays.stream(detailVO.getAgentLabelId().split(",")).toList()).stream().map(AgentLabelPO::getName).toList();
            detailVO.setAgentLabelText(String.join(",", nameList));
        }

        Map<String, String> planCodeMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(planCodes)) {
            List<AgentCommissionPlanVO> planBySiteAndCodes = commissionPlanApi.getPlanBySiteAndCodes(vo.getSiteCode(), planCodes);
            planCodeMap = planBySiteAndCodes.stream()
                    .collect(Collectors.toMap(AgentCommissionPlanVO::getPlanCode, AgentCommissionPlanVO::getPlanName));
        }
        //按变更类型替换变更前后文本
        Pair<String, String> pair = replaceChangeTextByChangeType(detailVO.getReviewApplicationType(), detailVO.getBeforeFixing(), detailVO.getAfterModification(), riskLevelMap, agentLabelMap, planCodeMap, vo.getDataDesensitization());
        detailVO.setBeforeFixing(pair.getKey());
        detailVO.setAfterModification(pair.getValue());
        // 判断锁单人
        int isLocker = Integer.parseInt(YesOrNoEnum.NO.getCode());
        if (StringUtils.isNotBlank(detailVO.getLocker()) && detailVO.getLocker().equals(adminName)) {
            isLocker = Integer.parseInt(YesOrNoEnum.YES.getCode());
        }
        detailVO.setIsLocker(isLocker);
        return detailVO;
    }

    /**
     * 根据申请类型，替换文本
     *
     * @param changeType        申请类型
     * @param beforeFixing      变更前
     * @param afterModification 变更后
     * @param finalRiskLevelMap 风控map
     * @param agentLabelMap     代理标签map
     * @param planCodeNameMap   佣金方案map
     * @param
     * @return 组装后的文本内容
     */
    private Pair<String, String> replaceChangeTextByChangeType(Integer changeType, String beforeFixing,
                                                               String afterModification,
                                                               Map<String, RiskLevelDetailsVO> finalRiskLevelMap,
                                                               Map<String, String> agentLabelMap,
                                                               Map<String, String> planCodeNameMap,
                                                               Boolean dataDesensitization) {
        dataDesensitization=dataDesensitization==null?Boolean.TRUE:Boolean.FALSE;
        String beforeStr = beforeFixing;
        String afterStr = afterModification;
        AgentInfoChangeTypeEnum changeTypeEnum = AgentInfoChangeTypeEnum.of(changeType);
        if (changeTypeEnum == null) {
            throw new BaowangDefaultException(ResultCode.AGENT_CHANGE_TYPE_ERROR);
        }
        switch (changeTypeEnum) {
            //账号状态
            case ACCOUNT_STATUS -> {
                if (StringUtils.isNotBlank(beforeFixing)) {
                    beforeStr = replaceAgentStatusText(beforeFixing);
                }
                if (StringUtils.isNotBlank(afterModification)) {
                    afterStr = replaceAgentStatusText(afterModification);
                }
            }
            //风控层级
            case RISK_LEVEL -> {
                if (StringUtils.isNotBlank(beforeFixing)) {
                    RiskLevelDetailsVO riskLevelDetailsVO = finalRiskLevelMap.get(beforeFixing);
                    if (ObjectUtil.isNotEmpty(riskLevelDetailsVO)) {
                        beforeStr = riskLevelDetailsVO.getRiskControlLevel();
                    }
                }
                if (StringUtils.isNotBlank(afterModification)) {
                    RiskLevelDetailsVO riskLevelDetailsVO = finalRiskLevelMap.get(afterModification);
                    if (ObjectUtil.isNotEmpty(riskLevelDetailsVO)) {
                        afterStr = riskLevelDetailsVO.getRiskControlLevel();
                    }
                }
            }
            // 代理标签
            case AGENT_LABEL -> {

                if (StringUtils.isNotBlank(beforeFixing)) {
                    String[] split = beforeFixing.split(CommonConstant.COMMA);
                    StringBuilder beforeStrBuilder = new StringBuilder();

                    for (String s : split) {
                        String value = agentLabelMap.get(s);
                        if (value != null) {
                            beforeStrBuilder.append(value).append(",");
                        }
                    }

                    if (!beforeStrBuilder.isEmpty()) {
                        beforeStrBuilder.setLength(beforeStrBuilder.length() - 1);
                    }
                    beforeStr = beforeStrBuilder.toString();
                }
                if (StringUtils.isNotBlank(afterModification)) {
                    String[] split = afterModification.split(CommonConstant.COMMA);
                    StringBuilder afterStrBuilder = new StringBuilder();
                    for (String s : split) {
                        String value = agentLabelMap.get(s);
                        if (value != null) {
                            afterStrBuilder.append(value).append(",");
                        }
                    }
                    if (!afterStrBuilder.isEmpty()) {
                        afterStrBuilder.setLength(afterStrBuilder.length() - 1);
                    }
                    afterStr = afterStrBuilder.toString();
                }
            }
            //代理归属
            case AGENT_BELONGING -> {
                if (StringUtils.isNotBlank(beforeFixing)) {
                    beforeStr = AgentAttributionEnum.nameOfCode(Integer.parseInt(beforeFixing)).getName();
                }
                if (StringUtils.isNotBlank(afterModification)) {
                    afterStr = AgentAttributionEnum.nameOfCode(Integer.parseInt(afterModification)).getName();
                }
            }
            //佣金方案
            case COMMISSION_PLAN -> {
                beforeStr = planCodeNameMap.get(beforeFixing);
                afterStr = planCodeNameMap.get(afterModification);
            }
            //会员福利
            case MEMBER_BENEFITS -> {
                beforeStr = replaceAgentBene(beforeFixing);
                afterStr = replaceAgentBene(afterModification);
            }
            //入口权限开启
            // 支付密码重置
            case PAYMENT_PASSWORD_RESET -> {
                beforeStr = "********";
                afterStr = "-";
            }
            //重置邮箱
            case EMAIL -> {
                beforeStr = beforeFixing;
                if (dataDesensitization) {
                    beforeStr = SymbolUtil.showEmail(beforeFixing);
                }
                afterStr = "-";
            }
        }
        return new Pair<>(beforeStr, afterStr);
    }

    private String replaceAgentStatusText(String text) {
        String[] split = text.split(CommonConstant.COMMA);
        List<String> list = new ArrayList<>();
        for (String s : split) {
            list.add(AgentStatusEnum.nameOfCode(s).getName());
        }
        return String.join(CommonConstant.COMMA, list);
    }

    private String replaceAgentBene(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        return Arrays.stream(text.split(CommonConstant.COMMA))
                .map(code -> AgentUserBenefitEnum.nameOfCode(Integer.parseInt(code)).getName()) // 获取对应的 name
                .collect(Collectors.joining(CommonConstant.COMMA));
    }

    public Page<AgentInfoChangeRecordPageVO> recordPageList(AgentInfoChangeRecordQueryVO vo) {
        LambdaQueryWrapper<AgentInfoChangeRecordPO> queryWrapper = new LambdaQueryWrapper<AgentInfoChangeRecordPO>()
                .ge(ObjectUtil.isNotEmpty(vo.getOperatorStartTime()), AgentInfoChangeRecordPO::getOperatorTime, vo.getOperatorStartTime())
                .le(ObjectUtil.isNotEmpty(vo.getOperatorEndTime()), AgentInfoChangeRecordPO::getOperatorTime, vo.getOperatorEndTime())
                .eq(StringUtils.isNotBlank(vo.getAgentAccount()), AgentInfoChangeRecordPO::getAgentAccount, vo.getAgentAccount())
                .eq(ObjectUtil.isNotEmpty(vo.getAgentType()), AgentInfoChangeRecordPO::getAgentType, vo.getAgentType())
                .eq(StringUtils.isNotBlank(vo.getChangeType()), AgentInfoChangeRecordPO::getChangeType, vo.getChangeType())
                .eq(StringUtils.isNotBlank(vo.getOperator()), AgentInfoChangeRecordPO::getOperator, vo.getOperator())
                .eq(AgentInfoChangeRecordPO::getSiteCode, CurrReqUtils.getSiteCode())
                .orderBy(true, StringUtils.isNotBlank(vo.getOrderType()) && vo.getOrderType().equals(CommonConstant.ORDER_BY_ASC), AgentInfoChangeRecordPO::getOperatorTime);

        Page<AgentInfoChangeRecordPO> page = agentInfoChangeRecordService.page(new Page<>(vo.getPageNumber(), vo.getPageSize()), queryWrapper);
        List<AgentInfoChangeRecordPO> records = page.getRecords();
        Page<AgentInfoChangeRecordPageVO> pageResult = new Page<>();
        if (CollectionUtil.isEmpty(records)) {
            return pageResult;
        }
        // 风控层级
        List<String> riskIds = records.stream().filter(s -> s.getChangeType().equals(AgentInfoChangeTypeEnum.RISK_LEVEL.getCode())).flatMap(po -> {
            String afterModification = po.getChangeAfter();
            String beforeFixing = po.getChangeBefore();
            return Lists.newArrayList(afterModification, beforeFixing).stream();
        }).toList();
        Map<String, RiskLevelDetailsVO> riskLevelMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(riskIds)) {
            riskLevelMap = riskApi.getByIds(riskIds);
        }

        // 佣金方案
        List<String> planCodes = records.stream().filter(s -> s.getChangeType().equals(AgentInfoChangeTypeEnum.COMMISSION_PLAN.getCode())).flatMap(po -> {
            String afterModification = po.getChangeAfter();
            String beforeFixing = po.getChangeBefore();
            return Lists.newArrayList(afterModification, beforeFixing).stream();
        }).toList();
        Map<String, String> planCodesMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(planCodes)) {
            List<AgentCommissionPlanVO> planBySiteAndCodes = commissionPlanApi.getPlanBySiteAndCodes(vo.getSiteCode(), planCodes);
            planCodesMap = planBySiteAndCodes.stream()
                    .collect(Collectors.toMap(AgentCommissionPlanVO::getPlanCode, AgentCommissionPlanVO::getPlanName));
        }
        // 代理标签
        List<String> labelIds = records.stream().filter(s -> s.getChangeType().equals(AgentInfoChangeTypeEnum.AGENT_LABEL.getCode())).flatMap(po -> {
            String afterModification = po.getChangeAfter();
            String beforeFixing = po.getChangeBefore();
            ArrayList<String> labelList = Lists.newArrayList();


            if (StringUtils.isNotBlank(afterModification)) {
                List<String> list = Arrays.asList(afterModification.split(CommonConstant.COMMA));
                labelList.addAll(list);
            }
            if (StringUtils.isNotBlank(beforeFixing)) {
                List<String> list = Arrays.asList(beforeFixing.split(CommonConstant.COMMA));
                labelList.addAll(list);
            }
            return labelList.stream();
        }).toList();
        Map<String, String> agentLabelMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(labelIds)) {
            agentLabelMap = agentLabelService.listByIds(labelIds).stream().collect(Collectors.toMap(AgentLabelPO::getId, AgentLabelPO::getName));
        }

        List<AgentInfoChangeRecordPageVO> pageVOList = new ArrayList<>();
        Map<String, RiskLevelDetailsVO> finalRiskLevelMap = riskLevelMap;
        Map<String, String> finalAgentLabelMap = agentLabelMap;
        Map<String, String> finalPlanCodesMap = planCodesMap;
        records.forEach(
                record -> {
                    AgentInfoChangeRecordPageVO pageVO = new AgentInfoChangeRecordPageVO();
                    BeanUtils.copyProperties(record, pageVO);
                    //按变更类型替换变更前后文本
                    Pair<String, String> pair = replaceChangeTextByChangeType(pageVO.getChangeType(), pageVO.getChangeBefore(), pageVO.getChangeAfter(), finalRiskLevelMap, finalAgentLabelMap, finalPlanCodesMap, vo.getDataDesensitization());
                    pageVO.setChangeBefore(pair.getKey());
                    pageVO.setChangeAfter(pair.getValue());
                    pageVO.setAgentTypeText(AgentTypeEnum.nameOfCode(pageVO.getAgentType()).getName());
                    pageVO.setChangeTypeText(AgentInfoChangeTypeEnum.of(pageVO.getChangeType()).getName());
                    pageVOList.add(pageVO);
                }
        );
        BeanUtils.copyProperties(page, pageResult);
        pageResult.setRecords(pageVOList);
        return pageResult;
    }


    public UserAccountUpdateVO findProcessingDataCount(String siteCode) {
        UserAccountUpdateVO vo = new UserAccountUpdateVO();
        Long count = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(AgentInfoModifyReviewPO::getSiteCode, siteCode)
                .in(AgentInfoModifyReviewPO::getReviewStatus, List.of(ReviewStatusEnum.REVIEW_PENDING.getCode(), ReviewStatusEnum.REVIEW_PROGRESS.getCode()))
                .count();
        vo.setCountType("1");
        vo.setNum(count);
        vo.setRouter("/Agent/AgentReview/AgentAccountChangeReview");
        return vo;
    }

    public Map<String, List<CodeValueVO>> getDownBox(String siteCode) {
        Map<String, List<CodeValueVO>> map = systemParamApi.getSystemParamsByList(Lists.newArrayList(
                //锁单状态
                CommonConstant.USER_REVIEW_LOCK_STATUS,
                //审核状态
                CommonConstant.USER_REVIEW_REVIEW_STATUS,
                //审核操作
                CommonConstant.USER_REVIEW_REVIEW_OPERATION,
                //账号类型
                CommonConstant.AGENT_TYPE,
                //变更类型
                CommonConstant.AGENT_CHANGE_TYPE,
                CommonConstant.AGENT_ATTRIBUTION,
                CommonConstant.AGENT_STATUS,
                CommonConstant.REGISTER_WAY,
                CommonConstant.AGENT_USER_BENEFIT,
                CommonConstant.AGENT_LEVEL_LOOKUP,
                CommonConstant.AGENT_CATEGORY
        )).getData();
        //锁单状态
        List<CodeValueVO> lockStatus = map.get(CommonConstant.USER_REVIEW_LOCK_STATUS);

        List<CodeValueVO> registerWay = map.get(CommonConstant.REGISTER_WAY);

        List<CodeValueVO> agentUserBenefit = map.get(CommonConstant.AGENT_USER_BENEFIT);

        List<CodeValueVO> agentCategory = map.get(CommonConstant.AGENT_CATEGORY);

        // 审核状态
        List<CodeValueVO> agentReviewStatus = map.get(CommonConstant.USER_REVIEW_REVIEW_STATUS);
        // 审核操作
        List<CodeValueVO> agentReviewOperation = map.get(CommonConstant.USER_REVIEW_REVIEW_OPERATION);
        // 账号类型
        List<CodeValueVO> typeEnums = map.get(CommonConstant.AGENT_TYPE);


        // 强制编辑契约生效
        List<CodeValueVO> switchEnums = AgentSwitchEnum.getList().stream().map(s -> CodeValueVO.builder()
                .code(s.getCode())
                .value(s.getName()).build()).toList();
        // 变更类型
        List<CodeValueVO> changeTypes = map.get(CommonConstant.AGENT_CHANGE_TYPE);
        // 代理状态
        List<CodeValueVO> agentStatus = map.get(CommonConstant.AGENT_STATUS);
        //代理归属
        List<CodeValueVO> agentAttribution = map.get(CommonConstant.AGENT_ATTRIBUTION);

        //代理归属
        List<CodeValueVO> agentLevels = map.get(CommonConstant.AGENT_LEVEL_LOOKUP);

        //佣金方案下拉框
        List<CodeValueNoI18VO> commissionPlanSelect = agentCommissionPlanService.getCommissionPlanSelect(siteCode);
        List<CodeValueVO> commissionPlanList = ConvertUtil.entityListToModelList(commissionPlanSelect, CodeValueVO.class);
        Map<String, List<CodeValueVO>> result = Maps.newHashMap();
        result.put("lockStatus", lockStatus);
        result.put("agentReviewOperation", agentReviewOperation);
        result.put("agentReviewStatus", agentReviewStatus);
        result.put("switchList", switchEnums);
        result.put("agentTypes", typeEnums);
        result.put("changeTypes", changeTypes);
        result.put("agentStatus", agentStatus);
        result.put("agentAttribution", agentAttribution);
        result.put("registerWay", registerWay);
        result.put("agentUserBenefit", agentUserBenefit);
        result.put("commissionPlan", commissionPlanList);
        result.put("agentCategory", agentCategory);
        result.put("agentLevels", agentLevels);

        return result;
    }

    public long getAgentInfoReviewRecord(String siteCode) {
        long count = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(AgentInfoModifyReviewPO::getSiteCode, siteCode)
                .in(AgentInfoModifyReviewPO::getReviewStatus, List.of(ReviewStatusEnum.REVIEW_PENDING.getCode(), ReviewStatusEnum.REVIEW_PROGRESS.getCode()))
                .count();
        return count;
    }
}
