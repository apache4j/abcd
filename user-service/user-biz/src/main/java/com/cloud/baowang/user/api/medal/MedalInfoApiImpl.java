package com.cloud.baowang.user.api.medal;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.medal.MedalInfoApi;
import com.cloud.baowang.user.api.vo.medal.MedalInfoRespVO;
import com.cloud.baowang.user.service.MedalInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Desciption: 勋章api
 * @Author: Ford
 * @Date: 2024/8/2 10:45
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class MedalInfoApiImpl implements MedalInfoApi {

    private final MedalInfoService medalInfoService;

    @Override
    public ResponseVO<List<MedalInfoRespVO>> listAll() {
        return medalInfoService.listAll();
    }
}
