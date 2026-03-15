package com.cloud.baowang.play.api.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderAbnormalDetailLabelVO;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderAbnormalRecordAdminResVO;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderAbnormalRecordPageRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "play-order-abnormal-record-api", value = ApiConstants.NAME)
@Tag(name = "异常注单记录相关api")
public interface OrderAbnormalRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/order-abnormal/api/";

    @Operation(summary = "异常注单列表查询-中控后台")
    @PostMapping(PREFIX + "admin/page")
    ResponseVO<Page<OrderAbnormalRecordPageRespVO>> adminAbnormalPage(@RequestBody OrderAbnormalRecordAdminResVO resVO);

    @Operation(summary = "获取异常订单详情")
    @PostMapping("/findOrderAbnormalDetailByOrderId")
    OrderAbnormalDetailLabelVO findOrderAbnormalDetailByOrderId(@RequestBody IdVO idVO);

    @Operation(summary = "异常注单列表-总记录数")
    @PostMapping(value = PREFIX + "getTotalCount")
    ResponseVO<Long> getTotalCount(@RequestBody OrderAbnormalRecordAdminResVO vo);
}
