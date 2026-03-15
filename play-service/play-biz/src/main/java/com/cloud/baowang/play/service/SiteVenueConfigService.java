package com.cloud.baowang.play.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.vo.venue.AddSiteVenueConfigVO;
import com.cloud.baowang.play.po.SiteVenueConfigPO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.repositories.SiteVenueConfigRepository;
import com.cloud.baowang.play.repositories.VenueInfoRepository;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.i18n.dto.I18NMessageDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SiteVenueConfigService extends ServiceImpl<SiteVenueConfigRepository, SiteVenueConfigPO> {

    private final VenueInfoRepository venueInfoRepository;

    private final I18nApi i18nApi;

    /**
     * 站点授权场馆的时候初始化站点场馆的配置
     */
    public void addSiteVenueConfig(List<String> venueList, String siteCode) {
        List<VenueInfoPO> venueInfoPOList = venueInfoRepository.selectList(null);
        Map<String, VenueInfoPO> venueInfoMap = venueInfoPOList.stream()
                .collect(Collectors.toMap(
                        VenueInfoPO::getVenueCode,
                        Function.identity(),
                        (first, second) -> first
                ));

        //原有的数据库中存的站点场馆配置信息
        List<SiteVenueConfigPO> siteVenueConfigPOS = baseMapper.selectList(Wrappers.lambdaQuery(SiteVenueConfigPO.class).eq(SiteVenueConfigPO::getSiteCode, siteCode));
        Map<String, String> siteDBConfigMap = siteVenueConfigPOS.stream().collect(Collectors.toMap(SiteVenueConfigPO::getVenueCode, SiteVenueConfigPO::getVenueCode));

        List<AddSiteVenueConfigVO> addSiteVenueConfigVOList = Lists.newArrayList();


        List<String> addVenueI18CodeList = Lists.newArrayList();

        for (String venueCode : venueList) {


            //判断新增的场馆在配置中原本是否存在 存在直接跳过
            if (siteDBConfigMap.containsKey(venueCode)) {
                continue;
            }


            VenueInfoPO venueInfoPO = venueInfoMap.get(venueCode);
            if (ObjectUtils.isEmpty(venueInfoPO)) {
                continue;
            }

            //重新初始化站点的场馆多语言CODE
            String venueName = null;//场馆名称
            String pcIconI18nCode = null;//PC-图片
            String h5IconI18nCode = null;//H5-图片
            String pcBackgroundCode = null;//pc_场馆背景图
            String pcLogoCode = null;//pc_场馆LOGO
            String venueDesc = null;//场馆备注

            String middleIconI18nCode = null;//皮肤4:中等图-多语言
            String htIconI18nCode = null;//游戏横版图标
            String smallIcon1I18nCode = null; //小图标1
            String smallIcon2I18nCode = null; //小图标2
            String smallIcon3I18nCode = null; //小图标3
            String smallIcon4I18nCode = null; //小图标4
            String smallIcon5I18nCode = null; //小图标5
            String smallIcon6I18nCode = null; //小图标6

            //皮肤4:中等图-多语言
            String dbMiddleIconI18nCode = venueInfoPO.getMiddleIconI18nCode();
            if (ObjectUtils.isNotEmpty(dbMiddleIconI18nCode)) {
                middleIconI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.MIDDLE_HT_ICON.getCode());
                addVenueI18CodeList.add(dbMiddleIconI18nCode);
            }


            String dbHtIconI18nCode = venueInfoPO.getHtIconI18nCode();
            if (ObjectUtils.isNotEmpty(dbHtIconI18nCode)) {
                htIconI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.HT_ICON.getCode());
                addVenueI18CodeList.add(dbHtIconI18nCode);
            }


            String dbSmallIcon1I18nCode = venueInfoPO.getSmallIcon1I18nCode();
            if (ObjectUtils.isNotEmpty(dbSmallIcon1I18nCode)) {
                smallIcon1I18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SMALL_ICON1.getCode());
                addVenueI18CodeList.add(dbSmallIcon1I18nCode);
            }

            String dbSmallIcon2I18nCode = venueInfoPO.getSmallIcon2I18nCode();
            if (ObjectUtils.isNotEmpty(dbSmallIcon2I18nCode)) {
                smallIcon2I18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SMALL_ICON2.getCode());
                addVenueI18CodeList.add(dbSmallIcon2I18nCode);
            }

            String dbSmallIcon3I18nCode = venueInfoPO.getSmallIcon3I18nCode();
            if (ObjectUtils.isNotEmpty(dbSmallIcon3I18nCode)) {
                smallIcon3I18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SMALL_ICON3.getCode());
                addVenueI18CodeList.add(dbSmallIcon3I18nCode);
            }

            String dbSmallIcon4I18nCode = venueInfoPO.getSmallIcon4I18nCode();
            if (ObjectUtils.isNotEmpty(dbSmallIcon4I18nCode)) {
                smallIcon4I18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SMALL_ICON4.getCode());
                addVenueI18CodeList.add(dbSmallIcon4I18nCode);
            }

            String dbSmallIcon5I18nCode = venueInfoPO.getSmallIcon5I18nCode();
            if (ObjectUtils.isNotEmpty(dbSmallIcon5I18nCode)) {
                smallIcon5I18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SMALL_ICON5.getCode());
                addVenueI18CodeList.add(dbSmallIcon5I18nCode);
            }

            String dbSmallIcon6I18nCode = venueInfoPO.getSmallIcon6I18nCode();
            if (ObjectUtils.isNotEmpty(dbSmallIcon6I18nCode)) {
                smallIcon6I18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SMALL_ICON6.getCode());
                addVenueI18CodeList.add(dbSmallIcon6I18nCode);
            }

            //取出场馆的多语言code统一查询
            String adminVenueName = venueInfoPO.getVenueName();
            if (ObjectUtils.isNotEmpty(adminVenueName)) {
                venueName = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.VENUE_INIT_NAME.getCode());
                addVenueI18CodeList.add(adminVenueName);
            }


            String adminPcIconI18nCode = venueInfoPO.getPcIconI18nCode();
            if (ObjectUtils.isNotEmpty(adminPcIconI18nCode)) {
                pcIconI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_PC_VENUE_ICON.getCode());
                addVenueI18CodeList.add(adminPcIconI18nCode);
            }


            String adminH5IconI18nCode = venueInfoPO.getH5IconI18nCode();
            if (ObjectUtils.isNotEmpty(adminH5IconI18nCode)) {
                h5IconI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.H5_VENUE_ICON.getCode());
                addVenueI18CodeList.add(adminH5IconI18nCode);
            }


            String adminPcBackgroundCode = venueInfoPO.getPcBackgroundCode();
            if (ObjectUtils.isNotEmpty(adminPcBackgroundCode)) {
                pcBackgroundCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.PC_BACKGROUND_ICON.getCode());
                addVenueI18CodeList.add(adminPcBackgroundCode);
            }

            String adminPcLogoCode = venueInfoPO.getPcLogoCode();
            if (ObjectUtils.isNotEmpty(adminPcLogoCode)) {
                pcLogoCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.PC_LOGO_CODE.getCode());
                addVenueI18CodeList.add(adminPcLogoCode);
            }


            String adminVenueDesc = venueInfoPO.getVenueDesc();
            if (ObjectUtils.isNotEmpty(adminVenueDesc)) {
                venueDesc = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.VENUE_DESC.getCode());
                addVenueI18CodeList.add(adminVenueDesc);
            }


            AddSiteVenueConfigVO siteVenueConfigPO = AddSiteVenueConfigVO
                    .builder()
                    .siteCode(siteCode)
                    .venueName(venueName)
                    .adminVenueName(venueInfoPO.getVenueName())
                    .venueCode(venueCode)
                    .pcIconI18nCode(pcIconI18nCode)
                    .adminPcIconI18nCode(venueInfoPO.getPcIconI18nCode())
                    .h5IconI18nCode(h5IconI18nCode)
                    .adminH5IconI18nCode(venueInfoPO.getH5IconI18nCode())
                    .pcBackgroundCode(pcBackgroundCode)
                    .adminPcBackgroundCode(venueInfoPO.getPcBackgroundCode())
                    .pcLogoCode(pcLogoCode)
                    .adminPcLogoCode(venueInfoPO.getPcLogoCode())
                    .venueDesc(venueDesc)
                    .adminVenueDesc(venueInfoPO.getVenueDesc())

                    .middleIconI18nCode(middleIconI18nCode)
                    .adminMiddleIconI18nCode(venueInfoPO.getMiddleIconI18nCode())

                    .htIconI18nCode(htIconI18nCode)
                    .adminHtIconI18nCode(venueInfoPO.getHtIconI18nCode())

                    .smallIcon1I18nCode(smallIcon1I18nCode)
                    .adminSmallIcon1I18nCode(venueInfoPO.getSmallIcon1I18nCode())

                    .smallIcon2I18nCode(smallIcon2I18nCode)
                    .adminSmallIcon2I18nCode(venueInfoPO.getSmallIcon2I18nCode())

                    .smallIcon3I18nCode(smallIcon3I18nCode)
                    .adminSmallIcon3I18nCode(venueInfoPO.getSmallIcon3I18nCode())

                    .smallIcon4I18nCode(smallIcon4I18nCode)
                    .adminSmallIcon4I18nCode(venueInfoPO.getSmallIcon4I18nCode())

                    .smallIcon5I18nCode(smallIcon5I18nCode)
                    .adminSmallIcon5I18nCode(venueInfoPO.getSmallIcon5I18nCode())

                    .smallIcon6I18nCode(smallIcon6I18nCode)
                    .adminSmallIcon6I18nCode(venueInfoPO.getSmallIcon6I18nCode())
                    .build();
