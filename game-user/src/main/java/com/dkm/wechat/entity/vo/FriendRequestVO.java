package com.dkm.wechat.entity.vo;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jie
 */
@Data
@ApiModel(value = "传递参数")
public class FriendRequestVO {
    @ApiModelProperty(value = "用户的ID",name = "id",allowEmptyValue = true)
    private Long id;
}
