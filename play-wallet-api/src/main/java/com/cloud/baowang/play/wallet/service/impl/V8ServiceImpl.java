//package com.cloud.baowang.play.wallet.service.impl;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.json.JSONObject;
//import com.alibaba.excel.util.StringUtils;
//import com.cloud.baowang.common.core.enums.CurrencyEnum;
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
//import com.cloud.baowang.play.api.api.order.OrderRecordApi;
//import com.cloud.baowang.play.api.api.third.VenueUserAccountApi;
//import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
//import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.wallet.enums.V8RespErrEnums;
//import com.cloud.baowang.play.wallet.service.V8Service;
//import com.cloud.baowang.play.wallet.service.base.BaseService;
//import com.cloud.baowang.play.wallet.util.Encrypt;
//import com.cloud.baowang.play.wallet.vo.res.v8.SeamlesswalletResp;
//import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
//import com.google.common.collect.Lists;
//import jakarta.servlet.http.HttpServletRequest;
//import jodd.util.StringUtil;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.api.RLock;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.List;
//import java.util.Objects;
//
//@Slf4j
//@Service
//@AllArgsConstructor
//public class V8ServiceImpl extends BaseService implements V8Service {
//
//    private final UserCoinRecordApi userCoinRecordApi;
//
//    private final PlayVenueInfoApi playVenueInfoApi;
//
//    private final OrderRecordApi orderRecordApi;
//
//    private final VenueUserAccountApi venueUserAccountApi;
//
//    private final RedisUtil redisUtil;
//
//
//    @Override
//    public SeamlesswalletResp seamlesswallet(HttpServletRequest request) {
//        String agent = request.getParameter("agent");
//        String param = request.getParameter("param");
//        String key = request.getParameter("key");
//        String timestamp = request.getParameter("timestamp");
//        log.info("v8获取账号余额agent={},param={},key={},timestamp={}",agent,param,key,timestamp);
//        int status = StringUtils.isEmpty(agent)?0:Integer.parseInt(agent);
//        if(StringUtils.isEmpty(agent) || StringUtils.isEmpty(param)){
//            log.info(("v8参数为空"));
//            return SeamlesswalletResp.success(V8RespErrEnums.DATEA_FORMAT_ERROR,status);
//        }
//        VenueInfoVO venueInfoVO = venueUserAccountApi.getVenueInfoByMerchantNo(VenueEnum.V8.getVenueCode(),agent);
//        if(Objects.isNull(venueInfoVO)){
//            log.info("v8 未查询到商户号");
//            return SeamlesswalletResp.success(V8RespErrEnums.AGENT_EXIST_NOT,status);
//        }
//        JSONObject json = new JSONObject();
//        try{
//            String paramKey = Encrypt.AESDecrypt(param,venueInfoVO.getAesKey(),false);
//            if(StringUtils.isEmpty(paramKey)){
//                log.info("v8参数解密失败{}",param);
//                return SeamlesswalletResp.success(V8RespErrEnums.DATEA_FORMAT_ERROR,status);
//            }
//            String[] splitParams = StringUtil.split(paramKey,"&");
//            for (String splitParam : splitParams) {
//                String[] split = StringUtil.split(splitParam,"=");
//                if(split.length == 2){
//                    if(!StringUtils.isEmpty(split[0])  && split[0].equals("currency")){
//                        if("VNDK".equals(split[1])){
//                            json.set(split[0],CurrencyEnum.KVND.getCode());
//                        }else{
//                            json.set(split[0],split[1]);
//                        }
//                    }else{
//                        json.set(split[0],split[1]);
//                    }
//                }
//            }
//            // 校验key是否一致
//            String pKey = agent+timestamp+venueInfoVO.getMerchantKey();
//            if(!Encrypt.MD5(pKey).equals(key)){
//                log.info("v8 key不一致{},{}",Encrypt.MD5(pKey), key);
//                return SeamlesswalletResp.success(V8RespErrEnums.MD5_KEY_ERROR,status);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            return SeamlesswalletResp.success(V8RespErrEnums.DATEA_FORMAT_ERROR,1001);
//        }
//        log.info("解析后的参数json={}",json);
//        String type = json.getStr("s");
//        String account = json.getStr("account");
//        // 经与第三方确认，数组里面只有1条内容
//        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//        casinoMemberReqVO.setVenueUserAccount(account);
//        casinoMemberReqVO.setVenueCode(VenueEnum.V8.getVenueCode());
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return SeamlesswalletResp.success(V8RespErrEnums.PLAYER_NOT_EXIST,type, account, null);
//        }
//        String userId = respVO.getData().getUserId();
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        if(Objects.isNull(userInfoVO)){
//            log.error("v8 games queryUserInfoByAccount userName[{}] not find.",account);
//            return SeamlesswalletResp.success(V8RespErrEnums.PLAYER_NOT_EXIST,type,account, null);
//        }
//
//        CasinoMemberRespVO casinoMember = respVO.getData();
//        String venueCode = casinoMember.getVenueCode();
//        List<String> venueCodes = Lists.newArrayList(VenueEnum.V8.getVenueCode());
//        if (!venueCodes.contains(venueCode)){
//            return SeamlesswalletResp.success(V8RespErrEnums.PLAYER_NOT_EXIST,type,account, null);
//        }
//        // 场馆关闭
//        if (venueMaintainClosed(venueCode,casinoMember.getSiteCode())) {
//            log.info("{}:场馆未开启", VenueEnum.MARBLES.getVenueName());
//            return SeamlesswalletResp.success(V8RespErrEnums.MAINTAINS,type,account, null);
//        }
//        // 游戏锁定
//        if (userGameLock(userInfoVO)) {
//            log.error("v8 game locked userName{}", userInfoVO.getUserName());
//            return SeamlesswalletResp.success(V8RespErrEnums.MAINTAINS,type,account, null);
//        }
//
//        log.info("v8 入参解析{}",json);
//        switch (type){
//            case "1001":
//                return getBalance(json,casinoMember);
//            case "1002":
//                return placeBet(json,userInfoVO,casinoMember);
//            case "1003":
//                return returnBalance(json, respVO.getData(), userInfoVO);
//            case "1004":
//                return getOrderStatus(json, respVO.getData(), userInfoVO);
//            case "1005":
//                return cancelBet(json,respVO.getData(),userInfoVO);
//
//        }
//        return SeamlesswalletResp.success();
//    }
//
//    /**
//     * 查询余额
//     */
//    public SeamlesswalletResp getBalance(JSONObject json,CasinoMemberRespVO casinoMember) {
//        String account = json.getStr("account");
//        String s = json.getStr("s");
//        String venueCode = casinoMember.getVenueCode();
//        List<String> venueCodes = Lists.newArrayList(VenueEnum.V8.getVenueCode());
//        if (!venueCodes.contains(venueCode)){
//            log.info("v8场馆不存在={}",venueCode);
//            return SeamlesswalletResp.success(V8RespErrEnums.MAINTAINS,s,account, null);
//        }
//        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userAccount(casinoMember.getUserAccount()).siteCode(casinoMember.getSiteCode()).build());
//        BigDecimal balance = BigDecimal.ZERO;
//        if (!Objects.isNull(userCenterCoin)) {
//            balance = userCenterCoin.getTotalAmount();
//        }
//
//        if (venueMaintainClosed(venueCode,userCenterCoin.getSiteCode())) {
//            log.info("v8场馆已关闭={}",venueCode);
//            return SeamlesswalletResp.success(V8RespErrEnums.MAINTAINS,s,account, null);
//        }
//
//        balance.setScale(2, RoundingMode.DOWN);
//        return SeamlesswalletResp.success(V8RespErrEnums.SUCCESS,s,account, balance.floatValue());
//    }
//
//
//    /**
//     * 获取订单状态
//     * @param json
//     * @param casinoMember
//     * @param userInfoVO
//     * @return
//     */
//    public SeamlesswalletResp getOrderStatus(JSONObject json,CasinoMemberRespVO casinoMember ,UserInfoVO userInfoVO){
//        log.info("v8 获取订单状态{},{},{}",json,casinoMember,userInfoVO);
//        String account = json.getStr("account");
//        String gameNo = json.getStr("gameNo");
//        String orderId = json.getStr("orderId");
//        String s = json.getStr("s");
//        try{
//            UserCoinRecordRequestVO vo = new UserCoinRecordRequestVO();
//            vo.setRemark(orderId);
//            vo.setOrderNo(gameNo);
//            ResponseVO<List<UserCoinRecordVO>> resp = userCoinRecordApi.getUserCoinRecords(vo);
//            if (!resp.isOk() || CollectionUtil.isEmpty(resp.getData()) || resp.getData().size() == 0) {
//                log.info("V8获取订单状态,userCoinAddVO:{},{}", VenueEnum.V8.getVenueName(), vo);
//                // 参考第三方文档查无订单返回4
//                return SeamlesswalletResp.successStatus(V8RespErrEnums.SUCCESS,4, s);
//            }
//            return SeamlesswalletResp.successStatus(V8RespErrEnums.SUCCESS,1, s);
//        }catch (Exception e){
//            log.error("v8获取订单状态异常{},{},{}",json,casinoMember,userInfoVO,e);
//            e.printStackTrace();
//            // 参考第三方文档失败返回2
//            return SeamlesswalletResp.successStatus(V8RespErrEnums.SUCCESS,2, s);
//        }
//    }
//
//    /**
//     * 返还余额
//     * 当我方请求返还余额时，在一定的时间内没有收到贵司正确的回应或没有任何的回应，该笔订单将会列为订单异常。
//     *  若该笔订单是派彩失败的异常订单号，我方重发时就会通过s1003 将金额返还哦
//     */
//    public SeamlesswalletResp returnBalance(JSONObject json,CasinoMemberRespVO casinoMember ,UserInfoVO userInfoVO) {
//        log.info("v8 返还余额{},{},{}",json,casinoMember,userInfoVO);
//        String account = json.getStr("account");
//        String money = json.getStr("money");
//        String orderId = json.getStr("orderId");
//        String gameId = json.getStr("gameId");
//        String kindId = json.getStr("kindId");
//        String s = json.getStr("s");
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(casinoMember.getVenueCode(), orderId+System.currentTimeMillis()));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("v8 errorBet error get locker error, req:{}", json);
//                return SeamlesswalletResp.success(V8RespErrEnums.FIAILURE_PLAYER,s,account, null);
//            }
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build());
//            if (Objects.isNull(userCenterCoin)) {
//                log.info("{}:用户钱包,不存在:{}", VenueEnum.V8.getVenueName(), userInfoVO.getUserId());
//                return SeamlesswalletResp.success(V8RespErrEnums.WALLET_NOT_EXIST,s, account, null);
//            }
//
//            BigDecimal amount = StringUtils.isEmpty(money)? BigDecimal.ZERO: new BigDecimal(money);
//            CoinRecordResultVO coinRecordResultVO = updateBalanceCancelBet(userInfoVO, gameId, amount,orderId);
//            SeamlesswalletResp resp = switch (coinRecordResultVO.getResultStatus()) {
//                case SUCCESS,AMOUNT_LESS_ZERO -> {
//                    UserCoinWalletVO coin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userAccount(casinoMember.getUserAccount()).siteCode(casinoMember.getSiteCode()).build());
//                    BigDecimal balance = BigDecimal.ZERO;
//                    if (!Objects.isNull(userCenterCoin)) {
//                        balance = coin.getTotalAmount();
//                    }
//                    yield SeamlesswalletResp.successBet(V8RespErrEnums.SUCCESS, s,account, balance.floatValue());
//                }
//                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL, REPEAT_TRANSACTIONS  ->{
//                    yield SeamlesswalletResp.success(V8RespErrEnums.FIAILURE_PLAYER,1);
//                }
//            };
//
//            if(resp.getD().getCode() != 0){
//                return resp;
//            }
//
//            /*OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
//            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
//            orderRecordMqVO.setOrderClassify(ClassifyEnum.SETTLED.getCode());
//            orderRecordMqVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
//            orderRecordMqVO.setVenueCode(VenueEnum.V8.getVenueCode());
//            orderRecordMqVO.setThirdOrderId(gameId);
//            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);*/
//            return resp;
//        }catch (Exception e){
//            e.printStackTrace();
//            return SeamlesswalletResp.success(V8RespErrEnums.FIAILURE_PLAYER,s,account, null);
//        }
//    }
//
//    private CoinRecordResultVO updateBalanceCancelBet(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount,String orderId) {
//        log.info("v8账变,orderId:{}, transferAmount:{}, transactionId:{}", orderId, transferAmount, transactionId);
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(transactionId);
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVO.setBalanceType(transferAmount.compareTo(BigDecimal.ZERO) > 0 ? CoinBalanceTypeEnum.INCOME.getCode() : CoinBalanceTypeEnum.EXPENSES.getCode());
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//        userCoinAddVO.setCoinValue(transferAmount.abs());
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        userCoinAddVO.setRemark(orderId);
//        userCoinAddVO.setVenueCode(VenueEnum.V8.getVenueCode());
//        //修改余额 记录账变
//        return userCoinApi.addCoin(userCoinAddVO);
//    }
//
//
//    /**
//     * 下注
//     */
//    public SeamlesswalletResp placeBet(JSONObject json,UserInfoVO userInfoVO,CasinoMemberRespVO casinoMember) {
//        log.info("v8 下注placeBet下注入参{}",json);
//        String account = json.getStr("account");
//        String orderId = json.getStr("orderId");
//        String gameId = json.getStr("gameId");
//        String money = json.getStr("money");
//        String kindId = json.getStr("kindId");
//        String s = json.getStr("s");
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(casinoMember.getVenueCode(), orderId));
//        try {
//            if (!rLock.tryLock()) {
//                log.error("v8 errorBet error get locker error, req:{}", json);
//                return SeamlesswalletResp.success(V8RespErrEnums.FIAILURE_PLAYER,s,account, null);
//            }
//
//            SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                    .siteCode(userInfoVO.getSiteCode())
//                    .venueCode(VenueEnum.V8.getVenueCode())
//                    .currencyCode(userInfoVO.getMainCurrency()).build();
//            ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//            VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
//            if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
//                log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), VenueEnum.V8.getVenueCode());
//                return SeamlesswalletResp.success(V8RespErrEnums.MAINTAINS,"1002",account, null);
//            }
//
//            BigDecimal amount = StringUtils.isEmpty(money)? BigDecimal.ZERO: new BigDecimal(money);
//            // 检查余额
//            if(!compareAmount(userInfoVO.getUserId(),userInfoVO.getSiteCode(),amount)){
//                log.info("v8 bet用户余额不足，userId:{},siteCode:{},money:{},",userInfoVO.getUserId(),userInfoVO.getSiteCode(),money);
//                return SeamlesswalletResp.success(V8RespErrEnums.INSUFFICIENT_BALANCE,s,account, amount.floatValue());
//            }
//            //修改余额 记录账变
//            CoinRecordResultVO coinRecordResultVO = this.updateBalanceBet(userInfoVO, gameId, amount,orderId);
//
//            return switch (coinRecordResultVO.getResultStatus()) {
//                case SUCCESS -> {
//                    yield SeamlesswalletResp.success(V8RespErrEnums.SUCCESS,s,account, coinRecordResultVO.getCoinAfterBalance().floatValue());
//                }
//                case REPEAT_TRANSACTIONS -> SeamlesswalletResp.success(V8RespErrEnums.DUPLICATE_ORDER,s,account, coinRecordResultVO.getCoinAfterBalance().floatValue());
//                case AMOUNT_LESS_ZERO, INSUFFICIENT_BALANCE, WALLET_NOT_EXIST ->{
//                    yield SeamlesswalletResp.success(V8RespErrEnums.FIAILURE_PLAYER,s,account, null);
//                }
//                case FAIL -> SeamlesswalletResp.success(V8RespErrEnums.FIAILURE_PLAYER,s,account, null);
//            };
//        }catch (Exception e){
//            log.error("v8 bet failed processAdjustBet error", e);
//            e.printStackTrace();
//            return SeamlesswalletResp.success(V8RespErrEnums.FIAILURE_PLAYER,s,account, null);
//        }
//    }
//
//
//
//
//
//    /**
//     * 取消下注
//     *
//     * 已下注取消
//     * 结算取消-派彩取消
//     *
//     * 1.当贵司成功扣款且在我方成功下注，但游戏判定为下注超时，就会发起1005取消下注通知，贵司必须处理不然玩家会有账变问题
//     *
//     * 2. 下注失败没有注单记录，所以连扣款都没有成功，则会发起1005取消下注通知。如贵司有回复成功，则重试的取消下注状态为成功
//     *
//     * 3. 如贵司一直未回复订单状态，就会进入重送，但超过15分钟后就不会再重送，将由人工进行处理
//     *
//     */
//    public SeamlesswalletResp cancelBet(JSONObject json,CasinoMemberRespVO casinoMember,UserInfoVO userInfoVO) {
//        log.info("v8cancelBet req:{}", json);
//        String account = json.getStr("account");
//        String gameNo = json.getStr("gameNo");
//        String gameId = json.getStr("gameId");
//        String money = json.getStr("money");
//        String currency = json.getStr("currency");
//        String orderId = json.getStr("orderId");
//        String s = json.getStr("s");
//        String kindId = json.getStr("kindId");
//        log.info("v8取消下注 account:{},gameNo:{},gameId:{},money:{},currency:{},orderId:{}",account,gameNo,gameId,money,currency,orderId);
//        RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getGameSeamlessWalletBetLockKey(casinoMember.getVenueCode(), orderId+System.currentTimeMillis()));
//        try{
//            if(!rLock.tryLock()){
//                log.error("v8 error cancelBet error get locker error, req:{}");
//                return SeamlesswalletResp.successCancel(V8RespErrEnums.FIAILURE_PLAYER,s,0);
//            }
//            UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//            coinRecordRequestVO.setRemark(orderId);
//            ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//            if(!userCoinRecords.isOk() || CollectionUtil.isEmpty(userCoinRecords.getData()) || userCoinRecords.getData().size() == 0){
//                log.error("v8取消下注 订单查询为空 orderId:{}",orderId);
//                return SeamlesswalletResp.successCancel(V8RespErrEnums.SUCCESS,s,0);
//            }
//            /*List<String> coinTypes = userCoinRecords.getData().stream().map(UserCoinRecordVO::getCoinType).toList();
//            if (coinTypes.contains(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())){
//                return SeamlesswalletResp.success(V8RespErrEnums.CANCEL_NOT_EXIST,s,account,null);
//            }*/
//            CoinRecordResultVO coinRecordResultVO = updateBalanceBetCancelLocal(userInfoVO, gameNo, new BigDecimal(money).abs(),orderId);
//            SeamlesswalletResp resp = switch (coinRecordResultVO.getResultStatus()) {
//                case SUCCESS ->{
//                    yield SeamlesswalletResp.successCancel(V8RespErrEnums.SUCCESS,s,0);
//                }
//                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL, REPEAT_TRANSACTIONS , AMOUNT_LESS_ZERO ->{
//                    yield SeamlesswalletResp.successCancel(V8RespErrEnums.FIAILURE_PLAYER,s,1);
//                }
//            };
//
//            if(resp.getD().getCode() != 0){
//                return resp;
//            }
//
//            // 第三方回复： 没有取消，重算
//
//            // 拉单的数状态都是已结算，故当取消时， 更新注单的状态
//            /*OrderRecordMqVO orderRecordMqVO = new OrderRecordMqVO();
//            BeanUtil.copyProperties(userInfoVO, orderRecordMqVO, "id");
//            orderRecordMqVO.setThirdOrderId(gameId);
//            if("VNDK".equals(currency)){
//                orderRecordMqVO.setCurrency("KVND");
//            }else{
//                orderRecordMqVO.setCurrency(currency);
//            }
//            orderRecordMqVO.setOrderClassify(ClassifyEnum.CANCEL.getCode());
//            orderRecordMqVO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
//            orderRecordMqVO.setVenueCode(VenueEnum.V8.getVenueCode());
//            KafkaUtil.send(TopicsConstants.THIRD_GAME_ORDER_RECORD, orderRecordMqVO);*/
//            return resp;
//        }catch (Exception e){
//            log.error("v8取消下注失败，失败原因为{}",e.getMessage());
//            e.printStackTrace();
//            return SeamlesswalletResp.successCancel(V8RespErrEnums.FIAILURE_PLAYER,s,0);
//        }
//    }
//
//
//
//
//
//    protected CoinRecordResultVO updateBalanceBet(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount,String remark) {
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(transactionId);
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//        userCoinAddVO.setCoinValue(transferAmount.abs());
//        userCoinAddVO.setRemark(remark);
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        userCoinAddVO.setVenueCode(VenueEnum.V8.getVenueCode());
//        //修改余额 记录账变
//        return userCoinApi.addCoin(userCoinAddVO);
//    }
//
//
//    protected CoinRecordResultVO updateBalanceBetCancelLocal(UserInfoVO userInfoVO, String orderNo, BigDecimal amount,String remark) {
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
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        userCoinAddVO.setRemark(remark);
//        userCoinAddVO.setVenueCode(VenueEnum.V8.getVenueCode());
//        //修改余额 记录账变
//        return userCoinApi.addCoin(userCoinAddVO);
//    }
//}
