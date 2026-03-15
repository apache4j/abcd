package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordCnReqVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordCnVO;
import com.cloud.baowang.user.api.vo.vip.VIPGradeVO;
import com.cloud.baowang.user.po.SiteVipChangeRecordCnPO;
import com.cloud.baowang.user.repositories.SiteVipChangeRecordCnRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Desciption: mufan
 * @Date: 2024/7/27 15:22
 * @Version: V1.0
 **/
@Service
@AllArgsConstructor
@Slf4j
public class SiteVipChangeRecordCnService extends ServiceImpl<SiteVipChangeRecordCnRepository, SiteVipChangeRecordCnPO> {

     private SiteVipOptionService siteVipOptionService;

    public void isertSiteVipChangeRecordCn(UserInfoVO userInfoVO, String siteCode, Integer vipNow, Integer changeType, String localDateTime){
        SiteVipChangeRecordCnPO po= SiteVipChangeRecordCnPO.builder().siteCode(siteCode).changeType(changeType)
                .userId(userInfoVO.getUserId()).userAccount(userInfoVO.getUserAccount()).accountType(userInfoVO.getAccountType())
                .accountStatus(userInfoVO.getAccountStatus()).userLabelId(userInfoVO.getUserLabelId()).userLabel(userInfoVO.getUserLabelName()).userRiskLevelId(userInfoVO.getRiskLevelId())
                .userRiskLevel(userInfoVO.getRiskLevel()).upVipTime(localDateTime)
                .vipOld(userInfoVO.getVipGradeCode()).vipNow(vipNow).build();
        this.getBaseMapper().insert(po);
    }

    public void isertSystemSiteVipChangeRecordCn(String userId,String userAccount,String accountType,String accountStatus, String siteCode,Integer vipOld, Integer vipNow, Integer changeType, String localDateTime){
        SiteVipChangeRecordCnPO po= SiteVipChangeRecordCnPO.builder().siteCode(siteCode).changeType(changeType)
                .userId(userId).userAccount(userAccount).accountType(accountType)
                .accountStatus(accountStatus).upVipTime(localDateTime).vipOld(vipOld).vipNow(vipNow).build();
        this.getBaseMapper().insert(po);
    }

    public ResponseVO<Page<SiteVipChangeRecordCnVO>> getList(SiteVipChangeRecordCnReqVO vo){
        Page<SiteVipChangeRecordCnPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<SiteVipChangeRecordCnPO> query = getLambdaQueryWrapper(vo);
        page = this.getBaseMapper().selectPage(page, query);
        Page<SiteVipChangeRecordCnVO> resultPage = new Page<>();
        BeanUtils.copyProperties(page, resultPage);
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            resultPage.setRecords(ConvertUtil.entityListToModelList(page.getRecords(), SiteVipChangeRecordCnVO.class));
            Map<Integer,String> vipGradeMap = siteVipOptionService.getInitVIPGrade().stream().collect(Collectors.toMap(VIPGradeVO::getVipGradeCode, VIPGradeVO::getVipGradeName, (k1, k2) -> k2));
            resultPage.getRecords().forEach(e -> {
                e.setVipOldName(vipGradeMap.get(e.getVipOld()));
                e.setVipNowName(vipGradeMap.get(e.getVipNow()));
            });
        }
        return ResponseVO.success(resultPage);
    }

    @NotNull
    private static LambdaQueryWrapper<SiteVipChangeRecordCnPO> getLambdaQueryWrapper(SiteVipChangeRecordCnReqVO vo) {
        LambdaQueryWrapper<SiteVipChangeRecordCnPO> query = Wrappers.lambdaQuery();
        query.eq(SiteVipChangeRecordCnPO::getSiteCode, vo.getSiteCode());
        //变更类型
        Integer changeType = vo.getChangeType();
        //开始时间
        Long startTime = vo.getCreatedTimeStart();
        //结束时间
        Long endTime = vo.getCreatedTimeEnd();
        //会员账号
        String userAccount = vo.getUserAccount();
        //操作人
        String operator = vo.getCreator();
        if (ObjectUtils.isNotEmpty(changeType)) {
            query.eq(SiteVipChangeRecordCnPO::getChangeType, changeType);
        }
        if (startTime != null) {
            query.ge(SiteVipChangeRecordCnPO::getCreatedTime, startTime);
        }
        if (endTime != null) {
            query.le(SiteVipChangeRecordCnPO::getCreatedTime, endTime);
        }
        if (StringUtils.isNotBlank(userAccount)) {
            query.like(SiteVipChangeRecordCnPO::getUserAccount, userAccount);
        }
        if (StringUtils.isNotBlank(operator)) {
            query.like(SiteVipChangeRecordCnPO::getCreator, operator);
        }
        if(StringUtils.isNotBlank(vo.getOrderField())  && StringUtils.isNotBlank(vo.getOrderType())){
            query.orderBy(true, vo.getOrderType().equalsIgnoreCase(CommonConstant.ORDER_BY_ASC), SiteVipChangeRecordCnPO::getCreatedTime);
        }else {
            query.orderByDesc(SiteVipChangeRecordCnPO::getCreatedTime);
        }
        return query;
    }


    public Integer findVIPCodeByDay(String userId,long startDayTime, long endDayTime) {
        LambdaQueryWrapper<SiteVipChangeRecordCnPO> query = Wrappers.lambdaQuery();
        query.eq(SiteVipChangeRecordCnPO::getUserId, userId);
        query.ge(SiteVipChangeRecordCnPO::getCreatedTime, startDayTime);
        query.le(SiteVipChangeRecordCnPO::getCreatedTime, endDayTime);
        query.orderByDesc(SiteVipChangeRecordCnPO::getCreatedTime);
        query.last("limit 1");
        SiteVipChangeRecordCnPO siteVipChangeRecordCnPO=this.getBaseMapper().selectOne(query);
        if (siteVipChangeRecordCnPO==null){
            return 1;
        }else {
            return siteVipChangeRecordCnPO.getVipNow();
        }
    }
}
