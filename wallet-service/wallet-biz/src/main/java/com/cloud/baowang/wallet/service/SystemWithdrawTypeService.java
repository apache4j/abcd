package com.cloud.baowang.wallet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.wallet.RechargeTypeEnum;
import com.cloud.baowang.common.core.utils.I18nMsgBindUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeAddVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeDetailResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeStatusVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeUpdateVO;
import com.cloud.baowang.wallet.po.SystemWithdrawTypePO;
import com.cloud.baowang.wallet.repositories.SystemWithdrawTypeRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @Author: qiqi
 **/
@Service
@Slf4j
public class SystemWithdrawTypeService extends ServiceImpl<SystemWithdrawTypeRepository, SystemWithdrawTypePO> {

    @Resource
    private SystemParamApi systemParamApi;

    @Resource
    private I18nApi i18nApi;


    public ResponseVO<Page<SystemWithdrawTypeResponseVO>> selectPage(SystemWithdrawTypeRequestVO systemWithdrawTypeRequestVO) {
        Page<SystemWithdrawTypePO> page = new Page<SystemWithdrawTypePO>(systemWithdrawTypeRequestVO.getPageNumber(), systemWithdrawTypeRequestVO.getPageSize());
        LambdaQueryWrapper<SystemWithdrawTypePO> lqw = new LambdaQueryWrapper<SystemWithdrawTypePO>();
        lqw.eq(StringUtils.hasText(systemWithdrawTypeRequestVO.getCurrencyCode()),SystemWithdrawTypePO::getCurrencyCode,systemWithdrawTypeRequestVO.getCurrencyCode());
        lqw.like(StringUtils.hasText(systemWithdrawTypeRequestVO.getWithdrawType()),SystemWithdrawTypePO::getWithdrawType,systemWithdrawTypeRequestVO.getWithdrawType());
        lqw.eq(null != systemWithdrawTypeRequestVO.getStatus(),SystemWithdrawTypePO::getStatus,systemWithdrawTypeRequestVO.getStatus());

        IPage<SystemWithdrawTypePO> withdrawTypePOPage =  this.baseMapper.selectPage(page,lqw);

        Page<SystemWithdrawTypeResponseVO> withdrawTypeResponseVOPage = new Page<>();
        BeanUtils.copyProperties(withdrawTypePOPage, withdrawTypeResponseVOPage);
        List<SystemWithdrawTypeResponseVO> list = withdrawTypePOPage.getRecords().stream().map(record -> {
            SystemWithdrawTypeResponseVO withdrawTypeResponseVO = new SystemWithdrawTypeResponseVO();
            BeanUtils.copyProperties(record, withdrawTypeResponseVO);
//            withdrawTypeResponseVO.setStatusDesc(EnableStatusEnum.nameOfCode(withdrawTypeResponseVO.getStatus()).getName());

            return withdrawTypeResponseVO;
        }).toList();
        withdrawTypeResponseVOPage.setRecords(list);
        return ResponseVO.success(withdrawTypeResponseVOPage);

    }

