package com.dkm.admin.controllers;


import com.dkm.admin.entities.vo.UserLoginResultVo;
import com.dkm.admin.entities.vo.UserLoginVo;
import com.dkm.admin.services.IAdminUserLoginService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author fkl
 */
@Api(tags = "用户登录")
@RequestMapping("/")
@RestController
public class AdminUserController {
    @Resource
    private IAdminUserLoginService userLoginService;

    public UserLoginResultVo login(@RequestBody UserLoginVo loginVo) {
        return userLoginService.login(loginVo);

    }
}
