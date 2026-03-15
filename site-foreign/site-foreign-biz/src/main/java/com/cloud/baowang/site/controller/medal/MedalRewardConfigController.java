package com.cloud.baowang.site.controller.medal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.medal.MedalRewardConfigApi;
import com.cloud.baowang.user.api.vo.medal.MedalRewardConfigBatchUpdateReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardConfigReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardConfigRespVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardConfigUpdateReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/29 16:36
 * @Version: V1.0
 **/
@RestController
@Tag(name = "会员-会员勋章管理-勋章奖励配置信息")
@RequestMapping("/medalRewardConfig/api")
@AllArgsConstructor
public class MedalRewardConfigController {

    private final MedalRewardConfigApi medalRewardConfigApi;

    @PostMapping("selectPage")
    @Operation(summary = "勋章奖励配置分页查询")
    ResponseVO<Page<MedalRewardConfigRespVO>> selectPage(@RequestBody @Validated MedalRewardConfigReqVO medalRewardConfigReqVO){
        medalRewardConfigReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return medalRewardConfigApi.listPage(medalRewardConfigReqVO);
    }


    @PostMapping("init/{siteCode}")
    @Operation(summary = "初始化")
    ResponseVO<Boolean> init(@PathVariable("siteCode")String siteCode){
        return medalRewardConfigApi.init(siteCode);
    }

    @PostMapping("batchSave")
    @Operation(summary = "批量保存")
    ResponseVO<Void> batchSave(@RequestBody MedalRewardConfigBatchUpdateReqVO medalRewardConfigBatchUpdateReqVO) {
        List<MedalRewardConfigUpdateReqVO> medalRewardVOList = medalRewardConfigBatchUpdateReqVO.getMedalRewardConfigUpdateReqVOList();
        if (medalRewardVOList.stream().anyMatch(s -> s.getUnlockMedalNum() == null || s.getUnlockMedalNum() == 0)) {
            throw new BaowangDefaultException(ResultCode.MEDAL_AMOUNT_ZERO_ERROR.getDesc());
        }
        int size = medalRewardVOList.size();
        int unLockMedalNum = medalRewardVOList.stream()
                .map(MedalRewardConfigUpdateReqVO::getUnlockMedalNum).filter(s -> s > 0).collect(Collectors.toSet()).size();
        if (unLockMedalNum != size) {
            throw new BaowangDefaultException(ResultCode.MEDAL_REPEAT_ERROR.getDesc());
        }
        if (medalRewardVOList.stream().anyMatch(s -> s.getRewardAmount() == null)) {
            throw new BaowangDefaultException(ResultCode.REWARD_NOT_NUll_ERROR.getDesc());
        }
        if (medalRewardVOList.stream().anyMatch(s -> s.getRewardAmount().compareTo(BigDecimal.ZERO) < 0 || s.getRewardAmount().stripTrailingZeros().scale() > 0)) {
            throw new BaowangDefaultException(ResultCode.INPUT_INVALID_ERROR.getDesc());
        }
        medalRewardConfigBatchUpdateReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return medalRewardConfigApi.batchSave(medalRewardConfigBatchUpdateReqVO);
    }
}
