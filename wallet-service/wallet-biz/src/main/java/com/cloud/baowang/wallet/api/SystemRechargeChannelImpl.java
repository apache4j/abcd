package com.cloud.baowang.wallet.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SystemRechargeChannelApi;
import com.cloud.baowang.wallet.api.vo.recharge.*;
import com.cloud.baowang.wallet.po.SystemRechargeChannelPO;
import com.cloud.baowang.wallet.service.SystemRechargeChannelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 09:45
 * @Version: V1.0
 **/
@RestController
@Validated
@Slf4j
public class SystemRechargeChannelImpl implements SystemRechargeChannelApi {
    @Resource
    private SystemRechargeChannelService systemRechargeChannelService;

    @Override
    public ResponseVO<Page<SystemRechargeChannelRespVO>> selectPage(SystemRechargeChannelReqVO systemRechargeChannelReqVO) {
        return systemRechargeChannelService.selectPage(systemRechargeChannelReqVO);
    }

    @Override
    public ResponseVO<List<SystemRechargeChannelRespVO>> selectBySort(SystemRechargeChannelReqVO systemRechargeChannelReqVO) {
        return systemRechargeChannelService.selectBySort(systemRechargeChannelReqVO);
    }


    @Override
    public ResponseVO<Void> insert(SystemRechargeChannelNewReqVO systemRechargeChannelNewReqVO) {
        return systemRechargeChannelService.insert(systemRechargeChannelNewReqVO);
    }

    @Override
    public ResponseVO<Void> update(SystemRechargeChannelUpdateReqVO systemRechargeChannelUpdateReqVO) {
        return systemRechargeChannelService.updateByInfo(systemRechargeChannelUpdateReqVO);
    }

    @Override
    public ResponseVO<Boolean> batchSave(String userAccount, List<SortNewReqVO> sortNewReqVOS) {
        return systemRechargeChannelService.batchSave(userAccount,sortNewReqVOS);
    }

    @Override
    public ResponseVO<Void> enableOrDisable(SystemRechargeChannelStatusReqVO systemRechargeChannelStatusReqVO) {
        return systemRechargeChannelService.enableOrDisable(systemRechargeChannelStatusReqVO);
    }

    @Override
    public SystemRechargeChannelBaseVO getChannelById(IdVO idVO) {
        SystemRechargeChannelPO po = systemRechargeChannelService.getById(idVO.getId());
        return ConvertUtil.entityToModel(po, SystemRechargeChannelBaseVO.class);
    }

    @Override
    public SystemRechargeChannelBaseVO getChannelByCode(ChannelQueryReqVO reqVO) {
        return systemRechargeChannelService.getChannelInfo(reqVO);
    }

    @Override
    public SystemRechargeChannelBaseVO getChannelInfoByMerNo(String channelName, String merchantNo) {
        return systemRechargeChannelService.getChannelInfoByMerNo(channelName, merchantNo);
    }

    @Override
    public SystemRechargeChannelBaseVO getChannelInfoByCurrencyAndWayId(String currencyCode,String rechargeWayId,String siteCode,String channelId){
        return systemRechargeChannelService.getChannelInfoByCurrencyAneWayId(currencyCode,rechargeWayId,siteCode,channelId);
    }


    @Override
    public SystemRechargeChannelBaseVO getChannelInfoByChannelId(String currencyCode,String rechargeWayId,String siteCode,String channelId){
        return systemRechargeChannelService.getChannelInfoByChannelId(currencyCode,rechargeWayId,siteCode,channelId);
    }
    @Override
    public ResponseVO<List<SystemRechargeChannelBaseVO>> getByWayIds(List<String> emptyRechargeWayIds) {
        LambdaQueryWrapper<SystemRechargeChannelPO> query = Wrappers.lambdaQuery();
        query.in(SystemRechargeChannelPO::getRechargeWayId, emptyRechargeWayIds);
        List<SystemRechargeChannelPO> list = systemRechargeChannelService.list(query);
        if (CollectionUtil.isEmpty(list)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        // 转换为 Map
        Map<String, List<SystemRechargeChannelPO>> rechargeWayMap = list.stream()
                .collect(Collectors.groupingBy(SystemRechargeChannelPO::getRechargeWayId));
        for (String emptyRechargeWayId : emptyRechargeWayIds) {
            if (!rechargeWayMap.containsKey(emptyRechargeWayId) || CollectionUtil.isEmpty(rechargeWayMap.get(emptyRechargeWayId))) {
                throw new BaowangDefaultException(ResultCode.DEPOSIT_CHOOSE_ERROR);
            }
        }
        return ResponseVO.success(BeanUtil.copyToList(list, SystemRechargeChannelBaseVO.class));
    }

    @Override
    public ResponseVO<List<SystemRechargeChannelBaseVO>> getByIds(List<String> platformList) {

        LambdaQueryWrapper<SystemRechargeChannelPO> query = Wrappers.lambdaQuery();
        query.in(SystemRechargeChannelPO::getId, platformList);
        List<SystemRechargeChannelPO> list = systemRechargeChannelService.list(query);
        if (CollectionUtil.isEmpty(list)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (platformList.size() != list.size()) {
            throw new BaowangDefaultException(ResultCode.DEPOSIT_CHOOSE_ERROR);
        }
        return ResponseVO.success(BeanUtil.copyToList(list, SystemRechargeChannelBaseVO.class));
    }

    @Override
    public ResponseVO<List<SystemRechargeChannelRespVO>> selectAll() {
        return systemRechargeChannelService.selectAll();
    }
}
