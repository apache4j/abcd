package com.cloud.baowang.system.api.api.member;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoAddVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoVO;
import com.cloud.baowang.system.api.vo.member.UserLoginRequestVO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteBusinessLoginInfoApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - businessLoginInfo")
public interface BusinessLoginInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/businessLoginInfo/api/";

    @PostMapping(PREFIX + "addLoginInfo")
    @Schema(description = "登录日志添加")
    String addLoginInfo(@RequestBody BusinessLoginInfoAddVO businessLoginInfoAddVO);

    @PostMapping(PREFIX + "queryBusinessLoginInfoPage")
    @Schema(description = "登录日志列表")
    ResponseVO<Page<BusinessLoginInfoVO>> queryBusinessLoginInfoPage(@RequestBody UserLoginRequestVO businessRoleQueryVO);
}
