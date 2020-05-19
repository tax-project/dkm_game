package com.dkm.feign;

import com.dkm.data.Result;
import com.dkm.entity.bo.UserInfoBo;
import com.dkm.entity.bo.UserInfoQueryBo;
import com.dkm.entity.bo.UserPlunderBo;
import com.dkm.feign.fallback.UserFeignClientFallback;
import com.dkm.knapsack.domain.bo.IncreaseUserInfoBO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qf
 * @date 2020/5/13
 * @vesion 1.0
 **/
@FeignClient(value = "service-user", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {

   /**
    *  查询用户所有信息
    * @param id
    * @return
    */
   @GetMapping("/v1/we/chat/queryUser/{id}")
   Result<UserInfoQueryBo> queryUser(@PathVariable("id") Long id);

   /**
    * 修改增加用户声望金币
    * @param increaseUserInfoBO
    * @return
    */
   @PostMapping("/v1/userInfo/increase")
   Result increaseUserInfo(@RequestBody IncreaseUserInfoBO increaseUserInfoBO);

   /**
    * 修改减少用户声望金币
    * @param increaseUserInfoBO
    * @return
    */
   @PostMapping("/v1/userInfo/cut")
   Result cutUserInfo(@RequestBody IncreaseUserInfoBO increaseUserInfoBO);

   /**
    *  随机返回20条用户信息
    * @return 返回用户信息
    */
   @GetMapping("/listUserPlunder")
   Result<List<UserPlunderBo>> listUserPlunder();
}
