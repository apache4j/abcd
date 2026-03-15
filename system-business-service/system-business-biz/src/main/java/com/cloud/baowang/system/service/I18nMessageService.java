package com.cloud.baowang.system.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.i18n.dto.I18NMessageDTO;
import com.cloud.baowang.system.api.vo.i18n.I18nSearchVO;
import com.cloud.baowang.system.po.I18NMessagePO;
import com.cloud.baowang.system.repositories.I18NMessageRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cloud.baowang.common.core.constants.I18MessageConstants.BACK_END_MESSAGE_TYPE;



@Slf4j
@Service
@AllArgsConstructor
public class I18nMessageService extends ServiceImpl<I18NMessageRepository, I18NMessagePO> {

    private final SystemParamApi systemParamApi;
    private final SystemParamService systemParamService;

    public String getMessage(String type, String key, String language) {
        String message = "";
        LambdaQueryWrapper<I18NMessagePO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(I18NMessagePO::getMessageType, type);
        lambdaQueryWrapper.eq(I18NMessagePO::getMessageKey, key);
        lambdaQueryWrapper.eq(I18NMessagePO::getLanguage, language);
        I18NMessagePO i18NMessagePO = getOne(lambdaQueryWrapper);
        if (ObjectUtils.isNotEmpty(i18NMessagePO)) {
            message = i18NMessagePO.getMessage();
        }
        return message;
    }


    public List<I18NMessageDTO> getMessageByType(String type) {
        LambdaQueryWrapper<I18NMessagePO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(I18NMessagePO::getMessageType, type);
        List<I18NMessagePO> i18NMessagePOS = list(lambdaQueryWrapper);

        if (CollUtil.isNotEmpty(i18NMessagePOS)) {
            return ConvertUtil.entityListToModelList(i18NMessagePOS, I18NMessageDTO.class);
        }
        return Lists.newArrayList();
    }

    public List<I18NMessageDTO> getMessageByKey(String messageKey) {
        List<I18NMessagePO> i18NMessagePOS = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(I18NMessagePO::getMessageType, BACK_END_MESSAGE_TYPE)
                .eq(I18NMessagePO::getMessageKey, messageKey)
                .list();

        if (CollUtil.isNotEmpty(i18NMessagePOS)) {
            return ConvertUtil.entityListToModelList(i18NMessagePOS, I18NMessageDTO.class);
        }
        return Lists.newArrayList();
    }


    public List<I18NMessageDTO> getMessageByKeyList(List<String> messageKeyList) {
        List<I18NMessagePO> i18NMessagePOS = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(I18NMessagePO::getMessageType, BACK_END_MESSAGE_TYPE)
                .in(I18NMessagePO::getMessageKey, messageKeyList)
                .list();

        if (CollUtil.isNotEmpty(i18NMessagePOS)) {
            return ConvertUtil.entityListToModelList(i18NMessagePOS, I18NMessageDTO.class);
        }
        return Lists.newArrayList();
    }


    public I18NMessageDTO getByMessageKeyAndLang(String messageKey, String languageCode) {
        I18NMessagePO i18NMessagePO = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(I18NMessagePO::getMessageType, BACK_END_MESSAGE_TYPE)
                .eq(I18NMessagePO::getMessageKey, messageKey)
                .eq(I18NMessagePO::getLanguage, languageCode)
                .getEntity();

        if (i18NMessagePO!=null) {
            return ConvertUtil.entityToModel(i18NMessagePO,I18NMessageDTO.class);
        }
        return null;
    }

