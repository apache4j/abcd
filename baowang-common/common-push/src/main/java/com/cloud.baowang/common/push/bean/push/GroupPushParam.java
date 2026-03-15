package com.cloud.baowang.common.push.bean.push;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupPushParam extends PushParam{

}
