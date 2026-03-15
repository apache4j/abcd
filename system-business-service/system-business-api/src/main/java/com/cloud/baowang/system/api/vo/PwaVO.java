package com.cloud.baowang.system.api.vo;

import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class PwaVO {
    private String name;
    private String short_name;
    private String description;
    private String theme_color;
    private String background_color;
    private String display;
    private String start_url;
    private String scope;
    private List<Icon> icons;

    @Data
    public static class Icon {
        private String src;
        private String sizes;
        private String type;
    }
}