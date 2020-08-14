package com.dkm.wechat.service;


import com.dkm.entity.bo.UserHeardUrlBo;
import com.dkm.entity.bo.UserInfoBo;
import com.dkm.entity.bo.UserInfoQueryBo;
import com.dkm.entity.vo.UserInfoAttVo;
import com.dkm.wechat.entity.User;
import com.dkm.wechat.entity.bo.FriendInfoBO;
import com.dkm.wechat.entity.bo.UserDataBO;
import com.dkm.wechat.entity.bo.UserPartBO;
import com.dkm.wechat.entity.vo.FriendRequestVO;
import com.dkm.wechat.entity.vo.UserChatInfoVo;
import com.dkm.wechat.entity.vo.UserLoginVo;
import com.dkm.wechat.entity.vo.UserRegisterVo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @Author: HuangJie
 * @Date: 2020/5/8 16:37
 * @Version: 1.0V
 */
public interface IWeChatService {
    /**
     * 微信登录
     * @param code 登录code
     * @return 用户信息和token
     */
    Map<String,Object> weChatLoginUserInfo(String code);

    /**
     * qf
     * 注册
     * @param vo 注册的参数
     */
    void userRegister (UserRegisterVo vo);

    /**
     *  qf
     *  登录
     * @param vo 登录的参数
     * @return token以及用户信息
     */
    Map<String,Object> userLogin (UserLoginVo vo);

    /**
     *  查询所有用户信息
     * @param id 用户id
     * @return 用户所有信息
     */
    UserInfoQueryBo queryUser (Long id);

    /**
     *  根据用户查询所有头像
     * @param list id集合
     * @return 返回数据集合
     */
    List<UserHeardUrlBo> queryAllHeardByUserId (List<Long> list);

    /**
     *  修改用户信息资料
     * @param userDataBO 修改的参数
     */
    void updateUserData (UserDataBO userDataBO);

    /**
     *  根据用户名或者昵称查询好友
     * @param userName
     * @return
     */
    List<User> queryUserByName (String userName);

    /**
     *  查询个人信息
     * @return 返回二维码等其他信息
     */
    UserChatInfoVo queryUserQrInfo ();

    /**
     * 根据用户ID集合查询用户信息
     * @param userIdList 用户ID集合
     * @return 用户信息集合
     */
    List<UserPartBO> queryUserPartAll(List<Long> userIdList);


    /**
     * 查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    FriendInfoBO friendRequest(Long userId);
}
