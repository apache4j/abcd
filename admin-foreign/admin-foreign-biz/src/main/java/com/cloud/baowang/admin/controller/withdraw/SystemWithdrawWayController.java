package com.cloud.baowang.admin.controller.withdraw;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.admin.vo.CurrencyCodeReqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.SystemParamTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithDrawCollectEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SystemWithdrawTypeApi;
import com.cloud.baowang.wallet.api.api.SystemWithdrawWayApi;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayAddVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayDetailResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayStatusVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayUpdateVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawCllectCodeValueVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author: qiqi
 **/
@RestController
@Tag(name = "金流-提现配置-提现方式")
@RequestMapping("/withdraw-way/api")
@AllArgsConstructor
public class SystemWithdrawWayController {

    private final SystemWithdrawWayApi systemWithdrawWayApi;

    private final SystemWithdrawTypeApi systemWithdrawTypeApi;

    private final SystemParamApi systemParamApi;


    @PostMapping("selectPage")
    @Operation(summary = "提现方式分页查询")
    ResponseVO<Page<SystemWithdrawWayResponseVO>> selectPage(@RequestBody @Validated SystemWithdrawWayRequestVO withdrawWayRequestVO){
        return systemWithdrawWayApi.selectPage(withdrawWayRequestVO);
    }

    @PostMapping("info")
    @Operation(summary = "提现方式详情查询")
    ResponseVO<SystemWithdrawWayDetailResponseVO> info(@RequestBody @Validated IdReqVO idReqVO){
        return systemWithdrawWayApi.info(idReqVO);
    }

    @PostMapping("selectBySort")
    @Operation(summary = "充值方式排序")
    ResponseVO<List<SystemWithdrawWayResponseVO>> selectBySort(@RequestBody @Validated SystemWithdrawWayRequestVO withdrawWayRequestVO){
        return systemWithdrawWayApi.selectBySort(withdrawWayRequestVO);
    }



    @PostMapping("batchSave")
    @Operation(summary = "批量保存充值方式")
    ResponseVO<Boolean> batchSave(@RequestBody List<SortNewReqVO> sortNewReqVOS){
        return systemWithdrawWayApi.batchSave(CurrReqUtils.getAccount(),sortNewReqVOS);
    }

    @PostMapping("insert")
    @Operation(summary = "提现方式新增")
    ResponseVO<Void> insert(@RequestBody @Validated SystemWithdrawWayAddVO withdrawWayAddVO){
        withdrawWayAddVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemWithdrawWayApi.insert(withdrawWayAddVO);
    }

    @PostMapping("update")
    @Operation(summary = "提现方式修改")
    ResponseVO<Void> update(@RequestBody @Validated SystemWithdrawWayUpdateVO withdrawWayUpdateVO){
        withdrawWayUpdateVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemWithdrawWayApi.update(withdrawWayUpdateVO);
    }


