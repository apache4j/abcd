package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.api.AgentManualUpDownApi;
import com.cloud.baowang.agent.api.enums.*;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentLowerLevelReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentDepositWithdrawStatisticsVO;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginUpdateVO;
import com.cloud.baowang.agent.api.vo.agentRegister.AgentRegisterRecordInsertVO;
import com.cloud.baowang.agent.api.vo.agentinfo.*;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentDetailParam;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoModifyVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoPartVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.agentreview.list.GetAllListVO;
import com.cloud.baowang.agent.api.vo.commission.AgentPlanInfoVO;
import com.cloud.baowang.agent.api.vo.commission.CommissionAgentReqVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelVO;
import com.cloud.baowang.agent.api.vo.remark.AgentRemarkRecordVO;
import com.cloud.baowang.agent.api.vo.site.AgentDataOverviewResVo;
import com.cloud.baowang.agent.po.*;
import com.cloud.baowang.agent.repositories.*;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.core.vo.IPRespVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.utils.IpAPICoUtils;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.GetUserInfoByAgentIdsVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 代理基本信息 服务类
 *
 * @author kimi
 * @since 2024-05-30
 */
@AllArgsConstructor
@Slf4j
@Service
public class AgentInfoService extends ServiceImpl<AgentInfoRepository, AgentInfoPO> {

    private final AgentCommissionPlanRepository commissionPlanRepository;
    private AgentInfoRepository agentInfoRepository;
    private UserInfoApi userInfoApi;
    private RiskApi riskApi;
    private final AgentUserOverflowRepository agentUserOverflowRepository;
    private final AgentInfoModifyReviewRepository agentInfoModifyReviewRepository;
    private final AgentRemarkRecordRepository agentRemarkRecordRepository;
    private final AgentLabelService agentLabelService;
    private final AgentRegisterRecordService agentRegisterRecordService;
    @Lazy
    private final AgentReviewService agentReviewService;
    private final AgentHomeAllButtonEntranceService agentHomeAllButtonEntranceService;

    private final AgentDepositWithdrawService agentDepositWithdrawalService;

    private final AgentManualUpDownApi agentManualUpDownApi;

    private final AgentInfoRelationService agentInfoRelationService;

    private final AgentInfoRelationRepository agentInfoRelationRepository;

    /**
     * 查询agentId的 所有下级代理(包含代理自己)
     * 如果计算数量，需要减1
     *
     * @return List
     */
    public List<GetAllListVO> findAllDownAgentById(String id) {
        if (StrUtil.isEmpty(id)) {
            return Lists.newArrayList();
        }
        return agentInfoRepository.findAllAgentInfoById(id);
    }

    /**
     * 查询agentId的 所有下级代理(包含代理自己)
     * 如果计算数量，需要减1
     *
     * @return List
     */
    public List<GetAllListVO> findAllDownAgentByIdNew(String id, Integer level) {
        if (StrUtil.isEmpty(id)) {
            return Lists.newArrayList();
        }
        if (level == null) {
            AgentInfoVO byIdSelf = agentInfoRepository.findByIdSelf(id);
            level = byIdSelf.getLevel();

        }

        return agentInfoRepository.findAllAgentInfoById(id);
    }

    /**
     * 查询每个agentId的 所有下级代理id(包含代理自己)
     * 如果计算数量，需要减1
     *
     * @return Map
     */
    public Map<String, List<String>> findAllDownAgentIdByIds(List<String> ids, List<GetAllListVO> getAllLists) {
        Map<String, List<String>> result = Maps.newHashMap();
        if (CollUtil.isEmpty(ids)) {
            return result;
        }
        // 这个集合可能有重复的数据，所以需要进一步处理
        //List<GetAllListVO> getAllLists = agentInfoRepository.findAllAgentInfoByIds(ids);

        List<GetAllListVO> newList = Lists.newArrayList();
        List<String> agentIds = Lists.newArrayList();
        for (GetAllListVO getAgent : getAllLists) {
            if (!agentIds.contains(getAgent.getAgentId())) {
                agentIds.add(getAgent.getAgentId());
                newList.add(getAgent);
            }
        }
        for (String id : ids) {
            List<String> list = Lists.newArrayList();
            for (GetAllListVO item : newList) {
                if (item.getPath().contains(id)) {
                    list.add(item.getAgentId());
                }
            }
            result.put(id, list);
        }
        return result;
    }

    /**
     * 查询每个agentId的 所有下级代理数(不包含代理自己)
     * 已经减掉1
     *
     * @return Map
     */
    public Map<String, Long> findAllDownAgentNumberByIds(List<String> agentIds, List<GetAllListVO> getAllLists) {
        Map<String, Long> result = Maps.newHashMap();
        if (CollUtil.isEmpty(agentIds)) {
            return result;
        }
        List<GetAllListVO> newList = Lists.newArrayList();
        List<String> newAgentIds = Lists.newArrayList();
        for (GetAllListVO getAgent : getAllLists) {
            if (!newAgentIds.contains(getAgent.getAgentId())) {
                newAgentIds.add(getAgent.getAgentId());
                newList.add(getAgent);
            }
        }
        for (String agentId : agentIds) {
            long number = 0;
            for (GetAllListVO item : newList) {
                if (item.getPath().contains(agentId)) {
                    number += 1;
                }
            }
            result.put(agentId, 0 == number ? 0 : number - 1);
        }
        return result;
    }


    /**
     * 查询每个agentId的 直属代理人数
     *
     * @return Map
     */
    public Map<String, Long> findDirectAgentNumberByIds(List<String> parentIds) {
        Map<String, Long> result = Maps.newHashMap();
        if (CollUtil.isEmpty(parentIds)) {
            return result;
        }
        List<GetAllListVO> findByParentId = agentInfoRepository.findByParentId(parentIds);
        for (String parentId : parentIds) {
            long number = 0;
            for (GetAllListVO agent : findByParentId) {
                if (parentId.equals(agent.getParentId())) {
                    number += 1;
                }
            }
            result.put(parentId, number);
        }

        return result;
    }

    /**
     * 查询每个agentId的 直属会员人数
     *
     * @return Map
     */
    public Map<String, Long> findDirectUserNumberByIds(List<String> agentIds) {
        Map<String, Long> result = Maps.newHashMap();
        if (CollUtil.isEmpty(agentIds)) {
            return result;
        }
        List<GetUserInfoByAgentIdsVO> userInfoByAgentIds = userInfoApi.getUserInfoByAgentIds(agentIds);
        for (String agentId : agentIds) {
            long number = 0;
            for (GetUserInfoByAgentIdsVO agent : userInfoByAgentIds) {
                if (agentId.equals(agent.getSuperAgentId())) {
                    number += 1;
                }
            }
            result.put(agentId, number);
        }

        return result;
    }

    /**
     * 查询所有代理
     *
     * @return List
     */
    public List<GetAllListVO> getAllList(String siteCode) {
        return agentInfoRepository.getAllList(siteCode);
    }

    public boolean updateAgentAttributionByAgentIds(List<String> agentIds, String agentAttribution) {
        if (CollectionUtils.isEmpty(agentIds)) {
            return false;
        }
        LambdaUpdateWrapper<AgentInfoPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.in(AgentInfoPO::getId, agentIds)
                .set(AgentInfoPO::getAgentAttribution, agentAttribution)
                .set(AgentInfoPO::getUpdatedTime, System.currentTimeMillis());
        return this.update(null, lambdaUpdate);
    }

    public AgentInfoVO getByAgentAccount(String agentAccount) {
        AgentInfoPO one = this.getOne(new LambdaQueryWrapper<AgentInfoPO>()
                .eq(AgentInfoPO::getAgentAccount, agentAccount));
        return ConvertUtil.entityToModel(one, AgentInfoVO.class);
    }

    public AgentInfoVO getByAgentAccountSite(String siteCode, String agentAccount) {
        AgentInfoPO one = this.getOne(new LambdaQueryWrapper<AgentInfoPO>()
                .eq(AgentInfoPO::getSiteCode, siteCode)
                .eq(AgentInfoPO::getAgentAccount, agentAccount));
        return ConvertUtil.entityToModel(one, AgentInfoVO.class);
    }

    public AgentInfoVO getByAgentAccountAndSiteCode(String agentAccount, String siteCode) {
        AgentInfoPO one = this.getOne(new LambdaQueryWrapper<AgentInfoPO>().eq(AgentInfoPO::getAgentAccount, agentAccount).eq(AgentInfoPO::getSiteCode, siteCode));
        return ConvertUtil.entityToModel(one, AgentInfoVO.class);
    }

