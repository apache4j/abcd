package com.cloud.baowang.play.vo.sh;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页类，封装分页基本信息
 */
@Data
public class Page<T> {
    //当前页
    private int curPage = 1;
    //总页数
    private int totalPage;
    //数据库记录数
    private int rows;
    //每页数据量
    private int pageNumber = 10;
    //要展示的List数据
    private List<T> data = new ArrayList<T>();
}
