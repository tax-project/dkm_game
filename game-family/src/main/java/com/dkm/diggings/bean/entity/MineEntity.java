package com.dkm.diggings.bean.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author OpenE
 */
@Data
@TableName("tb_diggings_battle_item")
public class MineEntity {
    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 战场 ID
     */
    private long battleId;
    /**
     * 设置矿区归属
     */
    private int belongItem;
    /**
     * 用户 ID
     */
    private long userId;
    /**
     * 家族 ID
     */
    private long familyId;
    /**
     * 矿区等级
     */
    private int itemLevel;
}
