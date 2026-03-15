package com.cloud.baowang.common.core.utils.tool.vo;

import lombok.Data;

@Data
public class MailVO {
    private String host;
    private String username;
    private String password;
    private String from;
    private Integer port;
    private String receiver;
    private String subject;
    private String messageText;
}
