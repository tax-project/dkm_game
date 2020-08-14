package com.dkm.turntable.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dkm.constanct.CodeType;
import com.dkm.data.Result;
import com.dkm.exception.ApplicationException;
import com.dkm.feign.ResourceFeignClient;
import com.dkm.feign.entity.AddGoodsInfo;
import com.dkm.task.service.TaskService;
import com.dkm.turntable.dao.GoodsDao;
import com.dkm.turntable.dao.TurntableCouponDao;
import com.dkm.turntable.entity.GoodsEntity;
import com.dkm.turntable.entity.TurntableCouponEntity;
import com.dkm.turntable.entity.vo.AddGoodsInfoVo;
import com.dkm.turntable.entity.vo.TurntableInfoVo;
import com.dkm.turntable.service.ITurntableService;
import com.dkm.utils.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class TurntableServiceImpl implements ITurntableService {


    @Resource
    private ResourceFeignClient resourceFeignClient;

    @Resource
    private GoodsDao goodsDao;
    @Resource
    private TurntableCouponDao turntableCouponDao;
    @Resource
    private IdGenerator idGenerator;
    @Resource
    private TaskService taskService;

    @Override
    public List<TurntableInfoVo> getTurntable(Long userId, Integer type) {
        //获取物品表物品
        List<GoodsEntity> goodsEntities = goodsDao.selectList(new LambdaQueryWrapper<GoodsEntity>().in(GoodsEntity::getGoodType, 3, 2));
        if (goodsEntities == null) throw new ApplicationException(CodeType.SERVICE_ERROR, "不好意思,物品备货中！");
        List<TurntableInfoVo> list = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            //随机插入物品中的6个 可重复
            int c = new Random().nextInt(goodsEntities.size());
            GoodsEntity a = goodsEntities.get(c);
            ///设置数量等
            list.add(new TurntableInfoVo(type * Math.max(c, 1), a.getUrl(), a.getName(), a.getId()));
        }
        return list;
    }

    @Override
    public void addGoods(Long userId, AddGoodsInfoVo addGoodsInfoVo) {
        TurntableCouponEntity turntableCouponEntity = turntableCouponDao.selectOne(new LambdaQueryWrapper<TurntableCouponEntity>().eq(TurntableCouponEntity::getUserId, userId));
        //更新券信息
        if (turntableCouponEntity == null) {
            turntableCouponEntity = new TurntableCouponEntity();
            turntableCouponEntity.setCouponBlue(0);
            turntableCouponEntity.setCouponPurple(0);
            turntableCouponEntity.setCouponGreen(20);
            turntableCouponEntity.setTurntableCouponId(idGenerator.getNumberId());
            turntableCouponEntity.setCouponTime(LocalDateTime.now());
            turntableCouponEntity.setUserId(userId);
        }
        //根据对应类型 减少相应券数量
        if (addGoodsInfoVo.getType() == 1 && turntableCouponEntity.getCouponGreen() > 0) {
            turntableCouponEntity.setCouponGreen(turntableCouponEntity.getCouponGreen() - 1);
        } else if (addGoodsInfoVo.getType() == 2 && turntableCouponEntity.getCouponBlue() > 0) {
            turntableCouponEntity.setCouponBlue(turntableCouponEntity.getCouponBlue() - 1);
        } else if (addGoodsInfoVo.getType() == 3 && turntableCouponEntity.getCouponPurple() > 0) {
            turntableCouponEntity.setCouponPurple(turntableCouponEntity.getCouponPurple() - 1);
        } else throw new ApplicationException(CodeType.SERVICE_ERROR, "优惠券不足！");
        turntableCouponDao.updateById(turntableCouponEntity);
        //调用背包增加feign
        AddGoodsInfo addGoodsInfo = new AddGoodsInfo();
        addGoodsInfo.setUserId(userId);
        addGoodsInfo.setNumber(addGoodsInfoVo.getNumber());
        addGoodsInfo.setGoodId(addGoodsInfoVo.getId());
        //增加物品
        Result result = resourceFeignClient.addBackpackGoods(addGoodsInfo);
        if (result.getCode() != 0) throw new ApplicationException(CodeType.FEIGN_CONNECT_ERROR, result.getMsg());
        try {
            taskService.setTaskProcess(userId,4L);
        } catch (Exception e) {
            log.info("转盘日常任务出错");
            e.printStackTrace();
        }
    }
}
