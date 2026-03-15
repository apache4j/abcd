package com.cloud.baowang.user.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "会员账号审核返回对象")
public class UserAccountUpdateVO {

    private Integer num;
    private String router;
}
