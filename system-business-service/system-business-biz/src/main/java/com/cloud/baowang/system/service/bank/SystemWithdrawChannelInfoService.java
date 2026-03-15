package com.cloud.baowang.system.service.bank;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.BankCodeStatusEnums;
import com.cloud.baowang.system.api.vo.bank.BankChannelInfoRspVO;
import com.cloud.baowang.system.api.vo.bank.BankChannelManageAddVO;
import com.cloud.baowang.system.api.vo.bank.BankCodeListReqVO;
import com.cloud.baowang.system.api.vo.bank.BankInfoVO;
import com.cloud.baowang.system.po.bank.SystemChannelBankRelationBasePO;
import com.cloud.baowang.system.repositories.bank.SystemWithdrawChannelInfoRepository;
import com.cloud.baowang.wallet.api.api.bank.BankCardManagerApi;
import com.cloud.baowang.wallet.api.vo.bank.BankInfoRspVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 列表
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemWithdrawChannelInfoService extends ServiceImpl<SystemWithdrawChannelInfoRepository, SystemChannelBankRelationBasePO> {

    private final SystemWithdrawChannelInfoRepository repository;
    private final SystemWithdrawBankChannelManageService channelManageDetailService;
    private final BankCardManagerApi bankCardManagerApi;

    public ResponseVO<Page<BankChannelInfoRspVO>> pageList(BankCodeListReqVO vo) {
        Page<SystemChannelBankRelationBasePO> page = new Page<>(vo.getPageNumber(),vo.getPageSize());
        LambdaQueryWrapper<SystemChannelBankRelationBasePO> queryWrapper = Wrappers.lambdaQuery(SystemChannelBankRelationBasePO.class);
        queryWrapper.like(StringUtils.isNotEmpty(vo.getChannelName()) , SystemChannelBankRelationBasePO::getChannelName, vo.getChannelName());
        queryWrapper.eq(StringUtils.isNotEmpty(vo.getChannelId()), SystemChannelBankRelationBasePO::getChannelId,vo.getChannelId());
        if(StringUtils.isEmpty(vo.getBankCodeStatus()) || vo.getBankCodeStatus().equals(CommonConstant.business_zero_str)){
            List<Integer> statusList = List.of(1, 2, 3);
            queryWrapper.in(SystemChannelBankRelationBasePO::getBankChannelStatus,statusList);
        }else {
            queryWrapper.eq(StringUtils.isNotEmpty(vo.getBankCodeStatus()), SystemChannelBankRelationBasePO::getBankChannelStatus,vo.getBankCodeStatus());
        }
        queryWrapper.eq(StringUtils.isNotEmpty(vo.getCurrency()), SystemChannelBankRelationBasePO::getCurrencyCode,vo.getCurrency());
        page = repository.selectPage(page, queryWrapper);
        IPage<BankChannelInfoRspVO> convert = page.convert(item -> {
            BankChannelInfoRspVO rspVO = BeanUtil.copyProperties(item, BankChannelInfoRspVO.class);
            String statusText = BankCodeStatusEnums.nameOfCode(item.getBankChannelStatus());
            rspVO.setBankChannelStatusText(statusText);
            rspVO.setId(String.valueOf(item.getId()));
            return rspVO;
        });
        return ResponseVO.success(ConvertUtil.toConverPage(convert));

    }

    @Transactional
    public ResponseVO<Boolean> add(BankChannelManageAddVO addVO) {
        String status = getChannelBankRelationStatus(addVO);
        LambdaUpdateWrapper<SystemChannelBankRelationBasePO> delWrapper = Wrappers.lambdaUpdate(SystemChannelBankRelationBasePO.class);
        delWrapper.eq(SystemChannelBankRelationBasePO::getChannelCode,addVO.getChannelCode());
        delWrapper.eq(SystemChannelBankRelationBasePO::getChannelName,addVO.getChannelName());
        delWrapper.eq(SystemChannelBankRelationBasePO::getCurrencyCode,addVO.getCurrencyCode());
        this.remove(delWrapper);
        SystemChannelBankRelationBasePO po = BeanUtil.copyProperties(addVO, SystemChannelBankRelationBasePO.class);
        long initTime = System.currentTimeMillis();
        po.setCreatedTime(initTime);
        po.setUpdatedTime(initTime);
        po.setCreator(CurrReqUtils.getAccount());
        po.setUpdater(CurrReqUtils.getAccount());
        po.setBankChannelStatus(status);
        this.save(po);
        //处理细节表
        if (!status.equals(CommonConstant.business_three_str)) {
            channelManageDetailService.saveChannelBankRelation(addVO,po.getId(),initTime);
        }
        return ResponseVO.success(true);
    }

    /**
     * 列表 - 删除整个通道
     * @param id
     * @return
     */
    @Transactional
    public ResponseVO<Void> deleteChannelManage(String id) {
        SystemChannelBankRelationBasePO channelInfoPO = this.baseMapper.selectById(id);
        if (channelInfoPO == null) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        channelManageDetailService.deleteChannelConfig(id);
        repository.deleteById(id);
        return ResponseVO.success();
    }

    /**
     * 编辑-重新提交
     * @param editVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> edit(BankChannelManageAddVO editVO) {
        String status = getChannelBankRelationStatus(editVO);

        SystemChannelBankRelationBasePO channelInfoPO = this.baseMapper.selectById(editVO.getId());
        if (channelInfoPO == null) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        long updatedTime = System.currentTimeMillis();
        channelInfoPO.setBankChannelStatus(status);
        channelInfoPO.setUpdatedTime(updatedTime);
        channelInfoPO.setUpdater(CurrReqUtils.getAccount());
        this.updateById(channelInfoPO);
        channelManageDetailService.updateChannelBankRelation(editVO,channelInfoPO.getId(),updatedTime);
        return ResponseVO.success();
    }


    private String getChannelBankRelationStatus(BankChannelManageAddVO editVO) {
        List<BankInfoVO> bankInfoList = editVO.getBankInfoVOList();
        String status;
        if (bankInfoList == null || bankInfoList.isEmpty()) {
            status = CommonConstant.business_three_str;
        }else {
            boolean dataCheck = bankInfoList.stream().anyMatch(item -> StringUtils.isEmpty(item.getBankChannelMapping())
                    || StringUtils.isEmpty(item.getBankId())
                    || StringUtils.isEmpty(item.getBankName())
                    ||StringUtils.isEmpty(item.getBankCode()));
            if (dataCheck) {
                throw new BaowangDefaultException(ResultCode.PARAM_MISSING);
            }
            List<BankInfoRspVO> bankInfos = bankCardManagerApi.queryBankInfoByCurrency(editVO.getCurrencyCode()).getData();
            status = bankInfoList.size()==bankInfos.size()? CommonConstant.business_one_str :CommonConstant.business_two_str;
        }
        return status;
    }

    public void updateMappingStatus(String configId) {
        LambdaUpdateWrapper<SystemChannelBankRelationBasePO> updateWrapper = Wrappers.lambdaUpdate(SystemChannelBankRelationBasePO.class);
        updateWrapper.set(SystemChannelBankRelationBasePO::getBankChannelStatus,BankCodeStatusEnums.NONE.getCode());
        updateWrapper.eq(SystemChannelBankRelationBasePO::getId,configId);
        this.update(updateWrapper);
    }
}
