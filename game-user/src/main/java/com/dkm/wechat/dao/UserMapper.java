package com.dkm.wechat.dao;

import com.dkm.IBaseMapper.IBaseMapper;
import com.dkm.entity.bo.UserHeardUrlBo;
import com.dkm.entity.bo.UserInfoBo;
import com.dkm.wechat.entity.User;
import com.dkm.wechat.entity.bo.UserBO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: HuangJie
 * @Date: 2020/5/9 10:05
 * @Version: 1.0V
 */
@Component
public interface UserMapper extends IBaseMapper<User> {

   /**
    * 查询用户信息
    * @param wxOpenId openID
    * @return 返回用户信息
    */
   UserBO queryUserInfo (String wxOpenId);

   /**
    *  查询所有用户信息
    * @param id 用户id
    * @return 用户所有信息
    */
   UserInfoBo queryUser (Long id);

   /**
    *  根据用户查询所有头像
    * @param list
    * @return
    */
   List<UserHeardUrlBo> queryAllHeardByUserId(List<Long> list);
}
