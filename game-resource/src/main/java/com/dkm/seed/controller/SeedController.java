package com.dkm.seed.controller;

import com.dkm.constanct.CodeType;
import com.dkm.data.Result;
import com.dkm.entity.bo.UserInfoQueryBo;
import com.dkm.exception.ApplicationException;
import com.dkm.jwt.contain.LocalUser;
import com.dkm.jwt.islogin.CheckToken;
import com.dkm.land.entity.vo.Message;
import com.dkm.seed.entity.Seed;
import com.dkm.seed.entity.bo.SendCollectBO;
import com.dkm.seed.entity.bo.SendPlantBO;
import com.dkm.seed.entity.vo.*;
import com.dkm.seed.service.ISeedService;
import com.dkm.seed.vilidata.TimeLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.dkm.seed.vilidata.TimeLimit.TackBackLimit;

import java.util.List;
import java.util.Map;

/**
 * @author 刘梦祺
 * @PROJECT_NAME: game_project
 * @DESCRIPTION:
 * @USER: 刘梦祺
 * @DATE: 2020/5/10 11:47
 */
@Api(tags = "种子api")
@Slf4j
@RestController
@RequestMapping("/Seed")
public class SeedController {
    @Autowired
    private ISeedService iSeedService;

    @Autowired
    private LocalUser localUser;


    @ApiOperation(value = "根据用户id得到种子信息", notes = "根据用户id得到种子")
    @GetMapping("/queryUserIdSeed")
    @CrossOrigin
    @CheckToken
    public List<SeedPlantUnlock> queryUserIdSeed() {
        return iSeedService.queryUserIdSeed(localUser.getUser().getId());
    }


    @GetMapping("/queryUserIdSeeds")
    @CrossOrigin
    public List<SeedPlantUnlock> queryUserIdSeed(Long userId) {
        return iSeedService.queryUserIdSeed(userId);
    }


    @ApiOperation(value = "解锁植物碎片", notes = "解锁植物碎片")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "unlockMoney", value = "购买碎片金额"),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "grade", value = "种子等级"),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "seedId", value = "种子id"),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "seedPresentUnlock", value = "当前解锁进度"),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "seedPresentAggregate", value = "当前解锁总进度")
    })
    @PostMapping("/unlockPlant")
    @CrossOrigin
    @CheckToken
    public Message unlockPlant(@RequestBody SeedVo seedVo) {
        if (seedVo.getGrade() == null || seedVo.getSeedId() == null || seedVo.getUnlockMoney() == null || seedVo.getSeedPresentUnlock() == null ||
                seedVo.getSeedPresentAggregate() == null) {
            throw new ApplicationException(CodeType.PARAMETER_ERROR, "参数不能为空");
        }
        return iSeedService.unlockPlant(seedVo);
    }

    @ApiOperation(value = "种植种子", notes = "种植种子")
    @ApiImplicitParams({
          @ApiImplicitParam(name = "seedId", value = "种子id", required = true, dataType = "Long", paramType = "path"),
          @ApiImplicitParam(name = "seedGrade", value = "种子等级", required = true, dataType = "Integer", paramType = "path"),
          @ApiImplicitParam(name = "seedGold", value = "种子种植金币", required = true, dataType = "int", paramType = "path"),
          @ApiImplicitParam(name = "landNumber", value = "种植的个数", required = true, dataType = "int", paramType = "path")
    })
    @PostMapping("/plant")
    @CrossOrigin
    @CheckToken
    public void plant(@RequestBody SendPlantBO sendPlantBO) {
        if(sendPlantBO.getSeedId()==null || sendPlantBO.getSeedGrade()==null
              || sendPlantBO.getSeedGold() == null || sendPlantBO.getLandNumber() == null){
            throw new ApplicationException(CodeType.PARAMETER_ERROR,"参数不能为空");
        }
         iSeedService.queryAlreadyPlantSeed(sendPlantBO);
    }


    @ApiOperation(value = "收取", notes = "收取")
    @ApiImplicitParams({
          @ApiImplicitParam(name = "seedGrade", value = "等级", required = true, dataType = "Integer", paramType = "path"),
          @ApiImplicitParam(name = "status", value = "0--正常收取 1--收取种子", required = true, dataType = "int", paramType = "path"),
          @ApiImplicitParam(name = "seedMeOrOther", value = "(0-我自己收 1--别人抢)", required = false, dataType = "Integer", paramType = "path"),
          @ApiImplicitParam(name = "userId", value = "抢谁的金币-->那个人的用户id", required = false, dataType = "Long", paramType = "path"),
    })
    @PostMapping("/collectSeed")
    @CrossOrigin
    @CheckToken
    public void collectSeed(@RequestBody SendCollectBO sendCollectBO) {
        if(sendCollectBO.getStatus() == null || sendCollectBO.getSeedGrade()==null){
            throw new ApplicationException(CodeType.PARAMETER_ERROR,"参数不能为空");
        }
        iSeedService.collectSeed(sendCollectBO);
    }


    @ApiOperation(value = "查询已经种植的种子", notes = "查询已经种植的种子")
    @GetMapping("/queryAlreadyPlantSd")
    @CrossOrigin
    @CheckToken
    public List<LandYesVo> queryAlreadyPlantSd(){
        return iSeedService.queryAlreadyPlantSd();
    }


    @ApiOperation(value = "根据用户id查询已解锁的种子", notes = "根据用户id查询已解锁的种子")
    @GetMapping("/queryAreUnlocked/{userId}")
    public List<SeedUnlockVo> queryAreUnlocked(@PathVariable("userId") Long userId){
        return iSeedService.queryAreUnlocked(userId);
    }

    @ApiOperation(value = "根据用户id查询用户所有信息(包括主人信息)", notes = "根据用户id查询用户所有信息(包括主人信息)")
    @GetMapping("/queryUserAll")
    @CrossOrigin
    @CheckToken
    public  Map<String,Object> queryUserAll(){
       return iSeedService.queryUserAll();
    }

    @ApiOperation(value = "根据种植id查询种植信息", notes = "根据种植id查询种植信息")
    @GetMapping("/querySeedById")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "seedId", value = "种子id"),
    })
    @CrossOrigin
    @CheckToken
    public SeedDetailsVo querySeedById(@RequestParam("seedId") Long seedId){
        if(seedId==null){
            throw new ApplicationException(CodeType.PARAMETER_ERROR,"参数不能为空");
        }
           return iSeedService.querySeedById(seedId);
    }

    @ApiOperation(value = "批量修改种子状态", notes = "批量修改种子状态")
    @GetMapping("/updateSeedStatus")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "id", value = "种子id"),
    })
    @CrossOrigin
    @CheckToken
    public int updateSeedStatus(@RequestParam("id") List<Long> id){
        if(id==null){
            log.info("参数为空");
            throw new ApplicationException(CodeType.SERVICE_ERROR);
        }
        return iSeedService.updateSeedStatus(id);
    }

    @ApiOperation(value = "查看自己个人经验信息", notes = "查看自己个人经验信息")
    @GetMapping("/personalExperience")
    @CrossOrigin
    @CheckToken
    public Map<String,Object> personalExperience(){
        return iSeedService.personalExperience();
    }
}
