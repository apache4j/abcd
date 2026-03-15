package com.cloud.baowang.play.api.vo.ace.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ACEGetbalanceReq extends ACEBaseReq{
    String playerID;

    public boolean isValid() {
        return super.isValid()
                ;
    }
}
