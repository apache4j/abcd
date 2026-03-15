package com.cloud.baowang.play.vo.jl;

import com.alibaba.fastjson2.JSONArray;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@NoArgsConstructor
public class GamePageVO {

    Boolean isOK;
    AtomicInteger currentPage;
    int currentSize;
    Integer pageSize;
    Integer totalItems;
    Integer totalPages;
    private JSONArray transactions;

}
