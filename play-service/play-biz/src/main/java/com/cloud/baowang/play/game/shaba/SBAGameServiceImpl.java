package com.cloud.baowang.play.game.shaba;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.I18nInitConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.data.transfer.cache.SystemDictCache;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.sb.*;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.SBATransferEnums;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.enums.SBActionEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.order.client.*;
import com.cloud.baowang.play.api.vo.sba.CheckTicketStatusVO;
import com.cloud.baowang.play.api.vo.sba.TransHistoryVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.third.SBA.SBAEventsInfo;
import com.cloud.baowang.play.api.vo.third.SBA.SBALoginReqVO;
import com.cloud.baowang.play.api.vo.third.SBA.SBARegisterReqVO;
import com.cloud.baowang.play.api.vo.transferRecordVO.TransferRecordResultVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.constants.ThirdConstants;
import com.cloud.baowang.play.game.acelt.response.SBABetRecordRes;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.shaba.constant.SbaConstant;
import com.cloud.baowang.play.game.shaba.response.BetDetail;
import com.cloud.baowang.play.game.shaba.response.LangName;
import com.cloud.baowang.play.game.shaba.response.ParlayData;
import com.cloud.baowang.play.game.shaba.response.ResettLementInfo;
import com.cloud.baowang.play.game.shaba.response.client.ClientBetDetail;
import com.cloud.baowang.play.game.shaba.response.client.ClientParlayInfo;
import com.cloud.baowang.play.mapper.OrderRecordEsMapper;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.play.po.SportEventsInfoPO;
import com.cloud.baowang.play.po.TransferRecordErrorPO;
import com.cloud.baowang.play.po.TransferRecordPO;
import com.cloud.baowang.play.repositories.TransferRecordErrorRepository;
import com.cloud.baowang.play.service.*;
import com.cloud.baowang.play.task.pulltask.sh.params.ShVenuePullBetParams;
import com.cloud.baowang.play.util.SBAOrderParseUtil;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.SBA)
public class SBAGameServiceImpl extends GameBaseService implements GameService {

    private final VenueUserAccountConfig venueUserAccountConfig;

    private final OrderRecordProcessService orderRecordProcessService;

    private final UserInfoApi userInfoApi;

    private final VenueInfoService venueInfoService;

    private final TransferRecordService transferRecordService;

    private final TransferRecordErrorRepository transferRecordErrorRepository;

    private final SportEventsService sportEventsService;

    private final SiteApi siteApi;

    private final GamePlayService gamePlayService;

    private final String sbaClientZone = "UTC-0";

    private final SportEventsInfoService sportEventsInfoService;

    private final BetCoinJoinService betCoinJoinService;

    private final OrderRecordEsMapper orderRecordEsMapper;

    private final SystemDictCache systemDictCache;

    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {

        String userAccount = casinoMemberVO.getVenueUserAccount();

        String url = venueDetailVO.getApiUrl() + ThirdConstants.SBA_REGISTER_API;

        String vendorId = venueDetailVO.getMerchantNo();

        String operatorId = venueDetailVO.getMerchantKey();

        UserInfoVO userInfoVO = getByUserId(CurrReqUtils.getOneId());
        String currencyCode = userInfoVO.getMainCurrency();
        if (ObjectUtil.isEmpty(currencyCode)) {
            log.info("查询用户信息失败,没有获取到币种:{}", CurrReqUtils.getOneId());
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        SBCurrencyEnum currencyEnum = SBCurrencyEnum.getByPlatformCurrencyCode(currencyCode);
        if (ObjectUtil.isEmpty(currencyEnum)) {
            log.info("映射币种失败,没有获取到币种:{}", currencyCode);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        //欧洲盘
        Integer odds = 3;
        SBARegisterReqVO reqVO = SBARegisterReqVO.builder()
                .operatorId(operatorId)
                .vendorId(vendorId)
                .vendorMemberId(userAccount)
                .username(userAccount)
                .oddstype(odds)
                .build();


        //产线环境才校验币种,测试环境不校验.因为测试环境沙巴传的是测试币
        int currency = 20;
        if (venueUserAccountConfig.getEvn()) {
            currency = currencyEnum.getCode();
        }
        reqVO.setCurrency(currency);


        log.info("沙巴体育-注册账号请求:{}", reqVO);
        try {

            String response = HttpClientHandler.post(url, SBARegisterReqVO.getRegisterInfo(reqVO));

            if (ObjectUtil.isNotEmpty(response)) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                if (jsonObject != null) {
                    Integer code = jsonObject.getInteger("error_code");
                    if (code.equals(0) || code.equals(6)) {//6= 重复创建游戏账号等于注册成功
                        return ResponseVO.success(true);
                    }
                }
            }
        } catch (Exception e) {
            log.info("沙巴体育-注册账号请求异常", e);
        }
        return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
    }


    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return loginSBAGame(venueDetailVO.getMerchantNo(), venueDetailVO.getGameUrl(), casinoMemberVO);
    }

    /**
     * 登陆沙巴体育
     *
     * @param vendorId       厂商识别码
     * @param gameUrl        地址
     * @param casinoMemberVO 传到三方的账号
     */
    private ResponseVO<GameLoginVo> loginSBAGame(String vendorId, String gameUrl, CasinoMemberVO casinoMemberVO) {
        GameLoginVo result = GameLoginVo.builder()
                .venueCode(VenueEnum.SBA.getVenueCode())
                .type(GameLoginTypeEnums.TOKEN.getType())
                .userAccount(casinoMemberVO.getUserAccount())
                .build();
        String userId = CurrReqUtils.getOneId();
        //用户未登陆获取的时候是匿名账号
        if (ObjectUtil.isEmpty(CurrReqUtils.getOneId())) {
            userId = casinoMemberVO.getVenueUserAccount();
        }
        String key = String.format(RedisConstants.THREE_GAME_TOKEN, VenueEnum.SBA.getVenueCode(), userId);
        String token = RedisUtil.getValue(key);

        if (!StringUtils.isBlank(token)) {
            result.setSource(token);
            return ResponseVO.success(result);
        }


        try {


            String url = gameUrl + ThirdConstants.SBA_LOGIN_API;
            SBALoginReqVO reqVO = SBALoginReqVO.builder().vendor_id(vendorId).vendor_member_id(casinoMemberVO.getVenueUserAccount()).build();
            log.info("沙巴体育-登陆请求:{}", reqVO);
            String response = HttpClientHandler.post(url, JSON.toJSONString(reqVO));

            if (ObjectUtil.isNotEmpty(response)) {
                JSONObject jsonObject = JSON.parseObject(response);
                if (jsonObject != null) {
                    token = jsonObject.getString("access_token");
                    if (ObjectUtil.isEmpty(token)) {
                        return ResponseVO.fail(ResultCode.NO_HAVE_DATA);
                    }
//                if (ObjectUtil.isNotEmpty(CurrReqUtils.getOneId())) {
                    RedisUtil.setValue(key, token, 5L, TimeUnit.MINUTES);
//                }
                }
            }
        } catch (Exception e) {
            log.error("沙巴体育-登陆异常:", e);
            return ResponseVO.fail(ResultCode.NO_HAVE_DATA);
        }

        if (ObjectUtil.isEmpty(token)) {
            return ResponseVO.fail(ResultCode.NO_HAVE_DATA);
        }

        result.setSource(token);
        return ResponseVO.success(result);
    }


    @Override
    public String genVenueUserPassword() {
        return "";
    }


