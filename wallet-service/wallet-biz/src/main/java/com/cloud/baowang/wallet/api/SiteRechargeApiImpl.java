package com.cloud.baowang.wallet.api;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.system.api.api.exchange.SystemCurrencyInfoApi;
import com.cloud.baowang.system.api.api.site.change.SiteInfoChangeRecordApi;
import com.cloud.baowang.system.api.enums.*;
import com.cloud.baowang.system.api.vo.JsonDifferenceVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeBodyVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordListReqVO;
import com.cloud.baowang.wallet.api.api.SiteRechargeApi;
import com.cloud.baowang.wallet.api.vo.recharge.*;
import com.cloud.baowang.wallet.po.SystemRechargeChannelPO;
import com.cloud.baowang.wallet.repositories.SystemRechargeChannelRepository;
import com.cloud.baowang.wallet.service.SiteRechargeChannelService;
import com.cloud.baowang.wallet.service.SiteRechargeWayService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/29 18:44
 * @Version: V1.0
 **/
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SiteRechargeApiImpl implements SiteRechargeApi {

    private final SystemRechargeChannelRepository systemChannelRepository;

    @Resource
    private SiteRechargeChannelService siteRechargeChannelService;

    @Resource
    private SiteRechargeWayService siteRechargeWayService;

    private final SiteInfoChangeRecordApi siteInfoChangeRecordApi;

    private final SystemCurrencyInfoApi systemCurrencyInfoApi;

    @Override
    @Transactional
    public ResponseVO<Boolean> batchSave(String creator, String siteCode, List<SiteRechargeBatchReqVO> siteRechargeBatchReqVOs,String siteName,Integer handicapMode) {
        SiteRechargeWayBatchReqVO siteRechargeWayBatchReqVO = new SiteRechargeWayBatchReqVO();
        siteRechargeWayBatchReqVO.setSiteCode(siteCode);
        siteRechargeWayBatchReqVO.setOperatorUserNo(creator);
        List<SiteRechargeWaySingleNewReqVO> siteRechargeWaySingleNewReqVOList = Lists.newArrayList();

//        if (CollectionUtil.isEmpty(siteRechargeBatchReqVOs)) {
//            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
//        }
        //取出渠道为空的充值方式id 默认为全选 2024-09-12最新需求
        List<Long> wayIds = siteRechargeBatchReqVOs.stream()
                .filter(item -> CollectionUtil.isEmpty(item.getPlatform()))
                .map(SiteRechargeBatchReqVO::getRechargeWayId)
                .toList();
        if (CollectionUtil.isNotEmpty(wayIds)) {
            LambdaQueryWrapper<SystemRechargeChannelPO> query = Wrappers.lambdaQuery();
            query.in(SystemRechargeChannelPO::getRechargeWayId, wayIds);
            List<SystemRechargeChannelPO> rechargeChannelPOS =  systemChannelRepository.selectList(query);
            if (CollectionUtil.isNotEmpty(rechargeChannelPOS)) {
                //转map，key是方式id,value为渠道ids
                Map<String, List<String>> channelIds = rechargeChannelPOS.stream()
                        .collect(Collectors.groupingBy(SystemRechargeChannelPO::getRechargeWayId, Collectors.mapping(SystemRechargeChannelPO::getId, Collectors.toList())));
                for (SiteRechargeBatchReqVO rechargeBatchReqVO : siteRechargeBatchReqVOs) {
                    Long wayId = rechargeBatchReqVO.getRechargeWayId();
                    if (channelIds.containsKey(String.valueOf(wayId))) {
                        rechargeBatchReqVO.setPlatform(channelIds.get(String.valueOf(wayId)));
                    }
                }
            }
        }
        List<SiteRechargeChannelBatchReqVO> batchChannelList = new ArrayList<>();
        for (SiteRechargeBatchReqVO siteRechargeBatchReqVO : siteRechargeBatchReqVOs) {
            SiteRechargeWaySingleNewReqVO siteRechargeWaySingleNewReqVO = new SiteRechargeWaySingleNewReqVO();
            siteRechargeWaySingleNewReqVO.setRechargeWayId(siteRechargeBatchReqVO.getRechargeWayId());
            siteRechargeWaySingleNewReqVO.setWayFee(siteRechargeBatchReqVO.getDepositFee());
            siteRechargeWaySingleNewReqVO.setFeeType(siteRechargeBatchReqVO.getFeeType());
            siteRechargeWaySingleNewReqVO.setWayFeeFixedAmount(siteRechargeBatchReqVO.getWayFeeFixedAmount());
            siteRechargeWaySingleNewReqVOList.add(siteRechargeWaySingleNewReqVO);

            SiteRechargeChannelBatchReqVO siteRechargeChannelBatchReqVO = new SiteRechargeChannelBatchReqVO();
            siteRechargeChannelBatchReqVO.setSiteCode(siteCode);
            siteRechargeChannelBatchReqVO.setOperatorUserNo(creator);
            List<SiteRechargeChannelSingleNewReqVO> siteRechargeChannelSingleNewReqVOS = Lists.newArrayList();
            for (String channelId : siteRechargeBatchReqVO.getPlatform()) {
                SiteRechargeChannelSingleNewReqVO siteRechargeChannelSingleNewReqVO = new SiteRechargeChannelSingleNewReqVO();
                siteRechargeChannelSingleNewReqVO.setRechargeWayId(siteRechargeBatchReqVO.getRechargeWayId());
                siteRechargeChannelSingleNewReqVO.setChannelId(channelId);
                siteRechargeChannelSingleNewReqVOS.add(siteRechargeChannelSingleNewReqVO);
            }
            siteRechargeChannelBatchReqVO.setSiteRechargeChannelSingleNewReqVOList(siteRechargeChannelSingleNewReqVOS);
            batchChannelList.add(siteRechargeChannelBatchReqVO);
        }

        //存款授权前 by mufan
        List<SiteRechargeWayResChangeVO> beforewayList=siteRechargeWayService.getRchargeWayBySiteCodeList(siteCode);
        //用于通道名称和币种的关系如操作日志补充用
        Map<String,String> currencyMap=new HashMap<>();
        systemCurrencyInfoApi.selectAll().getData().forEach(e ->{
            currencyMap.put(e.getCurrencyCode(),I18nMessageUtil.getI18NMessageInAdvice(e.getCurrencyNameI18()));
        });
        Map<String,String> wayFeeBeforeMap = new HashMap<>();
        Map<String,String> wayFeeFixedAmountBeforeMap = new HashMap<>();
        Map<String,String> currencyWayMap = new HashMap<>();
        Map<String,String> wayNameBeforeMap = new HashMap<>();
        beforewayList.forEach(e ->{
            wayNameBeforeMap.put(e.getRechargeWayId(),e.getRechargeWayName());
            currencyWayMap.put(e.getRechargeWayName(),currencyMap.get(e.getCurrencyCode()));
            wayFeeFixedAmountBeforeMap.put(e.getRechargeWayName(), ObjectUtils.isNotEmpty(e.getWayFeeFixedAmount())?e.getWayFeeFixedAmount().toString():"0");
            wayFeeBeforeMap.put(e.getRechargeWayName(), ObjectUtils.isNotEmpty(e.getWayFee())?e.getWayFee().toString():"0");
        });
        Map<String,List<String>> waybeforeChannelListMap = new HashMap<>();
        List<SiteRechargeChannelChangeVO> beforeChannel= siteRechargeChannelService.getSiteCodeList(siteCode);
        beforeChannel.forEach(e ->{
            if (StringUtils.isNotBlank(wayNameBeforeMap.get(e.getRechargeWayId()))) {
                List<String> data = waybeforeChannelListMap.get(wayNameBeforeMap.get(e.getRechargeWayId()));
                if (CollectionUtil.isEmpty(data)) {
                    data = new ArrayList<>();
                }
                data.add(e.getChannelCode());
                waybeforeChannelListMap.put(wayNameBeforeMap.get(e.getRechargeWayId()), data);
            }
        });
        beforewayList.forEach(e ->{
            if (!waybeforeChannelListMap.containsKey(wayNameBeforeMap.get(e.getRechargeWayId()))){
                waybeforeChannelListMap.put(wayNameBeforeMap.get(e.getRechargeWayId()),new ArrayList<>());
            }
        });

        siteRechargeChannelService.batchSave(batchChannelList, siteCode,handicapMode);
        siteRechargeWayBatchReqVO.setSiteRechargeWaySingleNewReqVOList(siteRechargeWaySingleNewReqVOList);
        siteRechargeWayBatchReqVO.setHandicapMode(handicapMode);
        siteRechargeWayService.batchSave(siteRechargeWayBatchReqVO);

        //存款授权后
        List<SiteRechargeWayResChangeVO> afterwayList=siteRechargeWayService.getRchargeWayBySiteCodeList(siteCode);
        Map<String,String> wayFeeAfterMap = new HashMap<>();
        Map<String,String> wayFeeFixedAmountAfterMap = new HashMap<>();
        Map<String,String> wayNameAfterMap = new HashMap<>();
        afterwayList.forEach(e ->{
            wayNameAfterMap.put(e.getRechargeWayId(),e.getRechargeWayName());
            currencyWayMap.put(e.getRechargeWayName(),currencyMap.get(e.getCurrencyCode()));
            wayFeeFixedAmountAfterMap.put(e.getRechargeWayName(), ObjectUtils.isNotEmpty(e.getWayFeeFixedAmount())?e.getWayFeeFixedAmount().toString():"0");
            wayFeeAfterMap.put(e.getRechargeWayName(), ObjectUtils.isNotEmpty(e.getWayFee())?e.getWayFee().toString():"0");
        });
        Map<String,List<String>>   afterChannelListMap = new HashMap<>();
        List<SiteRechargeChannelChangeVO> afterChannelList= siteRechargeChannelService.getSiteCodeList(siteCode);
        afterChannelList.forEach(e ->{
            if (StringUtils.isNotBlank(wayNameAfterMap.get(e.getRechargeWayId()))) {
                List<String> data = afterChannelListMap.get(wayNameAfterMap.get(e.getRechargeWayId()));
                if (CollectionUtil.isEmpty(data)) {
                    data = new ArrayList<>();
                }
                data.add(e.getChannelCode());
                afterChannelListMap.put(wayNameAfterMap.get(e.getRechargeWayId()), data);
            }
        });
        afterwayList.forEach(e ->{
            if (!afterChannelListMap.containsKey(wayNameAfterMap.get(e.getRechargeWayId()))){
                afterChannelListMap.put(wayNameAfterMap.get(e.getRechargeWayId()),new ArrayList<>());
            }
        });
        //充值渠道授权前后对比
        SiteInfoChangeBodyVO recharge=new SiteInfoChangeBodyVO();
        recharge.setChangeBeforeObj(waybeforeChannelListMap);
        recharge.setChangeAfterObj(afterChannelListMap);
        currencyWayMap.put(SitClounmDefaultEnum.baseClounm.getCode(),SiteChangeTypeEnum.RechargeAuthor.getname());
        recharge.setColumnNameMap(currencyWayMap);
        recharge.setChangeType(SiteChangeTypeEnum.RechargeAuthor.getname());
        List<JsonDifferenceVO> changevo= siteInfoChangeRecordApi.getJsonDifferenceListForRecharger(recharge);
        //百分比手续费
        Map<String,String> wayColumnMap=new HashMap<>();
        wayColumnMap.put(SitClounmDefaultEnum.baseClounm.getCode(),SitClounmDefaultEnum.percentageFee.getname());
        SiteInfoChangeBodyVO wayFee=new SiteInfoChangeBodyVO();
        wayFee.setChangeBeforeObj(wayFeeBeforeMap);
        wayFee.setChangeAfterObj(wayFeeAfterMap);
        wayFee.setColumnNameMap(wayColumnMap);
        wayFee.setChangeType(SiteChangeTypeEnum.RechargeAuthor.getname());
        List<JsonDifferenceVO> wayChange=  siteInfoChangeRecordApi.getJsonDifferenceList(wayFee);
        //单笔固定金额手续费
        Map<String,String> wayFeeFixedAmountMap=new HashMap<>();
        wayFeeFixedAmountMap.put(SitClounmDefaultEnum.baseClounm.getCode(),SitClounmDefaultEnum.ProportionalHandlingFee.getname());
        SiteInfoChangeBodyVO wayFeeFixedAmountFee=new SiteInfoChangeBodyVO();
        wayFeeFixedAmountFee.setChangeBeforeObj(wayFeeFixedAmountBeforeMap);
        wayFeeFixedAmountFee.setChangeAfterObj(wayFeeFixedAmountAfterMap);
        wayFeeFixedAmountFee.setColumnNameMap(wayFeeFixedAmountMap);
        wayFeeFixedAmountFee.setChangeType(SiteChangeTypeEnum.RechargeAuthor.getname());
        List<JsonDifferenceVO> wayFeeFixedAmountFeeChange=  siteInfoChangeRecordApi.getJsonDifferenceList(wayFeeFixedAmountFee);

        SiteInfoChangeRecordListReqVO vo =new SiteInfoChangeRecordListReqVO();
        vo.setLoginIp(CurrReqUtils.getReqIp());
        vo.setCreator(CurrReqUtils.getAccount());
        vo.setOptionType(SiteOptionTypeEnum.DataUpdate.getCode());
        vo.setOptionStatus(SiteOptionStatusEnum.success.getCode());
        vo.setOptionModelName(SiteOptionModelNameEnum.site.getname());
        vo.setOptionCode(siteCode);
        vo.setOptionName(siteName);
        List<JsonDifferenceVO>  sitadataList=new ArrayList<>();
        sitadataList.addAll(changevo);
        sitadataList.addAll(wayChange);
        sitadataList.addAll(wayFeeFixedAmountFeeChange);
        vo.setData(sitadataList);
        siteInfoChangeRecordApi.addJsonDifferenceList(vo);
        //end by mufan
        return ResponseVO.success(Boolean.TRUE);
    }

    @Override
    public ResponseVO<SiteRechargeAuthorizeResVO> queryDepositAuthorize(RechargeAuthorizeReqVO reqVO) {
        return siteRechargeWayService.queryDepositAuthorizePage(reqVO);
    }
}
