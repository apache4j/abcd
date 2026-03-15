package com.cloud.baowang.user.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.SiteUserFeedbackReplyTemplateApi;
import com.cloud.baowang.user.api.vo.user.SiteUserFbReTemplateReqVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFbReTemplateRespVO;
import com.cloud.baowang.user.po.SiteUserFeedbackReplyTemplatePO;
import com.cloud.baowang.user.service.SiteUserFeedbackReplyTemplateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SiteUserFeedbackReplyTemplateApiImpl implements SiteUserFeedbackReplyTemplateApi {
    private final SiteUserFeedbackReplyTemplateService templateService;

    @Override
    public ResponseVO<Boolean> add(SiteUserFbReTemplateReqVO reqVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        long count = templateService.count(Wrappers.<SiteUserFeedbackReplyTemplatePO>lambdaQuery().eq(SiteUserFeedbackReplyTemplatePO::getSiteCode, siteCode));
        if (count == 30) {
            return ResponseVO.fail(ResultCode.LESS_THAN_30);
        }
        SiteUserFeedbackReplyTemplatePO po = new SiteUserFeedbackReplyTemplatePO();
        po.setContent(reqVO.getContent());
        po.setSiteCode(siteCode);
        templateService.save(po);
        return ResponseVO.success(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> edit(List<SiteUserFbReTemplateReqVO> reqVO) {
        List<String> ids = reqVO.stream().map(SiteUserFbReTemplateReqVO::getId).toList();
        if (CollectionUtil.isEmpty(ids)) {
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        List<SiteUserFeedbackReplyTemplatePO> templatePOS = templateService.listByIds(ids);
        if (CollectionUtil.isEmpty(templatePOS)) {
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
        Map<String, String> id2VO = reqVO.stream().collect(Collectors.toMap(SiteUserFbReTemplateReqVO::getId, SiteUserFbReTemplateReqVO::getContent));
        for (SiteUserFeedbackReplyTemplatePO templatePO : templatePOS) {
            templatePO.setContent(id2VO.get(templatePO.getId()));
        }
        templateService.updateBatchById(templatePOS);
        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<Boolean> del(IdVO idVO) {
        templateService.removeById(idVO.getId());
        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<List<SiteUserFbReTemplateRespVO>> listTemplate() {
        List<SiteUserFeedbackReplyTemplatePO> list = templateService.list(Wrappers.<SiteUserFeedbackReplyTemplatePO>lambdaQuery().eq(SiteUserFeedbackReplyTemplatePO::getSiteCode, CurrReqUtils.getSiteCode()));
        if (CollectionUtil.isEmpty(list)) {
            return ResponseVO.success(new ArrayList<>());
        }
        return ResponseVO.success(BeanUtil.copyToList(list, SiteUserFbReTemplateRespVO.class));
    }


}
