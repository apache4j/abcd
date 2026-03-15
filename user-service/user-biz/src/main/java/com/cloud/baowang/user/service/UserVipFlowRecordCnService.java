package com.cloud.baowang.user.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.vo.UserVIPFlowRequestVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.enums.vip.VipChangeTypeEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipOptionVO;
import com.cloud.baowang.user.api.vo.vip.UserVipFlowRecordCnVO;
import com.cloud.baowang.user.api.vo.vip.UserVipFlowRecordReqVO;
import com.cloud.baowang.user.api.vo.vip.VIPGradeVO;
import com.cloud.baowang.user.po.SiteUserLabelRecordsPO;
import com.cloud.baowang.user.po.SiteVipChangeRecordCnPO;
import com.cloud.baowang.user.po.UserInfoPO;
import com.cloud.baowang.user.po.UserVipFlowRecordCnPO;
import com.cloud.baowang.user.repositories.UserVipFlowRecordCnRepository;
import com.cloud.baowang.user.service.vipV2.VipRewardsService;
import com.cloud.baowang.user.vo.VipUpGradeAwardVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Desciption: mufan
 * @Date: 2024/7/27 15:22
 * @Version: V1.0
 **/
@Service
@AllArgsConstructor
@Slf4j
public class UserVipFlowRecordCnService extends ServiceImpl<UserVipFlowRecordCnRepository, UserVipFlowRecordCnPO> {

    private final SiteApi siteApi;
    private final UserInfoService userInfoService;
    private final SiteVipOptionService siteVipOptionService;
    private final SiteVipChangeRecordCnService siteVipChangeRecordCnService;
    private final VipRewardsService vipRewardsService;
    private final SiteUserLabelConfigService userLabelConfigService;
    private final SiteUserLabelRecordService siteUserLabelRecordService;

    public UserVipFlowRecordCnVO getUserVipFlow(UserInfoVO userInfoVO){
        UserVipFlowRecordCnVO vo= new UserVipFlowRecordCnVO();
        // 查出该会员账户最近的一条VIP流水记录
        List<UserVipFlowRecordCnPO> userVipFlowRecords = this.getBaseMapper().lastSiteVipChangeRecordCnPOs(UserVipFlowRecordReqVO.builder().siteCode(userInfoVO.getSiteCode()).userId(userInfoVO.getUserId()).build());
        UserVipFlowRecordCnPO userVipFlowRecord=null;
        if (CollectionUtil.isNotEmpty(userVipFlowRecords)){
            userVipFlowRecord= userVipFlowRecords.get(0);
        }else{
            //站点当前时间
            SiteVO siteVO = siteApi.getSiteInfo(userInfoVO.getSiteCode()).getData();
            LocalDateTime localDateTime=TimeZoneUtils.timeByTimeZone(System.currentTimeMillis(),siteVO.getTimezone());
            String localDateTimeStr=TimeZoneUtils.formatLocalDateTime(localDateTime,TimeZoneUtils.patten_yyyyMMdd);
            // Vip所有升级经验配置
            List<SiteVipOptionVO> siteVIPGradePOList = siteVipOptionService.getList(userInfoVO.getSiteCode(),userInfoVO.getMainCurrency());
            Map<Integer, SiteVipOptionVO> siteVipOptionMap = siteVIPGradePOList.stream().collect(Collectors.toMap(SiteVipOptionVO::getVipGradeCode, e -> e));
            //添加失效天数转换
            LocalDateTime fuRelegationDate =  localDateTime.plusDays(siteVipOptionMap.get(userInfoVO.getVipGradeCode()).getRelegationDays()+1);
            String fuRelegationDateStr =TimeZoneUtils.formatLocalDateTime(fuRelegationDate,TimeZoneUtils.patten_yyyyMMdd);
            userVipFlowRecord = UserVipFlowRecordCnPO.builder().userId(userInfoVO.getUserId()).siteCode(userInfoVO.getSiteCode()).userAccount(userInfoVO.getUserAccount()).currencyCode(userInfoVO.getMainCurrency()).
                    vipGradeCode(userInfoVO.getVipGradeCode()).nextVipGradeCode(userInfoVO.getVipGradeUp()).relegationDays(siteVipOptionMap.get(userInfoVO.getVipGradeCode()).getRelegationDays()).upVipTime(localDateTimeStr).
                    relegationDaysTime(fuRelegationDateStr).upgradeBetAmount(siteVipOptionMap.get(userInfoVO.getVipGradeUp()).getVipUpgradeExp()).betAmountExe(BigDecimal.ZERO)
                    .gradeRelegationAmount(siteVipOptionMap.get(userInfoVO.getVipGradeCode()).getRelegationAmount()).finishRelegationAmount(BigDecimal.ZERO).finishBetAmount(BigDecimal.ZERO).build();
        }
        BeanUtils.copyProperties(userVipFlowRecord, vo);
        return vo;
    }

