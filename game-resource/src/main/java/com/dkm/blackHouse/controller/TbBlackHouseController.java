package com.dkm.blackHouse.controller;


import com.dkm.blackHouse.domain.TbBlackHouse;
import com.dkm.blackHouse.domain.vo.TbBlackHouseVo;
import com.dkm.blackHouse.service.TbBlackHouseService;
import com.dkm.constanct.CodeType;
import com.dkm.exception.ApplicationException;
import com.dkm.jwt.contain.LocalUser;
import com.dkm.jwt.entity.UserLoginQuery;
import com.dkm.jwt.islogin.CheckToken;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 黑屋表 前端控制器
 * </p>
 *
 * @author zy
 * @since 2020-05-10
 */
@RestController
@RequestMapping("/dkm/tbBlackHouse")
@Api(description = "游戏项目黑屋的接口文档")
public class TbBlackHouseController {

    @Resource
    TbBlackHouseService tbBlackHouseService;
    @Autowired
    private LocalUser localUser;
    /**
     * 查询自己是的小黑屋是否关了人
     * @return
     */
    @ApiOperation(value = "查询当前用户小黑屋是否关了人进去 ",notes = "如果查询返回结果为0则代表没关人 为1则代表小黑屋有人被关")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "cheapId",value = "黑屋主键"),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "fromId",value = "关人id"),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "toId",value = "被关人id"),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "isBlack",value = "是否释放"),
            @ApiImplicitParam(paramType = "query",dataType = "Date",name = "time",value = "被关时间"),
            @ApiImplicitParam(paramType = "query",dataType = "String",name = "timeone",value = "被关时间后十五分钟的时间")
    })
    @ApiResponses({
            @ApiResponse(code = 401,message="没有权限"),
            @ApiResponse(code = 403,message = "服务器拒绝请求"),
            @ApiResponse(code = 404,message="请求路径没有或页面跳转路径不对"),
            @ApiResponse(code = 500,message="后台报错"),
            @ApiResponse(code = 200,message="返回成功")
    })
    @GetMapping("/selectIsBlack")
    @CrossOrigin
    @CheckToken
    public int selectIsBlack(){
        UserLoginQuery user = localUser.getUser();
        int rows=tbBlackHouseService.selectIsBlack(user.getId());
        if(rows>0){
            return rows;
        }else{
            return rows;
        }
    }

    /**
     * 抓人进小黑屋的接口
     */
    @ApiOperation(value = "抓人进小黑屋的接口",notes = "成功则返回操作成功!")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "fromId",value = "关人id"),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "toId",value = "被关人id",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 401,message="没有权限"),
            @ApiResponse(code = 403,message = "服务器拒绝请求"),
            @ApiResponse(code = 404,message="请求路径没有或页面跳转路径不对"),
            @ApiResponse(code = 500,message="后台报错"),
            @ApiResponse(code = 200,message="返回成功")
    })
    @GetMapping("/addBlackHouse/{toId}")
    @CrossOrigin
    @CheckToken
    public void addBlackHouse(@PathVariable("toId") String toId){
        if(StringUtils.isEmpty(toId)){
            throw new ApplicationException(CodeType.PARAMETER_ERROR, "参数不能为空");
        }
        TbBlackHouse tbBlackHouse=new TbBlackHouse();
        tbBlackHouse.setToId(Long.valueOf(toId));
        tbBlackHouseService.insertLand(tbBlackHouse);
    }

    /**
     * 点击释放按钮的接口
     * @param tbBlackHouse
     * @return
     */
    @ApiOperation(value = "释放人出黑屋的接口",notes = "成功则返回操作成功!")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "fromId",value = "关人id"),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "toId",value = "被关人id",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 401,message="没有权限"),
            @ApiResponse(code = 403,message = "服务器拒绝请求"),
            @ApiResponse(code = 404,message="请求路径没有或页面跳转路径不对"),
            @ApiResponse(code = 500,message="后台报错"),
            @ApiResponse(code = 200,message="返回成功")
    })
    @PostMapping("/updateIsBlack")
    @CrossOrigin
    @CheckToken
    public void updateIsBlack(@RequestBody TbBlackHouse tbBlackHouse){
        if( StringUtils.isEmpty(tbBlackHouse.getToId()) ){
            throw new ApplicationException(CodeType.PARAMETER_ERROR, "参数不能为空");
        }
        tbBlackHouseService.updateIsBlack(tbBlackHouse);
    }

    /**
     * 查询用户小黑屋关的人信息
     * @param
     * @return
     */
    @ApiOperation(value = "查询用户小黑屋关的人信息的接口",notes = "成功则返回信息JSON!")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "fromId",value = "关人id"),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "toId",value = "被关人id"),
            @ApiImplicitParam(paramType = "query",dataType = "String",name = "weChatNickName",value = "微信用户呢称"),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "userInfoGrade",value = "用户等级"),
            @ApiImplicitParam(paramType = "query",dataType = "String",name = "weChatHeadImgUrl",value = "用户图片"),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "userInfoRenown",value = "用户声望值"),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "userId",value = "被关人id"),
            @ApiImplicitParam(paramType = "query",dataType = "Date",name = "time",value = "被关时间"),
            @ApiImplicitParam(paramType = "query",dataType = "String",name = "timeone",value = "被关时间后十五分钟的时间")
    })
    @ApiResponses({
            @ApiResponse(code = 401,message="没有权限"),
            @ApiResponse(code = 403,message = "服务器拒绝请求"),
            @ApiResponse(code = 404,message="请求路径没有或页面跳转路径不对"),
            @ApiResponse(code = 500,message="后台报错"),
            @ApiResponse(code = 200,message="返回成功")
    })
    @PostMapping("/selectIsBlackTwo")
    @CrossOrigin
    @CheckToken
    public TbBlackHouseVo selectIsBlackTwo(){

        //首先根据传过来的登录用户的id查询出被关人的id
        List<TbBlackHouse> selectById=tbBlackHouseService.selectById();
        TbBlackHouse tbBlackHouse=new TbBlackHouse();

        for (TbBlackHouse blackHouse : selectById) {

            if( StringUtils.isEmpty(blackHouse.getToId()) || StringUtils.isEmpty(blackHouse.getFromId()) && blackHouse.getIsBlack()==1 ){
                throw new ApplicationException(CodeType.RESOURCES_NOT_FIND, "当前没有人");
            }
            tbBlackHouse.setToId(blackHouse.getToId());
            tbBlackHouse.setFromId(blackHouse.getFromId());
        }
        //查询出被关黑屋用户的信息
        TbBlackHouseVo list=tbBlackHouseService.selectIsBlackT(tbBlackHouse);
        if( StringUtils.isEmpty(list) ){
            throw new ApplicationException(CodeType.RESOURCES_NOT_FIND, "当前没有人");
        }
        return list;
    }
    @ApiOperation(value = "个人中心专用查询用户小黑屋关的人信息的接口",notes = "成功则返回信息JSON!")
    @GetMapping("/selectIsBlackThree/{userId}")
    @CrossOrigin
    @CheckToken
    public TbBlackHouseVo selectIsBlackThree(@PathVariable("userId") Long userId){

        //查询出被关黑屋用户的信息
        TbBlackHouseVo list=tbBlackHouseService.selectIsBlackTwo(userId);
        if( StringUtils.isEmpty(list) ){
            throw new ApplicationException(CodeType.RESOURCES_NOT_FIND, "该用户的黑屋没人被关");
        }
        return list;
    }

    /**
     * 查询自己有没有被人关
     */
    @ApiOperation(value = "查询自己有没有被人关 通知接口",notes = "自己被关了返回 关自己的人信息  没有被关则code返回1003")
    @GetMapping("/selectMyIsBlack")
    @CrossOrigin
    @CheckToken
    public TbBlackHouseVo selectMyIsBlack(){
        //首先看自己有没有被人关起来
        TbBlackHouse tbBlackHouse=new TbBlackHouse();

        List<TbBlackHouse> selectById=tbBlackHouseService.selectToId();

        for (TbBlackHouse blackHouse : selectById) {
            if( StringUtils.isEmpty(blackHouse.getToId()) || StringUtils.isEmpty(blackHouse.getFromId()) && blackHouse.getIsBlack()==1 ){
                throw new ApplicationException(CodeType.RESOURCES_NOT_FIND, "当前自己没有被关");
            }
            tbBlackHouse.setToId(blackHouse.getToId());
            tbBlackHouse.setFromId(blackHouse.getFromId());
        }
        //查询出关自己的人信息
        TbBlackHouseVo list=tbBlackHouseService.selectIsBlackThree(tbBlackHouse);
        if( StringUtils.isEmpty(list) ){
            throw new ApplicationException(CodeType.RESOURCES_NOT_FIND, "当前没有人");
        }
        return list;
    }
}
