package com.dkm.wechat.controller;

import com.dkm.jwt.islogin.CheckToken;
import com.dkm.wechat.entity.bo.FriendInfoBO;
import com.dkm.wechat.entity.vo.FriendRequestVO;
import com.dkm.wechat.service.IWeChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author jie
 */
@RestController
@RequestMapping("/friend/request")
@Api(tags = "查询添加好友信息")
public class FriendRequestController {
    @Autowired
    private IWeChatService weChatService;

    @PostMapping("/info")
    @ApiOperation(value = "info",notes = "根据用户ID查询信息")
    @ApiImplicitParam(name = "Token",value = "Token",required = true,dataType = "String",paramType = "header")
    @CheckToken
    @CrossOrigin
    public FriendInfoBO friendRequest(@RequestBody FriendRequestVO friendRequestVO){
        return weChatService.friendRequest(friendRequestVO.getId());

    }
}