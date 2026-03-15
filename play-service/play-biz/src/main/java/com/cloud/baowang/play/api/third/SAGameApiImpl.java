package com.cloud.baowang.play.api.third;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.play.api.api.third.SAGameApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.cloud.baowang.play.api.vo.sa.SAGetUserBalanceRes;
import com.cloud.baowang.play.game.sa.SAGameServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SAGameApiImpl implements SAGameApi {

    @Autowired
    private SAGameServiceImpl gameService;

    @Override
    public String queryBalance(String data) {
        SAGetUserBalanceRes result = gameService.queryBalance(data);
        String resultJson = JSON.toJSONString(result);
        return jsonToXml(resultJson);
    }

    @Override
    public String placeBet(String data) {
        SAGetUserBalanceRes result = gameService.placeBet(data);
        String resultJson = JSON.toJSONString(result);
        return jsonToXml(resultJson);
    }

    @Override
    public String playerLost(String data) {
        SAGetUserBalanceRes result = gameService.playerLost(data);
        String resultJson = JSON.toJSONString(result);
        return jsonToXml(resultJson);
    }

    @Override
    public String placeBetCancel(String data) {
        SAGetUserBalanceRes result = gameService.placeBetCancel(data);
        String resultJson = JSON.toJSONString(result);
        return jsonToXml(resultJson);
    }

    @Override
    public String playerWin(String data) {
        SAGetUserBalanceRes result = gameService.playerWin(data);
        String resultJson = JSON.toJSONString(result);
        return jsonToXml(resultJson);
    }

    @Override
    public String balanceAdjustment(String data) {
        SAGetUserBalanceRes result = gameService.balanceAdjustment(data);
        String resultJson = JSON.toJSONString(result);
        return jsonToXml(resultJson);
    }

    public static String jsonToXml(String jsonStr) {
        try {
            ObjectMapper jsonMapper = new ObjectMapper();
            JsonNode node = jsonMapper.readTree(jsonStr);

            XmlMapper xmlMapper = new XmlMapper();
            // 生成XML字符串，默认根标签是ObjectNode
            String xml = xmlMapper.writeValueAsString(node);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<?xml version=\"\"1.0\"\" encoding=\"\"utf-8\"\"?>");
            stringBuilder.append("<RequestResponse>");
            stringBuilder.append(xml.replaceFirst("^<ObjectNode>", "").replaceFirst("</ObjectNode>$", ""));
            stringBuilder.append("</RequestResponse>");
            return stringBuilder.toString();
        } catch (Exception e) {
            log.error("转XML异常:{}", jsonStr);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        // 去掉根标签<ObjectNode>和</ObjectNode>，只留里面的内容
    }


}
