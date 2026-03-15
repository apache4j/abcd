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
import com.cloud.baowang.wallet.api.api.SystemWithdrawChannelApi;
import com.cloud.baowang.wallet.api.vo.recharge.ChannelQueryReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.*;
import com.cloud.baowang.wallet.po.SystemRechargeChannelPO;
import com.cloud.baowang.wallet.po.SystemWithdrawChannelPO;
import com.cloud.baowang.wallet.service.SystemWithdrawChannelService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: qiqi
 **/
@RestController
@Validated
@Slf4j
@AllArgsConstructor
public class SystemWithdrawChannelImpl implements SystemWithdrawChannelApi {

    private final SystemWithdrawChannelService withdrawChannelService;

    @Override
    public ResponseVO<Page<SystemWithdrawChannelResponseVO>> selectPage(SystemWithdrawChannelRequestVO withdrawChannelRequestVO) {
        return withdrawChannelService.selectPage(withdrawChannelRequestVO);
    }

    @Override
    public ResponseVO<Void> insert(SystemWithdrawChannelAddVO withdrawChannelAddVO) {
        return withdrawChannelService.insert(withdrawChannelAddVO);
    }

    @Override
    public ResponseVO<Void> update(SystemWithdrawChannelUpdateVO withdrawChannelUpdateVO) {
        return withdrawChannelService.updateByInfo(withdrawChannelUpdateVO);
    }

    @Override
    public ResponseVO<Void> enableOrDisable(SystemWithdrawChannelStatusVO withdrawChannelStatusVO) {
        return withdrawChannelService.enableOrDisable(withdrawChannelStatusVO);
    }

    @Override
    public ResponseVO<List<SystemWithdrawChannelResponseVO>> getByWayIds(List<String> emptyRechargeWayIds) {
        LambdaQueryWrapper<SystemWithdrawChannelPO> query = Wrappers.lambdaQuery();
        query.in(SystemWithdrawChannelPO::getWithdrawWayId, emptyRechargeWayIds);
        List<SystemWithdrawChannelPO> list = withdrawChannelService.list(query);
        if (CollectionUtil.isEmpty(list)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        // 转换为 Map
        Map<String, List<SystemWithdrawChannelPO>> rechargeWayMap = list.stream()
                .collect(Collectors.groupingBy(SystemWithdrawChannelPO::getWithdrawWayId));
        for (String emptyRechargeWayId : emptyRechargeWayIds) {
            if (!rechargeWayMap.containsKey(emptyRechargeWayId) || CollectionUtil.isEmpty(rechargeWayMap.get(emptyRechargeWayId))) {
                throw new BaowangDefaultException(ResultCode.DEPOSIT_CHOOSE_ERROR);
            }
        }
        return ResponseVO.success(BeanUtil.copyToList(list, SystemWithdrawChannelResponseVO.class));
    }

    @Override
    public ResponseVO<List<SystemWithdrawChannelResponseVO>> getByIds(List<String> platformList) {
        LambdaQueryWrapper<SystemWithdrawChannelPO> query = Wrappers.lambdaQuery();
        query.in(SystemWithdrawChannelPO::getId, platformList);
        List<SystemWithdrawChannelPO> list = withdrawChannelService.list(query);
        if (CollectionUtil.isEmpty(list)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (platformList.size() != list.size()) {
            throw new BaowangDefaultException(ResultCode.DEPOSIT_CHOOSE_ERROR);
        }
        return ResponseVO.success(BeanUtil.copyToList(list, SystemWithdrawChannelResponseVO.class));
    }

    @Override
    public ResponseVO<List<SystemWithdrawChannelResponseVO>> selectBySort(SystemWithdrawChannelRequestVO withdrawChannelRequestVO) {
        return withdrawChannelService.selectBySort(withdrawChannelRequestVO);
    }

    @Override
    public ResponseVO<Boolean> batchSave(String userAccount, List<SortNewReqVO> sortNewReqVOS) {
        return withdrawChannelService.batchSave(userAccount, sortNewReqVOS);
    }

    @Override
    public SystemWithdrawChannelResponseVO getChannelById(IdVO idVO) {
        SystemWithdrawChannelPO po = withdrawChannelService.getById(idVO.getId());
        return ConvertUtil.entityToModel(po, SystemWithdrawChannelResponseVO.class);
    }

    @Override
    public SystemWithdrawChannelResponseVO getChannelByCode(ChannelQueryReqVO reqVO) {
        return withdrawChannelService.getChannelInfo(reqVO);
    }

    @Override
    public SystemWithdrawChannelResponseVO getChannelInfoByMerNo(String channelName, String merchantNo) {
        return withdrawChannelService.getChannelInfoByMerNo(channelName, merchantNo);
    }

    @Override
    public ResponseVO<List<SystemWithdrawChannelResponseVO>> selectAll() {
        return withdrawChannelService.selectAll();
    }

    @Override
    public List<SiteWithdrawChannelVO> getListByWayId(String wayId,
                                                      String siteCode) {
        return withdrawChannelService.getListByWayId(wayId, siteCode);
    }

    @Override
    public List<SystemWithdrawChannelResponseVO> getChannelByIdAndChannelType(String channelType, String currencyCode, List<String> systemChannelIds) {
        return withdrawChannelService.getChannelByIdAndChannelType(channelType,currencyCode,systemChannelIds);
    }

    @Override
    public ResponseVO<List<SystemWithdrawChannelResponseVO>> selectBankAll(String currencyCode) {
        return withdrawChannelService.selectBankAll(currencyCode);
    }


}