    public ResponseVO<Void> insert(SystemWithdrawTypeAddVO systemWithdrawTypeAddVO) {
        LambdaQueryWrapper<SystemWithdrawTypePO> lqw = new LambdaQueryWrapper<SystemWithdrawTypePO>();
        lqw.eq(SystemWithdrawTypePO::getCurrencyCode, systemWithdrawTypeAddVO.getCurrencyCode());
        lqw.eq(SystemWithdrawTypePO::getWithdrawType, systemWithdrawTypeAddVO.getWithdrawType());
        SystemWithdrawTypePO systemWithdrawTypePOOld = this.baseMapper.selectOne(lqw);
        if(systemWithdrawTypePOOld ==null){
            String withdrawTypeI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.WITHDRAW_TYPE.getCode());

            SystemWithdrawTypePO systemWithdrawTypePO =new SystemWithdrawTypePO();
            BeanUtils.copyProperties(systemWithdrawTypeAddVO, systemWithdrawTypePO);
            systemWithdrawTypePO.setStatus(EnableStatusEnum.ENABLE.getCode());
            systemWithdrawTypePO.setCreator(systemWithdrawTypeAddVO.getOperatorUserNo());
            systemWithdrawTypePO.setUpdater(systemWithdrawTypeAddVO.getOperatorUserNo());
            systemWithdrawTypePO.setCreatedTime(System.currentTimeMillis());
            systemWithdrawTypePO.setUpdatedTime(System.currentTimeMillis());
            systemWithdrawTypePO.setWithdrawTypeI18(withdrawTypeI18Code);
            this.baseMapper.insert(systemWithdrawTypePO);
            //保存到多语言
            i18nApi.insert(I18nMsgBindUtil.bind(withdrawTypeI18Code,systemWithdrawTypeAddVO.getWithdrawTypeI18List()));
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_IS_EXIST);
    }

    public ResponseVO<Void> updateByInfo(SystemWithdrawTypeUpdateVO systemWithdrawTypeUpdateReqVO) {
        LambdaQueryWrapper<SystemWithdrawTypePO> lqw = new LambdaQueryWrapper<SystemWithdrawTypePO>();
        lqw.eq(SystemWithdrawTypePO::getId, systemWithdrawTypeUpdateReqVO.getId());
        SystemWithdrawTypePO systemWithdrawTypePOOld = this.baseMapper.selectOne(lqw);
        if(systemWithdrawTypePOOld !=null){
            BeanUtils.copyProperties(systemWithdrawTypeUpdateReqVO, systemWithdrawTypePOOld);
            systemWithdrawTypePOOld.setUpdater(systemWithdrawTypeUpdateReqVO.getOperatorUserNo());
            systemWithdrawTypePOOld.setUpdatedTime(System.currentTimeMillis());
            this.baseMapper.updateById(systemWithdrawTypePOOld);
            //保存到多语言
            if(!CollectionUtils.isEmpty(systemWithdrawTypeUpdateReqVO.getWithdrawTypeI18List())){
                i18nApi.update(I18nMsgBindUtil.bind(systemWithdrawTypePOOld.getWithdrawTypeI18(),systemWithdrawTypeUpdateReqVO.getWithdrawTypeI18List()));
            }
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    public ResponseVO<Void> enableOrDisable(SystemWithdrawTypeStatusVO withdrawTypeStatusVO) {
        LambdaQueryWrapper<SystemWithdrawTypePO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SystemWithdrawTypePO::getId, withdrawTypeStatusVO.getId());
        SystemWithdrawTypePO withdrawTypePO= this.baseMapper.selectOne(lqw);
        if(withdrawTypePO!=null){
            if(Objects.equals(EnableStatusEnum.ENABLE.getCode(), withdrawTypePO.getStatus())){
                withdrawTypePO.setStatus(EnableStatusEnum.DISABLE.getCode());
            }else {
                withdrawTypePO.setStatus(EnableStatusEnum.ENABLE.getCode());
            }
            withdrawTypePO.setUpdatedTime(System.currentTimeMillis());
            withdrawTypePO.setUpdater(withdrawTypeStatusVO.getOperatorUserNo());
            this.baseMapper.updateById(withdrawTypePO);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    public ResponseVO<List<SystemWithdrawTypeResponseVO>> selectAllValid() {
        LambdaQueryWrapper<SystemWithdrawTypePO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SystemWithdrawTypePO::getStatus, EnableStatusEnum.ENABLE.getCode());
        List<SystemWithdrawTypePO> systemWithdrawTypePOS =this.baseMapper.selectList(lqw);
        List<SystemWithdrawTypeResponseVO> resultLists= Lists.newArrayList();
        for(SystemWithdrawTypePO systemWithdrawTypePO:systemWithdrawTypePOS){
            SystemWithdrawTypeResponseVO withdrawTypeResponseVO=new SystemWithdrawTypeResponseVO();
            BeanUtils.copyProperties(systemWithdrawTypePO,withdrawTypeResponseVO);
            resultLists.add(withdrawTypeResponseVO);
        }
        return ResponseVO.success(resultLists);
    }

    public ResponseVO<List<SystemWithdrawTypeResponseVO>> selectAll() {
        LambdaQueryWrapper<SystemWithdrawTypePO> lqw = new LambdaQueryWrapper<>();
        List<SystemWithdrawTypePO> systemWithdrawTypePOS =this.baseMapper.selectList(lqw);
        List<SystemWithdrawTypeResponseVO> resultLists= Lists.newArrayList();
        for(SystemWithdrawTypePO systemWithdrawTypePO:systemWithdrawTypePOS){
            SystemWithdrawTypeResponseVO withdrawTypeResponseVO=new SystemWithdrawTypeResponseVO();
            BeanUtils.copyProperties(systemWithdrawTypePO,withdrawTypeResponseVO);
            resultLists.add(withdrawTypeResponseVO);
        }
        return ResponseVO.success(resultLists);
    }

    public ResponseVO<SystemWithdrawTypeDetailResponseVO> info(IdReqVO idReqVO) {
        LambdaQueryWrapper<SystemWithdrawTypePO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SystemWithdrawTypePO::getId, idReqVO.getId());
        SystemWithdrawTypePO systemWithdrawTypePO=this.baseMapper.selectOne(lqw);
        if(systemWithdrawTypePO!=null){
            SystemWithdrawTypeDetailResponseVO systemWithdrawTypeDetailResponseVO=new SystemWithdrawTypeDetailResponseVO();
            BeanUtils.copyProperties(systemWithdrawTypePO,systemWithdrawTypeDetailResponseVO);
            return ResponseVO.success(systemWithdrawTypeDetailResponseVO);
        }
        return ResponseVO.success();

    }


    /**
     * 创建币种时 自动创建提币类型
     * @param currencyCode 币种
     * @return
     */
    public ResponseVO<Boolean> init(String currencyCode) {
        LambdaQueryWrapper<SystemWithdrawTypePO> lqw = new LambdaQueryWrapper<SystemWithdrawTypePO>();
        lqw.eq(SystemWithdrawTypePO::getCurrencyCode, currencyCode);
        long countNum = this.baseMapper.selectCount(lqw);
        if(countNum<=0){
            List<CodeValueVO> codeValueVOS=systemParamApi.getSystemParamByType(CommonConstant.WITHDRAW_TYPE).getData();
            List<SystemWithdrawTypePO> systemWithdrawTypePOS=Lists.newArrayList();
            for(CodeValueVO codeValueVO:codeValueVOS){
                SystemWithdrawTypePO systemWithdrawTypePO=new SystemWithdrawTypePO();
                systemWithdrawTypePO.setCurrencyCode(currencyCode);
                systemWithdrawTypePO.setWithdrawTypeCode(codeValueVO.getCode());
                systemWithdrawTypePO.setWithdrawType(RechargeTypeEnum.parseName(codeValueVO.getCode()));
                systemWithdrawTypePO.setWithdrawTypeI18(codeValueVO.getValue());
                systemWithdrawTypePO.setSortOrder(1);
                systemWithdrawTypePO.setStatus(EnableStatusEnum.ENABLE.getCode());
                systemWithdrawTypePO.setCreatedTime(System.currentTimeMillis());
                systemWithdrawTypePO.setUpdatedTime(System.currentTimeMillis());
                systemWithdrawTypePOS.add(systemWithdrawTypePO);
            }
            this.saveBatch(systemWithdrawTypePOS);
        }
        return ResponseVO.success(Boolean.TRUE);
    }
}
