package com.cloud.baowang.system.api.api;

import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.business.BusinessConfigVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(contextId = "remoteSystemConfigApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - systemConfig")
public interface SystemConfigApi {

    String SYSTEM_PARAM_PREFIX = ApiConstants.PREFIX + "/system-config/api/";

    @GetMapping(SYSTEM_PARAM_PREFIX + "queryMinioDomain")
    @Schema(description = "查询Minio信息")
    ResponseVO<String> queryMinioDomain();

    @GetMapping(SYSTEM_PARAM_PREFIX + "businessConfig")
    @Schema(description = "查询商务信息")
    ResponseVO<List<BusinessConfigVO>> queryBusinessConfig();

    @PostMapping(SYSTEM_PARAM_PREFIX + "getMainCurrency")
    @Schema(description = "查询主货币")
    ResponseVO<List<CodeValueVO>> getMainCurrency();
}