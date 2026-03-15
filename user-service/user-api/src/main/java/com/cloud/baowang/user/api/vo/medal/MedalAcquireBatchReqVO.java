package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @Desciption: 批量获取勋章
 * @Author: Ford
 * @Date: 2024/10/8 11:43
 * @Version: V1.0
 **/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MedalAcquireBatchReqVO extends MessageBaseVO {

    /**
     * 获取勋章明显
     */
    private List<MedalAcquireReqVO> medalAcquireReqVOList;
}
