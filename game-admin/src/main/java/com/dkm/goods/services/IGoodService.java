package com.dkm.goods.services;

import com.dkm.goods.entities.vo.GoodsVo;
import com.dkm.utils.bean.ResultVo;
import java.util.List;
import org.jetbrains.annotations.NotNull;


public interface IGoodService {
   @NotNull
   List<GoodsVo> getAllGoods();

   @NotNull
   ResultVo addGoodsItem(@NotNull GoodsVo var1);

   @NotNull
   ResultVo updateItemById(long var1, @NotNull GoodsVo var3);

   @NotNull
   ResultVo deleteItemById(@NotNull String var1);
}
