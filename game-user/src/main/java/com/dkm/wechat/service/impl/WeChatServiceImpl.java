package com.dkm.wechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dkm.config.RedisConfig;
import com.dkm.constanct.CodeType;
import com.dkm.data.Result;
import com.dkm.entity.bo.UserHeardUrlBo;
import com.dkm.entity.bo.UserInfoBo;
import com.dkm.entity.bo.UserInfoQueryBo;
import com.dkm.entity.vo.FileVo;
import com.dkm.exception.ApplicationException;
import com.dkm.feign.FileFeignClient;
import com.dkm.jwt.contain.LocalUser;
import com.dkm.jwt.entity.UserLoginQuery;
import com.dkm.userInfo.service.IUserInfoService;
import com.dkm.utils.DateUtils;
import com.dkm.utils.IdGenerator;
import com.dkm.utils.ShaUtils;
import com.dkm.utils.StringUtils;
import com.dkm.wechat.dao.UserMapper;
import com.dkm.wechat.entity.User;
import com.dkm.wechat.entity.bo.FriendInfoBO;
import com.dkm.wechat.entity.bo.UserBO;
import com.dkm.wechat.entity.bo.UserDataBO;
import com.dkm.wechat.entity.bo.UserPartBO;
import com.dkm.wechat.entity.vo.UserChatInfoVo;
import com.dkm.wechat.entity.vo.UserLoginVo;
import com.dkm.wechat.entity.vo.UserRegisterVo;
import com.dkm.wechat.service.IWeChatService;
import com.dkm.wechat.util.CreateToken;
import com.dkm.wechat.util.WeChatUtil;
import com.dkm.wechat.util.bo.WeChatUtilBO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: HuangJie
 * @Date: 2020/5/8 16:37
 * @Version: 1.0V
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WeChatServiceImpl extends ServiceImpl<UserMapper,User> implements IWeChatService  {

    @Autowired
    private WeChatUtil weChatUtil;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private CreateToken createToken;

    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private RedisConfig redisConfig;

    @Autowired
    private LocalUser localUser;

    @Value("${file.qrCodeUrl}")
    private String qrCodeUrl;

    @Autowired
    private FileFeignClient fileFeignClient;

    @Override
    public Map<String, Object> weChatLoginUserInfo(String code) {
        UserBO resultBO = new UserBO();
        try {
            WeChatUtilBO weChatUtilBO = weChatUtil.codeToUserInfo(code);
            User user = new User();
            BeanUtils.copyProperties(weChatUtilBO, user);
            //查询用户是否存在
            UserBO userBO = baseMapper.queryUserInfo(weChatUtilBO.getWeChatOpenId());
            if (userBO == null) {
                //用户不存在，创建
                long userId = idGenerator.getNumberId();
                user.setUserIsEffective(0);
                user.setUserId(userId);
                //默认初始年龄
                LocalDate localDate = LocalDate.now().minusYears(20);
                user.setUserAge(localDate);
                if ("1".equals(weChatUtilBO.getWeChatSex())) {
                    //男
                    user.setUserSex(1);
                } else {
                    //女
                    user.setUserSex(2);
                }

                //地区
                String address = weChatUtilBO.getWeChatCountry() + weChatUtilBO.getWeChatProvince() + weChatUtilBO.getWeChatCity();

                user.setUserAddress(address);

                //生成属于自己的二维码
                Result<FileVo> qrCode = fileFeignClient.getQrCode(qrCodeUrl + "?userId=" + userId);

                if (qrCode.getCode() != 0) {
                    throw new ApplicationException(CodeType.FEIGN_CONNECT_ERROR, "文件feign出错");
                }
                user.setQrCode(qrCode.getData().getFileUrl());

                int insert = baseMapper.insert(user);
                if (insert == 0) {
                    throw new ApplicationException(CodeType.SERVICE_ERROR, "用户创建登录失败，请重新登录");
                }

                //同时初始化用户详情信息
                userInfoService.insertUserInfo(userId);
            } else {
                //用户存在更新
                LambdaQueryWrapper<User> updateWrapper = new LambdaQueryWrapper<>();
                updateWrapper.eq(User::getUserId, userBO.getUserId());
                int update = baseMapper.update(user, updateWrapper);
                if (update == 0) {
                    throw new ApplicationException(CodeType.SERVICE_ERROR, "用户更新登录失败，请重新登录");
                }
                BeanUtils.copyProperties(userBO, resultBO);
            }
                //拷贝新的微信信息，如果是创建用户的话还有新的ID
                BeanUtils.copyProperties(user, resultBO);

                //将设备Id和用户Id存入redis中
                String cid = idGenerator.getUuid();
                redisConfig.setString(resultBO.getUserId(),cid);
                //生成Token
                String token = createToken.getToken(resultBO);
                HashMap<String, Object> resultMap = new HashMap<>(3);
                resultMap.put("token", token);
                resultMap.put("resultUser", resultBO);
                resultMap.put("cid",cid);
                return resultMap;
            } catch(IOException e){
                throw new ApplicationException(CodeType.SERVICE_ERROR, "网络请求错误");
            }
        }


    /**
     *  注册 qf
     * @param vo 注册的参数
     */
    @Override
    public synchronized void userRegister(UserRegisterVo vo) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
              .eq(User::getWeChatOpenId,vo.getUserName());

        User user = baseMapper.selectOne(wrapper);

        if (user != null) {
            throw new ApplicationException(CodeType.SERVICE_ERROR, "该账号已被注册");
        }

        //注册
        User user1 = new User();
        long id = idGenerator.getNumberId();
        user1.setUserId(id);
        user1.setUserIsEffective(0);
        user1.setUserSex(1);
        user1.setUserAddress("中国湖南娄底");
        user1.setWeChatOpenId(vo.getUserName());
        user1.setWeChatNickName(vo.getNickName());
        user1.setUserRemark(ShaUtils.getSha1(vo.getPassword()));
        //默认初始年龄
        LocalDate localDate = LocalDate.now().minusYears(20);
        user1.setUserAge(localDate);

        //生成属于自己的二维码
        Result<FileVo> qrCode = fileFeignClient.getQrCode(qrCodeUrl + "?userId=" + id);

        if (qrCode.getCode() != 0) {
            throw new ApplicationException(CodeType.FEIGN_CONNECT_ERROR, "文件feign出错");
        }

        user1.setQrCode(qrCode.getData().getFileUrl());

        int insert = baseMapper.insert(user1);

        if (insert <= 0) {
            throw new ApplicationException(CodeType.SERVICE_ERROR, "注册失败");
        }

        //同时初始化用户详情信息
        userInfoService.insertUserInfo(id);
    }

    /**
     * 登录
     * @param vo 登录的参数
     * @return qf
     */
    @Override
    public Map<String, Object> userLogin(UserLoginVo vo) {

        UserBO userBO = baseMapper.queryUserInfo(vo.getUserName());

        if (userBO == null) {
            throw new ApplicationException(CodeType.SERVICE_ERROR, "该账号不存在");
        }

        if (!ShaUtils.getSha1(vo.getPassword()).equals(userBO.getUserRemark())) {
            throw new ApplicationException(CodeType.SERVICE_ERROR, "密码错误");
        }

        //查询用户是否有未在线消息

        //将设备Id和用户Id存入redis中
        String cid = idGenerator.getUuid();
        redisConfig.setString(userBO.getUserId(),cid);

        //生成Token
        String token = createToken.getToken(userBO);
        HashMap<String, Object> resultMap = new HashMap<>(3);
        resultMap.put("token",token);
        resultMap.put("resultUser",userBO);
        resultMap.put("cid",cid);
        return resultMap;
    }

    /**
     * qf
     *  查询所有用户信息
     * @param id 用户id
     * @return 用户所有信息
     */
    @Override
    public UserInfoQueryBo queryUser(Long id) {
        UserInfoBo bo = baseMapper.queryUser(id);
        UserInfoQueryBo result = new UserInfoQueryBo();
        if (bo == null) {
            return null;
        }
        BeanUtils.copyProperties(bo,result);
        if (bo.getUserInfoEnvelopeTime() != null) {
            result.setUserInfoEnvelopeQueryTime(DateUtils.formatDate(bo.getUserInfoEnvelopeTime()));
        }

        //算出年龄
        if (bo.getUserAge() != null) {
            LocalDate now = LocalDate.now();

            Long until = bo.getUserAge().until(now, ChronoUnit.YEARS);
            result.setAge(until);
        }

        return result;
    }

    /**
     * 查询用户头像
     * @param list
     * @return
     */
    @Override
    public List<UserHeardUrlBo> queryAllHeardByUserId(List<Long> list) {

        if (null == list || list.size() == 0) {
            return null;
        }

        return baseMapper.queryAllHeardByUserId (list);
    }


    /**
     *  修改资料
     * @param userDataBO 修改的参数
     */
    @Override
    public void updateUserData(UserDataBO userDataBO) {

        UserLoginQuery userLoginQuery = localUser.getUser();

        User user = new User();

        user.setUserId(userLoginQuery.getId());
        user.setUserAge(DateUtils.parseDate(userDataBO.getUserAge()));
        if (userDataBO.getUserSex() != 1 && userDataBO.getUserSex() != 2) {
            throw new ApplicationException(CodeType.SERVICE_ERROR, "修改的性别参数有误");
        }
        if (userDataBO.getUserSex() != null) {
            user.setUserSex(userDataBO.getUserSex());
        }
        user.setWeChatNickName(userDataBO.getNickName());
        user.setWeChatHeadImgUrl(userDataBO.getHeardUrl());
        if (StringUtils.isNotBlank(userDataBO.getUserSign())) {
            user.setUserSign(userDataBO.getUserSign());
        }
        if (StringUtils.isNotBlank(userDataBO.getUserExplain())) {
            user.setUserExplain(userDataBO.getUserExplain());
        }

        int updateById = baseMapper.updateById(user);

        if (updateById <= 0) {
            throw new ApplicationException(CodeType.SERVICE_ERROR, "保存失败..");
        }
    }

    @Override
    public List<User> queryUserByName(String userName) {

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
              .eq(User::getWeChatOpenId,userName)
              .or()
              .eq(User::getWeChatNickName,userName);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public UserChatInfoVo queryUserQrInfo() {
        UserLoginQuery user = localUser.getUser();

        User user1 = baseMapper.selectById(user.getId());

        UserChatInfoVo vo = new UserChatInfoVo();
        BeanUtils.copyProperties(user1, vo);

        return vo;
    }

    @Override
    public List<UserPartBO> queryUserPartAll(List<Long> userIdList) {
        return baseMapper.queryUserPartAll(userIdList);
    }

    @Override
    public FriendInfoBO friendRequest(Long userId) {
        FriendInfoBO friendInfoBo = baseMapper.friendRequest(userId);
        if (friendInfoBo==null){
            throw new ApplicationException(CodeType.SERVICE_ERROR, "查无此人");
        }
        return friendInfoBo;
    }
}
