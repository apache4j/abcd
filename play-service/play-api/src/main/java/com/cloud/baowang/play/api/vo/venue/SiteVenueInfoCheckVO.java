package com.cloud.baowang.play.api.vo.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "场馆校验")
public class SiteVenueInfoCheckVO {

    private String siteCode;

    private String currencyCode;

    private String venueCode;
}
