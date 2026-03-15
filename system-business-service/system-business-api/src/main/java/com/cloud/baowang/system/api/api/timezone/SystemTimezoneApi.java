package com.cloud.baowang.system.api.api.timezone;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.timezone.SystemTimezoneVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(contextId = "systemTimezoneApi", value = ApiConstants.NAME)
@Tag(name = "RPC 系统时区配置api")
public interface SystemTimezoneApi {
    String PREFIX = ApiConstants.PREFIX + "/SystemTimezone/api";

    @GetMapping("/getAll")
    ResponseVO<List<SystemTimezoneVO>> getAll();


}
