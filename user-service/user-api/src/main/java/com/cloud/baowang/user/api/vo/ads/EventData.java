package com.cloud.baowang.user.api.vo.ads;

import lombok.Data;

@Data
public class EventData {
    private String event_name;
    private long event_time;
    private String event_id;
    private UserData user_data;
    private CustomData custom_data;

}