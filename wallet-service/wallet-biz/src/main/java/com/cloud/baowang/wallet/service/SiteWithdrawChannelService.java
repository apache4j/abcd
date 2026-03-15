package com.cloud.baowang.wallet.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteWithdrawChannelChangeVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.*;
import com.cloud.baowang.wallet.po.SiteWithdrawChannelPO;
import com.cloud.baowang.wallet.po.SystemWithdrawChannelPO;
import com.cloud.baowang.wallet.repositories.SiteWithdrawChannelRepository;
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
 * @Desciption: 站点提现通道
 * @Author: qiqi
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class SiteWithdrawChannelService extends ServiceImpl<SiteWithdrawChannelRepository, SiteWithdrawChannelPO> {

    @Autowired
    private SiteWithdrawChannelRepository siteWithdrawChannelRepository;
    @Autowired
    private SystemWithdrawChannelService systemWithdrawChannelService;
    private final VipRankApi vipRankApi;

    public ResponseVO<Void> batchSave(List<SiteWithdrawChannelBatchRequestVO> withdrawChannelBatchRequestVO, String siteCode) {
//        if (CollectionUtil.isEmpty(withdrawChannelBatchRequestVO)) {
//            return ResponseVO.success();
//        }
        //获取站点授权通道数据
        SiteWithdrawChannelRequestVO vo = new SiteWithdrawChannelRequestVO();
        vo.setSiteCode(siteCode);
        List<SiteWithdrawChannelResponseVO> sortList = selectBySort(vo).getData();
        Map<String,Integer> siteChannelMap = sortList.stream().collect(Collectors.toMap(SiteWithdrawChannelResponseVO::getChannelId, SiteWithdrawChannelResponseVO::getSortOrder, (k1, k2) -> k2));
        //获取总控站通道数据
        List<SystemWithdrawChannelPO> channelList = systemWithdrawChannelService.list();
        Map<String,Integer> channelMap  = channelList.stream().collect(Collectors.toMap(SystemWithdrawChannelPO::getId, SystemWithdrawChannelPO::getSortOrder, (k1, k2) -> k2));
        Integer maxSort = sortList.stream().mapToInt(SiteWithdrawChannelResponseVO::getSortOrder).max().orElse(0);

        LambdaQueryWrapper<SiteWithdrawChannelPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteWithdrawChannelPO::getSiteCode, siteCode);
        List<SiteWithdrawChannelPO> siteWithdrawChannelPOS = this.baseMapper.selectList(lambdaQueryWrapper);
        if (!CollectionUtils.isEmpty(siteWithdrawChannelPOS)) {
            List<String> channelIdsForDb = siteWithdrawChannelPOS.stream().map(SiteWithdrawChannelPO::getChannelId).toList();
            //删除站点 减少授权数量
            systemWithdrawChannelService.subAuthNum(channelIdsForDb);
            this.baseMapper.delete(lambdaQueryWrapper);
        }
        List<SiteWithdrawChannelPO> batchLists = Lists.newArrayList();
        List<String> channelIds = Lists.newArrayList();
        for (SiteWithdrawChannelBatchRequestVO siteWithdrawChannelBatchRequestVO : withdrawChannelBatchRequestVO) {
            String operatorUserNo = siteWithdrawChannelBatchRequestVO.getOperatorUserNo();
            List<SiteWithdrawChannelSingleNewRequestVO> reqLists = siteWithdrawChannelBatchRequestVO.getSiteWithdrawChannelSingleNewRequestVOS();
            for (SiteWithdrawChannelSingleNewRequestVO singleNewReqVO : reqLists) {
                SiteWithdrawChannelPO siteWithdrawChannelPO = new SiteWithdrawChannelPO();
                siteWithdrawChannelPO.setSiteCode(siteCode);
                siteWithdrawChannelPO.setChannelId(singleNewReqVO.getChannelId());
                siteWithdrawChannelPO.setCreator(operatorUserNo);
                //设置排序 如果之前没有站点通道数据，排序直接取总控排序，如果有站点数据 （原数据通道已存在，取站点自己的排序,之前没有该通道，则排序放在最后）
                if(siteChannelMap.isEmpty()){
                    siteWithdrawChannelPO.setSortOrder(channelMap.get(String.valueOf(singleNewReqVO.getChannelId())));
                }else{
                    Integer sort = siteChannelMap.get(String.valueOf(singleNewReqVO.getChannelId()));
                    if(null == sort){
                        maxSort = maxSort+1;
                        siteWithdrawChannelPO.setSortOrder(maxSort);
                    }else{
                        siteWithdrawChannelPO.setSortOrder(sort);
                    }
                }
                Map<String,Integer> siteStatusMap = sortList.stream().collect(Collectors.toMap(SiteWithdrawChannelResponseVO::getChannelId, SiteWithdrawChannelResponseVO::getStatus, (k1, k2) -> k2));
                Map<String,Integer> wayStatusMap  = channelList.stream().collect(Collectors.toMap(SystemWithdrawChannelPO::getId, SystemWithdrawChannelPO::getStatus, (k1, k2) -> k2));
                String channelId = String.valueOf(singleNewReqVO.getChannelId());
                if(!siteStatusMap.isEmpty() && siteStatusMap.containsKey(channelId)){
                    siteWithdrawChannelPO.setStatus(siteStatusMap.get(channelId));
                }else{
                    siteWithdrawChannelPO.setStatus(wayStatusMap.get(channelId));
                }
                siteWithdrawChannelPO.setCreatedTime(System.currentTimeMillis());
                channelIds.add(singleNewReqVO.getChannelId());
                batchLists.add(siteWithdrawChannelPO);
            }
        }
        if (!CollectionUtil.isEmpty(batchLists)) {
            //保存站点 增加授权数量
            this.saveBatch(batchLists);
            systemWithdrawChannelService.addAuthNum(channelIds);
        }
        return ResponseVO.success();
    }

    public ResponseVO<Void> enableOrDisable(SiteWithdrawChannelStatusRequestVO siteWithdrawChannelStatusReqVO) {
        LambdaQueryWrapper<SiteWithdrawChannelPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SiteWithdrawChannelPO::getId, siteWithdrawChannelStatusReqVO.getId());
        SiteWithdrawChannelPO siteWithdrawChannelPO = this.baseMapper.selectOne(lqw);
        if (siteWithdrawChannelPO != null) {
            if (Objects.equals(EnableStatusEnum.ENABLE.getCode(), siteWithdrawChannelPO.getStatus())) {
                siteWithdrawChannelPO.setStatus(EnableStatusEnum.DISABLE.getCode());
            }else {
                String channelId = siteWithdrawChannelPO.getChannelId();
                SystemWithdrawChannelPO systemWithdrawChannelPO = systemWithdrawChannelService.getById(channelId);
                if(Objects.equals(EnableStatusEnum.DISABLE.getCode(), systemWithdrawChannelPO.getStatus())){
                    return ResponseVO.fail(ResultCode.ADMIN_CENTER_DISABLE_CHANNEL);
                }
                siteWithdrawChannelPO.setStatus(EnableStatusEnum.ENABLE.getCode());
            }
            siteWithdrawChannelPO.setUpdatedTime(System.currentTimeMillis());
            siteWithdrawChannelPO.setUpdater(siteWithdrawChannelStatusReqVO.getOperatorUserNo());
            this.baseMapper.updateById(siteWithdrawChannelPO);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    public ResponseVO<SiteWithdrawChannelResVO> queryWithdrawPlatformAuthorize(WithdrawChannelReqVO reqVO) {
        SiteWithdrawChannelResVO result = new SiteWithdrawChannelResVO();
        Page<SystemWithdrawChannelPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        Page<WithdrawChannelResVO> resultPage = siteWithdrawChannelRepository
                .queryWithdrawPlatformAuthorize(page, reqVO);
        //获取当前站点下所有vip段位
        List<WithdrawChannelResVO> records = resultPage.getRecords();
        //ResponseVO<List<SiteVIPRankVO>> vipRankListBySiteCode = vipRankApi.getVipRankListBySiteCode(reqVO.getSiteCode());
        ResponseVO<List<CodeValueNoI18VO>> vipRankResp = vipRankApi.getVipRank();
        if (vipRankResp.isOk() && CollectionUtil.isNotEmpty(records)) {
            List<CodeValueNoI18VO> data = vipRankResp.getData();
            Map<String, CodeValueNoI18VO> codeValueMap = data.stream()
                    .collect(Collectors.toMap(
                            CodeValueNoI18VO::getCode,
                            codeValueVO -> codeValueVO
                    ));
            // 处理 records 列表
            records.forEach(record -> {
                String useScope = record.getUseScope();
                if (StrUtil.isNotBlank(useScope)) {
                    // 分割 useScope 字符串，处理每个 code
                    List<CodeValueNoI18VO> newUseScopeList = Arrays.stream(useScope.split(","))
                            .map(codeValueMap::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()); // 收集到新的列表
                    record.setUseScopeList(newUseScopeList);
                }
            });
        }
        List<String> chooseId = Lists.newArrayList();
        List<String> allId = Lists.newArrayList();
        List<String> finalChooseId = chooseId;

        List<SystemWithdrawChannelPO> list = systemWithdrawChannelService.lambdaQuery()
                .eq(SystemWithdrawChannelPO::getWithdrawWayId, reqVO.getWithdrawWayId()).list();

        list.forEach(obj -> allId.add(obj.getId()));
        String siteCode = reqVO.getSiteCode();
        if (StringUtils.isBlank(siteCode)) {
            //新增站点选择通道
            list.forEach(obj -> finalChooseId.add(obj.getId()));
        } /*else {
            //编辑站点选择通道
            this.lambdaQuery()
                    .eq(SiteWithdrawChannelPO::getSiteCode, reqVO.getSiteCode())
                    .eq(SiteWithdrawChannelPO::getWithdrawWayId, reqVO.getWithdrawWayId())
                    .list()
                    .forEach(obj -> finalChooseId.add(obj.getChannelId()));
        }*/
        if (CollectionUtil.isNotEmpty(chooseId)) {
            // 创建一个包含 allId 中所有元素的集合
            Set<String> allIdSet = new HashSet<>(allId);

            // 过滤 chooseId，只保留在 allId 中存在的项
            chooseId = chooseId.stream()
                    .filter(allIdSet::contains)
                    .collect(Collectors.toList());
        }
        result.setChooseID(chooseId);
        result.setAllID(allId);
        result.setPageVO(resultPage);
        return ResponseVO.success(result);
    }

    public ResponseVO<List<SystemWithdrawChannelResponseVO>> queryByCond(SiteWithdrawChannelReqVO siteWithdrawChannelReqVO) {
        LambdaQueryWrapper<SiteWithdrawChannelPO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteWithdrawChannelPO>();
        lambdaQueryWrapper.eq(SiteWithdrawChannelPO::getSiteCode, siteWithdrawChannelReqVO.getSiteCode());
        List<SiteWithdrawChannelPO> siteWithdrawChannelPOS = siteWithdrawChannelRepository.selectList(lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(siteWithdrawChannelPOS)) {
            return ResponseVO.success(Lists.newArrayList());
        }
        List<String> withdrawChannelIds = siteWithdrawChannelPOS.stream().map(SiteWithdrawChannelPO::getChannelId).toList();
        List<SystemWithdrawChannelResponseVO> systemWithdrawChannelResponseVOS = systemWithdrawChannelService.selectByChannelIds(withdrawChannelIds);
        if (org.springframework.util.StringUtils.hasText(siteWithdrawChannelReqVO.getWithdrawTypeCode())) {
            systemWithdrawChannelResponseVOS = systemWithdrawChannelResponseVOS.stream().filter(o -> siteWithdrawChannelReqVO.getWithdrawTypeCode().equals(o.getWithdrawTypeCode())).toList();
        }
        return ResponseVO.success(systemWithdrawChannelResponseVOS);
    }

    public List<SiteWithdrawChannelVO> getListBySiteCodeAndWayId(String siteCode, String depositWithdrawWayId) {
        LambdaQueryWrapper<SiteWithdrawChannelPO> siteQuery = Wrappers.lambdaQuery();
        siteQuery.eq(SiteWithdrawChannelPO::getSiteCode, siteCode)
                //.eq(SiteWithdrawChannelPO::getWithdrawWayId, depositWithdrawWayId)
                .eq(SiteWithdrawChannelPO::getStatus, EnableStatusEnum.ENABLE.getCode());
        List<SiteWithdrawChannelPO> siteChannels = siteWithdrawChannelRepository.selectList(siteQuery);
        return BeanUtil.copyToList(siteChannels, SiteWithdrawChannelVO.class);
    }

    public ResponseVO<Page<SiteWithdrawChannelResponseVO>> selectWithdrawPage(SiteWithdrawChannelRequestVO vo) {
        Page<SiteWithdrawChannelResponseVO> page = new Page<SiteWithdrawChannelResponseVO>(vo.getPageNumber(), vo.getPageSize());
        Page<SiteWithdrawChannelResponseVO> result = siteWithdrawChannelRepository.selectWithdrawPage(page,vo);

        return ResponseVO.success(result);
    }

    public ResponseVO<List<SiteWithdrawChannelResponseVO>> selectBySort(SiteWithdrawChannelRequestVO siteWithdrawChannelReqVO) {
        List<SiteWithdrawChannelResponseVO> result = siteWithdrawChannelRepository.selectBySort(siteWithdrawChannelReqVO);
        return ResponseVO.success(result);
    }

    public ResponseVO<Boolean> batchSaveSort(String userAccount, List<SortNewReqVO> sortNewReqVOS) {
        List<SiteWithdrawChannelPO> batchLists = Lists.newArrayList();
        for (SortNewReqVO sortNewReqVO : sortNewReqVOS) {
            SiteWithdrawChannelPO siteWithdrawChannelPO = new SiteWithdrawChannelPO();
            siteWithdrawChannelPO.setId(sortNewReqVO.getId());
            siteWithdrawChannelPO.setSortOrder(sortNewReqVO.getSortOrder());
            siteWithdrawChannelPO.setUpdatedTime(System.currentTimeMillis());
            siteWithdrawChannelPO.setUpdater(userAccount);
            batchLists.add(siteWithdrawChannelPO);
        }
        this.updateBatchById(batchLists);
        return ResponseVO.success();
    }

    public List<SiteWithdrawChannelChangeVO> getWithdrawChannelBySiteCode(String siteCode){
        //获取总控站方式数据
        return this.getBaseMapper().getWithdrawChannelBySiteCode(siteCode);
    }
}
