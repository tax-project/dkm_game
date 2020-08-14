package com.dkm.admin.services.impl;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dkm.admin.entities.AdminUserEntity;
import com.dkm.admin.entities.vo.UserLoginResultVo;
import com.dkm.admin.entities.vo.UserLoginVo;
import com.dkm.admin.mappers.AdminUserMapper;
import com.dkm.admin.services.IAdminUserLoginService;
import com.dkm.constanct.CodeType;
import com.dkm.exception.ApplicationException;
import com.dkm.feign.AnotherUserFeignClient;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserLoginServiceImpl implements IAdminUserLoginService {
    @Resource
    private AnotherUserFeignClient anotherUserFeignClient;
    @Resource
    private AdminUserMapper adminUserMapper;

    @Override
    public UserLoginResultVo login(UserLoginVo loginVo) {
        val result = new UserLoginResultVo();
        val login = anotherUserFeignClient.login(loginVo);
        log.debug(JSONObject.toJSONString(login));
        if (login.getCode() == 1006) {
            throw new ApplicationException(CodeType.AUTHENTICATION_ERROR, "密码错误");
        }
        if (login.getData() == null) {
            throw new ApplicationException(CodeType.FEIGN_CONNECT_ERROR);
        }
        result.setLoginToken(login.getData().getToken());
        result.setLoginStatus(true);
        return result;
    }

    @Override
    public boolean checkToken(String token) {
        try {
            val id = JWT.decode(token).getClaim("id").asLong();
            val list = adminUserMapper.selectList(new QueryWrapper<AdminUserEntity>().eq("user_id", id));
            return !(list == null || list.isEmpty());
        } catch (Exception e) {
            throw new ApplicationException(CodeType.TOKENNULL_ERROR, "token无效！");
        }
    }
}