    public ResponseVO<Void> updateCnVipGrade(String userId, Integer changeVipGrade){
        UserInfoVO userInfoVO = userInfoService.getUserInfoByUserId(userId);
        if (userInfoVO.getVipGradeCode() == changeVipGrade){
            return ResponseVO.success();
        }
        VipChangeTypeEnum changeType = VipChangeTypeEnum.down;
        if (changeVipGrade > userInfoVO.getVipGradeCode()){
            changeType =  VipChangeTypeEnum.up;
        }

        SiteUserLabelRecordsPO siteUserLabelRecordsPO =new SiteUserLabelRecordsPO();
        if (ObjectUtil.isNotEmpty(userInfoVO.getUserLabelId())) {
            siteUserLabelRecordsPO.setBeforeChange(userInfoVO.getUserLabelId());
        }
        //添加标签
        if (StringUtils.isEmpty(userInfoVO.getUserLabelId())){
            userInfoVO.setUserLabelId("100007");
        }else if (!userInfoVO.getUserLabelId().contains("100007")){
            userInfoVO.setUserLabelId(userInfoVO.getUserLabelId()+",100007");
        }
        List<String> ids = stringToList(userInfoVO.getUserLabelId());
        List<GetUserLabelByIdsVO> userLabelConfigPOs = userLabelConfigService.getUserLabelByIds(ids);
        String userLabel = "";
        if (CollUtil.isNotEmpty(userLabelConfigPOs)) {
            for (GetUserLabelByIdsVO userLabelConfigPO : userLabelConfigPOs) {
                userLabel += userLabelConfigPO.getLabelName() + CommonConstant.COMMA;
            }
        }
        if (userLabel.endsWith(CommonConstant.COMMA)) {
            userLabel = userLabel.substring(0, userLabel.length() - 1);
        }
        userInfoVO.setUserLabelName(userLabel);
        SiteVO siteVO = siteApi.getSiteInfo(userInfoVO.getSiteCode()).getData();
        // Vip所有升级经验配置
        List<SiteVipOptionVO> siteVIPGradePOList = siteVipOptionService.getList(userInfoVO.getSiteCode(),userInfoVO.getMainCurrency());
        Map<Integer, SiteVipOptionVO> siteVipOptionMap = siteVIPGradePOList.stream().collect(Collectors.toMap(SiteVipOptionVO::getVipGradeCode, e -> e));
        SiteVipOptionVO maxItem = siteVIPGradePOList.stream()
                .max(Comparator.comparing(SiteVipOptionVO::getId))
                .orElse(null);
        long start = System.currentTimeMillis();
        Integer upGradeCode=0;
        if (changeVipGrade.equals(maxItem.getVipGradeCode())){
            changeVipGrade =maxItem.getVipGradeCode();
            upGradeCode = maxItem.getVipGradeCode() ;
        }else{
            upGradeCode = changeVipGrade+1 ;
        }
        SiteVipOptionVO siteVipOptionVO= siteVipOptionMap.get(upGradeCode);
        //保级流水金额
        BigDecimal relegationAmount = siteVipOptionMap.get(changeVipGrade).getRelegationAmount();
        LocalDateTime localDateTime=TimeZoneUtils.timeByTimeZone(start,siteVO.getTimezone());
        String localDateTimeStr=TimeZoneUtils.formatLocalDateTime(localDateTime,TimeZoneUtils.patten_yyyyMMdd);
        LocalDateTime fuRelegationDate =  localDateTime.plusDays(siteVipOptionMap.get(changeVipGrade).getRelegationDays()+1);
        String fuRelegationDateStr =TimeZoneUtils.formatLocalDateTime(fuRelegationDate,TimeZoneUtils.patten_yyyyMMdd);
        this.updateUserVipLevelAndsiteVipChangeData(userInfoVO,changeVipGrade,upGradeCode,siteVO,changeType,localDateTimeStr);
        UserVipFlowRecordCnPO userVipFlowRecord = UserVipFlowRecordCnPO.builder().userId(userInfoVO.getUserId()).currencyCode(userInfoVO.getMainCurrency()).siteCode(userInfoVO.getSiteCode()).userAccount(userInfoVO.getUserAccount()).betAmountExe(BigDecimal.ZERO).
                vipGradeCode(changeVipGrade).nextVipGradeCode(upGradeCode).relegationDays(siteVipOptionVO.getRelegationDays()).upVipTime(localDateTimeStr).finishRelegationAmount(BigDecimal.ZERO).finishBetAmount(BigDecimal.ZERO).
                relegationDaysTime(fuRelegationDateStr).upgradeBetAmount(siteVipOptionVO.getVipUpgradeExp()).gradeRelegationAmount(relegationAmount).build();
        userVipFlowRecord.setCreatedTime(start);
        userVipFlowRecord.setUpdatedTime(start);
        userVipFlowRecord.setCreator(CurrReqUtils.getAccount());
        userVipFlowRecord.setUpdatedTime(null);
        siteUserLabelRecordsPO.setAfterChange(userInfoVO.getUserLabelId());
        siteUserLabelRecordsPO.setMemberAccount(userInfoVO.getUserAccount());
        siteUserLabelRecordsPO.setAccountType(userInfoVO.getAccountType());
        siteUserLabelRecordsPO.setUpdater(CurrReqUtils.getAccount());
        siteUserLabelRecordsPO.setOperator(CurrReqUtils.getAccount());
        siteUserLabelRecordsPO.setUpdatedTime(System.currentTimeMillis());
        siteUserLabelRecordsPO.setSiteCode(userInfoVO.getSiteCode());
        siteUserLabelRecordsPO.setAccountStatus(userInfoVO.getAccountStatus());
        siteUserLabelRecordsPO.setRiskControlLevel(userInfoVO.getRiskLevelId());
        siteUserLabelRecordService.save(siteUserLabelRecordsPO);
        this.getBaseMapper().insert(userVipFlowRecord);
        return ResponseVO.success();
    }

