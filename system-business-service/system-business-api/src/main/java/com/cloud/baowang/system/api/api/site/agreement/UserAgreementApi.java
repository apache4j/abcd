//package com.cloud.baowang.system.api.api.site.agreement;
//
//import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.system.api.enums.ApiConstants;
//import com.cloud.baowang.system.api.vo.site.agreement.UserAgreementEditVO;
//import com.cloud.baowang.system.api.vo.site.agreement.UserAgreementVO;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.List;
//
//
//@FeignClient(contextId = "remoteUserAgreementApi", value = ApiConstants.NAME)
//@Tag(name = "用户协议管理 服务 - UserAgreementApi")
//public interface UserAgreementApi {
//
//    String PREFIX = ApiConstants.PREFIX + "/userAgreement/api/";
//
//    @PostMapping(PREFIX + "pageList")
//    @Operation(summary = "用户协议列表查询")
//    ResponseVO<List<UserAgreementVO>> getList(@RequestParam("siteCode") String siteCode);
//
//    @PostMapping(PREFIX + "save")
//    @Operation(summary = "用户协议保存")
//    ResponseVO<Boolean> save(@RequestBody UserAgreementEditVO vo);
//
//    @PostMapping(PREFIX + "getAgreement")
//    @Operation(summary = "用户协议查询")
//    ResponseVO<UserAgreementVO> getAgreement(@RequestParam("siteCode") String siteCode,
//                                             @RequestParam("language") String language);
//
//}
