package com.dkm.seed.controllers;

import com.dkm.config.annotations.CheckAdminPermission;
import com.dkm.seed.entities.vo.SeedVo;
import com.dkm.seed.services.ISeedService;
import com.dkm.utils.bean.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(
        tags = {"种子相关"}
)
@RestController
@RequestMapping({"/config/seed"})
public class SeedController {
    @Resource
    private ISeedService seedService;

    @CheckAdminPermission
    @ApiImplicitParam(
            paramType = "header",
            name = "TOKEN",
            required = true,
            dataType = "String",
            value = "请求的Token"
    )
    @ApiOperation("获取所有种子")
    @GetMapping(
            produces = {"application/json"},
            value = {"/getAllSeed"}
    )
    @NotNull
    public List<SeedVo> getAllProblems() {
        return seedService.getAll();
    }

    @CheckAdminPermission
    @ApiImplicitParam(
            paramType = "header",
            name = "TOKEN",
            required = true,
            dataType = "String",
            value = "请求的Token"
    )
    @ApiOperation("添加一個种子")
    @PostMapping(
            produces = {"application/json"},
            value = {"/add"}
    )
    @NotNull
    public ResultVo insertProblem(@RequestBody @NotNull SeedVo seedVo) {
        return seedService.insert(seedVo);
    }

    @CheckAdminPermission
    @ApiImplicitParam(
            paramType = "header",
            name = "TOKEN",
            required = true,
            dataType = "String",
            value = "请求的Token"
    )
    @ApiOperation("更新一个种子")
    @PostMapping(
            produces = {"application/json"},
            value = {"/{id}/update"}
    )
    @NotNull
    public ResultVo updateProblem(@RequestBody @NotNull SeedVo seedVo, @PathVariable long id) {
        return seedService.update(id, seedVo);
    }

    @CheckAdminPermission
    @ApiImplicitParam(
            paramType = "header",
            name = "TOKEN",
            required = true,
            dataType = "String",
            value = "请求的Token"
    )
    @ApiOperation("删除一个种子")
    @GetMapping(
            produces = {"application/json"},
            value = {"/{id}/delete"}
    )
    @NotNull
    public ResultVo deleteProblem(@PathVariable long id) {
        return seedService.delete(id);
    }
}
