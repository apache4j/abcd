package com.cloud.baowang.play.api.vo.jdb.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JDBLoginReq extends JDBBaseReq {

    private String parent;
    /** 语种 */
    private String lang;

    /** 1：使用 JDB 游戏大厅（默认值） 2-不使用游戏大厅*/
    private String windowMode;

    private Boolean isAPP;

    /** 是否显示币别符号 */
    private Boolean isShowDollarSign;

}
