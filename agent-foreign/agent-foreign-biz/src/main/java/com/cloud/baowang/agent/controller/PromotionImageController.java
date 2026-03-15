package com.cloud.baowang.agent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentImageApi;
import com.cloud.baowang.agent.api.api.PromotionImageApi;
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
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wade
 * @createTime: 2024/06/18 9:03
 * @description:
 */
@Tag(name = "代理-推广图片素材")
@AllArgsConstructor
@RestController
@RequestMapping(value = "/promotion-image/api")
public class PromotionImageController {

    private final PromotionImageApi promotionImageApi;

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


    @PostMapping("/getAgentImageById")
    @Operation(summary = "获取图片素材")
    public ResponseVO<AgentImageVO> getAgentImageById(@RequestBody AgentImageVO agentDomainVO) {

        String siteCode = CurrReqUtils.getSiteCode();
        String currentUserAccount = CurrReqUtils.getAccount();
        agentDomainVO.setSiteCode(siteCode);
        agentDomainVO.setAgentAccount(currentUserAccount);
        return promotionImageApi.getAgentImageById(agentDomainVO);
    }


    @Operation(summary = "获取图片素材的列表")
    @PostMapping("/getAgentImageList")
    public ResponseVO<Page<AgentImageVO>> getAgentImageList(@RequestBody AgentImageVO agentDomainVO) {
        String currentUserAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        agentDomainVO.setAgentAccount(currentUserAccount);
        agentDomainVO.setSiteCode(siteCode);
        return promotionImageApi.getAgentImageList(agentDomainVO);
    }

    @PostMapping("/getEnumList")
    @Operation(summary = "获取图片管理的常量-下拉框")
    public ResponseVO<HashMap<String, Object>> getEnumList() {
        return agentImageApi.getEnumList();
    }


}
