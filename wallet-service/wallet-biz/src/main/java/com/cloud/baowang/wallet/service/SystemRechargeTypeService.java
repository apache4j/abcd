package com.cloud.baowang.wallet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CacheConstants;
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
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeDetailRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeStatusReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeUpdateReqVO;
import com.cloud.baowang.wallet.po.SystemRechargeTypePO;
import com.cloud.baowang.wallet.repositories.SystemRechargeTypeRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 11:51
 * @Version: V1.0
 **/
@Service
@Slf4j
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SystemRechargeTypeService extends ServiceImpl<SystemRechargeTypeRepository, SystemRechargeTypePO> {

    @Resource
    private I18nApi i18nApi;

    @Resource
    private SystemRechargeTypeService _this;

    @Resource
    private LanguageManagerApi languageManagerApi;

    @Resource
    private SystemParamApi systemParamApi;



    public ResponseVO<List<SystemRechargeTypeRespVO>> selectAllValid() {
        List<SystemRechargeTypeRespVO> rechargeTypeList= _this.selectAll().getData();
        rechargeTypeList=rechargeTypeList.stream().filter(o->o.getStatus().equals(EnableStatusEnum.ENABLE.getCode())).collect(Collectors.toUnmodifiableList());
        return ResponseVO.success(rechargeTypeList);
    }

    //@Cacheable(value = CacheConstants.RECHARGE_TYPE_CACHE, key = CacheConstants.LIST, sync = true)
    public ResponseVO<List<SystemRechargeTypeRespVO>> selectAll() {
        LambdaQueryWrapper<SystemRechargeTypePO> lqw = new LambdaQueryWrapper<SystemRechargeTypePO>();
        List<SystemRechargeTypePO> rechargeTypeList= this.baseMapper.selectList(lqw);
        List<SystemRechargeTypeRespVO> resultLists= Lists.newArrayList();
        for(SystemRechargeTypePO systemRechargeTypePO :rechargeTypeList){
            SystemRechargeTypeRespVO systemRechargeTypeRespVO=new SystemRechargeTypeRespVO();
            BeanUtils.copyProperties(systemRechargeTypePO,systemRechargeTypeRespVO);
            resultLists.add(systemRechargeTypeRespVO);
        }
        return ResponseVO.success(resultLists);
    }
    
    public ResponseVO<Page<SystemRechargeTypeRespVO>> selectPage(SystemRechargeTypeReqVO systemRechargeTypeReqVO) {
        Page<SystemRechargeTypePO> page = new Page<SystemRechargeTypePO>(systemRechargeTypeReqVO.getPageNumber(), systemRechargeTypeReqVO.getPageSize());
        LambdaQueryWrapper<SystemRechargeTypePO> lqw = new LambdaQueryWrapper<SystemRechargeTypePO>();
        if(StringUtils.hasText(systemRechargeTypeReqVO.getCurrencyCode())){
            lqw.eq(SystemRechargeTypePO::getCurrencyCode, systemRechargeTypeReqVO.getCurrencyCode());
        }
        if(StringUtils.hasText(systemRechargeTypeReqVO.getRechargeCode())){
            lqw.eq(SystemRechargeTypePO::getRechargeCode, systemRechargeTypeReqVO.getRechargeCode());
        }
        if(StringUtils.hasText(systemRechargeTypeReqVO.getRechargeType())){
            lqw.like(SystemRechargeTypePO::getRechargeType, systemRechargeTypeReqVO.getRechargeType());
        }
        if(systemRechargeTypeReqVO.getStatus()!=null){
            lqw.eq(SystemRechargeTypePO::getStatus, systemRechargeTypeReqVO.getStatus());
        }
        IPage<SystemRechargeTypePO> systemRechargeTypeIPage =  this.baseMapper.selectPage(page,lqw);
        Page<SystemRechargeTypeRespVO> systemRechargeTypeRespVOPage=new Page<SystemRechargeTypeRespVO>(systemRechargeTypeReqVO.getPageNumber(), systemRechargeTypeReqVO.getPageSize());
        systemRechargeTypeRespVOPage.setTotal(systemRechargeTypeIPage.getTotal());
        systemRechargeTypeRespVOPage.setPages(systemRechargeTypeIPage.getPages());
        List<SystemRechargeTypeRespVO> resultLists= Lists.newArrayList();
        for(SystemRechargeTypePO systemRechargeTypePO :systemRechargeTypeIPage.getRecords()){
            SystemRechargeTypeRespVO systemRechargeTypeRespVO=new SystemRechargeTypeRespVO();
            BeanUtils.copyProperties(systemRechargeTypePO,systemRechargeTypeRespVO);
            resultLists.add(systemRechargeTypeRespVO);
        }
        systemRechargeTypeRespVOPage.setRecords(resultLists);
        return ResponseVO.success(systemRechargeTypeRespVOPage);
    }

    @CacheEvict(value = CacheConstants.RECHARGE_TYPE_CACHE, key = CacheConstants.LIST)
    public ResponseVO<Void> insert(SystemRechargeTypeNewReqVO systemRechargeTypeReqNewVO) {
        //必须传全部语言
        List<LanguageManagerListVO> languageManagerListVOS=languageManagerApi.list().getData();
        Set<String> reqLangSet=systemRechargeTypeReqNewVO.getRechargeTypeI18List().stream().map(o->o.getLanguage()).collect(Collectors.toSet());
        if(languageManagerListVOS.size()!=reqLangSet.size()){
            return ResponseVO.fail(ResultCode.LANG_NOT_FULL);
        }

        LambdaQueryWrapper<SystemRechargeTypePO> lqw = new LambdaQueryWrapper<SystemRechargeTypePO>();
        lqw.eq(SystemRechargeTypePO::getCurrencyCode, systemRechargeTypeReqNewVO.getCurrencyCode());
        lqw.eq(SystemRechargeTypePO::getRechargeType, systemRechargeTypeReqNewVO.getRechargeType());
        SystemRechargeTypePO systemRechargeTypePOOld = this.baseMapper.selectOne(lqw);
        if(systemRechargeTypePOOld ==null){
            String rechargeTypeI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.RECHARGE_TYPE.getCode());

            SystemRechargeTypePO systemRechargeTypePO =new SystemRechargeTypePO();
            BeanUtils.copyProperties(systemRechargeTypeReqNewVO, systemRechargeTypePO);
            systemRechargeTypePO.setStatus(EnableStatusEnum.ENABLE.getCode());
            systemRechargeTypePO.setCreator(systemRechargeTypeReqNewVO.getOperatorUserNo());
            systemRechargeTypePO.setUpdater(systemRechargeTypeReqNewVO.getOperatorUserNo());
            systemRechargeTypePO.setCreatedTime(System.currentTimeMillis());
            systemRechargeTypePO.setUpdatedTime(System.currentTimeMillis());
            systemRechargeTypePO.setRechargeTypeI18(rechargeTypeI18Code);
            this.baseMapper.insert(systemRechargeTypePO);
            //保存到多语言
            i18nApi.insert(I18nMsgBindUtil.bind(rechargeTypeI18Code,systemRechargeTypeReqNewVO.getRechargeTypeI18List()));
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_IS_EXIST);
    }

    @CacheEvict(value = CacheConstants.RECHARGE_TYPE_CACHE, key = CacheConstants.LIST)
    public ResponseVO<Void> updateByInfo(SystemRechargeTypeUpdateReqVO systemRechargeTypeUpdateReqVO) {
        LambdaQueryWrapper<SystemRechargeTypePO> lqw = new LambdaQueryWrapper<SystemRechargeTypePO>();
        lqw.eq(SystemRechargeTypePO::getId, systemRechargeTypeUpdateReqVO.getId());
        SystemRechargeTypePO systemRechargeTypePOOld = this.baseMapper.selectOne(lqw);
        if(systemRechargeTypePOOld !=null){
            BeanUtils.copyProperties(systemRechargeTypeUpdateReqVO, systemRechargeTypePOOld);
            systemRechargeTypePOOld.setUpdater(systemRechargeTypeUpdateReqVO.getOperatorUserNo());
            systemRechargeTypePOOld.setUpdatedTime(System.currentTimeMillis());
            this.baseMapper.updateById(systemRechargeTypePOOld);
            //保存到多语言
            if(!CollectionUtils.isEmpty(systemRechargeTypeUpdateReqVO.getRechargeTypeI18List())){
                i18nApi.update(I18nMsgBindUtil.bind(systemRechargeTypePOOld.getRechargeTypeI18(),systemRechargeTypeUpdateReqVO.getRechargeTypeI18List()));
            }
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    @CacheEvict(value = CacheConstants.RECHARGE_TYPE_CACHE, key = CacheConstants.LIST)
    public ResponseVO<Void> enableOrDisable(SystemRechargeTypeStatusReqVO systemRechargeTypeStatusReqVO) {
        LambdaQueryWrapper<SystemRechargeTypePO> lqw = new LambdaQueryWrapper<SystemRechargeTypePO>();
        lqw.eq(SystemRechargeTypePO::getId, systemRechargeTypeStatusReqVO.getId());
        SystemRechargeTypePO systemRechargeTypePOOld = this.baseMapper.selectOne(lqw);
        if(systemRechargeTypePOOld !=null){
            if(Objects.equals(EnableStatusEnum.ENABLE.getCode(), systemRechargeTypePOOld.getStatus())){
                systemRechargeTypePOOld.setStatus(EnableStatusEnum.DISABLE.getCode());
            }else {
                systemRechargeTypePOOld.setStatus(EnableStatusEnum.ENABLE.getCode());
            }
            systemRechargeTypePOOld.setUpdatedTime(System.currentTimeMillis());
            systemRechargeTypePOOld.setUpdater(systemRechargeTypeStatusReqVO.getOperatorUserNo());
            this.baseMapper.updateById(systemRechargeTypePOOld);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    public ResponseVO<SystemRechargeTypeDetailRespVO> info(IdReqVO idReqVO) {
        LambdaQueryWrapper<SystemRechargeTypePO> lqw = new LambdaQueryWrapper<SystemRechargeTypePO>();
        lqw.eq(SystemRechargeTypePO::getId, idReqVO.getId());
        SystemRechargeTypePO systemRechargeTypePOOld = this.baseMapper.selectOne(lqw);
        if(systemRechargeTypePOOld !=null){
            SystemRechargeTypeDetailRespVO systemRechargeTypeDetailRespVO=new SystemRechargeTypeDetailRespVO();
            BeanUtils.copyProperties(systemRechargeTypePOOld,systemRechargeTypeDetailRespVO);
            return ResponseVO.success(systemRechargeTypeDetailRespVO);
        }
        return ResponseVO.success();
    }

    /**
     * 创建币种时 自动创建充值类型
     * @param currencyCode 币种
     * @return
     */
    public ResponseVO<Boolean> init(String currencyCode) {
        LambdaQueryWrapper<SystemRechargeTypePO> lqw = new LambdaQueryWrapper<SystemRechargeTypePO>();
        lqw.eq(SystemRechargeTypePO::getCurrencyCode, currencyCode);
        long countNum = this.baseMapper.selectCount(lqw);
        if(countNum<=0){
            List<CodeValueVO> codeValueVOS=systemParamApi.getSystemParamByType(CommonConstant.RECHARGE_TYPE).getData();
            List<SystemRechargeTypePO> systemRechargeTypePOS=Lists.newArrayList();
            for(CodeValueVO codeValueVO:codeValueVOS){
                SystemRechargeTypePO systemRechargeTypePO=new SystemRechargeTypePO();
                systemRechargeTypePO.setCurrencyCode(currencyCode);
                systemRechargeTypePO.setRechargeCode(codeValueVO.getCode());
                systemRechargeTypePO.setRechargeType(RechargeTypeEnum.parseName(codeValueVO.getCode()));
                systemRechargeTypePO.setRechargeTypeI18(codeValueVO.getValue());
                systemRechargeTypePO.setSortOrder(1);
                systemRechargeTypePO.setStatus(EnableStatusEnum.ENABLE.getCode());
                systemRechargeTypePO.setCreatedTime(System.currentTimeMillis());
                systemRechargeTypePO.setUpdatedTime(System.currentTimeMillis());
                systemRechargeTypePOS.add(systemRechargeTypePO);
            }
            this.saveBatch(systemRechargeTypePOS);
        }
        return ResponseVO.success(Boolean.TRUE);
    }
}
