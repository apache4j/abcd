package com.cloud.baowang.wallet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentDepositSubordinatesApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesResVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.TradeRecordTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.wallet.api.vo.userwallet.UserManualUpDownDetailVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserPlatformTransferDetailVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserSuperTransferDetailVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordDetailRequestVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordDetailResponseVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserWithdrawDetailVO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalPO;
import com.cloud.baowang.wallet.po.UserManualUpDownRecordPO;
import com.cloud.baowang.wallet.po.UserPlatformTransferRecordPO;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalRepository;
import com.cloud.baowang.wallet.repositories.UserManualUpDownRecordRepository;
import com.cloud.baowang.wallet.repositories.UserPlatformTransferRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserTradeRecordService {

    private final UserDepositWithdrawalRepository userDepositWithdrawalRepository;

    private final UserPlatformTransferRecordRepository userPlatformTransferRecordRepository;

    private final UserRechargeService userRechargeService;

    private final UserManualUpDownRecordRepository userManualUpDownRecordRepository;

    private final AgentDepositSubordinatesApi agentDepositSubordinatesApi;


    public Page<UserTradeRecordResponseVO> tradeRecordList(UserTradeRecordRequestVO vo) {

        /*Long nowTime = System.currentTimeMillis();
        if(null == vo.getStartTime() || 0L == vo.getStartTime()){
            vo.setStartTime(TimeZoneUtils.getStartOfDayInTimeZone(nowTime, CurrReqUtils.getTimezone()));
        }

        if(null == vo.getEndTime() || 0L == vo.getEndTime()){
            vo.setEndTime(nowTime);
        }*/

        Page<UserTradeRecordResponseVO> page = new Page<>(vo.getPageNumber(),vo.getPageSize());
        Page<UserTradeRecordResponseVO> tradeRecordResponseVOPage =  userDepositWithdrawalRepository.tradeRecordRechargeList(page,vo);

       /* if(CommonConstant.business_one_str.equals(vo.getTradeType())){
            tradeRecordResponseVOPage  =  userDepositWithdrawalRepository.tradeRecordRechargeList(page,vo);
        }else  if(CommonConstant.business_two_str.equals(vo.getTradeType())){
            tradeRecordResponseVOPage  =  userDepositWithdrawalRepository.tradeRecordWithdrawList(page,vo);
        }else if(CommonConstant.business_three_str.equals(vo.getTradeType())){
            tradeRecordResponseVOPage  =  userPlatformTransferRecordRepository.tradeRecordPlatformList(page,vo);
        }*/


        return tradeRecordResponseVOPage;
    }

    public UserTradeRecordDetailResponseVO tradeRecordDetail(UserTradeRecordDetailRequestVO vo) {
        UserTradeRecordDetailResponseVO userTradeRecordDetailResponseVO = new UserTradeRecordDetailResponseVO();

        if(TradeRecordTypeEnum.CRYPTO_CURRENCY_WITHDRAW.getCode().equals(vo.getTradeWayType())
            || TradeRecordTypeEnum.BANK_CARD_WITHDRAW.getCode().equals(vo.getTradeWayType())
            || TradeRecordTypeEnum.ELECTRONIC_WALLET_WITHDRAW.getCode().equals(vo.getTradeWayType())
            || TradeRecordTypeEnum.MANUAL_WITHDRAW.getCode().equals(vo.getTradeWayType())){
            if(CommonConstant.business_two_str.equals(vo.getTradeType())){
                UserWithdrawDetailVO userWithdrawDetailVO =  getWithdrawDetail(vo.getOrderNo());
                userWithdrawDetailVO.setTradeWayType(vo.getTradeWayType());
                userTradeRecordDetailResponseVO.setWithdrawOrderDetailVO(userWithdrawDetailVO);
            }
        }else   if(TradeRecordTypeEnum.MANUAL_UP.getCode().equals(vo.getTradeWayType())
                || TradeRecordTypeEnum.MANUAL_DOWN.getCode().equals(vo.getTradeWayType())){
            UserManualUpDownDetailVO userManualUpDownDetailVO = getManualUpDetail(vo.getOrderNo());
            userManualUpDownDetailVO.setTradeWayType(vo.getTradeWayType());
            userTradeRecordDetailResponseVO.setManualUpDownDetailVO(userManualUpDownDetailVO);
        }else if(TradeRecordTypeEnum.SUPERIOR_TRANSFER.getCode().equals(vo.getTradeWayType())){
            UserSuperTransferDetailVO userSuperTransferDetailVO = getSuperTransferDetail(vo.getOrderNo());
            userSuperTransferDetailVO.setTradeWayType(vo.getTradeWayType());
            userTradeRecordDetailResponseVO.setSuperTransferDetailVO(userSuperTransferDetailVO);
        }else if(TradeRecordTypeEnum.PLATFORM_TRANSFER.getCode().equals(vo.getTradeWayType())){
            UserPlatformTransferDetailVO userPlatformTransferDetailVO = getPlatformTransferDetail(vo.getOrderNo());
            userPlatformTransferDetailVO.setTradeWayType(vo.getTradeWayType());
            userTradeRecordDetailResponseVO.setPlatformTransferDetailVO(userPlatformTransferDetailVO);
        }
        return userTradeRecordDetailResponseVO;
    }
    private UserPlatformTransferDetailVO getPlatformTransferDetail(String orderNo){

        LambdaQueryWrapper<UserPlatformTransferRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserPlatformTransferRecordPO::getOrderNo,orderNo);
        UserPlatformTransferRecordPO userManualUpDownRecordPO = userPlatformTransferRecordRepository.selectOne(lqw);
        UserPlatformTransferDetailVO userPlatformTransferDetailVO = new UserPlatformTransferDetailVO();
        userPlatformTransferDetailVO.setOrderNo(userManualUpDownRecordPO.getOrderNo());
        userPlatformTransferDetailVO.setTransferAmount(userManualUpDownRecordPO.getTargetAmount());
        userPlatformTransferDetailVO.setTransferRate(userManualUpDownRecordPO.getTransferRate());
        userPlatformTransferDetailVO.setUpdatedTime(userManualUpDownRecordPO.getOrderTime());
        userPlatformTransferDetailVO.setArriveAmount(userManualUpDownRecordPO.getTransferAmount());
        userPlatformTransferDetailVO.setCustomerStatus(CommonConstant.business_one_str);
        return userPlatformTransferDetailVO;
    }

    private UserSuperTransferDetailVO getSuperTransferDetail(String orderNo){
        AgentDepositOfSubordinatesResVO agentDepositOfSubordinatesResVO = agentDepositSubordinatesApi.getAgentDepositAmountByOderNo(orderNo);
        UserSuperTransferDetailVO userSuperTransferDetailVO = new UserSuperTransferDetailVO();
        userSuperTransferDetailVO.setOrderNo(agentDepositOfSubordinatesResVO.getOrderNo());
        userSuperTransferDetailVO.setSuperAgentAccount(agentDepositOfSubordinatesResVO.getAgentAccount());
        userSuperTransferDetailVO.setArriveAmount(agentDepositOfSubordinatesResVO.getAmount());
        userSuperTransferDetailVO.setUpdatedTime(agentDepositOfSubordinatesResVO.getDepositTime());
        userSuperTransferDetailVO.setCustomerStatus(CommonConstant.business_one_str);
        return userSuperTransferDetailVO;
    }
    private UserManualUpDownDetailVO getManualUpDetail(String orderNo){
        LambdaQueryWrapper<UserManualUpDownRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserManualUpDownRecordPO::getOrderNo,orderNo);
        UserManualUpDownRecordPO userManualUpDownRecordPO = userManualUpDownRecordRepository.selectOne(lqw);
        UserManualUpDownDetailVO userManualUpDownDetailVO = new UserManualUpDownDetailVO();
        userManualUpDownDetailVO.setOrderNo(userManualUpDownRecordPO.getOrderNo());
        userManualUpDownDetailVO.setCustomerStatus(CommonConstant.business_zero.equals(userManualUpDownRecordPO.getBalanceChangeStatus())?CommonConstant.business_two_str:CommonConstant.business_one_str);
        if(CommonConstant.business_one_str.equals(userManualUpDownDetailVO.getCustomerStatus())){
            userManualUpDownDetailVO.setUpdatedTime(userManualUpDownDetailVO.getUpdatedTime());
        }
        userManualUpDownDetailVO.setUpdatedTime(userManualUpDownRecordPO.getUpdatedTime());
        userManualUpDownDetailVO.setArriveAmount(userManualUpDownRecordPO.getAdjustAmount());
        return userManualUpDownDetailVO;
    }

    private UserWithdrawDetailVO getWithdrawDetail(String orderNo){
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserDepositWithdrawalPO::getOrderNo,orderNo);
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectOne(lqw);
        UserWithdrawDetailVO userWithdrawDetailVO = new  UserWithdrawDetailVO();
        userWithdrawDetailVO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
        userWithdrawDetailVO.setTradeWayType(userDepositWithdrawalPO.getDepositWithdrawTypeCode());
        userWithdrawDetailVO.setTradeWay(userDepositWithdrawalPO.getDepositWithdrawWay());
        userWithdrawDetailVO.setFeeAmount(userDepositWithdrawalPO.getFeeAmount());
        userWithdrawDetailVO.setApplyAmount(userDepositWithdrawalPO.getApplyAmount());
        userWithdrawDetailVO.setCratedTime(userDepositWithdrawalPO.getCreatedTime());
        if(CommonConstant.business_one_str.equals(userDepositWithdrawalPO.getCustomerStatus())){
            userWithdrawDetailVO.setArriveAmount(userDepositWithdrawalPO.getArriveAmount());
            userWithdrawDetailVO.setUpdatedTime(userDepositWithdrawalPO.getUpdatedTime());
        }
        userWithdrawDetailVO.setCustomerStatus(userDepositWithdrawalPO.getCustomerStatus());
        userWithdrawDetailVO.setExchangeRate(userDepositWithdrawalPO.getExchangeRate());
        if (null != userDepositWithdrawalPO) {
            if (WithdrawTypeEnum.BANK_CARD.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                userWithdrawDetailVO.setBankName(userDepositWithdrawalPO.getAccountType());
                userWithdrawDetailVO.setBankCode(userDepositWithdrawalPO.getAccountBranch());
                userWithdrawDetailVO.setBankCard(userDepositWithdrawalPO.getDepositWithdrawAddress());
                userWithdrawDetailVO.setUserName(userDepositWithdrawalPO.getDepositWithdrawName());
                userWithdrawDetailVO.setSurname(userDepositWithdrawalPO.getDepositWithdrawSurname());
                userWithdrawDetailVO.setUserEmail(userDepositWithdrawalPO.getEmail());
                userWithdrawDetailVO.setAreaCode(userDepositWithdrawalPO.getAreaCode());
                userWithdrawDetailVO.setUserPhone(userDepositWithdrawalPO.getTelephone());
                userWithdrawDetailVO.setProvinceName(userDepositWithdrawalPO.getProvince());
                userWithdrawDetailVO.setCityName(userDepositWithdrawalPO.getCity());
                userWithdrawDetailVO.setDetailAddress(userDepositWithdrawalPO.getAddress());
                userWithdrawDetailVO.setIfscCode(userDepositWithdrawalPO.getIfscCode());
            } else if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                userWithdrawDetailVO.setAreaCode(userDepositWithdrawalPO.getAreaCode());
                userWithdrawDetailVO.setUserPhone(userDepositWithdrawalPO.getTelephone());
                userWithdrawDetailVO.setUserAccount(userDepositWithdrawalPO.getDepositWithdrawAddress());
                userWithdrawDetailVO.setUserName(userDepositWithdrawalPO.getDepositWithdrawName());
                userWithdrawDetailVO.setSurname(userDepositWithdrawalPO.getDepositWithdrawSurname());
            } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                if (CommonConstant.business_one_str.equals(userDepositWithdrawalPO.getCustomerStatus())) {
                    userWithdrawDetailVO.setArriveAmount(userDepositWithdrawalPO.getTradeCurrencyAmount());
                }
                userWithdrawDetailVO.setNetworkType(userDepositWithdrawalPO.getAccountBranch());
                userWithdrawDetailVO.setAddressNo(userDepositWithdrawalPO.getDepositWithdrawAddress());
            }else if (WithdrawTypeEnum.MANUAL_WITHDRAW.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                userWithdrawDetailVO.setUserAccount(userDepositWithdrawalPO.getDepositWithdrawAddress());
                userWithdrawDetailVO.setUserName(userDepositWithdrawalPO.getDepositWithdrawName());
                userWithdrawDetailVO.setSurname(userDepositWithdrawalPO.getDepositWithdrawSurname());
            }
        }
        return  userWithdrawDetailVO;
    }
}
