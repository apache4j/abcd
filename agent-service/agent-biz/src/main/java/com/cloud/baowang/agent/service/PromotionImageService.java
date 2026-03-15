package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.cloud.baowang.agent.api.enums.DomainTypeEnum;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImageVO;
import com.cloud.baowang.agent.po.AgentDomainPO;
import com.cloud.baowang.agent.po.AgentImagePO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.repositories.AgentDomainRepository;
import com.cloud.baowang.agent.repositories.AgentImageRepository;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.agent.util.MinioFileService;
import com.cloud.baowang.common.core.constants.CommonConstant;
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

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 推广图片素材
 */
@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class PromotionImageService {


    private final AgentImageRepository agentImageRepository;


    private final AgentDomainRepository agentDomainRepository;


    private final MinioFileService minioFileService;


    private final AgentInfoRepository agentInfoRepository;


    /**
     * 获取图片素材
     */
    public AgentImageVO getAgentImageById(AgentImageVO agentImageVO) {
        String id = agentImageVO.getId();
        AgentImagePO agentImagePO = agentImageRepository.selectById(id);
        if (agentImagePO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        AgentImageVO vo = BeanUtil.copyProperties(agentImagePO, AgentImageVO.class);
        String imageUrl = vo.getImageUrl();
        if (StringUtils.isNotBlank(imageUrl)) {
            String minioDomain = minioFileService.getMinioDomain();
            vo.setImageUrlFull(minioDomain + "/" + imageUrl);
        }
        String agentAccount = agentImageVO.getAgentAccount();
        setDomainInfo(vo, agentAccount, vo.getSiteCode());
        return vo;
    }


    private void setDomainInfo(AgentImageVO agentImageVO, String agentAccount, String siteCode) {
        LambdaQueryWrapper<AgentInfoPO> agentInfoPOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        agentInfoPOLambdaQueryWrapper.eq(AgentInfoPO::getAgentAccount, agentAccount);
        agentInfoPOLambdaQueryWrapper.last(CommonConstant.query_limit);
        AgentInfoPO agentInfoPO = agentInfoRepository.selectOne(agentInfoPOLambdaQueryWrapper);

        if (agentInfoPO != null) {
            String inviteCode = agentInfoPO.getInviteCode();
            agentImageVO.setInviteCode(inviteCode);
        }
        LambdaQueryWrapper<AgentDomainPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AgentDomainPO::getDomainType, DomainTypeEnum.H5.getType());
        lambdaQueryWrapper.orderByAsc(AgentDomainPO::getSort);
        lambdaQueryWrapper.eq(AgentDomainPO::getSiteCode, siteCode);
        lambdaQueryWrapper.last(CommonConstant.query_limit);
        AgentDomainPO domainPO = agentDomainRepository.selectOne(lambdaQueryWrapper);
        if (domainPO != null) {
            String domainName = domainPO.getDomainName();
            agentImageVO.setDomainName(domainName);
        }
    }


    /**
     * 获取图片素材的列表
     */
    public ResponseVO<Page<AgentImageVO>> getAgentImageList(AgentImageVO agentImageVO) {
        LambdaQueryWrapper<AgentImagePO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        String imageName = agentImageVO.getImageName();
        String siteCode = agentImageVO.getSiteCode();
        if (StringUtils.isNotBlank(siteCode)) {
            lambdaQueryWrapper.eq(AgentImagePO::getSiteCode, siteCode);
        }
        if (StringUtils.isNotBlank(imageName)) {
            lambdaQueryWrapper.like(AgentImagePO::getImageName, imageName);
        }
        Integer imageType = agentImageVO.getImageType();
        if (imageType != null) {
            lambdaQueryWrapper.eq(AgentImagePO::getImageType, imageType);
        }
        String imageSize = agentImageVO.getImageSize();
        if (StringUtils.isNotBlank(imageSize)) {
            lambdaQueryWrapper.eq(AgentImagePO::getImageSize, imageSize);
        }
        // 启用状态
        //lambdaQueryWrapper.eq(AgentImagePO::getImageState,CommonConstant.business_two);
        // 删除状态为正常
        ///lambdaQueryWrapper.eq(AgentImagePO::getDeleteState,CommonConstant.business_two);
        lambdaQueryWrapper.orderByAsc(AgentImagePO::getSort);
        lambdaQueryWrapper.orderByDesc(AgentImagePO::getUpdatedTime);
        //
        //agentImageVO.setPageSize(20);
        Page<AgentImagePO> page = new Page<>(agentImageVO.getPageNumber(), agentImageVO.getPageSize());
        page = agentImageRepository.selectPage(page, lambdaQueryWrapper);
        String minioDomain = minioFileService.getMinioDomain();
        IPage<AgentImageVO> convert = page.convert(item -> {
            AgentImageVO vo = BeanUtil.copyProperties(item, AgentImageVO.class);

            String imageUrl = vo.getImageUrl();
            if (StringUtils.isNotBlank(imageUrl)) {
                vo.setImageUrlFull(minioDomain + "/" + imageUrl);
            }
            setDomainInfo(agentImageVO, vo.getAgentAccount(), agentImageVO.getSiteCode());
            return vo;
        });
        return ResponseVO.success(ConvertUtil.toConverPage(convert));
    }


}
