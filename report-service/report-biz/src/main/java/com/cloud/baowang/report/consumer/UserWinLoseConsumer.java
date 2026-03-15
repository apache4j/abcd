package com.cloud.baowang.report.consumer;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.BetGameTypeEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.vo.UserWinLoseMqVO;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.report.po.ReportUserWinLoseMessagePO;
import com.cloud.baowang.report.po.ReportUserWinLosePO;
import com.cloud.baowang.report.repositories.ReportUserWinLoseMessageRepository;
import com.cloud.baowang.report.repositories.ReportUserWinLoseRepository;
import com.cloud.baowang.report.service.ReportUserWinLoseMqMessageService;
import com.cloud.baowang.report.service.ReportUserWinLoseService;
import com.cloud.baowang.report.service.UserWinLoseListenerService;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
@AllArgsConstructor
public class UserWinLoseConsumer {

    private final UserWinLoseListenerService userWinLoseListenerService;

    private final ReportUserWinLoseMqMessageService reportUserWinLoseMqMessageService;

    private final ReportUserWinLoseMessageRepository reportUserWinLoseMessageRepository;

    private final ReportUserWinLoseRepository reportUserWinLoseRepository;

    private final ReportUserWinLoseService reportUserWinLoseService;

    private final SiteApi siteApi;

    private final UserInfoApi userInfoApi;

    private final AgentInfoApi agentInfoApi;

    /**
     * 缺少vip福利。todo wade
     *
     * @param vo      消息
     * @param ackItem 提交
     */
    @KafkaListener(topics = TopicsConstants.USER_WIN_LOSE_CHANNEL, groupId = GroupConstants.USER_WIN_LOSE_CHANNEL_GROUP)
    public void betOrderMessage(UserWinLoseMqVO vo, Acknowledgment ackItem) {
        // 检查消息体是否为空
        if (null == vo) {
            log.error("会员每日盈亏-MQ队列-参数不能为空");
            return;
        }
        // 将消息对象转换为 JSON 字符串，用于日志记录
        String jsonStr = JSON.toJSONString(vo);
        log.info("会员每日盈亏(==============MQ队列==============)参数:{}", jsonStr);
        try{
            long start = System.currentTimeMillis();
            // 检查是否存在订单列表
            if (CollUtil.isNotEmpty(vo.getOrderList())) {
                // 处理订单列表
                //processOrderList(vo.getOrderList(), start);
                // 新接口
                processOrderListBatch(vo.getOrderList(), start);
            } else {
                // 处理单个订单
                processSingleOrder(vo, start, jsonStr);
            }
        }catch (Exception e){
            log.info("会员每日盈亏(==============MQ队列==============)参数:{}", e.getMessage());
        }finally {
            // 确认消息已处理
            ackItem.acknowledge();
        }

    }

    private void processOrderList(List<UserWinLoseMqVO> orderList, long start) {
        for (UserWinLoseMqVO item : orderList) {
            // 验证必填参数
            // 组装消息并保存到数据库
            ReportUserWinLoseMessagePO mqMessage = new ReportUserWinLoseMessagePO();
            assemblyMessage(mqMessage, item);
            if (isOrderItemValid(item)) {
                try {
                    // 判断是否处理过，第一次处理为成功，其他处理为失败
                    if (checkOrProcess(mqMessage, item)) {
                        dealWithFailMsgRePeat(item);
                        return;
                    }
                    reportUserWinLoseMqMessageService.save(mqMessage);

                    // 处理业务逻辑
                    handleBusiness(start, item);
                    // 更新成功状态
                    dealWithSuccessMsg(mqMessage);
                } catch (Exception e) {
                    log.error("********************会员每日盈亏-MQ队列-数据重复(触发到唯一索引),当前注单数据:{}********************", JSON.toJSONString(item));
                    log.error("********************会员每日盈亏-MQ队列-数据重复(触发到唯一索引),当前注单数据:{}********************", e.getMessage(), e);
                    // 如果处理失败，更新为失败状态
                    dealWithFailMsg(item);
                }
            } else {
                log.error("会员每日盈亏-MQ队列-必填参数为空,当前注单:{}", item);
                // 参数不全
                mqMessage.setStatus(2);
                reportUserWinLoseMqMessageService.save(mqMessage);
            }
        }
    }

