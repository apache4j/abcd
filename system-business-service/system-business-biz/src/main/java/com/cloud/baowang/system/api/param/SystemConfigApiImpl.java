package com.cloud.baowang.system.api.param;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.business.BusinessConfigVO;
import com.cloud.baowang.system.api.api.SystemConfigApi;
import com.cloud.baowang.system.service.SystemParamService;
import com.cloud.baowang.system.service.business.BusinessConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SystemConfigApiImpl implements SystemConfigApi {

    private final BusinessConfigService businessConfigService;
    private final SystemParamService systemParamService;

    @Override
    public ResponseVO<String> queryMinioDomain() {
        return ResponseVO.success(businessConfigService.queryMinioDomain());
    }

    @Override
    public ResponseVO<List<BusinessConfigVO>> queryBusinessConfig() {
        return ResponseVO.success(businessConfigService.queryBusinessConfig());
    }

    @Override
    public ResponseVO<List<CodeValueVO>> getMainCurrency() {
        List<CodeValueVO> result = systemParamService.getSystemParamByType(CommonConstant.COIN_CODE);

        return ResponseVO.success(result);
    }
}
