package com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.response;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.LanguageNoticeVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(description = "通知配置列表展示")
public class UserNoticeConfigVO implements Serializable {

    private String id;
    /**
     * 通知类型(1:公告2:活动3:通知)
     */
    @Schema(title = "通知类型(1:公告2:活动3:通知)")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.NOTIFICATION_TYPE)
    private Integer noticeType;
    @Schema(title = "通知类型")
    private String noticeTypeText;


    /**
     * 通知标题
     */
    @Schema(title = "通知标题")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String noticeTitleI18nCode;
    @Schema(description = "消息标题多语言数组 字典code:language_type")
    private List<I18nMsgFrontVO> noticeTitleI18nCodeList;


    /**
     * 通知消息内容
     */
    @Schema(title = "活动图/通知图")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String picIconI18nCode;

    @Schema(description = "活动图/通知图多语言数组 字典code:language_type")
    private List<I18nMsgFrontVO> picIconI18nCodeList;

    /**
     * 发送对象
     */
    @Schema(title = "发送对象")
    private String sendObject;

    public String sendName;


    /**
     * 通知消息内容
     */
    @Schema(title = "通知消息内容")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String messageContentI18nCode;

    @Schema(description = "通知消息内容多语言数组 字典code:language_type")
    private List<I18nMsgFrontVO> messageContentI18nCodeList;

    @Schema(title = "操作人")
    private String updater;

    @Schema(title = "会员数量")
    private Integer number;

    /**
     * 发送对象(1:会员2:终端)
     */
    @Schema(title = "发送对象(1:会员2:终端 4代理)")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SEND_OBJECT_TYPE)
    private Integer targetType;

    /**
     * 发送对象(1:会员2:终端)
     */
    @Schema(title = "发送对象(1:会员2:终端 4代理)")
    private String targetTypeText;

    /**
     * 终端对象
     */
    @Schema(title = "终端对象")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TERMINAL)
    private String terminal;

    /**
     * 终端对象
     */
    @Schema(title = "终端对象")
    private String terminalText;

    /**
     * 当发送对象是会员的时候
     * 会员类型 1:全部会员 2:特定会员
     */
    @Schema(title = "会员类型 1:全部会员 2:特定会员 3:全部代理 4:特定代理")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.MEMBERSHIP_TYPE)
    private Integer memberShipType;

    /**
     * 当发送对象是会员的时候
     * 会员类型 1:全部会员 2:特定会员
     */
    @Schema(title = "会员类型 1:全部会员 2:特定会员 3:全部代理 4:特定代理")
    private String memberShipTypeText;


    /**
     * 当发送对象是代理的时候
     * 代理类型  1:全部代理 2:特定代理
     */
    @Schema(title = "代理类型  1:全部代理 2:特定代理")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.NOTICE_AGENT_TYPE)
    private Integer noticeAgentType;

    @Schema(title = "商务类型  1:全部商务 2:特定商务")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.NOTICE_BUSINESS_TYPE)
    private Integer noticeMerchantType;




    /**
     * 当发送对象是代理的时候
     * 代理类型  1:全部代理 2:特定代理
     */
    @Schema(title = "代理类型  1:全部代理 2:特定代理")
    private String noticeAgentTypeText;

    @Schema(title = "代理类型  1:全部商务 2:特定商务")
    private String noticeMerchantTypeText;



    /**
     * 发发送对象旋转特定会员的的时候，指定会员类型
     * 会员类型 1:vip等级 2:主货币 3:指定会员
     * 数据库存放的时候
     * specify_membership_type
     */
    @Schema(title = "特定会员类型 1:vip等级 2:主货币 3:特定会员")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SPECIFY_MEMBERSHIP_TYPE)
    private Integer specifyMemberShipType;


    /**
     * 发发送对象旋转特定会员的的时候，指定会员类型
     * 会员类型 1:vip等级 2:主货币 3:指定会员
     * 数据库存放的时候
     * specify_membership_type
     */
    @Schema(title = "特定会员类型 1:vip等级 2:主货币 3:特定会员")
    private String specifyMemberShipTypeText;
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

    @Schema(title = "发送对象")
    private String targetName;
    @Schema(title = "创建时间")
    private Long createdTime;

    @Schema(title = "更新时间")
    private Long updatedTime;


    /**
     * 状态(0:发送1:撤回)
     */
    @Schema(title = " 状态(1:撤回 1:已经撤回)")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SEND_STATUS)
    private Integer status;

    /**
     * 状态(0:发送1:撤回)
     */
    @Schema(title = " 状态(1:发送0:撤回)")
    private String statusText;

    /**
     * 弹窗类型(1:普通弹窗2:强制弹窗)
     */
    @Schema(title = "弹窗类型(1:普通弹窗2:强制弹窗)")
    private Integer popUpType;

    /**
     * 是否极光推送(0:否 1:是)是否极光推送(0:否 1:是)
     */
    @Schema(title = "是否极光推送(0:否 1:是)")
    private Integer isPush;

    /**
     * 轮播开始时间
     */
    @Schema(title = "轮播开始时间")
    private Long carouselStartTime;

    /**
     * 轮播结束时间
     */
    @Schema(title = "轮播结束时间")
    private Long carouselEndTime;

    @Schema(title = "顺序")
    private Integer sort;

    @I18nField(type = I18nFieldTypeConstants.FILE)
    @Schema(title = "图片链接")
    private String picIcon;

    @Schema(title = "图片链接")
    private String picIconFileUrl;

    @Schema(title = "发送对象描述")
    private String sendObjectDesc;

    public String getSendObjectDesc() {
        if(targetType == 1){
            return memberShipTypeText;
        }else if(targetType == 2){
            return terminalText;
        }else if(targetType == 4){
            return noticeAgentTypeText;
        }else if(targetType == 5){
            return noticeMerchantTypeText;
        }
        return null;
    }
}
