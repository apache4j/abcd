package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ImageSizeEnum;
import com.cloud.baowang.agent.api.enums.ImageTypeEnum;
import com.cloud.baowang.agent.api.enums.RecordImageEnum;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImagePageQueryVO;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImageVO;
import com.cloud.baowang.agent.po.AgentImagePO;
import com.cloud.baowang.agent.po.AgentImageRecordPO;
import com.cloud.baowang.agent.repositories.AgentImageRecordRepository;
import com.cloud.baowang.agent.repositories.AgentImageRepository;
import com.cloud.baowang.agent.util.MinioFileService;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 图片管理
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentImageService {


    private final AgentImageRepository agentImageRepository;

    private final MinioFileService minioFileService;

    private final AgentImageRecordRepository agentImageRecordRepository;


    /**
     * 获取图片管理的常量
     */
    public HashMap<String, Object> getEnumList() {
        LinkedList<Map<String, Object>> imageTypeList = ImageTypeEnum.toList();
        LinkedList<Map<String, Object>> imageSizeList = ImageSizeEnum.toList();
        LinkedList<Map<String, Object>> deleteStateList = DeleteStateEnum.toList();
        LinkedList<Map<String, Object>> stopStateList = StopStateEnum.toList();
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("imageTypeList", imageTypeList);
        resultMap.put("imageSizeList", imageSizeList);
        resultMap.put("deleteStateList", deleteStateList);
        resultMap.put("stopStateList", stopStateList);
        return resultMap;
    }


    /**
     * 添加图片管理
     */
    public void addAgentImage(AgentImageVO agentImageVO) {
        verifyAgentImage(agentImageVO);

        //添加图片管理
        AgentImagePO agentImagePO = new AgentImagePO();
        BeanUtils.copyProperties(agentImageVO, agentImagePO);
        // 启用停用状态未使用该开关
        agentImagePO.setStatus(EnableStatusEnum.DISABLE.getCode());
        // 启用停用状态未使用该开关
        agentImagePO.setStatus(EnableStatusEnum.ENABLE.getCode());
        agentImagePO.setCreator(agentImageVO.getCreator());
        agentImagePO.setCreatedTime(System.currentTimeMillis());
        agentImagePO.setUpdater(agentImageVO.getCreator());
        agentImagePO.setUpdatedTime(System.currentTimeMillis());
        int ii = agentImageRepository.insert(agentImagePO);
        if (ii <= 0) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
    }


    private void verifyAgentImage(AgentImageVO agentImageVO) {
        String imageName = agentImageVO.getImageName();
        String imageSize = agentImageVO.getImageSize();
        Integer imageType = agentImageVO.getImageType();
        String imageUrl = agentImageVO.getImageUrl();

        //图片尺寸
        if (ImageSizeEnum.isNotExist(imageSize)) {
            log.info("图片尺寸不正确:{}", agentImageVO);
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (ImageTypeEnum.isNotExist(imageType)) {
            log.info("图片类型不正确:{}", agentImageVO);
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        //图片名字
        imageName = imageName.trim();
        agentImageVO.setImageName(imageName);


        //图片地址
        if (StringUtils.isBlank(imageUrl)) {
            throw new BaowangDefaultException("图片地址是空");
        }
        imageName = imageName.trim();
        agentImageVO.setImageName(imageName);
    }


    /**
     * 修改图片管理
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAgentImage(AgentImageVO agentImageVO) {
        String id = agentImageVO.getId();
        AgentImagePO agentImagePO = agentImageRepository.selectById(id);
        if (agentImagePO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        verifyAgentImage(agentImageVO);

        String remark = agentImageVO.getRemark();
        //备注
        /*if (StringUtils.isBlank(remark)) {
            throw new BaowangDefaultException("备注是空");
        }*/
        if (StringUtils.isNotEmpty(remark) && (remark.length() < 2 || remark.length() > 50)) {
            throw new BaowangDefaultException("描述的范围2-50个字内");
        } else {
            remark = remark.trim();
            agentImageVO.setRemark(remark);
        }


        Integer orderNumberVo = agentImageVO.getSort();
        //交换排序
        Integer orderNumberPo = agentImagePO.getSort();
        if (orderNumberPo == null || !orderNumberPo.equals(orderNumberVo)) {
            LambdaUpdateWrapper<AgentImagePO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(AgentImagePO::getSort, orderNumberVo).eq(AgentImagePO::getSiteCode, agentImageVO.getSiteCode());
            updateWrapper.set(AgentImagePO::getSort, orderNumberPo);
            agentImageRepository.update(null, updateWrapper);
        }
        //修改图片管理
        AgentImagePO imagePO = new AgentImagePO();
        BeanUtils.copyProperties(agentImageVO, imagePO);
        imagePO.setUpdater(agentImageVO.getUpdater());
        imagePO.setUpdatedTime(System.currentTimeMillis());
        if (agentImageRepository.updateById(imagePO) <= 0) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        List<AgentImageRecordPO> arr = new ArrayList<>();
        if (!Objects.equals(agentImagePO.getImageType(), agentImageVO.getImageType())) {
            AgentImageRecordPO agentImageRecordPO = new AgentImageRecordPO();
            agentImageRecordPO.setSiteCode(imagePO.getSiteCode());
            agentImageRecordPO.setImageName(agentImagePO.getImageName());
            agentImageRecordPO.setRemark(agentImageVO.getRemark());
            agentImageRecordPO.setCreator(agentImageVO.getUpdater());
            agentImageRecordPO.setCreatedTime(System.currentTimeMillis());
            agentImageRecordPO.setUpdater(agentImageVO.getUpdater());
            agentImageRecordPO.setUpdatedTime(System.currentTimeMillis());
            agentImageRecordPO.setImageName(agentImagePO.getImageName());
            agentImageRecordPO.setRecordType(RecordImageEnum.ImageType.getType());
            if (agentImagePO.getImageType() != null) {
                agentImageRecordPO.setBeforeText(ImageTypeEnum.getOne(agentImagePO.getImageType()).getDescription());
            }
            if (agentImageVO.getImageType() != null) {
                agentImageRecordPO.setAfterText(ImageTypeEnum.getOne(agentImageVO.getImageType()).getDescription());
            }
            arr.add(agentImageRecordPO);
        }
        if (!Objects.equals(agentImagePO.getSort(), agentImageVO.getSort())) {
            AgentImageRecordPO agentImageRecordPO = new AgentImageRecordPO();
            agentImageRecordPO.setSiteCode(imagePO.getSiteCode());
            agentImageRecordPO.setImageName(agentImagePO.getImageName());
            agentImageRecordPO.setRemark(agentImageVO.getRemark());
            agentImageRecordPO.setCreator(agentImageVO.getUpdater());
            agentImageRecordPO.setCreatedTime(System.currentTimeMillis());
            agentImageRecordPO.setUpdater(agentImageVO.getUpdater());
            agentImageRecordPO.setUpdatedTime(System.currentTimeMillis());
            agentImageRecordPO.setImageName(agentImagePO.getImageName());
            agentImageRecordPO.setRecordType(RecordImageEnum.OrderNumber.getType());
            agentImageRecordPO.setBeforeText(agentImagePO.getSort() == null ? null : agentImagePO.getSort() + "");
            agentImageRecordPO.setAfterText(agentImageVO.getSort() == null ? null : agentImageVO.getSort() + "");
            arr.add(agentImageRecordPO);
        }
        if (!StringUtils.equals(agentImagePO.getImageUrl(), agentImageVO.getImageUrl())) {
            AgentImageRecordPO agentImageRecordPO = new AgentImageRecordPO();
            agentImageRecordPO.setSiteCode(imagePO.getSiteCode());
            agentImageRecordPO.setImageName(agentImagePO.getImageName());
            agentImageRecordPO.setRemark(agentImageVO.getRemark());
            agentImageRecordPO.setCreator(agentImageVO.getUpdater());
            agentImageRecordPO.setCreatedTime(System.currentTimeMillis());
            agentImageRecordPO.setUpdater(agentImageVO.getUpdater());
            agentImageRecordPO.setUpdatedTime(System.currentTimeMillis());
            agentImageRecordPO.setImageName(agentImagePO.getImageName());
            agentImageRecordPO.setRecordType(RecordImageEnum.Image.getType());
            agentImageRecordPO.setBeforeText(agentImagePO.getImageUrl());
            agentImageRecordPO.setAfterText(agentImageVO.getImageUrl());
            arr.add(agentImageRecordPO);
        }
        if (!agentImagePO.getImageSize().equals(agentImageVO.getImageSize())) {
            AgentImageRecordPO agentImageRecordPO = new AgentImageRecordPO();
            agentImageRecordPO.setSiteCode(imagePO.getSiteCode());
            agentImageRecordPO.setImageName(agentImagePO.getImageName());
            agentImageRecordPO.setRemark(agentImageVO.getRemark());
            agentImageRecordPO.setCreator(agentImageVO.getUpdater());
            agentImageRecordPO.setCreatedTime(System.currentTimeMillis());
            agentImageRecordPO.setUpdater(agentImageVO.getUpdater());
            agentImageRecordPO.setUpdatedTime(System.currentTimeMillis());
            agentImageRecordPO.setImageName(agentImagePO.getImageName());
            agentImageRecordPO.setRecordType(RecordImageEnum.IMAGE_size.getType());
            agentImageRecordPO.setBeforeText(agentImagePO.getImageSize());
            agentImageRecordPO.setAfterText(agentImageVO.getImageSize());
            arr.add(agentImageRecordPO);
        }
        if (CollectionUtil.isNotEmpty(arr)) {
            for (AgentImageRecordPO agentImageRecordPO : arr) {
                agentImageRecordRepository.insert(agentImageRecordPO);
            }
        }
    }


    /**
     * 删除图片管理
     */
    public ResponseVO<Boolean> deleteAgentImage(String id) {
        AgentImagePO agentImagePO = agentImageRepository.selectById(id);
        if (agentImagePO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        agentImageRepository.deleteById(id);
        return ResponseVO.success();
    }


    /**
     * 获取图片管理
     */
    public ResponseVO<AgentImageVO> getAgentImageById(String id) {
        AgentImagePO agentImagePO = agentImageRepository.selectById(id);
        if (agentImagePO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        AgentImageVO vo = BeanUtil.copyProperties(agentImagePO, AgentImageVO.class);
        String domain = minioFileService.getMinioDomain();
        vo.setImageUrlFull(domain + "/" + vo.getImageUrl());
        return ResponseVO.success(vo);
    }

    /**
     * 获取图片管理的列表
     */
    public ResponseVO<Page<AgentImageVO>> getAgentImageList(AgentImagePageQueryVO pageQueryVO) {
        LambdaQueryWrapper<AgentImagePO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AgentImagePO::getSiteCode, pageQueryVO.getSiteCode());

        if (StringUtils.isNotBlank(pageQueryVO.getOrderField()) && StringUtils.isNotBlank(pageQueryVO.getOrderType())) {
            if (pageQueryVO.getOrderField().equals("updatedTime")) {
                lambdaQueryWrapper.orderBy(true, pageQueryVO.getOrderType().equals("asc"), AgentImagePO::getUpdatedTime);
            } else if (pageQueryVO.getOrderField().equals("createdTime")) {
                lambdaQueryWrapper.orderBy(true, pageQueryVO.getOrderType().equals("asc"), AgentImagePO::getCreatedTime);
            }
        } else {
            lambdaQueryWrapper.orderByAsc(AgentImagePO::getSort);
            lambdaQueryWrapper.orderByDesc(AgentImagePO::getUpdatedTime);
        }


        String imageName = pageQueryVO.getImageName();
        if (StringUtils.isNotBlank(imageName)) {
            lambdaQueryWrapper.eq(AgentImagePO::getImageName, imageName);
        }

        String creator = pageQueryVO.getCreator();
        if (StringUtils.isNotBlank(creator)) {
            lambdaQueryWrapper.eq(AgentImagePO::getCreator, creator);
        }

        String updater = pageQueryVO.getUpdater();
        if (StringUtils.isNotBlank(updater)) {
            lambdaQueryWrapper.eq(AgentImagePO::getUpdater, updater);
        }

        Integer imageType = pageQueryVO.getImageType();
        if (imageType != null) {
            lambdaQueryWrapper.eq(AgentImagePO::getImageType, imageType);
        }

        String imageSize = pageQueryVO.getImageSize();
        if (StringUtils.isNotBlank(imageSize)) {
            lambdaQueryWrapper.eq(AgentImagePO::getImageSize, imageSize);
        }
        Page<AgentImagePO> page = new Page<>(pageQueryVO.getPageNumber(), pageQueryVO.getPageSize());
        page = agentImageRepository.selectPage(page, lambdaQueryWrapper);

        String minioDomain = minioFileService.getMinioDomain();
        IPage<AgentImageVO> convert = page.convert(item -> {
            AgentImageVO vo = BeanUtil.copyProperties(item, AgentImageVO.class);
            if (StringUtils.isNotBlank(vo.getImageUrl())) {
                vo.setImageUrlFull(minioDomain + "/" + vo.getImageUrl());
            }
            return vo;
        });
        return ResponseVO.success(ConvertUtil.toConverPage(convert));
    }


}
