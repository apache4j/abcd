package com.cloud.baowang.admin.controller.recharge;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.admin.vo.CurrencyCodeReqVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SystemRechargeTypeApi;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeDetailRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeStatusReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeUpdateReqVO;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 17:25
 * @Version: V1.0
 **/
@RestController
@Tag(name = "金流-充值配置-充值类型")
@RequestMapping("/exchange/rechargeType")
@AllArgsConstructor
public class SystemRechargeTypeController {

    private final SystemRechargeTypeApi systemRechargeTypeApi;


    @PostMapping("selectPage")
    @Operation(summary = "充值类型分页查询")
    ResponseVO<Page<SystemRechargeTypeRespVO>> selectPage(@RequestBody SystemRechargeTypeReqVO systemRechargeTypeReqVO){
        return systemRechargeTypeApi.selectPage(systemRechargeTypeReqVO);
    }

    @PostMapping("info")
    @Operation(summary = "充值类型详情查询")
    ResponseVO<SystemRechargeTypeDetailRespVO> info(@RequestBody IdReqVO idReqVO){
        return systemRechargeTypeApi.info(idReqVO);
    }

    @PostMapping("insert")
    @Operation(summary = "充值类型新增")
    ResponseVO<Void> insert(@RequestBody @Validated SystemRechargeTypeNewReqVO systemRechargeTypeReqNewVO){
        systemRechargeTypeReqNewVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemRechargeTypeApi.insert(systemRechargeTypeReqNewVO);
    }

    @PostMapping("update")
    @Operation(summary = "充值类型修改")
    ResponseVO<Void> update(@RequestBody @Validated SystemRechargeTypeUpdateReqVO systemRechargeTypeUpdateReqVO){
        systemRechargeTypeUpdateReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemRechargeTypeApi.update(systemRechargeTypeUpdateReqVO);
    }


    @PostMapping("enableOrDisable")
    @Operation(summary = "充值类型启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody @Validated SystemRechargeTypeStatusReqVO systemRechargeTypeStatusReqVO){
        systemRechargeTypeStatusReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemRechargeTypeApi.enableOrDisable(systemRechargeTypeStatusReqVO);
    }

    @Operation(summary = "下拉框 ENABLE_DISABLE_TYPE 启用禁用状态")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(@RequestBody @Validated CurrencyCodeReqVO currencyCodeReqVO) {

        List<CodeValueVO> rechargeTypeEnums = Lists.newArrayList();
        ResponseVO<List<SystemRechargeTypeRespVO>> listResponseVO=systemRechargeTypeApi.selectAll();
        if(listResponseVO.isOk()){
            List<SystemRechargeTypeRespVO> systemRechargeTypeRespVOS=listResponseVO.getData();
            systemRechargeTypeRespVOS=systemRechargeTypeRespVOS.stream().filter(o->o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for(SystemRechargeTypeRespVO systemRechargeTypeRespVO:systemRechargeTypeRespVOS){
                CodeValueVO codeValueVO=new CodeValueVO();
                codeValueVO.setType(systemRechargeTypeRespVO.getCurrencyCode());
                codeValueVO.setCode(systemRechargeTypeRespVO.getId());
                codeValueVO.setValue(systemRechargeTypeRespVO.getRechargeTypeI18());
                rechargeTypeEnums.add(codeValueVO);
            }
        }
        Map<String, List<CodeValueVO>> result = Maps.newHashMap();
        result.put("rechargeTypeEnums", rechargeTypeEnums);
        return ResponseVO.success(result);
    }



}
