package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.play.api.enums.order.OrderShowTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.FieldType;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
//@Document(indexName = "order_record_*")
@IndexName(value = "order_record_*", keepGlobalPrefix = true)
@TableName("order_record")
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
public class OrderRecordPO implements Serializable {

    public final static String COLLECTION_NAME = "order_record";

    /**
     *  必须有 id,这里的 id 是全局唯一的标识，等同于 es 中的"_id"
     */
    private String id;
    /**
     * 站点code
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String siteCode;
    /**
     * 站点名称
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String siteName;

    /**
     * 会员id
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String userId;

    /**
     * 会员账号
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String userAccount;
    /**
     * 会员姓名
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String userName;
    /**
     * 账号类型 1测试 2正式 3商务 4置换
     */
    @IndexField(fieldType = FieldType.LONG)
    private Integer accountType;
    /**
     * 三方会员账号
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String casinoUserName;
    /**
     * 上级代理id
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String agentId;
    /**
     * 上级代理账号
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String agentAcct;
    /**
     * VIP等级
     */
    @IndexField(fieldType = FieldType.LONG)
    private Integer vipGradeCode;
    /**
     * VIP段位
     */
    @IndexField(fieldType = FieldType.LONG)
    private Integer vipRank;

    /**
     * 游戏平台CODE
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String venueCode;
    /**
     * 游戏类别 lookup venue_type
     */
    @IndexField(fieldType = FieldType.LONG)
    private Integer venueType;
    /**
     * 游戏名称
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String gameName;

    /**
     * 三方游戏code
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String thirdGameCode;
    /**
     * 房间类型
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String roomType;
    /**
     * 房间类型名称
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String roomTypeName;
    /**
     * 玩法类型
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String playType;

    /**
     * 投注时间
     */
    @IndexField(fieldType = FieldType.LONG)
    private Long betTime;
    /**
     * 结算时间
     */
    @IndexField(fieldType = FieldType.LONG)
    private Long settleTime;

    /**
     * 首次结算时间
     */
    @IndexField(fieldType = FieldType.LONG)
    private Long firstSettleTime;
    /**
     * 投注额
     */
    @IndexField(fieldType = FieldType.SCALED_FLOAT, scalingFactor = 10000)
    private BigDecimal betAmount;
    /**
     * 有效投注
     */
    @IndexField(fieldType = FieldType.SCALED_FLOAT, scalingFactor = 10000)
    private BigDecimal validAmount;
    /**
     * 派彩金额
     */
    @IndexField(fieldType = FieldType.SCALED_FLOAT, scalingFactor = 10000)
    private BigDecimal payoutAmount;
    /**
     * 输赢金额
     */
    @IndexField(fieldType = FieldType.SCALED_FLOAT, scalingFactor = 10000)
    private BigDecimal winLossAmount;
    /**
     * 注单ID
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String orderId;
    /**
     * 三方注单ID
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String thirdOrderId;
    /**
     * 注单状态
     */
    @IndexField(fieldType = FieldType.LONG)
    private Integer orderStatus;
    /**
     * 注单归类
     */
    @IndexField(fieldType = FieldType.LONG)
    private Integer orderClassify;
    /**
     * 赔率
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String odds;
    /**
     * 局号/期号
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String gameNo;
    /**
     * 桌号
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String deskNo;
    /**
     * 靴号
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String bootNo;
    /**
     * 结果牌 /结果
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String resultList;
    /**
     * 下注内容
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String betContent;
    /**
     * 变更状态
     */
    @IndexField(fieldType = FieldType.LONG)
    private Integer changeStatus;
    /**
     * 变更次数
     */
    @IndexField(fieldType = FieldType.LONG)
    private Integer changeCount;
    /**
     * 变更时间
     */
    @IndexField(fieldType = FieldType.LONG)
    private Long changeTime;
    /**
     * 投注IP
     */
    @IndexField(fieldType = FieldType.IP)
    private String betIp;
    /**
     * 币种
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String currency;
    /**
     * 设备类型
     */
    @IndexField(fieldType = FieldType.LONG)
    private Integer deviceType;
    /**
     * 串关信息
     */
    private String parlayInfo;
    /**
     * 备注
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String remark;
    @IndexField(fieldType = FieldType.LONG)
    private Long createdTime;
    @IndexField(fieldType = FieldType.LONG)
    private Long updatedTime;

    /**
     * 结果产生时间(重结算后结算时间不会变化的场景, 使用该字段来判断是否发生重结算)
     */
    @IndexField(fieldType = FieldType.LONG)
    private Long resultTime;

    /**
     * 最新变更时间，有重结算和撤销等异常时变更，初始值为落库时间
     */
    @IndexField(fieldType = FieldType.LONG)
    private Long latestTime;

    /**
     * 注单详情
     */
    private String orderInfo;

    /**
     * 玩法
     */
    private String playInfo;

    /**
     * 赛事信息
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String eventInfo;


    /**
     * 转账ID
     */
    private String transactionId;

    /**
     * 备用字段
     */
    private String exId1;

    /**
     * 备用字段
     */
    private String exId2;
}
