package com.cloud.baowang.play.api.vo.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIdsVO {
    /**
     * 会员ID集合
     */
    private List<String> userIdList;

}