    private ResponseVO<ShVenuePullBetParams> toPullBetOrderList(Map<String, SiteVO> siteMap, Map<String, String> paramMap,
                                                                String versionKey, VenueInfoVO venueDetailVO) {
        String response;
        paramMap.put("version_key", versionKey);

        String url = venueDetailVO.getBetUrl() + ThirdConstants.SBA_BET_ORDER;
        try {
            log.info("{}-拉单 url: {}, 参数: {}", VenueEnum.SBA.getVenueName(), url, JSON.toJSONString(paramMap));

            response = HttpClientHandler.post(url, paramMap);
            log.info("{}-拉单 获取注单列表返回消息: {}", VenueEnum.SBA.getVenueName(), response);

            if (ObjectUtil.isNotEmpty(response)) {

                SBABetRecordRes sbaBetRecordRes = JSON.parseObject(response, new TypeReference<SBABetRecordRes>() {
                });

//                JSONObject jsonObject = JSONObject.parseObject(response);
                if (sbaBetRecordRes == null) {
                    log.info("{}-拉单 获取注单列表返回消息: {} 异常", VenueEnum.SBA.getVenueName(), response);
                    return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
                }
                Integer errorCode = sbaBetRecordRes.getError_code();
                if (SbaConstant.SUCC_CODE.equals(errorCode)) {
                    SBABetRecordData data = sbaBetRecordRes.getData();
                    if (data == null) {
                        log.info("{}-拉单 Data 获取注单列表返回消息: {} 异常", VenueEnum.SBA.getVenueName(), response);
                        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
                    }

                    String lastVersionKey = data.getLast_version_key();

                    // 解析注单数据
//                    JSONArray betDetails = dataJson.getJSONArray("BetDetails");
                    List<BetDetail> betDetails = data.getBetDetails();
                    List<OrderRecordVO> orderList = parseOrder(venueDetailVO, betDetails, siteMap, response);

                    // 解析虚拟体育注单数据
                    List<BetDetail> betVirtualSportDetails = data.getBetVirtualSportDetails();
                    List<OrderRecordVO> virtualOrderList = parseOrder(venueDetailVO, betVirtualSportDetails, siteMap, response);
                    if (CollectionUtils.isNotEmpty(virtualOrderList)) {
                        orderList.addAll(virtualOrderList);
                    }

//                    JSONArray betNumberDetails = dataJson.getJSONArray("BetNumberDetails");

                    List<BetDetail> betNumberDetails = data.getBetNumberDetails();
                    List<OrderRecordVO> betNumberOrderList = parseOrder(venueDetailVO, betNumberDetails, siteMap, response);
                    if (CollectionUtils.isNotEmpty(betNumberOrderList)) {
                        orderList.addAll(betNumberOrderList);
                    }

                    if (CollectionUtils.isNotEmpty(orderList)) {
                        orderRecordProcessService.orderProcess(orderList);
                    }

                    if (lastVersionKey.equals(versionKey)) {
                        long nextStart = DateUtils.getAddMinute(new Date(System.currentTimeMillis()), 2).getTime();
                        log.info("{},拉单返回偏移量没变,无法进行下次拉取,需等待2分钟。,下次拉取时间:{}", VenueEnum.SBA.getVenueName(), DateUtils.convertDateToString(new Date(nextStart)));
                        return ResponseVO.success(ShVenuePullBetParams.builder().versionKey(versionKey).startTime(nextStart).build());
                    }

                    if (lastVersionKey.equals("0")) {
                        log.info("{},拉单返回偏移量回归到初始值,三方返回异常", VenueEnum.SBA.getVenueName());
                        return ResponseVO.success(ShVenuePullBetParams.builder().versionKey(versionKey).build());
                    }

                    log.info("{},拉单 最新版本: {}", VenueEnum.SBA.getVenueName(), lastVersionKey);
                    return ResponseVO.success(ShVenuePullBetParams.builder().versionKey(lastVersionKey).build());
                }
            }
        } catch (Exception e) {
            log.error("SBA-getBetRecordList 获取注单列表发生错误!!,当前版本号:{}", versionKey, e);
            RedisUtil.setValue(RedisConstants.SBA_PULL_ERROR_BET_RECORD, versionKey, 10L, TimeUnit.MINUTES);
            //出现报错后把当前的版本号返出去,不继续往前拉了.让下次拉的时候从当前报错的版本开始拉
            return ResponseVO.success(ShVenuePullBetParams.builder().versionKey(versionKey).build());
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    @Override
    public ResponseVO<ShVenuePullBetParams> getBetRecordList(VenueInfoVO venueDetailVO, VenuePullParamVO venuePullParamVO) {

        String errorVersionKey = RedisUtil.getValue(RedisConstants.SBA_PULL_ERROR_BET_RECORD);
        if (ObjectUtil.isNotEmpty(errorVersionKey)) {
            log.info("沙巴体育拉单有异常版本,暂时不进行继续拉单:{}", errorVersionKey);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        List<SiteVO> siteAll = siteApi.siteInfoAllstauts().getData();
        Map<String, SiteVO> siteMap = siteAll.stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO -> SiteVO));

        String versionKey = venuePullParamVO.getVersionKey();
        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("vendor_id", venueDetailVO.getMerchantNo());

        while (true) {
            ResponseVO<ShVenuePullBetParams> result = toPullBetOrderList(siteMap, paramMap, versionKey, venueDetailVO);
            if (!result.isOk()) {
                return result;
            }

            ShVenuePullBetParams pullResult = result.getData();
            if (pullResult.getVersionKey().equals(versionKey)) {
                return result;
            }
            versionKey = pullResult.getVersionKey();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }


    /**
     * 取得所有已达重试上限的注单。
     */
    public void sbaPullReachLimitTrans(String date) {
        log.info("进入已达重试上限的注单:{},time:{}", VenueEnum.SBA.getVenueName(), date);

        VenueInfoVO venueDetailVO = venueInfoService.getAdminVenueInfoByVenueCode(VenueEnum.SBA.getVenueCode(), null);
        if (ObjectUtil.isEmpty(venueDetailVO)) {
            log.info("getReachLimitTrans:{} 进入已达重试上限的注单 未查询到配置。", VenueEnum.SBA.getVenueName());
            return;
        }
        String time = TimeZoneUtils.formatLocalDateTime(TimeZoneUtils.timeByTimeZone(System.currentTimeMillis(), TimeZoneUtils.sbaTimeZone), DateUtils.FULL_FORMAT_5);

        if (StringUtils.isNotBlank(date)) {
            time = date;
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("vendor_id", venueDetailVO.getMerchantNo());
        paramMap.put("start_Time", time);

        String url = venueDetailVO.getBetUrl() + ThirdConstants.SBA_GET_REACH_LIMIT_TRANS;

        String response = HttpClientHandler.post(url, paramMap);
        log.info("getReachLimitTrans:{} 已达重试上限的注单,参数:{},result:{}", VenueEnum.SBA.getVenueName(), JSON.toJSONString(paramMap), response);

        if (ObjectUtil.isEmpty(response)) {
            log.info("getReachLimitTrans:{} 已达重试上限的注单。查询失败,result:{}", VenueEnum.SBA.getVenueName(), response);
            return;
        }

        JSONObject jsonObject = JSONObject.parseObject(response);
        if (jsonObject == null || !SbaConstant.SUCC_CODE.equals(jsonObject.getInteger("error_code"))) {
            log.info("getReachLimitTrans:{} 已达重试上限的注单。查询失败,error_code,result:{}", VenueEnum.SBA.getVenueName(), response);
            return;
        }

        JSONObject dataJson = jsonObject.getJSONObject("Data");
        if (dataJson == null) {
            log.info("getReachLimitTrans:{} 已达重试上限的注单。Data==null,result:{}", VenueEnum.SBA.getVenueName(), response);
            return;
        }

        JSONArray detailJSON = dataJson.getJSONArray("Txns");

        if (detailJSON == null || CollectionUtil.isEmpty(detailJSON)) {
            log.info("getReachLimitTrans:{} 已达重试上限的注单。txnsJson==null,result:{}", VenueEnum.SBA.getVenueName(), response);
            return;
        }


        List<TransferRecordErrorPO> list = Lists.newArrayList();

        //将异常注单保存
        for (int i = 0; i < detailJSON.size(); i++) {
            JSONObject betDetail = detailJSON.getJSONObject(i);
            CheckTicketStatusVO checkTicketStatusVO = betDetail.toJavaObject(CheckTicketStatusVO.class);
            TransferRecordErrorPO transferRecordErrorPO = TransferRecordErrorPO
                    .builder()
                    .transId(checkTicketStatusVO.getLicenseeTxId())
                    .orderId(checkTicketStatusVO.getRefId())
                    .betId(checkTicketStatusVO.getTxId())
                    .venueCode(VenueEnum.SBA.getVenueCode())
                    .userAccount(checkTicketStatusVO.getUserId())
                    .startTime(time)
                    .build();

            for (TransHistoryVO historyVO : checkTicketStatusVO.getTransHistory()) {
                transferRecordErrorPO.setApi(historyVO.getAction());
                transferRecordErrorPO.setOperationId(historyVO.getOperationId());
                transferRecordErrorPO.setDetail(JSON.toJSONString(historyVO));
                list.add(transferRecordErrorPO);
            }
        }

        List<String> orderList = list.stream().map(TransferRecordErrorPO::getOrderId).toList();

        List<TransferRecordErrorPO> transferRecordErrorPOS = transferRecordErrorRepository.selectList(Wrappers.lambdaQuery(TransferRecordErrorPO.class)
                .in(TransferRecordErrorPO::getOrderId, orderList));

        Map<String, TransferRecordErrorPO> transferRecordMap = transferRecordErrorPOS.stream()
                .collect(Collectors.toMap(
                        x -> x.getOrderId() + "_" + x.getApi(),
                        x -> x
                ));

        for (TransferRecordErrorPO x : list) {
            String key = x.getOrderId() + "_" + x.getApi();
            if (transferRecordMap.containsKey(key)) {
                continue;
            }
            transferRecordErrorRepository.insert(x);
        }


        for (int i = 0; i < detailJSON.size(); i++) {
            JSONObject betDetail = detailJSON.getJSONObject(i);
            CheckTicketStatusVO checkTicketStatusVO = betDetail.toJavaObject(CheckTicketStatusVO.class);

            if (CollectionUtil.isEmpty(checkTicketStatusVO.getTransHistory())) {
                log.info("getReachLimitTrans:{} 拉取未处理的订单。参数异常 result:{}", VenueEnum.SBA.getVenueName(), checkTicketStatusVO);
                continue;
            }

            for (TransHistoryVO historyVO : checkTicketStatusVO.getTransHistory()) {
                SBActionEnum actionEnum = SBActionEnum.nameOfCode(historyVO.getAction());
                if (actionEnum == null) {
                    log.info("getReachLimitTrans:{} 拉取未处理的订单。actionEnum为空 result:{}", VenueEnum.SBA.getVenueName(), checkTicketStatusVO);
                    continue;
                }
                toRetRyOperation(venueDetailVO, historyVO, checkTicketStatusVO.getRefId());
            }
        }


    }

    /**
     * 重试异常订单让沙巴重试
     */
    public void sbaPullStatus(Integer orderStatus, String order) {

        List<String> orderList = Lists.newArrayList();
        if (StringUtils.isNotBlank(order)) {
            orderList = Arrays.asList(order.split(","));
        }

        log.info("getCheckStatus:{} 进入未处理的订单查询逻辑。", VenueEnum.SBA.getVenueName());
        VenueInfoVO venueDetailVO = venueInfoService.getAdminVenueInfoByVenueCode(VenueEnum.SBA.getVenueCode(), null);
        if (ObjectUtil.isEmpty(venueDetailVO)) {
            log.info("getCheckStatus:{} 未处理的订单查询逻辑 未查询到配置。", VenueEnum.SBA.getVenueName());
            return;
        }


        int time = -60;
        long startTime = DateUtils.getAddMinute(new Date(), time).getTime();

        long nowTime = new Date().getTime();
        TransferRecordResultVO transferRecord = TransferRecordResultVO.builder()
                .createStartTime(startTime)
                .createEndTime(nowTime)
                .venueCode(VenueEnum.SBA.getVenueCode())
                .orderStatusIds(Lists.newArrayList(orderStatus))
                .build();


        if (CollectionUtil.isNotEmpty(orderList)) {
            transferRecord = TransferRecordResultVO.builder()
                    .venueCode(VenueEnum.SBA.getVenueCode())
                    .orderIds(orderList)
                    .build();
        }


        //分批次查询出未处理的订单进行处理
        int currentPage = 1;
        boolean hasMoreData = true;
        while (hasMoreData) {
            IPage<TransferRecordResultVO> entityPage = transferRecordService.getTransferRecordPage(new Page<>(currentPage, 100), transferRecord);
            List<TransferRecordResultVO> dataList = entityPage.getRecords();
            if (dataList != null && !dataList.isEmpty()) {
                for (TransferRecordResultVO data : dataList) {
                    if (ObjectUtil.isEmpty(data.getOrderId())) {
                        continue;
                    }
                    toCheckStatus(venueDetailVO, data, orderStatus);
                }
            }
            if (currentPage >= entityPage.getPages()) {
                hasMoreData = false;
                log.info("getCheckStatus:{} 未处理的订单查询逻辑 结束。第[{}]页", VenueEnum.SBA.getVenueName(), currentPage);
            } else {
                currentPage++;
            }
        }
    }


    private void toCheckStatus(VenueInfoVO venueDetailVO, TransferRecordResultVO data, Integer orderStatus) {
        String versionKey = venueDetailVO.getMerchantNo();
        String url = venueDetailVO.getBetUrl() + ThirdConstants.SBA_CHECK_STATUS;

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("vendor_id", versionKey);
        paramMap.put("refId", data.getOrderId());
        String response = HttpClientHandler.post(url, paramMap);
        log.info("getCheckStatus:{} 拉取未处理的订单,参数:{},result:{}", VenueEnum.SBA.getVenueName(), JSON.toJSONString(paramMap), response);

        if (ObjectUtil.isEmpty(response)) {
            log.info("getCheckStatus:{} 拉取未处理的订单。查询失败,result:{}", VenueEnum.SBA.getVenueName(), response);
            return;
        }

        JSONObject jsonObject = JSONObject.parseObject(response);
        if (jsonObject == null || !SbaConstant.SUCC_CODE.equals(jsonObject.getInteger("error_code"))) {
            log.info("getCheckStatus:{} 拉取未处理的订单。查询失败,result:{}", VenueEnum.SBA.getVenueName(), response);
            return;
        }

        JSONObject dataJson = jsonObject.getJSONObject("Data");
        CheckTicketStatusVO checkTicketStatusVO = dataJson.toJavaObject(CheckTicketStatusVO.class);

        if (isInvalidCheckTicketStatusVO(checkTicketStatusVO)) {
            log.info("getCheckStatus:{} 拉取未处理的订单。参数异常 result:{}", VenueEnum.SBA.getVenueName(), checkTicketStatusVO);
            return;
        }

        for (TransHistoryVO historyVO : checkTicketStatusVO.getTransHistory()) {
            SBActionEnum actionEnum = SBActionEnum.nameOfCode(historyVO.getAction());

            if (Objects.equals(actionEnum.getCode(), SBActionEnum.PLACE_BET.getCode())
                    || Objects.equals(actionEnum.getCode(), SBActionEnum.PLACE_BET_PARLAY.getCode())) {
                continue;
            }

            //查询已下注未确认的订单
            if (Objects.equals(orderStatus, SBATransferEnums.PLACE_BET.getCode())) {

                if (actionEnum.getCode().equals(SBActionEnum.CONFIRM_BET_PARLAY.getCode()) || actionEnum.getCode().equals(SBActionEnum.CONFIRM_BET.getCode())) {
                    if (StringUtils.isBlank(historyVO.getOperationId())) {
                        log.info("getCheckStatus:{} 拉取未处理的订单。返回参数异常 result:{}", VenueEnum.SBA.getVenueName(), historyVO);
                        continue;
                    }

                    if (historyVO.getStatus() == 0) {
                        continue;
                    }
                    toRetRyOperation(venueDetailVO, historyVO, checkTicketStatusVO.getRefId());
                }
            }

            //查询已确认但是未结算的订单
            if (Objects.equals(orderStatus, SBATransferEnums.CONFIRM_BET.getCode())) {
                if (!actionEnum.getCode().equals(SBActionEnum.PLACE_BET.getCode()) && !actionEnum.getCode().equals(SBActionEnum.PLACE_BET_PARLAY.getCode()) &&
                        !actionEnum.getCode().equals(SBActionEnum.CONFIRM_BET.getCode()) && !actionEnum.getCode().equals(SBActionEnum.CONFIRM_BET_PARLAY.getCode())) {
                    if (historyVO.getStatus() == 0) {
                        continue;
                    }
                    toRetRyOperation(venueDetailVO, historyVO, checkTicketStatusVO.getRefId());
                }
            }
        }
    }


    private void toRetRyOperation(VenueInfoVO venueDetailVO, TransHistoryVO transHistoryVO, String refId) {

        String retUrl = venueDetailVO.getBetUrl() + ThirdConstants.SBA_RET_RY_OPERATION;
        Map<String, String> retParamMap = new HashMap<>();
        retParamMap.put("vendor_id", venueDetailVO.getMerchantNo());
        retParamMap.put("operationId", transHistoryVO.getOperationId());
        String retResponse = HttpClientHandler.post(retUrl, retParamMap);
        log.info("toRetRyOperation:{} ,refId:{}, 发起重试 result:{}", VenueEnum.SBA.getVenueName(), refId, retResponse);
    }


    private boolean isInvalidCheckTicketStatusVO(CheckTicketStatusVO checkTicketStatusVO) {
        return ObjectUtil.isEmpty(checkTicketStatusVO.getLicenseeTxId())
                || ObjectUtil.isEmpty(checkTicketStatusVO.getRefId())
                || ObjectUtil.isEmpty(checkTicketStatusVO.getUserId());
    }


    /**
     * 处理时间字段，包括匹配时间、交易时间、结算时间等。
     */
    private void processTimeFields(BetDetail betDetailEntity) {

        // 投注交易时间, 2023-08-31T07:52:19.793
        if (StringUtils.isNotBlank(betDetailEntity.getTransaction_time())) {
            if (!betDetailEntity.getTransaction_time().contains(".")) {
                betDetailEntity.setTransaction_time(betDetailEntity.getTransaction_time() + ".000");
            }
        }
        // 注单结算的时间 示例: 2023-08-31T09:27:21.703
        // 注单结算的时间,仅支援 sport_type:1~99,175, 当 ticket_status=void 时, 此字段非最终结算时间. 当 ticket_status=reject 时, 则不支持此字段.
        // Integer sportType, Integer betType, String ticketStatus, String winlostDatetime, String settlementTime)
//        betDetailEntity.setSettlement_time(getSettledTime(betDetailEntity.getSport_type(), betDetailEntity.getBet_type(),
//                betDetailEntity.getTicket_status(), betDetailEntity.getWinlost_datetime(), betDetailEntity.getSettlement_time()));

        betDetailEntity.setSettlement_time(formatTime(betDetailEntity.getSettlement_time()));

        // 决胜时间(仅显示日期),请依此字段做为后台报表对帐使用 2023-08-31T00:00:00
//        betDetailEntity.setWinlost_datetime(TimeZoneUtils.convertTimeZone2Str(betDetailEntity.getWinlost_datetime(), pattenT, gmt4TimeZone, NewYorkTimeZone));
        betDetailEntity.setWinlost_datetime(formatTime(betDetailEntity.getWinlost_datetime()));

    }

    public static String replaceSubstring(String originalStr, String targetStr, String replaceStr) {
        if (originalStr.contains(targetStr)) {
            return originalStr.replace(targetStr, replaceStr);
        } else {
            return originalStr;
        }
    }


    /**
     * 填充 OrderRecordVO 对象的基本信息。
     */
    private OrderRecordVO populateOrderRecordVO(BetDetail entity, UserInfoVO userInfoVO, VenueInfoVO venueInfoVO, UserLoginInfoVO userLoginInfoVO,
                                                Integer gameTypeId, Map<String, SiteVO> siteMap) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setSiteCode(userInfoVO.getSiteCode());
        recordVO.setUserId(userInfoVO.getUserId());
        // 会员账号
        recordVO.setUserAccount(userInfoVO.getUserAccount());
        // 会员姓名
        recordVO.setUserName(userInfoVO.getUserName());
        // 账号类型 1测试 2正式 3商务 4置换
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        // 三方会员账号
        recordVO.setCasinoUserName(entity.getVendor_member_id());
        // TODO 上级代理账号 agentAcct
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());//vip当前等级code
        recordVO.setVipRank(userInfoVO.getVipRank());//vip段位
        // 三方平台
        recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
        // 游戏平台名称
        // 游戏平台CODE
        recordVO.setVenueCode(venueInfoVO.getVenueCode());

        // 游戏大类
        recordVO.setVenueType(gameTypeId);

        // 游戏Code
        recordVO.setGameCode(VenueEnum.SBA.getVenueCode());

        recordVO.setPlayType(SBBetTypeEnum.of(entity.getBet_type()));

        //串关详情说明
//        recordVO.setDeskNo(SBAOrderParseUtil.getBetTypeList(entity.getCombo_type()));


        // 下注内容
        recordVO.setBetContent(entity.getBet_team()); // 下注选项内容
        if (SBBetTypeEnum.SBA_BET_TYPE_29.getCode().equals(entity.getBet_type())) {
            recordVO.setPlayType(SBBetTypeEnum.SBA_BET_TYPE_29.getDesc());
        }

        // 变更状态
//        recordVO.setChangeStatus(ChangeStatusEnum.NOT_CHANGE.getCode());

        SiteVO siteVO = siteMap.get(userInfoVO.getSiteCode());
        if (ObjectUtil.isNotEmpty(siteVO)) {
            recordVO.setSiteName(siteVO.getSiteName());
        }

        if (ObjectUtil.isNotEmpty(entity.getTransaction_time())) {
            // 投注时间
            recordVO.setBetTime(TimeZoneUtils.convertStringToTimestamp(entity.getTransaction_time(), TimeZoneUtils.sbaTimeZone));
        }

        if (ObjectUtil.isNotEmpty(entity.getSettlement_time())) {
            // 结算时间
            Long settlement = TimeZoneUtils.convertStringToTimestamp(entity.getSettlement_time(), TimeZoneUtils.sbaTimeZone);
            recordVO.setSettleTime(settlement);
        }

        // 投注额
        recordVO.setBetAmount(entity.getStake());
        // 已结算注单
//        BigDecimal validBetAmount;
//        if (recordVO.getSettleTime() != null) {
        // 输赢金额
        recordVO.setWinLossAmount(entity.getWinlost_amount());
        // 有效投注, 需要根据输赢金额进行计算
        BigDecimal validBetAmount = super.computerValidBetAmount(entity.getStake(), entity.getWinlost_amount(), VenueTypeEnum.SPORTS);
        recordVO.setValidAmount(validBetAmount);
        // 派彩金额
        recordVO.setPayoutAmount(recordVO.getWinLossAmount() != null ? recordVO.getBetAmount().add(recordVO.getWinLossAmount()) : null);
//        }
        // 注单状态
        recordVO.setOrderStatus(getSportStatus(entity.getTicket_status()));

        List<String> cancelStatusList = Lists.newArrayList(SBTicketStatusEnum.VOID.getCode(),
                SBTicketStatusEnum.REJECT.getCode(), SBTicketStatusEnum.REFUND.getCode());

        // 注单归类
        recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));


