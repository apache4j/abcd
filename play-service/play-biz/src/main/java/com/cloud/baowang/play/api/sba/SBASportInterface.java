package com.cloud.baowang.play.api.sba;

import com.cloud.baowang.play.api.enums.SBActionEnum;
import com.cloud.baowang.play.api.vo.base.SBResBaseVO;
import com.cloud.baowang.play.api.vo.sba.SBBaseReq;

public interface SBASportInterface {

    /**
     * 接口标识
     */
    SBActionEnum getAction();

    /**
     * 执行接口方法
     */
    SBResBaseVO toAction(SBBaseReq baseReq);


}