    public void batchVipUP(String userId , List<UserVIPFlowRequestVO> userVIPFlowVOs, Map<String, SiteVO> allSiteMap){
        long start = System.currentTimeMillis();
        boolean lock = false;
        RLock fairLock = RedisUtil.getFairLock(RedisConstants.VIP_UPGRADE_DOWNGRADE_LOCK_KEY +
                userId);
        BigDecimal userTotalExe = BigDecimal.ZERO;
        try {
            UserInfoVO userInfoVO = userInfoService.getUserInfoByUserId(userId);
            // Vip所有升级经验配置
            List<SiteVipOptionVO> siteVIPGradePOList = siteVipOptionService.getList(userInfoVO.getSiteCode(),userInfoVO.getMainCurrency());
            Map<Integer, SiteVipOptionVO> siteVipOptionMap = siteVIPGradePOList.stream().collect(Collectors.toMap(SiteVipOptionVO::getVipGradeCode, e -> e));
            // 使用 获取最小配置最大配置
            SiteVipOptionVO maxItem = siteVIPGradePOList.stream()
                    .max(Comparator.comparing(SiteVipOptionVO::getId))
                    .orElse(null);
            SiteVipOptionVO minItem = siteVIPGradePOList.stream()
                    .min(Comparator.comparing(SiteVipOptionVO::getId))
                    .orElse(null);
            for (UserVIPFlowRequestVO vo:userVIPFlowVOs){
                userTotalExe = userTotalExe.add(vo.getValidAmount());
                log.info("该用户:{}, : 升级经验:{}", userId, vo.getValidAmount());
            }
            //vip当前等级code
            Integer vipGradeCode =userInfoVO.getVipGradeCode();
            //vip当前下级等级
            Integer vipGradeUp =  userInfoVO.getVipGradeUp();
            SiteVO siteVO= allSiteMap.get(userInfoVO.getSiteCode());
            // 查出该会员账户最近的一条VIP流水记录
            List<UserVipFlowRecordCnPO> userVipFlowRecords = this.getBaseMapper().lastSiteVipChangeRecordCnPOs(UserVipFlowRecordReqVO.builder().siteCode(userInfoVO.getSiteCode()).userId(userId).build());
            UserVipFlowRecordCnPO userVipFlowRecord=null;
            //站点当前时间
            LocalDateTime LocalDateTime=TimeZoneUtils.timeByTimeZone(start,siteVO.getTimezone());
            String LocalDateTimeStr=TimeZoneUtils.formatLocalDateTime(LocalDateTime,TimeZoneUtils.patten_yyyyMMdd);
            if (CollectionUtil.isNotEmpty(userVipFlowRecords)){
                 userVipFlowRecord= userVipFlowRecords.get(0);
            }else{
                 //添加失效天数转换
                 LocalDateTime fuRelegationDate =  LocalDateTime.plusDays(siteVipOptionMap.get(vipGradeCode).getRelegationDays()+1);
                 String fuRelegationDateStr =TimeZoneUtils.formatLocalDateTime(fuRelegationDate,TimeZoneUtils.patten_yyyyMMdd);
                 userVipFlowRecord = UserVipFlowRecordCnPO.builder().userId(userId).siteCode(userInfoVO.getSiteCode()).userAccount(userInfoVO.getUserAccount()).currencyCode(userInfoVO.getMainCurrency()).
                        vipGradeCode(vipGradeCode).nextVipGradeCode(vipGradeUp).relegationDays(siteVipOptionMap.get(vipGradeCode).getRelegationDays()).upVipTime(LocalDateTimeStr).
                         relegationDaysTime(fuRelegationDateStr).upgradeBetAmount(siteVipOptionMap.get(vipGradeUp).getVipUpgradeExp()).betAmountExe(userTotalExe)
                         .gradeRelegationAmount(siteVipOptionMap.get(vipGradeCode).getRelegationAmount()).finishRelegationAmount(BigDecimal.ZERO).finishBetAmount(BigDecimal.ZERO).build();
            }
            SiteVipOptionVO siteVipOptionVO= siteVipOptionMap.get(vipGradeUp);
            //判断是否降级 判断降级天数 和保级流水<保级总流水的时候 去最新的当前升级流水和保级流水对象
            if (!userInfoVO.getVipGradeCode().equals(minItem.getVipGradeCode()) && LocalDateTimeStr.equals(userVipFlowRecord.getRelegationDaysTime()) && userVipFlowRecord.getFinishRelegationAmount().compareTo(siteVipOptionVO.getRelegationAmount()) < 0 ){
                userVipFlowRecord = this.upDownChangVIP(userInfoVO,siteVipOptionMap,siteVO,VipChangeTypeEnum.down,userVipFlowRecord,maxItem);
                vipGradeCode =userVipFlowRecord.getVipGradeCode();
                siteVipOptionVO=siteVipOptionMap.get(userVipFlowRecord.getNextVipGradeCode());
            }
            userVipFlowRecord.setBetAmountExe(userTotalExe);
            userVipFlowRecord.setFinishBetAmount(userVipFlowRecord.getFinishBetAmount().add(userTotalExe));
            userVipFlowRecord.setFinishRelegationAmount(userVipFlowRecord.getFinishRelegationAmount().add(userTotalExe));
            userVipFlowRecord.setUpgradeBetAmount(siteVipOptionVO.getVipUpgradeExp());
            userVipFlowRecord.setGradeRelegationAmount(siteVipOptionMap.get(vipGradeCode).getRelegationAmount());
            userVipFlowRecord.setRelegationDays(siteVipOptionMap.get(vipGradeCode).getRelegationDays());
            userVipFlowRecord.setRelegationDaysTime(this.calculateDate(userVipFlowRecord.getUpVipTime(),siteVipOptionMap.get(vipGradeCode).getRelegationDays()));
            if (userVipFlowRecord.getFinishBetAmount().compareTo(siteVipOptionVO.getVipUpgradeExp()) >= 0 && userInfoVO.getVipGradeCode() != maxItem.getVipGradeCode()) {
                //升级
                userVipFlowRecord = this.upDownChangVIP(userInfoVO,siteVipOptionMap,siteVO,VipChangeTypeEnum.up,userVipFlowRecord,maxItem);
            }else if(userInfoVO.getVipGradeCode().equals(maxItem.getVipGradeCode())  && LocalDateTimeStr.equals(userVipFlowRecord.getRelegationDaysTime())) {
                userVipFlowRecord.setFinishRelegationAmount(userTotalExe);
                userVipFlowRecord.setRelegationDays(userVipFlowRecord.getRelegationDays());
                userVipFlowRecord.setRelegationDaysTime(this.calculateDate(LocalDateTimeStr,siteVipOptionMap.get(vipGradeCode).getRelegationDays()));
                userVipFlowRecord.setUpVipTime(LocalDateTimeStr);
            }
            //增加vip用户流水
            userVipFlowRecord.setId(SnowFlakeUtils.getSnowId());
            Long time=System.currentTimeMillis();
            userVipFlowRecord.setCreatedTime(time);
            userVipFlowRecord.setUpdatedTime(time);
            userVipFlowRecord.setCreator(null);
            userVipFlowRecord.setUpdatedTime(null);
            this.getBaseMapper().insert(userVipFlowRecord);
        }catch (Exception e) {
            if (lock) {
                fairLock.unlock();
            }
            log.error("SiteVipChangeRecordCnService 用户id:{} 升级经验总和:{} 存储个人VIP记录表发生异常", userId, userTotalExe, e);
        }
    }