        Integer sportType = getSportType(entity);

        // 三方注单ID
        recordVO.setThirdOrderId(String.valueOf(entity.getTrans_id()));

        recordVO.setThirdGameCode(String.valueOf(sportType));
        recordVO.setGameName(String.valueOf(sportType));

        // 赔率
        recordVO.setOdds(entity.getOdds().toString());
        // 局号/期号
        if (entity.getMatch_id() != null) {
            recordVO.setGameNo(String.valueOf(entity.getMatch_id()));
        }

        //如果是串关则直接显示 -
//        if (SBBetTypeEnum.SBA_BET_TYPE_29.getCode().equals(entity.getBet_type())) {
//            recordVO.setGameNo("-");
//        }
        // 体育时时彩 | 比特币; 场次编号，当 sport_type = 169、175 时，显示字段
//            if (SBSportTypeEnum.Sport_169.getId().equals(entity.getSport_type()) ||
//                    SBSportTypeEnum.Sport_175.getId().equals(entity.getSport_type())) {
//                recordVO.setGameNo(String.valueOf(entity.getGame_id()));
//            }


        // 桌号 deskNo
        // 靴号 bootNo
        // 结果牌 / 结果 resultList
        if (VenueTypeEnum.ACELT.getCode().equals(sportType)) {
            if (SBTicketStatusEnum.WAITING.getCode().equals(entity.getTicket_status()) || SBTicketStatusEnum.RUNNING.getCode().equals(entity.getTicket_status())) {
                recordVO.setResultList("进行中");
            } else if (cancelStatusList.contains(entity.getTicket_status())) {
                recordVO.setResultList("已取消");
            } else {
                recordVO.setResultList(SBTicketStatusEnum.nameOfCode(entity.getTicket_status()));
            }
        }
//          结果牌 / 结果 resultList
        recordVO.setResultList(SBTicketStatusEnum.nameOfCode(entity.getTicket_status()));

