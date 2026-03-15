package com.cloud.baowang.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.enums.VipChangeTypeEnum;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordPageQueryVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordRequestVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordVO;
import com.cloud.baowang.user.po.SiteVIPGradePO;
import com.cloud.baowang.user.po.SiteVIPRankPO;
import com.cloud.baowang.user.po.SiteVipChangeRecordPO;
import com.cloud.baowang.user.repositories.SiteVIPGradeRepository;
import com.cloud.baowang.user.repositories.SiteVIPRankRepository;
import com.cloud.baowang.user.repositories.SiteVipChangeRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class SiteVipChangeRecordService extends ServiceImpl<SiteVipChangeRecordRepository, SiteVipChangeRecordPO> {
    private final SiteVipChangeRecordRepository repository;
    private final SiteVIPGradeRepository siteVIPGradeRepository;
    private final SiteVIPRankRepository siteVIPRankRepository;
    private final UserInfoApi userInfoApi;
    private final RiskApi riskApi;
    private final SiteUserLabelConfigService userLabelConfigService;


    /**
     * 分页条件查询所有变更记录数据
     *
     * @param pageQueryVO 分页参数对象
     * @return page
     */
    public ResponseVO<Page<SiteVipChangeRecordVO>> queryChangeRecordPage(SiteVipChangeRecordPageQueryVO pageQueryVO) {
        try {
            Page<SiteVipChangeRecordPO> page = new Page<>(pageQueryVO.getPageNumber(), pageQueryVO.getPageSize());
            LambdaQueryWrapper<SiteVipChangeRecordPO> query = getSiteVipChangeRecordPOLambdaQueryWrapper(pageQueryVO);
            page = repository.selectPage(page, query);

            List<SiteVipChangeRecordPO> records = page.getRecords();
            Map<String, UserInfoVO> userMap = new HashMap<>();
            Map<Integer, String> vipGradeNameMap = new HashMap<>();
            Map<Integer, String> vipRankNameMap = new HashMap<>();
            Map<String, RiskLevelDetailsVO> riskLevelDetailsVOMap = new HashMap<>();
            if (CollectionUtil.isNotEmpty(records)) {
                //这里vip等级,段位转换下返回前端的名字
                List<Integer> beforeAfterParam = new ArrayList<>();
                for (SiteVipChangeRecordPO record : records) {
                    beforeAfterParam.add(Integer.parseInt(record.getBeforeChange()));
                    beforeAfterParam.add(Integer.parseInt(record.getAfterChange()));
                }
                if (pageQueryVO.getOperationType().equals(VipChangeTypeEnum.VIP_GRADE_CHANGE.getType())) {
                    //vip等级变更,查询vip等级名称
                    LambdaQueryWrapper<SiteVIPGradePO> gradeQuery = Wrappers.lambdaQuery();
                    gradeQuery.eq(SiteVIPGradePO::getSiteCode, pageQueryVO.getSiteCode())
                            .in(SiteVIPGradePO::getVipGradeCode, beforeAfterParam);
                    List<SiteVIPGradePO> siteVIPGradePOS = siteVIPGradeRepository.selectList(gradeQuery);
                    for (SiteVIPGradePO siteVIPGradePO : siteVIPGradePOS) {
                        vipGradeNameMap.put(siteVIPGradePO.getVipGradeCode(), siteVIPGradePO.getVipGradeName());
                    }
                } else {
                    //vip段位变更,查询vip段位名称
                    LambdaQueryWrapper<SiteVIPRankPO> vipRankQuery = Wrappers.lambdaQuery();
                    vipRankQuery.eq(SiteVIPRankPO::getSiteCode, pageQueryVO.getSiteCode())
                            .in(SiteVIPRankPO::getVipRankCode, beforeAfterParam);
                    List<SiteVIPRankPO> siteVIPRankPOS = siteVIPRankRepository.selectList(vipRankQuery);
                    for (SiteVIPRankPO siteVIPRankPO : siteVIPRankPOS) {
                        vipRankNameMap.put(siteVIPRankPO.getVipRankCode(), I18nMessageUtil.getI18NMessageInAdvice(siteVIPRankPO.getVipRankNameI18nCode()));
                    }
                }
                if (pageQueryVO.getOperationType().equals(VipChangeTypeEnum.VIP_GRADE_CHANGE.getType())) {
                    //vip等级名称组装
                    records.forEach(item -> {
                        item.setBeforeChange(vipGradeNameMap.get(Integer.parseInt(item.getBeforeChange())));
                        item.setAfterChange(vipGradeNameMap.get(Integer.parseInt(item.getAfterChange())));
                    });
                } else {
                    //vip段位名称组装
                    records.forEach(item -> {
                        item.setBeforeChange(vipRankNameMap.get(Integer.parseInt(item.getBeforeChange())));
                        item.setAfterChange(vipRankNameMap.get(Integer.parseInt(item.getAfterChange())));
                    });
                }

                List<String> userAccounts = records.stream().map(SiteVipChangeRecordPO::getUserAccount).toList();
                List<UserInfoVO> userVos = userInfoApi.getUserBalanceBySiteCodeUserAccount(pageQueryVO.getSiteCode(), userAccounts);
                if (CollectionUtil.isNotEmpty(userVos)) {
                    userMap = userVos.stream()
                            .collect(Collectors.toMap(UserInfoVO::getUserAccount, user -> user));

                    List<String> riskId = userVos.stream().map(UserInfoVO::getRiskLevelId).filter(StringUtils::isNotBlank).toList();
                    riskLevelDetailsVOMap = riskApi.getByIds(new ArrayList<>(riskId));
                }
            }


            //查询会员风控
            Map<String, UserInfoVO> finalUserMap = userMap;
            Map<String, RiskLevelDetailsVO> finalRiskLevelDetailsVOMap = riskLevelDetailsVOMap;

            IPage<SiteVipChangeRecordVO> result = page.convert(item -> {
                SiteVipChangeRecordVO vo = BeanUtil.copyProperties(item, SiteVipChangeRecordVO.class);

                UserInfoVO userInfoVO = finalUserMap.get(vo.getUserAccount());
                if (userInfoVO != null) {
                    vo.setAccountStatus(userInfoVO.getAccountStatus());
                    vo.setAccountType(userInfoVO.getAccountType());
                    vo.setUserLabel(userInfoVO.getUserLabelId());
                    //风控id不为空
                    if (StringUtils.isNotBlank(userInfoVO.getRiskLevelId()) && finalRiskLevelDetailsVOMap.containsKey(userInfoVO.getRiskLevelId())) {
                        //组装风控信息
                        vo.setControlRank(finalRiskLevelDetailsVOMap.get(userInfoVO.getRiskLevelId()).getRiskControlLevel());
                    }
                    //组装会员标签
                    String userLabel = vo.getUserLabel();
                    if (StringUtils.isNotBlank(userLabel)) {
                        //获取到当前会员所有标签，组装名称
                        List<GetUserLabelByIdsVO> userLabelArr = userLabelConfigService.getUserLabelByIds(Arrays.asList(userLabel.split(",")));
                        vo.setUserLabelByIdsVOS(userLabelArr);
                        vo.setUserLabelName(StringUtils.join(userLabelArr.stream()
                                .map(GetUserLabelByIdsVO::getLabelName).toList(), ","));
                    }
                }
                return vo;
            });
            return ResponseVO.success(ConvertUtil.toConverPage(result));
        } catch (Exception e) {
            log.error("查询变更记录列表失败，:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static LambdaQueryWrapper<SiteVipChangeRecordPO> getSiteVipChangeRecordPOLambdaQueryWrapper(SiteVipChangeRecordPageQueryVO pageQueryVO) {
        LambdaQueryWrapper<SiteVipChangeRecordPO> query = Wrappers.lambdaQuery();
        query.eq(SiteVipChangeRecordPO::getOperationType, pageQueryVO.getOperationType());

        query.eq(SiteVipChangeRecordPO::getSiteCode, pageQueryVO.getSiteCode());
        //变更类型
        String changeType = pageQueryVO.getChangeType();
        //开始时间
        Long startTime = pageQueryVO.getStartTime();
        //结束时间
        Long endTime = pageQueryVO.getEndTime();
        //会员账号
        String userAccount = pageQueryVO.getUserAccount();
        //操作人
        String operator = pageQueryVO.getOperator();
        if (StringUtils.isNotBlank(changeType)) {
            query.eq(SiteVipChangeRecordPO::getChangeType, changeType);
        }
        if (startTime != null) {
            query.ge(SiteVipChangeRecordPO::getChangeTime, startTime);
        }
        if (endTime != null) {
            query.le(SiteVipChangeRecordPO::getChangeTime, endTime);
        }
        if (StringUtils.isNotBlank(userAccount)) {
            query.like(SiteVipChangeRecordPO::getUserAccount, userAccount);
        }
        if (StringUtils.isNotBlank(operator)) {
            query.like(SiteVipChangeRecordPO::getOperator, operator);
        }
        if(StringUtils.isNotBlank(pageQueryVO.getOrderField())  && StringUtils.isNotBlank(pageQueryVO.getOrderType())){
            query.orderBy(true, pageQueryVO.getOrderType().equalsIgnoreCase(CommonConstant.ORDER_BY_ASC), SiteVipChangeRecordPO::getChangeTime);
        }else {
            query.orderByDesc(SiteVipChangeRecordPO::getCreatedTime);
        }
        return query;
    }

    /**
     * 新增变更记录
     *
     * @param changeRecordVO 新增实体对象
     * @return void
     */
    @Transactional
    public ResponseVO<Boolean> insertChangeInfo(SiteVipChangeRecordRequestVO changeRecordVO) {
        SiteVipChangeRecordPO po = BeanUtil.copyProperties(changeRecordVO, SiteVipChangeRecordPO.class);

        //变更前
        String beforeChange = changeRecordVO.getBeforeChange();
        //变更后
        String afterChange = changeRecordVO.getAfterChange();
        if (!beforeChange.equals(afterChange)) {
            //变更前后等级不一致,才入库(可能存在最高等级,再升级的情况)
            String operator = changeRecordVO.getOperator();
            //升级
            po.setChangeType(VipChangeTypeEnum.UPGRADETYPE.getType());
            po.setOperationType(VipChangeTypeEnum.VIP_GRADE_CHANGE.getType());
            po.setCreator(operator);
            po.setUpdater(operator);
            po.setUpdatedTime(System.currentTimeMillis());
            po.setCreatedTime(System.currentTimeMillis());
            LambdaQueryWrapper<SiteVIPGradePO> beforeQuery = Wrappers.lambdaQuery();
            beforeQuery.eq(SiteVIPGradePO::getSiteCode, po.getSiteCode()).eq(SiteVIPGradePO::getVipGradeCode, Integer.parseInt(beforeChange));
            SiteVIPGradePO beforeVipGrade = siteVIPGradeRepository.selectOne(beforeQuery);
            //获取当前vip所属段位
            Integer vipRank = beforeVipGrade.getVipRankCode();
            //获取变更后vip等级,对应所在的段位
            LambdaQueryWrapper<SiteVIPGradePO> query = Wrappers.lambdaQuery();
            query.eq(SiteVIPGradePO::getSiteCode, po.getSiteCode()).eq(SiteVIPGradePO::getVipGradeCode, Integer.parseInt(afterChange));
            SiteVIPGradePO afterVipGradePO = siteVIPGradeRepository.selectOne(query);
            if (afterVipGradePO == null) {
                throw new BaowangDefaultException(ResultCode.DATA_IS_EXIST);
            }
            Integer afterVipRank = afterVipGradePO.getVipRankCode();
            if (!vipRank.equals(afterVipRank)) {
                log.info("当前存在会员:{},段位变更,会员当前段位:{},变更后段位:{}", po.getUserAccount(), vipRank, afterVipRank);
                //不等于这个段位,说明要去生成段位变更记录,这里是一张表,用类型区分
                SiteVipChangeRecordPO vipRankChangeRecord = BeanUtil.copyProperties(po, SiteVipChangeRecordPO.class);
                vipRankChangeRecord.setId(null);
                //变更类型为段位变更
                vipRankChangeRecord.setOperationType(VipChangeTypeEnum.VIP_RANK_CHANGE.getType());
                //会员当前段位
                vipRankChangeRecord.setBeforeChange(String.valueOf(vipRank));
                //变更后的段位
                vipRankChangeRecord.setAfterChange(String.valueOf(afterVipRank));
                repository.insert(vipRankChangeRecord);
            }
            repository.insert(po);
        }
        return ResponseVO.success();
    }

    public ResponseVO<Long> getTotalCount(SiteVipChangeRecordPageQueryVO reqVO) {
        LambdaQueryWrapper<SiteVipChangeRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(reqVO.getChangeType()),
                SiteVipChangeRecordPO::getChangeType, reqVO.getChangeType());
        queryWrapper.like(StringUtils.isNotEmpty(reqVO.getUserAccount()),
                SiteVipChangeRecordPO::getUserAccount, reqVO.getUserAccount());
        queryWrapper.eq(StringUtils.isNotEmpty(reqVO.getOperator()),
                SiteVipChangeRecordPO::getOperator, reqVO.getOperator());
        if (ObjectUtils.isNotEmpty(reqVO.getStartTime())) {
            queryWrapper.ge(SiteVipChangeRecordPO::getChangeTime, reqVO.getStartTime());
        }
        if (ObjectUtils.isNotEmpty(reqVO.getEndTime())) {
            queryWrapper.le(SiteVipChangeRecordPO::getChangeTime, reqVO.getEndTime());
        }
        return ResponseVO.success(repository.selectCount(queryWrapper));
    }


    public ResponseVO<Long> queryOperationCount(SiteVipChangeRecordPageQueryVO pageQueryVO) {
        LambdaQueryWrapper<SiteVipChangeRecordPO> query = getSiteVipChangeRecordPOLambdaQueryWrapper(pageQueryVO);
        return ResponseVO.success(repository.selectCount(query));
    }

    public ResponseVO<Boolean> insertChangeRecordList(List<SiteVipChangeRecordRequestVO> requestVOs) {
        List<SiteVipChangeRecordPO> pos = BeanUtil.copyToList(requestVOs, SiteVipChangeRecordPO.class);
        for (SiteVipChangeRecordPO po : pos) {
            po.setId(SnowFlakeUtils.getSnowId());
            //变更前
            String beforeChange = po.getBeforeChange();
            //变更后
            String afterChange = po.getAfterChange();
            if (!beforeChange.equals(afterChange)) {
                //变更前后等级不一致,才入库(可能存在最高等级,再升级的情况)
                String operator = po.getOperator();
                //升级
                po.setChangeType(VipChangeTypeEnum.UPGRADETYPE.getType());
                po.setOperationType(VipChangeTypeEnum.VIP_GRADE_CHANGE.getType());
                po.setCreator(operator);
                po.setUpdater(operator);
                po.setUpdatedTime(System.currentTimeMillis());
                po.setCreatedTime(System.currentTimeMillis());
                LambdaQueryWrapper<SiteVIPGradePO> beforeQuery = Wrappers.lambdaQuery();
                beforeQuery.eq(SiteVIPGradePO::getSiteCode, po.getSiteCode()).eq(SiteVIPGradePO::getVipGradeCode, Integer.parseInt(beforeChange));
                SiteVIPGradePO beforeVipGrade = siteVIPGradeRepository.selectOne(beforeQuery);
                //获取当前vip所属段位
                Integer vipRank = beforeVipGrade.getVipRankCode();
                //获取变更后vip等级,对应所在的段位
                LambdaQueryWrapper<SiteVIPGradePO> query = Wrappers.lambdaQuery();
                query.eq(SiteVIPGradePO::getSiteCode, po.getSiteCode()).eq(SiteVIPGradePO::getVipGradeCode, Integer.parseInt(afterChange));
                SiteVIPGradePO afterVipGradePO = siteVIPGradeRepository.selectOne(query);
                if (afterVipGradePO == null) {
                    throw new BaowangDefaultException(ResultCode.DATA_IS_EXIST);
                }
                Integer afterVipRank = afterVipGradePO.getVipRankCode();
                if (!vipRank.equals(afterVipRank)) {
                    log.info("当前存在会员:{},段位变更,会员当前段位:{},变更后段位:{}", po.getUserAccount(), vipRank, afterVipRank);
                    //不等于这个段位,说明要去生成段位变更记录,这里是一张表,用类型区分
                    SiteVipChangeRecordPO vipRankChangeRecord = BeanUtil.copyProperties(po, SiteVipChangeRecordPO.class);
                    vipRankChangeRecord.setId(SnowFlakeUtils.getSnowId());
                    //变更类型为段位变更
                    vipRankChangeRecord.setOperationType(VipChangeTypeEnum.VIP_RANK_CHANGE.getType());
                    //会员当前段位
                    vipRankChangeRecord.setBeforeChange(String.valueOf(vipRank));
                    //变更后的段位
                    vipRankChangeRecord.setAfterChange(String.valueOf(afterVipRank));
                    repository.insert(vipRankChangeRecord);
                }
                repository.insert(po);
            }
        }
        return ResponseVO.success();
    }
}