    public AgentInfoVO getByAgentAccountAndSite(String agentAccount, String siteCode) {
        AgentInfoPO one = this.getOne(new LambdaQueryWrapper<AgentInfoPO>().eq(AgentInfoPO::getAgentAccount, agentAccount)
                .eq(AgentInfoPO::getSiteCode, siteCode));
        return ConvertUtil.entityToModel(one, AgentInfoVO.class);
    }

    public AgentInfoVO getByCurrAgentAccount(String agentAccount) {
        String siteCode = CurrReqUtils.getSiteCode();
        AgentInfoPO one = this.getOne(new LambdaQueryWrapper<AgentInfoPO>()
                .eq(AgentInfoPO::getAgentAccount, agentAccount)
                .eq(AgentInfoPO::getSiteCode, siteCode)

        );
        return ConvertUtil.entityToModel(one, AgentInfoVO.class);
    }

    public List<String> getListByCurrAgentAccount(String siteCode, String agentAccount) {
        return agentInfoRepository.findAgentListByParam(siteCode, agentAccount);
    }

    public List<AgentInfoVO> getByAgentAccounts(List<String> agentAccounts) {
        if (CollectionUtils.isEmpty(agentAccounts)) {
            return Lists.newArrayList();
        }
        List<AgentInfoPO> list = this.list(new LambdaQueryWrapper<AgentInfoPO>().in(AgentInfoPO::getAgentAccount, agentAccounts));
        return ConvertUtil.entityListToModelList(list, AgentInfoVO.class);
    }

    public List<AgentInfoVO> getByAgentAccountsAndSiteCode(List<String> agentAccounts, String siteCode) {
        List<AgentInfoPO> list = this.list(new LambdaQueryWrapper<AgentInfoPO>().eq(AgentInfoPO::getSiteCode, siteCode).in(AgentInfoPO::getAgentAccount, agentAccounts));
        return ConvertUtil.entityListToModelList(list, AgentInfoVO.class);
    }


    public ResponseVO<CheckAesSecretKeyVO> checkAesSecretKey(IdVO vo) {
        AgentInfoPO byId = this.getById(vo.getId());
        if (null == byId) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        // 只有流量代理才可以查看密钥
        if (!AgentCategoryEnum.FLOW_AGENT.getCode().equals(byId.getAgentCategory())) {
            return ResponseVO.fail(ResultCode.FLOW_AGENT_SECRET_KEY_ERROR);
        }

        CheckAesSecretKeyVO result = new CheckAesSecretKeyVO();
        result.setAesSecretKey(byId.getAesSecretKey());
        return ResponseVO.success(result);
    }

    public ResponseVO<?> updateWhitelist(UpdateWhitelistVO vo) {
        AgentInfoPO byId = this.getById(vo.getId());
        if (null == byId) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        // 只有流量代理才可以更新白名单
        if (!AgentCategoryEnum.FLOW_AGENT.getCode().equals(byId.getAgentCategory())) {
            return ResponseVO.fail(ResultCode.FLOW_AGENT_WHITE_LIST_ERROR);
        } else {
            // IP格式校验
            String[] whiteLists = vo.getAgentWhiteList().split(",");
            for (String white : whiteLists) {
                if (UserChecker.checkIp(white)) {
                    return ResponseVO.fail(ResultCode.AGENT_CATEGORY_WHITE_LIST_STYLE_ERROR);
                }
            }
        }

        byId.setAgentWhiteList(vo.getAgentWhiteList());
        this.updateById(byId);

        return ResponseVO.success();
    }


    public ResponseVO<List<AgentListTreeVO>> getAgentTree(String siteCode) {
        // 所有代理
        List<GetAllListVO> list = this.getAllList(siteCode);
        // 总代人数
        Long count = list.stream().filter(item -> StrUtil.isEmpty(item.getParentId())).count();
        AgentListTreeVO players = new AgentListTreeVO();
        players.setAgentId("-1");
        players.setParentId("-2");
        players.setAgentAccount("OKsport");
        players.setDirectDownAgentNum(count);
        players.setAllDownAgentNum((long) list.size());
        List<AgentListTreeVO> agentListTreeVOS = ConvertUtil.entityListToModelList(list, AgentListTreeVO.class);
        for (AgentListTreeVO agentListTreeVO : agentListTreeVOS) {
            if (null == agentListTreeVO.getParentId()) {
                agentListTreeVO.setParentId("-1");
            }
        }
        for (AgentListTreeVO vo : agentListTreeVOS) {
            if (vo.getLevel() != null && !AgentLevelEnum.THREE_AGENT.getCode().equals(vo.getLevel().toString())) {
                // 直属下级代理
                long directDownAgentNum = 0;
                // 所有下级代理
                long allDownAgentNum = 0;
                for (AgentListTreeVO vo1 : agentListTreeVOS) {
                    if (org.springframework.util.StringUtils.hasText(vo.getAgentId()) && vo.getAgentId().equals(vo1.getParentId())) {
                        directDownAgentNum += 1;
                    }

                    if (org.springframework.util.StringUtils.hasText(vo1.getPath()) && org.springframework.util.StringUtils.hasText(vo1.getAgentId()) && vo1.getPath().contains(vo.getAgentId())) {
                        allDownAgentNum += 1;
                    }
                }
                vo.setDirectDownAgentNum(directDownAgentNum);
                vo.setAllDownAgentNum(0 == allDownAgentNum ? 0 : allDownAgentNum - 1);
            }
        }
        agentListTreeVOS.add(players);
        // 集合转权限树
        JSONArray objects = ListToTreeUtils.listToTree(agentListTreeVOS, "agentId", "parentId", "children");
        List<AgentListTreeVO> result = JSONArray.parseArray(JSONArray.toJSONString(objects), AgentListTreeVO.class);
        return ResponseVO.success(result);
    }

