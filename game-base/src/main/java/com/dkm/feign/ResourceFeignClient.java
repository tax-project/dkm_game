package com.dkm.feign;

import com.dkm.data.Result;
import com.dkm.feign.entity.*;
import com.dkm.feign.fallback.ResourceFeignClientFallback;
import com.dkm.housekeeper.entity.vo.TbEquipmentVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @program: game_project
 * @description: 调用resource服务
 * @author: zhd
 * @create: 2020-05-16 15:25
 **/
@FeignClient(value = "resource",fallback = ResourceFeignClientFallback.class)
public interface ResourceFeignClient {

    /**
     * 管家收装备
     * @param boxId
     * @return
     */
    @GetMapping("/dkm/tbBox/selectByBoxIdTwo/{boxId}")
    Result<List<TbEquipmentVo>> selectByBoxIdTwo(@PathVariable("boxId") String boxId);

    /**
     * 查询多个接口 得到个人中心信息
     * @param userId
     * @return
     */
    @GetMapping("/center/PersonalCenterAll")
    Result<Map<String,Object>>  personalCenterAll(@RequestParam("userId") Long userId);

    @GetMapping("/backpack/getFoodsFegin")
    Result<List<FoodInfoVo>> getFoodsFegin(@RequestParam("userId") Long userId);
    /**
     * 收取、种植种子
     * @param seedPlantVo
     * @return
     */
    @PostMapping("/Seed/plants")
    Result plant(@RequestBody SeedPlantVo seedPlantVo);

    @PostMapping("/backpack/addBackpackGoods")
    Result addBackpackGoods(@RequestBody AddGoodsInfo addGoodsInfo);

    /**
     * 查询用户种子
     * @param userId
     * @return
     */
    @GetMapping("/Seed/queryUserIdSeeds")
    Result<List<SeedPlantUnlock>>  queryUserIdSeed(@RequestParam("userId")Long userId);

    /**
     * 根据登录人id查看自己主人
     * @return
     */
    @GetMapping("/Attendant/queryAidMaster")
    Result<Map<String,Object>> queryAidMaster();

    /**
     * 根据用户id查看主人
     * @param userId
     * @return
     */
    @GetMapping("/Attendant/queryUserIdMaster")
    Result<Map<String,Object>> queryUserIdMaster(@RequestParam(value = "userId") Long userId);
}
