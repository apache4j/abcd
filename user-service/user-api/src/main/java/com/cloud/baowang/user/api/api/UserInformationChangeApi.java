package com.cloud.baowang.user.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.UserInformationChange.UserChangeTypesVO;
import com.cloud.baowang.user.api.vo.UserInformationChange.UserInformationChangeReqVO;
import com.cloud.baowang.user.api.vo.UserInformationChange.UserInformationChangeResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteUserInfoChangeApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 会员登录记录")
public interface UserInformationChangeApi {

    String PREFIX = ApiConstants.PREFIX + "/user-info-change/api/";

    @Operation(summary ="查询会员信息变更记录")
    @PostMapping(value = PREFIX + "query")
    ResponseVO<Page<UserInformationChangeResVO>> getUserInformationChange(@RequestBody UserInformationChangeReqVO userInformationChangeReqVO);

    @Operation(summary ="查询会员信息变更记录统计")
    @PostMapping(value = PREFIX + "queryCount")
    Long getUserInformationChangeCount(@RequestBody UserInformationChangeReqVO userInformationChangeReqVO);
}
