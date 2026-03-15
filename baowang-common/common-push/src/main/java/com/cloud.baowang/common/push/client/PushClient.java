package com.cloud.baowang.common.push.client;

import com.cloud.baowang.common.push.bean.push.PushParam;
import com.cloud.baowang.common.push.bean.push.PushResult;
import feign.Headers;
import feign.RequestLine;


/**
 * (<a href="https://www.engagelab.com/docs/app-push/rest-api/create-push-api">REST API - Push</a>)
 */
public interface PushClient {

    @RequestLine("POST /v4/push")
    @Headers("Content-Type: application/json; charset=utf-8")
    PushResult push(PushParam param);

}
