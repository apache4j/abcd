package com.cloud.baowang.websocket.dto;

import com.cloud.baowang.websocket.api.enums.WSSubscribeEnum;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NettyEventBaseDto {
    private Channel channel;
    private WSSubscribeEnum subscribeEnum;
    private String data;
    private String siteCode;
    private String uid;
    private String userAccount;
}
