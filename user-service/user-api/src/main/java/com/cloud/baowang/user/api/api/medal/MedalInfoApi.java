package com.cloud.baowang.user.api.api.medal;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.medal.MedalInfoRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(contextId = "remoteMedalInfoApi", value = ApiConstants.NAME)
@Tag(name = "RPC-勋章信息api")
public interface MedalInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/medalIfo/api";

    @Operation(summary = "勋章信息列表")
    @PostMapping(value = PREFIX+"/listAll")
    ResponseVO<List<MedalInfoRespVO>> listAll();


}
