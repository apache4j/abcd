package com.cloud.baowang.system.api.site.tutorial;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.tutorial.TutorialCategoryApi;
import com.cloud.baowang.system.api.vo.site.tutorial.*;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryAddVO;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryQueryVO;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryRspVO;
import com.cloud.baowang.system.service.tutorial.TutorialCategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class TutorialCategoryApiImpl implements TutorialCategoryApi {
    private final TutorialCategoryService tutorialCategoryService;
    @Override
    public ResponseVO<Page<TutorialCategoryRspVO>> listPage(TutorialCategoryQueryVO vo) {
        return ResponseVO.success(tutorialCategoryService.listPage(vo));
    }

    @Override
    public ResponseVO<Boolean> add(TutorialCategoryAddVO vo) {
        return ResponseVO.success(tutorialCategoryService.add(vo));
    }

    @Override
    public ResponseVO<Boolean> edit(TutorialCategoryAddVO vo) {
        return ResponseVO.success(tutorialCategoryService.edit(vo));
    }

    @Override
    public ResponseVO<Boolean> del(String id) {
        return ResponseVO.success(tutorialCategoryService.del(id));
    }

    @Override
    public ResponseVO<Boolean> enableAndDisAble(TutorialCategoryRspVO vo) {
        return ResponseVO.success(tutorialCategoryService.enableAndDisAble(vo));
    }

    @Override
    public List<CodeValueVO> getCategoryDownBox(TutorialDownBoxResVo resVo) {
        return tutorialCategoryService.getCategoryDownBox(resVo);
    }

    @Override
    public ResponseVO<Boolean> sort(List<TutorialCategoryRspVO> sourceList, String siteCode) {
        return tutorialCategoryService.sort(sourceList,siteCode);
    }

    @Override
    public List<TutorialClientShowVO> getCategoryList() {
        return tutorialCategoryService.getCategoryList();
    }
}
