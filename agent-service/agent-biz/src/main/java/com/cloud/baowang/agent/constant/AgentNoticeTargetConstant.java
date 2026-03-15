package com.cloud.baowang.agent.constant;

public class AgentNoticeTargetConstant {
    /***
     * 未读
     */
    public final static Integer UNREAD = 0;
    /***
     * 已读
     */
    public final static Integer READ = 1;
    /***
     * 正常
     */
    public final static Integer NORMAL = 1;
    /***
     * 删除
     */
    public final static Integer DELETE = 2;
    /***
     * 未撤销
     */
    public final static Integer NOT_REVOKED = 1;
    /***
     * 撤销
     */
    public final static Integer READ_UNDO = 2;
    /***
     * 插入已读
     */
    public final static Integer INSERT_READ = 1;
    /**
     * 发送
     */
    public final static Integer SEND = 1;
    /***
     * 撤回
     */
    public final static Integer REVOCATION = 0;

    /**
     * 数据库 全部会员
     */
    public final static Integer ALL_MEMBER = 1;
    /**
     * 数据库 特定会员
     */
    public final static Integer SPECIFIC_MEMBER =2;

    /**
     * 数据库 终端
     */
    public final static Integer TERMINAL = 3;


    /**
     * 前端发送对象(1:会员2:终端) 会员
     */
    public final static Integer SEND_MEMBER = 1;
    /**
     * 前端发送对象(1:会员2:终端) 终端
     */
    public final static Integer SEND_TERMINAL = 2;


}
