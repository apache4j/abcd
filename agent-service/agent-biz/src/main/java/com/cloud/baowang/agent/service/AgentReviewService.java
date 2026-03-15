package com.cloud.baowang.agent.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentCategoryEnum;
import com.cloud.baowang.agent.api.enums.AgentLevelEnum;
import com.cloud.baowang.agent.api.enums.AgentStatusEnum;
import com.cloud.baowang.agent.api.enums.RegisterWayEnum;
import com.cloud.baowang.agent.api.vo.StatusVO;
import com.cloud.baowang.agent.api.vo.agentRegister.AgentRegisterRecordInsertVO;
import com.cloud.baowang.agent.api.vo.agentreview.*;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantVO;
import com.cloud.baowang.agent.po.*;
import com.cloud.baowang.agent.repositories.*;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.IPRespVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.utils.IpAPICoUtils;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代理审核表 服务类
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Slf4j
@Service
public class AgentReviewService extends ServiceImpl<AgentReviewRepository, AgentReviewPO> {
    @Autowired
    private AgentInfoRepository agentInfoRepository;
    @Autowired
    private AgentInfoRelationService agentInfoRelationService;
    @Autowired
    private AgentReviewRepository agentReviewRepository;
    @Autowired
    @Lazy
    private AgentInfoModifyReviewService agentInfoModifyReviewService;

    @Autowired
    private AgentHomeAllButtonEntranceService agentHomeAllButtonEntranceService;

    @Autowired
    private AgentRegisterRecordService agentRegisterRecordService;

    @Lazy
    @Autowired
    private UserTransferAgentService userTransferAgentService;
    @Lazy
    @Autowired
    private AgentUserOverflowService agentUserOverflowService;

    @Autowired
    private AgentCommissionPlanRepository commissionPlanRepository;

    @Autowired
    private AgentMerchantService merchantService;

    @Autowired
    private AgentMerchantReviewRecordRepository reviewRecordRepository;
    @Autowired
    private AgentMerchantModifyReviewRepository merchantModifyReviewRepository;

    @Autowired
    private AgentClosureService agentClosureService;

    @Autowired
    private SiteApi siteApi;

    /**
     * 根据agentAccount查询，排除一审拒绝
     *
     * @param siteCode     站点编号
     * @param agentAccount 站点编号
     * @return
     */
    public AgentReviewPO getByAgentAccount(String siteCode, String agentAccount) {
        return agentReviewRepository.getByAgentAccount(siteCode, agentAccount);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<String> addGeneralAgent(AddGeneralAgentVO vo) {
        String adminName = vo.getAdminName();
        String siteCode = vo.getSiteCode();
        // 注册不区分大小写(保存用户原始输入的)，登录区分
        // 获取参数
        Integer maxLevel = vo.getMaxLevel() == null ? CommonConstant.business_four : vo.getMaxLevel();
        Integer agentCategory = vo.getAgentCategory();
        Integer agentType = vo.getAgentType();
        Integer agentAttribution = vo.getAgentAttribution();
        String agentAccount = vo.getAgentAccount();
        String agentPassword = vo.getAgentPassword();
        String whiteList = vo.getWhiteList();
        String remark = vo.getRemark();

        // 代理类别校验:如果是流量代理，那么"代理线层级上限"只能是1
        if (AgentCategoryEnum.FLOW_AGENT.getCode().equals(agentCategory)) {
            maxLevel = CommonConstant.business_one;
            // 流量代理
          /* if (!maxLevel.equals(CommonConstant.business_one)) {
               return ResponseVO.fail(ResultCode.AGENT_CATEGORY_ERROR);
           }*/
            // IP白名单校验
            if (StrUtil.isEmpty(whiteList)) {
                return ResponseVO.fail(ResultCode.AGENT_CATEGORY_WHITE_LIST_ERROR);
            } else {
                // IP格式校验
                String[] whiteLists = whiteList.split(",");
                for (String white : whiteLists) {
                    if (UserChecker.checkIp(white)) {
                        return ResponseVO.fail(ResultCode.AGENT_CATEGORY_WHITE_LIST_STYLE_ERROR);
                    }
                }
            }
        }

        // 代理账号校验
        if (UserChecker.checkUserAccount(agentAccount)) {
            return ResponseVO.fail(ResultCode.USER_ACCOUNT_ERROR);
        }

        // 代理账号 是否已经存在
        if (null != this.getByAgentAccount(siteCode, agentAccount) || null != this.getAgentInfoByAgentAccount(siteCode, agentAccount)) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_REPEAT_ERROR);
        }

