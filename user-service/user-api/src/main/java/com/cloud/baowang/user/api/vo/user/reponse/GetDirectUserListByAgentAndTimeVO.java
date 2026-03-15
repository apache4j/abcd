package com.cloud.baowang.user.api.vo.user.reponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 指定时间内 代理新增的直属会员数、直属会员首存人数 Request
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "指定时间内 代理新增的直属会员数、直属会员首存人数 Request")
public class GetDirectUserListByAgentAndTimeVO {
    // 站点编号
    private String siteCode;

    @Schema(title = "注册时间-开始")
    private Long registerTimeStart;

    @Schema(title = "注册时间-结束")
    private Long registerTimeEnd;

    @Schema(title = "上级代理账号")
    private List<String> superAgentId;
}