    /**
     * 构建唯一键的集合，用于快速判断记录是否已存在。
     * 每条记录通过 userId、dayHourMillis、agentId 生成唯一标识符（字符串），
     * 使用 Set 可实现 O(1) 的查重效率，适合大批量数据对比。
     *
     * @param items 要处理的 ReportUserWinLosePO 列表
     * @return 包含唯一键的 Set，用于后续快速匹配
     */
    private Set<String> buildUniqueKeySet(List<ReportUserWinLosePO> items) {
        return items.stream()
                .map(item -> generateKey(item.getUserId(), item.getDayHourMillis(), item.getAgentId()))
                .collect(Collectors.toSet());
    }

    /**
     * 根据 userId、dayHourMillis、agentId 生成唯一标识字符串，用于记录比对。
     * 格式为：userId|dayHourMillis|agentId
     * - 若字段为 null，会自动转换为 "[NULL]"，防止字符串拼接歧义。
     * - 此方法主要用于数据幂等判断、去重等场景。
     *
     * @param userId        用户ID
     * @param dayHourMillis 小时粒度的时间戳
     * @param agentId       代理ID，可能为 null
     * @return 拼接后的唯一标识字符串
     */
    private String generateKey(Object userId, Object dayHourMillis, Object agentId) {
        return userId + "|" + dayHourMillis + "|" + agentId;
    }

