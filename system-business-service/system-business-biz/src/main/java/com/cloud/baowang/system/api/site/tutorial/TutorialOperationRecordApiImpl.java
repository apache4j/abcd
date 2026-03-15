package com.cloud.baowang.system.api.site.tutorial;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.tutorial.TutorialOperationRecordApi;
import com.cloud.baowang.system.api.vo.site.tutorial.operation.TutorialOperationRecordResVO;
import com.cloud.baowang.system.api.vo.site.tutorial.operation.TutorialOperationRecordRspVO;
import com.cloud.baowang.system.service.tutorial.TutorialOperationRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class TutorialOperationRecordApiImpl implements TutorialOperationRecordApi {
    private TutorialOperationRecordService tutorialOperationRecordService;

    @Override
    public ResponseVO<Page<TutorialOperationRecordRspVO>> listPage(TutorialOperationRecordResVO resVO) {
        return ResponseVO.success(tutorialOperationRecordService.listPage(resVO));
    }


}
