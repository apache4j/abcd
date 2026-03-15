package com.cloud.baowang.system.api.site.tutorial;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.tutorial.TutorialTabsApi;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialClientShowVO;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialDownBoxResVo;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryRspVO;
import com.cloud.baowang.system.api.vo.site.tutorial.tabs.TutorialTabsAddVO;
import com.cloud.baowang.system.api.vo.site.tutorial.tabs.TutorialTabsResVO;
import com.cloud.baowang.system.api.vo.site.tutorial.tabs.TutorialTabsRspVO;
import com.cloud.baowang.system.service.tutorial.TutorialTabsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class TutorialTabsApiImpl implements TutorialTabsApi {

    private final TutorialTabsService tutorialTabsService;

    @Override
    public ResponseVO<Page<TutorialTabsRspVO>> listPage(TutorialTabsResVO vo) {
        return ResponseVO.success(tutorialTabsService.listPage(vo));
    }

    @Override
    public ResponseVO<Boolean> add(TutorialTabsAddVO vo) {
        return ResponseVO.success(tutorialTabsService.add(vo));
    }

    @Override
    public ResponseVO<Boolean> edit(TutorialTabsAddVO vo) {
        return ResponseVO.success(tutorialTabsService.edit(vo));
    }

    @Override
    public ResponseVO<Boolean> del(String id) {
        return ResponseVO.success(tutorialTabsService.del(id));
    }

    @Override
    public ResponseVO<Boolean> enableAndDisAble(TutorialTabsRspVO vo) {
        return ResponseVO.success(tutorialTabsService.enableAndDisAble(vo));
    }

    @Override
    public List<CodeValueVO> getTabsDownBox(TutorialDownBoxResVo resVo) {
        return tutorialTabsService.getTabsDownBox(resVo);
    }

    @Override
    public ResponseVO<Boolean> sort(List<TutorialCategoryRspVO> sourceList, String siteCode) {
        return tutorialTabsService.sort(sourceList, siteCode);
    }

    @Override
    public List<TutorialClientShowVO> getTabsList(TutorialDownBoxResVo resVo) {
        return tutorialTabsService.getTabsList(resVo);
    }
}
