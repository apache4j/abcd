package com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO;

import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.user.api.vo.user.UserTypeVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "审核申请类型")
public class ReviewTypeVO {

    @Schema(title = "审核状态")
    private List<CodeValueVO> reviewStatus;

    @Schema(title = "锁单状态")
    private List<CodeValueVO> lockStatus;

    @Schema(title = "审核操作")
    private List<CodeValueVO> reviewOperation;

    @Schema(title = "审核申请类型")
    private List<CodeValueVO> reviewType;

    @Schema(title = "账户类型")
    private List<UserTypeVO> accountTypes;

}
