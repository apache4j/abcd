package com.cloud.baowang.system.api.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigPageQueryVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigReqVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.service.dict.SystemDictConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SystemDictConfigApiImpl implements SystemDictConfigApi {
    private final SystemDictConfigService configService;

    @Override
    public ResponseVO<Page<SystemDictConfigRespVO>> pageQuery(SystemDictConfigPageQueryVO queryVO) {
        return configService.pageQuery(queryVO);
    }

    @Override
    public ResponseVO<Boolean> upd(SystemDictConfigReqVO reqVO) {
        return configService.upd(reqVO);
    }

    @Override
    public ResponseVO<SystemDictConfigRespVO> getByCode(Integer dictCode, String siteCode) {
        return configService.getByCode(dictCode, siteCode);
    }

    @Override
    public ResponseVO<List<SystemDictConfigRespVO>> getByCodes(List<Integer> dictCodes, String siteCode) {
        return configService.getByCodes(dictCodes,siteCode);
    }

    @Override
    public ResponseVO<List<SystemDictConfigRespVO>> getListByCode(Integer dictCode) {
        return configService.getListByCode(dictCode);
    }

    @Override
    public void initSiteDictConfig(String siteCode,String operator) {
        configService.initSiteDictConfig(siteCode,operator);
    }

    @Override
    public ResponseVO<SystemDictConfigRespVO> queryWithdrawSwitch(String siteCode, Integer code) {
        return configService.queryWithdrawSwitch(siteCode,code);
    }
}
