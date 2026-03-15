package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.vo.vip.VIPChangeRequestVO;
import com.cloud.baowang.user.api.vo.vip.VIPChangeVO;
import com.cloud.baowang.user.po.VIPChangePO;
import com.cloud.baowang.user.repositories.VIPChangeRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author 小智
 * @Date 8/5/23 5:21 PM
 * @Version 1.0
 */
@Service
@Slf4j
public class VIPChangeService extends ServiceImpl<VIPChangeRepository, VIPChangePO> {

    @Autowired
    private VIPChangeRepository vipChangeRepository;

    @Autowired
    private SystemParamApi systemParamApi;

//    private final UserInfoResource userInfoResource;
//
//    private final RiskControlTypeService riskControlTypeService;


    public ResponseVO<Page<VIPChangeVO>> queryVIPOperation(final VIPChangeRequestVO requestVO) {
        try {
            Map<String, String> accountTypeMap = systemParamApi.getSystemParamMap(CommonConstant.USER_ACCOUNT_TYPE).getData();
            Map<String, String> accountStatusMap = systemParamApi.getSystemParamMap(CommonConstant.USER_ACCOUNT_STATUS).getData();
            Map<String, String> vipChangeMap = systemParamApi.getSystemParamMap(CommonConstant.VIP_CHANGE_TYPE).getData();
            Page<VIPChangePO> page = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
            Page<VIPChangeVO> resultPage = vipChangeRepository.selectVIPChangePage(page, requestVO);
            // 获取所有未删除的会员标签
//            ResponseVO<List<GetAllUserLabelVO>> responseVO = userInfoResource.getAllUserLabel();
//            if(responseVO.getCode() != ResultCode.SUCCESS.getCode()){
//                return ResponseVO.fail(ResultCode.VIP_CHANGE_QUERY_ERROR);
//            }
//            List<GetAllUserLabelVO> labelList = responseVO.getData();
//            Map<Long, String> labelMap = labelList.stream().collect(Collectors
//                    .toMap(GetAllUserLabelVO::getId, GetAllUserLabelVO::getLabelName));
            resultPage.getRecords().forEach(obj -> {
                obj.setAccountTypeText(accountTypeMap.get(String.valueOf(obj.getAccountType())));
                List<CodeValueVO> list = Lists.newArrayList();
                for (String str : obj.getAccountStatus().split(",")) {
                    list.add(CodeValueVO.builder().code(str).value(accountStatusMap.get(str)).build());
                }
                // 会员标签
                if (null != obj.getUserLabel()) {
//                    obj.setUserLabelName(labelMap.get(obj.getUserLabel()));
                }
                // 风控层级
                if (null != obj.getControlRank()){
//                    obj.setControlRankName(riskControlTypeService
//                            .getById(obj.getControlRank()).getRiskControlLevel());
                }
                obj.setAccountStatusName(list);
                obj.setChangeTypeText(vipChangeMap.get(String.valueOf(obj.getChangeType())));
            });
            return ResponseVO.success(resultPage);
        } catch (Exception e) {
            log.error("查询VIP变更记录异常", e);
            return ResponseVO.fail(ResultCode.VIP_CHANGE_QUERY_ERROR);
        }
    }

    public ResponseVO<Map<String, List<CodeValueVO>>> queryVipChangeSelect() {
        Map<String, List<CodeValueVO>> map = Maps.newHashMap();
        List<CodeValueVO> list = systemParamApi.getSystemParamByType(CommonConstant.VIP_CHANGE_TYPE).getData();
        map.put(CommonConstant.VIP_CHANGE_TYPE, list);
        return ResponseVO.success(map);
    }

//    public void insertChangeInfo(final List<VIPChangeVO> changeVOList) {
//        try {
//            List<VIPChangePO> list = Lists.newArrayList();
//            for(VIPChangeVO vo : changeVOList){
//                VIPChangePO po = new VIPChangePO();
//                po.setUserAccount(vo.getUserAccount());
//                po.setAccountType(vo.getAccountType());
//                po.setAccountStatus(vo.getAccountStatus());
//                po.setControlRank(vo.getControlRank());
//                po.setSiteCode(vo.getSiteCode());
//                po.setUserLabel(vo.getUserLabel());
//                po.setOperator(vo.getOperator());
//                po.setChangeType(vo.getChangeType());
//                po.setChangeTime(System.currentTimeMillis());
//                po.setCreatedTime(System.currentTimeMillis());
//                po.setUpdatedTime(System.currentTimeMillis());
//                po.setBeforeChange(vo.getChangeBefore());
//                po.setAfterChange(vo.getChangeAfter());
//                list.add(po);
//            }
//            this.saveBatch(list);
//        } catch (Exception e) {
//            log.error("落VIP变更记录发生异常", e);
//        }
//    }

}
