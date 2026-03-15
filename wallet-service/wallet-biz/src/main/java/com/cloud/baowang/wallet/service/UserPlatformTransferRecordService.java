package com.cloud.baowang.wallet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.userwallet.ClientUserPlatformTransferRecordReqVO;
import com.cloud.baowang.wallet.api.vo.userwallet.ClientUserPlatformTransferRespVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserPlatformTransferCondReqVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserPlatformTransferRespVO;
import com.cloud.baowang.wallet.po.UserPlatformTransferRecordPO;
import com.cloud.baowang.wallet.repositories.UserPlatformTransferRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Desciption: 平台币兑换
 * @Author: Ford
 * @Date: 2024/10/16 11:55
 * @Version: V1.0
 **/
@Service
@Slf4j
@AllArgsConstructor
public class UserPlatformTransferRecordService extends ServiceImpl<UserPlatformTransferRecordRepository, UserPlatformTransferRecordPO> {
    private UserPlatformTransferRecordRepository userPlatformTransferRecordRepository;

    /**
     * 平台币划转记录
     *
     * @param userPlatformTransferRecordPO 记录数据
     */
    public void insert(UserPlatformTransferRecordPO userPlatformTransferRecordPO) {
        LambdaQueryWrapper<UserPlatformTransferRecordPO> lambdaQueryWrapper = new LambdaQueryWrapper<UserPlatformTransferRecordPO>();
        lambdaQueryWrapper.eq(UserPlatformTransferRecordPO::getOrderNo, userPlatformTransferRecordPO.getOrderNo());
        long countNum = userPlatformTransferRecordRepository.selectCount(lambdaQueryWrapper);
        if (countNum <= 0) {
            log.info("开始记录平台币兑换记录:{}", userPlatformTransferRecordPO);
            userPlatformTransferRecordRepository.insert(userPlatformTransferRecordPO);
        }
    }

    /**
     * 分页查询
     *
     * @param userPlatformTransferCondReqVO 查询条件
     * @return
     */
    public Page<UserPlatformTransferRespVO> listPage(UserPlatformTransferCondReqVO userPlatformTransferCondReqVO) {
        LambdaQueryWrapper<UserPlatformTransferRecordPO> lambdaQueryWrapper = new LambdaQueryWrapper<UserPlatformTransferRecordPO>();
        lambdaQueryWrapper.eq(UserPlatformTransferRecordPO::getSiteCode, userPlatformTransferCondReqVO.getSiteCode());
        if (userPlatformTransferCondReqVO.getBeginTime() != null) {
            lambdaQueryWrapper.ge(UserPlatformTransferRecordPO::getOrderTime, userPlatformTransferCondReqVO.getBeginTime());
        }
        if (userPlatformTransferCondReqVO.getEndTime() != null) {
            lambdaQueryWrapper.le(UserPlatformTransferRecordPO::getOrderTime, userPlatformTransferCondReqVO.getEndTime());
        }
        if (StringUtils.hasText(userPlatformTransferCondReqVO.getOrderNo())) {
            lambdaQueryWrapper.eq(UserPlatformTransferRecordPO::getOrderNo, userPlatformTransferCondReqVO.getOrderNo());
        }
        if (StringUtils.hasText(userPlatformTransferCondReqVO.getUserAccount())) {
            lambdaQueryWrapper.eq(UserPlatformTransferRecordPO::getUserAccount, userPlatformTransferCondReqVO.getUserAccount());
        }
        if (StringUtils.hasText(userPlatformTransferCondReqVO.getTargetCurrencyCode())) {
            lambdaQueryWrapper.eq(UserPlatformTransferRecordPO::getTargetCurrencyCode, userPlatformTransferCondReqVO.getTargetCurrencyCode());
        }
        lambdaQueryWrapper.orderByDesc(UserPlatformTransferRecordPO::getOrderTime);
        Page<UserPlatformTransferRecordPO> pageParam = new Page<UserPlatformTransferRecordPO>(userPlatformTransferCondReqVO.getPageNumber(), userPlatformTransferCondReqVO.getPageSize());
        Page<UserPlatformTransferRecordPO> userPlatformTransferRecordPOPage = userPlatformTransferRecordRepository.selectPage(pageParam, lambdaQueryWrapper);
        Page<UserPlatformTransferRespVO> respVOPage = new Page<UserPlatformTransferRespVO>();
        BeanUtils.copyProperties(userPlatformTransferRecordPOPage, respVOPage);
        if (userPlatformTransferRecordPOPage.getTotal() >= 1) {
            List<UserPlatformTransferRecordPO> userPlatformTransferRecordPOList = userPlatformTransferRecordPOPage.getRecords();
            List<UserPlatformTransferRespVO> respVOList = Lists.newArrayList();
            for (UserPlatformTransferRecordPO userPlatformTransferRecordPO : userPlatformTransferRecordPOList) {
                UserPlatformTransferRespVO userPlatformTransferRespVO = new UserPlatformTransferRespVO();
                BeanUtils.copyProperties(userPlatformTransferRecordPO, userPlatformTransferRespVO);
                respVOList.add(userPlatformTransferRespVO);
            }
            respVOPage.setRecords(respVOList);
        }
        return respVOPage;
    }


    public ResponseVO<Page<ClientUserPlatformTransferRespVO>> platformTransferRecord(ClientUserPlatformTransferRecordReqVO vo) {
        LambdaQueryWrapper<UserPlatformTransferRecordPO> lambdaQueryWrapper = new LambdaQueryWrapper<UserPlatformTransferRecordPO>();

        lambdaQueryWrapper.ge(null != vo.getBeginTime(), UserPlatformTransferRecordPO::getOrderTime, vo.getBeginTime());
        lambdaQueryWrapper.le(null != vo.getEndTime(), UserPlatformTransferRecordPO::getOrderTime, vo.getEndTime());
        lambdaQueryWrapper.eq(UserPlatformTransferRecordPO::getSiteCode, vo.getSiteCode());
        lambdaQueryWrapper.eq(UserPlatformTransferRecordPO::getUserId, vo.getUserId());
        lambdaQueryWrapper.orderByDesc(UserPlatformTransferRecordPO::getOrderTime);
        Page<UserPlatformTransferRecordPO> pageParam = new Page<UserPlatformTransferRecordPO>(vo.getPageNumber(), vo.getPageSize());
        Page<UserPlatformTransferRecordPO> userPlatformTransferRecordPOPage = userPlatformTransferRecordRepository.selectPage(pageParam, lambdaQueryWrapper);
        Page<ClientUserPlatformTransferRespVO> respVOPage = new Page<>();
        BeanUtils.copyProperties(userPlatformTransferRecordPOPage, respVOPage);
        respVOPage.setRecords(ConvertUtil.entityListToModelList(userPlatformTransferRecordPOPage.getRecords(), ClientUserPlatformTransferRespVO.class));
        return ResponseVO.success(respVOPage);

    }

    public ResponseVO<Boolean> hasPlatformTransferRecord(String userId) {
        Integer count = userPlatformTransferRecordRepository.existsTransferRecordByUserId(userId);
        return ResponseVO.success(count != null && count > 0);
    }

    public BigDecimal getTransferAmountByUserAccount(String userAccount, String siteCode) {
        return userPlatformTransferRecordRepository.getTransferAmountByUserAccount(userAccount, siteCode);
    }
}
