package com.cloud.baowang.wallet.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemConfigApi;
import com.cloud.baowang.wallet.api.vo.recharge.FeeVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteWithdrawWayResChangeVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.*;
import com.cloud.baowang.wallet.po.SiteWithdrawWayPO;
import com.cloud.baowang.wallet.po.SystemWithdrawWayPO;
import com.cloud.baowang.wallet.repositories.SiteWithdrawChannelRepository;
import com.cloud.baowang.wallet.repositories.SiteWithdrawWayRepository;
import com.cloud.baowang.wallet.repositories.SystemWithdrawChannelRepository;
import com.cloud.baowang.wallet.repositories.SystemWithdrawWayRepository;
import com.cloud.baowang.wallet.util.MinioFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Desciption: 站点提款方式
 * @Author: qiqi
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class SiteWithdrawWayService extends ServiceImpl<SiteWithdrawWayRepository, SiteWithdrawWayPO> {

    private final SystemConfigApi configApi;

    @Autowired
    private SiteWithdrawWayRepository siteWithdrawWayRepository;
    private final SiteWithdrawChannelRepository siteWithdrawChannelRepository;
    private final SystemWithdrawChannelRepository systemWithdrawChannelRepository;

    @Autowired
    private SystemWithdrawWayService systemWithdrawWayService;

    private final SystemWithdrawWayRepository systemWithdrawWayRepository;

    private final MinioFileService minioFileService;
    public ResponseVO<Void> batchSave(SiteWithdrawWayBatchRequestVO siteWithdrawWayBatchRequestVO) {
        List<SiteWithdrawWaySingleNewRequestVO> reqLists = siteWithdrawWayBatchRequestVO.getSiteWithdrawWaySingleNewReqVOList();
//        if (CollectionUtils.isEmpty(reqLists)) {
//            return ResponseVO.success();
//        }

        //获取站点授权方式数据
        SiteWithdrawWayRequestVO vo = new SiteWithdrawWayRequestVO();
        vo.setSiteCode(siteWithdrawWayBatchRequestVO.getSiteCode());
        List<SiteWithdrawWayResponseVO> sortList = selectBySort(vo).getData();
        Map<String,Integer> siteWayMap = sortList.stream().collect(Collectors.toMap(SiteWithdrawWayResponseVO::getWithdrawWayId, SiteWithdrawWayResponseVO::getSortOrder, (k1, k2) -> k2));
        //获取总控站方式数据
        List<SystemWithdrawWayPO> wayList = systemWithdrawWayRepository.selectList(new LambdaQueryWrapper<>());
        Map<String,Integer> wayMap  = wayList.stream().collect(Collectors.toMap(SystemWithdrawWayPO::getId, SystemWithdrawWayPO::getSortOrder, (k1, k2) -> k2));
        Integer maxSort = sortList.stream().mapToInt(SiteWithdrawWayResponseVO::getSortOrder).max().orElse(0);

        LambdaQueryWrapper<SiteWithdrawWayPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteWithdrawWayPO::getSiteCode, siteWithdrawWayBatchRequestVO.getSiteCode());
        this.baseMapper.delete(lambdaQueryWrapper);
        List<SiteWithdrawWayPO> batchLists = Lists.newArrayList();
        for (SiteWithdrawWaySingleNewRequestVO singleNewReqVO : reqLists) {
            SiteWithdrawWayPO siteWithdrawWayPO = new SiteWithdrawWayPO();
            siteWithdrawWayPO.setSiteCode(siteWithdrawWayBatchRequestVO.getSiteCode());
            siteWithdrawWayPO.setWithdrawId(singleNewReqVO.getWithdrawId());
            siteWithdrawWayPO.setWayFee(singleNewReqVO.getWayFee());
            siteWithdrawWayPO.setFeeType(singleNewReqVO.getFeeType());
            //设置排序 如果之前没有站点方式数据，排序直接取总控排序，如果有站点数据 （原数据方式已存在，取站点自己的排序,之前没有该方式，则排序放在最后）
            if(siteWayMap.isEmpty()){
                siteWithdrawWayPO.setSortOrder(wayMap.get(String.valueOf(singleNewReqVO.getWithdrawId())));
            }else{
                Integer sort = siteWayMap.get(String.valueOf(singleNewReqVO.getWithdrawId()));
                if(null == sort){
                    maxSort = maxSort+1;
                    siteWithdrawWayPO.setSortOrder(maxSort);
                }else{
                    siteWithdrawWayPO.setSortOrder(sort);
                }
            }
            Map<String,Integer> siteStatusMap = sortList.stream().collect(Collectors.toMap(SiteWithdrawWayResponseVO::getWithdrawWayId, SiteWithdrawWayResponseVO::getStatus, (k1, k2) -> k2));
            Map<String,Integer> wayStatusMap  = wayList.stream().collect(Collectors.toMap(SystemWithdrawWayPO::getId, SystemWithdrawWayPO::getStatus, (k1, k2) -> k2));
            String wayId = String.valueOf(singleNewReqVO.getWithdrawId());
            if(!siteStatusMap.isEmpty() && siteStatusMap.containsKey(wayId)){
                siteWithdrawWayPO.setStatus(siteStatusMap.get(wayId));
            }else{
                siteWithdrawWayPO.setStatus(wayStatusMap.get(wayId));
            }
            siteWithdrawWayPO.setWayFeeFixedAmount(singleNewReqVO.getWayFeeFixedAmount());
            siteWithdrawWayPO.setCreator(siteWithdrawWayBatchRequestVO.getOperatorUserNo());
            siteWithdrawWayPO.setCreatedTime(System.currentTimeMillis());
            batchLists.add(siteWithdrawWayPO);
        }
        if (!CollectionUtils.isEmpty(batchLists)) {
            this.saveBatch(batchLists);
        }

        return ResponseVO.success();
    }

    public ResponseVO<Void> enableOrDisable(SiteWithdrawWayStatusRequestVO siteWithdrawWayStatusReqVO) {
        LambdaQueryWrapper<SiteWithdrawWayPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SiteWithdrawWayPO::getId, siteWithdrawWayStatusReqVO.getId());
        SiteWithdrawWayPO siteWithdrawWayPO = this.baseMapper.selectOne(lqw);
        if (siteWithdrawWayPO != null) {
            if (Objects.equals(EnableStatusEnum.ENABLE.getCode(), siteWithdrawWayPO.getStatus())) {
                siteWithdrawWayPO.setStatus(EnableStatusEnum.DISABLE.getCode());
            }else {
                String wayId =siteWithdrawWayPO.getWithdrawId();
                SystemWithdrawWayPO systemWithdrawWayPO = systemWithdrawWayService.getById(wayId);
                if(Objects.equals(EnableStatusEnum.DISABLE.getCode(), systemWithdrawWayPO.getStatus())){
                    return ResponseVO.fail(ResultCode.ADMIN_CENTER_DISABLE_WAY);
                }
                siteWithdrawWayPO.setStatus(EnableStatusEnum.ENABLE.getCode());
            }
            siteWithdrawWayPO.setUpdatedTime(System.currentTimeMillis());
            siteWithdrawWayPO.setUpdater(siteWithdrawWayStatusReqVO.getOperatorUserNo());
            this.baseMapper.updateById(siteWithdrawWayPO);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    public ResponseVO<SiteWithdrawAuthorizeResVO> queryWithdrawAuthorize(final WithdrawAuthorizeReqVO reqVO) {
        SiteWithdrawAuthorizeResVO result = new SiteWithdrawAuthorizeResVO();
        Page<SystemWithdrawWayPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        Page<WithdrawAuthorizeResVO> resultPage = siteWithdrawWayRepository.queryWithdrawAuthorizePage(page, reqVO);
        ResponseVO<String> fileDomainResp = configApi.queryMinioDomain();
        String domain = "";
        if (fileDomainResp.isOk()) {
            domain = fileDomainResp.getData();
            String finalDomain = domain;
            resultPage.convert(item -> {
                String wayIcon = item.getWayIcon();
                if (StringUtils.isNotBlank(wayIcon)) {
                    wayIcon = finalDomain + "/" + wayIcon;
                    item.setWayIcon(wayIcon);
                }
                return item;
            });
        }

        List<FeeVO> chooseId = Lists.newArrayList();
        List<FeeVO> allId = Lists.newArrayList();

        List<FeeVO> finalChooseId = chooseId;
        this.lambdaQuery().eq(SiteWithdrawWayPO::getSiteCode, reqVO.getSiteCode()).list()
                .forEach(obj -> finalChooseId.add(FeeVO.builder().feeType(obj.getFeeType()).wayFeeFixedAmount(obj.getWayFeeFixedAmount()).id(obj.getWithdrawId())
                        .fee(obj.getWayFee()).build()));
        Map<String, String> wayCurrencyMap = new HashMap<>();

        List<SystemWithdrawWayPO> systemWithdrawWayPOList = systemWithdrawWayService.lambdaQuery().list();
        for (SystemWithdrawWayPO obj : systemWithdrawWayPOList) {
            allId.add(FeeVO.builder().id(obj.getId()).fee(obj.getWayFee()).feeType(obj.getFeeType()).wayFeeFixedAmount(obj.getWayFeeFixedAmount()).build());
            wayCurrencyMap.put(obj.getId(), obj.getCurrencyCode());
        }
        String siteCode = reqVO.getSiteCode();

        if (StringUtils.isNotBlank(siteCode) && CollectionUtil.isNotEmpty(chooseId)) {
            Set<String> allIdSet = allId.stream()
                    .map(FeeVO::getId)
                    .collect(Collectors.toSet());
            chooseId = chooseId.stream()
                    .filter(fee -> allIdSet.contains(fee.getId()))
                    .collect(Collectors.toList());

            //站点提款列表
            LambdaQueryWrapper<SiteWithdrawWayPO> query = Wrappers.lambdaQuery();
            query.eq(SiteWithdrawWayPO::getSiteCode, siteCode);
            List<SiteWithdrawWayPO> siteWithdrawWayPOS = siteWithdrawWayRepository.selectList(query);
            //处理一下提款方式新老数据
            if (CollectionUtil.isNotEmpty(siteWithdrawWayPOS)) {
                Set<String> allIdsSet = systemWithdrawWayPOList.stream()
                        .map(SystemWithdrawWayPO::getId)
                        .collect(Collectors.toSet());
                siteWithdrawWayPOS = siteWithdrawWayPOS.stream()
                        .filter(withdrawWay -> allIdsSet.contains(withdrawWay.getWithdrawId()))
                        .toList();
            }

            //获取当前站点已选择所有提款通道
            List<String> wayList = siteWithdrawWayPOS.stream().map(SiteWithdrawWayPO::getWithdrawId).toList();
            List<SiteWithdrawChannelVO> siteWithdrawChannelPOS = siteWithdrawChannelRepository.selectSiteWithdrawChannelList(siteCode, wayList, null);
            Map<String, List<String>> collect = new HashMap<>();
            //处理一下提款通道新老数据
            if (CollectionUtil.isNotEmpty(siteWithdrawChannelPOS)) {
                //站点已选通道转map,key是提款方式id,value是对应的通道ids
                collect = siteWithdrawChannelPOS.stream()
                        .filter(item -> StringUtils.isNotBlank(item.getWithdrawWayId()))
                        .collect(Collectors.groupingBy(
                                SiteWithdrawChannelVO::getWithdrawWayId,
                                Collectors.mapping(
                                        SiteWithdrawChannelVO::getChannelId,
                                        Collectors.toList()
                                )
                        ));

            }

            List<SiteWithdrawChannelQueryVO> chooseChannelList = new ArrayList<>();
            for (SiteWithdrawWayPO withdrawWayPO : siteWithdrawWayPOS) {
                SiteWithdrawChannelQueryVO queryVO = new SiteWithdrawChannelQueryVO();
                queryVO.setWithdrawWayId(withdrawWayPO.getWithdrawId());
                queryVO.setPlatform(collect.get(withdrawWayPO.getWithdrawId()));
                queryVO.setWithdrawFee(withdrawWayPO.getWayFee());
                queryVO.setFeeType(withdrawWayPO.getFeeType());
                queryVO.setWayFeeFixedAmount(withdrawWayPO.getWayFeeFixedAmount());
                if (wayCurrencyMap.containsKey(String.valueOf(withdrawWayPO.getWithdrawId()))) {
                    queryVO.setCurrencyGroup(wayCurrencyMap.get(String.valueOf(withdrawWayPO.getWithdrawId())));
                }
                chooseChannelList.add(queryVO);
            }

            result.setSiteWithdraw(chooseChannelList);
        }
        result.setPageVO(resultPage);
        result.setChooseID(chooseId);
        result.setAllID(allId);
        result.setCurrencyCode(reqVO.getCurrency());
        return ResponseVO.success(result);
    }

    public List<SiteWithdrawWayResVO> queryBySite() {
        return siteWithdrawWayRepository.queryBySite();
    }

    public List<SiteWithdrawWayResVO> queryBySiteAndTypeCode(String siteCode, String typeCode) {
        return siteWithdrawWayRepository.queryBySiteAndTypeCode(siteCode, typeCode);
    }

    public ResponseVO<List<SiteWithdrawWayResVO>> queryWithdrawListBySite(String siteCode) {
        List<SiteWithdrawWayResVO> siteWithdrawWayResVOS = siteWithdrawWayRepository.queryWithdrawListBySite(siteCode);
        return ResponseVO.success(siteWithdrawWayResVOS);
    }

    public SiteWithdrawWayVO queryWithdrawWay(String siteCode, String withdrawWayId) {
        LambdaQueryWrapper<SiteWithdrawWayPO> channelQuery = new LambdaQueryWrapper<>();
        channelQuery.eq(SiteWithdrawWayPO::getSiteCode, siteCode);
        channelQuery.eq(SiteWithdrawWayPO::getWithdrawId, withdrawWayId);
        SiteWithdrawWayPO siteWithdrawWayPO = siteWithdrawWayRepository.selectOne(channelQuery);
        return ConvertUtil.entityToModel(siteWithdrawWayPO, SiteWithdrawWayVO.class);
    }

    public ResponseVO<List<CodeValueVO>> queryListBySiteAndCurrencyCode(String siteCode, String currencyCode) {
        List<SiteWithdrawWayResVO> resVOS = siteWithdrawWayRepository.queryListBySiteAndCurrencyCode(siteCode, currencyCode);
        if (CollectionUtil.isNotEmpty(resVOS)) {
            List<CodeValueVO> codeValueList = resVOS.stream()
                    .map(item -> new CodeValueVO(item.getWithdrawId(), item.getWithdrawWayI18())) // 创建 CodeValueVO 对象
                    .collect(Collectors.toList());
            return ResponseVO.success(codeValueList);
        }
        return ResponseVO.success();
    }

    public ResponseVO<Page<SiteWithdrawWayResponseVO>> selectWithdrawPage(SiteWithdrawWayRequestVO vo) {

        Page<SiteWithdrawWayResponseVO> page = new Page<SiteWithdrawWayResponseVO>(vo.getPageNumber(), vo.getPageSize());
        Page<SiteWithdrawWayResponseVO> result = siteWithdrawWayRepository.selectWithdrawPage(page,vo);

        return ResponseVO.success(result);

    }

    public ResponseVO<List<SiteWithdrawWayResponseVO>> selectBySort(SiteWithdrawWayRequestVO siteWithdrawWayRequestVO) {
        List<SiteWithdrawWayResponseVO> result = siteWithdrawWayRepository.selectBySort(siteWithdrawWayRequestVO);
        return ResponseVO.success(result);
    }

    public ResponseVO<Boolean> batchSaveSort(String userAccount, List<SortNewReqVO> sortNewReqVOS) {
        List<SiteWithdrawWayPO> batchLists = Lists.newArrayList();
        for (SortNewReqVO sortNewReqVO : sortNewReqVOS) {
            SiteWithdrawWayPO siteWithdrawWayPO = new SiteWithdrawWayPO();
            siteWithdrawWayPO.setId(sortNewReqVO.getId());
            siteWithdrawWayPO.setSortOrder(sortNewReqVO.getSortOrder());
            siteWithdrawWayPO.setUpdatedTime(System.currentTimeMillis());
            siteWithdrawWayPO.setUpdater(userAccount);
            batchLists.add(siteWithdrawWayPO);
        }
        this.updateBatchById(batchLists);
        return ResponseVO.success();
    }

    public List<SiteWithdrawWayResChangeVO> getWithdrawWayBySiteCodeList(String siteCode){
        //获取总控站方式数据
        return this.getBaseMapper().getWithdrawWayBySiteCodeList(siteCode);
    }
}
