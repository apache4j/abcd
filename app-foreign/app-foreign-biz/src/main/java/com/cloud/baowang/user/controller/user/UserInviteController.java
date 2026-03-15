package com.cloud.baowang.user.controller.user;

import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.SiteUserInviteRecordApi;
import com.cloud.baowang.user.api.vo.user.invite.UserInviteResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: fangfei
 * @createTime: 2024/11/24 23:25
 * @description:
 */
@Tag(name = "我的页面-邀请好友")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/user-info/invite")
public class UserInviteController {

    private final SiteUserInviteRecordApi siteUserInviteRecordApi;

    @Operation(summary = "邀请好友")
    @PostMapping(value =  "inviteFriend")
    ResponseVO<UserInviteResVO> inviteFriend() {
        String userId = CurrReqUtils.getOneId();
        String siteCode = CurrReqUtils.getSiteCode();
        return ResponseVO.success(siteUserInviteRecordApi.inviteFriend(userId, siteCode));
    }
}
