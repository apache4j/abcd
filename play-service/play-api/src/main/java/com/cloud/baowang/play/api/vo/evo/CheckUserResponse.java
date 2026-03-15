package com.cloud.baowang.play.api.vo.evo;

import com.cloud.baowang.play.api.enums.evo.EvoGameErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * CheckUserResponse
 * 用于表示 Evolution API 返回的 CheckUserResponse 数据
 */
@Data
public class CheckUserResponse implements Serializable {

    /**
     * 请求状态
     * 可能的值参考 Evolution API Status Types 文档
     * 如果 HTTP 不是 200，则映射为 TEMPORARYERROR
     * 如果无法解析响应，则映射为 TEMPORARYERROR
     * 未知值映射为 UNKNOWN_ERROR
     */
    private String status;

    /**
     * 唯一响应 ID，用于标识本次 CheckUserResponse
     */
    private String uuid;

    /**
     * 玩家 Session ID
     * 可选字段，如果需要更新初始 UserAuthentication 返回的 SID，可以在此返回新值
     */
    private String sid;

    /**
     * 构建成功响应
     */
    public static CheckUserResponse success(String uuid, String sid) {
        CheckUserResponse resp = new CheckUserResponse();
        resp.setStatus("OK");  // 通常状态用大写 OK
        resp.setUuid(uuid);
        resp.setSid(sid);
        return resp;
    }

    /**
     * 构建失败响应
     */
    public static CheckUserResponse fail(String uuid, String sid, EvoGameErrorCode evoGameErrorCode) {
        CheckUserResponse resp = new CheckUserResponse();
        resp.setStatus(evoGameErrorCode.getMessage());  // 通常状态用大写 FAIL
        resp.setUuid(uuid);
        resp.setSid(sid);
        return resp;
    }
}
