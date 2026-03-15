package com.cloud.baowang.admin.controller.agent;

import com.cloud.baowang.agent.api.api.AgentListApi;
import com.cloud.baowang.agent.api.enums.AgentAttributionEnum;
import com.cloud.baowang.agent.api.enums.AgentCategoryEnum;
import com.cloud.baowang.agent.api.enums.AgentStatusEnum;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoPageVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoResponseVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoResultVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentListTreeVO;
import com.cloud.baowang.agent.api.vo.agentinfo.CheckAesSecretKeyVO;
import com.cloud.baowang.agent.api.vo.agentinfo.UpdateWhitelistVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.agent.api.enums.AgentEntranceEnum;
import com.cloud.baowang.agent.api.enums.AgentSubTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentTypeEnum;
import com.cloud.baowang.agent.api.enums.RegisterWayEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: kimi
 */
@Tag(name = "代理列表")
@AllArgsConstructor
@RestController
@RequestMapping("/agent/api")
public class AgentController {

    private final AgentListApi agentListApi;

    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "查看AES秘钥")
    @PostMapping(value = "/checkAesSecretKey")
    public ResponseVO<CheckAesSecretKeyVO> checkAesSecretKey(@Valid @RequestBody IdVO vo) {
        return agentListApi.checkAesSecretKey(vo);
    }

    @Operation(summary = "更新流量代理的白名单")
    @PostMapping(value = "/updateWhitelist")
    public ResponseVO<?> updateWhitelist(@Valid @RequestBody UpdateWhitelistVO vo) {
        return agentListApi.updateWhitelist(vo);
    }

    @Operation(summary = "查询代理树")
    @PostMapping(value = "/getAgentTree")
    public ResponseVO<List<AgentListTreeVO>> getAgentTree() {
        return agentListApi.getAgentTree(null);
    }

    @Operation(summary = "分页列表")
    @PostMapping(value = "/getAgentPage")
    public ResponseVO<AgentInfoResultVO> getAgentPage(@RequestBody AgentInfoPageVO vo) {
        return agentListApi.getAgentPage(vo);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/agentExport")
    public ResponseVO<?> agentExport(@RequestBody AgentInfoPageVO vo) {
        String uniqueKey = "tableExport::centerControl::agentInfo::" + CommonConstant.ADMIN_CENTER_SITE_CODE + CurrReqUtils.getAccount();
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<AgentInfoResultVO> responseVO =agentListApi.getAgentPage(vo);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        long totalRecords=responseVO.getData().getPageList().getTotal();
        if (responseVO.getData().getPageList().getTotal() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentInfoResponseVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), totalRecords),
                param -> agentListApi.getAgentPage(param).getData().getPageList().getRecords());

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_LIST)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }

    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        // 代理下级类型
        List<CodeValueVO> agentSubType = AgentSubTypeEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode()).value(item.getName()).build())
                .toList();
        // 代理类型
        List<CodeValueVO> agentType = AgentTypeEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode()).value(item.getName()).build())
                .toList();

        // 代理状态
        List<CodeValueVO> agentStatus = AgentStatusEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode()).value(item.getName()).build())
                .toList();

        // 注册方式
        List<CodeValueVO> registerWay = RegisterWayEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode().toString()).value(item.getName()).build())
                .toList();

        // 入口权限
        List<CodeValueVO> agentEntrance = AgentEntranceEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode()).value(item.getName()).build())
                .toList();

        // 代理归属 1推广 2招商 3官资
        List<CodeValueVO> agentAttribution = AgentAttributionEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode().toString()).value(item.getName()).build())
                .toList();
        // 代理类别 1常规代理 2流量代理
        List<CodeValueVO> agentCategory = AgentCategoryEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(item.getCode().toString()).value(item.getName()).build())
                .toList();

        Map<String, Object> result = Maps.newHashMap();
        result.put("agentSubType", agentSubType);
        result.put("agentType", agentType);
        result.put("agentStatus", agentStatus);
        result.put("registerWay", registerWay);
        result.put("agentEntrance", agentEntrance);
        result.put("agentAttribution", agentAttribution);
        result.put("agentCategory", agentCategory);

        return ResponseVO.success(result);
    }
}
