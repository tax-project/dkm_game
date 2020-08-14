package com.dkm.wechat.entity.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jie
 */
@Data
@ApiModel("查询到的用户信息")
public class FriendInfoBO {
    /**
     * 用户ID
     */
    @ApiModelProperty(name = "userId",value = "用户ID")
    private Long userId;


    /**
     * 微信昵称
     */
    @ApiModelProperty(name = "weChatNickName",value = "用户昵称")
    private String weChatNickName;

    /**
     * 微信头像地址
     */
    @ApiModelProperty(name = "weChatHeadImgUrl",value = "头像地址")
    private String weChatHeadImgUrl;

    /**
     * 性别
     * 1--男
     * 2--女
     */
    @ApiModelProperty(name = "userSex",value = "用户性别")
    private Integer userSex;


}
