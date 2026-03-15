package com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig;


import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.user.api.enums.UserSysMessageEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统消息通知配置新增入参对象")
public class UserSysNoticeConfigAddVO implements Serializable {
    /**
     * 通知类型(1:公告2:活动3:通知4:系统消息)
     */
    @NotNull(message = "通知类型不能为空")
    @Schema(title = "通知类型(4：系统消息, 固定传4)")
    private Integer noticeType;


    /**
     * 业务线(1:VIP权益类消息2:注册,3:存款,4:取款,5:参与活动,6:VIP晋级,7:活动奖励到账通知)
     * {@link UserSysMessageEnum}
     */
    @NotNull(message = "业务线不能为空")
    @Schema(title = "业务线(1:VIP权益类消息2:注册,3:存款,4:取款,5:参与活动,6:VIP晋级,7:活动奖励到账通知)")
    private Integer businessLine;

    /**
     * 消息类型
     * (1:VIP权益类消息(1:会员VIP返水,2:会员生日礼金,3:会员升级礼金,4:会员上半月红包,5:会员下半月红包)
     * 2:注册,(1:注册成功通知)
     * 3:存款,(1:存款订单成功通知)
     * 4:取款,(1:取款订单成功通知,2:取款订单失败通知)
     * 5:参与活动,(1:参与活动通知)
     * 6:VIP晋级,(1:VIP晋级通知)
     * 7:活动奖励到账通知(1:活动奖励)
     * )
     * {@link UserSysMessageEnum}
     */
    @NotNull(message = "消息类型不能为空")
    @Schema(title = "1:VIP权益类消息(1:会员VIP返水,2:会员生日礼金,3:会员升级礼金,4:会员上半月红包,5:会员下半月红包)4:取款,(1:取款订单成功通知,2:取款订单失败通知)")
    private Integer messageType;
    /**
     * 通知标题
     */
    @NotBlank(message = "通知标题不能为空")
    @Schema(title = "通知标题")
    private String noticeTitle;

    @Schema(description = "消息标题多语言数组 字典code:language_type", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<LanguageNoticeVO> noticeTitleList;


    /**
     * 通知消息内容
     */
    @NotBlank(message = "通知消息内容不能为空")
    @Schema(title = "通知消息内容")
    @Length(max = 1000)
    private String messageContent;


    @Schema(description = "通知消息内容多语言数组 字典code:language_type", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<LanguageNoticeVO> messageContentList;

    /**
     * 操作人
     */
    @Schema(title = "操作人")
    private String operator;


    /**
     * 发送对象账号
     */
    @Schema(title = "发送对象账号")
    private String userAccount;


}
