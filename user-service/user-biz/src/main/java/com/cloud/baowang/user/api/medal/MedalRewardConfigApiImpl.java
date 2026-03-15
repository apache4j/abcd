package com.cloud.baowang.user.api.medal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.medal.MedalRewardConfigApi;
import com.cloud.baowang.user.api.vo.medal.MedalRewardConfigBatchUpdateReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardConfigReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardConfigRespVO;
import com.cloud.baowang.user.service.MedalRewardConfigService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/29 16:23
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class MedalRewardConfigApiImpl implements MedalRewardConfigApi {
    @Resource
    private MedalRewardConfigService medalRewardConfigService;


    @Override
    public ResponseVO<Page<MedalRewardConfigRespVO>> listPage(MedalRewardConfigReqVO medalRewardConfigReqVO) {
        return medalRewardConfigService.listPage(medalRewardConfigReqVO);
    }

    @Override
    public ResponseVO<Boolean> init(String siteCode) {
        return medalRewardConfigService.init(siteCode);
    }

    @Override
    public ResponseVO<Void> batchSave(MedalRewardConfigBatchUpdateReqVO medalRewardConfigBatchUpdateReqVO) {
        return medalRewardConfigService.batchSave(medalRewardConfigBatchUpdateReqVO);
    }
}
