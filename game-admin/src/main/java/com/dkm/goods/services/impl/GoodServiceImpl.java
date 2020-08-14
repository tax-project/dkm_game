package com.dkm.goods.services.impl;

import com.dkm.goods.entities.bo.GoodsEntity;
import com.dkm.goods.entities.vo.GoodsVo;
import com.dkm.goods.mappers.GoodsMapper;
import com.dkm.goods.services.IGoodService;
import com.dkm.utils.IdGenerator;
import com.dkm.utils.bean.ResultVo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(
        rollbackFor = {Exception.class}
)

public class GoodServiceImpl implements IGoodService {
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private IdGenerator idGenerator;

    @Override
    @NotNull
    public List<GoodsVo> getAllGoods() {
        val result = new ArrayList<GoodsVo>();
        val var14 = this.goodsMapper.selectAllGoods();
        for (GoodsEntity it : var14) {
            result.add(new GoodsVo(String.valueOf(it.getId()), it.getName(), it.getUrl(), it.getGoodType(), it.getGoodContent(), it.getGoodMoney(), it.getTabUrl()));
        }
        return result;
    }

    @Override
    @NotNull
    public ResultVo addGoodsItem(@NotNull GoodsVo goods) {
        long id = idGenerator.getNumberId();
        goodsMapper.insert(new GoodsEntity(id, goods.getName(), goods.getUrl(), goods.getGoodType(), goods.getGoodContent(), goods.getGoodMoney(), goods.getTabUrl()));
        return new ResultVo(true, id);
    }

    @Override
    @NotNull
    public ResultVo updateItemById(long id, @NotNull GoodsVo goods) {


        GoodsEntity var5 = goodsMapper.selectById(id);
        if (var5 != null) {
            if (goods.getName() != null) {
                var5.setName(goods.getName());
            }

            if (goods.getUrl() != null) {
                var5.setUrl(goods.getUrl());
            }

            if (goods.getGoodType() >= 0) {
                var5.setGoodType(goods.getGoodType());
            }

            if (goods.getGoodContent() != null) {
                var5.setGoodContent(goods.getGoodContent());
            }

            if (goods.getGoodMoney() >= 0) {
                var5.setGoodMoney(goods.getGoodMoney());
            }

            if (goods.getTabUrl() != null) {
                var5.setTabUrl(goods.getTabUrl());
            }
            goodsMapper.updateById(var5);
            return new ResultVo(true, var5.getId());
        } else {
            return new ResultVo(false, "未找到此ID的数据");
        }
    }

    @Override
    @NotNull
    public ResultVo deleteItemById(@NotNull String id) {

        GoodsEntity var3 = goodsMapper.selectById(id);
        if (var3 != null) {
            goodsMapper.deleteById(var3.getId());
            return new ResultVo(true, var3.getId());
        } else {
            return new ResultVo(false, "未找到此ID的数据");
        }
    }
}
