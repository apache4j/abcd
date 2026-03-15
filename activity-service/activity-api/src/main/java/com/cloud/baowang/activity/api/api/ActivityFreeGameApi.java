package com.cloud.baowang.activity.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.ApiConstants;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.free.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author wade
 * @Date 2024-09-09
 * 转盘活动
 */
@FeignClient(contextId = "ActivityFreeGameApi", value = ApiConstants.NAME)
@Tag(name = "活动配置-接口")
public interface ActivityFreeGameApi {



    String PREFIX = ApiConstants.PREFIX + "/" + ApiConstants.PATH + "/ActivityFreeGameApi/api/";



    @PostMapping(PREFIX + "updateFreeGameCount")
    @Operation(summary = "免费旋转更新旋转次数")
    ResponseVO<Boolean> updateFreeGameCount(@RequestBody FreeGameRecordReqVO freeGameRecordVO);


    @PostMapping(PREFIX + "getFreeGameCount")
    @Operation(summary = "获取免费旋转旋转次数")
    ResponseVO<Integer> getFreeGameCount(@RequestParam("siteCode") String siteCode,
                                         @RequestParam("userId") String userId,
                                         @RequestParam("venueCode") String venueCode);


    @Operation(summary = "旋转次数获取/使用记录-")
    @PostMapping(PREFIX + "freeGamePageList")
    ResponseVO<Page<ActivityFreeGameRespVO>> freeGamePageList(@RequestBody FreeGameReqVO vo);

    @Operation(summary = "添加旋转次数")
    @PostMapping(PREFIX + "addFreeGameConfig")
    ResponseVO<?> addFreeGameConfig(@RequestBody FreeGameSubmitModifyVO vo);

    @Operation(summary = "旋转次数获取/使用记录")
    @PostMapping(PREFIX + "getTotalCount")
    ResponseVO<Long> getTotalCount(@RequestBody FreeGameReqVO vo);


    @Operation(summary = "获取用户旋转次数")
    @PostMapping(PREFIX + "getFreeGameCountAll")
    ResponseVO<SiteActivityFreeGameRecordConfigVO> getFreeGameCountAll(@RequestParam("userId") String userId);


    @Operation(summary = "旋转次数消费记录")
    @PostMapping(PREFIX + "getFreeGameConsumePageList")
    ResponseVO<Page<SiteActivityFreeGameConsumeResp>> getFreeGameConsumePageList(@RequestBody FreeGameConsumerReqVO vo);

    @Operation(summary = "xxl-job-免费旋转过期")
    @PostMapping(PREFIX + "freeGameExpire")
    ResponseVO<Boolean> freeGameExpire();

}
