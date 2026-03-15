package com.cloud.baowang.admin.controller.withdraw;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.admin.vo.CurrencyCodeReqVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SystemWithdrawTypeApi;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeAddVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeDetailResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeStatusVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeUpdateVO;
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

 * @Author: qiqi
 **/
@RestController
@Tag(name = "金流-提现配置-提现类型")
@RequestMapping("/withdraw-type/api")
@AllArgsConstructor
public class SystemWithdrawTypeController {

    private final SystemWithdrawTypeApi systemWithdrawTypeApi;


    @PostMapping("selectPage")
    @Operation(summary = "提现类型分页查询")
    ResponseVO<Page<SystemWithdrawTypeResponseVO>> selectPage(@RequestBody @Validated SystemWithdrawTypeRequestVO withdrawTypeRequestVO){
        return systemWithdrawTypeApi.selectPage(withdrawTypeRequestVO);
    }

    @PostMapping("info")
    @Operation(summary = "提现类型详情查询")
    ResponseVO<SystemWithdrawTypeDetailResponseVO> info(@RequestBody @Validated IdReqVO idReqVO){
        return systemWithdrawTypeApi.info(idReqVO);
    }

    @PostMapping("insert")
    @Operation(summary = "提现类型新增")
    ResponseVO<Void> insert(@RequestBody @Validated SystemWithdrawTypeAddVO withdrawTypeAddVO){
        withdrawTypeAddVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemWithdrawTypeApi.insert(withdrawTypeAddVO);
    }

    @PostMapping("update")
    @Operation(summary = "提现类型修改")
    ResponseVO<Void> update(@RequestBody @Validated SystemWithdrawTypeUpdateVO withdrawTypeUpdateVO){
        withdrawTypeUpdateVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemWithdrawTypeApi.update(withdrawTypeUpdateVO);
    }


    @PostMapping("enableOrDisable")
    @Operation(summary = "提现类型启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody @Validated SystemWithdrawTypeStatusVO withdrawTypeStatusVO){
        withdrawTypeStatusVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemWithdrawTypeApi.enableOrDisable(withdrawTypeStatusVO);
    }

    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(@RequestBody CurrencyCodeReqVO currencyCodeReqVO) {
        List<CodeValueVO> rechargeTypeEnums = Lists.newArrayList();
        ResponseVO<List<SystemWithdrawTypeResponseVO>> listResponseVO= systemWithdrawTypeApi.selectAll();
        if(listResponseVO.isOk()){
            List<SystemWithdrawTypeResponseVO> systemWithdrawTypeResponseVOS=listResponseVO.getData();
            systemWithdrawTypeResponseVOS=systemWithdrawTypeResponseVOS.stream().filter(o->o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for(SystemWithdrawTypeResponseVO systemWithdrawTypeResponseVO:systemWithdrawTypeResponseVOS){
                CodeValueVO codeValueVO=new CodeValueVO();
                codeValueVO.setType(systemWithdrawTypeResponseVO.getCurrencyCode());
                codeValueVO.setCode(systemWithdrawTypeResponseVO.getId());
                codeValueVO.setValue(systemWithdrawTypeResponseVO.getWithdrawTypeI18());
                rechargeTypeEnums.add(codeValueVO);
            }
        }
        Map<String, List<CodeValueVO>> result = Maps.newHashMap();
        result.put("withdrawTypeEnums", rechargeTypeEnums);
        return ResponseVO.success(result);
    }



}
