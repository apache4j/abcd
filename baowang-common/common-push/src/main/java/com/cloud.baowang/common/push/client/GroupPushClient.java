package com.cloud.baowang.common.push.client;

import com.cloud.baowang.common.push.bean.push.GroupPushParam;
import com.cloud.baowang.common.push.bean.push.GroupPushResult;
import feign.Headers;
import feign.RequestLine;


/**
 * (<a href="https://www.engagelab.com/zh_CN/docs/app-push/rest-api/group-push-api">REST API - Group Push</a>)
 */
public interface GroupPushClient {

    @RequestLine("POST v4/grouppush")
    @Headers("Content-Type: application/json; charset=utf-8")
    GroupPushResult push(GroupPushParam param);

}