    private UserVipFlowRecordCnPO upDownChangVIP( UserInfoVO userInfoVO,Map<Integer, SiteVipOptionVO> siteVipOptionMap,SiteVO siteVO,VipChangeTypeEnum vipChangeTypeEnum, UserVipFlowRecordCnPO userVipFlowRecord, SiteVipOptionVO maxItem ){
        log.info("upDownChangVIP入口:{}", userVipFlowRecord);

        //vip当前等级
        Integer vipGradeCode=0;
        //vip升级后的等级
        Integer vipGradeUp=0;
        if (vipChangeTypeEnum.getCode().equals(VipChangeTypeEnum.up.getCode())){
             vipGradeCode=userInfoVO.getVipGradeCode();
             vipGradeUp =userInfoVO.getVipGradeUp();
        }else{
             vipGradeCode=userInfoVO.getVipGradeCode()-1;
             vipGradeUp =userInfoVO.getVipGradeUp()-1;
        }
        //站点当前时间
        long start = System.currentTimeMillis();
        LocalDateTime LocalDateTime=TimeZoneUtils.timeByTimeZone(start,siteVO.getTimezone());
        String localDateTimeStr=TimeZoneUtils.formatLocalDateTime(LocalDateTime,TimeZoneUtils.patten_yyyyMMdd);
        //添加失效天数转换
        LocalDateTime fuRelegationDate = LocalDateTime.plusDays(siteVipOptionMap.get(vipGradeCode).getRelegationDays()+1);
        String fuRelegationDateStr =TimeZoneUtils.formatLocalDateTime(fuRelegationDate,TimeZoneUtils.patten_yyyyMMdd);
        SiteVipOptionVO siteVipOptionVO=null;
        BigDecimal finishRelegationAmount=BigDecimal.ZERO;
        BigDecimal finishBetAmount = BigDecimal.ZERO;
        BigDecimal betAmountExe = BigDecimal.ZERO;
        BigDecimal relegationAmount =BigDecimal.ZERO;

        if (vipChangeTypeEnum.getCode().equals(VipChangeTypeEnum.up.getCode())){
            //升级逻辑
            //查询用户今天是否有升级
            SiteVipChangeRecordCnPO siteVipChangeRecordCnPO = siteVipChangeRecordCnService.getBaseMapper().selectOne(Wrappers.lambdaQuery(SiteVipChangeRecordCnPO.class)
                    .eq(SiteVipChangeRecordCnPO::getUserId, userInfoVO.getUserId())
                    .eq(SiteVipChangeRecordCnPO::getChangeType, VipChangeTypeEnum.up.getCode())
                    .eq(SiteVipChangeRecordCnPO::getUpVipTime, localDateTimeStr)
                    .last(" order by created_time  limit 1 "));
            if (!userInfoVO.getVipGradeCode().equals(maxItem.getVipGradeCode()) &&Objects.isNull(siteVipChangeRecordCnPO)){
                userVipFlowRecord.setId(SnowFlakeUtils.getSnowId());
                Long time=System.currentTimeMillis();
                userVipFlowRecord.setCreatedTime(time);
                userVipFlowRecord.setUpdatedTime(time);
                this.getBaseMapper().insert(userVipFlowRecord);
                //今天没有升级的用户才可以升级
                vipGradeCode=userInfoVO.getVipGradeCode()+1;
                vipGradeUp =userInfoVO.getVipGradeUp()+1;
                if (vipGradeCode.equals(maxItem.getVipGradeCode())){
                    vipGradeUp =maxItem.getVipGradeCode();
                }
                //保级流水金额
                relegationAmount = siteVipOptionMap.get(vipGradeCode).getRelegationAmount();
                fuRelegationDate = LocalDateTime.plusDays(siteVipOptionMap.get(vipGradeCode).getRelegationDays()+1);
                fuRelegationDateStr =TimeZoneUtils.formatLocalDateTime(fuRelegationDate,TimeZoneUtils.patten_yyyyMMdd);
                this.updateUserVipLevelAndsiteVipChangeData(userInfoVO,vipGradeCode,vipGradeUp,siteVO,vipChangeTypeEnum,localDateTimeStr);
                //当前经验溢出的-去当前总经验得到= 溢出经验值
                betAmountExe =BigDecimal.ZERO;
                finishRelegationAmount =userVipFlowRecord.getFinishBetAmount().subtract(userVipFlowRecord.getUpgradeBetAmount());
                finishBetAmount = finishRelegationAmount;
                List<VipUpGradeAwardVO> vipUpGradeAwardVOList=new ArrayList<>();
                VipUpGradeAwardVO vodata=new VipUpGradeAwardVO();
                vodata.setUserInfoVO(userInfoVO);
                vodata.setSiteVipOptionVO(siteVipOptionMap.get(vipGradeCode));
                vipUpGradeAwardVOList.add(vodata);
                vipRewardsService.vipUpGradeRewardBatchHandle(vipUpGradeAwardVOList);
                log.info("upDownChangVIP:{}", vipUpGradeAwardVOList);
            }else{
                betAmountExe =userVipFlowRecord.getBetAmountExe();
                finishRelegationAmount =userVipFlowRecord.getFinishRelegationAmount();
                finishBetAmount = userVipFlowRecord.getFinishBetAmount();
                relegationAmount = userVipFlowRecord.getGradeRelegationAmount();
            }
            siteVipOptionVO= siteVipOptionMap.get(vipGradeUp);
        }else{
            //降级逻辑
            //更新用户等级
            betAmountExe =userVipFlowRecord.getBetAmountExe();
            siteVipOptionVO= siteVipOptionMap.get(vipGradeUp);
            relegationAmount = siteVipOptionMap.get(vipGradeCode).getRelegationAmount();
            fuRelegationDate = LocalDateTime.plusDays(siteVipOptionMap.get(vipGradeCode).getRelegationDays()+1);
            fuRelegationDateStr = TimeZoneUtils.formatLocalDateTime(fuRelegationDate,TimeZoneUtils.patten_yyyyMMdd);
            userVipFlowRecord.setBetAmountExe(BigDecimal.ZERO);
            userVipFlowRecord.setFinishRelegationAmount(BigDecimal.ZERO);
            userVipFlowRecord.setFinishBetAmount(BigDecimal.ZERO);
            userVipFlowRecord.setId(SnowFlakeUtils.getSnowId());
            userVipFlowRecord.setRelegationDays(siteVipOptionMap.get(vipGradeCode).getRelegationDays());
            userVipFlowRecord.setRelegationDaysTime(fuRelegationDateStr);
            userVipFlowRecord.setUpVipTime(localDateTimeStr);
            userVipFlowRecord.setVipGradeCode(vipGradeCode);
            userVipFlowRecord.setNextVipGradeCode(vipGradeUp);
            userVipFlowRecord.setGradeRelegationAmount(relegationAmount);
            userVipFlowRecord.setUpgradeBetAmount(siteVipOptionVO.getVipUpgradeExp());
            Long time=System.currentTimeMillis();
            userVipFlowRecord.setCreatedTime(time);
            userVipFlowRecord.setUpdatedTime(time);
            this.getBaseMapper().insert(userVipFlowRecord);
            this.updateUserVipLevelAndsiteVipChangeData(userInfoVO,vipGradeCode,vipGradeUp,siteVO,vipChangeTypeEnum,localDateTimeStr);
        }
        return  UserVipFlowRecordCnPO.builder().userId(userInfoVO.getUserId()).currencyCode(userInfoVO.getMainCurrency()).siteCode(userInfoVO.getSiteCode()).userAccount(userInfoVO.getUserAccount()).betAmountExe(betAmountExe).
                vipGradeCode(vipGradeCode).nextVipGradeCode(vipGradeUp).relegationDays(siteVipOptionMap.get(vipGradeCode).getRelegationDays()).upVipTime(localDateTimeStr).finishRelegationAmount(finishRelegationAmount).finishBetAmount(finishBetAmount).
                relegationDaysTime(fuRelegationDateStr).upgradeBetAmount(siteVipOptionVO.getVipUpgradeExp()).gradeRelegationAmount(relegationAmount).build();
    }

