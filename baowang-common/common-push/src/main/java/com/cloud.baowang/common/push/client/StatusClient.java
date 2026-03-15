package com.cloud.baowang.common.push.client;

import com.cloud.baowang.common.push.bean.status.MessageLifecycleGetResult;
import com.cloud.baowang.common.push.bean.status.MessageStatusGetResult;
import com.cloud.baowang.common.push.bean.status.UserStatusGetResult;
import com.cloud.baowang.common.push.enums.TimeUnit;
import feign.Headers;
import feign.Param;
import feign.RequestLine;


import java.util.List;
import java.util.Map;

/**
 * (<a href="https://www.engagelab.com/docs/app-push/rest-api/statistics-api">REST API - Status</a>)
 */
public interface StatusClient {

    @RequestLine("GET /v4/status/users?time_unit={time_unit}&start={start}&duration={duration}")
    @Headers("Content-Type: application/json; charset=utf-8")
    UserStatusGetResult getUserStatus(@Param("time_unit") TimeUnit timeUnit, @Param("start") String startTime, @Param("duration") Integer duration);

    @RequestLine("GET /v4/status/detail?message_ids={message_ids}")
    @Headers("Content-Type: application/json; charset=utf-8")
    Map<String, MessageStatusGetResult> getMessageStatus(@Param("message_ids") List<String> messageIds);

    @RequestLine("GET /v4/status/message?message_id={message_id}&registration_ids={registration_ids}")
    @Headers("Content-Type: application/json; charset=utf-8")
    Map<String, MessageLifecycleGetResult> getMessageLifecycle(@Param("message_id") String messageId, @Param("registration_ids") List<String> registrationIds);

}
