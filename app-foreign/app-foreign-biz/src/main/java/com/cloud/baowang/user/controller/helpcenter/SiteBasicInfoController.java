package com.cloud.baowang.user.controller.helpcenter;

import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.agreement.HelpCenterManageApi;
import com.cloud.baowang.system.api.vo.site.ContactInfoVO;
import com.cloud.baowang.system.api.vo.site.agreement.BusinessBasicClientVO;
import com.cloud.baowang.system.api.vo.site.tutorial.SiteBasicVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "帮助中心")
@RestController
@RequestMapping("/app-helpCenter/api")
@AllArgsConstructor
@Slf4j
public class SiteBasicInfoController {
    private final HelpCenterManageApi helpCenterManageApi;

    @PostMapping("getHelpCenterConfigList")
    @Operation(summary = "获取站点基础信息配置")
    public ResponseVO<List<SiteBasicVo>> getHelpCenterConfigList() {
        return helpCenterManageApi.getSiteBasicInfo();
    }

    @PostMapping("getContactInfo")
    @Operation(summary = "加入我们")
    public ResponseVO<List<ContactInfoVO>> getContactInfo() {
        return helpCenterManageApi.getContactInfo();
    }


    @PostMapping("getBusinessBasicInfoClient")
    @Operation(summary = "获取商务专员信息")
    public ResponseVO<List<BusinessBasicClientVO>> getBusinessBasicInfoClient() {
        return helpCenterManageApi.getBusinessBasicInfoClient(CurrReqUtils.getSiteCode());
    }


//    @GetMapping("getSingleSpecificInfo")
//    @Operation(summary = "PC获取单个标签具体信息")
//    public ResponseVO<UserAgreementVO> getSingleSpecificInfo(@RequestParam("code") Integer code) {
//        return helpCenterManageApi.getSingleSpecificInfo(code);
//    }

}
