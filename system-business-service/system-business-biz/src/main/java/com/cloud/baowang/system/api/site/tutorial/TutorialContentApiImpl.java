package com.cloud.baowang.system.api.site.tutorial;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.tutorial.TutorialContentApi;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialClientShowVO;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialDownBoxResVo;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryRspVO;
import com.cloud.baowang.system.api.vo.site.tutorial.content.TutorialContentAddVO;
import com.cloud.baowang.system.api.vo.site.tutorial.content.TutorialContentResVO;
import com.cloud.baowang.system.api.vo.site.tutorial.content.TutorialContentRspVO;
import com.cloud.baowang.system.service.tutorial.TutorialContentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class TutorialContentApiImpl implements TutorialContentApi {
    private final TutorialContentService tutorialContentService;

    @Override
    public ResponseVO<Page<TutorialContentRspVO>> listPage(TutorialContentResVO vo) {
        return ResponseVO.success(tutorialContentService.listPage(vo));
    }

    @Override
    public ResponseVO<Boolean> add(TutorialContentAddVO vo) {
        return ResponseVO.success(tutorialContentService.add(vo));
    }

    @Override
    public ResponseVO<Boolean> edit(TutorialContentAddVO vo) {
        return ResponseVO.success(tutorialContentService.edit(vo));
    }

    @Override
    public ResponseVO<Boolean> del(String id) {
        return ResponseVO.success(tutorialContentService.del(id));
    }

    @Override
    public ResponseVO<Boolean> enableAndDisAble(TutorialContentRspVO vo) {
        return ResponseVO.success(tutorialContentService.enableAndDisAble(vo));
    }

    @Override
    public List<CodeValueVO> getContentDownBox(TutorialDownBoxResVo resVo) {
        return tutorialContentService.getContentDownBox(resVo);
    }

    @Override
    public ResponseVO<Boolean> sort(List<TutorialCategoryRspVO> sourceList, String siteCode) {
        return tutorialContentService.sort(sourceList, siteCode);
    }

    @Override
    public List<TutorialClientShowVO> showContent(TutorialDownBoxResVo resVo) {
        return tutorialContentService.showContent(resVo);
    }

    @Override
    public Boolean checkDataExist() {
        return tutorialContentService.checkDataExist();
    }
}
