package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(title = "会员提款记录分页查询接参对象")
public class UserWithdrawalRecordRequestVO extends PageVO {
    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "完成开始时间")
    private Long withdrawalStartTime;

    @Schema(description = "完成结束时间")
    private Long withdrawalEndTime;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "订单来源  下拉框列表从单独接口获取")
    private String deviceType;

    @Schema(description = "订单状态 ")
    private Integer status;

    @Schema(description = "提款终端设备号")
    private String deviceName;

    @Schema(description = "客户端状态")
    private Integer customerStatus;

    @Schema(description = "提款ip")
    private String applyIp;

    @Schema(description = "提款方式")
    private String depositWithdrawWay;

    @Schema(description = "是否为大额提款")
    private Integer isBigMoney;

    @Schema(description = "是否为首提")
    private Integer isFirstOut;

    @Schema(title = "完成开始时间")
    private String finishStartTime;

    @Schema(title = "完成结束时间")
    private String finishEndTime;

    @Schema(title = "提款通道")
    private String depositWithdrawChannelName;

    @Schema(title = "申请时间-开始")
    private Long createStartTime;

    @Schema(title = "申请时间-结束")
    private Long createEndTime;

    @Schema(title = "去重参数: 0 不去重, 非0去重")
    private int duplicate;

    @Schema(title = "提现账号信息")
    private String withdrawAccountInfo;
    private String depositWithdrawChannelCode;
    private Boolean dataDesensitization;

    @Schema(description = "区号")
    private String areaCode;

    @Schema(description = "手机号码")
    private String telephone;

    @Schema(description = "邮箱")
    private String email;


    @Schema(description = "userId")
    private String userId;



}
