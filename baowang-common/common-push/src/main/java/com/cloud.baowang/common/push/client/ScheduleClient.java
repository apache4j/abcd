package com.cloud.baowang.common.push.client;


import com.cloud.baowang.common.push.bean.schedule.SchedulePushDetailGetResult;
import com.cloud.baowang.common.push.bean.schedule.SchedulePushGetResult;
import com.cloud.baowang.common.push.bean.schedule.SchedulePushListResult;
import com.cloud.baowang.common.push.bean.schedule.SchedulePushParam;
import com.cloud.baowang.common.push.bean.schedule.SchedulePushResult;
import com.cloud.baowang.common.push.bean.schedule.SchedulePushUpdateResult;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

/**
 * (<a href="https://www.engagelab.com/docs/app-push/rest-api/scheduled-tasks-api">REST API - Schedule</a>)
 */
public interface ScheduleClient {

    @RequestLine("POST /v4/schedules")
    @Headers("Content-Type: application/json; charset=utf-8")
    SchedulePushResult schedulePush(SchedulePushParam param);

    @RequestLine("PUT /v4/schedules/{schedule_id}")
    @Headers("Content-Type: application/json; charset=utf-8")
    SchedulePushUpdateResult updateSchedulePush(@Param("schedule_id") String scheduleId, SchedulePushParam param);

    @RequestLine("DELETE /v4/schedules/{schedule_id}")
    @Headers("Content-Type: application/json; charset=utf-8")
    void deleteSchedulePush(@Param("schedule_id") String scheduleId);

    @RequestLine("GET /v4/schedules/{schedule_id}")
    @Headers("Content-Type: application/json; charset=utf-8")
    List<SchedulePushGetResult> getSchedulePush(@Param("schedule_id") String scheduleId);

    @RequestLine("GET /v4/schedules?page={page}")
    @Headers("Content-Type: application/json; charset=utf-8")
    SchedulePushListResult listSchedulePush(@Param("page") Integer page);


    @RequestLine("GET /v4/schedules/{schedule_id}/msg-ids")
    @Headers("Content-Type: application/json; charset=utf-8")
    SchedulePushDetailGetResult getSchedulePushDetail(@Param("schedule_id") String scheduleId);

}
