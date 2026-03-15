package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentUserOverflowAuditStatusEnum;
import com.cloud.baowang.agent.api.enums.AgentUserOverflowAuditStepEnum;
import com.cloud.baowang.agent.api.enums.UserOverFlowSourceEnums;
import com.cloud.baowang.agent.api.vo.agentreview.UserAccountUpdateVO;
import com.cloud.baowang.agent.api.vo.manualup.ReviewInfoVO;
import com.cloud.baowang.agent.api.vo.member.AgentOverflowApplyInfo;
import com.cloud.baowang.agent.api.vo.member.AgentUserOverflowApplyVO;
import com.cloud.baowang.agent.api.vo.member.AgentUserOverflowClientApplyVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowAuthReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowClientPageReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowClientPageResVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowDetailResVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowLockReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowReviewPageReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowReviewPageResVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferModifyInfoVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferUserReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferUserRespVO;
import com.cloud.baowang.agent.api.vo.member.UserTransferAgentUserDetail;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentLabelPO;
import com.cloud.baowang.agent.po.AgentUserOverflowPO;
import com.cloud.baowang.agent.repositories.AgentLabelRepository;
import com.cloud.baowang.agent.repositories.AgentUserOverflowRepository;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.agent.util.MinioFileService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.LockStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.ReviewOperationEnum;
import com.cloud.baowang.common.core.enums.ReviewStatusEnum;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.user.api.enums.UserTypeEnum;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.agent.api.enums.AgentTypeEnum;
import com.cloud.baowang.user.api.enums.UserAccountTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AssertUtil;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author aomiao
 * 会员溢出审核业务相关处理
 */
