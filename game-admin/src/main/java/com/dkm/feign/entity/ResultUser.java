package com.dkm.feign.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public final class ResultUser {
   @Nullable
   private  Integer userGold;
   @Nullable
   private  Integer userGrade;
   @Nullable
   private  String userId;
   @Nullable
   private  Object userIdCard;
   @Nullable
   private  Integer userIsEffective;
   @Nullable
   private  Integer userIsVip;
   @Nullable
   private  Integer userPacketBalance;
   @Nullable
   private  Object userPhone;
   @Nullable
   private  String userRemark;
   @Nullable
   private  Integer userRenown;
   @Nullable
   private  Object weChatHeadImgUrl;
   @Nullable
   private  String weChatNickName;
   @Nullable
   private  String weChatOpenId;

}