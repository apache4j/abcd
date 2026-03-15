package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption: 勋章获取条件参数
 * @Author: Ford
 * @Date: 2024/10/7 16:42
 * @Version: V1.0
 **/
@Schema(description = "勋章获取条件参数")
@Data
public class MedalAcquireCondVO {
    /**
     * 站点编码
     */
    private String siteCode;
    /**
     * 勋章信息枚举
     */
    private MedalCodeEnum medalCodeEnum;
    /**
     * 来源值1
     * 业务方统计好的来源值1 与勋章信息条件1进行对比
     */
    private String originVal1;
    /**
     * 来源值2
     * 业务方统计好的来源值2 与勋章信息条件2进行对比
     */
    private String originVal2;
}
