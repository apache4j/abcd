package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.v8.SeamlesswalletResp;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "sh-api", value = ApiConstants.NAME)
@Tag(name = "V8")
public interface V8GameApi {
    String PREFIX = ApiConstants.PREFIX + "/sh/api/";

    @PostMapping(PREFIX + "seamlessWallet")
    SeamlesswalletResp seamlessWallet(@RequestBody HttpServletRequest request);



}
