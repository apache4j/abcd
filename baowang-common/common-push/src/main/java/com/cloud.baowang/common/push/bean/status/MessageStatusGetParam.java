package com.cloud.baowang.common.push.bean.status;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
public class MessageStatusGetParam {

    @NonNull
    private List<String> messageIds;

}
