package com.cloud.baowang.wallet.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountAdjustTypeEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserActivityTypingRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingRecordVO;
import com.cloud.baowang.wallet.po.*;
import com.cloud.baowang.wallet.repositories.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class UserActivityTypingAmountRecordService extends ServiceImpl<UserActivityTypingAmountRecordRepository, UserActivityTypingAmountRecordPO> {

    private final UserActivityTypingAmountRecordRepository repository;

    private final UserActivityTypingAmountService activityTypingAmountService;

    private final UserInfoApi userInfoApi;



    //改打码量+添加记录
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public boolean insertActivityTypingRecord(UserTypingAmountRequestVO typingVo, UserInfoVO userInfoVO){
        boolean limit = activityTypingAmountService.checkUserActivityTypingLimit(typingVo.getUserId(), typingVo.getSiteCode());
        if(!limit){
            return true;
        }

        LambdaQueryWrapper<UserActivityTypingAmountRecordPO> typingAmountRecordLqw = new LambdaQueryWrapper<>();
        typingAmountRecordLqw.eq(UserActivityTypingAmountRecordPO::getOrderNo,typingVo.getOrderNo());
        typingAmountRecordLqw.eq(UserActivityTypingAmountRecordPO::getAdjustWay,typingVo.getType());
        typingAmountRecordLqw.eq(UserActivityTypingAmountRecordPO::getMsgId,typingVo.getMsgId());
        List<UserActivityTypingAmountRecordPO> recordList = repository.selectList(typingAmountRecordLqw);
        if(!recordList.isEmpty()){
            log.info("订单编号为{}的订单已添加打码量",typingVo.getOrderNo());
            return false;
        }
        LambdaQueryWrapper<UserActivityTypingAmountPO> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserActivityTypingAmountPO::getUserId,typingVo.getUserId());
        UserActivityTypingAmountPO amountPO = activityTypingAmountService.getOne(lqw);


        try {
            //组装打码量记录
            List<UserActivityTypingAmountRecordPO> recordPOS = new ArrayList<>();
            List<UserTypingAmountRequestVO> typingList = typingVo.getTypingList();
            BigDecimal startValue =( null == amountPO || null ==amountPO.getTypingAmount())?BigDecimal.ZERO:amountPO.getTypingAmount();
            List<UserTypingAmountRequestVO> newTypingList = new ArrayList<>();
            for (UserTypingAmountRequestVO vo:typingList) {
                BigDecimal amount = vo.getTypingAmount();
                if(BigDecimal.ZERO.compareTo(amount) == 0){
                    continue;
                }
                String type = vo.getAdjustType();
                if(!type.equals(TypingAmountAdjustTypeEnum.BET.getCode()) && !type.equals(TypingAmountAdjustTypeEnum.SYSTEM.getCode())
                    && !type.equals(TypingAmountAdjustTypeEnum.MANUAL.getCode())){
                    continue;
                }
                if(type.equals(TypingAmountAdjustTypeEnum.MANUAL.getCode())){
                    if(null == vo.getIsClear() ||!vo.getIsClear() ){
                        continue;
                    }else{
                        amount = vo.getActivityTypingAmount() == null?vo.getTypingAmount():vo.getActivityTypingAmount();
                    }
                }
                if(type.equals(TypingAmountAdjustTypeEnum.SYSTEM.getCode())){
                    amount = vo.getActivityTypingAmount() == null?vo.getTypingAmount():vo.getActivityTypingAmount();
                }
                UserActivityTypingAmountRecordPO po = new UserActivityTypingAmountRecordPO();
                po.setUserAccount(userInfoVO.getUserAccount());
                po.setSiteCode(userInfoVO.getSiteCode());
                po.setUserId(userInfoVO.getUserId());
                po.setAccountType(userInfoVO.getAccountType());
                po.setOrderNo(vo.getOrderNo());
                po.setAdjustWay(vo.getType());
                po.setAdjustType(vo.getAdjustType());
                po.setCoinFrom(startValue);
                po.setCurrency(userInfoVO.getMainCurrency());
                po.setCoinValue(amount);
                po.setMsgId(typingVo.getMsgId());
                po.setCreatedTime(System.currentTimeMillis());
                BigDecimal afterChange = startValue.add(amount);
                afterChange = afterChange.compareTo(BigDecimal.ZERO) < 0 ?BigDecimal.ZERO:afterChange;
                po.setCoinTo(afterChange);
                if(startValue.compareTo(BigDecimal.ZERO) == 0 && afterChange.compareTo(BigDecimal.ZERO) == 0){
                    break;
                }
                startValue = afterChange;
                recordPOS.add(po);
                newTypingList.add(vo);
            }
            if(CollectionUtil.isEmpty(recordPOS)){
                return true;
            }
            this.saveBatch(recordPOS);
            BigDecimal typingAmount = newTypingList.stream().map(UserTypingAmountRequestVO::getTypingAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            if(null != amountPO){
                typingAmount = typingAmount.add(amountPO.getTypingAmount()==null? typingAmount:amountPO.getTypingAmount());
            }
            typingAmount = typingAmount.compareTo(BigDecimal.ZERO) < 0?BigDecimal.ZERO:typingAmount;

            //打码量修改
            boolean result ;
            if(null == amountPO){
                amountPO = new UserActivityTypingAmountPO();
                amountPO.setTypingAmount(typingAmount);
                amountPO.setSiteCode(userInfoVO.getSiteCode());
                amountPO.setUserAccount(userInfoVO.getUserAccount());
                amountPO.setCurrency(userInfoVO.getMainCurrency());
                amountPO.setUserId(userInfoVO.getUserId());
                amountPO.setStartTime(System.currentTimeMillis());
                result = activityTypingAmountService.save(amountPO);
            }else{
                if(amountPO.getTypingAmount().compareTo(BigDecimal.ZERO) <= 0){
                    amountPO.setStartTime(System.currentTimeMillis());
                }
                amountPO.setTypingAmount(typingAmount);
                result =  activityTypingAmountService.updateById(amountPO);
            }
            return result;
        }catch (Exception e){
            log.info("打码量事务执行失败：{}",e.getMessage());
            return false;
        }
    }


    public Page<UserTypingRecordVO> listPage(UserActivityTypingRecordRequestVO vo) {
        Page<UserActivityTypingAmountRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        //绑定条件
        LambdaQueryWrapper<UserActivityTypingAmountRecordPO> lqw = buildLqw(vo);

        Page<UserActivityTypingAmountRecordPO> userTypingAmountRecordPOPage = repository.selectPage(page, lqw);

        Page<UserTypingRecordVO> userTypingRecordVOPage = new Page<>();
        BeanUtils.copyProperties(userTypingAmountRecordPOPage, userTypingRecordVOPage);

        List<UserTypingRecordVO> userCoinRecordVOList = ConvertUtil.entityListToModelList(userTypingAmountRecordPOPage.getRecords(), UserTypingRecordVO.class);
        userTypingRecordVOPage.setRecords(userCoinRecordVOList);
        return userTypingRecordVOPage;
    }

    public LambdaQueryWrapper<UserActivityTypingAmountRecordPO> buildLqw(UserActivityTypingRecordRequestVO vo) {
        LambdaQueryWrapper<UserActivityTypingAmountRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserActivityTypingAmountRecordPO::getSiteCode,vo.getSiteCode());
        lqw.ge(null != vo.getRecordStartTime(), UserActivityTypingAmountRecordPO::getCreatedTime, vo.getRecordStartTime());
        lqw.lt(null != vo.getRecordEndTime(), UserActivityTypingAmountRecordPO::getCreatedTime, vo.getRecordEndTime());
        lqw.eq(StringUtils.isNotBlank(vo.getOrderNo()), UserActivityTypingAmountRecordPO::getOrderNo, vo.getOrderNo());
        lqw.eq(StringUtils.isNotBlank(vo.getCurrency()), UserActivityTypingAmountRecordPO::getCurrency, vo.getCurrency());
        lqw.eq(StringUtils.isNotBlank(vo.getUserAccount()), UserActivityTypingAmountRecordPO::getUserAccount, vo.getUserAccount());
        lqw.eq(StringUtils.isNotBlank(vo.getAccountType()), UserActivityTypingAmountRecordPO::getAccountType, vo.getAccountType());
        lqw.eq(StringUtils.isNotBlank(vo.getAdjustWay()), UserActivityTypingAmountRecordPO::getAdjustWay, vo.getAdjustWay());
        lqw.eq(StringUtils.isNotBlank(vo.getAdjustType()), UserActivityTypingAmountRecordPO::getAdjustType, vo.getAdjustType());
        lqw.orderByDesc(UserActivityTypingAmountRecordPO::getCreatedTime);
        return lqw;
    }


    public Long userTypingRecordPageCount(UserActivityTypingRecordRequestVO vo) {
        LambdaQueryWrapper<UserActivityTypingAmountRecordPO> lqw = buildLqw(vo);
        return repository.selectCount(lqw);
    }

}
