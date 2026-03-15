package com.cloud.baowang.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CommonDownBoxIosVO {
    @NotEmpty
    @Schema(title = "下拉框type")
    private List<String> types;

}
