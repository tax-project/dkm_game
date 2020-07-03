package com.dkm.turntable.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dkm.constanct.CodeType;
import com.dkm.data.Result;
import com.dkm.exception.ApplicationException;
import com.dkm.feign.ResourceFeignClient;
import com.dkm.feign.entity.TbEquipmentVoTwo;
import com.dkm.turntable.dao.GoodsMapper;
import com.dkm.turntable.dao.TurntableCouponDao;
import com.dkm.turntable.entity.GoodsEntity;
import com.dkm.turntable.entity.TurntableCouponEntity;
import com.dkm.turntable.entity.vo.AddGoodsInfoVo;
import com.dkm.turntable.entity.vo.TurntableInfoVo;
import com.dkm.turntable.service.ITurntableService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @program: game_project
 * @description: 实现类
 * @author: zhd
 * @create: 2020-06-11 10:05
 **/
@Service
public class TurntableServiceImpl implements ITurntableService {


    @Resource
    private ResourceFeignClient resourceFeignClient;

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private TurntableCouponDao turntableCouponDao;
    @Override
    public List<TurntableInfoVo> getTurntable(Long userId, Integer type) {
        List<GoodsEntity> goodsEntities = goodsMapper.selectList(new LambdaQueryWrapper<GoodsEntity>().in(GoodsEntity::getGoodType,3,1));
        if(goodsEntities==null)throw new ApplicationException(CodeType.SERVICE_ERROR,"不好意思,物品备货中！");
        List<TurntableInfoVo> list = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                int c = new Random().nextInt(goodsEntities.size());
                GoodsEntity a = goodsEntities.get(c);
                list.add(new TurntableInfoVo(type,a.getUrl(),a.getName(),a.getId()));
            }
        return list;
    }

    @Override
    public void addGoods(Long userId, AddGoodsInfoVo addGoodsInfoVo) {
        TurntableCouponEntity turntableCouponEntity = turntableCouponDao.selectOne(new LambdaQueryWrapper<TurntableCouponEntity>().eq(TurntableCouponEntity::getUserId, userId));
        if(addGoodsInfoVo.getType()==1&&turntableCouponEntity.getCouponGreen()>0){
            turntableCouponEntity.setCouponGreen(turntableCouponEntity.getCouponGreen()-1);
        }else if(addGoodsInfoVo.getType()==2&&turntableCouponEntity.getCouponBlue()>0){
            turntableCouponEntity.setCouponBlue(turntableCouponEntity.getCouponBlue()-1);
        }else if(addGoodsInfoVo.getType()==3&&turntableCouponEntity.getCouponPurple()>0){
            turntableCouponEntity.setCouponPurple(turntableCouponEntity.getCouponPurple()-1);
        }else throw new ApplicationException(CodeType.SERVICE_ERROR,"优惠券不足！");
        turntableCouponDao.updateById(turntableCouponEntity);
        TbEquipmentVoTwo tbEquipmentKnapsack = new TbEquipmentVoTwo();
        tbEquipmentKnapsack.setFoodId(addGoodsInfoVo.getId());
        tbEquipmentKnapsack.setFoodNumber(addGoodsInfoVo.getNumber());
        tbEquipmentKnapsack.setUserId(userId);
        Result result = resourceFeignClient.addTbEquipmentKnapsackThree(tbEquipmentKnapsack);
        if(result.getCode()!=0)throw  new ApplicationException(CodeType.FEIGN_CONNECT_ERROR,result.getMsg());
    }
}
