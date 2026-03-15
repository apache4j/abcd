package com.cloud.baowang.user.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardPageQueryVO;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardRespVO;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "welfareCenterCnApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - app会员福利中心api")
public interface WelfareCenterCnApi {
    String PREFIX = ApiConstants.PREFIX + "/user-welfareCenterV2/api/";

    @PostMapping(PREFIX + "pageQuery")
    @Operation(summary = "福利中心-分页查询")
    ResponseVO<WelfareCenterRewardResultVO> pageQuery(@RequestBody @Validated WelfareCenterRewardPageQueryVO queryVO);

    @PostMapping(PREFIX + "detail")
    @Operation(summary = "福利中心-分页查询")
    ResponseVO<WelfareCenterRewardRespVO> detail(@RequestBody WelfareCenterRewardPageQueryVO queryVO);

    @GetMapping(PREFIX + "getWaitReceiveByUserId")
    @Operation(summary = "获取某个会员待领取福利总数")
    Integer getWaitReceiveByUserId(@RequestParam("userId")String userId);

}
