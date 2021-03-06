package com.dkm.seed.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 刘梦祺
 * @PROJECT_NAME: game_project
 * @DESCRIPTION:
 * @DATE: 2020/5/30 11:23
 */
@Data
public class LandYesVo {

    /**
     * 主键
     */
    private Long SeedId;

    /**
     * 种子名称
     */
    private String seedName;
    /**
     * 种子等级
     */
    private Integer seedGrade;

    /**
     *种子产出的金币
     */
    private Integer seedProdgold;
    /**
     *首次产出的红包
     */
    private Integer seedProdred;
    /**
     *种子产出的经验
     */
    private Integer seedExperience;
    /**
     *种子种植金币
     */
    private Integer seedGold;
    /**
     * 种子图片
     */
    private String seedImg;
    /**
     * 种子种植结束时间
     */
    private LocalDateTime plantTime;

    private Long time;

    /**
     *是否新种子 （1新种子 0不是）
     */
    private Integer newSeedIs;


}
