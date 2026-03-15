package com.cloud.baowang.agent.api.vo.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Description
 * @auther amos
 * @create 2024-11-04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "代理代存")
public class AgentStoredMemberVO   {
    private String date;
    /**币种*/
    private String currency;
    private String siteCode;

    /** 代存会员额度 */
    private BigDecimal storedMembersLimit;
    /** 代存会员额度人数 */
    private Integer storedMembersLimitPeopleNumber;
    /** 代存会员额度次数 */
    private Integer storedMembersLimitTimes;
}
