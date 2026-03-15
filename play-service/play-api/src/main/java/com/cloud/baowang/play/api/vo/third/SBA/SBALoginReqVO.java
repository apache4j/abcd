package com.cloud.baowang.play.api.vo.third.SBA;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "沙巴体育登陆对象")
public class SBALoginReqVO implements Serializable {

    /**
     * 厂商识别码, 最大长度 = 50
     */
    private String vendor_id;


    /**
     * 会员账号（建议跟 Username 一样）, 支援 ASCII Table 33-126, 最大长度 = 30
     */
    private String vendor_member_id;



}
