package com.dkm.plunder.controller;

import com.dkm.constanct.CodeType;
import com.dkm.exception.ApplicationException;
import com.dkm.good.entity.vo.GoodQueryVo;
import com.dkm.jwt.islogin.CheckToken;
import com.dkm.plunder.entity.bo.PlunderBO;
import com.dkm.plunder.entity.vo.PlunderVo;
import com.dkm.plunder.service.IPlunderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author qf
 * @date 2020/5/19
 * @vesion 1.0
 **/
@Api(tags = "掠夺API")
@RestController
@RequestMapping("/v1/plunder")
public class PlunderController {

   @Autowired
   private IPlunderService plunderService;

   @ApiOperation(value = "增加掠夺表", notes = "增加掠夺表")
   @ApiImplicitParams({
         @ApiImplicitParam(name = "userId", value = "被抢人的用户id", required = true, dataType = "Long", paramType = "path"),
         @ApiImplicitParam(name = "goodsIdList", value = "物品id数组", required = true, dataType = "Long", paramType = "path"),
         @ApiImplicitParam(name = "grade", value = "被抢人的等级", required = true, dataType = "int", paramType = "path")
   })
   @PostMapping("/insertPlunder")
   @CrossOrigin
   @CheckToken
   public void insertPlunder (@RequestBody PlunderVo vo) {
      if (vo.getGoodsIdList().size() == 0 || vo.getUserId() == null || vo.getGrade() == null) {
         throw new ApplicationException(CodeType.PARAMETER_ERROR, "参数不能为空");
      }

      plunderService.insertPlunder(vo);
   }


   @ApiOperation(value = "展示掠夺物品的列表", notes = "展示掠夺物品的列表")
   @GetMapping("/queryPlunderList")
   @CrossOrigin
   @CheckToken
   public Map<String,Object> queryPlunderList () {
      return plunderService.queryPlunderList();
   }




   @ApiOperation(value = "根据被抢人的用户Id查询所有物品", notes = "根据被抢人的用户Id查询所有物品")
   @GetMapping("/getGoodByUserId")
   @CrossOrigin
   @CheckToken
   public PlunderBO getGoodByUserId (@RequestParam("UserId") Long userId) {
      return plunderService.getGoodByUserId(userId);
   }

}
