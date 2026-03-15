package com.cloud.baowang.activity.api.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Schema(title = "福利中心-活动礼包记录")
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityOrderRecordPartRespVO {

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "活动详情分页")
    private Page<ActivityOrderRecordDetailPartRespVO> page;
}
