package com.cloud.baowang.user.api.api;

import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewResVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserLabelVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserListInformationDownVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.UserDetails.*;
import com.cloud.baowang.user.api.vo.user.reponse.UserInformationDownVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @className: UserDetailsApi
 * @author: wade
 * @description: 会员详情接口
 * @date: 2024/3/23 16:34
 */
@FeignClient(contextId = "userDetailsApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 新增会员审核")
public interface UserDetailsApi {
    String PREFIX = ApiConstants.PREFIX + "/userDetail/api/";

    @Operation(summary = "信息编辑-变更类型")
    @PostMapping(PREFIX + "informationEditing")
    ResponseVO updateInformation(@RequestBody UserDetailsReqVO userDetailsReqVO, @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

    @Operation(summary = "信息编辑-下拉框")
    @PostMapping(PREFIX + "getInformationDown")
    ResponseVO<UserInformationDownVO> getInformationDown(@RequestBody UserInfoDwonReqVO userInfoDwonReqVO);

    @Operation(summary = "信息编辑-下拉框")
    @PostMapping(PREFIX + "getUserListInformationDown")
    ResponseVO<UserListInformationDownVO> getUserListInformationDown(@RequestBody UserInfoListDownReqVO vo);

    @PostMapping(PREFIX + "bathUpdateLabel")
    @Operation(summary = "批量修改用户标签")
    void bathUpdateLabel(@RequestBody BathUserReqVO vo,@RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

    @PostMapping(PREFIX + "bathUpdateRemark")
    @Operation(summary = "批量修改用户备注")
    void bathUpdateRemark(@RequestBody BathUserRemarkReqVO vo);

    @PostMapping(PREFIX + "checkUsers")
    @Operation(summary = "检查用户")
    ResponseVO<String> checkUsers(@RequestBody CheckUserReqVO vo);

    @PostMapping(PREFIX + "getBathUsersLabel")
    @Operation(summary = "检查用户")
    ResponseVO<List<UserLabelVO>> getBathUsersLabel(@RequestBody CheckUserReqVO vo);

    @PostMapping(PREFIX + "bathDeleteLabel")
    @Operation(summary = "批量修改用户标签")
    void bathDeleteLabel(@RequestBody BathUserReqVO vo,@RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);

    @PostMapping(PREFIX + "addUserReceiveAccountReview")
    @Operation(summary = "解绑会员收款信息审核")
    ResponseVO<Boolean> addUserReceiveAccountReview(@RequestBody UserDetailsReqVO vo);

    @PostMapping(PREFIX + "getReviewingList")
    @Operation(summary = "获取审核中会员解绑账号信息")
    List<UserAccountUpdateReviewResVO> getReviewingList(@RequestBody List<String> ids);
}
