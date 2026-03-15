package com.cloud.baowang.system.api.language;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.vo.language.*;
import com.cloud.baowang.system.service.language.LanguageManagerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class LanguageManagerApiImpl implements LanguageManagerApi {

    private final LanguageManagerService languageManagerService;

    @Override
    public ResponseVO<List<LanguageManagerListVO>> list() {
        return languageManagerService.languageByList(CurrReqUtils.getSiteCode());
    }

    @Override
    public ResponseVO<Void> sort(List<LanguageManagerSortVO> vo) {
        return languageManagerService.sort(vo);
    }

    @Override
    public ResponseVO<List<LanguageValidListCacheVO>> validList() {
        return languageManagerService.validList();
    }

    @Override
    public ResponseVO<List<LanguageValidListCacheVO>> validListBySiteCode(String siteCode) {
        return languageManagerService.validList(siteCode);
    }

    @Override
    public ResponseVO<Page<LanguageManagerVO>> pageList(LanguageManagerPageReqVO vo) {
        return languageManagerService.pageList(vo);
    }

    @Override
    public ResponseVO<Void> edit(LanguageManagerEditVO vo) {
        return languageManagerService.edit(vo);
    }

    @Override
    public ResponseVO<LanguageManagerInfoResVO> info(LanguageManagerInfoReqVO vo) {
        return languageManagerService.info(vo);
    }

    @Override
    public ResponseVO<Void> add(LanguageManagerAddVO vo) {
        return languageManagerService.add(vo);
    }

    @Override
    public ResponseVO<List<SiteLanguageVO>> getSiteLanguageDownBox(String siteCode) {
        return languageManagerService.getSiteLanguageDownBox(siteCode);
    }

    @Override
    public ResponseVO<Void> changeStatus(LanguageManagerChangStatusReqVO vo) {
        return languageManagerService.changeStatus(vo);
    }

}
