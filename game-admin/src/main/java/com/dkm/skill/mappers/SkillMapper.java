package com.dkm.skill.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dkm.skill.entities.bo.SkillEntity;
import org.apache.ibatis.annotations.Select;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface SkillMapper extends BaseMapper<SkillEntity> {
   @Select({"select * from tb_skill"})
   @NotNull
   List<SkillEntity> selectAll();
}
