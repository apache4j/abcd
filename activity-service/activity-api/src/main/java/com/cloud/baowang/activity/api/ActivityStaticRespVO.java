package com.cloud.baowang.activity.api;

import com.cloud.baowang.activity.api.vo.ActivityBaseRespVO;
import com.cloud.baowang.activity.api.vo.CheckInRewardConfigVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 签到
 */
@Data
@Builder
@I18nClass
@NoArgsConstructor
public class ActivityStaticRespVO extends ActivityBaseRespVO implements Serializable {


}
