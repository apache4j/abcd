package com.cloud.baowang.admin.controller.systemActivity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.SystemActivityTemplateApi;
import com.cloud.baowang.activity.api.vo.SystemActivityTemplateInfoVO;
import com.cloud.baowang.activity.api.vo.SystemActivityTemplateReqVO;
import com.cloud.baowang.activity.api.vo.SystemActivityTemplateVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author : 慕凡
 * @Date : 2025/8/20 10:17
 * @Version : 1.0
 */
@Tag(name = "站点活动模版列表")
@RestController
@RequestMapping("/systemActivityTemplate/api")
@AllArgsConstructor
public class SystemActivityTemplateController {

    private final SystemActivityTemplateApi systemActivityTemplateApi;

    @Operation(summary = "分页列表")
    @PostMapping(value = "/getPage")
    public ResponseVO<Page<SystemActivityTemplateVO>> getPage(@RequestBody SystemActivityTemplateReqVO vo) {
        return systemActivityTemplateApi.getPage(vo);
    }

    @Operation(summary = "单个查询")
    @PostMapping(value = "/getInfo")
    public ResponseVO<List<SystemActivityTemplateInfoVO>> getInfo(@RequestBody SystemActivityTemplateReqVO vo) {
        return systemActivityTemplateApi.getInfo(vo);
    }

}
