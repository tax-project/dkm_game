package com.dkm.seed.entity.vo;

import lombok.Data;

/**
 * @author 刘梦祺
 * @PROJECT_NAME: dkm_game
 * @DESCRIPTION:
 * @USER: 刘梦祺
 * @DATE: 2020/5/11 20:20
 */
@Data
public class SeedVo {
    /**
     * 解锁金额
     */
    private Integer unlockmoeny;
    /**
     * 种子等级
     */
    private Integer grade;
    /**
     * 用户id
     */
    private long userId;
    /**
     * 种子id
     */
    private Integer seedid;
    /**
     * 当前解锁进度
     */
    private Integer seedPresentUnlock;
}