        String betIp = userLoginInfoVO != null ? userLoginInfoVO.getIp() : "";
        Integer deviceType = userLoginInfoVO != null ? Integer.valueOf(userLoginInfoVO.getLoginTerminal()) : null;

        // 投注IP
        recordVO.setBetIp(betIp);
        // 币种
        recordVO.setCurrency(userInfoVO.getMainCurrency());
        // 设备类型
        recordVO.setDeviceType(deviceType);
        // 体育、电竞、彩票, 需要保存原始注单到串关字段中
        recordVO.setParlayInfo(entity.getOriginalParlayInfo());
        // 创建时间
        recordVO.setCreatedTime(System.currentTimeMillis());

        //联赛信息
        if (CollectionUtils.isNotEmpty(entity.getLeaguename())) {
            String leagueNames = entity.getLeaguename().stream()
                    .filter(langName -> "cs".equals(langName.getLang()))
                    .map(LangName::getName)
                    .collect(Collectors.joining("\n"));
            recordVO.setEventInfo(leagueNames);
        }


        //串关信息
        List<ParlayData> parlayData = entity.getParlayData();
//         代表是串关
        if (CollectionUtils.isNotEmpty(parlayData) && parlayData.size() > 1) {
            List<Integer> matchIdList = parlayData.stream().map(ParlayData::getMatch_id).toList();
            String result = matchIdList.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            recordVO.setGameNo(result);

            //联赛信息
            String leagueNames = parlayData.stream()
                    .flatMap(x -> x.getLeaguename().stream())
                    .filter(langName -> "cs".equals(langName.getLang()))
                    .map(LangName::getName)
                    .collect(Collectors.joining(","));
            recordVO.setEventInfo(leagueNames);
        }


