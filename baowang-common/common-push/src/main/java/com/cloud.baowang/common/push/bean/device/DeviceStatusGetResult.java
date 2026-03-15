package com.cloud.baowang.common.push.bean.device;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeviceStatusGetResult {

    @JsonProperty("registration_ids")
    private List<String> registrationIds;

}
