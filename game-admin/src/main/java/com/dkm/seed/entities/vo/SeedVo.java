package com.dkm.seed.entities.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("种子表")
public final class SeedVo {
   @ApiModelProperty("种子ID")
   @Nullable
   private String id;
   @ApiModelProperty("种子名称")
   @Nullable
   private String name;
   @ApiModelProperty("种子等级")
   @Nullable
   private Integer level;
   @ApiModelProperty("首次种植时金币数目")
   @Nullable
   private Integer firstGold;
   @ApiModelProperty("首次种植时的红包大小")
   @Nullable
   private BigDecimal firstPrize;
   @ApiModelProperty("种子产出的经验")
   @Nullable
   private Integer xp;
   @ApiModelProperty("种植的金币")
   @Nullable
   private Integer gold;
   @ApiModelProperty("图片地址")
   @Nullable
   private String imageUrl;

}