    private void updateUserVipLevelAndsiteVipChangeData(UserInfoVO userInfoVO,int vipGradeCode,int vipGradeUp,SiteVO siteVO,VipChangeTypeEnum vipChangeTypeEnum,  String localDateTime){
        LambdaUpdateWrapper<UserInfoPO> updateWrapper = new LambdaUpdateWrapper<>();
        //降级逻辑
        //更新用户等级
        updateWrapper.eq(UserInfoPO::getUserId, userInfoVO.getUserId());
        updateWrapper.set(UserInfoPO::getVipGradeCode, vipGradeCode);
        updateWrapper.set(UserInfoPO::getVipGradeUp, vipGradeUp);
        updateWrapper.set(UserInfoPO::getUserLabelId, userInfoVO.getUserLabelId());
        userInfoService.update(null, updateWrapper);
        siteVipChangeRecordCnService.isertSiteVipChangeRecordCn(
                userInfoVO, siteVO.getSiteCode(), vipGradeCode, vipChangeTypeEnum.getCode(),localDateTime
        );
    }


    @Transactional(rollbackFor = Exception.class)
    public void cnVipUpDownAllSiteCode(String timezone){
        log.info("站点升降级查询站点时区: {}", timezone);
        if(ObjectUtil.isEmpty(timezone)){
            timezone = TimeZoneUtils.get0TimeZone();
            log.info("站点默认时区: {}", timezone);
        }
        List<SiteVO> sites = siteApi.getSiteInfoByTimezone(timezone);
//        List<SiteVO> sites=siteApi.allSiteInfo().getData();
        long start = System.currentTimeMillis();
        Map<String, SiteVO> allSiteMap = sites.stream().filter(e ->SiteHandicapModeEnum.China.getCode().equals(e.getHandicapMode())).collect(Collectors.toMap(SiteVO::getSiteCode, e -> e));
        List<VIPGradeVO> allGrade=siteVipOptionService.getInitVIPGrade();
        VIPGradeVO maxItem = allGrade.stream()
                .max(Comparator.comparing(VIPGradeVO::getVipGradeCode))
                .orElse(null);
        for (Map.Entry<String, SiteVO> map : allSiteMap.entrySet()) {
            //循环处理大陆盘所有站点的用户
            log.info("站点升降级 : {} ,exp :{}", map.getKey(), JSONObject.toJSONString(map.getValue()));
            SiteVO siteVO=map.getValue();
            //站点当前时间
            LocalDateTime localDateTime=TimeZoneUtils.timeByTimeZone(start,siteVO.getTimezone());
            String localDateTimeStr=TimeZoneUtils.formatLocalDateTime(localDateTime,TimeZoneUtils.patten_yyyyMMdd);
            //查询出截止日期是今天的保级时间是今天的并且 保级流水小于保证金设定用户，批处理降级用户
            List<UserVipFlowRecordCnPO> userVipFlowRecords = this.getBaseMapper().lastSiteVipChangeRecordCnPOs(UserVipFlowRecordReqVO.builder().siteCode(siteVO.getSiteCode()).relegationDaysTime(localDateTimeStr).build());
            if (CollectionUtil.isNotEmpty(userVipFlowRecords)){
                List<String> userIds = userVipFlowRecords.stream().map(UserVipFlowRecordCnPO::getUserId).collect(Collectors.toList());
                List<UserInfoVO> users=userInfoService.getUserInfoByUserIds(userIds);
                Map<String, UserInfoVO> allUserMap =users.stream().collect(Collectors.toMap(UserInfoVO::getUserId, e -> e));
                batchDownUserVip(userVipFlowRecords,allUserMap,localDateTimeStr,localDateTime);
            }
            //查询站点下所有用户升级流水>升级流水总金额用户 批处理升级用户
            List<UserVipFlowRecordCnPO> userVipFlowUpRecords = this.getBaseMapper().lastSiteVipChangeRecordCnPOs(UserVipFlowRecordReqVO.builder().siteCode(siteVO.getSiteCode()).maxVipLevel(maxItem.getVipGradeCode()).build());
            if (CollectionUtil.isNotEmpty(userVipFlowUpRecords)){
                log.info("vip满级升级降级: {}", userVipFlowUpRecords);
                List<String> userIds = userVipFlowUpRecords.stream().map(UserVipFlowRecordCnPO::getUserId).collect(Collectors.toList());
                List<UserInfoVO> users=userInfoService.getUserInfoByUserIds(userIds);
                Map<String, UserInfoVO> allUserMap =users.stream().collect(Collectors.toMap(UserInfoVO::getUserId, e -> e));
                batchUpUserVip(userVipFlowUpRecords,allUserMap,localDateTimeStr,localDateTime);
            }
            //等级是11级并且达到满级保级流水并且 是保级天数是今天的用户全部自动 已完成有效流水为0；
            List<UserVipFlowRecordCnPO> userVipFlowMaxRecords = this.getBaseMapper().lastSiteVipChangeRecordCnPOs(UserVipFlowRecordReqVO.builder().siteCode(siteVO.getSiteCode()).vipMaxGradeCode(maxItem.getVipGradeCode()).maxGradeRelegationDaysTime(localDateTimeStr).build());
            if (CollectionUtil.isNotEmpty(userVipFlowMaxRecords)){
                log.info("vip满级自动降级: {}", userVipFlowMaxRecords);
                List<String> userIds = userVipFlowMaxRecords.stream().map(UserVipFlowRecordCnPO::getUserId).collect(Collectors.toList());
                List<UserInfoVO> users=userInfoService.getUserInfoByUserIds(userIds);
                Map<String, UserInfoVO> allUserMap =users.stream().collect(Collectors.toMap(UserInfoVO::getUserId, e -> e));
                batchUpMaxVipAmount(userVipFlowMaxRecords,allUserMap,localDateTimeStr,localDateTime);
            }

        }
    }