    public ResponseVO<AgentInfoResultVO> getAgentPage(AgentInfoPageVO vo) {
        AgentInfoResultVO result = new AgentInfoResultVO();
        if (StrUtil.isNotEmpty(vo.getAgentAccount())) {
            AgentInfoPO one = this.lambdaQuery()
                    .eq(AgentInfoPO::getAgentAccount, vo.getAgentAccount())
                    .eq(AgentInfoPO::getSiteCode, vo.getSiteCode())
                    .one();
            if (null == one) {
                vo.setAgentId("-1");
            } else {
                vo.setAgentId(one.getAgentId());
            }
        }

        Page<AgentInfoResponseVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<AgentInfoResponseVO> pageResult = agentInfoRepository.getAgentPage(page, vo);
        List<AgentInfoResponseVO> records = pageResult.getRecords();
        List<String> agentIds = records.stream().map(AgentInfoResponseVO::getAgentId).collect(Collectors.toList());
        List<String> agentAccounts = records.stream().map(AgentInfoResponseVO::getAgentAccount).collect(Collectors.toList());

        long downAgentNumber = 0;
        long downUserNumber = 0;
        long directAgentNumber = 0;
        long directUserNumber = 0;
        BigDecimal commissionWalletBalance = BigDecimal.ZERO;
        BigDecimal quotaWalletBalance = BigDecimal.ZERO;
        BigDecimal totalDepositAmount = BigDecimal.ZERO;
        int totalDepositTimes = 0;
        BigDecimal totalWithdrawAmount = BigDecimal.ZERO;
        int totalWithdrawTimes = 0;

        // 下级代理人数 Map
        // 这个集合可能有重复的数据，所以需要进一步处理
        List<GetAllListVO> getAllLists = Lists.newArrayList();
        //所有代理 包含下级
        List<String> allAgentIds = agentIds;
        if (!CollectionUtils.isEmpty(agentIds)) {
            getAllLists = agentInfoRepository.findAllAgentInfoByIds(agentIds);
            allAgentIds = getAllLists.stream().map(GetAllListVO::getAgentId).collect(Collectors.toList());
        }
        Map<String, Long> allDownAgentNumber = this.findAllDownAgentNumberByIds(agentIds, getAllLists);
        // 查询每个agentId的 所有下级代理id(包含代理自己)
        Map<String, List<String>> allDownAgentId = this.findAllDownAgentIdByIds(agentIds, getAllLists);
        // 直属代理人数 Map
        Map<String, Long> directAgentNumberByIds = this.findDirectAgentNumberByIds(agentIds);
        // 直属会员人数 Map
        Map<String, Long> directUserNumberByIds = this.findDirectUserNumberByIds(agentIds);
        //下级会员人数 Map 按照agentId 分类汇总 不包含测试会员
        Map<String, Long> userNumByAgentIds = this.findAllDownUserNumberByIds(allAgentIds);
        // 充值提币统计
        Map<String, AgentDepositWithdrawStatisticsVO> agentDepositWithdrawPageMap = agentDepositWithdrawalService.getAgentDepositWithdraws(vo.getSiteCode(), agentAccounts);

        //人工加减额统计
        // Map<String, AgentManualUpRecordResponseVO> agentManualUpRecordResponseVOMap = agentManualUpDownApi.listStaticData(agentIds);

        List<String> riskLevelIds = records.stream().map(o -> o.getRiskLevelId()).toList();
        Map<String, RiskLevelDetailsVO> riskMap = riskApi.getByIds(riskLevelIds);

        List<String> agentLabelIds = Lists.newArrayList();
        for (AgentInfoResponseVO agentInfoResponseVO : records) {
            if (org.springframework.util.StringUtils.hasText(agentInfoResponseVO.getAgentLabelId())) {
                agentLabelIds.addAll(Arrays.stream(agentInfoResponseVO.getAgentLabelId().split(CommonConstant.COMMA)).toList());
            }
      /*      //充值金额
            AgentDepositWithdrawStatisticsVO agentDepositWithdraw = agentDepositWithdrawPageMap.get(agentInfoResponseVO.getAgentAccount());
            //人工加额
            AgentManualUpRecordResponseVO agentManualUpRecordResponseVO = agentManualUpRecordResponseVOMap.get(agentInfoResponseVO.getAgentId().concat(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode().toString()));
            BigDecimal manualUpAmount=agentManualUpRecordResponseVO==null?BigDecimal.ZERO:agentManualUpRecordResponseVO.getAdjustAmount();
            Integer manualUpTimes=agentManualUpRecordResponseVO==null?0:agentManualUpRecordResponseVO.getAdjustTimes();
            BigDecimal totalDepositAmountPage=agentDepositWithdraw.getDepositAmount().add(manualUpAmount);
            Integer totalDepositNum=agentDepositWithdraw.getDepositNum()+manualUpTimes;
            agentInfoResponseVO.setTotalDepositAmount(totalDepositAmountPage);
            agentInfoResponseVO.setTotalDepositTimes(totalDepositNum);

            AgentManualUpRecordResponseVO agentManualDownRecordResponseVO = agentManualUpRecordResponseVOMap.get(agentInfoResponseVO.getAgentId().concat(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode().toString()));
            BigDecimal manualDownAmount=agentManualDownRecordResponseVO==null?BigDecimal.ZERO:agentManualDownRecordResponseVO.getAdjustAmount();
            Integer manualDownTimes=agentManualDownRecordResponseVO==null?0:agentManualDownRecordResponseVO.getAdjustTimes();
            BigDecimal totalWithdrawAmountPage=agentDepositWithdraw.getWithdrawAmount().add(manualDownAmount);
            Integer totalWithdrawNumPage=agentDepositWithdraw.getWithdrawNum()+manualDownTimes;
            agentInfoResponseVO.setTotalWithdrawAmount(totalWithdrawAmountPage);
            agentInfoResponseVO.setTotalWithdrawTimes(totalWithdrawNumPage);*/
        }


        List<AgentLabelPO> agentLabelPOS = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(agentLabelIds)) {
            agentLabelPOS = agentLabelService.listByIds(agentLabelIds);
        }

        for (AgentInfoResponseVO record : records) {
            // 层级名称
            if (null != record.getLevel()) {
                AgentLevelEnum agentLevelEnum = AgentLevelEnum.nameOfCode(record.getLevel());
                if (null != agentLevelEnum) {
                    record.setLevelName(agentLevelEnum.getName());
                }
            }
            // 直属上级
            if (null != record.getParentId()) {
                AgentInfoPO byId = this.getByAgentId(record.getParentId());
                if (null != byId) {
                    record.setParentIdName(byId.getAgentAccount());
                }
            }

            // 风控层级
            if (null != record.getRiskLevelId()) {
                RiskLevelDetailsVO riskLevelDetailsVO = riskMap.get(record.getRiskLevelId());
                record.setRiskLevel(null == riskLevelDetailsVO ? null : riskLevelDetailsVO.getRiskControlLevel());
            }

            // 代理标签
            if (null != record.getAgentLabelId()) {
                List<AgentLabelPO> agentLabelPOList = agentLabelPOS.stream().filter(o -> record.getAgentLabelId().contains(o.getId())).toList();
                if (!CollectionUtils.isEmpty(agentLabelPOList)) {
                    List<String> agentLabelNames = agentLabelPOList.stream().map(o -> o.getName()).toList();
                    String labelNames = String.join(CommonConstant.COMMA, agentLabelNames);
                    record.setAgentLabel(labelNames);
                }
            }

            // 代理归属 1推广 2招商 3官资
            /*if (null != record.getAgentAttribution()) {
                AgentAttributionEnum agentAttributionEnum = AgentAttributionEnum.nameOfCode(record.getAgentAttribution());
                record.setAgentAttributionName(null == agentAttributionEnum ? null : agentAttributionEnum.getName());
            }*/


            // 代理类别 1常规代理 2流量代理
           /* if (null != record.getAgentCategory()) {
                AgentCategoryEnum agentCategoryEnum = AgentCategoryEnum.nameOfCode(record.getAgentCategory());
                record.setAgentCategoryName(null == agentCategoryEnum ? null : agentCategoryEnum.getName());
            }*/

            // 账号类型
           /* if (null != record.getAgentType()) {
                AgentTypeEnum agentTypeEnum = AgentTypeEnum.nameOfCode(record.getAgentType());
                if (null != agentTypeEnum) {
                    record.setAgentTypeName(agentTypeEnum.getName());
                }
            }*/

            // 账号状态
           /* if (StrUtil.isNotEmpty(record.getStatus())) {
                List<CodeValueVO> accountStatusName = Lists.newArrayList();
                // 账号状态 - 用于导出
                StringBuilder accountStatusExport = new StringBuilder();

                String[] accountStatusList = record.getStatus().split(",");
                for (String status : accountStatusList) {
                    for (AgentStatusEnum agentStatusEnum : AgentStatusEnum.getList()) {
                        if (status.equals(agentStatusEnum.getCode())) {
                            CodeValueVO codeValueVO = new CodeValueVO();
                            codeValueVO.setCode(agentStatusEnum.getCode());
                            codeValueVO.setValue(agentStatusEnum.getName());
                            accountStatusName.add(codeValueVO);

                            accountStatusExport.append(agentStatusEnum.getName()).append(" ");
                        }
                    }
                }
                record.setStatusName(accountStatusName);
                record.setStatusExport(accountStatusExport.toString());
            }*/


            // 注册方式
            /*if (null != record.getRegisterWay()) {
                RegisterWayEnum registerWayEnum = RegisterWayEnum.nameOfCode(record.getRegisterWay());
                if (null != registerWayEnum) {
                    record.setRegisterWayName(registerWayEnum.getName());
                }
            }*/

            // 下级代理人数
            Long downAgent = allDownAgentNumber.get(record.getAgentId());
            downAgent = null == downAgent ? 0 : downAgent;
            downAgentNumber = downAgentNumber + downAgent;

            // 下级会员人数
            List<String> downAgentIds = allDownAgentId.get(record.getAgentId());
            //  Long downUser = userInfoApi.getCountByAgentIds(downAgentIds);
            Long downUser = getUserNumberByAgentIds(downAgentIds, userNumByAgentIds);
            downUser = null == downUser ? 0 : downUser;
            downUserNumber = downUserNumber + downUser;

            // 直属代理人数
            Long directAgent = directAgentNumberByIds.get(record.getAgentId());
            directAgent = null == directAgent ? 0 : directAgent;
            directAgentNumber = directAgentNumber + directAgent;

            // 直属会员人数
            Long directUser = directUserNumberByIds.get(record.getAgentId());
            directUser = null == directUser ? 0 : directUser;
            directUserNumber = directUserNumber + directUser;

            // 佣金钱包余额
            commissionWalletBalance = commissionWalletBalance.add(record.getCommissionWalletBalance());
            // 额度钱包余额
            quotaWalletBalance = quotaWalletBalance.add(record.getQuotaWalletBalance());
            //充值金额
            AgentDepositWithdrawStatisticsVO agentDepositWithdraw = agentDepositWithdrawPageMap.get(record.getAgentAccount());
            //人工加额
            //  AgentManualUpRecordResponseVO agentManualUpRecordResponseVO = agentManualUpRecordResponseVOMap.get(record.getAgentId().concat(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode().toString()));
            //  BigDecimal manualUpAmount = agentManualUpRecordResponseVO == null ? BigDecimal.ZERO : agentManualUpRecordResponseVO.getAdjustAmount();
            // 总存款金额
            BigDecimal depositAmount = agentDepositWithdraw.getDepositAmount();
            BigDecimal depositAmountCurrent = depositAmount;
            totalDepositAmount = totalDepositAmount.add(depositAmountCurrent);
            record.setTotalDepositAmount(depositAmountCurrent);
            // 总存款次数
            Integer depositNum = agentDepositWithdraw.getDepositNum();
            //Integer manualUpTimes = agentManualUpRecordResponseVO == null ? 0 : agentManualUpRecordResponseVO.getAdjustTimes();
            Integer depositTimesCurrent = depositNum;
            totalDepositTimes = totalDepositTimes + depositTimesCurrent;
            record.setTotalDepositTimes(depositTimesCurrent);
            // 总提款金额
            BigDecimal withdrawAmount = agentDepositWithdraw.getWithdrawAmount();
            //人工减额
            //  AgentManualUpRecordResponseVO agentManualDownRecordResponseVO = agentManualUpRecordResponseVOMap.get(record.getAgentId().concat(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode().toString()));
            // BigDecimal manualDownAmount = agentManualDownRecordResponseVO == null ? BigDecimal.ZERO : agentManualDownRecordResponseVO.getAdjustAmount();
            BigDecimal withdrawAmountCurrent = withdrawAmount;
            totalWithdrawAmount = totalWithdrawAmount.add(withdrawAmountCurrent);
            record.setTotalWithdrawAmount(withdrawAmountCurrent);
            // 总提款次数
            Integer withdrawNum = agentDepositWithdraw.getWithdrawNum();
            //Integer manualDownTimes = agentManualDownRecordResponseVO == null ? 0 : agentManualDownRecordResponseVO.getAdjustTimes();
            Integer withdrawTimesCurrent = withdrawNum;
            totalWithdrawTimes = totalWithdrawTimes + withdrawTimesCurrent;
            record.setTotalWithdrawTimes(withdrawTimesCurrent);

            // 下级代理人数
            record.setDownAgentNumber(downAgent.intValue());
            // 下级会员人数
            record.setDownUserNumber(downUser.intValue());
            // 直属代理人数
            record.setDirectAgentNumber(directAgent.intValue());
            // 直属会员人数
            record.setDirectUserNumber(directUser.intValue());
        }
        pageResult.setRecords(records);

