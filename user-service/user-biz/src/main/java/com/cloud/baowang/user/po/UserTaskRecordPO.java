package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @className: TaskUserRecord
 * @author: wade
 * @description: 会员详情
 * @date: 27/7/24 17:59
 */
@Getter
@Setter
@TableName("user_task_record")
public class UserTaskRecordPO extends BasePO {


    /**
     * 订单号
     */
    @TableField(value = "order_no")
    private String orderNo;

    /**
     * 订单生成时间
     */
    @TableField(value = "order_generate_time")
    private Long orderGenerateTime;

    /**
     * 领取方式
     */
    @TableField(value = "receive_way")
    private Integer receiveWay;

    /**
     * 领取时间
     */
    @TableField(value = "receive_time")
    private Long receiveTime;

    /**
     * 会员id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 会员账号
     */
    @TableField(value = "user_account")
    private String userAccount;

    /**
     * 代理账号
     */
    @TableField(value = "agent_account")
    private String agentAccount;

    /**
     * 姓名
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 任务类型
     */
    @TableField(value = "task_type")
    private Integer taskType;

    /**
     * 任务id
     */
    @TableField(value = "task_id")
    private Long taskId;

    /**
     * 任务名称
     */
    @TableField(value = "task_name")
    private String taskName;

    /**
     * 场馆id/支付方式id
     */
    @TableField(value = "complex_id")
    private Long complexId;

    /**
     * 场馆名称/支付方式
     */
    @TableField(value = "complex_name")
    private String complexName;

    /**
     * 有效投注
     */
    @TableField(value = "bet_valid_amount")
    private BigDecimal betValidAmount;

    /**
     * 奖励金额
     */
    @TableField(value = "award_amount")
    private BigDecimal awardAmount;

    /**
     * 流水倍数
     */
    @TableField(value = "running_water_multiple")
    private BigDecimal runningWaterMultiple;

    /**
     * 流水要求
     */
    @TableField(value = "running_water")
    private BigDecimal runningWater;

    /**
     * 领取状态
     */
    @TableField(value = "receive_status")
    private Integer receiveStatus;

    /**
     * 派发人
     */
    @TableField(value = "dispatcher")
    private String dispatcher;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;


    /**
     * 设备号
     */
    @TableField(value = "device_no")
    private String deviceNo;

    /**
     * ip
     */
    @TableField(value = "ip")
    private String ip;

    /**
     * 奖励详细信息
     */
    @TableField(value = "reward_details")
    private String rewardDetails;

   
}