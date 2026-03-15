package com.cloud.baowang.site.controller.user.detail;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.vo.order.*;
import com.cloud.baowang.site.controller.base.ExportBaseController;
import com.cloud.baowang.site.controller.play.order.OrderRecordController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "站点后台注单查询-个人")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/detail/order-record/api")
public class DetailUserOrderRecordController extends ExportBaseController {
    private final OrderRecordController orderRecordController;

    @Operation(summary = "注单列表查询-站点后台")
    @PostMapping("site/page")
    public ResponseVO<Page<OrderRecordPageRespVO>> adminPage(@RequestBody OrderRecordAdminResVO dto) {
        if(ObjectUtil.isEmpty(dto.getUserAccount())){
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return orderRecordController.adminPage(dto);
    }

    @Operation(summary = "注单列表总计-站点后台")
    @PostMapping("site/total")
    public ResponseVO<OrderRecordAdminTotalRespVO> adminTotal(@RequestBody OrderRecordAdminResVO dto) {
        if(ObjectUtil.isEmpty(dto.getUserAccount())){
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return orderRecordController.adminTotal(dto);
    }


    @Operation(summary = "注单详情")
    @PostMapping("site/info")
    public ResponseVO<OrderRecordAdminInfoRespVO> orderInfo(@Valid @RequestBody OrderRecordAdminInfoResVO vo) {
        return orderRecordController.orderInfo(vo);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<String> export(@RequestBody OrderRecordAdminResVO dto, HttpServletResponse response) {
        if(ObjectUtil.isEmpty(dto.getUserAccount())){
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return orderRecordController.export(dto, response);
    }
}
