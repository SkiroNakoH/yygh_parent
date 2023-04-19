package com.atguigu.yygh.vo.sms;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "短信实体")
public class SmsVo {

    @ApiModelProperty(value = "phone")
    private String phone;

    @ApiModelProperty(value = "短信模板code")
    private String templateCode;

    @ApiModelProperty(value = "短信模板参数")
    private String[] param;
}
