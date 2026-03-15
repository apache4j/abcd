package com.cloud.baowang.user.vo;

import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipOptionVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "VIP升级对象")
public class VipUpGradeAwardVO {

    SiteVipOptionVO siteVipOptionVO;
    UserInfoVO userInfoVO;
}
