package com.dkm.good.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dkm.constanct.CodeType;
import com.dkm.exception.ApplicationException;
import com.dkm.good.dao.GoodMapper;
import com.dkm.good.entity.Goods;
import com.dkm.good.entity.vo.GoodQueryVo;
import com.dkm.good.entity.vo.GoodsVo;
import com.dkm.good.service.IGoodsService;
import com.dkm.utils.IdGenerator;
import com.dkm.vilidata.RandomData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author qf
 * @date 2020/5/19
 * @vesion 1.0
 **/
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GoodsServiceImpl extends ServiceImpl<GoodMapper, Goods> implements IGoodsService {

   @Autowired
   private IdGenerator idGenerator;

   @Autowired
   private RandomData randomData;

   @Override
   public void insertGood(GoodsVo vo) {

      LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
            .eq(Goods::getName,vo.getName());

      Goods goods1 = baseMapper.selectOne(wrapper);

      if (goods1 != null) {
         throw new ApplicationException(CodeType.SERVICE_ERROR, "该物品已存在");
      }

      Goods goods = new Goods();
      goods.setId(idGenerator.getNumberId());
      goods.setName(vo.getName());
      goods.setUrl(vo.getUrl());
      goods.setGoodType(vo.getGoodType());

      int insert = baseMapper.insert(goods);

      if (insert <= 0) {
         throw new ApplicationException(CodeType.SERVICE_ERROR, "增加失败");
      }
   }



   @Override
   public List<GoodQueryVo> queryGoodsList(List<Long> list) {
      return baseMapper.queryGoodsList (list);
   }


   /**
    *  随机生成一个物品
    * @return 返回物品信息
    */
   @Override
   public Goods queryRandomGoods() {
      return baseMapper.queryRandomGoods();
   }

   @Override
   public List<GoodQueryVo> getGoodList(Long userId) {
      return baseMapper.getGoodList(userId);
   }

   @Override
   public Goods queryOne() {

      LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
            .ne(Goods::getGoodType, 1);

      List<Goods> goods = baseMapper.selectList(wrapper);

      Set<Integer> list = randomData.getList(goods.size(), 1);

      for (Integer integer : list) {
         return goods.get(integer);
      }

      return null;
   }
}
