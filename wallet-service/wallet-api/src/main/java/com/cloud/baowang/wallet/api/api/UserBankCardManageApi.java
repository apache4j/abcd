package com.cloud.baowang.wallet.api.api;


import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.userbankcard.EditBankCardInfoVO;
import com.cloud.baowang.wallet.api.vo.userbankcard.RiskEditBankCardInfoVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteUserBankCardManageApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - userBankCardManage")
public interface UserBankCardManageApi {

    String PREFIX = ApiConstants.PREFIX + "/userBankCard/api/";

    @Operation(summary = "根据银行卡号 查询绑定信息")
    @PostMapping(value = PREFIX + "getRiskEditBankCardInfoGetByCardNo")
    ResponseVO<RiskEditBankCardInfoVO> getRiskEditBankCardInfoGetByCardNo(@RequestParam("bankCardNo") String bankCardNo);

    @Operation(summary = "根据银行卡号,siteCode 查询绑定信息")
    @GetMapping(value = PREFIX + "getRiskEditBankCardInfoGetByCardNoAndSiteCode")
    ResponseVO<RiskEditBankCardInfoVO> getRiskEditBankCardInfoGetByCardNoAndSiteCode(@RequestParam("bankCardNo") String bankCardNo,
                                                                                     @RequestParam("siteCode") String siteCode);

    @Operation(summary = "根据id更新银行卡信息")
    @PostMapping(value = PREFIX + "updateBankInfoById")
    ResponseVO<Boolean> updateBankInfoById(@RequestBody EditBankCardInfoVO editBankCardInfoVO);


}
