package com.cloud.baowang.system.api.site.tutorial;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.tutorial.TutorialClassApi;
import com.cloud.baowang.system.api.vo.site.tutorial.*;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryRspVO;
import com.cloud.baowang.system.api.vo.site.tutorial.classif.TutorialClassAddVO;
import com.cloud.baowang.system.api.vo.site.tutorial.classif.TutorialClassResVO;
import com.cloud.baowang.system.api.vo.site.tutorial.classif.TutorialClassRspVO;
import com.cloud.baowang.system.service.tutorial.TutorialClassService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class TutorialClassApiImpl implements TutorialClassApi {
    private TutorialClassService tutorialClassService;
    @Override
    public ResponseVO<Page<TutorialClassRspVO>> listPage(TutorialClassResVO vo) {
        return ResponseVO.success(tutorialClassService.listPage(vo));
    }

    @Override
    public ResponseVO<Boolean> add(TutorialClassAddVO vo) {
        return ResponseVO.success(tutorialClassService.add(vo));
    }

    @Override
    public ResponseVO<Boolean> edit(TutorialClassAddVO vo) {
        return ResponseVO.success(tutorialClassService.edit(vo));
    }

    @Override
    public ResponseVO<Boolean> del(String id) {
        return ResponseVO.success(tutorialClassService.del(id));
    }

    @Override
    public ResponseVO<Boolean> enableAndDisAble(TutorialClassRspVO vo) {
        return ResponseVO.success(tutorialClassService.enableAndDisAble(vo));
    }

    @Override
    public List<CodeValueVO> getClassDownBox(TutorialDownBoxResVo resVo) {
        return tutorialClassService.getClassDownBox(resVo);
    }

    @Override
    public ResponseVO<Boolean> sort(List<TutorialCategoryRspVO> sourceList, String siteCode) {
        return tutorialClassService.sort(sourceList, siteCode);
    }

    @Override
    public List<TutorialClientShowVO> getClassList(String id) {
        return tutorialClassService.getClassList(id);
    }
}
