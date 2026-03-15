package com.cloud.baowang.activity.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.GameSelectVO;
import com.cloud.baowang.activity.po.SiteActivityAssignDayPO;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityFirstRechargePO;
import com.cloud.baowang.activity.po.SiteActivitySecondRechargePO;
import com.cloud.baowang.activity.po.v2.SiteActivityAssignDayV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivityFirstRechargeV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivitySecondRechargeV2PO;
import com.cloud.baowang.activity.repositories.ActivityFirstRechargeRepository;
import com.cloud.baowang.activity.repositories.ActivitySecondRechargeRepository;
import com.cloud.baowang.activity.repositories.SiteActivityAssignDayRepository;
import com.cloud.baowang.activity.repositories.SiteActivityBaseRepository;
import com.cloud.baowang.activity.repositories.v2.ActivityFirstRechargeV2Repository;
import com.cloud.baowang.activity.repositories.v2.ActivitySecondRechargeV2Repository;
import com.cloud.baowang.activity.repositories.v2.SiteActivityAssignDayV2Repository;
import com.cloud.baowang.activity.repositories.v2.SiteActivityBaseV2Repository;
import com.cloud.baowang.activity.utils.DataUtils;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class ActivityGameService {
    private final ActivityFirstRechargeRepository firstRechargeRepository;

    private final ActivitySecondRechargeRepository secondRechargeRepository;

    private final SiteActivityAssignDayRepository assignDayRepository;

    private final SiteActivityBaseRepository baseRepository;

    private final ActivityFirstRechargeV2Repository firstRechargeV2Repository;

    private final ActivitySecondRechargeV2Repository secondRechargeV2Repository;

    private final SiteActivityAssignDayV2Repository assignDayV2Repository;

    private final SiteActivityBaseV2Repository baseV2Repository;


    public Boolean isCheckStatus(String activityId,String template, String siteCode){
        List<GameSelectVO> resultS = getGameSelect(template, siteCode);

        Integer handicapMode = CurrReqUtils.getHandicapMode();

        if (handicapMode==null || handicapMode==0){
            // 查询本身
            if (template.equals(ActivityTemplateEnum.FIRST_DEPOSIT.getType())) {
                SiteActivityFirstRechargePO firstRechargePO = firstRechargeRepository.selectOne(new LambdaQueryWrapper<SiteActivityFirstRechargePO>()
                        .eq(SiteActivityFirstRechargePO::getSiteCode, siteCode)
                        .eq(SiteActivityFirstRechargePO::getActivityId, activityId)
                        .last(" limit 1 "));
                if (firstRechargePO == null) {
                    return false;
                }else {
                    if(StringUtils.isBlank(firstRechargePO.getVenueType())){
                        return true;
                    }
                    for (GameSelectVO vo : resultS) {
                        if (!DataUtils.checkStringSame(firstRechargePO.getVenueType(), vo.getVenueType())) {
                            log.info("{}, 配置游戏大类校验失败");
                            return false;
                        }
                    }
                }
            }else if (template.equals(ActivityTemplateEnum.SECOND_DEPOSIT.getType())) {
                SiteActivitySecondRechargePO secondRechargePO = secondRechargeRepository.selectOne(new LambdaQueryWrapper<SiteActivitySecondRechargePO>()
                        .eq(SiteActivitySecondRechargePO::getSiteCode, siteCode)
                        .eq(SiteActivitySecondRechargePO::getActivityId, activityId));
                if (secondRechargePO == null) {
                    return false;
                }else {
                    if(StringUtils.isBlank(secondRechargePO.getVenueType())){
                        return true;
                    }
                    for (GameSelectVO vo : resultS) {
                        if (!DataUtils.checkStringSame(secondRechargePO.getVenueType(), vo.getVenueType())) {
                            log.info("{}, 次充配置游戏大类校验失败");
                            return false;
                        }
                    }
                }
            }else if (template.equals(ActivityTemplateEnum.ASSIGN_DAY.getType())) {
                SiteActivityAssignDayPO assignDayPO = assignDayRepository.selectOne(new LambdaQueryWrapper<SiteActivityAssignDayPO>()
                        .eq(SiteActivityAssignDayPO::getSiteCode, siteCode)
                        .eq(SiteActivityAssignDayPO::getActivityId, activityId));
                if (assignDayPO == null) {
                    return false;
                }else {
                    if(StringUtils.isBlank(assignDayPO.getVenueType())){
                        return true;
                    }
                    for (GameSelectVO vo : resultS) {
                        if (!DataUtils.checkStringSame(assignDayPO.getVenueType(), vo.getVenueType())) {
                            log.info("{}, 指定日V2配置游戏大类校验失败");
                            return false;
                        }
                    }
                }
            }
        }else {
            // 查询本身
            if (template.equals(ActivityTemplateV2Enum.FIRST_DEPOSIT_V2.getType())) {
                SiteActivityFirstRechargeV2PO firstRechargePO = firstRechargeV2Repository.selectOne(new LambdaQueryWrapper<SiteActivityFirstRechargeV2PO>()
                        .eq(SiteActivityFirstRechargeV2PO::getSiteCode, siteCode)
                        .eq(SiteActivityFirstRechargeV2PO::getActivityId, activityId)
                        .last(" limit 1 "));
                if (firstRechargePO == null) {
                    return false;
                }else {
                    if(StringUtils.isBlank(firstRechargePO.getVenueType())){
                        return true;
                    }
                    for (GameSelectVO vo : resultS) {
                        if (!DataUtils.checkStringSame(firstRechargePO.getVenueType(), vo.getVenueType())) {
                            log.info("{}, V2配置游戏大类校验失败");
                            return false;
                        }
                    }
                }
            }else if (template.equals(ActivityTemplateV2Enum.SECOND_DEPOSIT_V2.getType())) {
                SiteActivitySecondRechargeV2PO secondRechargePO = secondRechargeV2Repository.selectOne(new LambdaQueryWrapper<SiteActivitySecondRechargeV2PO>()
                        .eq(SiteActivitySecondRechargeV2PO::getSiteCode, siteCode)
                        .eq(SiteActivitySecondRechargeV2PO::getActivityId, activityId));
                if (secondRechargePO == null) {
                    return false;
                }else {
                    if(StringUtils.isBlank(secondRechargePO.getVenueType())){
                        return true;
                    }
                    for (GameSelectVO vo : resultS) {
                        if (!DataUtils.checkStringSame(secondRechargePO.getVenueType(), vo.getVenueType())) {
                            log.info("{}, 次充V2配置游戏大类校验失败");
                            return false;
                        }
                    }
                }
            }else if (template.equals(ActivityTemplateV2Enum.ASSIGN_DAY_V2.getType())) {
                SiteActivityAssignDayV2PO assignDayPO = assignDayV2Repository.selectOne(new LambdaQueryWrapper<SiteActivityAssignDayV2PO>()
                        .eq(SiteActivityAssignDayV2PO::getSiteCode, siteCode)
                        .eq(SiteActivityAssignDayV2PO::getActivityId, activityId));
                if (assignDayPO == null) {
                    return false;
                }else {
                    if(StringUtils.isBlank(assignDayPO.getVenueType())){
                        return true;
                    }
                    for (GameSelectVO vo : resultS) {
                        if (!DataUtils.checkStringSame(assignDayPO.getVenueType(), vo.getVenueType())) {
                            log.info("{}, 指定日V2配置游戏大类校验失败");
                            return false;
                        }
                    }
                }
            }
        }

        return true;

    }

    /**
     * @param template 活动模板
     * @param siteCode siteCode
     * @return 返回保存启用游戏大类配置
     */
    public List<GameSelectVO> getGameSelect(String template, String siteCode) {
        List<GameSelectVO> resultS = new ArrayList<>();


        Integer handicapMode = CurrReqUtils.getHandicapMode();

        if (handicapMode==null || handicapMode==0){

            List<SiteActivityBasePO> basePOS = baseRepository.selectList(new LambdaQueryWrapper<SiteActivityBasePO>()
                    .eq(SiteActivityBasePO::getSiteCode, siteCode)
                    .eq(SiteActivityBasePO::getStatus, StatusEnum.OPEN.getCode()));

            if (CollectionUtil.isEmpty(basePOS)) {
                return Collections.emptyList();
            }

            // 首次存款
            if (!template.equals(ActivityTemplateEnum.FIRST_DEPOSIT.getType())) {
                List<String> baseIds = basePOS.stream().map(SiteActivityBasePO::getId).toList();
                List<SiteActivityFirstRechargePO> siteActivity = firstRechargeRepository.selectList
                        (new LambdaQueryWrapper<SiteActivityFirstRechargePO>()
                                .eq(SiteActivityFirstRechargePO::getSiteCode, siteCode)
                                .in(SiteActivityFirstRechargePO::getActivityId, baseIds));

                if (CollectionUtil.isNotEmpty(siteActivity)) {
                    List<GameSelectVO> gameSelectVOS = siteActivity.stream()
                            .filter(activity -> StringUtils.isNotBlank(activity.getVenueType()))
                            .map(activity -> GameSelectVO.builder()
                                    .activityTemplate(ActivityTemplateEnum.FIRST_DEPOSIT.getType())
                                    .baseId(activity.getActivityId().toString())
                                    .venueType(activity.getVenueType()).build())
                            .toList();
                    resultS.addAll(gameSelectVOS);

                }
            }
            // 次存款
            if (!template.equals(ActivityTemplateEnum.SECOND_DEPOSIT.getType())) {
                List<String> baseIds = basePOS.stream().map(SiteActivityBasePO::getId).toList();
                List<SiteActivitySecondRechargePO> siteActivity = secondRechargeRepository.selectList
                        (new LambdaQueryWrapper<SiteActivitySecondRechargePO>()
                                .eq(SiteActivitySecondRechargePO::getSiteCode, siteCode)
                                .in(SiteActivitySecondRechargePO::getActivityId, baseIds));

                if (CollectionUtil.isNotEmpty(siteActivity)) {
                    List<GameSelectVO> gameSelectVOS = siteActivity.stream()
                            .filter(activity -> StringUtils.isNotBlank(activity.getVenueType()))
                            .map(activity -> GameSelectVO.builder()
                                    .activityTemplate(ActivityTemplateEnum.SECOND_DEPOSIT.getType())
                                    .baseId(activity.getActivityId().toString())
                                    .venueType(activity.getVenueType()).build())
                            .toList();
                    resultS.addAll(gameSelectVOS);

                }
            }
            // 指定日存款
            if (!template.equals(ActivityTemplateEnum.ASSIGN_DAY.getType())) {
                List<String> baseIds = basePOS.stream().map(SiteActivityBasePO::getId).toList();
                List<SiteActivityAssignDayPO> siteActivity = assignDayRepository.selectList
                        (new LambdaQueryWrapper<SiteActivityAssignDayPO>()
                                .eq(SiteActivityAssignDayPO::getSiteCode, siteCode)
                                .in(SiteActivityAssignDayPO::getActivityId, baseIds));

                if (CollectionUtil.isNotEmpty(siteActivity)) {
                    List<GameSelectVO> gameSelectVOS = siteActivity.stream()
                            .filter(activity -> StringUtils.isNotBlank(activity.getVenueType()))
                            .map(activity -> GameSelectVO.builder()
                                    .activityTemplate(ActivityTemplateEnum.ASSIGN_DAY.getType())
                                    .baseId(activity.getActivityId())
                                    .venueType(activity.getVenueType()).build()
                            )
                            .toList();
                    resultS.addAll(gameSelectVOS);

                }
            }
        }else {

            List<SiteActivityBaseV2PO> baseV2POS = baseV2Repository.selectList(new LambdaQueryWrapper<SiteActivityBaseV2PO>()
                    .eq(SiteActivityBaseV2PO::getSiteCode, siteCode)
                    .eq(SiteActivityBaseV2PO::getStatus, StatusEnum.OPEN.getCode()));

            if (CollectionUtil.isEmpty(baseV2POS)) {
                return Collections.emptyList();
            }
            // 首次存款
            if (!template.equals(ActivityTemplateV2Enum.FIRST_DEPOSIT_V2.getType())) {
                List<String> baseIds = baseV2POS.stream().map(SiteActivityBaseV2PO::getId).toList();
                List<SiteActivityFirstRechargeV2PO> siteActivity = firstRechargeV2Repository.selectList
                        (new LambdaQueryWrapper<SiteActivityFirstRechargeV2PO>()
                                .eq(SiteActivityFirstRechargeV2PO::getSiteCode, siteCode)
                                .in(SiteActivityFirstRechargeV2PO::getActivityId, baseIds));

                if (CollectionUtil.isNotEmpty(siteActivity)) {
                    List<GameSelectVO> gameSelectVOS = siteActivity.stream()
                            .filter(activity -> StringUtils.isNotBlank(activity.getVenueType()))
                            .map(activity -> GameSelectVO.builder()
                                    .activityTemplate(ActivityTemplateV2Enum.FIRST_DEPOSIT_V2.getType())
                                    .baseId(activity.getActivityId().toString())
                                    .venueType(activity.getVenueType()).build())
                            .toList();
                    resultS.addAll(gameSelectVOS);

                }
            }
            // 次存款
            if (!template.equals(ActivityTemplateV2Enum.SECOND_DEPOSIT_V2.getType())) {
                List<String> baseIds = baseV2POS.stream().map(SiteActivityBaseV2PO::getId).toList();
                List<SiteActivitySecondRechargeV2PO> siteActivity = secondRechargeV2Repository.selectList
                        (new LambdaQueryWrapper<SiteActivitySecondRechargeV2PO>()
                                .eq(SiteActivitySecondRechargeV2PO::getSiteCode, siteCode)
                                .in(SiteActivitySecondRechargeV2PO::getActivityId, baseIds));

                if (CollectionUtil.isNotEmpty(siteActivity)) {
                    List<GameSelectVO> gameSelectVOS = siteActivity.stream()
                            .filter(activity -> StringUtils.isNotBlank(activity.getVenueType()))
                            .map(activity -> GameSelectVO.builder()
                                    .activityTemplate(ActivityTemplateV2Enum.SECOND_DEPOSIT_V2.getType())
                                    .baseId(activity.getActivityId().toString())
                                    .venueType(activity.getVenueType()).build())
                            .toList();
                    resultS.addAll(gameSelectVOS);

                }
            }
            // 指定日存款
            if (!template.equals(ActivityTemplateV2Enum.ASSIGN_DAY_V2.getType())) {
                List<String> baseIds = baseV2POS.stream().map(SiteActivityBaseV2PO::getId).toList();
                List<SiteActivityAssignDayV2PO> siteActivity = assignDayV2Repository.selectList
                        (new LambdaQueryWrapper<SiteActivityAssignDayV2PO>()
                                .eq(SiteActivityAssignDayV2PO::getSiteCode, siteCode)
                                .in(SiteActivityAssignDayV2PO::getActivityId, baseIds));

                if (CollectionUtil.isNotEmpty(siteActivity)) {
                    List<GameSelectVO> gameSelectVOS = siteActivity.stream()
                            .filter(activity -> StringUtils.isNotBlank(activity.getVenueType()))
                            .map(activity -> GameSelectVO.builder()
                                    .activityTemplate(ActivityTemplateV2Enum.ASSIGN_DAY_V2.getType())
                                    .baseId(activity.getActivityId())
                                    .venueType(activity.getVenueType()).build()
                            )
                            .toList();
                    resultS.addAll(gameSelectVOS);

                }
            }
        }

        return resultS;

    }


}
