package com.dkm.problem.entities.bo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("tb_problem")
public final class ProblemEntity {
    @TableId
    private long id;
    @NotNull
    private String problemSubject;
    @Nullable
    private String problemAnswerA;
    @Nullable
    private String problemAnswerB;
    @Nullable
    private String problemAnswerC;
    @Nullable
    private String problemAnswerD;
    @Nullable
    private String problemAnswer;


}
