package com.cloud.baowang.user.api.vip;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPVenueExeVO;
import com.cloud.baowang.user.api.vo.vip.VIPGradeUpdateVO;
import com.cloud.baowang.user.api.vo.vip.VIPGradeVO;
import com.cloud.baowang.user.po.SiteVIPGradePO;
import com.cloud.baowang.user.po.SiteVIPVenueExePO;
import com.cloud.baowang.user.service.SiteVIPGradeService;
import com.cloud.baowang.user.service.SiteVIPVenueExeService;
import com.cloud.baowang.user.service.UserVipFlowRecordCnService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author : 小智
 * @Date : 2024/8/7 11:32
 * @Version : 1.0
 */
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class VipGradeApiImpl implements VipGradeApi {

    private SiteVIPGradeService siteVIPGradeService;

    private SiteVIPVenueExeService siteVIPVenueExeService;
    private UserVipFlowRecordCnService userVipFlowRecordCnService;

    @Override
    public List<CodeValueNoI18VO> getVipGrade() {
        return siteVIPGradeService.getVipGrade();
    }

    @Override
    public ResponseVO<?> updateVIPGrade(VIPGradeUpdateVO vipGradeUpdateVO) {
        return siteVIPGradeService.updateVIPGrade(vipGradeUpdateVO);
    }

    @Override
    public ResponseVO<Page<SiteVIPGradeVO>> queryVIPGradePage(PageVO pageVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        return siteVIPGradeService.queryVIPGradePage(siteCode, pageVO);
    }

    @Override
    public SiteVIPGradeVO queryVIPGradeByGrade(String vipRankCode) {
        String siteCode = CurrReqUtils.getSiteCode();
        return siteVIPGradeService.queryVIPGradeByGrade(vipRankCode, siteCode);
    }

    @Override
    public List<SiteVIPGradeVO> queryAllVIPGrade(String siteCode) {
        return siteVIPGradeService.queryAllVIPGrade(siteCode);
    }

    @Override
    public List<SiteVIPGradeVO> queryAllVIPGradeBySiteCode(String siteCode) {
        return siteVIPGradeService.queryAllVIPGrade(siteCode);
    }
    public Map<Integer,String> queryAllVIPGradeNameMap(String siteCode) {
        return siteVIPGradeService.queryAllVIPGradeNameMap(siteCode);
    }

    @Override
    public List<SiteVIPVenueExeVO> getVenueExperience() {
        String siteCode = CurrReqUtils.getSiteCode();
        List<SiteVIPVenueExeVO> result = Lists.newArrayList();
        List<SiteVIPVenueExePO> list = siteVIPVenueExeService.lambdaQuery()
                .eq(SiteVIPVenueExePO::getSiteCode, siteCode).list();
        list.forEach(obj -> {
            SiteVIPVenueExeVO vo = new SiteVIPVenueExeVO();
            BeanUtils.copyProperties(obj, vo);
            result.add(vo);
        });
        return result;
    }

    @Override
    public List<SiteVIPGradeVO> getSiteVipGradeList(String siteCode, String vipRankCode) {
        return siteVIPGradeService.getSiteVipGradeList(siteCode, vipRankCode);
    }

    @Override
    public List<SiteVIPGradeVO> getSiteVipGradeListByCodes(String siteCode, List<Integer> vipGradeCodes) {
        LambdaQueryWrapper<SiteVIPGradePO> query = Wrappers.lambdaQuery();
        query.eq(SiteVIPGradePO::getSiteCode, siteCode).in(SiteVIPGradePO::getVipGradeCode, vipGradeCodes);
        return BeanUtil.copyToList(siteVIPGradeService.list(query), SiteVIPGradeVO.class);
    }

    @Override
    public SiteVIPGradeVO getSiteVipGradeByCodeAndSiteCode(String siteCode, Integer vipGradeCode) {
        LambdaQueryWrapper<SiteVIPGradePO> query = Wrappers.lambdaQuery();
        query.eq(SiteVIPGradePO::getSiteCode, siteCode).in(SiteVIPGradePO::getVipGradeCode, vipGradeCode);
        SiteVIPGradePO one = siteVIPGradeService.getOne(query);
        return BeanUtil.copyProperties(one, SiteVIPGradeVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addVenueExperience(String siteCode, List<SiteVIPVenueExeVO> vos) {
        List<Integer> typeList = vos.stream()
                .map(SiteVIPVenueExeVO::getVenueType)
                .toList();
        LambdaQueryWrapper<SiteVIPVenueExePO> exQuery = Wrappers.lambdaQuery();
        exQuery.eq(SiteVIPVenueExePO::getSiteCode, siteCode);
        siteVIPVenueExeService.remove(exQuery);
        List<SiteVIPVenueExePO> siteVIPVenueExePOS = BeanUtil.copyToList(vos, SiteVIPVenueExePO.class);
        siteVIPVenueExePOS.forEach(item->item.setSiteCode(siteCode));
        siteVIPVenueExeService.saveOrUpdateBatch(siteVIPVenueExePOS);
        return true;
    }

    @Override
    public List<VIPGradeVO> getSystemVipGradeList() {
        return siteVIPGradeService.getSystemVipGradeList();
    }

    @Override
    public SiteVIPGradeVO getFirstSiteVipGrade(String siteCode) {
        return siteVIPGradeService.getFirstSiteVipGrade(siteCode);
    }

    @Override
    public SiteVIPGradeVO getLastSiteVipGrade(String siteCode) {
        return siteVIPGradeService.getLastSiteVipGrade(siteCode);
    }

    @Override
    public Map<String, List<SiteVIPGradeVO>> getAllSiteVipGrade() {
        return siteVIPGradeService.getAllSiteVipGrade();
    }

    @Override
    public List<CodeValueNoI18VO> getVipGradeTopTen() {
        return siteVIPGradeService.getVipGradeTopTen();
    }

}