        // 本页合计
        AgentInfoResponseVO currentPage = new AgentInfoResponseVO();
        currentPage.setAgentAccount("小计");
        currentPage.setDownAgentNumber((int) downAgentNumber);
        currentPage.setDownUserNumber((int) downUserNumber);
        currentPage.setDirectAgentNumber((int) directAgentNumber);
        currentPage.setDirectUserNumber((int) directUserNumber);
        currentPage.setCommissionWalletBalance(commissionWalletBalance);
        currentPage.setQuotaWalletBalance(quotaWalletBalance);
        currentPage.setTotalDepositAmount(totalDepositAmount);
        currentPage.setTotalDepositTimes(totalDepositTimes);
        currentPage.setTotalWithdrawAmount(totalWithdrawAmount);
        currentPage.setTotalWithdrawTimes(totalWithdrawTimes);
        result.setCurrentPage(currentPage);

        // 全部合计
        List<AgentInfoResponseVO> totalPages = agentInfoRepository.getTotalPage(vo);

        List<String> agentAccountTotal = totalPages.stream().map(AgentInfoResponseVO::getAgentAccount).collect(Collectors.toList());

        List<String> idTotal = totalPages.stream().map(AgentInfoResponseVO::getAgentId).collect(Collectors.toList());

        long downAgentNumberAll = 0;
        long downUserNumberAll = 0;
        long directAgentNumberAll = 0;
        long directUserNumberAll = 0;
        BigDecimal commissionWalletBalanceAll = BigDecimal.ZERO;
        BigDecimal quotaWalletBalanceAll = BigDecimal.ZERO;
        BigDecimal totalDepositAmountAll = BigDecimal.ZERO;
        int totalDepositTimesAll = 0;
        BigDecimal totalWithdrawAmountAll = BigDecimal.ZERO;
        int totalWithdrawTimesAll = 0;

        // 下级代理人数 Map
        Map<String, Long> allDownAgentNumberTotal = this.findAllDownAgentNumberByIds(idTotal, getAllLists);
        // 查询每个agentId的 所有下级代理id(包含代理自己)
        Map<String, List<String>> allDownAgentIdTotal = this.findAllDownAgentIdByIds(idTotal, getAllLists);
        // 直属代理人数 Map
        Map<String, Long> directAgentNumberByIdsTotal = this.findDirectAgentNumberByIds(idTotal);
        // 直属会员人数 Map
        Map<String, Long> directUserNumberByIdsTotal = this.findDirectUserNumberByIds(idTotal);
        // 充值提币统计
        Map<String, AgentDepositWithdrawStatisticsVO> agentDepositWithdrawMap = agentDepositWithdrawalService.getAgentDepositWithdraws(vo.getSiteCode(), agentAccountTotal);

        //人工加减额统计
        // Map<String, AgentManualUpRecordResponseVO> agentManualUpRecordTotalResponse = agentManualUpDownApi.listStaticData(idTotal);


        for (AgentInfoResponseVO record : totalPages) {
            // 下级代理人数
            Long downAgent = allDownAgentNumberTotal.get(record.getAgentId());
            downAgent = null == downAgent ? 0 : downAgent;
            downAgentNumberAll = downAgentNumberAll + downAgent;

            // 下级会员人数
            List<String> downAgentIds = allDownAgentIdTotal.get(record.getAgentId());
            //Long downUser = userInfoApi.getCountByAgentIds(downAgentIds);
            Long downUser = getUserNumberByAgentIds(downAgentIds, userNumByAgentIds);
            downUser = null == downUser ? 0 : downUser;
            downUserNumberAll = downUserNumberAll + downUser;

            // 直属代理人数
            Long directAgent = directAgentNumberByIdsTotal.get(record.getAgentId());
            directAgent = null == directAgent ? 0 : directAgent;
            directAgentNumberAll = directAgentNumberAll + directAgent;

            // 直属会员人数
            Long directUser = directUserNumberByIdsTotal.get(record.getAgentId());
            directUser = null == directUser ? 0 : directUser;
            directUserNumberAll = directUserNumberAll + directUser;

            // 佣金钱包余额
            commissionWalletBalanceAll = commissionWalletBalanceAll.add(record.getCommissionWalletBalance());
            // 额度钱包余额
            quotaWalletBalanceAll = quotaWalletBalanceAll.add(record.getQuotaWalletBalance());

            AgentDepositWithdrawStatisticsVO agentDepositWithdraw = agentDepositWithdrawMap.get(record.getAgentAccount());
            //AgentManualUpRecordResponseVO agentManualUpRecordResponseVO = agentManualUpRecordTotalResponse.get(record.getAgentId().concat(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode().toString()));
            // BigDecimal manualUpAmount = agentManualUpRecordResponseVO == null ? BigDecimal.ZERO : agentManualUpRecordResponseVO.getAdjustAmount();
            // 总存款金额
            totalDepositAmountAll = totalDepositAmountAll.add(agentDepositWithdraw.getDepositAmount());
            // 总存款次数
            // Integer manualUpTimes = agentManualUpRecordResponseVO == null ? 0 : agentManualUpRecordResponseVO.getAdjustTimes();
            totalDepositTimesAll = totalDepositTimesAll + agentDepositWithdraw.getDepositNum();

            // AgentManualUpRecordResponseVO agentManualDownRecordResponseVO = agentManualUpRecordTotalResponse.get(record.getAgentId().concat(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode().toString()));
            //BigDecimal manualDownAmount = agentManualDownRecordResponseVO == null ? BigDecimal.ZERO : agentManualDownRecordResponseVO.getAdjustAmount();
            // 总提款金额
            totalWithdrawAmountAll = totalWithdrawAmountAll.add(agentDepositWithdraw.getWithdrawAmount());
            // 总提款次数
            // Integer manualDownTimes = agentManualDownRecordResponseVO == null ? 0 : agentManualDownRecordResponseVO.getAdjustTimes();
            totalWithdrawTimesAll = totalWithdrawTimesAll + agentDepositWithdraw.getWithdrawNum();
        }
        // 全部合计
        AgentInfoResponseVO totalPage = new AgentInfoResponseVO();
        totalPage.setAgentAccount("总计");
        totalPage.setDownAgentNumber((int) downAgentNumberAll);
        totalPage.setDownUserNumber((int) downUserNumberAll);
        totalPage.setDirectAgentNumber((int) directAgentNumberAll);
        totalPage.setDirectUserNumber((int) directUserNumberAll);
        totalPage.setCommissionWalletBalance(commissionWalletBalanceAll);
        totalPage.setQuotaWalletBalance(quotaWalletBalanceAll);
        totalPage.setTotalDepositAmount(totalDepositAmountAll);
        totalPage.setTotalDepositTimes(totalDepositTimesAll);
        totalPage.setTotalWithdrawAmount(totalWithdrawAmountAll);
        totalPage.setTotalWithdrawTimes(totalWithdrawTimesAll);
        result.setTotalPage(totalPage);

