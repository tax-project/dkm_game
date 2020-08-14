package com.dkm.seed.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author MQ
 * @PROJECT_NAME: game-base
 * @DESCRIPTION:
 * @DATE: 2020/8/14 10:39
 */
@Data
public class PropertyValueVo {

    /**
     * 生命值
     */
    private Integer lifeValue;

    /**
     * 才华
     */
    private Integer artisticTalent;

    /**
     * 体力上限
     */
    private Integer maximumPhysicalStrength;

    /**
     * 暴击概率
     */
    private BigDecimal criticalHitProbability;
}
