package com.dkm.updateImage.controllers;

import com.dkm.config.annotations.CheckAdminPermission;
import com.dkm.updateImage.entities.ImageUpdateResultVo;
import com.dkm.updateImage.services.IPictureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api("上传文件相关")
@RestController("/image/")

public class PictureController {
    @Resource
    private IPictureService pictureService;

    @ApiOperation("上传图片")
    @ApiImplicitParams({@ApiImplicitParam(
            dataType = "String",
            paramType = "header",
            required = true,
            value = "请求的Token",
            name = "TOKEN"
    )})
    @CheckAdminPermission
    @PostMapping(
            consumes = {"multipart/form-data"},
            produces = {"application/json;charset=UTF-8"},
            value = {"/update"}
    )
    @NotNull
    public ImageUpdateResultVo update(@RequestPart("file") @NotNull MultipartFile multipartFile, @NotNull HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("TOKEN");
        return pictureService.update(multipartFile, token);
    }
}
