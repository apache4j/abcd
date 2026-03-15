package com.cloud.baowang.play.wallet.vo.req.db.sh.vo;

import lombok.Data;

import java.util.List;

@Data
public class SHUserInfos {

    private List<String> loginNames;

    private String currency;

    private List<String> stokens;
}
