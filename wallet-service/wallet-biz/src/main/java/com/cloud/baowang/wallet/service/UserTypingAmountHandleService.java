package com.cloud.baowang.wallet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountMqVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountEnum;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingAmountVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.WithdrawRunningWaterAddVO;
import com.cloud.baowang.wallet.po.UserActivityTypingAmountPO;
import com.cloud.baowang.wallet.repositories.UserActivityTypingAmountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserTypingAmountHandleService {

    private final UserTypingAmountService userTypingAmountService;

    private final UserActivityTypingAmountRepository userActivityTypingAmountRepository;


    @PostMapping(value = "cleanWithdrawRunningWater")
    public ResponseVO<Object> cleanWithdrawRunningWater(String siteCode,String userAccount){
        UserTypingAmountVO userTypingAmountVO = userTypingAmountService.getUserTypingAmountByAccount(siteCode,userAccount);
        if(null != userTypingAmountVO){
            UserTypingAmountRequestVO vo = new UserTypingAmountRequestVO();
            vo.setUserId(userTypingAmountVO.getUserId());
            vo.setUserAccount(userAccount);
            vo.setCurrencyCode(userTypingAmountVO.getCurrency());
            vo.setType(TypingAmountEnum.SUBTRACT.getCode());
            vo.setAdjustType(TypingAmountAdjustTypeEnum.MANUAL.getCode());
            vo.setOrderNo("W"+ SnowFlakeUtils.getSnowId());
            vo.setIsClear(true);
            vo.setTypingAmount(userTypingAmountVO.getTypingAmount());
            //同时清除会员活动流水
            LambdaQueryWrapper<UserActivityTypingAmountPO> lqw = new LambdaQueryWrapper<>();
            lqw.eq(UserActivityTypingAmountPO::getUserAccount,userAccount);
            lqw.eq(UserActivityTypingAmountPO::getSiteCode,siteCode);
            UserActivityTypingAmountPO userActivityTypingAmountPO = userActivityTypingAmountRepository.selectOne(lqw);
            if(null != userActivityTypingAmountPO){
                vo.setActivityTypingAmount(userActivityTypingAmountPO.getTypingAmount());
            }
            List<UserTypingAmountRequestVO> userTypingAmountRequestVOS = List.of(vo);
            UserTypingAmountMqVO userTypingAmountMqVO = UserTypingAmountMqVO.builder().userTypingAmountRequestVOList(userTypingAmountRequestVOS).build();
            KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC,userTypingAmountMqVO);
        }
        return ResponseVO.success();
    }

    @PostMapping(value = "addWithdrawRunningWater")
    public ResponseVO<Object> addWithdrawRunningWater(@RequestBody WithdrawRunningWaterAddVO requestVO){
        if(BigDecimal.ZERO.compareTo(requestVO.getAddTypingAmount()) >= 0){
            throw new BaowangDefaultException(ResultCode.ADD_TYPING_AMOUNT);
        }

        UserTypingAmountRequestVO vo = new UserTypingAmountRequestVO();
        vo.setSiteCode(requestVO.getSiteCode());
        vo.setUserId(requestVO.getUserId());
        vo.setUserAccount(requestVO.getUserAccount());
        vo.setType(TypingAmountEnum.ADD.getCode());
        vo.setTypingAmount(requestVO.getAddTypingAmount());
        vo.setAdjustType(TypingAmountAdjustTypeEnum.MANUAL.getCode());
        vo.setRemark(requestVO.getRemark());
        vo.setOrderNo("W"+ SnowFlakeUtils.getSnowId());
        List<UserTypingAmountRequestVO> userTypingAmountRequestVOS = List.of(vo);
        UserTypingAmountMqVO userTypingAmountMqVO = UserTypingAmountMqVO.builder().userTypingAmountRequestVOList(userTypingAmountRequestVOS).build();

        KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC,userTypingAmountMqVO);
        return ResponseVO.success();
    }

    public ResponseVO<Object> cleanActivityRunningWater(String siteCode, String userAccount) {
        LambdaQueryWrapper<UserActivityTypingAmountPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserActivityTypingAmountPO::getUserAccount,userAccount);
        lqw.eq(UserActivityTypingAmountPO::getSiteCode,siteCode);
        UserActivityTypingAmountPO userTypingAmountVO = userActivityTypingAmountRepository.selectOne(lqw);
        if(null != userTypingAmountVO){
            UserTypingAmountRequestVO vo = new UserTypingAmountRequestVO();
            vo.setUserId(userTypingAmountVO.getUserId());
            vo.setUserAccount(userAccount);
            vo.setCurrencyCode(userTypingAmountVO.getCurrency());
            vo.setType(TypingAmountEnum.SUBTRACT.getCode());
            vo.setAdjustType(TypingAmountAdjustTypeEnum.MANUAL.getCode());
            vo.setOrderNo("W"+ SnowFlakeUtils.getSnowId());
            vo.setTypingAmount(userTypingAmountVO.getTypingAmount());
            vo.setActivityTypingAmount(userTypingAmountVO.getTypingAmount());
            vo.setIsClear(true);
            vo.setOnlyActivity(true);
            List<UserTypingAmountRequestVO> userTypingAmountRequestVOS = List.of(vo);
            UserTypingAmountMqVO userTypingAmountMqVO = UserTypingAmountMqVO.builder().userTypingAmountRequestVOList(userTypingAmountRequestVOS).build();

            KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC,userTypingAmountMqVO);
        }
        return ResponseVO.success();
    }
}
