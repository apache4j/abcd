package com.cloud.baowang.play.game.cmd.impl;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.order.client.OrderMultipleBetVO;
import com.cloud.baowang.play.api.vo.order.client.SportOrderClientResVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.VenueOrderInfoService;
import com.cloud.baowang.play.game.cmd.enums.*;
import com.cloud.baowang.play.game.cmd.response.CmdWebCollusionReqVO;
import com.cloud.baowang.play.game.cmd.response.CmdWebResponseVO;
import com.cloud.baowang.play.game.cmd.utils.CmdOrderParseUtil;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.play.service.VenueInfoService;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@AllArgsConstructor
@Service(ServiceType.GAME_INFO_THIRD_API_SERVICE + VenuePlatformConstants.CMD)
public class CmdOrderInfoServiceImpl implements VenueOrderInfoService {

    private final CmdServiceImpl cmdServiceImpl;
    private final VenueInfoService venueInfoService;
//    private final CmdCryptoConfig cmdCryptoConfig;


    @Override
    public String getGameVenueCode() {
        return VenuePlatformConstants.CMD;
    }

    @Override
    public List<Map<String, Object>> getOrderInfo(Map<String, Object> parlayMap) {
        List<Map<String, Object>> resultList = Lists.newArrayList();
//        VenueInfoVO venueInfoVO = cmdCryptoConfig.getVenueInfoVO();
        VenueInfoVO venueInfoVO = venueInfoService.getVenueInfoByVenueCode(VenueEnum.CMD.getVenueCode());
        //投注类型
        CmdBetTypeEnum cmdBetTypeEnum = CmdBetTypeEnum.of((String) parlayMap.get("transType"),(Boolean)parlayMap.get("isFirstHalf"));
        if (CmdBetTypeEnum.SBA_BET_TYPE_PAR.getCode().equals(cmdBetTypeEnum.getCode())){
            Gson gson = new Gson();
            String json = gson.toJson(parlayMap);
            CmdWebResponseVO betDetailEntity = gson.fromJson(json, CmdWebResponseVO.class);
            if (CmdBetTypeEnum.SBA_BET_TYPE_PAR.getCode().equals(cmdBetTypeEnum.getCode())){
                for (CmdWebCollusionReqVO vo:betDetailEntity.getCollusionBetDetails()) {
                    Map<String, Object> sportsMap = Maps.newHashMap();
                    //串关单有ParTransType
                    CmdBetTypeEnum cmdCollusionBetTypeEnum=CmdBetTypeEnum.of(vo.getParTransType(),vo.getIsFH());
                    // 全场半场
                    String half= this.isHalf(vo.getIsFH(),CMDLangCodeEnum.ZH_CN.getCode());
                    //主队
                    String homeTeamName = cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type0.getCode(), vo.getHomeId(),null);
                    //客队
                    String awayTeamName = cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type0.getCode(), vo.getAwayId(),null);
                    //盘口
                    String oddTypeName = CmdOddTypeEnum.of((String)parlayMap.get("oddsType")).getDescCn();
                    //赔率
                    BigDecimal odds= vo.getParOdds();
                    String gameNo=vo.getMatchId().toString();
                    //联赛名字
                    String leagueNames=cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type1.getCode(),vo.getLeagueId(),null);
                    //下注队伍名称
                    String teamName=vo.getIsBetHome()?homeTeamName:awayTeamName;
                    //投注信息
                    String betInfo=getBetInfo(cmdCollusionBetTypeEnum,vo.getChoice(),teamName,vo.getHdp().toString(),null);
                    //滚球
                    String live=CmdLiveEnums.nameOfCode(vo.getIsRun()).getDesc();
                    // 比赛详情
                    String matchInfo = CmdOrderParseUtil.getSimpleMatchInfo(leagueNames,homeTeamName,awayTeamName,gameNo+"");
                    sportsMap.put("matchInfo", matchInfo);
                    // 盘口详情
                    String marketInfo = CmdOrderParseUtil.getMarketInfo(cmdCollusionBetTypeEnum,live,half);
                    sportsMap.put("marketInfo", marketInfo);
                    // 注单详情
                    sportsMap.put("orderInfo", CmdOrderParseUtil.getOrderInfo(teamName,cmdCollusionBetTypeEnum.getDescCn(),betInfo,odds.toString(),oddTypeName));
                    Long settleTime =null;
                    CmdOrderStatusEnum cmdOrderStatusEnum=CmdOrderParseUtil.getSportStatus(betDetailEntity.getStatusChange(),vo.getParDangerStatus(),vo.getParStatus());
                    if(!cmdOrderStatusEnum.getCode().equals(CmdOrderStatusEnum.P.getCode())){
                        // 结算时间
                        sportsMap.put("settleTime", String.valueOf(settleTime));
                    }
                    // 注单结果
                    String orderResult = CmdOrderParseUtil.getOrderResult(betDetailEntity.getStatusChange(),vo.getParDangerStatus(),vo.getParStatus());
                    sportsMap.put("orderResult", orderResult);
                    resultList.add(sportsMap);
                }
            }
        }else{
            Map<String, Object> sportsMap = Maps.newHashMap();
            // 全场半场
            String half= this.isHalf((Boolean)parlayMap.get("isFirstHalf"),CMDLangCodeEnum.ZH_CN.getCode());
            //主队
            String homeTeamName = cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type0.getCode(), (Integer)parlayMap.get("homeTeamId"),null);
            //客队
            String awayTeamName = cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type0.getCode(), (Integer)parlayMap.get("awayTeamId"),null);
            //盘口
            String oddTypeName = CmdOddTypeEnum.of((String)parlayMap.get("oddsType")).getDescCn();
            //赔率
            BigDecimal odds = (BigDecimal)parlayMap.get("odds");
            Integer gameNo= (Integer)parlayMap.get("matchID");
            //联赛信息
            String leagueNames = cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type1.getCode(),(Integer)parlayMap.get("leagueId"),null);
            String teamName=(Boolean)parlayMap.get("isBetHome")?homeTeamName:awayTeamName;
            //投注信息
            String betInfo=getBetInfo(cmdBetTypeEnum,(String) parlayMap.get("choice"),teamName,parlayMap.get("hdp").toString(),null);
            // 比赛详情
            String matchInfo = CmdOrderParseUtil.getSimpleMatchInfo(leagueNames,homeTeamName,awayTeamName,gameNo+"");
            sportsMap.put("matchInfo", matchInfo);
            // 盘口详情
            String marketInfo = CmdOrderParseUtil.getMarketInfo(cmdBetTypeEnum,CmdLiveEnums.nameOfCode((Boolean) parlayMap.get("isRunning")).getDesc(),half);
            sportsMap.put("marketInfo", marketInfo);
            // 注单详情
            sportsMap.put("orderInfo", CmdOrderParseUtil.getOrderInfo(teamName,cmdBetTypeEnum.getDescCn(),betInfo,odds.toString(),oddTypeName));
            Long settleTime = cmdServiceImpl.getChangeDate((Long)parlayMap.get("stateUpdateTs"));
            // 注单结果
            Integer StatusChange= (Integer)parlayMap.get("statusChange");
            String dangerStatus= (String)parlayMap.get("dangerStatus");
            String winLoseStatus= (String)parlayMap.get("winLoseStatus");
            CmdOrderStatusEnum cmdOrderStatusEnum=CmdOrderParseUtil.getSportStatus(StatusChange,dangerStatus,winLoseStatus);
            if(!cmdOrderStatusEnum.getCode().equals(CmdOrderStatusEnum.P.getCode())){
                // 结算时间
                sportsMap.put("settleTime", String.valueOf(settleTime));
            }
            String orderResult = CmdOrderParseUtil.getOrderResult(StatusChange,dangerStatus,winLoseStatus);
            sportsMap.put("orderResult", orderResult);
            resultList.add(sportsMap);
        }
        return resultList;
    }

    public String getOrderRecordInfo(OrderRecordPO recordPO) {
        return footballRecordInfo(recordPO);
    }

    private String footballRecordInfo(OrderRecordPO recordPO) {
        StringBuilder stringBuilder = new StringBuilder();
//        VenueInfoVO venueInfoVO = cmdCryptoConfig.getVenueInfoVO();
        VenueInfoVO venueInfoVO = venueInfoService.getVenueInfoByVenueCode(VenueEnum.CMD.getVenueCode());
        try {
            CmdWebResponseVO betDetailEntity = JSONObject.parseObject(recordPO.getParlayInfo(), CmdWebResponseVO.class);
            Long betTime = recordPO.getBetTime();
            //盘口
            String oddTypeName = " ["+CmdOddTypeEnum.of((String)betDetailEntity.getOddsType()).getDescCn() +"]";
            CmdSportTypeEnum sportTypeEnum = CmdSportTypeEnum.of(betDetailEntity.getSportType());
            CmdBetTypeEnum cmdBetTypeEnum = CmdBetTypeEnum.of(betDetailEntity.getTransType(),betDetailEntity.getIsFirstHalf());
            if (CmdBetTypeEnum.SBA_BET_TYPE_PAR.getCode().equals(cmdBetTypeEnum.getCode())){
                for (CmdWebCollusionReqVO vo:betDetailEntity.getCollusionBetDetails()) {
                    CmdSportTypeEnum collusionSportTypeEnum = CmdSportTypeEnum.of(vo.getSportType());
                    if (ObjectUtils.isNotEmpty(sportTypeEnum)) {
                        stringBuilder.append(collusionSportTypeEnum.getDescCn());
                    }
                    // 串关单有ParTransType
                    CmdBetTypeEnum cmdCollusionBetTypeEnum=CmdBetTypeEnum.of(vo.getParTransType(),vo.getIsFH());
                    String half=this.isHalf(vo.getIsFH(),CMDLangCodeEnum.ZH_CN.getCode());
                    String betContent=ObjectUtils.isNotEmpty(cmdCollusionBetTypeEnum)?cmdCollusionBetTypeEnum.getDescCn():"";
                    // 下注队,主队还是客队信息
                    String homeTeamName = cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type0.getCode(), vo.getHomeId(),null);
                    String awayTeamName = cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type0.getCode(), vo.getAwayId(),null);
                    String betTeamName=vo.getIsBetHome()?  homeTeamName:awayTeamName;
                    String gameNo=vo.getMatchId().toString();
                    //联赛名字
                    String leagueNames=cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type1.getCode(),vo.getLeagueId(),null);
                    //单个赔率
                    BigDecimal odds= vo.getParOdds();
                    //下注项目
                    String betInfo =getBetInfo(cmdCollusionBetTypeEnum,vo.getChoice(),betTeamName,vo.getHdp().toString(),null);
                    stringBuilder.append("(")
                            .append(TimeZoneUtils.convertTimestampToString(betTime, CurrReqUtils.getTimezone(), TimeZoneUtils.patten_yyyyMMddHHmmss))
                            .append(")")
                            .append("\n");
                    stringBuilder.append(leagueNames);
                    stringBuilder.append("\n").append(homeTeamName)
                            .append(" VS ")
                            .append(awayTeamName)
                            .append("(")
                            .append("赛事ID: ")
                            .append(gameNo)
                            .append(")");
                    String result = "<span style=\"color: red;\">" + betInfo +"@"+odds + oddTypeName +"</span>";
                    stringBuilder.append("\n").append(result);
                    String sbLive = CmdLiveEnums.nameOfCode(vo.getIsRun()).getDesc();
                    stringBuilder.append("\n").append(sbLive);
                    stringBuilder.append(" 玩法:").append(half).append(" ").append(betContent).append(" ");
                    stringBuilder.append("\n").append("全场比分:").append(vo.getFTScore()).append("\n");
                }
            }else{
                if (ObjectUtils.isNotEmpty(sportTypeEnum)) {
                    stringBuilder.append(sportTypeEnum.getDescCn());
                }
                stringBuilder.append("(")
                        .append(TimeZoneUtils.convertTimestampToString(betTime, CurrReqUtils.getTimezone(), TimeZoneUtils.patten_yyyyMMddHHmmss))
                        .append(")")
                        .append("\n");
                stringBuilder.append(recordPO.getEventInfo());
                String homeTeamName = cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type0.getCode(), betDetailEntity.getHomeTeamId(),null);
                String awayTeamName = cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type0.getCode(), betDetailEntity.getAwayTeamId(),null);
                stringBuilder.append("\n").append(homeTeamName)
                        .append(" VS ")
                        .append(awayTeamName)
                        .append("(")
                        .append("赛事ID: ")
                        .append(recordPO.getGameNo())
                        .append(")");

                String odds = betDetailEntity.getOdds().toString();
                String teamName=betDetailEntity.getIsBetHome()?homeTeamName:awayTeamName;
                //下注项目
                String betInfo =getBetInfo(cmdBetTypeEnum,betDetailEntity.getChoice(),teamName,betDetailEntity.getHdp().toString(),null);
                String half= this.isHalf(betDetailEntity.getIsFirstHalf(),CMDLangCodeEnum.ZH_CN.getCode());
                String result = "<span style=\"color: red;\">" +betInfo+"@"+odds + oddTypeName +"</span>";
                stringBuilder.append("\n").append(result);
                String sbLive = CmdLiveEnums.nameOfCode(betDetailEntity.getIsRunning()).getDesc();
                stringBuilder.append("\n").append(sbLive);
                stringBuilder.append(" 玩法:").append(half).append(" ").append(cmdBetTypeEnum.getDescCn()).append(" ");
                stringBuilder.append("\n").append("全场比分:").append(betDetailEntity.getHomeScore()).append("-").append(betDetailEntity.getAwayScore()).append("\n");
              }
            stringBuilder.append(getCustoms(cmdBetTypeEnum,CMDLangCodeEnum.ZH_CN.getCode())).append("\n");
            } catch (Exception e) {
            log.info("cmd体育,注单详情异常:{}", recordPO, e);
        }
        return stringBuilder.toString();
    }



    public String getBetInfo(CmdBetTypeEnum cmdBetTypeEnum,String choice,String teamName,String hdp,String lang) {
        StringBuffer sb=new StringBuffer();
        Map<String,String> MapValue=null;
        if (Objects.isNull(lang) || CMDLangCodeEnum.ZH_CN.getCode().equals(lang)){
            MapValue = cmdBetTypeEnum.getMapCnValue();
        }else{
            MapValue = cmdBetTypeEnum.getMapValue();
        }
        if (CmdBetTypeEnum.SBA_BET_TYPE_FLG.equals(cmdBetTypeEnum)){
            if (choice.equals("5")){
                sb.append(MapValue.get(choice));
            }else{
                sb.append(teamName +" " +MapValue.get(choice));
            }
        }
        if (CmdBetTypeEnum.SBA_BET_TYPE_HDP.equals(cmdBetTypeEnum) || CmdBetTypeEnum.SBA_BET_TYPE_HP3.equals(cmdBetTypeEnum) ) {
            sb.append(teamName +" （" +hdp + ")");
        }
        if (CmdBetTypeEnum.SBA_BET_TYPE_OUT.equals(cmdBetTypeEnum)) {
            sb.append(teamName);
        }
        if (sb.isEmpty()){
            sb.append(MapValue.get(choice));
        }
        return sb.toString();
    }

    public String getBetType(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        CmdBetTypeEnum cmdBetTypeEnum = CmdBetTypeEnum.of((String) jsonObject.get("transType"),(Boolean)jsonObject.get("isFirstHalf"));
        if (CmdBetTypeEnum.SBA_BET_TYPE_PAR.getCode().equals(cmdBetTypeEnum.getCode())){
            return "串关";
        }else{
            return "单关";
        }
    }

    public String isHalf(Boolean half,String lang){
        if (half){
            if (CMDLangCodeEnum.ZH_CN.getCode().equals(lang)){
                return "上半场";
            }else{
                return "half time";
            }
        }else{
            if ( CMDLangCodeEnum.ZH_CN.getCode().equals(lang)){
                return "全场";
            }else{
                return "all time";
            }
        }
    }

    public String getCustoms(CmdBetTypeEnum cmdBetTypeEnum,String lang) {
        if (CmdBetTypeEnum.SBA_BET_TYPE_PAR.getCode().equals(cmdBetTypeEnum.getCode())){
            if (CMDLangCodeEnum.ZH_CN.getCode().equals(lang)){
                return "串关";
            }else{
                return "multiple customs";
            }
        }else{
            if ( CMDLangCodeEnum.ZH_CN.getCode().equals(lang)){
                return "单关";
            }else{
                return "single customs";
            }
        }
    }


    public SportOrderClientResVO getSportData(OrderRecordPO record,String lang, VenueInfoVO venueInfoVO){
        SportOrderClientResVO data =new SportOrderClientResVO();
        BeanUtils.copyProperties(record, data);
        List<OrderMultipleBetVO> orderMultipleBetList=new ArrayList<>();
        CmdWebResponseVO betDetailEntity = JSONObject.parseObject(record.getParlayInfo(), CmdWebResponseVO.class);
        CMDLangCodeEnum cmdLangCodeEnum= CMDLangCodeEnum.fromCode(lang);
        CmdBetTypeEnum cmdBetTypeEnum = CmdBetTypeEnum.of(betDetailEntity.getTransType(),betDetailEntity.getIsFirstHalf());
        data.setCustoms(this.getCustoms(cmdBetTypeEnum,lang));
        if (CmdBetTypeEnum.SBA_BET_TYPE_PAR.getCode().equals(cmdBetTypeEnum.getCode())){
            for (CmdWebCollusionReqVO vo:betDetailEntity.getCollusionBetDetails()) {
                OrderMultipleBetVO  betVO=new OrderMultipleBetVO();
                // 下注队,主队还是客队信息
                String homeTeamName = cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type0.getCode(), vo.getHomeId(),cmdLangCodeEnum.getCode());
                String awayTeamName = cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type0.getCode(), vo.getAwayId(),cmdLangCodeEnum.getCode());
                String betTeamName=vo.getIsBetHome()?homeTeamName:awayTeamName;
                //联赛名字
                String leagueNames=cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type1.getCode(),vo.getLeagueId(),cmdLangCodeEnum.getCode());
                //单个赔率
                BigDecimal odds= vo.getParOdds();
                CmdBetTypeEnum cmdCollusionBetTypeEnum=CmdBetTypeEnum.of(vo.getParTransType(),vo.getIsFH());
                String half= this.isHalf(vo.getIsFH(),lang);
                //投注信息
                String betContent=getBetInfo(cmdCollusionBetTypeEnum,vo.getChoice(),betTeamName,vo.getHdp().toString(),lang);
                CmdOrderStatusEnum cmdOrderStatusEnum= cmdServiceImpl.getSportStatus(0,vo.getParDangerStatus(),vo.getParStatus());
                String betType = getBetType(lang,cmdCollusionBetTypeEnum);
                //注单状态
                betVO.setOrderClassify(data.getOrderClassify());
                betVO.setOrderId(record.getOrderId());
                betVO.setOdds(odds.toString());
                betVO.setTeamInfo(homeTeamName +" VS "+ awayTeamName);
                betVO.setEventInfo(leagueNames);
                betVO.setBetContent(half+" "+betType+" "+ betContent + " " + betTeamName);
                betVO.setWinlossStatus(winLossStatus(cmdOrderStatusEnum.getCode()));
                orderMultipleBetList.add(betVO);
            }
        }else{
            OrderMultipleBetVO  betVO=new OrderMultipleBetVO();
            String homeTeamName = cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type0.getCode(), betDetailEntity.getHomeTeamId(),cmdLangCodeEnum.getCode());
            String awayTeamName = cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type0.getCode(), betDetailEntity.getAwayTeamId(),cmdLangCodeEnum.getCode());
            String leagueNames = cmdServiceImpl.getLanguageInfo(venueInfoVO, CmdTypeEnum.type1.getCode(),   betDetailEntity.getLeagueId(),cmdLangCodeEnum.getCode());
            CmdOrderStatusEnum cmdOrderStatusEnum= cmdServiceImpl.getSportStatus(betDetailEntity.getStatusChange(),betDetailEntity.getDangerStatus(),betDetailEntity.getWinLoseStatus());
            CmdBetTypeEnum cmdCollusionBetTypeEnum=CmdBetTypeEnum.of(betDetailEntity.getTransType(),betDetailEntity.getIsFirstHalf());
            String half= this.isHalf(betDetailEntity.getIsFirstHalf(),lang);
            String odds = betDetailEntity.getOdds().toString();
            String teamName=betDetailEntity.getIsBetHome()?homeTeamName:awayTeamName;
            //投注信息
            String betContent=getBetInfo(cmdCollusionBetTypeEnum,betDetailEntity.getChoice(),teamName,betDetailEntity.getHdp().toString(),lang);
            String betType = getBetType(lang,cmdCollusionBetTypeEnum);
            betVO.setOrderClassify(data.getOrderClassify());
            betVO.setOrderId(record.getOrderId());
            betVO.setOdds(odds.toString());
            betVO.setTeamInfo(homeTeamName +" VS "+ awayTeamName);
            betVO.setEventInfo(leagueNames);
            betVO.setBetContent(half+" "+betType+" "+ betContent + " " + teamName);
            betVO.setWinlossStatus(winLossStatus(cmdOrderStatusEnum.getCode()));
            orderMultipleBetList.add(betVO);
        }
        data.setOrderMultipleBetList(orderMultipleBetList);
        return data;
    }

    private String getBetType(String lang,CmdBetTypeEnum cmdBetTypeEnum){
        String BetType="";
        if ( CMDLangCodeEnum.ZH_CN.getCode().equals(lang)){
            BetType=cmdBetTypeEnum.getDescCn();
        }else{
            BetType=cmdBetTypeEnum.getDesc();
        }
        return BetType;
    }



    private Integer winLossStatus(String status) {
        if (CmdOrderStatusEnum.LA.getCode().equals(status) || CmdOrderStatusEnum.LH.getCode().equals(status)) {//输
            return -1;
        } else if (CmdOrderStatusEnum.WA.getCode().equals(status) || CmdOrderStatusEnum.WH.getCode().equals(status)) {//赢
            return 1;
        } else if (CmdOrderStatusEnum.D.getCode().equals(status)) {//和局
            return 0;
        } else {
            return null;
        }
    }

}
