package com.dkm.feign;


import com.dkm.admin.entities.vo.UserLoginVo;
import com.dkm.data.Result;
import com.dkm.feign.entity.UserLoginStatusVo;
import com.dkm.feign.fallback.AnotherUserFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "user", fallback = AnotherUserFeignClientFallback.class)
public interface AnotherUserFeignClient {

    /**
     * user login feign
     *
     * - userName
     * - password
     *
     * @param userLoginVo UserLoginVo login info
     * @return Result<UserLoginStatusVo> user info result
     */
    @PostMapping("/v1/we/chat/loginUser")
    Result<UserLoginStatusVo> login(@RequestBody UserLoginVo userLoginVo);
 }
