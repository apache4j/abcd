package com.cloud.baowang.system.service.tutorial;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.system.api.enums.tutorial.ChangeDirectoryEnum;
import com.cloud.baowang.system.api.enums.tutorial.ChangeTypeEnum;
import com.cloud.baowang.system.api.vo.site.tutorial.operation.TutorialOperationRecordRspVO;
import com.cloud.baowang.system.api.vo.site.tutorial.operation.TutorialOperationRecordResVO;
import com.cloud.baowang.system.po.tutorial.TutorialOperationRecordPO;
import com.cloud.baowang.system.repositories.site.tutorial.TutorialOperationRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
@AllArgsConstructor
public class TutorialOperationRecordService extends ServiceImpl<TutorialOperationRecordRepository, TutorialOperationRecordPO> {

    private final TutorialOperationRecordRepository tutorialCategoryRepository;
    private final ThreadPoolTaskExecutor asyncService;

    public Page<TutorialOperationRecordRspVO> listPage(TutorialOperationRecordResVO vo) {

        Page<TutorialOperationRecordPO> pageVo = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<TutorialOperationRecordPO> query = new LambdaQueryWrapper<>();
        query.eq(StringUtils.isNotBlank(vo.getSiteCode()), TutorialOperationRecordPO::getSiteCode, vo.getSiteCode());
        if (StringUtils.isNotBlank(vo.getChangeCatalog())){
            String directName = ChangeDirectoryEnum.nameOfCode(Integer.valueOf(vo.getChangeCatalog())).getName();
            query.eq(TutorialOperationRecordPO::getChangeCatalog,directName );
        }
        if (StringUtils.isNotBlank(vo.getChangeType())){
            String typeName = ChangeTypeEnum.nameOfCode(Integer.valueOf(vo.getChangeType())).getName();
            query.eq(TutorialOperationRecordPO::getChangeType, typeName);
        }
        query.ge(TutorialOperationRecordPO::getUpdateTime, vo.getStartTime());

        query.le(TutorialOperationRecordPO::getUpdateTime, vo.getEndTime());

        if (StringUtils.isNotBlank(vo.getOrderField()) && "updateTime".equals(vo.getOrderField())) {
            query.orderBy(StringUtils.isNotBlank(vo.getOrderField()), Sort.Direction.ASC.name().equalsIgnoreCase(vo.getOrderType()), TutorialOperationRecordPO::getUpdateTime);
        }
        Page<TutorialOperationRecordPO> page = tutorialCategoryRepository.selectPage(pageVo, query);

        return ConvertUtil.toConverPage(page.convert(item -> {
            TutorialOperationRecordRspVO rspVO = BeanUtil.copyProperties(item, TutorialOperationRecordRspVO.class);
            return rspVO;
        }));

    }

    public void asyncAddChangeRecord(List<TutorialOperationRecordPO> po) {
        CompletableFuture.runAsync(() -> {
            this.saveBatch(po);
        }, asyncService);
    }

}
