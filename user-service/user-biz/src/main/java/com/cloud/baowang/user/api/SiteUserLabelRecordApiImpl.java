package com.cloud.baowang.user.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.api.SiteUserLabelRecordApi;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.userlabel.userLabelRecord.UserLabelRecordsReqVO;
import com.cloud.baowang.user.api.vo.userlabel.userLabelRecord.UserLabelRecordsResVO;
import com.cloud.baowang.user.service.SiteUserLabelRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class SiteUserLabelRecordApiImpl implements SiteUserLabelRecordApi {
    private final SiteUserLabelRecordService siteUserLabelRecordService;

    @Override
    public ResponseVO<Page<UserLabelRecordsResVO>> getUserLabelRecords(UserLabelRecordsReqVO reqVO) {
        return siteUserLabelRecordService.getUserLabelRecords(reqVO);
    }

    @Override
    public ResponseVO<Long> getTotalCount(UserLabelRecordsReqVO reqVO) {
        return siteUserLabelRecordService.getTotalCount(reqVO);
    }
}