    private void batchDownUserVip(List<UserVipFlowRecordCnPO> userVipFlowRecords,Map<String, UserInfoVO> allUserMap,String localDateTimeStr,LocalDateTime localDateTime){
        log.info("vip自动降级batchDownUserVip: {}", userVipFlowRecords);
        //用户
        List<LambdaUpdateWrapper<UserInfoPO>> updateUsers= new ArrayList<>();
        //用户等级记录
        List<SiteVipChangeRecordCnPO> batchInsertVip= new ArrayList<>();
        List<UserVipFlowRecordCnPO> batchInsertUserVipFlow= new ArrayList<>();
        //用户vip流水
        for (UserVipFlowRecordCnPO po:userVipFlowRecords){
            UserInfoVO userInfoVO= allUserMap.get(po.getUserId());
            List<SiteVipOptionVO> siteVIPGradePOList = siteVipOptionService.getList(userInfoVO.getSiteCode(),userInfoVO.getMainCurrency());
            SiteVipOptionVO maxItem = siteVIPGradePOList.stream()
                    .max(Comparator.comparing(SiteVipOptionVO::getId))
                    .orElse(null);
            Map<Integer, SiteVipOptionVO> siteVipOptionMap = siteVIPGradePOList.stream().collect(Collectors.toMap(SiteVipOptionVO::getVipGradeCode, e -> e));
            if (po.getVipGradeCode() == 1){
                //最低等级不降级
                continue;
            }
            Integer vipGradeCode=userInfoVO.getVipGradeCode()-1;
            Integer vipGradeUp = 0;
            if (userInfoVO.getVipGradeCode().equals(maxItem.getVipGradeCode()) && userInfoVO.getVipGradeUp().equals(maxItem.getVipGradeCode())){
                //下集等级
                 vipGradeUp= userInfoVO.getVipGradeUp();
            }else{
                //下集等级
                 vipGradeUp= userInfoVO.getVipGradeUp()-1;
            }
            //已完成保级流水
            BigDecimal finishRelegationAmount=BigDecimal.ZERO;
            //已完成流水
            BigDecimal finishBetAmount = BigDecimal.ZERO;
            //单次有效流水
            BigDecimal betAmountExe =BigDecimal.ZERO;
            //保级流水
            BigDecimal relegationAmount = siteVipOptionMap.get(vipGradeCode).getRelegationAmount();
            //降级后等级配置
            SiteVipOptionVO siteVipOptionVO= siteVipOptionMap.get(vipGradeUp);
            //添加失效天数转换
            LocalDateTime fuRelegationDate = localDateTime.plusDays(siteVipOptionMap.get(vipGradeCode).getRelegationDays()+1);
            String fuRelegationDateStr =TimeZoneUtils.formatLocalDateTime(fuRelegationDate,TimeZoneUtils.patten_yyyyMMdd);
            LambdaUpdateWrapper<UserInfoPO> updateWrapper = new LambdaUpdateWrapper<>();
            //更新用户等级
            updateWrapper.eq(UserInfoPO::getUserId, userInfoVO.getUserId());
            updateWrapper.set(UserInfoPO::getVipGradeCode, vipGradeCode);
            updateWrapper.set(UserInfoPO::getVipGradeUp, vipGradeUp);
            updateUsers.add(updateWrapper);
            batchInsertVip.add(this.initSiteVipChangeRecordCnPO(userInfoVO,vipGradeCode,VipChangeTypeEnum.down.getCode(),localDateTimeStr));
            batchInsertUserVipFlow.add(this.initVipFlow(userInfoVO,siteVipOptionVO,localDateTimeStr,vipGradeCode,vipGradeUp,finishRelegationAmount,finishBetAmount,fuRelegationDateStr,betAmountExe,relegationAmount,siteVipOptionMap.get(vipGradeCode).getRelegationDays()));
        }
        this.saveBatch(batchInsertUserVipFlow);
        this.batchUpdateUser(updateUsers);
        siteVipChangeRecordCnService.saveBatch(batchInsertVip);
    }