    // 处理订单列表的方法
    public void processOrderListBatch(List<UserWinLoseMqVO> orderList, long start) {
        // 批量查询，1. 校验参数，2.过滤已经成功消费的
        List<UserWinLoseMqVO> orderListValid = new ArrayList<>();
        // 重算的
        List<UserWinLoseMqVO> orderListValidReCalc = new ArrayList<>();
        for (UserWinLoseMqVO vo : orderList) {
            if (isOrderItemValid(vo) && isOrderItemValidBizTwo(vo)) {
                // 重算的，取消的，都单独走之前的逻辑
                if (ObjUtil.isNotNull(vo.getLastDayHour())
                        || ObjUtil.equals(vo.getOrderStatus(), OrderStatusEnum.CANCEL.getCode())
                        || ObjUtil.equals(vo.getOrderStatus(), OrderStatusEnum.RESETTLED.getCode())) {
                    orderListValidReCalc.add(vo);
                } else {
                    orderListValid.add(vo);
                }
                //
            }
        }
        // 查询出需要处理不重算的
        List<UserWinLoseMqVO> userWinLoseMqVOS = checkBatchOrProcess(orderListValid);

        if (!CollUtil.isEmpty(userWinLoseMqVOS)) {
            // 区分重算的，不重算的走批量处理，重算的走之前的逻辑
            List<ReportUserWinLoseMessagePO> mqMessagePOList = new ArrayList<>();
            // 每个小时的时间
            Set<Long> dayHourMillisSet = new HashSet<>();
            // 用户id set
            Set<String> userIdSet = new HashSet<>();
            // userId List<uuid>
            Map<String, List<String>> messageMap = new HashMap<>();
            Set<String> agentIdSet = new HashSet<>();
            for (UserWinLoseMqVO item : userWinLoseMqVOS) {
                ReportUserWinLoseMessagePO mqMessage = new ReportUserWinLoseMessagePO();
                assemblyMessage(mqMessage, item);
                mqMessagePOList.add(mqMessage);
                Long dateUTCHourTime = TimeZoneUtils.convertToUtcStartOfHour(item.getDayHourMillis());
                dayHourMillisSet.add(dateUTCHourTime);
                userIdSet.add(item.getUserId());
                if (item.getAgentId() != null) {
                    agentIdSet.add(item.getAgentId());
                }
                // 组装messageMap
                messageMap.computeIfAbsent(item.getUserId(), k -> new ArrayList<>())
                        .add(mqMessage.getJsonStrUuid());

            }

            // 批量插入会员盈亏消费消息
            reportUserWinLoseMqMessageService.saveBatch(mqMessagePOList);
            ResponseVO<List<SiteVO>> listResponseVO = siteApi.siteInfoAllstauts();
            // 获取所有siteCode timezone
            Map<String, String> siteMap = new HashMap<>();
            if (listResponseVO.isOk()) {
                List<SiteVO> data = listResponseVO.getData();
                for (SiteVO vo : data) {
                    siteMap.put(vo.getSiteCode(), vo.getTimezone());
                }
            }
            // userInfo
            List<UserInfoVO> userInfoByUserIds = userInfoApi.getUserInfoByUserIds(new ArrayList<>(userIdSet));
            Map<String, UserInfoVO> userInfoMap = userInfoByUserIds.stream().collect(Collectors.toMap(UserInfoVO::getUserId, vo -> vo));
            List<AgentInfoVO> byAgentIds = agentInfoApi.getByAgentIds(new ArrayList<>(agentIdSet));
            Map<String, AgentInfoVO> agentInfoVOMap = byAgentIds.stream().collect(Collectors.toMap(AgentInfoVO::getAgentId, vo -> vo));
            // 场景是对这些集合的人员进行组装  ，不是很多人，一个人来了很多次投注，加锁看看
            // userid+long+agentId
            Map<String, UserWinLoseMqVO> userWinLoseMqVOMap = new HashMap<>();

            for (UserWinLoseMqVO item : userWinLoseMqVOS) {
                if (ObjUtil.equals(OrderStatusEnum.CANCEL.getCode(), item.getOrderStatus())) {
                    // 把除了投注的都计算为0
                    // item.setBetAmount(BigDecimal.ZERO);
                    item.setBetWinLose(BigDecimal.ZERO);
                    item.setTipsAmount(BigDecimal.ZERO);
                    item.setValidBetAmount(BigDecimal.ZERO);
                    item.setLastBetAmount(BigDecimal.ZERO);
                    item.setLastBetWinLose(BigDecimal.ZERO);
                    item.setLastTipsAmount(BigDecimal.ZERO);
                    item.setLastValidBetAmount(BigDecimal.ZERO);
                }
                String userKey = generateKey(item.getUserId(), item.getDayHourMillis(), item.getAgentId());
                if (!userWinLoseMqVOMap.containsKey(userKey)) {
                    if (Objects.equals(BetGameTypeEnum.FREE_SPIN.getCode(),item.getBetType())){
                        item.setBetNum(0);
                        item.setAlreadyUseAmount(item.getBetWinLose() == null?BigDecimal.ZERO :item.getBetWinLose());
                        item.setBetWinLose(BigDecimal.ZERO);
                    }else{
                        item.setBetNum(1);
                    }
                    userWinLoseMqVOMap.put(userKey, item);
                } else {
                    // 重复的，累加
                    UserWinLoseMqVO userWinLoseMqVO = userWinLoseMqVOMap.get(userKey);
                    if (userWinLoseMqVO.getBetAmount() == null) userWinLoseMqVO.setBetAmount(BigDecimal.ZERO);
                    if (userWinLoseMqVO.getBetWinLose() == null) userWinLoseMqVO.setBetWinLose(BigDecimal.ZERO);
                    if (userWinLoseMqVO.getTipsAmount() == null) userWinLoseMqVO.setTipsAmount(BigDecimal.ZERO);
                    if (userWinLoseMqVO.getValidBetAmount() == null) userWinLoseMqVO.setValidBetAmount(BigDecimal.ZERO);
                    if (userWinLoseMqVO.getAlreadyUseAmount() == null) userWinLoseMqVO.setAlreadyUseAmount(BigDecimal.ZERO);

                    userWinLoseMqVO.setBetAmount(userWinLoseMqVO.getBetAmount().add(
                            item.getBetAmount() == null ? BigDecimal.ZERO : item.getBetAmount()));
                    if (Objects.equals(BetGameTypeEnum.FREE_SPIN.getCode(),item.getBetType())){
                        userWinLoseMqVO.setBetWinLose(userWinLoseMqVO.getBetWinLose().add(
                                item.getBetWinLose() == null ? BigDecimal.ZERO : BigDecimal.ZERO));
                        userWinLoseMqVO.setAlreadyUseAmount(userWinLoseMqVO.getAlreadyUseAmount().add(item.getBetWinLose()));
                    }else{
                        userWinLoseMqVO.setBetWinLose(userWinLoseMqVO.getBetWinLose().add(
                                item.getBetWinLose() == null ? BigDecimal.ZERO : item.getBetWinLose()));
                        userWinLoseMqVO.setBetNum(userWinLoseMqVO.getBetNum() + 1);
                    }
                    userWinLoseMqVO.setTipsAmount(userWinLoseMqVO.getTipsAmount().add(
                            item.getTipsAmount() == null ? BigDecimal.ZERO : item.getTipsAmount()));
                    userWinLoseMqVO.setValidBetAmount(userWinLoseMqVO.getValidBetAmount().add(
                            item.getValidBetAmount() == null ? BigDecimal.ZERO : item.getValidBetAmount()));
                }
            }
            List<UserWinLoseMqVO> userWinLoseMqVOSFinal = new ArrayList<>(userWinLoseMqVOMap.values());
            List<String> successList = new ArrayList<>();
            List<String> failList = new ArrayList<>();
            // 批量处理
            for (UserWinLoseMqVO item : userWinLoseMqVOSFinal) {
                try {
                    userWinLoseListenerService.userWinLoseBatch(item, siteMap, userInfoMap.get(item.getUserId()), agentInfoVOMap);

                    successList.addAll(messageMap.get(item.getUserId()));
                } catch (Exception e) {
                    log.error("********************会员每日盈亏-错误：", e);
                    log.error("********************会员每日盈亏-信息：{}", JSON.toJSONString(item));
                    failList.addAll(messageMap.get(item.getUserId()));
                }
            }
            // 处理成功的
            dealWithSuccessMsgBatch(successList);
            // 单个处理重算的
            dealWithFailMsgBatch(failList);

        }
        // 处理重算的
        for (UserWinLoseMqVO item : orderListValidReCalc) {

            processSingleOrder(item, start, JSON.toJSONString(item));
        }

    }


