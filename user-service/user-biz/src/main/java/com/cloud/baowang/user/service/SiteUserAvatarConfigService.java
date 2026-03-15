package com.cloud.baowang.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigAddSortVO;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigAddVO;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigPageQueryVO;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigRespVO;
import com.cloud.baowang.user.po.SiteUserAvatarConfigPO;
import com.cloud.baowang.user.repositories.SiteUserAvatarConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
@Slf4j
public class SiteUserAvatarConfigService extends ServiceImpl<SiteUserAvatarConfigMapper, SiteUserAvatarConfigPO> {
    private final UserInfoApi userInfoApi;

    public ResponseVO<Page<SiteUserAvatarConfigRespVO>> pageQuery(SiteUserAvatarConfigPageQueryVO queryVO) {
        Page<SiteUserAvatarConfigPO> page = new Page<>(queryVO.getPageNumber(), queryVO.getPageSize());
        LambdaQueryWrapper<SiteUserAvatarConfigPO> query = Wrappers.lambdaQuery();
        query.eq(SiteUserAvatarConfigPO::getSiteCode, queryVO.getSiteCode());
        String avatarId = queryVO.getAvatarId();
        if (StringUtils.isNotBlank(avatarId)) {
            query.eq(SiteUserAvatarConfigPO::getAvatarId, avatarId);
        }

        String avatarName = queryVO.getAvatarName();
        if (StringUtils.isNotBlank(avatarName)) {
            query.like(SiteUserAvatarConfigPO::getAvatarName, avatarName);
        }
        String creator = queryVO.getUpdater();
        if (StringUtils.isNotBlank(creator)) {
            query.eq(SiteUserAvatarConfigPO::getUpdater, creator);
        }
        Integer status = queryVO.getStatus();
        if (status != null) {
            query.eq(SiteUserAvatarConfigPO::getStatus, status);
        }
        //todo 2024-11-11 根据头像名称排序
        query.orderByAsc(SiteUserAvatarConfigPO::getSort);
        query.orderByDesc(SiteUserAvatarConfigPO::getAvatarName);

        page = this.page(page, query);
        List<SiteUserAvatarConfigPO> records = page.getRecords();
        List<String> usedAvaIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(records)) {
            List<String> avatarIds = records.stream().map(SiteUserAvatarConfigPO::getAvatarId).toList();
            usedAvaIds = userInfoApi.getUsedAvatarList(avatarIds, queryVO.getSiteCode());
        }
        if (CollectionUtil.isEmpty(usedAvaIds)) {
            usedAvaIds = new ArrayList<>();
        }

