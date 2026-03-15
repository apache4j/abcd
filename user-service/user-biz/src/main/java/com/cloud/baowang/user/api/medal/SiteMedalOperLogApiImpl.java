package com.cloud.baowang.user.api.medal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.medal.SiteMedalOperLogApi;
import com.cloud.baowang.user.api.vo.medal.SiteMedalOperLogReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalOperLogRespVO;
import com.cloud.baowang.user.enums.MedalOperationEnum;
import com.cloud.baowang.user.service.SiteMedalOperLogService;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/8/20 11:01
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SiteMedalOperLogApiImpl implements SiteMedalOperLogApi {
    @Resource
    private SiteMedalOperLogService siteMedalOperLogService;

    @Override
    public ResponseVO<Page<SiteMedalOperLogRespVO>> listPage(SiteMedalOperLogReqVO siteMedalOperLogReqVO) {
        return siteMedalOperLogService.listPage(siteMedalOperLogReqVO);
    }

    @Override
    public ResponseVO<List<CodeValueVO>> getMedalOperationEnums() {
        List<CodeValueVO> codeValueVOS= Lists.newArrayList();
        for(MedalOperationEnum medalOperationEnum:MedalOperationEnum.values()){
            CodeValueVO codeValueVO=new CodeValueVO();
            codeValueVO.setCode(medalOperationEnum.getFieldCode());
            codeValueVO.setValue(medalOperationEnum.getDesc());
            codeValueVOS.add(codeValueVO);
        }
        return ResponseVO.success(codeValueVOS);
    }


}
