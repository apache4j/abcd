package com.cloud.baowang.user.vo;

import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/09/14 11:10
 * @description:
 */
@Data
public class VerifyVO {
    String sceneId;
    String certifyId;
    String deviceToken;
    String data;
}
