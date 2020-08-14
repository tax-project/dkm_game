package com.dkm.seed.services;

import com.dkm.seed.entities.vo.SeedVo;
import com.dkm.utils.bean.ResultVo;
import java.util.List;
import org.jetbrains.annotations.NotNull;


public interface ISeedService {
   @NotNull
   List<SeedVo> getAll();

   @NotNull
   ResultVo insert(@NotNull SeedVo var1);

   @NotNull
   ResultVo update(long var1, @NotNull SeedVo var3);

   @NotNull
   ResultVo delete(long var1);
}
