package com.cloud.baowang.common.push.bean.status;

import com.cloud.baowang.common.push.enums.TimeUnit;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class UserStatusGetParam {

    @NonNull
    private TimeUnit timeUnit;

    @NonNull
    private String startTime;

    @NonNull
    private Integer duration;

}