        result.setPageList(pageResult);
        return ResponseVO.success(result);
    }

    /**
     * 按照代理 获取下级会员数
     *
     * @param downAgentIds
     * @param userNumByAgentIds
     * @return
     */
    private Long getUserNumberByAgentIds(List<String> downAgentIds, Map<String, Long> userNumByAgentIds) {
        Long totalUserNum = 0L;
        for (String agentId : downAgentIds) {
            Long userNum = userNumByAgentIds.get(agentId);
            userNum = userNum == null ? 0L : userNum;
            totalUserNum = totalUserNum + userNum;
        }
        return totalUserNum;
    }

    private Map<String, Long> findAllDownUserNumberByIds(List<String> downAgentIds) {
        return userInfoApi.getUserCountGroupByAgentId(downAgentIds);
    }

    public ResponseVO<Long> getTotalCount(AgentInfoPageVO vo) {
        if (StrUtil.isNotEmpty(vo.getAgentAccount())) {
            AgentInfoPO one = this.lambdaQuery().eq(AgentInfoPO::getAgentAccount, vo.getAgentAccount()).one();
            if (null == one) {
                return ResponseVO.success(0L);
            }
            vo.setAgentId(one.getId());
        }

        Long totalCount = agentInfoRepository.getTotalCount(vo);
        return ResponseVO.success(totalCount);
    }


    public ResponseVO<AgentDetailIBasicVO> getBasicAgentInfo(final AgentDetailParam param) {
        try {
            LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AgentInfoPO::getAgentAccount, param.getAgentAccount());
            queryWrapper.eq(AgentInfoPO::getSiteCode, param.getSiteCode());
            AgentInfoPO po = agentInfoRepository.selectOne(queryWrapper);
            if (null == po) {
                return ResponseVO.fail(ResultCode.AGENT_NOT_EXISTS);
            }

            AgentDetailIBasicVO vo = BeanUtil.copyProperties(po, AgentDetailIBasicVO.class);

            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AgentInfoPO::getAgentId, po.getParentId());
            AgentInfoPO superAgent = agentInfoRepository.selectOne(queryWrapper);
            if (ObjectUtil.isNotEmpty(superAgent)) {
                vo.setSuperAgent(superAgent.getAgentAccount());
            }
            //查询当前代理是否存在待审核的内容
            LambdaQueryWrapper<AgentInfoModifyReviewPO> reviewPendingQuery = Wrappers.lambdaQuery();
            reviewPendingQuery.eq(AgentInfoModifyReviewPO::getSiteCode, param.getSiteCode())
                    .eq(AgentInfoModifyReviewPO::getAgentAccount, param.getAgentAccount())
                    .and(wrapper -> wrapper.eq(AgentInfoModifyReviewPO::getReviewStatus, ReviewStatusEnum.REVIEW_PROGRESS.getCode())
                            .or()
                            .eq(AgentInfoModifyReviewPO::getReviewStatus, ReviewStatusEnum.REVIEW_PENDING.getCode()));
            Long reviewCount = agentInfoModifyReviewRepository.selectCount(reviewPendingQuery);
            if (reviewCount > 0) {
                vo.setAgentReviewStatus(Integer.parseInt(YesOrNoEnum.YES.getCode()));
            } else {
                vo.setAgentReviewStatus(Integer.parseInt(YesOrNoEnum.NO.getCode()));
            }
            //当前代理是否存在待审核支付密码重置,条件与上面基本一致，增加待审核类型
            reviewPendingQuery.eq(AgentInfoModifyReviewPO::getReviewApplicationType, AgentInfoChangeTypeEnum.PAYMENT_PASSWORD_RESET.getCode());

            Long l = agentInfoModifyReviewRepository.selectCount(reviewPendingQuery);
            if (l > 0) {
                vo.setPayPasswordStatus(Integer.parseInt(YesOrNoEnum.YES.getCode()));
                //待审核支付密码，替换文本
                vo.setPayPassword("密码重置 审核中");
            } else {
                // 支付密码为空脱敏
                if (ObjectUtil.isNotEmpty(po.getPayPassword())) {
                    vo.setPayPassword("******");
                }
                vo.setPayPasswordStatus(Integer.parseInt(YesOrNoEnum.NO.getCode()));
            }
            vo.setAgentType(String.valueOf(po.getAgentType()));
            vo.setAgentTypeName(AgentTypeEnum.parseName(po.getAgentType()));

            String statusNames = Arrays.stream(po.getStatus().split(CommonConstant.COMMA))
                    .map(String::trim) // 去掉前后空格
                    .map(AgentStatusEnum::nameOfCode)
                    .map(AgentStatusEnum::getName)
                    .collect(Collectors.joining(CommonConstant.COMMA));
            vo.setStatusNames(statusNames);
            // 代理风控层级
            if (null != vo.getRiskLevelId()) {
                RiskLevelDetailsVO riskLevelDetailsVO =
                        riskApi.getById(IdVO.builder().id(vo.getRiskLevelId()).build());
                String riskLevel = null;
                if (null != riskLevelDetailsVO) {
                    riskLevel = riskLevelDetailsVO.getRiskControlLevel();
                }
                vo.setRiskLevelName(riskLevel);
            }
            // IP风控层级
            if (null != vo.getRegisterIp()) {
                RiskAccountQueryVO riskAccountQueryVO = new RiskAccountQueryVO();
                riskAccountQueryVO.setSiteCode(po.getSiteCode());
                riskAccountQueryVO.setRiskControlAccount(vo.getRegisterIp());
                riskAccountQueryVO.setRiskControlTypeCode(RiskTypeEnum.RISK_IP.getCode());
                RiskAccountVO riskAccountVO = riskApi.getRiskAccountByAccount(riskAccountQueryVO);
                if (riskAccountVO != null) {
                    vo.setIpControlId(riskAccountVO.getRiskControlLevelId());
                    vo.setIpControlName(riskAccountVO.getRiskControlLevel());
                }
            }
            // 入口权限
            vo.setEntrancePermName(AgentSwitchEnum.nameOfCode(String.valueOf(po.getEntrancePerm()))
                    .getName());
            // 注册端
            vo.setRegisterDeviceName(Objects.requireNonNull(DeviceType.nameOfCode(po
                    .getRegisterDeviceType())).getName());
            // 充值限制
            String key = String.format(RedisConstants.AGENT_DEPOSIT_FAIL_COUNT, po.getAgentAccount());

            Long increment = RedisUtil.getAtomicLong(key);
            vo.setRemoveRechargeLimit((increment >= CommonConstant.business_five) ?
                    Integer.parseInt(YesOrNoEnum.YES.getCode()) : Integer.parseInt(YesOrNoEnum.NO.getCode()));
            vo.setRemoveRechargeLimitName(Integer.parseInt(YesOrNoEnum.YES.getCode()) == vo.getRemoveRechargeLimit() ?
                    "有" : "没有");
            // 代理类别 1常规代理 2流量代理
            AgentCategoryEnum agentCategoryEnum = AgentCategoryEnum.nameOfCode(vo.getAgentCategory());
            vo.setAgentCategoryName(null == agentCategoryEnum ? null : agentCategoryEnum.getName());
            if (StringUtils.isNotBlank(po.getAgentLabelId())) {
                // 代理标签
                List<AgentLabelVO> labelPO = agentLabelService.getListByIds(Arrays.asList(po.getAgentLabelId().split(CommonConstant.COMMA)));
                if (CollectionUtil.isNotEmpty(labelPO)) {
                    String result = labelPO.stream()
                            .map(AgentLabelVO::getName)
                            .collect(Collectors.joining(CommonConstant.COMMA));
                    vo.setAgentLabelName(result);
                    //
                    String labelIds = labelPO.stream()
                            .map(AgentLabelVO::getId)
                            .collect(Collectors.joining(CommonConstant.COMMA));
                    vo.setAgentLabelId(labelIds);
                } else {
                    vo.setAgentLabelId(null);
                }
            }
            if (param.getDataDesensitization()) {
                // 手机号
                vo.setPhone(SymbolUtil.showPhone(vo.getPhone()));
                // 邮箱
                vo.setEmail(SymbolUtil.showEmail(vo.getEmail()));
            }
            String planCode = po.getPlanCode();
            if (StrUtil.isNotBlank(planCode)) {
                //佣金方案
                LambdaQueryWrapper<AgentCommissionPlanPO> planQuery = Wrappers.lambdaQuery();
                planQuery.eq(AgentCommissionPlanPO::getSiteCode, po.getSiteCode()).eq(AgentCommissionPlanPO::getPlanCode, planCode);
                AgentCommissionPlanPO planPO = commissionPlanRepository.selectOne(planQuery);
                if (planPO != null) {
                    vo.setPlanCodeName(planPO.getPlanName());
                    vo.setPlanCode(planCode);
                }
            }
            //计算一下离线天数
            Long lastLoginTime = vo.getLastLoginTime();
            /*Long registerTime = vo.getRegisterTime();
            if (lastLoginTime == null) {
                lastLoginTime = registerTime;
            }*/
            long nowTime = System.currentTimeMillis();
            if (lastLoginTime != null && nowTime > lastLoginTime) {
                long offlineDays = (nowTime - lastLoginTime) / 1000 / 60 / 60 / 24;
                vo.setOfflineDays((int) offlineDays);
            }
            if (vo.getBirthday() != null) {
                vo.setBirthdayText(DateUtils.formatDateByZoneId(vo.getBirthday(), DateUtils.DATE_FORMAT_1, param.getTimeZone()));
            }
            return ResponseVO.success(vo);
        } catch (Exception e) {
            log.error("代理详情, 该用户:{} 基本信息查询发生异常", param.getAgentAccount(), e);
            return ResponseVO.fail(ResultCode.QUERY_AGENT_DETAIL_ERROR);
        }
    }

    public ResponseVO<Page<AgentRemarkRecordVO>> getAgentRemark(final AgentDetailParam param) {
        try {
            Page<AgentRemarkRecordVO> page = new Page<>(param.getPageNumber(), param.getPageSize());
            Page<AgentRemarkRecordVO> result = agentRemarkRecordRepository.getAgentRemarkPage(page, param);
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("查询代理详情该代理用户:{} 备注信息发生异常", param.getAgentAccount(), e);
            return ResponseVO.fail(ResultCode.QUERY_AGENT_REMARK_ERROR);
        }
    }

    public boolean updateByAgentId(String agentId, BigDecimal firstDepositAmount) {
        LambdaUpdateWrapper<AgentInfoPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(AgentInfoPO::getId, agentId)
                .set(AgentInfoPO::getFirstDepositTime, System.currentTimeMillis())
                .set(AgentInfoPO::getFirstDepositAmount, firstDepositAmount);
        return this.update(null, lambdaUpdate);
    }

    /**
     * 查询代理下级所有代理 不包含本身
     *
     * @param page，vo
     * @return
     */
    public Page<AgentInfoVO> findAllChildAgentsByPage(Page<Object> page, AgentLowerLevelReqVO vo, boolean containsSelf) {
        return agentInfoRepository.findAllChildAgentsByPage(page, vo, containsSelf);
    }

    /**
     * 查询代理下级所有代理 包含本身
     *
     * @param agentId
     * @return
     */
    public List<AgentInfoVO> findAllChildAgents(String agentId) {
       /* List<AgentInfoVO> list1 = agentInfoRepository.findAllDirectChildAgents(Collections.singletonList(agentId));
        List<String> agentIds = list1.stream().map(AgentInfoVO::getAgentId).toList();
        if (CollUtil.isNotEmpty(agentIds)) {
            List<AgentInfoVO> list2 = agentInfoRepository.findAllDirectChildAgents(agentIds);
            list1.addAll(list2);
            if (CollUtil.isNotEmpty(list2)) {
                List<String> agentIds2 = list2.stream().map(AgentInfoVO::getAgentId).toList();
                List<AgentInfoVO> list3 = agentInfoRepository.findAllDirectChildAgents(agentIds2);
                list1.addAll(list3);
            }
        }
        // 添加自己
        AgentInfoVO vo = agentInfoRepository.findByIdSelf(agentId);
        list1.add(vo);
        return list1;*/
        // 无限代的
        // 1️⃣ 参数校验，防止 NPE
        if (StringUtils.isBlank(agentId)) {
            return Collections.emptyList();
        }

        // 2️⃣ 查询所有子节点（closure 表）
        LambdaQueryWrapper<AgentInfoRelationPO> closureWrapper = Wrappers.lambdaQuery();
        closureWrapper.eq(AgentInfoRelationPO::getAncestorAgentId, agentId);

        List<AgentInfoRelationPO> closureList = agentInfoRelationRepository.selectList(closureWrapper);
        if (CollUtil.isEmpty(closureList)) {
            return Collections.emptyList();
        }

        // 3️⃣ 提取子 agentId
        List<String> agentIds = closureList.stream()
                .map(AgentInfoRelationPO::getDescendantAgentId)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .toList();

        if (CollUtil.isEmpty(agentIds)) {
            return Collections.emptyList();
        }

        // 4️⃣ 查询代理信息
        LambdaQueryWrapper<AgentInfoPO> agentWrapper = Wrappers.lambdaQuery();
        agentWrapper.in(AgentInfoPO::getAgentId, agentIds);

        List<AgentInfoPO> agentInfoList = agentInfoRepository.selectList(agentWrapper);
        if (CollUtil.isEmpty(agentInfoList)) {
            return Collections.emptyList();
        }

        // 5️⃣ PO → VO
        return ConvertUtil.entityListToModelList(agentInfoList, AgentInfoVO.class);


    }



   /* public List<AgentSubLineUserResVO> findAgentSubLineUserNum(AgentSubLineReqVO reqVO) {
        return agentInfoRepository.findAgentSubLineUserNum(reqVO);
    }*/

    public void updateAgentByAccount(AgentInfoVO agentInfoVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        LambdaUpdateWrapper<AgentInfoPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AgentInfoPO::getSiteCode, siteCode);
        updateWrapper.eq(AgentInfoPO::getAgentAccount, agentInfoVO.getAgentAccount());
        updateWrapper.set(agentInfoVO.getSuperRemark() != null, AgentInfoPO::getSuperRemark, agentInfoVO.getSuperRemark());
        updateWrapper.set(StringUtils.isNotBlank(agentInfoVO.getLanguage()), AgentInfoPO::getLanguage, agentInfoVO.getLanguage());
        agentInfoRepository.update(null, updateWrapper);
    }


    public AgentInfoPO getAgentInfoPO(String siteCode, String agentAccount) {
        return this.getOne(new LambdaQueryWrapper<AgentInfoPO>()
                .eq(AgentInfoPO::getSiteCode, siteCode)
                .eq(AgentInfoPO::getAgentAccount, agentAccount)
        );
    }

    public AgentInfoPO getByAgentId(String agentId) {
        return this.getOne(new LambdaQueryWrapper<AgentInfoPO>()
                .eq(AgentInfoPO::getAgentId, agentId)
        );
    }

    public List<AgentInfoPO> getByAgentIds(String siteCode,Set<String> agentIds) {
        LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentInfoPO::getSiteCode, siteCode);
        queryWrapper.in(AgentInfoPO::getAgentId, agentIds);
        queryWrapper.orderByDesc(AgentInfoPO::getLevel);
        return agentInfoRepository.selectList(queryWrapper);
    }

    public int updateAgentLoginInfo(AgentLoginUpdateVO agentLoginUpdateVO) {
        LambdaUpdateWrapper<AgentInfoPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AgentInfoPO::getAgentId, agentLoginUpdateVO.getAgentId());
        updateWrapper.set(agentLoginUpdateVO.getLastLoginTime() != null, AgentInfoPO::getLastLoginTime, agentLoginUpdateVO.getLastLoginTime());
        updateWrapper.set(agentLoginUpdateVO.getUpdatedTime() != null, AgentInfoPO::getUpdatedTime, agentLoginUpdateVO.getUpdatedTime());
        updateWrapper.set(agentLoginUpdateVO.getOfflineDays() != null, AgentInfoPO::getOfflineDays, agentLoginUpdateVO.getOfflineDays());
        return agentInfoRepository.update(null, updateWrapper);
    }

    public List<String> getAgentAccountByName(String agentName) {
        List<AgentInfoPO> list = this.lambdaQuery().eq(AgentInfoPO::getName, agentName).list();
        return list.stream().map(AgentInfoPO::getAgentAccount).toList();
    }

    public List<String> getALLAgentAccountList(String siteCode, final String agentAccount) {
        AgentInfoPO vo = this.getAgentInfoPO(siteCode, agentAccount);
        if (vo == null) return null;
        List<AgentInfoPO> list = agentInfoRepository.findAgentInfoTree(vo.getAgentId());
        return list.stream().map(AgentInfoPO::getAgentAccount).toList();
    }

    public boolean updatePasswordByAgentAccount(String siteCode, String agentAccount, String password) {
        LambdaUpdateWrapper<AgentInfoPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate
                .eq(AgentInfoPO::getSiteCode, siteCode)
                .eq(AgentInfoPO::getAgentAccount, agentAccount)
                .set(AgentInfoPO::getAgentPassword, password)
                .set(AgentInfoPO::getUpdatedTime, System.currentTimeMillis());
        return this.update(null, lambdaUpdate);
    }

    public Boolean updateAgentInfoById(AgentInfoModifyVO editVO) {
        LambdaUpdateWrapper<AgentInfoPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(AgentInfoPO::getId, editVO.getId())
                .set(editVO.getRiskLevelId() != null, AgentInfoPO::getRiskLevelId, editVO.getRiskLevelId())
                .set(AgentInfoPO::getUpdatedTime, System.currentTimeMillis());
        return this.update(null, lambdaUpdate);
    }


    @DistributedLock(name = RedisConstants.AGENT_ADD_LOCK_KEY, unique = "#id", fair = true, waitTime = 3, leaseTime = 60)
    public ResponseVO addAgent(AddAgentNewVO vo, String registerIp, Integer registerDeviceType,
                               String id) {
        // 注册不区分大小写(保存用户原始输入的)，登录区分
        // 获取参数
        String agentAccount = vo.getAgentAccount();
        String siteCode = vo.getSiteCode();
        String agentPassword = vo.getAgentPassword();

        AgentInfoPO upAgentInfo = this.selectByAgentId(id);
        if (upAgentInfo.getLevel() + 1 > upAgentInfo.getMaxLevel()) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_A);
        }

        // 代理账号校验
        if (UserChecker.checkUserAccount(agentAccount)) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_ERROR);
        }

        // 代理账号 是否已经存在
        if (null != agentReviewService.getByAgentAccount(siteCode, agentAccount)
                || null != agentReviewService.getAgentInfoByAgentAccount(siteCode, agentAccount)) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_EXIST_ERROR);
        }

        // 密码校验
        if (UserChecker.checkPassword(agentPassword)) {
            return ResponseVO.fail(ResultCode.AGENT_PASSWORD_ERROR);
        }

        // 代理账号和登录密码 不能一致
        if (agentAccount.equals(agentPassword)) {
            return ResponseVO.fail(ResultCode.AGENT_PASSWORD_SAME);
        }
        // 上级代理
        AgentInfoPO upAgentInfoPO = agentInfoRepository.selectByAgentId(id);
        if (upAgentInfoPO == null) {
            return ResponseVO.fail(ResultCode.AGENT_SUPER_AGENT_NOT_VALID_ERROR);
        }
        // 插入agent_info表
        AgentInfoPO po = new AgentInfoPO();
        po.setAvatarCode("avatar_0");
        po.setParentId(id);
        po.setLevel(upAgentInfo.getLevel() + 1);
        po.setMaxLevel(upAgentInfo.getMaxLevel());
        po.setAgentAccount(agentAccount);
        // 生成15位加密盐
        String salt = MD5Util.randomGen();
        po.setSalt(salt);
        // 密码加密
        String encryptPassword = AgentServerUtil.getEncryptPassword(agentPassword, salt);
        po.setAgentPassword(encryptPassword);
        po.setAgentType(upAgentInfo.getAgentType());
        po.setStatus(AgentStatusEnum.NORMAL.getCode());
        // 入口权限 默认开启
        po.setEntrancePerm(CommonConstant.business_one);
        po.setRemoveRechargeLimit(CommonConstant.business_zero);
        po.setRegisterWay(CommonConstant.business_one);
        po.setRegisterDeviceType(registerDeviceType);
        po.setRegisterTime(System.currentTimeMillis());
        po.setRegisterIp(registerIp);
        po.setInviteCode(MD5Util.random7Gen());
        po.setIsAgentArrears(CommonConstant.business_zero);
        po.setPlanCode(vo.getPlanCode());
        po.setCurrentPlanCode(vo.getPlanCode());
        // 获取父代理，取父代理的这个配置
        po.setUserBenefit(upAgentInfoPO.getUserBenefit());
        // 1:PC端 2:H5端
        po.setHomeButtonEntrance(agentHomeAllButtonEntranceService.getAllButtonEntranceByType(CommonConstant.business_one));
        po.setHomeButtonEntranceH5(agentHomeAllButtonEntranceService.getAllButtonEntranceByType(CommonConstant.business_two));
        po.setAgentAttribution(upAgentInfo.getAgentAttribution());
        po.setAgentCategory(upAgentInfo.getAgentCategory());
        po.setParentId(upAgentInfo.getAgentId());
        po.setParentAccount(upAgentInfo.getAgentAccount());
        po.setSiteCode(siteCode);
        po.setAgentId(SnowFlakeUtils.getCommonRandomId());
        //商务信息
        po.setMerchantAccount(upAgentInfo.getMerchantAccount());
        po.setMerchantName(upAgentInfo.getMerchantName());
        agentInfoRepository.insert(po);

        // 再更新AgentInfoPO的path字段

        LambdaUpdateWrapper<AgentInfoPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(AgentInfoPO::getId, po.getId())
                .set(AgentInfoPO::getPath, upAgentInfoPO.getPath() + "," + po.getAgentId());
        this.update(null, lambdaUpdate);

        //记录到代理上下级关系表
        agentInfoRelationService.insertRelation(po.getSiteCode(), po.getAgentId(), po.getParentId());

        // 代理注册记录
        AgentRegisterRecordInsertVO registerRecord = new AgentRegisterRecordInsertVO();
        registerRecord.setAgentType(upAgentInfo.getAgentType());
        registerRecord.setAgentAccount(agentAccount);
        registerRecord.setRegisterIp(registerIp);
        //IPInfoUtils.getIpInfo(registerIp).getAddress();
        IPRespVO ipApiVO = IpAPICoUtils.getIp(registerIp);
        if (ipApiVO != null) {
            registerRecord.setIpAttribution(ipApiVO.getAddress());
        }
        registerRecord.setRegisterDevice(registerDeviceType);
        registerRecord.setRegisterTime(System.currentTimeMillis());
        registerRecord.setSiteCode(siteCode);
        registerRecord.setAgentId(po.getAgentId());
        registerRecord.setDeviceNumber(vo.getDeviceNo());
        agentRegisterRecordService.recordAgentRegister(registerRecord);

        return ResponseVO.success();
    }

    public AgentInfoVO getAgentInfoPOByCode(String inviteCode, String siteCode) {
        AgentInfoPO one = getOne(new LambdaQueryWrapper<AgentInfoPO>()
                .eq(AgentInfoPO::getInviteCode, inviteCode).eq(AgentInfoPO::getSiteCode, siteCode));
        return ConvertUtil.entityToModel(one, AgentInfoVO.class);
    }

    public Long getAgentCountByPlanCode(String planCode) {
        return this.lambdaQuery()
                .eq(AgentInfoPO::getPlanCode, planCode)
                .count();
    }

    public Page<AgentPlanInfoVO> getAgentPageByPlanCode(CommissionAgentReqVO param) {
        Page<AgentInfoPO> page = new Page<>(param.getPageNumber(), param.getPageSize());
        LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentInfoPO::getPlanCode, param.getPlanCode());
        Page<AgentInfoPO> poPage = agentInfoRepository.selectPage(page, queryWrapper);
        Page<AgentPlanInfoVO> voPage = new Page<>();
        BeanUtils.copyProperties(poPage, voPage);
        if (poPage.getRecords().size() > 0) {
            List<AgentPlanInfoVO> voList = ConvertUtil.entityListToModelList(poPage.getRecords(), AgentPlanInfoVO.class);
            voPage.setRecords(voList);
            return voPage;
        }

        return new Page<>();
    }

    public AgentInfoPO selectByAgentId(String agentId) {
        LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentInfoPO::getAgentId, agentId);
        return this.baseMapper.selectOne(queryWrapper);
    }

    public List<AgentInfoVO> getByAgentIds(List<String> superAgentIds) {
        if (CollectionUtils.isEmpty(superAgentIds)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AgentInfoPO::getAgentId, superAgentIds);
        List<AgentInfoPO> list = this.list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            return BeanUtil.copyToList(list, AgentInfoVO.class);
        }
        return new ArrayList<>();
    }

    public List<String> getALLAgentIds(String siteCode) {
        LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(AgentInfoPO::getAgentId);
        queryWrapper.eq(AgentInfoPO::getSiteCode, siteCode);
        return this.list(queryWrapper).stream().map(AgentInfoPO::getAgentId).collect(Collectors.toList());
    }

    public List<AgentInfoPO> getAllBySiteCode(String siteCode) {
        LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentInfoPO::getSiteCode, siteCode);
        return agentInfoRepository.selectList(queryWrapper);
    }

    public List<AgentInfoPartVO> getAllPartAgentInfoBySiteCode(String siteCode) {
        LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentInfoPO::getSiteCode, siteCode);
        //queryWrapper.select(AgentInfoPO::getAgentId, AgentInfoPO::getAgentAccount, AgentInfoPO::getAgentType, AgentInfoPO::getAgentAttribution);
        List<AgentInfoPO> agentInfoPOS = agentInfoRepository.selectList(queryWrapper);

        return ConvertUtil.entityListToModelList(agentInfoPOS, AgentInfoPartVO.class);
    }

    public List<String> getSubAgentIdList(String agentId) {
        return agentInfoRepository.getSubAgentIdList(agentId);
    }

    public List<String> getSubAgentIdDirectReportList(String agentId) {
        List<String> retAgentList = agentInfoRepository.getSubAgentIdDirectReportList(agentId);
        if (CollectionUtils.isEmpty(retAgentList)) {
            return Lists.newArrayList();
        }
        return retAgentList;
    }


    public Long getNewAgents(AgentDataOverviewResVo vo) {
        LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentInfoPO::getSiteCode, vo.getSiteCode())
                .ge(AgentInfoPO::getRegisterTime, vo.getStartTime())
                .le(AgentInfoPO::getRegisterTime, vo.getEndTime());
        return agentInfoRepository.selectCount(queryWrapper);
    }

    public List<AgentInfoVO> getAgentListByCond(AgentInfoCondVo agentInfoCondVo) {
        LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        if (agentInfoCondVo.getStartRegisterDay() != null) {
            queryWrapper.ge(AgentInfoPO::getRegisterTime, agentInfoCondVo.getStartRegisterDay());
        }
        if (agentInfoCondVo.getEndRegisterDay() != null) {
            queryWrapper.le(AgentInfoPO::getRegisterTime, agentInfoCondVo.getEndRegisterDay());
        }
        if (StringUtils.isNotBlank(agentInfoCondVo.getSiteCode())) {
            queryWrapper.eq(AgentInfoPO::getSiteCode, agentInfoCondVo.getSiteCode());
        }
        if (StringUtils.isNotBlank(agentInfoCondVo.getAgentAccount())) {
            queryWrapper.eq(AgentInfoPO::getAgentAccount, agentInfoCondVo.getAgentAccount());
        }

        if (StringUtils.isNotBlank(agentInfoCondVo.getSuperAgentAccount())) {
            queryWrapper.eq(AgentInfoPO::getParentAccount, agentInfoCondVo.getSuperAgentAccount());
        }
        if (StringUtils.isNotBlank(agentInfoCondVo.getAgentCategory())) {
            queryWrapper.eq(AgentInfoPO::getAgentCategory, agentInfoCondVo.getAgentCategory());
        }

        if (StringUtils.isNotBlank(agentInfoCondVo.getAgentType())) {
            queryWrapper.eq(AgentInfoPO::getAgentType, agentInfoCondVo.getAgentType());
        }
        if (StringUtils.isNotBlank(agentInfoCondVo.getAgentLevel())) {
            queryWrapper.eq(AgentInfoPO::getLevel, agentInfoCondVo.getAgentLevel());
        }

        if (StringUtils.isNotBlank(agentInfoCondVo.getInviteCode())) {
            queryWrapper.eq(AgentInfoPO::getInviteCode, agentInfoCondVo.getInviteCode());
        }
        List<AgentInfoPO> list = this.list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            return BeanUtil.copyToList(list, AgentInfoVO.class);
        }
        return new ArrayList<>();
    }

    public AgentInfoVO getAgentBenefit(String userId) {
        return agentInfoRepository.getAgentBenefit(userId);
    }

    public Page<String> getAgentIdListPage(AgentIdPageVO agentIdPageVO) {
        Page<AgentInfoPO> page = new Page<>(agentIdPageVO.getPageNumber(), agentIdPageVO.getPageSize());
        LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<AgentInfoPO>();
        queryWrapper.in(AgentInfoPO::getAgentId, agentIdPageVO.getAgentIdList());
        queryWrapper.eq(StringUtils.isNotBlank(agentIdPageVO.getAgentAccount()), AgentInfoPO::getAgentAccount, agentIdPageVO.getAgentAccount());
        queryWrapper.orderByDesc(AgentInfoPO::getRegisterTime);

        Page<AgentInfoPO> poPage = agentInfoRepository.selectPage(page, queryWrapper);
        Page<String> userIdPage = new Page<>();
        BeanUtils.copyProperties(poPage, userIdPage);
        List<String> agentIdList = poPage.getRecords().stream().map(AgentInfoPO::getAgentId).toList();
        userIdPage.setRecords(agentIdList);
        return userIdPage;
    }

    public Page<AgentInfoVO> findAllDirectChildAgentsByPage(Page<AgentInfoVO> agentInfoVOPage, AgentLowerLevelReqVO vo) {
        Page<AgentInfoPO> page = new Page<>(agentInfoVOPage.getCurrent(), agentInfoVOPage.getSize());
        LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentInfoPO::getParentId, vo.getAgentId());
        queryWrapper.eq(StrUtil.isNotEmpty(vo.getLowerLevelAccount()), AgentInfoPO::getAgentAccount, vo.getLowerLevelAccount());
        Page<AgentInfoPO> poPage = agentInfoRepository.selectPage(page, queryWrapper);
        BeanUtils.copyProperties(poPage, agentInfoVOPage);
        if (!poPage.getRecords().isEmpty()) {
            List<AgentInfoVO> voList = ConvertUtil.entityListToModelList(poPage.getRecords(), AgentInfoVO.class);
            agentInfoVOPage.setRecords(voList);
            return agentInfoVOPage;
        }

        return agentInfoVOPage;
    }

    public Page<AgentInfoPageResultVo> listPage(AgentInfoPageVO vo) {
        Page<AgentInfoPageResultVo> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        return agentInfoRepository.listPage(page, vo);
    }

    public Map<String, AgentInfoVO> getAgentBenefitList(List<String> userId) {
        List<AgentInfoVO> agentInfoVOList = agentInfoRepository.getAgentBenefitList(userId);
        return Optional
                .of(agentInfoVOList)
                .orElse(Lists.newArrayList())
                .stream()
                .collect(Collectors.toMap(AgentInfoVO::getUserId, Function.identity(), (existing, replacement) -> existing));
    }

    public void updateUserBenefit(List<GetAllListVO> allDownAgentById, String afterModification) {
        LambdaUpdateWrapper<AgentInfoPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(AgentInfoPO::getAgentId, allDownAgentById.stream().map(GetAllListVO::getAgentId).toList());
        updateWrapper.set(AgentInfoPO::getUserBenefit, afterModification);
        agentInfoRepository.update(null, updateWrapper);

    }

    public void agentRelationRefresh() {
        for (int agentLevel = 1; agentLevel <= 4; agentLevel++) {
            List<AgentInfoPO> agentInfoPOS = listAgentInfoByLevel(agentLevel);
            log.info("开始刷新第{}层代理上下级关系", agentLevel);
            for (AgentInfoPO agentInfoPO : agentInfoPOS) {
                log.info("开始刷新代理上下级关系:{}", agentInfoPO);
                agentInfoRelationService.insertRelation(agentInfoPO.getSiteCode(), agentInfoPO.getAgentId(), agentInfoPO.getParentId());
            /*   try{
                   TimeUnit.MILLISECONDS.sleep(100);
               }catch (Exception e){
                e.printStackTrace();
               }*/

            }
        }
    }

    public List<AgentInfoPO> listAgentInfoByLevel(int agentLevel) {
        LambdaQueryWrapper<AgentInfoPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AgentInfoPO::getLevel, agentLevel);
        lambdaQueryWrapper.orderByAsc(AgentInfoPO::getId);
        List<AgentInfoPO> agentInfoPOS = agentInfoRepository.selectList(lambdaQueryWrapper);
        return agentInfoPOS;
    }
}
