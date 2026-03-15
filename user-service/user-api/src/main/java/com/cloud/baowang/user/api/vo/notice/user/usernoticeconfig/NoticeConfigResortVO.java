package com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(description = "公告显示排序")
public class NoticeConfigResortVO {
    @Schema(title ="操作人")
    private String operator;

    @Schema(title = "排序集合")
    @NotNull(message = "排序集合不能为空")
    private List<NoticeSortSelectResponseVO> categoryList;
}
