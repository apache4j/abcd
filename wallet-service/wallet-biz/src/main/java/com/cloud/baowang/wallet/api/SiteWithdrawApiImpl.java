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
import com.cloud.baowang.wallet.api.api.SiteWithdrawApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteWithdrawChannelChangeVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteWithdrawWayResChangeVO;
import com.cloud.baowang.wallet.api.vo.withdraw.*;
import com.cloud.baowang.wallet.po.SystemWithdrawChannelPO;
import com.cloud.baowang.wallet.repositories.SystemWithdrawChannelRepository;
import com.cloud.baowang.wallet.service.SiteWithdrawChannelService;
import com.cloud.baowang.wallet.service.SiteWithdrawWayService;
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
 * @Author:qiqi
 **/
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SiteWithdrawApiImpl implements SiteWithdrawApi {

    private final SystemWithdrawChannelRepository systemWithdrawChannelRepository;

    @Resource
    private SiteWithdrawChannelService siteWithdrawChannelService;

    @Resource
    private SiteWithdrawWayService siteWithdrawWayService;

    private final SystemCurrencyInfoApi systemCurrencyInfoApi;

    private final SiteInfoChangeRecordApi siteInfoChangeRecordApi;


    @Override
    @Transactional
    public ResponseVO<Boolean> batchSave(String creator, String siteCode, List<SiteWithdrawBatchRequsetVO> siteWithdrawBatchRequestVOS,String siteName) {
        SiteWithdrawWayBatchRequestVO siteWithdrawWayBatchRequestVO = new SiteWithdrawWayBatchRequestVO();
        siteWithdrawWayBatchRequestVO.setSiteCode(siteCode);
        siteWithdrawWayBatchRequestVO.setOperatorUserNo(creator);
        List<SiteWithdrawWaySingleNewRequestVO> siteWithdrawWaySingleNewRequestVOList = Lists.newArrayList();
//        if (CollectionUtil.isEmpty(siteWithdrawBatchRequestVOS)) {
//            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
//        }
        //取出渠道为空的充值方式id 默认为全选 2024-09-12最新需求
        List<String> wayIds = siteWithdrawBatchRequestVOS.stream()
                .filter(item -> CollectionUtil.isEmpty(item.getPlatform()))
                .map(SiteWithdrawBatchRequsetVO::getWithdrawWayId)
                .toList();
        if (CollectionUtil.isNotEmpty(wayIds)) {
            //说明存在全选的场馆，找到该场馆对应的游戏列表
            LambdaQueryWrapper<SystemWithdrawChannelPO> query = Wrappers.lambdaQuery();
            query.in(SystemWithdrawChannelPO::getWithdrawWayId, wayIds);
            List<SystemWithdrawChannelPO> systemWithdrawChannelPOS = systemWithdrawChannelRepository.selectList(query);
            if (CollectionUtil.isNotEmpty(systemWithdrawChannelPOS)) {
                //转map，key是方式id,value为渠道ids
                Map<String, List<String>> channelIds = systemWithdrawChannelPOS.stream()
                        .collect(Collectors.groupingBy(SystemWithdrawChannelPO::getWithdrawWayId, Collectors.mapping(SystemWithdrawChannelPO::getId, Collectors.toList())));
                for (SiteWithdrawBatchRequsetVO rechargeBatchReqVO : siteWithdrawBatchRequestVOS) {
                    String wayId = rechargeBatchReqVO.getWithdrawWayId();
                    if (channelIds.containsKey(wayId)) {
                        rechargeBatchReqVO.setPlatform(channelIds.get(wayId));
                    }
                }
            }
        }
        ArrayList<SiteWithdrawChannelBatchRequestVO> batchChannelList = new ArrayList<>();
        for (SiteWithdrawBatchRequsetVO siteWithdrawBatchRequestVO : siteWithdrawBatchRequestVOS) {
            SiteWithdrawWaySingleNewRequestVO siteWithdrawWaySingleNewRequestVO = new SiteWithdrawWaySingleNewRequestVO();
            siteWithdrawWaySingleNewRequestVO.setWithdrawId(siteWithdrawBatchRequestVO.getWithdrawWayId());
            siteWithdrawWaySingleNewRequestVO.setWayFee(siteWithdrawBatchRequestVO.getWithdrawFee());
            siteWithdrawWaySingleNewRequestVO.setFeeType(siteWithdrawBatchRequestVO.getFeeType());
            siteWithdrawWaySingleNewRequestVO.setWayFeeFixedAmount(siteWithdrawBatchRequestVO.getWayFeeFixedAmount());
            siteWithdrawWaySingleNewRequestVOList.add(siteWithdrawWaySingleNewRequestVO);

            SiteWithdrawChannelBatchRequestVO siteWithdrawChannelBatchRequestVO = new SiteWithdrawChannelBatchRequestVO();
            siteWithdrawChannelBatchRequestVO.setSiteCode(siteCode);
            siteWithdrawChannelBatchRequestVO.setOperatorUserNo(creator);
            List<SiteWithdrawChannelSingleNewRequestVO> siteWithdrawChannelSingleNewRequestVOS = Lists.newArrayList();
            if (CollectionUtil.isNotEmpty(siteWithdrawBatchRequestVO.getPlatform())) {
                for (String channelId : siteWithdrawBatchRequestVO.getPlatform()) {
                    SiteWithdrawChannelSingleNewRequestVO siteWithdrawChannelSingleNewRequestVO = new SiteWithdrawChannelSingleNewRequestVO();
                    siteWithdrawChannelSingleNewRequestVO.setWithdrawWayId(siteWithdrawBatchRequestVO.getWithdrawWayId());
                    siteWithdrawChannelSingleNewRequestVO.setChannelId(channelId);
                    siteWithdrawChannelSingleNewRequestVOS.add(siteWithdrawChannelSingleNewRequestVO);
                }
                siteWithdrawChannelBatchRequestVO.setSiteWithdrawChannelSingleNewRequestVOS(siteWithdrawChannelSingleNewRequestVOS);
                batchChannelList.add(siteWithdrawChannelBatchRequestVO);
            }
        }


        //提款授权前 by mufan
        List<SiteWithdrawWayResChangeVO>  withdrawWayBeforeList=siteWithdrawWayService.getWithdrawWayBySiteCodeList(siteCode);
        //用于通道名称和币种的关系如操作日志补充用
        Map<String,String> currencyMap=new HashMap<>();
        systemCurrencyInfoApi.selectAll().getData().forEach(e ->{
            currencyMap.put(e.getCurrencyCode(),I18nMessageUtil.getI18NMessageInAdvice(e.getCurrencyNameI18()));
        });
        Map<String,String> wayFeeBeforeMap = new HashMap<>();
        Map<String,String> wayFeeFixedAmountBeforeMap = new HashMap<>();
        Map<String,String> currencyWayMap = new HashMap<>();
        Map<String,String> wayNameBeforeMap = new HashMap<>();
        withdrawWayBeforeList.forEach(e ->{
            wayNameBeforeMap.put(e.getWithdrawId(),e.getWithdrawWayName());
            currencyWayMap.put(e.getWithdrawWayName(),currencyMap.get(e.getCurrencyCode()));
            wayFeeFixedAmountBeforeMap.put(e.getWithdrawWayName(), ObjectUtils.isNotEmpty(e.getWayFeeFixedAmount())?e.getWayFeeFixedAmount().toString():"0");
            wayFeeBeforeMap.put(e.getWithdrawWayName(), ObjectUtils.isNotEmpty(e.getWayFee())?e.getWayFee().toString():"0");
        });
        Map<String,List<String>> waybeforeChannelListMap = new HashMap<>();
        List<SiteWithdrawChannelChangeVO> beforewithdrawChannelList= siteWithdrawChannelService.getWithdrawChannelBySiteCode(siteCode);
        beforewithdrawChannelList.forEach(e ->{
            if (StringUtils.isNotBlank(wayNameBeforeMap.get(e.getWithdrawWayId()))) {
                List<String> data = waybeforeChannelListMap.get(wayNameBeforeMap.get(e.getWithdrawWayId()));
                if (CollectionUtil.isEmpty(data)) {
                    data = new ArrayList<>();
                }
                data.add(e.getChannelCode());
                waybeforeChannelListMap.put(wayNameBeforeMap.get(e.getWithdrawWayId()), data);
            }
        });
        withdrawWayBeforeList.forEach(e ->{
            if (!waybeforeChannelListMap.containsKey(wayNameBeforeMap.get(e.getWithdrawId()))){
                waybeforeChannelListMap.put(wayNameBeforeMap.get(e.getWithdrawId()),new ArrayList<>());
            }
        });
        siteWithdrawChannelService.batchSave(batchChannelList, siteCode);
        siteWithdrawWayBatchRequestVO.setSiteWithdrawWaySingleNewReqVOList(siteWithdrawWaySingleNewRequestVOList);
        siteWithdrawWayService.batchSave(siteWithdrawWayBatchRequestVO);

        List<SiteWithdrawWayResChangeVO>  withdrawWayAfterList=siteWithdrawWayService.getWithdrawWayBySiteCodeList(siteCode);
        Map<String,String> wayFeeAfterMap = new HashMap<>();
        Map<String,String> wayFeeFixedAmountAfterMap = new HashMap<>();
        Map<String,String> wayNameAfterMap = new HashMap<>();
        withdrawWayAfterList.forEach(e ->{
            wayNameAfterMap.put(e.getWithdrawId(),e.getWithdrawWayName());
            currencyWayMap.put(e.getWithdrawWayName(),currencyMap.get(e.getCurrencyCode()));
            wayFeeFixedAmountAfterMap.put(e.getWithdrawWayName(), ObjectUtils.isNotEmpty(e.getWayFeeFixedAmount())?e.getWayFeeFixedAmount().toString():"0");
            wayFeeAfterMap.put(e.getWithdrawWayName(), ObjectUtils.isNotEmpty(e.getWayFee())?e.getWayFee().toString():"0");
        });
        Map<String,List<String>>   afterChannelListMap = new HashMap<>();
        List<SiteWithdrawChannelChangeVO> withdrawChanneAfterlList= siteWithdrawChannelService.getWithdrawChannelBySiteCode(siteCode);
        withdrawChanneAfterlList.forEach(e ->{
            if (StringUtils.isNotBlank(wayNameAfterMap.get(e.getWithdrawWayId()))){
                List<String> data= afterChannelListMap.get(wayNameAfterMap.get(e.getWithdrawWayId()));
                if (CollectionUtil.isEmpty(data)){
                    data= new ArrayList<>();
                }
                data.add(e.getChannelCode());
                afterChannelListMap.put(wayNameAfterMap.get(e.getWithdrawWayId()),data);
            }
        });
        withdrawWayAfterList.forEach(e ->{
            if (!afterChannelListMap.containsKey(wayNameAfterMap.get(e.getWithdrawId()))){
                afterChannelListMap.put(wayNameAfterMap.get(e.getWithdrawId()),new ArrayList<>());
            }
        });
        //提款渠道授权前后对比
        SiteInfoChangeBodyVO recharge=new SiteInfoChangeBodyVO();
        recharge.setChangeBeforeObj(waybeforeChannelListMap);
        recharge.setChangeAfterObj(afterChannelListMap);
        currencyWayMap.put(SitClounmDefaultEnum.baseClounm.getCode(), SiteChangeTypeEnum.withdrawAuthor.getname());
        recharge.setColumnNameMap(currencyWayMap);
        recharge.setChangeType(SiteChangeTypeEnum.withdrawAuthor.getname());
        List<JsonDifferenceVO> changevo= siteInfoChangeRecordApi.getJsonDifferenceListForRecharger(recharge);
        //百分比手续费
        Map<String,String> wayColumnMap=new HashMap<>();
        wayColumnMap.put(SitClounmDefaultEnum.baseClounm.getCode(),SitClounmDefaultEnum.percentageFee.getname());
        SiteInfoChangeBodyVO wayFee=new SiteInfoChangeBodyVO();
        wayFee.setChangeBeforeObj(wayFeeBeforeMap);
        wayFee.setChangeAfterObj(wayFeeAfterMap);
        wayFee.setColumnNameMap(wayColumnMap);
        wayFee.setChangeType(SiteChangeTypeEnum.withdrawAuthor.getname());
        List<JsonDifferenceVO> wayChange=  siteInfoChangeRecordApi.getJsonDifferenceList(wayFee);
        //单笔固定金额手续费
        Map<String,String> wayFeeFixedAmountMap=new HashMap<>();
        wayFeeFixedAmountMap.put(SitClounmDefaultEnum.baseClounm.getCode(),SitClounmDefaultEnum.ProportionalHandlingFee.getname());
        SiteInfoChangeBodyVO wayFeeFixedAmountFee=new SiteInfoChangeBodyVO();
        wayFeeFixedAmountFee.setChangeBeforeObj(wayFeeFixedAmountBeforeMap);
        wayFeeFixedAmountFee.setChangeAfterObj(wayFeeFixedAmountAfterMap);
        wayFeeFixedAmountFee.setColumnNameMap(wayFeeFixedAmountMap);
        wayFeeFixedAmountFee.setChangeType(SiteChangeTypeEnum.withdrawAuthor.getname());
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
        //end by 记录操作日志 mufan
        return ResponseVO.success(Boolean.TRUE);
    }

    @Override
    public ResponseVO<SiteWithdrawAuthorizeResVO> queryWithdrawAuthorize(WithdrawAuthorizeReqVO reqVO) {
        return siteWithdrawWayService.queryWithdrawAuthorize(reqVO);
    }
}