    /**
     * 处理单个订单的方法
     *
     * @param vo      kafka消息
     * @param start   时间
     * @param jsonStr 字符串
     */
    public void processSingleOrder(UserWinLoseMqVO vo, long start, String jsonStr) {
        // 验证必填参数
        if (isSingleOrderValid(vo)) {
            ReportUserWinLoseMessagePO mqMessage = new ReportUserWinLoseMessagePO();
            try {
                // 组装消息并保存到数据库
                assemblySingleMessage(mqMessage, vo, jsonStr);
                //  // 判断是否处理过，第一次处理为成功，其他处理为失败
                if (checkOrProcess(mqMessage, vo)) {
                    dealWithFailMsgRePeat(vo);
                    return;
                }
                reportUserWinLoseMqMessageService.save(mqMessage);
                // 处理业务逻辑
                handleBusiness(start, vo);
                // 更新成功状态
                dealWithSuccessMsg(mqMessage);
            } catch (Exception e) {
                log.error("********************会员每日盈亏-MQ队列-数据重复(触发到唯一索引)", JSON.toJSONString(e));
                log.error("********************会员每日盈亏-MQ队列-数据重复(触发到唯一索引),当前注单数据:{}********************", jsonStr);
                // 如果处理失败，更新为失败状态
                dealWithFailMsg(vo);
            }
        } else {
            log.error("会员每日盈亏-MQ队列-必填参数为空,参数:{}", jsonStr);
        }
    }

    // 检查订单项是否有效
    private boolean isOrderItemValid(UserWinLoseMqVO item) {
        return item != null &&
                item.getDayHourMillis() != null &&
                StrUtil.isNotEmpty(item.getUserId()) &&
                item.getBizCode() != null &&
                item.getOrderId() != null
                && item.getBizCode() != 1; // 未结算的不处理
    }

