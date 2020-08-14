package com.dkm.goods.entities.bo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("tb_goods")
public final class GoodsEntity {
   @TableId
   private  long id;
   @Nullable
   private String name;
   @Nullable
   private String url;
   private int goodType;
   @Nullable
   private String goodContent;
   private int goodMoney;
   @Nullable
   private String tabUrl;

}
