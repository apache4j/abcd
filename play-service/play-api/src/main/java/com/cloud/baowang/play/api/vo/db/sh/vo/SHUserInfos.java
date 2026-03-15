package com.cloud.baowang.play.api.vo.db.sh.vo;

import lombok.Data;

import java.util.List;

@Data
public class SHUserInfos {

    private List<String> loginNames;

    private String currency;

    private List<String> stokens;
}
