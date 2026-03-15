package com.cloud.baowang.admin.controller.vip;

import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/8/7 11:24
 * @Version : 1.0
 */
@Tag(name = "总台vip段位相关配置")
@RestController
@RequestMapping("/vipRank/api")
@AllArgsConstructor
public class VipRankController {
    private final VipRankApi vipRankApi;

    @Operation(summary = "VIP段位下拉")
    @PostMapping(value = "/getVipRank")
    public ResponseVO<List<CodeValueNoI18VO>> getVipRank() {
        return vipRankApi.getVipRank();
    }

    @Operation(summary = "初始化段位数据")
    //@GetMapping("initSystemVipRank")
    public ResponseVO<Boolean> initSystemVipRank() {
        return vipRankApi.initSystemVipRank();
    }
}
