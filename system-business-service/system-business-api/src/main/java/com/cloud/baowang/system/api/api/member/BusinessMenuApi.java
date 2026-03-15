package com.cloud.baowang.system.api.api.member;


import com.cloud.baowang.system.api.vo.business.BusinessUserMenuRespVO;
import com.cloud.baowang.system.api.vo.member.BusinessMenuAddVO;
import com.cloud.baowang.system.api.vo.member.BusinessMenuQueryVO;
import com.cloud.baowang.system.api.vo.member.BusinessMenuUpdateVO;
import com.cloud.baowang.system.api.vo.member.BusinessMenuVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.common.core.vo.base.IdVO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author qiqi
 */
@FeignClient(contextId = "remoteBusinessMenuApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - businessMenu")
public interface BusinessMenuApi {

    String PREFIX = ApiConstants.PREFIX + "/businessMenu/api/";

    @PostMapping(PREFIX + "addMenu")
    @Schema(description = "菜单添加")
    String addMenu(@RequestBody BusinessMenuAddVO businessMenuAddVO);

    @PostMapping(PREFIX + "updateMenu")
    @Schema(description = "菜单修改")
    String updateMenu(@RequestBody BusinessMenuUpdateVO businessMenuUpdateVO);


    @PostMapping(PREFIX + "deleteMenu")
    @Schema(description = "菜单删除")
    Integer deleteMenu(@RequestBody IdVO idVO);

    @PostMapping(PREFIX + "listTreeMenu")
    @Schema(description = "菜单列表")
    List<BusinessMenuVO> listTreeMenu(@RequestBody BusinessMenuQueryVO businessMenuQueryVO);

    @PostMapping(PREFIX + "listTreeMenuByAdminId")
    @Schema(description = "菜单列表-当前人员")
    List<BusinessMenuVO> listTreeMenuByAdminId(@RequestParam("adminId") String adminId);

    @PostMapping(PREFIX + "checkMenuKeyUnique")
    @Schema(description = "校验menuKey")
    boolean checkMenuKeyUnique(@RequestParam("menuKey") String menuKey);

    @PostMapping(PREFIX + "listMenuByAdminId")
    @Schema(description = "所有菜单列表-当前人员")
    List<BusinessUserMenuRespVO> listAllMenuByAdminId(@RequestParam("adminId") String adminId);
}
