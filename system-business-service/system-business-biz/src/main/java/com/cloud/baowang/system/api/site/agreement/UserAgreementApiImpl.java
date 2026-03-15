//package com.cloud.baowang.system.api.site.agreement;
//
//import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.system.api.api.site.agreement.UserAgreementApi;
//import com.cloud.baowang.system.api.vo.site.agreement.UserAgreementEditVO;
//import com.cloud.baowang.system.api.vo.site.agreement.UserAgreementVO;
//import com.cloud.baowang.system.service.site.agreement.UserAgreementService;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@Slf4j
//@AllArgsConstructor
//@RestController
//public class UserAgreementApiImpl implements UserAgreementApi {
//
//    private final UserAgreementService userAgreementService;
//
//    @Override
//    public ResponseVO<List<UserAgreementVO>> getList(String siteCode) {
//        return ResponseVO.success(userAgreementService.getList(siteCode));
//    }
//
//    @Override
//    public ResponseVO<Boolean> save(UserAgreementEditVO vo) {
//        return ResponseVO.success(userAgreementService.saveAgreement(vo));
//    }
//
//    @Override
//    public ResponseVO<UserAgreementVO> getAgreement(String siteCode, String language) {
//        return ResponseVO.success(userAgreementService.getAgreement(siteCode, language));
//    }
//}
//
