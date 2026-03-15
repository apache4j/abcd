package com.cloud.baowang.site.controller.user.detail;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceReqVO;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceRespVO;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.controller.userCoin.UserActivityFinanceRecordController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/26 14:22
 * @Version: V1.0
 **/
@RestController
@Tag(name = "资金-会员资金记录-会员活动记录")
@RequestMapping("/detail/user-activity-finance-record/api")
@Slf4j
@AllArgsConstructor
public class DetailUserActivityFinanceRecordController {

    private final UserActivityFinanceRecordController userActivityFinanceRecordController;


    @PostMapping("financeListPage")
    @Operation(summary = "会员活动记录分页列表")
    public ResponseVO<Page<ActivityFinanceRespVO>> financeListPage(@RequestBody ActivityFinanceReqVO activityFinanceReqVO) {
        if (ObjectUtil.isEmpty(activityFinanceReqVO.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userActivityFinanceRecordController.financeListPage(activityFinanceReqVO);
    }


    @PostMapping("export")
    @Operation(summary = "会员活动记录导出")
    public ResponseVO<?> export(@RequestBody ActivityFinanceReqVO activityFinanceReqVO) {
        if (ObjectUtil.isEmpty(activityFinanceReqVO.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userActivityFinanceRecordController.export(activityFinanceReqVO);

    }

}
