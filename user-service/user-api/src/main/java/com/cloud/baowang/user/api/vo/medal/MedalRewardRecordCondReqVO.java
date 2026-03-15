package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Desciption: 宝箱获取条件参数
 * @Author: Ford
 * @Date: 2024/10/7 16:42
 * @Version: V1.0
 **/
@Schema(description = "根据宝箱代码获取宝箱信息参数")
@Accessors(chain = true)
@Data
public class MedalRewardRecordCondReqVO {
    /**
     * 站点编码
     */
    private String siteCode;
    /**
     * 勋章信息枚举
     */
    private MedalCodeEnum medalCodeEnum;

}
