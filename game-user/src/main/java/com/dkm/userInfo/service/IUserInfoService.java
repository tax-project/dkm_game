package com.dkm.userInfo.service;

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
    * 增加用户金币数
    * @param goldNumber 金币数
    */
   void increaseUserInfoGold(Double goldNumber);
}