package com.cloud.baowang.wallet.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.vip.VIPRankVO;
import com.cloud.baowang.wallet.api.vo.SiteRechargeChannelVO;
import com.cloud.baowang.wallet.api.vo.recharge.*;
import com.cloud.baowang.wallet.po.SiteRechargeChannelPO;
import com.cloud.baowang.wallet.po.SystemRechargeChannelPO;
import com.cloud.baowang.wallet.repositories.SiteRechargeChannelRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Desciption: 站点充值通道
 * @Author: Ford
 * @Date: 2024/7/26 11:51
 * @Version: V1.0
 **/
@Service
@Slf4j
public class SiteRechargeChannelService extends ServiceImpl<SiteRechargeChannelRepository, SiteRechargeChannelPO> {

    @Autowired
    private SiteRechargeChannelRepository siteRechargeChannelRepository;

    @Resource
    private SystemRechargeChannelService systemRechargeChannelService;


    @Autowired
    private VipRankApi vipRankApi;

    @Resource
    private  VipGradeApi vipGradeApi;



    /**
     * 批量保存站点渠道
     *
     * @param siteRechargeChannelBatchReqVO
     * @return
     */
    @Transactional
    public ResponseVO<Void> batchSave(List<SiteRechargeChannelBatchReqVO> siteRechargeChannelBatchReqVO, String siteCode,Integer handicapMode) {
//        if (CollectionUtil.isEmpty(siteRechargeChannelBatchReqVO)) {
//            return ResponseVO.success();
//        }
        //获取站点授权通道数据
        SiteRechargeChannelReqVO vo = new SiteRechargeChannelReqVO();
        vo.setSiteCode(siteCode);
        List<SiteRechargeChannelRespVO> sortList = selectBySort(vo).getData();
       // Map<String,Integer> siteChannelMap = sortList.stream().collect(Collectors.toMap(SiteRechargeChannelRespVO::getChannelId, SiteRechargeChannelRespVO::getSortOrder, (k1, k2) -> k2));
        //获取总控站通道数据
        List<SystemRechargeChannelPO> channelList = systemRechargeChannelService.list();
       // Map<String,Integer> channelMap  = channelList.stream().collect(Collectors.toMap(SystemRechargeChannelPO::getId, SystemRechargeChannelPO::getSortOrder, (k1, k2) -> k2));
        Integer maxSort = sortList.stream().mapToInt(SiteRechargeChannelRespVO::getSortOrder).max().orElse(0);
        LambdaQueryWrapper<SiteRechargeChannelPO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteRechargeChannelPO>();
        lambdaQueryWrapper.eq(SiteRechargeChannelPO::getSiteCode, siteCode);
        List<SiteRechargeChannelPO> siteRechargeChannelPOS = this.baseMapper.selectList(lambdaQueryWrapper);
        if (!CollectionUtils.isEmpty(siteRechargeChannelPOS)) {
            List<String> channelIdsForDb = siteRechargeChannelPOS.stream().map(SiteRechargeChannelPO::getChannelId).toList();
            //删除站点 减少授权数量
            systemRechargeChannelService.subAuthNum(channelIdsForDb);
            this.baseMapper.delete(lambdaQueryWrapper);
        }
        List<SiteRechargeChannelPO> batchLists = Lists.newArrayList();
        List<String> channelIds = Lists.newArrayList();
        for (SiteRechargeChannelBatchReqVO rechargeChannelBatchReqVO : siteRechargeChannelBatchReqVO) {
            String operatorUserNo = rechargeChannelBatchReqVO.getOperatorUserNo();
            List<SiteRechargeChannelSingleNewReqVO> reqLists = rechargeChannelBatchReqVO.getSiteRechargeChannelSingleNewReqVOList();
            for (SiteRechargeChannelSingleNewReqVO singleNewReqVO : reqLists) {
                SiteRechargeChannelPO siteRechargeChannelPO = new SiteRechargeChannelPO();
                siteRechargeChannelPO.setSiteCode(siteCode);
                siteRechargeChannelPO.setChannelId(singleNewReqVO.getChannelId());
                //设置排序 如果之前没有站点通道数据，排序直接取总控排序，如果有站点数据 （原数据通道已存在，取站点自己的排序,之前没有该通道，则排序放在最后）
                Optional<SiteRechargeChannelRespVO> rechargeChannelRespVOOptional = sortList.stream().filter(o->o.getChannelId().equals(singleNewReqVO.getChannelId())).findFirst();
                if(rechargeChannelRespVOOptional.isPresent()){
                    SiteRechargeChannelRespVO rechargeChannelDbOld=rechargeChannelRespVOOptional.get();
                    log.info("根据channelId:{},查询出原始渠道信息:{}",singleNewReqVO.getChannelId(),rechargeChannelDbOld);
                    BeanUtils.copyProperties(rechargeChannelDbOld,siteRechargeChannelPO);
                    siteRechargeChannelPO.setUpdater(operatorUserNo);
                    siteRechargeChannelPO.setUpdatedTime(System.currentTimeMillis());
                }else {
                    log.info("根据channelId:{},没有查询出原始渠道信息",singleNewReqVO.getChannelId());
                    siteRechargeChannelPO.setCreator(operatorUserNo);
                    siteRechargeChannelPO.setCreatedTime(System.currentTimeMillis());
                    siteRechargeChannelPO.setUpdater(operatorUserNo);
                    siteRechargeChannelPO.setUpdatedTime(System.currentTimeMillis());
                    maxSort = maxSort+1;
                    siteRechargeChannelPO.setSortOrder(maxSort);
                }
                //设置状态
                Map<String,Integer> siteStatusMap = sortList.stream().collect(Collectors.toMap(SiteRechargeChannelRespVO::getChannelId, SiteRechargeChannelRespVO::getStatus, (k1, k2) -> k2));
                Map<String,Integer> wayStatusMap  = channelList.stream().collect(Collectors.toMap(SystemRechargeChannelPO::getId, SystemRechargeChannelPO::getStatus, (k1, k2) -> k2));
                String channelId = String.valueOf(singleNewReqVO.getChannelId());
                if(!siteStatusMap.isEmpty() && siteStatusMap.containsKey(channelId)){
                    siteRechargeChannelPO.setStatus(siteStatusMap.get(channelId));
                }else{
                    siteRechargeChannelPO.setStatus(wayStatusMap.get(channelId));
                }
                if(SiteHandicapModeEnum.China.getCode().equals(handicapMode)){
                    Map<String,SiteRechargeChannelRespVO> siteVipGradeUseScopeMap = sortList.stream().collect(Collectors.toMap(SiteRechargeChannelRespVO::getChannelId, o->o, (k1, k2) -> k2));
                    Map<String,SystemRechargeChannelPO> channelVipGradeUseScopeMap = channelList.stream().collect(Collectors.toMap(SystemRechargeChannelPO::getId,  o->o, (k1, k2) -> k2));
                    if(!siteVipGradeUseScopeMap.isEmpty() && siteVipGradeUseScopeMap.containsKey(channelId)){
                        siteRechargeChannelPO.setVipGradeUseScope(siteVipGradeUseScopeMap.get(channelId).getVipGradeUseScope());
                    }else{
                        siteRechargeChannelPO.setVipGradeUseScope(channelVipGradeUseScopeMap.get(channelId).getVipGradeUseScope());
                    }
                }


                channelIds.add(singleNewReqVO.getChannelId());
                log.info("本次需要保存的存款通道信息:{}",siteRechargeChannelPO);
                batchLists.add(siteRechargeChannelPO);
            }
        }

        if (CollectionUtil.isNotEmpty(batchLists) && CollectionUtil.isNotEmpty(channelIds) ) {
            //保存站点 增加授权数量
            this.saveBatch(batchLists);
            systemRechargeChannelService.addAuthNum(channelIds);
        }
        return ResponseVO.success();
    }

