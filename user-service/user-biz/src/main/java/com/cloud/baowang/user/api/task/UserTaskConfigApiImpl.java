/*
package com.cloud.baowang.user.api.task;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.task.UserTaskConfigApi;
import com.cloud.baowang.user.api.vo.task.UserTaskConfigDelReqVO;
import com.cloud.baowang.user.api.vo.task.UserTaskConfigReqVO;
import com.cloud.baowang.user.api.vo.task.UserTaskConfigResVO;
import com.cloud.baowang.user.api.vo.task.UserTaskConfigSaveReqVO;
import com.cloud.baowang.user.service.UserTaskConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserTaskConfigApiImpl implements UserTaskConfigApi {
     private final UserTaskConfigService userTaskConfigService;

    @Override
    public ResponseVO<Page<UserTaskConfigResVO>> listPage(UserTaskConfigReqVO vo) {
        return userTaskConfigService.listPage(vo);
    }

    @Override
    public ResponseVO<Void> save(UserTaskConfigSaveReqVO vo) {
        return userTaskConfigService.save(vo);
    }

    @Override
    public ResponseVO<Void> edit(UserTaskConfigSaveReqVO vo) {
        return userTaskConfigService.edit(vo);
    }

    @Override
    public ResponseVO<Void> delete(UserTaskConfigDelReqVO vo) {
        return userTaskConfigService.delete(vo);
    }
}
*/
