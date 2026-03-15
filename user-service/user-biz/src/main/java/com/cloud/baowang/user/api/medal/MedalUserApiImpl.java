package com.cloud.baowang.user.api.medal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.user.api.enums.MedalLockStatusEnum;
import com.cloud.baowang.user.api.enums.MedalOpenStatusEnum;
import com.cloud.baowang.user.service.UserCommonPlatformCoinService;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.PlatformWalletEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountEnum;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountMqVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.common.kafka.vo.UserWinLoseMqVO;
import com.cloud.baowang.user.api.api.medal.MedalUserApi;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRemarkRespVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardAcquireReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardConfigRespVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardRemarkRespVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardRespVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoCondReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoRespVO;
import com.cloud.baowang.user.api.vo.medal.UserCenterMedalMyRespVo;
import com.cloud.baowang.user.api.vo.medal.UserCenterMedalRespDetailVO;
import com.cloud.baowang.user.api.vo.medal.UserCenterMedalRespVO;
import com.cloud.baowang.user.po.MedalAcquireRecordPO;
import com.cloud.baowang.user.po.MedalRewardConfigPO;
import com.cloud.baowang.user.po.MedalRewardRecordPO;
import com.cloud.baowang.user.service.MedalAcquireRecordService;
import com.cloud.baowang.user.service.MedalRewardConfigService;
import com.cloud.baowang.user.service.MedalRewardRecordService;
import com.cloud.baowang.user.service.SiteMedalInfoService;
import com.cloud.baowang.user.service.UserInfoService;
import com.cloud.baowang.user.util.MinioFileService;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.api.UserPlatformCoinApi;
import com.cloud.baowang.wallet.api.api.UserTypingAmountApi;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyFromTransferVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.cloud.baowang.user.api.constant.UserConstant.TOP_NUM;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/8/1 15:53
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class MedalUserApiImpl implements MedalUserApi {

    private final SiteMedalInfoService siteMedalInfoService;

    private final MedalAcquireRecordService medalAcquireRecordService;

    private final MedalRewardConfigService medalRewardConfigService;

    private final MinioFileService minioFileService;

    private final UserInfoService userInfoService;


    private final MedalRewardRecordService medalRewardRecordService;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final UserTypingAmountApi userTypingAmountApi;

    private final UserCommonPlatformCoinService userCommonPlatformCoinService;


    /**
     * APP或H5客户端 个人中心 前N个勋章
     * @return
     */
    @Override
    public ResponseVO<UserCenterMedalRespVO> topNList(String currentUserNo, String siteCode) {
        ResponseVO<List<SiteMedalInfoRespVO>> listResponseVO= siteMedalInfoService.selectBySiteCode(siteCode);
        List<SiteMedalInfoRespVO> siteMedalInfoRespVOS = listResponseVO.getData();
        //只需要前N个
        List<UserCenterMedalRespDetailVO> resultList=listAll(currentUserNo,siteCode,siteMedalInfoRespVOS);
        long canLightNum=resultList.stream().filter(o->o.getLockStatus()==MedalLockStatusEnum.CAN_UNLOCK.getCode()).count();
        if(!CollectionUtils.isEmpty(resultList)&&resultList.size()>=TOP_NUM){
            resultList=resultList.subList(0,TOP_NUM);
        }
        //填充内容
        resultList=fillContent(resultList,siteMedalInfoRespVOS);
        UserCenterMedalRespVO userCenterMedalRespVO=new UserCenterMedalRespVO();
        //  可点亮数量
        userCenterMedalRespVO.setCanLightNum(canLightNum);
        userCenterMedalRespVO.setUserCenterMedalDetailRespVoList(resultList);
        return ResponseVO.success(userCenterMedalRespVO);
    }

    private List<UserCenterMedalRespDetailVO> fillContent(List<UserCenterMedalRespDetailVO> resultList, List<SiteMedalInfoRespVO> siteMedalInfoValidRespVOS) {
        List<UserCenterMedalRespDetailVO> userCenterMedalRespDetailVOS=Lists.newArrayList();
        for(UserCenterMedalRespDetailVO userCenterMedalRespDetailVO :resultList){
            //按照所有进行填充
            Optional<SiteMedalInfoRespVO> siteMedalInfoRespVODbOptional = siteMedalInfoValidRespVOS.stream().filter(o->o.getMedalCode().equals(userCenterMedalRespDetailVO.getMedalCode())).findFirst();
            if(siteMedalInfoRespVODbOptional.isPresent()){
                SiteMedalInfoRespVO siteMedalInfoRespVODb=siteMedalInfoRespVODbOptional.get();
                userCenterMedalRespDetailVO.setActivatedPic(siteMedalInfoRespVODb.getActivatedPic());
                userCenterMedalRespDetailVO.setInactivatedPic(siteMedalInfoRespVODb.getInactivatedPic());
                userCenterMedalRespDetailVO.setMedalName(siteMedalInfoRespVODb.getMedalName());
                userCenterMedalRespDetailVO.setMedalDesc(siteMedalInfoRespVODb.getMedalDesc());
                userCenterMedalRespDetailVO.setUnlockCondName(siteMedalInfoRespVODb.getUnlockCondName());
                userCenterMedalRespDetailVO.setCondNum1(siteMedalInfoRespVODb.getCondNum1());
                userCenterMedalRespDetailVO.setCondNum2(siteMedalInfoRespVODb.getCondNum2());
                userCenterMedalRespDetailVO.setLockStatusName(MedalLockStatusEnum.parseName(userCenterMedalRespDetailVO.getLockStatus()));
                userCenterMedalRespDetailVO.setLockStatusSortNum(MedalLockStatusEnum.parseSortNum(userCenterMedalRespDetailVO.getLockStatus()));
                userCenterMedalRespDetailVO.setRewardAmount(siteMedalInfoRespVODb.getRewardAmount());
                userCenterMedalRespDetailVO.setTypingMultiple(siteMedalInfoRespVODb.getTypingMultiple());
                userCenterMedalRespDetailVO.setMedalDescI18(siteMedalInfoRespVODb.getMedalDescI18());
                userCenterMedalRespDetailVO.setMedalNameI18(siteMedalInfoRespVODb.getMedalNameI18());
                userCenterMedalRespDetailVO.setActivatedPicUrl(minioFileService.getFileUrlByKey(userCenterMedalRespDetailVO.getActivatedPic()));
                userCenterMedalRespDetailVO.setInactivatedPicUrl(minioFileService.getFileUrlByKey(userCenterMedalRespDetailVO.getInactivatedPic()));
                userCenterMedalRespDetailVOS.add(userCenterMedalRespDetailVO);
            }
        }
        userCenterMedalRespDetailVOS=userCenterMedalRespDetailVOS.stream()
                        .sorted(Comparator.comparing(UserCenterMedalRespDetailVO::getLockStatusSortNum)
                        .thenComparing(UserCenterMedalRespDetailVO::getSortNum))
                         .toList();
        return userCenterMedalRespDetailVOS;
    }


    /**
     * 获取当前用户已或勋章,所有勋章列表
     * @param currentUserNo
     * @param siteCode
     * @param siteMedalInfoRespVOS
     * @return
     */
    private List<UserCenterMedalRespDetailVO> listAll(String currentUserNo, String siteCode, List<SiteMedalInfoRespVO> siteMedalInfoRespVOS){
        List<MedalAcquireRecordPO> medalAcquireRecordPOList=medalAcquireRecordService.listByUserNoAndSiteCode(currentUserNo,siteCode);
        List<UserCenterMedalRespDetailVO> resultList= Lists.newArrayList();
        Integer sortNum=1;
        if(!CollectionUtils.isEmpty(medalAcquireRecordPOList)){
            //可点亮的靠前
            for(MedalAcquireRecordPO medalAcquireRecordPO:medalAcquireRecordPOList){
                UserCenterMedalRespDetailVO userCenterMedalRespVO =new UserCenterMedalRespDetailVO();
                userCenterMedalRespVO.setSortNum(sortNum);
                userCenterMedalRespVO.setSiteCode(medalAcquireRecordPO.getSiteCode());
                userCenterMedalRespVO.setUserId(medalAcquireRecordPO.getUserId());
                userCenterMedalRespVO.setUserAccount(medalAcquireRecordPO.getUserAccount());
                userCenterMedalRespVO.setMedalCode(medalAcquireRecordPO.getMedalCode());
                userCenterMedalRespVO.setLockStatus(medalAcquireRecordPO.getLockStatus());
                userCenterMedalRespVO.setCompleteTimeStamp(medalAcquireRecordPO.getCompleteTime());
                userCenterMedalRespVO.setUnlockTimeStamp(medalAcquireRecordPO.getUnlockTime());
                String zoneId= CurrReqUtils.getTimezone();
                userCenterMedalRespVO.setCompleteTime(TimeZoneUtils.formatDateByTimeZone(medalAcquireRecordPO.getCompleteTime(),zoneId));
                userCenterMedalRespVO.setUnlockTime(TimeZoneUtils.formatDateByTimeZone(medalAcquireRecordPO.getUnlockTime(),zoneId));
                resultList.add(userCenterMedalRespVO);
                sortNum++;
            }
        }
        siteMedalInfoRespVOS= siteMedalInfoRespVOS.stream().sorted(Comparator.comparingInt(SiteMedalInfoRespVO::getSortOrder)).toList();
        for(SiteMedalInfoRespVO siteMedalInfoRespVO:siteMedalInfoRespVOS){
            //只展示启用的
            if(EnableStatusEnum.ENABLE.getCode().equals(siteMedalInfoRespVO.getStatus())){
                UserCenterMedalRespDetailVO userCenterMedalRespVO =new UserCenterMedalRespDetailVO();
                userCenterMedalRespVO.setSortNum(siteMedalInfoRespVO.getSortOrder()+sortNum);
                userCenterMedalRespVO.setSiteCode(siteMedalInfoRespVO.getSiteCode());
                userCenterMedalRespVO.setMedalCode(siteMedalInfoRespVO.getMedalCode());
                userCenterMedalRespVO.setLockStatus(MedalLockStatusEnum.NOT_UNLOCK.getCode());
                if(resultList.stream().noneMatch(o->o.getMedalCode().equals(userCenterMedalRespVO.getMedalCode()))){
                    resultList.add(userCenterMedalRespVO);
                }
            }
        }
        return resultList;
    }

    /**
     * APP或H5客户端 个人中心 勋章详情
     * @param currentUserNo 当前登录用户
     * @param siteCode 站点代码
     * @return 用户勋章详情
     */
    @Override
    public ResponseVO<UserCenterMedalMyRespVo> getUserMedalInfo(String currentUserNo, String siteCode) {
        ResponseVO<List<SiteMedalInfoRespVO>> listResponseVO= siteMedalInfoService.selectBySiteCode(siteCode);
        List<SiteMedalInfoRespVO> siteMedalInfoRespVOS = listResponseVO.getData();
        List<UserCenterMedalRespDetailVO> resultList=listAll(currentUserNo,siteCode,siteMedalInfoRespVOS);
        //填充内容
        resultList=fillContent(resultList,siteMedalInfoRespVOS);
        //已解锁列表
        List<UserCenterMedalRespDetailVO> hasUnlockList=resultList.stream().filter(o->o.getLockStatus().equals(MedalLockStatusEnum.HAS_UNLOCK.getCode())).toList();
        //未解锁列表
        List<UserCenterMedalRespDetailVO> notUnlockList=resultList.stream().filter(o->!o.getLockStatus().equals(MedalLockStatusEnum.HAS_UNLOCK.getCode())).toList();
        // 获取宝箱配置
        List<MedalRewardConfigRespVO> medalRewardConfigRespVOS=medalRewardConfigService.listAllVo(siteCode);

        UserCenterMedalMyRespVo userCenterMedalDetailRespVo=new UserCenterMedalMyRespVo();
        if(!CollectionUtils.isEmpty(notUnlockList)){
            notUnlockList=notUnlockList.stream().sorted(Comparator.comparingInt(UserCenterMedalRespDetailVO::getSortNum)).toList();
        }
        userCenterMedalDetailRespVo.setNotUnlockList(notUnlockList);
        userCenterMedalDetailRespVo.setHasUnlockList(hasUnlockList);

        // 宝箱奖励记录
        List<MedalRewardRecordPO> medalRewardRecordPOS=medalRewardRecordService.selectBySiteAndUser(siteCode,currentUserNo);
        List<MedalRewardRespVO> medalRewardRespVOS=Lists.newArrayList();
        for(MedalRewardConfigRespVO medalRewardConfigRespVO:medalRewardConfigRespVOS){
            MedalRewardRespVO medalRewardRespVO=new MedalRewardRespVO();
            medalRewardRespVO.setSiteCode(medalRewardConfigRespVO.getSiteCode());
            medalRewardRespVO.setRewardNo(medalRewardConfigRespVO.getRewardNo());
            medalRewardRespVO.setUnlockMedalNum(medalRewardConfigRespVO.getUnlockMedalNum());
            medalRewardRespVO.setRewardAmount(medalRewardConfigRespVO.getRewardAmount());
            medalRewardRespVO.setTypingMultiple(medalRewardConfigRespVO.getTypingMultiple());
            medalRewardRespVO.setOpenStatus(MedalOpenStatusEnum.NOT_UNLOCK.getCode());
            fillRewardContent(medalRewardRespVO,medalRewardRecordPOS);
            medalRewardRespVOS.add(medalRewardRespVO);
        }
        List<MedalRewardRespVO> medalRewardRespVOSSorted=medalRewardRespVOS.stream().sorted(Comparator.comparing(MedalRewardRespVO::getRewardNo)).toList();
        userCenterMedalDetailRespVo.setMedalRewardRespVOS(medalRewardRespVOSSorted);
        //奖励描述
        List<MedalRewardRemarkRespVO> remarkList=Lists.newArrayList();
        for(MedalRewardRecordPO medalRewardRecordPO:medalRewardRecordPOS){
            MedalRewardRemarkRespVO medalRewardRemarkRespVO=new MedalRewardRemarkRespVO();
            medalRewardRemarkRespVO.setUnlockMedalNum(medalRewardRecordPO.getCondNum());
            medalRewardRemarkRespVO.setRewardAmount(medalRewardRecordPO.getRewardAmount());
            remarkList.add(medalRewardRemarkRespVO);
        }
        userCenterMedalDetailRespVo.setRewardRemarkList(remarkList);


        return ResponseVO.success(userCenterMedalDetailRespVo);
    }

    /**
     * 奖励填充
     * @param medalRewardRespVO
     * @param medalRewardRecordPOS
     */
    private void fillRewardContent(MedalRewardRespVO medalRewardRespVO, List<MedalRewardRecordPO> medalRewardRecordPOS) {
        if(CollectionUtils.isEmpty(medalRewardRecordPOS)){
            return;
        }
        Optional<MedalRewardRecordPO> medalRewardRecordPOOptional=medalRewardRecordPOS.stream().filter(o-> Objects.equals(o.getRewardNo(), medalRewardRespVO.getRewardNo())).findFirst();
        if(medalRewardRecordPOOptional.isPresent()){
            MedalRewardRecordPO medalRewardRecordPO=medalRewardRecordPOOptional.get();
            medalRewardRespVO.setUserAccount(medalRewardRecordPO.getUserAccount());
            medalRewardRespVO.setOpenStatus(medalRewardRecordPO.getOpenStatus());
            medalRewardRespVO.setRewardNo(medalRewardRecordPO.getRewardNo());
            medalRewardRespVO.setCondNum(medalRewardRecordPO.getCondNum());
        }
    }

    /**
     * 点亮勋章
     * @param medalAcquireReqVO 点亮参数
     * @return
     */
    @Override
    public ResponseVO<MedalRemarkRespVO> lightUpMedal(MedalAcquireReqVO medalAcquireReqVO) {

        MedalAcquireRecordPO medalAcquireRecordPO=medalAcquireRecordService.selectByUniq(medalAcquireReqVO.getSiteCode(),medalAcquireReqVO.getUserAccount(),medalAcquireReqVO.getMedalCode());
        if(medalAcquireRecordPO==null){
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        if(MedalLockStatusEnum.CAN_UNLOCK.getCode()!=medalAcquireRecordPO.getLockStatus()){
            log.info("当前勋章:{}不可以被点亮:{}",medalAcquireReqVO.getMedalCode(),medalAcquireRecordPO.getLockStatus());
            return ResponseVO.fail(ResultCode.MEDAL_HAS_LIGHT);
        }
        //修改状态
        medalAcquireRecordService.lightUpMedal(medalAcquireRecordPO.getId(),MedalLockStatusEnum.HAS_UNLOCK.getCode());
        UserInfoVO userInfoVO=userInfoService.getInfoByUserAccountAndSite(medalAcquireReqVO.getUserAccount(),medalAcquireReqVO.getSiteCode());
        SiteMedalInfoCondReqVO siteMedalInfoCondReqVO=new SiteMedalInfoCondReqVO();
        siteMedalInfoCondReqVO.setMedalCode(medalAcquireReqVO.getMedalCode());
        siteMedalInfoCondReqVO.setSiteCode(medalAcquireReqVO.getSiteCode());
        ResponseVO<SiteMedalInfoRespVO> siteMedalInfoRespVOResponseVO=siteMedalInfoService.selectByCond(siteMedalInfoCondReqVO);
        SiteMedalInfoRespVO siteMedalInfoRespVO=siteMedalInfoRespVOResponseVO.getData();
        // 增加当前勋章奖励金额
        UserPlatformCoinAddVO userPlatformCoinAddVO=new UserPlatformCoinAddVO();
        userPlatformCoinAddVO.setOrderNo(medalAcquireRecordPO.getOrderNo());
        userPlatformCoinAddVO.setUserId(userInfoVO.getUserId());
        userPlatformCoinAddVO.setCoinType(PlatformWalletEnum.CoinTypeEnum.MEDAL_REWARD.getCode());
        userPlatformCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userPlatformCoinAddVO.setBusinessCoinType(PlatformWalletEnum.BusinessCoinTypeEnum.MEDAL_REWARD.getCode());
//        userPlatformCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.ADD_COIN.getCode());
        userPlatformCoinAddVO.setCoinValue(siteMedalInfoRespVO.getRewardAmount());
        userPlatformCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        MedalRemarkRespVO medalRemarkRespVO=new MedalRemarkRespVO();
        medalRemarkRespVO.setMedalName(siteMedalInfoRespVO.getMedalName());
        medalRemarkRespVO.setRewardAmount(siteMedalInfoRespVO.getRewardAmount());
        String rewardRemarkText =medalRemarkRespVO.getMedalName().concat(":").concat(AmountUtils.format(medalRemarkRespVO.getRewardAmount()));
        userPlatformCoinAddVO.setRemark(rewardRemarkText);
        userCommonPlatformCoinService.userCommonPlatformCoin(userPlatformCoinAddVO);

        //记录当前勋章打码量
        UserTypingAmountMqVO userTypingAmountMqVO=new UserTypingAmountMqVO();
        UserTypingAmountRequestVO userTypingAmountRequestVO=new UserTypingAmountRequestVO();
        List<UserTypingAmountRequestVO> userTypingAmountRequestVOList=Lists.newArrayList();
        userTypingAmountRequestVO.setRemark(rewardRemarkText);
        userTypingAmountRequestVO.setUserId(userInfoVO.getUserId());

        userTypingAmountRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        userTypingAmountRequestVO.setSiteCode(medalAcquireRecordPO.getSiteCode());
        userTypingAmountRequestVO.setUserAccount(medalAcquireRecordPO.getUserAccount());
        // 奖励金额(WTC)*打码倍数*汇率(平台币转法币汇率)
        PlatCurrencyFromTransferVO platCurrencyTransferVO=new PlatCurrencyFromTransferVO();
        platCurrencyTransferVO.setSiteCode(medalAcquireRecordPO.getSiteCode());
        platCurrencyTransferVO.setSourceAmt(siteMedalInfoRespVO.getRewardAmount());
        platCurrencyTransferVO.setTargetCurrencyCode(userInfoVO.getMainCurrency());
        BigDecimal mainCurrencyAmount=siteCurrencyInfoApi.transferPlatToMainCurrency(platCurrencyTransferVO).getData();
        BigDecimal typingAmount=AmountUtils.multiply(mainCurrencyAmount,siteMedalInfoRespVO.getTypingMultiple());
      //  BigDecimal typingAmount=siteMedalInfoRespVO.getRewardAmount().multiply(siteMedalInfoRespVO.getTypingMultiple());
        userTypingAmountRequestVO.setTypingAmount(typingAmount);
        userTypingAmountRequestVO.setType(TypingAmountEnum.ADD.getCode());
        userTypingAmountRequestVO.setAdjustType(TypingAmountAdjustTypeEnum.MEDAL.getCode());
        userTypingAmountRequestVO.setOrderNo(medalAcquireRecordPO.getOrderNo());
        userTypingAmountRequestVOList.add(userTypingAmountRequestVO);
        userTypingAmountMqVO.setUserTypingAmountRequestVOList(userTypingAmountRequestVOList);
        KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC,userTypingAmountMqVO);

        List<MedalRewardConfigPO> medalRewardConfigPOS=medalRewardConfigService.listAll(medalAcquireReqVO.getSiteCode());
        LambdaQueryWrapper<MedalAcquireRecordPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MedalAcquireRecordPO::getSiteCode,medalAcquireReqVO.getSiteCode());
        lambdaQueryWrapper.eq(MedalAcquireRecordPO::getUserAccount,medalAcquireReqVO.getUserAccount());
        lambdaQueryWrapper.eq(MedalAcquireRecordPO::getLockStatus,MedalLockStatusEnum.HAS_UNLOCK.getCode());
        Long userCountNum = medalAcquireRecordService.count(lambdaQueryWrapper);
        for(MedalRewardConfigPO medalRewardConfigPO:medalRewardConfigPOS){
            if(medalRewardConfigPO.getUnlockMedalNum()<=userCountNum.intValue()){
                //解锁宝箱
                MedalRewardRecordPO medalRewardRecordPO=new MedalRewardRecordPO();
                medalRewardRecordPO.setUserAccount(medalAcquireReqVO.getUserAccount());
                medalRewardRecordPO.setSuperAgentId(userInfoVO.getSuperAgentId());
                medalRewardRecordPO.setSuperAgentAccount(userInfoVO.getSuperAgentAccount());
                medalRewardRecordPO.setSiteCode(medalAcquireReqVO.getSiteCode());
                medalRewardRecordPO.setRewardNo(medalRewardConfigPO.getRewardNo());
                medalRewardRecordPO.setRewardAmount(medalRewardConfigPO.getRewardAmount());
                medalRewardRecordPO.setTypingMultiple(medalRewardConfigPO.getTypingMultiple());
                medalRewardRecordPO.setCondNum(medalRewardConfigPO.getUnlockMedalNum());
                medalRewardRecordPO.setCompleteTime(System.currentTimeMillis());
                medalRewardRecordService.insert(medalRewardRecordPO);
            }
        }
        //增加会员盈亏
        UserWinLoseMqVO userWinLoseMqVO=new UserWinLoseMqVO();
        userWinLoseMqVO.setUserId(userInfoVO.getUserId());
        userWinLoseMqVO.setDayHourMillis(TimeZoneUtils.convertToUtcStartOfHour(System.currentTimeMillis()));
        userWinLoseMqVO.setAgentId(userInfoVO.getSuperAgentId());
        // 勋章当作活动
        userWinLoseMqVO.setBizCode(5);
        userWinLoseMqVO.setOrderId(medalAcquireRecordPO.getOrderNo());
        userWinLoseMqVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        userWinLoseMqVO.setPlatformFlag(true);
        userWinLoseMqVO.setActivityAmount(siteMedalInfoRespVO.getRewardAmount());
        log.info("用户点亮勋章,发送到会员盈亏统计:{}成功",userWinLoseMqVO);
        KafkaUtil.send(TopicsConstants.USER_WIN_LOSE_CHANNEL,userWinLoseMqVO);
        return ResponseVO.success(medalRemarkRespVO);
    }


    /**
     * 打开宝箱 获取奖励
     * @param medalRewardAcquireReqVO 打开宝箱
     * @return
     */
    @Override
    public ResponseVO<MedalRewardRemarkRespVO> openMedalReward(MedalRewardAcquireReqVO medalRewardAcquireReqVO) {

        MedalRewardRecordPO medalRewardRecordPO=medalRewardRecordService.selectByUniq(medalRewardAcquireReqVO.getSiteCode(),medalRewardAcquireReqVO.getUserAccount(),medalRewardAcquireReqVO.getRewardNo());
        if(medalRewardRecordPO==null || MedalOpenStatusEnum.CAN_UNLOCK.getCode()!=medalRewardRecordPO.getOpenStatus()){
            log.info("当前宝箱:{}不能被打开",medalRewardAcquireReqVO.getRewardNo());
            return ResponseVO.fail(ResultCode.MEDAL_REWARD_HAS_OPEN);
        }
        //打开宝箱
        medalRewardRecordService.openReward(medalRewardRecordPO.getId(),medalRewardAcquireReqVO.getUserAccount());
        // 增加宝箱奖励金额
        UserPlatformCoinAddVO userPlatformCoinAddVO=new UserPlatformCoinAddVO();
        userPlatformCoinAddVO.setOrderNo(medalRewardRecordPO.getOrderNo());
        userPlatformCoinAddVO.setUserId(medalRewardAcquireReqVO.getUserId());
        userPlatformCoinAddVO.setCoinType(PlatformWalletEnum.CoinTypeEnum.MEDAL_REWARD.getCode());
        userPlatformCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userPlatformCoinAddVO.setBusinessCoinType(PlatformWalletEnum.BusinessCoinTypeEnum.MEDAL_REWARD.getCode());
//        userPlatformCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.ADD_COIN.getCode());
        userPlatformCoinAddVO.setCoinValue(medalRewardRecordPO.getRewardAmount());
        UserInfoVO platformCoinAddUserInfo = userInfoService.getByUserId(medalRewardAcquireReqVO.getUserId());
        userPlatformCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(platformCoinAddUserInfo, WalletUserInfoVO.class));
        UserInfoVO userInfoVO=userInfoService.getInfoByUserAccountAndSite(medalRewardAcquireReqVO.getUserAccount(),medalRewardAcquireReqVO.getSiteCode());
        userPlatformCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        MedalRewardRemarkRespVO medalRewardRemarkExtractRespVO=new MedalRewardRemarkRespVO();
        medalRewardRemarkExtractRespVO.setUnlockMedalNum(medalRewardRecordPO.getCondNum());
        medalRewardRemarkExtractRespVO.setRewardAmount(medalRewardRecordPO.getRewardAmount());
        //String rewardRemarkExtract=JSONObject.toJSONString(medalRewardRemarkExtractRespVO);
        String rewardRemarkExtract="宝箱奖励".concat(":").concat(medalRewardRemarkExtractRespVO.getUnlockMedalNum().toString()).concat(":").concat(AmountUtils.format(medalRewardRemarkExtractRespVO.getRewardAmount()));
        userPlatformCoinAddVO.setRemark(rewardRemarkExtract);
        userCommonPlatformCoinService.userCommonPlatformCoin(userPlatformCoinAddVO);

        // 记录宝箱奖励打码量
        UserTypingAmountMqVO userTypingAmountMqVOReward=new UserTypingAmountMqVO();
        UserTypingAmountRequestVO userTypingAmountRequestVOReward=new UserTypingAmountRequestVO();
        List<UserTypingAmountRequestVO> userTypingAmountRequestVOListReward=Lists.newArrayList();
        userTypingAmountRequestVOReward.setRemark(rewardRemarkExtract);
        userTypingAmountRequestVOReward.setUserId(userInfoVO.getUserId());
        userTypingAmountRequestVOReward.setType(TypingAmountEnum.ADD.getCode());
        userTypingAmountRequestVOReward.setAdjustType(TypingAmountAdjustTypeEnum.MEDAL.getCode());
        userTypingAmountRequestVOReward.setUserAccount(medalRewardRecordPO.getUserAccount());
       // BigDecimal typingAmountReward=medalRewardRecordPO.getRewardAmount().multiply(medalRewardRecordPO.getTypingMultiple());
        // 奖励金额(WTC)*打码倍数*汇率(平台币转法币汇率)
        PlatCurrencyFromTransferVO platCurrencyTransferVO=new PlatCurrencyFromTransferVO();
        platCurrencyTransferVO.setSiteCode(medalRewardRecordPO.getSiteCode());
        platCurrencyTransferVO.setSourceAmt(medalRewardRecordPO.getRewardAmount());
        platCurrencyTransferVO.setTargetCurrencyCode(userInfoVO.getMainCurrency());

        BigDecimal mainCurrencyAmount=siteCurrencyInfoApi.transferPlatToMainCurrency(platCurrencyTransferVO).getData();
        BigDecimal typingAmountReward=AmountUtils.multiply(mainCurrencyAmount,medalRewardRecordPO.getTypingMultiple());
        userTypingAmountRequestVOReward.setTypingAmount(typingAmountReward);
        userTypingAmountRequestVOReward.setOrderNo(medalRewardRecordPO.getOrderNo());
        userTypingAmountRequestVOListReward.add(userTypingAmountRequestVOReward);
        userTypingAmountMqVOReward.setUserTypingAmountRequestVOList(userTypingAmountRequestVOListReward);
        KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC,userTypingAmountMqVOReward);


        //增加会员盈亏
        UserWinLoseMqVO userWinLoseMqVO=new UserWinLoseMqVO();
        userWinLoseMqVO.setUserId(userInfoVO.getUserId());
        userWinLoseMqVO.setDayHourMillis(TimeZoneUtils.convertToUtcStartOfHour(System.currentTimeMillis()));
        userWinLoseMqVO.setAgentId(userInfoVO.getSuperAgentId());
        userWinLoseMqVO.setBizCode(5);
        userWinLoseMqVO.setOrderId(medalRewardRecordPO.getOrderNo());
        userWinLoseMqVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        userWinLoseMqVO.setPlatformFlag(true);
        userWinLoseMqVO.setActivityAmount(medalRewardRecordPO.getRewardAmount());
        log.info("用户打开宝箱,发送到会员盈亏统计:{}成功",userWinLoseMqVO);
        KafkaUtil.send(TopicsConstants.USER_WIN_LOSE_CHANNEL,userWinLoseMqVO);

        MedalRewardRemarkRespVO medalRewardRemarkRespVO=new MedalRewardRemarkRespVO();
        medalRewardRemarkRespVO.setUnlockMedalNum(medalRewardRecordPO.getCondNum());
        medalRewardRemarkRespVO.setRewardAmount(medalRewardRecordPO.getRewardAmount());
        return ResponseVO.success(medalRewardRemarkRespVO);

    }

}