    @PostMapping("enableOrDisable")
    @Operation(summary = "提现方式启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody @Validated SystemWithdrawWayStatusVO withdrawWayStatusVO){
        withdrawWayStatusVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemWithdrawWayApi.enableOrDisable(withdrawWayStatusVO);
    }

    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(@RequestBody CurrencyCodeReqVO currencyCodeReqVO) {

        List<CodeValueVO> withdrawTypeEnums = Lists.newArrayList();
        ResponseVO<List<SystemWithdrawTypeResponseVO>> listWithdrawTypeResponseVO=systemWithdrawTypeApi.selectAll();
        if(listWithdrawTypeResponseVO.isOk()){
            List<SystemWithdrawTypeResponseVO> systemWithdrawTypeRespVOS=listWithdrawTypeResponseVO.getData();
            systemWithdrawTypeRespVOS=systemWithdrawTypeRespVOS.stream().filter(o->o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for(SystemWithdrawTypeResponseVO systemWithdrawTypeRespVO:systemWithdrawTypeRespVOS){
                CodeValueVO codeValueVO=new CodeValueVO();
                codeValueVO.setType(systemWithdrawTypeRespVO.getWithdrawTypeCode());
                codeValueVO.setCode(systemWithdrawTypeRespVO.getId());
                codeValueVO.setValue(systemWithdrawTypeRespVO.getWithdrawTypeI18());
                withdrawTypeEnums.add(codeValueVO);
            }
        }

        List<CodeValueVO> withdrawWayEnums = Lists.newArrayList();
        ResponseVO<List<SystemWithdrawWayResponseVO>> listResponseVO=systemWithdrawWayApi.selectAll();
        if(listResponseVO.isOk()){
            List<SystemWithdrawWayResponseVO> systemWithdrawWayResponseVOS=listResponseVO.getData();
            systemWithdrawWayResponseVOS=systemWithdrawWayResponseVOS.stream().filter(o->o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for(SystemWithdrawWayResponseVO systemWithdrawWayResponseVO:systemWithdrawWayResponseVOS){
                CodeValueVO codeValueVO=new CodeValueVO();
                codeValueVO.setType(systemWithdrawWayResponseVO.getCurrencyCode());
                codeValueVO.setCode(systemWithdrawWayResponseVO.getId());
                codeValueVO.setValue(systemWithdrawWayResponseVO.getWithdrawWayI18());
                withdrawWayEnums.add(codeValueVO);
            }
        }
        Map<String, List<CodeValueVO>> result = Maps.newHashMap();
        result.put("withdrawWayEnums", withdrawWayEnums);
        result.put("withdrawTypeEnums", withdrawTypeEnums);
        return ResponseVO.success(result);
    }



    @Operation(summary = "新增-修改下拉框")
    @PostMapping(value = "/getWithdrawTypes")
    public ResponseVO<Map<String, List<WithdrawCllectCodeValueVO>>> getWithdrawTypes() {
        List<String> systemParamTypes=Lists.newArrayList();
        systemParamTypes.add(SystemParamTypeEnum.WITHDRAW_TYPE.getType());
        systemParamTypes.add(SystemParamTypeEnum.WITHDRAW_COLLECT.getType());
        ResponseVO<Map<String, List<CodeValueVO>>> systemParamsRO = systemParamApi.getSystemParamsByList(systemParamTypes);
        Map<String, List<WithdrawCllectCodeValueVO>> resultMap=new HashMap<>();
        if(systemParamsRO.isOk()){
            Map<String, List<CodeValueVO>> responseResultMap= systemParamsRO.getData();
            List<WithdrawCllectCodeValueVO> withdrawCodes= ConvertUtil.entityListToModelList(responseResultMap.get(SystemParamTypeEnum.WITHDRAW_TYPE.getType()),WithdrawCllectCodeValueVO.class);
            List<WithdrawCllectCodeValueVO> withdrawCollectCodes=ConvertUtil.entityListToModelList(responseResultMap.get(SystemParamTypeEnum.WITHDRAW_COLLECT.getType()),WithdrawCllectCodeValueVO.class);
            //必选项
            List<String> list = Arrays.asList(WithDrawCollectEnum.BANK_CARD.getType(),
//                    WithDrawCollectEnum.BANK_NAME.getType(),
//                    WithDrawCollectEnum.BANK_CODE.getType(),
                    WithDrawCollectEnum.SURNAME.getType(),
//                    WithDrawCollectEnum.USER_NAME.getType(),
                    WithDrawCollectEnum.ADDRESS_NO.getType(),WithDrawCollectEnum.USER_ACCOUNT.getType(),WithDrawCollectEnum.ADDRESS_NO.getType(),
                    WithDrawCollectEnum.NETWORK_TYPE.getType());
            for(WithdrawCllectCodeValueVO codeValueVO:withdrawCodes){
                WithdrawTypeEnum withdrawTypeEnum=WithdrawTypeEnum.nameOfCode(codeValueVO.getCode());
               List<WithDrawCollectEnum> withDrawCollectEnums=WithDrawCollectEnum.parseByWalletTyps(withdrawTypeEnum);
               List<WithdrawCllectCodeValueVO> collectCodeVals=Lists.newArrayList();
               //遍历所有提款类型 进行构造信息收集下拉框
                for(WithDrawCollectEnum withDrawCollectEnum:withDrawCollectEnums){
                    WithdrawCllectCodeValueVO collectCodeValue=new WithdrawCllectCodeValueVO();
                    if(list.contains(withDrawCollectEnum.getType())){
                        collectCodeValue.setIsRequired(CommonConstant.business_one);
                    }else {
                        collectCodeValue.setIsRequired(CommonConstant.business_zero);
                    }
                    if(withdrawTypeEnum.getCode().equals(WithdrawTypeEnum.ELECTRONIC_WALLET.getCode())){
                        collectCodeValue.setIsRequired(CommonConstant.business_zero);
                    }
                    collectCodeValue.setType(withdrawTypeEnum.getCode());
                    collectCodeValue.setCode(withDrawCollectEnum.getType());
                    Optional<WithdrawCllectCodeValueVO> withdrawOption=withdrawCollectCodes.stream().filter(o->o.getCode().equals(withDrawCollectEnum.getType())).findFirst();
                    withdrawOption.ifPresent(valueVO -> collectCodeValue.setValue(valueVO.getValue()));
                    collectCodeVals.add(collectCodeValue);
                }
                resultMap.put(withdrawTypeEnum.getCode(),collectCodeVals);
            }
            resultMap.put(SystemParamTypeEnum.WITHDRAW_TYPE.getType(),withdrawCodes);

        }
        return ResponseVO.success(resultMap);
   }


}
