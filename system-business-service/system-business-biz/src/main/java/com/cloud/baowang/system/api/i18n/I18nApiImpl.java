package com.cloud.baowang.system.api.i18n;

import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.i18n.dto.I18NMessageDTO;
import com.cloud.baowang.system.api.vo.i18n.I18nSearchVO;
import com.cloud.baowang.system.service.I18nAddInitLangMessageService;
import com.cloud.baowang.system.service.I18nMessageService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@AllArgsConstructor
public class I18nApiImpl implements I18nApi {

    private final I18nMessageService i18nMessageService;

    private final I18nAddInitLangMessageService i18nAddInitLangMessageService;



    @Override
    public ResponseVO<String> getMessage(String type, String key, String language) {
        return ResponseVO.success(i18nMessageService.getMessage(type, key, language));
    }

    @Override
    public ResponseVO<List<I18NMessageDTO>> getMessageByType(String type) {
        return ResponseVO.success(i18nMessageService.getMessageByType(type));
    }

    @Override
    public ResponseVO<List<I18NMessageDTO>> getMessageByKey(String messageKey) {
        return ResponseVO.success(i18nMessageService.getMessageByKey(messageKey));
    }

    @Override
    public ResponseVO<List<I18NMessageDTO>> getMessageByKeyList(List<String> messageKeyList) {
        return ResponseVO.success(i18nMessageService.getMessageByKeyList(messageKeyList));
    }

    @Override
    public ResponseVO<I18NMessageDTO> getByMessageKeyAndLang(String messageKey,String languageCode) {
        return ResponseVO.success(i18nMessageService.getByMessageKeyAndLang(messageKey,languageCode));
    }

    @Override
    public ResponseVO<List<I18NMessageDTO>> getMessageLikeKey(String messageKey){
        return ResponseVO.success(i18nMessageService.getMessageLikeKey(messageKey));
    }


    @Override
    public ResponseVO<Map<String, String>> getMessageInKey(List<String> keyList,String  languageCode){
        return ResponseVO.success(i18nMessageService.getMessageInKey(keyList,languageCode));
    }


    @Override
    public ResponseVO<Boolean> insert(Map<String, List<I18nMsgFrontVO>> req) {
        return i18nMessageService.insert(req);
    }


    @Override
    public ResponseVO<Boolean> update(Map<String, List<I18nMsgFrontVO>> req) {
        return i18nMessageService.update(req);
    }

    @Override
    public ResponseVO<Boolean> deleteByMsgKey(String messageKey) {
        return i18nMessageService.deleteByMsgKey(messageKey);
    }

    @Override
    public ResponseVO<Boolean> deleteBatchByMsgKey(List<String> messageKeys) {
        return i18nMessageService.deleteByMsgKey(messageKeys);
    }

    @Override
    public ResponseVO<List<String>> search(I18nSearchVO vo) {
        return i18nMessageService.search(vo);
    }

    @Override
    public ResponseVO<List<String>>  getMessageKeyLikeKeyAndMessage(String messageKey, String messageContent) {
        I18nSearchVO vo=new I18nSearchVO();
        vo.setBizKeyPrefix(messageKey);
        vo.setSearchContent(messageContent);
        return i18nMessageService.search(vo);
    }

    @Override
    public ResponseVO<Void> initI18nMessagesForLang(String lang) {
        i18nAddInitLangMessageService.initI18nMessagesForLang(lang);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<List<CodeValueVO>> searchGetLookup(I18nSearchVO vo) {
        return ResponseVO.success(i18nMessageService.searchGetLookup(vo));
    }
}
