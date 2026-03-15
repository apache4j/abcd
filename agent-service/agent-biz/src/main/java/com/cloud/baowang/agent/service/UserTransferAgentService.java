package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.agentreview.UserAccountUpdateVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferAgentApplyVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferAuthReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferDetailResVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferLockReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferModifyInfoVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferReviewPageReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferReviewPageResVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferUserReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferUserRespVO;
import com.cloud.baowang.agent.api.vo.member.ReportUserTransferReqVO;
import com.cloud.baowang.agent.api.vo.member.ReportUserTransferRespVO;
import com.cloud.baowang.agent.api.vo.member.UserTransferAgentApplyInfo;
import com.cloud.baowang.agent.api.vo.member.UserTransferAgentUserDetail;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentLabelPO;
import com.cloud.baowang.agent.po.UserTransferAgentPO;
import com.cloud.baowang.agent.repositories.AgentLabelRepository;
import com.cloud.baowang.agent.repositories.UserTransferAgentRepository;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.LockStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.ReviewOperationEnum;
import com.cloud.baowang.common.core.enums.ReviewStatusEnum;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.agent.api.enums.AgentEntranceEnum;
import com.cloud.baowang.agent.api.enums.AgentTypeEnum;
import com.cloud.baowang.user.api.enums.UserAccountTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AssertUtil;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskListAccountQueryVO;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.vo.user.GetByUserAccountVO;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.websocket.api.enums.WSSubscribeEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 会员转代审核业务相关
 */
