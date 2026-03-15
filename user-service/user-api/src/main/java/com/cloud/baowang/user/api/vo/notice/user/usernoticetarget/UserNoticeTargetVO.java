package com.cloud.baowang.user.api.vo.notice.user.usernoticetarget;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点击查看更多会员返回")
public class UserNoticeTargetVO implements Serializable {


    @Schema(title ="序号")
    private Long id;
    @Schema(title ="发送会员账号")
    private String userAccount;
}
