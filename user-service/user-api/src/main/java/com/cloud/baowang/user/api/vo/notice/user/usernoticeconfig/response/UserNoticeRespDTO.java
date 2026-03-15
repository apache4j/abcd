package com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.response;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeUnreadRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "获取用户通知列表的响应对象")
@I18nClass
public class UserNoticeRespDTO implements Serializable {

    @Schema(title ="通知列表数据")
    @I18nField
    private Page<UserNoticeRespVO> userNoticeList;

//    @Schema(title ="未读数量")
//    private List<UserNoticeUnreadRespVO> unreadCountList;
//    @Schema(title ="活动列表数据")
//    private Page<UserNoticeRespVO> userActivityList;

//    @Schema(title ="总未读数量")
//    @I18nField
//    private Integer unreadTotal;



}
