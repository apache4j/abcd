package com.cloud.baowang.system.api.api.i18n;

import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.i18n.dto.I18NMessageDTO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.i18n.I18nSearchVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteI18nApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - i18n")
public interface I18nApi {

    String PREFIX = ApiConstants.PREFIX + "/message/api/";

    @GetMapping(value = PREFIX + "{type}/{key}/{language}")
    @Operation(summary = "获取翻译结果")
    ResponseVO<String> getMessage(@PathVariable("type") String type, @PathVariable("key") String key, @PathVariable("language") String language);


    @GetMapping(value = PREFIX + "{type}")
    @Operation(summary = "根据类型获取多语言配置信息")
    ResponseVO<List<I18NMessageDTO>> getMessageByType(@PathVariable("type") String type);

    @GetMapping(value = PREFIX + "getByMessageKey")
    @Operation(summary = "根据key获取多语言配置信息")
    ResponseVO<List<I18NMessageDTO>> getMessageByKey(@RequestParam("messageKey") String messageKey);

    @PostMapping(value = PREFIX + "getByMessageKeyList")
    @Operation(summary = "根据key获取多语言配置信息")
    ResponseVO<List<I18NMessageDTO>> getMessageByKeyList(@RequestBody List<String> messageKeyList);

    @GetMapping(value = PREFIX + "getByMessageKeyAndLang")
    @Operation(summary = "根据key和语言获取多语言配置信息")
    ResponseVO<I18NMessageDTO> getByMessageKeyAndLang(@RequestParam("messageKey") String messageKey,@RequestParam("languageCode") String  languageCode);

    @GetMapping(value = PREFIX + "getMessageLikeKey")
    @Operation(summary = "根据key模糊匹配,获取多语言配置信息")
    ResponseVO<List<I18NMessageDTO>> getMessageLikeKey(@RequestParam("messageKey") String messageKey);

    @PostMapping(value = PREFIX + "getMessageInKey")
    @Operation(summary = "根据多个key查找,获取多语言配置信息")
    ResponseVO<Map<String, String>> getMessageInKey(@RequestBody List<String> keyList,@RequestParam("languageCode") String  languageCode);

    @PostMapping(value = PREFIX + "insert")
    @Operation(summary = "批量插入多语言配置信息")
    ResponseVO<Boolean> insert(@RequestBody Map<String, List<I18nMsgFrontVO>> req);

    @PostMapping(value = PREFIX + "updateByMap")
    @Operation(summary = "更新多语言配置信息")
    ResponseVO<Boolean> update(@RequestBody Map<String, List<I18nMsgFrontVO>> req);

    @PostMapping(value = PREFIX + "deleteByMsgKey")
    @Operation(summary = "根据key删除多语言配置信息")
    ResponseVO<Boolean> deleteByMsgKey(@RequestParam("messageKey") String messageKey);

    @PostMapping(value = PREFIX + "deleteBatchByMsgKey")
    @Operation(summary = "根据批量key删除多语言配置信息")
    ResponseVO<Boolean> deleteBatchByMsgKey(@RequestBody List<String> messageKeys);

    /**
     * 根据内容模糊搜索
     * @param vo
     * @return list 业务message key
     */
    @PostMapping(value = PREFIX + "search")
    @Operation(summary = "根据内容模糊搜索")
    ResponseVO<List<String>> search(@Valid @RequestBody I18nSearchVO vo);

    /**
     * 根据内容搜索lookup，默认取当前语言环境
     *
     * @param vo
     * @return list 业务message key
     */
    @PostMapping(value = PREFIX + "searchGetLookup")
    @Operation(summary = "根据i18nkey匹配名称反查lookup")
    ResponseVO<List<CodeValueVO>> searchGetLookup(@Valid @RequestBody I18nSearchVO vo);

    @GetMapping(value = PREFIX + "getMessageKeyLikeKeyAndMessage")
    @Operation(summary = "根据key模糊匹配,message模糊匹配,获取多语言配置信息")
    ResponseVO<List<String>> getMessageKeyLikeKeyAndMessage(@RequestParam("messageKey")String messageKey, @RequestParam("messageContent")String messageContent);



    @GetMapping(value = PREFIX + "initI18nMessagesForLang")
    @Operation(summary = "根据指定语言初始化国际化消息")
    ResponseVO<Void> initI18nMessagesForLang(@RequestParam("lang") String lang);

}