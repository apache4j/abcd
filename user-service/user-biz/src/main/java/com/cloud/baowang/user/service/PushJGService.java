package com.cloud.baowang.user.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.push.api.PushApi;
import com.cloud.baowang.common.push.bean.push.PushParam;
import com.cloud.baowang.common.push.bean.push.PushResult;
import com.cloud.baowang.common.push.bean.push.message.notification.NotificationMessage;
import com.cloud.baowang.common.push.bean.push.options.Options;
import com.cloud.baowang.common.push.constants.ApiConstants;
import com.cloud.baowang.common.push.enums.Platform;
import com.cloud.baowang.user.config.ThreadPoolConfig;
import com.cloud.baowang.user.po.UserNoticeConfigPO;
import com.cloud.baowang.user.util.PushLangUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 极光推送服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PushJGService {
    private final PushApi pushApi;


    /**
     * 向所有用户推送通知。
     *
     * @param userNoticeConfigPO 用户通知配置对象，包含用户的通知设置。
     * @param i18nData           国际化数据的映射，键为语言代码，值为对应的国际化消息列表。
     */
    public void pushAll(UserNoticeConfigPO userNoticeConfigPO, Map<String, List<I18nMsgFrontVO>> i18nData) {
        PushParam param = createPushParam(userNoticeConfigPO, i18nData, Arrays.asList(Platform.android, Platform.ios), new ArrayList<>());
        log.info("极光发送数据:{}", JSONObject.toJSONString(param));
        pushToApi(param);
    }

    /**
     * 异步地向指定用户推送通知。
     *
     * <p>该方法使用自定义的线程池 {@code ThreadPoolConfig.USER_NOTICE_EXECUTOR} 异步执行，以避免阻塞主线程。</p>
     *
     * @param userNoticeConfigPO 用户通知配置对象，包含用户的通知设置。
     * @param i18nData           国际化数据的映射，键为语言代码，值为对应的国际化消息列表。
     * @param userIds            接收通知的用户 ID 列表。
     */
    @Async(ThreadPoolConfig.USER_NOTICE_EXECUTOR)
    public void pushByUserIds(UserNoticeConfigPO userNoticeConfigPO, Map<String, List<I18nMsgFrontVO>> i18nData, List<String> userIds) {
        PushParam param = createPushParam(userNoticeConfigPO, i18nData, Arrays.asList(Platform.android, Platform.ios), userIds);
        log.info("极光发送数据:{}", JSONObject.toJSONString(param));
        pushToApi(param);
    }

    /**
     * 向所有 iOS 设备推送通知。
     *
     * @param userNoticeConfigPO 用户通知配置对象，包含用户的通知设置。
     * @param i18nData           国际化数据的映射，键为语言代码，值为对应的国际化消息列表。
     */
    public void pushDeviceIOS(UserNoticeConfigPO userNoticeConfigPO, Map<String, List<I18nMsgFrontVO>> i18nData) {
        PushParam param = createPushParam(userNoticeConfigPO, i18nData, List.of(Platform.ios), new ArrayList<>());
        log.info("极光发送数据:{}", JSONObject.toJSONString(param));
        pushToApi(param);
    }

    /**
     * 向所有 Android 设备推送通知。
     *
     * @param userNoticeConfigPO 用户通知配置对象，包含用户的通知设置。
     * @param i18nData           国际化数据的映射，键为语言代码，值为对应的国际化消息列表。
     */
    public void pushDeviceAndroid(UserNoticeConfigPO userNoticeConfigPO, Map<String, List<I18nMsgFrontVO>> i18nData) {
        PushParam param = createPushParam(userNoticeConfigPO, i18nData, List.of(Platform.android), new ArrayList<>());
        log.info("极光发送数据:{}", JSONObject.toJSONString(param));
        pushToApi(param);
    }


    /**
     * 创建用于推送的参数对象。
     *
     * <p>该方法根据提供的用户通知配置、国际化数据、平台列表和用户 ID 列表，构建并返回一个 {@link PushParam} 对象，
     * 该对象包含推送所需的所有信息。</p>
     *
     * @param userNoticeConfigPO 用户通知配置对象，包含通知的标题和内容的国际化代码。
     * @param i18nData           国际化数据的映射，键为语言代码，值为对应的国际化消息列表。
     * @param platforms          目标推送的平台列表，例如 Android 或 iOS。
     * @param userIds            接收通知的用户 ID 列表。如果为空，则表示推送给所有用户。
     * @return 构建的 {@link PushParam} 对象，包含推送所需的所有信息。
     */
    private PushParam createPushParam(UserNoticeConfigPO userNoticeConfigPO,
                                      Map<String, List<I18nMsgFrontVO>> i18nData,
                                      List<Platform> platforms,
                                      List<String> userIds) {
        String title = userNoticeConfigPO.getNoticeTitleI18nCode();
        String content = userNoticeConfigPO.getMessageContentI18nCode();

        PushParam param = new PushParam();
        PushParam.Body body = new PushParam.Body();

        //  构建多语言
        Map<String, Object> multiLanguage = prepareMultiLanguage(i18nData, title, content);

        // Set up Notification Message
        NotificationMessage notificationMessage = createNotificationMessage();
        body.setNotification(notificationMessage);

        Options options = new Options();
        options.setMultiLanguage(multiLanguage);
        // 设置厂商通道
        /*Map<String, Object> thirdPartyChannel = new HashMap<>();
        Map<String,Object>   distribution  = new HashMap<>();
        distribution.put()
        thirdPartyChannel.put("fcm",distribution);
        options.setThirdPartyChannel(thirdPartyChannel);*/
        body.setOptions(options);

        param.setBody(body);
        if (CollectionUtil.isNotEmpty(userIds)) {
            Map<String, Object> mapAlias = new HashMap<>();
            mapAlias.put("alias", userIds);
            param.setTo(mapAlias);

        } else {
            param.setTo(ApiConstants.To.ALL);  // Send to all users
        }
        body.setPlatform(platforms);  // Send to specified platforms

        return param;
    }

    /**
     * 构建多语言推送内容。
     *
     * <p>根据提供的国际化数据、标题和内容的国际化代码，生成一个包含多语言标题、内容和 iOS 副标题的映射，
     * 以支持不同语言的推送通知。</p>
     *
     * @param i18nData 国际化数据的映射，键为语言代码，值为对应的国际化消息列表。
     * @param title    通知标题的国际化代码，用于从国际化数据中获取对应的标题。
     * @param content  通知内容的国际化代码，用于从国际化数据中获取对应的内容。
     * @return 包含多语言推送内容的映射，键为推送服务支持的语言代码，值为包含标题、内容和 iOS 副标题的映射。
     */
    private Map<String, Object> prepareMultiLanguage(Map<String, List<I18nMsgFrontVO>> i18nData, String title, String content) {
        Map<String, String> titleMap = processListToMap(i18nData.get(title));
        Map<String, String> contentMap = processListToMap(i18nData.get(content));
        Map<String, Object> multiLanguage = new HashMap<>();

        for (LanguageEnum type : LanguageEnum.values()) {
            String lang = type.getLang();
            String langJG = PushLangUtil.getLangCode(lang);
            Map<String, Object> oneLanguage = new HashMap<>();
            oneLanguage.put("content", contentMap.get(lang));
            oneLanguage.put("title", titleMap.get(lang));
            oneLanguage.put("ios_subtitle", contentMap.get(lang)); // iOS subtitle
            multiLanguage.put(langJG, oneLanguage);
        }

        return multiLanguage;
    }

    /**
     * 创建并返回一个包含默认设置的通知消息对象。
     *
     * <p>该方法初始化一个 {@link NotificationMessage} 对象，并为 Android 和 iOS 平台设置默认的通知参数。
     * 这些默认值在实际推送过程中可能会被覆盖。</p>
     *
     * @return 初始化后的 {@link NotificationMessage} 对象，包含默认的通知设置。
     */
    private NotificationMessage createNotificationMessage() {
        NotificationMessage notificationMessage = new NotificationMessage();
        NotificationMessage.Android android = new NotificationMessage.Android();
        android.setAlert("default ");//会被覆盖
        android.setTitle("default");//会被覆盖
        android.setSound("default");//会被覆盖
        android.setBadgeAddNumber(1);
        notificationMessage.setAndroid(android);

        NotificationMessage.IOS ios = new NotificationMessage.IOS();
        Map<String, Object> iosAlert = new HashMap<>();
        iosAlert.put("title", "hello");//会被覆盖
        iosAlert.put("body", "hello");//会被覆盖
        ios.setAlert(iosAlert);
        ios.setSound("default");
        ios.setBadge(1);
        notificationMessage.setIos(ios);

        return notificationMessage;
    }

    /**
     * 向极光推送 API 发送推送请求。
     * <p>
     * 该方法接收一个 {@link PushParam} 对象，调用极光推送 API 进行消息推送，并记录操作结果。
     * </p>
     *
     * @param param {@link PushParam} 对象，包含推送所需的参数。
     *              结果：
     *              <p>
     *              错误码
     *              Code	描述	详细解释	HTTP Status Code
     *              20101	推送参数无效	registration_id 无效或不属于当前 appkey	400
     *              21001	只支持 HTTP Post 方法	不支持 Get 方法	405
     *              21002	缺少了必须的参数	必须改正	400
     *              21003	参数值不合法	必须改正	400
     *              21004	验证失败	必须改正，详情请看：调用验证	401
     *              21005	消息体太大	必须改正， Notification+Message长度限制为 2048 字节	400
     *              21008	app_key 参数非法	必须改正，请仔细对比你所传的 appkey 是否与应用信息中的一致，是否多了空格	400
     *              21009	系统内部错误	必须改正	400
     *              21011	没有满足条件的推送目标	请检查 to 字段	400
     *              21015	请求参数校验失败	存在非预期的参数	400
     *              21016	请求参数校验失败	参数类型错误，或者参数长度超出限制	400
     *              21030	内部服务超时	稍后重试	503
     *              21050	live_activity event参数错误	event参数必须为“start”，“update”、"end"	400
     *              21051	live_activity audience参数错误	实时活动创建时，推送目标只能是广播或者reg推送	400
     *              21052	live_activity attributes-type参数错误	event=start时，attributes-type不允许为空	400
     *              21053	live_activity content-state参数错误	content-state不允许为空	400
     *              21054	live_activity 参数错误,不允许同时通知和自定义消息	voip、message、notificatioin、live_activity不能并存	400
     *              21055	live_activity ios非p8证书	实时活动仅支持p8证书	400
     *              21056	live_activity 仅支持ios平台	platform参数必须是ios	400
     *              21057	voip 消息不允许和其他消息类型并存	voip、message、notificatioin、live_activity不能并存	400
     *              21058	voip 仅支持ios平台	platform参数必须是ios	400
     *              21059	参数错误	该消息类型不支持 big_push_duration	401
     *              23006	参数错误	定速推送 big_push_duration 超过最大值 1440	400
     *              23008	接口限速	单应用推送接口 qps 达到上限(500 qps)	400
     *              23009	推送权限错误	当前推送ip地址不在应用ip白名单内	400
     *              27000	系统内存错误	请重试	500
     *              27001	参数错误	校验信息为空	401
     *              27008	参数错误	third_party_channel 里面的 distribution 不为空，但是 notification 的 alert 内容为空	401
     *              27009	参数错误	third_party_channel 中 distribution 格式无效或为空	401
     *              21038	推送权限错误	VIP已过期或未开通	401
     *              21306	参数错误	通知消息和自定义消息不能同时推送	401
     */
    private void pushToApi(PushParam param) {
        try {
            PushResult result = pushApi.push(param);
            log.info("极光发送数据:result{}", JSONObject.toJSONString(result));
        } catch (Exception e) {
            log.error("极光发送数据失败: 参数={}, 异常信息={}", JSONObject.toJSONString(param), e.getMessage(), e);

        }
    }

    /**
     * 将 {@link I18nMsgFrontVO} 对象列表转换为语言与消息的映射。
     * <p>
     * 该方法遍历传入的 {@link I18nMsgFrontVO} 列表，将每个对象的语言代码作为键，对应的消息内容作为值，构建一个 {@link Map}。
     * </p>
     *
     * @param i18nMsgFrontVOS {@link I18nMsgFrontVO} 对象列表，包含语言代码和对应的消息内容。
     * @return {@link Map}，键为语言代码，值为对应的消息内容。
     */
    private Map<String, String> processListToMap(List<I18nMsgFrontVO> i18nMsgFrontVOS) {
        Map<String, String> result = new HashMap<>();
        for (I18nMsgFrontVO vo : i18nMsgFrontVOS) {
            result.put(vo.getLanguage(), vo.getMessage());
        }
        return result;
    }


}
