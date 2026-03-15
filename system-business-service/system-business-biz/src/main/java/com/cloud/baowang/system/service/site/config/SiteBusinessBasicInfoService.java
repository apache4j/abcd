package com.cloud.baowang.system.service.site.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.i18n.dto.I18NMessageDTO;
import com.cloud.baowang.system.api.file.MinioFileService;
import com.cloud.baowang.system.api.vo.site.agreement.BusinessBasicClientVO;
import com.cloud.baowang.system.api.vo.site.agreement.BusinessBasicVO;
import com.cloud.baowang.system.po.site.config.SiteBusinessBasicInfoPO;
import com.cloud.baowang.system.repositories.site.agreement.SiteBusinessBasicInfoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class SiteBusinessBasicInfoService extends ServiceImpl<SiteBusinessBasicInfoRepository, SiteBusinessBasicInfoPO> {
    private final I18nApi i18nApi;

    private final MinioFileService fileService;

    private final HelpCenterManageService basicConfigService;


    public boolean addBusinessChampionInfo(BusinessBasicVO reqVO) {

        List<SiteBusinessBasicInfoPO> BusinessBasicInfoPOS = new ArrayList<>();

        SiteBusinessBasicInfoPO po = new SiteBusinessBasicInfoPO();
        BeanUtils.copyProperties(reqVO, po);
        String businessNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.BUSINESS_BASIC_INFO.getCode());
        i18nApi.insert(Map.of(businessNameI18, reqVO.getI18nMessages()));
        po.setBusinessName(businessNameI18);
        po.setUpdater(CurrReqUtils.getAccount());
        po.setUpdatedTime(System.currentTimeMillis());
        BusinessBasicInfoPOS.add(po);
        basicConfigService.updateBasicInfo(reqVO.getSiteCode(), 13);
        return this.save(po);
    }


    public List<BusinessBasicVO> getBusinessChampionInfo(String siteCode) {
        LambdaQueryWrapper<SiteBusinessBasicInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteBusinessBasicInfoPO::getSiteCode, siteCode);
        queryWrapper.orderByAsc(SiteBusinessBasicInfoPO::getSort);
        List<SiteBusinessBasicInfoPO> businessBasicInfoPOS = this.baseMapper.selectList(queryWrapper);
        List<BusinessBasicVO> result = new ArrayList<>();
        String prefix = fileService.getMinioDomain();
        for (SiteBusinessBasicInfoPO businessBasicInfoPO : businessBasicInfoPOS) {
            List<I18NMessageDTO> nameI18n = i18nApi.getMessageByKey(businessBasicInfoPO.getBusinessName()).getData();
            List<I18nMsgFrontVO> nameI18nVO = Lists.newArrayList();
            try {
                nameI18nVO = ConvertUtil.convertListToList(nameI18n, new I18nMsgFrontVO());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                continue;
            }
            BusinessBasicVO businessBasicVO = new BusinessBasicVO();
            BeanUtils.copyProperties(businessBasicInfoPO, businessBasicVO);
            businessBasicVO.setI18nMessages(nameI18nVO);
            if (StringUtils.isNotEmpty(businessBasicInfoPO.getH5Icon())) {
                businessBasicVO.setH5IconFullUrl(prefix + "/" + businessBasicInfoPO.getH5Icon());
            }
            if (StringUtils.isNotEmpty(businessBasicInfoPO.getPcIcon())) {
                businessBasicVO.setPcIconFullUrl(prefix + "/" + businessBasicInfoPO.getPcIcon());
            }
            businessBasicVO.setId(String.valueOf(businessBasicInfoPO.getId()));
            result.add(businessBasicVO);
        }
        return result;
    }

    public Boolean updateBusinessBasicInfo(BusinessBasicVO reqVO) {
        LambdaQueryWrapper<SiteBusinessBasicInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteBusinessBasicInfoPO::getSiteCode, reqVO.getSiteCode());
        queryWrapper.eq(SiteBusinessBasicInfoPO::getId, reqVO.getId());
        SiteBusinessBasicInfoPO basicInfoPO = this.baseMapper.selectOne(queryWrapper);
        if (basicInfoPO == null) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        SiteBusinessBasicInfoPO po = new SiteBusinessBasicInfoPO();
        BeanUtils.copyProperties(reqVO, po);
        String businessNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.BUSINESS_BASIC_INFO.getCode());
        i18nApi.insert(Map.of(businessNameI18, reqVO.getI18nMessages()));
        po.setBusinessName(businessNameI18);
        po.setUpdatedTime(System.currentTimeMillis());
        po.setId(basicInfoPO.getId());
        this.updateById(po);
        basicConfigService.updateBasicInfo(reqVO.getSiteCode(), 13);
        return Boolean.TRUE;
    }

    /**
     * 客户端获取
     *
     * @param siteCode
     * @return
     */
    public List<BusinessBasicClientVO> getBusinessBasicInfoClient(String siteCode) {
        LambdaQueryWrapper<SiteBusinessBasicInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteBusinessBasicInfoPO::getSiteCode, siteCode);
        queryWrapper.orderByAsc(SiteBusinessBasicInfoPO::getSort);
        List<SiteBusinessBasicInfoPO> businessBasicInfoPOS = this.baseMapper.selectList(queryWrapper);
        List<BusinessBasicClientVO> result = new ArrayList<>();
        String prefix = fileService.getMinioDomain();
        for (SiteBusinessBasicInfoPO businessBasicInfoPO : businessBasicInfoPOS) {
            BusinessBasicClientVO businessBasicVO = new BusinessBasicClientVO();
            BeanUtils.copyProperties(businessBasicInfoPO, businessBasicVO);
            if (StringUtils.isNotEmpty(businessBasicInfoPO.getH5Icon())) {
                businessBasicVO.setH5IconFullUrl(prefix + "/" + businessBasicInfoPO.getH5Icon());
            }
            if (StringUtils.isNotEmpty(businessBasicInfoPO.getPcIcon())) {
                businessBasicVO.setPcIconFullUrl(prefix + "/" + businessBasicInfoPO.getPcIcon());
            }
            businessBasicVO.setId(String.valueOf(businessBasicInfoPO.getId()));
            result.add(businessBasicVO);
        }
        return result;
    }

    public Boolean delBusinessBasicInfo(BusinessBasicVO reqVO) {
        LambdaQueryWrapper<SiteBusinessBasicInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteBusinessBasicInfoPO::getSiteCode, reqVO.getSiteCode());
        queryWrapper.eq(SiteBusinessBasicInfoPO::getId, reqVO.getId());
        SiteBusinessBasicInfoPO basicInfoPO = this.baseMapper.selectOne(queryWrapper);
        if (basicInfoPO == null) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        this.baseMapper.deleteById(basicInfoPO.getId());
        basicConfigService.updateBasicInfo(reqVO.getSiteCode(), 13);
        return Boolean.TRUE;
    }

    public Boolean sort(List<BusinessBasicVO> reqVO) {
        for (BusinessBasicVO vo : reqVO) {
            LambdaUpdateWrapper<SiteBusinessBasicInfoPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SiteBusinessBasicInfoPO::getId, vo.getId())
                    .set(SiteBusinessBasicInfoPO::getSort, vo.getSort());
            this.update(updateWrapper);
        }
        return true;
    }

}


