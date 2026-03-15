package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "意见反馈提交")
public class SiteUserFeedbackAppPageReqVO {

    @Schema(title = "当前页(默认第1页)", example = "1")
    private Integer pageNumber = 1;

    @Schema(title = "每页条数(默认10条)", example = "10")
    private Integer pageSize = 10;

    public Integer getPageNumber() {
        if (null == pageNumber) {
            return 1;
        }
        return pageNumber;
    }

    public Integer getPageSize() {
        if (null == pageSize) {
            return 10;
        }
        Integer max = Integer.MAX_VALUE - 1;
        if (pageSize > max) {
            return max;
        }
        return pageSize;
    }

}
