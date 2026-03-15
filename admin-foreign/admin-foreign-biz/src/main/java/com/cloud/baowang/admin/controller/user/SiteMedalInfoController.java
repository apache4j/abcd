package com.cloud.baowang.admin.controller.user;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.medal.SiteMedalInfoApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 17:25
 * @Version: V1.0
 **/
@RestController
@Tag(name = "勋章信息")
@RequestMapping("/siteMedalInfo/api/")
@AllArgsConstructor
public class SiteMedalInfoController {

    private final SiteMedalInfoApi siteMedalInfoApi;


    @PostMapping("init/{siteCode}")
    @Operation(summary = "勋章信息初始化")
    ResponseVO<Boolean> selectPage(@PathVariable("siteCode")String siteCode){
        return siteMedalInfoApi.init(siteCode);
    }





}
