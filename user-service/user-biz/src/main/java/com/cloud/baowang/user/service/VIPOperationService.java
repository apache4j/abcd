package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.vo.vip.VIPOperationRequestVO;
import com.cloud.baowang.user.api.vo.vip.VIPOperationVO;
import com.cloud.baowang.user.enums.ChangeOperationEnum;
import com.cloud.baowang.user.po.SiteVIPOperationPO;
import com.cloud.baowang.user.repositories.VIPOperationRepository;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author 小智
 * @Date 6/5/23 1:47 PM
 * @Version 1.0
 */
@Service
@Slf4j
public class VIPOperationService extends ServiceImpl<VIPOperationRepository, SiteVIPOperationPO> {

    @Autowired
    private VIPOperationRepository vipOperationRepository;

    @Autowired
    private SystemParamApi systemParamApi;


    public ResponseVO<Page<VIPOperationVO>> queryVIPOperation(final VIPOperationRequestVO requestVO) {
        Map<String, String> paraMap = systemParamApi.getSystemParamMap(CommonConstant.VIP_OPERATION_TYPE).getData();
        try {
            Page<SiteVIPOperationPO> page = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
            Page<VIPOperationVO> resultPage = vipOperationRepository.selectVIPOperationPage(page, requestVO);
            resultPage.getRecords().forEach(obj -> {
                obj.setOperationTypeName(paraMap.get(obj.getOperationType()));
                obj.setOperationName(ChangeOperationEnum.of(obj.getOperation()));
            });
            return ResponseVO.success(resultPage);
        } catch (Exception e) {
            log.error("查询操作配置记录异常", e);
            return ResponseVO.fail(ResultCode.VIP_OPERATION_QUERY_ERROR);
        }
    }

    public ResponseVO<Map<String, List<CodeValueVO>>> queryVipOperationSelect() {
        List<CodeValueVO> list = systemParamApi.getSystemParamByType(CommonConstant.VIP_OPERATION_TYPE).getData();
        Map<String, List<CodeValueVO>> map = Maps.newHashMap();
        map.put(CommonConstant.VIP_OPERATION_TYPE, list);
        map.put(ChangeOperationEnum.VIP_GRADE.getCode(), ChangeOperationEnum.VIP_GRADE.getList());
        map.put(ChangeOperationEnum.VIP_BENEFIT.getCode(), ChangeOperationEnum.VIP_BENEFIT.getList());
        return ResponseVO.success(map);
    }
}
