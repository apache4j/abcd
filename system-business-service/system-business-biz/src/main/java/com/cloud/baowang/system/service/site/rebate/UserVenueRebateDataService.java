package com.cloud.baowang.system.service.site.rebate;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.system.api.vo.rebate.ReportUserRebateInitVO;
import com.cloud.baowang.system.api.vo.rebate.SystemUserVenueStaticsVO;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.vip.SiteVipChangeRecordCnApi;
import com.cloud.baowang.user.api.enums.UserLabelEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.system.api.vo.site.rebate.GameInfoRebateVO;
import com.cloud.baowang.system.api.vo.site.rebate.SiteNonRebateConfigVO;
import com.cloud.baowang.system.api.vo.site.rebate.SiteNonRebateQueryVO;
import com.cloud.baowang.system.po.site.rebate.SiteRebateConfigPO;
import com.cloud.baowang.system.po.site.rebate.UserRebateRecordPO;
import com.cloud.baowang.system.po.site.rebate.UserRebateVenueRecordPO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserVenueRebateDataService {

    private final SiteNonRebateConfigService nonRebateConfigService;

    private final AgentInfoApi agentInfoApi;

    private final SiteCurrencyInfoApi currencyInfoApi;

    private final UserInfoApi userInfoApi;

    private final SiteRebateConfigService siteRebateConfigService;

    private final UserRebateRecordService userRebateTotalService;

    private final UserRebateVenueRecordService userRebateVenueService;

    private final SiteUserLabelConfigApi siteUserLabelConfigApi;

    private final SiteVipChangeRecordCnApi siteVipChangeRecordCnApi;


    private Long createdTime;
    private String currencyCode;

    /**
     * 根据venueType生成
     *
     * @param rebateInitVO
     * @return
     */
    public void handleUserVenueBetInfo(ReportUserRebateInitVO rebateInitVO) {
        Long startTime = rebateInitVO.getStartTime();
        Long endTime = rebateInitVO.getEndTime();
        String siteCode = rebateInitVO.getSiteCode();
        String timeZone=rebateInitVO.getTimeZoneStr();
        List<SystemUserVenueStaticsVO> records = rebateInitVO.getRecords();
        Long flushTime = rebateInitVO.getFlushTime();
        if (records.isEmpty()) {
            log.info("当前站点:{},日期:{}反水记录原始数据不存在",siteCode,startTime);
            return;
        }
        long auditNum=userRebateTotalService.rebateRecordAuditCount(siteCode,startTime);
        if(auditNum>=1){
            log.info("当前站点:{},日期:{}反水记录存在审核情况,不能再次计算",siteCode,startTime);
            return;
        }
        this.currencyCode = rebateInitVO.getCurrencyCode();
        if (flushTime != null) {
            clearOldUserRebateInfo(flushTime,rebateInitVO);
        }
        this.createdTime = flushTime == null ? System.currentTimeMillis() : flushTime;
        log.info(" clearOldUserRebateInfo : startTime:{},endTime:{},siteCode:{}", startTime, endTime,siteCode);
        Map<String, List<SystemUserVenueStaticsVO>> map = new HashMap<>();

        Map<String, List<SystemUserVenueStaticsVO>> noAgentUsers = records.stream()
                .filter(record -> record.getAgentId() == null)
                .collect(Collectors.groupingBy(SystemUserVenueStaticsVO::getUserId));

        Map<String, List<SystemUserVenueStaticsVO>> agentUsers = records.stream()
                .filter(record -> record.getAgentId() != null)
                .collect(Collectors.groupingBy(SystemUserVenueStaticsVO::getUserId));

        if (!agentUsers.isEmpty()) {
            map.putAll(handleWithAgentUsers(agentUsers));
        }

        //处理会员溢出
        if (!noAgentUsers.isEmpty()) {
            noAgentUsers.forEach((key, value) ->
                    map.merge(key, value, (v1, v2) -> {
                        if (v1.isEmpty()) {
                            return v2;
                        }
                        if (!v2.isEmpty()) {
                            v1.addAll(v2);
                        }
                        return v1;
                    })
            );
        }

        if (map.isEmpty()) {
            return;
        }
        log.info("UserVenueRebateDataService.mergeRebateInfo map  : 过滤开始 -> " + map);
        //拿到真正需要计算返水的数据
        Map<String, List<SystemUserVenueStaticsVO>> finalMap = handleRebateConfigLimit(map,siteCode);

        //根绝venue合并
        Map<String, List<SystemUserVenueStaticsVO>> finalRebateData = mergeRebateInfo(finalMap);

        //计算反水
        Map<String, List<UserRebateVenueRecordPO>> venueRebatePOS = calculateUserVenueRebate(finalRebateData,timeZone,siteCode,startTime,endTime);

        //统计总返水
        List<UserRebateRecordPO> userTotalRebateList = calculateUserTotalRebate(venueRebatePOS,timeZone,siteCode,startTime,endTime);

        //入库
        insertUserRebateData(venueRebatePOS, userTotalRebateList);

    }

    public void clearOldUserRebateInfo(Long flushTime,ReportUserRebateInitVO rebateInitVO) {
        try {
            String timeZoneStr=rebateInitVO.getTimeZoneStr();
            String currencyCode=rebateInitVO.getCurrencyCode();
            String siteCode=rebateInitVO.getSiteCode();
            userRebateTotalService.clearUserRebateRecord(flushTime,timeZoneStr,siteCode,currencyCode);
            userRebateVenueService.clearUserVenueRebateRecord(flushTime,timeZoneStr,siteCode,currencyCode);
        } catch (Exception e) {
            log.info(" 清空返水就数据失败 : "+e.getMessage());
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
    }

    private Map<String, List<SystemUserVenueStaticsVO>> mergeRebateInfo(Map<String, List<SystemUserVenueStaticsVO>> finalMap) {
        Map<String, List<SystemUserVenueStaticsVO>> resultMap = new HashMap<>();
        finalMap.forEach((key, value) -> {

            log.info("UserVenueRebateDataService.mergeRebateInfo finalMap : 集合检查开始 -> " + value);

            Map<Integer, BigDecimal> validAmounts = value.stream()
                    .collect(Collectors.groupingBy(
                            SystemUserVenueStaticsVO::getVenueType,  // 按venueType分组
                            Collectors.reducing(BigDecimal.ZERO, SystemUserVenueStaticsVO::getValidAmount, BigDecimal::add)  // 对validBetAmount求和
                    ));

            for (SystemUserVenueStaticsVO vo : value) {
                BigDecimal totalBetAmount = validAmounts.get(vo.getVenueType());
                vo.setValidAmount(totalBetAmount);
            }
            List<SystemUserVenueStaticsVO> resultList = value.stream().collect(Collectors.toMap(SystemUserVenueStaticsVO::getVenueType, vo -> vo, (v1, v2) -> v1))  // 如果遇到重复的venueType，保留第一个
                    .values()
                    .stream()
                    .toList();

            log.info("UserVenueRebateDataService.mergeRebateInfo finalMap : 集合检查 结束-> " + resultList);

            resultMap.put(key, resultList);

        });

        return resultMap;
    }

    public void insertUserRebateData(Map<String, List<UserRebateVenueRecordPO>> venueRebatePOS, List<UserRebateRecordPO> userTotalRebateList) {

        List<UserRebateVenueRecordPO> userVenuePOList = new ArrayList<>();
        for (Map.Entry<String, List<UserRebateVenueRecordPO>> entry : venueRebatePOS.entrySet()) {
            userVenuePOList.addAll(entry.getValue());
        }
        for(UserRebateVenueRecordPO userRebateVenueRecordPO:userVenuePOList){
            Optional<UserRebateRecordPO> userRebateRecordPOOptional=userTotalRebateList.stream().filter(o->o.getUserId().equalsIgnoreCase(userRebateVenueRecordPO.getUserId())).findFirst();
            if(!userRebateRecordPOOptional.isEmpty()){
                UserRebateRecordPO userRebateRecordPO=userRebateRecordPOOptional.get();
                BigDecimal totalExpectRebateAmount=userRebateRecordPO.getExpectRebateAmount();
                BigDecimal totalActRebateAmount=userRebateRecordPO.getRebateAmount();
                BigDecimal actRebateAmount=AmountUtils.divide(userRebateVenueRecordPO.getRebateAmount(),totalExpectRebateAmount).multiply(totalActRebateAmount);
                userRebateVenueRecordPO.setActRebateAmount(actRebateAmount);
            }else {
                userRebateVenueRecordPO.setActRebateAmount(BigDecimal.ZERO);
            }

        }
        boolean saveFlag1=userRebateVenueService.saveBatch(userVenuePOList);
        boolean saveFlag2=userRebateTotalService.saveBatch(userTotalRebateList);
        log.info("userRebateVenueService.saveBatch save:{},userRebateTotalService save:{}",saveFlag1,saveFlag2);
    }

    /**
     * 单用户返水总额
     *
     * @param venueRebatePOS
     * @return
     */
    private List<UserRebateRecordPO> calculateUserTotalRebate(Map<String, List<UserRebateVenueRecordPO>> venueRebatePOS,String timeZone,String siteCode,long startTime,long endTime) {
        List<UserRebateRecordPO> userRebateRecordPOS = new ArrayList<>();
        List<SiteRebateConfigPO> siteRebateConfigVOS = siteRebateConfigList(siteCode);
        Iterator<Map.Entry<String, List<UserRebateVenueRecordPO>>> iterator = venueRebatePOS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<UserRebateVenueRecordPO>> entry = iterator.next();
            String userId = entry.getKey();
            UserInfoVO userInfo = userInfoApi.getByUserId(userId);
            //计算用户所在从注册时间-返水计算时间范围内的VIP等级变化
            Integer vipChangeGrade =siteVipChangeRecordCnApi.findVIPCodeByDay(userId,userInfo.getRegisterTime(),endTime);
            log.info("单用户返水总额计算,用户:{},等级:{}" , userId,vipChangeGrade);
            SiteRebateConfigPO config = siteRebateConfigVOS.stream().filter(item -> item.getVipGradeCode().equals(String.valueOf(vipChangeGrade))).findFirst().orElse(null);
            if(config==null){
                log.info("单用户返水总额计算,用户:{},等级:{}未找到配置信息,无需计算" , userId,vipChangeGrade);
                continue;
            }
            List<UserRebateVenueRecordPO> rebateList = entry.getValue();
            long dayMills=startTime;
            //有效投注
            BigDecimal maxRebateAmount = config.getDailyLimit();
            BigDecimal validAmount = rebateList.stream().map(UserRebateVenueRecordPO::getValidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            //返水金额
            BigDecimal totalAmount = rebateList.stream().map(UserRebateVenueRecordPO::getRebateAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal minRebateAmount = new BigDecimal("0.01");
            if (totalAmount.compareTo(minRebateAmount) < 0) {
                log.info("未达到返水的用户信息 : {}" , userId);
                continue;
            }
            BigDecimal rebateAmount = maxRebateAmount.compareTo(totalAmount) > 0 ? totalAmount : maxRebateAmount;
            UserRebateRecordPO userRebateTotalPO = new UserRebateRecordPO();
            userRebateTotalPO.setUserId(userId);
            userRebateTotalPO.setSiteCode(siteCode);
            userRebateTotalPO.setUserAccount(userInfo.getUserAccount());
            userRebateTotalPO.setVipRankCode(String.valueOf(vipChangeGrade));
            userRebateTotalPO.setOrderNo(rebateList.get(0).getOrderNo());
            userRebateTotalPO.setValidAmount(validAmount);
            userRebateTotalPO.setRebateAmount(rebateAmount);
            userRebateTotalPO.setExpectRebateAmount(totalAmount);
            userRebateTotalPO.setCurrencyCode(userInfo.getMainCurrency());
            userRebateTotalPO.setLockStatus(0);
            userRebateTotalPO.setOrderStatus(1);
            userRebateTotalPO.setCreatedTime(createdTime);
            userRebateTotalPO.setDayMillis(dayMills);
            userRebateTotalPO.setDayStr(DateUtils.formatDayByZoneId(dayMills,timeZone));
            userRebateRecordPOS.add(userRebateTotalPO);
        }
        log.debug(" 用户返水信息 : " + userRebateRecordPOS);
        return userRebateRecordPOS;

    }

    private Map<String, List<UserRebateVenueRecordPO>> calculateUserVenueRebate(Map<String, List<SystemUserVenueStaticsVO>> source,String timeZone,String siteCode,long startTime,long endTime) {
        Map<String, List<UserRebateVenueRecordPO>> rebateData = new HashMap<>();
        //目前只使用法币
        Map<String, BigDecimal> allFinalRate = currencyInfoApi.getAllFinalRate(siteCode);
        List<SiteRebateConfigPO> siteRebateConfigVOS = siteRebateConfigList(siteCode);
        source.forEach((key, value) -> {
                    UserInfoVO userInfo = userInfoApi.getByUserId(key);
                    String userLabelIds=userInfo.getUserLabelId();
                    if(StringUtils.hasText(userLabelIds)){
                        boolean existsFlag=siteUserLabelConfigApi.existsLabelId(userLabelIds,UserLabelEnum.NO_CASHBACK.getLabelId());
                        if(existsFlag){
                            log.info("当前会员:{},设置了标签不返水,无需处理",key);
                            return;
                        }
                    }
                    //计算用户所在从注册时间-返水计算时间范围内的VIP等级变化
                    Integer vipChangeGrade =siteVipChangeRecordCnApi.findVIPCodeByDay(key,userInfo.getRegisterTime(),endTime);
                    log.info("单用户返水总额计算,用户:{},等级:{}" , key,vipChangeGrade);

                    String orderNo = "AR" + SnowFlakeUtils.getSnowId();
                    List<UserRebateVenueRecordPO> userVenueStaticsPOS = new ArrayList<>();
                    value.stream().forEach(sourceItem -> {
                        for (SiteRebateConfigPO config : siteRebateConfigVOS) {
                            if (String.valueOf(vipChangeGrade).equals(config.getVipGradeCode())) {
                                //段位匹配,构建返水信息结构体
                                UserRebateVenueRecordPO record = new UserRebateVenueRecordPO();
                                record.setSiteCode(siteCode);
                                record.setUserId(userInfo.getUserId());
                                record.setUserAccount(userInfo.getUserAccount());
                                record.setOrderNo(orderNo);
                                BigDecimal validBetAmount = sourceItem.getValidAmount();
                              //  BigDecimal rate = allFinalRate.get(userInfo.getMainCurrency());

                                Integer venueType = sourceItem.getVenueType();
                                BigDecimal rebateAmount = BigDecimal.ZERO;

                                /**
                                 *   SPORTS(1, "体育"),
                                 *     SH(2, "视讯"),
                                 *     CHESS(3, "棋牌"),
                                 *     ELECTRONICS(4, "电子"),
                                 *     ACELT(5, "彩票"),
                                 *     COCKFIGHTING(6, "斗鸡"),
                                 *     ELECTRONIC_SPORTS(7, "电竞"),
                                 *     FISHING(8, "捕鱼"),
                                 *     MARBLES(9, "娱乐"),
                                 */
                                switch (venueType) {
                                    case 1:
//                                         rebateAmount = buildRebateAmount(validBetAmount, config.getSportsRebate(), rate);//平台币
                                        rebateAmount = buildRebateAmountNoWTC(validBetAmount,config.getSportsRebate());//法币
                                        record.setRebateAmount(rebateAmount);
                                        record.setRebatePercent(config.getSportsRebate());
                                        record.setVenueType("1");
                                        break;
                                    case 2:
                                        //rebateAmount = buildRebateAmount(validBetAmount, config.getVideoRebate(), rate);
                                         rebateAmount = buildRebateAmountNoWTC(validBetAmount,config.getVideoRebate());
                                        record.setRebateAmount(rebateAmount);
                                        record.setRebatePercent(config.getVideoRebate());
                                        record.setVenueType("2");
                                        break;
                                    case 3:
                                        // rebateAmount = buildRebateAmount(validBetAmount, config.getPokerRebate(), rate);
                                         rebateAmount = buildRebateAmountNoWTC(validBetAmount,config.getPokerRebate());
                                        record.setRebateAmount(rebateAmount);
                                        record.setRebatePercent(config.getPokerRebate());
                                        record.setVenueType("3");
                                        break;
                                    case 4:
                                        //rebateAmount = buildRebateAmount(validBetAmount, config.getSlotsRebate(), rate);
                                         rebateAmount = buildRebateAmountNoWTC(validBetAmount,config.getSlotsRebate());
                                        record.setRebateAmount(rebateAmount);
                                        record.setRebatePercent(config.getSlotsRebate());
                                        record.setVenueType("4");
                                        break;
                                    case 5:
                                        // rebateAmount = buildRebateAmount(validBetAmount, config.getLotteryRebate(), rate);
                                        rebateAmount = buildRebateAmountNoWTC(validBetAmount,config.getLotteryRebate());
                                        record.setRebateAmount(rebateAmount);
                                        record.setRebatePercent(config.getLotteryRebate());
                                        record.setVenueType("5");
                                        break;
                                    case 6:
                                        //  rebateAmount = buildRebateAmount(validBetAmount, config.getCockfightingRebate(), rate);
                                        rebateAmount = buildRebateAmountNoWTC(validBetAmount,config.getCockfightingRebate());
                                        record.setRebateAmount(rebateAmount);
                                        record.setRebatePercent(config.getCockfightingRebate());
                                        record.setVenueType("6");
                                        break;
                                    case 7:
                                        // rebateAmount = buildRebateAmount(validBetAmount, config.getEsportsRebate(), rate);
                                         rebateAmount = buildRebateAmountNoWTC(validBetAmount,config.getEsportsRebate());
                                        record.setRebateAmount(rebateAmount);
                                        record.setRebatePercent(config.getEsportsRebate());
                                        record.setVenueType("7");
                                        break;
                                    case 8:
                                        //rebateAmount = buildRebateAmount(validBetAmount, config.getFishingRebate(), rate);
                                         rebateAmount = buildRebateAmountNoWTC(validBetAmount,config.getFishingRebate());
                                        record.setRebateAmount(rebateAmount);
                                        record.setRebatePercent(config.getFishingRebate());
                                        record.setVenueType("8");
                                        break;
                                    case 9:
                                        // rebateAmount = buildRebateAmount(validBetAmount, config.getMarblesRebate(), rate);
                                        rebateAmount = buildRebateAmountNoWTC(validBetAmount,config.getMarblesRebate());
                                        record.setRebateAmount(rebateAmount);
                                        record.setRebatePercent(config.getMarblesRebate());
                                        record.setVenueType("9");
                                        break;
                                }
                                record.setValidAmount(validBetAmount);
                                record.setCurrencyCode(userInfo.getMainCurrency());
                                record.setCreatedTime(createdTime);
                                record.setStatus(0);
                                record.setDayMillis(sourceItem.getDayMillis());
                                record.setDayStr(DateUtils.formatDayByZoneId(record.getDayMillis(),timeZone));
                                userVenueStaticsPOS.add(record);
                            }

                        }
                    });
                    rebateData.put(key, userVenueStaticsPOS);
                }


        );

        log.info("计算返水之后的map : " + rebateData);
        return rebateData;
    }

    private static BigDecimal buildRebateAmount(BigDecimal validAmount, BigDecimal rebatePercent, BigDecimal rate) {
        BigDecimal divide = AmountUtils.divide(validAmount, rate,4);
        return divide.multiply(rebatePercent).setScale(4, RoundingMode.DOWN);
    }

    private static BigDecimal buildRebateAmountNoWTC(BigDecimal validAmount, BigDecimal rebatePercent) {
        return validAmount.multiply(rebatePercent).setScale(4, RoundingMode.DOWN);
    }

    /**
     * 处理有代理的
     */
    public Map<String, List<SystemUserVenueStaticsVO>> handleWithAgentUsers(Map<String, List<SystemUserVenueStaticsVO>> source) {
        Map<String, List<SystemUserVenueStaticsVO>> result = new HashMap<>();
        log.error(" handleWithAgentUsers begin source : " + source.values());
        List<String> userIds = source.keySet().stream().toList();
        Map<String, AgentInfoVO> agentInfos = agentInfoApi.getAgentBenefitList(userIds);
        source.forEach((key1, value1) -> {
            agentInfos.forEach((key2, value2) -> {
                if (key1.equals(key2)) {
                    //匹配user-agent
                    AgentInfoVO agentInfoVO = agentInfos.get(key2);
                    String benefit = agentInfoVO.getUserBenefit();
                    List<String> benefitListTemp = Arrays.stream(benefit.split(CommonConstant.COMMA)).toList();
                    //匹配返水项
                    List<String> benefitList = adaptAgentConfig(benefitListTemp);
                    log.error(" handleWithAgentUsers begin 代理配置的返水项 : "+ "userId : "+ key1 + benefitListTemp + " 适配得到的返水项" + benefitList);
                    if (!benefitList.isEmpty()) {
                        //配置了哪些返水项
                        List<SystemUserVenueStaticsVO> userBetInfo = source.get(key1);
                        List<SystemUserVenueStaticsVO> userVenueStaticsVOS = userBetInfo.stream().filter(item -> benefitList.contains(String.valueOf(item.getVenueType()))).collect(Collectors.toList());
                        result.put(key1, userVenueStaticsVOS);
                    }
                }
            });
        });
        log.error(" handleWithAgentUsers end source : " + result.values());
        return result;
    }

    /**
     *
     * 0	VIP奖励
     * 1	任务奖励
     * 2	勋章奖励
     * 3	优惠活动
     * 4	转盘奖励
     * 5	体育返水==>1
     * 6	电竞返水==>7
     * 7	视讯返水==>2
     * 8	棋牌返水==>3
     * 9	电子返水==>4
     * 10	彩票返水==>5
     * 11	斗鸡返水==>6
     * 12	捕鱼返水==>8
     * 13	娱乐返水==>9
     *
     * @param benefitList
     * @return
     */

    /**
     *   SPORTS(1, "体育"),
     *     SH(2, "视讯"),
     *     CHESS(3, "棋牌"),
     *     ELECTRONICS(4, "电子"),
     *     ACELT(5, "彩票"),
     *     COCKFIGHTING(6, "斗鸡"),
     *     ELECTRONIC_SPORTS(7, "电竞"),
     *     FISHING(8, "捕鱼"),
     *     MARBLES(9, "娱乐"),
     */
    private List<String> adaptAgentConfig(List<String> benefitList) {
        List<String> result = new ArrayList<>();
        for (String benefit : benefitList) {
            switch (benefit) {
                case "5":
                    result.add(CommonConstant.business_one_str);
                    break;
                case "6":
                    result.add(CommonConstant.business_seven_str);
                    break;
                case "7":
                    result.add(CommonConstant.business_two_str);
                    break;
                case "8":
                    result.add(CommonConstant.business_three_str);
                    break;
                case "9":
                    result.add(CommonConstant.business_four_str);
                    break;
                case "10":
                    result.add(CommonConstant.business_five_str);
                    break;
                case "11":
                    result.add(CommonConstant.business_six_str);
                    break;
                case "12":
                    result.add(CommonConstant.business_eight_str);
                    break;
                case "13":
                    result.add(CommonConstant.business_nine_str);
                    break;
            }
        }
        return result;
    }

    /**
     * 处理不返水游戏
     */
    public Map<String, List<SystemUserVenueStaticsVO>> handleRebateConfigLimit(Map<String, List<SystemUserVenueStaticsVO>> map, String siteCode) {
        log.info(" handleRebateConfigLimit --- 处理不返水游戏之前 map : " + map);
        Map<String, List<SystemUserVenueStaticsVO>> result = new HashMap<>();
        Page<SiteNonRebateConfigVO> siteNonRebateConfigVOPage = nonRebateConfigService.listPage(SiteNonRebateQueryVO.builder().siteCode(siteCode).build());
        List<SiteNonRebateConfigVO> configList = siteNonRebateConfigVOPage.getRecords();
        if (configList.isEmpty()) {
            return map;
        }
        map.forEach((key, valueList) -> {
            List<SystemUserVenueStaticsVO> userVenueStaticsVOS = valueList.stream()
                    .filter(vo -> {
                        boolean filter = configList.stream().allMatch(config -> venueLimitFilter(config,vo));
                        log.info(" handleRebateConfigLimit --- 是否过滤 对象. new  : " + vo + " --- 结果 --- " + filter);
                        return filter;
                    })
                    .toList();
            result.put(key, userVenueStaticsVOS);
        });

        log.info(" handleRebateConfigLimit --- 处理不返水游戏之后 map : " + result);
        return result;
    }

    /**
     * 返水配置列表
     *
     * @return
     */
    public List<SiteRebateConfigPO> siteRebateConfigList(String siteCode) {
        List<SiteRebateConfigPO> list = siteRebateConfigService.rebateConfigList(siteCode,currencyCode);
        return list;
    }


    public boolean venueLimitFilter(SiteNonRebateConfigVO config, SystemUserVenueStaticsVO source) {
        String venueType = String.valueOf(source.getVenueType());
        String venueTypeConfig = config.getVenueType();
        String venueCode = source.getVenueCode();
        String venueCodeConfig = config.getVenueCode();
        if (venueType.equals(venueTypeConfig)) {
            if (venueCode.equals(venueCodeConfig)) {
                //处理体育,电竞,斗鸡
                if (venueTypeConfig.equals(CommonConstant.business_one_str) || venueTypeConfig.equals(CommonConstant.business_six_str) || venueTypeConfig.equals(CommonConstant.business_seven_str)) {
                    //直接限制
                    log.info(" handleRebateConfigLimit --- 有体育电竞斗鸡限制 配置 : " + config + " 会员下注 : " + source);
                    return false;
                } else {
                    //比对具体游戏
                    List<GameInfoRebateVO> gameInfo = config.getGameInfo();
                    String venueGameType;
                    if (venueType.equals(CommonConstant.business_two_str)) {
                        //视讯单独处理
                        venueGameType = source.getRoomType();
                    } else {
                        venueGameType = source.getVenueGameType();
                    }
                    boolean result = gameInfo.stream().noneMatch(item -> item.getGameId().equals(venueGameType));
                    if (!result) {
                        log.info(" handleRebateConfigLimit --- new  --- 游戏 配置 : " + config + " 游戏 : " + source);
                        return false;
                    }
                    return true;
                }
            }
        }
        return true;
    }

}
