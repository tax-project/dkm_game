package com.dkm.entity.user;

import lombok.Data;

/**
 * @author qf
 * @date 2020/7/30
 * @vesion 1.0
 **/
@Data
public class SeedCollectVo {

   /**
    * 收取的总金币
    */
   private Integer userGold;
   /**
    * 收取的红包
    */
   private Double userInfoPacketBalance;

   /**
    * 用户id
    */
   private Long userId;

   /**
    * 升级前的总经验（升级后的当前经验）
    */
   private Long userInfoNowExperience;

   /**
    * 下一级需要的经验
    */
   private Long userInfoNextExperience;

   /**
    * 升级等级
    */
   private Integer grade;

   /**
    * 0--收取金币和红包
    * 1--收取经验和种子 不升级
    * 2-- 升级
    */
   private Integer status;
}
