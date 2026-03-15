package com.cloud.baowang.play.game.lgd.respon;


import lombok.Data;

import java.util.List;

@Data
public class IMContentResp {
    private IMInfoResp info;
    private List<IMRecordLogResp> log;

}