    // 检查订单项是否有效 结算的订单是否有效 biz=2
    private boolean isOrderItemValidBizTwo(UserWinLoseMqVO item) {
        return null != item.getValidBetAmount()
                && null != item.getBetWinLose()
                && null != item.getOrderStatus()
                && null != item.getBetAmount();
    }

    // 检查单个订单是否有效
    private boolean isSingleOrderValid(UserWinLoseMqVO vo) {
        return vo != null &&
                vo.getDayHourMillis() != null &&
                StrUtil.isNotEmpty(vo.getUserId()) &&
                vo.getBizCode() != null &&
                vo.getOrderId() != null;
    }

    /**
     * 重复消费记录
     *
     * @param userWinLoseMqVO 重复消费
     */
    private void dealWithFailMsgRePeat(UserWinLoseMqVO userWinLoseMqVO) {
        String jsonString = JSON.toJSONString(userWinLoseMqVO);
        String uuid = UUID.nameUUIDFromBytes(jsonString.getBytes()).toString();
        ReportUserWinLoseMessagePO messagePO = new ReportUserWinLoseMessagePO();
        messagePO.setType(userWinLoseMqVO.getBizCode());
        messagePO.setTypeOrder(userWinLoseMqVO.getOrderId());
        messagePO.setJsonStr(JSON.toJSONString(userWinLoseMqVO));
        messagePO.setJsonStrUuid(uuid);
        messagePO.setCreatedTime(System.currentTimeMillis());
        messagePO.setUpdatedTime(System.currentTimeMillis());
        messagePO.setStatus(CommonConstant.business_three);
        reportUserWinLoseMqMessageService.save(messagePO);

    }

    /**
     * 重复消费 失败处理
     */
    private void dealWithFailMsg(UserWinLoseMqVO userWinLoseMqVO) {
        String jsonString = JSON.toJSONString(userWinLoseMqVO);
        String uuid = UUID.nameUUIDFromBytes(jsonString.getBytes()).toString();

        // 不知道是否在哪一步，是否插入
        ReportUserWinLoseMessagePO queryOne = reportUserWinLoseMessageRepository
                .selectOne(new LambdaQueryWrapper<ReportUserWinLoseMessagePO>()
                        .eq(ReportUserWinLoseMessagePO::getType, userWinLoseMqVO.getBizCode())
                        .eq(ReportUserWinLoseMessagePO::getJsonStrUuid, uuid)
                        .last("LIMIT 1")); // 只获取一条记录，即id最大的
        if (queryOne == null) {
            ReportUserWinLoseMessagePO messagePO = new ReportUserWinLoseMessagePO();
            messagePO.setType(userWinLoseMqVO.getBizCode());
            messagePO.setTypeOrder(userWinLoseMqVO.getOrderId());
            messagePO.setJsonStr(JSON.toJSONString(userWinLoseMqVO));
            messagePO.setCreatedTime(System.currentTimeMillis());
            messagePO.setUpdatedTime(System.currentTimeMillis());
            messagePO.setStatus(CommonConstant.business_zero);
            messagePO.setJsonStrUuid(uuid);
            reportUserWinLoseMqMessageService.save(messagePO);
        } else {
            ReportUserWinLoseMessagePO update = new ReportUserWinLoseMessagePO();
            update.setId(queryOne.getId());
            update.setStatus(CommonConstant.business_zero);
            update.setUpdatedTime(System.currentTimeMillis());
            reportUserWinLoseMqMessageService.updateById(update);
        }


    }

    private void dealWithSuccessMsg(ReportUserWinLoseMessagePO mqMessage) {
        ReportUserWinLoseMessagePO update = new ReportUserWinLoseMessagePO();
        update.setId(mqMessage.getId());
        update.setStatus(CommonConstant.business_one);
        reportUserWinLoseMqMessageService.updateById(update);
    }

