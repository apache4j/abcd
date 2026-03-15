package com.cloud.baowang.user.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.user.reponse.GetRegisterInfoByAccountVO;
import com.cloud.baowang.user.api.vo.user.UserRegistrationInfoResVO;
import com.cloud.baowang.user.api.vo.user.request.UserRegistrationInfoReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteUserRegistrationInfoApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员注册信息 服务")
public interface UserRegistrationInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/userRegistration/api/";

    @Operation(summary = "会员注册信息-分页接口")
    @PostMapping(value = PREFIX + "getRegistrationInfo")
    ResponseVO<Page<UserRegistrationInfoResVO>> getRegistrationInfo(@RequestBody UserRegistrationInfoReqVO userRegistrationInfoReqVO);

    @Operation(summary = "会员注册信息-分页查询")
    @PostMapping(value = PREFIX + "listPage")
   Page<UserRegistrationInfoResVO> listPage(@RequestBody UserRegistrationInfoReqVO userRegistrationInfoReqVO);

    @Operation(summary = "会员注册信息-总记录数")
    @PostMapping(value = PREFIX + "getTotalCount")
    Long getTotalCount(@RequestBody UserRegistrationInfoReqVO vo);

    @Operation(summary = "获取会员注册信息")
    @PostMapping(value = PREFIX + "getRegisterInfoByAccount")
    GetRegisterInfoByAccountVO getRegisterInfoByAccount(@RequestParam("userAccount") String userAccount);

    @Operation(summary = "获取会员注册信息")
    @PostMapping(value = PREFIX + "getRegisterInfoByAccountAndSiteCode")
    GetRegisterInfoByAccountVO getRegisterInfoByAccountAndSiteCode(@RequestParam("userAccount") String userAccount,
                                                                   @RequestParam("siteCode")String siteCode);
}