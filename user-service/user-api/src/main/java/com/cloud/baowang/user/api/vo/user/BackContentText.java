package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "回复内容信息")
public class BackContentText implements Serializable {

    @Schema(description = "回复内容")
    public String backContent;

    @Schema(description = "回复人")
    public String backAccount;

    @Schema(description = "回复时间")
    public Long backTime;

    @Schema(description = "是否已读 1 已读 | 0 未读")
    private int isRead;
}
