package com.dkm.userInfo.service;

import com.dkm.userInfo.entity.bo.IncreaseUserInfoBO;

/**
 * @author qf
 * @date 2020/5/11
 * @vesion 1.0
 **/
public interface IUserInfoService {

   /**
    * 增加用户详细信息
    * 初始化用户信息
    * @param userId
    */
   void insertUserInfo (Long userId);

   /**
    * 修改红包总次数
    * @param much 次数
    * @param userId 用户id
    */
   void updateUserInfo (Integer much, Long userId);

   /**
    * 增加用户金币、钻石、声望
    * @param increaseUserInfoBO 参数对象
    */
   void increaseUserInfo(IncreaseUserInfoBO increaseUserInfoBO);

   /**
    * 减少用户金币、钻石、声望
    * @param increaseUserInfoBO 参数对象
    */
   void cutUserInfo(IncreaseUserInfoBO increaseUserInfoBO);
}