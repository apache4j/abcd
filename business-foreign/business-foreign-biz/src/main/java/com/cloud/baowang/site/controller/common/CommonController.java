package com.cloud.baowang.site.controller.common;

import com.cloud.baowang.agent.api.api.AgentMerchantApi;
import com.cloud.baowang.agent.api.vo.merchant.MerchantSecuritySetVO;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "公共controller")
@RestController
@RequestMapping("/common")
@AllArgsConstructor
public class CommonController {


    private final SiteApi siteApi;

    private final AgentMerchantApi agentMerchantApi;


    @GetMapping("getSiteInfo")
    @Operation(summary = "获取当前代理所属站点基础信息")
    public ResponseVO<SiteVO> getSiteInfo() {
        String siteCode = CurrReqUtils.getSiteCode();
        return siteApi.getCustomerSiteInfo(siteCode);
    }


    @Operation(summary = "商务安全中心栏目")
    @PostMapping(value = "/column")
    public ResponseVO<MerchantSecuritySetVO> column() {
        String merchatAccount = CurrReqUtils.getAccount();
        if (!StringUtils.hasLength(merchatAccount)) {
            return ResponseVO.fail(ResultCode.LOGIN_EXPIRE);
        }
        return agentMerchantApi.column(CurrReqUtils.getSiteCode(),merchatAccount);
    }

}
