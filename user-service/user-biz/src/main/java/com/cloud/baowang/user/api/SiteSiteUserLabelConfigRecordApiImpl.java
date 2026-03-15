package com.cloud.baowang.user.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigRecordApi;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigPageRequestVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigRecordPageReqVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigRecordPageResVO;
import com.cloud.baowang.user.service.SiteUserLabelConfigRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;


/**
 * 会员标签配置记录 服务类
 *
 * @author 阿虹
 * @since 2023-05-09 10:00:00
 */
@Slf4j
@AllArgsConstructor
@RestController
public class SiteSiteUserLabelConfigRecordApiImpl implements SiteUserLabelConfigRecordApi {

    private final SiteUserLabelConfigRecordService siteUserLabelConfigRecordService;

    @Override
    public Page<UserLabelConfigRecordPageResVO> getLabelConfigRecordPage(UserLabelConfigRecordPageReqVO vo) {
        return siteUserLabelConfigRecordService.getLabelConfigRecordPage(vo);
    }

    @Override
    public ResponseVO<Long> getTotalCount(UserLabelConfigRecordPageReqVO reqVO) {
        return siteUserLabelConfigRecordService.getTotalCount(reqVO);
    }
}