    public List<I18NMessageDTO> getMessageLikeKey(String messageKey) {
        List<I18NMessagePO> i18NMessagePOS = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(I18NMessagePO::getMessageType, BACK_END_MESSAGE_TYPE)
                .likeRight(I18NMessagePO::getMessageKey, messageKey)
                .list();
        if (CollUtil.isNotEmpty(i18NMessagePOS)) {
            return ConvertUtil.entityListToModelList(i18NMessagePOS, I18NMessageDTO.class);
        }
        return Lists.newArrayList();
    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> deleteByMsgKey(String messageKey) {
        baseMapper.delete(Wrappers.<I18NMessagePO>lambdaQuery().eq(I18NMessagePO::getMessageKey, messageKey));
        // delay del redis cache
        RedisUtil.deleteLocalCachedMap(CacheConstants.KEY_I18N_MESSAGE, messageKey);
        return ResponseVO.success(true);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> deleteByMsgKey(List<String> messageKeys) {
        baseMapper.delete(Wrappers.<I18NMessagePO>lambdaQuery().in(I18NMessagePO::getMessageKey, messageKeys));
        // delay del redis cache
        messageKeys.forEach(messageKey ->
                RedisUtil.deleteLocalCachedMap(CacheConstants.KEY_I18N_MESSAGE, messageKey));
        return ResponseVO.success(true);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> insert(Map<String, List<I18nMsgFrontVO>> req) {
        if (MapUtil.isEmpty(req)){
            return ResponseVO.success(false);
        }
        for (Map.Entry<String, List<I18nMsgFrontVO>> entry : req.entrySet()) {
            String messageKey = entry.getKey();
            List<I18nMsgFrontVO> value = entry.getValue();
            List<I18NMessagePO> i18NMessagePOS = ConvertUtil.entityListToModelList(value, I18NMessagePO.class);
            i18NMessagePOS.forEach(s -> {
                s.setMessageType(BACK_END_MESSAGE_TYPE);
                s.setMessageKey(messageKey);
            });
            saveBatch(i18NMessagePOS);
            RedisUtil.deleteLocalCachedMap(CacheConstants.KEY_I18N_MESSAGE, messageKey);
        }
        return ResponseVO.success(true);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> update(List<I18nMsgFrontVO> req) {
        if (CollUtil.isEmpty(req)) {
            return ResponseVO.success(true);
        }
        List<I18NMessagePO> i18NMessagePOS = ConvertUtil.entityListToModelList(req, I18NMessagePO.class);
        List<String> messageKeys = i18NMessagePOS.stream().map(I18NMessagePO::getMessageKey).distinct().toList();
        // del
        baseMapper.delete(Wrappers.<I18NMessagePO>lambdaQuery().eq(I18NMessagePO::getMessageType, BACK_END_MESSAGE_TYPE).in(I18NMessagePO::getMessageKey, messageKeys));
        for (I18NMessagePO e : i18NMessagePOS) {
            e.setMessageType(BACK_END_MESSAGE_TYPE);
        }
        // insert
        saveBatch(i18NMessagePOS);
        // del cache
        messageKeys.forEach(messageKey ->
                RedisUtil.deleteLocalCachedMap(CacheConstants.KEY_I18N_MESSAGE, messageKey)
        );
        return ResponseVO.success(true);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> update(Map<String, List<I18nMsgFrontVO>> req) {
        if (CollUtil.isEmpty(req)) {
            return ResponseVO.success(true);
        }
        // del
        Set<String> messageKeys = req.keySet();
        baseMapper.delete(Wrappers.<I18NMessagePO>lambdaQuery().in(I18NMessagePO::getMessageKey, messageKeys));
        // insert
        for (Map.Entry<String, List<I18nMsgFrontVO>> entry : req.entrySet()) {
            String messageKey = entry.getKey();
            List<I18nMsgFrontVO> value = entry.getValue();
            List<I18NMessagePO> i18NMessagePOS = ConvertUtil.entityListToModelList(value, I18NMessagePO.class);
            i18NMessagePOS.forEach(s -> {
                s.setMessageType(BACK_END_MESSAGE_TYPE);
                s.setMessageKey(messageKey);
            });
            saveBatch(i18NMessagePOS);
            RedisUtil.deleteLocalCachedMap(CacheConstants.KEY_I18N_MESSAGE, messageKey);
        }
        return ResponseVO.success(true);
    }

    public ResponseVO<List<String>> search(I18nSearchVO vo) {
        List<I18NMessagePO> messagePOS = new LambdaQueryChainWrapper<>(baseMapper)
                .select(I18NMessagePO::getMessageKey)
                .eq(I18NMessagePO::getMessageType, BACK_END_MESSAGE_TYPE)
                .eq(StrUtil.isNotBlank(vo.getLang()),I18NMessagePO::getLanguage, vo.getLang())
                .likeRight(StrUtil.isNotBlank(vo.getBizKeyPrefix()), I18NMessagePO::getMessageKey, vo.getBizKeyPrefix()) // like “BIZ_%”
                .like(StrUtil.isNotBlank(vo.getSearchContent()), I18NMessagePO::getMessage, vo.getSearchContent())
                .eq(StrUtil.isNotBlank(vo.getExactSearchContent()), I18NMessagePO::getMessage, vo.getExactSearchContent())
                .list();
        if (CollUtil.isEmpty(messagePOS)) {
            return ResponseVO.success();
        }
        return ResponseVO.success(messagePOS.stream().map(I18NMessagePO::getMessageKey).toList());
    }


    public Map<String, String> getMessageInKey(List<String> keyList,String  languageCode) {
        List<I18NMessagePO> messagePOS = new LambdaQueryChainWrapper<>(baseMapper)
                .select(I18NMessagePO::getMessageKey,I18NMessagePO::getMessage)
                .eq(I18NMessagePO::getMessageType, BACK_END_MESSAGE_TYPE)
                .eq(I18NMessagePO::getLanguage, languageCode)
                .in(I18NMessagePO::getMessageKey, keyList) // like “BIZ_%”
                .list();
        if (CollUtil.isEmpty(messagePOS)) {
            return Maps.newHashMap();
        }
        return messagePOS.stream().collect(Collectors.toMap(I18NMessagePO::getMessageKey,I18NMessagePO::getMessage));
    }

    public List<CodeValueVO> searchGetLookup(I18nSearchVO vo) {
        List<I18NMessagePO> messagePOS = new LambdaQueryChainWrapper<>(baseMapper)
                .select(I18NMessagePO::getMessageKey)
                .eq(I18NMessagePO::getMessageType, BACK_END_MESSAGE_TYPE)
                .eq(StrUtil.isNotBlank(vo.getLang()), I18NMessagePO::getLanguage, vo.getLang())
                .likeRight(StrUtil.isNotBlank(vo.getBizKeyPrefix()), I18NMessagePO::getMessageKey, vo.getBizKeyPrefix()) // like “BIZ_%”
                .likeRight(StrUtil.isNotBlank(vo.getSearchContent()), I18NMessagePO::getMessage, vo.getSearchContent())
                .and(CollectionUtil.isNotEmpty(vo.getBizKeyPrefixList()), wrapper -> {
                    for (String prefix : vo.getBizKeyPrefixList()) {
                        wrapper.or().likeRight(I18NMessagePO::getMessageKey, prefix);
                    }
                })
                .list();
        if (CollUtil.isEmpty(messagePOS)) {
            return List.of();
        }
        List<String> list = messagePOS.stream().map(I18NMessagePO::getMessageKey).toList();
        return systemParamService.getSystemParamByValue(list);
    }
}
