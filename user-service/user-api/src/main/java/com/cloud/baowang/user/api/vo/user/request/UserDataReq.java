package com.cloud.baowang.user.api.vo.user.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDataReq {

    @Schema(description = "游戏ID")
    private String gameId;


}
