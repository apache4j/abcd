package com.cloud.baowang.system.api.api.site;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.adminLogin.AdminUpdateVO;
import com.cloud.baowang.system.api.vo.adminLogin.GoogleKeyVO;
import com.cloud.baowang.system.api.vo.adminLogin.PasswordEditVO;
import com.cloud.baowang.system.api.vo.member.AccountSetParamVO;
import com.cloud.baowang.system.api.vo.member.AdminPasswordEditVO;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.member.NameUniqueVO;
import com.cloud.baowang.system.api.vo.site.admin.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteSiteAdminApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - siteAdmin")
public interface SiteAdminApi {

    String PREFIX = ApiConstants.PREFIX + "/siteAdmin/api/";

    @PostMapping(PREFIX + "listAdmin")
    @Schema(description = "职员列表")
    Page<SiteAdminPageVO> listAdmin(@RequestBody SiteAdminQueryVO siteAdminQueryVO);


    @PostMapping(PREFIX + "addAdmin")
    @Schema(description = "职员添加")
    String addAdmin(@RequestBody SiteAdminAddVO siteAdminAddVO);


    @PostMapping(PREFIX + "updateAdmin")
    @Schema(description = "职员修改")
    String updateAdmin(@RequestBody SiteAdminUpdateVO siteAdminUpdateVO);


    @PostMapping(PREFIX + "deleteAdmin")
    @Schema(description = "职员删除")
    Integer deleteAdmin(@RequestBody IdVO idVO);

    @PostMapping(PREFIX + "getAdminById")
    @Schema(description = "职员详情")
    SiteAdminDetailVO getAdminById(@RequestBody IdVO idVO);

    @PostMapping(PREFIX + "checkAdminNameUnique")
    @Schema(description = "职员唯一校验")
    Boolean checkAdminNameUnique(@RequestBody NameUniqueVO nameUniqueVO);

    @PostMapping(PREFIX + "updateAdminStatus")
    @Schema(description = "职员状态修改")
    Integer updateAdminStatus(@RequestBody ChangeStatusVO changeStatusVO);

    @PostMapping(PREFIX + "adminUnLock")
    @Schema(description = "职员解锁")
    Integer  adminUnLock(@RequestBody IdVO idVO);

    @PostMapping(PREFIX + "resetPassword")
    @Schema(description = "职员重置密码")
    Integer  resetPassword(@RequestBody SiteAdminResetPasswordVO siteAdminResetPasswordVO);

    @PostMapping(PREFIX + "getAdminByUserName")
    @Schema(description = "查询职员信息-根据职员名")
    SiteAdminVO getAdminByUserName(@RequestParam("userName") String userName);

    @PostMapping(PREFIX + "getAdminByUserNameAndSite")
    @Schema(description = "查询职员信息-根据职员名")
    SiteAdminVO getAdminByUserNameAndSite(@RequestParam("userName") String userName, @RequestParam("siteCode") String siteCode);


    @PostMapping(PREFIX + "lockAdmin")
    @Schema(description = "职员锁定")
    Integer  lockAdmin(@RequestParam("userName")String userName, @RequestParam("siteCode")String siteCode);

    @PostMapping(PREFIX + "getUserIdsByUseName")
    @Schema(description = "职员IDS-根据职员名")
    List<String> getUserIdsByUseName(@RequestParam("userName")String userName);


    @PostMapping(PREFIX + "getUserNameById")
    @Schema(description = "职员名称-根据职员ID")
    String getUserNameById(@RequestParam("id") String id);

    @PostMapping(PREFIX + "getUserByIds")
    @Schema(description = "职员列表-根据职员IDS")
    List<SiteAdminVO> getUserByIds(@RequestBody List<String> ids);

    @PostMapping(PREFIX + "getRoleIdsByUseName")
    @Schema(description = "职员角色列表-根据职员ID")
    List<String> getRoleIdsByUseName(@RequestParam("adminId") String adminId);


    @PostMapping(PREFIX + "accountSet")
    @Schema(description = "职员账号设置")
    Integer accountSet(@RequestBody AccountSetParamVO accountSetParamVO);

    @PostMapping(PREFIX + "getSiteAdminById")
    @Schema(description = "获取职员对象-根据id")
    SiteAdminVO getSiteAdminById(@RequestParam("id")String id);

    @PostMapping(PREFIX + "updatePassword")
    @Schema(description = "首次修改密码")
    ResponseVO<Boolean> updatePassword(@RequestBody PasswordEditVO passwordEditVO);

    @PostMapping(PREFIX + "updateGoogleKey")
    @Schema(description = "更新google密钥")
    ResponseVO<Boolean> updateGoogleKey(@RequestBody GoogleKeyVO googleKeyVO);

    @PostMapping(PREFIX + "update")
    @Schema(description = "更新")
    ResponseVO<Boolean> update(@RequestBody AdminUpdateVO adminUpdateVO);


    @PostMapping(PREFIX + "resetGoogleAuthKey")
    @Schema(description = "重置谷歌秘钥")
    Integer resetGoogleAuthKey(@RequestBody IdVO idVO);

    @PostMapping(PREFIX + "editPassword")
    @Schema(description = "管理员修改自身密码")
    Integer editPassword(@RequestBody AdminPasswordEditVO editVO);
}
