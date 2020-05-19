package com.dkm.turntable.controller;

import com.dkm.jwt.islogin.CheckToken;
import com.dkm.turntable.entity.bo.TurntableItemBO;
import com.dkm.turntable.service.ITurntableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: HuangJie
 * @Date: 2020/5/9 14:42
 * @Version: 1.0V
 */
@Api(tags = "幸运大转盘API")
@RestController
@RequestMapping("v1/turntable")
public class TurntableController {

    @Autowired
    private ITurntableService turntableService;

    @GetMapping("/lucky/draw/items")
    @ApiOperation(value = "转盘物品获取接口",notes = "获得物品信息",produces = "application/json")
    @ApiImplicitParam(name = "token",value = "用户登录token",dataType = "String",paramType = "body",required = true)
    @CheckToken
    @CrossOrigin
    public List<TurntableItemBO> luckyDrawItems(){
        return turntableService.luckyDrawItems();
    }



}
