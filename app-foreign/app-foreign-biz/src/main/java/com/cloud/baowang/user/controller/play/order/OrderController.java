package com.cloud.baowang.user.controller.play.order;


import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.play.api.vo.order.client.OrderRecordClientReqVO;
import com.cloud.baowang.play.api.vo.order.client.OrderRecordClientRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "注单相关")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/order/api")
public class OrderController {

    private final OrderRecordApi orderRecordApi;

    @Operation(summary = "客户端用户投注记录")
    @PostMapping("client/orderRecord")
    public ResponseVO<OrderRecordClientRespVO> clientOrderRecord(@RequestBody OrderRecordClientReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setUserId(CurrReqUtils.getOneId());
        vo.setUserAccount(CurrReqUtils.getAccount());
        vo.setTimezone(CurrReqUtils.getTimezone());
        return orderRecordApi.clientOrderRecord(vo);
    }
}
