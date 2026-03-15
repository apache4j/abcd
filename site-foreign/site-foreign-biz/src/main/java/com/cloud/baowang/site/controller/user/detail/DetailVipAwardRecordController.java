package com.cloud.baowang.site.controller.user.detail;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.controller.vip.VipAwardRecordController;
import com.cloud.baowang.user.api.vo.vip.SiteVipAwardRecordReqVo;
import com.cloud.baowang.user.api.vo.vip.SiteVipAwardRecordVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @auther amos
 * @create 2024-10-28
 */
@Tag(name = "资金-会员资金记录-vip奖励记录")
@RestController
@Slf4j
@RequestMapping("/detail/vip/award")
@AllArgsConstructor
public class DetailVipAwardRecordController {

    private final VipAwardRecordController vipAwardRecordController;

    @PostMapping("query")
    @Operation(summary = "站点后台vip奖励记录查询")
    public ResponseVO<Page<SiteVipAwardRecordVo>> query(@RequestBody SiteVipAwardRecordReqVo reqVo) {
        if (ObjectUtil.isEmpty(reqVo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return vipAwardRecordController.query(reqVo);
    }

    @PostMapping("export")
    @Operation(summary = "站点后台vip奖励记录导出")
    public ResponseVO<?> export(@RequestBody SiteVipAwardRecordReqVo reqVo) {
        if (ObjectUtil.isEmpty(reqVo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return vipAwardRecordController.export(reqVo);
    }
}
