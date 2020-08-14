package com.dkm.skill.controllers;

import com.dkm.config.annotations.CheckAdminPermission;
import com.dkm.skill.entities.vo.SkillVo;
import com.dkm.skill.services.ISkillService;
import com.dkm.utils.bean.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(
        tags = {"技能相关"}
)
@RestController
@RequestMapping({"/config/skill"})
public class SkillController {
    @Resource
    private ISkillService skinService;

    @CheckAdminPermission
    @ApiImplicitParam(
            paramType = "header",
            name = "TOKEN",
            required = true,
            dataType = "String",
            value = "请求的Token"
    )
    @ApiOperation("获取所有技能")
    @GetMapping(
            produces = {"application/json"},
            value = {"/getAllSkins"}
    )
    @NotNull
    public List<SkillVo> getAllProblems() {

        return skinService.getAll();
    }

    @CheckAdminPermission
    @ApiImplicitParam(
            paramType = "header",
            name = "TOKEN",
            required = true,
            dataType = "String",
            value = "请求的Token"
    )
    @ApiOperation("添加一個技能")
    @PostMapping(
            produces = {"application/json"},
            value = {"/add"}
    )
    public ResultVo insertProblem(@RequestBody @NotNull SkillVo skillVo) {
        return skinService.insert(skillVo);
    }

    @CheckAdminPermission
    @ApiImplicitParam(
            paramType = "header",
            name = "TOKEN",
            required = true,
            dataType = "String",
            value = "请求的Token"
    )
    @ApiOperation("更新一个技能")
    @PostMapping(
            produces = {"application/json"},
            value = {"/{id}/update"}
    )
    @NotNull
    public ResultVo updateProblem(@RequestBody @NotNull SkillVo skillVo, @PathVariable long id) {
        return skinService.update(id, skillVo);
    }

    @CheckAdminPermission
    @ApiImplicitParam(
            paramType = "header",
            name = "TOKEN",
            required = true,
            dataType = "String",
            value = "请求的Token"
    )
    @ApiOperation("删除一个技能")
    @GetMapping(
            produces = {"application/json"},
            value = {"/{id}/delete"}
    )
    @NotNull
    public ResultVo deleteProblem(@PathVariable long id) {

        return skinService.delete(id);
    }
}
