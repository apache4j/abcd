package com.cloud.baowang.activity.api.enums.task;

import io.netty.handler.codec.spdy.SpdyHttpResponseStreamIdHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务没有创建与删除，所以使用枚举，方便修改
 */
@AllArgsConstructor
@Getter
public enum TaskEnum {

    // 新人任务
    NOVICE_WELCOME("novice", "welcome", "欢迎新人", "50", "16"),
    NOVICE_CURRENCY("novice", "currency", "币种确认", "51", "7"),
    NOVICE_PHONE("novice", "phone", "手机号确认", "52", "18"),
    NOVICE_EMAIL("novice", "email", "邮箱确认", "53", "19"),

    // 每日任务
    DAILY_BET("daily", "betDaily", "每日投注", "54", "21"),
    DAILY_PROFIT("daily", "profitDaily", "每日盈利", "55", "22"),
    DAILY_NEGATIVE("daily", "negativeDaily", "每日负盈利", "56", "23"),

    DAILY_DEPOSIT("daily", "depositDaily", "每日存款", "60", "20"),

    // 每周任务
    WEEK_BET("week", "betWeek", "每周投注", "57", "24"),
    WEEK_PROFIT("week", "profitWeek", "每周盈利", "58", "25"),
    WEEK_NEGATIVE("week", "negativeWeek", "每周负盈利", "59", "26"),
    WEEK_INVITE_FRIENDS("week", "inviteFriendsWeek", "每周邀请好友", "61", "27");

    private final String taskType;  // 主任务类型（新人、每日、每周等）
    private final String subTaskType;  // 子任务类型（投注、盈利、负盈利等）
    private final String name;  // 子任务名称
    private final String accountCoinType;  // 子任务名称

    //序号 不重复
    private final String serialNo;

    // 通过任务类型和子任务类型获取枚举实例
    public static TaskEnum fromTask(String taskType, String subTaskType) {
        for (TaskEnum task : TaskEnum.values()) {
            if (task.getTaskType().equals(taskType) && task.getSubTaskType().equals(subTaskType)) {
                return task;
            }
        }
        throw new IllegalArgumentException("无效的任务类型或子任务类型: " + taskType + ", " + subTaskType);
    }

    public static TaskEnum fromSubTaskType(String subTaskType) {
        for (TaskEnum task : TaskEnum.values()) {
            if (task.getSubTaskType().equals(subTaskType)) {
                return task;
            }
        }
        throw new IllegalArgumentException("无效的子任务类型: " + subTaskType);
    }

    public static TaskEnum queryByTaskName(String subTaskName) {
        for (TaskEnum task : TaskEnum.values()) {
            if (task.getName().equals(subTaskName)) {
                return task;
            }
        }
       return null;
    }
    // 通过任务类型获取所有相关的枚举列表
    public static List<TaskEnum> getTaskListByType(String taskType) {
        List<TaskEnum> taskList = new ArrayList<>(8);
        for (TaskEnum task : TaskEnum.values()) {
            if (task.getTaskType().equals(taskType)) {
                taskList.add(task);
            }
        }
        return taskList;
    }

}

