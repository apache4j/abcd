package com.cloud.baowang.system.api.api.member;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.member.BusinessRoleAddVO;
import com.cloud.baowang.system.api.vo.member.BusinessRoleDetailVO;
import com.cloud.baowang.system.api.vo.member.BusinessRolePageVO;
import com.cloud.baowang.system.api.vo.member.BusinessRoleQueryVO;
import com.cloud.baowang.system.api.vo.member.BusinessRoleUpdateVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteBusinessRoleApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - businessRole")
public interface BusinessRoleApi {


    String PREFIX = ApiConstants.PREFIX + "/businessRole/api/";

    @PostMapping(PREFIX + "listRole")
    @Schema(description = "角色列表")
    Page<BusinessRolePageVO> listRole(@RequestBody BusinessRoleQueryVO businessRoleQueryVO);

    @PostMapping(PREFIX + "addRole")
    @Schema(description = "角色添加")
    String addRole(@RequestBody BusinessRoleAddVO businessRoleAddVO);

    @PostMapping(PREFIX + "updateRole")
    @Schema(description = "角色修改")
    ResponseVO<String> updateRole(@RequestBody BusinessRoleUpdateVO businessRoleUpdateVO);


    @PostMapping(PREFIX + "deleteRole")
    @Schema(description = "角色删除")
    ResponseVO<Integer> deleteRole(@RequestBody IdVO idVO);

    @PostMapping(PREFIX + "getRoleById")
    @Schema(description = "角色详情")
    BusinessRoleDetailVO getRoleById(@RequestBody IdVO idVO);

    @PostMapping(PREFIX + "checkRoleNameUnique")
    @Schema(description = "角色名唯一校验")
    boolean checkRoleNameUnique(@RequestParam("name") String name);

    @PostMapping(PREFIX + "listAllRole")
    @Schema(description = "角色列表")
    List<BusinessRoleDetailVO> listAllRole(@RequestBody BusinessRoleQueryVO businessRoleQueryVO);


    @PostMapping(PREFIX + "updateRoleStatus")
    @Schema(description = "角色状态修改")
    Integer updateRoleStatus(@RequestBody ChangeStatusVO changeStatusVO);
}
