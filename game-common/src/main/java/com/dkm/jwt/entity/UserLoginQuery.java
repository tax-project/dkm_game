package com.dkm.jwt.entity;

import lombok.Data;

/**
 * @Author qf
 * @Date 2019/9/19
 * @Version 1.0
 */
@Data
public class UserLoginQuery {

    /**
     * 用户ID
     */
    private Long id;
    /**
     * 用户微信openid
     */
    private String wxOpenId;
    /**
     * 用户微信昵称
     */
    private String wxNickName;

    /**
     * 用户等级
     */
    private Integer userLevel;

    /**
     * 0--不是VIP
     * 1--是VIP
     */
    private Integer userInfoIsVip;
}
