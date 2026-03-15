package com.cloud.baowang.play.api.vo.tf;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginRespVO {
    @JsonProperty("launch_url")
    private String launchUrl;
}