        List<String> finalUsedAvaIds = usedAvaIds;
        int yesCode = Integer.parseInt(YesOrNoEnum.YES.getCode());
        int noCode = Integer.parseInt(YesOrNoEnum.NO.getCode());
        return ResponseVO.success(ConvertUtil.toConverPage(page.convert(item -> {
            SiteUserAvatarConfigRespVO vo = BeanUtil.copyProperties(item, SiteUserAvatarConfigRespVO.class);
            if (finalUsedAvaIds.contains(vo.getAvatarId())) {
                vo.setIsUsed(yesCode);
            } else {
                vo.setIsUsed(noCode);
            }
            return vo;
        })));
    }

    @Transactional
    public ResponseVO<Boolean> addConfig(SiteUserAvatarConfigAddVO addVO) {

        validateAvatarName(addVO.getAvatarName());
        addVO.setStatus(EnableStatusEnum.ENABLE.getCode());
        LambdaQueryWrapper<SiteUserAvatarConfigPO> query = Wrappers.lambdaQuery();
        query.eq(SiteUserAvatarConfigPO::getSiteCode, addVO.getSiteCode());
        /*long count = this.count(query);

        if (count > 19) {
            addVO.setStatus(EnableStatusEnum.DISABLE.getCode());
        } else {

        }*/
        if (StringUtils.isNotBlank(addVO.getAvatarName())) {
            //不为空校验下重复
            query.eq(SiteUserAvatarConfigPO::getAvatarName, addVO.getAvatarName());
            if (this.count(query) > 0) {
                throw new BaowangDefaultException(ResultCode.AVATOR_NAME_REPEAT);
            }
        }
        SiteUserAvatarConfigPO po = BeanUtil.copyProperties(addVO, SiteUserAvatarConfigPO.class);
        //根据创建时间,获取到最近的一条数据,然后处理头像id自增
        LambdaQueryWrapper<SiteUserAvatarConfigPO> maxPoQuery = Wrappers.lambdaQuery();
        maxPoQuery.eq(SiteUserAvatarConfigPO::getSiteCode, addVO.getSiteCode()).last("limit 0,1").orderByDesc(SiteUserAvatarConfigPO::getCreatedTime);
        SiteUserAvatarConfigPO one = this.getOne(maxPoQuery);
        String txId = "TX01";
        if (one == null) {
            //之前没有数据,当前第一条数据头像id
            po.setAvatarId(txId);
        } else {
            // 之前存在数据，取出最大值并自增
            String maxAvatarIdStr = one.getAvatarId();
            if (maxAvatarIdStr != null && maxAvatarIdStr.startsWith("TX")) {
                // 提取数字部分并自增
                String numberPart = maxAvatarIdStr.substring(2);
                Integer newAvatarId = Integer.parseInt(numberPart) + 1;
                // 格式化 ID，确保小于 10 的情况下前面补零
                String formattedAvatarId = String.format("TX%02d", newAvatarId);
                po.setAvatarId(formattedAvatarId);
            }
        }
        this.save(po);
        return ResponseVO.success();
    }

    private void validateAvatarName(String avatarName) {
        if (StringUtils.isNotEmpty(avatarName)) {
            if (!avatarName.matches("^[\u4e00-\u9fa5A-Za-z0-9]{1,10}$")) {
                throw new BaowangDefaultException(ResultCode.AVATAR_NAME_ERROR);
            }
        }
    }


    @Transactional
    public ResponseVO<Boolean> updConfig(SiteUserAvatarConfigAddVO addVO) {
        String id = addVO.getId();
        SiteUserAvatarConfigPO byId = this.getById(id);

        validateAvatarName(addVO.getAvatarName());

        if (byId == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        SiteUserAvatarConfigPO po = BeanUtil.copyProperties(addVO, SiteUserAvatarConfigPO.class);
        String avatarName = addVO.getAvatarName();
        if (StringUtils.isNotBlank(avatarName)) {
            LambdaQueryWrapper<SiteUserAvatarConfigPO> query = Wrappers.lambdaQuery();
            query.eq(SiteUserAvatarConfigPO::getSiteCode, addVO.getSiteCode())
                    .eq(SiteUserAvatarConfigPO::getAvatarName, addVO.getAvatarName())
                    .ne(SiteUserAvatarConfigPO::getId, addVO.getId());
            if (this.count(query) > 0) {
                throw new BaowangDefaultException(ResultCode.DATA_IS_EXIST);
            }
        }
        this.updateById(po);
        return ResponseVO.success();
    }

    public ResponseVO<Boolean> enableOrDisAble(SiteUserAvatarConfigAddVO addVO) {
        String id = addVO.getId();
        SiteUserAvatarConfigPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        //当前状态与老数据一样
        if (po.getStatus().equals(addVO.getStatus())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        Integer status = addVO.getStatus();
        EnableStatusEnum statusEnum = EnableStatusEnum.nameOfCode(status);
        if (statusEnum == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
//        if (EnableStatusEnum.DISABLE.getCode().equals(statusEnum.getCode())) {
//            if (getAvatarCountByUserUse(po.getAvatarId(), po.getSiteCode()) > 0) {
//                throw new BaowangDefaultException(ResultCode.AVATAR_AL_USED);
//            }
//        } else {
//            //启用头像不能超过11个
//            /*LambdaQueryWrapper<SiteUserAvatarConfigPO> query = Wrappers.lambdaQuery();
//            query.eq(SiteUserAvatarConfigPO::getSiteCode, po.getSiteCode())
//                    .eq(SiteUserAvatarConfigPO::getStatus, EnableStatusEnum.ENABLE.getCode())
//                    .ne(SiteUserAvatarConfigPO::getId, id);
//            if (this.count(query) >= 11) {
//                throw new BaowangDefaultException(ResultCode.AVATAR_MAX_ENABLE_ERROR);
//            }*/
//        }
        po.setUpdater(addVO.getUpdater());
        po.setUpdatedTime(addVO.getUpdatedTime());
        po.setStatus(addVO.getStatus());
        this.updateById(po);
        return ResponseVO.success();
    }

//    /**
//     * 查看某个头像id是否被使用过
//     *
//     * @param avatarId
//     * @param siteCode
//     * @return
//     */
//    private int getAvatarCountByUserUse(String avatarId, String siteCode) {
//        List<String> param = new ArrayList<>();
//        param.add(avatarId);
//        List<String> usedAvatarList = userInfoApi.getUsedAvatarList(param, siteCode);
//        if (CollectionUtil.isNotEmpty(usedAvatarList)) {
//            return usedAvatarList.size();
//        }
//        return 0;
//    }

    @Transactional
    public ResponseVO<Boolean> del(String id) {
        SiteUserAvatarConfigPO po = this.getById(id);
//        if (getAvatarCountByUserUse(po.getAvatarId(), po.getSiteCode()) > 0) {
//            throw new BaowangDefaultException(ResultCode.AVATAR_AL_USED);
//        }
        this.removeById(id);
        return ResponseVO.success();
    }

    public ResponseVO<List<SiteUserAvatarConfigRespVO>> getListBySiteCode(String siteCode) {
        LambdaQueryWrapper<SiteUserAvatarConfigPO> query = Wrappers.lambdaQuery();
        query.eq(SiteUserAvatarConfigPO::getSiteCode, siteCode)
                .eq(SiteUserAvatarConfigPO::getStatus, EnableStatusEnum.ENABLE.getCode())
                .orderByAsc(SiteUserAvatarConfigPO::getSort);
        List<SiteUserAvatarConfigPO> list = this.list(query);
        return ResponseVO.success(BeanUtil.copyToList(list, SiteUserAvatarConfigRespVO.class));
    }

    public ResponseVO<SiteUserAvatarConfigRespVO> getAvatarConfigByTXIdSiteCode(String siteCode, String avatarId) {
        LambdaQueryWrapper<SiteUserAvatarConfigPO> query = Wrappers.lambdaQuery();
        query.eq(SiteUserAvatarConfigPO::getSiteCode, siteCode).eq(SiteUserAvatarConfigPO::getAvatarId, avatarId);
        SiteUserAvatarConfigPO one = this.getOne(query);
        return ResponseVO.success(BeanUtil.copyProperties(one, SiteUserAvatarConfigRespVO.class));
    }

    public SiteUserAvatarConfigRespVO getRandomUserAvatar(String siteCode) {
        SiteUserAvatarConfigRespVO result = new SiteUserAvatarConfigRespVO();
        ResponseVO<List<SiteUserAvatarConfigRespVO>> resp = this.getListBySiteCode(siteCode);
        if (resp.isOk()) {
            List<SiteUserAvatarConfigRespVO> data = resp.getData();
            if (CollectionUtil.isNotEmpty(data)) {
                //从集合中随机取一条,随机数从0开始,到集合长度-1结束
                Random random = new Random();
                int randomIndex = random.nextInt(data.size()); // 生成一个随机索引
                result = data.get(randomIndex); // 获取随机元素
            }
        }
        return result;
    }

    public ResponseVO<Boolean> addSort(List<SiteUserAvatarConfigAddSortVO> addSortVOS) {
        List<SiteUserAvatarConfigPO> siteUserAvatarConfigPOS = BeanUtil.copyToList(addSortVOS, SiteUserAvatarConfigPO.class);
        this.updateBatchById(siteUserAvatarConfigPOS);
        return ResponseVO.success();
    }

    public ResponseVO<List<SiteUserAvatarConfigAddSortVO>> getSortList(String siteCode) {
        LambdaQueryWrapper<SiteUserAvatarConfigPO> query = Wrappers.lambdaQuery();
        query.eq(SiteUserAvatarConfigPO::getSiteCode, siteCode).orderByAsc(SiteUserAvatarConfigPO::getSort);
        List<SiteUserAvatarConfigPO> list = this.list(query);
        return ResponseVO.success(BeanUtil.copyToList(list, SiteUserAvatarConfigAddSortVO.class));
    }
}