package com.dkm.skill.services;

import com.dkm.skill.entities.vo.SkillVo;
import com.dkm.utils.bean.ResultVo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ISkillService {
   @NotNull
   List<SkillVo> getAll();

   @NotNull
   ResultVo insert(@NotNull SkillVo var1);

   @NotNull
   ResultVo update(long var1, @NotNull SkillVo var3);

   @NotNull
   ResultVo delete(long var1);
}
