package com.cloud.baowang.play.api.vo.third.SBA;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "沙巴体育获取盘口信息")
public class SBAGetEventsReqVO implements Serializable {


    private String query;


    private String includeMarkets;


    private String language;



}
