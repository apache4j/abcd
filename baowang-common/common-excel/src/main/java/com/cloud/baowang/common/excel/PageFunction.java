package com.cloud.baowang.common.excel;


import com.cloud.baowang.common.core.vo.base.PageVO;

@FunctionalInterface
public interface PageFunction<R, P extends PageVO> {

    R apply(P param);
}