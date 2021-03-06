package com.dkm.feign.fallback;

import com.dkm.constanct.CodeType;
import com.dkm.data.Result;
import com.dkm.entity.bo.ParamBo;
import com.dkm.entity.bo.UserHeardUrlBo;
import com.dkm.entity.bo.UserInfoBo;
import com.dkm.entity.bo.UserInfoQueryBo;
import com.dkm.feign.UserFeignClient;
import com.dkm.feign.entity.IncreaseUserInfoBO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author qf
 * @date 2020/5/13
 * @vesion 1.0
 **/
@Component
public class UserFeignClientFallback implements UserFeignClient {


   @Override
   public Result<UserInfoQueryBo> queryUser(Long id) {
      return Result.fail(CodeType.FEIGN_CONNECT_ERROR);
   }

   @Override
   public Result updateUserInfo(Integer much, Long userId, Integer userInfoDiamonds) {
      return Result.fail(CodeType.FEIGN_CONNECT_ERROR);
   }

   @Override
   public Result<List<UserHeardUrlBo>> queryAllHeardByUserId(ParamBo bo) {
      return Result.fail(CodeType.FEIGN_CONNECT_ERROR);
   }

   @Override
   public Result increaseUserInfo(IncreaseUserInfoBO increaseUserInfoBO) {
      return Result.fail(CodeType.FEIGN_CONNECT_ERROR);
   }
}