    private void batchUpUserVip(List<UserVipFlowRecordCnPO> userVipFlowRecords,Map<String, UserInfoVO> allUserMap,String localDateTimeStr,LocalDateTime localDateTime){
        log.info("该用户升级自动升级:{},{},{},{}", userVipFlowRecords,allUserMap,localDateTimeStr,localDateTime);
        //用户
        List<LambdaUpdateWrapper<UserInfoPO>> updateUsers= new ArrayList<>();
        //用户等级记录
        List<SiteVipChangeRecordCnPO> batchInsertVip= new ArrayList<>();
        List<UserVipFlowRecordCnPO> batchInsertUserVipFlow= new ArrayList<>();
        List<VipUpGradeAwardVO> vipUpGradeAwardVOList=new ArrayList<>();
        //用户vip流水
        for (UserVipFlowRecordCnPO po:userVipFlowRecords){
            UserInfoVO userInfoVO= allUserMap.get(po.getUserId());
            List<SiteVipOptionVO> siteVIPGradePOList = siteVipOptionService.getList(userInfoVO.getSiteCode(),userInfoVO.getMainCurrency());
            SiteVipOptionVO maxItem = siteVIPGradePOList.stream()
                    .max(Comparator.comparing(SiteVipOptionVO::getId))
                    .orElse(null);
            Map<Integer, SiteVipOptionVO> siteVipOptionMap = siteVIPGradePOList.stream().collect(Collectors.toMap(SiteVipOptionVO::getVipGradeCode, e -> e));
            //升级逻辑
            //查询用户今天是否有升级
            SiteVipChangeRecordCnPO siteVipChangeRecordCnPO = siteVipChangeRecordCnService.getBaseMapper().selectOne(Wrappers.lambdaQuery(SiteVipChangeRecordCnPO.class)
                    .eq(SiteVipChangeRecordCnPO::getUserId, userInfoVO.getUserId())
                    .eq(SiteVipChangeRecordCnPO::getChangeType, VipChangeTypeEnum.up.getCode())
                    .eq(SiteVipChangeRecordCnPO::getUpVipTime, localDateTimeStr)
                    .last(" order by created_time  limit 1 "));
            if (!Objects.isNull(siteVipChangeRecordCnPO)){
                continue;
            }
            if (po.getVipGradeCode().equals(maxItem.getVipGradeCode()) && localDateTimeStr.equals(po.getRelegationDaysTime()) ){
                //最高等级不升级
                continue;
            }
            //vip当前等级
            Integer vipGradeCode=userInfoVO.getVipGradeCode()+1;
            //下集等级
            Integer vipGradeUp= userInfoVO.getVipGradeUp()+1;
            //已完成保级流水
            BigDecimal finishRelegationAmount=po.getFinishBetAmount().subtract(po.getUpgradeBetAmount());
            //已完成流水
            BigDecimal finishBetAmount = po.getFinishBetAmount().subtract(po.getUpgradeBetAmount());
            //单次有效流水
            BigDecimal betAmountExe =BigDecimal.ZERO;
            //保级流水
            BigDecimal relegationAmount = siteVipOptionMap.get(vipGradeCode).getRelegationAmount();
            if (vipGradeCode.equals(maxItem.getVipGradeCode())){
                vipGradeUp =maxItem.getVipGradeCode();
            }
            //升级后等级配置
            SiteVipOptionVO siteVipOptionVO= siteVipOptionMap.get(vipGradeUp);
            //添加失效天数转换
            LocalDateTime fuRelegationDate = localDateTime.plusDays(siteVipOptionMap.get(vipGradeCode).getRelegationDays()+1);
            String fuRelegationDateStr =TimeZoneUtils.formatLocalDateTime(fuRelegationDate,TimeZoneUtils.patten_yyyyMMdd);
            LambdaUpdateWrapper<UserInfoPO> updateWrapper = new LambdaUpdateWrapper<>();
            //更新用户等级
            updateWrapper.eq(UserInfoPO::getUserId, userInfoVO.getUserId());
            updateWrapper.set(UserInfoPO::getVipGradeCode, vipGradeCode);
            updateWrapper.set(UserInfoPO::getVipGradeUp, vipGradeUp);
            updateUsers.add(updateWrapper);

            batchInsertVip.add(this.initSiteVipChangeRecordCnPO(userInfoVO,vipGradeCode,VipChangeTypeEnum.up.getCode(),localDateTimeStr));
            batchInsertUserVipFlow.add(this.initVipFlow(userInfoVO,siteVipOptionVO,localDateTimeStr,vipGradeCode,vipGradeUp,finishRelegationAmount,finishBetAmount,fuRelegationDateStr,betAmountExe,relegationAmount,siteVipOptionMap.get(vipGradeCode).getRelegationDays()));
            VipUpGradeAwardVO vodata=new VipUpGradeAwardVO();
            vodata.setUserInfoVO(userInfoVO);
            vodata.setSiteVipOptionVO(siteVipOptionMap.get(vipGradeCode));
            vipUpGradeAwardVOList.add(vodata);
        }
        this.saveBatch(batchInsertUserVipFlow);
        this.batchUpdateUser(updateUsers);
        siteVipChangeRecordCnService.saveBatch(batchInsertVip);
        vipRewardsService.vipUpGradeRewardBatchHandle(vipUpGradeAwardVOList);
        log.info("该用户升级发放升级奖励入口:{}", vipUpGradeAwardVOList);
    }

