package com.cloud.baowang.user.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.api.AgentMerchantApi;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.user.constant.UserNoticeTargetConstant;
import com.cloud.baowang.common.core.enums.DeviceType;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.user.api.enums.NotificationTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.vo.i18n.I18nSearchVO;
import com.cloud.baowang.user.api.enums.notice.AgentShipTypeEnum;
import com.cloud.baowang.user.api.enums.notice.MemberShipTypeEnum;
import com.cloud.baowang.user.api.enums.notice.MemberTargetTypeEnum;
import com.cloud.baowang.user.api.enums.notice.SpecifyMemberShipTypeEnum;
import com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.*;
import com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.response.SiteHomeNoticeConfigVO;
import com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.response.UserNoticeConfigVO;
import com.cloud.baowang.user.config.ThreadPoolConfig;
import com.cloud.baowang.user.po.UserInfoPO;
import com.cloud.baowang.user.po.UserNoticeConfigPO;
import com.cloud.baowang.user.po.UserNoticeTargetPO;
import com.cloud.baowang.user.repositories.UserNoticeConfigRepository;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserNoticeConfigService extends ServiceImpl<UserNoticeConfigRepository, UserNoticeConfigPO> {

    private final UserNoticeConfigRepository userNoticeConfigRepository;


    private final UserNoticeTargetService userNoticeTargetService;

    private final UserInfoService userInfoService;

    private final AgentInfoApi agentInfoApi;

    private final AgentMerchantApi agentMerchantApi;

    private final I18nApi i18nApi;

    private final PushJGService pushJGService;

    private final UserNoticeConfigService _this;


    /***
     * 通知配置列表展示
     * @param userNoticeConfigGetVO 入参
     * @return 分页响应数据，其中包含 {@link UserNoticeConfigVO} 对象的列表。
     * 如果查询条件未匹配到结果，则返回空分页对象。
     * @throws BaowangDefaultException 当方法执行过程中发生异常时，抛出自定义异常。
     */
    public ResponseVO<Page<UserNoticeConfigVO>> getUserNoticeConfigPage(UserNoticeConfigGetVO userNoticeConfigGetVO) {
        try {

            Page<UserNoticeConfigPO> page = new Page<>(userNoticeConfigGetVO.getPageNumber(), userNoticeConfigGetVO.getPageSize());
            // 首先根据name在国际化查询
            if (ObjectUtil.isNotEmpty(userNoticeConfigGetVO.getNoticeTitle())) {
                List<String> noticeTitleList = i18nApi.search(I18nSearchVO.builder().searchContent(userNoticeConfigGetVO.getNoticeTitle()).bizKeyPrefix(I18MsgKeyEnum.NOTICE_TITLE.getCode()).lang(CurrReqUtils.getLanguage()).build()).getData();
                if (CollectionUtil.isEmpty(noticeTitleList)) {
                    return ResponseVO.success(new Page<>(userNoticeConfigGetVO.getPageNumber(), userNoticeConfigGetVO.getPageSize()));
                } else {
                    userNoticeConfigGetVO.setNoticeTitleCodeList(noticeTitleList);
                }
            }
            LambdaQueryWrapper<UserNoticeConfigPO> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(userNoticeConfigGetVO.getNoticeType())) {
                queryWrapper.eq(UserNoticeConfigPO::getNoticeType, userNoticeConfigGetVO.getNoticeType());
            } else {
                queryWrapper.in(UserNoticeConfigPO::getNoticeType, List.of(1, 2, 3));
            }
            if (CollectionUtil.isNotEmpty(userNoticeConfigGetVO.getNoticeTitleCodeList())) {
                queryWrapper.in(UserNoticeConfigPO::getNoticeTitleI18nCode, userNoticeConfigGetVO.getNoticeTitleCodeList());
            }
            if (StringUtils.isNotBlank(userNoticeConfigGetVO.getFounder())) {
                queryWrapper.like(UserNoticeConfigPO::getCreator, userNoticeConfigGetVO.getFounder());
            }
            if (StringUtils.isNotBlank(userNoticeConfigGetVO.getSiteCode())) {
                queryWrapper.eq(UserNoticeConfigPO::getSiteCode, userNoticeConfigGetVO.getSiteCode());
            }
            if (!Objects.isNull(userNoticeConfigGetVO.getIsPush())) {
                queryWrapper.eq(UserNoticeConfigPO::getIsPush, userNoticeConfigGetVO.getIsPush());
            }
            if (!Objects.isNull(userNoticeConfigGetVO.getTargetType())) {
                queryWrapper.eq(UserNoticeConfigPO::getTargetType, userNoticeConfigGetVO.getTargetType());
            }


            if (CommonConstant.ORDER_BY_ASC.equals(userNoticeConfigGetVO.getOrderType())) {
                queryWrapper.orderByAsc(UserNoticeConfigPO::getCreatedTime);
            } else {
                //降序
                queryWrapper.orderByDesc(UserNoticeConfigPO::getCreatedTime);
            }
            Page<UserNoticeConfigPO> result = userNoticeConfigRepository.selectPage(page, queryWrapper);
            //获取主键

            List<UserNoticeConfigVO> list = result.getRecords().stream().map(po -> {
                UserNoticeConfigVO vo = new UserNoticeConfigVO();
                BeanUtils.copyProperties(po, vo);
                return vo;
            }).toList();
            Page<UserNoticeConfigVO> pageResult = new Page<>();
            BeanUtils.copyProperties(result, pageResult);
            pageResult.setRecords(list);
            return ResponseVO.success(pageResult);
        } catch (Exception e) {
            log.error("通知配置列表发生异常：", e);
            throw new BaowangDefaultException(ResultCode.NOTIFY_LIST);
        }
    }

    public List<SiteHomeNoticeConfigVO> siteNoticeLists(String siteCode) {
        LambdaQueryWrapper<UserNoticeConfigPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserNoticeConfigPO::getSiteCode, siteCode).eq(UserNoticeConfigPO::getNoticeType, NotificationTypeEnum.ANNOUNCEMENT.getCode()).eq(UserNoticeConfigPO::getStatus, EnableStatusEnum.ENABLE).orderByDesc(UserNoticeConfigPO::getCreatedTime).last(" limit 10  ");
        List<UserNoticeConfigPO> userNoticeConfigPOS = userNoticeConfigRepository.selectList(lambdaQueryWrapper);
        return ConvertUtil.entityListToModelList(userNoticeConfigPOS, SiteHomeNoticeConfigVO.class);
    }

    /**
     * 添加通知
     *
     * @param userNoticeConfigAddVO 通知配置的请求参数
     */
    @Transactional
    public ResponseVO<?> addUserNoticeConfig(UserNoticeConfigAddModifyVO userNoticeConfigAddVO) {
        try {

            UserNoticeConfigPO userNoticeConfigPO = new UserNoticeConfigPO();
            BeanUtils.copyProperties(userNoticeConfigAddVO, userNoticeConfigPO);
            userNoticeConfigPO.setCreator(userNoticeConfigAddVO.getOperator());
            userNoticeConfigPO.setUpdater(userNoticeConfigAddVO.getOperator());
            userNoticeConfigPO.setCreatedTime(System.currentTimeMillis());
            userNoticeConfigPO.setUpdatedTime(System.currentTimeMillis());
            userNoticeConfigPO.setStatus(UserNoticeTargetConstant.SEND);
            String noticeTitleI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.NOTICE_TITLE.getCode());
            String noticeContentI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.NOTICE_CONTENT.getCode());
            String picIconI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.NOTICE_PIC.getCode());
            userNoticeConfigPO.setNoticeTitleI18nCode(noticeTitleI18Code);
            userNoticeConfigPO.setMessageContentI18nCode(noticeContentI18Code);
            // 查询
            Integer maxSortBySiteCode = userNoticeConfigRepository.getMaxSortBySiteCode(userNoticeConfigAddVO.getSiteCode());
            maxSortBySiteCode = maxSortBySiteCode == null ? 1 : maxSortBySiteCode + 1;
            userNoticeConfigPO.setSort(maxSortBySiteCode);
            // 插入国际化信息
            // 使用 HashMap 替代不可变的 Map
            Map<String, List<I18nMsgFrontVO>> i18nData = new HashMap<>();
            i18nData.put(noticeTitleI18Code, userNoticeConfigAddVO.getNoticeTitleI18nCodeList());
            i18nData.put(noticeContentI18Code, userNoticeConfigAddVO.getMessageContentI18nCodeList());


            // 如果 picIconI18nCodeList 非空，添加到 i18nData 中
            if (CollectionUtil.isNotEmpty(userNoticeConfigAddVO.getPicIconI18nCodeList())) {

                userNoticeConfigPO.setPicIconI18nCode(picIconI18Code);
                i18nData.put(picIconI18Code, userNoticeConfigAddVO.getPicIconI18nCodeList());
            }
            // 需要更新redis
            // 跑马灯
            if (userNoticeConfigAddVO.getPopUpType() != null) {
                if (CommonConstant.business_one.equals(userNoticeConfigAddVO.getPopUpType())) {
                    RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_MARQUEE_CONTENT_CACHE));

                }
            }
            // 强制弹窗

            List<UserNoticeTargetPO> userNoticeTargetPOList = new ArrayList<>();

            //校验是会员 1：表示会员 2 表示终端 4.代理  : 校验1：表示会员，
            if (userNoticeConfigAddVO.getTargetType() == MemberTargetTypeEnum.MEMBERS.getCode()) {
                // membershipType 会员类型 会员类型 1:全部会员 2:特定会员 -- 2:特定会员 或者特定代理
                if (MemberShipTypeEnum.SPECIFIC_MEMBERS.getCode() == userNoticeConfigAddVO.getMemberShipType()) {
                    // 特定会员与特定代理  specifyMemberShipType 特定会员类型 1:vip等级 2:主货币 3:指定会员
                    if (SpecifyMemberShipTypeEnum.SPECIFIC_MEMBERS.getCode() == userNoticeConfigAddVO.getSpecifyMemberShipType()) {
                        // specifyMemberShipType 特定会员类型 1:vip等级 2:主货币 3:指定会员 -- 3:指定会员
                        /*if (ObjectUtil.isEmpty(userNoticeConfigAddVO.getFile()) || userNoticeConfigAddVO.getFile().isEmpty()) {
                            return ResponseVO.fail(ResultCode.ACCOUNT_NOT_NULL);
                        }*/
                        // 解析excel文件提取会员账号/代理账号
                        List<String> userList = userNoticeConfigAddVO.getFileAccount();
                        if (CollectionUtils.isEmpty(userList)) {
                            return ResponseVO.fail(ResultCode.ACCOUNT_NOT_NULL);
                        }
                        // 去重的用户
                        List<String> exitsUserList = Lists.newArrayList();
                        //根据输入账号查询用户信息
                        List<UserInfoVO> userInfoList;
                        if (userNoticeConfigAddVO.getTargetType() == MemberTargetTypeEnum.MEMBERS.getCode()) {
                            userInfoList = userInfoService.getUserInfoListByAccounts(userNoticeConfigAddVO.getSiteCode(), userList);
                        } else {
                            userInfoList = new ArrayList<>();
                        }
                        // 代理账号
                        List<AgentInfoVO> agentInfoList;
                        if (userNoticeConfigAddVO.getTargetType() == MemberTargetTypeEnum.AGENTS.getCode()) {
                            agentInfoList = agentInfoApi.getByAgentAccountsAndSiteCode(userNoticeConfigAddVO.getSiteCode(), userList);
                        } else {
                            agentInfoList = new ArrayList<>();
                        }
                        // 合并
                        List<String> notExitsUser;
                        if (userNoticeConfigAddVO.getTargetType() != 4 && CollectionUtils.isEmpty(userInfoList)) {
                            notExitsUser = new ArrayList<>(userList);
                        } else if (userNoticeConfigAddVO.getTargetType() == 4 && CollectionUtils.isEmpty(agentInfoList)) {
                            notExitsUser = new ArrayList<>(userList);
                        } else {
                            Map<Boolean, List<String>> map = new HashMap<>();
                            if (userNoticeConfigAddVO.getTargetType() != 4) {
                                map = userList.stream().collect(Collectors.partitioningBy(s -> userInfoList.stream().anyMatch(user -> user.getUserAccount().equals(s))));
                            } else {
                                map = userList.stream().collect(Collectors.partitioningBy(s -> agentInfoList.stream().anyMatch(user -> user.getAgentAccount().equals(s))));
                            }
                            exitsUserList = Optional.ofNullable(map.get(true)).orElse(Lists.newArrayList());
                            notExitsUser = Optional.ofNullable(map.get(false)).orElse(Lists.newArrayList());
                            List<String> exitsUserIdList = userInfoList.stream().map(UserInfoVO::getUserId).toList();
                            for (String userAccount : exitsUserIdList) {
                                UserNoticeTargetPO userNoticeTargetPO = new UserNoticeTargetPO();
                                //校验是否是特定会员 0：表示是全部会员 1：表示特定会员
                                userNoticeTargetPO.setNoticeType(userNoticeConfigPO.getNoticeType());
                                userNoticeTargetPO.setReadState(UserNoticeTargetConstant.UNREAD);
                                userNoticeTargetPO.setRevokeState(UserNoticeTargetConstant.NOT_REVOKED);
                                userNoticeTargetPO.setDeleteState(UserNoticeTargetConstant.NORMAL);
                                userNoticeTargetPO.setCreatedTime(System.currentTimeMillis());
                                userNoticeTargetPO.setUpdatedTime(System.currentTimeMillis());
                                userNoticeTargetPO.setUserId(userAccount);
                                userNoticeTargetPO.setPlatform(CommonConstant.business_one);
                                userNoticeTargetPOList.add(userNoticeTargetPO);
                            }
                        }

                        // 是否包含无效用户
                        if (CollectionUtils.isNotEmpty(notExitsUser)) {
                            //全部都是无效用户
                            if (0 == userInfoList.size()) {
                                return ResponseVO.fail(ResultCode.USER_NOT_EXIST, ResultCode.USER_NOT_EXIST.getDesc() + ":" + String.join(",", notExitsUser));
                            }
                            userNoticeConfigPO.setSendAccounts(String.join(",", exitsUserList));
                            // 前端 送对象(1:会员2:终端 4：代理) 数据库 targetType  发送对象(1 全体会员 2 特定会员-vip等级 3 特定会员-主货币 4 特定会员-指定了会员  4 终端 5 全部代理 6 特定代理)
                            saveNoticeConfig(userNoticeConfigPO, i18nData);
                            saveNoticeId(userNoticeTargetPOList, userNoticeConfigPO.getId());
                            userNoticeTargetService.saveBatch(userNoticeTargetPOList);
                            // 指定会员  添加极光指定会员
                            if (userNoticeConfigAddVO.getIsPush() == 1) {
                                List<String> userIdsSend = userNoticeTargetPOList.stream().map(UserNoticeTargetPO::getUserId).collect(Collectors.toList());
                                pushJGService.pushByUserIds(userNoticeConfigPO, i18nData, userIdsSend);
                            }
                            //多语言插入
                            return ResponseVO.success(ResultCode.USER_NOT_EXIST.getDesc() + ":" + String.join(",", notExitsUser));
                        }
                        userNoticeConfigPO.setSendAccounts(String.join(",", exitsUserList));
                        // 前端 送对象(1:会员2:终端 4：代理) 数据库 targetType  1 全体会员 2 特定会员 3 终端 4 全部代理 5 特定代理
                        saveNoticeConfig(userNoticeConfigPO, i18nData);
                        saveNoticeId(userNoticeTargetPOList, userNoticeConfigPO.getId());
                        userNoticeTargetService.saveBatch(userNoticeTargetPOList);
                        log.debug("公告消息:指定会员账号:{}", userNoticeConfigPO.getId());
                        // 指定会员  添加极光指定会员
                        if (userNoticeConfigAddVO.getIsPush() == 1) {
                            List<String> userIdsSend = userNoticeTargetPOList.stream().map(UserNoticeTargetPO::getUserId).collect(Collectors.toList());

                            pushJGService.pushByUserIds(userNoticeConfigPO, i18nData, userIdsSend);
                        }
                        return ResponseVO.success();
                    } else if (SpecifyMemberShipTypeEnum.VIP_LEVEL_MEMBERS.getCode() == userNoticeConfigAddVO.getSpecifyMemberShipType()) {
                        // 特定会员类型 1:vip等级 2:主货币 3:特定会员 -- vip等级
                        userNoticeConfigPO.setTerminal(null);
                        userNoticeConfigPO.setVipGradeMix(userNoticeConfigAddVO.getVipGradeMin());
                        userNoticeConfigPO.setVipGradeMax(userNoticeConfigAddVO.getVipGradeMax());
                        saveNoticeConfig(userNoticeConfigPO, i18nData);
                        log.debug("公告消息:指定vip等级会员账号:{}", userNoticeConfigPO.getId());
                        // 插入target
                        _this.saveTarget(userNoticeConfigPO, userNoticeConfigAddVO.getVipGradeMin(), userNoticeConfigAddVO.getVipGradeMax(), i18nData);
                        return ResponseVO.success();
                    } else if (SpecifyMemberShipTypeEnum.MAIN_CURRENCY_MEMBERS.getCode() == userNoticeConfigAddVO.getSpecifyMemberShipType()) {
                        // 特定会员类型 1:vip等级 2:主货币 3:特定会员 -- 主货币
                        userNoticeConfigPO.setTerminal(null);
                        userNoticeConfigPO.setVipGradeMax(null);
                        saveNoticeConfig(userNoticeConfigPO, i18nData);
                        log.debug("公告消息:按照主货币消息:{}", userNoticeConfigPO.getId());
                        _this.saveTargetByCurrency(userNoticeConfigPO, userNoticeConfigAddVO.getCurrencyCode(), i18nData);
                        // 插入target
                        return ResponseVO.success();
                    }
                } else if (MemberShipTypeEnum.ALL_MEMBER.getCode() == userNoticeConfigAddVO.getMemberShipType()) {
                    // 全部会员
                    //
                    userNoticeConfigPO.setTerminal(null);
                    saveNoticeConfig(userNoticeConfigPO, i18nData);
                    log.debug("公告消息:全部会员:{}", userNoticeConfigPO.getId());
                    _this.saveTargetAll(userNoticeConfigPO, i18nData);
                    return ResponseVO.success();
                }
                //2：表示终端
            } else if (MemberTargetTypeEnum.TERMINAL.getCode() == userNoticeConfigAddVO.getTargetType()) {
                // 前端 送对象(1:会员2:终端 4：代理) 数据库 targetType  1:会员2:终端 4：代理
                //userNoticeConfigPO.setTargetType(MemberTargetTypeEnum.TERMINAL.getCode());
                if (ObjectUtil.isEmpty(userNoticeConfigAddVO.getTerminal())) {
                    return ResponseVO.fail(ResultCode.TERMINAL_NOT_NULL);
                }
                //userNoticeConfigPO.setTerminal(userNoticeConfigAddVO.getTerminal());
                saveNoticeConfig(userNoticeConfigPO, i18nData);
                log.debug("公告消息:插入终端消息:{}", userNoticeConfigPO.getId());
                _this.saveTargetTerminal(userNoticeConfigPO, i18nData);
            } else if (MemberTargetTypeEnum.AGENTS.getCode() == userNoticeConfigAddVO.getTargetType()) {
                // 代理
                // 全部代理
                if (AgentShipTypeEnum.ALL_AGENS.getCode() == userNoticeConfigAddVO.getNoticeAgentType()) {
                    saveNoticeConfig(userNoticeConfigPO, i18nData);
                    log.debug("公告消息:全部代理:{}", userNoticeConfigPO.getId());
                    _this.saveTargetAgentAll(userNoticeConfigPO,true);
                    return ResponseVO.success();
                } else {
                    //特定代理
                    /*if (ObjectUtil.isEmpty(userNoticeConfigAddVO.getFile()) || userNoticeConfigAddVO.getFile().isEmpty()) {
                        return ResponseVO.fail(ResultCode.ACCOUNT_NOT_NULL);
                    }*/
                    // 解析excel文件提取会员账号/代理账号
                    List<String> agentList = userNoticeConfigAddVO.getFileAccount();
                    if (CollectionUtils.isEmpty(agentList)) {
                        return ResponseVO.fail(ResultCode.ACCOUNT_NOT_NULL);
                    }
                    // 去重的用户
                    List<String> agentDisList = agentList.stream().distinct().toList();

                    List<String> exitsUserList = Lists.newArrayList();
                    // 代理账号,去重
                    List<AgentInfoVO> agentInfoList = agentInfoApi.getByAgentAccountsAndSiteCode(userNoticeConfigAddVO.getSiteCode(), agentDisList);

                    // 合并
                    List<String> notExitsUser;
                    if (CollectionUtils.isEmpty(agentInfoList)) {
                        notExitsUser = new ArrayList<>(agentList);
                    } else {
                        Map<Boolean, List<String>> map = agentList.stream().collect(Collectors.partitioningBy(s -> agentInfoList.stream().anyMatch(user -> user.getAgentAccount().equals(s))));

                        exitsUserList = Optional.ofNullable(map.get(true)).orElse(Lists.newArrayList());
                        notExitsUser = Optional.ofNullable(map.get(false)).orElse(Lists.newArrayList());
                        List<String> exitsUserIdList = agentInfoList.stream().map(AgentInfoVO::getAgentId).toList();
                        for (String userAccount : exitsUserIdList) {
                            UserNoticeTargetPO userNoticeTargetPO = new UserNoticeTargetPO();
                            //校验是否是特定会员 0：表示是全部会员 1：表示特定会员
                            userNoticeTargetPO.setNoticeType(userNoticeConfigPO.getNoticeType());
                            userNoticeTargetPO.setReadState(UserNoticeTargetConstant.UNREAD);
                            userNoticeTargetPO.setRevokeState(UserNoticeTargetConstant.NOT_REVOKED);
                            userNoticeTargetPO.setDeleteState(UserNoticeTargetConstant.NORMAL);
                            userNoticeTargetPO.setCreatedTime(System.currentTimeMillis());
                            userNoticeTargetPO.setUpdatedTime(System.currentTimeMillis());
                            userNoticeTargetPO.setUserId(userAccount);
                            userNoticeTargetPO.setPlatform(CommonConstant.business_two);
                            userNoticeTargetPOList.add(userNoticeTargetPO);
                        }
                    }

                    // 是否包含无效用户
                    if (CollectionUtils.isNotEmpty(notExitsUser)) {
                        //全部都是无效用户
                        if (agentInfoList.isEmpty()) {
                            return ResponseVO.fail(ResultCode.USER_NOT_EXIST, ResultCode.USER_NOT_EXIST.getDesc() + ":" + String.join(",", notExitsUser));
                        }
                        userNoticeConfigPO.setSendAccounts(String.join(",", exitsUserList));
                        saveNoticeConfig(userNoticeConfigPO, i18nData);
                        saveNoticeId(userNoticeTargetPOList, userNoticeConfigPO.getId());
                        log.debug("公告消息:特定代理:{}", userNoticeConfigPO.getId());
                        userNoticeTargetService.saveBatch(userNoticeTargetPOList);
                        //  wade 添加指定极光代理
                        //多语言插入
                        return ResponseVO.success(ResultCode.USER_NOT_EXIST.getDesc() + ":" + String.join(",", notExitsUser));
                    }
                    userNoticeConfigPO.setSendAccounts(String.join(",", exitsUserList));
                    // 前端 送对象(1:会员2:终端 4：代理) 数据库 targetType  1 全体会员 2 特定会员 3 终端 4 全部代理 5 特定代理
                    saveNoticeConfig(userNoticeConfigPO, i18nData);
                    saveNoticeId(userNoticeTargetPOList, userNoticeConfigPO.getId());
                    log.debug("公告消息:特定代理:{}", userNoticeConfigPO.getId());
                    userNoticeTargetService.saveBatch(userNoticeTargetPOList);
                    return ResponseVO.success();
                }
            } else if (MemberTargetTypeEnum.BUSINESS.getCode() == userNoticeConfigAddVO.getTargetType()){
                userNoticeConfigPO.setNoticeMerchantType(userNoticeConfigPO.getNoticeAgentType());
                if (AgentShipTypeEnum.ALL_AGENS.getCode() == userNoticeConfigAddVO.getNoticeAgentType()) {
                    saveNoticeConfig(userNoticeConfigPO, i18nData);
                    log.debug("公告消息:全部商务:{}", userNoticeConfigPO.getId());
                    _this.saveTargetAgentAll(userNoticeConfigPO,false);
                    return ResponseVO.success();
                }else {
                    List<String> agentList = userNoticeConfigAddVO.getFileAccount();
                    if (CollectionUtils.isEmpty(agentList)) {
                        return ResponseVO.fail(ResultCode.ACCOUNT_NOT_NULL);
                    }
                    // 去重的用户
                    List<String> agentDisList = agentList.stream().distinct().toList();

                    List<String> exitsUserList = Lists.newArrayList();
                    // 商务账号,去重
                    List<AgentMerchantVO> merchantList = agentMerchantApi.getListByAccounts(userNoticeConfigAddVO.getSiteCode(), agentDisList);

                    // 合并
                    List<String> notExitsUser;
                    if (CollectionUtils.isEmpty(merchantList)) {
                        notExitsUser = new ArrayList<>(agentList);
                    } else {
                        Map<Boolean, List<String>> map = agentList.stream().collect(Collectors.partitioningBy(s -> merchantList.stream().anyMatch(merchant -> merchant.getMerchantAccount().equals(s))));

                        exitsUserList = Optional.ofNullable(map.get(true)).orElse(Lists.newArrayList());
                        notExitsUser = Optional.ofNullable(map.get(false)).orElse(Lists.newArrayList());
                        List<String> exitsMerchantIdList = merchantList.stream().map(AgentMerchantVO::getMerchantId).toList();
                        for (String userAccount : exitsMerchantIdList) {
                            UserNoticeTargetPO userNoticeTargetPO = new UserNoticeTargetPO();
                            //校验是否是特定会员 0：表示是全部会员 1：表示特定会员
                            userNoticeTargetPO.setNoticeType(userNoticeConfigPO.getNoticeType());
                            userNoticeTargetPO.setReadState(UserNoticeTargetConstant.UNREAD);
                            userNoticeTargetPO.setRevokeState(UserNoticeTargetConstant.NOT_REVOKED);
                            userNoticeTargetPO.setDeleteState(UserNoticeTargetConstant.NORMAL);
                            userNoticeTargetPO.setCreatedTime(System.currentTimeMillis());
                            userNoticeTargetPO.setUpdatedTime(System.currentTimeMillis());
                            userNoticeTargetPO.setUserId(userAccount);
                            userNoticeTargetPO.setPlatform(CommonConstant.business_three);
                            userNoticeTargetPOList.add(userNoticeTargetPO);
                        }
                    }

                    // 是否包含无效用户
                    if (CollectionUtils.isNotEmpty(notExitsUser)) {
                        //全部都是无效用户
                        if (merchantList.isEmpty()) {
                            return ResponseVO.fail(ResultCode.USER_NOT_EXIST, ResultCode.USER_NOT_EXIST.getDesc() + ":" + String.join(",", notExitsUser));
                        }
                        userNoticeConfigPO.setSendAccounts(String.join(",", exitsUserList));
                        saveNoticeConfig(userNoticeConfigPO, i18nData);
                        saveNoticeId(userNoticeTargetPOList, userNoticeConfigPO.getId());
                        userNoticeTargetService.saveBatch(userNoticeTargetPOList);
                        //  wade 添加指定极光代理
                        //多语言插入
                        return ResponseVO.success(ResultCode.USER_NOT_EXIST.getDesc() + ":" + String.join(",", notExitsUser));
                    }
                    userNoticeConfigPO.setSendAccounts(String.join(",", exitsUserList));
                    // 前端 送对象(1:会员2:终端 4：代理) 数据库 targetType  1 全体会员 2 特定会员 3 终端 4 全部代理 5 特定代理
                    saveNoticeConfig(userNoticeConfigPO, i18nData);
                    saveNoticeId(userNoticeTargetPOList, userNoticeConfigPO.getId());
                    log.debug("公告消息:特定商务:{}", userNoticeConfigPO.getId());
                    userNoticeTargetService.saveBatch(userNoticeTargetPOList);
                    return ResponseVO.success();
                }
            }
            return ResponseVO.success();
        } catch (Exception e) {
            log.error("公告消息:新增通知异常：", e);
            throw new BaowangDefaultException(ResultCode.INSERT_NOTIFY);
        }
    }


    /**
     * 全部代理
     * <p>
     * 此方法用于将通知配置应用到所有代理用户，并将目标通知信息保存到目标表中。
     * 方法运行在异步线程池中，以提高处理效率。
     *
     * @param userNoticeConfigPO 用户通知配置对象，包含通知的相关配置信息
     *                           - id: 通知的唯一标识
     *                           - noticeType: 通知类型
     *                           - siteCode: 站点代码，用于获取该站点的所有代理
     *                           <p>
     */
    @Async(ThreadPoolConfig.USER_NOTICE_EXECUTOR)
    public void saveTargetAgentAll(UserNoticeConfigPO userNoticeConfigPO,boolean isAgent) {
        // 获取所有代理
        List<String> agentIds = new ArrayList<>();
        if (isAgent){
             agentIds = agentInfoApi.getALLAgentIds(userNoticeConfigPO.getSiteCode());
        }else {
             agentIds = agentMerchantApi.getList(userNoticeConfigPO.getSiteCode()).stream().map(AgentMerchantVO::getMerchantId).collect(Collectors.toList());
        }
        // 生成插入
        List<UserNoticeTargetPO> insertTarget = new ArrayList<>();
        for (String agentId : agentIds) {
            UserNoticeTargetPO po = new UserNoticeTargetPO();
            po.setNoticeId(userNoticeConfigPO.getId());
            po.setNoticeType(userNoticeConfigPO.getNoticeType());
            po.setUserId(agentId);
            po.setSiteCode(userNoticeConfigPO.getSiteCode());
            po.setReadState(UserNoticeTargetConstant.UNREAD);
            po.setRevokeState(UserNoticeTargetConstant.NOT_REVOKED);
            po.setDeleteState(UserNoticeTargetConstant.NORMAL);
            //target表中Platform=3是商务,=2是代理
            po.setPlatform(isAgent ?CommonConstant.business_two:CommonConstant.business_three);
            insertTarget.add(po);
        }
        // 分批插入，每批次1000条
        int batchSize = 1000;
        for (int i = 0; i < insertTarget.size(); i += batchSize) {
            // 获取当前批次的数据
            List<UserNoticeTargetPO> batchList = insertTarget.subList(i, Math.min(i + batchSize, insertTarget.size()));
            // 执行批量插入
            userNoticeTargetService.saveBatch(batchList);
        }


    }

    /**
     * 指定终端
     * <p>
     * 此方法用于为特定用户配置通知的终端展示，并根据终端类型进行推送操作。
     * 方法运行在异步线程池中，以支持高效的后台任务处理。
     *
     * @param userNoticeConfigPO 用户通知配置对象，包含通知相关的配置信息
     *                           - siteCode: 站点代码，用于过滤用户
     *                           - isPush: 是否需要推送（1 表示推送，0 表示不推送）
     *                           - terminal: 指定的终端类型集合，如 IOS_APP、Android_APP 等
     * @param i18nData           国际化数据，包含通知内容的多语言配置
     *                           - 键为语言代码（如 "en"，"zh"），值为国际化消息内容的列表
     */
    @Async(ThreadPoolConfig.USER_NOTICE_EXECUTOR)
    public void saveTargetTerminal(UserNoticeConfigPO userNoticeConfigPO, Map<String, List<I18nMsgFrontVO>> i18nData) {
        // 插入消息,给指定用户的客户端看,设置消息的终端
        LambdaQueryWrapper<UserInfoPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserInfoPO::getSiteCode, userNoticeConfigPO.getSiteCode())
                .select(UserInfoPO::getUserId);
        queryAndInsertTarget(lambdaQueryWrapper, userNoticeConfigPO, i18nData);
        // 指定终端 IOS_APP-3 Android_APP-5
        if (userNoticeConfigPO.getIsPush() == 1) {
            if (userNoticeConfigPO.getTerminal().contains(DeviceType.IOS_APP.getCode().toString())) {
                pushJGService.pushDeviceIOS(userNoticeConfigPO, i18nData);
            }
            if (userNoticeConfigPO.getTerminal().contains(DeviceType.Android_APP.getCode().toString())) {
                pushJGService.pushDeviceAndroid(userNoticeConfigPO, i18nData);
            }
        }


    }

    /**
     * 全部会员，所有注册的会员
     *
     * @param userNoticeConfigPO 入参
     * @param i18nData           国际化参数
     */
    @Async(ThreadPoolConfig.USER_NOTICE_EXECUTOR)
    public void saveTargetAll(UserNoticeConfigPO userNoticeConfigPO, Map<String, List<I18nMsgFrontVO>> i18nData) {
        // 插入消息,给指定用户的客户端看,设置消息的终端
        LambdaQueryWrapper<UserInfoPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserInfoPO::getSiteCode, userNoticeConfigPO.getSiteCode()).select(UserInfoPO::getUserId);
        queryAndInsertTarget(lambdaQueryWrapper, userNoticeConfigPO, i18nData);
        // 全部极光会员
        // 测试使用中文
        if (userNoticeConfigPO.getIsPush() == 1) {
            pushJGService.pushAll(userNoticeConfigPO, i18nData);
        }
        // test
        //pushJGService.pushByUserIds(userNoticeConfigPO, i18nData, Arrays.asList("92667773", "81472617"));

    }

    @Transactional
    public void queryAndInsertTarget(LambdaQueryWrapper<UserInfoPO> lambdaQueryWrapper, UserNoticeConfigPO userNoticeConfigPO, Map<String, List<I18nMsgFrontVO>> i18nData) {

        List<String> userIds = userInfoService.getBaseMapper().selectList(lambdaQueryWrapper).stream().map(UserInfoPO::getUserId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(userIds)) {
            log.debug("公告消息:插入公告消息，没有符合条件的id，消息id是: {}", userNoticeConfigPO.getId());
            return;
        }
        log.debug("公告消息:查询符合条件的插入消息的userID 个数: {}", userIds.size());
        // 生成插入
        List<UserNoticeTargetPO> insertTarget = new ArrayList<>();
        for (String userId : userIds) {
            log.debug("公告消息:查询符合条件的插入消息的userID: {}", userId);
            UserNoticeTargetPO po = new UserNoticeTargetPO();
            po.setNoticeId(userNoticeConfigPO.getId());
            po.setNoticeType(userNoticeConfigPO.getNoticeType());
            po.setUserId(userId);
            po.setSiteCode(userNoticeConfigPO.getSiteCode());
            po.setReadState(UserNoticeTargetConstant.UNREAD);
            po.setRevokeState(UserNoticeTargetConstant.NOT_REVOKED);
            po.setDeleteState(UserNoticeTargetConstant.NORMAL);
            po.setPlatform(CommonConstant.business_one);
            insertTarget.add(po);
        }
        // 分批插入，每批次1000条
        int batchSize = 1000;
        for (int i = 0; i < insertTarget.size(); i += batchSize) {
            // 获取当前批次的数据
            List<UserNoticeTargetPO> batchList = insertTarget.subList(i, Math.min(i + batchSize, insertTarget.size()));
            // 执行批量插入
            userNoticeTargetService.saveBatch(batchList);
            // 仅处理按照vip等级，主货币的会员发送极光配置

            if (userNoticeConfigPO.getIsPush() == 1 && userNoticeConfigPO.getMemberShipType() == MemberShipTypeEnum.SPECIFIC_MEMBERS.getCode() && userNoticeConfigPO.getTargetType() == MemberTargetTypeEnum.MEMBERS.getCode()) {
                List<String> userIdSends = batchList.stream().map(UserNoticeTargetPO::getUserId).collect(Collectors.toList());
                pushJGService.pushByUserIds(userNoticeConfigPO, i18nData, userIdSends);

            }
            //pushJGService.push(batchList);
        }

    }

    /**
     * 根据主货币指定通知目标
     * <p>
     * 此方法用于将通知目标设定为使用指定主货币的用户，并生成通知目标记录。
     * 方法运行在异步线程池中，以提高处理效率。
     *
     * @param userNoticeConfigPO 用户通知配置对象，包含通知的相关配置信息：
     *                           - id: 通知的唯一标识
     *                           - siteCode: 站点代码，用于筛选符合条件的用户
     * @param currencyCode       主货币代码，用于筛选使用该主货币的用户
     * @param i18nData           国际
     */
    @Async(ThreadPoolConfig.USER_NOTICE_EXECUTOR)
    public void saveTargetByCurrency(UserNoticeConfigPO userNoticeConfigPO, String currencyCode, Map<String, List<I18nMsgFrontVO>> i18nData) {
        // 查询主货币
        LambdaQueryWrapper<UserInfoPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserInfoPO::getSiteCode, userNoticeConfigPO.getSiteCode()).eq(UserInfoPO::getMainCurrency, currencyCode).select(UserInfoPO::getUserId);
        queryAndInsertTarget(lambdaQueryWrapper, userNoticeConfigPO, i18nData);

    }

    /**
     * 指定会员或者代理
     * <p>
     * 此方法用于将通知目标设定为符合条件的会员或代理，并根据会员或代理的 VIP 等级范围生成通知目标记录。
     * 方法运行在异步线程池中，以提高处理效率。
     *
     * @param userNoticeConfigPO 用户通知配置对象，包含通知的相关配置信息：
     *                           - id: 通知的唯一标识
     *                           - siteCode: 站点代码，用于筛选符合条件的用户
     * @param vipGradeMin        最低 VIP 等级，用于筛选会员或代理的 VIP 范围
     * @param vipGradeMax        最高 VIP 等级，用于筛选会员或代理的 VIP 范围
     * @param i18nData           国际化数据，用于多语言支持的推送信息内容
     */
    @Async(ThreadPoolConfig.USER_NOTICE_EXECUTOR)
    public void saveTarget(UserNoticeConfigPO userNoticeConfigPO, Integer vipGradeMin, Integer vipGradeMax, Map<String, List<I18nMsgFrontVO>> i18nData) {
        // 查询主货币
        LambdaQueryWrapper<UserInfoPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserInfoPO::getSiteCode, userNoticeConfigPO.getSiteCode()).le(UserInfoPO::getVipGradeCode, vipGradeMax).ge(UserInfoPO::getVipGradeCode, vipGradeMin).select(UserInfoPO::getUserId);
        queryAndInsertTarget(lambdaQueryWrapper, userNoticeConfigPO, i18nData);
        // 根据userId进行推送


    }

    /**
     * 插入消息模版配置
     *
     * @param userNoticeConfigPO 消息模版内容
     * @param i18nData           国际化标题与内容
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveNoticeConfig(UserNoticeConfigPO userNoticeConfigPO, Map<String, List<I18nMsgFrontVO>> i18nData) {
        userNoticeConfigRepository.insert(userNoticeConfigPO);
        ResponseVO<Boolean> i18Bool = i18nApi.insert(i18nData);
        if (!i18Bool.isOk() || !i18Bool.getData()) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Async(ThreadPoolConfig.USER_NOTICE_EXECUTOR)
    public void saveLangNoticeConfig(String id, UserNoticeConfigAddVO userNoticeConfigAddVO) {
       /* languageNoticeConfigService.addLanguageNotice(LanguageNoticeCodeEnum.TITLE.getCode(), id, userNoticeConfigAddVO.getNoticeTitleList());
        languageNoticeConfigService.addLanguageNotice(LanguageNoticeCodeEnum.MESSAGE_CONTENT.getCode(), id, userNoticeConfigAddVO.getMessageContentList());*/
    }


    /**
     * 系统消息添加通知，不插入noticeConfig表，直接插入tager表
     *
     * @param sysNoticeConfigAddVO 入参
     */
    @Transactional
    public ResponseVO<?> addUserSysNoticeConfig(UserSysNoticeConfigAddVO sysNoticeConfigAddVO) {
        // 获取分布式锁
        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getAddUserSystemMessage(sysNoticeConfigAddVO.getUserAccount(), CommonConstant.UNDERSCORE + sysNoticeConfigAddVO.getBusinessLine().toString() + sysNoticeConfigAddVO.getMessageType().toString()));
        try {
            // 尝试获取锁，等待 20 秒，锁的持有时间为 30 秒
            if (rLock.tryLock(20000, 30000L, TimeUnit.MILLISECONDS)) {
                // 根据值获取产品线与业务类型查询 configId
                UserNoticeTargetPO userNoticeTargetPO = new UserNoticeTargetPO();
                //1：表示特定会员
                userNoticeTargetPO.setNoticeType(sysNoticeConfigAddVO.getNoticeType());
                userNoticeTargetPO.setBusinessLine(sysNoticeConfigAddVO.getBusinessLine());
                userNoticeTargetPO.setMessageType(sysNoticeConfigAddVO.getMessageType());
                userNoticeTargetPO.setReadState(UserNoticeTargetConstant.UNREAD);
                userNoticeTargetPO.setRevokeState(UserNoticeTargetConstant.NOT_REVOKED);
                userNoticeTargetPO.setDeleteState(UserNoticeTargetConstant.NORMAL);
                userNoticeTargetPO.setCreatedTime(System.currentTimeMillis());
                userNoticeTargetPO.setUpdatedTime(System.currentTimeMillis());
                // 系统消息都是特定消息
                //userNoticeTargetPO.setNoticeTitle(sysNoticeConfigAddVO.getNoticeTitle());
                userNoticeTargetPO.setUserId(sysNoticeConfigAddVO.getUserAccount());
                // 查询出的消息类型 不关联config表
                userNoticeTargetPO.setNoticeId("0");
                // 插入target表
                userNoticeTargetPO.setNoticeType(sysNoticeConfigAddVO.getNoticeType());
                //userNoticeTargetPO.setMessageContent(sysNoticeConfigAddVO.getMessageContent());
                userNoticeTargetService.save(userNoticeTargetPO);
                UserNoticeConfigAddVO addVO = new UserNoticeConfigAddVO();

                saveLangNoticeConfig(userNoticeTargetPO.getId(), addVO);
                return ResponseVO.success();
            } else {
                return ResponseVO.fail(ResultCode.INSERT_SYS_NOTICE_NOTIFY);
            }
        } catch (Exception e) {
            log.error("新增系统消息通知异常：", e);
            return ResponseVO.fail(ResultCode.INSERT_SYS_NOTICE_NOTIFY);
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.info("新增系统消息通知{}", RedisKeyTransUtil.getAddUserSystemMessage(sysNoticeConfigAddVO.getUserAccount(), CommonConstant.UNDERSCORE + sysNoticeConfigAddVO.getBusinessLine().toString() + sysNoticeConfigAddVO.getMessageType().toString()));
            }
        }
    }


    /**
     * 批量设置通知主键 ID
     * <p>
     * 此方法用于为一组代理通知请求对象批量设置通知的唯一标识 (`noticeId`)。
     * 通常在生成通知记录后，使用该方法将通知 ID 注入到目标对象中。
     *
     * @param userNoticeTargetPOList 代理通知请求对象列表，每个对象代表一个目标代理的通知信息：
     *                               - `AgentNoticeReq` 对象包含通知相关的属性，例如代理 ID、通知类型等。
     * @param id                     主键
     */
    private void saveNoticeId(List<UserNoticeTargetPO> userNoticeTargetPOList, String id) {
        userNoticeTargetPOList.forEach(po -> po.setNoticeId(id));
    }


    /**
     * 撤回通知
     * <p>
     * 此方法用于撤回指定的用户通知配置，并更新相关通知目标的状态。
     * 方法在事务中执行，确保所有数据库操作要么成功，要么全部回滚。
     *
     * @param userNoticeConfigGetVO 用户通知配置请求参数
     *                              - id: 通知配置的唯一标识
     * @return ResponseVO<?> 成功时返回操作成功的响应对象；若操作失败，抛出异常
     * @throws BaowangDefaultException 如果撤回操作失败，抛出自定义异常
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> updateUserNoticeConfig(UserNoticeConfigGetVO userNoticeConfigGetVO) {
        try {
            UserNoticeConfigPO userNoticeConfigRecord = this.baseMapper.selectById(userNoticeConfigGetVO.getId());
            if (Objects.nonNull(userNoticeConfigRecord.getIsPush()) && userNoticeConfigRecord.getIsPush() != 1) {
                LambdaUpdateWrapper<UserNoticeConfigPO> wrapper = new LambdaUpdateWrapper<>();
                wrapper.eq(UserNoticeConfigPO::getId, userNoticeConfigGetVO.getId());
                wrapper.set(UserNoticeConfigPO::getStatus, UserNoticeTargetConstant.REVOCATION);
                this.update(wrapper);
                LambdaUpdateWrapper<UserNoticeTargetPO> userNoticeTargetPOLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                userNoticeTargetPOLambdaUpdateWrapper.eq(UserNoticeTargetPO::getNoticeId, userNoticeConfigGetVO.getId());
                userNoticeTargetPOLambdaUpdateWrapper.eq(UserNoticeTargetPO::getReadState, UserNoticeTargetConstant.UNREAD);
                userNoticeTargetPOLambdaUpdateWrapper.set(UserNoticeTargetPO::getRevokeState, UserNoticeTargetConstant.READ_UNDO);
                userNoticeTargetService.update(userNoticeTargetPOLambdaUpdateWrapper);
                if (userNoticeConfigRecord.getPopUpType() != null) {
                    if (CommonConstant.business_one.equals(userNoticeConfigRecord.getPopUpType())) {
                        RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_MARQUEE_CONTENT_CACHE));

                    }
                }
                return ResponseVO.success();
            } else {
                return ResponseVO.fail(ResultCode.WITHDRAW_NOTIFICATION);
            }

        } catch (Exception e) {
            log.error("撤回通知异常：", e);
            throw new BaowangDefaultException(ResultCode.WITHDRAW);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean del(Long id) {
        UserNoticeConfigPO userNoticeConfigPO = userNoticeConfigRepository.selectById(id);
        if (ObjectUtil.isEmpty(userNoticeConfigPO)) {
            return true;
        }
        /*if (NotificationTypeEnum.ANNOUNCEMENT.getCode() != userNoticeConfigPO.getNoticeType()) {
            // 删除仅支持公告类型
            return false;
        }*/
        userNoticeConfigRepository.deleteById(id);
        userNoticeTargetService.remove(Wrappers.<UserNoticeTargetPO>lambdaQuery().eq(UserNoticeTargetPO::getNoticeId, id));
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean edit(UserNoticeConfigEditVO userNoticeConfigEditVO) {
        /*if (NotificationTypeEnum.ANNOUNCEMENT.getCode() != userNoticeConfigEditVO.getNoticeType()) {
            // 编辑仅支持公告类型
            return false;
        }*/
        UserNoticeConfigPO userNoticeConfigPO = userNoticeConfigRepository.selectById(userNoticeConfigEditVO.getId());
        if (ObjectUtil.isEmpty(userNoticeConfigPO)) {
            return true;
        }
        // 插入国际化信息
        // 只更新标题与内容
        Map<String, List<I18nMsgFrontVO>> i18nData = Map.of(userNoticeConfigPO.getNoticeTitleI18nCode(), userNoticeConfigEditVO.getNoticeTitleI18nCodeList(), userNoticeConfigPO.getMessageContentI18nCode(), userNoticeConfigEditVO.getMessageContentI18nCodeList());
        // 更新人与更新时间
        userNoticeConfigPO.setUpdater(userNoticeConfigEditVO.getOperator());
        userNoticeConfigPO.setUpdatedTime(System.currentTimeMillis());
        userNoticeConfigRepository.updateById(userNoticeConfigPO);
        ResponseVO<Boolean> i18Bool = i18nApi.update(i18nData);
        if (!i18Bool.isOk() || !i18Bool.getData()) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        return true;
    }

    /**
     * 查看排序
     */
    public ResponseVO<List<NoticeSortSelectResponseVO>> sortNoticeSelect(SortNoticeSelectVO vo) {
        List<Integer> targetList = new ArrayList<>();
        if (vo.getNoticeType() == 1) {
            targetList.add(MemberTargetTypeEnum.MEMBERS.getCode());
            targetList.add(MemberTargetTypeEnum.TERMINAL.getCode());
        } else if (vo.getNoticeType() == 4) {
            targetList.add(MemberTargetTypeEnum.AGENTS.getCode());
        }


        List<UserNoticeConfigPO> list = this.lambdaQuery()
                .eq(UserNoticeConfigPO::getNoticeType, CommonConstant.business_one)
                .in(UserNoticeConfigPO::getTargetType, targetList)
                .eq(UserNoticeConfigPO::getSiteCode, vo.getSiteCode())
                .orderByAsc(UserNoticeConfigPO::getSort).list();
        return ResponseVO.success(ConvertUtil.entityListToModelList(list, NoticeSortSelectResponseVO.class));
    }

    /**
     * 排序通知配置
     * <p>
     * 此方法根据前端传递的通知配置列表重新设置排序值，
     * 按照列表的顺序依次更新每个通知配置的排序字段。
     *
     * @param vo 通知排序请求参数，包含分类列表及操作人信息
     *           - categoryList: 通知分类列表，每个元素包含通知ID
     *           - operator: 操作者信息
     * @return ResponseVO<?> 成功时返回操作成功的响应对象
     * 如果更新失败则抛出相应的异常
     */
    public ResponseVO<?> sortNotice(NoticeConfigResortVO vo) {
        // 查询原有的排序
        List<NoticeSortSelectResponseVO> list = vo.getCategoryList();
        List<UserNoticeConfigPO> activityBases = Lists.newArrayList();
        for (int i = 0; i < list.size(); i++) {
            NoticeSortSelectResponseVO bo = list.get(i);
            UserNoticeConfigPO byId = this.getById(bo.getId());
            byId.setSort(i + 1);
            byId.setUpdater(vo.getOperator());
            byId.setUpdatedTime(System.currentTimeMillis());
            activityBases.add(byId);
        }
        this.updateBatchById(activityBases);
        RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_MARQUEE_CONTENT_CACHE));
        return ResponseVO.success();
    }


}
