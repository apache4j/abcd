package com.cloud.baowang.user.api.vo.notice.user.usernoticetarget;


import com.cloud.baowang.common.core.vo.base.PageVO;
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
@Schema(description = "点击查看更多会员入参")
public class UserNoticeTargetGetVO  extends PageVO implements Serializable {

    private Integer id;
}
