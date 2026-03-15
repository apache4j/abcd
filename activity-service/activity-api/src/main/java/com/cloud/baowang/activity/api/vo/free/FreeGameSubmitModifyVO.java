package com.cloud.baowang.activity.api.vo.free;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(title = "免费旋转次数添加申请-提交-modify Request")
public class FreeGameSubmitModifyVO {
    /**
     * 站点code
     */
    @Schema(title = "站点code", hidden = true)
    private String siteCode;


    @Schema(title = "会员账号")
    private List<AddFreeGameConfigDTO> fileAccount;



    @Schema(title = "调整类型:1-账号输入，2-导入账号")
    @NotNull(message = "调整类型不能为空")
    private Integer addType;

    @Schema(title = "赠送次数")
    private Integer acquirerNum;




    @Schema(title = "时效-小时")
    private Integer timeLimit;

    @Schema(title = "限注金额")
    private BigDecimal betLimitAmount;

    @Schema(title = "币种限制额度")
    private List<BetLimitAmountVO> betLimitAmountS;

    /**
     * 操作人
     */
    @Schema(title = "操作人")
    private String operator;

    /**
     * 场馆
     */
    @Schema(title = "场馆")
    @NotNull(message = "场馆不能为空")
    private String venueCode;


    /**
     * 场馆
     */
    @Schema(title = "游戏id")
    @NotNull(message = "游戏id不能为空")
    private String gameId;
    /**
     * 活动id
     */
    @Schema(title = "活动id")
    private String activityId;

    /**
     * 洗码倍率
     */
    @Schema(title = "流水倍数")
    private BigDecimal washRatio;
}
