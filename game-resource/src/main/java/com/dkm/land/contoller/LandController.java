package com.dkm.land.contoller;

import com.dkm.constanct.CodeType;
import com.dkm.exception.ApplicationException;
import com.dkm.jwt.islogin.CheckToken;
import com.dkm.land.entity.Land;
import com.dkm.land.entity.vo.Message;
import com.dkm.land.entity.vo.UserLandUnlock;
import com.dkm.land.service.ILandService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 刘梦祺
 * @PROJECT_NAME: game_project
 * @DESCRIPTION:
 * @USER: 刘梦祺
 * @DATE: 2020/5/9 9:40
 */
@Api(tags = "土地Api")
@RestController
@RequestMapping("/Land")
public class LandController {
    @Resource
    private ILandService iLandService;

    @PostMapping("/insertLand")
    public Message insertLand(@RequestBody List<Land> list){
        if(list.size()==0){
            throw new ApplicationException(CodeType.PARAMETER_ERROR, "参数不能为空");
        }
        iLandService.insertLand(list);
        Message message=new Message();
        message.setMsg("成功");
        return  message;
    }

    @ApiOperation(value = "查询所有土地", notes = "如果查询成功返回list集合，失败返回为null")
    @GetMapping("/queryUserByIdLand")
    @CrossOrigin
    @CheckToken
    public List<UserLandUnlock> queryUserByIdLand() {
        return iLandService.queryUserByIdLand();
    }


    @ApiOperation(value = "根据用户id和土地编号解锁土地", notes = "根据用户id和土地编号解锁土地")
    @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "laNo", value = "土地编号")
    @GetMapping("/updateLandStatus")
    @CrossOrigin
    @CheckToken
    public void updateLandStatus(@RequestParam(value = "laNo") Integer laNo){
        if(laNo==null){
            throw new ApplicationException(CodeType.PARAMETER_ERROR,"土地编号不能为空");
        }
        iLandService.updateLandStatus(laNo);
    }
}