package com.dkm.entity.vo;

import lombok.Data;

/**
 * @author qf
 * @date 2020/6/5
 * @vesion 1.0
 **/
@Data
public class AttendantWithUserVo {

   /**
    * 用户id
    */
   private long userId;
   /**
    *用户声望
    */
   private Integer userInfoRenown;
   /**
    *用户名称
    */
   private String weChatNickName;
   /**
    *用户头像
    */
   private String weChatHeadImgUrl;
   /**
    *用户金币
    */
   private Integer userInfoGold;
}
