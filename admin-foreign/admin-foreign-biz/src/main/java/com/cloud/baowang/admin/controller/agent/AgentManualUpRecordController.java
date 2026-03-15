/*
package com.cloud.baowang.admin.controller.agent;

import com.cloud.baowang.agent.api.api.AgentManualUpApi;
import com.cloud.baowang.agent.api.enums.AgentAdjustTypeEnum;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpRecordPageVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpRecordResponseVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpRecordResult;
import com.cloud.baowang.common.core.utils.CurrentRequestUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.funding.UserManualOrderStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.SystemParamVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

*/
/**
 * @author: kimi
 *//*

@Slf4j
@Tag(name = "代理人工加额记录")
@RestController
@RequestMapping("/agent-manual-up-record/api")
public class AgentManualUpRecordController {


    private final AgentManualUpApi agentManualUpApi;

    @Autowired
    public AgentManualUpRecordController(AgentManualUpApi agentManualUpApi) {
        this.agentManualUpApi = agentManualUpApi;
    }

    @Operation(summary ="下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        // 调整类型
        List<SystemParamVO> adjustType = AgentAdjustTypeEnum.getList().stream().map(item ->
                SystemParamVO.builder().code(item.getCode()).value(item.getName()).build()).toList();
        // 订单状态
        List<UserManualOrderStatusEnum> list = UserManualOrderStatusEnum.getList();
        List<SystemParamVO> orderStatus = list.stream().map(item ->
                SystemParamVO.builder().code(item.getCode().toString()).value(item.getName()).build())
                .toList();

        Map<String, Object> result = Maps.newHashMap();
        result.put("adjustType", adjustType);
        result.put("orderStatus", orderStatus);

        return ResponseVO.success(result);
    }

    @Operation(summary ="分页列表")
    @PostMapping(value = "/getUpRecordPage")
    public ResponseVO<AgentManualUpRecordResult> getUpRecordPage(@RequestBody AgentManualUpRecordPageVO vo) {
        return ResponseVO.success(agentManualUpApi.getUpRecordPage(vo));
    }

    @Operation(summary ="导出")
    @PostMapping(value = "/export")
    public void export(@RequestBody AgentManualUpRecordPageVO vo, HttpServletResponse response) {
        String adminId = CurrentRequestUtils.getCurrentOneId();
        String uniqueKey = "tableExport::centerControl::agentManualUpRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            log.warn(remain + "秒后才能导出下载");
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = agentManualUpApi.getUpRecordPageCount(vo);
        if (!responseVO.isOk() || responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        String fileName = "代理人工加额记录-" + DateUtils.dateToyyyyMMddHHmmss(new Date());
        ExcelUtil.writeForParallel(response, fileName, AgentManualUpRecordResponseVO.class, vo, 4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> agentManualUpApi.getUpRecordPage(param).getPageList().getRecords());
    }

}
*/
