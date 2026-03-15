package com.cloud.baowang.system.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.system.po.I18NMessagePO;
import com.cloud.baowang.system.repositories.I18NMessageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class I18nAddInitLangMessageService extends ServiceImpl<I18NMessageRepository, I18NMessagePO> {


    /**
     * 根据指定语言初始化国际化消息
     * <p>
     * 逻辑：
     * 1. 先查询 EN_US 语言的所有国际化消息（只查必要字段，避免多余开销）
     * 2. 遍历查询结果，替换语言为目标语言，构造新的消息记录
     * 3. 每 1000 条执行一次批量保存（saveOrUpdateBatch），避免单次插入过多导致内存或 SQL 压力
     * 4. 最后不足 1000 条也要补一次保存，保证数据不丢失
     */
    public void initI18nMessagesForLang(String language) {
        // 1. 查询 EN_US 语言的所有消息
        List<I18NMessagePO> i18NMessagePOS = new LambdaQueryChainWrapper<>(baseMapper)
                .select(I18NMessagePO::getMessageType,
                        I18NMessagePO::getMessageKey,
                        I18NMessagePO::getLanguage,
                        I18NMessagePO::getMessage)
                .eq(I18NMessagePO::getLanguage, LanguageEnum.EN_US.getLang())
                .list();

        if (CollUtil.isEmpty(i18NMessagePOS)) {
            // 没有找到任何 EN_US 语言的消息，不做处理
            log.warn("未查询到 EN_US 语言的国际化消息，跳过初始化");
            return;
        }
        // 如果是中文，英语，越南语，繁体中文，葡萄牙语，韩语，印地语，不初始化
        if (LanguageEnum.ZH_CN.getLang().equals(language) ||
                LanguageEnum.EN_US.getLang().equals(language) ||
                LanguageEnum.VI_VN.getLang().equals(language) ||
                LanguageEnum.ZH_TW.getLang().equals(language) ||
                LanguageEnum.PT_BR.getLang().equals(language) ||
                LanguageEnum.KO_KR.getLang().equals(language) ||
                LanguageEnum.HI_IN.getLang().equals(language)) {
            log.warn("不初始化中文、英语、越南语、繁体中文、葡萄牙语、韩语、印地语的国际化消息");
            return;
        }

        // 检查目标语言是否合法
        String codeByLang = LanguageEnum.getCodeByLang(language);
        if (codeByLang == null) {
            // 传入的 language 不是枚举里定义的语言，直接返回
            log.error("没有匹配到语言:{}", language);
            return;
        }

        // 预分配容量，避免 ArrayList 扩容带来的性能损耗
        List<I18NMessagePO> insertList = new ArrayList<>(1000);
        for (I18NMessagePO i18NMessagePO : i18NMessagePOS) {
            // 构造要插入的新记录
            I18NMessagePO insertOne = new I18NMessagePO();
            insertOne.setMessageType(i18NMessagePO.getMessageType());
            insertOne.setMessageKey(i18NMessagePO.getMessageKey());
            insertOne.setLanguage(language); // 替换成目标语言
            insertOne.setMessage(i18NMessagePO.getMessage());
            insertList.add(insertOne);

            // 每 1000 条保存一次，降低单次 SQL 压力
            if (insertList.size() >= 1000) {
                try {
                    this.saveOrUpdateBatch(insertList);
                } catch (Exception e) {
                    // 这里捕获异常，不中断循环，避免一个批次失败导致后续批次也不执行
                    log.error("批量保存国际化消息失败，当前批次大小：{}", insertList.size(), e);
                }
                insertList.clear(); // 清空，准备下一批
            }
        }

        // 保存最后不足 1000 的那批，避免丢失
        if (!insertList.isEmpty()) {
            try {
                this.saveOrUpdateBatch(insertList);
            } catch (Exception e) {
                log.error("批量保存国际化消息失败，最后一批大小：{}", insertList.size(), e);
            }
        }

        log.info("初始化 {} 语言国际化消息完成，共插入/更新 {} 条", language, i18NMessagePOS.size());
    }


}
