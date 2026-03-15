//package com.cloud.baowang.play.wallet.service.impl;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.util.ObjectUtil;
//import com.alibaba.fastjson2.JSONObject;
//import com.cloud.baowang.common.core.enums.StatusEnum;
//import com.cloud.baowang.common.core.utils.ConvertUtil;
//import com.cloud.baowang.play.api.enums.venue.VenueEnum;
//import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
//import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
//import com.cloud.baowang.common.core.utils.CurrReqUtils;
//import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.user.api.vo.UserInfoVO;
//import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
//import com.cloud.baowang.common.redis.config.RedisUtil;
//import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
//import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
//import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.wallet.enums.CmdRespErrEnums;
//import com.cloud.baowang.play.wallet.service.CmdService;
//import com.cloud.baowang.play.wallet.service.base.BaseService;
//import com.cloud.baowang.play.wallet.vo.req.cmd.CmdBetReq;
//import com.cloud.baowang.play.wallet.vo.req.cmd.CmdReq;
//import com.cloud.baowang.play.wallet.vo.req.cmd.CmdUpdateBaseReq;
//import com.cloud.baowang.play.wallet.vo.req.cmd.CmdUpdateDataReq;
//import com.cloud.baowang.play.wallet.vo.res.cmd.CmdBaseRsp;
//import com.cloud.baowang.play.wallet.vo.res.cmd.CmdUserRsp;
//import com.cloud.baowang.play.wallet.vo.res.cmd.constant.CmdConstant;
//import com.cloud.baowang.play.wallet.vo.res.cmd.utils.AESUtil;
//import com.cloud.baowang.play.wallet.vo.res.cmd.utils.CmdCryptoConfig;
//import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
//import com.google.common.collect.Lists;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.dom4j.Document;
//import org.dom4j.DocumentHelper;
//import org.dom4j.Element;
//import org.redisson.api.RLock;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StopWatch;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//
//@Slf4j
//@Service
//@AllArgsConstructor
//public class CmdServiceImpl extends BaseService implements CmdService {
//    private CmdCryptoConfig cmdCryptoConfig;
//    private final PlayVenueInfoApi playVenueInfoApi;
//    private final UserCoinRecordApi userCoinRecordApi;
//
//    @Override
//    public String doAction(CmdReq cmdReq) {
//        String key=cmdCryptoConfig.getKey();;
//        String method=cmdReq.getMethod();
//        String returnData=null;
//        String data=AESUtil.decrypt(cmdReq.getBalancePackage(),key);
//        JSONObject obj= JSONObject.parseObject(data);
//        log.info(" {} Cmd-doAction :{}",method,obj.toString());
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        returnData = switch (method) {
//            case "GetBalance" -> getBalance(cmdReq,obj);
//            case "DeductBalance" -> bet(cmdReq,obj);
//            case "UpdateBalance" -> batchProcess(cmdReq,obj);
//            default -> CmdBaseRsp.err(CmdRespErrEnums.METHON_NOT_FOUND,cmdReq);
//        };
//        stopWatch.stop();
//        long totalTimeMillis = stopWatch.getTotalTimeMillis();
//        if (totalTimeMillis>=3000){
//            log.info("Cmd-doAction 请求: {} 返回: {} 耗时: {}ms", obj.toString(),returnData, totalTimeMillis);
//        }
//        String respData=AESUtil.encrypt(returnData,key);
//        log.info("Cmd-doAction 加密前: {} 加密后: {}", returnData, respData);
//        return respData;
//    }
//
//    @Override
//    public String token(String token) {
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setVenueCode(VenueEnum.CMD.getVenueCode());
//        //只有authorize 才有token，所以才对token获取
//        if (StringUtils.isNotEmpty(token)){
//            casinoMemberReqVO.setCasinoPassword(token);
//        }
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        Document document = DocumentHelper.createDocument();
//        Element root = document.addElement("authenticate");
//        if (!respVO.isOk() || respVO.getData() == null) {
//            root.addElement("member_id").addText("");
//            root.addElement("status_code").addText(CmdRespErrEnums.MEMBER_NOT_EXIST.getCode()+"");
//            root.addElement("message").addText(CmdRespErrEnums.MEMBER_NOT_EXIST.getDescription());
//        }else{
//            root.addElement("member_id").addText(respVO.getData().getVenueUserAccount());
//            root.addElement("status_code").addText("0");
//            root.addElement("message").addText(CmdRespErrEnums.SUCCESS.getDescription());
//        }
//        return document.asXML();
//    }
//
//    private String getBalance(CmdReq cmdReq,JSONObject request){
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setVenueUserAccount(request.getString("SourceName"));
//        casinoMemberReqVO.setVenueCode(VenueEnum.CMD.getVenueCode());
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return CmdUserRsp.err(CmdRespErrEnums.MEMBER_NOT_EXIST,cmdReq);
//        }
//        CasinoMemberRespVO casinoMember = respVO.getData();
//        String venueCode = casinoMember.getVenueCode();
//        List<String> venueCodes = Lists.newArrayList(VenueEnum.CMD.getVenueCode());
//        if (!venueCodes.contains(venueCode)){
//            return CmdUserRsp.err(CmdRespErrEnums.GAME_MAINTAINED,cmdReq);
//        }
//        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
//            return CmdUserRsp.err(CmdRespErrEnums.GAME_MAINTAINED,cmdReq);
//        }
//        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userAccount(casinoMember.getUserAccount()).siteCode(casinoMember.getSiteCode()).build());
//        BigDecimal balance = BigDecimal.ZERO;
//        if (!Objects.isNull(userCenterCoin)) {
//            balance = userCenterCoin.getTotalAmount();
//        }
//        return CmdUserRsp.success(cmdReq,balance);
//    }
//
//    private String bet(CmdReq cmdReq,JSONObject request){
//        log.info("CMD bet : "+request.toString());
//        CmdBetReq req = request.toJavaObject(CmdBetReq.class);
//        req.setTransactionAmount(isNegativeSignum(req.getTransactionAmount())?req.getTransactionAmount().abs():req.getTransactionAmount());
//        CasinoMemberRespVO casinoMember = userCheck(req.getSourceName());
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(casinoMember.getUserId());
//        String isValid = checkRequestValid(userInfoVO,cmdReq,true);
//        if (isValid != null) {
//            return isValid;
//        }
//        String venueCode = casinoMember.getVenueCode();
//        //用户中心钱包余额
//        BigDecimal centerAmount=getUserCoin(userInfoVO.getUserId(),userInfoVO.getSiteCode()).getCenterAmount();
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(venueCode, req.getReferenceNo()));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("CMD errorBet error get locker error, req:{}", req);
//                return CmdUserRsp.err(CmdRespErrEnums.ACCOUNT_LOCKED,cmdReq,centerAmount);
//            }
//            // 检查余额
//            if(!compareAmount(userInfoVO.getUserId(),userInfoVO.getSiteCode(),req.getTransactionAmount())){
//                log.info("CMD 用户余额不足，userId:{},siteCode:{},money:{},",userInfoVO.getUserId(),userInfoVO.getSiteCode(),req.getTransactionAmount());
//                return CmdUserRsp.err(CmdRespErrEnums.INSUFFICIENT_BALANCE,cmdReq,centerAmount);
//            }
//            //投注金额为0直接跳过
//            if(BigDecimal.ZERO.compareTo(req.getTransactionAmount()) == 0){
//                CmdUserRsp.success(cmdReq,centerAmount);
//            }
//            //修改余额 记录账变
//            CoinRecordResultVO coinRecordResultVO = this.updateBalanceBet(userInfoVO, req.getReferenceNo(),
//                    req.getTransactionAmount(),cmdReq.getPackageId());
//            return switch (coinRecordResultVO.getResultStatus()) {
//                case SUCCESS -> {
//                    yield CmdUserRsp.success(cmdReq,coinRecordResultVO.getCoinAfterBalance());
//                } case REPEAT_TRANSACTIONS ->{
//                    yield CmdUserRsp.err(CmdRespErrEnums.DUPLICATE_ID_NO, cmdReq,centerAmount);
//                }   default -> {
//                    yield CmdUserRsp.err(CmdRespErrEnums.INSUFFICIENT_BALANCE, cmdReq,centerAmount);
//                }
//            };
//        }catch (Exception e){
//            log.error("CMD failed bet error {}", e.getMessage());
//            return CmdUserRsp.err(CmdRespErrEnums.INSUFFICIENT_BALANCE,cmdReq,centerAmount);
//        }finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//    }
//
//    private String batchProcess(CmdReq cmdReq,JSONObject request){
//        CmdUpdateBaseReq req = request.toJavaObject(CmdUpdateBaseReq.class);
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(VenueEnum.CMD.getVenueCode(), cmdReq.getPackageId()));
//        try {
//            return Optional.ofNullable(req.getTicketDetails()).map(lst -> {
//                String lastResult = null;
//                for (CmdUpdateDataReq item : lst) {
//                    lastResult = processItem(item,cmdReq,req.getActionId()); // 调用处理方法
//                }
//                return lastResult; // 返回最后一次调用的结果
//            }).orElse(CmdBaseRsp.success(cmdReq));
//        }catch (Exception e){
//            log.error("CMD failed bet error {}", e.getMessage());
//            return CmdUserRsp.err(CmdRespErrEnums.BETORDERNO_NOT_HAVA,cmdReq);
//        }finally {
//            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
//                rLock.unlock();
//            }
//        }
//    }
//
//
//
//   private String processItem(CmdUpdateDataReq item,CmdReq cmdReq,Integer actionId){
//       if(Objects.isNull(item)){
//           return CmdUserRsp.success(cmdReq);
//       }
//       CasinoMemberRespVO casinoMember = userCheck(item.getSourceName());
//       UserInfoVO userInfoVO = userInfoApi.getByUserId(casinoMember.getUserId());
//       if(Objects.isNull(userInfoVO)){
//           log.error("CMD processItem userName:{} not find.",item.getSourceName());
//           return CmdBaseRsp.err(CmdRespErrEnums.MEMBER_NOT_EXIST,cmdReq);
//       }
//       //派彩金额为0直接跳过
//       if(Objects.isNull(item.getTransactionAmount())|| BigDecimal.ZERO.compareTo(item.getTransactionAmount()) == 0 ) {
//           log.error("CMD processItem success userName:{} , amount:{}",item.getSourceName(),item.getTransactionAmount());
//           return CmdUserRsp.success(cmdReq);
//       }
//       //检测是否有投注注单不存在则直接退出
//       if (checkBetUserCoinRecordVO(userInfoVO,item.getReferenceNo(),CoinBalanceTypeEnum.EXPENSES.getCode(),WalletEnum.CoinTypeEnum.GAME_BET.getCode(),null)){
//           return CmdBaseRsp.err(CmdRespErrEnums.BETORDERNO_NOT_HAVA,cmdReq);
//       }
//       return switch (actionId) {
//           case CmdConstant.DangerRefund, CmdConstant.ResettleTicket ->{
//               yield cancel(userInfoVO,item,cmdReq);
//           } default ->{
//               yield updateBalance(userInfoVO,item,cmdReq);
//           }
//       };
//   }
//
//   private String cancel(UserInfoVO userInfoVO,CmdUpdateDataReq item,CmdReq cmdReq){
//       CoinRecordResultVO coinRecordResultVO=updateBalanceBetCancel(userInfoVO, item.getReferenceNo(), item.getTransactionAmount(),cmdReq.getPackageId());
//       return switch (coinRecordResultVO.getResultStatus()) {
//           case SUCCESS ->{
//               yield CmdBaseRsp.success(cmdReq);
//           } default ->{
//               yield CmdBaseRsp.err(CmdRespErrEnums.DUPLICATE_ID_NO,cmdReq);
//           }
//       };
//   }
//
//    private String updateBalance (UserInfoVO userInfoVO,CmdUpdateDataReq item,CmdReq cmdReq){
//        //根据三方给的金额判断是收入还是支出
//        String coinBalanceType=isNegativeSignum(item.getTransactionAmount())?CoinBalanceTypeEnum.EXPENSES.getCode():CoinBalanceTypeEnum.INCOME.getCode();
//        String checkCoinType=isNegativeSignum(item.getTransactionAmount())?WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode():WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode();
//        //检测当前注单本批次有没有派彩是否处理过
//        if (!checkBetUserCoinRecordVO(userInfoVO,item.getReferenceNo(),coinBalanceType,null,cmdReq.getPackageId())){
//            return CmdBaseRsp.err(CmdRespErrEnums.DUPLICATE_ID_NO,cmdReq);
//        }
//        //根据 收入|支出 和 派彩|派彩取消 查询订单号是否有存在的，然后给派彩或者重派彩，如果状态为植入则给出取消派彩
//        Boolean isHaveCoinRecord=false;
//        if (checkBetUserCoinRecordVO(userInfoVO,item.getReferenceNo(),coinBalanceType,checkCoinType,null)){
//            isHaveCoinRecord=true;
//        }
//        String coinType=null;
//        String businessCoinType=null;
//        String customerCoinType=null;
//        if (CoinBalanceTypeEnum.INCOME.getCode().equals(coinBalanceType) && isHaveCoinRecord){
//            coinType= WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode();
//            businessCoinType= WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode();
//            customerCoinType=WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode();
//        }else if (CoinBalanceTypeEnum.EXPENSES.getCode().equals(coinBalanceType) && isHaveCoinRecord){
//            coinType= WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode();
//            businessCoinType= WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode();
//            customerCoinType=WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode();
//        }else{
//            coinType= WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode();
//            businessCoinType=CoinBalanceTypeEnum.INCOME.getCode().equals(coinBalanceType)?WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode():WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode();
//            customerCoinType=CoinBalanceTypeEnum.INCOME.getCode().equals(coinBalanceType)?WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode():WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode();
//        }
//        // 正常派彩
//        item.setTransactionAmount(isNegativeSignum(item.getTransactionAmount())?item.getTransactionAmount().abs():item.getTransactionAmount());
//        CoinRecordResultVO coinRecordResultVO = updateBalancePayoutLocal(userInfoVO, item.getReferenceNo(), item.getTransactionAmount(),cmdReq.getPackageId(),
//                 coinBalanceType,coinType,businessCoinType,customerCoinType);
//        return switch (coinRecordResultVO.getResultStatus()) {
//            case SUCCESS ->{
//                yield  CmdBaseRsp.success(cmdReq);
//            }
//            case REPEAT_TRANSACTIONS ->{
//                yield CmdBaseRsp.err(CmdRespErrEnums.DUPLICATE_ID_NO, cmdReq);
//            }   default -> {
//                yield CmdBaseRsp.err(CmdRespErrEnums.INSUFFICIENT_BALANCE, cmdReq);
//            }
//        };
//    }
//
//    protected CoinRecordResultVO updateBalancePayoutLocal(UserInfoVO userInfoVO, String transactionId, BigDecimal payoutAmount,String remark,
//                                                          String balanceType,String coinType,String businessCoinType,String customerCoinType) {
//        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
//        userCoinAddVOPayout.setOrderNo(transactionId);
//        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVOPayout.setBalanceType(balanceType);
//        userCoinAddVOPayout.setCoinType(coinType);
//        userCoinAddVOPayout.setBusinessCoinType(businessCoinType);
//        userCoinAddVOPayout.setCustomerCoinType(customerCoinType);
//        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
//        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
//        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        userCoinAddVOPayout.setRemark(remark);
//        return userCoinApi.addCoin(userCoinAddVOPayout);
//    }
//
//
//
//    /**
//     * 投注取消
//     * @param userInfoVO
//     * @param orderNo
//     * @param amount
//     * @return
//     */
//    protected CoinRecordResultVO updateBalanceBetCancel(UserInfoVO userInfoVO, String orderNo, BigDecimal amount,String remark) {
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(orderNo);
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//        // 账变类型
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//        userCoinAddVO.setCoinValue(amount.abs());
//        userCoinAddVO.setRemark(remark);
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        //修改余额 记录账变
//        return userCoinApi.addCoin(userCoinAddVO);
//    }
//
//   private Boolean checkBetUserCoinRecordVO(UserInfoVO userInfoVO,String referenceNo,String coinBalanceType,String coinType,String remark){
//       UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//       coinRecordRequestVO.setOrderNo(referenceNo);
//       if (StringUtils.isNotBlank(remark)){
//           coinRecordRequestVO.setRemark(remark);
//       }
//       coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
//       coinRecordRequestVO.setUserId(userInfoVO.getUserId());
//       coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
//       coinRecordRequestVO.setBalanceType(coinBalanceType);
//       if (StringUtils.isNotBlank(coinType)){
//           coinRecordRequestVO.setCoinType(coinType);
//       }
//       ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//       boolean flag=false;
//       if(!userCoinRecords.isOk() || CollectionUtil.isEmpty(userCoinRecords.getData()) || userCoinRecords.getData().size() == 0) {
//           log.error("CMD 查询交易订单为空 或不存在 req:{}", referenceNo);
//           flag=true;
//       }
//       return flag;
//   }
//    /**
//     * 是否为负数
//     * @param transactionAmount
//     * @return
//     */
//    public boolean isNegativeSignum(BigDecimal transactionAmount) {
//        return transactionAmount.signum() == -1;
//    }
//
//    public CasinoMemberRespVO userCheck(String userAccount) {
//
//        CasinoMemberReqVO casinoMember = new CasinoMemberReqVO();
//        casinoMember.setVenueUserAccount(userAccount);
//        casinoMember.setVenueCode(VenueEnum.CMD.getVenueCode());
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMember);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return null;
//        }
//        return respVO.getData();
//    }
//
//    public String checkRequestValid(UserInfoVO userInfoVO, CmdReq cmdReq, boolean isBetting) {
//        if (ObjectUtil.isEmpty(userInfoVO)) {
//            log.info("CMD getBalance : userId : {} 用户不存在  场馆 - {}", userInfoVO.getUserId(), VenueEnum.CMD.getVenueName());
//            return CmdBaseRsp.err(CmdRespErrEnums.MEMBER_NOT_EXIST,cmdReq);
//        }
//
//
//        if (venueMaintainClosed(VenueEnum.CMD.getVenueCode(),userInfoVO.getSiteCode())) {
//            log.info("场馆未开启:{} ", VenueEnum.CMD.getVenueCode());
//            return CmdBaseRsp.err(CmdRespErrEnums.GAME_MAINTAINED,cmdReq);
//        }
//
//        if (userGameLock(userInfoVO)) {
//            log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.CMD.getVenueCode());
//            return CmdBaseRsp.err(CmdRespErrEnums.ACCOUNT_LOCKED,cmdReq);
//        }
//
//        if (isBetting){
//            SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder().siteCode(userInfoVO.getSiteCode())
//                    .venueCode(VenueEnum.CMD.getVenueCode())
//                    .currencyCode(userInfoVO.getMainCurrency()).build();
//            ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//            VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
//            if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
//                log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.CMD.getVenueCode());
//                return CmdBaseRsp.err(CmdRespErrEnums.VENUE_CLOSE,cmdReq);
//            }
//        }
//        return null;
//    }
//
//
//}
