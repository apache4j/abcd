/*
package com.cloud.baowang.admin.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentManualUpApi;
import com.cloud.baowang.agent.api.vo.manualup.AgentGetRecordPageVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentGetRecordResponseResultVO;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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

@Tag(name = "代理加额审核记录")
@RestController
@RequestMapping("/agent-up-review-record/api")
public class AgentManualUpReviewRecordController {

    private final AgentManualUpApi agentManualUpApi;


    @Autowired
    public AgentManualUpReviewRecordController(AgentManualUpApi agentManualUpApi) {
        this.agentManualUpApi = agentManualUpApi;
    }

    @Operation(summary="下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        // 订单状态-下拉框
        List<SystemParamVO> orderStatus = Lists.newArrayList();
        SystemParamVO success = new SystemParamVO(
                null,
                UserManualOrderStatusEnum.REVIEW_SUCCESS.getCode().toString(),
                UserManualOrderStatusEnum.REVIEW_SUCCESS.getName());
        SystemParamVO oneFail = new SystemParamVO(
                null,
                UserManualOrderStatusEnum.ONE_REVIEW_FAIL.getCode().toString(),
                UserManualOrderStatusEnum.ONE_REVIEW_FAIL.getName());
        SystemParamVO twoFail = new SystemParamVO(
                null,
                UserManualOrderStatusEnum.TWO_REVIEW_FAIL.getCode().toString(),
                UserManualOrderStatusEnum.TWO_REVIEW_FAIL.getName());
        orderStatus.add(success);
        orderStatus.add(oneFail);
        orderStatus.add(twoFail);

        Map<String, Object> result = Maps.newHashMap();
        result.put("orderStatus", orderStatus);

        return ResponseVO.success(result);
    }

    @Operation(summary="分页列表")
    @PostMapping(value = "/getRecordPage")
    public ResponseVO<Page<AgentGetRecordResponseResultVO>> getRecordPage(@RequestBody AgentGetRecordPageVO vo) {
        return ResponseVO.success(agentManualUpApi.getRecordPage(vo));
    }

    @Operation(summary="导出")
    @PostMapping(value = "/export")
    public void export(@RequestBody AgentGetRecordPageVO vo, HttpServletResponse response) {
        String adminId = CurrentRequestUtils.getCurrentOneId();
        String uniqueKey = "tableExport::centerControl::agentManualUpReviewRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);

            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = agentManualUpApi.getTotalCount(vo);
        if(!responseVO.isOk()){
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        String fileName = "代理加额审核记录" + DateUtils.dateToyyyyMMddHHmmss(new Date());
        ExcelUtil.writeForParallel(response, fileName, AgentGetRecordResponseResultVO.class, vo, 4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()), param -> agentManualUpApi.getRecordPage(param).getRecords());
    }
}
*/
