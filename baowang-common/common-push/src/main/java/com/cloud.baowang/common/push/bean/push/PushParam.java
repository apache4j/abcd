package com.cloud.baowang.common.push.bean.push;

import com.cloud.baowang.common.push.bean.push.message.custom.CustomMessage;
import com.cloud.baowang.common.push.bean.push.message.liveactivity.LiveActivityMessage;
import com.cloud.baowang.common.push.bean.push.message.notification.NotificationMessage;
import com.cloud.baowang.common.push.bean.push.options.Options;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PushParam {

    /**
     * 可选	当前业务发送方
     */
    @JsonProperty("from")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String from;

    /**
     * 指定推送目标的两种格式：
     * <p>
     * 1. 字符串形式：
     * - "all"：表示向所有设备推送消息。推送目标为365天内活跃过的设备。
     * <p>
     * 2. {@link To} 对象形式（JSON 对象）：
     * - "tag"：JSON 数组，表示标签。多个标签之间是 OR 的关系，即取并集。用于大规模的设备属性、用户属性分群。一次推送最多支持 20 个标签。有效的标签由字母（区分大小写）、数字、下划线、汉字组成。每个标签的长度限制为 40 字节（采用 UTF-8 编码）。
     * - "tag_and"：JSON 数组，表示标签的 AND 关系。多个标签之间是 AND 的关系，即取交集。一次推送最多支持 20 个标签。
     * - "tag_not"：JSON 数组，表示标签的 NOT 关系。多个标签之间，先取多标签的并集，再对该结果取补集。一次推送最多支持 20 个标签。
     * - "alias"：JSON 数组，表示别名。多个别名之间是 OR 的关系，即取并集。用于标识一个用户。一个设备只能绑定一个别名。一次推送最多支持 1000 个别名。有效的别名由字母（区分大小写）、数字、下划线、汉字组成。每个别名的长度限制为 40 字节（采用 UTF-8 编码）。
     * - "registration_id"：JSON 数组，表示注册 ID。多个注册 ID 之间是 OR 的关系，即取并集。用于设备标识。一次推送最多支持 1000 个注册 ID。
     * - "live_activity_id"：字符串，表示实时活动标识。对应 iOS SDK 的 liveActivityId 值。在实时活动更新时必填。
     * <p>
     * 以上字段用于指定推送的目标设备或用户，确保消息能够准确地送达预期的受众。
     *
     * @see <a href="https://www.engagelab.com/zh_CN/docs/app-push/rest-api/create-push-api">EngageLab 推送 API 文档</a>
     */
    @JsonProperty("to")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object to;

    @JsonProperty("request_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String requestId;

    @JsonProperty("custom_args")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> customArgs;

    @JsonProperty("body")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Body body;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        /**
         * 指定推送平台，必填项。
         * <p>
         * MTPush 当前支持以下平台的推送：
         * <ul>
         *   <li>"android"：Android 平台</li>
         *   <li>"ios"：iOS 平台</li>
         * </ul>
         * <p>
         * 示例：
         * <pre>
         * {
         *   "platform": [
         *     "android",
         *     "ios"
         *   ]
         * }
         * </pre>
         * 或者：
         * <pre>
         * {
         *   "platform": "all"
         * }
         * </pre>
         * <p>
         * 以上配置用于指定消息推送的目标平台，确保消息能够发送到指定的设备类型。
         *
         * @see <a href="https://www.engagelab.com/zh_CN/docs/app-push/rest-api/create-push-api">EngageLab 推送 API 文档</a>
         */

        @JsonProperty("platform")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Object platform;

        /**
         * JSON Object	可选
         * 通知内容体，是被推送到客户端的内容。
         * 与 message 一起二者必须有其一，二者不可以并存。
         */
        @JsonProperty("notification")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private NotificationMessage notification;

        /**
         * JSON Object	可选
         * 消息内容体，是被推送到客户端的内容。
         * 与 notification 一起二者必须有其一，二者不可以并存。
         */
        @JsonProperty("message")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CustomMessage custom;

        @JsonProperty("live_activity")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private LiveActivityMessage liveActivity;

        /**
         * JSON Object	可选	推送参数
         */
        @JsonProperty("options")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Options options;
    }

}
