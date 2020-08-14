package com.dkm.goods.controllers;


import com.dkm.config.annotations.CheckAdminPermission;
import com.dkm.goods.entities.vo.GoodsVo;
import com.dkm.goods.services.IGoodService;
import com.dkm.utils.bean.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;
import javax.annotation.Resource;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(
        tags = {"物品相关"}
)
@RestController
@RequestMapping({"/config/goods"})
public class GoodsController {
    @Resource
    private IGoodService iGoodService;

    @CheckAdminPermission
    @ApiImplicitParam(
            paramType = "header",
            name = "TOKEN",
            required = true,
            dataType = "String",
            value = "请求的Token"
    )
    @ApiOperation(
            value = "获取所有物品列表",
            notes = "通过此接口来查询所有的基础物品表"
    )
    @GetMapping(
            produces = {"application/json"},
            value = {"/getAllGoods"}
    )
    @NotNull
    public List<GoodsVo> getAllGoods() {
        return iGoodService.getAllGoods();
    }

    @CheckAdminPermission
    @ApiOperation("添加一个物品")
    @PostMapping(
            consumes = {"application/json"},
            produces = {"application/json"},
            value = {"/add"}
    )
    @NotNull
    public ResultVo addGoods(@RequestBody @NotNull GoodsVo goods) {
        return iGoodService.addGoodsItem(goods);
    }

    @CheckAdminPermission
    @ApiOperation("更新一个物品")
    @PostMapping(
            consumes = {"application/json"},
            produces = {"application/json"},
            value = {"/{id}/update"}
    )
    @NotNull
    public ResultVo updateGoods(@ApiParam("物品ID，仅针对现有的物品") @PathVariable long id, @RequestBody @NotNull GoodsVo goods) {
        return iGoodService.updateItemById(id, goods);
    }

    @CheckAdminPermission
    @ApiOperation("删除一个物品")
    @GetMapping(
            consumes = {"application/json"},
            produces = {"application/json"},
            value = {"/{id}/delete"}
    )
    @NotNull
    public ResultVo deleteGoodsById(@ApiParam("物品ID，仅针对现有的物品") @PathVariable @NotNull String id) {
        return iGoodService.deleteItemById(id);
    }
}
