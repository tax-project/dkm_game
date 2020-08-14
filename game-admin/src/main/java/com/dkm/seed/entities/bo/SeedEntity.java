package com.dkm.seed.entities.bo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("tb_seed")
public final class SeedEntity {
   @TableId
   private long seedId;
   @Nullable
   private String seedName;
   @Nullable
   private Integer seedGrade;
   @Nullable
   private Integer seedProdgold;
   @Nullable
   private BigDecimal seedProdred;
   @Nullable
   private Integer seedExperience;
   @Nullable
   private Integer seedGold;
   @Nullable
   private String seedImg;


}