    private void batchUpMaxVipAmount(List<UserVipFlowRecordCnPO> userVipFlowRecords,Map<String, UserInfoVO> allUserMap,String localDateTimeStr,LocalDateTime localDateTime) {
        log.info("该用户升级batchUpMaxVipAmount:{},{},{},{}", userVipFlowRecords,allUserMap,localDateTimeStr,localDateTime);
        List<UserVipFlowRecordCnPO> batchInsertUserVipFlow= new ArrayList<>();
        Long time=System.currentTimeMillis();
        for (UserVipFlowRecordCnPO po:userVipFlowRecords) {
            po.setId(SnowFlakeUtils.getSnowId());
            po.setCreatedTime(time);
            po.setUpdatedTime(time);
            po.setFinishRelegationAmount(BigDecimal.ZERO);
            po.setRelegationDays(po.getRelegationDays());
            po.setRelegationDaysTime(calculateDate(localDateTimeStr, po.getRelegationDays()));
            po.setUpVipTime(localDateTimeStr);
            batchInsertUserVipFlow.add(po);
        }
        this.saveBatch(batchInsertUserVipFlow);
    }

    private SiteVipChangeRecordCnPO initSiteVipChangeRecordCnPO(UserInfoVO userInfoVO, Integer vipNow, Integer changeType, String localDateTime){
        return SiteVipChangeRecordCnPO.builder().siteCode(userInfoVO.getSiteCode()).changeType(changeType)
                .userId(userInfoVO.getUserId()).userAccount(userInfoVO.getUserAccount()).accountType(userInfoVO.getAccountType())
                .accountStatus(userInfoVO.getAccountStatus()).userLabelId(userInfoVO.getUserLabelId()).userLabel(userInfoVO.getUserLabelName()).userRiskLevelId(userInfoVO.getRiskLevelId())
                .userRiskLevel(userInfoVO.getRiskLevel()).upVipTime(localDateTime)
                .vipOld(userInfoVO.getVipGradeCode()).vipNow(vipNow).build();
    }

    private UserVipFlowRecordCnPO initVipFlow(UserInfoVO userInfoVO,SiteVipOptionVO siteVipOptionVO,String localDateTimeStr,
                                             Integer vipGradeCode,Integer vipGradeUp, BigDecimal finishRelegationAmount,BigDecimal finishBetAmount,String fuRelegationDateStr, BigDecimal betAmountExe,BigDecimal relegationAmount,Integer days){
       return  UserVipFlowRecordCnPO.builder().userId(userInfoVO.getUserId()).siteCode(userInfoVO.getSiteCode()).userAccount(userInfoVO.getUserAccount()).
                vipGradeCode(vipGradeCode).nextVipGradeCode(vipGradeUp).relegationDays(days).upVipTime(localDateTimeStr).
               finishRelegationAmount(finishRelegationAmount).betAmountExe(betAmountExe).currencyCode(userInfoVO.getMainCurrency()).
               finishBetAmount(finishBetAmount).relegationDaysTime(fuRelegationDateStr).
               upgradeBetAmount(siteVipOptionVO.getVipUpgradeExp()).gradeRelegationAmount(relegationAmount).build();
    }

    private void batchUpdateUser(List<LambdaUpdateWrapper<UserInfoPO>> userList) {
        for (LambdaUpdateWrapper<UserInfoPO> user : userList) {
            userInfoService.update(user);
        }
    }




    /**
     * 根据字符串日期和天数偏移量计算新日期
     * @param dateStr 原始日期字符串，格式：yyyy-MM-dd
     * @param days 天数偏移量，正数表示加天数，负数表示减天数
     * @return 计算后的日期字符串
     */
    public static String calculateDate(String dateStr, int days) {
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 解析字符串为LocalDate
        LocalDate date = LocalDate.parse(dateStr, formatter);
        // 计算偏移后的日期
        LocalDate resultDate = date.plusDays(days+1);
        // 格式化为字符串返回
        return resultDate.format(formatter);
    }

    public void batchUpdateVIPconfigUserVip(SiteVipOptionVO vo){
        SiteVO siteVO = siteApi.getSiteInfo(vo.getSiteCode()).getData();
        List<UserVipFlowRecordCnPO> all=new ArrayList<>();
        //获取该站点下该层级的用户内容批量修改 当前VIP 升级经验以及保级流水，保级时间相关内容
        List<UserVipFlowRecordCnPO> userVipNextFlowRecords = this.getBaseMapper().lastSiteVipChangeRecordCnPOs(UserVipFlowRecordReqVO.builder().
                siteCode(vo.getSiteCode()).currencyCode(vo.getCurrencyCode()).nextVipGradeCode(vo.getVipGradeCode()).build());
        userVipNextFlowRecords.forEach(e ->{
            e.setUpgradeBetAmount(vo.getVipUpgradeExp());
        });
        //获取该站点当前成绩下层级的用户内容批量修改 以及保级流水，保级时间相关内容
        List<UserVipFlowRecordCnPO> userVipFlowRecords = this.getBaseMapper().lastSiteVipChangeRecordCnPOs(UserVipFlowRecordReqVO.builder().
                siteCode(vo.getSiteCode()).currencyCode(vo.getCurrencyCode()).vipGradeCode(vo.getVipGradeCode()).build());
        userVipFlowRecords.forEach(e ->{
            LocalDateTime localDateTime= TimeZoneUtils.timeByTimeZone(e.getCreatedTime(),siteVO.getTimezone());
            LocalDateTime fuRelegationDate =  localDateTime.plusDays(vo.getRelegationDays()+1);
            String fuRelegationDateStr =TimeZoneUtils.formatLocalDateTime(fuRelegationDate,TimeZoneUtils.patten_yyyyMMdd);
            e.setRelegationDaysTime(fuRelegationDateStr);
            e.setRelegationDays(vo.getRelegationDays());
            e.setGradeRelegationAmount(vo.getRelegationAmount());
        });
        all.addAll(userVipNextFlowRecords);
        all.addAll(userVipFlowRecords);
        this.saveOrUpdateBatch(all);
    }

    private List<String> stringToList(String str) {
        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }
        // 使用 split 方法将字符串按逗号分隔
        String[] items = str.split(",");
        // 将数组转换为列表
        return Arrays.asList(items);
    }
}
