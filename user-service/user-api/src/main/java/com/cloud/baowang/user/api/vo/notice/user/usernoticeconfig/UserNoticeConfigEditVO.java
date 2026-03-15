package com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig;


import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
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
@Schema(description = "通知配置编辑入参对象")
public class UserNoticeConfigEditVO implements Serializable {
    @NotNull(message = "id不能为空")
    private String id;
    /*
       通知类型(1:公告2:活动3:通知)
    */
    @Schema(title = "通知类型(1:公告2:活动3:通知)")
    private Integer noticeType;
    /*
    通知标题
     */
    @NotBlank(message = "通知标题不能为空")
    @Schema(title = "通知标题")
    @Length(max = 20)
    private String noticeTitleI18nCode;

    @Schema(description = "消息标题多语言数组 字典code:language_type")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> noticeTitleI18nCodeList;

    /*
    通知消息内容
     */
    @NotNull(message = "通知消息内容不能为空")
    @Schema(title = "通知消息内容")
    @Length(max = 300)
    private String messageContentI18nCode;

    @Schema(description = "通知消息内容多语言数组 字典code:language_type")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> messageContentI18nCodeList;
    /*
    发送对象(1:会员2:终端)
     */
    /*@NotNull(message = "发送对象发送对象")
    @Schema(title ="发送对象(1:会员2:终端)")
    private Integer targetType;*/
    /*
    终端对象
     */
    /*@Schema(title ="终端对象")
    private String terminalObject;*/
    /*
    操作人
     */
    @Schema(title = "操作人")
    private String operator;
    /*
    会员类型 1:全部会员 2:特定会员
     */
    /*@Schema(title ="会员类型 1:全部会员 2:特定会员")
    private Integer membershipType;*/

    /*@Schema(title ="特定会员上传文件")
    private MultipartFile file;*/
}
