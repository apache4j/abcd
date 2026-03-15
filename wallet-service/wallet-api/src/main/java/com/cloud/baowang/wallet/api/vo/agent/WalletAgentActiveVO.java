package com.cloud.baowang.wallet.api.vo.agent;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 11/11/23 5:45 PM
 * @Version : 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema( description = "代理活跃和有效活跃满足的会员")
public class WalletAgentActiveVO implements Serializable {

     @Schema(description ="今日活跃")
    private List<String> todayActive = new ArrayList<>();

     @Schema(description ="今日有效活跃")
    private List<String> todayValidActive= new ArrayList<>();

     @Schema(description ="本月活跃")
    private List<String> monthActive= new ArrayList<>();

     @Schema(description ="本月有效活跃")
    private List<String> monthValidActive= new ArrayList<>();
}
