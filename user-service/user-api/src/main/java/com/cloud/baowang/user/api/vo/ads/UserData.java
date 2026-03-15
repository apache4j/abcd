package com.cloud.baowang.user.api.vo.ads;

import lombok.Data;

import java.util.List;

@Data
public class UserData {
    private List<String> em;
    private List<String> ph;
    private String client_ip_address;
    private String client_user_agent;
    private List<String> external_id;

}
