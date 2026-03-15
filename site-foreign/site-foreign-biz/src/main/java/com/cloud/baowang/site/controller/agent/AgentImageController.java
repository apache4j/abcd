package com.cloud.baowang.site.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentImageApi;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImagePageQueryVO;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImageVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.agent.api.enums.ImageSizeEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.system.api.api.SystemParamApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "图片管理")
@AllArgsConstructor
@RestController
@RequestMapping("/agentImage/api")
public class AgentImageController {

    private final AgentImageApi agentImageApi;
    private final SystemParamApi paramApi;

    @GetMapping("getDownBox")
    @Operation(summary = "获取下拉框")
    public ResponseVO<Map<String, List<CodeValueNoI18VO>>> getDownBox() {
        ResponseVO<List<CodeValueVO>> resp = paramApi.getSystemParamByType(CommonConstant.AGENT_IMAGE_TYPE);
        Map<String, List<CodeValueNoI18VO>> result = new HashMap<>();
        if (resp.isOk()) {
            List<CodeValueVO> data = resp.getData();
            ImageSizeEnum[] values = ImageSizeEnum.values();
            List<CodeValueNoI18VO> agentImageTypeArr = new ArrayList<>();
            List<CodeValueNoI18VO> imageSizeArr = new ArrayList<>();
            for (ImageSizeEnum value : values) {
                imageSizeArr.add(
                        CodeValueNoI18VO.builder().type(CommonConstant.AGENT_IMAGE_SIZE)
                                .code(value.getType()).value(value.getDescription())
                                .build());
            }
            data.forEach(item -> agentImageTypeArr.add(
                    CodeValueNoI18VO.builder().type(CommonConstant.AGENT_IMAGE_TYPE)
                            .code(item.getCode())
                            .value(I18nMessageUtil.getI18NMessageInAdvice(item.getValue()))
                            .build()
            ));
            result.put(CommonConstant.AGENT_IMAGE_TYPE, agentImageTypeArr);
            result.put(CommonConstant.AGENT_IMAGE_SIZE, imageSizeArr);
        }
        return ResponseVO.success(result);
    }


    @Operation(summary = "获取图片管理的常量字典")
    @GetMapping(value = "/getEnumList")
    public ResponseVO<HashMap<String, Object>> getEnumList() {
        return agentImageApi.getEnumList();
    }

    @Operation(summary = "添加图片")
    @PostMapping(value = "/addAgentImage")
    public ResponseVO<Boolean> addAgentImage(@Valid @RequestBody AgentImageVO agentDomainVO) {
        agentDomainVO.setSiteCode(CurrReqUtils.getSiteCode());
        agentDomainVO.setCreator(CurrReqUtils.getAccount());
        agentDomainVO.setUpdater(CurrReqUtils.getAccount());
        long l = System.currentTimeMillis();
        agentDomainVO.setCreatedTime(l);
        agentDomainVO.setUpdatedTime(l);
        return agentImageApi.addAgentImage(agentDomainVO);
    }

    @Operation(summary = "修改图片")
    @PostMapping(value = "/updateAgentImage")
    public ResponseVO<Boolean> updateAgentImage(@Valid @RequestBody AgentImageVO agentDomainVO) {
        agentDomainVO.setUpdater(CurrReqUtils.getAccount());
        agentDomainVO.setUpdatedTime(System.currentTimeMillis());
        return agentImageApi.updateAgentImage(agentDomainVO);
    }

    @Operation(summary = "删除图片")
    @GetMapping(value = "/deleteAgentImage")
    public ResponseVO<Boolean> deleteAgentImage(@RequestParam("id") String id) {
        return agentImageApi.deleteAgentImage(id);
    }

    @Operation(summary = "获取图片")
    @GetMapping(value = "/getAgentImageById")
    public ResponseVO<AgentImageVO> getAgentImageById(@RequestParam("id") String id) {
        return agentImageApi.getAgentImageById(id);
    }

    @Operation(summary = "获取图片的列表")
    @PostMapping(value = "/getAgentImageList")
    public ResponseVO<Page<AgentImageVO>> getAgentImageList(@RequestBody AgentImagePageQueryVO agentDomainVO) {
        agentDomainVO.setSiteCode(CurrReqUtils.getSiteCode());
        return agentImageApi.getAgentImageList(agentDomainVO);
    }

}
