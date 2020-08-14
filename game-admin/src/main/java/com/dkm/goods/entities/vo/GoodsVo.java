package com.dkm.goods.entities.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("物品ID")
public final class GoodsVo {
    @ApiModelProperty("物品ID")
    @Nullable
    private String id;
    @ApiModelProperty("物品名称")
    @Nullable
    private String name;
    @ApiModelProperty("图片地址")
    @Nullable
    private String url;
    @ApiModelProperty("物品类型[0:金币、2：道具、3：食物]")
    private int goodType;
    @ApiModelProperty("物品描述")
    @Nullable
    private String goodContent;
    @ApiModelProperty("物品价格")
    private int goodMoney;
    @ApiModelProperty("前端使用跳转路径")
    @Nullable
    private String tabUrl;

}
