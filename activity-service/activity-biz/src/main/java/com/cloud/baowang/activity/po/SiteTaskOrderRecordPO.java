package com.cloud.baowang.activity.po;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.activity.api.enums.task.TaskDistributionTypeEnum;
import com.cloud.baowang.activity.api.enums.task.TaskReceiveStatusEnum;
import com.cloud.baowang.activity.api.vo.task.SiteTaskOrderRecordReqVO;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value = "site_task_order_record")
public class SiteTaskOrderRecordPO extends BasePO {
    /**
     * 站点code
     */
    private String siteCode;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 所属活动
     */
    private String taskId;
    /**
     * 活动类型
     * {@link com.cloud.baowang.activity.api.enums.task.TaskEnum}
     */
    private String taskType;

    /**
     * 活动类型
     * {@link com.cloud.baowang.activity.api.enums.task.TaskEnum}
     */
    private String subTaskType;
    /**
     * 会员id
     */
    private String userId;
    /**
     * 姓名
     */
    private String userName;
    /**
     * 会员账号
     */
    private String userAccount;
    /**
     * 代理账号
     */
    private String superAgentId;
    /**
     * VIP等级
     */
    private Integer vipGradeCode;
    /**
     * vip段位
     */
    private Integer vipRankCode;

    /**
     * 任务过期时间。 新人任务，过期24小时，默认在system_param novice-task-expire novice
     * 没有这个字段，只有获取奖励了，才插入这个表，达不到就不插入记录
     */
    //private Long noviceTaskExpire;
    /**
     * 派发方式: 0:玩家自领-过期作废，1:玩家自领-过期不作废
     * {@link TaskDistributionTypeEnum}
     */
    private Integer distributionType;
    /**
     * 可领取开始时间
     */
    private Long receiveStartTime;
    /**
     * 可领取结束时间
     */
    private Long receiveEndTime;
    /**
     * 领取状态
     * {"领取状态 字典CODE:activity_receive_status")}
     * {@link TaskReceiveStatusEnum}
     */
    private Integer receiveStatus;

    /**
     * 发放礼金时的汇率
     */
    private BigDecimal finalRate;

    /**
     * 任务赠送金额
     */
    private BigDecimal taskAmount;
    /**
     * 币种
     */
    private String currencyCode;
    /**
     * 流水倍数
     */
    private BigDecimal washRatio;

    /**
     * 流水倍数
     */
    private BigDecimal runningWater;
    /**
     * 备注
     */
    private String remark;
    /**
     * 领取时间
     */
    private Long receiveTime;
    /**
     * 领取时用户-设备号
     */
    private String deviceNo;
    /**
     * 领取时用户-ip
     */
    private String ip;

    /**
     * 每日任务/每周任务阶梯排序值
     */
    private Integer step;

    public static LambdaQueryWrapper<SiteTaskOrderRecordPO> getQueryWrapper(SiteTaskOrderRecordReqVO requestVO) {
        LambdaQueryWrapper<SiteTaskOrderRecordPO> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper
                .eq(ObjectUtil.isNotEmpty(requestVO.getSiteCode()), SiteTaskOrderRecordPO::getSiteCode, requestVO.getSiteCode())
                .in(ObjectUtil.isNotEmpty(requestVO.getTaskIds()), SiteTaskOrderRecordPO::getTaskId, requestVO.getTaskIds())
                .eq(ObjectUtil.isNotEmpty(requestVO.getOrderNo()), SiteTaskOrderRecordPO::getOrderNo, requestVO.getOrderNo())
                .eq(ObjectUtil.isNotEmpty(requestVO.getUserAccount()), SiteTaskOrderRecordPO::getUserAccount, requestVO.getUserAccount())
                .eq(ObjectUtil.isNotEmpty(requestVO.getReceiveStatus()), SiteTaskOrderRecordPO::getReceiveStatus, requestVO.getReceiveStatus())
                .ge(ObjectUtil.isNotEmpty(requestVO.getDistributionStartTime()), SiteTaskOrderRecordPO::getCreatedTime, requestVO.getDistributionStartTime())
                .le(ObjectUtil.isNotEmpty(requestVO.getDistributionEndTime()), SiteTaskOrderRecordPO::getCreatedTime, requestVO.getDistributionEndTime())
                .ge(ObjectUtil.isNotEmpty(requestVO.getReceiveStartTime()), SiteTaskOrderRecordPO::getReceiveTime, requestVO.getReceiveStartTime())
                .le(ObjectUtil.isNotEmpty(requestVO.getReceiveEndTime()), SiteTaskOrderRecordPO::getReceiveTime, requestVO.getReceiveEndTime());
        if (!StringUtils.isBlank(requestVO.getOrderField())) {
            if (StringUtils.equalsIgnoreCase("receiveTime", requestVO.getOrderField()) && StringUtils.equalsIgnoreCase("desc", requestVO.getOrderType())) {
                queryWrapper.orderByDesc(SiteTaskOrderRecordPO::getReceiveTime);

            }
            if (StringUtils.equalsIgnoreCase("receiveTime", requestVO.getOrderField()) && StringUtils.equalsIgnoreCase("asc", requestVO.getOrderType())) {
                queryWrapper.orderByAsc(SiteTaskOrderRecordPO::getReceiveTime);
            }
            if (StringUtils.equalsIgnoreCase("receiveStartTime", requestVO.getOrderField()) && StringUtils.equalsIgnoreCase("desc", requestVO.getOrderType())) {
                queryWrapper.orderByDesc(SiteTaskOrderRecordPO::getCreatedTime);
            }
            if (StringUtils.equalsIgnoreCase("receiveStartTime", requestVO.getOrderField()) && StringUtils.equalsIgnoreCase("asc", requestVO.getOrderType())) {
                queryWrapper.orderByAsc(SiteTaskOrderRecordPO::getCreatedTime);
            }
        } else {
            queryWrapper.orderByDesc(SiteTaskOrderRecordPO::getReceiveTime);  // 默认按创建时间倒序作为次要排序
        }

        return queryWrapper;
    }


}