@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserTransferAgentService extends ServiceImpl<UserTransferAgentRepository, UserTransferAgentPO> {

    private final UserInfoApi userInfoApi;

    private final AgentInfoService agentInfoService;
    private final UserTransferAgentRepository repository;
    private final RiskApi riskApi;
    private final AgentLabelRepository agentLabelRepository;
    private final SiteUserLabelConfigApi userLabelConfigApi;
    private final VipGradeApi vipGradeApi;
    private final AgentTransferAgentSocketService socketService;

    /**
     * 发起会员转代
     *
     * @param applyVO 包含站点信息，申请内容
     * @return true
     */
    public Boolean apply(MemberTransferAgentApplyVO applyVO, String operator) {
        //改为根据站点code+账号获取会员信息
        GetByUserAccountVO userAccountVO = userInfoApi.getByUserAccountAndSiteCode(applyVO.getUserAccount(), applyVO.getSiteCode());
        if (userAccountVO == null) {
            throw new BaowangDefaultException(ResultCode.USER_NOT_EXIST);
        }

        AgentInfoPO afterAgentInfo = agentInfoService.getOne(Wrappers.<AgentInfoPO>lambdaQuery().eq(AgentInfoPO::getAgentAccount, applyVO.getTransferAgentName()).eq(AgentInfoPO::getSiteCode, applyVO.getSiteCode()));
        if (afterAgentInfo == null) {
            throw new BaowangDefaultException(ResultCode.AGENT_NOT_EXISTS);
        }
        AssertUtil.isTrue(!AgentEntranceEnum.OPEN.getCode().equals(String.valueOf(afterAgentInfo.getEntrancePerm())), "代理入口权限未开启");
        if (StringUtils.isBlank(userAccountVO.getSuperAgentAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_NOT_HAV_AGENT);
        }
        /*AgentInfoPO agent = agentInfoService.getOne(Wrappers.<AgentInfoPO>lambdaQuery().eq(AgentInfoPO::getAgentAccount, applyVO.getUserAccount()).eq(AgentInfoPO::getSiteCode, applyVO.getSiteCode()));
        if (agent != null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }*/
        if (userAccountVO.getSuperAgentAccount().equals(applyVO.getTransferAgentName())) {
            throw new BaowangDefaultException(ResultCode.CURRENT_AGENT_EQ_TRAGENT);
        }

        String accountType = userAccountVO.getAccountType();
        Integer agentType = afterAgentInfo.getAgentType();
        if ((UserAccountTypeEnum.TEST_ACCOUNT.getCode().equals(Integer.parseInt(accountType)) &&
                AgentTypeEnum.FORMAL.getCode().equals(String.valueOf(agentType))) ||
                (UserAccountTypeEnum.FORMAL_ACCOUNT.getCode().equals(Integer.parseInt(accountType)) &&
                        AgentTypeEnum.TEST.getCode().equals(String.valueOf(agentType)))) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_TYPE_NOT_EQ_AGENT);
        }

        //判断申请会员，是否已经提交过申请
        List<Integer> auditStatus = Lists.newArrayList();
        auditStatus.add(ReviewStatusEnum.REVIEW_PENDING.getCode());
        auditStatus.add(ReviewStatusEnum.REVIEW_PROGRESS.getCode());

        List<UserTransferAgentPO> list = super.list(Wrappers.<UserTransferAgentPO>lambdaQuery().eq(UserTransferAgentPO::getUserAccount, applyVO.getUserAccount())
                .in(UserTransferAgentPO::getAuditStatus, auditStatus));
        if (!CollectionUtils.isEmpty(list)) {
            throw new BaowangDefaultException(ResultCode.USER_EXISTS_TO_REVIEWED);
        }
        try {
            String orderNo = AgentServerUtil.createOrderNo();
            String superAgentAccount = userAccountVO.getSuperAgentAccount();
            String superAgentId = userAccountVO.getSuperAgentId();
            UserTransferAgentPO transferAgentPO = UserTransferAgentPO.builder()
                    .userId(userAccountVO.getUserId())
                    //绑定站点
                    .siteCode(applyVO.getSiteCode())
                    .userAccount(userAccountVO.getUserAccount())
                    //会员账号类型
                    .accountType(Integer.parseInt(userAccountVO.getAccountType()))
                    //当前代理id
                    .currentAgentId(superAgentId)
                    //当前代理名称
                    .currentAgentName(superAgentAccount)
                    //变更后代理id
                    .transferAgentId(afterAgentInfo.getAgentId())
                    //变更后代理名称
                    .transferAgentName(afterAgentInfo.getAgentAccount())
                    //备注
                    .applyRemark(applyVO.getApplyRemark())
                    //申请人id
                    .applyId(operator)
                    //申请人名称
                    .applyName(operator)
                    //申请单号
                    .eventId(orderNo)
                    //审核状态，待审核
                    .auditStatus(ReviewStatusEnum.REVIEW_PENDING.getCode())
                    //当前审核操作
                    .auditStep(ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode()).build();
            transferAgentPO.setCreatedTime(System.currentTimeMillis());
            super.save(transferAgentPO);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("发起会员转代申请异常，error:{},申请人:{},", e, operator);
            throw new BaowangDefaultException("会员转代申请异常");
        }
    }

    /**
     * 分页查询会员转代申请列表
     *
     * @param pageReqVO 分页列表查询条件对象
     * @param adminName 当前审核人员名称
     * @return 分页
     */
    public Page<MemberTransferReviewPageResVO> listPage(MemberTransferReviewPageReqVO pageReqVO, String adminName) {
        try {
            Page<UserTransferAgentPO> page = new Page<>(pageReqVO.getPageNumber(), pageReqVO.getPageSize());
            LambdaQueryWrapper<UserTransferAgentPO> query = Wrappers.lambdaQuery();
            query.eq(UserTransferAgentPO::getSiteCode, pageReqVO.getSiteCode());
            //申请时间-开始时间
            Long applyTimeStart = pageReqVO.getApplyTimeStart();
            if (applyTimeStart != null) {
                query.ge(UserTransferAgentPO::getCreatedTime, applyTimeStart);
            }
            //申请时间-结束时间
            Long applyTimeEnd = pageReqVO.getApplyTimeEnd();
            if (applyTimeEnd != null) {
                query.le(UserTransferAgentPO::getCreatedTime, applyTimeEnd);
            }
            //审核时间-开始时间
            Long auditTimeStart = pageReqVO.getAuditTimeStart();
            if (auditTimeStart != null) {
                query.ge(UserTransferAgentPO::getAuditDatetime, auditTimeStart);
            }
            //审核时间-结束时间
            Long auditTimeEnd = pageReqVO.getAuditTimeEnd();
            if (auditTimeEnd != null) {
                query.le(UserTransferAgentPO::getAuditDatetime, auditTimeEnd);
            }
            //当前流程审批状态
            Integer auditStatus = pageReqVO.getAuditStatus();
            if (auditStatus != null) {
                query.eq(UserTransferAgentPO::getAuditStatus, auditStatus);
            }
            //锁单状态
            Integer lockStatus = pageReqVO.getLockStatus();
            if (lockStatus != null) {
                query.eq(UserTransferAgentPO::getLockStatus, lockStatus);
            }
            //审核操作类型
            Integer auditStep = pageReqVO.getAuditStep();
            if (auditStep != null) {
                query.eq(UserTransferAgentPO::getAuditStep, auditStep);
            }
            //申请人
            String applyName = pageReqVO.getApplyName();
            if (StringUtils.isNotBlank(applyName)) {
                query.eq(UserTransferAgentPO::getApplyName, applyName);
            }
            //审核人
            String auditName = pageReqVO.getAuditName();
            if (StringUtils.isNotBlank(auditName)) {
                //分两种情况,待审核的匹配锁单人,已审核的看审核人
                query.and(q -> q.eq(UserTransferAgentPO::getLockName, auditName).or().eq(UserTransferAgentPO::getAuditName, auditName));

            }
            //转代会员id
            String userAccount = pageReqVO.getUserAccount();
            if (StringUtils.isNotBlank(userAccount)) {
                query.eq(UserTransferAgentPO::getUserAccount, userAccount);
            }
            //当前代理账号
            String currentAgentName = pageReqVO.getCurrentAgentName();
            if (StringUtils.isNotBlank(currentAgentName)) {
                query.eq(UserTransferAgentPO::getCurrentAgentName, currentAgentName);
            }
            //转入代理账号
            String transferAgentName = pageReqVO.getTransferAgentName();
            if (StringUtils.isNotBlank(transferAgentName)) {
                query.eq(UserTransferAgentPO::getTransferAgentName, transferAgentName);
            }
            //审核单号
            String eventId = pageReqVO.getEventId();
            if (StringUtils.isNotBlank(eventId)) {
                query.eq(UserTransferAgentPO::getEventId, eventId);
            }
            //String orderName = pageReqVO.getOrderName();
            query.orderByDesc(UserTransferAgentPO::getAuditDatetime);
            /*if (StringUtils.isNotBlank(orderName) && orderName.equals("auditDatetime")) {

            } else {
                query.orderByAsc(UserTransferAgentPO::getCreatedTime);
            }*/
            page = repository.selectPage(page, query);

            IPage<MemberTransferReviewPageResVO> pageResult = page.convert(item -> {
                MemberTransferReviewPageResVO vo = BeanUtil.copyProperties(item, MemberTransferReviewPageResVO.class);
                if (adminName.equals(vo.getLockName())) {
                    //锁单人是否是当前用户
                    vo.setIsLocker(Integer.parseInt(YesOrNoEnum.YES.getCode()));
                } else {
                    vo.setIsLocker(Integer.parseInt(YesOrNoEnum.NO.getCode()));
                }
                if (adminName.equals(vo.getApplyName())) {
                    vo.setIsApplicant(Integer.parseInt(YesOrNoEnum.YES.getCode()));
                } else {
                    vo.setIsApplicant(Integer.parseInt(YesOrNoEnum.NO.getCode()));
                }
                //审核用时
                if (vo.getAuditStep().equals(ReviewOperationEnum.CHECK.getCode())
                        && vo.getCreatedTime() != null && vo.getAuditDatetime() != null) {
                    Long createdTime = vo.getCreatedTime();
                    Long auditDatetime = vo.getAuditDatetime();
                    //审核用时
                    vo.setReviewDuration(DateUtils.formatTime(auditDatetime - createdTime));
                }
                return vo;
            });
            return ConvertUtil.toConverPage(pageResult);
        } catch (Exception e) {
            throw new BaowangDefaultException("获取待审核列表失败");
        }
    }

    /**
     * 锁单/解锁
     *
     * @param vo 包含审核单据id,锁单/解锁状态
     * @return true
     */
    public boolean lockOrder(MemberTransferLockReqVO vo, String operator) {
        AssertUtil.isEmptyObject(vo.getLockStatus(), "操作状态不能为空");

        RLock lock = null;
        try {
            lock = RedisUtil.getFairLock(RedisConstants.AGENT_TRANSFER_MEMBER_LOCK_KEY + vo.getId());
            if (lock.tryLock(RedisLockConstants.WAIT_TIME, RedisLockConstants.UNLOCK_TIME, TimeUnit.SECONDS)) {
                UserTransferAgentPO agentPO = super.getById(vo.getId());
                if (agentPO == null) {
                    throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
                }
                if (agentPO.getApplyId().equals(operator)) {
                    throw new BaowangDefaultException(ResultCode.WRONG_OPERATION);
                }
                if (!ReviewStatusEnum.REVIEW_PENDING.getCode().equals(agentPO.getAuditStatus())
                        && !ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(agentPO.getAuditStatus())) {
                    throw new BaowangDefaultException("只有审核状态为待处理或处理中才可以锁单和解单操作");
                }
                //锁单
                if (LockStatusEnum.LOCK.getCode().equals(vo.getLockStatus())) {
                    AssertUtil.isTrue(LockStatusEnum.LOCK.getCode().equals(agentPO.getLockStatus()), "订单已锁定，不允许再次锁单");
                    LambdaQueryWrapper<UserTransferAgentPO> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(UserTransferAgentPO::getId, vo.getId());
                    //锁单状态(0-未锁单 1-已锁定)
                    agentPO.setLockStatus(LockStatusEnum.LOCK.getCode());
                    //锁单人工号使用oneId
                    agentPO.setLockerId(operator);
                    //锁单人名称使用账号名
                    agentPO.setLockName(operator);

                    agentPO.setLockDatetime(System.currentTimeMillis());
                    //审核人id
                    agentPO.setAuditId(operator);
                    //审核人使用oneId
                    agentPO.setAuditName(operator);

                    //状态变更为处理中
                    agentPO.setAuditStatus(ReviewStatusEnum.REVIEW_PROGRESS.getCode());
                    super.update(agentPO, queryWrapper);
                } else {
                    //解锁
                    AssertUtil.isTrue(LockStatusEnum.UNLOCK.getCode().equals(agentPO.getLockStatus()), "订单已解锁，不需要再次解锁");
                    AssertUtil.isTrue(!operator.equals(agentPO.getLockerId()), "订单被锁定后只能是锁定账号解锁");
                    LambdaQueryWrapper<UserTransferAgentPO> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(UserTransferAgentPO::getId, vo.getId());
                    agentPO.setLockStatus(LockStatusEnum.UNLOCK.getCode());//锁单状态(0-未锁单 1-已锁定)
                    agentPO.setLockName(StringUtils.EMPTY);
                    agentPO.setAuditName(StringUtils.EMPTY);
                    //状态还原为待处理
                    agentPO.setAuditStatus(ReviewStatusEnum.REVIEW_PENDING.getCode());
                    super.update(agentPO, queryWrapper);
                }
                return true;
            }
        } catch (BaowangDefaultException e) {
            throw e;
        } catch (Exception e) {
            log.error("会员转代锁单/解单异常", e);
        } finally {
            if (!ObjectUtils.isEmpty(lock) && lock.isLocked()) {
                lock.unlock();
                log.info("会员转代锁单/解单，锁已释放");
            }
        }
        return false;
    }

    /**
     * 审核单据详情
     *
     * @param vo id
     * @return 转代详情
     */
    public ResponseVO<MemberTransferDetailResVO> detail(MemberTransferLockReqVO vo) {
        MemberTransferDetailResVO result = new MemberTransferDetailResVO();
        String id = vo.getId();
        UserTransferAgentPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_IS_EXIST);
        }
        //本次申请信息
        UserTransferAgentApplyInfo info = BeanUtil.copyProperties(po, UserTransferAgentApplyInfo.class);
        result.setApplyInfo(info);
        String siteCode = po.getSiteCode();
        String currentAgentName = po.getCurrentAgentName();
        String transferAgentName = po.getTransferAgentName();
        //变更前代理信息
        MemberTransferModifyInfoVO before = getAgentMsgByAgentAccount(siteCode, currentAgentName);
        result.setBeforeFixing(before);
        //变更后代理信息
        MemberTransferModifyInfoVO after = getAgentMsgByAgentAccount(siteCode, transferAgentName);
        result.setAfterModification(after);

        String userAccount = po.getUserAccount();

        UserInfoVO userInfoVO = userInfoApi.getUserByUserAccountAndSiteCode(userAccount, siteCode);
        if (userInfoVO == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        //会员信息
        UserTransferAgentUserDetail detail = BeanUtil.copyProperties(userInfoVO, UserTransferAgentUserDetail.class);
        detail.setCurrentAgentName(userInfoVO.getSuperAgentAccount());
        // 获取IP风控层级list ip
        RiskListAccountQueryVO riskQuery = new RiskListAccountQueryVO();
        riskQuery.setRiskControlTypeCode(RiskTypeEnum.RISK_MEMBER.getCode());
        riskQuery.setRiskControlAccounts(List.of(userInfoVO.getUserAccount()));
        riskQuery.setSiteCode(siteCode);
        // 封控等级待补充
        List<RiskAccountVO> userAccountRisk = riskApi.getRiskListAccount(riskQuery);
        if (CollectionUtil.isNotEmpty(userAccountRisk)) {
            //风控会员
            detail.setRiskLevel(userAccountRisk.stream()
                    .map(RiskAccountVO::getRiskControlLevel)  // 获取每个对象的 riskControlLevel 字段
                    .collect(Collectors.joining(CommonConstant.COMMA)));  // 使用逗号拼接
        }
        //会员标签
        String userLabelId = userInfoVO.getUserLabelId();
        if (StringUtils.isNotBlank(userLabelId)) {
            List<GetUserLabelByIdsVO> userLabels = userLabelConfigApi.getUserLabelByIds(Arrays.asList(userLabelId.split(CommonConstant.COMMA)));
            if (CollectionUtil.isNotEmpty(userLabels)) {
                detail.setUserLabel(userLabels.stream().map(GetUserLabelByIdsVO::getLabelName).collect(Collectors.joining(CommonConstant.COMMA)));
            }
        }
        //会员vip等级
        Integer vipGradeCode = userInfoVO.getVipGradeCode();
        SiteVIPGradeVO vipGradeVO = vipGradeApi.getSiteVipGradeByCodeAndSiteCode(siteCode, vipGradeCode);
        detail.setVipGradeName(vipGradeVO.getVipGradeName());

        result.setUserDetail(detail);
        return ResponseVO.success(result);
    }

    private MemberTransferModifyInfoVO getAgentMsgByAgentAccount(String siteCode, String agentAccount) {
        LambdaQueryWrapper<AgentInfoPO> query = Wrappers.lambdaQuery();
        query.eq(AgentInfoPO::getSiteCode, siteCode).eq(AgentInfoPO::getAgentAccount, agentAccount);
        AgentInfoPO one = agentInfoService.getOne(query);
        if (one == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        MemberTransferModifyInfoVO agentInfo = BeanUtil.copyProperties(one, MemberTransferModifyInfoVO.class);
        agentInfo.setAgentName(one.getAgentAccount());
        agentInfo.setStatus(one.getStatus());
        //代理风控层级
        // 获取IP风控层级list ip
        RiskListAccountQueryVO riskQuery = new RiskListAccountQueryVO();
        riskQuery.setRiskControlTypeCode(RiskTypeEnum.RISK_AGENT.getCode());
        riskQuery.setRiskControlAccounts(List.of(one.getAgentAccount()));
        riskQuery.setSiteCode(siteCode);
        List<RiskAccountVO> agentAccountRisks = riskApi.getRiskListAccount(riskQuery);
        if (CollectionUtil.isNotEmpty(agentAccountRisks)) {
            //风控会员
            agentInfo.setAgentRiskLevel(agentAccountRisks.stream()
                    .map(RiskAccountVO::getRiskControlLevel)
                    .collect(Collectors.joining(CommonConstant.COMMA)));
        }
        String agentLabelId = one.getAgentLabelId();
        if (StringUtils.isNotBlank(agentLabelId)) {
            //代理标签
            LambdaQueryWrapper<AgentLabelPO> labelQuery = Wrappers.lambdaQuery();
            labelQuery.eq(AgentLabelPO::getSiteCode, siteCode).in(AgentLabelPO::getId, Arrays.asList(agentLabelId.split(CommonConstant.COMMA)));
            List<AgentLabelPO> agentLabelPOS = agentLabelRepository.selectList(labelQuery);
            if (CollectionUtil.isNotEmpty(agentLabelPOS)) {
                agentInfo.setAgentLabel(agentLabelPOS.stream().map(AgentLabelPO::getName).collect(Collectors.joining(CommonConstant.COMMA)));
            }
        }

        return agentInfo;
    }

    /**
     * 审核
     *
     * @param vo 包含id,审核状态（通过驳回）
     * @return true
     */
    public ResponseVO<Boolean> audit(MemberTransferAuthReqVO vo, String operator) {
        UserTransferAgentPO agentPO = repository.selectById(vo.getId());
        if (agentPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        if (ReviewStatusEnum.REVIEW_PASS.getCode().equals(agentPO.getAuditStatus())
                || ReviewStatusEnum.REVIEW_REJECTED.getCode().equals(agentPO.getAuditStatus())) {
            throw new BaowangDefaultException("");
        }
        if (agentPO.getApplyName().equals(operator)) {
            throw new BaowangDefaultException(ResultCode.WRONG_OPERATION);
        }
        if (LockStatusEnum.UNLOCK.getCode().equals(agentPO.getLockStatus())) {
            throw new BaowangDefaultException(ResultCode.APPLY_UNLOCK);
        }
        if (!agentPO.getLockName().equals(operator)) {
            throw new BaowangDefaultException(ResultCode.NOT_CURRENT_LOCK);
        }
        //获取转入后代理信息
        AgentInfoPO agentInfo = agentInfoService.getOne(Wrappers.<AgentInfoPO>lambdaQuery().eq(AgentInfoPO::getAgentAccount, agentPO.getTransferAgentName()).eq(AgentInfoPO::getSiteCode, agentPO.getSiteCode()));
        if (agentInfo == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        LambdaQueryWrapper<UserTransferAgentPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTransferAgentPO::getId, vo.getId());

        agentPO.setAuditStatus(vo.getAuditStatus());
        //锁定状态还原
        agentPO.setLockStatus(LockStatusEnum.UNLOCK.getCode());
        //锁单人置空
        agentPO.setLockName(StringUtils.EMPTY);
        //审核人id
        agentPO.setAuditId(operator);
        //审核人账号
        agentPO.setAuditName(operator);
        agentPO.setAuditDatetime(System.currentTimeMillis());
        agentPO.setAuditRemark(vo.getAuditRemark());
        //结单查看
        agentPO.setAuditStep(ReviewOperationEnum.CHECK.getCode());
        super.update(agentPO, queryWrapper);
        //审核通过
        if (ReviewStatusEnum.REVIEW_PASS.getCode().equals(vo.getAuditStatus())) {

            //更新会员上级代理信息
            socketService.sendAgentDepositWithdrawSocket(SystemMessageEnum.AGENT_MEMBER_TRANSFER_SUCCESS,
                    agentInfo.getSiteCode(), agentPO.getCurrentAgentId(), agentPO.getUserAccount(),
                    WSSubscribeEnum.USER_TRANSFER_AGENT.getTopic());

            //更新会员上级代理信息
            userInfoApi.updateAgentTransferInfoBySiteCode(agentPO.getSiteCode(), agentPO.getUserAccount(), agentInfo.getAgentId(), agentInfo.getAgentAccount(), true);
            //发送消息通知代理
            /*rechargeWithdrawSocketService.sendDepositWithdrawSocket(SystemMessageEnum.MEMBER_DEPOSIT_COMPLETED,userDepositWithdrawalPO.getSiteCode(),
                    userDepositWithdrawalPO.getUserId(),userDepositWithdrawalPO.getUserAccount(),userDepositWithdrawalPO.getArriveAmount()
                    ,WSSubscribeEnum.MEMBER_DEPOSIT_COMPLETED.getTopic(),userDepositWithdrawalPO.getCurrencyCode());*/

        }
        return ResponseVO.success(true);
    }


    /**
     * 查询待处理条数
     *
     * @return Long
     */
    public UserAccountUpdateVO queryPendingCount() {
        UserAccountUpdateVO vo = new UserAccountUpdateVO();
        List<Integer> statusArr = new ArrayList<>();
        statusArr.add(ReviewStatusEnum.REVIEW_PENDING.getCode());
        statusArr.add(ReviewStatusEnum.REVIEW_PROGRESS.getCode());
        Long count = this.lambdaQuery()
                .in(UserTransferAgentPO::getAuditStatus, statusArr)
                .count();
        vo.setNum(count);
        vo.setRouter("/Agent/AgentReview/MemberTransferReview");
        return vo;
    }

    /**
     * 获取当前站点下，会员转代查询待处理条数
     *
     * @param siteCode 站点code
     * @return vo
     */
    public UserAccountUpdateVO queryPendingCountBySiteCode(String siteCode) {
        UserAccountUpdateVO vo = new UserAccountUpdateVO();
        List<Integer> statusArr = new ArrayList<>();
        statusArr.add(ReviewStatusEnum.REVIEW_PENDING.getCode());
        statusArr.add(ReviewStatusEnum.REVIEW_PROGRESS.getCode());
        Long count = this.lambdaQuery()
                .eq(UserTransferAgentPO::getSiteCode, siteCode)
                .in(UserTransferAgentPO::getAuditStatus, statusArr)
                .count();
        vo.setCountType("2");
        vo.setNum(count);
        vo.setRouter("/Agent/AgentReview/MemberTransferReview");
        return vo;
    }

    public ResponseVO<MemberTransferUserRespVO> queryUser(MemberTransferUserReqVO vo) {
        UserBasicRequestVO userBasicRequestVO = new UserBasicRequestVO();
        BeanUtils.copyProperties(vo, userBasicRequestVO);
        UserInfoVO user = userInfoApi.getUserInfoVOByAccountOrRegister(userBasicRequestVO);
        if (user == null) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        MemberTransferUserRespVO memberTransferUserRespVO = new MemberTransferUserRespVO();
        BeanUtils.copyProperties(user, memberTransferUserRespVO);
        return ResponseVO.success(memberTransferUserRespVO);
    }

    public Long getTotal(MemberTransferReviewPageReqVO pageReqVO) {
        LambdaQueryWrapper<UserTransferAgentPO> query = Wrappers.lambdaQuery();
        query.eq(UserTransferAgentPO::getSiteCode, pageReqVO.getSiteCode());
        //申请时间-开始时间
        Long applyTimeStart = pageReqVO.getApplyTimeStart();
        if (applyTimeStart != null) {
            query.ge(UserTransferAgentPO::getCreatedTime, applyTimeStart);
        }
        //申请时间-结束时间
        Long applyTimeEnd = pageReqVO.getApplyTimeEnd();
        if (applyTimeEnd != null) {
            query.le(UserTransferAgentPO::getCreatedTime, applyTimeEnd);
        }
        //审核时间-开始时间
        Long auditTimeStart = pageReqVO.getAuditTimeStart();
        if (auditTimeStart != null) {
            query.ge(UserTransferAgentPO::getAuditDatetime, auditTimeStart);
        }
        //审核时间-结束时间
        Long auditTimeEnd = pageReqVO.getAuditTimeEnd();
        if (auditTimeEnd != null) {
            query.le(UserTransferAgentPO::getAuditDatetime, auditTimeEnd);
        }
        //当前流程审批状态
        Integer auditStatus = pageReqVO.getAuditStatus();
        if (auditStatus != null) {
            query.eq(UserTransferAgentPO::getAuditStatus, auditStatus);
        }
        //锁单状态
        Integer lockStatus = pageReqVO.getLockStatus();
        if (lockStatus != null) {
            query.eq(UserTransferAgentPO::getLockStatus, lockStatus);
        }
        //审核操作类型
        Integer auditStep = pageReqVO.getAuditStep();
        if (auditStep != null) {
            query.eq(UserTransferAgentPO::getAuditStep, auditStep);
        }
        //申请人
        String applyName = pageReqVO.getApplyName();
        if (StringUtils.isNotBlank(applyName)) {
            query.eq(UserTransferAgentPO::getApplyName, applyName);
        }
        //审核人
        String auditName = pageReqVO.getAuditName();
        if (StringUtils.isNotBlank(auditName)) {
            //分两种情况,待审核的匹配锁单人,已审核的看审核人
            if (ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode().equals(pageReqVO.getAuditStep())) {
                query.eq(UserTransferAgentPO::getLockName, auditName);
            } else {
                query.eq(UserTransferAgentPO::getAuditName, auditName);
            }

        }
        //转代会员id
        String userAccount = pageReqVO.getUserAccount();
        if (StringUtils.isNotBlank(userAccount)) {
            query.eq(UserTransferAgentPO::getUserAccount, userAccount);
        }
        //转入代理账号
        String transferAgentName = pageReqVO.getTransferAgentName();
        if (StringUtils.isNotBlank(transferAgentName)) {
            query.eq(UserTransferAgentPO::getTransferAgentName, transferAgentName);
        }
        //审核单号
        String eventId = pageReqVO.getEventId();
        if (StringUtils.isNotBlank(eventId)) {
            query.eq(UserTransferAgentPO::getEventId, eventId);
        }

        query.orderByAsc(UserTransferAgentPO::getCreatedTime);
        return repository.selectCount(query);
    }

    public List<ReportUserTransferRespVO> queryUserTransferCount(ReportUserTransferReqVO vo) {
        List<ReportUserTransferRespVO> respVOS = this.baseMapper.queryUserTransferCount(vo.getSiteCode(), vo.getStartTime(), vo.getEndTime());
        if (respVOS == null) {
            respVOS = new ArrayList<>(); // 避免传入 null
        }
        return respVOS;


    }

    public List<ReportUserTransferRespVO> queryUserTransferCountAllPlatForm(ReportUserTransferReqVO vo) {
        List<ReportUserTransferRespVO> respVOS = this.baseMapper.queryUserTransferCountAllPlatform(vo.getStartTime(), vo.getEndTime());
        if (respVOS == null) {
            respVOS = new ArrayList<>(); // 避免传入 null
        }
        return respVOS;


    }

    /**
     * 按照会员账号、时间区间获取 会员转代记录
     *
     * @param memberTransferReviewPageReqVO 查询条件
     * @return
     */
    public List<MemberTransferReviewPageResVO> getRecordListByAccounts(MemberTransferReviewPageReqVO memberTransferReviewPageReqVO) {
        LambdaQueryWrapper<UserTransferAgentPO> query = Wrappers.lambdaQuery();
        query.eq(UserTransferAgentPO::getSiteCode, memberTransferReviewPageReqVO.getSiteCode()).eq(UserTransferAgentPO::getAuditStatus, ReviewStatusEnum.REVIEW_PASS.getCode());
        query.in(UserTransferAgentPO::getUserAccount, memberTransferReviewPageReqVO.getUserAccounts());
        query.ge(UserTransferAgentPO::getAuditDatetime, memberTransferReviewPageReqVO.getAuditTimeStart());
        query.le(UserTransferAgentPO::getAuditDatetime, memberTransferReviewPageReqVO.getAuditTimeEnd());
        List<UserTransferAgentPO> list = this.list(query);
        return BeanUtil.copyToList(list, MemberTransferReviewPageResVO.class);
    }

    public Page<MemberTransferReviewPageResVO> listByAuditTime(MemberTransferReviewPageReqVO memberTransferReviewPageReqVO) {
        Page<UserTransferAgentPO> page = new Page<>(memberTransferReviewPageReqVO.getPageNumber(), memberTransferReviewPageReqVO.getPageSize());
        LambdaQueryWrapper<UserTransferAgentPO> query = Wrappers.lambdaQuery();
        query.eq(UserTransferAgentPO::getSiteCode, memberTransferReviewPageReqVO.getSiteCode());
        query.eq(UserTransferAgentPO::getAuditStatus, ReviewStatusEnum.REVIEW_PASS.getCode());
        if(StringUtils.isNotBlank(memberTransferReviewPageReqVO.getUserAccount())){
            query.eq(UserTransferAgentPO::getUserAccount, memberTransferReviewPageReqVO.getUserAccount());
        }
        if(memberTransferReviewPageReqVO.getAuditTimeStart()!=null){
            query.ge(UserTransferAgentPO::getAuditDatetime, memberTransferReviewPageReqVO.getAuditTimeStart());
        }
        if(memberTransferReviewPageReqVO.getAuditTimeEnd()!=null){
            query.le(UserTransferAgentPO::getAuditDatetime, memberTransferReviewPageReqVO.getAuditTimeEnd());
        }

        Page<UserTransferAgentPO> userTransferAgentPOPage = this.page(page,query);
        Page<MemberTransferReviewPageResVO> resultPage = new Page<>(memberTransferReviewPageReqVO.getPageNumber(), memberTransferReviewPageReqVO.getPageSize());
        BeanUtils.copyProperties(userTransferAgentPOPage,resultPage);
        List<MemberTransferReviewPageResVO> memberTransferReviewPageResVOS=BeanUtil.copyToList(userTransferAgentPOPage.getRecords(), MemberTransferReviewPageResVO.class);
        resultPage.setRecords(memberTransferReviewPageResVOS);
        return resultPage;
    }

}
