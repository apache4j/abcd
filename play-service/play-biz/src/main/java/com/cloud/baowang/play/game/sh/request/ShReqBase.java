package com.cloud.baowang.play.game.sh.request;

import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.play.util.HashUtil;
import com.cloud.baowang.play.util.SecurityUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.HashMap;

@Data
public class ShReqBase {

    @Schema(title = "商户号", description = "商户号", required = true)
    private String merchantNo;

    @Schema(title = "调用时间戳", description = "调用时间戳", required = true)
    private Long timeStamp;

    @Schema(title = "语言", description = "语言 目前只支持zh:简体中文 en:英文", required = true, allowableValues = {"zh", "en"})
    private String lang;

    @Schema(title = "hash签名", description = "hash签名(merchantNo + timeStamp + apiSign字符串SHA-256加密)", required = true)
    private String hashSign;


    @Schema(title = "Md5签名", description = "Md5签名(apiSign+｜+md5Sign+｜+userName字符串MD5加密后并转换成大写)", required = true)
    private String md5Sign;


    public String getLang() {
        return LanguageEnum.ZH_CN.getLang().equals(this.lang) ? "zh" : "en";
    }


    public void setHashSign(String hashSign) {
        this.hashSign = HashUtil.sha256(hashSign);
    }

    public void setMd5Sign(HashMap<String, Object> hashMap) {
        this.md5Sign = SecurityUtil.paramSigns(hashMap);
    }





}
