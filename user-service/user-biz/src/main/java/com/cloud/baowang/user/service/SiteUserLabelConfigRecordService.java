package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigRecordPageReqVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigRecordPageResVO;
import com.cloud.baowang.user.po.SiteUserLabelConfigRecordPO;
import com.cloud.baowang.user.repositories.SiteUserLabelConfigRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class SiteUserLabelConfigRecordService extends ServiceImpl<SiteUserLabelConfigRecordRepository, SiteUserLabelConfigRecordPO> {

    private final SiteUserLabelConfigRecordRepository siteUserLabelConfigRecordRepository;
    private final SystemParamApi systemParamApi;

    public Page<UserLabelConfigRecordPageResVO> getLabelConfigRecordPage(UserLabelConfigRecordPageReqVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        Page<UserLabelConfigRecordPageResVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<UserLabelConfigRecordPageResVO> pageResult = siteUserLabelConfigRecordRepository.getLabelConfigRecordPage(page, vo, siteCode);
        Map<String, String> userLabelChangeTypeMap = systemParamApi.getSystemParamMap(CommonConstant.USER_LABEL_CHANGE_TYPE).getData();
        for (UserLabelConfigRecordPageResVO record : pageResult.getRecords()) {
            if (null != record.getChangeType()) {
                record.setChangeTypeName(userLabelChangeTypeMap.get(record.getChangeType()));
            }
        }
        return pageResult;
    }

    public ResponseVO<Long> getTotalCount(UserLabelConfigRecordPageReqVO reqVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        Long total = siteUserLabelConfigRecordRepository.getLabelConfigRecordTotal(reqVO, siteCode);
        return ResponseVO.success(total);
    }
}
