package com.cloud.baowang.play.api.vo.third;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "返回瓜分该会员余额对象")
public class VenueUserBalanceVO {
    private Long id;
    private String userAccount;
    private String venueUserAccount;
    private String venuePlatform;
    private String venueCode;
    private String venueName;
    private BigDecimal balance;

    @Schema(title = "参与的活动")
    private ActivityVenueVO activityVenueVO;

    public VenueUserBalanceVO(Long id,
                              String userAccount,
                              String venueUserAccount,
                              String venuePlatform,
                              String venueCode,
                              String venueName,
                              BigDecimal balance) {
        this.id = id;
        this.userAccount = userAccount;
        this.venueUserAccount = venueUserAccount;
        this.venuePlatform = venuePlatform;
        this.venueCode = venueCode;
        this.venueName = venueName;
        this.balance = balance;

    }


}
