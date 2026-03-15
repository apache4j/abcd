package com.cloud.baowang.system.api.site.rebate;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.rebate.ReportUserRebateInitVO;
import com.cloud.baowang.system.api.api.site.rebate.UserVenueRebateApi;
import com.cloud.baowang.system.api.vo.site.rebate.user.RebateListVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateAuditQueryVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateAuditRspVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateDetailsRspVO;
import com.cloud.baowang.system.service.site.rebate.UserRebateRecordService;
import com.cloud.baowang.system.service.site.rebate.UserRebateVenueRecordService;
import com.cloud.baowang.system.service.site.rebate.UserVenueRebateDataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@AllArgsConstructor
public class UserRebateApiImpl implements UserVenueRebateApi {

    private final UserRebateRecordService userRebateRecordService;
    private final UserRebateVenueRecordService userRebateVenueRecordService;

    private final UserVenueRebateDataService dataHandleService;

    @Override
    public ResponseVO<Page<UserRebateAuditRspVO>> userRebatePage(UserRebateAuditQueryVO vo) {
        return ResponseVO.success(userRebateRecordService.userRebatePage(vo));
    }

    @Override
    public ResponseVO<List<UserRebateDetailsRspVO>> userRebateDetails(UserRebateAuditQueryVO vo) {
        return ResponseVO.success(userRebateVenueRecordService.userRebateDetails(vo));
    }

    @Override
    public ResponseVO<Page<UserRebateAuditRspVO>> userRebateRecordPage(UserRebateAuditQueryVO vo) {
        return ResponseVO.success(userRebateRecordService.userRebateRecordPage(vo));
    }

    @Override
    public ResponseVO<Boolean> lockRebate(RebateListVO vo) {
        return userRebateRecordService.lockRebate(vo);
    }

    @Override
    public ResponseVO<Boolean> rejectRebate(RebateListVO vo) {
        return ResponseVO.success(userRebateRecordService.rejectRebate(vo));
    }

    @Override
    public ResponseVO<Boolean> issueRebate(RebateListVO vo) {
        return ResponseVO.success(userRebateRecordService.issueRebate(vo));
    }

    @Override
    public ResponseVO<Void> handleUserVenueBetInfo(ReportUserRebateInitVO rebateInitVO) {
        dataHandleService.handleUserVenueBetInfo(rebateInitVO);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Long> rebateRecordCount(UserRebateAuditQueryVO vo) {
        return ResponseVO.success(userRebateRecordService.rebateRecordCount(vo));
    }

    @Override
    public ResponseVO<Boolean> onUserRebateReceived(UserRebateAuditQueryVO vo) {
        return ResponseVO.success(userRebateVenueRecordService.onUserRebateReceived(vo));
    }


}