    public ResponseVO<Void> enableOrDisable(SiteRechargeChannelStatusReqVO siteRechargeChannelStatusReqVO) {
        LambdaQueryWrapper<SiteRechargeChannelPO> lqw = new LambdaQueryWrapper<SiteRechargeChannelPO>();
        lqw.eq(SiteRechargeChannelPO::getId, siteRechargeChannelStatusReqVO.getId());
        SiteRechargeChannelPO siteRechargeChannelPOOld = this.baseMapper.selectOne(lqw);
        if (siteRechargeChannelPOOld != null) {
            if (Objects.equals(EnableStatusEnum.ENABLE.getCode(), siteRechargeChannelPOOld.getStatus())) {
                siteRechargeChannelPOOld.setStatus(EnableStatusEnum.DISABLE.getCode());
            } else {
                String channelId = siteRechargeChannelPOOld.getChannelId();
                SystemRechargeChannelPO systemRechargeChannelPO = systemRechargeChannelService.getById(channelId);
                if(Objects.equals(EnableStatusEnum.DISABLE.getCode(), systemRechargeChannelPO.getStatus())){
                    return ResponseVO.fail(ResultCode.ADMIN_CENTER_DISABLE_CHANNEL);
                }
                siteRechargeChannelPOOld.setStatus(EnableStatusEnum.ENABLE.getCode());
            }
            siteRechargeChannelPOOld.setUpdatedTime(System.currentTimeMillis());
            siteRechargeChannelPOOld.setUpdater(siteRechargeChannelStatusReqVO.getOperatorUserNo());
            this.baseMapper.updateById(siteRechargeChannelPOOld);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    public ResponseVO<SiteRechargeChannelResVO> queryPlatformAuthorize(final RechargeChannelReqVO reqVO) {
        SiteRechargeChannelResVO result = new SiteRechargeChannelResVO();
        Page<SystemRechargeChannelPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        Page<RechargeChannelResVO> resultPage = siteRechargeChannelRepository
                .queryPlatformAuthorize(page, reqVO);
        List<RechargeChannelResVO> records = resultPage.getRecords();

        ResponseVO<List<CodeValueNoI18VO>> vipRankResp = vipRankApi.getVipRank();
        //获取当前站点下所有会员段位
        //ResponseVO<List<SiteVIPRankVO>> vipRankListBySiteCode = vipRankApi.getVipRankListBySiteCode(reqVO.getSiteCode());
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

        List<SystemRechargeChannelPO> list = systemRechargeChannelService.lambdaQuery()
                .eq(SystemRechargeChannelPO::getRechargeWayId, reqVO.getRechargeWayId()).list();
        list.forEach(obj -> allId.add(obj.getId()));
        if (StringUtils.isBlank(reqVO.getSiteCode())) {
            list.forEach(item -> finalChooseId.add(item.getId()));
        } else {
            ArrayList<Long> rechargeWayIds = new ArrayList<>();
            rechargeWayIds.add(Long.parseLong(reqVO.getRechargeWayId()));
            List<SiteRechargeChannelVO> channelPOS = this.selectSiteSystemChannelList(rechargeWayIds, reqVO.getSiteCode());
            channelPOS.forEach(obj->finalChooseId.add(obj.getChannelId()));
        }
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

    public List<SiteRechargeChannelVO> selectSiteSystemChannelList(List<Long> rechargeWayIds, String siteCode) {
        return siteRechargeChannelRepository.selectSiteSystemChannelList(rechargeWayIds, siteCode);
    }

    public ResponseVO<Page<SiteRechargeChannelRespVO>> selectRechargePage(SiteRechargeChannelReqVO vo) {
        Page<SiteRechargeChannelRespVO> page = new Page<SiteRechargeChannelRespVO>(vo.getPageNumber(), vo.getPageSize());
        Page<SiteRechargeChannelRespVO> result = siteRechargeChannelRepository.selectRechargePage(page,vo);

        List<CodeValueNoI18VO> vipGradeTopTen =  vipGradeApi.getVipGradeTopTen();
        for(SiteRechargeChannelRespVO siteRechargeChannelRespVO :result.getRecords()) {
            String useScope = siteRechargeChannelRespVO.getVipGradeUseScope();
            Map<String, String> vipGradeMap = vipGradeTopTen.stream()
                    .collect(Collectors.toMap(CodeValueNoI18VO::getCode, CodeValueNoI18VO::getValue));
            if (StringUtils.isNotBlank(useScope)) {
                List<String> scope = Arrays.asList(useScope.split(CommonConstant.COMMA));
                String vipGradeUseScope = scope.stream()
                        .filter(vipGradeMap::containsKey)
                        .map(vipGradeMap::get)
                        .collect(Collectors.joining(","));
                siteRechargeChannelRespVO.setVipGradeUseScopeText(vipGradeUseScope);
            }
        }
        return ResponseVO.success(result);

    }

    public ResponseVO<List<SiteRechargeChannelRespVO>> selectBySort(SiteRechargeChannelReqVO siteRechargeChannelReqVO) {
        List<SiteRechargeChannelRespVO> result = siteRechargeChannelRepository.selectBySort(siteRechargeChannelReqVO);
        return ResponseVO.success(result);
    }

    public ResponseVO<Boolean> batchSaveSort(String userAccount, List<SortNewReqVO> sortNewReqVOS) {
        List<SiteRechargeChannelPO> batchLists = Lists.newArrayList();
        for (SortNewReqVO sortNewReqVO : sortNewReqVOS) {
            SiteRechargeChannelPO siteRechargeChannelPO = new SiteRechargeChannelPO();
            siteRechargeChannelPO.setId(sortNewReqVO.getId());
            siteRechargeChannelPO.setSortOrder(sortNewReqVO.getSortOrder());
            siteRechargeChannelPO.setUpdatedTime(System.currentTimeMillis());
            siteRechargeChannelPO.setUpdater(userAccount);
            batchLists.add(siteRechargeChannelPO);
        }
        this.updateBatchById(batchLists);
        return ResponseVO.success();
    }

    public List<SiteRechargeChannelChangeVO> getSiteCodeList(String siteCode){
        return this.baseMapper.selectSiteRechargeChannelChangeVO(siteCode);
    }

    /**
     * 收款信息保存
     * @param siteRechargeChannelRecvInfoVO
     * @return
     */
    public ResponseVO<Boolean> saveReceiveInfo(SiteRechargeChannelRecvInfoVO siteRechargeChannelRecvInfoVO) {
        SiteRechargeChannelPO siteRechargeChannelPO = new SiteRechargeChannelPO();
        siteRechargeChannelPO.setId(siteRechargeChannelRecvInfoVO.getId());
        siteRechargeChannelPO.setRecvBankAccount(siteRechargeChannelRecvInfoVO.getRecvBankAccount());
        siteRechargeChannelPO.setRecvBankBranch(siteRechargeChannelRecvInfoVO.getRecvBankBranch());
        siteRechargeChannelPO.setRecvBankCard(siteRechargeChannelRecvInfoVO.getRecvBankCard());
        siteRechargeChannelPO.setRecvBankCode(siteRechargeChannelRecvInfoVO.getRecvBankCode());
        siteRechargeChannelPO.setRecvUserName(siteRechargeChannelRecvInfoVO.getRecvUserName());
        siteRechargeChannelPO.setRecvBankName(siteRechargeChannelRecvInfoVO.getRecvBankName());
        siteRechargeChannelPO.setRecvQrCode(siteRechargeChannelRecvInfoVO.getRecvQrCode());
        siteRechargeChannelPO.setUpdatedTime(System.currentTimeMillis());
        siteRechargeChannelPO.setUpdater(siteRechargeChannelRecvInfoVO.getCurrentUserNo());
        this.updateById(siteRechargeChannelPO);
        return ResponseVO.success();
    }

    public ResponseVO<Boolean> saveVipGradeUseScope(SiteRechargeChangeVipUseScopeVO siteRechargeChangeVipUseScopeVO) {
        SiteRechargeChannelPO siteRechargeChannelPO = new SiteRechargeChannelPO();
        siteRechargeChannelPO.setId(siteRechargeChangeVipUseScopeVO.getId());
        siteRechargeChannelPO.setVipGradeUseScope(siteRechargeChangeVipUseScopeVO.getVipGradeUseScope());
        siteRechargeChannelPO.setUpdater(CurrReqUtils.getAccount());
        this.updateById(siteRechargeChannelPO);
        return ResponseVO.success();
    }
}