//
            addSiteVenueConfigVOList.add(siteVenueConfigPO);
        }


        Map<String, List<I18NMessageDTO>> i18NMessageMap = Maps.newHashMap();
        //将场馆的配置I18拿查出来COPY一份发到站点下
        if (CollectionUtil.isNotEmpty(addVenueI18CodeList)) {
            ResponseVO<List<I18NMessageDTO>> listResponseVO = i18nApi.getMessageByKeyList(addVenueI18CodeList);
            List<I18NMessageDTO> i18NMessageList = listResponseVO.getData();
            i18NMessageMap = i18NMessageList.stream().collect(Collectors.groupingBy(I18NMessageDTO::getMessageKey));
        }

        List<SiteVenueConfigPO> addSiteVenueConfigList = Lists.newArrayList();
        for (AddSiteVenueConfigVO item : addSiteVenueConfigVOList) {

            //将多语言的场馆的配置查出来插入一份到 站点场馆配置
            List<I18NMessageDTO> adminVenueNameI18Code = i18NMessageMap.get(item.getAdminVenueName());
            addToI18nMsgFrontVO(adminVenueNameI18Code, item.getVenueName());


            List<I18NMessageDTO> adminPcIconI18nCode = i18NMessageMap.get(item.getAdminPcIconI18nCode());
            addToI18nMsgFrontVO(adminPcIconI18nCode, item.getPcIconI18nCode());


            List<I18NMessageDTO> adminH5IconI18nCode = i18NMessageMap.get(item.getAdminH5IconI18nCode());
            addToI18nMsgFrontVO(adminH5IconI18nCode, item.getH5IconI18nCode());


            List<I18NMessageDTO> adminPcBackgroundCode = i18NMessageMap.get(item.getAdminPcBackgroundCode());
            addToI18nMsgFrontVO(adminPcBackgroundCode, item.getPcBackgroundCode());


            List<I18NMessageDTO> adminPcLogoCode = i18NMessageMap.get(item.getAdminPcLogoCode());
            addToI18nMsgFrontVO(adminPcLogoCode, item.getPcLogoCode());


            List<I18NMessageDTO> adminVenueDesc = i18NMessageMap.get(item.getAdminVenueDesc());
            addToI18nMsgFrontVO(adminVenueDesc, item.getVenueDesc());


            List<I18NMessageDTO> middleIconI18nCode = i18NMessageMap.get(item.getAdminMiddleIconI18nCode());
            addToI18nMsgFrontVO(middleIconI18nCode, item.getMiddleIconI18nCode());

            List<I18NMessageDTO> htIconI18nCode = i18NMessageMap.get(item.getAdminHtIconI18nCode());
            addToI18nMsgFrontVO(htIconI18nCode, item.getHtIconI18nCode());

            List<I18NMessageDTO> smallIcon1I18nCode = i18NMessageMap.get(item.getAdminSmallIcon1I18nCode());
            addToI18nMsgFrontVO(smallIcon1I18nCode, item.getSmallIcon1I18nCode());

            List<I18NMessageDTO> smallIcon2I18nCode = i18NMessageMap.get(item.getAdminSmallIcon2I18nCode());
            addToI18nMsgFrontVO(smallIcon2I18nCode, item.getSmallIcon2I18nCode());

            List<I18NMessageDTO> smallIcon3I18nCode = i18NMessageMap.get(item.getAdminSmallIcon3I18nCode());
            addToI18nMsgFrontVO(smallIcon3I18nCode, item.getSmallIcon3I18nCode());

            List<I18NMessageDTO> smallIcon4I18nCode = i18NMessageMap.get(item.getAdminSmallIcon4I18nCode());
            addToI18nMsgFrontVO(smallIcon4I18nCode, item.getSmallIcon4I18nCode());

            List<I18NMessageDTO> smallIcon5I18nCode = i18NMessageMap.get(item.getAdminSmallIcon5I18nCode());
            addToI18nMsgFrontVO(smallIcon5I18nCode, item.getSmallIcon5I18nCode());

            List<I18NMessageDTO> smallIcon6I18nCode = i18NMessageMap.get(item.getAdminSmallIcon6I18nCode());
            addToI18nMsgFrontVO(smallIcon6I18nCode, item.getSmallIcon6I18nCode());


            SiteVenueConfigPO addVenueConfigPO = SiteVenueConfigPO.builder().build();
            BeanUtils.copyProperties(item, addVenueConfigPO);
            addSiteVenueConfigList.add(addVenueConfigPO);
        }


        if (CollectionUtil.isNotEmpty(addSiteVenueConfigList)) {
            super.saveBatch(addSiteVenueConfigList);
        }
    }


    /**
     *
     * 插入站点多语言配置
     * @param i18NMessageList 总控的场馆配置多语言
     * @param messageKey 新的站点场馆多语言CODE
     */
    private void addToI18nMsgFrontVO(List<I18NMessageDTO> i18NMessageList, String messageKey) {
        if (CollectionUtil.isEmpty(i18NMessageList) || ObjectUtil.isEmpty(messageKey)) {
            return;
        }
        List<I18nMsgFrontVO> addList = i18NMessageList.stream().map(x -> {
            if(ObjectUtil.isEmpty(x.getMessage())){
                x.setMessage("");
            }
            I18nMsgFrontVO add = I18nMsgFrontVO.builder().build();
            BeanUtils.copyProperties(x, add);
            add.setMessageKey(messageKey);
            return add;
        }).toList();


        Map<String, List<I18nMsgFrontVO>> i18nData = Maps.newHashMap();
        i18nData.put(messageKey, addList);
        i18nApi.insert(i18nData);
    }


}
