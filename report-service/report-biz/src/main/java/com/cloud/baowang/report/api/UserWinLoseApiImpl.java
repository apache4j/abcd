package com.cloud.baowang.report.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.UserWinLoseApi;
import com.cloud.baowang.report.api.vo.userwinlose.ClickUserAccountPageVO;
import com.cloud.baowang.report.api.vo.userwinlose.ClickUserAccountResponseVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseAgentReqVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseAgentVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseListPageCondVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseListResponseVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLosePageVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseResponseVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseResult;
import com.cloud.baowang.report.service.ReportUserWinLoseService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class UserWinLoseApiImpl implements UserWinLoseApi {

    private final ReportUserWinLoseService reportUserWinLoseService;

    @Override
    public ResponseVO<UserWinLoseResult> getUserWinLosePage(UserWinLosePageVO vo) {
        return reportUserWinLoseService.getUserWinLosePage(vo);
    }

    @Override
    public Long getTotalCount(UserWinLosePageVO vo) {
        return reportUserWinLoseService.getTotalCount(vo);
    }

    @Override
    public ResponseVO<Long> getUserWinLosePageCount(UserWinLosePageVO vo) {
        return reportUserWinLoseService.getUserWinLosePageCount(vo);
    }

    @Override
    public ResponseVO<Page<ClickUserAccountResponseVO>> clickUserAccount(ClickUserAccountPageVO vo) {
        return reportUserWinLoseService.clickUserAccount(vo);
    }

    @Override
    public ResponseVO<List<UserWinLoseAgentVO>> queryByTimeAndAgent(UserWinLoseAgentReqVO vo) {
        return ResponseVO.success(reportUserWinLoseService.queryByTimeAndAgent(vo));
    }

    @Override
    public List<UserWinLoseResponseVO> queryListByParam(UserWinLoseAgentReqVO vo) {
        return reportUserWinLoseService.queryListByParam(vo);
    }

    @Override
    public ResponseVO<Page<UserWinLoseListResponseVO>> listPage(UserWinLoseListPageCondVO vo) {
        return ResponseVO.success(reportUserWinLoseService.listPage(vo));
    }

    @Override
    public Long getBetUserNum(UserWinLoseAgentReqVO userWinLoseAgentReqVO) {
        return reportUserWinLoseService.getBetUserNum(userWinLoseAgentReqVO);
    }
}
