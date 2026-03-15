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
import org.springframework.web.multipart.MultipartFile;


import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通知配置（包括代理通知）新增入参对象")
public class UserNoticeConfigAddVO implements Serializable {
    /**
     * 通知类型(1:公告2:通知 3:活动)
     */
    @NotNull(message = "通知类型不能为空")
    @Schema(title = "通知类型(1:公告2:通知 3:活动)")
    private Integer noticeType;

    /**
     * 弹窗类型(1:跑马灯 2:强制弹窗)
     */
    @Schema(title = "弹窗类型(1:跑马灯2:强制弹窗)")
    private Integer popUpType;

    /**
     * 是否极光推送(0:否 1:是)是否极光推送(0:否 1:是)
     */
    @Schema(title = "是否极光推送(0:否 1:是)")
    private Integer isPush;

    /**
     * 轮播开始时间
     */
    /*@Schema(title ="轮播开始时间")
    private Long carouselStartTime;*/

    /**
     * 轮播结束时间
     */
    /*@Schema(title ="轮播结束时间")
    private Long carouselEndTime;*/


    /**
     * 通知标题
     */
    @Schema(title = "通知标题")
    private String noticeTitleI18nCode;

    @Schema(description = "消息标题多语言数组 字典code:language_type")
    private String noticeTitleI18nCodeList;
    /**
     * 通知消息内容
     */
    @Schema(title = "通知消息内容")
    private String messageContentI18nCode;


    @Schema(description = "通知消息内容多语言数组 字典code:language_type")
    private String messageContentI18nCodeList;

    /**
     * 通知消息内容
     */
    @Schema(title = "活动图/通知图")
    private String picIconI18nCode;


    @Schema(description = "活动图/通知图多语言数组 字典code:language_type")
    private String picIconI18nCodeList;




    /**
     * 前端 送对象(1:会员2:终端 4：代理)
     * 数据库 targetType
     * 数据库 发送对象发送对象(1 全体会员 2 特定会员-vip等级 3 特定会员-主货币 4 特定会员-指定了会员  4 终端 5 全部代理 6 特定代理)
     *
     *   system_param(target_type)
     */
    @NotNull(message = "发送对象发送对象")
    @Schema(title = "发送对象(1:会员2:终端 4：代理 ,5:商务)")
    private Integer targetType;
    /**
     * 终端对象
     */
    @Schema(title = "终端对象")
    private String terminal;
    /**
     * 操作人
     */
    @Schema(title = "操作人")
    private String operator;
    /**
     * 当发送对象是会员的时候
     * 会员类型 1:全部会员 2:特定会员
     */
    @Schema(title = "会员类型 1:全部会员 2:特定会员 ")
    private Integer memberShipType;


    /**
     * 当发送对象是代理的时候
     * 会员类型 1:全部代理 2:特定代理 notice_agent_type
     */
    @Schema(title = "代理类型  1:全部代理 2:特定代理")
    private Integer noticeAgentType;

    /**
     * 发发送对象旋转特定会员的的时候，指定会员类型
     * 会员类型 1:vip等级 2:主货币 3:指定会员
     * 数据库存放的时候
     * specify_membership_type
     */
    @Schema(title = "特定会员类型 1:vip等级 2:主货币 3:特定会员")
    private Integer specifyMemberShipType;

    /**
     * 当选择2主货币的时候
     * currency_code
     */
    @Schema(title = "货币")
    private String currencyCode;
    /**
     * 当选择1 vip等级
     * vip_grade_mix
     */
    @Schema(title = "vip等级下限")
    private Integer vipGradeMin;
    /**
     * 当选择1 vip等级
     * vip_grade_max
     */
    @Schema(title = "vip等级上限")
    private Integer vipGradeMax;

    /**
     * 站点siteCode
     */
    @Schema(title = "站点siteCode")
    private String siteCode;


    @Schema(title = "账号输入，以逗号隔开")
    private String accounts;


    @Schema(title = "特定会员上传文件")
    private MultipartFile file;


}
