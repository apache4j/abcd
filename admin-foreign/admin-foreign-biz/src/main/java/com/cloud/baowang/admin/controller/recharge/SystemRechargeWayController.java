package com.cloud.baowang.admin.controller.recharge;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.admin.vo.CurrencyCodeReqVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SystemRechargeTypeApi;
import com.cloud.baowang.wallet.api.api.SystemRechargeWayApi;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayDetailRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayStatusReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayUpdateReqVO;
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
@Tag(name = "金流-充值配置-充值方式")
@RequestMapping("/exchange/rechargeWay")
@AllArgsConstructor
public class SystemRechargeWayController {

    private final SystemRechargeWayApi systemRechargeWayApi;

    private final SystemRechargeTypeApi systemRechargeTypeApi;


    @PostMapping("selectPage")
    @Operation(summary = "充值方式分页查询")
    ResponseVO<Page<SystemRechargeWayRespVO>> selectPage(@RequestBody @Validated SystemRechargeWayReqVO systemRechargeWayReqVO){
        return systemRechargeWayApi.selectPage(systemRechargeWayReqVO);
    }


    @PostMapping("info")
    @Operation(summary = "充值方式详情查询")
    ResponseVO<SystemRechargeWayDetailRespVO> info(@RequestBody IdReqVO idReqVO){
        return systemRechargeWayApi.info(idReqVO);
    }

    @PostMapping("selectBySort")
    @Operation(summary = "充值方式排序")
    ResponseVO<List<SystemRechargeWayRespVO>> selectBySort(@RequestBody @Validated SystemRechargeWayReqVO systemRechargeWayReqVO){
        return systemRechargeWayApi.selectBySort(systemRechargeWayReqVO);
    }



    @PostMapping("batchSave")
    @Operation(summary = "批量保存充值方式")
    ResponseVO<Boolean> batchSave(@RequestBody List<SortNewReqVO> sortNewReqVOS){
        return systemRechargeWayApi.batchSave(CurrReqUtils.getAccount(),sortNewReqVOS);
    }


    @PostMapping("insert")
    @Operation(summary = "充值方式新增")
    ResponseVO<Void> insert(@RequestBody @Validated SystemRechargeWayNewReqVO systemRechargeWayReqNewVO){
        systemRechargeWayReqNewVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemRechargeWayApi.insert(systemRechargeWayReqNewVO);
    }

    @PostMapping("update")
    @Operation(summary = "充值方式修改")
    ResponseVO<Void> update(@RequestBody @Validated SystemRechargeWayUpdateReqVO systemRechargeWayUpdateReqVO){
        systemRechargeWayUpdateReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemRechargeWayApi.update(systemRechargeWayUpdateReqVO);
    }


    @PostMapping("enableOrDisable")
    @Operation(summary = "充值方式启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody @Validated SystemRechargeWayStatusReqVO systemRechargeWayStatusReqVO){
        systemRechargeWayStatusReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemRechargeWayApi.enableOrDisable(systemRechargeWayStatusReqVO);
    }

    @Operation(summary = "下拉框 ENABLE_DISABLE_TYPE 启用禁用状态")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(@RequestBody @Validated CurrencyCodeReqVO currencyCodeReqVO) {

        List<CodeValueVO> rechargeTypeEnums = Lists.newArrayList();
        ResponseVO<List<SystemRechargeTypeRespVO>> listRechargeTypeResponseVO=systemRechargeTypeApi.selectAll();
        if(listRechargeTypeResponseVO.isOk()){
            List<SystemRechargeTypeRespVO> systemRechargeTypeRespVOS=listRechargeTypeResponseVO.getData();
            systemRechargeTypeRespVOS=systemRechargeTypeRespVOS.stream().filter(o->o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for(SystemRechargeTypeRespVO systemRechargeTypeRespVO:systemRechargeTypeRespVOS){
                CodeValueVO codeValueVO=new CodeValueVO();
                codeValueVO.setType(systemRechargeTypeRespVO.getRechargeCode());
                codeValueVO.setCode(systemRechargeTypeRespVO.getId());
                codeValueVO.setValue(systemRechargeTypeRespVO.getRechargeTypeI18());
                rechargeTypeEnums.add(codeValueVO);
            }
        }

        List<CodeValueVO> rechargeWayEnums = Lists.newArrayList();
        ResponseVO<List<SystemRechargeWayRespVO>> listResponseVO=systemRechargeWayApi.selectAll();
        if(listResponseVO.isOk()){
            List<SystemRechargeWayRespVO> systemRechargeWayRespVOS=listResponseVO.getData();
            systemRechargeWayRespVOS=systemRechargeWayRespVOS.stream().filter(o->o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for(SystemRechargeWayRespVO systemRechargeWayRespVO:systemRechargeWayRespVOS){
                CodeValueVO codeValueVO=new CodeValueVO();
                codeValueVO.setType(systemRechargeWayRespVO.getRechargeTypeCode());
                codeValueVO.setCode(systemRechargeWayRespVO.getId());
                codeValueVO.setValue(systemRechargeWayRespVO.getRechargeWayI18());
                rechargeWayEnums.add(codeValueVO);
            }
        }
        Map<String, List<CodeValueVO>> result = Maps.newHashMap();
        result.put("rechargeWayEnums", rechargeWayEnums);
        result.put("rechargeTypeEnums", rechargeTypeEnums);
        return ResponseVO.success(result);
    }






}
