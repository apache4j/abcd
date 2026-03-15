package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 22/5/23 10:28 AM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP等级配置入参对象")
@Slf4j
public class VIPRankRequestVO implements Serializable {

    @Schema(title = "VIP等级配置单个入参对象")
    private List<VIPRankConfigRequestVO> rankConfigRequestVO;

    @Schema(title ="登录个人信息")
    private String userName;
}
