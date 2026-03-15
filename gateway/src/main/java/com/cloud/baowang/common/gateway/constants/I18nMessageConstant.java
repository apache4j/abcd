package com.cloud.baowang.common.gateway.constants;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @author: fangfei
 * @createTime: 2024/12/28 18:04
 * @description:
 */
public class I18nMessageConstant {
    public static final Map<String, String> LOGIN_ERROR_OTHER_AREA_MAP = ImmutableMap.<String, String>builder()
            .put("zh-CN", "您已在其他客户端登录，若非您本人操作，请修改密码")
            .put("zh-TW", "您已在其他客戶端登錄，若非您本人操作，請修改密碼")
            .put("en-US", "You are already logged in on another device,If this wasn't you, please change your password")
            .put("vi-VN", "Bạn đã đăng nhập trên một thiết bị khác, nếu không phải là bạn, hãy thay đổi mật khẩu")
            .put("ko-KR", "다른 클라이언트를 통해 로그인하셨습니다. 본인이 아닌 경우 비밀번호를 변경해 주세요.")
            .put("hi-IN", "आप पहले से ही किसी अन्य डिवाइस पर लॉग इन हैं, यदि यह आप नहीं थे, तो कृपया अपना पासवर्ड बदलें.")
            .build();

    public static final Map<String, String> LOGIN_EXPIRE_MAP = ImmutableMap.<String, String>builder()
            .put("zh-CN", "登录过期")
            .put("zh-TW", "登錄過期")
            .put("en-US", "Login expired")
            .put("vi-VN", "Đăng nhập đã hết hạn.")
            .put("ko-KR", "로그인이 만료되었습니다")
            .put("hi-IN", "लॉगिन समाप्त हो गया")
            .build();
}
