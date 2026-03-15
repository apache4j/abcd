//package com.cloud.baowang.site.controller.agreement;
//
//import com.cloud.baowang.common.core.utils.CurrReqUtils;
//import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.system.api.api.site.agreement.UserAgreementApi;
//import com.cloud.baowang.system.api.vo.site.agreement.UserAgreementEditVO;
//import com.cloud.baowang.system.api.vo.site.agreement.UserAgreementVO;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.AllArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Tag(name = "基础设置-用户协议")
//@RestController
//@RequestMapping("/user-agreement/api")
//@AllArgsConstructor
//public class UserAgreementController {
//    private final UserAgreementApi userAgreementApi;
//
//    @PostMapping("getList")
//    @Operation(summary = "用户协议查询")
//    public ResponseVO<List<UserAgreementVO>> getList() {
//        String siteCode = CurrReqUtils.getSiteCode();
//        return userAgreementApi.getList(siteCode);
//    }
//
//    @PostMapping("save")
//    @Operation(summary = "用户协议保存")
//    public ResponseVO<Boolean> save(@RequestBody UserAgreementEditVO vo) {
//        String updater = CurrReqUtils.getAccount();
//        String siteCode = CurrReqUtils.getSiteCode();
//        vo.setUpdater(updater);
//        vo.setSiteCode(siteCode);
//        return userAgreementApi.save(vo);
//    }
//}
