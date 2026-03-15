package com.cloud.baowang.wallet.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemConfigApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.user.api.api.vip.SiteVipOptionApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.wallet.api.vo.SiteRechargeChannelVO;
import com.cloud.baowang.wallet.api.vo.recharge.*;
import com.cloud.baowang.wallet.po.SiteRechargeWayPO;
import com.cloud.baowang.wallet.po.SystemRechargeWayPO;
import com.cloud.baowang.wallet.repositories.SiteRechargeWayRepository;
import com.cloud.baowang.wallet.repositories.SystemRechargeWayRepository;
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
 * @Desciption: 站点充值方式
 * @Author: qiqi
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class SiteRechargeWayService extends ServiceImpl<SiteRechargeWayRepository, SiteRechargeWayPO> {

    @Autowired
    private SiteRechargeWayRepository siteRechargeWayRepository;
    @Autowired
    private SystemRechargeWayRepository systemRechargeWayRepository;
    @Autowired
    private SystemRechargeWayService systemRechargeWayService;
    private final SiteRechargeChannelService rechargeChannelService;
    private final SystemConfigApi configApi;

    private final VipGradeApi vipGradeApi;



    public ResponseVO<Void> batchSave(SiteRechargeWayBatchReqVO siteRechargeWayBatchReqVO) {
        List<SiteRechargeWaySingleNewReqVO> reqLists = siteRechargeWayBatchReqVO.getSiteRechargeWaySingleNewReqVOList();
//        if (CollectionUtils.isEmpty(reqLists)) {
//            return ResponseVO.success();
//        }
        //获取站点授权方式数据
        SiteRechargeWayRequestVO vo = new SiteRechargeWayRequestVO();
        vo.setSiteCode(siteRechargeWayBatchReqVO.getSiteCode());
        List<SiteRechargeWayResponseVO> sortList = selectBySort(vo).getData();
        Map<String,Integer> siteWayMap = sortList.stream().collect(Collectors.toMap(SiteRechargeWayResponseVO::getRechargeWayId, SiteRechargeWayResponseVO::getSortOrder, (k1, k2) -> k2));
        //获取总控站方式数据
        List<SystemRechargeWayPO> wayList = systemRechargeWayRepository.selectList(new LambdaQueryWrapper<>());
        Map<String,Integer> wayMap  = wayList.stream().collect(Collectors.toMap(SystemRechargeWayPO::getId, SystemRechargeWayPO::getSortOrder, (k1, k2) -> k2));
        Integer maxSort = sortList.stream().mapToInt(SiteRechargeWayResponseVO::getSortOrder).max().orElse(0);
        LambdaQueryWrapper<SiteRechargeWayPO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteRechargeWayPO>();
        lambdaQueryWrapper.eq(SiteRechargeWayPO::getSiteCode, siteRechargeWayBatchReqVO.getSiteCode());
        this.baseMapper.delete(lambdaQueryWrapper);
        List<SiteRechargeWayPO> batchLists = Lists.newArrayList();
        for (SiteRechargeWaySingleNewReqVO singleNewReqVO : reqLists) {
            SiteRechargeWayPO siteRechargeWayPO = new SiteRechargeWayPO();
            siteRechargeWayPO.setSiteCode(siteRechargeWayBatchReqVO.getSiteCode());
            siteRechargeWayPO.setRechargeWayId(singleNewReqVO.getRechargeWayId());
            siteRechargeWayPO.setFeeType(singleNewReqVO.getFeeType());
            siteRechargeWayPO.setVipGradeUseScope(singleNewReqVO.getVipGradeUseScope());
            siteRechargeWayPO.setWayFeeFixedAmount(singleNewReqVO.getWayFeeFixedAmount());
            siteRechargeWayPO.setWayFee(singleNewReqVO.getWayFee());
            //设置排序 如果之前没有站点方式数据，排序直接取总控排序，如果有站点数据 （原数据方式已存在，取站点自己的排序,之前没有该方式，则排序放在最后）
             if(siteWayMap.isEmpty()){
                siteRechargeWayPO.setSortOrder(wayMap.get(String.valueOf(singleNewReqVO.getRechargeWayId())));
            }else{
                Integer sort = siteWayMap.get(String.valueOf(singleNewReqVO.getRechargeWayId()));
                if(null == sort){
                    maxSort = maxSort+1;
                    siteRechargeWayPO.setSortOrder(maxSort);
                }else{
                    siteRechargeWayPO.setSortOrder(sort);
                }
            }
            Map<String,Integer> siteStatusMap = sortList.stream().collect(Collectors.toMap(SiteRechargeWayResponseVO::getRechargeWayId, SiteRechargeWayResponseVO::getStatus, (k1, k2) -> k2));
            Map<String,Integer> wayStatusMap  = wayList.stream().collect(Collectors.toMap(SystemRechargeWayPO::getId, SystemRechargeWayPO::getStatus, (k1, k2) -> k2));

            String wayId = String.valueOf(singleNewReqVO.getRechargeWayId());
            if(!siteStatusMap.isEmpty() && siteStatusMap.containsKey(wayId)){
                siteRechargeWayPO.setStatus(siteStatusMap.get(wayId));
            }else{
                siteRechargeWayPO.setStatus(wayStatusMap.get(wayId));
            }
            if(SiteHandicapModeEnum.China.getCode().equals(siteRechargeWayBatchReqVO.getHandicapMode())){
                Map<String,SiteRechargeWayResponseVO> siteVipGradeUseScopeMap = sortList.stream().collect(Collectors.toMap(SiteRechargeWayResponseVO::getRechargeWayId, o->o, (k1, k2) -> k2));
                if(!siteVipGradeUseScopeMap.isEmpty() && siteVipGradeUseScopeMap.containsKey(wayId)){
                    siteRechargeWayPO.setVipGradeUseScope(siteVipGradeUseScopeMap.get(wayId).getVipGradeUseScope());
                }else{
                    List<CodeValueNoI18VO> vipList = vipGradeApi.getVipGradeTopTen();
                    String vipGradeUseScope = vipList.stream().map(obj ->obj.getCode()).collect(Collectors.joining(CommonConstant.COMMA));
                    siteRechargeWayPO.setVipGradeUseScope(vipGradeUseScope);
                }
            }
            siteRechargeWayPO.setCreator(siteRechargeWayBatchReqVO.getOperatorUserNo());
            siteRechargeWayPO.setCreatedTime(System.currentTimeMillis());
            batchLists.add(siteRechargeWayPO);
        }
        if (!CollectionUtils.isEmpty(batchLists)){
            this.saveBatch(batchLists);
        }
        return ResponseVO.success();
    }

    public ResponseVO<Void>  enableOrDisable(SiteRechargeWayStatusReqVO siteRechargeWayStatusReqVO) {
        LambdaQueryWrapper<SiteRechargeWayPO> lqw = new LambdaQueryWrapper<SiteRechargeWayPO>();
        lqw.eq(SiteRechargeWayPO::getId, siteRechargeWayStatusReqVO.getId());
        SiteRechargeWayPO siteRechargeWayPOOld = this.baseMapper.selectOne(lqw);
        if (siteRechargeWayPOOld != null) {
            if (Objects.equals(EnableStatusEnum.ENABLE.getCode(), siteRechargeWayPOOld.getStatus())) {
                siteRechargeWayPOOld.setStatus(EnableStatusEnum.DISABLE.getCode());
            } else {
                Long wayId =siteRechargeWayPOOld.getRechargeWayId();
                SystemRechargeWayPO systemRechargeWayPO = systemRechargeWayRepository.selectById(wayId);
                if(Objects.equals(EnableStatusEnum.DISABLE.getCode(), systemRechargeWayPO.getStatus())){
                    return ResponseVO.fail(ResultCode.ADMIN_CENTER_DISABLE_WAY);
                }
                siteRechargeWayPOOld.setStatus(EnableStatusEnum.ENABLE.getCode());
            }
            siteRechargeWayPOOld.setUpdatedTime(System.currentTimeMillis());
            siteRechargeWayPOOld.setUpdater(siteRechargeWayStatusReqVO.getOperatorUserNo());
            this.baseMapper.updateById(siteRechargeWayPOOld);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    public ResponseVO<SiteRechargeAuthorizeResVO> queryDepositAuthorizePage(final RechargeAuthorizeReqVO reqVO) {
        SiteRechargeAuthorizeResVO result = new SiteRechargeAuthorizeResVO();
        Page<SystemRechargeWayPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        Page<RechargeAuthorizeResVO> resultPage = siteRechargeWayRepository.queryDepositAuthorizePage(page, reqVO);
        List<FeeVO> allIdList = Lists.newArrayList();
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
        //系统的存款列表
        List<SystemRechargeWayPO> systemRechargeWayList = systemRechargeWayService.list();
        //存款方式-对应币种map
        Map<String, String> wayCurrencyMap = new HashMap<>();
        //当前站点的存款配置列表
        List<SiteRechargeWayPO> siteRechargeWayPOS = this.lambdaQuery().eq(SiteRechargeWayPO::getSiteCode, reqVO.getSiteCode()).list();

        for (SystemRechargeWayPO obj : systemRechargeWayList) {
            allIdList.add(FeeVO.builder().id(obj.getId()).fee(obj.getWayFee()).feeType(obj.getFeeType()).wayFeeFixedAmount(obj.getWayFeeFixedAmount()).build());
            wayCurrencyMap.put(obj.getId(), obj.getCurrencyCode());
        }

        //判断当前是编辑，并且有过勾选的，这里做一个反查
        if (StringUtils.isNotBlank(reqVO.getSiteCode()) && CollectionUtil.isNotEmpty(siteRechargeWayPOS)) {
            List<Long> rechargeWayIds = siteRechargeWayPOS.stream()
                    .map(SiteRechargeWayPO::getRechargeWayId)
                    .toList();
            List<SiteRechargeChannelVO> channelPOS = rechargeChannelService.selectSiteSystemChannelList(rechargeWayIds, reqVO.getSiteCode());
            Map<String, List<SiteRechargeChannelVO>> map = new HashMap<>();
            if (CollectionUtil.isNotEmpty(channelPOS)) {
                // 根据 wayId 分组
                map = channelPOS.stream()
                        .collect(Collectors.groupingBy(SiteRechargeChannelVO::getWayId));
            }

            List<SiteRechargeWayQueryVO> siteDeposit = new ArrayList<>();
            for (SiteRechargeWayPO siteRechargeWayPO : siteRechargeWayPOS) {
                SiteRechargeWayQueryVO queryVO = new SiteRechargeWayQueryVO();
                queryVO.setRechargeWayId(String.valueOf(siteRechargeWayPO.getRechargeWayId()));
                queryVO.setDepositFee(siteRechargeWayPO.getWayFee());
                queryVO.setFeeType(siteRechargeWayPO.getFeeType());
                queryVO.setWayFeeFixedAmount(siteRechargeWayPO.getWayFeeFixedAmount());
                if (map.containsKey(String.valueOf(siteRechargeWayPO.getRechargeWayId()))) {
                    List<SiteRechargeChannelVO> wayChannel = map.get(String.valueOf(siteRechargeWayPO.getRechargeWayId()));
                    if (CollectionUtil.isNotEmpty(wayChannel)) {
                        queryVO.setPlatform(wayChannel.stream().map(SiteRechargeChannelVO::getChannelId).toList());
                    }
                }
                Long rechargeWayId = siteRechargeWayPO.getRechargeWayId();
                if (wayCurrencyMap.containsKey(String.valueOf(rechargeWayId))) {
                    queryVO.setCurrencyGroup(wayCurrencyMap.get(String.valueOf(rechargeWayId)));
                }
                siteDeposit.add(queryVO);
            }

            result.setSiteDeposit(siteDeposit);

        }
        result.setAllID(allIdList);
        result.setCurrency(reqVO.getCurrency());
        result.setPageVO(resultPage);
        return ResponseVO.success(result);
    }

    public List<SiteRechargeWayPO> getRechargeWayList(String siteCode) {
        LambdaQueryWrapper<SiteRechargeWayPO> channelQuery = new LambdaQueryWrapper<>();
        channelQuery.eq(SiteRechargeWayPO::getSiteCode, siteCode);
        return siteRechargeWayRepository.selectList(channelQuery);
    }

    public List<SiteRechargeWayResVO> queryBySite() {
        return siteRechargeWayRepository.queryBySite();
    }

    public SiteRechargeWayVO queryRechargeWay(String siteCode, String rechargeWayId) {
        LambdaQueryWrapper<SiteRechargeWayPO> channelQuery = new LambdaQueryWrapper<>();
        channelQuery.eq(SiteRechargeWayPO::getSiteCode, siteCode);
        channelQuery.eq(SiteRechargeWayPO::getRechargeWayId, rechargeWayId);
        SiteRechargeWayPO siteRechargeWayPO = siteRechargeWayRepository.selectOne(channelQuery);
        return ConvertUtil.entityToModel(siteRechargeWayPO, SiteRechargeWayVO.class);
    }

    public ResponseVO<Page<SiteRechargeWayResponseVO>> selectRechargePage(SiteRechargeWayRequestVO vo) {

        Page<SiteRechargeWayResponseVO> page = new Page<SiteRechargeWayResponseVO>(vo.getPageNumber(), vo.getPageSize());
        Page<SiteRechargeWayResponseVO> result = siteRechargeWayRepository.selectRechargePage(page,vo);

        List<CodeValueNoI18VO> vipGradeTopTen =  vipGradeApi.getVipGradeTopTen();
        for(SiteRechargeWayResponseVO siteRechargeWayResponseVO :result.getRecords()) {
            String useScope = siteRechargeWayResponseVO.getVipGradeUseScope();
            Map<String, String> vipGradeMap = vipGradeTopTen.stream()
                    .collect(Collectors.toMap(CodeValueNoI18VO::getCode, CodeValueNoI18VO::getValue));
            if (StringUtils.isNotBlank(useScope)) {
                List<String> scope = Arrays.asList(useScope.split(CommonConstant.COMMA));
                String vipGradeUseScope = scope.stream()
                        .filter(vipGradeMap::containsKey)
                        .map(vipGradeMap::get)
                        .collect(Collectors.joining(","));
                siteRechargeWayResponseVO.setVipGradeUseScopeText(vipGradeUseScope);
            }
        }
        return ResponseVO.success(result);

    }

    public ResponseVO<List<SiteRechargeWayResponseVO>> selectBySort(SiteRechargeWayRequestVO siteRechargeWayRequestVO) {
        List<SiteRechargeWayResponseVO> result = siteRechargeWayRepository.selectBySort(siteRechargeWayRequestVO);
        return ResponseVO.success(result);
    }

    public ResponseVO<Boolean> batchSaveSort(String userAccount, List<SortNewReqVO> sortNewReqVOS) {
        List<SiteRechargeWayPO> batchLists = Lists.newArrayList();
        for (SortNewReqVO sortNewReqVO : sortNewReqVOS) {
            SiteRechargeWayPO siteRechargeWayPO = new SiteRechargeWayPO();
            siteRechargeWayPO.setId(sortNewReqVO.getId());
            siteRechargeWayPO.setSortOrder(sortNewReqVO.getSortOrder());
            siteRechargeWayPO.setUpdatedTime(System.currentTimeMillis());
            siteRechargeWayPO.setUpdater(userAccount);
            batchLists.add(siteRechargeWayPO);
        }
        this.updateBatchById(batchLists);
        return ResponseVO.success();
    }

    public List<SiteRechargeWayResChangeVO> getRchargeWayBySiteCodeList(String siteCode){
        //获取总控站方式数据
       return siteRechargeWayRepository.selectRechargeWayList(siteCode);
    }

    public ResponseVO<Boolean> saveVipGradeUseScope(SiteRechargeWayVipUseScopeVO siteRechargeWayVipUseScopeVO) {

        SiteRechargeWayPO siteRechargeWayPO = new SiteRechargeWayPO();
        siteRechargeWayPO.setId(siteRechargeWayVipUseScopeVO.getId());
        siteRechargeWayPO.setVipGradeUseScope(siteRechargeWayVipUseScopeVO.getVipGradeUseScope());
        this.updateById(siteRechargeWayPO);
        return ResponseVO.success();
    }
}
