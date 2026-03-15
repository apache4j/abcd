package com.cloud.baowang.wallet.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.po.UserTypingAmountPO;
import com.cloud.baowang.wallet.po.UserTypingAmountRecordPO;
import com.cloud.baowang.wallet.repositories.UserTypingAmountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserTypingAmountAddService {

    private final UserTypingAmountRepository userTypingAmountRepository;


    private final UserTypingAmountRecordService userTypingAmountRecordService;


    private final UserInfoApi userInfoApi;



    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Boolean userTypingAmountAdd(UserTypingAmountRequestVO userTypingAmountRequestVO,UserInfoVO userInfoVO){
        try {
            LambdaQueryWrapper<UserTypingAmountRecordPO> typingAmountRecordLqw = new LambdaQueryWrapper<>();
            typingAmountRecordLqw.eq(UserTypingAmountRecordPO::getOrderNo,userTypingAmountRequestVO.getOrderNo());
            typingAmountRecordLqw.eq(UserTypingAmountRecordPO::getAdjustWay,userTypingAmountRequestVO.getType());
            typingAmountRecordLqw.eq(UserTypingAmountRecordPO::getMsgId,userTypingAmountRequestVO.getMsgId());
            List<UserTypingAmountRecordPO> recordList = userTypingAmountRecordService.list(typingAmountRecordLqw);
            if(!recordList.isEmpty()){
                log.info("订单编号为{}的订单已添加打码量",userTypingAmountRequestVO.getOrderNo());
                return false;
            }
            LambdaQueryWrapper<UserTypingAmountPO> lqw = Wrappers.lambdaQuery();
            lqw.eq(UserTypingAmountPO::getUserId,userTypingAmountRequestVO.getUserId());
            UserTypingAmountPO userTypingAmountPO = userTypingAmountRepository.selectOne(lqw);

            //组装打码量记录
            List<UserTypingAmountRecordPO> recordPOS = new ArrayList<>();

            List<UserTypingAmountRequestVO> typingList = userTypingAmountRequestVO.getTypingList();


            Integer vipRankCoe = userInfoVO.getVipRank();
            String vipRankCodeName = "VIP"+vipRankCoe;
            BigDecimal startValue = (null== userTypingAmountPO || null == userTypingAmountPO.getTypingAmount())?BigDecimal.ZERO:userTypingAmountPO.getTypingAmount();
            for (UserTypingAmountRequestVO vo:typingList) {
                BigDecimal amount = vo.getTypingAmount();
                if(BigDecimal.ZERO.compareTo(amount) == 0){
                  continue;
                }
                UserTypingAmountRecordPO po = new UserTypingAmountRecordPO();
                po.setUserAccount(userInfoVO.getUserAccount());
                po.setSiteCode(userInfoVO.getSiteCode());
                po.setUserId(userInfoVO.getUserId());
                po.setAccountType(userInfoVO.getAccountType());
                po.setVipRankCode(vipRankCoe);
                po.setVipRankCodeName(vipRankCodeName);
                po.setOrderNo(vo.getOrderNo());
                po.setAdjustWay(vo.getType());
                po.setAdjustType(vo.getAdjustType());
                po.setCoinFrom(startValue);
                po.setCurrency(userInfoVO.getMainCurrency());
                po.setCoinValue(amount);
                po.setCreatedTime(System.currentTimeMillis());
                po.setRemark(vo.getRemark());
                po.setMsgId(userTypingAmountRequestVO.getMsgId());
                BigDecimal coinTo = startValue.add(amount);
                coinTo = coinTo.compareTo(BigDecimal.ZERO) < 0 ?BigDecimal.ZERO:coinTo;
                po.setCoinTo(coinTo);
                if(startValue.compareTo(BigDecimal.ZERO) == 0 && coinTo.compareTo(BigDecimal.ZERO) == 0){
                    break;
                }
                startValue = coinTo;
                recordPOS.add(po);
            }
            userTypingAmountRecordService.saveBatch(recordPOS);
            BigDecimal typingAmount = typingList.stream().map(UserTypingAmountRequestVO::getTypingAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            if(null != userTypingAmountPO){
                typingAmount = typingAmount.add(userTypingAmountPO.getTypingAmount()==null? typingAmount:userTypingAmountPO.getTypingAmount());
            }
            typingAmount = typingAmount.compareTo(BigDecimal.ZERO) < 0?BigDecimal.ZERO:typingAmount;

            //打码量修改
            int num = 0;
            if(null == userTypingAmountPO){
                userTypingAmountPO = new UserTypingAmountPO();
                userTypingAmountPO.setTypingAmount(typingAmount);
                userTypingAmountPO.setSiteCode(userInfoVO.getSiteCode());
                userTypingAmountPO.setUserAccount(userInfoVO.getUserAccount());
                userTypingAmountPO.setCurrency(userInfoVO.getMainCurrency());
                userTypingAmountPO.setUserId(userInfoVO.getUserId());
                userTypingAmountPO.setStartTime(System.currentTimeMillis());
                num = userTypingAmountRepository.insert(userTypingAmountPO);
            }else{
                if(userTypingAmountPO.getTypingAmount().compareTo(BigDecimal.ZERO) <= 0){
                    userTypingAmountPO.setStartTime(System.currentTimeMillis());
                }
                userTypingAmountPO.setTypingAmount(typingAmount);
                num =  userTypingAmountRepository.updateById(userTypingAmountPO);
            }

            return num > 0;

        }catch (Exception e){
            log.info("打码量事务执行失败：{}",e.getMessage());
            return false;
        }
    }
}
