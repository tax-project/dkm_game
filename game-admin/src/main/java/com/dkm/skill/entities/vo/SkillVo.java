package com.dkm.skill.entities.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("技能")
public final class SkillVo {
    @ApiModelProperty("技能ID")
    @Nullable
    private String id;
    @ApiModelProperty("技能名称")
    @Nullable
    private String skinName;
    @ApiModelProperty("第一个技能效果")
    @Nullable
    private String firstSkinEffect;
    @ApiModelProperty("第二个技能效果")
    @Nullable
    private String secondSkinEffect;


}
