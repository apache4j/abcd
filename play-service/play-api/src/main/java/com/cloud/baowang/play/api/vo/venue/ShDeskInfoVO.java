package com.cloud.baowang.play.api.vo.venue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShDeskInfoVO {
    private String deskName;
    private String deskNumber;
}
