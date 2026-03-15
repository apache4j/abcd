package com.cloud.baowang.site.controller.user.detail;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.controller.vip.VipLevelChangeRecordController;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordPageQueryVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "vip等级变更记录相关")
@RestController
@RequestMapping("/detail/vip/changeRecord")
@AllArgsConstructor
public class DetailVipLevelChangeRecordController {

    private final VipLevelChangeRecordController vipLevelChangeRecordController;


    @PostMapping("/queryChangeRecordPage")
    @Operation(summary = "分页列表,获取下拉框在system_param中根据vip_level_change_type 类型获取")
    public ResponseVO<Page<SiteVipChangeRecordVO>> queryChangeRecordPage(@RequestBody SiteVipChangeRecordPageQueryVO pageQueryVO) {
        if (ObjectUtil.isEmpty(pageQueryVO.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return vipLevelChangeRecordController.queryChangeRecordPage(pageQueryVO);
    }

    @PostMapping("/export")
    @Operation(summary = "导出")
    public ResponseVO<?> export(@RequestBody SiteVipChangeRecordPageQueryVO vo) {
        if (ObjectUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return vipLevelChangeRecordController.export(vo);

    }


}
