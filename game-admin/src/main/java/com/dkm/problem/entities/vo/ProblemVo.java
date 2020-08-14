package com.dkm.problem.entities.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("問題")
public final class ProblemVo {
    @ApiModelProperty("id")
    @Nullable
    private String id;
    @ApiModelProperty("問題標題")
    @NotNull
    private String problemSubject;
    @ApiModelProperty("問題答案 A")
    @Nullable
    private String problemAnswerA;
    @ApiModelProperty("問題答案 B")
    @Nullable
    private String problemAnswerB;
    @ApiModelProperty("問題答案 C")
    @Nullable
    private String problemAnswerC;
    @ApiModelProperty("問題答案 D")
    @Nullable
    private String problemAnswerD;
    @ApiModelProperty("正確答案")
    @Nullable
    private String problemAnswer;

}
