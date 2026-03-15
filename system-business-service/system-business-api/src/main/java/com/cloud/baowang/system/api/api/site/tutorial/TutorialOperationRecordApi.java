package com.cloud.baowang.system.api.api.site.tutorial;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.site.tutorial.operation.TutorialOperationRecordResVO;
import com.cloud.baowang.system.api.vo.site.tutorial.operation.TutorialOperationRecordRspVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "siteTutorialRecordApi", value = ApiConstants.NAME)
@Tag(name = "教程配置操作记录 ")
public interface TutorialOperationRecordApi {
    String TUTORIAL_OPERATION_PREFIX = ApiConstants.PREFIX + "/tutorial-record/api/";

    @PostMapping(TUTORIAL_OPERATION_PREFIX + "listPage")
    ResponseVO<Page<TutorialOperationRecordRspVO>> listPage(@RequestBody TutorialOperationRecordResVO resVO);



}
