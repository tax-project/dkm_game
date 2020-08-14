package com.dkm.feign.fallback;

import com.dkm.admin.entities.vo.UserLoginVo;
import com.dkm.constanct.CodeType;
import com.dkm.data.Result;
import com.dkm.feign.AnotherUserFeignClient;
import com.dkm.feign.entity.UserLoginStatusVo;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class AnotherUserFeignClientFallback implements AnotherUserFeignClient {
   @NotNull
   public Result<UserLoginStatusVo> login(@NotNull UserLoginVo userLoginVo) {
      return Result.fail(CodeType.FEIGN_CONNECT_ERROR);
   }
}
