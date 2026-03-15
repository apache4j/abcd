package com.cloud.baowang.play.game.sh.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ShDeskStatusListResVO {
    @Schema(description = "分类编码")
    private String hallType;
    @Schema(description = "分类名称")
    private String hallTypeName;
    @Schema(description = "桌台列表")
    private List<ShDeskStatusDetailResVO> deskResponseVOList;
}
