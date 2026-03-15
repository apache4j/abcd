package com.cloud.baowang.play.api.vo.sh;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShAdjustBalanceReq implements Serializable {

  @Schema(title = "交易编号")
  private String transferNo;

  @Schema(title = "玩家账号")
  private String userName;

  @Schema(title = "实际交易金额(+ -)")
  private BigDecimal totalAmount;

  @Schema(title = "调用时间戳")
  private Long timestamp;

  @Schema(title = "游戏类型，打赏也包含在内")
  private Long gameTypeId;

  @Schema(description = "1=下注(负数，需要扣款)；\n" +
          "3=正常结算(加款)\n" +
          "5=跳局结算(加款)\n" +
          "6=取消局(有可能负数，需要扣款)\n" +
          "7=重算局(有可能负数，需要扣款)\n" +
          "8=打赏(负数，需要扣款)\n" +
          "9=重算回滚(加款)\n")
  private Integer transferType;

  @Schema(title = "签名")
  private String md5Sign;

  @Schema(title = "交易详情")
  private List<ShTransferOrderReq> transferOrderVOList;

  @Schema(title = "商户")
  private String merchantNo;


  /**
   * 验证参数是否齐全
   *
   * @return true表示成功 false表示失败
   */
  public boolean validate() {
    return StringUtils.isNotBlank(userName)
            && StringUtils.isNotBlank(transferNo)
            && StringUtils.isNotBlank(merchantNo)
            && Objects.nonNull(totalAmount)
            && Objects.nonNull(timestamp)
//            && Objects.nonNull(gameTypeId)
            && Objects.nonNull(transferType)
            && !CollectionUtils.isEmpty(transferOrderVOList)
            && StringUtils.isNotBlank(md5Sign);
  }

}
