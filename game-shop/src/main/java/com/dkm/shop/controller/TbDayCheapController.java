package com.dkm.shop.controller;



import com.dkm.jwt.islogin.CheckToken;
import com.dkm.shop.domain.TbDayCheap;
import com.dkm.shop.service.TbDayCheapService;
import com.dkm.shop.utils.Message;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 每日特惠表 前端控制器
 * </p>
 *
 * @author zy
 * @since 2020-05-09
 */
@RestController
@RequestMapping("/dkm/tbDayCheap")
@Api(description = "游戏项目每日特惠表接口文档")
public class TbDayCheapController {

    @Resource
    TbDayCheapService tbDayCheapService;
    /**
     * 查询所有的每日特惠
     * @param
     */
    @ApiOperation(value = "查询所有的每日特惠",notes = "如果查询成功返回每日特惠的json,没有则返回空")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "cheapId",value = "特惠主键"),
            @ApiImplicitParam(paramType = "query",dataType = "String",name = "cheapImg",value = "特惠图片"),
            @ApiImplicitParam(paramType = "query",dataType = "String",name = "cheapName",value = "特惠名称"),
            @ApiImplicitParam(paramType = "query",dataType = "String",name = "cheapInfo",value = "特惠内容"),
            @ApiImplicitParam(paramType = "query",dataType = "BigDecimal",name = "cheapMoney",value = "特惠价格"),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "foodId",value = "蜂蜜id"),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "foodFm",value = "蜂蜜数量"),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "cheapDm",value = "钻石数量"),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "cheapGold",value = "金币数量"),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "lanId",value = "蓝卷id"),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "lanNum",value = "蓝卷数量"),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "yuId",value = "鱼的id"),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "yuNum",value = "鱼的数量"),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "sdId",value = "闪电的id"),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "sdNum",value = "闪电的的数量"),
    })
    @ApiResponses({
            @ApiResponse(code = 401,message="没有权限"),
            @ApiResponse(code = 403,message = "服务器拒绝请求"),
            @ApiResponse(code = 404,message="请求路径没有或页面跳转路径不对"),
            @ApiResponse(code = 500,message="后台报错"),
            @ApiResponse(code = 200,message="返回成功")
    })
    @GetMapping("/selectAll")
    @CrossOrigin
    @CheckToken
    public List<TbDayCheap> selectAll(){
        List<TbDayCheap> list=tbDayCheapService.selectAll();
        return list;
    }

    /**
     * 每日特惠的增加接口
     * @param tbDayCheap
     */
    @ApiOperation(value = "每日特惠的增加接口",notes = "成功则返回操作成功")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "cheapId",value = "特惠主键"),
            @ApiImplicitParam(paramType = "query",dataType = "String",name = "cheapImg",value = "特惠图片",required = true),
            @ApiImplicitParam(paramType = "query",dataType = "String",name = "cheapName",value = "特惠名称",required = true),
            @ApiImplicitParam(paramType = "query",dataType = "String",name = "cheapInfo",value = "特惠内容",required = true),
            @ApiImplicitParam(paramType = "query",dataType = "BigDecimal",name = "cheapMoney",value = "特惠价格",required = true),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "foodId",value = "蜂蜜id"),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "foodFm",value = "蜂蜜数量"),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "cheapDm",value = "钻石数量",required = true),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "cheapGold",value = "金币数量",required = true),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "lanId",value = "蓝卷id"),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "lanNum",value = "蓝卷数量"),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "yuId",value = "鱼的id"),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "yuNum",value = "鱼的数量"),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "sdId",value = "闪电的id"),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "sdNum",value = "闪电的的数量"),
    })
    @ApiResponses({
            @ApiResponse(code = 401,message="没有权限"),
            @ApiResponse(code = 403,message = "服务器拒绝请求"),
            @ApiResponse(code = 404,message="请求路径没有或页面跳转路径不对"),
            @ApiResponse(code = 500,message="后台报错"),
            @ApiResponse(code = 200,message="返回成功")
    })
    @PostMapping("/addTbDayCheap")
    @CrossOrigin
    @CheckToken
    public Message addTbDayCheap(@RequestBody TbDayCheap tbDayCheap){
        Message errorResult=new Message();
        tbDayCheapService.addTbDayCheap(tbDayCheap);
        errorResult.setNum(1);
        errorResult.setMsg("操作成功");
        return errorResult;
    }
}
