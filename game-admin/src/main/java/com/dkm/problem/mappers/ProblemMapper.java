package com.dkm.problem.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.dkm.problem.entities.bo.ProblemEntity;
import org.apache.ibatis.annotations.Select;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemMapper extends BaseMapper<ProblemEntity> {
   @Select({"select * from tb_problem"})
   @NotNull
   List<ProblemEntity> selectAll();
}
