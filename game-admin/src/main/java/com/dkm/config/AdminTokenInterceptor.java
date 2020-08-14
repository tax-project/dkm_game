package com.dkm.config;

import com.dkm.admin.services.IAdminUserLoginService;
import com.dkm.config.annotations.CheckAdminPermission;
import com.dkm.constanct.CodeType;
import com.dkm.exception.ApplicationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class AdminTokenInterceptor extends HandlerInterceptorAdapter {
    @Resource
    private IAdminUserLoginService userLoginService;

    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        } else {
            Method method = ((HandlerMethod) handler).getMethod();
            if (method.isAnnotationPresent(CheckAdminPermission.class)) {
                String token = request.getHeader("token");
                if (token == null) {
                    token = request.getHeader("TOKEN");
                }
                if (token != null) {

                    if (userLoginService.checkToken(token)) {
                        return true;
                    } else {
                        throw new ApplicationException(CodeType.LOGIN_ERROR, "不是管理员，没有权限！");
                    }
                } else {
                    throw new ApplicationException(CodeType.TOKENNULL_ERROR);
                }
            } else {
                return true;
            }
        }
    }
}
