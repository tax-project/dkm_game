package com.dkm.feign.fallback;

import com.dkm.constanct.CodeType;
import com.dkm.data.Result;
import com.dkm.entity.bo.UserInfoBo;
import com.dkm.feign.UserFeignClient;

/**
 * @author qf
 * @date 2020/5/13
 * @vesion 1.0
 **/
public class UserFeignClientFallback implements UserFeignClient {


   @Override
   public Result<UserInfoBo> queryUser(Long id) {
      return Result.fail(CodeType.SERVICE_ERROR);
   }
}
