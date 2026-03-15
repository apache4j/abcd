package com.cloud.baowang.common.es.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.PageVO;
import org.dromara.easyes.core.biz.EsPageInfo;

import java.util.List;
import java.util.function.Consumer;


/**
 * @Author: sheldon
 * @Date: 3/18/24 10:59 上午
 */
public class PageConvertUtil {
    public static final int DEFAULT_START_CURRENT = 0;

    public PageConvertUtil() {
    }

    public static <T> Page<T> getPage(PageVO query) {
        int pageNumber = NumberUtil.toInt(String.valueOf(query.getPageNumber()), 1);
        int size = NumberUtil.toInt(String.valueOf(query.getPageSize()), 10);
        int current = pageNumber - 1;
        if (current < 0) {
            current = 0;
        }

        return new Page<>(current,size);
    }

    /**
     * 此方法用于查数据库分页对象
     */
    public static <T> Page<T> getMybatisPage(PageVO query) {
        return new Page<>(
                NumberUtil.toInt(String.valueOf(query.getPageNumber() ), 1),
                NumberUtil.toInt(String.valueOf(query.getPageSize()), 10));
    }

    public static <T, F> Page<F> convertPage(EsPageInfo<T> pageInfo, Class<F> clazz) {
        Page<F> page = new Page<>();
        page.setCurrent(pageInfo.getPageNum());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setSize(pageInfo.getSize());
        page.setRecords(ConvertUtil.entityListToModelList(pageInfo.getList(), clazz));
        return page;
    }

    public static <T, F> Page<F> convertPage(EsPageInfo<T> pageInfo, Class<F> clazz, List<F> list) {
        Page<F> page = new Page<>();
        page.setCurrent(pageInfo.getPageNum());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setSize(pageInfo.getSize());
        page.setRecords(list);
        return page;
    }

    public static <T, F> Page<F> convertPage(EsPageInfo<T> pageInfo, Class<F> clazz, Consumer<T> consumer) {
        Page<F> page = new Page<>();
        page.setCurrent(pageInfo.getPageNum());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setSize(pageInfo.getSize());
        page.setRecords(ConvertUtil.entityListToModelList(pageInfo.getList(), clazz, consumer));
        return page;
    }


}
