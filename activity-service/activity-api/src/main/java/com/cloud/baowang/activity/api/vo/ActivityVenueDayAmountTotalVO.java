package com.cloud.baowang.activity.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Schema(title = "连续30天打码到1000打用户累计对象")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityVenueDayAmountTotalVO {

    private Integer day;

    private List<String> userList;
}
