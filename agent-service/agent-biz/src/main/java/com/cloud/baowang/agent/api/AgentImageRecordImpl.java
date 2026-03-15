package com.cloud.baowang.agent.api;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentImageRecordApi;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImageRecordBO;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImageRecordVO;
import com.cloud.baowang.agent.po.AgentImageRecordPO;
import com.cloud.baowang.agent.repositories.AgentImageRecordRepository;
import com.cloud.baowang.agent.util.MinioFileService;
import com.cloud.baowang.agent.api.enums.RecordImageEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 图片的变更记录
 */
@Slf4j
@Service
@RestController
public class AgentImageRecordImpl implements AgentImageRecordApi {


    @Autowired
    private AgentImageRecordRepository agentImageRecordRepository;

    @Autowired
    private MinioFileService minioFileService;


    /**
     * 获取图片的变更记录的字典
     */
    public ResponseVO<HashMap<String, Object>> getEnumList() {
        LinkedList<Map<String, Object>> recordImageList = RecordImageEnum.toList();
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("recordImageList", recordImageList);
        return ResponseVO.success(resultMap);
    }


    /**
     * 获取图片的变更记录的列表
     */
    public ResponseVO<Page<AgentImageRecordBO>> getAgentImageRecordList(AgentImageRecordVO agentImageRecordVO) {
        LambdaQueryWrapper<AgentImageRecordPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        String siteCode = agentImageRecordVO.getSiteCode();
        if (StringUtils.isNotBlank(siteCode)) {
            lambdaQueryWrapper.eq(AgentImageRecordPO::getSiteCode, siteCode);
        }
        if (StringUtils.isNotBlank(agentImageRecordVO.getOrderField())
                && StringUtils.isNotBlank(agentImageRecordVO.getOrderType())) {
            if (agentImageRecordVO.getOrderField().equals("createdTime") && agentImageRecordVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(AgentImageRecordPO::getCreatedTime);
            }
            if (agentImageRecordVO.getOrderField().equals("createdTime") && agentImageRecordVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(AgentImageRecordPO::getCreatedTime);
            }
            if (agentImageRecordVO.getOrderField().equals("updatedTime") && agentImageRecordVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(AgentImageRecordPO::getUpdatedTime);
            }
            if (agentImageRecordVO.getOrderField().equals("updatedTime") && agentImageRecordVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(AgentImageRecordPO::getUpdatedTime);
            }
        } else {
            lambdaQueryWrapper.orderByDesc(AgentImageRecordPO::getUpdatedTime);
        }

        //
        lambdaQueryWrapper.ge(agentImageRecordVO.getUpdateTimeBegin() != null, AgentImageRecordPO::getUpdatedTime, agentImageRecordVO.getUpdateTimeBegin());
        lambdaQueryWrapper.le(agentImageRecordVO.getUpdateTimeEnd() != null, AgentImageRecordPO::getUpdatedTime, agentImageRecordVO.getUpdateTimeEnd());
        Integer recordType = agentImageRecordVO.getRecordType();
        if (recordType != null) {
            lambdaQueryWrapper.like(AgentImageRecordPO::getRecordType, recordType);
        }

        String updateName = agentImageRecordVO.getUpdateName();
        if (StringUtils.isNotBlank(updateName)) {
            lambdaQueryWrapper.eq(AgentImageRecordPO::getUpdater, updateName);
        }


        //
        Page<AgentImageRecordPO> page = new Page<>(agentImageRecordVO.getPageNumber(), agentImageRecordVO.getPageSize());
        Page<AgentImageRecordPO> pageList = agentImageRecordRepository.selectPage(page, lambdaQueryWrapper);

        Page<AgentImageRecordBO> pageResultList = new Page<>();
        BeanUtils.copyProperties(pageList, pageResultList);
        List<AgentImageRecordBO> agentImageBOList = pageList.getRecords().stream().map(po -> {
            AgentImageRecordBO imageRecordBO = new AgentImageRecordBO();
            BeanUtils.copyProperties(po, imageRecordBO);
            String createdTimeStr = DateUtil.format(new Date(imageRecordBO.getCreatedTime()), DatePattern.NORM_DATETIME_PATTERN);
            imageRecordBO.setCreatedTimeStr(createdTimeStr);
            String updatedTimeStr = DateUtil.format(new Date(imageRecordBO.getUpdatedTime()), DatePattern.NORM_DATETIME_PATTERN);
            imageRecordBO.setUpdatedTimeStr(updatedTimeStr);
            if (imageRecordBO.getRecordType() == RecordImageEnum.Image.getType()) {
                String beforeText = minioFileService.getFileUrlByKey(imageRecordBO.getBeforeText());
                imageRecordBO.setBeforeText(beforeText);
                String afterText = minioFileService.getFileUrlByKey(imageRecordBO.getAfterText());
                imageRecordBO.setAfterText(afterText);
            }
            return imageRecordBO;
        }).collect(Collectors.toList());
        pageResultList.setRecords(agentImageBOList);

        return ResponseVO.success(pageResultList);
    }


}
