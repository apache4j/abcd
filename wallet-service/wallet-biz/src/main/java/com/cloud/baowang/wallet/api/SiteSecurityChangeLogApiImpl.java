package com.cloud.baowang.wallet.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SiteSecurityChangeLogApi;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityChangeLogAllRespVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityChangeLogPageReqVO;
import com.cloud.baowang.wallet.service.SiteSecurityChangeLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption: 站点保证金账变记录
 * @Author: Ford
 * @Date: 2025/6/27 20:52
 * @Version: V1.0
 **/
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SiteSecurityChangeLogApiImpl implements SiteSecurityChangeLogApi {
    private final SiteSecurityChangeLogService siteSecurityChangeLogService;
    @Override
    public ResponseVO<SiteSecurityChangeLogAllRespVO> listPage(SiteSecurityChangeLogPageReqVO securityChangeLogPageReqVO) {
        return siteSecurityChangeLogService.listPage(securityChangeLogPageReqVO);
    }
}
