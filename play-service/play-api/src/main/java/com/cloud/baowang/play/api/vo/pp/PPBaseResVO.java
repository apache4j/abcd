package com.cloud.baowang.play.api.vo.pp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PPBaseResVO implements Serializable {

    String error;
    String description;

}