        // 玩法 ADD by xiaozhi 注单展示需要
        Map<String, String> result = SBAOrderParseUtil.getNewOrderInfo(entity.getOriginalParlayInfo());

        String playInfo = result.get("playInfo");
        String orderInfo = result.get("orderInfo");

        Map<String, Object> jsonMap = JSONObject.parseObject(entity.getOriginalParlayInfo(), Map.class);
        playInfo = SBAOrderParseUtil.getBetTag(playInfo, jsonMap);
        orderInfo = SBAOrderParseUtil.getBetTag(orderInfo, jsonMap);

        recordVO.setPlayInfo(playInfo);
        recordVO.setOrderInfo(orderInfo);
        return recordVO;
    }


    /**
     * 解析注单数据
     */
    public List<OrderRecordVO> parseOrder(VenueInfoVO venueInfoVO, List<BetDetail> betVirtualSportDetails, Map<String, SiteVO> siteMap, String json) {
        List<OrderRecordVO> orderRecordList = Lists.newArrayList();
        List<BetDetail> betDetailEntityList = Lists.newArrayList();

        if (betVirtualSportDetails == null || betVirtualSportDetails.isEmpty()) {
            return orderRecordList;
        }

        for (BetDetail betDetailEntity : betVirtualSportDetails) {
            processTimeFields(betDetailEntity);
            // 如果是串关, 保留串关的数据结构
            if (SbaConstant.PARLAY_TYPE.equals(betDetailEntity.getBet_type())) {
                List<ParlayData> parlayDataList = betDetailEntity.getParlayData();
                betDetailEntity.setParlayData(parlayDataList);
            }
            List<ResettLementInfo> reSetInfoList = betDetailEntity.getResettlementinfo();
            betDetailEntity.setResettlementinfo(reSetInfoList);


            betDetailEntity.setOriginalParlayInfo(getBetDetailsByTransId(json, Long.parseLong(betDetailEntity.getTrans_id())));
            betDetailEntityList.add(betDetailEntity);
        }


        // 所有注单用户信息
        List<String> thirdUserName = betDetailEntityList.stream().map(BetDetail::getVendor_member_id).distinct().toList();

        if (CollectionUtils.isEmpty(thirdUserName)) {
            log.info("{} 拉单异常,没有用户信息", venueInfoVO.getVenueCode());
            return orderRecordList;
        }

        List<String> userIds = venueUserAccountConfig.getVenueUserAccountList(thirdUserName);

        if (CollectionUtils.isEmpty(userIds)) {
            log.info("{} 拉单异常,accountList", venueInfoVO.getVenueCode());
            return orderRecordList;
        }

        userIds = userIds.stream().distinct().toList();

        Map<String, UserInfoVO> userMap = super.getUserInfoByUserIds(userIds);
        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = super.getLoginInfoByUserIds(userIds);

        Integer gameTypeId = VenueTypeEnum.SPORTS.getCode();

        List<String> betIds = betDetailEntityList.stream().map(BetDetail::getTrans_id).toList();
        TransferRecordResultVO vo = TransferRecordResultVO.builder()
                .betIds(betIds)
                .venueCode(VenueEnum.SBA.getVenueCode())
                .build();
        List<TransferRecordResultVO> transferRecordList = transferRecordService.getTransferRecordList(vo);
        Map<String, TransferRecordResultVO> transferRecordMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(transferRecordList)) {
            transferRecordMap = transferRecordList.stream().collect(Collectors.toMap(TransferRecordResultVO::getBetId, Function.identity()));
        }

        for (BetDetail entity : betDetailEntityList) {
            String account = entity.getVendor_member_id();
            String userId = venueUserAccountConfig.getVenueUserAccount(account);
            UserInfoVO userInfoVO = userMap.get(userId);
            if (userInfoVO == null) {
                log.error("SBA玩家信息不存在，注单信息{}", entity);
                continue;
            }
            UserLoginInfoVO userLoginInfoVO = loginVOMap.get(userId);

            OrderRecordVO recordVO = populateOrderRecordVO(entity, userInfoVO, venueInfoVO, userLoginInfoVO, gameTypeId, siteMap);
            recordVO.setOrderId(OrderUtil.getGameNo());
            if (ObjectUtil.isNotEmpty(entity.getTrans_id())) {
                TransferRecordResultVO transferRecordResultVO = transferRecordMap.get(entity.getTrans_id());
                if (ObjectUtil.isNotEmpty(transferRecordResultVO)) {
                    recordVO.setTransactionId(transferRecordResultVO.getTransId());
                    recordVO.setThirdOrderId(transferRecordResultVO.getBetId());
                }
            }
            orderRecordList.add(recordVO);
        }

        return orderRecordList;
    }

    public Integer getSportType(BetDetail entity) {
        Integer sportType = entity.getSport_type(); // 体育种类
        if (sportType == null) {
            log.info("{} [getSportType] trans_id: {}, betType: {}, sportType: {}, gameType: {}", VenueEnum.SBA.getVenueName(),
                    entity.getTrans_id(), entity.getBet_type(), null, VenueTypeEnum.SPORTS.getCode());
            return VenueTypeEnum.SPORTS.getCode();
        }

        Integer gameType = SBSportTypeEnum.gameTypeOfId(entity.getSport_type());
        log.info("{} [getSportType] trans_id: {}, betType: {}, sportType: {}, gameType: {}", VenueEnum.SBA.getVenueName(), entity.getTrans_id(), entity.getBet_type(), entity.getSport_type(), gameType);
        return gameType;
    }

    public String formatTime(String settlementTime) {
        // 结算时间只取结算状态下的结算时间!!
        if (StringUtils.isNotBlank(settlementTime) && !settlementTime.contains(".")) {
            settlementTime = settlementTime + ".000";
        }
        return settlementTime;
    }

    private Integer winLossStatus(String status) {
        if (SBTicketStatusEnum.LOSE.getCode().equals(status) || SBTicketStatusEnum.HALF_LOSE.getCode().equals(status)) {//输
            return -1;
        } else if (SBTicketStatusEnum.WON.getCode().equals(status) || SBTicketStatusEnum.HALF_WON.getCode().equals(status)) {//赢
            return 1;
        } else if (SBTicketStatusEnum.DRAW.getCode().equals(status)) {//和局
            return 0;
        } else {
            return null;
        }
    }

    public Integer getSportStatus(String status) {
        List<String> settledStatusList = Lists.newArrayList(SBTicketStatusEnum.HALF_WON.getCode(),
                SBTicketStatusEnum.HALF_LOSE.getCode(), SBTicketStatusEnum.WON.getCode(),
                SBTicketStatusEnum.LOSE.getCode(), SBTicketStatusEnum.DRAW.getCode());

        if (SBTicketStatusEnum.WAITING.getCode().equals(status)) {
            return OrderStatusEnum.PRE_PROCESS.getCode();
        } else if (SBTicketStatusEnum.RUNNING.getCode().equals(status)) {
            return OrderStatusEnum.NOT_SETTLE.getCode();
        } else if (settledStatusList.contains(status)) {
            return OrderStatusEnum.SETTLED.getCode();
        } else {
            // reject(已取消): 在注单为 waiting 的状态下，玩家下注注金返回。可能状况很多。
            if (SBTicketStatusEnum.REJECT.getCode().equals(status)) {
                return OrderStatusEnum.MANUAL_CANCEL.getCode();
            }
            // void(作废): 在注单为 running 的状态下，玩家下注注金返回。原因可能为我们交易员对此场赛事有些疑虑。可与我们联系询问发生什么状况。
            // refund(退款): 在注单为 running 的状态下，玩家下注注金返回。原因有可能是赛事取消或发生什么意外。
            return OrderStatusEnum.CANCEL.getCode();
        }
    }

    /**
     * 拉取7天赛事数据
     */
    public void sbaPullGameEventsTask() {

        log.info("开始拉取7天赛事数据");

        VenueInfoVO venueDetailVO = venueInfoService.getAdminVenueInfoByVenueCode(VenueEnum.SBA.getVenueCode(), null);

        long startTime = System.currentTimeMillis();
        long endTime = TimeZoneUtils.adjustTimestamp(System.currentTimeMillis(), 8, sbaClientZone);
        String startTimeStr = TimeZoneUtils.convertTimestampToString(TimeZoneUtils.getStartOfDayInTimeZone(startTime, sbaClientZone), sbaClientZone, DateUtils.pattenT_Z);
        String endTimeStr = TimeZoneUtils.convertTimestampToString(TimeZoneUtils.getEndOfDayInTimeZone(endTime, sbaClientZone), sbaClientZone, DateUtils.pattenT_Z);


        CasinoMemberVO casinoMemberVO = new CasinoMemberVO();
        casinoMemberVO.setUserAccount(venueUserAccountConfig.getAnonymousAccount());
        casinoMemberVO.setVenueUserAccount(venueUserAccountConfig.getAnonymousAccount());
        ResponseVO<GameLoginVo> gameLoginVo = loginSBAGame(venueDetailVO.getMerchantNo(), venueDetailVO.getGameUrl(), casinoMemberVO);

        if (!gameLoginVo.isOk()) {
            log.info("调用失败...");
            return;
        }

        String token = gameLoginVo.getData().getSource();

        Map<String, String> header = Maps.newHashMap();
        header.put("Authorization", TokenConstants.PREFIX + token);

        List<SBSportTypeEnum> typeEnumList = Lists.newArrayList();
        typeEnumList.addAll(SBSportTypeEnum.footballSportTypeEnumList());
        typeEnumList.addAll(SBSportTypeEnum.basketballSportTypeEnumList());
        for (SBSportTypeEnum sbSportTypeEnum : typeEnumList) {
            Map<String, String> params = Maps.newHashMap();
            params.put("from", startTimeStr);
            params.put("until", endTimeStr);
            params.put("query", "$filter=sporttype eq " + sbSportTypeEnum.getId());
//            String param = "?from=" + startTimeStr + "&until=" + endTimeStr + "&query=$filter=sporttype eq " + sbSportTypeEnum.getId();
            String url = venueDetailVO.getGameUrl() + ThirdConstants.GET_EVENTS;
            try {
                String result = HttpClientHandler.get(url, header, params);
                if (result == null) {
                    return;
                }

                //这个接口没有状体码,所以这个地方只能这种方式验证,如果返回JsonObject 是有异常的
                if (!isValidJSONObject(result)) {
                    log.info("沙巴体育:获取赛事异常,result:{},param:{}", result, params);
                    continue;
                } else if (isValidEmptyJSONArray(result)) {
                    log.info("沙巴体育:获取赛事异常,没数据");
                    continue;
                }

                JSONObject dataJson = JSONObject.parseObject(result);

                String events = dataJson.getString("events");
                List<SBAEventsInfo> infoList = JSONArray.parseArray(events, SBAEventsInfo.class);
                if (CollectionUtil.isEmpty(infoList)) {
                    log.info("沙巴体育:获取赛事,没数据,params:{}", params);
                    continue;
                }
                List<SBAEventsInfo> addInfoList = infoList.stream()
                        .filter(eventsInfo -> {
                            boolean isValid = eventsInfo.validate() && eventsInfo.teamInfoValidate();
                            if (!isValid) {
                                log.info("赛事缺少参数:{},params:{}", eventsInfo, params);
                            }
                            return isValid;
                        })
                        .collect(Collectors.toList());
                sportEventsService.addEventsRecommend(addInfoList);
            } catch (Exception e) {
                log.error("获取赛事异常,params:{}:", params, e);
            }
        }
        RedisUtil.deleteKeysByPattern(RedisConstants.getWildcardsKey(RedisConstants.KEY_QUERY_SPORT_EVENTS_RECOMMEND_ALL));
    }


    private String getOrderClientUrl(OrderRecordVenueClientReqVO reqVO, VenueInfoVO venueInfoVO, Boolean isSettled) {
        String startTime = DateUtils.formatDateByZoneId(reqVO.getBetStartTime(), DateUtils.pattenT, sbaClientZone);
        String endTime = DateUtils.formatDateByZoneId(reqVO.getBetEndTime(), DateUtils.pattenT, sbaClientZone);
        String apiName = String.format(ThirdConstants.SBA_CLIENT_ORDER_DETAIL_URL, startTime, endTime, isSettled, SBLanguageEnum.getPlatLangCodeForm(reqVO.getLang()));
        return venueInfoVO.getGameUrl() + apiName;
    }

    private String getTeamInfo(String homeTeamName, String AwayTeamName) {
        if (ObjectUtil.isNotEmpty(homeTeamName) && ObjectUtil.isNotEmpty(AwayTeamName)) {
            return homeTeamName + " VS " + AwayTeamName;
        }
        return null;
    }


    public ResponseVO<OrderRecordClientRespVO> orderClientQuery(OrderRecordVenueClientReqVO reqVO, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {

        OrderRecordClientRespVO respVO = new OrderRecordClientRespVO();

        List<EventOrderClientResVO> sabOrderList = Lists.newArrayList();

        String userId = CurrReqUtils.getOneId();

        String userAccount = CurrReqUtils.getAccount();
        //状态参数
        Boolean isSettled = settleStatus(reqVO);

        //开始时间
        if (ObjectUtil.isEmpty(reqVO.getBetStartTime())) {
            reqVO.setBetStartTime(TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), sbaClientZone));
        }

        //结束时间
        if (ObjectUtil.isEmpty(reqVO.getBetEndTime())) {
            reqVO.setBetEndTime(TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), sbaClientZone));
        }


        //调用三方查询前先查询一下该用户在这时间段是否有下注.如果没有直接不调用三方
        Long count = transferRecordService.getBaseMapper().selectCount(Wrappers.lambdaQuery(TransferRecordPO.class)
                .eq(TransferRecordPO::getVenueCode, VenueEnum.SBA.getVenueCode())
                .ge(ObjectUtil.isNotEmpty(reqVO.getBetStartTime()), TransferRecordPO::getCreatedTime, reqVO.getBetStartTime())
                .le(ObjectUtil.isNotEmpty(reqVO.getBetEndTime()), TransferRecordPO::getCreatedTime, reqVO.getBetEndTime())
                .eq(TransferRecordPO::getUserAccount, venueUserAccountConfig.addVenueUserAccountPrefix(userId))
        );
        if (count <= 0) {
            return ResponseVO.success(respVO);
        }


        String key = String.format(RedisConstants.THREE_GAME_TOKEN, VenueEnum.SBA.getVenueCode(), userId);
        String token = RedisUtil.getValue(key);
        if (ObjectUtil.isEmpty(token)) {
            LoginVO loginVO = new LoginVO();
            loginVO.setVenueCode(VenueEnum.SBA.getVenueCode());
            loginVO.setUserId(userId);
            loginVO.setUserAccount(userAccount);
            ResponseVO<GameLoginVo> loginVoResponseVO = gamePlayService.loginGame(loginVO);
            if (!loginVoResponseVO.isOk()) {
                log.error("查询沙巴注单,用户未登录,手动调用登陆异常-1");
                return ResponseVO.success(respVO);
            }

            GameLoginVo gameLoginVo = loginVoResponseVO.getData();
            if (ObjectUtil.isEmpty(gameLoginVo) || ObjectUtil.isEmpty(gameLoginVo.getSource())) {
                log.error("查询沙巴注单,用户未登录,手动调用登陆异常-2");
                return ResponseVO.success(respVO);
            }
            token = gameLoginVo.getSource();
        }

        reqVO.setSabToken(token);

        //没有传状态 查询所有状态集合
        if (isSettled == null) {
            //已结算
            List<EventOrderClientResVO> settledList = getOrderDetailList(reqVO, venueInfoVO, true);
            if (CollectionUtils.isNotEmpty(settledList)) {
                sabOrderList.addAll(settledList);
            }

            //未结算
            List<EventOrderClientResVO> unSettledList = getOrderDetailList(reqVO, venueInfoVO, false);
            if (CollectionUtils.isNotEmpty(unSettledList)) {
                sabOrderList.addAll(unSettledList);
            }
        } else {//查指定状态
            List<EventOrderClientResVO> settledList = getOrderDetailList(reqVO, venueInfoVO, isSettled);
            if (CollectionUtils.isNotEmpty(settledList)) {
                sabOrderList.addAll(settledList);
            }
        }

        if (CollectionUtil.isNotEmpty(reqVO.getOrderClassifyList())) {
            //沙巴订单不能查询取消的单
            Integer i = reqVO.getOrderClassifyList().get(0);
            ClassifyEnum classifyEnum = ClassifyEnum.nameOfCode(i);
            //已取消的需要手动过滤
            if (ClassifyEnum.CANCEL.getCode().equals(classifyEnum.getCode())
                    || ClassifyEnum.SETTLED.getCode().equals(classifyEnum.getCode())) {
                List<EventOrderClientResVO> resultList = Lists.newArrayList();
                for (EventOrderClientResVO item : sabOrderList) {
                    if (ObjectUtil.isNotEmpty(item.getOrderClassify()) && classifyEnum.getCode().equals(item.getOrderClassify())) {
                        EventOrderClientResVO ev = new EventOrderClientResVO();
                        BeanUtil.copyProperties(item, ev);
                        resultList.add(ev);
                    }
                }

                if (CollectionUtil.isNotEmpty(resultList)) {
                    resultList.sort((o1, o2) -> {
                        if (o1 == null && o2 == null) return 0;
                        if (o1 == null || o1.getBetTime() == null) return 1; // o1 为 null，排在后面
                        if (o2 == null || o2.getBetTime() == null) return -1; // o2 为 null，排在后面
                        return Long.compare(o2.getBetTime(), o1.getBetTime());
                    });
                }
                respVO.setSabOrderList(resultList);
                return ResponseVO.success(respVO);
            }
        }

        if (CollectionUtil.isNotEmpty(sabOrderList)) {
            sabOrderList.sort((o1, o2) -> {
                if (o1 == null && o2 == null) return 0;
                if (o1 == null || o1.getBetTime() == null) return 1; // o1 为 null，排在后面
                if (o2 == null || o2.getBetTime() == null) return -1; // o2 为 null，排在后面
                return Long.compare(o2.getBetTime(), o1.getBetTime());
            });
        }


        respVO.setSabOrderList(sabOrderList);
        return ResponseVO.success(respVO);
    }


    private Boolean settleStatus(OrderRecordVenueClientReqVO resVO) {
        if (CollectionUtil.isNotEmpty(resVO.getOrderClassifyList())) {
            // 仅支持 结算与未结算筛选
            Integer i = resVO.getOrderClassifyList().get(0);
            ClassifyEnum classifyEnum = ClassifyEnum.nameOfCode(i);
            switch (classifyEnum) {
                case SETTLED -> {
                    return true;
                }
                case NOT_SETTLE -> {
                    return false;
                }
            }
        }
        return null;
    }



    /**
     * 获取沙巴注单信息
     *
     * @param reqVO
     * @param venueInfoVO
     * @param isSettled
     * @return
     */
    private List<EventOrderClientResVO> getOrderDetailList(OrderRecordVenueClientReqVO reqVO, VenueInfoVO venueInfoVO, Boolean isSettled) {
        List<EventOrderClientResVO> reslutList = Lists.newArrayList();

        try {

            String token = reqVO.getSabToken();

            //已结算
            String url = getOrderClientUrl(reqVO, venueInfoVO, isSettled);

            Map<String, String> header = Maps.newHashMap();

            header.put("Authorization", TokenConstants.PREFIX + token);

            Map<String, String> params = Maps.newHashMap();

            String result = HttpClientHandler.get(url, header, params);
            if (ObjectUtil.isEmpty(result)) {
                log.info("沙巴体育:获取注单详情异常,三方未获取到");
                return List.of();
            }


            //这个接口没有状体码,所以这个地方只能这种方式验证,如果返回JsonObject 是有异常的
            if (isValidEmptyJSONArray(result)) {
                log.info("沙巴体育:没数据");
                return List.of();
            }


            List<ClientBetDetail> infoList = JSONArray.parseArray(result, ClientBetDetail.class);
            if (CollectionUtils.isEmpty(infoList)) {
                log.info("沙巴体育:获取注单详情异常,解析异常");
                return List.of();
            }

            List<String> orderIdList = infoList.stream().map(ClientBetDetail::getTransId).toList();

//            TransferRecordResultVO recordResultVO = TransferRecordResultVO.builder()
//                    .betIds(orderIdList)
//                    .venueCode(VenueEnum.SBA.getVenueCode())
//                    .build();
//            List<TransferRecordResultVO> transferRecordList = transferRecordService.getTransferRecordList(recordResultVO);
//            Map<String, TransferRecordResultVO> transferRecordMap = Maps.newHashMap();
//            if (CollectionUtils.isNotEmpty(transferRecordList)) {
//                transferRecordMap = transferRecordList.stream().collect(Collectors.toMap(TransferRecordResultVO::getBetId, Function.identity()));
//            }

            LambdaEsQueryWrapper<OrderRecordPO> wrapper = new LambdaEsQueryWrapper<>();
            wrapper.in(OrderRecordPO::getThirdOrderId,orderIdList);
            List<OrderRecordPO> orderList = orderRecordEsMapper.selectList(wrapper);

            Map<String, OrderRecordPO> orderMap = orderList.stream().collect(Collectors.toMap(OrderRecordPO::getThirdOrderId, Function.identity()));

//            Map<String, TransferRecordResultVO> finalTransferRecordMap = transferRecordMap;

            String venueName = venueInfoService.getVenueNameCodeBySiteCodeVenueCode(CurrReqUtils.getSiteCode(), VenueEnum.SBA.getVenueCode());
            for (ClientBetDetail detail : infoList) {

                String betContent = detail.getBetTypeName() + " " + detail.getKeyName();
                EventOrderClientResVO eventOrderClientResVO = EventOrderClientResVO.builder()
                        .venueName(venueName)
//                        .orderId(transferRecordResultVO == null ? null : transferRecordResultVO.getTransId())
                        .eventInfo(detail.getLeagueName())
                        .teamInfo(getTeamInfo(detail.getHomeTeamName(), detail.getAwayTeamName()))
                        .betAmount(detail.getStake())
                        .odds(ObjectUtil.isNotEmpty(detail.getPrice()) ? detail.getPrice().setScale(2, RoundingMode.HALF_UP).toString() : null)
                        .multipleBet(detail.getIsLucky())
                        .data(JSONObject.parseObject(JSON.toJSONString(detail)))
                        .build();


                //三分让分投注
                //有效投注
                OrderRecordPO orderRecordPO = orderMap.get(detail.getTransId());
                if(orderRecordPO != null){
                   String data = orderRecordPO.getParlayInfo();
                    Map<String, Object> parlayMap = JSON.parseObject(data);
                   if(parlayMap.containsKey("bet_type")){
                       Integer betType = (Integer) parlayMap.get("bet_type");
                       if(SBBetTypeEnum.getThreeHandicap().contains(betType)){
                           String key = I18MsgKeyEnum.SBA_BET_TEAM_NAME.getCode() + betType;
                           List<CodeValueVO> list = systemDictCache.getSystemParamByType(key);
                           String betTeamName = SBAOrderParseUtil.getBetTeamName(list, parlayMap);
                           betContent += " "+SBAOrderParseUtil.replaceBetName(betType, betTeamName, parlayMap);
                       }
                   }

                    eventOrderClientResVO.setValidAmount(orderRecordPO.getValidAmount());
                    eventOrderClientResVO.setBetContent(betContent);
                    eventOrderClientResVO.setOrderId(orderRecordPO.getOrderId());
                }

                //投注时间
                if (ObjectUtil.isNotEmpty(detail.getTransTime())) {
                    eventOrderClientResVO.setBetTime(TimeZoneUtils.convertTimeZone(detail.getTransTime(), sbaClientZone, CurrReqUtils.getTimezone()));
                }

                //注单结算状态
                if (ObjectUtil.isNotEmpty(detail.getStatus())) {
                    Integer status = getSportStatus(detail.getStatus());
                    //只有结算的才会有输赢金额
                    if (OrderStatusEnum.SETTLED.getCode().equals(status)) {
                        eventOrderClientResVO.setWinLossAmount(detail.getSettlementPrice());
                    }
                    eventOrderClientResVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(status));
                }

                //串关信息
                List<ClientParlayInfo> parlayInfo = detail.getParlayInfo();
                if (CollectionUtils.isNotEmpty(parlayInfo)) {
                    eventOrderClientResVO.setMultipleBet(true);
                    eventOrderClientResVO.setEventInfo(I18nMessageUtil.getI18NMessageInAdvice(I18nInitConstant.BIZ_ORDER_MUTIL_DESC));
                    List<OrderMultipleBetVO> orderMultipleBetList = parlayInfo.stream().map(x -> {

                        OrderMultipleBetVO vo = new OrderMultipleBetVO();
                        vo.setOrderId(String.valueOf(x.getParlayId()));//Parlay注单号码
                        vo.setEventInfo(x.getLeagueName());//联赛名称
                        vo.setTeamInfo(getTeamInfo(x.getHomeTeamName(), x.getAwayTeamName()));
                        vo.setBetContent(x.getBetTypeName() + " " + x.getKeyName());//投注内容
                        vo.setOdds(ObjUtil.isNotNull(x.getPrice()) ? x.getPrice().setScale(2, RoundingMode.HALF_UP).toString() : null);//赔率
                        vo.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(getSportStatus(x.getStatus())));//注单状态
                        vo.setWinlossStatus(winLossStatus(x.getStatus()));
                        return vo;
                    }).toList();
                    eventOrderClientResVO.setOrderMultipleBetList(orderMultipleBetList);
                }
                reslutList.add(eventOrderClientResVO);
            }

        } catch (Exception e) {
            log.error("调用沙巴注单记录异常", e);
        }
        return reslutList;
    }


    private static boolean isValidJSONObject(String jsonString) {
        try {
            JSONObject.parseObject(jsonString);
            return true;
        } catch (JSONException e) {
            log.info("", e);
            return false;
        }
    }

    private static boolean isValidEmptyJSONArray(String jsonString) {
        return "[]".equals(jsonString);
    }


    public static String getBetDetailsByTransId(String jsonString, long transId) {
        // 将输入的 JSON 字符串解析成 JSONObject
        JSONObject jsonObject = JSON.parseObject(jsonString);

        // 获取 Data 部分的 JSONObject
        JSONObject data = jsonObject.getJSONObject("Data");

        // 获取 BetDetails 数组
        JSONArray betDetails = data.getJSONArray("BetDetails");

        // 遍历数组寻找匹配的 trans_id
        for (int i = 0; i < betDetails.size(); i++) {
            JSONObject betDetail = betDetails.getJSONObject(i);
            if (betDetail.getLong("trans_id") == transId) {

                //如果这个字段为null 则默认给一个 ture值.代表正常赛事
                if (betDetail.containsKey("game_group") && betDetail.get("game_group") == null) {
                    betDetail.put("game_group", "true");
                }
                // 找到对应的 trans_id 返回该对象的字符串表示
                return betDetail.toString();
            }
        }

        // 如果没有找到对应的 trans_id，返回 null 或适当的值
        return null;
    }
