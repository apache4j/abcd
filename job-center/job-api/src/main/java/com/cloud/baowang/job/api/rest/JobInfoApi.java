package com.cloud.baowang.job.api.rest;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.job.api.constant.ApiConstants;
import com.cloud.baowang.job.api.vo.JobUpdateAndStartVo;
import com.cloud.baowang.job.api.vo.JobUpsertVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/6/13 13:57
 * @Version: V1.0
 **/
@FeignClient(contextId = "remoteJobAPi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 -调度中心aip")
public interface JobInfoApi {
    String PREFIX = ApiConstants.PREFIX+"/jobInfo/api";

    @Operation(description = "修改并启动任务")
    @PostMapping(value = PREFIX+"/updateAndStart")
    ResponseVO<String> updateAndStart(@RequestBody JobUpdateAndStartVo jobUpdateAndStartVo);

    @Operation(description = "创建任务")
    @PostMapping(value = PREFIX + "/create")
    ResponseVO<Map<String, String>> create(@RequestBody List<JobUpsertVO> vo);

    @Operation(description = "更新任务")
    @PostMapping(value = PREFIX + "/update")
    ResponseVO<Void> update(@RequestBody List<JobUpsertVO> vo);

    @Operation(description = "启动任务")
    @PostMapping(value = PREFIX + "/start")
    ResponseVO<Void> start(@RequestBody List<String> jobIds);

    @Operation(description = "停止任务")
    @PostMapping(value = PREFIX + "/stop")
    ResponseVO<Void> stop(@RequestBody List<String> jobIds);

    @Operation(description = "删除任务")
    @PostMapping(value = PREFIX + "/remove")
    ResponseVO<Void> remove(@RequestBody List<String> jobIds);

}
