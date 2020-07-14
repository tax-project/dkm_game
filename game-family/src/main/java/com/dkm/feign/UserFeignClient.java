package com.dkm.feign;

import com.dkm.data.Result;
import com.dkm.diggings.bean.vo.RenownVo;
import com.dkm.diggings.bean.vo.UserInfoBO;
import com.dkm.feign.fallback.UserFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "user", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {


    @PostMapping("/v1/userInfo/increase")
    Result<String> update(UserInfoBO userInfoBO);

    @GetMapping("/v1/userInfo/query/user/section")
    Result<RenownVo> queryUserSection(@RequestParam(value = "userId") long userId);

    @GetMapping("/v1/we/chat/queryUser/{id}")
    Result<RenownVo> queryUser(@PathVariable long id);

}