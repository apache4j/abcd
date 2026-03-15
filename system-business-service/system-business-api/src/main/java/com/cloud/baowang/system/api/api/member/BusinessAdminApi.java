package com.cloud.baowang.system.api.api.member;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.business.BusinessStorageMenuRespVO;
import com.cloud.baowang.system.api.vo.adminLogin.AdminUpdateVO;
import com.cloud.baowang.system.api.vo.adminLogin.GoogleBindVO;
import com.cloud.baowang.system.api.vo.member.*;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.system.api.vo.param.SystemSiteSelectQuickEntryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "remoteBusinessAdminApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - businessAdmin")
public interface BusinessAdminApi {

    String BUSINESS_ADMIN_PREFIX = ApiConstants.PREFIX + "/businessAdmin/api/";

    @PostMapping(BUSINESS_ADMIN_PREFIX + "listAdmin")
    @Schema(description = "职员列表")
    Page<BusinessAdminPageVO> listAdmin(@RequestBody BusinessAdminQueryVO businessAdminQueryVO);


    @PostMapping(BUSINESS_ADMIN_PREFIX + "addAdmin")
    @Schema(description = "职员添加")
    String addAdmin(@RequestBody BusinessAdminAddVO businessAdminAddVO);


    @PostMapping(BUSINESS_ADMIN_PREFIX + "updateAdmin")
    @Schema(description = "职员修改")
    String updateAdmin(@RequestBody BusinessAdminUpdateVO businessAdminUpdateVO);


    @PostMapping(BUSINESS_ADMIN_PREFIX + "deleteAdmin")
    @Schema(description = "职员删除")
    Integer deleteAdmin(@RequestBody IdVO idVO);

    @PostMapping(BUSINESS_ADMIN_PREFIX + "getAdminById")
    @Schema(description = "职员详情")
    BusinessAdminDetailVO getAdminById(@RequestBody IdVO idVO);

    @PostMapping(BUSINESS_ADMIN_PREFIX + "checkAdminNameUnique")
    @Schema(description = "职员唯一校验")
    Boolean checkAdminNameUnique(@RequestBody NameUniqueVO nameUniqueVO);

    @PostMapping(BUSINESS_ADMIN_PREFIX + "updateAdminStatus")
    @Schema(description = "职员状态修改")
    Integer updateAdminStatus(@RequestBody ChangeStatusVO changeStatusVO);

    @PostMapping(BUSINESS_ADMIN_PREFIX + "adminUnLock")
    @Schema(description = "职员解锁")
    Integer  adminUnLock(@RequestBody IdVO idVO);

    @PostMapping(BUSINESS_ADMIN_PREFIX + "resetPassword")
    @Schema(description = "职员重置密码")
    Integer  resetPassword(@RequestBody BusinessAdminResetPasswordVO businessAdminResetPasswordVO);

    @PostMapping(BUSINESS_ADMIN_PREFIX + "getAdminByUserName")
    @Schema(description = "查询职员信息-根据职员名")
    BusinessAdminVO getAdminByUserName(@RequestParam("userName") String userName);

    @PostMapping(BUSINESS_ADMIN_PREFIX + "lockAdmin")
    @Schema(description = "职员锁定")
    Integer  lockAdmin(@RequestParam("userName")String userName);

    @PostMapping(BUSINESS_ADMIN_PREFIX + "getUserIdsByUseName")
    @Schema(description = "职员IDS-根据职员名")
    List<String> getUserIdsByUseName(@RequestParam("userName")String userName);


    @PostMapping(BUSINESS_ADMIN_PREFIX + "getUserNameById")
    @Schema(description = "职员名称-根据职员ID")
    String getUserNameById(@RequestParam("id") String id);

    @PostMapping(BUSINESS_ADMIN_PREFIX + "getUserByIds")
    @Schema(description = "职员列表-根据职员IDS")
    List<BusinessAdminVO> getUserByIds(@RequestBody List<String> ids);

    @PostMapping(BUSINESS_ADMIN_PREFIX + "getRoleIdsByUseName")
    @Schema(description = "职员角色列表-根据职员ID")
    List<String> getRoleIdsByUseName(@RequestParam("adminId") String adminId);


    @PostMapping(BUSINESS_ADMIN_PREFIX + "accountSet")
    @Schema(description = "职员账号设置")
    Integer accountSet(@RequestBody AccountSetParamVO accountSetParamVO);

    @PostMapping(BUSINESS_ADMIN_PREFIX + "getBusinessAdminById")
    @Schema(description = "获取职员对象-根据id")
    BusinessAdminVO getBusinessAdminById(@RequestParam("id")String id);

    @PostMapping(BUSINESS_ADMIN_PREFIX + "editPassword")
    @Schema(description = "管理员修改自身密码")
    Integer editPassword(@RequestBody AdminPasswordEditVO editVO);

    @PostMapping(BUSINESS_ADMIN_PREFIX + "updateGoogleKey")
    @Schema(description = "更新google密钥")
    ResponseVO<Boolean> updateGoogleKey(@RequestBody GoogleBindVO googleBindVO);

    @PostMapping(BUSINESS_ADMIN_PREFIX + "resetGoogleAuthKey")
    @Schema(description = "重置谷歌秘钥")
    Integer resetGoogleAuthKey(@RequestBody IdVO idVO);

    @PostMapping(BUSINESS_ADMIN_PREFIX + "update")
    @Schema(description = "更新")
    Boolean update(@RequestBody AdminUpdateVO adminUpdateVO);

    @PostMapping(BUSINESS_ADMIN_PREFIX + "updateQuickButton")
    @Schema(description = "更新首页快捷方式入口")
    Boolean updateQuickButton(@RequestParam("adminId") String adminId, @RequestBody List<BusinessStorageMenuRespVO> quickEntry);

    @PostMapping (BUSINESS_ADMIN_PREFIX + "getQuickButton")
    @Schema(description = "获取已保存常用功能入口")
    List<BusinessStorageMenuRespVO> getQuickButton(@RequestBody SystemSiteSelectQuickEntryParam vo);

    @GetMapping (BUSINESS_ADMIN_PREFIX + "selectUserBySiteCodeAndApiUrl")
    @Schema(description = "获取当前站点下,满足某个接口权限的全部管理员")
    List<String> selectUserBySiteCodeAndApiUrl(@RequestParam("siteCode") String siteCode, @RequestBody List<String> menuKey);

    @GetMapping(BUSINESS_ADMIN_PREFIX+"getUserIdsBySiteCode")
    List<String> getUserIdsBySiteCode(@RequestParam("siteCode") String siteCode);
}
