package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @Author : dami
 * @Date : 11/6/24 11:20 AM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP权益请求返回对象")
@I18nClass
public class UserVIPInfoResVO implements Serializable {

    @Schema(title = "id")
    private String id;

    private String userId;

    private String userAccount;

    private String siteCode;


    @Schema(description = " handicap模式 0-国际盘 1- 中国盘")
    private Integer handicapMode;

    @Schema(description = " timeZone")
    private String timezone;



}