@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AgentUserOverflowService extends ServiceImpl<AgentUserOverflowRepository, AgentUserOverflowPO> {

    private final UserInfoApi userInfoApi;
    private final AgentUserOverflowRepository repository;
    private final AgentInfoService agentInfoService;
    private final MinioFileService minioFileService;
    private final VipGradeApi vipGradeApi;
    private final SiteUserLabelConfigApi configApi;
    private final SystemParamApi systemParamApi;
    private final RiskApi riskApi;
    private final AgentLabelRepository agentLabelRepository;
    private final AgentTransferAgentSocketService socketService;

    /**
     * 发起会员溢出审核申请
     *
     * @param applyVO   申请对象
     * @param adminName 申请人
     * @return true
     */
    public Boolean apply(AgentUserOverflowApplyVO applyVO, String adminName) {
        GetByUserAccountVO userInfoByAccount = userInfoApi.getByUserAccountAndSiteCode(applyVO.getMemberName(), applyVO.getSiteCode());
        if (userInfoByAccount == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (StringUtils.isNotBlank(applyVO.getLink()) && applyVO.getLink().length() > 50) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (StringUtils.isNotBlank(userInfoByAccount.getSuperAgentAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_AL_HAV_AGENT);
        }

        AgentInfoPO agentInfo = agentInfoService.getOne(Wrappers.<AgentInfoPO>lambdaQuery().
                eq(AgentInfoPO::getAgentAccount, applyVO.getTransferAgentName()).eq(AgentInfoPO::getSiteCode, applyVO.getSiteCode()));
        if (agentInfo == null) {
            throw new BaowangDefaultException(ResultCode.AGENT_NOT_EXISTS);
        }
        String accountType = userInfoByAccount.getAccountType();
        Integer agentType = agentInfo.getAgentType();
        if ((UserAccountTypeEnum.TEST_ACCOUNT.getCode().equals(Integer.parseInt(accountType)) &&
                AgentTypeEnum.FORMAL.getCode().equals(String.valueOf(agentType))) ||
                (UserAccountTypeEnum.FORMAL_ACCOUNT.getCode().equals(Integer.parseInt(accountType)) &&
                        AgentTypeEnum.TEST.getCode().equals(String.valueOf(agentType)))) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_TYPE_NOT_EQ_AGENT);
        }

        if (UserTypeEnum.FORMAL.getCode().equals(Integer.valueOf(accountType)) && !AgentTypeEnum.FORMAL.getCode().equals(String.valueOf(agentType))) {
            throw new BaowangDefaultException(ResultCode.USER_TYPE_NOT_EQ_AGENT_TYPE);
        }
        //判断当前会员是否存在待审核溢出流程
        LambdaQueryWrapper<AgentUserOverflowPO> query = Wrappers.lambdaQuery();
        query.eq(AgentUserOverflowPO::getMemberName, applyVO.getMemberName())
                .eq(AgentUserOverflowPO::getSiteCode, applyVO.getSiteCode())
                .eq(AgentUserOverflowPO::getAuditStep, ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
        if (repository.selectCount(query) > 0) {
            throw new BaowangDefaultException(ResultCode.USER_EXISTS_TO_REVIEWED);
        }
        try {
            String imageStr = "";
            List<String> image = applyVO.getImage();
            if (CollectionUtil.isNotEmpty(image)) {
                imageStr = String.join(CommonConstant.COMMA, image);
            }
            String orderNo = AgentServerUtil.createOrderNo();
            AgentUserOverflowPO agentUserOverflowPO = AgentUserOverflowPO.builder()
                    .applySource(applyVO.getApplySource())
                    .userId(userInfoByAccount.getUserId())
                    .transferAgentName(applyVO.getTransferAgentName())
                    .transferAgentId(agentInfo.getAgentId())
                    .applyRemark(applyVO.getApplyRemark())
                    .memberName(applyVO.getMemberName())
                    .userRegister(applyVO.getUserRegister())
                    .applyName(adminName)
                    .link(applyVO.getLink())
                    .device(applyVO.getDevice())
                    .accountType(Integer.parseInt(accountType))
                    .agentType(agentType)
                    .image(imageStr)
                    .eventId(orderNo)
                    //审核状态为待审核
                    .auditStatus(ReviewStatusEnum.REVIEW_PENDING.getCode())
                    //审核操作为一审审核
                    .auditStep(ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode()).build();
            agentUserOverflowPO.setCreatedTime(System.currentTimeMillis());
            agentUserOverflowPO.setSiteCode(applyVO.getSiteCode());
            this.save(agentUserOverflowPO);
            return true;
        } catch (Exception e) {
            log.error("AgentUserOverflowApplyVO error", e);
            throw new BaowangDefaultException("会员溢出申请异常");
        }
    }

    /**
     * 会员溢出审核分页列表
     *
     * @param vo        分页查询对象
     * @param adminName 当前登陆人员（用于判断是否是锁单人，是否是申请人等）
     * @return 分页
     */
    public Page<MemberOverflowReviewPageResVO> listPage(MemberOverflowReviewPageReqVO vo, String adminName) {
        try {
            String siteCode = vo.getSiteCode();
            Page<AgentUserOverflowPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
            LambdaQueryWrapper<AgentUserOverflowPO> query = Wrappers.lambdaQuery();
            query.eq(AgentUserOverflowPO::getSiteCode, siteCode);
            query.eq(AgentUserOverflowPO::getAuditStep, vo.getAuditStep());
            //申请开始时间
            Long applyTimeStart = vo.getApplyTimeStart();
            if (applyTimeStart != null) {
                query.ge(AgentUserOverflowPO::getCreatedTime, applyTimeStart);
            }
            //申请结束时间
            Long applyTimeEnd = vo.getApplyTimeEnd();
            if (applyTimeEnd != null) {
                query.le(AgentUserOverflowPO::getCreatedTime, applyTimeEnd);
            }
            //审核开始时间
            Long auditTimeStart = vo.getAuditTimeStart();
            if (auditTimeStart != null) {
                query.ge(AgentUserOverflowPO::getAuditDatetime, auditTimeStart);
            }
            //审核结束时间
            Long auditTimeEnd = vo.getAuditTimeEnd();
            if (auditTimeEnd != null) {
                query.le(AgentUserOverflowPO::getAuditDatetime, auditTimeEnd);
            }
            //转入代理账号
            String transferAgentName = vo.getTransferAgentName();
            if (StringUtils.isNotBlank(transferAgentName)) {
                query.eq(AgentUserOverflowPO::getTransferAgentName, transferAgentName);
            }
            //审核单号
            String eventId = vo.getEventId();
            if (StringUtils.isNotBlank(eventId)) {
                query.eq(AgentUserOverflowPO::getEventId, eventId);
            }
            //审核操作
            Integer auditStep = vo.getAuditStep();
            if (auditStep != null) {
                query.eq(AgentUserOverflowPO::getAuditStep, auditStep);
            }
            //审核状态
            Integer auditStatus = vo.getAuditStatus();
            if (auditStatus != null) {
                query.eq(AgentUserOverflowPO::getAuditStatus, auditStatus);
            }
            //锁单状态
            Integer lockStatus = vo.getLockStatus();
            if (lockStatus != null) {
                query.eq(AgentUserOverflowPO::getLockStatus, lockStatus);
            }
            //申请人
            String applyName = vo.getApplyName();
            if (StringUtils.isNotBlank(applyName)) {
                query.eq(AgentUserOverflowPO::getApplyName, applyName);
            }
            //审核人
            String auditName = vo.getAuditName();
            if (StringUtils.isNotBlank(auditName)) {
                query.eq(AgentUserOverflowPO::getAuditName, auditName);
            }
            //会员账号
            String memberName = vo.getMemberName();
            if (StringUtils.isNotBlank(memberName)) {
                query.eq(AgentUserOverflowPO::getMemberName, memberName);
            }
            query.orderByDesc(AgentUserOverflowPO::getAuditDatetime);
            page = repository.selectPage(page, query);
            IPage<MemberOverflowReviewPageResVO> iPage = page.convert(item -> {
                MemberOverflowReviewPageResVO resultVo = BeanUtil.copyProperties(item, MemberOverflowReviewPageResVO.class);
                if (item.getAuditStep().equals(ReviewOperationEnum.CHECK.getCode())
                        && item.getCreatedTime() != null
                        && item.getAuditDatetime() != null) {
                    Long createdTime = item.getCreatedTime();
                    Long auditDatetime = item.getAuditDatetime();
                    //审核用时
                    resultVo.setReviewDuration(DateUtils.formatTime(auditDatetime - createdTime));
                }
                //当前人员是否是锁单人
                if (adminName.equals(resultVo.getLockName())) {
                    resultVo.setIsLocker(Integer.parseInt(YesOrNoEnum.YES.getCode()));
                } else {
                    resultVo.setIsLocker(Integer.parseInt(YesOrNoEnum.NO.getCode()));
                }
                //当前人员是否是申请人
                if (adminName.equals(resultVo.getApplyName()) &&
                        item.getApplySource().equals(UserOverFlowSourceEnums.SITE_BACKEND.getType())) {
                    resultVo.setIsApplicant(Integer.parseInt(YesOrNoEnum.YES.getCode()));
                } else {
                    resultVo.setIsApplicant(Integer.parseInt(YesOrNoEnum.NO.getCode()));
                }
                return resultVo;
            });
            return ConvertUtil.toConverPage(iPage);
        } catch (Exception e) {
            log.error("会员溢出审核分页查询异常,req:{},e:{}", vo, e.getMessage());
            throw new BaowangDefaultException("会员溢出审核分页查询异常");
        }
    }

    /**
     * 锁单/解锁
     *
     * @param vo        id与对应解锁，锁单操作状态
     * @param adminName 审核人
     * @return true
     */
    public Boolean lockOrder(MemberOverflowLockReqVO vo, String adminName) {
        AssertUtil.isEmptyObject(vo.getLockStatus(), "操作状态不能为空");
        if (vo.getLockStatus() == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        RLock lock = null;
        try {
            lock = RedisUtil.getFairLock(RedisConstants.AGENT_OVERFLOW_MEMBER_LOCK_KEY + vo.getId());
            if (lock.tryLock(RedisLockConstants.WAIT_TIME, RedisLockConstants.UNLOCK_TIME, TimeUnit.SECONDS)) {
                AgentUserOverflowPO agentPO = this.getById(vo.getId());
                if (agentPO == null) {
                    throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
                }

                if (ReviewOperationEnum.CHECK.getCode().equals(agentPO.getAuditStep())) {
                    throw new BaowangDefaultException(ResultCode.APPLY_IS_COMPLATE);
                }
                //锁单
                if (LockStatusEnum.LOCK.getCode().equals(vo.getLockStatus())) {
                    if (ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(agentPO.getAuditStatus())) {
                        throw new BaowangDefaultException(ResultCode.AL_IS_LOCK);
                    }
                    //锁单人不能是申请人
                    if (agentPO.getApplyName().equals(adminName) && agentPO.getApplySource().equals(UserOverFlowSourceEnums.SITE_BACKEND.getType())) {
                        throw new BaowangDefaultException(ResultCode.APPLICANT_CANNOT_REVIEW);
                    }
                    LambdaQueryWrapper<AgentUserOverflowPO> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(AgentUserOverflowPO::getId, vo.getId());
                    agentPO.setLockStatus(LockStatusEnum.LOCK.getCode());//锁单状态(0-未锁单 1-已锁定)
                    agentPO.setLockName(adminName);
                    agentPO.setAuditName(adminName);
                    //审核状态(1-待处理 2-处理中 3-审核通过 4-审核拒绝)
                    agentPO.setAuditStatus(ReviewStatusEnum.REVIEW_PROGRESS.getCode());
                    this.update(agentPO, queryWrapper);
                } else {
                    //解锁
                    if (ReviewStatusEnum.REVIEW_PENDING.getCode().equals(agentPO.getAuditStatus())) {
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }
                    if (!adminName.equals(agentPO.getAuditName())) {
                        throw new BaowangDefaultException(ResultCode.NOT_CURRENT_LOCK);
                    }
                    LambdaQueryWrapper<AgentUserOverflowPO> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(AgentUserOverflowPO::getId, vo.getId());
                    agentPO.setLockStatus(LockStatusEnum.UNLOCK.getCode());
                    agentPO.setLockName(StringUtils.EMPTY);
                    agentPO.setAuditName(StringUtils.EMPTY);
                    //审核状态还原为待处理
                    agentPO.setAuditStatus(ReviewStatusEnum.REVIEW_PENDING.getCode());
                    this.update(agentPO, queryWrapper);
                }
            }
        } catch (BaowangDefaultException e) {
            throw e;
        } catch (Exception e) {
            log.error("调线申请锁单/解单异常", e);
        } finally {
            if (!ObjectUtils.isEmpty(lock) && lock.isLocked()) {
                lock.unlock();
                log.info("调线申请锁单/解单，锁已释放");
            }
        }
        return true;
    }

    /**
     * 溢出申请详情
     *
     * @param vo id
     * @return 详情
     */
    public ResponseVO<MemberOverflowDetailResVO> detail(MemberOverflowLockReqVO vo) {
        MemberOverflowDetailResVO result = new MemberOverflowDetailResVO();
        try {
            String id = vo.getId();
            AgentUserOverflowPO po = this.getById(id);
            if (po == null) {
                throw new BaowangDefaultException(ResultCode.DATA_IS_EXIST);
            }
            //本次申请信息
            AgentOverflowApplyInfo info = BeanUtil.copyProperties(po, AgentOverflowApplyInfo.class);
            String image = info.getImage();
            if (StringUtils.isNotBlank(image)) {
                String minioDomain = minioFileService.getMinioDomain();
                List<String> imageArr = Arrays.asList(image.split(CommonConstant.COMMA));
                imageArr = imageArr.stream()
                        .map(i -> minioDomain + "/" + i)  // 在每个元素前加上 prefix
                        .toList();
                info.setImageArr(imageArr);
            }
            result.setApplyInfo(info);
            String siteCode = po.getSiteCode();
            String transferAgentName = po.getTransferAgentName();
            //封装审核信息
            ArrayList<ReviewInfoVO> reviewInfoVOS = new ArrayList<>();
            ReviewInfoVO infoVO = new ReviewInfoVO();
            infoVO.setReviewer(info.getAuditName());
            infoVO.setOrderStatus(info.getAuditStatus());
            infoVO.setReviewFinishTime(info.getAuditDatetime());
            infoVO.setReviewRemark(info.getAuditRemark());
            reviewInfoVOS.add(infoVO);
            result.setReviewInfoVOS(reviewInfoVOS);

            //变更后代理信息
            MemberTransferModifyInfoVO after = getAgentMsgByAgentAccount(siteCode, transferAgentName);
            result.setAfterModification(after);

            String userAccount = po.getMemberName();

            UserInfoVO userInfoVO = userInfoApi.getUserByUserAccountAndSiteCode(userAccount, siteCode);
            if (userInfoVO == null) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            UserTransferAgentUserDetail detail = BeanUtil.copyProperties(userInfoVO, UserTransferAgentUserDetail.class);
            Integer vipGradeCode = userInfoVO.getVipGradeCode();
            if (vipGradeCode != null) {
                SiteVIPGradeVO vipGrade = vipGradeApi.getSiteVipGradeByCodeAndSiteCode(userInfoVO.getSiteCode(), userInfoVO.getVipGradeCode());
                detail.setVipGradeName(vipGrade.getVipGradeName());
            }
            String userLabelId = userInfoVO.getUserLabelId();
            if (StringUtils.isNotBlank(userLabelId)) {
                List<GetUserLabelByIdsVO> userLabelByIds = configApi.getUserLabelByIds(Arrays.asList(userLabelId.split(CommonConstant.COMMA)));
                if (CollectionUtil.isNotEmpty(userLabelByIds)) {
                    String labelNames = userLabelByIds.stream()
                            .map(GetUserLabelByIdsVO::getLabelName)
                            .collect(Collectors.joining(CommonConstant.COMMA));
                    detail.setUserLabel(labelNames);
                }
            }
            RiskAccountQueryVO riskAccountQueryVO = new RiskAccountQueryVO();
            riskAccountQueryVO.setSiteCode(siteCode);
            riskAccountQueryVO.setRiskControlAccount(userInfoVO.getUserAccount());
            riskAccountQueryVO.setRiskControlTypeCode(RiskTypeEnum.RISK_MEMBER.getCode());
            RiskAccountVO riskAccountVO = riskApi.getRiskAccountByAccount(riskAccountQueryVO);
            if (riskAccountVO != null) {
                detail.setRiskLevel(riskAccountVO.getRiskControlLevel());
            }
            result.setUserDetail(detail);
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("会员转代详情查询报错", e);
        }
        return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
    }


    private MemberTransferModifyInfoVO getAgentMsgByAgentAccount(String siteCode, String agentAccount) {
        LambdaQueryWrapper<AgentInfoPO> query = Wrappers.lambdaQuery();
        query.eq(AgentInfoPO::getSiteCode, siteCode).eq(AgentInfoPO::getAgentAccount, agentAccount);
        AgentInfoPO one = agentInfoService.getOne(query);
        if (one == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        RiskAccountQueryVO riskAccountQueryVO = new RiskAccountQueryVO();
        riskAccountQueryVO.setSiteCode(one.getSiteCode());
        riskAccountQueryVO.setRiskControlAccount(one.getAgentAccount());
        riskAccountQueryVO.setRiskControlTypeCode(RiskTypeEnum.RISK_AGENT.getCode());
        RiskAccountVO riskAccountVO = riskApi.getRiskAccountByAccount(riskAccountQueryVO);

        MemberTransferModifyInfoVO result = new MemberTransferModifyInfoVO();

        if (riskAccountVO != null) {
            result.setAgentRiskLevel(riskAccountVO.getRiskControlLevel());
        }
        if (StringUtils.isNotBlank(one.getAgentLabelId())) {
            List<String> labelIdList = Arrays.asList(one.getAgentLabelId().split(CommonConstant.COMMA));
            LambdaQueryWrapper<AgentLabelPO> agentQuery = Wrappers.lambdaQuery();
            agentQuery.in(AgentLabelPO::getId, labelIdList);
            List<AgentLabelPO> agentLabelPOList = agentLabelRepository.selectList(agentQuery);
            if (CollectionUtil.isNotEmpty(agentLabelPOList)) {
                List<String> labelList = agentLabelPOList.stream().map(AgentLabelPO::getName).collect(Collectors.toList());
                result.setAgentLabel(CollectionUtil.join(labelList, CommonConstant.COMMA));
            }
        }
        result.setLevel(one.getLevel());
        result.setAgentName(one.getAgentAccount());
        result.setStatus(one.getStatus());
        result.setRemark(one.getRemark());
        return result;
    }

    /**
     * 溢出审核
     *
     * @param vo        包含id,同意/驳回，审核备注
     * @param adminName 审核人
     * @return true
     */
    public ResponseVO<Boolean> audit(MemberOverflowAuthReqVO vo, String adminName) {
        log.info("MemberOverflowAuthReqVO request:{}", vo);
        AgentUserOverflowPO agentPO = this.getById(vo.getId());

        if (ReviewOperationEnum.CHECK.getCode().equals(agentPO.getAuditStep())) {
            throw new BaowangDefaultException(ResultCode.APPLY_IS_COMPLATE);
        }
        if (agentPO.getApplyName().equals(adminName) && agentPO.getApplySource().equals(UserOverFlowSourceEnums.SITE_BACKEND.getType())) {
            throw new BaowangDefaultException(ResultCode.WRONG_OPERATION);
        }
        if (!agentPO.getLockName().equals(adminName)) {
            throw new BaowangDefaultException(ResultCode.NOT_CURRENT_LOCK);
        }
        if (LockStatusEnum.UNLOCK.getCode().equals(agentPO.getLockStatus())) {
            throw new BaowangDefaultException(ResultCode.APPLY_UNLOCK);
        }
        AgentInfoPO agentInfo = agentInfoService.getOne(Wrappers.<AgentInfoPO>lambdaQuery()
                .eq(AgentInfoPO::getSiteCode, agentPO.getSiteCode())
                .eq(AgentInfoPO::getAgentAccount, agentPO.getTransferAgentName()));
        try {
            LambdaQueryWrapper<AgentUserOverflowPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AgentUserOverflowPO::getId, vo.getId());
            agentPO.setAuditDatetime(System.currentTimeMillis());
            agentPO.setAuditRemark(vo.getAuditRemark());
            agentPO.setAuditStatus(vo.getAuditStatus());
            agentPO.setAuditName(adminName);
            //锁单人名称置空
            agentPO.setLockName(StringUtils.EMPTY);
            //锁单状态还原为未锁
            agentPO.setLockStatus(LockStatusEnum.UNLOCK.getCode());
            agentPO.setAuditStep(ReviewOperationEnum.CHECK.getCode());//结单查看
            this.update(agentPO, queryWrapper);
            //审核通过
            if (ReviewStatusEnum.REVIEW_PASS.getCode().equals(vo.getAuditStatus())) {
                //更新会员上级代理信息,发送消息通知
                socketService.sendAgentDepositWithdrawSocket(SystemMessageEnum.AGENT_MEMBER_OVERFLOW_SUCCESS,
                        agentInfo.getSiteCode(), agentPO.getTransferAgentId(), agentPO.getMemberName(),
                        WSSubscribeEnum.USER_OVER_FLOW.getTopic());

                userInfoApi.updateAgentTransferInfoBySiteCode(agentPO.getSiteCode(), agentPO.getMemberName(), agentInfo.getAgentId(), agentInfo.getAgentAccount(), false);
            }
            return ResponseVO.success(true);
        } catch (Exception e) {
            log.error("MemberTransferAuth audit error", e);
        }
        return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
    }


    /**
     * 查询待处理条数
     *
     * @return Long
     */
    public UserAccountUpdateVO queryPendingCount() {
        UserAccountUpdateVO vo = new UserAccountUpdateVO();
        Long count = this.lambdaQuery()
                .eq(AgentUserOverflowPO::getAuditStep, ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode())
                .count();
        vo.setNum(count);
        vo.setCountType("3");
        vo.setRouter("/Agent/AgentReview/MemberOverfloReview");
        return vo;
    }

    /**
     * 统计待审核记录数
     *
     * @param siteCode 站点code
     * @return 条数和路由
     */
    public UserAccountUpdateVO queryPendingCountBySiteCode(String siteCode) {
        UserAccountUpdateVO vo = new UserAccountUpdateVO();
        Long count = this.lambdaQuery()
                .eq(AgentUserOverflowPO::getSiteCode, siteCode)
                .eq(AgentUserOverflowPO::getAuditStep, ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode())
                .count();
        vo.setCountType("3");
        vo.setNum(count);
        vo.setRouter("/Agent/AgentReview/MemberOverfloReview");
        return vo;
    }

    /**
     * 调线申请使用，用途暂时未知
     *
     * @param vo 查询对象
     * @return 分页
     */
    public ResponseVO<Page<MemberOverflowClientPageResVO>> clientListPage(MemberOverflowClientPageReqVO vo) {
        long startTime = vo.getApplyTimeStart();
        long endTime = vo.getApplyTimeEnd();
        if (startTime > endTime) {
            throw new BaowangDefaultException(ResultCode.TIME_NOT_GOOD);
        }
        if (DateUtils.checkTime(startTime, endTime)) {
            throw new BaowangDefaultException(ResultCode.FORTY_DAY_OVER);
        }
        LambdaQueryWrapper<AgentUserOverflowPO> queryWrapper = new LambdaQueryWrapper<>();
        //拼接siteCode
        queryWrapper.eq(AgentUserOverflowPO::getSiteCode, vo.getSiteCode());
        queryWrapper.ge(AgentUserOverflowPO::getCreatedTime, startTime);
        queryWrapper.le(AgentUserOverflowPO::getCreatedTime, endTime);
        if (StringUtils.isNotBlank(vo.getAuditStatus())) {
            queryWrapper.eq(AgentUserOverflowPO::getAuditStatus, vo.getAuditStatus());
        }
        queryWrapper.eq(AgentUserOverflowPO::getApplyName, vo.getApplyName());
        if (StringUtils.isNotBlank(vo.getMemberName())) {
            queryWrapper.eq(AgentUserOverflowPO::getMemberName, vo.getMemberName());
        }
        String orderField = vo.getOrderField();
        if (StringUtils.isBlank(orderField)) {
            //为空，默认设置为根据申请时间正排吧
            queryWrapper.orderByAsc(AgentUserOverflowPO::getCreatedTime);
        }
        String orderType = vo.getOrderType();
        //是否根据创建时间排序
        if (StringUtils.isNotBlank(orderField) && "createdTime".equals(orderField)) {
            //不为空，判断一下升序降序
            if ("asc".equals(orderType)) {
                queryWrapper.orderByAsc(AgentUserOverflowPO::getCreatedTime);
            } else {
                queryWrapper.orderByDesc(AgentUserOverflowPO::getCreatedTime);
            }
        }
        //是否根据审核时间排序
        if (StringUtils.isNotBlank(orderField) && "auditDatetime".equals(orderField)) {
            //不为空，判断一下升序降序
            if ("asc".equals(orderType)) {
                queryWrapper.orderByAsc(AgentUserOverflowPO::getAuditDatetime);
            } else {
                queryWrapper.orderByDesc(AgentUserOverflowPO::getAuditDatetime);
            }
        }

        try {
            IPage<AgentUserOverflowPO> pageList = this.page(new Page<>(vo.getPageNumber(), vo.getPageSize()), queryWrapper);
            List<MemberOverflowClientPageResVO> resVOList = ConvertUtil.convertListToList(pageList.getRecords(), new MemberOverflowClientPageResVO());
            Map<String, String> reviewStatus = systemParamApi.getSystemParamMapInner(CommonConstant.USER_REVIEW_REVIEW_STATUS);
            for (MemberOverflowClientPageResVO record : resVOList) {
                List<String> images = Arrays.stream(record.getImage().split(CommonConstant.COMMA)).toList();
                if (!CollectionUtils.isEmpty(images)) {
                    record.setImages(minioFileService.getFileUrlByKeys(images));
                }
                Integer auditStatus = record.getAuditStatus();
                if (auditStatus != null) {
                    record.setAuditStatusName(reviewStatus.get(record.getAuditStatus().toString()));
                }
            }
            Page<MemberOverflowClientPageResVO> pageResult = new Page<>();
            BeanUtils.copyProperties(pageList, pageResult);
            pageResult.setRecords(resVOList);
            return ResponseVO.success(pageResult);
        } catch (Exception e) {
            log.error("调线申请客户端查询异常,req:{}", vo, e);
        }
        return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
    }

    /**
     * 个人中心-调线申请
     *
     * @param vo
     * @return
     */
    public ResponseVO<?> clientApply(AgentUserOverflowClientApplyVO vo) {
        UserInfoVO userInfoByAccount = userInfoApi.getUserInfoByAccount(vo.getMemberName());
        if (userInfoByAccount == null) {
            throw new BaowangDefaultException(ResultCode.AGENT_ACCOUNT_NOT_EXISTS);
        }
        if (StringUtils.isBlank(vo.getLink())) {
            throw new BaowangDefaultException(ResultCode.LINK_CANNOT_BE_EMPTY);
        }
        if (StringUtils.isNotBlank(vo.getLink()) && vo.getLink().length() > 100) {
            throw new BaowangDefaultException(ResultCode.LINK_LENGTH_ERR);
        }
        if (StringUtils.isNotBlank(vo.getApplyRemark()) && vo.getApplyRemark().length() > 50) {
            throw new BaowangDefaultException(ResultCode.REASON_TOO_LONG);

        }
        if (StringUtils.isNotEmpty(userInfoByAccount.getSuperAgentAccount())) {
            throw new BaowangDefaultException(ResultCode.MEMBER_CANNOT_APPLY);

        }
        String timezone = CurrReqUtils.getTimezone();
        if (timezone == null) {
            timezone = "UTC-4";
        }
        if (LocalDateTime.now().minusDays(3).atZone(ZoneId.of(timezone)).toInstant().toEpochMilli() > userInfoByAccount.getRegisterTime()) {
            throw new BaowangDefaultException(ResultCode.MEMBER_REGISTER_OVER_3_DAYS);
        }
        List<Integer> auditStatus = Lists.newArrayList();
        auditStatus.add(ReviewStatusEnum.REVIEW_REJECTED.getCode());
        auditStatus.add(ReviewStatusEnum.REVIEW_PENDING.getCode());
        auditStatus.add(ReviewStatusEnum.REVIEW_PROGRESS.getCode());

        List<AgentUserOverflowPO> oldPO = this.list(Wrappers.<AgentUserOverflowPO>lambdaQuery()
                .eq(AgentUserOverflowPO::getSiteCode, vo.getSiteCode())
                .eq(AgentUserOverflowPO::getMemberName, vo.getMemberName())
                .in(AgentUserOverflowPO::getAuditStatus, auditStatus));
        List<AgentUserOverflowPO> list = oldPO.stream().filter(po -> po.getApplyName().equals(vo.getTransferAgentName())
                && ReviewStatusEnum.REVIEW_REJECTED.getCode().equals(po.getAuditStatus())).toList();
        if (!CollectionUtils.isEmpty(list)) {
            throw new BaowangDefaultException(ResultCode.ALREADY_REJECT);
        }
        List<AgentUserOverflowPO> processList = oldPO.stream().filter(po ->
                Objects.equals(po.getAuditStatus(), Integer.valueOf(AgentUserOverflowAuditStatusEnum.PENDING.getCode()))
                        || Objects.equals(po.getAuditStatus(), Integer.valueOf(AgentUserOverflowAuditStatusEnum.PROCESSING.getCode()))).toList();
        if (!CollectionUtils.isEmpty(processList)) {
            throw new BaowangDefaultException(ResultCode.DEALING);
        }
        AgentInfoPO agentInfo = agentInfoService.getOne(Wrappers.<AgentInfoPO>lambdaQuery().eq(AgentInfoPO::getAgentAccount, vo.getTransferAgentName()).eq(AgentInfoPO::getSiteCode, CurrReqUtils.getSiteCode()));
        if (agentInfo.getRegisterTime() > userInfoByAccount.getRegisterTime()) {
            throw new BaowangDefaultException(ResultCode.REGISTER_TIME_EARLY_AGENT_TIME);
        }
        Integer agentType = agentInfo.getAgentType();
        String accountType = userInfoByAccount.getAccountType();
        try {
            String imageStr = "";
            List<String> image = vo.getImage();
            if (CollectionUtil.isNotEmpty(image)) {
                imageStr = String.join(CommonConstant.COMMA, image);
            }
            String orderNo = AgentServerUtil.getAgentReviewOrderNo();
            AgentUserOverflowPO agentUserOverflowPO = AgentUserOverflowPO.builder()
                    .applySource(vo.getApplySource())
                    .transferAgentName(vo.getTransferAgentName())
                    .transferAgentId(agentInfo.getAgentId())
                    .applyRemark(vo.getApplyRemark())
                    .memberName(userInfoByAccount.getUserAccount())
                    .userId(userInfoByAccount.getUserId())
                    .applyName(vo.getTransferAgentName())
                    .accountType(Integer.parseInt(accountType))
                    .agentType(agentType)
                    .link(vo.getLink())
                    .image(imageStr)
                    .eventId(orderNo)
                    .auditStep(Integer.valueOf(AgentUserOverflowAuditStepEnum.FIRST_REVIEW.getCode())).build();
            agentUserOverflowPO.setCreatedTime(System.currentTimeMillis());
            agentUserOverflowPO.setSiteCode(vo.getSiteCode());
            super.save(agentUserOverflowPO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error("AgentUserOverflowClientApplyVO error", e);
        }
        return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
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

    public Long getTotal(MemberOverflowReviewPageReqVO vo) {
        String siteCode = vo.getSiteCode();
        Page<AgentUserOverflowPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<AgentUserOverflowPO> query = Wrappers.lambdaQuery();
        query.eq(AgentUserOverflowPO::getSiteCode, siteCode);
        query.eq(AgentUserOverflowPO::getAuditStep, vo.getAuditStep());
        //申请开始时间
        Long applyTimeStart = vo.getApplyTimeStart();
        if (applyTimeStart != null) {
            query.ge(AgentUserOverflowPO::getCreatedTime, applyTimeStart);
        }
        //申请结束时间
        Long applyTimeEnd = vo.getApplyTimeEnd();
        if (applyTimeEnd != null) {
            query.le(AgentUserOverflowPO::getCreatedTime, applyTimeEnd);
        }
        //审核开始时间
        Long auditTimeStart = vo.getAuditTimeStart();
        if (auditTimeStart != null) {
            query.ge(AgentUserOverflowPO::getAuditDatetime, auditTimeStart);
        }
        //审核结束时间
        Long auditTimeEnd = vo.getAuditTimeEnd();
        if (auditTimeEnd != null) {
            query.le(AgentUserOverflowPO::getAuditDatetime, auditTimeEnd);
        }
        //转入代理账号
        String transferAgentName = vo.getTransferAgentName();
        if (StringUtils.isNotBlank(transferAgentName)) {
            query.eq(AgentUserOverflowPO::getTransferAgentName, transferAgentName);
        }
        //审核单号
        String eventId = vo.getEventId();
        if (StringUtils.isNotBlank(eventId)) {
            query.eq(AgentUserOverflowPO::getEventId, eventId);
        }
        //审核操作
        Integer auditStep = vo.getAuditStep();
        if (auditStep != null) {
            query.eq(AgentUserOverflowPO::getAuditStep, auditStep);
        }
        //审核状态
        Integer auditStatus = vo.getAuditStatus();
        if (auditStatus != null) {
            query.eq(AgentUserOverflowPO::getAuditStatus, auditStatus);
        }
        //锁单状态
        Integer lockStatus = vo.getLockStatus();
        if (lockStatus != null) {
            query.eq(AgentUserOverflowPO::getLockStatus, lockStatus);
        }
        //申请人
        String applyName = vo.getApplyName();
        if (StringUtils.isNotBlank(applyName)) {
            query.eq(AgentUserOverflowPO::getApplyName, applyName);
        }
        //审核人
        String auditName = vo.getAuditName();
        if (StringUtils.isNotBlank(auditName)) {
            query.eq(AgentUserOverflowPO::getAuditName, auditName);
        }
        //会员账号
        String memberName = vo.getMemberName();
        if (StringUtils.isNotBlank(memberName)) {
            query.eq(AgentUserOverflowPO::getMemberName, memberName);
        }
        return repository.selectCount(query);
    }

    public List<MemberOverflowReviewPageResVO> getUserOverflowByAccount(MemberOverflowReviewPageReqVO memberOverflowReviewPageReqVO) {
        LambdaQueryWrapper<AgentUserOverflowPO> query = Wrappers.lambdaQuery();
        query.eq(AgentUserOverflowPO::getSiteCode, memberOverflowReviewPageReqVO.getSiteCode())
                .eq(AgentUserOverflowPO::getAuditStatus, ReviewStatusEnum.REVIEW_PASS)
                .ge(AgentUserOverflowPO::getAuditDatetime, memberOverflowReviewPageReqVO.getAuditTimeStart())
                .le(AgentUserOverflowPO::getAuditDatetime, memberOverflowReviewPageReqVO.getAuditTimeEnd())
                .in(AgentUserOverflowPO::getMemberName, memberOverflowReviewPageReqVO.getUserAccounts());
        List<AgentUserOverflowPO> list = this.list(query);
        return BeanUtil.copyToList(list, MemberOverflowReviewPageResVO.class);
    }

    public Page<MemberOverflowReviewPageResVO> listByAuditTime(MemberOverflowReviewPageReqVO memberOverflowReviewPageReqVO) {
        Page<AgentUserOverflowPO> pageParam = new Page<>(memberOverflowReviewPageReqVO.getPageNumber(), memberOverflowReviewPageReqVO.getPageSize());
        LambdaQueryWrapper<AgentUserOverflowPO> query = Wrappers.lambdaQuery();
        query.eq(AgentUserOverflowPO::getSiteCode, memberOverflowReviewPageReqVO.getSiteCode());
        query.eq(AgentUserOverflowPO::getAuditStatus, ReviewStatusEnum.REVIEW_PASS.getCode());
        if (memberOverflowReviewPageReqVO.getAuditTimeStart() != null) {
            query.ge(AgentUserOverflowPO::getAuditDatetime, memberOverflowReviewPageReqVO.getAuditTimeStart());
        }

        if (memberOverflowReviewPageReqVO.getAuditTimeEnd() != null) {
            query.le(AgentUserOverflowPO::getAuditDatetime, memberOverflowReviewPageReqVO.getAuditTimeEnd());
        }
        if (org.springframework.util.StringUtils.hasText(memberOverflowReviewPageReqVO.getMemberName())) {
            query.eq(AgentUserOverflowPO::getMemberName, memberOverflowReviewPageReqVO.getMemberName());
        }
        Page<AgentUserOverflowPO> agentUserOverflowPOPage = this.page(pageParam, query);
        Page<MemberOverflowReviewPageResVO> resultPage = new Page<>(memberOverflowReviewPageReqVO.getPageNumber(), memberOverflowReviewPageReqVO.getPageSize());
        BeanUtils.copyProperties(agentUserOverflowPOPage, resultPage);
        ;
        List<MemberOverflowReviewPageResVO> resultLists = BeanUtil.copyToList(agentUserOverflowPOPage.getRecords(), MemberOverflowReviewPageResVO.class);
        resultPage.setRecords(resultLists);
        return resultPage;
    }

}