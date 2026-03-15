package com.cloud.baowang.user.api.vo.user.request;

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
@Schema(description = "会员登录设备查询参数")
public class UserDeviceReqVO extends PageVO implements Serializable {

    @Schema(description = "会员账号", hidden = true)
    private String userAccount;
}
