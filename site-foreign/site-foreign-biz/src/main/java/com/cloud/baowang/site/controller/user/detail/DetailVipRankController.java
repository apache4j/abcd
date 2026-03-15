package com.cloud.baowang.site.controller.user.detail;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.controller.vip.VipRankController;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordPageQueryVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : 小智
 * @Date : 2024/8/2 09:51
 * @Version : 1.0
 */
@Slf4j
@Tag(name = "站点后台vip段位相关配置")
@RestController
@RequestMapping("/detail/vip/api")
@AllArgsConstructor
public class DetailVipRankController {

    private final VipRankController vipRankController;


    @Operation(summary = "VIP段位变更记录")
    @PostMapping(value = "/detail/queryVIPRankOperation")
    public ResponseVO<Page<SiteVipChangeRecordVO>> queryVIPRankOperation(@RequestBody SiteVipChangeRecordPageQueryVO reqVO) {
        if (ObjectUtil.isEmpty(reqVO.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return vipRankController.queryVIPRankOperation(reqVO);
    }


    @Operation(summary = "导出VIP段位变更记录")
    @PostMapping(value = "/exportOperation")
    public ResponseVO<?> exportOperation(@RequestBody SiteVipChangeRecordPageQueryVO reqVO) {
        if (ObjectUtil.isEmpty(reqVO.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return vipRankController.exportOperation(reqVO);

    }

    /*@Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(
            @RequestBody SiteVipChangeRecordPageQueryVO reqVO) {
        return vipRankController.export(reqVO);
    }*/
}
