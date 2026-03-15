package com.cloud.baowang.play.wallet.service;


import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.wallet.vo.sexy.SexyActionVo;


public interface SexyGameApi {
    JSONObject action(SexyActionVo actionVo);
}
