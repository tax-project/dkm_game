package com.dkm.problem.services;

import com.dkm.problem.entities.vo.ProblemVo;
import com.dkm.utils.bean.ResultVo;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface IProblemService {
   /**
    * 得到所有的問答列表
    * @return List<ProblemVo> 所有的問答列表，如果不存在問答則返回 NULL
    */
   @NotNull
   List<ProblemVo> getAllProblems();

   /**
    * 添加一个问答选项
    *
    * @param var1 ProblemVo
    * @return ResultVo
    */
   @NotNull
   ResultVo insert(@NotNull ProblemVo var1);

   /**
    * 根据 ID 来更新一个数据
    * @param var1 String
    * @param var3 ProblemVo
    * @return ResultVo
    */

   @NotNull
   ResultVo update(long var1, @NotNull ProblemVo var3);


   /**
    * 根据一个 ID 来删除问答
    *
    * @param var1 Long
    * @return ResultVo
    */
   @NotNull
   ResultVo delete(long var1);
}