//    public static void main(String[] args) {
//        String j = "{\"error_code\":0,\"message\":\"\",\"Data\":{\"last_version_key\":165987,\"BetDetails\":[{\"trans_id\":371953260283559997,\"vendor_member_id\":\"Utest_60366859\",\"operator_id\":\"okok\",\"league_id\":247,\"leaguename\":[{\"lang\":\"en\",\"name\":\"CHINA FOOTBALL SUPER LEAGUE\"},{\"lang\":\"cs\",\"name\":\"中国超级联赛\"}],\"match_id\":102678162,\"home_id\":745637,\"hometeamname\":[{\"lang\":\"en\",\"name\":\"Tianjin Jinmen Tiger\"},{\"lang\":\"cs\",\"name\":\"天津津门虎\"}],\"away_id\":250865,\"awayteamname\":[{\"lang\":\"en\",\"name\":\"Qingdao Hainiu\"},{\"lang\":\"cs\",\"name\":\"青岛海牛\"}],\"match_datetime\":\"2025-03-29T03:30:00\",\"game_group\":null,\"sport_type\":1,\"sportname\":[{\"lang\":\"en\",\"name\":\"Soccer\"},{\"lang\":\"cs\",\"name\":\"足球\"}],\"bet_type\":1,\"bettypename\":[{\"lang\":\"en\",\"name\":\"Handicap\"},{\"lang\":\"cs\",\"name\":\"让球\"}],\"parlay_ref_no\":0,\"odds\":1.96,\"stake\":74.0000,\"transaction_time\":\"2025-03-29T04:08:37.743\",\"ticket_status\":\"running\",\"winlost_amount\":-74.0000,\"after_amount\":-74.0000,\"currency\":20,\"winlost_datetime\":\"2025-03-29T00:00:00\",\"odds_type\":3,\"bet_team\":\"h\",\"betteamname\":[{\"lang\":\"en\",\"name\":\"Tianjin Jinmen Tiger\"},{\"lang\":\"cs\",\"name\":\"天津津门虎\"}],\"isLucky\":\"False\",\"home_hdp\":0.5000,\"away_hdp\":0.0000,\"hdp\":-0.50,\"betfrom\":\"^\",\"islive\":\"1\",\"home_score\":1,\"away_score\":0,\"settlement_time\":null,\"customInfo1\":\"\",\"customInfo2\":\"\",\"customInfo3\":\"\",\"customInfo4\":\"\",\"customInfo5\":\"\",\"ba_status\":\"0\",\"version_key\":165987,\"ParlayData\":null,\"risklevelname\":\"New\",\"risklevelnamecs\":\"新玩家\",\"actual_amount\":74.0000}]}}\n";
//
//        System.err.println(getBetDetailsByTransId(j,371953260283559997L));
//    }


    /**
     * 获取沙巴体育联赛
     */
    public void sbaPullEventInfo() {
        betCoinJoinService.delete();
        log.info("获取沙巴体育联赛");
        List<VenueInfoVO> list = venueInfoService.getAdminVenueInfoByVenueCodeList(VenueEnum.SBA.getVenueCode());

        if (CollectionUtil.isEmpty(list)) {
            return;
        }

        VenueInfoVO venueDetailVO = list.get(0);

        String url = venueDetailVO.getApiUrl() + ThirdConstants.SBA_SELECTION_INFO;

        Map<String, String> paramMap = new HashMap<>();

        paramMap.put("vendor_id", venueDetailVO.getMerchantNo());

        paramMap.put("type", "1");

        Long lastVersionKey = RedisUtil.getValue(RedisConstants.SBA_EVENT_INFO_LAST_VERSION_KEY);

        //第一次获取
        if (lastVersionKey == null) {
            lastVersionKey = 0L;
        }

        int max = 0;
        while (true) {
            log.info("sbaPullEventInfo,进入获取沙巴体育联赛:{}", max);
            //最大调用次数
            if (max == 200) {
                log.info("sbaPullEventInfo:{} 联赛获取超过最大次数", VenueEnum.SBA.getVenueName());
                return;
            }
            paramMap.put("version_key", lastVersionKey.toString());
            String response = HttpClientHandler.post(url, paramMap);
            if (ObjectUtil.isEmpty(response)) {
                return;
            }

            try {

                JSONObject jsonObject = JSONObject.parseObject(response);
                if (jsonObject == null || !SbaConstant.SUCC_CODE.equals(jsonObject.getInteger("error_code"))) {
                    log.info("sbaPullEventInfo:{} 联赛获取失败", VenueEnum.SBA.getVenueName());
                    return;
                }

                JSONObject dataJson = jsonObject.getJSONObject("Data");
                Long sbaVersionKey = dataJson.getLong("last_version_key");
                if (sbaVersionKey == null || sbaVersionKey == 0L) {
                    log.info("sbaPullEventInfo:{} 联赛获取失败,获取版本号异常:{}", VenueEnum.SBA.getVenueName(), sbaVersionKey);
                    return;
                }


                if (lastVersionKey.equals(sbaVersionKey)) {
                    log.info("sbaPullEventInfo:{} 联赛获取结束,获取时版本号:{},结束时版本号:{}", VenueEnum.SBA.getVenueName(), lastVersionKey, sbaVersionKey);
                    return;
                }

                JSONArray betDetails = dataJson.getJSONArray("league");


                List<SportEventsInfoPO> sportEventsInfoPOS = Lists.newArrayList();
                for (int i = 0; i < betDetails.size(); i++) {
                    LeagueInfoVO leagueInfo = JSON.parseObject(betDetails.getString(i), LeagueInfoVO.class);


                    String leagueNameZhcn = leagueInfo.getLeagueNameZhcn();
                    if (StringUtils.isBlank(leagueNameZhcn)) {
                        continue;
                    }


                    SportEventsInfoPO sportEventsInfoPO = SportEventsInfoPO
                            .builder()
                            .build();
                    BeanUtil.copyProperties(leagueInfo, sportEventsInfoPO);
                    sportEventsInfoPO.setLeagueNameZhCn(leagueNameZhcn);
                    sportEventsInfoPO.setVenueCode(VenueEnum.SBA.getVenueCode());
                    sportEventsInfoPOS.add(sportEventsInfoPO);
                }
                if (CollectionUtil.isNotEmpty(sportEventsInfoPOS)) {
                    List<String> leagueIdList = sportEventsInfoPOS.stream().map(SportEventsInfoPO::getLeagueId).toList();

                    List<SportEventsInfoPO> dbList = sportEventsInfoService.getBaseMapper().selectList(Wrappers.lambdaQuery(SportEventsInfoPO.class)
                            .in(SportEventsInfoPO::getLeagueId, leagueIdList)
                            .select(SportEventsInfoPO::getLeagueId));
                    if (CollectionUtil.isNotEmpty(dbList)) {
                        List<String> dbLeagueIdList = dbList.stream().map(SportEventsInfoPO::getLeagueId).toList();
                        sportEventsInfoPOS = sportEventsInfoPOS.stream().filter(x -> !dbLeagueIdList.contains(x.getLeagueId())).toList();
                    }
                    if (CollectionUtil.isNotEmpty(sportEventsInfoPOS)) {
                        sportEventsInfoService.saveBatch(sportEventsInfoPOS, sportEventsInfoPOS.size());
                    }

                }

                lastVersionKey = sbaVersionKey;
            } catch (Exception e) {
                log.error("获取联赛异常,版本号:{}", lastVersionKey, e);
                return;
            }
            RedisUtil.setValue(RedisConstants.SBA_EVENT_INFO_LAST_VERSION_KEY, lastVersionKey);
            max++;
        }

    }

}
