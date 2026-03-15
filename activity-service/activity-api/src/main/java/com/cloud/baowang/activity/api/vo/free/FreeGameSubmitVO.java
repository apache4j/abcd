package com.cloud.baowang.activity.api.vo.free;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(title = "免费旋转次数添加申请-提交 Request")
public class FreeGameSubmitVO {
    /**
     * 站点code
     */
    @Schema(title = "站点code", hidden = true)
    private String siteCode;

    @Schema(title = "会员账号")
    private String userAccounts;



    @Schema(title = "调整类型:1-账号输入，2-导入账号")
    //@NotNull(message = "调整类型不能为空")
    private Integer addType;

    @Schema(title = "赠送次数")
    @NotNull(message = "赠送次数不能为空")
    @Min(value = 1, message = "赠送次数必须大于0")
    private Integer acquirerNum;


    @Schema(title = "旋转配置上传文件")
    private MultipartFile file;


    @Schema(title = "时效-小时")
    @NotNull(message = "赠送次数不能为空")
    @Min(value = 1, message = "赠送次数必须大于0")
    private Integer timeLimit;

    @Schema(title = "限注金额")
    @NotNull(message = "限注金额不能为空")
    private BigDecimal betLimitAmount;

    @Schema(title = "游戏场馆")
    private String venueCode;


    @Schema(title = "游戏名称Code")
    private String gameId;

    @Schema(title = "币种")
    private String currency;

    @Schema(title = "洗码")
    @NotNull(message = "洗码不能为空")
    private BigDecimal washRatio;

    @Schema(title = "活动ID")
    @NotNull(message = "活动ID不能为空")
    private String activityId;





}
