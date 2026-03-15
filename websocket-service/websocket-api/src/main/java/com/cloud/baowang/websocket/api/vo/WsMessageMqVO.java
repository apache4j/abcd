package com.cloud.baowang.websocket.api.vo;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import com.cloud.baowang.websocket.api.enums.ClientTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(title = "websocket发送消息VO")
public class WsMessageMqVO extends MessageBaseVO {

    /**
     * 发送消息客户端类型
     */
    private ClientTypeEnum clientTypeEnum = ClientTypeEnum.CLIENT;
    /**
     * * UidList 为null 全站点推送 站点推送必须携带 site code
     */
    @Schema(title = "发送用户 可不填 部分用户则必填 管理端用户填adminId 客户端填userId 代理端填agentId")
    private List<String> uidList;
    /**
     * @see com.cloud.baowang.websocket.api.enums.SendTypeEnum
     */
    @Schema(title = "ws 发送数据")
    private WSBaseResp<?> message;
}
