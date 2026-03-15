package com.cloud.baowang.play.api.vo.ftg;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FTGGetBalanceReq extends FTGBaseReq{


    /**
     * 游戏编号
     * 详细信息请参考“6.3. 游戏大厅清单”。
     */
    private Integer game_id;



    public Boolean valid() {
        return ObjectUtil.isAllNotEmpty(this.getUid(), this.getToken());
    }


}
