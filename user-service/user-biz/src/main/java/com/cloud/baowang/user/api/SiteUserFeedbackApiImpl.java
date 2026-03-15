package com.cloud.baowang.user.api;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.IdSVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.user.api.api.SiteUserFeedbackApi;
import com.cloud.baowang.user.api.vo.user.*;
import com.cloud.baowang.user.po.SiteUserFeedbackPO;
import com.cloud.baowang.user.service.SiteUserFeedbackService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SiteUserFeedbackApiImpl implements SiteUserFeedbackApi {

    private final SiteUserFeedbackService siteUserFeedbackService;


    @Override
    public ResponseVO<Boolean> submit(SiteUserFeedbackAddVO siteUserFeedbackAddVO) {

        return siteUserFeedbackService.submit(siteUserFeedbackAddVO);
    }

    @Override
    public ResponseVO<Page<SiteUserFeedbackAppResVO>> userPageList(SiteUserFeedbackAppPageReqVO reqVO) {
        return siteUserFeedbackService.userPageList(reqVO);
    }

    @Override
    public ResponseVO<List<SiteUserFeedbackDetailResVO>> detail(IdVO idVO) {
        ResponseVO<List<SiteUserFeedbackDetailResVO>> detail = siteUserFeedbackService.detail(idVO);
        List<SiteUserFeedbackDetailResVO> data = detail.getData();
        if(CollectionUtil.isNotEmpty(data)){
            for (SiteUserFeedbackDetailResVO datum : data) {
                List<BackContentText> backContents = datum.getBackContent();
                if (CollectionUtil.isEmpty(backContents)){
                    continue;
                }
                for (BackContentText backContent : backContents) {
                    backContent.setBackAccount(I18nMessageUtil.getI18NMessage(CommonConstant.PLATFORM_REPLY));
                }
            }
        }
        return detail;
    }

    @Override
    public ResponseVO<Page<SiteUserFeedbackSiteRespVO>> feedbackList(SiteUserFeedbackSiteReqVO reqVO) {
        return siteUserFeedbackService.feedbackList(reqVO);
    }

    @Override
    public ResponseVO<SiteUserFeedbackSiteRespVO> lock(StatusListVO statusVO) {
        return siteUserFeedbackService.lock(statusVO);
    }

    @Override
    public ResponseVO<Boolean> reply(SiteUserFeedbackReplyReqVO reqVO) {
        return siteUserFeedbackService.reply(reqVO);
    }

    @Override
    public ResponseVO<Boolean> del(IdVO idVO) {
        return ResponseVO.success(siteUserFeedbackService.del(idVO.getId()));
    }

    @Override
    public ResponseVO<Boolean> batchDel(@RequestBody IdSVO idsVO){
        return ResponseVO.success(siteUserFeedbackService.batchDel(idsVO.getIds()));
    }

    @Override
    public void read(IdVO idVO) {
        siteUserFeedbackService.read(idVO);
    }

    @Override
    public ResponseVO<Boolean> replyAgain(SiteUserFeedbackReplyReqVO reqVO) {
        return siteUserFeedbackService.replyAgain(reqVO);
    }

    @Override
    public ResponseVO<List<SiteUserFeedbackDetailResVO>> history(IdVO idVO) {
        SiteUserFeedbackPO po = siteUserFeedbackService.getById(idVO.getId());
        if(po == null){
            return ResponseVO.fail(ResultCode.ORDER_NOT_EXIST);
        }
        return siteUserFeedbackService.detail(idVO.setId(po.getFeedId()));
    }
}
