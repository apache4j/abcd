package com.cloud.baowang.admin.controller.vip;

import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/8/7 11:24
 * @Version : 1.0
 */
@Tag(name = "总台vip等级相关配置")
@RestController
@RequestMapping("/vip/api")
@AllArgsConstructor
public class VipGradeController {

    private final VipGradeApi vipGradeApi;

    @Operation(summary = "VIP等级下拉查询")
    @PostMapping(value = "/getVipGrade")
    public ResponseVO<List<CodeValueNoI18VO>> getVipGrade() {
        return ResponseVO.success(vipGradeApi.getVipGrade());
    }

}
