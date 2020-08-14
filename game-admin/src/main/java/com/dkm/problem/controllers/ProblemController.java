package com.dkm.problem.controllers;

import com.dkm.config.annotations.CheckAdminPermission;
import com.dkm.problem.entities.vo.ProblemVo;
import com.dkm.problem.services.IProblemService;
import com.dkm.utils.bean.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

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
        tags = {"问答相关"}
)
@RestController
@RequestMapping({"/config/problem"})
public class ProblemController {
    @Resource
    private IProblemService problemService;

    @ApiImplicitParam(
            paramType = "header",
            name = "TOKEN",
            required = true,
            dataType = "String",
            value = "请求的Token"
    )
    @CheckAdminPermission
    @ApiOperation("获取所有问答")
    @GetMapping(
            produces = {"application/json"},
            value = {"/getAllProblem"}
    )
    @NotNull
    public List<ProblemVo> getAllProblems() {
        return problemService.getAllProblems();
    }

    @CheckAdminPermission
    @ApiImplicitParam(
            paramType = "header",
            name = "TOKEN",
            required = true,
            dataType = "String",
            value = "请求的Token"
    )
    @ApiOperation("添加一個问答")
    @PostMapping(
            produces = {"application/json"},
            value = {"/add"}
    )
    @NotNull
    public ResultVo insertProblem(@RequestBody @NotNull ProblemVo problemVo) {
        return problemService.insert(problemVo);
    }

    @CheckAdminPermission
    @ApiImplicitParam(
            paramType = "header",
            name = "TOKEN",
            required = true,
            dataType = "String",
            value = "请求的Token"
    )
    @ApiOperation("更新一个问答")
    @PostMapping(
            produces = {"application/json"},
            value = {"/{id}/update"}
    )
    @NotNull
    public ResultVo updateProblem(@RequestBody @NotNull ProblemVo problemVo, @PathVariable long id) {
        return problemService.update(id, problemVo);
    }

    @CheckAdminPermission
    @ApiImplicitParam(
            paramType = "header",
            name = "TOKEN",
            required = true,
            dataType = "String",
            value = "请求的Token"
    )
    @ApiOperation("删除一个问答")
    @GetMapping(
            produces = {"application/json"},
            value = {"/{id}/delete"}
    )
    @NotNull
    public ResultVo deleteProblem(@PathVariable long id) {
        return problemService.delete(id);
    }
}