        // 密码校验
        if (UserChecker.checkPassword(agentPassword)) {
            return ResponseVO.fail(ResultCode.USER_PASSWORD_ERROR);
        }

        // 代理账号和登录密码 不能一致
        if (agentAccount.equals(agentPassword)) {
            return ResponseVO.fail(ResultCode.AGENT_PASSWORD_SAME);
        }

        AgentMerchantVO agentMerchantVO = merchantService.getAdminByMerchantAccountAndSite(vo.getMerchantAccount(), vo.getSiteCode());
        if (agentMerchantVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_MERCHANT_NOT_EXISTS);
        }
        if (!agentMerchantVO.getMerchantName().equals(vo.getMerchantName())) {
            return ResponseVO.fail(ResultCode.AGENT_MERCHANT_NOT_MATCH);
        }
        // 保存到代理审核表
        AgentReviewPO agentReviewPO = new AgentReviewPO();
        agentReviewPO.setParentId(null);
        agentReviewPO.setLevel(CommonConstant.business_one);
        agentReviewPO.setMaxLevel(maxLevel);
        String upAgentAccount = vo.getUpAgentAccount();
        if (StringUtils.hasText(upAgentAccount)) {
            // 分配直属上级 校验
            AgentInfoPO upAgentInfo = agentInfoRepository.selectOne(
                    new LambdaQueryWrapper<AgentInfoPO>()
                            .eq(AgentInfoPO::getSiteCode, siteCode)
                            .eq(AgentInfoPO::getAgentAccount, upAgentAccount)
            );
            if (null == upAgentInfo) {
                return ResponseVO.fail(ResultCode.UP_AGENT_ACCOUNT_ERROR);
            }

            // 获取站点抽成方案
            SiteVO siteInfo = getSiteDetail(siteCode);
            // 0:负盈利 1:有效流水
            if (siteInfo != null && siteInfo.getCommissionPlan() == 1) {
                // 抽成方案为有效流水时，最大支持层级为50级
                if (upAgentInfo.getLevel().equals(CommonConstant.business_fifty)) {
                    return ResponseVO.fail(ResultCode.AGENT_EXCEED_THE_MAX_LEVEL);
                }
            } else {
                // 非有效流水方案， 保持原逻辑
                if (upAgentInfo.getLevel().equals(CommonConstant.business_four)) {
                    // 4 三代 不能创建下级
                    return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT);
                }
            }

            if (AgentCategoryEnum.FLOW_AGENT.getCode().equals(upAgentInfo.getAgentCategory())) {
                return ResponseVO.fail(ResultCode.AGENT_FLOW_SUB_ERROR);
            }

            agentReviewPO.setParentId(upAgentInfo.getAgentId());
            agentReviewPO.setParentAccount(upAgentInfo.getAgentAccount());
            agentReviewPO.setLevel(upAgentInfo.getLevel() + 1);

            if (!agentType.equals(upAgentInfo.getAgentType())) {
                log.info("当前代理和上级代理 代理类型不一致");
                return ResponseVO.fail(ResultCode.AGENT_NOT_MATCH_SUPER);
            }
            if (!agentCategory.equals(upAgentInfo.getAgentCategory())) {
                log.info("当前代理和上级代理 代理类别不一致");
                return ResponseVO.fail(ResultCode.AGENT_NOT_MATCH_SUPER);
            }

            if (!vo.getPlanCode().equals(upAgentInfo.getPlanCode())) {
                log.info("当前代理和上级代理 佣金方案不一致");
                return ResponseVO.fail(ResultCode.AGENT_NOT_MATCH_SUPER);
            }

            if (!vo.getUserBenefit().equals(upAgentInfo.getUserBenefit())) {
                log.info("当前代理和上级代理 会员福利不一致");
                return ResponseVO.fail(ResultCode.AGENT_NOT_MATCH_SUPER);
            }


        }
        agentReviewPO.setAgentId(SnowFlakeUtils.getCommonRandomId());
        agentReviewPO.setAgentAccount(agentAccount);
        agentReviewPO.setAgentPassword(agentPassword);
        agentReviewPO.setAgentType(agentType);
        agentReviewPO.setAgentAttribution(agentAttribution);
        agentReviewPO.setAgentCategory(agentCategory);
        agentReviewPO.setAgentWhiteList(whiteList);

        agentReviewPO.setReviewOrderNo("A" + SnowFlakeUtils.getSnowId());
        agentReviewPO.setApplyInfo(remark);
        agentReviewPO.setApplyTime(System.currentTimeMillis());
        agentReviewPO.setApplicant(adminName);

        agentReviewPO.setReviewOperation(ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
        agentReviewPO.setReviewStatus(ReviewStatusEnum.REVIEW_PENDING.getCode());
        agentReviewPO.setLockStatus(LockStatusEnum.UNLOCK.getCode());

        agentReviewPO.setPlanCode(vo.getPlanCode());
        agentReviewPO.setUserBenefit(vo.getUserBenefit());
        //商务账号、商务名称
        agentReviewPO.setMerchantAccount(vo.getMerchantAccount());
        agentReviewPO.setMerchantName(vo.getMerchantName());

        agentReviewPO.setSiteCode(vo.getSiteCode());
        agentReviewPO.setCreatedTime(System.currentTimeMillis());
        agentReviewPO.setUpdatedTime(System.currentTimeMillis());
        this.save(agentReviewPO);

        return ResponseVO.success(agentReviewPO.getId());
    }

    /**
     * 查询站点信息
     *
     * @param siteCode 站点code
     * @return SiteVO 站点信息
     */
    private SiteVO getSiteDetail(String siteCode) {
        try {
            // 查询站点API
            ResponseVO<SiteVO> siteInfo = siteApi.getSiteInfo(siteCode);
            if (siteInfo.isOk() && siteInfo.getData() != null) {
                return siteInfo.getData();
            }
        } catch (Exception e) {
            log.error("新增代理-->getSiteDetail-->查询站点api异常， siteCode:{}", siteCode, e);
        }
        return null;
    }

    /**
     * 根据agentAccount 查询代理
     *
     * @param siteCode     站点编号
     * @param agentAccount 代理账号
     * @return
     */
    public AgentInfoPO getAgentInfoByAgentAccount(String siteCode, String agentAccount) {
        return agentInfoRepository.findAgentInfoNotCase(siteCode, agentAccount);
    }

    public ResponseVO lock(StatusVO vo, String adminName) {
        // 获取参数
        String id = vo.getId();
        AgentReviewPO agentReview = this.getById(id);
        if (null == agentReview) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        try {
            // 业务操作
            return lockOperate(vo, agentReview, adminName);
        } catch (Exception e) {
            log.error("新增代理审核-锁单/解锁error,审核单号:{},操作人:{}", agentReview.getReviewOrderNo(), adminName, e);
            return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
        }
    }

    private ResponseVO lockOperate(StatusVO vo, AgentReviewPO agentReview, String adminName) {
        Integer myLockStatus;
        Integer myReviewStatus;
        String locker;
        // 锁单状态 0未锁 1已锁
        if (LockStatusEnum.LOCK.getCode().equals(vo.getStatus())) {
            // 开始锁单
            if (LockStatusEnum.LOCK.getCode().equals(agentReview.getLockStatus())) {
                return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
            }
            // 审核操作 1一审审核 2结单查看
            if (ReviewOperationEnum.CHECK.getCode().equals(agentReview.getReviewOperation())) {
                return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
            }
            myLockStatus = LockStatusEnum.LOCK.getCode();
            myReviewStatus = ReviewStatusEnum.REVIEW_PROGRESS.getCode();
            locker = adminName;
        } else {
            // 开始解锁
            myLockStatus = LockStatusEnum.UNLOCK.getCode();
            myReviewStatus = ReviewStatusEnum.REVIEW_PENDING.getCode();
            locker = null;
        }

        LambdaUpdateWrapper<AgentReviewPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(AgentReviewPO::getId, vo.getId())
                .set(AgentReviewPO::getLockStatus, myLockStatus)
                .set(AgentReviewPO::getLocker, locker)
                .set(AgentReviewPO::getReviewStatus, myReviewStatus)
                .set(AgentReviewPO::getUpdater, adminName)
                .set(AgentReviewPO::getUpdatedTime, System.currentTimeMillis());
        this.update(null, lambdaUpdate);
        return ResponseVO.success();
    }

    @Transactional(rollbackFor = Throwable.class)
    public ResponseVO reviewSuccess(ReviewVO vo, String registerIp, String adminId, String adminName) {
        // 获取参数
        String id = vo.getId();
        String reviewRemark = vo.getReviewRemark();

        AgentReviewPO agentReview = this.getById(id);
        if (null == agentReview) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        // 审核状态 1待处理 2处理中 3审核通过 4一审拒绝
        if (!ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(agentReview.getReviewStatus())) {
            return ResponseVO.fail(ResultCode.REVIEW_STATUS_ERROR);
        }

        LambdaUpdateWrapper<AgentReviewPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(AgentReviewPO::getId, id)
                .set(AgentReviewPO::getOneReviewFinishTime, System.currentTimeMillis())
                .set(AgentReviewPO::getReviewer, adminName)
                .set(AgentReviewPO::getReviewRemark, reviewRemark)

                .set(AgentReviewPO::getReviewOperation, ReviewOperationEnum.CHECK.getCode())
                .set(AgentReviewPO::getReviewStatus, ReviewStatusEnum.REVIEW_PASS.getCode())
                .set(AgentReviewPO::getLockStatus, LockStatusEnum.UNLOCK.getCode())
                .set(AgentReviewPO::getLocker, null)

                .set(AgentReviewPO::getUpdater, adminId)
                .set(AgentReviewPO::getUpdatedTime, System.currentTimeMillis());
        this.update(null, lambdaUpdate);

        // 插入agent_info表
        AgentInfoPO po = new AgentInfoPO();
        po.setAvatarCode("avatar_0");
        po.setParentId(agentReview.getParentId());
        po.setParentAccount(agentReview.getParentAccount());
        po.setAgentId(agentReview.getAgentId());
        po.setLevel(agentReview.getLevel());
        po.setMaxLevel(agentReview.getMaxLevel());
        po.setAgentAccount(agentReview.getAgentAccount());
        // 生成15位加密盐
        String salt = MD5Util.randomGen();
        po.setSalt(salt);
        // 密码加密
        String encryptPassword = AgentServerUtil.getEncryptPassword(agentReview.getAgentPassword(), salt);
        po.setAgentPassword(encryptPassword);
        po.setAgentType(agentReview.getAgentType());
        po.setStatus(AgentStatusEnum.NORMAL.getCode());
        // 入口权限 默认开启
        po.setEntrancePerm(CommonConstant.business_one);
        po.setRegisterWay(RegisterWayEnum.MANUAL.getCode());
        po.setRemoveRechargeLimit(CommonConstant.business_zero);
        po.setRegisterDeviceType(DeviceType.Home.getCode());
        po.setRegisterTime(System.currentTimeMillis());
        po.setRegisterIp(registerIp);
        po.setInviteCode(MD5Util.random7Gen());
        po.setIsAgentArrears(CommonConstant.business_zero);
        po.setRemark(agentReview.getApplyInfo());

        po.setPlanCode(agentReview.getPlanCode());

        po.setCurrentPlanCode(agentReview.getPlanCode());
        po.setUserBenefit(agentReview.getUserBenefit());
        po.setCreator(adminId);
        po.setUpdater(adminId);
        po.setCreatedTime(System.currentTimeMillis());
        po.setUpdatedTime(System.currentTimeMillis());
        // 1:PC端 2:H5端
        po.setHomeButtonEntrance(agentHomeAllButtonEntranceService.getAllButtonEntranceByType(CommonConstant.business_one));
        po.setHomeButtonEntranceH5(agentHomeAllButtonEntranceService.getAllButtonEntranceByType(CommonConstant.business_two));
        po.setAgentAttribution(agentReview.getAgentAttribution());
        po.setAgentCategory(agentReview.getAgentCategory());
        po.setAgentWhiteList(agentReview.getAgentWhiteList());
        // AES密钥(只有流量代理需要)，Base64编码的字符串
        if (AgentCategoryEnum.FLOW_AGENT.getCode().equals(agentReview.getAgentCategory())) {
            po.setAesSecretKey(AESCBCUtil.generateKeyBase64());
        }
        //商务账号、商务名称
        po.setMerchantAccount(agentReview.getMerchantAccount());
        po.setMerchantName(agentReview.getMerchantName());

        po.setSiteCode(vo.getSiteCode());
        agentInfoRepository.insert(po);

        // 再更新AgentInfoPO的path字段
        AgentInfoPO agentInfoPO = agentInfoRepository.selectByAgentId(po.getAgentId());
        if (null == agentInfoPO.getParentId()) {
            agentInfoPO.setPath(po.getAgentId());
            agentInfoRepository.updateById(agentInfoPO);
            //记录到代理上下级关系表
            agentInfoRelationService.insertRelation(po.getSiteCode(), po.getAgentId(), null);
        } else {
            // 上级代理
            AgentInfoPO upAgentInfoPO = agentInfoRepository.selectByAgentId(agentInfoPO.getParentId());
            agentInfoPO.setPath(upAgentInfoPO.getPath() + "," + po.getAgentId());
            agentInfoRepository.updateById(agentInfoPO);
            //记录到代理上下级关系表
            agentInfoRelationService.insertRelation(po.getSiteCode(), po.getAgentId(), po.getParentId());
        }

        // 代理注册记录
        AgentRegisterRecordInsertVO registerRecord = new AgentRegisterRecordInsertVO();
        registerRecord.setAgentType(agentReview.getAgentType());
        registerRecord.setAgentId(agentReview.getAgentId());
        registerRecord.setAgentAccount(agentReview.getAgentAccount());
        registerRecord.setRegisterIp(registerIp);
        try {
            //IPInfo ipInfo = IPInfoUtils.getIpInfo(registerIp);
            IPRespVO ipApiVO = IpAPICoUtils.getIp(registerIp);
            if (ipApiVO != null) {
                registerRecord.setIpAttribution(ipApiVO.getAddress());
            }
        } catch (Exception e) {
            log.info("根据ip地址:{}获取位置error", registerIp);
        }
        registerRecord.setRegisterDevice(DeviceType.Home.getCode());
        registerRecord.setRegisterTime(System.currentTimeMillis());
        registerRecord.setRegistrant(agentReview.getApplicant());
        registerRecord.setSiteCode(vo.getSiteCode());
        agentRegisterRecordService.recordAgentRegister(registerRecord);

        //全民代
        try {
            agentInfoRelationService.insertRelation(po.getSiteCode(), po.getAgentId(), po.getParentId());
        } catch (Exception e) {
            log.info("全民代节点插入失败:{}", e.getMessage());
        }

        return ResponseVO.success();
    }

    public ResponseVO reviewFail(ReviewVO vo, String adminId, String adminName) {
        // 获取参数
        String id = vo.getId();
        String reviewRemark = vo.getReviewRemark();

        AgentReviewPO agentReview = this.getById(id);
        if (null == agentReview) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        // 审核状态 1待处理 2处理中 3审核通过 4一审拒绝
        if (!ReviewStatusEnum.REVIEW_PROGRESS.getCode().equals(agentReview.getReviewStatus())) {
            return ResponseVO.fail(ResultCode.REVIEW_STATUS_ERROR);
        }

        LambdaUpdateWrapper<AgentReviewPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(AgentReviewPO::getId, id)
                .set(AgentReviewPO::getOneReviewFinishTime, System.currentTimeMillis())
                .set(AgentReviewPO::getReviewer, adminName)
                .set(AgentReviewPO::getReviewRemark, reviewRemark)

                .set(AgentReviewPO::getReviewOperation, ReviewOperationEnum.CHECK.getCode())
                .set(AgentReviewPO::getReviewStatus, ReviewStatusEnum.REVIEW_REJECTED.getCode())
                .set(AgentReviewPO::getLockStatus, LockStatusEnum.UNLOCK.getCode())
                .set(AgentReviewPO::getLocker, null)

                .set(AgentReviewPO::getUpdater, adminId)
                .set(AgentReviewPO::getUpdatedTime, System.currentTimeMillis());
        this.update(null, lambdaUpdate);

        return ResponseVO.success();
    }

    public ResponseVO<Page<AgentReviewResponseVO>> getReviewPage(AgentReviewPageVO vo, String adminName) {

        Page<AgentReviewResponseVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());

        Page<AgentReviewResponseVO> pageResult = agentReviewRepository.getReviewPage(page, vo, adminName);

        for (AgentReviewResponseVO record : pageResult.getRecords()) {


            // 锁单人是否当前登录人 0否 1是
            // 前端先判断locker，再判断isLocker
            if (StrUtil.isNotEmpty(record.getLocker())) {
                if (record.getLocker().equals(adminName)) {
                    record.setIsLocker(Integer.parseInt(YesOrNoEnum.YES.getCode()));
                } else {
                    record.setIsLocker(Integer.parseInt(YesOrNoEnum.NO.getCode()));
                }
            }

            // 申请人是否当前登录人 0否 1是
            if (StrUtil.isNotEmpty(record.getApplicant())) {
                if (record.getApplicant().equals(adminName)) {
                    record.setIsApplicant(Integer.parseInt(YesOrNoEnum.YES.getCode()));
                } else {
                    record.setIsApplicant(Integer.parseInt(YesOrNoEnum.NO.getCode()));
                }
            }
            //级别名称
            record.setLevelName(AgentLevelEnum.parseName(record.getLevel()));
        }
        return ResponseVO.success(pageResult);
    }

    public ResponseVO<AgentReviewDetailsVO> getReviewDetails(IdVO vo) {
        AgentReviewPO agentReview = this.getById(vo.getId());
        if (null == agentReview) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        AgentReviewDetailsVO result = ConvertUtil.entityToModel(agentReview, AgentReviewDetailsVO.class);

        String planCode = result.getPlanCode();
        if (StrUtil.isNotBlank(planCode)) {
            //佣金方案
            LambdaQueryWrapper<AgentCommissionPlanPO> planQuery = Wrappers.lambdaQuery();
            planQuery.eq(AgentCommissionPlanPO::getSiteCode, result.getSiteCode()).eq(AgentCommissionPlanPO::getPlanCode, planCode);
            AgentCommissionPlanPO planPO = commissionPlanRepository.selectOne(planQuery);
            if (planPO != null) {
                result.setPlanCodeName(planPO.getPlanName());
            }
        }

        //级别名称
        result.setLevelName(AgentLevelEnum.parseName(result.getLevel()));

        return ResponseVO.success(result);
    }


    /**
     * 新增代理审核 未审核数量
     *
     * @param siteCode
     * @return
     */
    public Long getNoReviewNum(String siteCode) {
        Long countNum = this.lambdaQuery()
                .eq(AgentReviewPO::getSiteCode, siteCode)
                .eq(AgentReviewPO::getReviewOperation, CommonConstant.business_one)
                .count();
        return countNum;
    }


    /**
     * 查询代理页签下的未审核数量角标
     *
     * @return
     */
    public ResponseVO<List<UserAccountUpdateVO>> getNotReviewNum(String siteCode) {
        List<UserAccountUpdateVO> list = Lists.newArrayList();
        // 新增代理审核-页面
        UserAccountUpdateVO vo = new UserAccountUpdateVO();
        Long count = getNoReviewNum(siteCode);
        vo.setCountType("0");
        vo.setNum(Long.valueOf(count.toString()));
        vo.setRouter("/Agent/AgentReview/AddAgentReview");
        list.add(vo);
        // 代理账户修改审核-页面
        list.add(agentInfoModifyReviewService.findProcessingDataCount(siteCode));
        // 会员转代审核-页面
        //统计未审核条数时，需要带上siteCode 2024-08-20 10:50:00  by：aomiao
        list.add(userTransferAgentService.queryPendingCountBySiteCode(siteCode));
        // 会员溢出审核-页面
        //统计未审核条数时，需要带上siteCode 2024-08-20 10:50:00  by：aomiao
        list.add(agentUserOverflowService.queryPendingCountBySiteCode(siteCode));
        //新增商务审核
        LambdaQueryWrapper<AgentMerchantReviewRecordPO> query = Wrappers.lambdaQuery();
        query.eq(AgentMerchantReviewRecordPO::getSiteCode, siteCode).eq(AgentMerchantReviewRecordPO::getReviewOperation, ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
        Long merchantReviewCount = reviewRecordRepository.selectCount(query);
        UserAccountUpdateVO merchantUpdVo = new UserAccountUpdateVO();
        merchantUpdVo.setCountType("4");
        merchantUpdVo.setNum(merchantReviewCount);
        merchantUpdVo.setRouter("/Agent/BusinessManage/AddBusinessReview");
        list.add(merchantUpdVo);
        //商务信息修改审核
        LambdaQueryWrapper<AgentMerchantModifyReviewPO> merchantModifyQuery = Wrappers.lambdaQuery();
        merchantModifyQuery.eq(AgentMerchantModifyReviewPO::getSiteCode, siteCode).eq(AgentMerchantModifyReviewPO::getReviewOperation, ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
        Long modifyCount = merchantModifyReviewRepository.selectCount(merchantModifyQuery);
        UserAccountUpdateVO modifyVO = new UserAccountUpdateVO();
        modifyVO.setCountType("5");
        modifyVO.setNum(modifyCount);
        modifyVO.setRouter("/Agent/BusinessManage/EditBusinessReview");
        list.add(modifyVO);
        return ResponseVO.success(list);
    }


    /**
     * 按照站点获取未审核数量
     *
     * @param siteCode 站点
     * @return
     */
    public ResponseVO<Map<String, Long>> getNotReviewNumMap(String siteCode) {
        Map<String, Long> resultMap = new HashMap<>();
        Long countNum = getNoReviewNum(siteCode);
        resultMap.put("0", countNum);
        UserAccountUpdateVO userAccountUpdateVO = agentInfoModifyReviewService.findProcessingDataCount(siteCode);
        resultMap.put("1", userAccountUpdateVO.getNum());
        UserAccountUpdateVO userAccountUpdateVO1 = userTransferAgentService.queryPendingCountBySiteCode(siteCode);
        resultMap.put("2", userAccountUpdateVO1.getNum());
        UserAccountUpdateVO userAccountUpdateVO2 = agentUserOverflowService.queryPendingCountBySiteCode(siteCode);
        resultMap.put("3", userAccountUpdateVO2.getNum());
        return ResponseVO.success(resultMap);
    }
}
