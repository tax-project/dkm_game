package com.dkm.skill.entities.bo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("tb_skill")
public final class SkillEntity {
   @TableId
   private long id;
   @Nullable
   private String skName;
   @Nullable
   private String skEffectOne;
   @Nullable
   private String skEffectTwo;


}
