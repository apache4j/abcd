package com.cloud.baowang.user.api.vo.notice.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
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
@I18nClass
@Schema(description = "代理通知BO")
public class AgentNoticeRespBO implements Serializable {

    @Schema(title ="通知列表数据")
    private Page<UserNoticeRespVO> noticeList;

    @Schema(title ="公告-总未读数量")
    private Long unReadAnnounceTotal;

    @Schema(title ="消息-总未读数量")
    private Long unReadNoticeTotal;



}
