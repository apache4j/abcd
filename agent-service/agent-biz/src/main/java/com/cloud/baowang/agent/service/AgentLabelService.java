package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentLabelChangeEnum;
import com.cloud.baowang.agent.api.vo.label.AgentLabelAddVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelDeleteVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelEditVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelListPageVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelListVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelRecordListVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelReordListPageVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelReordListUserPageVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelUserVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentLabelPO;
import com.cloud.baowang.agent.po.AgentLabelRecordPO;
import com.cloud.baowang.agent.repositories.AgentInfoModifyReviewRepository;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.agent.repositories.AgentLabelRecordRepository;
import com.cloud.baowang.agent.repositories.AgentLabelRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.ReviewStatusEnum;
import com.cloud.baowang.agent.api.enums.AgentInfoChangeTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AgentLabelService extends ServiceImpl<AgentLabelRepository, AgentLabelPO> {

    private final AgentLabelRepository repository;

    private final AgentLabelRecordRepository agentLabelRecordRepository;

    private final AgentInfoModifyReviewRepository reviewRepository;

    private final AgentInfoRepository agentInfoRepository;

    private final SystemParamApi systemParamApi;

    public ResponseVO<Void> add(AgentLabelAddVO agentLabelAddVO) {
        LambdaQueryWrapper<AgentLabelPO> query = Wrappers.lambdaQuery();
        query.eq(AgentLabelPO::getName, agentLabelAddVO.getName())
                .eq(AgentLabelPO::getSiteCode, agentLabelAddVO.getSiteCode())
                .eq(AgentLabelPO::getDeleted, EnableStatusEnum.ENABLE.getCode());
        //判断当前站点是否已配置该名称的代理标签
        long count = this.count(query);
        if (0 < count) {
            throw new BaowangDefaultException(ResultCode.AGENT_LABEL_EXISTED);
        }
        long cTime = System.currentTimeMillis();
        AgentLabelPO agentLabelPo = new AgentLabelPO();
        agentLabelPo.setSiteCode(agentLabelAddVO.getSiteCode());
        agentLabelPo.setName(agentLabelAddVO.getName());
        agentLabelPo.setDescription(agentLabelAddVO.getDescription());
        agentLabelPo.setOperator(agentLabelAddVO.getOperator());
        agentLabelPo.setCreator(agentLabelAddVO.getOperator());
        agentLabelPo.setCreatedTime(cTime);
        agentLabelPo.setUpdatedTime(cTime);
        this.save(agentLabelPo);
        AgentLabelRecordPO record = createRecord(agentLabelAddVO.getSiteCode(), AgentLabelChangeEnum.ADD.getCode(), null, agentLabelAddVO.getName(), null,
                agentLabelPo, agentLabelAddVO.getOperator());
        agentLabelRecordRepository.insert(record);
        return ResponseVO.success();
    }

    /**
     * 构建代理标签变更配置记录
     *
     * @param siteCode     siteCode
     * @param type         当前操作类型 {@link AgentLabelChangeEnum}
     * @param agentLabelId 标签id,新增为null
     * @param name         标签名称
     * @param before       变更之前
     * @param after        变更后
     * @param operator     操作人
     * @return 配置记录实体
     */
    private AgentLabelRecordPO createRecord(String siteCode,
                                            Integer type,
                                            String agentLabelId,
                                            String name,
                                            AgentLabelPO before,
                                            AgentLabelPO after,
                                            String operator) {
        AgentLabelRecordPO agentLabelRecordPO = new AgentLabelRecordPO();
        agentLabelRecordPO.setType(type);
        agentLabelRecordPO.setAgentLabelId(agentLabelId);
        agentLabelRecordPO.setAgentLabelName(name);
        //修改标签名
        if (AgentLabelChangeEnum.EDIT_NAME.getCode().equals(type)) {
            agentLabelRecordPO.setChangeBefore(before.getName());
            agentLabelRecordPO.setChangeAfter(after.getName());
        }
        //修改描述
        if (AgentLabelChangeEnum.EDIT_DESCRIPTION.getCode().equals(type)) {
            agentLabelRecordPO.setChangeBefore(before.getDescription());
            agentLabelRecordPO.setChangeAfter(after.getDescription());
        }
        //新增标签
        if (AgentLabelChangeEnum.ADD.getCode().equals(type)) {
            agentLabelRecordPO.setChangeBefore("");
            agentLabelRecordPO.setChangeAfter("");
        }
        //删除标签
        if (AgentLabelChangeEnum.DELETE.getCode().equals(type)) {
            agentLabelRecordPO.setChangeBefore("");
            agentLabelRecordPO.setChangeAfter("");
        }
        agentLabelRecordPO.setSiteCode(siteCode);
        //创建变更记录
        agentLabelRecordPO.setOperator(operator);
        agentLabelRecordPO.setCreator(operator);
        agentLabelRecordPO.setCreatedTime(System.currentTimeMillis());
        agentLabelRecordPO.setUpdatedTime(System.currentTimeMillis());
        return agentLabelRecordPO;
    }

    /**
     * 修改标签属性
     *
     * @param agentLabelEditVO 修改对象
     * @return void
     */
    public ResponseVO<Void> edit(AgentLabelEditVO agentLabelEditVO) {
        Long id = agentLabelEditVO.getId();
        AgentLabelPO afterAgentLabelPo = this.getById(id);
        if (afterAgentLabelPo == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        LambdaQueryWrapper<AgentLabelPO> query = Wrappers.lambdaQuery();
        query.eq(AgentLabelPO::getSiteCode, agentLabelEditVO.getSiteCode());
        query.eq(AgentLabelPO::getName, agentLabelEditVO.getName())
                .eq(AgentLabelPO::getDeleted,EnableStatusEnum.ENABLE.getCode())
                .ne(AgentLabelPO::getId, agentLabelEditVO.getId()).last("limit 1");
        AgentLabelPO checkPO = repository.selectOne(query);
        if (checkPO != null) {
            throw new BaowangDefaultException(ResultCode.AGENT_LABEL_NAME_DUPLICATE);
        }
//                LambdaQueryWrapper<AgentLabelPO> query = Wrappers.lambdaQuery();
//        query.eq(AgentLabelPO::getSiteCode, agentLabelEditVO.getSiteCode())
//                .eq(AgentLabelPO::getName, name)
//                .ne(AgentLabelPO::getId, afterAgentLabelPo.getId());
//        if (this.count(query) > 0) {
//            throw new BaowangDefaultException(ResultCode.AGENT_LABEL_NAME_DUPLICATE);
//        }
        AgentLabelPO beforeAgentLabelPo = new AgentLabelPO();
        BeanUtil.copyProperties(afterAgentLabelPo, beforeAgentLabelPo);
        afterAgentLabelPo.setName(agentLabelEditVO.getName());
        afterAgentLabelPo.setDescription(agentLabelEditVO.getDescription());
        afterAgentLabelPo.setUpdater(agentLabelEditVO.getOperator());
        afterAgentLabelPo.setUpdatedTime(System.currentTimeMillis());
        afterAgentLabelPo.setOperator(agentLabelEditVO.getOperator());
        //判断当前修改的是标签名还是内容
        if (StringUtils.isNotBlank(agentLabelEditVO.getName()) && !agentLabelEditVO.getName().equals(beforeAgentLabelPo.getName())) {
            //修改名称操作
            AgentLabelRecordPO record = createRecord(agentLabelEditVO.getSiteCode(), AgentLabelChangeEnum.EDIT_NAME.getCode(), beforeAgentLabelPo.getId()
                    , agentLabelEditVO.getName(), beforeAgentLabelPo,
                    afterAgentLabelPo, agentLabelEditVO.getOperator());
            agentLabelRecordRepository.insert(record);
        }
        if (StringUtils.isNotBlank(agentLabelEditVO.getDescription()) && !agentLabelEditVO.getDescription().equals(beforeAgentLabelPo.getDescription())) {
            String beforeName = beforeAgentLabelPo.getName();
            if (StringUtils.isNotBlank(agentLabelEditVO.getName()) && !agentLabelEditVO.getName().equals(beforeAgentLabelPo.getName())) {
                beforeName = agentLabelEditVO.getName();
            }
            //修改标签操作
            AgentLabelRecordPO record = createRecord(agentLabelEditVO.getSiteCode(), AgentLabelChangeEnum.EDIT_DESCRIPTION.getCode(), beforeAgentLabelPo.getId()
                    , beforeName, beforeAgentLabelPo,
                    afterAgentLabelPo, agentLabelEditVO.getOperator());
            agentLabelRecordRepository.insert(record);
        }
        this.updateById(afterAgentLabelPo);
        return ResponseVO.success();
    }

    /**
     * 删除标签
     *
     * @param vo id
     * @return void
     */
    public ResponseVO<Void> delete(AgentLabelDeleteVO vo) {
        AgentLabelPO agentLabelPo = this.getById(vo.getId());
        if (null != agentLabelPo) {
            //校验标签是否有被使用,如果被使用,不允许删除
            Long count = agentInfoRepository.selectLabelUseCount(agentLabelPo.getId());
            if (count > 0) {
                throw new BaowangDefaultException(ResultCode.AGENT_LABEL_AL_USED);
            }

            ArrayList<Integer> reviewStatusArr = new ArrayList<>();
            reviewStatusArr.add(ReviewStatusEnum.REVIEW_PENDING.getCode());
            reviewStatusArr.add(ReviewStatusEnum.REVIEW_PROGRESS.getCode());
            String siteCode = agentLabelPo.getSiteCode();
            Integer code = AgentInfoChangeTypeEnum.AGENT_LABEL.getCode();
            String afterValue = agentLabelPo.getId();
            Long l = reviewRepository.selectUseCount(siteCode, code, reviewStatusArr, afterValue);
            if (l > 0) {
                throw new BaowangDefaultException(ResultCode.AGENT_LABEL_AL_USED);
            }

            //删除标签操作
            AgentLabelRecordPO record = createRecord(vo.getSiteCode(),
                    AgentLabelChangeEnum.DELETE.getCode(), agentLabelPo.getId()
                    , agentLabelPo.getName(), agentLabelPo,
                    null, vo.getOperator());
            agentLabelRecordRepository.insert(record);
            //this.removeById(agentLabelPo.getId());
            agentLabelPo.setUpdatedTime(System.currentTimeMillis());
            agentLabelPo.setDeleted(EnableStatusEnum.DISABLE.getCode());
            this.updateById(agentLabelPo);
        }
        return ResponseVO.success();
    }

    /**
     * 分页查询标签信息
     *
     * @param vo 分页对象
     * @return 标签列表
     */
    public ResponseVO<Page<AgentLabelListVO>> listPage(AgentLabelListPageVO vo) {

        LambdaQueryWrapper<AgentLabelPO> query = Wrappers.lambdaQuery();
        String name = vo.getName();
        query.eq(AgentLabelPO::getDeleted, EnableStatusEnum.ENABLE.getCode());
        if (StringUtils.isNotBlank(name)) {
            query.eq(AgentLabelPO::getName, name);
        }
        String operator = vo.getOperator();
        if (StringUtils.isNotBlank(operator)) {
            query.eq(AgentLabelPO::getOperator, operator);
        }
        String siteCode = vo.getSiteCode();
        if (StringUtils.isNotBlank(siteCode)) {
            query.eq(AgentLabelPO::getSiteCode, siteCode);
        }
        String orderField = vo.getOrderField();
        if (StringUtils.isNotBlank(orderField)) {
            if ("createdTime".equals(orderField)) {
                if ("asc".equals(vo.getOrderType())) {
                    query.orderByAsc(AgentLabelPO::getCreatedTime);
                } else {
                    query.orderByDesc(AgentLabelPO::getCreatedTime);
                }
            } else if ("updatedTime".equals(orderField)) {
                if ("asc".equals(vo.getOrderType())) {
                    query.orderByAsc(AgentLabelPO::getUpdatedTime);
                } else {
                    query.orderByDesc(AgentLabelPO::getUpdatedTime);
                }
            }
        } else {
            query.orderByDesc(AgentLabelPO::getUpdatedTime);
        }
        Page<AgentLabelPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        page = repository.selectPage(page, query);

        IPage<AgentLabelListVO> voPage = page.convert(item -> {
            String labelId = item.getId();
            //统计当前标签被多少代理使用到，一个代理可能会包含多个标签，这里用find_in_set
            AgentLabelListVO resultVo = BeanUtil.copyProperties(item, AgentLabelListVO.class);
            // todo
            Long useLabelCount = agentInfoRepository.getAgentLabelCount(labelId);
            //统计使用条数
            resultVo.setUserCount(useLabelCount);
            resultVo.setCreatorName(item.getCreator());

            return resultVo;
        });
        return ResponseVO.success(ConvertUtil.toConverPage(voPage));
    }

    /**
     * @param vo
     * @return
     */
    /*public ResponseVO<Page<AgentLabelListVO>> listPage(AgentLabelListPageVO vo) {
        String operator = null;
        if (Strings.isNotBlank(vo.getName())) {
            BusinessAdminVO adminVO = businessAdminApi.getAdminByUserName(vo.getName());
            if (Optional.ofNullable(adminVO).map(BusinessAdminVO::getId).isPresent()) {
                operator = adminVO.getId();
            }
        }
        LambdaQueryChainWrapper<AgentLabelPO> eq = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(StringUtils.isNotBlank(vo.getName()), AgentLabelPO::getName, vo.getName())
                .eq(StringUtils.isNotBlank(operator), AgentLabelPO::getCreator, operator);

        if (StringUtils.isNotBlank(vo.getOrderField()) && BasePO.Fields.createdTime.equals(vo.getOrderField())) {
            eq.orderBy(StringUtils.isNotBlank(vo.getOrderField()), Sort.Direction.ASC.name().equalsIgnoreCase(vo.getOrderType()), AgentLabelPO::getCreatedTime);
        }
        if (StringUtils.isNotBlank(vo.getOrderField()) && BasePO.Fields.updatedTime.equals(vo.getOrderField())) {
            eq.orderBy(StringUtils.isNotBlank(vo.getOrderField()), Sort.Direction.ASC.name().equalsIgnoreCase(vo.getOrderType()), AgentLabelPO::getUpdatedTime);
        }
        Page<AgentLabelPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        page = eq.page(page);
        List<AgentLabelPO> records = page.getRecords();
        if (records.isEmpty()) {
            Page<AgentLabelListVO> pageResult = new Page<>();
            BeanUtil.copyProperties(page, pageResult);
            return ResponseVO.success(pageResult);
        }
        List<String> ids = records.stream().map(AgentLabelPO::getId).collect(Collectors.toList());
        List<AgentInfoLabelCountVO> agentLabelCount = agentInfoRepository.agentLabelCount(ids);
        Map<String, Long> maps = agentLabelCount.stream().collect(Collectors.toMap(AgentInfoLabelCountVO::getAgentLabelId, AgentInfoLabelCountVO::getCount, (key1, key2) -> key2));
        List<AgentLabelListVO> rspList = new ArrayList<>();
        List<String> collect = records.stream().map(AgentLabelPO::getCreator).filter(Strings::isNotBlank).collect(Collectors.toList());
        collect.addAll(records.stream().map(AgentLabelPO::getOperator).filter(Strings::isNotBlank).toList());
        Map<String, String> adminMap = Optional.ofNullable(businessAdminApi.getUserByIds(collect))
                .filter(CollUtil::isNotEmpty).map(s -> s.stream().collect(Collectors.toMap(BusinessAdminVO::getId, BusinessAdminVO::getUserName))).orElse(Maps.newHashMap());

        records.forEach(agentLabelPo -> {
            AgentLabelListVO agentLabelVO = new AgentLabelListVO();
            BeanUtil.copyProperties(agentLabelPo, agentLabelVO);
            Long count = maps.get(agentLabelPo.getId());
            agentLabelVO.setUserCount(null == count ? 0 : count);
            agentLabelVO.setCreatorName(adminMap.get(agentLabelPo.getCreator()));
            agentLabelVO.setOperator(adminMap.get(agentLabelPo.getOperator()));
            agentLabelVO.setCreatedTime(agentLabelPo.getCreatedTime());
            agentLabelVO.setUpdatedTime(agentLabelPo.getUpdatedTime());
            agentLabelVO.setId(agentLabelPo.getId());
            rspList.add(agentLabelVO);
        });

        Page<AgentLabelListVO> pageResult = new Page<>();
        BeanUtil.copyProperties(page, pageResult);
        pageResult.setRecords(rspList);
        return ResponseVO.success(pageResult);
    }*/

    /**
     * 代理标签配置变更记录列表
     *
     * @param vo 站点信息，分页查询条件
     * @return 当前站点下，所有标签变更记录
     */
    public ResponseVO<Page<AgentLabelRecordListVO>> recordListPage(AgentLabelReordListPageVO vo) {
//        IPage<AgentLabelRecordPO> pageList = agentLabelRecordRepository.selectPage(new Page<>(vo.getPageNumber(), vo.getPageSize()),
//                Wrappers.lambdaQuery(AgentLabelRecordPO.class)
//                        .orderByDesc(AgentLabelRecordPO::getCreatedTime)
//                        //站点code
//                        .eq(AgentLabelRecordPO::getSiteCode, vo.getSiteCode())
//                        .gt(StringUtils.isNotBlank(vo.getStartTime()), AgentLabelRecordPO::getCreatedTime, vo.getStartTime())
//                        .lt(StringUtils.isNotBlank(vo.getEndTime()), AgentLabelRecordPO::getCreatedTime, vo.getEndTime())
//                        .in(CollectionUtil.isNotEmpty(vo.getType()), AgentLabelRecordPO::getType, vo.getType())
//                        .eq(StringUtils.isNotBlank(vo.getAgentLabelName()), AgentLabelRecordPO::getAgentLabelName, vo.getAgentLabelName())
//                        .eq(StringUtils.isNotBlank(vo.getOperator()), AgentLabelRecordPO::getOperator, vo.getOperator())
//                        .orderBy(StringUtils.isNotBlank(vo.getOrderField()), Sort.Direction.DESC.name().equalsIgnoreCase(vo.getOrderType()), AgentLabelRecordPO::getUpdatedTime)
//
//        );
        LambdaQueryWrapper<AgentLabelRecordPO> query = Wrappers.lambdaQuery();
        query.eq(AgentLabelRecordPO::getSiteCode, vo.getSiteCode())
                .gt(StringUtils.isNotBlank(vo.getStartTime()), AgentLabelRecordPO::getCreatedTime, vo.getStartTime())
                .lt(StringUtils.isNotBlank(vo.getEndTime()), AgentLabelRecordPO::getCreatedTime, vo.getEndTime())
                .in(CollectionUtil.isNotEmpty(vo.getType()), AgentLabelRecordPO::getType, vo.getType())
                .eq(StringUtils.isNotBlank(vo.getAgentLabelName()), AgentLabelRecordPO::getAgentLabelName, vo.getAgentLabelName())
                .eq(StringUtils.isNotBlank(vo.getOperator()), AgentLabelRecordPO::getOperator, vo.getOperator());
        query.orderByDesc(AgentLabelRecordPO::getUpdatedTime);
        IPage<AgentLabelRecordPO> pageList = new Page<>(vo.getPageNumber(), vo.getPageSize());
        pageList = agentLabelRecordRepository.selectPage(pageList, query);
        //获取变更操作国际化
        Map<String, List<CodeValueVO>> map = systemParamApi.getSystemParamsByList(
                List.of(CommonConstant.AGENT_LABEL_OPERATION_TYPE)).getData();

        IPage<AgentLabelRecordListVO> voPage = pageList.convert(item -> {
            AgentLabelRecordListVO recordListVO = BeanUtil.copyProperties(item, AgentLabelRecordListVO.class);
            //变更操作国际化
            Optional<CodeValueVO> operationOpt = map.get(CommonConstant.AGENT_LABEL_OPERATION_TYPE)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(recordListVO.getType()))).findFirst();
            //设置一下启用禁用名称进去
            operationOpt.ifPresent(systemParamVO -> recordListVO.setTypeName(systemParamVO.getValue()));
            return recordListVO;
        });
        return ResponseVO.success(ConvertUtil.toConverPage(voPage));
    }

    /**
     * 标签分页列表中查询当前标签所关联代理列表
     *
     * @param vo 所属标签id,站点code
     * @return 当前标签下关联的所有代理
     */
    public ResponseVO<Page<AgentLabelUserVO>> recordListUserPage(AgentLabelReordListUserPageVO vo) {
        String siteCode = vo.getSiteCode();
        String labelId = vo.getLabelId();
        Page<AgentInfoPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        page = agentInfoRepository.findInSetPageByLabelId(page, siteCode, labelId);

        //代理类型国际化
        Map<String, List<CodeValueVO>> map = systemParamApi.getSystemParamsByList(
                List.of(CommonConstant.AGENT_TYPE)).getData();

        IPage<AgentLabelUserVO> convert = page.convert(item -> {
            AgentLabelUserVO resultVo = BeanUtil.copyProperties(item, AgentLabelUserVO.class);

            //代理类型
            Optional<CodeValueVO> agentTypeOpt = map.get(CommonConstant.AGENT_TYPE)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(resultVo.getAgentType()))).findFirst();
            //设置一下启用禁用名称进去
            agentTypeOpt.ifPresent(systemParamVO -> resultVo.setAgentTypeName(systemParamVO.getValue()));
            return resultVo;
        });
        return ResponseVO.success(ConvertUtil.toConverPage(convert));
    }

    /**
     * 获取所有标签
     *
     * @return 标签vos
     */
    public List<AgentLabelVO> getAllAgentLabel() {
        List<AgentLabelPO> list = this.list();
        return ConvertUtil.entityListToModelList(list, AgentLabelVO.class);
    }

    public List<AgentLabelVO> getAgentLabelByAgentLabelIds(List<String> agentLabelIds) {
        if (CollectionUtils.isEmpty(agentLabelIds)) {
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<AgentLabelPO> query = Wrappers.lambdaQuery();
        query.in(AgentLabelPO::getId, agentLabelIds);
        List<AgentLabelPO> list = this.list(query);
        return ConvertUtil.entityListToModelList(list, AgentLabelVO.class);
    }


    /**
     * 获取当前站点所有标签-站点code
     *
     * @return 标签vos
     */
    public List<AgentLabelVO> getAllAgentLabel(String siteCode) {
        LambdaQueryWrapper<AgentLabelPO> query = Wrappers.lambdaQuery();
        query.eq(AgentLabelPO::getSiteCode, siteCode).eq(AgentLabelPO::getDeleted, EnableStatusEnum.ENABLE.getCode());
        List<AgentLabelPO> list = this.list(query);
        return ConvertUtil.entityListToModelList(list, AgentLabelVO.class);
    }

    public List<AgentLabelVO> getListByIds(List<String> ids) {
        List<AgentLabelPO> list = this.list(Wrappers.lambdaQuery(AgentLabelPO.class).eq(AgentLabelPO::getDeleted, EnableStatusEnum.ENABLE.getCode()).in(AgentLabelPO::getId, ids));
        return ConvertUtil.entityListToModelList(list, AgentLabelVO.class);
    }

}
