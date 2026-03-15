package com.cloud.baowang.common.push.bean.device;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AliasStatusGetResult {

    @JsonProperty("registration_ids")
    private List<String> registrationId;

}
