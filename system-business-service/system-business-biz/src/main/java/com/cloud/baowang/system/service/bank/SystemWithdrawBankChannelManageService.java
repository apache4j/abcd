package com.cloud.baowang.system.service.bank;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.SpringUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.bank.*;
import com.cloud.baowang.system.po.bank.SystemChannelBankRelationPO;
import com.cloud.baowang.system.repositories.bank.SystemChannelBankRelationRepository;
import com.cloud.baowang.wallet.api.api.bank.BankCardManagerApi;
import com.cloud.baowang.wallet.api.vo.bank.BankInfoRspVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 细节,带银行卡信息
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemWithdrawBankChannelManageService extends ServiceImpl<SystemChannelBankRelationRepository, SystemChannelBankRelationPO> {

    private final SystemChannelBankRelationRepository repository;
    private final BankCardManagerApi bankCardManagerApi;

    public ResponseVO<List<BankChannelManageRspVO>> queryChannelBankRelation(String id) {
        LambdaQueryWrapper<SystemChannelBankRelationPO> queryWrapper = Wrappers.lambdaQuery(SystemChannelBankRelationPO.class);
        queryWrapper.eq(SystemChannelBankRelationPO::getConfigId, id);
        List<SystemChannelBankRelationPO> pos = this.list(queryWrapper);
        List<BankChannelManageRspVO> result = new ArrayList<>();
        try {
            result = ConvertUtil.convertListToList(pos, new BankChannelManageRspVO());
        } catch (Exception e) {
            log.error("queryChannelBankRelation - ConvertUtil.convertListToList error: {}", e.getMessage());
        }
        return ResponseVO.success(result);
    }


    public ResponseVO<Void> deleteBankChannelConfig(String id) {
        SystemChannelBankRelationPO bankRelationPO = this.baseMapper.selectById(id);
        repository.deleteById(id);
        LambdaQueryWrapper<SystemChannelBankRelationPO> queryWrapper = Wrappers.lambdaQuery(SystemChannelBankRelationPO.class);
        queryWrapper.eq(StringUtils.isNotEmpty(bankRelationPO.getChannelName()), SystemChannelBankRelationPO::getChannelName, bankRelationPO.getChannelName());
        queryWrapper.eq(StringUtils.isNotEmpty(bankRelationPO.getChannelCode()), SystemChannelBankRelationPO::getChannelCode, bankRelationPO.getChannelCode());
        queryWrapper.eq(StringUtils.isNotEmpty(bankRelationPO.getCurrencyCode()), SystemChannelBankRelationPO::getCurrencyCode, bankRelationPO.getCurrencyCode());
        List<SystemChannelBankRelationPO> pos = this.list(queryWrapper);
        if (pos.isEmpty()) {
            //更新base表
            SystemWithdrawChannelInfoService baseInfo = SpringUtils.getBean(SystemWithdrawChannelInfoService.class);
            baseInfo.updateMappingStatus(bankRelationPO.getConfigId());
        }
        return ResponseVO.success();
    }

    public void deleteChannelConfig(String configId) {
        LambdaUpdateWrapper<SystemChannelBankRelationPO> updateWrapper = Wrappers.lambdaUpdate(SystemChannelBankRelationPO.class);
        updateWrapper.eq(SystemChannelBankRelationPO::getConfigId,configId);
        repository.delete(updateWrapper);
    }

    /**
     * 批量插入 configId关联
     * @param addVO
     * @param id
     */
    public void saveChannelBankRelation(BankChannelManageAddVO addVO, String id,long initTime) {
        LambdaUpdateWrapper<SystemChannelBankRelationPO> delWrapper = Wrappers.lambdaUpdate(SystemChannelBankRelationPO.class);
        delWrapper.eq(SystemChannelBankRelationPO::getChannelCode,addVO.getChannelCode());
        delWrapper.eq(SystemChannelBankRelationPO::getChannelName,addVO.getChannelName());
        delWrapper.eq(SystemChannelBankRelationPO::getCurrencyCode,addVO.getCurrencyCode());
        this.remove(delWrapper);
        List<SystemChannelBankRelationPO> poData = new ArrayList<>();
        List<BankInfoVO> bankInfoList = addVO.getBankInfoVOList();
        String channelCode = addVO.getChannelCode();
        String channelName = addVO.getChannelName();
        String currencyCode = addVO.getCurrencyCode();
        for (BankInfoVO bankInfoVO : bankInfoList) {
            SystemChannelBankRelationPO po = BeanUtil.copyProperties(bankInfoVO, SystemChannelBankRelationPO.class);
            po.setChannelCode(channelCode);
            po.setChannelName(channelName);
            po.setCurrencyCode(currencyCode);
            po.setConfigId(id);
            po.setCreatedTime(initTime);
            po.setCreator(CurrReqUtils.getAccount());
            poData.add(po);
        }
        this.saveBatch(poData);
    }


    /**
     * 编辑 - 删了再存
     * @param editVO
     * @param id
     * @param updatedTime
     */
    public void updateChannelBankRelation(BankChannelManageAddVO editVO, String id, long updatedTime) {
        deleteChannelConfig(id);
        saveChannelBankRelation(editVO,id,updatedTime);
    }

    public ResponseVO<BankChannelManageRspVO> getSystemChannelBankRelation(ChannelBankRelationReqVO reqVO) {
        LambdaQueryWrapper<SystemChannelBankRelationPO> queryWrapper = Wrappers.lambdaQuery(SystemChannelBankRelationPO.class);
        queryWrapper.eq(SystemChannelBankRelationPO::getChannelCode,reqVO.getChannelCode());
        queryWrapper.eq(SystemChannelBankRelationPO::getChannelName,reqVO.getChannelName());
        queryWrapper.eq(SystemChannelBankRelationPO::getCurrencyCode,reqVO.getCurrencyCode());
        queryWrapper.eq(SystemChannelBankRelationPO::getBankCode,reqVO.getBankCode());
        SystemChannelBankRelationPO po = this.baseMapper.selectOne(queryWrapper);
        try {
            BankChannelManageRspVO result = ConvertUtil.entityToModel(po,BankChannelManageRspVO.class);
            return ResponseVO.success(result);
        } catch (Exception e) {
           return ResponseVO.success(null);
        }
    }

    public ResponseVO<Set<BankInfoAdminRspVO>> queryAllChannelBankRelation(BankChannelRelationQueryVO req) {
        Set<BankInfoAdminRspVO> result = new LinkedHashSet<>();
        LambdaQueryWrapper<SystemChannelBankRelationPO> queryWrapper = Wrappers.lambdaQuery(SystemChannelBankRelationPO.class);
        queryWrapper.eq(StringUtils.isNotEmpty(req.getChannelName()), SystemChannelBankRelationPO::getChannelName, req.getChannelName());
        queryWrapper.eq(StringUtils.isNotEmpty(req.getChannelCode()), SystemChannelBankRelationPO::getChannelCode, req.getChannelCode());
        queryWrapper.eq(StringUtils.isNotEmpty(req.getCurrencyCode()), SystemChannelBankRelationPO::getCurrencyCode, req.getCurrencyCode());
        List<SystemChannelBankRelationPO> pos = this.list(queryWrapper);
        List<BankInfoRspVO> bankInfoList = bankCardManagerApi.queryBankInfoByCurrency(req.getCurrencyCode()).getData();
       //查基础表
        if (!pos.isEmpty()) {
            pos.forEach( po -> {
                BankInfoAdminRspVO bankVO = BeanUtil.copyProperties(po,BankInfoAdminRspVO.class);
                result.add(bankVO);
            });
            List<BankInfoRspVO> noRelationList = bankInfoList.stream()
                    .filter(bankInfo -> pos.stream().noneMatch(po -> Objects.equals(bankInfo.getId(), po.getBankId())))
                    .toList();
            if (!noRelationList.isEmpty()) {
                noRelationList.forEach(bank ->{
                    BankInfoAdminRspVO bankRsp = BankInfoAdminRspVO.builder().bankId(bank.getId()).bankName(bank.getBankName()).bankCode(bank.getBankCode()).build();
                    result.add(bankRsp);
                });
            }
        }else {
            bankInfoList.forEach(bank ->{
                BankInfoAdminRspVO bankRsp = BankInfoAdminRspVO.builder().bankId(bank.getId()).bankCode(bank.getBankCode()).bankName(bank.getBankName()).build();
                result.add(bankRsp);
            });
        }
        return ResponseVO.success(result);
    }
}
