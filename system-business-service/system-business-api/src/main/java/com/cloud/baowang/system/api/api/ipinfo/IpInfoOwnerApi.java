package com.cloud.baowang.system.api.api.ipinfo;

import com.cloud.baowang.common.core.vo.IPRespVO;
import com.cloud.baowang.common.core.vo.IpApiVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(contextId = "remoteIpInfoOwnerApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - ipinfoOwner")
public interface IpInfoOwnerApi {

    String PREFIX = ApiConstants.PREFIX + "/ipInfo/api/";

    @GetMapping(value = PREFIX + "/maxMind/{ipAddr}")
    @Operation(summary = "根据IP获取归属地信息")
    IPRespVO parseIpAddr(@PathVariable("ipAddr") String ipAddr);


}
