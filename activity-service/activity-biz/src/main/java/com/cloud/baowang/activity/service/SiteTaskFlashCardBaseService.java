package com.cloud.baowang.activity.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.vo.task.SiteTaskFlashCardBaseReqVO;
import com.cloud.baowang.activity.api.vo.task.SiteTaskFlashCardBaseRespVO;
import com.cloud.baowang.activity.api.vo.task.SiteTaskFlashCardSaveVO;
import com.cloud.baowang.activity.api.vo.task.SiteTaskFlashCardStatusVO;
import com.cloud.baowang.activity.po.SiteTaskFlashCardBasePO;
import com.cloud.baowang.activity.repositories.SiteTaskFlashCardRepository;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class SiteTaskFlashCardBaseService extends ServiceImpl<SiteTaskFlashCardRepository, SiteTaskFlashCardBasePO> {

    private final I18nApi i18nApi;

    /**
     * 保存任务闪卡配置信息（如果存在则更新，不存在则插入）
     *
     * @param vo 任务闪卡配置信息 VO（前端入参）
     */
    public void saveTaskFlashCard(SiteTaskFlashCardSaveVO vo) {
        // 构建查询条件：根据任务类型和状态查找是否已存在记录
        LambdaQueryWrapper<SiteTaskFlashCardBasePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteTaskFlashCardBasePO::getTaskType, vo.getTaskType());
        queryWrapper.eq(SiteTaskFlashCardBasePO::getSiteCode, vo.getSiteCode());

        // 查询数据库中是否已有相同任务类型和状态的记录
        SiteTaskFlashCardBasePO siteTaskFlashCardBasePO = this.baseMapper.selectOne(queryWrapper);

        if (siteTaskFlashCardBasePO == null) {
            // 不存在记录，则执行插入操作
            siteTaskFlashCardBasePO = new SiteTaskFlashCardBasePO();
            BeanUtils.copyProperties(vo, siteTaskFlashCardBasePO); // VO 转为实体
            siteTaskFlashCardBasePO.setCreatedTime(System.currentTimeMillis()); // 设置创建时间
            siteTaskFlashCardBasePO.setUpdatedTime(System.currentTimeMillis()); // 设置更新时间
            siteTaskFlashCardBasePO.setCreator(vo.getOperator()); // 创建人
            siteTaskFlashCardBasePO.setUpdater(vo.getOperator()); // 更新人
            assembly(vo, siteTaskFlashCardBasePO);
            this.baseMapper.insert(siteTaskFlashCardBasePO); // 插入记录
        } else {
            String id = siteTaskFlashCardBasePO.getId();
            // 如果已存在记录，则执行更新操作
            BeanUtils.copyProperties(vo, siteTaskFlashCardBasePO); // 用最新数据覆盖
            siteTaskFlashCardBasePO.setUpdatedTime(System.currentTimeMillis()); // 更新时间
            siteTaskFlashCardBasePO.setUpdater(vo.getOperator()); // 更新人
            assembly(vo,siteTaskFlashCardBasePO);
            siteTaskFlashCardBasePO.setId(id);
            this.baseMapper.updateById(siteTaskFlashCardBasePO); // 执行更新
        }
        Map<String, List<I18nMsgFrontVO>> i18nData = new HashMap<>();

        putIfNotNull(i18nData, siteTaskFlashCardBasePO.getActivityNameI18nCode(), vo.getActivityNameI18nCodeList());
        putIfNotNull(i18nData, siteTaskFlashCardBasePO.getEntrancePictureI18nCode(), vo.getEntrancePictureI18nCodeList());
        putIfNotNull(i18nData, siteTaskFlashCardBasePO.getEntrancePicturePcI18nCode(), vo.getEntrancePicturePcI18nCodeList());

        putIfNotNull(i18nData, siteTaskFlashCardBasePO.getActivityIntroduceI18nCode(), vo.getActivityIntroduceI18nCodeList());

        ResponseVO<Boolean> i18Bool = i18nApi.insert(i18nData);

        if (!i18Bool.isOk() || !i18Bool.getData()) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }



    }

    private void putIfNotNull(Map<String, List<I18nMsgFrontVO>> map, String key, List<I18nMsgFrontVO> value) {
        if (key != null && value != null) {
            map.put(key, value);
        }
    }
    private void assembly(SiteTaskFlashCardSaveVO vo,SiteTaskFlashCardBasePO siteTaskFlashCardBasePO){
        String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_NAME.getCode());
        siteTaskFlashCardBasePO.setActivityNameI18nCode(activityNameI18);
        if(CollectionUtil.isNotEmpty(vo.getEntrancePictureI18nCodeList())){
            String activityEntrancePicI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_ENTRANCE_PICTURE.getCode());
            siteTaskFlashCardBasePO.setEntrancePictureI18nCode(activityEntrancePicI18);
        }
        if (CollectionUtil.isNotEmpty(vo.getEntrancePicturePcI18nCodeList())) {
            String activityEntrancePicPcI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_ENTRANCE_PICTURE_PC.getCode());
            siteTaskFlashCardBasePO.setEntrancePicturePcI18nCode(activityEntrancePicPcI18);
        }

        if(CollectionUtil.isNotEmpty(vo.getActivityIntroduceI18nCodeList())){
            // 活动简介
            String activityIntroduceI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_INTRODUCE.getCode());
            siteTaskFlashCardBasePO.setActivityIntroduceI18nCode(activityIntroduceI18nCode);
        }
    }




    public SiteTaskFlashCardBaseRespVO queryFlashCard(SiteTaskFlashCardBaseReqVO vo) {
        // 构建查询条件：根据任务类型和站点编码查找是否已存在记录
        LambdaQueryWrapper<SiteTaskFlashCardBasePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteTaskFlashCardBasePO::getTaskType, vo.getTaskType());
        queryWrapper.eq(SiteTaskFlashCardBasePO::getSiteCode, vo.getSiteCode());

        // 执行查询
        SiteTaskFlashCardBasePO siteTaskFlashCardBasePO = this.baseMapper.selectOne(queryWrapper);

        // 如果没有找到对应记录，则创建一个默认对象
        if (siteTaskFlashCardBasePO == null) {
            siteTaskFlashCardBasePO = new SiteTaskFlashCardBasePO();
            siteTaskFlashCardBasePO.setTaskType(vo.getTaskType());
            siteTaskFlashCardBasePO.setSiteCode(vo.getSiteCode());
            siteTaskFlashCardBasePO.setStatus(0); // 默认状态，可根据业务设定
            siteTaskFlashCardBasePO.setCreatedTime(System.currentTimeMillis());
            siteTaskFlashCardBasePO.setUpdatedTime(System.currentTimeMillis());
            siteTaskFlashCardBasePO.setCreator(vo.getOperator());
            siteTaskFlashCardBasePO.setUpdater(vo.getOperator());

            // 插入新记录到数据库
            this.baseMapper.insert(siteTaskFlashCardBasePO);
        }

        // 转换为响应对象并返回
        return ConvertUtil.entityToModel(siteTaskFlashCardBasePO, SiteTaskFlashCardBaseRespVO.class);
    }

    public void updateTaskFlashCardStatus(SiteTaskFlashCardStatusVO vo) {
        LambdaUpdateWrapper<SiteTaskFlashCardBasePO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SiteTaskFlashCardBasePO::getTaskType, vo.getTaskType());
        updateWrapper.eq(SiteTaskFlashCardBasePO::getSiteCode, vo.getSiteCode());
        updateWrapper.set(SiteTaskFlashCardBasePO::getStatus, vo.getStatus());
        updateWrapper.set(SiteTaskFlashCardBasePO::getUpdatedTime, System.currentTimeMillis());
        updateWrapper.set(SiteTaskFlashCardBasePO::getUpdater, vo.getOperator());
        this.baseMapper.update(null, updateWrapper);
    }
}

