package com.cloud.baowang.play.game.lgd.respon;


import lombok.Data;

@Data
public class IMRecordResp {
    private String status;
    private String error;
    private IMContentResp content;
}
