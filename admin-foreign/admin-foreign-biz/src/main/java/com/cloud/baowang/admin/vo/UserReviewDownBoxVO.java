package com.cloud.baowang.admin.vo;


import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "新增会员审核下拉框")
public class UserReviewDownBoxVO {
    @Schema(title = "审核状态")
    private List<CodeValueVO> reviewStatus;
    @Schema(title = "锁单状态")
    private List<CodeValueVO> lockStatus;
    @Schema(title = "审核操作")
    private List<CodeValueVO> reviewOperation;
    @Schema(title = "审核申请类型")
    private List<CodeValueVO> reviewType;
    @Schema(title = "账号类型")
    private List<CodeValueVO> accountTypes;
}
