package com.cloud.baowang.play.api.vo.transferRecordVO;

import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferRecordResultVO {

    //场馆CODE
    private String venueCode;

    //下注跟踪ID
    private String transId;

    //订单ID
    private String orderId;

    //注单ID
    private String betId;

    //注单ID
    private List<String> betIds;

    //用户账号
    private String userAccount;

    //订单状态
    private Integer orderStatus;

    //转账金额
    private BigDecimal amount;

    //1 转入 2 转出
    private Integer transferType;

    //备注
    private String remark;


    //查询时间区间
    private Long updateStartTime;

    //查询时间区间
    private Long updateEndTime;


    private List<String> orderIds;


    //订单状态
    private List<Integer> orderStatusIds;

    //查询时间区间
    private Long createStartTime;

    //查询时间区间
    private Long createEndTime;


    //重结算次数
    private Integer settleCount;

    /**
     * 查询条数
     */
    private Integer limit;


    public static TransferRecordResultVO getSBARecordId(String orderId){
        return TransferRecordResultVO.builder().orderId(orderId).venueCode(VenuePlatformConstants.SBA).build();
    }



}