    private void dealWithSuccessMsgBatch(List<String> uuids) {
        if (CollUtil.isEmpty(uuids)) {
            return;
        }

        LambdaUpdateWrapper<ReportUserWinLoseMessagePO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ReportUserWinLoseMessagePO::getJsonStrUuid, uuids);
        updateWrapper.set(ReportUserWinLoseMessagePO::getStatus, CommonConstant.business_one);

        // 第一个参数为 entity，传 null 表示只根据 wrapper 更新
        reportUserWinLoseMqMessageService.update(null, updateWrapper);
    }

    /**
     * 重复消费 失败处理
     */
    private void dealWithFailMsgBatch(List<String> uuids) {

        if (CollUtil.isEmpty(uuids)) {
            return;
        }
        for (String uuid : uuids) {
            // 不知道是否在哪一步，是否插入
            ReportUserWinLoseMessagePO queryOne = reportUserWinLoseMessageRepository
                    .selectOne(new LambdaQueryWrapper<ReportUserWinLoseMessagePO>()
                            .eq(ReportUserWinLoseMessagePO::getJsonStrUuid, uuid)
                            .last("LIMIT 1")); // 只获取一条记录，即id最大的
            ReportUserWinLoseMessagePO update = new ReportUserWinLoseMessagePO();
            update.setId(queryOne.getId());
            update.setStatus(CommonConstant.business_zero);
            update.setUpdatedTime(System.currentTimeMillis());
            reportUserWinLoseMqMessageService.updateById(update);


        }


    }


    private void assemblyMessage(ReportUserWinLoseMessagePO mqMessage, UserWinLoseMqVO item) {
        String jsonString = JSON.toJSONString(item);
        mqMessage.setType(item.getBizCode());
        mqMessage.setTypeOrder(item.getOrderId());
        mqMessage.setJsonStr(jsonString);
        // 根据 JSON 字符串生成 UUID 并设置到消息对象中，防止消息重复消费
        mqMessage.setJsonStrUuid(UUID.nameUUIDFromBytes(jsonString.getBytes()).toString());
        mqMessage.setCreatedTime(System.currentTimeMillis());
        mqMessage.setUpdatedTime(System.currentTimeMillis());
    }

    /**
     * 组装消息
     *
     * @param mqMessage mq消息
     * @param vo        消息结果
     * @param jsonStr   消息字符串
     */
    private void assemblySingleMessage(ReportUserWinLoseMessagePO mqMessage, UserWinLoseMqVO vo, String jsonStr) {
        mqMessage.setType(vo.getBizCode());
        mqMessage.setTypeOrder(vo.getOrderId());
        mqMessage.setJsonStr(jsonStr);
        mqMessage.setJsonStrUuid(UUID.nameUUIDFromBytes(jsonStr.getBytes()).toString());
        mqMessage.setCreatedTime(System.currentTimeMillis());
        mqMessage.setUpdatedTime(System.currentTimeMillis());
        mqMessage.setStatus(CommonConstant.business_zero);
    }

    /**
     * 处理业务逻辑，使用分布式锁避免并发问题。
     * 锁基于用户ID，锁的等待时间为3秒，租期为180秒。
     *
     * @param start 业务开始时间
     * @param vo    包含业务数据的 UserWinLoseMqVO 对象
     */
    public void handleBusiness(long start, UserWinLoseMqVO vo) {


        try {
            if (vo.getBizCode() == 1) {
                return;
            }
            if (vo.getBizCode() == 2 && vo.getOrderStatus().equals(OrderStatusEnum.NOT_SETTLE.getCode())) {
                return;
            }
            userWinLoseListenerService.userWinLose(vo);
        } catch (Exception e) {
            // 处理过程异常
            log.error("会员每日盈亏-MQ队列-------------------------------执行fail,参数：{},错误：{}", vo.getOrderId(), e.getMessage(), e);
            throw e;
        }
        log.info("会员每日盈亏-MQ队列-------------------------------执行success,耗时{}毫秒", System.currentTimeMillis() - start);
    }

    /**
     * 判断是否处理过，第一次处理为成功，其他处理为失败,c
     * 查看是否处理成功过，如果处理成功过，则下次不允许处理,这块逻辑考虑重启的时候，消费消息失败，重新推送消息，重新消费
     */
    private boolean checkOrProcess(ReportUserWinLoseMessagePO messagePO, UserWinLoseMqVO vo) {
        if (vo.getBizCode() != 1 && vo.getBizCode() != 2) {
            // 构建查询条件，判断是否存在对应 orderId 的记录
            LambdaQueryWrapper<ReportUserWinLoseMessagePO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ReportUserWinLoseMessagePO::getTypeOrder, vo.getOrderId());
            queryWrapper.eq(ReportUserWinLoseMessagePO::getStatus, CommonConstant.business_one);
            // 使用 exists 判断是否存在记录，提高查询效率
            boolean exists = reportUserWinLoseMessageRepository.exists(queryWrapper);
            // 如果存在记录，直接返回
            if (exists) {
                log.info("会员每日盈亏-MQ队列------重复消费{}", JSONObject.toJSONString(vo));
                return true;
            }
            return false;
        } else if (vo.getBizCode() == 2) {
            // 因为投注可以一直重算，因为需要比较的uuid来确定是否同一条记录，查询uuid与status 是确定服务重启导致kafka消费失败的情况
            LambdaQueryWrapper<ReportUserWinLoseMessagePO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ReportUserWinLoseMessagePO::getJsonStrUuid, messagePO.getJsonStrUuid());
            queryWrapper.eq(ReportUserWinLoseMessagePO::getStatus, CommonConstant.business_one);
            // 使用 exists 判断是否存在记录，提高查询效率
            boolean exists = reportUserWinLoseMessageRepository.exists(queryWrapper);
            // 如果存在记录，直接返回
            if (exists) {
                log.info("会员每日盈亏-MQ队列------重复消费{}", JSONObject.toJSONString(vo));
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否处理过，第一次处理为成功，其他处理为失败,c
     * 查看是否处理成功过，如果处理成功过，则下次不允许处理,这块逻辑考虑重启的时候，消费消息失败，重新推送消息，重新消费
     */
    private List<UserWinLoseMqVO> checkBatchOrProcess(List<UserWinLoseMqVO> vos) {
        Map<String, UserWinLoseMqVO> userWinLoseMqVOMap = new HashMap<>();
        List<String> uuIds = new ArrayList<>();
        for (UserWinLoseMqVO vo : vos) {
            String jsonString = JSON.toJSONString(vo);
            String uuid = UUID.nameUUIDFromBytes(jsonString.getBytes()).toString();
            userWinLoseMqVOMap.put(uuid, vo);
            uuIds.add(uuid);
        }

        List<ReportUserWinLoseMessagePO> reportUserWinLoseMessagePOS = new ArrayList<>();
        if (!uuIds.isEmpty()) {
            // 因为投注可以一直重算，因为需要比较的uuid来确定是否同一条记录，查询uuid与status 是确定服务重启导致kafka消费失败的情况
            LambdaQueryWrapper<ReportUserWinLoseMessagePO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(ReportUserWinLoseMessagePO::getJsonStrUuid, uuIds);
            queryWrapper.eq(ReportUserWinLoseMessagePO::getStatus, CommonConstant.business_one);
            List<ReportUserWinLoseMessagePO> reportUserWinLoseMessagePOS1 = reportUserWinLoseMessageRepository.selectList(queryWrapper);
            reportUserWinLoseMessagePOS.addAll(reportUserWinLoseMessagePOS1);
        }
        // 去掉存在的
        if (!reportUserWinLoseMessagePOS.isEmpty()) {
            for (ReportUserWinLoseMessagePO vo : reportUserWinLoseMessagePOS) {
                String uuid = vo.getJsonStrUuid();
                if (userWinLoseMqVOMap.containsKey(uuid)) {
                    userWinLoseMqVOMap.remove(uuid); // 只保留未处理过的
                }
            }
        }
        return new ArrayList<>(userWinLoseMqVOMap.values());

    }


}


