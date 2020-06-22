package com.dkm.apparel.controller;

import com.dkm.apparel.entity.ApparelOrderEntity;
import com.dkm.apparel.entity.vo.ApparelMarketDetailVo;
import com.dkm.apparel.entity.vo.ApparelOrderVo;
import com.dkm.apparel.entity.vo.ApparelPutVo;
import com.dkm.apparel.service.IApparelMarketService;
import com.dkm.constanct.CodeType;
import com.dkm.exception.ApplicationException;
import com.dkm.jwt.contain.LocalUser;
import com.dkm.jwt.islogin.CheckToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: game_project
 * @description: 集市
 * @author: zhd
 * @create: 2020-06-19 15:59
 **/
@Api(tags = "服饰集市api")
@RestController
@RequestMapping("/apparelMarket")
public class ApparelMarketController {

    @Resource
    private IApparelMarketService apparelMarketService;

    @Resource
    private LocalUser localUser;

    @ApiOperation("上架服饰")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "apparelId",name="服饰id",paramType = "path",dataType = "Long",required = true),
            @ApiImplicitParam(value = "gold",name="金额",paramType = "path",dataType = "int",required = true)
    })
    @CrossOrigin
    @CheckToken
    @PostMapping("/putOnSell")
    public void putOnSell(@RequestBody ApparelPutVo apparelPutVo){
        if(apparelPutVo==null||apparelPutVo.getApparelId()==null||apparelPutVo.getGold()==null||apparelPutVo.getGold()<=0){
            throw new ApplicationException(CodeType.PARAMETER_ERROR);
        }
        apparelMarketService.putOnSell(localUser.getUser().getId(),apparelPutVo);
    }

    @ApiOperation("下架服饰")
    @ApiImplicitParam(value = "apparelMarketId",name="服饰上架id",paramType = "path",dataType = "Long",required = true)
    @CrossOrigin
    @CheckToken
    @PostMapping("/downApparel")
    public void putOnSell(Long apparelMarketId){
        if(apparelMarketId==null){
            throw new ApplicationException(CodeType.PARAMETER_ERROR);
        }
        apparelMarketService.downApparel(apparelMarketId);
    }

    @ApiOperation("摆摊")
    @ApiImplicitParam(value = "type",name="0全部1上衣2裤子",paramType = "path",dataType = "int",required = true)
    @CrossOrigin
    @CheckToken
    @GetMapping("/ApparelMarket")
    public List<ApparelMarketDetailVo> ApparelMarket(@RequestParam("type") Integer type){
        if(type==null||type>2||type<0)throw new ApplicationException(CodeType.PARAMETER_ERROR);
        return apparelMarketService.apparelMarket(localUser.getUser().getId(),type);
    }

    @ApiOperation("在上架的服饰")
    @CrossOrigin
    @CheckToken
    @GetMapping("/getPuttingApparel")
    public List<ApparelMarketDetailVo> puttingApparel(){
        return apparelMarketService.puttingApparel(localUser.getUser().getId());
    }

    @ApiOperation("交易记录")
    @CrossOrigin
    @CheckToken
    @GetMapping("/getOrders")
    public List<ApparelOrderVo> getOrders(){
        return apparelMarketService.getApparelOrders(localUser.getUser().getId());
    }

}
