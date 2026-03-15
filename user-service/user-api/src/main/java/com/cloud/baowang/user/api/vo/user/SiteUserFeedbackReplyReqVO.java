package com.cloud.baowang.user.api.vo.user;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "意见反馈站点回复req")
public class SiteUserFeedbackReplyReqVO  implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "ids")
    private List<String> ids;

    @Schema(description = "回复内容")
    private String context;

}
