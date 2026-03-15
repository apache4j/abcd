package com.cloud.baowang.wallet.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.CallbackWithdrawParamVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualPageReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualRecordPageResVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualDetailReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualPayReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualRecordlDetailVO;
import com.cloud.baowang.wallet.po.UserWithdrawalManualRecordPO;
import com.cloud.baowang.wallet.repositories.UserWithdrawalManualRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@AllArgsConstructor
public class UserWithdrawManualRecordService extends ServiceImpl<UserWithdrawalManualRecordRepository, UserWithdrawalManualRecordPO> {

    private final UserWithdrawalManualRecordRepository userWithdrawalManualRecordRepository;

    private final UserDepositWithdrawCallbackService userDepositWithdrawCallbackService;
    public Page<UserWithdrawManualRecordPageResVO> withdrawManualPage(UserWithdrawManualPageReqVO vo) {

        Page<UserWithdrawalManualRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<UserWithdrawalManualRecordPO> lqw = buildLqw(vo);

        Page<UserWithdrawalManualRecordPO> userWithdrawReviewPageResVOPage = userWithdrawalManualRecordRepository.selectPage(page, lqw);

        Page<UserWithdrawManualRecordPageResVO> userWithdrawManualRecordPageResVOPage=new Page<UserWithdrawManualRecordPageResVO>(vo.getPageNumber(), vo.getPageSize());
        BeanUtils.copyProperties(userWithdrawReviewPageResVOPage, userWithdrawManualRecordPageResVOPage);
        userWithdrawManualRecordPageResVOPage.setTotal(userWithdrawReviewPageResVOPage.getTotal());
        userWithdrawManualRecordPageResVOPage.setPages(userWithdrawReviewPageResVOPage.getPages());
        return userWithdrawManualRecordPageResVOPage;
    }

    public UserWithdrawManualRecordlDetailVO withdrawManualDetail(UserWithdrawManualDetailReqVO vo) {
        UserWithdrawalManualRecordPO userWithdrawalManualRecordPO = this.getById(vo.getId());
        if (null == userWithdrawalManualRecordPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        UserWithdrawManualRecordlDetailVO result = ConvertUtil.entityToModel(userWithdrawalManualRecordPO, UserWithdrawManualRecordlDetailVO.class);

        return result;
    }

    @DistributedLock(name = RedisConstants.USER_WITHDRAW_MANUAL_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> withdrawManualPay(UserWithdrawManualPayReqVO vo) {
        UserWithdrawalManualRecordPO userWithdrawalManualRecordPO = this.getById(vo.getId());
        if(!DepositWithdrawalOrderCustomerStatusEnum.PENDING.getCode().equals(userWithdrawalManualRecordPO.getCustomerStatus())){
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }
        userWithdrawalManualRecordPO.setCustomerStatus(vo.getCustomerStatus());
        userWithdrawalManualRecordPO.setFileKey(vo.getFileKey());
        userWithdrawalManualRecordPO.setUpdatedTime(System.currentTimeMillis());
        this.updateById(userWithdrawalManualRecordPO);
        //回调出款
        CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
        paramVO.setAmount(userWithdrawalManualRecordPO.getApplyAmount());
        paramVO.setOrderNo(userWithdrawalManualRecordPO.getOrderNo());
        paramVO.setPayId("");
        paramVO.setRemark("人工提款");
        paramVO.setStatus(Integer.parseInt(vo.getCustomerStatus()));
        boolean result = userDepositWithdrawCallbackService.userWithdrawCallback(paramVO);
        if(!result){
            throw new BaowangDefaultException(ResultCode.WITHDRAW_FAIL);
        }
        return ResponseVO.success(true);
    }
    public LambdaQueryWrapper<UserWithdrawalManualRecordPO> buildLqw(UserWithdrawManualPageReqVO vo) {
        LambdaQueryWrapper<UserWithdrawalManualRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.in(UserWithdrawalManualRecordPO::getCustomerStatus, vo.getCustomerStatusList());
        lqw.eq(UserWithdrawalManualRecordPO::getSiteCode,vo.getSiteCode());
        lqw.eq(StringUtils.isNotBlank(vo.getOrderNo()),UserWithdrawalManualRecordPO::getOrderNo,vo.getOrderNo());
        lqw.eq(StringUtils.isNotBlank(vo.getUserAccount()),UserWithdrawalManualRecordPO::getUserAccount,vo.getUserAccount());
        lqw.eq(StringUtils.isNotBlank(vo.getCurrencyCode()),UserWithdrawalManualRecordPO::getCurrencyCode,vo.getCurrencyCode());
        lqw.eq(StringUtils.isNotBlank(vo.getWithdrawWayId()),UserWithdrawalManualRecordPO::getDepositWithdrawWayId,vo.getWithdrawWayId());
        lqw.ge(null != vo.getStartTime(),UserWithdrawalManualRecordPO::getUpdatedTime,vo.getStartTime());
        lqw.le(null != vo.getEndTime(),UserWithdrawalManualRecordPO::getUpdatedTime,vo.getEndTime());
        lqw.orderByDesc(UserWithdrawalManualRecordPO::getCreatedTime);
        return lqw;
    }

    public Long withdrawalManualRecordPageCount(UserWithdrawManualPageReqVO vo) {
        LambdaQueryWrapper<UserWithdrawalManualRecordPO> lqw = buildLqw(vo);
        return userWithdrawalManualRecordRepository.selectCount(lqw);
    }
}
