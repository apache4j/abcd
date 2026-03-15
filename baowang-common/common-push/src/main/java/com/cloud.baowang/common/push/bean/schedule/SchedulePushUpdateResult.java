package com.cloud.baowang.common.push.bean.schedule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchedulePushUpdateResult extends SchedulePushParam{

}
