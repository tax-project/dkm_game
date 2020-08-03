package com.dkm.admin.controller

import com.dkm.admin.mapper.vo.UserLoginResultVo
import com.dkm.admin.mapper.vo.UserLoginVo
import com.dkm.admin.service.IAdminUserLoginService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource

/**
 * @author fkl
 */
@Api(tags = ["用户登录"])
@RequestMapping("/admin")
@RestController
class AdminUserController {
    @Resource
    private lateinit var userLoginService: IAdminUserLoginService

    @ApiOperation("登录",notes = "输入账号 + SHA1(密码) 来登录.")
    @PostMapping(path = ["/login"], consumes = ["application/json"], produces = ["application/json"])
    fun login(@RequestBody loginVo: UserLoginVo): UserLoginResultVo = run {
        userLoginService.login(loginVo)
    }
}