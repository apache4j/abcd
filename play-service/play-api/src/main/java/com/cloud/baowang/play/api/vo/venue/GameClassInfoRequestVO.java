package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiqi
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "一级分类配置参数对象")
public class GameClassInfoRequestVO extends PageVO {

    @Schema(description = "目录名称")
    private String directoryName;

    @Schema(description = "首页名称")
    private String homeName;

    @Schema(description = "状态,字典CODE:class_status_type")
    private Integer status;

    @Schema(description = "游戏平台id")
    private String id;

    @Schema(description = "一级分类ID")
    private String gameOneId;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建人ID")
    private List<Long> creatorIdList;

    @Schema(description = "创建人")
    private String creatorName;

}
