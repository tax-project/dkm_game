package com.dkm.seed.entity.vo;

import lombok.Data;

/**
 * @author MQ
 * @PROJECT_NAME: game-base
 * @DESCRIPTION:
 * @DATE: 2020/8/14 10:44
 */
@Data
public class UserValueVo {

    /**
     * 经验差值
     */
    private Long difference;

    /**
     * 当前等级
     */
    private Integer currentLevel;

    /**
     * 下一级等级
     */
    private Integer nextLevel;

    /**
     * 当前经验值
     */
    private Long currentExperience;
    /**
     * 下一级经验值
     */
    private Long nextLevelExperienceValue;

}
